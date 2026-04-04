(ns baredom.exports.x-scroll
  (:require [baredom.components.x-scroll.x-scroll :as x-scroll]
            [baredom.components.x-scroll.model     :as model]))

(defn register! []
  (x-scroll/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
