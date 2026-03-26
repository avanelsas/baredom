(ns app.exports.x-tab
  (:require [app.components.x-tab.x-tab :as x-tab]
            [app.components.x-tab.model :as model]))

(defn register! []
  (x-tab/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
