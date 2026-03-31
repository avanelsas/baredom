(ns baredom.exports.x-spacer
  (:require [baredom.components.x-spacer.x-spacer :as x-spacer]
            [baredom.components.x-spacer.model    :as model]))

(defn register! []
  (x-spacer/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
