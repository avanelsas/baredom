(ns app.exports.x-text-area
  (:require [app.components.x-text-area.model :as model]
            [app.components.x-text-area.x-text-area]))

(defn register!
  []
  (app.components.x-text-area.x-text-area/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
