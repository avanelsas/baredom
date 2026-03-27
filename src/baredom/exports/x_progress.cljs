(ns baredom.exports.x-progress
  (:require [baredom.components.x-progress.model :as model]
            [baredom.components.x-progress.x-progress]))

(defn register!
  []
  (baredom.components.x-progress.x-progress/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
