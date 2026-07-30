(ns demo.consumer-form
  "Shared form-consumer glue for the demo's write forms (create task, create project,
  quick-add). Each form consumer supplies its own markup, record building, and success action;
  this module owns the parts they all repeat: the submit-pending flag, inline field errors,
  form-level alerts, and the failure and writing handlers. The alert host is derived from the
  form's parent, so it works for a modal form and an inline form alike."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [baredom.utils.dom :as du]))

(def ^:private k-pending "__xConsumerFormPending")

(defn pending? [^js this] (du/getv this k-pending))
(defn set-pending! [^js this v] (du/setv! this k-pending v))

(defn clear-form! [^js form]
  (.clearErrors form)
  (.reset form))

(defn- alert-host [^js form] (.-parentElement form))

(defn remove-alert! [^js form]
  (when-let [^js existing (.querySelector (alert-host form) "x-alert")]
    (.remove existing)))

(defn show-alert! [^js form message]
  (remove-alert! form)
  (let [alert (.createElement js/document "x-alert")]
    (du/set-attr! alert "type" "error")
    (du/set-attr! alert "text" message)
    (du/set-attr! alert "dismissible" "")
    (.insertBefore (alert-host form) alert form)))

(defn write-failure-message [failure]
  (case (:failure failure)
    :network  "Couldn't reach the server, please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn- apply-rejection! [^js form failure ^js this]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (str "[name='" field "']")))
      (.setFieldError form field message)
      (show-alert! form message))
    (set-pending! this false)))

(defn on-failure!
  "The shared :on-failure handler. Clears the UI on recovery, maps a rejection to an inline
  field error or a form-level alert, and surfaces a write or transport failure as an alert."
  [^js form failure ^js this]
  (cond
    (nil? failure)
    (do (.clearErrors form)
        (remove-alert! form))

    (and (pending? this) (= :rejected (:failure failure)))
    (apply-rejection! form failure this)

    (and (pending? this) (:write failure))
    (do (show-alert! form (write-failure-message failure))
        (set-pending! this false))))

(defn validate-and-write!
  "Validate `record` against `shape`. On success set the pending flag and dispatch `payload`
  as a write; otherwise show each field error inline."
  [^js consumer ^js form record shape payload]
  (.clearErrors form)
  (let [errors (validation/validate-payload record shape)]
    (if (seq errors)
      (doseq [{:keys [field message]} errors]
        (.setFieldError form field message))
      (do (set-pending! consumer true)
          (consumer-resource/submit-write! consumer payload)))))

(defn on-writing!
  "The shared :on-writing handler. Toggle the submit button's loading state, and when a pending
  submit completes, run `on-success` (close or collapse the form), clear it, and reset pending."
  [^js form writing ^js this ^js button on-success]
  (if writing
    (du/set-attr! button "loading" "")
    (do (du/remove-attr! button "loading")
        (when (pending? this)
          (on-success)
          (clear-form! form)
          (set-pending! this false)))))
