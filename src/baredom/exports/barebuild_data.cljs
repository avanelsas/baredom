(ns baredom.exports.barebuild-data
  (:require
   [baredom.components.barebuild-data.barebuild-data :as barebuild-data]
   [baredom.components.barebuild-data.model :as model]))

(defn register! []
  (barebuild-data/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
