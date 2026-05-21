(ns baredom.exports.x-split-pane
  (:require [baredom.components.x-split-pane.x-split-pane :as x-split-pane]
            [baredom.components.x-split-pane.model        :as model]))

(defn register! []
  (x-split-pane/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
