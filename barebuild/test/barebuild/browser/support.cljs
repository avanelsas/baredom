(ns barebuild.browser.support
  "Fixtures for the browser element tests: a once-registered <server-resource> and a spy
   consumer, a fetch stub that echoes the request id, and mount / settle / teardown helpers."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.elements.server-resource.server-resource :as server-resource]))

(defonce spy-calls   (atom []))   ; accepted values handed to the spy consumer's render
(defonce fetch-calls (atom []))   ; urls the stubbed fetch received

(defonce real-fetch (.-fetch js/window))

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

(defn reset-state! []
  (reset! spy-calls [])
  (reset! fetch-calls []))

(defn reset-url! []
  (.replaceState js/history nil "" "/"))

(defn mount!
  "Append a <server-resource resource-id=id src=src> with one spy consumer to the document,
   and return the host element."
  [resource-id src]
  (let [host     (js/document.createElement "server-resource")
        consumer (js/document.createElement "x-spy-consumer")
        child    (js/document.createElement "div")]
    (.setAttribute host "resource-id" resource-id)
    (.setAttribute host "src" src)
    (.appendChild consumer child)
    (.appendChild host consumer)
    (.appendChild js/document.body host)
    host))

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
