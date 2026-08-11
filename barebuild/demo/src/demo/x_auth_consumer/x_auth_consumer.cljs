(ns demo.x-auth-consumer.x-auth-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]
   [demo.alert :as alert]
   [demo.auth :as auth]
   [demo.x-auth-consumer.model :as model]))

(def ^:private sel-token "[data-token]")
(def ^:private sel-refresh "[data-action=refresh]")
(def ^:private sel-expire "[data-action=expire]")
(def ^:private sel-reauth "[data-action=reauth]")
(def ^:private sel-log "[data-log]")
(def ^:private attr-unauthorized "data-unauthorized")
(def ^:private k-listeners "__xAuthConsumerListeners")

(defn- consumer-of [^js e]
  (.closest (.-currentTarget e) model/tag-name))

(defn- paint-token! [^js this]
  (when-let [^js out (.querySelector this sel-token)]
    (set! (.-textContent out) (model/token-label (auth/current)))))

(defn- ask-again!
  [^js this]
  (consumer-resource/submit-refresh! this))

(defn- expire-token! [^js e]
  (let [^js this (consumer-of e)]
    (auth/expire!)
    (paint-token! this)
    (ask-again! this)))

(defn- reauthenticate! [^js e]
  (let [^js this (consumer-of e)]
    (auth/reauthenticate!)
    (paint-token! this)
    (ask-again! this)))

(defn- refresh! [^js e]
  (ask-again! (consumer-of e)))

(def ^:private listener-spec
  [[sel-refresh "press" refresh!]
   [sel-expire  "press" expire-token!]
   [sel-reauth  "press" reauthenticate!]])

(defn- install-listeners!
  "Install once per element. `on-connect` runs on every connectedCallback, and these bind to
  authored children that outlive a disconnect, so a second pass would double every gesture."
  [^js this]
  (when-not (du/getv this k-listeners)
    (doseq [[selector event handler] listener-spec]
      (when-let [^js target (.querySelector this selector)]
        (.addEventListener target event handler)))
    (du/setv! this k-listeners true)))

(defn- make-cell! [text]
  (let [cell (.createElement js/document "x-table-cell")]
    (set! (.-textContent cell) (str text))
    cell))

(defn- make-header-cell! [text]
  (doto (make-cell! text)
    (du/set-attr! "type" "header")
    (du/set-attr! "scope" "col")))

(defn- make-header! []
  (let [row (.createElement js/document "x-table-row")]
    (doseq [{:keys [label]} model/columns]
      (.appendChild row (make-header-cell! label)))
    row))

(defn- make-row! [cells]
  (let [row (.createElement js/document "x-table-row")]
    (doseq [text cells]
      (.appendChild row (make-cell! text)))
    row))

(defn- log-cell! [class-name text]
  (let [span (.createElement js/document "span")]
    (du/set-attr! span "class" class-name)
    (set! (.-textContent span) (str text))
    span))

(defn- make-log-line! [{:keys [token status outcome detail]}]
  (let [line (.createElement js/document "div")]
    (du/set-attr! line "class" "log-line")
    (du/set-attr! line "data-outcome" outcome)
    (doseq [[class-name text] [["log-token" token]
                               ["log-status" status]
                               ["log-outcome" outcome]
                               ["log-detail" detail]]]
      (.appendChild line (log-cell! class-name text)))
    line))

(defn- append-log!
  "Record what the decorator attached and what came back, so the page shows the exchange rather
  than asserting it."
  [^js this view]
  (when-let [^js log (.querySelector this sel-log)]
    (when-let [entry (model/log-line (auth/last-attached) view)]
      (.appendChild log (make-log-line! entry)))))

(defn- clear-table! [^js table]
  (set! (.-innerHTML table) ""))

(defn- paint-table!
  "Repaint the rows."
  [^js table accepted]
  (du/set-attr! table "columns" model/grid-template)
  (clear-table! table)
  (.appendChild table (make-header!))
  (doseq [row (model/rows accepted)]
    (.appendChild table (make-row! row))))

(defn- exchange-completed!
  "One request has ended, answered or refused."
  [^js table {:keys [pending? failure accepted] :as view} ^js this]
  (when-not pending?
    (when (and accepted (not failure))
      (paint-table! table accepted))
    (append-log! this view)))

(defn- on-failure!
  "A refused credential gets its own prompt, since the app decides what a 401 means and this
  app's answer is to offer re-authentication. Every other failure is a banner. Losing access
  empties the table"
  [^js table {:keys [failure]} ^js this]
  (cond
    (model/unauthorized? failure)
    (do (du/set-attr! this attr-unauthorized "")
        (clear-table! table)
        (alert/show! this (model/unauthorized-banner (auth/last-attached))))

    failure
    (do (du/remove-attr! this attr-unauthorized)
        (alert/show! this (model/failure-message failure)))

    :else
    (do (du/remove-attr! this attr-unauthorized)
        (alert/clear! this))))

(defn- on-connect! [_child ^js this]
  (auth/install!)
  (install-listeners! this)
  (paint-token! this))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-table"
    :on-failure on-failure!
    :on-pending exchange-completed!
    :on-connect on-connect!}))
