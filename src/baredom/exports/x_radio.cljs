(ns baredom.exports.x-radio
  (:require [baredom.components.x-radio.model :as model]
            [baredom.components.x-radio.x-radio]))

(defn register!
  []
  (baredom.components.x-radio.x-radio/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
