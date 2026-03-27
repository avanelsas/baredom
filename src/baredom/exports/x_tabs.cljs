(ns baredom.exports.x-tabs
  (:require
   [baredom.components.x-tabs.x-tabs :as x-tabs]
   [baredom.components.x-tabs.model :as model]))

(defn register! []
  (x-tabs/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
