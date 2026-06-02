(ns demo-app.board
  "Read-side wiring for the /tasks board route.

  Canonical BareBuild read-side: the <barebuild-data> broker is dormant until the
  route activates; on activation we set its `src`; on each `barebuild-data-state`
  transition we re-render. Filtering is client-side and never refetches — but it
  reads the last server value from the BROKER'S `.state` (which <barebuild-data>
  holds by reference), not from a copy stashed somewhere. `render-board!` is thus a
  pure projection: DOM = f(broker value, live filter controls), with no app-level
  place-state of its own.

  The WRITE surfaces (New-task modal + form, row Delete) are built but their
  submit/delete glue is the Phase-4 stub seam, added in a later step."
  (:require [clojure.string :as str]
            [goog.object :as gobj]
            [demo-app.dom :as dom]
            [demo-app.wiring :as w]))

(defn- status-variant [status]
  (case status
    "done"  "success"
    "doing" "info"
    "neutral"))

(defn- header-row! []
  (dom/el! "x-table-row" nil
           (mapv (fn [label] (let [c (dom/text-el! "x-table-cell" label)]
                               (.setAttribute c "type" "header") c))
                 ["ID" "Title" "Status" "Assignee" "Due" ""])))

(defn- view-link! [id]
  (let [a (dom/el! "a" {"class" "nav-link" "href" (str w/path-tasks "/" id) "data-barebuild-route" true})]
    (set! (.-textContent a) "View")
    a))

(defn- task-row! [^js t]
  (let [id     (gobj/get t "id")
        status (gobj/get t "status")]
    (dom/el! "x-table-row" {"row-index" id}
             [(dom/text-el! "x-table-cell" id)
              (dom/text-el! "x-table-cell" (gobj/get t "title"))
              (dom/el! "x-table-cell" nil [(dom/el! "x-badge" {"variant" (status-variant status)
                                                               "text"    status})])
              (dom/text-el! "x-table-cell" (gobj/get t "assignee"))
              (dom/text-el! "x-table-cell" (gobj/get t "due"))
              (dom/el! "x-table-cell" nil
                       [(dom/el! "div" {"class" "row-actions"}
                                 [(view-link! id)
                                  ;; The Phase-4 DELETE seam (write_side) matches this attr.
                                  (dom/el! "x-button" {"size" "sm" "variant" "ghost"
                                                       w/attr-delete-id id} [(dom/text-el! "span" "Delete")])])])])))

(defn- visible-tasks [tasks search status]
  (let [needle (str/lower-case search)]
    (filter (fn [^js t]
              (and (or (str/blank? status) (= status (gobj/get t "status")))
                   (or (str/blank? needle)
                       (str/includes? (str/lower-case (str (gobj/get t "title"))) needle))))
            tasks)))

(defn- update-stats! [^js route tasks]
  (let [counts (frequencies (map (fn [^js t] (gobj/get t "status")) tasks))]
    (doseq [^js s (array-seq (.querySelectorAll route "x-stat"))]
      (.setAttribute s "value" (str (get counts (.getAttribute s "data-status") 0))))))

(defn- loaded-tasks
  "The last server value the broker holds — read from its `.state`, not a copy we
  stash (values, not places). <barebuild-data> caches `.state` by reference, so
  this is the same value the load event delivered. Empty until the first load
  (phase idle/loading/error have no `data`)."
  [^js route]
  (let [^js st (.-state (.querySelector route w/id-tasks-data))]
    (array-seq (or (some-> st .-data) #js []))))

(defn- render-board! [^js route]
  (let [^js table  (.querySelector route w/id-tasks-table)
        ^js search (.querySelector route w/id-task-search)
        ^js sel    (.querySelector route w/id-status-filter)
        tasks      (loaded-tasks route)]
    (update-stats! route tasks)
    (dom/clear! table)
    (.appendChild table (header-row!))
    (doseq [t (visible-tasks tasks (or (.-value search) "") (or (.-value sel) ""))]
      (.appendChild table (task-row! t)))))

;; ── Handlers ─────────────────────────────────────────────────────────────────
;; Each takes only the event and resolves what it needs from `currentTarget` (the
;; element the listener sits on) — so they are named, parameter-free of captured
;; place, and `init-board!` reads as a wiring list (matching write_side.cljs).

(defn- on-route-change
  "Activation → read. route-change is pushed to every route on each resolution,
  so gate on the path; re-reads on each return to /tasks."
  [^js e]
  (when (= w/path-tasks (.. e -detail -path))
    (let [^js route (.-currentTarget e)]
      (set! (.-src (.querySelector route w/id-tasks-data)) "/api/tasks"))))

(defn- on-data-state
  "Toggle the loading/error surfaces from event.detail.state's phase; on `loaded`,
  render from the broker value (render-board! reads the broker's .state — no stash)."
  [^js e]
  (let [^js route    (.-currentTarget e)
        ^js state    (.. e -detail -state)
        phase        (.-phase state)
        ^js skeleton (.querySelector route "#board-skeleton")
        ^js err      (.querySelector route "#board-error")]
    (dom/show! skeleton (= "loading" phase))
    (dom/show! err (= "error" phase))
    (when (= "error" phase)
      (let [status (.-httpStatus state)]
        (.setAttribute err "text" (str "Couldn't load tasks" (when status (str " (" status ")")) "."))))
    ;; render-board! reads the value straight from the broker's .state — no stash.
    (when (= "loaded" phase)
      (render-board! route))))

(defn- on-filter-change
  "A filter control changed → re-render from the broker's retained value (no server round-trip)."
  [^js e]
  (render-board! (-> e .-currentTarget (.closest w/tag-route))))

(defn- on-new-task
  "New-task button pressed → open the modal. The form's submit is the Phase-4 seam."
  [^js e]
  (-> e .-currentTarget (.closest w/tag-route) (.querySelector w/id-new-task-modal) .show))

(defn init-board! []
  (let [^js route  (.querySelector js/document (w/route-selector w/path-tasks))
        ^js search (.querySelector route w/id-task-search)
        ^js sel    (.querySelector route w/id-status-filter)
        ^js newbtn (.querySelector route "#new-task-btn")]
    (.addEventListener route  w/ev-route-change  on-route-change)
    (.addEventListener route  w/ev-data-state    on-data-state)
    (.addEventListener search w/ev-search-input  on-filter-change)
    (.addEventListener sel    w/ev-select-change on-filter-change)
    (.addEventListener newbtn w/ev-press         on-new-task)))
