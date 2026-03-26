(ns app.exports.x-modal
  (:require [app.components.x-modal.model :as model]
            [app.components.x-modal.x-modal]))

(defn register!
  []
  (app.components.x-modal.x-modal/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
