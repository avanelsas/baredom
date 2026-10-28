(ns baredom.exports.x-comment
  (:require [baredom.components.x-comment.x-comment :as x-comment]
            [baredom.components.x-comment.model     :as model]))

(defn register! []
  (x-comment/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
