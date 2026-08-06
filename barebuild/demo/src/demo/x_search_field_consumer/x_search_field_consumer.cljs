(ns demo.x-search-field-consumer.x-search-field-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-search-field-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- on-connect! [^js x-search-field ^js el]
  ;; These event listeners are disposed of automatically
  (.addEventListener x-search-field "x-search-field-input"
                     (fn [e]
                       (consumer-resource/submit-intent! el (model/translate-search-gesture (.. e -detail -value)))))
  (.addEventListener x-search-field "x-search-field-clear"
                     (fn [_e]
                       (consumer-resource/submit-intent! el (model/translate-search-gesture "")))))

(defn- render! [^js x-search-field {accepted-response :accepted} _this]
  (let [term    (or (model/project-search-value accepted-response) "")
        current (.-value x-search-field)]
    (when (and (not= term current)
               (not= x-search-field (.-activeElement js/document))) ; user is not typing
      (du/set-attr! x-search-field "value" term))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-search-field"
    :render-key (fn [view] (model/project-search-value (:accepted view)))
    :render     render!
    :on-connect on-connect!}))
