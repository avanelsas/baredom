(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.selector :as selector]
   [demo.x-task-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def ^:private k-refs "__xConsumerRefs")
(def ^:private k-populated? "__xConsumerPopulated?")
(def ^:private k-edit-id "__xConsumerEditId")
(def ^:private k-edit-extras "__xConsumerEditExtras")

(defn- submit! [^js e]
  (let [entered  (consumer-form/form-values e)
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (get-in (consumer-resource/view consumer) [:accepted :shape])
        edit-id  (du/getv consumer k-edit-id)
        record   (merge entered (du/getv consumer k-edit-extras))]
    (when shape
      (let [payload (if edit-id
                      {:op :update :id edit-id :record record}
                      {:op :create :record record})]
        (consumer-form/attempt-write! consumer form record shape payload)))))

(defn- refs
  "The consumer's own elements, found once at connect."
  [^js el]
  (du/getv el k-refs))

(defn- on-failure! [^js form view ^js this]
  (consumer-form/on-failure! form (:modal (refs this)) view this))

(defn- on-writing! [^js form view ^js this]
  (let [{:keys [^js modal submit]} (refs this)]
    (consumer-form/on-writing! form view this submit (fn [] (.hide modal)))))

(defn- prefill-form! [^js form row fields]
  (doseq [{:keys [key]} fields]
    (when-let [^js control (.querySelector form (selector/attr= "name" key))]
      (set! (.-value control) (str (get row key))))))

(defn- find-refs [^js el]
  {:trigger (.querySelector el "x-button[data-role='open']")
   ;; on-writing! disables the submit button, not the trigger
   :submit  (.querySelector el "x-button[type='submit']")
   :modal   (.querySelector el "x-modal")
   :header  (.querySelector el "[data-role='title']")
   :form    (.querySelector el "x-form")})

(defn- apply-mode!
  "Dress the modal as a create or an edit, dropping any alert from last time."
  [^js el verb title]
  (let [{:keys [^js modal ^js submit ^js header]} (refs el)]
    (set! (.-textContent submit) verb)
    (du/set-attr! modal "label" title)
    (set! (.-textContent header) title)
    (consumer-form/remove-alert! modal)))

(defn- open-create! [^js el]
  (du/setv! el k-edit-id nil)
  (du/setv! el k-edit-extras nil)
  (apply-mode! el "Create" "Create Task")
  (.show ^js (:modal (refs el))))

(defn- open-edit! [^js el id]
  (let [accepted (:accepted (consumer-resource/view el))]
    (when-let [row (model/row-by-id accepted id)]
      (du/setv! el k-edit-id id)
      (du/setv! el k-edit-extras (select-keys row ["projectId" "assigneeId"]))
      (apply-mode! el "Update" "Edit Task")
      (prefill-form! (:form (refs el)) row (get-in accepted [:shape :fields]))
      (.show ^js (:modal (refs el))))))

(defn- connect!
  [_child ^js el]
  (du/setv! el k-refs (find-refs el))
  ;; own-subtree listeners are GC'd with the consumer. The edit request is not: the table
  ;; consumer dispatches it on the resource.
  (let [{:keys [^js trigger ^js modal ^js form]} (refs el)]
    (.addEventListener trigger "press" (fn [_e] (open-create! el)))
    (.addEventListener modal "x-modal-dismiss" (fn [_e] (consumer-form/clear-form! form)))
    (.addEventListener form "x-form-submit" (fn [e] (submit! e)))
    (.addEventListener (.closest el "server-resource") "x-task-edit-request"
                       (fn [^js e] (open-edit! el (.. e -detail -id))))))

(defn- make-option! [{:keys [value label]}]
  (let [opt (.createElement js/document "option")]
    (set! (.-value opt) value)
    (set! (.-textContent opt) label)
    opt))

(defn- render! [^js form {accepted :accepted} ^js this]
  ;; populate once, and not before a response, or it stays empty for good
  (when (and accepted (not (du/getv this k-populated?)))
    (let [^js select (.querySelector form "x-select")]
      (doseq [choice (model/status-choices accepted)]
        (.appendChild select (make-option! choice)))
      (du/setv! this k-populated? true))))

(defn init!
  []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :render     render!
    :on-writing on-writing!
    :on-failure on-failure!}))
