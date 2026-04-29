(ns baredom.exports.x-container
  (:require
   [baredom.components.x-container.x-container]
   [baredom.components.x-container.model :as model]))

(defn register!
  []
  (baredom.components.x-container.x-container/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
