(ns baredom.exports.x-avatar
  (:require
   [baredom.components.x-avatar.x-avatar :as x-avatar]
   [baredom.components.x-avatar.model    :as model]))

(defn register! [] (x-avatar/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
