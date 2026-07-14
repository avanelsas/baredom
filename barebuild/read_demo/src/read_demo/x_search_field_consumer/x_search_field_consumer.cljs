(ns read-demo.x-search-field-consumer.x-search-field-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [read-demo.x-search-field-consumer.model :as model]
   [baredom.utils.dom :as du]))

(defn- on-pending! [^js x-search-field pending _this]
  (if pending
    (du/set-attr! x-search-field "aria-busy" "true")
    (du/remove-attr! x-search-field "aria-busy")))

(defn- on-connect! [^js el]
  (let [x-search-field (.querySelector el "x-search-field")]
    ;; These event listeners are disposed of automatically
    (.addEventListener x-search-field "x-search-field-input"
                       (fn [e]
                         (consumer-resource/submit-intent! el (model/translate-search-gesture (.. e -detail -value)))))
    (.addEventListener x-search-field "x-search-field-clear"
                       (fn [_e]
                         (consumer-resource/submit-intent! el (model/translate-search-gesture ""))))))

(defn- render! [^js x-search-field accepted-response _this]
  (let [term    (or (model/project-search-value accepted-response) "")
        current (.-value x-search-field)]
    (when (and (not= term current)
               (not= x-search-field (.-activeElement js/document))) ; user is not typing
      (du/set-attr! x-search-field "value" term))))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-search-field"
    :observed-attributes model/observed-attributes
    :on-pending          on-pending!
    :render              render!
    :on-connect          on-connect!}))
