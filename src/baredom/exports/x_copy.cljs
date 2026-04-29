(ns baredom.exports.x-copy
  (:require [baredom.components.x-copy.model :as model]
            [baredom.components.x-copy.x-copy]))

(defn register!
  []
  (baredom.components.x-copy.x-copy/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
