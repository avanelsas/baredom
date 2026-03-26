(ns app.exports.x-form-field
  (:require [app.components.x-form-field.model :as model]
            [app.components.x-form-field.x-form-field]))

(defn register!
  []
  (app.components.x-form-field.x-form-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
