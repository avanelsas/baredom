(ns demo.x-project-form-consumer.x-project-form-consumer
  "A create-only consumer of the PROJECTS <server-resource>. A New project button opens a
  modal form; a valid submit sends a :create write, and the projects refetch so the new
  project appears in the selector. No optimism: nothing shows until the server confirms."
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.x-project-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-button "__xProjectFormButton")
(def ^:private k-modal "__xProjectFormModal")
(def ^:private k-shape "__xProjectFormShape")

(defn- submit! [^js e]
  (let [vals     (.. e -detail -values)
        record   (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)]
    (when shape
      (consumer-form/validate-and-write! consumer form record shape {:op :create :record record}))))

(defn- on-writing! [^js form writing ^js this]
  (consumer-form/on-writing! form writing this (du/getv this k-button)
                             (fn [] (.hide (du/getv this k-modal)))))

(defn- connect! [^js el]
  (let [trigger    (.querySelector el "x-button[data-role='open']")
        submit-btn (.querySelector el "x-button[type='submit']")
        modal      (.querySelector el "x-modal")
        form       (.querySelector el "x-form")]
    (du/setv! el k-button submit-btn)
    (du/setv! el k-modal modal)
    (.addEventListener trigger "press"
                       (fn [_e]
                         (consumer-form/remove-alert! form)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e] (consumer-form/clear-form! form)))
    (.addEventListener form "x-form-submit"
                       (fn [e] (submit! e)))))

(defn- render! [^js _form accepted ^js this]
  (du/setv! this k-shape (:shape accepted)))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :render     render!
    :on-writing on-writing!
    :on-failure consumer-form/on-failure!}))
