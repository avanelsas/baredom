(ns baredom.exports.x-checkbox
  (:require [baredom.components.x-checkbox.model :as model]
            [baredom.components.x-checkbox.x-checkbox]))

(defn register!
  []
  (baredom.components.x-checkbox.x-checkbox/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
