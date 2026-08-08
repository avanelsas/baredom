(ns demo.x-project-form-consumer.x-project-form-consumer
  "A create project consumer"
  (:require
   [demo.alert :as alert]
   [demo.consumer-form :as consumer-form]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def tag-name "x-project-form-consumer")

(def ^:private k-button "__xProjectFormButton")
(def ^:private k-modal "__xProjectFormModal")

(defn- submit! [^js e]
  (let [record   (consumer-form/form-values e)
        form     (.-currentTarget e)
        consumer (.closest form tag-name)
        shape    (get-in (consumer-resource/view consumer) [:accepted :shape])]
    (when shape
      (consumer-form/attempt-write! consumer form shape {:op :create :record record}))))

(defn- on-writing! [^js form view ^js this]
  (consumer-form/on-writing! form view this (du/getv this k-button)
                             (fn [] (.hide (du/getv this k-modal)))))

(defn- on-failure! [^js form view ^js this]
  (consumer-form/on-failure! form (du/getv this k-modal) view this))

(defn- connect! [^js form ^js el]
  (let [trigger    (.querySelector el "x-button[data-role='open']")
        submit-btn (.querySelector el "x-button[type='submit']")
        modal      (.querySelector el "x-modal")]
    (du/setv! el k-button submit-btn)
    (du/setv! el k-modal modal)
    (.addEventListener trigger "press"
                       (fn [_e]
                         (alert/clear! modal)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e] (consumer-form/clear-form! form)))
    (.addEventListener form "x-form-submit"
                       (fn [e] (submit! e)))))

;; No :render: the form only submits.
(defn init! []
  (consumer-resource/register!
   {:tag        tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :on-writing on-writing!
    :on-failure on-failure!}))
