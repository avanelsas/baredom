(ns baredom.exports.x-confetti
  (:require [baredom.components.x-confetti.x-confetti :as x-confetti]
            [baredom.components.x-confetti.model :as model]))

(defn register!
  []
  (x-confetti/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
