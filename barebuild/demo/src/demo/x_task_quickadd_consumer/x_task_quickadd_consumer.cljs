(ns demo.x-task-quickadd-consumer.x-task-quickadd-consumer
  "The create-task form at the bottom of the To Do column."
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.x-task-quickadd-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]))

(def ^:private k-add-button "__xQuickAddButton")

(defn- today-iso []
  (subs (.toISOString (js/Date.)) 0 10))

(defn- form-wrap [^js el] (.querySelector el ".quickadd-form"))
(defn- open-link [^js el] (.querySelector el "[data-role='open']"))

(defn- reveal! [^js el]
  (du/set-attr! (open-link el) "hidden" "")
  (du/remove-attr! (form-wrap el) "hidden")
  (when-let [^js title (.querySelector el "[name='title']")]
    (.focus title)))

(defn- collapse! [^js el]
  (du/remove-attr! (open-link el) "hidden")
  (du/set-attr! (form-wrap el) "hidden" ""))

(defn- submit! [^js e]
  (let [entered  (consumer-form/form-values e)
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        view     (consumer-resource/view consumer)
        shape    (get-in view [:accepted :shape])
        project  (get-in view [:intent :project])]
    (when (and shape project)
      (consumer-form/attempt-write!
       consumer form shape
       {:op :create :record (model/new-task-record entered project (today-iso))}))))

(defn- on-writing! [^js form view ^js this]
  (consumer-form/on-writing! form view this (du/getv this k-add-button)
                             (fn [] (collapse! this))))

(defn- on-failure! [^js form view ^js this]
  (consumer-form/on-failure! form (form-wrap this) view this))

(defn- connect! [^js form ^js el]
  (let [open-btn (open-link el)
        cancel   (.querySelector el "[data-role='cancel']")
        add-btn  (.querySelector el "x-button[type='submit']")]
    (du/setv! el k-add-button add-btn)
    (.addEventListener open-btn "press" (fn [_e] (reveal! el)))
    (.addEventListener cancel "press" (fn [_e] (consumer-form/clear-form! form) (collapse! el)))
    (.addEventListener form "x-form-submit" (fn [e] (submit! e)))))

;; No :render: the form only submits.
(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-form"
    :on-connect connect!
    :on-writing on-writing!
    :on-failure on-failure!}))
