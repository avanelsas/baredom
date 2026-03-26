(ns app.exports.x-table-cell
  (:require [app.components.x-table-cell.x-table-cell :as x-table-cell]
            [app.components.x-table-cell.model         :as model]))

(defn register! []
  (x-table-cell/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
