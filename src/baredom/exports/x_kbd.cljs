(ns baredom.exports.x-kbd
  (:require [baredom.components.x-kbd.x-kbd :as x-kbd]
            [baredom.components.x-kbd.model :as model]))

(defn register! []
  (x-kbd/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
