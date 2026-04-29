(ns baredom.exports.x-tooltip
  (:require [baredom.components.x-tooltip.x-tooltip :as x-tooltip]
            [baredom.components.x-tooltip.model      :as model]))

(defn register! []
  (x-tooltip/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
