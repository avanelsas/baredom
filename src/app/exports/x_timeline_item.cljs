(ns app.exports.x-timeline-item
  (:require [app.components.x-timeline-item.x-timeline-item :as x-timeline-item]
            [app.components.x-timeline-item.model           :as model]))

(defn register! []
  (x-timeline-item/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
