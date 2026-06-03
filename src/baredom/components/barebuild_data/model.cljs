(ns baredom.components.barebuild-data.model
  (:require [goog.object :as gobj]
            [baredom.components.barebuild.protocol :as protocol]))

;; barebuild-data fetches a URL and publishes the response as ONE value (.state).
;; This namespace is the pure layer: the closed phase set and the state-value
;; constructors. There is no DOM here and no fetch-timing knobs — when to read is
;; decided in the effect shell (barebuild_data.cljs), driven only by `src` and
;; the refresh event.
;;
;; State is a PLAIN JS OBJECT (not a CLJS map): { phase, data, error, httpStatus }
;; with a STRING phase. This is the load-bearing choice — the components ship as
;; per-component ESM, so a consumer's app is shadow-compiled with its OWN cljs.core,
;; separate from the module's. A CLJS persistent map would carry this module's
;; Keyword/PersistentArrayMap classes, which the consumer's cljs.core cannot read
;; (different Keyword class → key lookups return nil). A JS object is readable by
;; any consumer — vanilla JS or a separately-compiled CLJS app via `.-phase` etc.

(def tag-name "barebuild-data")

(def attr-src "src")

(def observed-attributes #js [attr-src])

;; ── Events ───────────────────────────────────────────────────────────────────
;; The two cross-component names come from the shared protocol ns (single source
;; of truth — see protocol.cljs); refresh is a self-only name, declared locally.
(def event-data-state   protocol/event-data-state)   ; emitted on every phase transition
(def event-data-refresh "barebuild-data-refresh")    ; listened for on self; user-driven manual refetch
(def event-invalidate   protocol/event-invalidate)   ; listened for at document; refetch when detail.src matches our src

;; ── Phases ───────────────────────────────────────────────────────────────────
;; The closed set of phase STRINGS, published so a consumer comparing `.phase`
;; can reference it. Each phase string is named once here and reused by the
;; `phases` set AND the state constructors below — so a rename touches one place
;; and the set can never drift from what the constructors actually stamp. Adding
;; a value here is a BREAKING change (release-note it).
(def phase-idle    "idle")
(def phase-loading "loading")
(def phase-loaded  "loaded")
(def phase-error   "error")

(def phases #{phase-idle phase-loading phase-loaded phase-error})

(def property-api
  {:src   {:type 'string :reflects-attribute attr-src :default ""}
   :state {:type 'object :readonly true}})

(def event-schema
  {event-data-state {:detail {:state 'object}}})

(def method-api {})

;; ── State constructors (plain JS objects) ──────────────────────────────────────
;; Keys are present only when meaningful for the phase (absent → undefined, not
;; null-valued): `data` only in loaded, `error` only in error, `httpStatus` when a
;; response carried one. So `("httpStatus" in state)` / `state.data !== undefined`
;; are honest questions.

(defn idle-state [] (js/Object.freeze #js {:phase phase-idle}))

(defn loading-state [] (js/Object.freeze #js {:phase phase-loading}))

;; Every state wrapper is frozen, not just the idle singleton: `.state` returns
;; the cached object by reference (identical? across reads — see barebuild_data.cljs),
;; and `same-state?` diffs that cached object on the next transition. A consumer
;; mutating it would corrupt the change-guard's baseline (suppressing or spuriously
;; firing events), so the "treat `.state` as read-only" contract is enforced
;; uniformly here. The freeze is shallow by design — the parsed `data` payload is
;; the consumer's to own; only the wrapper's phase/error/httpStatus are locked.

(defn loaded-state
  "Successful load. `data` is the parsed (JS) response body (nil for an empty /
  204 body); `status` the HTTP status code."
  [data status]
  (let [s #js {:phase phase-loaded :data data}]
    (when (some? status) (gobj/set s "httpStatus" status))
    (js/Object.freeze s)))

(defn error-state
  "Failed load. `message` is a human-readable string; `status` the HTTP status
  when one was received (nil for a transport-level failure)."
  [message status]
  (let [s #js {:phase phase-error :error message}]
    (when (some? status) (gobj/set s "httpStatus" status))
    (js/Object.freeze s)))
