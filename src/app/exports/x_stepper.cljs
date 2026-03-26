(ns app.exports.x-stepper
  (:require [app.components.x-stepper.x-stepper :as x-stepper]
            [app.components.x-stepper.model     :as model]))

(defn register! []
  (x-stepper/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
