(ns app.exports.x-spinner
  (:require [app.components.x-spinner.x-spinner :as x-spinner]
            [app.components.x-spinner.model     :as model]))

(defn register! []
  (x-spinner/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
