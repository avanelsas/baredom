(ns app.exports.x-search-field
  (:require [app.components.x-search-field.model :as model]
            [app.components.x-search-field.x-search-field]))

(defn register!
  []
  (app.components.x-search-field.x-search-field/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
