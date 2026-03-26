(ns app.exports.x-tabs
  (:require
   [app.components.x-tabs.x-tabs :as x-tabs]
   [app.components.x-tabs.model :as model]))

(defn register! []
  (x-tabs/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
