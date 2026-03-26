(ns app.exports.x-checkbox
  (:require [app.components.x-checkbox.model :as model]
            [app.components.x-checkbox.x-checkbox]))

(defn register!
  []
  (app.components.x-checkbox.x-checkbox/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
