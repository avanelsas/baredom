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

(defn- clear-form! [^js form]
  ;; BareDOM 3.4.0 made x-date-picker/x-select form-associated, so reset() clears them too.
  (.reset form))

(defn- on-failure! [^js form failure ^js this]
  (cond
    (nil? failure) (.clearErrors form)
    (= :rejected (:failure failure))
    (let [{:keys [message details]} (get-in failure [:response :error])
          field (get details "field")]
      (when (and field (.querySelector form (str "[name='" field "']")))
        (.setFieldError form field message)
        (du/setv! this k-submit-pending false)))
    (:write failure)
    (du/setv! this k-submit-pending false)))

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
            (consumer-resource/submit-write! consumer {:op :create :record record})))))))

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

(defn- connect!
  [^js el]
  (let [trigger    (.querySelector el "x-button[data-role='open']")
        submit-btn (.querySelector el "x-button[type='submit']")
        modal      (.querySelector el "x-modal")
        form       (.querySelector el "x-form")]

    (du/setv! el k-button submit-btn)         ; on-writing! disables the submit button, not the trigger
    (du/setv! el k-modal modal)
    ;; These event listeners are disposed of automatically
    (.addEventListener trigger "press"
                       (fn [_e]
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e]
                         (clear-form! form)))
    (.addEventListener form "x-form-submit"
                       (fn [e]
                         (submit! e)))))

(defn- render! [^js form accepted ^js this]
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
