(ns demo-app.wiring
  "Single source for the event names, route paths, and element ids the wiring
  namespaces share. The library mandates constants for event/tag/attr names
  because a misspelled `addEventListener` string fails *silently* — the listener
  just never fires, with no error — and the same risk applies here. Several ids
  and paths are also spelled in more than one place; keeping them here de-complects
  the spelling of a wire from the wiring. (File-local, single-use ids stay inline
  in their namespace — they are not duplicated, and they read 1:1 with index.html.)")

;; ── Events ───────────────────────────────────────────────────────────────────
;; Orchestration (barebuild-*) — listened for on the route element.
(def ev-route-change "barebuild-route-change")
(def ev-data-state   "barebuild-data-state")
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

(def tag-route "barebuild-route")

(defn route-selector
  "The querySelector that finds the <barebuild-route> declaring `path`."
  [path]
  (str tag-route "[path='" path "']"))

;; Marks a board row's Delete button — set by board, matched by write_side.
(def attr-delete-id "data-delete-id")

;; ── Element ids referenced from more than one place ──────────────────────────
;; Board read surface.
(def id-tasks-data    "#tasks-data")
(def id-tasks-table   "#tasks-table")
(def id-task-search   "#task-search")
(def id-status-filter "#status-filter")
;; Write-seam targets (built by board/detail/settings, wired by write_side).
(def id-new-task-form  "#new-task-form")
(def id-edit-task-form "#edit-task-form")
(def id-settings-form  "#settings-form")
(def id-delete-confirm "#delete-confirm")
