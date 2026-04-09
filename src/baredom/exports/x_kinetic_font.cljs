(ns baredom.exports.x-kinetic-font
  (:require [baredom.components.x-kinetic-font.model :as model]
            [baredom.components.x-kinetic-font.x-kinetic-font]))

(defn register!
  []
  (baredom.components.x-kinetic-font.x-kinetic-font/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
