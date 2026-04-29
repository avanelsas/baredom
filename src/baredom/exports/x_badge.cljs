(ns baredom.exports.x-badge
  (:require
   [baredom.components.x-badge.x-badge :as x-badge]
   [baredom.components.x-badge.model   :as model]))

(defn register! [] (x-badge/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
