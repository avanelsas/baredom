(ns read-demo.x-stat-consumer.x-stat-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [read-demo.x-stat-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- on-pending! [^js x-stat pending _this]
  (if pending
    (du/set-attr! x-stat "loading" "")
    (du/remove-attr! x-stat "loading")))

(defn- render! [^js x-stat accepted-response _this]
  (du/set-attr! x-stat "value" (model/project-stat accepted-response)))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-stat"
    :observed-attributes model/observed-attributes
    :on-pending          on-pending!
    :render              render!}))
