(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.decorator :as decorator]
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
    (model/normalize-headers (js->clj obj))))

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

(defn- read-transport
  "The transport config every request this resource issues will carry. Read once at connect,
  like the endpoint, so the value step builds from cannot change under an in-flight request."
  [^js el]
  (model/transport (read-credentials el) (read-headers el) (read-timeout el)))

;; ── The engine ───────────────────────────────────────────────────────────────

(defn- fetch-init [{:keys [method headers body credentials]}]
  (clj->js (cond-> {:method method}
             headers     (assoc :headers headers)
             credentials (assoc :credentials credentials)
             body        (assoc :body (js/JSON.stringify (clj->js body))))))

(defn- decorator-failed!
  "Classify a decorator that could not produce headers as a network failure value, so a consumer
  branches on it like any other. The request is never sent."
  [e]
  (js/console.error "[server-resource] request decorator failed, request not sent:" e)
  {:network-failure {:kind :decorator}})

(defn- decorator-headers
  "The headers a decorator returned. A JS object is read as readily as a CLJS map, since either is
  natural to write at the edge. Nil and an empty map both mean 'attach nothing', anything else
  that is not a map is reported rather than silently attaching nothing."
  [extra]
  (let [returned (js->clj extra)]
    (when-not (or (nil? returned) (map? returned))
      (js/console.error "[server-resource] request decorator returned no map of headers:" extra))
    (model/normalize-headers returned)))

(defn- request-init
  "A promise of the fetch init for request `m`, with the registered decorator's headers merged on
  top of the resource's own. Resolves to a network-failure value when the decorator throws or
  rejects, classified at the edge exactly as a bad response is. Without a decorator registered
  there is nothing to await, so an app that uses none pays nothing."
  [m]
  (if-let [decorate (decorator/current)]
    (-> (js/Promise.resolve)
        (.then (fn [] (decorate m)))
        (.then (fn [extra]
                 (fetch-init (utils/merge-request-headers m (decorator-headers extra)))))
        (.catch decorator-failed!))
    (js/Promise.resolve (fetch-init m))))

(def ^:private abort-error-name "AbortError")
(def ^:private timeout-error-name "TimeoutError")

(defn- timeout-error [ms]
  (js/DOMException. (str "request exceeded its " ms "ms budget") timeout-error-name))

(defn- aborted
  "A promise that rejects once `controller` aborts, carrying the signal's own reason. Without it a
  teardown could not end an operation that has no fetch to reject yet, a decorator still waiting
  on its token, and the request would stay pending until its budget elapsed."
  [^js controller]
  (js/Promise.
   (fn [_resolve reject]
     (let [^js signal (.-signal controller)
           fail       (fn [] (reject (.-reason signal)))]
       (if (.-aborted signal)
         (fail)
         (.addEventListener signal "abort" fail))))))

(defn- expiring
  "A promise that rejects once `ms` has passed, aborting `controller` on the way so the socket is
  released. The same TimeoutError travels both paths, so whichever of the racers settles first
  classifies identically. Stamps its handle into `timer` for the caller to cancel."
  [ms ^js controller timer]
  (js/Promise.
   (fn [_resolve reject]
     (let [e (timeout-error ms)]
       (reset! timer (js/setTimeout (fn [] (.abort controller e) (reject e)) ms))))))

(defn- bounded
  "Race `operation` against the two things that end a request early: an abort, which a disconnect
  raises, and the budget running out. Racing rather than leaving it to the fetch to reject is what
  covers a decorator that never settles, where there is no fetch yet for either to cancel."
  [operation ms ^js controller]
  (let [timer  (atom nil)
        racers (cond-> [operation (aborted controller)]
                 ms (conj (expiring ms controller timer)))]
    (-> (js/Promise.race (into-array racers))
        (.finally (fn [] (js/clearTimeout @timer))))))

(defn- transport-error
  "Classify a rejected request: a spent budget, or a transport failure."
  [^js e ms]
  (if (= timeout-error-name (.-name e))
    {:kind :timeout :after ms}
    {:kind :offline}))

(defn- parse-body
  "Read the ok response body as text (not .json) and parse it into an envelope. A nil body parses
  to an empty-body protocol marker."
  [^js resp]
  (.then (.text resp)
         (fn [^js body]
           (wire/parse-envelope (try (js/JSON.parse body) (catch :default _ nil))))))

(defn- fetch-envelope
  "Fetch and classify the outcome as a value: a parsed envelope on a 2xx response, a
  network-failure marker carrying the HTTP status on a non-ok response, or a protocol-failure
  marker on an unparseable body. A genuine transport rejection (offline, DNS, CORS, abort) rejects
  the promise and is classified by the caller. Returns a promise of the classified result."
  [url init]
  (.then (js/fetch url init)
         (fn [^js resp]
           (if (.-ok resp)
             (parse-body resp)
             {:network-failure {:kind :http-status :status (.-status resp)}}))))

(defn- deliver-read! [^js el result request-id]
  (cond
    (:protocol-failure result) (handle-event! el [:protocol-failed (assoc result :request/id request-id)])
    (:network-failure result)  (handle-event! el [:network-failed {:request/id request-id
                                                                    :error      (:network-failure result)}])
    :else                      (handle-event! el [:response result])))

(defn- perform!
  "The request pipeline shared by reads and writes: decorate, send, and give up when the budget
  runs out. Resolves to a classified result, or rejects for the caller to classify."
  [m ^js controller]
  (bounded (-> (request-init m)
               (.then (fn [init]
                        (if (:network-failure init)
                          init
                          (fetch-envelope (:url m)
                                          (js/Object.assign init
                                                            #js {:signal (.-signal controller)}))))))
           (:timeout m)
           controller))

(defn- execute-fetch! [^js el m]
  (let [controller (js/AbortController.)
        request-id (:request/id m)]
    ;; stashed before the decorator is awaited, so an abort landing in that window still reaches
    ;; this request: fetch rejects immediately on an already-aborted signal
    (du/setv-untraced! el k-abort controller)
    (-> (perform! m controller)
      (.then (fn [result] (deliver-read! el result request-id)))
      (.catch (fn [^js e]
                ;; an aborted fetch is intentional (disconnect / supersede), not a failure
                (when-not (= abort-error-name (.-name e))
                  (handle-event! el [:network-failed {:request/id request-id
                                                      :error      (transport-error e (:timeout m))}])))))))

(defn- deliver-write! [^js el result write-id]
  (cond
    (:protocol-failure result) (handle-event! el [:write-failed (assoc result :write/id write-id)])
    (:network-failure result)  (handle-event! el [:write-failed {:write/id write-id
                                                                  :error    (:network-failure result)}])
    :else                      (handle-event! el [:write-ack (assoc result :write/id write-id)])))

(defn- execute-write! [^js el m]
  ;; the write's controller exists only so its budget can cancel it. It is never stashed in
  ;; k-abort, so a disconnect or a superseding read still leaves an in-flight write alone
  (let [controller (js/AbortController.)
        write-id   (:write/id m)]
    (-> (perform! m controller)
      (.then (fn [result] (deliver-write! el result write-id)))
      (.catch (fn [^js e]
                (handle-event! el [:write-failed {:write/id write-id
                                                  :error    (transport-error e (:timeout m))}]))))))

(defn- find-resource
  "The <server-resource> on the page whose resolved id is `target-id`, or nil."
  [target-id]
  (some (fn [^js e]
          (when (= target-id (:resource/id (du/getv e k-resource))) e))
        (array-seq (.querySelectorAll js/document model/tag-name))))

(defn- apply-consumer!
  "Notify one consumer, isolating a throwing applyResource so later consumers still run."
  [^js c r ctx own-id]
  (try
    (.applyResource c r ctx)
    (catch :default e
      (js/console.error "[server-resource]" (or own-id "(unnamed)")
                        "consumer applyResource threw:" e))))

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
      (apply-consumer! c r ctx own-id))))

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
        transport      (read-transport el)
        on-popstate    (fn [_e] (handle-popstate el resource-id))
        embed          (read-boot-embed el)]
    (du/setv! el k-resource (cond-> {:resource/id    resource-id
                                     :endpoint       (du/get-attr el model/attr-src)
                                     :last-accepted  nil
                                     :url-intent     (construct-url-intent resource-id)
                                     :history-policy history-policy}
                              transport (assoc :transport transport)))
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)
    (let [consumers (collect-consumers el)]
      (when (empty? consumers)
        (js/console.error "[server-resource]" (or resource-id "(unnamed)") "has no consumers"))
      (du/setv! el k-consumers consumers))
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
