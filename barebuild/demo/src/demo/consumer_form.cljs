(ns demo.consumer-form
  "Shared form-consumer glue for the demo's write forms (create task, create project, quick-add)"
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [demo.alert :as alert]
   [demo.selector :as selector]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(defn form-values
  "The x-form-submit values as a CLJS map keyed by field name. Read key by key rather than with
   js->clj: x-form hands over a null-prototype object, which js->clj does not recognise as one to
   convert and returns untouched."
  [^js e]
  (let [vals (.. e -detail -values)]
    (into {} (map (fn [k] [k (gobj/get vals k)])) (js/Object.keys vals))))

(defn clear-form! [^js form]
  (.clearErrors form)
  (.reset form))

(defn write-failure-message [failure]
  (case (:cause failure)
    :network  "Couldn't reach the server, please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn write-plan
  "{:errors [...]} when the payload's record fails `shape`, else {:payload ...} carrying that
  record coerced to the types the shape declares."
  [shape payload]
  (let [{:keys [errors] conformed :record} (validation/conform-payload (:record payload) shape)]
    (if (seq errors)
      {:errors errors}
      {:payload (assoc payload :record conformed)})))

(defn- alert!
  "Report `message` above the form, where a field-level error has nowhere to land."
  [^js host message]
  (alert/show! host message (.querySelector host "x-form")))

(defn- show-field-errors! [^js form errors]
  (doseq [{:keys [field message]} errors]
    (.setFieldError form field message)))

(defn attempt-write!
  "Carry out the write `payload` describes. A write already in flight is ignored."
  [^js consumer ^js form shape payload]
  (when-not (:writing? (consumer-resource/view consumer))
    (.clearErrors form)
    (let [plan (write-plan shape payload)]
      (if-let [errors (:errors plan)]
        (show-field-errors! form errors)
        (consumer-resource/submit-write! consumer (:payload plan))))))

(defn- apply-rejection! [^js form ^js host failure]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (selector/attr= "name" field)))
      (.setFieldError form field message)
      (alert! host message))))

(defn on-failure!
  "Clear the UI on recovery, and report this form's own write failing: a rejection inline where
  it names a field, else an alert in `host`."
  [^js form ^js host view ^js this]
  (let [failure (:failure view)]
    (cond
      (nil? failure)
      (do (.clearErrors form)
          (alert/clear! host))

      (and (= :write (:for failure)) (consumer-resource/own-write? this view))
      (if (= :rejected (:cause failure))
        (apply-rejection! form host failure)
        (alert! host (write-failure-message failure))))))

(defn on-writing!
  "Drive the submit button's loading state from this form's own write, running `on-success` and
  clearing the form once it is accepted."
  [^js form view ^js this ^js button on-success]
  (let [status (when (consumer-resource/own-write? this view)
                 (get-in view [:write :status]))]
    (if (= :in-flight status)
      (du/set-attr! button "loading" "")
      (do (du/remove-attr! button "loading")
          (when (= :accepted status)
            (on-success)
            (clear-form! form))))))
