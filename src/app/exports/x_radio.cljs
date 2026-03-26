(ns app.exports.x-radio
  (:require [app.components.x-radio.model :as model]
            [app.components.x-radio.x-radio]))

(defn register!
  []
  (app.components.x-radio.x-radio/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
