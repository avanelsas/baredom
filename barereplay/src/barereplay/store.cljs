(ns barereplay.store)

;; save all recordings in a store
(defonce ^:private log (atom []))

(defn record! [entry] (swap! log conj entry))
(defn entries [] @log)
(defn clear!  [] (reset! log []))
