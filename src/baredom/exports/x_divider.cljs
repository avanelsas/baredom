(ns baredom.exports.x-divider
  (:require
   [baredom.components.x-divider.x-divider :as x-divider]
   [baredom.components.x-divider.model     :as model]))

(defn register! [] (x-divider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
