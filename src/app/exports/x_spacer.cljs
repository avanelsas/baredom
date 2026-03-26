(ns app.exports.x-spacer
  (:require [app.components.x-spacer.x-spacer :as x-spacer]
            [app.components.x-spacer.model    :as model]))

(defn register! []
  (x-spacer/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
