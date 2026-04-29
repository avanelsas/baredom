(ns baredom.exports.x-command-palette
  (:require [baredom.components.x-command-palette.model :as model]
            [baredom.components.x-command-palette.x-command-palette :as x]))

(defn register!
  []
  (x/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
