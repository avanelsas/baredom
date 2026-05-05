(ns baredom.exports.x-i18n-provider
  (:require [baredom.components.x-i18n-provider.x-i18n-provider :as x-i18n-provider]
            [baredom.components.x-i18n-provider.model :as model]))

(defn register! []
  (x-i18n-provider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
