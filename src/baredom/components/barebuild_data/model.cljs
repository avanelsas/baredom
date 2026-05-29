(ns baredom.components.barebuild-data.model)

;; barebuild-data fetches a URL and publishes the response as ONE value (.state).
;; This namespace is the pure layer: the closed phase set and the state-value
;; constructors. There is no DOM here and no fetch-timing knobs — when to read is
;; decided in the effect shell (barebuild_data.cljs), driven only by `src` and
;; the refresh event.

(def tag-name "barebuild-data")

(def attr-src "src")

(def observed-attributes #js [attr-src])

;; ── Events ───────────────────────────────────────────────────────────────────
(def event-data-state   "barebuild-data-state")    ; emitted on every phase transition
(def event-data-refresh "barebuild-data-refresh")  ; listened for; user-driven manual refetch

;; ── Phases ───────────────────────────────────────────────────────────────────
;; The closed set of phase values, published so a user `case` over `:phase`
;; can reference it. Adding a value here is a BREAKING change (release-note it):
;; existing exhaustive `case` expressions would silently miss the new phase.
(def phases #{:idle :loading :loaded :error})

(def property-api
  {:src   {:type 'string :reflects-attribute attr-src :default ""}
   :state {:type 'object :readonly true}})

(def event-schema
  {event-data-state {:detail {:state 'object}}})

(def method-api {})

;; ── State constructors ─────────────────────────────────────────────────────────
;; Keys are present only when meaningful for the phase (absent, not nil-valued):
;; :data only in :loaded, :error only in :error, :http-status when a response
;; carried one. This keeps `(contains? state :data)` an honest question.

(defn idle-state []
  {:phase :idle})

(defn loading-state []
  {:phase :loading})

(defn loaded-state
  "Successful load. `data` is the parsed (JS) response body; `status` the HTTP
  status code."
  [data status]
  (cond-> {:phase :loaded :data data}
    (some? status) (assoc :http-status status)))

(defn error-state
  "Failed load. `message` is a human-readable string; `status` the HTTP status
  when one was received (nil for a transport-level failure)."
  [message status]
  (cond-> {:phase :error :error message}
    (some? status) (assoc :http-status status)))
