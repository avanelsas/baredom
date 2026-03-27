(ns baredom.exports.x-navbar
  (:require
   [baredom.components.x-navbar.x-navbar]
   [baredom.components.x-navbar.model :as model]))

(defn register!
  []
  (baredom.components.x-navbar.x-navbar/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
