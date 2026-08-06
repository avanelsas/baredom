(ns demo.x-progress-consumer.x-progress-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-progress-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- render! [^js x-progress {accepted-response :accepted} _this]
  (when accepted-response
    (let [{:keys [max value]} (model/project-progress (:page-info accepted-response))]
      (du/set-attr! x-progress "max" max)
      (du/set-attr! x-progress "value" value))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-progress"
    :render-key (fn [view] (model/project-progress (:page-info (:accepted view))))
    :render     render!}))
