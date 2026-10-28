(ns baredom.exports.x-comments
  (:require [baredom.components.x-comments.x-comments :as x-comments]
            [baredom.components.x-comments.model      :as model]))

(defn register! []
  (x-comments/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
