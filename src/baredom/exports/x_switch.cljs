(ns baredom.exports.x-switch
  (:require [baredom.components.x-switch.x-switch :as x-switch]
            [baredom.components.x-switch.model    :as model]))

(defn register! []
  (x-switch/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
