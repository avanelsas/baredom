(ns app.exports.x-container
  (:require
   [app.components.x-container.x-container]
   [app.components.x-container.model :as model]))

(defn register!
  []
  (app.components.x-container.x-container/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
