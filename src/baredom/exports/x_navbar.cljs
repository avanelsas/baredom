(ns baredom.exports.x-navbar
  (:require
   [baredom.components.x-navbar.x-navbar :as x-navbar]
   [baredom.components.x-navbar.model    :as model]))

(defn register!
  []
  (x-navbar/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
