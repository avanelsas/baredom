(ns baredom.exports.x-i18n
  (:require [baredom.components.x-i18n.x-i18n :as x-i18n]
            [baredom.components.x-i18n.model :as model]))

(defn register! []
  (x-i18n/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
