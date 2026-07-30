(ns demo.x-task-form-consumer.x-task-form-consumer
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.x-task-form-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-button "__xConsumerButton")
(def ^:private k-modal "__xConsumerModal")
(def ^:private k-populated? "__xConsumerPopulated?")
(def ^:private k-shape "__xConsumerShape")
(def ^:private k-edit-id "__xConsumerEditId")
(def ^:private k-accepted "__xConsumerAccepted")

(defn- submit! [^js e]
  (let [vals     (.. e -detail -values)
        record   (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)]
    (when shape
      (let [edit-id (du/getv consumer k-edit-id)
            payload (if edit-id
                      {:op :update :id edit-id :record record}
                      {:op :create :record record})]
        (consumer-form/validate-and-write! consumer form record shape payload)))))

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
                         (set! (.-textContent submit-btn) "Create")
                         (du/set-attr! modal "label" "Create Task")
                         (set! (.-textContent modal-header) "Create Task")
                         (consumer-form/remove-alert! form)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e]
                         (consumer-form/clear-form! form)))
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
                               (consumer-form/remove-alert! form)
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
    :on-failure consumer-form/on-failure!}))
