(ns app.exports.x-form
  (:require [app.components.x-form.model :as model]
            [app.components.x-form.x-form]))

(defn register!
  []
  (app.components.x-form.x-form/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
