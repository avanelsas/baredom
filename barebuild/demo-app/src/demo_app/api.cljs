(ns demo-app.api
  "Write-side HTTP for the demo: one method-aware `request` against the
   single-origin JSON backend (`bb serve`). A 2xx resolves to the parsed JS body
   (nil for an empty 204/205); any non-2xx rejects. DOM-free by design — the
   caller owns the success effects (refresh a broker, close a modal, navigate).")

(defn- parse-body
  "Resolve a 2xx response to its parsed JS body, or nil for an empty body. Reads
   text first so an empty 204/205 (or a Content-Length: 0 200) yields nil rather
   than making `.json` reject on `JSON.parse(\"\")` — the same guard the read-side
   <barebuild-data> uses in settle-ok-body!."
  [^js response]
  (-> (.text response)
      (.then (fn [^js text]
               (when (pos? (.-length (.trim text)))
                 (js/JSON.parse text))))))

(defn request
  "Send `method` to `url`, JSON-encoding `data` when non-nil (omit it for DELETE).
   Returns a promise of the parsed response body, rejecting on any non-2xx so the
   caller's `.catch` is the single error path."
  [method url data]
  (let [init #js {:method method :headers #js {"Content-Type" "application/json"}}]
    (when (some? data)
      (set! (.-body init) (js/JSON.stringify data)))
    (-> (js/fetch url init)
        (.then (fn [^js response]
                 (if (.-ok response)
                   (parse-body response)
                   (throw (js/Error. (str method " " url " → " (.-status response))))))))))
