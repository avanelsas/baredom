(ns baredom.exports.x-cancel-dialogue
  (:require [baredom.components.x-cancel-dialogue.model :as model]
            [baredom.components.x-cancel-dialogue.x-cancel-dialogue]))

(defn register!
  []
  (baredom.components.x-cancel-dialogue.x-cancel-dialogue/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
