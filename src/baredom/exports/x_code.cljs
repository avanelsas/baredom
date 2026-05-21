(ns baredom.exports.x-code
  (:require [baredom.components.x-code.x-code :as x-code]
            [baredom.components.x-code.model :as model]))

(defn register! []
  (x-code/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
