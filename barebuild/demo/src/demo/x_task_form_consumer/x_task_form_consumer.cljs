(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.x-task-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def ^:private k-button "__xConsumerButton")
(def ^:private k-modal "__xConsumerModal")
(def ^:private k-populated? "__xConsumerPopulated?")
(def ^:private k-shape "__xConsumerShape")
(def ^:private k-edit-id "__xConsumerEditId")
(def ^:private k-edit-extras "__xConsumerEditExtras")
(def ^:private k-accepted "__xConsumerAccepted")

(defn- submit! [^js e]
  (let [entered  (consumer-form/form-values e)
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)
        edit-id  (du/getv consumer k-edit-id)
        record   (merge entered (du/getv consumer k-edit-extras))]
    (when shape
      (let [payload (if edit-id
                      {:op :update :id edit-id :record record}
                      {:op :create :record record})]
        (consumer-form/attempt-write! consumer form record shape payload)))))

(defn- on-failure! [^js form failure ^js this]
  (consumer-form/on-failure! form (du/getv this k-modal) failure this))

(defn- on-writing! [^js form writing ^js this]
  (consumer-form/on-writing! form writing this (du/getv this k-button)
                             (fn [] (.hide (du/getv this k-modal)))))

(defn- prefill-form! [^js form row fields]
  (doseq [{:keys [key]} fields]
    (when-let [^js control (.querySelector form (str "[name='" key "']"))]
      (set! (.-value control) (str (get row key))))))

(defn- connect!
  [^js el]
  (let [trigger      (.querySelector el "x-button[data-role='open']")
        submit-btn   (.querySelector el "x-button[type='submit']")
        modal        (.querySelector el "x-modal")
        modal-header (.querySelector el "[data-role='title']")
        form         (.querySelector el "x-form")]

    (du/setv! el k-button submit-btn) ; on-writing! disables the submit button, not the trigger
    (du/setv! el k-modal modal)

    ;; Listeners on the consumer's own subtree (trigger, modal, form) are GC'd with it.
    (.addEventListener trigger "press"
                       (fn [_e]
                         (du/setv! el k-edit-id nil)
                         (du/setv! el k-edit-extras nil)
                         (set! (.-textContent submit-btn) "Create")
                         (du/set-attr! modal "label" "Create Task")
                         (set! (.-textContent modal-header) "Create Task")
                         (consumer-form/remove-alert! modal)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e]
                         (consumer-form/clear-form! form)))
    (let [resource (.closest el "server-resource")]
      (.addEventListener resource "x-task-edit-request"
                         (fn [^js e]
                           (let [accepted (du/getv el k-accepted)
                                 id (.. e -detail -id)
                                 id-key (get-in accepted [:shape :id-key])
                                 fields (get-in accepted [:shape :fields])
                                 row (first (filter
                                             #(= (str (get % id-key)) id)
                                             (:value accepted)))]
                             (when row
                               (du/setv! el k-edit-id id)
                               (du/setv! el k-edit-extras (select-keys row ["projectId" "assigneeId"]))
                               (set! (.-textContent submit-btn) "Update")
                               (du/set-attr! modal "label" "Edit Task")
                               (set! (.-textContent modal-header) "Edit Task")
                               (consumer-form/remove-alert! modal)
                               (prefill-form! form row fields)
                               (.show modal))))))
    (.addEventListener form "x-form-submit"
                       (fn [e]
                         (submit! e)))))

(defn- render! [^js form {accepted :accepted} ^js this]
  (du/setv! this k-accepted accepted)
  ;; populate the select + stash shape once
  (when-not (du/getv this k-populated?)
    (let [enum   (->> (get-in accepted [:shape :fields]) (filter #(= "status" (:key %))) first :enum)
          select (.querySelector form "x-select")]
      (doseq [v enum]
        (let [opt (.createElement js/document "option")]
          (set! (.-value opt) v)
          (set! (.-textContent opt) v)
          (.appendChild select opt)))
      (du/setv! this k-populated? true)
      (du/setv! this k-shape (:shape accepted)))))

(defn init!
  []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :render     render!
    :on-writing on-writing!
    :on-failure on-failure!}))
