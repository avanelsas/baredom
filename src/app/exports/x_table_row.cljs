(ns app.exports.x-table-row
  (:require [app.components.x-table-row.x-table-row :as x-table-row]
            [app.components.x-table-row.model       :as model]))

(defn register! []
  (x-table-row/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
