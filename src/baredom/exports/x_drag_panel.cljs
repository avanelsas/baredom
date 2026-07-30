(ns baredom.exports.x-drag-panel
  (:require [baredom.components.x-drag-panel.x-drag-panel :as x-drag-panel]
            [baredom.components.x-drag-panel.model        :as model]))

(defn register! []
  (x-drag-panel/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
