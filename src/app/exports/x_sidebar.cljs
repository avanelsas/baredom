(ns app.exports.x-sidebar
  (:require [app.components.x-sidebar.x-sidebar :as sidebar]
            [app.components.x-sidebar.model :as model]))

(defn register!
  []
  (sidebar/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
