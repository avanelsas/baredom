(ns baredom.exports.x-tree
  (:require [baredom.components.x-tree.x-tree :as x-tree]
            [baredom.components.x-tree.model :as model]))

(defn register! []
  (x-tree/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
