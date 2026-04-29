(ns baredom.exports.x-form-field
  (:require [baredom.components.x-form-field.model :as model]
            [baredom.components.x-form-field.x-form-field]))

(defn register!
  []
  (baredom.components.x-form-field.x-form-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
