(ns baredom.exports.x-bento-grid
  (:require [baredom.components.x-bento-grid.x-bento-grid :as x-bento-grid]
            [baredom.components.x-bento-grid.model :as model]))

(defn register! []
  (x-bento-grid/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
