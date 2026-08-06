(ns demo.x-spinner-consumer.x-spinner-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-spinner-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-pending "__xSpinnerConsumerPending")
(def ^:private k-writing "__xSpinnerConsumerWriting")

(defn- apply-busy! [^js spinner busy]
  (set! (.. spinner -style -display) (if busy "" "none")))

(defn- on-pending! [^js spinner pending ^js this]
  (du/setv! this k-pending pending)
  (apply-busy! spinner (or pending (du/getv this k-writing))))

(defn- on-writing! [^js spinner writing ^js this]
  (du/setv! this k-writing writing)
  (apply-busy! spinner (or writing (du/getv this k-pending))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-spinner"
    :on-pending on-pending!
    :on-writing on-writing!}))
