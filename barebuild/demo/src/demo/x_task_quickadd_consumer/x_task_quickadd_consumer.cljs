(ns demo.x-task-quickadd-consumer.x-task-quickadd-consumer
  "A create-only consumer of the TASKS <server-resource>, living at the bottom of the To Do
  column. An Add a task link opens a small inline form (title + name); a valid submit sends a
  :create write scoped to the currently viewed project, and the new card is observed back at
  the bottom of To Do. No optimism: nothing shows until the server confirms."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barebuild.validation :as validation]
   [demo.x-task-quickadd-consumer.model :as model]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-submit-pending "__xQuickAddPending")
(def ^:private k-add-button "__xQuickAddButton")
(def ^:private k-shape "__xQuickAddShape")

(defn- current-project-id []
  (.get (js/URLSearchParams. (.-search js/location)) "tasks.project"))

(defn- today-iso []
  (subs (.toISOString (js/Date.)) 0 10))

(defn- form-el [^js el] (.querySelector el "x-form"))
(defn- form-wrap [^js el] (.querySelector el ".quickadd-form"))
(defn- open-link [^js el] (.querySelector el "[data-role='open']"))

(defn- clear-form! [^js form]
  (.clearErrors form)
  (.reset form))

(defn- reveal! [^js el]
  (du/set-attr! (open-link el) "hidden" "")
  (du/remove-attr! (form-wrap el) "hidden")
  (when-let [^js title (.querySelector el "[name='title']")]
    (.focus title)))

(defn- collapse! [^js el]
  (du/remove-attr! (open-link el) "hidden")
  (du/set-attr! (form-wrap el) "hidden" ""))

(defn- remove-alert! [^js el]
  (when-let [^js existing (.querySelector el "x-alert")]
    (.remove existing)))

(defn- show-alert! [^js el message]
  (remove-alert! el)
  (let [alert (.createElement js/document "x-alert")]
    (du/set-attr! alert "type" "error")
    (du/set-attr! alert "text" message)
    (du/set-attr! alert "dismissible" "")
    (.insertBefore (form-wrap el) alert (form-el el))))

(defn- write-failure-message [failure]
  (case (:failure failure)
    :network  "Couldn't reach the server, please try again."
    :protocol "The server sent an unexpected response."
    "Something went wrong."))

(defn- on-rejection! [^js form ^js this failure]
  (let [{:keys [message details]} (get-in failure [:response :error])
        field                     (get details "field")]
    (if (and field (.querySelector form (str "[name='" field "']")))
      (.setFieldError form field message)
      (show-alert! this message))
    (du/setv! this k-submit-pending false)))

(defn- on-failure! [^js form failure ^js this]
  (let [submitting? (du/getv this k-submit-pending)]
    (cond
      (nil? failure)
      (do (.clearErrors form)
          (remove-alert! this))

      (and submitting? (= :rejected (:failure failure)))
      (on-rejection! form this failure)

      (and submitting? (:write failure))
      (do (show-alert! this (write-failure-message failure))
          (du/setv! this k-submit-pending false)))))

(defn- submit! [^js e]
  (let [vals     (.. e -detail -values)
        entered  (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)
        project  (current-project-id)]
    (when (and shape project)
      (let [record (model/new-task-record entered project (today-iso))
            errors (validation/validate-payload record shape)]
        (.clearErrors form)
        (if (seq errors)
          (doseq [{:keys [field message]} errors]
            (.setFieldError form field message))
          (do (du/setv! consumer k-submit-pending true)
              (consumer-resource/submit-write! consumer {:op :create :record record})))))))

(defn- on-writing! [^js form writing ^js this]
  (let [button (du/getv this k-add-button)]
    (if writing
      (du/set-attr! button "loading" "")
      (do (du/remove-attr! button "loading")
          (when (du/getv this k-submit-pending)
            (clear-form! form)
            (collapse! this)
            (du/setv! this k-submit-pending false))))))

(defn- connect! [^js el]
  (let [open-btn (open-link el)
        cancel   (.querySelector el "[data-role='cancel']")
        add-btn  (.querySelector el "x-button[type='submit']")
        form     (form-el el)]
    (du/setv! el k-add-button add-btn)
    (.addEventListener open-btn "press" (fn [_e] (reveal! el)))
    (.addEventListener cancel "press" (fn [_e] (clear-form! form) (collapse! el)))
    (.addEventListener form "x-form-submit" (fn [e] (submit! e)))))

(defn- render! [^js _form accepted ^js this]
  (du/setv! this k-shape (:shape accepted)))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :render-key (fn [_accepted] true)
    :on-connect connect!
    :render     render!
    :on-writing on-writing!
    :on-failure on-failure!}))
