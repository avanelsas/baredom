(ns app.exports.x-breadcrumbs
  (:require
   [app.components.x-breadcrumbs.x-breadcrumbs :as x-breadcrumbs]
   [app.components.x-breadcrumbs.model         :as model]))

(defn register! [] (x-breadcrumbs/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
