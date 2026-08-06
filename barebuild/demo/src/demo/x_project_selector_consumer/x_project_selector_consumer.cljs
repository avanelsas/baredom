(ns demo.x-project-selector-consumer.x-project-selector-consumer
  "A consumer of the PROJECTS <server-resource> that drives the TASKS resource. Selecting a
  project sends a targeted intent to \"tasks\", which writes tasks.project to the URL and
  refetches the filtered set."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-project-selector-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private tasks-resource-id "tasks")
(def ^:private all-label "All projects")

;; --- the current selection, read from the shared URL projection ---------------

(defn- current-project-id
  "The selection lives in the TASKS resource's URL scope, and this consumer belongs to PROJECTS,
  so its own projected intent cannot carry it. Reading the address bar is the only route a
  per-resource view leaves open, and it is why this one component does not rewind with a
  time-travel projection the way the board does."
  []
  (or (.get (js/URLSearchParams. (.-search js/location)) "tasks.project")
      model/all-projects-value))

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
  (du/set-attr! x-select "value" (current-project-id)))

;; --- wiring -------------------------------------------------------------------

(defn- on-select-change! [^js el ^js e]
  (let [id (.. e -detail -value)]
    (when (not= id (current-project-id))
      (consumer-resource/submit-intent! el (model/translate-project-gesture id) tasks-resource-id))))

(defn- show-selection!
  "Drive the x-select to the current URL selection. A user pick updates only the inner
  <select>, never the value attribute, so the attribute can be stale (e.g. \"\" at load while
  the box shows a project). Setting the attribute to a value it already holds is a no-op that
  never re-applies, so when the displayed value differs we remove first to force a re-apply."
  [^js x-select]
  (let [desired (current-project-id)]
    (when (not= desired (.-value x-select))
      (du/remove-attr! x-select "value")
      (du/set-attr! x-select "value" desired))))

(defn- on-connect! [^js el]
  (let [x-select (.querySelector el "x-select")]
    (.addEventListener x-select "select-change" (fn [e] (on-select-change! el e)))
    (.addEventListener js/window "popstate" (fn [_e] (show-selection! x-select)))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-select"
    :render-key (fn [view] [(model/project-options (:accepted view)) (current-project-id)])
    :render     render!
    :on-connect on-connect!}))
