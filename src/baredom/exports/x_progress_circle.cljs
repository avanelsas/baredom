(ns baredom.exports.x-progress-circle
  (:require [baredom.components.x-progress-circle.model :as model]
            [baredom.components.x-progress-circle.x-progress-circle]))

(defn register!
  []
  (baredom.components.x-progress-circle.x-progress-circle/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
