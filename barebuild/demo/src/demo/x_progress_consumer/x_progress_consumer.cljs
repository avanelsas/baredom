(ns demo.x-progress-consumer.x-progress-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-progress-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- on-pending! [^js x-progress pending _this]
  (if pending
    (du/set-attr! x-progress "indeterminate" "")
    (du/remove-attr! x-progress "indeterminate")))

(defn- render! [^js x-progress accepted-response _this]
  (let [{:keys [max value]} (model/project-progress (:page-info accepted-response))]
    (du/set-attr! x-progress "max" max)
    (du/set-attr! x-progress "value" value)))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-progress"
    :observed-attributes model/observed-attributes
    :on-pending          on-pending!
    :render              render!}))
