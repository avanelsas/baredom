(ns app.exports.x-timeline
  (:require [app.components.x-timeline.x-timeline :as x-timeline]
            [app.components.x-timeline.model      :as model]))

(defn register! []
  (x-timeline/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
