(ns baredom.exports.x-sidebar
  (:require [baredom.components.x-sidebar.x-sidebar :as sidebar]
            [baredom.components.x-sidebar.model :as model]))

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
