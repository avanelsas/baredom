(ns baredom.exports.x-skeleton-group
  (:require [baredom.components.x-skeleton-group.x-skeleton-group :as x-skeleton-group]
            [baredom.components.x-skeleton-group.model             :as model]))

(defn register! []
  (x-skeleton-group/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
