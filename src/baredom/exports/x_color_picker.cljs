(ns baredom.exports.x-color-picker
  (:require [baredom.components.x-color-picker.x-color-picker :as x-color-picker]
            [baredom.components.x-color-picker.model           :as model]))

(defn register! []
  (x-color-picker/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
