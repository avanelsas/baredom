(ns baredom.exports.x-spotlight-card
  (:require [baredom.components.x-spotlight-card.x-spotlight-card :as x-spotlight-card]
            [baredom.components.x-spotlight-card.model :as model]))

(defn register!
  []
  (x-spotlight-card/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
