(ns app.exports.x-toast
  (:require [app.components.x-toast.x-toast :as x-toast]
            [app.components.x-toast.model   :as model]))

(defn register! []
  (x-toast/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
