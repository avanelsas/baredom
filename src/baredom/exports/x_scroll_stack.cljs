(ns baredom.exports.x-scroll-stack
  (:require [baredom.components.x-scroll-stack.x-scroll-stack :as x-scroll-stack]
            [baredom.components.x-scroll-stack.model           :as model]))

(defn register! []
  (x-scroll-stack/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
