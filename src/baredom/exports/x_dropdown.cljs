(ns baredom.exports.x-dropdown
  (:require [baredom.components.x-dropdown.model :as model]
            [baredom.components.x-dropdown.x-dropdown]))

(defn register!
  []
  (baredom.components.x-dropdown.x-dropdown/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
