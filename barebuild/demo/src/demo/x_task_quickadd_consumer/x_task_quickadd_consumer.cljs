(ns demo.x-task-quickadd-consumer.x-task-quickadd-consumer
  "A create-only consumer of the TASKS <server-resource>, living at the bottom of the To Do
  column. An Add a task link opens a small inline form (title + name); a valid submit sends a
  :create write scoped to the currently viewed project, and the new card is observed back at
  the bottom of To Do. No optimism: nothing shows until the server confirms."
  (:require
   [demo.consumer-form :as consumer-form]
   [demo.x-task-quickadd-consumer.model :as model]
   [barebuild.consumer-resource :as consumer-resource]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]))

(def ^:private k-add-button "__xQuickAddButton")
(def ^:private k-shape "__xQuickAddShape")

(defn- current-project-id []
  (.get (js/URLSearchParams. (.-search js/location)) "tasks.project"))

(defn- today-iso []
  (subs (.toISOString (js/Date.)) 0 10))

(defn- form-el [^js el] (.querySelector el "x-form"))
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
  (let [vals     (.. e -detail -values)
        entered  (into {} (map (fn [k] [k (gobj/get vals k)]) (js/Object.keys vals)))
        form     (.-currentTarget e)
        consumer (.closest form model/tag-name)
        shape    (du/getv consumer k-shape)
        project  (current-project-id)]
    (when (and shape project)
      (let [record (model/new-task-record entered project (today-iso))]
        (consumer-form/validate-and-write! consumer form record shape {:op :create :record record})))))

(defn- on-writing! [^js form writing ^js this]
  (consumer-form/on-writing! form writing this (du/getv this k-add-button)
                             (fn [] (collapse! this))))

(defn- connect! [^js el]
  (let [open-btn (open-link el)
        cancel   (.querySelector el "[data-role='cancel']")
        add-btn  (.querySelector el "x-button[type='submit']")
        form     (form-el el)]
    (du/setv! el k-add-button add-btn)
    (.addEventListener open-btn "press" (fn [_e] (reveal! el)))
    (.addEventListener cancel "press" (fn [_e] (consumer-form/clear-form! form) (collapse! el)))
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
    :on-failure consumer-form/on-failure!}))
