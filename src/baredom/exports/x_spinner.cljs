(ns baredom.exports.x-spinner
  (:require [baredom.components.x-spinner.x-spinner :as x-spinner]
            [baredom.components.x-spinner.model     :as model]))

(defn register! []
  (x-spinner/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
