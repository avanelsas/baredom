(ns baredom.exports.x-search-field
  (:require [baredom.components.x-search-field.model :as model]
            [baredom.components.x-search-field.x-search-field]))

(defn register!
  []
  (baredom.components.x-search-field.x-search-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
