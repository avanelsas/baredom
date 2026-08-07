(ns barebuild.transport
  "Getting the bytes: decorate the request, send it, and give up when the budget runs out or a
  teardown aborts. Knows nothing of resources, consumers, intents or the DOM."
  (:require
   [barebuild.decorator :as decorator]
   [barebuild.utils.request :as request]
   [barebuild.wire :as wire]))

(defn- fetch-init [{:keys [method headers body credentials]}]
  (clj->js (cond-> {:method method}
             headers     (assoc :headers headers)
             credentials (assoc :credentials credentials)
             body        (assoc :body (js/JSON.stringify (clj->js body))))))

(defn- network-failure
  "A network-failure marker naming `kind`. The kind is merged last, so `extra` cannot overwrite
  it."
  ([kind] (network-failure kind nil))
  ([kind extra] {:network-failure (merge extra {:kind kind})}))

(defn- decorator-failed!
  "A decorator that could not produce headers, as a network failure value. The request is never
  sent."
  [e]
  (js/console.error "[server-resource] request decorator failed, request not sent:" e)
  {:failure (network-failure :decorator)})

(defn- decorator-headers
  "The headers a decorator returned, read from either a JS object or a CLJS map. Nil and an empty
  map both attach nothing, anything else that is not a map is reported."
  [extra]
  (let [returned (js->clj extra)]
    (when-not (or (nil? returned) (map? returned))
      (js/console.error "[server-resource] request decorator returned no map of headers:" extra))
    (request/normalize-headers returned)))

(defn- request-init
  "A promise of {:init <fetch init>} for request `m`, with the registered decorator's headers
  merged on top of the resource's own, or {:failure <network-failure value>} when the decorator
  throws or rejects. The two arms are tagged rather than told apart by shape, one being a JS
  object and the other a CLJS map. Without a decorator registered there is nothing to await."
  [m]
  (if-let [decorate (decorator/current)]
    (-> (js/Promise.resolve)
      (.then (fn [] (decorate m)))
      (.then (fn [extra]
               {:init (fetch-init (request/merge-request-headers m (decorator-headers extra)))}))
      (.catch decorator-failed!))
    (js/Promise.resolve {:init (fetch-init m)})))

(def ^:private abort-error-name "AbortError")
(def ^:private timeout-error-name "TimeoutError")

(defn abort-error?
  "True when `e` is a request ended on purpose rather than one that failed."
  [^js e]
  (= abort-error-name (.-name e)))

(defn- timeout-error [ms]
  (js/DOMException. (str "request exceeded its " ms "ms budget") timeout-error-name))

(defn- aborted
  "A promise that rejects once `controller` aborts, carrying the signal's own reason."
  [^js controller]
  (js/Promise.
   (fn [_resolve reject]
     (let [^js signal (.-signal controller)
           fail       (fn [] (reject (.-reason signal)))]
       (if (.-aborted signal)
         (fail)
         (.addEventListener signal "abort" fail))))))

(defn bounded
  "Race `operation` against the two things that end a request early: an abort, and the budget
  running out. A spent budget aborts the controller with the timeout error as the reason, and
  `aborted` rejects with whatever reason the signal carries, so the budget needs no racer of its
  own and only its timer is left to clear."
  [operation ms ^js controller]
  (let [race (js/Promise.race #js [operation (aborted controller)])]
    (if-not ms
      race
      (let [timer (js/setTimeout (fn [] (.abort controller (timeout-error ms))) ms)]
        (.finally race (fn [] (js/clearTimeout timer)))))))

(defn transport-error
  "Classify a rejected request: a spent budget, or a transport failure. An abort is neither and
  reads as :offline, so a caller whose request can be aborted tests `abort-error?` first."
  [^js e ms]
  (if (= timeout-error-name (.-name e))
    {:kind :timeout :after ms}
    {:kind :offline}))

(defn- read-envelope
  "The ok response body read as text (not .json) and parsed into an envelope. Reading it as text is
  what lets `wire/parse-body` tell an absent body from an unreadable one."
  [^js resp]
  (.then (.text resp) wire/parse-body))

(defn- fetch-envelope
  "A promise of the classified outcome: a parsed envelope on a 2xx response, a network-failure
  marker carrying the HTTP status on a non-ok response, or a protocol-failure marker on an
  unparseable body. A genuine transport rejection (offline, DNS, CORS, abort) rejects the promise
  for the caller to classify."
  [url init]
  (.then (js/fetch url init)
         (fn [^js resp]
           (if (.-ok resp)
             (read-envelope resp)
             (network-failure :http-status {:status (.-status resp)})))))

(defn- send!
  "Send the decorated request under `controller`'s signal, unless decorating it already failed, in
  which case that failure is the result and nothing goes out."
  [m ^js controller {:keys [init failure]}]
  (or failure
      (fetch-envelope (:url m)
                      (js/Object.assign #js {} init #js {:signal (.-signal controller)}))))

(defn perform!
  "The request pipeline shared by reads and writes: decorate, send, and give up when the budget
  runs out. Resolves to an envelope, a {:network-failure ...} or a {:protocol-failure ...}, and
  rejects when the request never completed, for the caller to classify."
  [m ^js controller]
  (bounded (.then (request-init m) (fn [decorated] (send! m controller decorated)))
           (:timeout m)
           controller))
