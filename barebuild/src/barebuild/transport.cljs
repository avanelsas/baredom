(ns barebuild.transport
  "Getting the bytes. `wire` reads the envelope that came back, this reads it off the network:
  decorate the request, send it, and give up when the budget runs out or a teardown aborts.
  Knows nothing of resources, consumers, intents or the DOM."
  (:require
   [barebuild.decorator :as decorator]
   [barebuild.utils.request :as request]
   [barebuild.wire :as wire]))

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
  {:failure {:network-failure {:kind :decorator}}})

(defn- decorator-headers
  "The headers a decorator returned. A JS object is read as readily as a CLJS map, since either is
  natural to write at the edge. Nil and an empty map both mean 'attach nothing', anything else
  that is not a map is reported rather than silently attaching nothing."
  [extra]
  (let [returned (js->clj extra)]
    (when-not (or (nil? returned) (map? returned))
      (js/console.error "[server-resource] request decorator returned no map of headers:" extra))
    (request/normalize-headers returned)))

(defn- request-init
  "A promise of `{:init <fetch init>}` for request `m`, with the registered decorator's headers
  merged on top of the resource's own, or `{:failure <network-failure value>}` when the decorator
  throws or rejects, classified at the edge exactly as a bad response is. The two arms are tagged
  rather than told apart by shape, because one is a JS object and the other a CLJS map. Without a
  decorator registered there is nothing to await, so an app that uses none pays nothing."
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
  "True when `e` is a request ended on purpose, by a disconnect or a supersede, rather than one
  that failed."
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

(defn- expiring
  "A promise that rejects once `ms` has passed, aborting `controller` on the way so the socket is
  released. Stamps its handle into `timer` for the caller to cancel."
  [ms ^js controller timer]
  (js/Promise.
   (fn [_resolve reject]
     (let [e (timeout-error ms)]
       (reset! timer (js/setTimeout (fn [] (.abort controller e) (reject e)) ms))))))

(defn bounded
  "Race `operation` against the two things that end a request early: an abort, which a disconnect
  raises, and the budget running out."
  [operation ms ^js controller]
  (let [timer  (atom nil)
        racers (cond-> [operation (aborted controller)]
                 ms (conj (expiring ms controller timer)))]
    (-> (js/Promise.race (into-array racers))
      (.finally (fn [] (js/clearTimeout @timer))))))

(defn transport-error
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

(defn perform!
  "The request pipeline shared by reads and writes: decorate, send, and give up when the budget
  runs out. Resolves to a classified result, or rejects for the caller to classify."
  [m ^js controller]
  (bounded (-> (request-init m)
             (.then (fn [{:keys [init failure]}]
                      (or failure
                          (fetch-envelope (:url m)
                                          (js/Object.assign init
                                                            #js {:signal (.-signal controller)}))))))
           (:timeout m)
           controller))
