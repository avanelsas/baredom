(ns baredom.exports.x-morph-stack
  (:require [baredom.components.x-morph-stack.x-morph-stack :as x-morph-stack]
            [baredom.components.x-morph-stack.model         :as model]))

(defn register! []
  (x-morph-stack/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes
   :methods             [:goTo :next :prev :states]
   :slots               [model/slot-state]})

(defn ^:export init []
  (register!))
