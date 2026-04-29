(ns baredom.exports.x-file-upload
  (:require [baredom.components.x-file-upload.x-file-upload :as x-file-upload]
            [baredom.components.x-file-upload.model          :as model]))

(defn register! []
  (x-file-upload/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :methods             model/method-api
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
