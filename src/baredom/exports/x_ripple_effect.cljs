(ns baredom.exports.x-ripple-effect
  (:require [baredom.components.x-ripple-effect.x-ripple-effect :as x-ripple-effect]
            [baredom.components.x-ripple-effect.model            :as model]))

(defn register! []
  (x-ripple-effect/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
