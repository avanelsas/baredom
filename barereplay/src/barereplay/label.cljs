(ns barereplay.label)

(defn event->label
  "A short readable label for a recorded event."
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
  [entries n]
  (let [{:keys [live? n total event]} (status entries n)]
    (str (if live? "LIVE" "REPLAYING") " " n " / " total
         (when event (str " · " (event->label event))))))

(defn detail-at [entries n]
  (when (pos? n)
    (let [{:keys [event effects]} (nth entries (dec n))
          [k payload] event
          request  (some (fn [[fx m]] (when (or (= fx :write) (= fx :fetch)) m)) effects)
          response (when (or (= k :response) (= k :write-ack)) payload)]
      (cond-> nil
        request  (assoc :request request)
        response (assoc :response response)))))

(defn clamp [n lo hi]
  (max lo (min hi n)))

(defn item-status [idx n]
  (cond (= idx n) "active"
        (< idx n) "complete"
        :else     "pending"))
