(ns baredom.exports.x-popover
  (:require [baredom.components.x-popover.model :as model]
            [baredom.components.x-popover.x-popover]))

(defn register!
  []
  (baredom.components.x-popover.x-popover/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
