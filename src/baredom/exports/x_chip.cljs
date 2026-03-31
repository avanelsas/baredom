(ns baredom.exports.x-chip
  (:require
   [baredom.components.x-chip.x-chip :as x-chip]
   [baredom.components.x-chip.model  :as model]))

(defn register! [] (x-chip/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
