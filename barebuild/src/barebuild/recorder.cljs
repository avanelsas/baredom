(ns barebuild.recorder)

;; An event recorder that can be used to replay events in debug mode
(defonce ^:private hook (atom nil))

(defn set-recorder! [f] (reset! hook f))

(defn record! [entry]
  (when-some [f @hook]
    (try (f entry) (catch :default _ nil)))
  nil)
