(ns baredom.exports.x-chart
  (:require [baredom.components.x-chart.model :as model]
            [baredom.components.x-chart.x-chart]))

(defn register! []
  (baredom.components.x-chart.x-chart/init!))

(def public-api
  {:tag-name           model/tag-name
   :properties         model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
