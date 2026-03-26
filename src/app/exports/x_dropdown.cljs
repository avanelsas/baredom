(ns app.exports.x-dropdown
  (:require [app.components.x-dropdown.model :as model]
            [app.components.x-dropdown.x-dropdown]))

(defn register!
  []
  (app.components.x-dropdown.x-dropdown/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
