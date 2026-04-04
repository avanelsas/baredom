(ns baredom.exports.x-scroll-story
  (:require [baredom.components.x-scroll-story.x-scroll-story :as x-scroll-story]
            [baredom.components.x-scroll-story.model           :as model]))

(defn register! []
  (x-scroll-story/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
