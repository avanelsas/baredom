(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [demo.x-task-form-consumer.model :as model]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-submit-pending  "__xConsumerSubmitPending")
(def ^:private k-button  "__xConsumerButton")
(def ^:private k-modal  "__xConsumerModal")
(def ^:private k-populated?  "__xConsumerPopulated?")
(def ^:private k-shape  "__xConsumerShape")
(def ^:private k-edit-id  "__xConsumerEditId")
(def ^:private k-accepted  "__xConsumerAccepted")

(defn- clear-form! [^js form]
  (.clearErrors form)
  (.reset form))

(defn- remove-alert! [^js modal]
  (when-let [^js existing (.querySelector modal "x-alert")]
    (.remove existing)))

(defn- show-alert! [^js modal message]
  (remove-alert! modal)
  (let [alert (.createElement js/document "x-alert")]
    (du/set-attr! alert "type" "error")
    (du/set-attr! alert "text" message)
    (du/set-attr! alert "dismissible" "")
    (.insertBefore modal alert (.querySelector modal "x-form"))))

(defn- write-failure-message [failure]
  (case (:failure failure)
    :network  "Couldn't reach the server — please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn- on-rejection! [^js form ^js modal failure ^js this]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (str "[name='" field "']")))
      (.setFieldError form field message)
      (show-alert! modal message))
    (du/setv! this k-submit-pending false)))

(defn- on-failure! [^js form failure ^js this]
  (let [^js modal   (du/getv this k-modal)
        submitting? (du/getv this k-submit-pending)]
    (cond
      (nil? failure)
      (do
        (.clearErrors form)
        (remove-alert! modal))

      (and submitting? (= :rejected (:failure failure)))
      (on-rejection! form modal failure this)

      (and submitting? (:write failure))
      (do
        (show-alert! modal (write-failure-message failure))
        (du/setv! this k-submit-pending false)))))

(defn- submit! [^js e]
  (let [vals     (.. e -detail -values)
        record   (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form "x-task-form-consumer")
        shape    (du/getv consumer k-shape)]
    (when shape
      (let [errors (validation/validate-payload record shape)]
        (.clearErrors form)
        (if (seq errors)
          (doseq [{:keys [field message]} errors]
            (.setFieldError form field message)) ; name = field = shape key = input name
          (do
            (du/setv! consumer k-submit-pending true)
            (if-let [edit-id (du/getv consumer k-edit-id)]
              (consumer-resource/submit-write! consumer {:op :update :id edit-id :record record})
              (consumer-resource/submit-write! consumer {:op :create :record record}))))))))

(defn- on-writing!
  [^js form writing ^js this]
  (let [button (du/getv this k-button)]
    (if writing
      (du/set-attr! button "loading" "")
      (do
        (du/remove-attr! button "loading")
        (when (du/getv this k-submit-pending)
          (.hide (du/getv this k-modal))
          (clear-form! form)
          (du/setv! this k-submit-pending false))))))

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
                         (set! (.-textContent submit-btn) "Create")
                         (du/set-attr! modal "label" "Create Task")
                         (set! (.-textContent modal-header) "Create Task")
                         (remove-alert! modal)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e]
                         (clear-form! form)))
    ;; This one is on the shared <server-resource> ancestor, so it is NOT scoped to this
    ;; consumer's lifetime — fine for the demo (never unmounts); a real host wanting
    ;; unmount safety would need a disconnect hook on consumer-resource/register!.
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
                               (set! (.-textContent submit-btn) "Update")
                               (du/set-attr! modal "label" "Edit Task")
                               (set! (.-textContent modal-header) "Edit Task")
                               (remove-alert! modal)
                               (prefill-form! form row fields)
                               (.show modal))))))
    (.addEventListener form "x-form-submit"
                       (fn [e]
                         (submit! e)))))

(defn- render! [^js form accepted ^js this]
  (du/setv! this k-accepted accepted)
  ;; populate the select + stash shape — once
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
