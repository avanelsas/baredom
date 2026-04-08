(ns baredom.exports.x-soft-body
  (:require [baredom.components.x-soft-body.x-soft-body :as x-soft-body]
            [baredom.components.x-soft-body.model :as model]))

(defn register! []
  (x-soft-body/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
