(ns baredom.exports.x-breadcrumbs
  (:require
   [baredom.components.x-breadcrumbs.x-breadcrumbs :as x-breadcrumbs]
   [baredom.components.x-breadcrumbs.model         :as model]))

(defn register! [] (x-breadcrumbs/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init [] (register!))
