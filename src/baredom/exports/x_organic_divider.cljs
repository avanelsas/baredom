(ns baredom.exports.x-organic-divider
  (:require [baredom.components.x-organic-divider.model :as model]
            [baredom.components.x-organic-divider.x-organic-divider]))

(defn register!
  []
  (baredom.components.x-organic-divider.x-organic-divider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
