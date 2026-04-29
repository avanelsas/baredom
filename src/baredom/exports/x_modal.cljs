(ns baredom.exports.x-modal
  (:require [baredom.components.x-modal.model :as model]
            [baredom.components.x-modal.x-modal]))

(defn register!
  []
  (baredom.components.x-modal.x-modal/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
