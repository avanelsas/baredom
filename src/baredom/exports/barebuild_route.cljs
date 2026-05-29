(ns baredom.exports.barebuild-route
  (:require
   [baredom.components.barebuild-route.barebuild-route :as barebuild-route]
   [baredom.components.barebuild-route.model :as model]))

(defn register! []
  (barebuild-route/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
