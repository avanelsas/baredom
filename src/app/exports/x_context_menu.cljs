(ns app.exports.x-context-menu
  (:require [app.components.x-context-menu.model :as model]
            [app.components.x-context-menu.x-context-menu]))

(defn register! []
  (app.components.x-context-menu.x-context-menu/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
