(ns baredom.exports.x-theme
  (:require [baredom.components.x-theme.x-theme :as x-theme]
            [baredom.components.x-theme.model   :as model]))

(defn register! []
  (x-theme/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export registerPreset [preset-name data]
  (model/register-preset! preset-name data))

(defn ^:export init []
  (register!))
