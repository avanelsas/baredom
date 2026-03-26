(ns app.exports.x-toaster
  (:require
   [app.components.x-toaster.x-toaster :as x-toaster]
   [app.components.x-toaster.model     :as model]))

(defn register! []
  (x-toaster/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
