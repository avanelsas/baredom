(ns app.exports.x-cancel-dialogue
  (:require [app.components.x-cancel-dialogue.model :as model]
            [app.components.x-cancel-dialogue.x-cancel-dialogue]))

(defn register!
  []
  (app.components.x-cancel-dialogue.x-cancel-dialogue/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
