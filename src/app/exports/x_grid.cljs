(ns app.exports.x-grid
  (:require
   [app.components.x-grid.x-grid :as x-grid]
   [app.components.x-grid.model :as model]))

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
