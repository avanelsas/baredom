(ns demo.x-project-selector-consumer.x-project-selector-consumer
  "A consumer of the PROJECTS <server-resource> that drives the TASKS resource: selecting a
   project sends a targeted intent to \"tasks\", which writes tasks.project to the URL and
   refetches the filtered set. This is the cross-resource, URL-mediated coordination the
   runtime primitive enables — projects and tasks stay independent projections."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-project-selector-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private tasks-resource-id "tasks")
(def ^:private all-label "All projects")

;; --- the current selection, read from the shared URL projection ---------------

(defn- current-project-id []
  (or (.get (js/URLSearchParams. (.-search js/location)) "tasks.project")
      model/all-projects-value))

;; --- option rendering ---------------------------------------------------------

(defn- make-option! [{:keys [id name]}]
  (let [opt (.createElement js/document "option")]
    (set! (.-value opt) id)
    (set! (.-textContent opt) name)
    opt))

(defn- render! [^js x-select accepted _this]
  (set! (.-innerHTML x-select) "")
  (.appendChild x-select (make-option! {:id model/all-projects-value :name all-label}))
  (doseq [o (model/project-options accepted)]
    (.appendChild x-select (make-option! o)))
  (du/set-attr! x-select "value" (current-project-id)))

;; --- wiring -------------------------------------------------------------------

(defn- on-select-change! [^js el ^js e]
  (let [id (.. e -detail -value)]
    ;; Guard against a programmatic value set echoing back as a change: only drive tasks
    ;; when the pick actually differs from what the URL already holds.
    (when (not= id (current-project-id))
      (consumer-resource/submit-intent! el (model/translate-project-gesture id) tasks-resource-id))))

(defn- on-connect! [^js el]
  (let [x-select (.querySelector el "x-select")]
    (.addEventListener x-select "select-change" (fn [e] (on-select-change! el e)))
    ;; Keep the dropdown in step with Back/Forward, which change tasks.project in the URL
    ;; but do not re-render this projects consumer.
    (.addEventListener js/window "popstate"
                       (fn [_e] (du/set-attr! x-select "value" (current-project-id))))))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-select"
    :observed-attributes model/observed-attributes
    :render-key          model/project-options
    :render              render!
    :on-connect          on-connect!}))
