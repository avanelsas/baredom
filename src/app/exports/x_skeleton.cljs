(ns app.exports.x-skeleton
  (:require [app.components.x-skeleton.model :as model]
            [app.components.x-skeleton.x-skeleton]))

(defn register!
  []
  (app.components.x-skeleton.x-skeleton/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              {}
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
