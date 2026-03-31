(ns baredom.exports.x-notification-center
  (:require [baredom.components.x-notification-center.x-notification-center :as x-notification-center]
            [baredom.components.x-notification-center.model                 :as model]))

(defn register! []
  (x-notification-center/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
