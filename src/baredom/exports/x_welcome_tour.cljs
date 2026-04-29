(ns baredom.exports.x-welcome-tour
  (:require [baredom.components.x-welcome-tour.x-welcome-tour :as x-welcome-tour]
            [baredom.components.x-welcome-tour.model :as model]
            [baredom.components.x-welcome-tour-step.x-welcome-tour-step :as x-welcome-tour-step]
            [baredom.components.x-welcome-tour-step.model :as step-model]))

(defn register! []
  (x-welcome-tour-step/init!)
  (x-welcome-tour/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes
   :step-tag-name       step-model/tag-name
   :step-properties     step-model/property-api})

(defn ^:export init []
  (register!))
