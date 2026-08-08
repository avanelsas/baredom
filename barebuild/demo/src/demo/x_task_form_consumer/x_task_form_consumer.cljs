(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [demo.alert :as alert]
   [demo.consumer-form :as consumer-form]
   [demo.selector :as selector]
   [demo.task-edit :as task-edit]
   [demo.x-task-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def ^:private k-refs "__xTaskFormRefs")
(def ^:private k-edit-id "__xTaskFormEditId")
(def ^:private k-edit-extras "__xTaskFormEditExtras")

(defn- refs
  "The consumer's own elements, found once at connect."
  [^js el]
  (du/getv el k-refs))

(defn- submit! [^js e]
  (let [entered  (consumer-form/form-values e)
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (get-in (consumer-resource/view consumer) [:accepted :shape])
        edit-id  (du/getv consumer k-edit-id)
        record   (merge entered (du/getv consumer k-edit-extras))]
    (when shape
      (consumer-form/attempt-write!
       consumer form shape
       (if edit-id
         {:op :update :id edit-id :record record}
         {:op :create :record record})))))

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
    (alert/clear! modal)))

(defn- open-create! [^js el]
  (du/setv! el k-edit-id nil)
  (du/setv! el k-edit-extras nil)
  (apply-mode! el "Create" "Create Task")
  (.show ^js (:modal (refs el))))

(defn- open-edit! [^js el id]
  (let [accepted (:accepted (consumer-resource/view el))]
    (when-let [row (model/row-by-id accepted id)]
      (du/setv! el k-edit-id id)
      (du/setv! el k-edit-extras (model/unshown-fields row))
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
    (task-edit/on-request! el (fn [id] (open-edit! el id)))))

(defn- make-option! [{:keys [value label]}]
  (let [opt (.createElement js/document "option")]
    (set! (.-value opt) value)
    (set! (.-textContent opt) label)
    opt))

(defn- render! [^js form {accepted :accepted} _this]
  (when-let [^js select (.querySelector form "x-select")]
    (set! (.-innerHTML select) "")
    (doseq [choice (model/status-choices accepted)]
      (.appendChild select (make-option! choice)))))

(defn init!
  []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :render-key (fn [view] (model/status-choices (:accepted view)))
    :render     render!
    :on-writing on-writing!
    :on-failure on-failure!}))
