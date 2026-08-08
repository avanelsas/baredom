(ns demo.x-spinner-consumer.x-spinner-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-spinner-consumer.model :as model]))

(defn- apply-busy!
  "Shows while the resource is reading or writing."
  [^js spinner {:keys [pending? writing?]} _this]
  (set! (.. spinner -style -display) (if (or pending? writing?) "" "none")))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-spinner"
    :on-pending apply-busy!
    :on-writing apply-busy!}))
