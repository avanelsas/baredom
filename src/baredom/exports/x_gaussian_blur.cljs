(ns baredom.exports.x-gaussian-blur
  (:require [baredom.components.x-gaussian-blur.model :as model]
            [baredom.components.x-gaussian-blur.x-gaussian-blur]))

(defn register!
  []
  (baredom.components.x-gaussian-blur.x-gaussian-blur/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
