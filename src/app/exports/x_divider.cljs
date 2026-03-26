(ns app.exports.x-divider
  (:require
   [app.components.x-divider.x-divider :as x-divider]
   [app.components.x-divider.model     :as model]))

(defn register! [] (x-divider/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
