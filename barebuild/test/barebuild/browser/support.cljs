(ns barebuild.browser.support
  "Fixtures for the browser element tests: a once-registered <server-resource>, a spy consumer
   and a throwing consumer, immediate and controlled (deferred) fetch stubs, a console.error
   capture, url / popstate / submit helpers, and mount / settle / teardown helpers."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.elements.server-resource.server-resource :as server-resource]))

(defonce spy-calls     (atom []))   ; accepted values handed to the spy consumer's render
(defonce failure-calls (atom []))   ; last-failure values handed to the spy consumer's on-failure
(defonce fetch-calls   (atom []))   ; urls the stubbed fetch received
(defonce error-calls   (atom []))   ; args passed to a stubbed console.error
(defonce aborted-calls (atom []))   ; urls whose in-flight request was aborted
(defonce pending       (atom []))   ; controlled-stub entries {:url :resolve}, awaiting resolution

(defonce real-fetch (.-fetch js/window))
(defonce real-error (.-error js/console))

;; Custom elements cannot be re-defined, so register once and reuse across tests.
(defonce registered
  (do
    (when-not (js/customElements.get "server-resource")
      (server-resource/init!))
    (when-not (js/customElements.get "x-spy-consumer")
      (consumer-resource/register!
       {:tag        "x-spy-consumer"
        :child-tag  "div"
        :render     (fn [_child accepted _this] (swap! spy-calls conj accepted))
        :on-failure (fn [_child failure _this] (swap! failure-calls conj failure))}))
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

(defn- ok-response [envelope]
  (let [json (js/JSON.stringify (clj->js envelope))]
    #js {:ok true :text (fn [] (js/Promise.resolve json))}))

(defn respond-with!
  "Stub window.fetch with `responder`: (fn [url method body]) -> envelope-map | promise<envelope-map>.
   Records the url and resolves an ok text response."
  [responder]
  (set! (.-fetch js/window)
        (fn [url ^js init]
          (swap! fetch-calls conj url)
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
        (fn [url _init]
          (swap! fetch-calls conj url)
          (js/Promise.resolve #js {:ok false :status status}))))

(defn stub-reject!
  "Stub window.fetch to reject every request, as a genuine transport failure (offline) does."
  []
  (set! (.-fetch js/window)
        (fn [url _init]
          (swap! fetch-calls conj url)
          (js/Promise.reject (js/Error. "network down")))))

(defn stub-controlled!
  "Stub window.fetch so each call is withheld in `pending` until resolve-nth! releases it, and its
   AbortSignal (if any) records the url in aborted-calls. Use to hold a request in flight."
  []
  (set! (.-fetch js/window)
        (fn [url ^js init]
          (swap! fetch-calls conj url)
          (js/Promise.
           (fn [resolve reject]
             (swap! pending conj {:url url :resolve resolve})
             (when-let [signal (some-> init .-signal)]
               (.addEventListener signal "abort"
                                  (fn []
                                    (swap! aborted-calls conj url)
                                    (reject (doto (js/Error. "aborted") (aset "name" "AbortError")))))))))))

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

(defn reset-state! []
  (reset! spy-calls [])
  (reset! failure-calls [])
  (reset! fetch-calls [])
  (reset! error-calls [])
  (reset! aborted-calls [])
  (reset! pending []))

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
  "Submit an intent patch through the host's spy consumer, as a gesture handler would."
  [^js host patch]
  (consumer-resource/submit-intent! (consumer-in host) patch))

(defn submit-write!
  "Submit a write through the host's spy consumer, as a gesture handler would."
  [^js host payload]
  (consumer-resource/submit-write! (consumer-in host) payload))

(defn mount-consumers!
  "Append a <server-resource resource-id=id src=src> hosting one consumer per tag in
   `consumer-tags` (document order), each wrapping a <div> child. Returns the host element."
  [resource-id src consumer-tags]
  (let [host (js/document.createElement "server-resource")]
    (.setAttribute host "resource-id" resource-id)
    (.setAttribute host "src" src)
    (doseq [tag consumer-tags]
      (let [consumer (js/document.createElement tag)]
        (.appendChild consumer (js/document.createElement "div"))
        (.appendChild host consumer)))
    (.appendChild js/document.body host)
    host))

(defn mount!
  "Append a <server-resource> with one spy consumer to the document, and return the host element."
  [resource-id src]
  (mount-consumers! resource-id src ["x-spy-consumer"]))

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
