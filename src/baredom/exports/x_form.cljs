(ns baredom.exports.x-form
  (:require [baredom.components.x-form.model :as model]
            [baredom.components.x-form.x-form]))

(defn register!
  []
  (baredom.components.x-form.x-form/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
