(ns app.exports.x-slider
  (:require [app.components.x-slider.model :as model]
            [app.components.x-slider.x-slider]))

(defn register!
  []
  (app.components.x-slider.x-slider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
