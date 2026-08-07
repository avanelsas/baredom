(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.elements.server-resource.config :as config]
   [barebuild.elements.server-resource.consumers :as consumers]
   [barebuild.elements.server-resource.executor :as executor]
   [barebuild.elements.server-resource.model :as model]
   [barebuild.recorder :as recorder]
   [barebuild.resource :as resource]
   [barebuild.utils.url :as url]
   [barebuild.wire :as wire]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [clojure.string :as str]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-resource   "__xServerResource")
(def ^:private k-popstate   "__xPopstate")
(def ^:private k-generation "__xConnectGeneration")
;; The effect drain. Transient bookkeeping with no diagnostic display value, so untraced.
(def ^:private k-queue      "__xEffectQueue")
(def ^:private k-draining   "__xDraining")

;; ── The engine ───────────────────────────────────────────────────────────────
;; `handle-event!` drains through `effect-handlers`, which is built from `handle-event!`. The loop
;; and its own table are mutually recursive, so one of the three forms has to be declared.
(declare ^:private effect-handlers)

(defn- drain-effects!
  "Perform the queued effects, and whatever a re-entrant step queues while they run, until nothing
  is left. A performer can hand an event straight back in: a consumer notified here may remove the
  host, which fires `disconnectedCallback` synchronously. Draining in rounds puts the nested step's
  effects after this one's, so an :abort cannot run before the :fetch it names has been issued."
  [^js el]
  (du/setv-untraced! el k-draining true)
  (try
    (loop []
      (when-let [queued (seq (du/getv el k-queue))]
        (du/setv-untraced! el k-queue [])
        (executor/run-effects! effect-handlers el queued)
        (recur)))
    (finally
      (du/setv-untraced! el k-draining false))))

(defn- handle-event!
  [^js el event]
  (let [r                          (du/getv el k-resource)
        {:keys [resource effects]} (resource/step r event)]
    (du/setv! el k-resource resource)
    (recorder/record! {:resource/id (:resource/id r)
                       :el          el
                       :event       event
                       :before      r
                       :after       resource
                       :effects     effects})
    (du/setv-untraced! el k-queue (into (or (du/getv el k-queue) []) effects))
    (when-not (du/getv el k-draining)
      (drain-effects! el))))

(def ^:private effect-handlers (executor/performers handle-event!))

(defn- current-url-intent
  "The resource's query as the address bar currently holds it. The only place this element reads
  the address bar."
  [resource-id]
  (url/parse-scoped-query (.-search js/location) resource-id))

(defn- handle-popstate! [^js el resource-id]
  (handle-event! el [:url-changed (current-url-intent resource-id)]))

(defn- element-descendants [^js el]
  (array-seq (.querySelectorAll el "*")))

(defn- owned-by? [^js el ^js node]
  (identical? el (.closest (.-parentElement node) model/tag-name)))

(defn- owned-descendants
  "The elements inside `el` that answer to it rather than to a nested <server-resource>. Both the
  consumers it drives and the definitions it waits for are drawn from here, so it never blocks on
  an element it does not own."
  [^js el]
  (filterv (fn [^js node] (owned-by? el node)) (element-descendants el)))

(defn- custom-element-tags [^js el]
  (into []
        (comp (map (fn [^js node] (.. node -tagName toLowerCase)))
              (filter (fn [tag] (str/includes? tag "-")))
              (distinct))
        (owned-descendants el)))

(defn- read-boot-embed
  "The <script type=\"application/json\"> embed inside the host, read through the same parse path
  as a network response. Nil when there is no embed."
  [^js el]
  (when-let [^js script (.querySelector el "script[type=\"application/json\"]")]
    (wire/parse-body (.-textContent script))))

(defn- install-resource!
  "Build the resource value from the host's attributes and the current URL, carrying over what the
  previous connection left in flight."
  [^js el resource-id]
  ;; Read once at connect, like the resource id, so the value step builds from cannot change under
  ;; an in-flight request.
  (du/setv! el k-resource
            (resource/initial {:resource/id    resource-id
                               :endpoint       (du/get-attr el model/attr-src)
                               :url-intent     (current-url-intent resource-id)
                               :request-config (config/read-request-config! el)
                               :carried        (resource/carry-over (du/getv el k-resource))})))

(defn- install-popstate! [^js el resource-id]
  (let [on-popstate (fn [_e] (handle-popstate! el resource-id))]
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)))

(defn- boot!
  "Install everything the element holds for the life of this connection, then hand in :connected
  with the SSR embed the host carries, if any."
  [^js el resource-id]
  (install-resource! el resource-id)
  (install-popstate! el resource-id)
  (consumers/install! el resource-id (owned-descendants el) handle-event!)
  (handle-event! el [:connected {:embed (read-boot-embed el)}]))

(defn- next-generation!
  "Open a new lifecycle generation on `el` and return it. Every connect and every disconnect
  starts one, so a deferred boot can tell whether its connection is still the current one."
  [^js el]
  (let [n (inc (or (du/getv el k-generation) 0))]
    (du/setv! el k-generation n)
    n))

(defn- disconnected! [^js el]
  (next-generation! el)
  ;; A connect whose boot never ran leaves nothing to tear down, and stepping a resource that was
  ;; never built would record a transition that did not happen.
  (when (du/getv el k-resource)
    (handle-event! el [:disconnected {}])
    (.removeEventListener js/window "popstate" (du/getv el k-popstate))
    (du/setv! el k-popstate nil)))

(def ^:private undefined-tags-budget-ms 5000)

(defn- report-undefined-tags!
  "Name the tags still undefined after the budget. The boot waits on every custom element inside
  the host, so one unregistered component leaves the resource issuing no request at all."
  [^js el resource-id tags generation]
  (js/setTimeout
   (fn []
     (when (= generation (du/getv el k-generation))
       (when-let [missing (seq (remove #(js/customElements.get %) tags))]
         (js/console.error "[server-resource]" (model/label resource-id)
                           "cannot boot, these custom elements inside it are never defined:"
                           (clj->js (vec missing))))))
   undefined-tags-budget-ms))

(defn- definitions-rejected!
  "Report a boot that never ran. `whenDefined` rejects on a hyphenated tag that is not a legal
  custom element name, `font-face` and `annotation-xml` among them."
  [resource-id e]
  (js/console.error "[server-resource]" (model/label resource-id)
                    "cannot boot, waiting on its custom elements failed:" e))

(defn- report-unusable-id!
  "Report a `resource-id` attribute the request query cannot carry. It is dropped, so the element
  is the unnamed root until the author fixes it."
  [declared resolved]
  (when (and (not (str/blank? declared)) (nil? resolved))
    (js/console.error "[server-resource]" model/attr-resource-id
                      "cannot go in a request query, ignoring it:" declared)))

(defn- connected! [^js el]
  (let [declared    (du/get-attr el model/attr-resource-id)
        resource-id (model/resolve-resource-id declared)
        tags        (custom-element-tags el)
        generation  (next-generation! el)]
    (report-unusable-id! declared resource-id)
    (report-undefined-tags! el resource-id tags generation)
    ;; the rejection handler rides `then`, so a throw out of `boot!` is not reported as a boot that
    ;; never started
    (.then (js/Promise.all (clj->js (map #(js/customElements.whenDefined %) tags)))
           (fn []
             ;; the connection this boot was scheduled for may have ended, or been replaced by
             ;; a later one, while the definitions were awaited
             (when (= generation (du/getv el k-generation))
               (boot! el resource-id)))
           (fn [e] (definitions-rejected! resource-id e)))))

;; register! always installs attributeChangedCallback and calls this, so it must exist.
(defn- attribute-changed! [_el _name _old _new] nil)

;; The replay hook. BareReplay's dock calls el.projectResource(value) to push a reconstructed
;; resource value at the consumers, reusing the same notify path a live step uses. It hands in a
;; resource rather than a view, so this is the one place outside `step` that projects. The sole
;; caller is barereplay.dock, a sibling project.
(defn- setup-prototype! [^js proto]
  (.defineProperty js/Object proto "projectResource"
                   #js {:value        (fn [value]
                                        (this-as ^js el
                                          (consumers/apply! el
                                                            (resource/project value)
                                                            (:resource/id value))))
                        :writable     true
                        :configurable true}))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   setup-prototype!}))
