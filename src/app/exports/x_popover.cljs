(ns app.exports.x-popover
  (:require [app.components.x-popover.model :as model]
            [app.components.x-popover.x-popover]))

(defn register!
  []
  (app.components.x-popover.x-popover/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
