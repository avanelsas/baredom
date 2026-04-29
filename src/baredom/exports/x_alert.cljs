(ns baredom.exports.x-alert
  (:require [baredom.components.x-alert.x-alert :as x-alert]
            [baredom.components.x-alert.model   :as model]))

(defn register! []
  (x-alert/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
