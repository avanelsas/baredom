(ns baredom.exports.x-particle-button
  (:require [baredom.components.x-particle-button.model :as model]
            [baredom.components.x-particle-button.x-particle-button]))

(defn register!
  []
  (baredom.components.x-particle-button.x-particle-button/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
