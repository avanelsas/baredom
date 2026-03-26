(ns app.exports.x-table
  (:require [app.components.x-table.x-table :as x-table]
            [app.components.x-table.model   :as model]))

(defn register! []
  (x-table/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
