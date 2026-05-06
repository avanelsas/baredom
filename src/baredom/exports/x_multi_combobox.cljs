(ns baredom.exports.x-multi-combobox
  (:require [baredom.components.x-multi-combobox.x-multi-combobox :as x-multi-combobox]
            [baredom.components.x-multi-combobox.model            :as model]))

(defn register! []
  (x-multi-combobox/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
