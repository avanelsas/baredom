(ns baredom.exports.x-metaball-cursor
  (:require [baredom.components.x-metaball-cursor.x-metaball-cursor :as x-metaball-cursor]
            [baredom.components.x-metaball-cursor.model              :as model]))

(defn register! []
  (x-metaball-cursor/init!))

(def public-api
  {:tag-name            model/tag-name
   :properties          model/property-api
   :events              model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init []
  (register!))
