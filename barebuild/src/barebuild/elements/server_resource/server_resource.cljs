(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.elements.server-resource.model :as model]
   [barebuild.recorder :as recorder]
   [barebuild.resource :as resource]
   [barebuild.utils :as utils]
   [barebuild.wire :as wire]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [clojure.string :as str]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-resource  "__xServerResource")
(def ^:private k-consumers "__xConsumers")
(def ^:private k-popstate  "__xPopstate")
(def ^:private k-abort     "__xAbort")
(declare handle-event!)

(defn- construct-url-intent [resource-id]
  (let [current-url-params (js/URLSearchParams. (.-search js/location))
        prefix             (utils/url-prefix resource-id)
        owned              (utils/owned-url-keys resource-id
                                                 (js/Array.from (.keys current-url-params)))]
    (utils/canonicalize-query
     (into {}
           (for [k owned]
             [(keyword (subs k (count prefix)))
              (.get current-url-params k)])))))

(defn- handle-popstate [^js el resource-id]
  (handle-event! el [:url-changed (construct-url-intent resource-id)]))

;; ── The engine ───────────────────────────────────────────────────────────────

(defn- fetch-init [{:keys [method headers body]}]
  (clj->js (cond-> {:method method}
             headers (assoc :headers headers)
             body    (assoc :body (js/JSON.stringify (clj->js body))))))

(defn- fetch-envelope
  "Fetch, reject a non-ok status, read the body as text (not .json), and parse it into an envelope. A nil body parses to an empty-body marker.
  Returns a promise of the parsed result"
  [url init]
  (-> (js/fetch url init)
    (.then (fn [^js resp]
             (if (.-ok resp)
               (.text resp)
               (throw (js/Error. (str "HTTP " (.-status resp)))))))
    (.then (fn [^js body]
             (wire/parse-envelope (try (js/JSON.parse body) (catch :default _ nil)))))))

(defn- execute-fetch! [^js el m]
  (let [controller (js/AbortController.)
        request-id (:request/id m)]
    (du/setv-untraced! el k-abort controller)
    (-> (fetch-envelope (:url m) (js/Object.assign (fetch-init m) #js {:signal (.-signal controller)}))
      (.then (fn [result]
               (if (:protocol-failure result)
                 (handle-event! el [:protocol-failed (assoc result :request/id request-id)])
                 (handle-event! el [:response result]))))
      (.catch (fn [^js e]
                ;; an aborted fetch is intentional (disconnect / supersede), not a failure
                (when-not (= "AbortError" (.-name e))
                  (handle-event! el [:network-failed {:request/id request-id :error {:kind :offline}}])))))))

(defn- execute-write! [^js el m]
  (let [write-id (:write/id m)]
    (-> (fetch-envelope (:url m) (fetch-init m))
      (.then (fn [result]
               (let [result* (assoc result :write/id write-id)]
                 (if (:protocol-failure result*)
                   (handle-event! el [:write-failed result*])
                   (handle-event! el [:write-ack result*])))))
      (.catch (fn [^js _e]
                (handle-event! el [:write-failed {:write/id write-id :error {:kind :offline}}]))))))

(defn- find-resource
  "The <server-resource> on the page whose resolved id is `target-id`, or nil."
  [target-id]
  (some (fn [^js e]
          (when (= target-id (:resource/id (du/getv e k-resource))) e))
        (array-seq (.querySelectorAll js/document model/tag-name))))

(defn- notify-consumers! [^js el r]
  (let [own-id    (:resource/id r)
        consumers (du/getv el k-consumers)
        ctx       {:submit-intent! (fn [patch & [target-id]]
                                     (let [target (if (model/targets-sibling? own-id target-id)
                                                    (find-resource target-id)
                                                    el)]
                                       (when target
                                         (handle-event! target [:intent-patch patch]))))
                   :submit-write!  (fn [payload] (handle-event! el [:submit-write payload]))}]
    (doseq [^js c consumers]
      (.applyResource c r ctx))))

(defn- run-effects!
  [^js el effects]
  (doseq [[fx m] effects]
    (case fx
      :fetch
      (execute-fetch! el m)

      :notify-consumers
      (notify-consumers! el (:resource m))

      :url-write
      (let [new-url (utils/build-scoped-url (.-search js/location)
                                            (.-pathname js/location)
                                            (:resource/id m)
                                            (:params m))]
        (if (= (:mode m) :push)
          (.pushState js/history nil "" new-url)
          (.replaceState js/history nil "" new-url)))

      :abort
      (when-let [^js controller (du/getv el k-abort)]
        (.abort controller)
        (du/setv-untraced! el k-abort nil))

      :write
      (execute-write! el m)

      :diagnostic
      (js/console.debug "[server-resource]" (name (:code m)))

      nil)))

(defn- handle-event!
  [^js el event]
  (let [r                          (du/getv el k-resource)
        {:keys [resource effects]} (resource/step r event)]
    (du/setv! el k-resource resource)
    ;; Note: if no recorder is set then this is won't record anything
    (recorder/record! {:el el :event event :before r :after resource :effects effects})
    (run-effects! el effects)))

(defn- read-boot-embed
  "Read the <script type=\"application/json\"> embed inside the host and
  run it through the same parse path as a network response. Returns the parsed response
  value (or a protocol-failure marker for a broken embed), or nil when there is no embed."
  [^js el]
  (when-let [^js script (.querySelector el "script[type=\"application/json\"]")]
    (let [text (.-textContent script)
          obj  (try (js/JSON.parse text) (catch :default _ nil))]
      (wire/parse-envelope obj))))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- element-descendants [^js el]
  (array-seq (.querySelectorAll el "*")))

(defn- owned-by? [^js el ^js node]
  (identical? el (.closest (.-parentElement node) model/tag-name)))

(defn- collect-consumers [^js el]
  (->> (element-descendants el)
    (filterv
     (fn [^js c]
       (and (some? (.-applyResource c))
            (owned-by? el c))))))

(defn- custom-element-tags [^js el]
  (into []
        (comp (map (fn [^js node] (.. node -tagName toLowerCase)))
              (filter (fn [tag] (str/includes? tag "-")))
              (distinct))
        (element-descendants el)))

(defn- boot!
  "When reloading, read the current url and see if there are url parameters that
  have to be processed to get the element in the right state (e.g. table sorting).
  If there is an embedderd version, load that first."
  [^js el]
  (let [resource-id    (model/resolve-resource-id (du/get-attr el model/attr-resource-id))
        history-policy {:navigation :push}
        on-popstate    (fn [_e] (handle-popstate el resource-id))
        embed          (read-boot-embed el)]
    (du/setv! el k-resource {:resource/id    resource-id
                             :endpoint       (du/get-attr el model/attr-src)
                             :last-accepted  nil
                             :url-intent     (construct-url-intent resource-id)
                             :history-policy history-policy})
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)
    (du/setv! el k-consumers (collect-consumers el))
    (handle-event! el [:connected {:embed embed}])))

(defn- disconnected! [^js el]
  (handle-event! el [:disconnected {}])
  (.removeEventListener js/window "popstate" (du/getv el k-popstate))
  (du/setv! el k-popstate nil))

(defn- connected! [^js el]
  (let [tags (custom-element-tags el)]
    (-> (js/Promise.all (clj->js (map #(js/customElements.whenDefined %) tags)))
      (.then (fn [] (boot! el))))))

;; register! always installs attributeChangedCallback and calls this — so it must exist.
(defn- attribute-changed! [_el _name _old _new] nil)

;; The replay hook. BareReplay's dock calls el.projectResource(value) to push a reconstructed
;; (time-travelled) resource value at the consumers, so the components paint that historical
;; state. It reuses the same notify path a live step uses. The sole caller is
;; barereplay.dock, a sibling project.
(defn- setup-prototype! [^js proto]
  (.defineProperty js/Object proto "projectResource"
                   #js {:value        (fn [value] (this-as ^js el (notify-consumers! el value)))
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
