(ns baredom.exports.x-currency-field
  (:require [baredom.components.x-currency-field.model :as model]
            [baredom.components.x-currency-field.x-currency-field]))

(defn register!
  []
  (baredom.components.x-currency-field.x-currency-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
