(ns baredom.exports.x-image
  (:require [baredom.components.x-image.x-image :as x-image]
            [baredom.components.x-image.model   :as model]))

(defn register! []
  (x-image/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
