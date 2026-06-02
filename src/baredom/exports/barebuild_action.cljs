(ns baredom.exports.barebuild-action
  (:require
   [baredom.components.barebuild-action.barebuild-action :as barebuild-action]
   [baredom.components.barebuild-action.model :as model]))

(defn register! []
  (barebuild-action/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
