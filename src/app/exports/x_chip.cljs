(ns app.exports.x-chip
  (:require
   [app.components.x-chip.x-chip :as x-chip]
   [app.components.x-chip.model  :as model]))

(defn register! [] (x-chip/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
