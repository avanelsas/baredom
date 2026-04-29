(ns baredom.exports.x-bento-item
  (:require [baredom.components.x-bento-item.x-bento-item :as x-bento-item]
            [baredom.components.x-bento-item.model :as model]))

(defn register! []
  (x-bento-item/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
