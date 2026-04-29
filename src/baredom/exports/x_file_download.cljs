(ns baredom.exports.x-file-download
  (:require [baredom.components.x-file-download.model :as model]
            [baredom.components.x-file-download.x-file-download]))

(defn register!
  []
  (baredom.components.x-file-download.x-file-download/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :methods             model/method-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
