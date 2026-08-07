(ns barebuild.browser.support
  "Fixtures for the browser element tests: a once-registered <server-resource>, a spy consumer
   and a throwing consumer, immediate and controlled (deferred) fetch stubs, a console.error
   capture, url / popstate / submit helpers, and mount / settle / teardown helpers."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.decorator :as decorator]
   [barebuild.elements.server-resource.server-resource :as server-resource]
   [barebuild.recorder :as recorder]))

(defonce spy-calls     (atom []))   ; views handed to the spy consumer's render
(defonce order-calls   (atom []))   ; hook names in the order applyResource invoked them
(defonce failure-calls (atom []))   ; last-failure values handed to the spy consumer's on-failure
(defonce fetch-calls   (atom []))   ; urls the stubbed fetch received
(defonce fetch-inits   (atom []))   ; the init objects alongside them, positionally matched
(defonce error-calls   (atom []))   ; args passed to a stubbed console.error
(defonce aborted-calls (atom []))   ; urls whose in-flight request was aborted
(defonce pending       (atom []))   ; controlled-stub entries {:url :resolve}, awaiting resolution
(defonce records       (atom []))   ; recorder entries, when a test opts in with capture-records!
(defonce connect-calls (atom []))   ; [child this] pairs the connect consumer's on-connect saw
(defonce removed-host? (atom false)) ; the self-removing consumer fires once per test
(defonce writer-calls  (atom []))   ; {:tag :own? :status} each writing consumer's on-writing saw

(defonce real-fetch (.-fetch js/window))
(defonce real-error (.-error js/console))

(defn- record-writing!
  "What one writing consumer made of a view: whether the write it reports is its own, and how that
   write ended."
  [^js this view]
  (swap! writer-calls conj {:tag    (.. this -tagName toLowerCase)
                            :own?   (consumer-resource/own-write? this view)
                            :status (get-in view [:write :status])}))

;; Custom elements cannot be re-defined, so register once and reuse across tests.
(defonce registered
  (do
    (when-not (js/customElements.get "server-resource")
      (server-resource/init!))
    (when-not (js/customElements.get "x-spy-consumer")
      (consumer-resource/register!
       {:tag        "x-spy-consumer"
        :child-tag  "div"
        :render     (fn [_child view _this] (swap! spy-calls conj view))
        :on-failure (fn [_child view _this] (swap! failure-calls conj (:failure view)))}))
    (when-not (js/customElements.get "x-order-consumer")
      (consumer-resource/register!
       {:tag        "x-order-consumer"
        :child-tag  "div"
        :render     (fn [_child _view _this] (swap! order-calls conj :render))
        :on-failure (fn [_child _view _this] (swap! order-calls conj :on-failure))
        :on-pending (fn [_child _view _this] (swap! order-calls conj :on-pending))
        :on-writing (fn [_child _view _this] (swap! order-calls conj :on-writing))}))
    (when-not (js/customElements.get "x-self-removing-consumer")
      (consumer-resource/register!
       {:tag       "x-self-removing-consumer"
        :child-tag "div"
        :render    (fn [_child _view ^js this]
                     (when (compare-and-set! removed-host? false true)
                       (.remove (.closest this "server-resource"))))}))
    (when-not (js/customElements.get "x-connect-consumer")
      (consumer-resource/register!
       {:tag        "x-connect-consumer"
        :child-tag  "div"
        :on-connect (fn [child this] (swap! connect-calls conj [child this]))}))
    (doseq [tag ["x-writer-a-consumer" "x-writer-b-consumer"]]
      (when-not (js/customElements.get tag)
        (consumer-resource/register!
         {:tag        tag
          :child-tag  "div"
          :on-writing (fn [_child view ^js this] (record-writing! this view))})))
    (when-not (js/customElements.get "x-throwing-consumer")
      (consumer-resource/register!
       {:tag       "x-throwing-consumer"
        :child-tag "div"
        :render    (fn [_child _accepted _this] (throw (js/Error. "consumer boom")))}))
    true))

(defn- request-id [url]
  (second (re-find #"requestId=([^&]+)" url)))

(defn- query-from-url
  "The url's query as a string-keyed map, minus requestId, matching what the server echoes."
  [url]
  (let [params (js/URLSearchParams. (or (second (re-find #"\?(.*)$" url)) ""))]
    (reduce (fn [m k] (if (= k "requestId") m (assoc m k (.get params k))))
            {}
            (array-seq (js/Array.from (.keys params))))))

(defn accepted
  "An accepted wire envelope for `url`: request id and query echo derived from the url."
  [url value shape]
  {"outcome"   "accepted"
   "requestId" (request-id url)
   "query"     (query-from-url url)
   "value"     value
   "shape"     shape})

(defn- text-response
  "An ok response whose body reads back as `text` verbatim."
  [text]
  #js {:ok true :text (fn [] (js/Promise.resolve text))})

(defn- ok-response [envelope]
  (text-response (js/JSON.stringify (clj->js envelope))))

(defn- record-call!
  "Record one stubbed call, so every stub captures the same thing and a test can read the init
   whichever stub it installed."
  [url init]
  (swap! fetch-calls conj url)
  (swap! fetch-inits conj init))

(defn respond-with!
  "Stub window.fetch with `responder`: (fn [url method body]) -> envelope-map | promise<envelope-map>.
   Records the call and resolves an ok text response."
  [responder]
  (set! (.-fetch js/window)
        (fn [url ^js init]
          (record-call! url init)
          (let [method (or (some-> init .-method) "GET")
                body   (some-> init .-body)]
            (-> (js/Promise.resolve (responder url method body))
                (.then ok-response))))))

(defn stub-accepted!
  "Answer every request with an accepted envelope carrying `value` + `shape`, echoing the request
   id and the url's query."
  [value shape]
  (respond-with! (fn [url _method _body] (accepted url value shape))))

(defn stub-status!
  "Stub window.fetch to answer every request with a non-ok HTTP `status` and no body."
  [status]
  (set! (.-fetch js/window)
        (fn [url init]
          (record-call! url init)
          (js/Promise.resolve #js {:ok false :status status}))))

(defn stub-body!
  "Stub window.fetch to answer every request with an ok response carrying `text` verbatim, so a
   test can send a body the wire edge cannot read."
  [text]
  (set! (.-fetch js/window)
        (fn [url init]
          (record-call! url init)
          (js/Promise.resolve (text-response text)))))

(defn stub-reject!
  "Stub window.fetch to reject every request, as a genuine transport failure (offline) does."
  []
  (set! (.-fetch js/window)
        (fn [url init]
          (record-call! url init)
          (js/Promise.reject (js/Error. "network down")))))

(defn- abort-reason
  "What a real fetch rejects with when its signal aborts: the signal's own reason, which is a
   caller-supplied error when abort was given one (a timeout, say) and a plain AbortError when
   it was not."
  [^js signal]
  (or (some-> signal .-reason)
      (doto (js/Error. "aborted") (aset "name" "AbortError"))))

(defn stub-controlled!
  "Stub window.fetch so each call is withheld in `pending` until resolve-nth! releases it, and its
   AbortSignal (if any) records the url in aborted-calls. A signal that is *already* aborted when
   the call arrives rejects at once, as the real fetch does. Use to hold a request in flight."
  []
  (set! (.-fetch js/window)
        (fn [url ^js init]
          (record-call! url init)
          (js/Promise.
           (fn [resolve reject]
             (let [^js signal (some-> init .-signal)]
               (cond
                 (some-> signal .-aborted)
                 (do (swap! aborted-calls conj url) (reject (abort-reason signal)))

                 :else
                 (do (swap! pending conj {:url url :resolve resolve})
                     (when signal
                       (.addEventListener signal "abort"
                                          (fn []
                                            (swap! aborted-calls conj url)
                                            (reject (abort-reason signal)))))))))))))

(defn resolve-nth!
  "Release the n-th still-pending controlled request with an accepted envelope of value + shape."
  [n value shape]
  (let [{:keys [url resolve]} (nth @pending n)]
    (resolve (ok-response (accepted url value shape)))))

(defn restore-fetch! []
  (set! (.-fetch js/window) real-fetch))

(defn capture-errors! []
  (set! (.-error js/console)
        (fn [& args] (swap! error-calls conj (vec args)))))

(defn restore-error! []
  (set! (.-error js/console) real-error))

(defn capture-records!
  "Install a recorder so a test can assert what reached the trace, which is the only place an
   event that changes no state is visible at all."
  []
  (recorder/install! (fn [entry] (swap! records conj entry))))

(defn recorded-events
  "The event vectors the recorder saw, in order."
  []
  (mapv :event @records))

(defn reset-state! []
  (reset! spy-calls [])
  (reset! order-calls [])
  (reset! failure-calls [])
  (reset! fetch-calls [])
  (reset! fetch-inits [])
  (reset! error-calls [])
  (reset! aborted-calls [])
  (reset! pending [])
  (reset! records [])
  (reset! connect-calls [])
  (reset! removed-host? false)
  (reset! writer-calls [])
  (decorator/install! nil)
  (recorder/install! nil))

(defn reset-url! []
  (.replaceState js/history nil "" "/"))

(defn set-url!
  "Put `search` (e.g. \"?tasks.sort=owner\") on the address bar before mounting."
  [search]
  (.replaceState js/history nil "" (str "/" search)))

(defn pop-to!
  "Simulate a back/forward navigation to `search`: move the url, then fire popstate."
  [search]
  (.replaceState js/history nil "" (str "/" search))
  (.dispatchEvent js/window (js/PopStateEvent. "popstate")))

(defn- consumer-in [^js host]
  (.querySelector host "x-spy-consumer"))

(defn submit-intent!
  "Submit an intent patch through the host's spy consumer, as a gesture handler would. With a
   `target-id` the patch names a sibling resource rather than the host's own."
  ([^js host patch]
   (consumer-resource/submit-intent! (consumer-in host) patch))
  ([^js host patch target-id]
   (consumer-resource/submit-intent! (consumer-in host) patch target-id)))

(defn submit-write!
  "Submit a write through the host's spy consumer, as a gesture handler would."
  [^js host payload]
  (consumer-resource/submit-write! (consumer-in host) payload))

(defn submit-write-from!
  "Submit a write through the host's `tag` consumer, so the payload is stamped with that tag."
  [^js host tag payload]
  (consumer-resource/submit-write! (.querySelector host tag) payload))

(defn mount-consumers!
  "Append a <server-resource resource-id=id src=src> hosting one consumer per tag in
   `consumer-tags` (document order), each wrapping a <div> child. `attrs` is a map of extra
   attribute name -> value set before the element connects. Returns the host element."
  ([resource-id src consumer-tags] (mount-consumers! resource-id src consumer-tags {}))
  ([resource-id src consumer-tags attrs]
   (let [host (js/document.createElement "server-resource")]
     (.setAttribute host "resource-id" resource-id)
     (.setAttribute host "src" src)
     (doseq [[k v] attrs]
       (.setAttribute host k v))
     (doseq [tag consumer-tags]
       (let [consumer (js/document.createElement tag)]
         (.appendChild consumer (js/document.createElement "div"))
         (.appendChild host consumer)))
     (.appendChild js/document.body host)
     host)))

(defn mount!
  "Append a <server-resource> with one spy consumer to the document, and return the host element."
  ([resource-id src] (mount! resource-id src {}))
  ([resource-id src attrs] (mount-consumers! resource-id src ["x-spy-consumer"] attrs)))

(defn unmount-all! []
  (doseq [^js el (array-seq (js/document.querySelectorAll "server-resource"))]
    (.remove el)))

(defn settle
  "A promise that resolves once `pred` (a no-arg fn) returns truthy, polling on macrotasks up to
   `tries` times (default 50). Rejects on timeout so a hung pipeline fails loudly."
  ([pred] (settle pred 50))
  ([pred tries]
   (js/Promise.
    (fn [resolve reject]
      (letfn [(step [n]
                (cond
                  (pred)    (resolve true)
                  (zero? n) (reject (js/Error. "settle: predicate never became true"))
                  :else     (js/setTimeout #(step (dec n)) 0)))]
        (step tries))))))
