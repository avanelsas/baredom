(ns demo-app.view
  "Pure projection helpers for the demo — calculation only, no DOM and no effects.
  This is the demo's small `model` layer: the status taxonomy and the user-facing
  strings the render namespaces (board / detail / settings) derive from a value.
  Keeping it data-driven means a new status or a reworded error is one edit here,
  not a hunt across the render code.")

;; The status taxonomy, as data. `:value` is the wire value (server + <option>);
;; `:label` and `:variant` are the projection. `status-variant` and any
;; status-aware rendering derive from this — never hand-spell the mapping again.
;; NOTE: index.html's <option> lists mirror these `:value`/`:label` pairs; they
;; are static markup (a build step would generate them), so keep them in sync.
(def statuses
  [{:value "todo"  :label "To do" :variant "neutral"}
   {:value "doing" :label "Doing" :variant "info"}
   {:value "done"  :label "Done"  :variant "success"}])

(def ^:private variant-by-status
  (into {} (map (juxt :value :variant)) statuses))

(defn status-variant
  "The x-badge variant for a task status; unknown statuses fall back to neutral."
  [status]
  (get variant-by-status status "neutral"))

(defn load-error-text
  "User-facing load-failure message for `noun` (e.g. \"tasks\"). Includes the HTTP
  status only when the broker carried one — a network-level failure has none, so
  appending it would render empty parens (\"Couldn't load tasks ().\")."
  [noun status]
  (if (some? status)
    (str "Couldn't load " noun " (" status ").")
    (str "Couldn't load " noun ".")))
