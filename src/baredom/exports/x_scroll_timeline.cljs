(ns baredom.exports.x-scroll-timeline
  (:require [baredom.components.x-scroll-timeline.x-scroll-timeline :as x-scroll-timeline]
            [baredom.components.x-scroll-timeline.model             :as model]))

(defn register! []
  (x-scroll-timeline/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
