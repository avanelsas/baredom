(ns app.exports.x-copy
  (:require [app.components.x-copy.model :as model]
            [app.components.x-copy.x-copy]))

(defn register!
  []
  (app.components.x-copy.x-copy/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
