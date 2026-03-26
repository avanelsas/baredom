(ns app.exports.x-command-palette
  (:require [app.components.x-command-palette.model :as model]
            [app.components.x-command-palette.x-command-palette :as x]))

(defn register!
  []
  (x/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
