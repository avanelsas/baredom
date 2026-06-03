(ns baredom.components.barebuild-action.model
  (:require [goog.object :as gobj]
            [baredom.components.barebuild.protocol :as protocol]))

;; barebuild-action wraps a SUBMIT EMITTER by containment: any descendant
;; dispatching a cancelable, bubbling event of the configured `submit-event`
;; name, carrying a values map at `values-path` in `event.detail`. On that event
;; it preventDefault()s, JSON-encodes the values, fetches `action` with `method`,
;; and publishes the HTTP result as ONE value (.state) + a `barebuild-action-state`
;; event per phase transition. It does NOT dispatch invalidation (its child
;; <barebuild-invalidate-on> does) and does NOT name x-form anywhere.
;;
;; ALPHA (4.0.0-alpha) — explicitly unstable. Attribute shapes may change before
;; stable; the no-selectors / values-not-places spine will not.
;;
;; State is a PLAIN JS OBJECT (string phase), identical in spirit to
;; barebuild-data's: the per-component ESM ships separate from a consumer's
;; cljs.core, so a CLJS map would carry this module's Keyword class (unreadable
;; by the consumer). A JS object is readable by anyone.

(def tag-name "barebuild-action")

(def attr-name         "name")
(def attr-action       "action")
(def attr-method       "method")
(def attr-submit-event "submit-event")
(def attr-values-path  "values-path")

(def observed-attributes
  #js [attr-name attr-action attr-method attr-submit-event attr-values-path])

;; ── Events ───────────────────────────────────────────────────────────────────
;; The cross-component name comes from the shared protocol ns (single source of
;; truth — see protocol.cljs), keeping the handshake compile-coupled.
(def event-action-state protocol/event-action-state)  ; emitted on every phase transition

;; ── Defaults ───────────────────────────────────────────────────────────────────
(def default-method      "POST")
;; The default values path. NOT a coupling to x-form: it is the path WITHIN
;; event.detail (any emitter putting its values at detail.values matches), never
;; the event name — `submit-event` is required and names the emitter explicitly.
(def default-values-path "[:values]")

;; ── Phases ───────────────────────────────────────────────────────────────────
;; Closed set of phase STRINGS; each named once and reused by `phases` and the
;; constructors, so the set can never drift from what the constructors stamp.
(def phase-idle       "idle")
(def phase-submitting "submitting")
(def phase-success    "success")
(def phase-error      "error")

(def phases #{phase-idle phase-submitting phase-success phase-error})

(def property-api
  {:name        {:type 'string :reflects-attribute attr-name         :default ""}
   :action      {:type 'string :reflects-attribute attr-action       :default ""}
   :method      {:type 'string :reflects-attribute attr-method       :default ""}
   :submitEvent {:type 'string :reflects-attribute attr-submit-event :default ""}
   :valuesPath  {:type 'string :reflects-attribute attr-values-path  :default ""}
   :state       {:type 'object :readonly true}})

(def event-schema
  {event-action-state {:detail {:name 'string :state 'object}}})

(def method-api
  {:submit {:args [{:name "values" :type 'object}] :returns 'void}})

;; ── State constructors (plain JS objects) ──────────────────────────────────────
;; The action's `name` is carried at the EVENT-detail top level (so matchers read
;; it without traversing state), not duplicated inside state. Keys are present
;; only when meaningful for the phase (absent → undefined, not null-valued).

(defn idle-state [] (js/Object.freeze #js {:phase phase-idle}))

(defn submitting-state [] (js/Object.freeze #js {:phase phase-submitting}))

(defn success-state
  "Successful mutation. `response` is the parsed (JS) body (nil for an empty /
  204 body); `status` the HTTP status code."
  [response status]
  (let [s #js {:phase phase-success :response response}]
    (when (some? status) (gobj/set s "httpStatus" status))
    (js/Object.freeze s)))

(defn error-state
  "Failed mutation. `message` is human-readable; `status` the HTTP status when one
  was received (nil for a transport-level failure)."
  [message status]
  (let [s #js {:phase phase-error :error message}]
    (when (some? status) (gobj/set s "httpStatus" status))
    (js/Object.freeze s)))
