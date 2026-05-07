(ns baredom.exports.x-otp-input
  (:require [baredom.components.x-otp-input.model :as model]
            [baredom.components.x-otp-input.x-otp-input]))

(defn register!
  []
  (baredom.components.x-otp-input.x-otp-input/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
