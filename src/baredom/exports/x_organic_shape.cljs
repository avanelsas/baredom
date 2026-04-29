(ns baredom.exports.x-organic-shape
  (:require [baredom.components.x-organic-shape.model :as model]
            [baredom.components.x-organic-shape.x-organic-shape]))

(defn register!
  []
  (baredom.components.x-organic-shape.x-organic-shape/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
