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
  (:require [demo-app.wiring :as w]
            [demo-app.api :as api]))

(def ^:private api-tasks    "/api/tasks")
(def ^:private api-settings "/api/settings")

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

(defn- refresh-data!
  "Ask a <barebuild-data> broker (by id selector) to re-run its current src fetch."
  [selector]
  (when-let [^js el (.querySelector js/document selector)]
    (.dispatchEvent el (js/CustomEvent. w/ev-data-refresh))))

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

(defn- without-blanks
  "Drop name→value pairs whose value is a blank string. CREATE-ONLY. x-form reports
   every named control, defaulting an unset one to \"\", and on a POST a present-but-blank
   key shadows the server's default (a blank `status` would override {:status \"todo\"});
   omitting blanks lets the defaults apply. Do NOT use on a PUT/merge endpoint — there a
   blank is a deliberate \"clear this field\", and dropping it makes the clear silently fail."
  [^js values]
  (let [out #js {}]
    (doseq [k (js/Object.keys values)]
      (let [v (aget values k)]
        (when-not (and (string? v) (= "" (.trim v)))
          (aset out k v))))
    out))

(defn- with-number
  "Return a shallow copy of `values` with key `k` parsed to an integer (left as-is if
   unparseable). x-form reports a number control's value as a string; on a PUT/merge that
   would otherwise flip the server field's type (e.g. page-size 25 → \"25\") on first save."
  [^js values k]
  (let [out (js/Object.assign #js {} values)
        n   (js/parseInt (aget out k) 10)]
    (when-not (js/isNaN n) (aset out k n))
    out))

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

;; ── 1. CREATE — New-task modal form submit ────────────────────────────────────
(defn- on-create-submit! [^js e]
  (.preventDefault e)
  ;; Capture the form NOW: in the async .then, e.currentTarget is null (the event has
  ;; finished dispatching), so a reset must hold the handle from sync time.
  (let [^js form (.-currentTarget e)]
    (-> (api/request "POST" api-tasks (without-blanks (.. e -detail -values)))
        (.then (fn created [_created]
                 (refresh-data! w/id-tasks-data)   ;; board re-reads /api/tasks → re-renders
                 (when-let [^js m (.querySelector js/document w/id-new-task-modal)]
                   (.hide m))                       ;; x-modal :hide closes the dialog
                 (.reset form)                      ;; clear inputs so a reopen starts blank
                 (notify! "Task created" "success")))
        (.catch (on-failure "Create failed")))))

;; ── 2. UPDATE — detail inline edit form submit ────────────────────────────────
(defn- on-edit-submit! [^js e]
  (.preventDefault e)
  ;; PUT the edited fields AS-IS (no blank-stripping): this is a merge endpoint, so a
  ;; blank field is a deliberate "clear it" — dropping blanks would make that clear
  ;; silently fail. Then refresh the detail broker so the card + form re-render from the
  ;; server. The board isn't mounted on this route; it re-reads on its next activation.
  (-> (api/request "PUT" (detail-endpoint e) (.. e -detail -values))
      (.then (fn updated [_updated]
               (refresh-data! w/id-detail-data)   ;; detail re-reads /api/tasks/:id → re-renders
               (notify! "Task updated" "success")))
      (.catch (on-failure "Update failed"))))

;; ── 3. DELETE (detail) — confirm dialogue confirmed ───────────────────────────
(defn- on-delete-confirm! [^js e]
  ;; The dialogue closes itself on confirm (x-cancel-dialogue do-confirm! → close),
  ;; so we only DELETE, then navigate back to /tasks — which re-reads the board.
  (-> (api/request "DELETE" (detail-endpoint e) nil)
      (.then (fn deleted [_deleted]
               (navigate! w/path-tasks)           ;; /tasks activation re-reads the board
               (notify! "Task deleted" "success")))
      (.catch (on-failure "Delete failed"))))

;; ── 4. DELETE (board row) — per-row Delete button ─────────────────────────────
(defn- on-row-delete! [^js e]
  (when-let [^js btn (some-> (.-target e) (.closest (str "[" w/attr-delete-id "]")))]
    (let [id (.getAttribute btn w/attr-delete-id)]
      ;; Already on /tasks → no navigation; just DELETE and refresh the board read
      ;; so the row disappears (DOM = f(broker value, filters), untouched by hand).
      (-> (api/request "DELETE" (api-task id) nil)
          (.then (fn row-deleted [_deleted]
                   (refresh-data! w/id-tasks-data)
                   (notify! (str "Task #" id " deleted") "success")))
          (.catch (on-failure "Delete failed"))))))

;; ── 5. SETTINGS — settings form submit ────────────────────────────────────────
(defn- on-settings-save! [^js e]
  (.preventDefault e)
  ;; PUT the settings AS-IS but coerce page-size to a number: x-form reports it as a
  ;; string, and the server merges, so an uncoerced save flips the stored field
  ;; number→string. Then refresh the settings broker so the form reflects the PERSISTED
  ;; value (server is truth — catches any server-side normalization).
  (-> (api/request "PUT" api-settings (with-number (.. e -detail -values) "page-size"))
      (.then (fn saved [_saved]
               (refresh-data! w/id-settings-data)  ;; re-read /api/settings → re-fill form
               (notify! "Settings saved" "success")))
      (.catch (on-failure "Save failed"))))

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
