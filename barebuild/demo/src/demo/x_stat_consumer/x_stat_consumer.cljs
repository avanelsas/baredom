(ns demo.x-stat-consumer.x-stat-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-stat-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- render! [^js x-stat {accepted-response :accepted} _this]
  (du/set-attr! x-stat "value" (model/project-stat accepted-response)))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-stat"
    :render-key (fn [view] (model/project-stat (:accepted view)))
    :render     render!}))
