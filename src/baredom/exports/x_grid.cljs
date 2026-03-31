(ns baredom.exports.x-grid
  (:require
   [baredom.components.x-grid.x-grid :as x-grid]
   [baredom.components.x-grid.model :as model]))

(defn register!
  []
  (x-grid/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
