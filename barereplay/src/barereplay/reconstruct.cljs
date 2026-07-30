(ns barereplay.reconstruct
  (:require [barebuild.resource :as resource]))

(defn- resource-id-of [entry]
  (:resource/id (:before entry)))

(defn- fold [seed events]
  (reduce (fn [r e] (:resource (resource/step r e))) seed events))

(defn resources-at [entries n]
  (let [taken (take n entries)]
    (into {}
          (map (fn [[rid group]]
                 (let [events (->> taken
                                   (filter #(= rid (resource-id-of %)))
                                   (map :event))]
                   [rid {:el    (:el (first group))
                         :value (fold (:before (first group)) events)}])))
          (group-by resource-id-of entries))))
