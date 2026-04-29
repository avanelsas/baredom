(ns baredom.exports.x-neural-glow
  (:require [baredom.components.x-neural-glow.x-neural-glow :as x-neural-glow]
            [baredom.components.x-neural-glow.model          :as model]))

(defn register! []
  (x-neural-glow/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
