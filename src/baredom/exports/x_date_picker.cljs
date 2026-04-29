(ns baredom.exports.x-date-picker
  (:require [baredom.components.x-date-picker.model :as model]
            [baredom.components.x-date-picker.x-date-picker :as x]))

(defn register!
  []
  (x/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
