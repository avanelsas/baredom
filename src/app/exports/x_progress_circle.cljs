(ns app.exports.x-progress-circle
  (:require [app.components.x-progress-circle.model :as model]
            [app.components.x-progress-circle.x-progress-circle]))

(defn register!
  []
  (app.components.x-progress-circle.x-progress-circle/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
