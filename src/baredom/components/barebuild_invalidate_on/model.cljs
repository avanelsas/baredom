(ns baredom.components.barebuild-invalidate-on.model)

;; barebuild-invalidate-on is placed as a CHILD of a source element (typically
;; <barebuild-action>). It listens to the source via parentNode.addEventListener
;; for the configured `event` (default barebuild-action-state); on a match
;; (when-phase against detail.state.phase, when-name against detail.name; at least
;; one required, AND-composed) it dispatches a bubbling `barebuild-invalidate {:src}`
;; at document level. Any <barebuild-data> with a matching src self-matches (exact
;; URL.pathname equality) and refetches. No selectors: source = parent by
;; construction, receiver matched by URL.
;;
;; ALPHA (4.0.0-alpha) — explicitly unstable. Attribute shapes may change.

(def tag-name "barebuild-invalidate-on")

(def attr-event      "event")
(def attr-when-phase "when-phase")
(def attr-when-name  "when-name")
(def attr-src        "src")

(def observed-attributes
  #js [attr-event attr-when-phase attr-when-name attr-src])

;; ── Events ───────────────────────────────────────────────────────────────────
(def event-invalidate "barebuild-invalidate")  ; dispatched on a full matcher match

;; The default source event. Matches <barebuild-action>; override to
;; "barebuild-data-state" or "barebuild-route-change" for other sources.
(def default-source-event "barebuild-action-state")

(def property-api
  {:event     {:type 'string :reflects-attribute attr-event      :default ""}
   :whenPhase {:type 'string :reflects-attribute attr-when-phase :default ""}
   :whenName  {:type 'string :reflects-attribute attr-when-name  :default ""}
   :src       {:type 'string :reflects-attribute attr-src        :default ""}})

(def event-schema
  {event-invalidate {:detail {:src 'string}}})

(def method-api
  {:invalidate {:args [] :returns 'void}})
