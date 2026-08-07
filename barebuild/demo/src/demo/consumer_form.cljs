(ns demo.consumer-form
  "Shared form-consumer glue for the demo's write forms (create task, create project, quick-add)"
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [demo.selector :as selector]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

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
  (case (:cause failure)
    :network  "Couldn't reach the server, please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn write-plan
  "Given a `record`, its `shape`, and the `payload` to send, return {:errors [...]} when the
  record fails the shape, else {:payload payload} carrying the record as the shape declares it,
  so a field the shape calls a number leaves the form as one."
  [record shape payload]
  (let [{:keys [errors] conformed :record} (validation/conform-payload record shape)]
    (if (seq errors)
      {:errors errors}
      {:payload (assoc payload :record conformed)})))

(defn- show-field-errors! [^js form errors]
  (doseq [{:keys [field message]} errors]
    (.setFieldError form field message)))

(defn attempt-write!
  "Carry out the write-plan for `record`. A write already in flight is ignored, so a double click
  cannot fire two writes."
  [^js consumer ^js form record shape payload]
  (when-not (:writing? (consumer-resource/view consumer))
    (.clearErrors form)
    (let [plan (write-plan record shape payload)]
      (if-let [errors (:errors plan)]
        (show-field-errors! form errors)
        (consumer-resource/submit-write! consumer (:payload plan))))))

(defn- apply-rejection! [^js form ^js host failure]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (selector/attr= "name" field)))
      (.setFieldError form field message)
      (show-alert! host message))))

(defn on-failure!
  "Clears the UI on recovery, and reports this form's own write failing: a rejection as an inline
  field error or an alert in `host`, a transport failure as an alert."
  [^js form ^js host view ^js this]
  (let [failure (:failure view)]
    (cond
      (nil? failure)
      (do (.clearErrors form)
          (remove-alert! host))

      (and (= :write (:for failure)) (consumer-resource/own-write? this view))
      (if (= :rejected (:cause failure))
        (apply-rejection! form host failure)
        (show-alert! host (write-failure-message failure))))))

(defn on-writing!
  "Drive the submit button's loading state from this form's own write, and on that write being
  accepted run `on-success` (close or collapse the form) and clear it."
  [^js form view ^js this ^js button on-success]
  (let [status (when (consumer-resource/own-write? this view)
                 (get-in view [:write :status]))]
    (if (= :in-flight status)
      (du/set-attr! button "loading" "")
      (do (du/remove-attr! button "loading")
          (when (= :accepted status)
            (on-success)
            (clear-form! form))))))
