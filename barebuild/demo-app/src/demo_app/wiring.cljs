(ns demo-app.wiring
  "Single source for the event names, route paths, and element ids the wiring
  namespaces share. The library mandates constants for event/tag/attr names
  because a misspelled `addEventListener` string fails *silently* — the listener
  just never fires, with no error — and the same risk applies here. Several ids
  and paths are also spelled in more than one place; keeping them here de-complects
  the spelling of a wire from the wiring. (File-local, single-use ids stay inline
  in their namespace — they are not duplicated, and they read 1:1 with index.html.)"
  (:require [clojure.string :as str]))

;; ── Backends (data-driven backend registry — the demo's "swap server" picker) ──
;; The demo's whole server-agnosticism story as DATA: each entry is a backend the demo
;; can talk to. Adding one (e.g. the future Node server) is a SINGLE row here + that
;; server implementing server/API-CONTRACT.md on its port with CORS — nothing else
;; changes. `:base` "" = same-origin as the page (the default; works with just
;; `bb serve`). A non-empty `:base` is a SEPARATE origin, so that server must be running
;; AND CORS-enabled (the page is one origin; a different base = cross-origin). `:value`
;; is the option value, the sessionStorage value, and the `?backend=<value>` deep-link id.
;; PORTS: the labels/bases assume the servers' DEFAULT ports (serve.clj :3000,
;; serve_api.clj :3001). Override a server's PORT and you must edit its row here too —
;; there is no shared source of truth between the servers and these labels.
(def backends
  [{:value "same-origin" :label "Same-origin · :3000 (babashka)"    :base ""}
   {:value "bb-cors"     :label "CORS · :3001 (babashka serve_api)" :base "http://localhost:3001"}
   ;; Add the future Node server here once it implements the contract on :3002 with CORS:
   ;; {:value "node-cors" :label "CORS · :3002 (Node)" :base "http://localhost:3002"}
   ])

(def ^:private backend-storage-key "barebuildDemoBackend")

(defn- backend-by-value [v]
  (first (filter #(= v (:value %)) backends)))

;; NOTE: `url-backend-param` / `active-backend` / `api-base` read the browser environment at
;; namespace LOAD (not in init!) ON PURPOSE — the backend choice is fixed for the page's
;; lifetime (an epoch); swapping it is a page reload (a new epoch), never a live mutation. It
;; must be a load-time value because write_side's `(def api-tasks (w/api …))` reads api-base
;; at its own load. Don't "fix" this into an init-time read without reworking that chain.
;;
;; The `?backend=<value>` deep-link, read ONCE at load (the URL doesn't change between
;; ns-load and init!, so `active-backend` and `capture-backend!` share this single read).
(def ^:private url-backend-param
  (.get (js/URLSearchParams. (.. js/window -location -search)) "backend"))

(defn- store-backend! [v]
  (.setItem js/sessionStorage backend-storage-key v))

;; The active backend, resolved ONCE at load (a READ, no writes): a VALID `?backend=<value>`
;; deep-link wins, else the remembered sessionStorage choice, else the first (same-origin).
;; Persisting the deep-link is done separately by `capture-backend!` (from core/init!), so
;; resolving the value here has no side effects.
(def active-backend
  (or (backend-by-value url-backend-param)
      (backend-by-value (.getItem js/sessionStorage backend-storage-key))
      (first backends)))

(defn capture-backend!
  "If the URL carried a VALID `?backend=<value>`, remember it for the tab so the choice
  survives in-app navigation (the router drops the query) and reloads. Validates first, so
  an unknown/empty `?backend` is ignored rather than persisted as junk. Called from init!."
  []
  (when-let [b (backend-by-value url-backend-param)]
    (store-backend! (:value b))))

(defn select-backend!
  "Persist the chosen backend `value` for the tab; `active-backend` reads it on the next
  load. The caller reloads to apply it — a backend swap re-initialises every broker
  cleanly, rather than live-repointing (which would make the base a place, not a value)."
  [value]
  (store-backend! value))

;; Every API URL flows through `api`. The base is normalised (trailing slash stripped) so a
;; `backends` row pasted with a trailing slash can't produce `//api/x` — which the server
;; 404s while the invalidate still origin-matches (a silent failure). "" → relative → origin.
(def api-base (str/replace (:base active-backend) #"/+$" ""))

(defn api
  "Prefix an API path with the configured base (empty default = relative URL → page
  origin). The single home for API-URL construction so a cross-origin swap stays
  uniform across the read brokers, the write actions, and the invalidate-on srcs."
  [path]
  (str api-base path))

;; ── Events ───────────────────────────────────────────────────────────────────
;; Orchestration (barebuild-*) — listened for on the route element.
(def ev-route-change "barebuild-route-change")
(def ev-data-state   "barebuild-data-state")
(def ev-data-refresh "barebuild-data-refresh")  ; dispatched AT a <barebuild-data> to refetch its src
(def ev-navigate     "barebuild-navigate")      ; dispatched AT the router to SPA-navigate without a click
(def ev-action-state "barebuild-action-state")  ; emitted by a <barebuild-action> on each phase transition
(def ev-invalidate   "barebuild-invalidate")    ; document-level protocol: a matching <barebuild-data> refetches
;; Component events the demo reacts to.
(def ev-search-input  "x-search-field-input")
(def ev-select-change "select-change")
(def ev-press         "press")
(def ev-form-submit   "x-form-submit")
(def ev-confirm       "x-cancel-dialogue-confirm")

;; ── Route paths (each used as a route selector AND a path gate) ───────────────
(def path-tasks    "/tasks")
(def path-task     "/tasks/:id")
(def path-settings "/settings")

(def tag-route  "barebuild-route")
(def tag-router "barebuild-router")

(defn route-selector
  "The querySelector that finds the <barebuild-route> declaring `path`."
  [path]
  (str tag-route "[path='" path "']"))

;; Marks a board row's Delete button — set by board, matched by write_side.
(def attr-delete-id "data-delete-id")

;; ── Element ids referenced from more than one place ──────────────────────────
;; Read-side brokers (set their `src` from the read ns; refreshed from write_side).
(def id-tasks-data    "#tasks-data")
(def id-detail-data   "#detail-data")
(def id-settings-data "#settings-data")
;; Board read surface.
(def id-tasks-table   "#tasks-table")
(def id-task-search   "#task-search")
(def id-status-filter "#status-filter")
;; Write-seam targets (built by board/detail/settings, wired by write_side).
(def id-new-task-modal "#new-task-modal")
(def id-new-task-form  "#new-task-form")
(def id-edit-task-form "#edit-task-form")
(def id-settings-form  "#settings-form")
(def id-delete-confirm "#delete-confirm")
