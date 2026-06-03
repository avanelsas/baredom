(ns demo-app.detail
  "Read-side wiring for the /tasks/:id detail route.

  Param-driven read: the route's `:params` (e.g. {id \"5\"}) arrive in the
  `barebuild-route-change` detail; we set the broker's `src` to /api/tasks/<id>.
  On `loaded` we project the task into the card and pre-fill the inline edit form.

  The edit submit and the Delete action are wired live in write-side (update +
  delete handlers)."
  (:require [goog.object :as gobj]
            [demo-app.dom :as dom]
            [demo-app.view :as view]
            [demo-app.wiring :as w]))

(def ^:private detail-fields
  [["status" "Status"] ["assignee" "Assignee"] ["due" "Due"] ["description" "Description"]])

(def ^:private form-fields ["title" "description" "status" "assignee" "due"])

(defn- render-detail! [^js route ^js task]
  (let [^js title (.querySelector route "#detail-title")
        ^js dl    (.querySelector route "#detail-fields")
        ^js form  (.querySelector route w/id-edit-task-form)]
    (set! (.-textContent title) (str "Task #" (gobj/get task "id") " — " (gobj/get task "title")))
    (dom/clear! dl)
    (doseq [[k label] detail-fields]
      (.appendChild dl (dom/text-el! "dt" label))
      (.appendChild dl (dom/text-el! "dd" (let [v (gobj/get task k)]
                                            (if (or (nil? v) (= "" v)) "—" v)))))
    (dom/fill-form! form task form-fields)))

;; ── Handlers ─────────────────────────────────────────────────────────────────
;; Named, event-only handlers (resolve handles from `currentTarget`), so
;; `init-detail!` reads as a wiring list — matching write_side.cljs.

(defn- on-route-change
  "Activation → read /api/tasks/<id>. route-change is pushed to every route on each
  resolution carrying the GLOBAL match (resolved path + the active route's params),
  so gate on the resolved path matching THIS route's /tasks/:id shape — not merely
  on an `id` param being present (consistent with board/settings, and correct even
  if some other route also carries an `id`)."
  [^js e]
  (let [id (gobj/get (.. e -detail -params) "id")]
    (when (and id (= (str w/path-tasks "/" id) (.. e -detail -path)))
      (let [^js route (.-currentTarget e)]
        (set! (.-src (.querySelector route w/id-detail-data)) (str "/api/tasks/" id))))))

(defn- on-data-state
  ;; Renders straight from e.detail.state.data — unlike board, detail has no
  ;; filter-change events that re-render with no payload, so it never needs to read
  ;; the retained broker value.
  [^js e]
  (let [^js route (.-currentTarget e)
        ^js state (.. e -detail -state)
        phase     (.-phase state)
        ^js err   (.querySelector route "#detail-error")]
    (dom/show! err (= "error" phase))
    (when (= "error" phase)
      (.setAttribute err "text" (view/load-error-text "task" (.-httpStatus state))))
    (when (= "loaded" phase)
      (render-detail! route (.-data state)))))

(defn- on-delete-click
  "Open the confirm dialogue. The DELETE fetch on confirm lives in write-side."
  [^js e]
  (let [^js route (-> e .-currentTarget (.closest w/tag-route))]
    (set! (.-open (.querySelector route w/id-delete-confirm)) true)))

(defn init-detail! []
  (let [^js route  (.querySelector js/document (w/route-selector w/path-task))
        ^js delbtn (.querySelector route "#delete-task-btn")]
    (when-let [^js edit-status (.querySelector route "#edit-task-form [name='status']")]
      (dom/fill-options! edit-status view/statuses nil))   ; data-driven status options
    (.addEventListener route  w/ev-route-change on-route-change)
    (.addEventListener route  w/ev-data-state   on-data-state)
    (.addEventListener delbtn w/ev-press        on-delete-click)))
