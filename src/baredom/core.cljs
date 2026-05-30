(ns baredom.core
  (:require
   [baredom.registry                                :as registry]
   [baredom.dev.hot-reload                          :as hot-reload]
   [baredom.dev.x-debug.x-debug                     :as x-debug]
   [baredom.dev.x-trace-history.x-trace-history     :as x-trace-history]))

(defn ^:dev/after-load start!
  []
  (doseq [register! registry/all-registers]
    (register!))
  ;; BareBuild orchestration components are not in all-registers (the kitchen-sink
  ;; UI bundle stays pure); the dev demo registers them explicitly.
  (doseq [register! registry/barebuild-registers]
    (register!))
  (x-debug/register!)
  (x-trace-history/register!)
  (hot-reload/refresh-styles!))

(defn init!
  []
  (start!))
