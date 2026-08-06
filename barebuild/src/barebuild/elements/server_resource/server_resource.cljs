(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.elements.server-resource.model :as model]
   [barebuild.recorder :as recorder]
   [barebuild.resource :as resource]
   [barebuild.transport :as transport]
   [barebuild.utils.request :as request]
   [barebuild.utils.url :as url]
   [barebuild.wire :as wire]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [clojure.string :as str]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-resource   "__xServerResource")
(def ^:private k-consumers  "__xConsumers")
(def ^:private k-popstate   "__xPopstate")
(def ^:private k-abort      "__xAbort")
(def ^:private k-generation "__xConnectGeneration")
(def ^:private k-ctx        "__xConsumerCtx")
;; The effect drain. Transient bookkeeping with no diagnostic display value, so untraced.
(def ^:private k-queue      "__xEffectQueue")
(def ^:private k-draining   "__xDraining")
(declare ^:private handle-event!)

(defn- label
  "How a resource is named in a console message, an unnamed one included."
  [resource-id]
  (or resource-id "(unnamed)"))

(defn- current-url-intent
  "The resource's query as the address bar currently holds it. The only place this element reads
  the address bar."
  [resource-id]
  (url/parse-scoped-query (.-search js/location) resource-id))

(defn- handle-popstate! [^js el resource-id]
  (handle-event! el [:url-changed (current-url-intent resource-id)]))

;; ── Transport config ─────────────────────────────────────────────────────────

(defn- json-object
  "`text` parsed as a JSON object, or nil when it does not parse or is not an object."
  [text]
  (let [parsed (try (js/JSON.parse text) (catch :default _ nil))]
    (when (object? parsed) parsed)))

(defn- parse-headers
  "The static headers the `headers` attribute declares, and whether it was a JSON object."
  [text]
  (let [obj (json-object text)]
    {:value (request/normalize-headers (js->clj obj)) :usable? (some? obj)}))

(defn- parse-credentials
  "The fetch credentials mode the `credentials` attribute declares. An unrecognised mode is not
  usable and is never passed to fetch."
  [text]
  (let [mode (model/resolve-credentials text)]
    {:value mode :usable? (some? mode)}))

(defn- parse-timeout
  "The request budget the `timeout` attribute declares. An unusable value keeps the default."
  [text]
  (let [{:keys [ms valid?]} (model/parse-timeout text)]
    {:value ms :usable? valid?}))

;; The request configuration the host declares as attributes: which attribute carries each one,
;; which key it becomes in the resource value, how to read it, and what to say when the attribute
;; is there but cannot be read. A new knob is a row here.
(def ^:private request-config
  [{:key :credentials :attr model/attr-credentials :parse parse-credentials
    :complaint "is not a fetch credentials mode, ignoring it:"}
   {:key :headers :attr model/attr-headers :parse parse-headers
    :complaint "is not a JSON object, ignoring it:"}
   {:key :timeout :attr model/attr-timeout :parse parse-timeout
    :complaint "is not a number of milliseconds, keeping the default:"}])

(defn- read-config-attr!
  "One configured attribute as the value it declares, nil when it declares none. A present one that
  cannot be read is reported, the row's parse deciding what it falls back to."
  [^js el {:keys [attr parse complaint]}]
  (let [text                    (du/get-attr el attr)
        {:keys [value usable?]} (parse text)]
    (when-not (or (str/blank? text) usable?)
      (js/console.error "[server-resource]" attr complaint text))
    value))

(defn- read-request-config!
  "The request configuration the host declared, as the keys the resource value carries. A key is
  present only when its attribute yielded a value. `timeout` is the one whose absence means
  something: a budget of 0 drops the key, and no key is no budget."
  [^js el]
  (into {}
        (keep (fn [row]
                (when-let [value (read-config-attr! el row)]
                  [(:key row) value])))
        request-config))

;; ── The engine ───────────────────────────────────────────────────────────────

(defn- delivery-threw!
  "Report a throw while delivering a result. It is a defect in this code, not a transport
  failure."
  [e]
  (js/console.error "[server-resource] delivering a result threw:" e))

(defn- delivery-outcome
  "What came back for a request, as the outcome it is and the detail that outcome reports. Every
  outcome is named by the id this client minted, never by the server's echo of it."
  [result request-id]
  (cond
    (:protocol-failure result) [:protocol (assoc result :request/id request-id)]
    (:network-failure result)  [:network {:request/id request-id
                                          :error      (:network-failure result)}]
    :else                      [:ok (assoc result :request/id request-id)]))

(defn- read-rejected!
  "Report a read that never arrived. An abort is intentional, so it is not a failure."
  [^js el request-id timeout ^js e]
  (when-not (transport/abort-error? e)
    (handle-event! el [:network-failed {:request/id request-id
                                        :error      (transport/transport-error e timeout)}])))

(defn- write-rejected!
  "Report a write whose outcome the client never learned. An aborted write is reported where an
  aborted read is not, a write having possibly committed before it ended."
  [^js el write-id timeout ^js e]
  (handle-event! el [:write-failed {:request/id write-id
                                    :error      (transport/transport-error e timeout)}]))

;; The two kinds of request as data: the event each outcome becomes, and who reports a rejection.
;; A write reports both failures as one event, since either leaves its outcome unknown, while a
;; read tells them apart. One row per kind, so a kind cannot be paired with the other's handler.
(def ^:private delivery
  {:read  {:events    {:protocol :protocol-failed :network :network-failed :ok :response}
           :on-reject read-rejected!}
   :write {:events    {:protocol :write-failed :network :write-failed :ok :write-ack}
           :on-reject write-rejected!}})

(defn- deliver!
  "Hand what came back for a request of `kind` in as the event that outcome names."
  [^js el kind result request-id]
  (let [[outcome detail] (delivery-outcome result request-id)]
    (handle-event! el [(get-in delivery [kind :events outcome]) detail])))

(defn- perform-request!
  "Send `m` under `controller` and hand what comes back in as `kind`'s event. The kind's rejection
  handler rides `then` rather than a trailing `catch`, so it sees only what the request did."
  [^js el kind m ^js controller]
  (let [id        (:request/id m)
        on-reject (get-in delivery [kind :on-reject])]
    (-> (.then (transport/perform! m controller)
               (fn [result] (deliver! el kind result id))
               (fn [^js e] (on-reject el id (:timeout m) e)))
      (.catch delivery-threw!))))

(defn- fetch!
  "Send a read, stashing its controller so a disconnect or a supersede can abort it."
  [^js el m]
  (let [controller (js/AbortController.)]
    ;; stashed before the decorator is awaited, so an abort landing in that window still reaches
    ;; this request. Kept with its request id so the :abort effect aborts the request it names.
    (du/setv-untraced! el k-abort {:request/id (:request/id m) :controller controller})
    (perform-request! el :read m controller)))

(defn- write!
  "Send a write. Its controller is never stashed in k-abort, so a disconnect or a superseding read
  leaves an in-flight write alone and only its own budget can end it."
  [^js el m]
  (perform-request! el :write m (js/AbortController.)))

(defn- find-resource
  "The <server-resource> on the page whose resolved id is `target-id`, or nil. A blank name
  resolves to no id at all, so it names nothing rather than matching a blank attribute."
  [target-id]
  (when-let [id (model/resolve-resource-id target-id)]
    (.querySelector js/document
                    (str model/tag-name
                         "[" model/attr-resource-id "=\"" (js/CSS.escape id) "\"]"))))

(defn- apply-consumer!
  "Notify one consumer, isolating a throwing applyResource so later consumers still run."
  [^js c view ctx own-id]
  (try
    (.applyResource c view ctx)
    (catch :default e
      (js/console.error "[server-resource]" (label own-id)
                        "consumer applyResource threw:" e))))

(defn- submit-intent!
  "Hand a consumer's intent patch back in as an event. A `target-id` names a sibling resource,
  which `step` routes."
  ([^js el patch] (handle-event! el [:intent-patch patch]))
  ([^js el patch target-id] (submit-intent! el (assoc patch :target-id target-id))))

(defn- consumer-ctx
  "What a consumer may call back into. Built once at boot, the same two closures for the element's
  whole life."
  [^js el]
  {:submit-intent! (partial submit-intent! el)
   :submit-write!  (fn [payload] (handle-event! el [:submit-write payload]))})

(defn- apply-consumers! [^js el view own-id]
  (let [ctx (du/getv el k-ctx)]
    (doseq [^js c (du/getv el k-consumers)]
      (apply-consumer! c view ctx own-id))))

(defn- notify-consumers! [^js el m]
  (apply-consumers! el (:view m) (:resource/id m)))

(defn- url-write! [^js _el m]
  (let [new-url (url/build-scoped-url (.-search js/location)
                                      (.-pathname js/location)
                                      (:resource/id m)
                                      (:params m))]
    (if (= (:mode m) :push)
      (.pushState js/history nil "" new-url)
      (.replaceState js/history nil "" new-url))))

(defn- abort! [^js el m]
  (when-let [pending (du/getv el k-abort)]
    (when (= (:request/id m) (:request/id pending))
      (.abort ^js (:controller pending))
      (du/setv-untraced! el k-abort nil))))

(defn- route-intent!
  "Resolve the name `step` chose to an element and hand the patch over. A name that resolves to
  nothing goes back in as an event."
  [^js el m]
  (if-let [^js target (find-resource (:resource/id m))]
    (handle-event! target [:intent-patch (:patch m)])
    (handle-event! el [:intent-unroutable {:resource/id (:resource/id m)}])))

(defn- diagnostic! [^js _el m]
  (if-let [detail (:detail m)]
    (js/console.debug "[server-resource]" (name (:code m)) (clj->js detail))
    (js/console.debug "[server-resource]" (name (:code m)))))

;; The executor, as data: one performer per effect tag, each named after the tag it performs like
;; the constructors in `effect`, each taking the host and the effect value and deciding nothing.
;; `effect-handlers-cover-the-vocabulary` pins these keys against effect/tags.
;; Public for test purposes only
(def effect-handlers
  {:fetch            fetch!
   :write            write!
   :abort            abort!
   :url-write        url-write!
   :route-intent     route-intent!
   :notify-consumers notify-consumers!
   :diagnostic       diagnostic!})

(defn- run-effects!
  [^js el effects]
  (doseq [[fx m] effects]
    (if-let [perform! (get effect-handlers fx)]
      (perform! el m)
      (js/console.error "[server-resource] no performer for effect" (str fx)))))

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
        (run-effects! el queued)
        (recur)))
    (finally
      (du/setv-untraced! el k-draining false))))

(defn- handle-event!
  [^js el event]
  (let [r                          (du/getv el k-resource)
        {:keys [resource effects]} (resource/step r event)]
    (du/setv! el k-resource resource)
    ;; Note: with no recorder set this records nothing
    (recorder/record! {:el el :event event :before r :after resource :effects effects})
    (du/setv-untraced! el k-queue (into (or (du/getv el k-queue) []) effects))
    (when-not (du/getv el k-draining)
      (drain-effects! el))))

(defn- read-boot-embed
  "The <script type=\"application/json\"> embed inside the host, read through the same parse path
  as a network response. Nil when there is no embed."
  [^js el]
  (when-let [^js script (.querySelector el "script[type=\"application/json\"]")]
    (wire/parse-body (.-textContent script))))

;; ── Element class ────────────────────────────────────────────────────────────
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

(defn- collect-consumers [^js el]
  (filterv (fn [^js c] (some? (.-applyResource c))) (owned-descendants el)))

(defn- custom-element-tags [^js el]
  (into []
        (comp (map (fn [^js node] (.. node -tagName toLowerCase)))
              (filter (fn [tag] (str/includes? tag "-")))
              (distinct))
        (owned-descendants el)))

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
                               :request-config (read-request-config! el)
                               :carried        (resource/carry-over (du/getv el k-resource))})))

(defn- install-popstate! [^js el resource-id]
  (let [on-popstate (fn [_e] (handle-popstate! el resource-id))]
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)))

(defn- install-consumers!
  "Collect the consumers this element drives, reporting a host that drives none."
  [^js el resource-id]
  (let [consumers (collect-consumers el)]
    (when (empty? consumers)
      (js/console.error "[server-resource]" (label resource-id) "has no consumers"))
    (du/setv! el k-consumers consumers)))

(defn- boot!
  "Install everything the element holds for the life of this connection, then hand in :connected
  with the SSR embed the host carries, if any."
  [^js el resource-id]
  (install-resource! el resource-id)
  (install-popstate! el resource-id)
  (du/setv! el k-ctx (consumer-ctx el))
  (install-consumers! el resource-id)
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
         (js/console.error "[server-resource]" (label resource-id)
                           "cannot boot, these custom elements inside it are never defined:"
                           (clj->js (vec missing))))))
   undefined-tags-budget-ms))

(defn- definitions-rejected!
  "Report a boot that never ran. `whenDefined` rejects on a hyphenated tag that is not a legal
  custom element name, `font-face` and `annotation-xml` among them."
  [resource-id e]
  (js/console.error "[server-resource]" (label resource-id)
                    "cannot boot, waiting on its custom elements failed:" e))

(defn- connected! [^js el]
  (let [resource-id (model/resolve-resource-id (du/get-attr el model/attr-resource-id))
        tags        (custom-element-tags el)
        generation  (next-generation! el)]
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
                                          (apply-consumers! el
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
