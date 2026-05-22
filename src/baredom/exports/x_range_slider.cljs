(ns baredom.exports.x-range-slider
  (:require [baredom.components.x-range-slider.model :as model]
            [baredom.components.x-range-slider.x-range-slider]))

(defn register!
  []
  (baredom.components.x-range-slider.x-range-slider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
