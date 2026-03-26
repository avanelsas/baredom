(ns app.exports.x-currency-field
  (:require [app.components.x-currency-field.model :as model]
            [app.components.x-currency-field.x-currency-field]))

(defn register!
  []
  (app.components.x-currency-field.x-currency-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
