(ns baredom.exports.x-timeline
  (:require [baredom.components.x-timeline.x-timeline :as x-timeline]
            [baredom.components.x-timeline.model      :as model]))

(defn register! []
  (x-timeline/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
