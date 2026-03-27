(ns baredom.exports.x-stepper
  (:require [baredom.components.x-stepper.x-stepper :as x-stepper]
            [baredom.components.x-stepper.model     :as model]))

(defn register! []
  (x-stepper/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
