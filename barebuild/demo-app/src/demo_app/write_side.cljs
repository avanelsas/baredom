(ns demo-app.write-side
  "★ THE PHASE-4 TELEMETRY SEAM — this is the file you (a Phase-4 participant) edit. ★

  BareBuild V1 ships the READ side only (router / route / data). It ships NO
  write-side elements: no <barebuild-action>, no <barebuild-bind>, no
  <barebuild-invalidate-on>. That is deliberate. The write-side contracts will be
  designed FROM what people actually write here — not guessed first. So the demo's
  create / update / delete surfaces are built (forms, buttons, a confirm dialogue)
  but their submit→fetch→DOM→invalidate glue is left as the five inert stubs below.

  Your job: make these actually work, however feels natural to you. Then tell us
  how you did it. The backend (server/serve.clj, `bb serve`) already implements the
  write endpoints, so your fetch will round-trip:

    POST   /api/tasks            create   (body: JSON task)        → 201 task
    PUT    /api/tasks/:id        update   (body: JSON partial)     → 200 task
    DELETE /api/tasks/:id        delete                            → 204
    PUT    /api/settings         save     (body: JSON partial)     → 200 settings

  The five questions we are watching (also in barebuild/docs/write-side-design-notes.md):
    1. submit → fetch: how did you turn the submit into a request? what payload shape?
    2. response → DOM: how did you make the read reflect the write? re-set `src` to
       re-read? splice the row directly? dispatch `barebuild-data-refresh`? a refs map?
    3. invalidation: how did you decide the /api/tasks read was stale after a write?
    4. re-frame: did you reach for it? at what point, and for what?
    5. renderer: did you wish for a Hiccup renderer? what shape would you have wanted?

  DON'T copy barebuild/docs/write-side-sketch.md — that is our PRIOR guess; we want
  your unbiased version to compare against it. Write what you'd write in a real app.

  Until you fill these in, every stub just shows a toast and changes nothing, so the
  demo stays read-only-correct."
  (:require [demo-app.wiring :as w]))

(defn- toaster [] (.querySelector js/document "#toaster"))

(defn- notify!
  "Surface that a seam fired but is unwired. Replace these calls with your real
  result handling (success/error toasts are fine — keep them if you like)."
  [msg]
  (when-let [^js t (toaster)]
    (.toast t #js {:type "info" :message msg})))

;; ── 1. CREATE — New-task modal form submit ────────────────────────────────────
(defn- on-create-submit! [^js e]
  (.preventDefault e)
  ;; AVAILABLE: (.. e -detail -values) → #js {title, description, status, assignee, due}
  ;;            (the modal is #new-task-modal; close it with (.hide modal) on success)
  ;; YOUR CODE: POST /api/tasks with that payload, then make the board reflect it
  ;;            (re-set src on #tasks-data to re-read, or splice the new row), then
  ;;            close the modal. ▼▼▼ write it here ▼▼▼

  ;; ▲▲▲ write it here ▲▲▲
  (notify! "Create is unwired — fill on-create-submit! in write_side.cljs"))

;; ── 2. UPDATE — detail inline edit form submit ────────────────────────────────
(defn- on-edit-submit! [^js e]
  (.preventDefault e)
  ;; AVAILABLE: (.. e -detail -values) → the edited fields.
  ;;            The task id is the last URL segment: (last (.split js/location.pathname "/"))
  ;; YOUR CODE: PUT /api/tasks/:id, then re-read the detail (and/or the board).

  (notify! "Update is unwired — fill on-edit-submit! in write_side.cljs"))

;; ── 3. DELETE (detail) — confirm dialogue confirmed ───────────────────────────
(defn- on-delete-confirm! [^js _e]
  ;; AVAILABLE: the task id is the last URL segment (see above). Close the dialogue
  ;;            with (.close (.querySelector js/document "#delete-confirm")).
  ;; YOUR CODE: DELETE /api/tasks/:id, then navigate back to /tasks and re-read.

  (notify! "Delete is unwired — fill on-delete-confirm! in write_side.cljs"))

;; ── 4. DELETE (board row) — per-row Delete button ─────────────────────────────
(defn- on-row-delete! [^js e]
  (when-let [^js btn (some-> (.-target e) (.closest (str "[" w/attr-delete-id "]")))]
    (let [id (.getAttribute btn w/attr-delete-id)]
      ;; YOUR CODE: DELETE /api/tasks/<id>, then refresh the board read so the row
      ;;            disappears (re-set src on #tasks-data, or remove the row node).

      (notify! (str "Row delete (#" id ") is unwired — fill on-row-delete! in write_side.cljs")))))

;; ── 5. SETTINGS — settings form submit ────────────────────────────────────────
(defn- on-settings-save! [^js e]
  (.preventDefault e)
  ;; AVAILABLE: (.. e -detail -values) → #js {theme, page-size, default-status}
  ;; YOUR CODE: PUT /api/settings, then confirm (toast) and/or re-read.

  (notify! "Save settings is unwired — fill on-settings-save! in write_side.cljs"))

(defn attach-stubs!
  "Attach the inert write-side seams. Called from core/init! alongside the
  read-side wiring (before component registration, so the listeners are live when
  the elements upgrade). Each handler is a no-op + toast until you wire it."
  []
  (let [^js new-form      (.querySelector js/document w/id-new-task-form)
        ^js edit-form     (.querySelector js/document w/id-edit-task-form)
        ^js confirm       (.querySelector js/document w/id-delete-confirm)
        ^js table         (.querySelector js/document w/id-tasks-table)
        ^js settings-form (.querySelector js/document w/id-settings-form)]
    (.addEventListener new-form      w/ev-form-submit on-create-submit!)
    (.addEventListener edit-form     w/ev-form-submit on-edit-submit!)
    (.addEventListener confirm       w/ev-confirm     on-delete-confirm!)
    ;; Board row Delete buttons are delegated through the table (press bubbles).
    (.addEventListener table         w/ev-press       on-row-delete!)
    (.addEventListener settings-form w/ev-form-submit on-settings-save!)))
