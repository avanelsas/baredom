(ns app.exports.x-avatar
  (:require
   [app.components.x-avatar.x-avatar :as x-avatar]
   [app.components.x-avatar.model    :as model]))

(defn register! [] (x-avatar/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
