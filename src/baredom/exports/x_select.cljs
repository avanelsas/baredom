(ns baredom.exports.x-select
  (:require [baredom.components.x-select.model :as model]
            [baredom.components.x-select.x-select]))

(defn register!
  []
  (baredom.components.x-select.x-select/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
