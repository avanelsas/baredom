(ns app.exports.x-menu
  (:require
   [app.components.x-menu.x-menu :as x-menu]
   [app.components.x-menu.model :as model]))

(defn register! []
  (x-menu/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
