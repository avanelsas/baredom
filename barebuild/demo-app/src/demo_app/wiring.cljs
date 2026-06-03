(ns demo-app.wiring
  "Single source for the event names, route paths, and element ids the wiring
  namespaces share. The library mandates constants for event/tag/attr names
  because a misspelled `addEventListener` string fails *silently* — the listener
  just never fires, with no error — and the same risk applies here. Several ids
  and paths are also spelled in more than one place; keeping them here de-complects
  the spelling of a wire from the wiring. (File-local, single-use ids stay inline
  in their namespace — they are not duplicated, and they read 1:1 with index.html.)"
  (:require [clojure.string :as str]))

;; ── API base (cross-origin / backend-swap switch) ─────────────────────────────
;; API URLs are RELATIVE by default ("") → they resolve to the PAGE origin, exactly
;; what single-origin `bb serve` needs. Pass `?api=<origin>` (e.g.
;; ?api=http://localhost:3001) to drive a SEPARATE backend cross-origin — the
;; independent `bb serve-api` server (see server/API-CONTRACT.md).
;;
;; This is the SINGLE place the base is read and normalized — there is no JS boot
;; script and no `window` global. Read once at load (a backend swap is a page reload,
;; not a live mutation). A trailing slash is stripped so `(str api-base "/api/x")` can
;; never produce a double slash (which the server 404s — and which still origin-matches
;; the invalidate, so the failure would be silent). Every API URL flows through `api`
;; below, so read brokers, write actions, and invalidate-on srcs all prefix uniformly.
(def api-base
  (let [raw  (or (.get (js/URLSearchParams. (.. js/window -location -search)) "api") "")
        base (str/replace raw #"/+$" "")]
    (when (and (seq base) (not (str/includes? base "://")))
      (js/console.warn (str "demo-app: ?api=\"" base "\" has no scheme; it resolves against the "
                            "page origin, not a separate backend. Use e.g. http://localhost:3001")))
    base))

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
