(ns app.exports.x-menu-item
  (:require
   [app.components.x-menu-item.x-menu-item :as x-menu-item]
   [app.components.x-menu-item.model :as model]))

(defn register! []
  (x-menu-item/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
