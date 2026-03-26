(ns app.exports.x-progress
  (:require [app.components.x-progress.model :as model]
            [app.components.x-progress.x-progress]))

(defn register!
  []
  (app.components.x-progress.x-progress/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
