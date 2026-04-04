(ns baredom.exports.x-typography
  (:require [baredom.components.x-typography.x-typography :as x-typography]
            [baredom.components.x-typography.model         :as model]))

(defn register! []
  (x-typography/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
