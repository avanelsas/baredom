(ns baredom.exports.x-kinetic-canvas
  (:require [baredom.components.x-kinetic-canvas.x-kinetic-canvas :as x-kinetic-canvas]
            [baredom.components.x-kinetic-canvas.model :as model]))

(defn register! []
  (x-kinetic-canvas/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
