(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.elements.server-resource.model :as model]
   [barebuild.recorder :as recorder]
   [barebuild.resource :as resource]
   [barebuild.transport :as transport]
   [barebuild.utils.query :as query]
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
(declare ^:private handle-event!)

(defn- construct-url-intent [resource-id]
  (let [current-url-params (js/URLSearchParams. (.-search js/location))
        prefix             (url/url-prefix resource-id)
        owned              (url/owned-url-keys resource-id
                                                 (js/Array.from (.keys current-url-params)))]
    (query/canonicalize-query
     (into {}
           (for [k owned]
             [(keyword (subs k (count prefix)))
              (.get current-url-params k)])))))

(defn- handle-popstate [^js el resource-id]
  (handle-event! el [:url-changed (construct-url-intent resource-id)]))

;; ── Transport config ─────────────────────────────────────────────────────────

(defn- json-object
  "`text` parsed as a JSON object, or nil when it does not parse or is not an object."
  [text]
  (let [parsed (try (js/JSON.parse text) (catch :default _ nil))]
    (when (object? parsed) parsed)))

(defn- read-headers
  "The static headers from the `headers` attribute. A malformed attribute is reported and treated
  as absent."
  [^js el]
  (let [text (du/get-attr el model/attr-headers)
        obj  (json-object text)]
    (when-not (or (str/blank? text) obj)
      (js/console.error "[server-resource]" model/attr-headers
                        "is not a JSON object, ignoring it:" text))
    (request/normalize-headers (js->clj obj))))

(defn- read-credentials
  "The fetch credentials mode from the `credentials` attribute. An unrecognised mode is reported
  and ignored rather than passed to fetch, which would throw on every request."
  [^js el]
  (let [text (du/get-attr el model/attr-credentials)
        mode (model/resolve-credentials text)]
    (when (and (nil? mode) (not (str/blank? text)))
      (js/console.error "[server-resource]" model/attr-credentials
                        "is not a fetch credentials mode, ignoring it:" text))
    mode))

(defn- read-timeout
  "The request budget from the `timeout` attribute. An unusable value is reported and the default
  budget kept, so a typo cannot leave requests running unbounded."
  [^js el]
  (let [text (du/get-attr el model/attr-timeout)]
    (when-not (model/valid-timeout? text)
      (js/console.error "[server-resource]" model/attr-timeout
                        "is not a number of milliseconds, keeping the default:" text))
    (model/resolve-timeout text)))

;; ── The engine ───────────────────────────────────────────────────────────────

(defn- delivery-threw!
  "A throw while delivering a result is a defect in this code, not something the transport did.
  Classifying it as a transport failure reported a server that could not be reached for a server
  that had answered."
  [e]
  (js/console.error "[server-resource] delivering a result threw:" e))

(defn- deliver-read! [^js el result request-id]
  (cond
    (:protocol-failure result) (handle-event! el [:protocol-failed (assoc result :request/id request-id)])
    (:network-failure result)  (handle-event! el [:network-failed {:request/id request-id
                                                                   :error      (:network-failure result)}])
    :else                      (handle-event! el [:response result])))

(defn- read-rejected!
  "Report a read that never arrived. An abort is intentional, a disconnect or a supersede, so it
  is not a failure."
  [^js el request-id timeout ^js e]
  (when-not (transport/abort-error? e)
    (handle-event! el [:network-failed {:request/id request-id
                                        :error      (transport/transport-error e timeout)}])))

(defn- execute-fetch!
  "Send a read and deliver what comes back. The rejection handler rides `then` rather than a
  trailing `catch`, so it sees only what the request did and never what delivering its result
  did."
  [^js el m]
  (let [controller (js/AbortController.)
        request-id (:request/id m)]
    ;; stashed before the decorator is awaited, so an abort landing in that window still reaches
    ;; this request: fetch rejects immediately on an already-aborted signal. Kept with its request
    ;; id so the :abort effect aborts the request it names rather than whatever is stashed.
    (du/setv-untraced! el k-abort {:request/id request-id :controller controller})
    (-> (.then (transport/perform! m controller)
               (fn [result] (deliver-read! el result request-id))
               (fn [^js e] (read-rejected! el request-id (:timeout m) e)))
      (.catch delivery-threw!))))

(defn- deliver-write! [^js el result write-id]
  (cond
    (:protocol-failure result) (handle-event! el [:write-failed (assoc result :request/id write-id)])
    (:network-failure result)  (handle-event! el [:write-failed {:request/id write-id
                                                                 :error    (:network-failure result)}])
    :else                      (handle-event! el [:write-ack (assoc result :request/id write-id)])))

(defn- write-rejected!
  "Report a write whose outcome the client never learned."
  [^js el write-id timeout ^js e]
  (handle-event! el [:write-failed {:request/id write-id
                                    :error      (transport/transport-error e timeout)}]))

(defn- execute-write!
  "Send a write and deliver its ack. As with a read, the rejection handler rides `then`, so a
  throw while delivering is not reported as a write that failed."
  [^js el m]
  ;; the write's controller exists only so its budget can cancel it. It is never stashed in
  ;; k-abort, so a disconnect or a superseding read still leaves an in-flight write alone
  (let [controller (js/AbortController.)
        write-id   (:request/id m)]
    (-> (.then (transport/perform! m controller)
               (fn [result] (deliver-write! el result write-id))
               (fn [^js e] (write-rejected! el write-id (:timeout m) e)))
      (.catch delivery-threw!))))

(defn- find-resource
  "The <server-resource> on the page whose resolved id is `target-id`, or nil."
  [target-id]
  (some (fn [^js e]
          (when (= target-id (:resource/id (du/getv e k-resource))) e))
        (array-seq (.querySelectorAll js/document model/tag-name))))

(defn- apply-consumer!
  "Notify one consumer, isolating a throwing applyResource so later consumers still run."
  [^js c view ctx own-id]
  (try
    (.applyResource c view ctx)
    (catch :default e
      (js/console.error "[server-resource]" (or own-id "(unnamed)")
                        "consumer applyResource threw:" e))))

(defn- notify-consumers! [^js el r]
  (let [own-id    (:resource/id r)
        consumers (du/getv el k-consumers)
        ctx       {:submit-intent! (fn [patch & [target-id]]
                                     (handle-event! el [:intent-patch
                                                        (cond-> patch
                                                          target-id (assoc :target-id target-id))]))
                   :submit-write!  (fn [payload] (handle-event! el [:submit-write payload]))}
        view      (resource/project r)]
    (doseq [^js c consumers]
      (apply-consumer! c view ctx own-id))))

(defn- notify-effect! [^js el m]
  (notify-consumers! el (:resource m)))

(defn- url-write! [^js _el m]
  (let [new-url (url/build-scoped-url (.-search js/location)
                                      (.-pathname js/location)
                                      (:resource/id m)
                                      (:params m))]
    (if (= (:mode m) :push)
      (.pushState js/history nil "" new-url)
      (.replaceState js/history nil "" new-url))))

(defn- abort-request! [^js el m]
  (when-let [pending (du/getv el k-abort)]
    (when (= (:request/id m) (:request/id pending))
      (.abort ^js (:controller pending))
      (du/setv-untraced! el k-abort nil))))

(defn- route-intent!
  "Resolve the name `step` chose to an element and hand the patch over. A name that resolves to
  nothing goes back in as an event, so the lost gesture reaches the trace."
  [^js el m]
  (if-let [^js target (find-resource (:resource/id m))]
    (handle-event! target [:intent-patch (:patch m)])
    (handle-event! el [:intent-unroutable {:resource/id (:resource/id m)}])))

(defn- diagnostic! [^js _el m]
  (if-let [detail (:detail m)]
    (js/console.debug "[server-resource]" (name (:code m)) (clj->js detail))
    (js/console.debug "[server-resource]" (name (:code m)))))

;; The executor, as data: one performer per effect tag, each taking the host and the effect value
;; and deciding nothing. `effect-handlers-cover-the-vocabulary` pins these keys against
;; resource/effect-tags, so an effect `step` learns to emit cannot go quietly unperformed.
;; Public for test purposes only
(def effect-handlers
  {:fetch            execute-fetch!
   :write            execute-write!
   :abort            abort-request!
   :url-write        url-write!
   :route-intent     route-intent!
   :notify-consumers notify-effect!
   :diagnostic       diagnostic!})

(defn- run-effects!
  [^js el effects]
  (doseq [[fx m] effects]
    (if-let [perform! (get effect-handlers fx)]
      (perform! el m)
      (js/console.error "[server-resource] no performer for effect" (str fx)))))

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
  ;; Read once at connect, like the endpoint, so the value step builds from cannot change under
  ;; an in-flight request.
  (let [resource-id    (model/resolve-resource-id (du/get-attr el model/attr-resource-id))
        history-policy {:navigation :push}
        credentials    (read-credentials el)
        headers        (read-headers el)
        timeout        (read-timeout el)
        on-popstate    (fn [_e] (handle-popstate el resource-id))
        embed          (read-boot-embed el)]
    (du/setv! el k-resource (cond-> {:resource/id    resource-id
                                     :endpoint       (du/get-attr el model/attr-src)
                                     :last-accepted  nil
                                     :url-intent     (construct-url-intent resource-id)
                                     :history-policy history-policy}
                              credentials (assoc :credentials credentials)
                              headers     (assoc :headers headers)
                              timeout     (assoc :timeout timeout)))
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)
    (let [consumers (collect-consumers el)]
      (when (empty? consumers)
        (js/console.error "[server-resource]" (or resource-id "(unnamed)") "has no consumers"))
      (du/setv! el k-consumers consumers))
    (handle-event! el [:connected {:embed embed}])))

(defn- next-generation!
  "Open a new lifecycle generation on `el` and return it. Every connect and every disconnect
  starts one, so a deferred boot can tell whether the connection it was scheduled for is still
  the current one."
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
  "Name the tags still undefined after the budget. Until every custom element inside the host is
  defined the boot cannot run, so one unregistered component leaves the resource issuing no
  request at all, with nothing to see."
  [^js el tags generation]
  (js/setTimeout
   (fn []
     (when (= generation (du/getv el k-generation))
       (when-let [missing (seq (remove #(js/customElements.get %) tags))]
         (js/console.error "[server-resource]"
                           (or (model/resolve-resource-id (du/get-attr el model/attr-resource-id))
                               "(unnamed)")
                           "cannot boot, these custom elements inside it are never defined:"
                           (clj->js (vec missing))))))
   undefined-tags-budget-ms))

(defn- connected! [^js el]
  (let [tags       (custom-element-tags el)
        generation (next-generation! el)]
    (report-undefined-tags! el tags generation)
    (-> (js/Promise.all (clj->js (map #(js/customElements.whenDefined %) tags)))
      (.then (fn []
               ;; the connection this boot was scheduled for may have ended, or been replaced by
               ;; a later one, while the definitions were awaited
               (when (= generation (du/getv el k-generation))
                 (boot! el)))))))

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
