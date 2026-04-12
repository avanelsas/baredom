(ns baredom.exports.x-carousel
  (:require [baredom.components.x-carousel.x-carousel :as x-carousel]
            [baredom.components.x-carousel.model       :as model]))

(defn register! []
  (x-carousel/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
