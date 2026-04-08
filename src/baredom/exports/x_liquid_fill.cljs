(ns baredom.exports.x-liquid-fill
  (:require [baredom.components.x-liquid-fill.x-liquid-fill :as x-liquid-fill]
            [baredom.components.x-liquid-fill.model :as model]))

(defn register! []
  (x-liquid-fill/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
