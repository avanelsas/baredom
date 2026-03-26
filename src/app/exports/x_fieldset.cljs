(ns app.exports.x-fieldset
  (:require [app.components.x-fieldset.model :as model]
            [app.components.x-fieldset.x-fieldset]))

(defn register!
  []
  (app.components.x-fieldset.x-fieldset/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
