(ns app.exports.x-button
  (:require [app.components.x-button.model :as model]
            [app.components.x-button.x-button]))

(defn register!
  []
  (app.components.x-button.x-button/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
