(ns baredom.exports.x-organic-progress
  (:require [baredom.components.x-organic-progress.x-organic-progress :as x-organic-progress]
            [baredom.components.x-organic-progress.model :as model]))

(defn register! []
  (x-organic-progress/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
