(ns app.exports.x-select
  (:require [app.components.x-select.model :as model]
            [app.components.x-select.x-select]))

(defn register!
  []
  (app.components.x-select.x-select/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
