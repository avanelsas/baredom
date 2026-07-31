(ns barereplay.reconstruct
  (:require [barebuild.resource :as resource]))

(defn- resource-id-of [entry]
  (:resource/id (:before entry)))

(def ^:private step-rf
  (completing (fn [r e] (:resource (resource/step r e)))))

(defn resources-at [entries n]
  ;; Recompute-from-seed per call is deliberate: a scrub is O(n) per step, no cache to invalidate.
  (let [taken (take n entries)]
    (into {}
          (map (fn [[rid group]]
                 [rid {:el    (:el (first group))
                       :value (transduce (comp (filter #(= rid (resource-id-of %)))
                                               (map :event))
                                         step-rf
                                         (:before (first group))
                                         taken)}]))
          (group-by resource-id-of entries))))
