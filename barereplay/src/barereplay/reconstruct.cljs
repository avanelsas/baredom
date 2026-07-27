(ns barereplay.reconstruct
  (:require [barebuild.resource :as resource]))

(defn- reconstruct
  "The resource value after replaying the first `n` events onto `seed`."
  [seed events n]
  (reduce (fn [r e] (:resource (resource/step r e)))
          seed
          (take n events)))

(defn resource-at
  "The resource value the log holds after its first `n` entries, replayed from seed."
  [entries n]
  (reconstruct (:before (first entries))
               (map :event entries)
               n))
