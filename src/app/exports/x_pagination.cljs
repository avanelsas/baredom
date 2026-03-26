(ns app.exports.x-pagination
  (:require
   [app.components.x-pagination.x-pagination :as x-pagination]
   [app.components.x-pagination.model        :as model]))

(defn register! [] (x-pagination/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
