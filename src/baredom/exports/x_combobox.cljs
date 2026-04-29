(ns baredom.exports.x-combobox
  (:require [baredom.components.x-combobox.x-combobox :as x-combobox]
            [baredom.components.x-combobox.model       :as model]))

(defn register! []
  (x-combobox/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
