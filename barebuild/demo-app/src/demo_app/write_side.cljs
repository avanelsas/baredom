(ns demo-app.write-side
  "Write-side wiring for the demo — the live create / update / delete / settings
  handlers (create modal, detail edit, delete confirm, board-row delete, settings).

  ★ ALPHA-BRANCH HYBRID ★ This branch ships the experimental write-side elements, and the
  three FORM flows are ported onto them:
    • CREATE   — <barebuild-action> wraps the new-task modal (POST /api/tasks).
    • UPDATE   — <barebuild-action> wraps the edit form (PUT /api/tasks/:id; the dynamic
                 URL is set in detail/on-route-change).
    • SETTINGS — <barebuild-action> wraps the settings form (PUT /api/settings).
  Each has a child <barebuild-invalidate-on> that refetches on success. So for these,
  write_side only reacts to barebuild-action-state for the UI side-effects the elements
  do NOT cover (close modal, reset, toast).

  The two DELETES stay hand-wired — the action can't drive them (a confirm dialogue and N
  per-id row buttons carry no form values) — but they COORDINATE through the same document
  protocols the declarative flows use: detail-delete dispatches `barebuild-navigate`,
  row-delete dispatches `barebuild-invalidate {src}`. So every write invalidates-or-navigates
  the same way; only the trigger differs. See the port evaluation in
  barebuild/docs/write-side-design-notes.md.

  The original (V1) hand-wiring was the Phase-4 telemetry that informed the design. The
  five questions it answered, for reference:

    1. submit → fetch: hook x-form-submit; POST/PUT/DELETE via api/request. Payload is
       event.detail.values, blank-stripped on CREATE only (without-blanks).
    2. response → DOM: the response is NOT read — the server is re-read. Each handler
       dispatches barebuild-data-refresh at the backing broker (or navigates) and the
       existing read pipeline reprojects. No DOM splicing, no app-level place-state.
    3. invalidation: the writer knows its reader by id and refreshes exactly that broker
       (#tasks-data / #detail-data / #settings-data); delete navigates, which re-reads.
    4. re-frame: not reached for.
    5. renderer: no Hiccup renderer wanted — refetch-and-reproject built no DOM by hand.

  The backend (server/serve.clj, `bb serve`) implements the endpoints:
    POST /api/tasks · PUT /api/tasks/:id · DELETE /api/tasks/:id · PUT /api/settings"
  (:require [demo-app.wiring :as w]
            [demo-app.api :as api]))

(def ^:private api-tasks "/api/tasks")

(defn- api-task
  "The single-task endpoint for `id` — used by the board row delete, whose id comes
   straight off the clicked button's attribute."
  [id]
  (str api-tasks "/" id))

(defn- detail-endpoint
  "The /api/tasks/:id endpoint for the detail route the event `e` fired in — read
   from the detail broker's already-resolved `src` (the value the read side set on
   activation), not re-derived from js/location. The write stays a function of the
   value in hand rather than reaching into the address bar (a place)."
  [^js e]
  (let [^js route  (.closest (.-currentTarget e) w/tag-route)
        ^js broker (.querySelector route w/id-detail-data)]
    (.-src broker)))

(defn- dispatch-invalidate!
  "Dispatch the document-level `barebuild-invalidate {src}` protocol — the SAME signal
   `<barebuild-invalidate-on>` emits for the declarative flows. Any <barebuild-data>
   whose src matches refetches. Lets a hand-wired write (a delete the action can't drive)
   invalidate exactly like the declarative ones, so the coordination stays uniform."
  [src]
  (.dispatchEvent js/document
                  (js/CustomEvent. w/ev-invalidate
                                   #js {:bubbles true :composed true :detail #js {:src src}})))

(defn- navigate!
  "Programmatic SPA navigation: dispatch the router's `barebuild-navigate` AT the
   router element, which pushState's `path` and re-resolves. Re-resolving /tasks
   re-reads the board (its on-route-change re-sets src), so a delete needs no
   separate refresh — the activation read replaces the stale list."
  [path]
  (when-let [^js router (.querySelector js/document w/tag-router)]
    (.dispatchEvent router (js/CustomEvent. w/ev-navigate
                                            #js {:bubbles true :composed true
                                                 :detail  #js {:path path}}))))

(defn- toaster [] (.querySelector js/document "#toaster"))

(defn- notify!
  "Show a result toast for a completed write through the shared #toaster. `type` is an
  x-toast type — \"success\" / \"error\" / \"info\". \"error\" promotes the toast to
  role=alert (assertive), so a failure is announced and reads as an error, not info."
  ([msg] (notify! msg "info"))
  ([msg type]
   (when-let [^js t (toaster)]
     (.toast t #js {:type type :message msg}))))

(defn- on-failure
  "Build a promise `.catch` handler that logs the error and shows an error toast.
   One shape for all five seams, so the failure path cannot drift between them."
  [msg]
  (fn report-failure [^js err]
    (js/console.error err)
    (notify! msg "error")))

;; ── 1. CREATE — DECLARATIVE (barebuild-action + invalidate-on) ─────────────────
;; The elements do submit→POST→refetch /api/tasks (see #create-action in index.html).
;; write_side only reacts to the published barebuild-action-state for the UI side-
;; effects the elements do NOT cover: close the modal, reset the form, toast.
(defn- on-create-state! [^js e]
  (case (.. e -detail -state -phase)
    "success" (do (when-let [^js m (.querySelector js/document w/id-new-task-modal)] (.hide m))
                  (when-let [^js f (.querySelector js/document w/id-new-task-form)] (.reset f))
                  (notify! "Task created" "success"))
    "error"   (notify! "Create failed" "error")
    nil))

;; ── 2 + 5. UPDATE / SETTINGS — DECLARATIVE (barebuild-action + invalidate-on) ──
;; The elements PUT and refetch (#edit-action / #settings-action in index.html; the edit
;; action's dynamic /api/tasks/:id URL is set in detail/on-route-change). The only side-
;; effect they don't cover is the result toast — so write_side just reacts to the state.
(defn- toasting-state-handler
  "A barebuild-action-state listener that toasts `success-msg` / `error-msg` by phase.
   Shared by update + settings (their only uncovered side-effect is the toast)."
  [success-msg error-msg]
  (fn on-state [^js e]
    (case (.. e -detail -state -phase)
      "success" (notify! success-msg "success")
      "error"   (notify! error-msg "error")
      nil)))

;; ── 3. DELETE (detail) — imperative trigger + navigate protocol ───────────────
;; The trigger is a confirm dialogue (no values, two-step) → hand-wired. The deleted
;; task is gone, so the success effect is navigate-away (not refetch — that would 404);
;; navigating to /tasks re-reads the board for free via route activation.
(defn- on-delete-confirm! [^js e]
  ;; The dialogue closes itself on confirm (x-cancel-dialogue do-confirm! → close).
  (-> (api/request "DELETE" (detail-endpoint e) nil)
      (.then (fn deleted [_deleted]
               (navigate! w/path-tasks)           ;; /tasks activation re-reads the board
               (notify! "Task deleted" "success")))
      (.catch (on-failure "Delete failed"))))

;; ── 4. DELETE (board row) — imperative trigger + invalidate protocol ──────────
;; Per-row Delete buttons are a dynamic list → event delegation (the action can't wrap
;; N per-id buttons). The DELETE is hand-wired, but the refetch goes through the SAME
;; `barebuild-invalidate` protocol the declarative flows use — so coordination is uniform.
(defn- on-row-delete! [^js e]
  (when-let [^js btn (some-> (.-target e) (.closest (str "[" w/attr-delete-id "]")))]
    (let [id (.getAttribute btn w/attr-delete-id)]
      (-> (api/request "DELETE" (api-task id) nil)
          (.then (fn row-deleted [_deleted]
                   (dispatch-invalidate! api-tasks)   ;; board self-matches src → refetches
                   (notify! (str "Task #" id " deleted") "success")))
          (.catch (on-failure "Delete failed"))))))

(defn attach-write-handlers!
  "Attach the live write-side wiring. Called from core/init! alongside the read-side
  wiring (before component registration, so the listeners are live when the elements
  upgrade). CREATE / UPDATE / SETTINGS are DECLARATIVE — the barebuild-action elements
  do the fetch + invalidation; we only react to barebuild-action-state for the UI side-
  effects they don't cover. The two DELETES are hand-wired triggers (no values / dynamic
  rows) that coordinate via the same navigate / invalidate protocols."
  []
  (let [^js create-action   (.querySelector js/document "#create-action")
        ^js edit-action     (.querySelector js/document "#edit-action")
        ^js settings-action (.querySelector js/document "#settings-action")
        ^js confirm         (.querySelector js/document w/id-delete-confirm)
        ^js table           (.querySelector js/document w/id-tasks-table)]
    (.addEventListener create-action   w/ev-action-state on-create-state!)
    (.addEventListener edit-action     w/ev-action-state (toasting-state-handler "Task updated" "Update failed"))
    (.addEventListener settings-action w/ev-action-state (toasting-state-handler "Settings saved" "Save failed"))
    ;; Deletes: hand-wired triggers, protocol coordination.
    (.addEventListener confirm w/ev-confirm on-delete-confirm!)
    (.addEventListener table   w/ev-press   on-row-delete!)))
