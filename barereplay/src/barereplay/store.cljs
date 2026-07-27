(ns barereplay.store)

;; save all recordings in a store
(defonce ^:private log (atom []))

(defn record! [entry] (swap! log conj entry))
(defn entries [] @log)
(defn clear!  [] (reset! log []))
(defn subscribe!   [f] (add-watch log ::sub (fn [_ _ _ entries] (f entries))))
(defn unsubscribe! []  (remove-watch log ::sub))
