(ns baredom.exports.x-table
  (:require [baredom.components.x-table.x-table :as x-table]
            [baredom.components.x-table.model   :as model]))

(defn register! []
  (x-table/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
