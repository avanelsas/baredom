(ns baredom.exports.x-proximity-list
  (:require [baredom.components.x-proximity-list.x-proximity-list :as x-proximity-list]
            [baredom.components.x-proximity-list.model            :as model]))

(defn register! []
  (x-proximity-list/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
