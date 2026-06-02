(ns baredom.exports.barebuild-invalidate-on
  (:require
   [baredom.components.barebuild-invalidate-on.barebuild-invalidate-on :as barebuild-invalidate-on]
   [baredom.components.barebuild-invalidate-on.model :as model]))

(defn register! []
  (barebuild-invalidate-on/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
