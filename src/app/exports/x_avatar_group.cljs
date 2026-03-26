(ns app.exports.x-avatar-group
  (:require
   [app.components.x-avatar-group.x-avatar-group :as x-avatar-group]
   [app.components.x-avatar-group.model          :as model]))

(defn register! [] (x-avatar-group/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
