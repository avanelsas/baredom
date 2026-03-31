(ns baredom.exports.x-drawer
  (:require [baredom.components.x-drawer.model :as model]
            [baredom.components.x-drawer.x-drawer]))

(defn register!
  []
  (baredom.components.x-drawer.x-drawer/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
