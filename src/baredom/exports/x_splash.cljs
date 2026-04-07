(ns baredom.exports.x-splash
  (:require [baredom.components.x-splash.x-splash :as x-splash]
            [baredom.components.x-splash.model     :as model]))

(defn register! []
  (x-splash/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
