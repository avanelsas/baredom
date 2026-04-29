(ns baredom.exports.x-icon
  (:require [baredom.components.x-icon.x-icon :as x-icon]
            [baredom.components.x-icon.model  :as model]))

(defn register! []
  (x-icon/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
