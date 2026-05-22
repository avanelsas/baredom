(ns baredom.exports.x-rating
  (:require [baredom.components.x-rating.model :as model]
            [baredom.components.x-rating.x-rating]))

(defn register!
  []
  (baredom.components.x-rating.x-rating/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
