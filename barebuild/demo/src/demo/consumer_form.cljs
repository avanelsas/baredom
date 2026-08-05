(ns demo.consumer-form
  "Shared form-consumer glue for the demo's write forms (create task, create project, quick-add)"
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-pending "__xConsumerFormPending")

(defn pending? [^js this] (du/getv this k-pending))
(defn set-pending! [^js this v] (du/setv! this k-pending v))

(defn form-values
  "The submitted values from an x-form-submit event as a CLJS map keyed by the form's string
  field names."
  [^js e]
  (let [vals (.. e -detail -values)]
    (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))))

(defn clear-form! [^js form]
  (.clearErrors form)
  (.reset form))

(defn remove-alert! [^js host]
  (when-let [^js existing (.querySelector host "x-alert")]
    (.remove existing)))

(defn show-alert! [^js host message]
  (remove-alert! host)
  (let [alert (.createElement js/document "x-alert")]
    (du/set-attr! alert "type" "error")
    (du/set-attr! alert "text" message)
    (du/set-attr! alert "dismissible" "")
    (.insertBefore host alert (.querySelector host "x-form"))))

(defn write-failure-message [failure]
  (case (:failure failure)
    :network  "Couldn't reach the server, please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn write-plan
  "Given a `record`, its `shape`, and the `payload` to send, return {:errors [...]} when
  the record fails the shape, else {:payload payload}."
  [record shape payload]
  (let [errors (validation/validate-payload record shape)]
    (if (seq errors) {:errors errors} {:payload payload})))

(defn- show-field-errors! [^js form errors]
  (doseq [{:keys [field message]} errors]
    (.setFieldError form field message)))

(defn attempt-write!
  "Carry out the write-plan for `record`. A submit already in flight is ignored,
  so a double click cannot fire two writes."
  [^js consumer ^js form record shape payload]
  (when-not (pending? consumer)
    (.clearErrors form)
    (let [plan (write-plan record shape payload)]
      (if-let [errors (:errors plan)]
        (show-field-errors! form errors)
        (do (set-pending! consumer true)
            (consumer-resource/submit-write! consumer (:payload plan)))))))

(defn- apply-rejection! [^js form ^js host failure ^js this]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (str "[name='" field "']")))
      (.setFieldError form field message)
      (show-alert! host message))
    (set-pending! this false)))

(defn on-failure!
  "Clears the UI on recovery, maps a rejection to an inline field error or a form-level alert in
  `host`, and surfaces a write or transport failure as an alert."
  [^js form ^js host failure ^js this]
  (cond
    (nil? failure)
    (do (.clearErrors form)
        (remove-alert! host))

    (and (pending? this) (= :rejected (:failure failure)))
    (apply-rejection! form host failure this)

    (and (pending? this) (= :write (:for failure)))
    (do (show-alert! host (write-failure-message failure))
        (set-pending! this false))))

(defn on-writing!
  "Toggle the submit button's loading state, and when a pending submit completes, run `on-success`
  (close or collapse the form), clear it, and reset pending."
  [^js form writing ^js this ^js button on-success]
  (if writing
    (du/set-attr! button "loading" "")
    (do (du/remove-attr! button "loading")
        (when (pending? this)
          (on-success)
          (clear-form! form)
          (set-pending! this false)))))
