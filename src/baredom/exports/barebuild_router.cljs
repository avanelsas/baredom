(ns baredom.exports.barebuild-router
  (:require
   [baredom.components.barebuild-router.barebuild-router :as barebuild-router]
   [baredom.components.barebuild-router.model :as model]))

(defn register! []
  (barebuild-router/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
