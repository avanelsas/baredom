(ns demo.x-project-selector-consumer.x-project-selector-consumer
  "A PROJECTS consumer that drives TASKS. A selection is a targeted intent to \"tasks\", which
  writes tasks.project to the URL and refetches."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [barereplay.dock :as dock]
   [demo.url :as url]
   [demo.x-project-selector-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private tasks-resource-id "tasks")
(def ^:private all-label "All projects")

;; --- the current selection -----------------------------------------------------

(defn- current-project-id
  "The selection as the x-select spells it, blank meaning all projects. See `demo.url`."
  []
  (or (url/tasks-project-id) model/all-projects-value))

(defn- show-selection!
  "Drive the x-select to the URL selection. A user pick moves only the inner <select>, so the
  attribute can be stale, and re-setting the value it already holds never re-applies. Hence the
  remove first. Both routes to the selection end here: a render, and a URL the browser or the
  replay dock moved under us."
  [^js x-select]
  (let [desired (current-project-id)]
    (when (not= desired (.-value x-select))
      (du/remove-attr! x-select "value")
      (du/set-attr! x-select "value" desired))))

;; --- option rendering ---------------------------------------------------------

(defn- make-option! [{:keys [id name]}]
  (let [opt (.createElement js/document "option")]
    (set! (.-value opt) id)
    (set! (.-textContent opt) name)
    opt))

(defn- render! [^js x-select {accepted :accepted} _this]
  (set! (.-innerHTML x-select) "")
  (.appendChild x-select (make-option! {:id model/all-projects-value :name all-label}))
  (doseq [o (model/project-options accepted)]
    (.appendChild x-select (make-option! o)))
  (show-selection! x-select))

;; --- wiring -------------------------------------------------------------------

(defn- on-select-change! [^js el ^js e]
  (let [id (.. e -detail -value)]
    (when (not= id (current-project-id))
      (consumer-resource/submit-intent! el (model/translate-project-gesture id) tasks-resource-id))))

(defn- on-connect! [^js x-select ^js el]
  (.addEventListener x-select "select-change" (fn [e] (on-select-change! el e)))
  (.addEventListener js/window "popstate" (fn [_e] (show-selection! x-select)))
  (.addEventListener js/window dock/url-changed-event (fn [_e] (show-selection! x-select))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-select"
    :render-key (fn [view] (model/project-options (:accepted view)))
    :render     render!
    :on-connect on-connect!}))
