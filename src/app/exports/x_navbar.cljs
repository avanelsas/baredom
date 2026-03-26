(ns app.exports.x-navbar
  (:require
   [app.components.x-navbar.x-navbar]
   [app.components.x-navbar.model :as model]))

(defn register!
  []
  (app.components.x-navbar.x-navbar/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
