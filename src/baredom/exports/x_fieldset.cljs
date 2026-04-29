(ns baredom.exports.x-fieldset
  (:require [baredom.components.x-fieldset.model :as model]
            [baredom.components.x-fieldset.x-fieldset]))

(defn register!
  []
  (baredom.components.x-fieldset.x-fieldset/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
