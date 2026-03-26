(ns app.exports.x-card
  (:require [app.components.x-card.x-card :as x-card]
            [app.components.x-card.model :as model]))

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
