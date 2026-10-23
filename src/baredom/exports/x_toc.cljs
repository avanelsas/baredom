(ns baredom.exports.x-toc
  (:require [baredom.components.x-toc.x-toc :as x-toc]
            [baredom.components.x-toc.model :as model]))

(defn register! []
  (x-toc/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
