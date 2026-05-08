(ns baredom.exports.all
  (:require [baredom.registry :as registry]))

(defn register!
  []
  (doseq [register! registry/all-registers]
    (register!)))

(defn ^:export init
  []
  (register!))
