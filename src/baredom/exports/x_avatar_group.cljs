(ns baredom.exports.x-avatar-group
  (:require
   [baredom.components.x-avatar-group.x-avatar-group :as x-avatar-group]
   [baredom.components.x-avatar-group.model          :as model]))

(defn register! [] (x-avatar-group/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
