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
            [demo-app.api :as demo-app.api]))

(def ^:private api-tasks    "/api/tasks")
(def ^:private api-settings "/api/settings")

(defn- api-task
  "The single-task endpoint. The id is the last URL segment on the detail route."
  [id]
  (str api-tasks "/" id))

(defn- task-id-from-url
  "The id of the task on the current /tasks/:id detail URL — its last segment."
  []
  (last (.split js/location.pathname "/")))

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
  "Drop name→value pairs whose value is a blank string. x-form reports every
   named control, defaulting an unset one to \"\" — and a present-but-blank key
   shadows server-side defaults (a blank `status` would override the API's
   {:status \"todo\"}). Omitting blanks lets those defaults apply."
  [^js values]
  (let [out #js {}]
    (doseq [k (js/Object.keys values)]
      (let [v (aget values k)]
        (when-not (and (string? v) (= "" (.trim v)))
          (aset out k v))))
    out))

(defn- toaster [] (.querySelector js/document "#toaster"))

(defn- notify!
  "Show a result toast (success/error) for a completed write. The toaster is a
  shared #toaster element; each seam reports its own outcome through here."
  [msg]
  (when-let [^js t (toaster)]
    (.toast t #js {:type "info" :message msg})))

;; ── 1. CREATE — New-task modal form submit ────────────────────────────────────
(defn- on-create-submit! [^js e]
  (.preventDefault e)
  (-> (demo-app.api/request "POST" api-tasks (without-blanks (.. e -detail -values)))
      (.then (fn [_created]
               (refresh-data! w/id-tasks-data)   ;; board re-reads /api/tasks → re-renders
               (when-let [^js m (.querySelector js/document "#new-task-modal")]
                 (.hide m))                       ;; x-modal :hide closes the dialog
               (notify! "Task created")))
      (.catch (fn [err]
                (js/console.error err)
                (notify! "Create failed")))))

;; ── 2. UPDATE — detail inline edit form submit ────────────────────────────────
(defn- on-edit-submit! [^js e]
  (.preventDefault e)
  ;; PUT the edited fields, then refresh the detail broker so the card + form
  ;; re-render from the server's value. The board isn't mounted on this route; it
  ;; re-reads on its next activation, so refreshing the detail alone is enough.
  (-> (demo-app.api/request "PUT" (api-task (task-id-from-url))
                            (without-blanks (.. e -detail -values)))
      (.then (fn [_updated]
               (refresh-data! w/id-detail-data)   ;; detail re-reads /api/tasks/:id → re-renders
               (notify! "Task updated")))
      (.catch (fn [err]
                (js/console.error err)
                (notify! "Update failed")))))

;; ── 3. DELETE (detail) — confirm dialogue confirmed ───────────────────────────
(defn- on-delete-confirm! [^js _e]
  ;; The dialogue closes itself on confirm (x-cancel-dialogue do-confirm! → close),
  ;; so we only DELETE, then navigate back to /tasks — which re-reads the board.
  (-> (demo-app.api/request "DELETE" (api-task (task-id-from-url)) nil)
      (.then (fn [_deleted]
               (navigate! w/path-tasks)           ;; /tasks activation re-reads the board
               (notify! "Task deleted")))
      (.catch (fn [err]
                (js/console.error err)
                (notify! "Delete failed")))))

;; ── 4. DELETE (board row) — per-row Delete button ─────────────────────────────
(defn- on-row-delete! [^js e]
  (when-let [^js btn (some-> (.-target e) (.closest (str "[" w/attr-delete-id "]")))]
    (let [id (.getAttribute btn w/attr-delete-id)]
      ;; Already on /tasks → no navigation; just DELETE and refresh the board read
      ;; so the row disappears (DOM = f(broker value, filters), untouched by hand).
      (-> (demo-app.api/request "DELETE" (api-task id) nil)
          (.then (fn [_deleted]
                   (refresh-data! w/id-tasks-data)
                   (notify! (str "Task #" id " deleted"))))
          (.catch (fn [err]
                    (js/console.error err)
                    (notify! "Delete failed")))))))

;; ── 5. SETTINGS — settings form submit ────────────────────────────────────────
(defn- on-settings-save! [^js e]
  (.preventDefault e)
  ;; PUT the settings, then refresh the settings broker so the form reflects the
  ;; PERSISTED value (server is truth — catches any server-side normalization).
  (-> (demo-app.api/request "PUT" api-settings (without-blanks (.. e -detail -values)))
      (.then (fn [_saved]
               (refresh-data! w/id-settings-data)  ;; re-read /api/settings → re-fill form
               (notify! "Settings saved")))
      (.catch (fn [err]
                (js/console.error err)
                (notify! "Save failed")))))

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
