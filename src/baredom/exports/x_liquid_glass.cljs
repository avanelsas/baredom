(ns baredom.exports.x-liquid-glass
  (:require [baredom.components.x-liquid-glass.x-liquid-glass :as x-liquid-glass]
            [baredom.components.x-liquid-glass.model :as model]))

(defn register! []
  (x-liquid-glass/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
