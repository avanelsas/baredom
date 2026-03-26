(ns app.exports.x-badge
  (:require
   [app.components.x-badge.x-badge :as x-badge]
   [app.components.x-badge.model   :as model]))

(defn register! [] (x-badge/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
