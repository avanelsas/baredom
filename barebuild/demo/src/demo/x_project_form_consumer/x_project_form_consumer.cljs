(ns demo.x-project-form-consumer.x-project-form-consumer
  "A create-only consumer of the PROJECTS <server-resource>. A New project button opens a
  modal form; a valid submit sends a :create write, and the projects refetch so the new
  project appears in the selector. No optimism: nothing shows until the server confirms."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [demo.x-project-form-consumer.model :as model]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-submit-pending "__xProjectFormPending")
(def ^:private k-button "__xProjectFormButton")
(def ^:private k-modal "__xProjectFormModal")
(def ^:private k-shape "__xProjectFormShape")

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
    :network  "Couldn't reach the server, please try again."
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
      (do (.clearErrors form)
          (remove-alert! modal))

      (and submitting? (= :rejected (:failure failure)))
      (on-rejection! form modal failure this)

      (and submitting? (:write failure))
      (do (show-alert! modal (write-failure-message failure))
          (du/setv! this k-submit-pending false)))))

(defn- submit! [^js e]
  (let [vals     (.. e -detail -values)
        record   (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)]
    (when shape
      (let [errors (validation/validate-payload record shape)]
        (.clearErrors form)
        (if (seq errors)
          (doseq [{:keys [field message]} errors]
            (.setFieldError form field message))
          (do (du/setv! consumer k-submit-pending true)
              (consumer-resource/submit-write! consumer {:op :create :record record})))))))

(defn- on-writing! [^js form writing ^js this]
  (let [button (du/getv this k-button)]
    (if writing
      (du/set-attr! button "loading" "")
      (do (du/remove-attr! button "loading")
          (when (du/getv this k-submit-pending)
            (.hide (du/getv this k-modal))
            (clear-form! form)
            (du/setv! this k-submit-pending false))))))

(defn- connect! [^js el]
  (let [trigger    (.querySelector el "x-button[data-role='open']")
        submit-btn (.querySelector el "x-button[type='submit']")
        modal      (.querySelector el "x-modal")
        form       (.querySelector el "x-form")]
    (du/setv! el k-button submit-btn)
    (du/setv! el k-modal modal)
    (.addEventListener trigger "press"
                       (fn [_e]
                         (remove-alert! modal)
                         (.show modal)))
    (.addEventListener modal "x-modal-dismiss"
                       (fn [_e] (clear-form! form)))
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
    :on-failure on-failure!}))
