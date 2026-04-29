(ns baredom.exports.x-scroll-parallax
  (:require [baredom.components.x-scroll-parallax.x-scroll-parallax :as x-scroll-parallax]
            [baredom.components.x-scroll-parallax.model             :as model]))

(defn register! []
  (x-scroll-parallax/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
