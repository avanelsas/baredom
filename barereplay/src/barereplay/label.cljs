(ns barereplay.label)

(defn event->label
  "A short human label for a recorded event."
  [[k payload]]
  (case k
    :connected       "connected"
    :intent-patch    (str "intent " (:query-patch payload))
    :url-changed     (str "url " payload)
    :response        (str "response " (name (:outcome payload)))
    :submit-write    (str "write " (name (:op payload))
                          (when (:id payload) (str " #" (:id payload))))
    :write-ack       (str "write-ack " (name (:outcome payload)))
    :write-failed    "write failed"
    :network-failed  "network failed"
    :protocol-failed "protocol failed"
    :disconnected    "disconnected"
    (str k)))

(defn status [entries n]
  (let [total (count entries)]
    {:live? (>= n total) :n n :total total
     :event (when (pos? n) (:event (nth entries (dec n))))}))

(defn readout
  "Status line for scrub position `n` over `entries`: LIVE at the end, else the
  REPLAYING step and the event that produced it."
  [entries n]
  (let [{:keys [live? n total event]} (status entries n)]
    (str (if live? "LIVE" "REPLAYING") " " n " / " total
         (when event (str " · " (event->label event))))))
