(ns barebuild.browser.support
  "Fixtures for the browser element tests: a once-registered <server-resource>, a spy consumer
   and a throwing consumer, a fetch stub that echoes the request id, a console.error capture, and
   mount / settle / teardown helpers."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.elements.server-resource.server-resource :as server-resource]))

(defonce spy-calls    (atom []))   ; accepted values handed to the spy consumer's render
(defonce fetch-calls  (atom []))   ; urls the stubbed fetch received
(defonce error-calls  (atom []))   ; args passed to a stubbed console.error

(defonce real-fetch (.-fetch js/window))
(defonce real-error (.-error js/console))

;; Custom elements cannot be re-defined, so register once and reuse across tests.
(defonce registered
  (do
    (when-not (js/customElements.get "server-resource")
      (server-resource/init!))
    (when-not (js/customElements.get "x-spy-consumer")
      (consumer-resource/register!
       {:tag       "x-spy-consumer"
        :child-tag "div"
        :render    (fn [_child accepted _this] (swap! spy-calls conj accepted))}))
    (when-not (js/customElements.get "x-throwing-consumer")
      (consumer-resource/register!
       {:tag       "x-throwing-consumer"
        :child-tag "div"
        :render    (fn [_child _accepted _this] (throw (js/Error. "consumer boom")))}))
    true))

(defn- request-id [url]
  (second (re-find #"requestId=([^&]+)" url)))

(defn stub-accepted!
  "Stub window.fetch to answer every request with `envelope` (a wire-shaped map keyed by strings,
   minus requestId), echoing the request id from the url so installable? is satisfied."
  [envelope]
  (set! (.-fetch js/window)
        (fn [url _init]
          (swap! fetch-calls conj url)
          (let [json (js/JSON.stringify (clj->js (assoc envelope "requestId" (request-id url))))]
            (js/Promise.resolve #js {:ok true :text (fn [] (js/Promise.resolve json))})))))

(defn restore-fetch! []
  (set! (.-fetch js/window) real-fetch))

(defn capture-errors! []
  (set! (.-error js/console)
        (fn [& args] (swap! error-calls conj (vec args)))))

(defn restore-error! []
  (set! (.-error js/console) real-error))

(defn reset-state! []
  (reset! spy-calls [])
  (reset! fetch-calls [])
  (reset! error-calls []))

(defn reset-url! []
  (.replaceState js/history nil "" "/"))

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
