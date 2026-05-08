(ns baredom.core
  (:require
   [baredom.registry             :as registry]
   [baredom.dev.hot-reload       :as hot-reload]
   [baredom.dev.x-debug.x-debug  :as x-debug]))

(defn ^:dev/after-load start!
  []
  (doseq [register! registry/all-registers]
    (register!))
  (x-debug/register!)
  (hot-reload/refresh-styles!))

(defn init!
  []
  (start!))
