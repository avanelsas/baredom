(ns app.exports.x-alert
  (:require [app.components.x-alert.x-alert :as x-alert]
            [app.components.x-alert.model   :as model]))

(defn register! []
  (x-alert/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
