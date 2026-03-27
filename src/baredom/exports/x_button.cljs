(ns baredom.exports.x-button
  (:require [baredom.components.x-button.model :as model]
            [baredom.components.x-button.x-button]))

(defn register!
  []
  (baredom.components.x-button.x-button/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
