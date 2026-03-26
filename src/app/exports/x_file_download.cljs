(ns app.exports.x-file-download
  (:require [app.components.x-file-download.model :as model]
            [app.components.x-file-download.x-file-download]))

(defn register!
  []
  (app.components.x-file-download.x-file-download/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :observed-attributes model/observed-attributes
   :events              model/event-schema})

(defn ^:export init
  []
  (register!))
