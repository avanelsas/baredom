(ns app.exports.x-date-picker
  (:require [app.components.x-date-picker.model :as model]
            [app.components.x-date-picker.x-date-picker :as x]))

(defn register!
  []
  (x/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
