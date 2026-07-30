(ns baredom.exports.x-drop-zone
  (:require [baredom.components.x-drop-zone.x-drop-zone :as x-drop-zone]
            [baredom.components.x-drop-zone.model       :as model]))

(defn register! []
  (x-drop-zone/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
