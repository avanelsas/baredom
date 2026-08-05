(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.selector :as selector]
   [demo.x-task-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def ^:private k-refs "__xConsumerRefs")
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

(defn- refs
  "The consumer's own elements, found once at connect: the open trigger, the submit button, the
   modal, its title, and the form."
  [^js el]
  (du/getv el k-refs))

(defn- on-failure! [^js form failure ^js this]
  (consumer-form/on-failure! form (:modal (refs this)) failure this))

(defn- on-writing! [^js form writing ^js this]
  (let [{:keys [^js modal submit]} (refs this)]
    (consumer-form/on-writing! form writing this submit (fn [] (.hide modal)))))

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
  "Dress the modal as a create or an edit: the submit verb, both titles, and no alert carried
   over from the last time it was open."
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

(defn- row-by-id
  "The accepted row `id` names. The id arrives from the DOM as a string, so both sides are
   compared as strings rather than trusting the server's id to be one."
  [accepted id]
  (let [id-key (get-in accepted [:shape :id-key])]
    (first (filter #(= (str (get % id-key)) id) (:value accepted)))))

(defn- open-edit! [^js el id]
  (let [accepted (du/getv el k-accepted)]
    (when-let [row (row-by-id accepted id)]
      (du/setv! el k-edit-id id)
      (du/setv! el k-edit-extras (select-keys row ["projectId" "assigneeId"]))
      (apply-mode! el "Update" "Edit Task")
      (prefill-form! (:form (refs el)) row (get-in accepted [:shape :fields]))
      (.show ^js (:modal (refs el))))))

(defn- connect!
  [^js el]
  (du/setv! el k-refs (find-refs el))
  ;; Listeners on the consumer's own subtree (trigger, modal, form) are GC'd with it. The edit
  ;; request is the exception: it is dispatched by the table consumer and caught on the resource.
  (let [{:keys [^js trigger ^js modal ^js form]} (refs el)]
    (.addEventListener trigger "press" (fn [_e] (open-create! el)))
    (.addEventListener modal "x-modal-dismiss" (fn [_e] (consumer-form/clear-form! form)))
    (.addEventListener form "x-form-submit" (fn [e] (submit! e)))
    (.addEventListener (.closest el "server-resource") "x-task-edit-request"
                       (fn [^js e] (open-edit! el (.. e -detail -id))))))

(defn- field-choices
  "What the status select should offer. A field's :options are the catalogue, each with the
   value to submit and the label to show. Falling back to :enum keeps a server that declares
   only the constraint working, with the raw value as its own label."
  [field]
  (or (:options field)
      (mapv (fn [v] {:value v :label v}) (:enum field))))

(defn- render! [^js form {accepted :accepted} ^js this]
  (du/setv! this k-accepted accepted)
  ;; populate the select + stash shape once. Not before a response: there are no choices to
  ;; offer yet, and marking it populated would leave the select empty for good
  (when (and accepted (not (du/getv this k-populated?)))
    (let [status (->> (get-in accepted [:shape :fields]) (filter #(= "status" (:key %))) first)
          select (.querySelector form "x-select")]
      (doseq [{:keys [value label]} (field-choices status)]
        (let [opt (.createElement js/document "option")]
          (set! (.-value opt) value)
          (set! (.-textContent opt) label)
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
