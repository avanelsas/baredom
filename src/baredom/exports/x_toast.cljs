(ns baredom.exports.x-toast
  (:require [baredom.components.x-toast.x-toast :as x-toast]
            [baredom.components.x-toast.model   :as model]))

(defn register! []
  (x-toast/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
