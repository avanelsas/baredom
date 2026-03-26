(ns app.exports.x-chart
  (:require [app.components.x-chart.model :as model]
            [app.components.x-chart.x-chart]))

(defn register! []
  (app.components.x-chart.x-chart/init!))

(def public-api
  {:tag-name           model/tag-name
   :properties         model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
