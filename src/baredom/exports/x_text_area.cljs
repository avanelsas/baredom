(ns baredom.exports.x-text-area
  (:require [baredom.components.x-text-area.x-text-area :as x-text-area]
            [baredom.components.x-text-area.model        :as model]))

(defn register! []
  (x-text-area/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
