(ns baredom.exports.x-floating-panel
  (:require [baredom.components.x-floating-panel.x-floating-panel :as x-floating-panel]
            [baredom.components.x-floating-panel.model            :as model]))

(defn register! []
  (x-floating-panel/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
