(ns barebuild.elements.server-resource.executor
  "One performer per tag in `effect/tags`, each named after the tag it performs. Every decision was
  made inside `step` and rides the effect value, so nothing here decides anything. The three
  effects that are round trips hand their outcome back through `emit!`, and are the only three the
  table wraps."
  (:require
   [barebuild.elements.server-resource.consumers :as consumers]
   [barebuild.elements.server-resource.model :as model]
   [barebuild.transport :as transport]
   [barebuild.utils.url :as url]
   [baredom.utils.dom :as du]))

;; The in-flight read's controller. Untraced: its value is uninterpretable in a trace.
(def ^:private k-abort "__xAbort")

;; ── Effects that answer nobody ───────────────────────────────────────────────

(defn- abort! [^js el m]
  (when-let [pending (du/getv el k-abort)]
    (when (= (:request/id m) (:request/id pending))
      (.abort ^js (:controller pending))
      (du/setv-untraced! el k-abort nil))))

(defn- url-write! [^js _el m]
  (let [new-url (url/build-scoped-url (.-search js/location)
                                      (.-pathname js/location)
                                      {(:resource/id m) (:params m)})]
    (if (= (:mode m) :push)
      (.pushState js/history nil "" new-url)
      (.replaceState js/history nil "" new-url))))

(defn- notify-consumers! [^js el m]
  (consumers/apply! el (:view m) (:resource/id m)))

(defn- diagnostic! [^js _el m]
  (if-let [detail (:detail m)]
    (js/console.debug "[server-resource]" (name (:code m)) (clj->js detail))
    (js/console.debug "[server-resource]" (name (:code m)))))

;; ── Effects that come back as an event ───────────────────────────────────────

(defn- delivery-threw!
  "Report a throw while delivering a result. It is a defect in this code, not a transport
  failure."
  [e]
  (js/console.error "[server-resource] delivering a result threw:" e))

(defn- delivery-reason
  "Why a request ended, as the reason it is and the detail that reason reports. Every one is named
  by the id this client minted, never by the server's echo of it."
  [result request-id]
  (cond
    (:protocol-failure result) [:protocol (assoc result :request/id request-id)]
    (:network-failure result)  [:network {:request/id request-id
                                          :error      (:network-failure result)}]
    :else                      [:ok (assoc result :request/id request-id)]))

(defn- read-rejected!
  "Report a read that never arrived. An abort is intentional, so it is not a failure."
  [emit! ^js el request-id timeout ^js e]
  (when-not (transport/abort-error? e)
    (emit! el [:network-failed {:request/id request-id
                                :error      (transport/transport-error e timeout)}])))

(defn- write-rejected!
  "Report a write whose outcome the client never learned. An aborted write is reported where an
  aborted read is not, a write having possibly committed before it ended."
  [emit! ^js el write-id timeout ^js e]
  (emit! el [:write-failed {:request/id write-id
                            :error      (transport/transport-error e timeout)}]))

;; The two kinds of request as data: the event each reason becomes, and who reports a rejection.
;; A write reports both failures as one event, since either leaves its outcome unknown, while a
;; read tells them apart. One row per kind, so a kind cannot be paired with the other's handler.
(def ^:private delivery
  {:read  {:events    {:protocol :protocol-failed :network :network-failed :ok :response}
           :on-reject read-rejected!}
   :write {:events    {:protocol :write-failed :network :write-failed :ok :write-ack}
           :on-reject write-rejected!}})

(defn- deliver!
  "Hand what came back for a request of `kind` in as the event its reason names."
  [emit! ^js el kind result request-id]
  (let [[reason detail] (delivery-reason result request-id)]
    (emit! el [(get-in delivery [kind :events reason]) detail])))

(defn- perform-request!
  "Send `m` under `controller` and hand what comes back in as `kind`'s event. The kind's rejection
  handler rides `then` rather than a trailing `catch`, so it sees only what the request did."
  [emit! ^js el kind m ^js controller]
  (let [id        (:request/id m)
        on-reject (get-in delivery [kind :on-reject])]
    (-> (.then (transport/perform! m controller)
               (fn [result] (deliver! emit! el kind result id))
               (fn [^js e] (on-reject emit! el id (:timeout m) e)))
      (.catch delivery-threw!))))

(defn- fetch!
  "Send a read, stashing its controller so a disconnect or a supersede can abort it."
  [emit! ^js el m]
  (let [controller (js/AbortController.)]
    ;; stashed before the decorator is awaited, so an abort landing in that window still reaches
    ;; this request. Kept with its request id so the :abort effect aborts the request it names.
    (du/setv-untraced! el k-abort {:request/id (:request/id m) :controller controller})
    (perform-request! emit! el :read m controller)))

(defn- write!
  "Send a write. Its controller is never stashed in k-abort, so a disconnect or a superseding read
  leaves an in-flight write alone and only its own budget can end it."
  [emit! ^js el m]
  (perform-request! emit! el :write m (js/AbortController.)))

(defn- find-resource
  "The <server-resource> on the page whose resolved id is `target-id`, or nil. A blank name
  resolves to no id at all, so it names nothing rather than matching a blank attribute."
  [target-id]
  (when-let [id (model/resolve-resource-id target-id)]
    (.querySelector js/document
                    (str model/tag-name
                         "[" model/attr-resource-id "=\"" (js/CSS.escape id) "\"]"))))

(defn- route-intent!
  "Resolve the name `step` chose to an element and hand the patch over. A name that resolves to
  nothing goes back in as an event."
  [emit! ^js el m]
  (if-let [^js target (find-resource (:resource/id m))]
    (emit! target [:intent-patch (:patch m)])
    (emit! el [:intent-unroutable {:resource/id (:resource/id m)}])))

(defn performers
  "The effect table for an engine that takes events back through `emit!`, a fn of `[el event]`."
  [emit!]
  {:fetch            (fn [^js el m] (fetch! emit! el m))
   :write            (fn [^js el m] (write! emit! el m))
   :route-intent     (fn [^js el m] (route-intent! emit! el m))
   :abort            abort!
   :url-write        url-write!
   :notify-consumers notify-consumers!
   :diagnostic       diagnostic!})

(defn run-effects!
  "Perform `effects` against `handlers`, in the order `step` returned them."
  [handlers ^js el effects]
  (doseq [[fx m] effects]
    (if-let [perform! (get handlers fx)]
      (perform! el m)
      (js/console.error "[server-resource] no performer for effect" (str fx)))))
