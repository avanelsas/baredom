(ns baredom.exports.x-calendar
  (:require
   [baredom.components.x-calendar.x-calendar :as x-calendar]
   [baredom.components.x-calendar.model      :as model]))

(defn register! [] (x-calendar/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
