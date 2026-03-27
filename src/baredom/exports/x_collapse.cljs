(ns baredom.exports.x-collapse
  (:require [baredom.components.x-collapse.model :as model]
            [baredom.components.x-collapse.x-collapse]))

(defn register!
  []
  (baredom.components.x-collapse.x-collapse/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
