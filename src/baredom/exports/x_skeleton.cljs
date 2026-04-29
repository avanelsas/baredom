(ns baredom.exports.x-skeleton
  (:require [baredom.components.x-skeleton.model :as model]
            [baredom.components.x-skeleton.x-skeleton]))

(defn register!
  []
  (baredom.components.x-skeleton.x-skeleton/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              {}
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
