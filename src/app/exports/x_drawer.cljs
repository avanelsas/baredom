(ns app.exports.x-drawer
  (:require [app.components.x-drawer.model :as model]
            [app.components.x-drawer.x-drawer]))

(defn register!
  []
  (app.components.x-drawer.x-drawer/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
