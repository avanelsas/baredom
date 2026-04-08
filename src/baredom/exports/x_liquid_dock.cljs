(ns baredom.exports.x-liquid-dock
  (:require [baredom.components.x-liquid-dock.x-liquid-dock :as x-liquid-dock]
            [baredom.components.x-liquid-dock.model          :as model]))

(defn register! []
  (x-liquid-dock/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
