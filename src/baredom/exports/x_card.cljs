(ns baredom.exports.x-card
  (:require [baredom.components.x-card.x-card :as x-card]
            [baredom.components.x-card.model :as model]))

(defn register!
  []
  (x-card/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
