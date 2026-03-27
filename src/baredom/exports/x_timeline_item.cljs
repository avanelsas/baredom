(ns baredom.exports.x-timeline-item
  (:require [baredom.components.x-timeline-item.x-timeline-item :as x-timeline-item]
            [baredom.components.x-timeline-item.model           :as model]))

(defn register! []
  (x-timeline-item/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
