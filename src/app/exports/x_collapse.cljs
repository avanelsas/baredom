(ns app.exports.x-collapse
  (:require [app.components.x-collapse.model :as model]
            [app.components.x-collapse.x-collapse]))

(defn register!
  []
  (app.components.x-collapse.x-collapse/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
