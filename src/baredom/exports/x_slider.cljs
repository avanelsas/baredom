(ns baredom.exports.x-slider
  (:require [baredom.components.x-slider.model :as model]
            [baredom.components.x-slider.x-slider]))

(defn register!
  []
  (baredom.components.x-slider.x-slider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
