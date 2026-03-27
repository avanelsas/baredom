(ns baredom.exports.x-pagination
  (:require
   [baredom.components.x-pagination.x-pagination :as x-pagination]
   [baredom.components.x-pagination.model        :as model]))

(defn register! [] (x-pagination/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
