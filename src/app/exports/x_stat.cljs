(ns app.exports.x-stat
  (:require
   [app.components.x-stat.x-stat :as x-stat]
   [app.components.x-stat.model :as model]))

(defn register!
  []
  (x-stat/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
