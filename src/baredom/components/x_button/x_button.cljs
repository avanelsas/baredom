(ns baredom.components.x-button.x-button
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-button.model :as model]))

;; ── Instance-field keys (shared `du/setv!` / `du/getv`) ─────────────────────
(def ^:private k-refs                  "__xButtonRefs")
(def ^:private k-model                 "__xButtonModel")
;; The three fields below survived the values-not-places sweep: each encodes
;; transient interaction history with no DOM-attribute equivalent.
;; - k-focus-visible: gates the custom `focus-visible` event. There is no
;;   native `focusvisible` event, and `focus`/`blur` fire for mouse focus too.
;; - k-active-source / k-last-activation-src: track which input source
;;   ("pointer" | "keyboard") drove the current/last press, used for
;;   press-start/-end pairing and for the `press` event's `:source` detail.
(def ^:private k-focus-visible         "__xButtonFocusVisible")
(def ^:private k-active-source         "__xButtonActiveSource")
(def ^:private k-last-activation-src   "__xButtonLastActivationSource")

;; ── Refs-object keys (set on the JS refs map) ───────────────────────────────
(def ^:private rk-root            "root")
(def ^:private rk-button          "button")
(def ^:private rk-label-slot      "label-slot")
(def ^:private rk-icon-start-slot "icon-start-slot")
(def ^:private rk-icon-end-slot   "icon-end-slot")
(def ^:private rk-spinner-slot    "spinner-slot")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-form         "form")
(def ^:private attr-name         "name")
(def ^:private attr-part         "part")
(def ^:private attr-type         "type")
(def ^:private attr-aria-hidden  "aria-hidden")
(def ^:private attr-aria-label   "aria-label")
(def ^:private attr-aria-busy    "aria-busy")
(def ^:private attr-aria-pressed "aria-pressed")
(def ^:private attr-data-variant         "data-variant")
(def ^:private attr-data-size            "data-size")
(def ^:private attr-data-loading         "data-loading")
(def ^:private attr-data-disabled        "data-disabled")
(def ^:private attr-data-active          "data-active")
(def ^:private attr-data-has-icon-start  "data-has-icon-start")
(def ^:private attr-data-has-icon-end    "data-has-icon-end")
(def ^:private attr-data-has-spinner     "data-has-spinner")

(def ^:private part-button           "button")
(def ^:private part-inner            "inner")
(def ^:private part-icon-start       "icon-start")
(def ^:private part-icon-end         "icon-end")
(def ^:private part-label            "label")
(def ^:private part-spinner          "spinner")
(def ^:private part-spinner-slot     "spinner-slot")
(def ^:private part-spinner-fallback "spinner-fallback")

(def ^:private val-true        "true")
(def ^:private val-false       "false")
(def ^:private val-programmatic "programmatic")

(def ^:private src-pointer  "pointer")
(def ^:private src-keyboard "keyboard")

(def ^:private btn-type-submit "submit")
(def ^:private btn-type-reset  "reset")

(def ^:private key-enter "Enter")
(def ^:private key-space " ")

(def ^:private ev-pointerenter "pointerenter")
(def ^:private ev-pointerleave "pointerleave")
(def ^:private ev-pointerdown  "pointerdown")
(def ^:private ev-pointerup    "pointerup")
(def ^:private ev-pointercancel "pointercancel")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-keyup        "keyup")
(def ^:private ev-blur         "blur")
(def ^:private ev-focus        "focus")
(def ^:private ev-click        "click")
(def ^:private ev-slotchange   "slotchange")

(def ^:private selector-focus-visible ":focus-visible")
;; `attr-form` is the *attribute name* on the host (`<x-button form="my-form">`);
;; `selector-form` is the CSS *tag* selector used to locate the closest <form>
;; ancestor when no form-id attribute is supplied. They happen to share a
;; string but encode different intent.
(def ^:private selector-form "form")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-button-radius:var(--x-radius-md,0.75rem);"
   "--x-button-gap:0.5rem;"
   "--x-button-padding-inline:0.95rem;"
   "--x-button-height-sm:2rem;"
   "--x-button-height-md:2.5rem;"
   "--x-button-height-lg:3rem;"
   "--x-button-font-size-sm:0.875rem;"
   "--x-button-font-size-md:0.9375rem;"
   "--x-button-font-size-lg:1rem;"
   "--x-button-font-weight:600;"
   "--x-button-icon-size-sm:0.875rem;"
   "--x-button-icon-size-md:1rem;"
   "--x-button-icon-size-lg:1.125rem;"
   "--x-button-spinner-size:1em;"
   "--x-button-spinner-stroke:2px;"
   "--x-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08),0 1px 1px rgba(15,23,42,0.04));"
   "--x-button-shadow-hover:var(--x-shadow-md,0 4px 10px rgba(15,23,42,0.10),0 2px 4px rgba(15,23,42,0.06));"
   "--x-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08));"
   "--x-button-bg:var(--x-color-primary,#2563eb);"
   "--x-button-bg-hover:var(--x-color-primary-hover,#1d4ed8);"
   "--x-button-bg-active:var(--x-color-primary-active,#1e40af);"
   "--x-button-bg-disabled:#cbd5e1;"
   "--x-button-fg:#ffffff;"
   "--x-button-fg-disabled:#ffffff;"
   "--x-button-border:transparent;"
   "--x-button-border-hover:transparent;"
   "--x-button-border-active:transparent;"
   "--x-button-secondary-bg:var(--x-color-surface,#ffffff);"
   "--x-button-secondary-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-button-secondary-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-secondary-fg:var(--x-color-text,#0f172a);"
   "--x-button-secondary-border:var(--x-color-border,#cbd5e1);"
   "--x-button-tertiary-bg:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-tertiary-bg-hover:var(--x-color-surface-active,#e2e8f0);"
   "--x-button-tertiary-bg-active:var(--x-color-border,#cbd5e1);"
   "--x-button-tertiary-fg:var(--x-color-text,#0f172a);"
   "--x-button-ghost-bg:transparent;"
   "--x-button-ghost-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-button-ghost-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-ghost-fg:var(--x-color-text-muted,#334155);"
   "--x-button-danger-bg:var(--x-color-danger,#dc2626);"
   "--x-button-danger-bg-hover:#b91c1c;"
   "--x-button-danger-bg-active:#991b1b;"
   "--x-button-danger-fg:#ffffff;"
   "--x-button-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-button-transition-duration:var(--x-transition-duration,140ms);"
   "--x-button-transition-easing:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "}"
   "@media (prefers-color-scheme: dark){"
   ":host{"
   "--x-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.35),0 1px 1px rgba(0,0,0,0.2));"
   "--x-button-shadow-hover:var(--x-shadow-md,0 6px 14px rgba(0,0,0,0.35),0 2px 6px rgba(0,0,0,0.24));"
   "--x-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.3));"
   "--x-button-bg:var(--x-color-primary,#3b82f6);"
   "--x-button-bg-hover:var(--x-color-primary-hover,#2563eb);"
   "--x-button-bg-active:var(--x-color-primary-active,#1d4ed8);"
   "--x-button-bg-disabled:#334155;"
   "--x-button-fg:#eff6ff;"
   "--x-button-fg-disabled:#94a3b8;"
   "--x-button-secondary-bg:var(--x-color-surface,#111827);"
   "--x-button-secondary-bg-hover:var(--x-color-surface-hover,#1f2937);"
   "--x-button-secondary-bg-active:var(--x-color-surface-active,#273449);"
   "--x-button-secondary-fg:var(--x-color-text,#e5e7eb);"
   "--x-button-secondary-border:var(--x-color-border,#374151);"
   "--x-button-tertiary-bg:var(--x-color-surface-active,#1e293b);"
   "--x-button-tertiary-bg-hover:var(--x-color-surface-active,#273449);"
   "--x-button-tertiary-bg-active:var(--x-color-border,#334155);"
   "--x-button-tertiary-fg:var(--x-color-text,#e5e7eb);"
   "--x-button-ghost-bg:transparent;"
   "--x-button-ghost-bg-hover:var(--x-color-surface-hover,#0f172a);"
   "--x-button-ghost-bg-active:var(--x-color-surface-active,#172033);"
   "--x-button-ghost-fg:var(--x-color-text-muted,#cbd5e1);"
   "--x-button-danger-bg:var(--x-color-danger,#ef4444);"
   "--x-button-danger-bg-hover:#dc2626;"
   "--x-button-danger-bg-active:#b91c1c;"
   "--x-button-danger-fg:#ffffff;"
   "--x-button-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "@keyframes x-button-spin{"
   "from{transform:rotate(0deg);}"
   "to{transform:rotate(360deg);}"
   "}"
   "button{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "inline-size:100%;"
   "min-inline-size:0;"
   "min-block-size:var(--x-button-height-md);"
   "padding-inline:var(--x-button-padding-inline);"
   "border-radius:var(--x-button-radius);"
   "border:1px solid var(--x-button-border);"
   "background:var(--x-button-bg);"
   "color:var(--x-button-fg);"
   "font-size:var(--x-button-font-size-md);"
   "font-weight:var(--x-button-font-weight);"
   "line-height:1;"
   "cursor:pointer;"
   "user-select:none;"
   "touch-action:manipulation;"
   "-webkit-tap-highlight-color:transparent;"
   "box-shadow:var(--x-button-shadow);"
   "transition:"
   "background var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "border-color var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "color var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "transform var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "box-shadow var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "opacity var(--x-button-transition-duration) var(--x-button-transition-easing);"
   "}"
   "button[disabled]{cursor:default;box-shadow:none;}"
   "button:focus-visible{outline:none;box-shadow:0 0 0 3px var(--x-button-focus-ring),var(--x-button-shadow);}"
   "button[data-size='sm']{min-block-size:var(--x-button-height-sm);font-size:var(--x-button-font-size-sm);}"
   "button[data-size='md']{min-block-size:var(--x-button-height-md);font-size:var(--x-button-font-size-md);}"
   "button[data-size='lg']{min-block-size:var(--x-button-height-lg);font-size:var(--x-button-font-size-lg);}"

   "button[data-variant='primary']{"
   "background:var(--x-button-bg);"
   "color:var(--x-button-fg);"
   "border-color:var(--x-button-border);"
   "}"
   "button[data-variant='primary']:hover:not([disabled]){"
   "background:var(--x-button-bg-hover);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='primary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-variant='secondary']{"
   "background:var(--x-button-secondary-bg);"
   "color:var(--x-button-secondary-fg);"
   "border-color:var(--x-button-secondary-border);"
   "}"
   "button[data-variant='secondary']:hover:not([disabled]){"
   "background:var(--x-button-secondary-bg-hover);"
   "border-color:var(--x-button-secondary-border);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='secondary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-secondary-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-variant='tertiary']{"
   "background:var(--x-button-tertiary-bg);"
   "color:var(--x-button-tertiary-fg);"
   "border-color:transparent;"
   "box-shadow:none;"
   "}"
   "button[data-variant='tertiary']:hover:not([disabled]){"
   "background:var(--x-button-tertiary-bg-hover);"
   "}"
   "button[data-variant='tertiary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-tertiary-bg-active);"
   "transform:translateY(1px);"
   "}"

   "button[data-variant='ghost']{"
   "background:var(--x-button-ghost-bg);"
   "color:var(--x-button-ghost-fg);"
   "border-color:transparent;"
   "box-shadow:none;"
   "}"
   "button[data-variant='ghost']:hover:not([disabled]){"
   "background:var(--x-button-ghost-bg-hover);"
   "}"
   "button[data-variant='ghost'][data-active='true']:not([disabled]){"
   "background:var(--x-button-ghost-bg-active);"
   "transform:translateY(1px);"
   "}"

   "button[data-variant='danger']{"
   "background:var(--x-button-danger-bg);"
   "color:var(--x-button-danger-fg);"
   "border-color:transparent;"
   "}"
   "button[data-variant='danger']:hover:not([disabled]){"
   "background:var(--x-button-danger-bg-hover);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='danger'][data-active='true']:not([disabled]){"
   "background:var(--x-button-danger-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-loading='true']{cursor:progress;}"
   "button[data-disabled='true']{"
   "background:var(--x-button-bg-disabled);"
   "color:var(--x-button-fg-disabled);"
   "border-color:transparent;"
   "opacity:0.72;"
   "}"
   "[part='inner']{display:inline-flex;align-items:center;justify-content:center;gap:var(--x-button-gap);min-inline-size:0;}"
   "[part='label']{display:inline-flex;align-items:center;justify-content:center;min-inline-size:0;white-space:nowrap;}"
   "[part='icon-start'],[part='icon-end'],[part='spinner']{display:inline-flex;align-items:center;justify-content:center;flex:none;}"
   "button[data-size='sm'] [part='icon-start'],button[data-size='sm'] [part='icon-end']{inline-size:var(--x-button-icon-size-sm);block-size:var(--x-button-icon-size-sm);}"
   "button[data-size='md'] [part='icon-start'],button[data-size='md'] [part='icon-end']{inline-size:var(--x-button-icon-size-md);block-size:var(--x-button-icon-size-md);}"
   "button[data-size='lg'] [part='icon-start'],button[data-size='lg'] [part='icon-end']{inline-size:var(--x-button-icon-size-lg);block-size:var(--x-button-icon-size-lg);}"
   "[part='spinner']{inline-size:var(--x-button-spinner-size);block-size:var(--x-button-spinner-size);position:relative;}"
   "[part='spinner-slot']{display:inline-flex;align-items:center;justify-content:center;}"
   "[part='spinner-fallback']{"
   "display:none;"
   "inline-size:100%;"
   "block-size:100%;"
   "box-sizing:border-box;"
   "border-radius:999px;"
   "border:var(--x-button-spinner-stroke) solid currentColor;"
   "border-inline-end-color:transparent;"
   "animation:x-button-spin 0.7s linear infinite;"
   "opacity:0.9;"
   "}"
   "button[data-has-icon-start='false'] [part='icon-start']{display:none;}"
   "button[data-has-icon-end='false'] [part='icon-end']{display:none;}"
   "button[data-loading='false'] [part='spinner']{display:none;}"
   "button[data-loading='true'][data-has-spinner='false'] [part='spinner-fallback']{display:inline-block;}"
   "button[data-loading='true'][data-has-spinner='true'] [part='spinner-fallback']{display:none;}"
   "@media (prefers-reduced-motion: reduce){"
   "button{transition:none;}"
   "[part='spinner-fallback']{animation:none;}"
   "}"))

;; ── Shadow DOM builders ─────────────────────────────────────────────────────
;; Each per-section builder creates + decorates + assembles + returns its
;; element(s). `build-refs!` composes them, attaches to the host shadow, and
;; populates the refs JS object the caller stashes via `du/setv!`.

(defn- make-style-section! []
  (let [el (.createElement js/document "style")]
    (set! (.-textContent el) style-text)
    el))

(defn- make-icon-start-segment! []
  (let [icon-el (.createElement js/document "span")
        slot-el (.createElement js/document "slot")]
    (du/set-attr! icon-el attr-part part-icon-start)
    (du/set-attr! slot-el attr-name model/slot-icon-start)
    (.appendChild icon-el slot-el)
    {:wrapper icon-el :slot slot-el}))

(defn- make-label-segment! []
  (let [label-el (.createElement js/document "span")
        slot-el  (.createElement js/document "slot")]
    (du/set-attr! label-el attr-part part-label)
    (.appendChild label-el slot-el)
    {:wrapper label-el :slot slot-el}))

(defn- make-spinner-segment! []
  (let [spinner-el  (.createElement js/document "span")
        slot-el     (.createElement js/document "slot")
        fallback-el (.createElement js/document "span")]
    (du/set-attr! spinner-el  attr-part        part-spinner)
    (du/set-attr! slot-el     attr-part        part-spinner-slot)
    (du/set-attr! slot-el     attr-name        model/slot-spinner)
    (du/set-attr! fallback-el attr-part        part-spinner-fallback)
    (du/set-attr! spinner-el  attr-aria-hidden val-true)
    (du/set-attr! fallback-el attr-aria-hidden val-true)
    (.appendChild spinner-el slot-el)
    (.appendChild spinner-el fallback-el)
    {:wrapper spinner-el :slot slot-el}))

(defn- make-icon-end-segment! []
  (let [icon-el (.createElement js/document "span")
        slot-el (.createElement js/document "slot")]
    (du/set-attr! icon-el attr-part part-icon-end)
    (du/set-attr! slot-el attr-name model/slot-icon-end)
    (.appendChild icon-el slot-el)
    {:wrapper icon-el :slot slot-el}))

(defn- make-inner-section!
  "Build the inner wrapper containing the four segments and return both the
  wrapper element and the four slots (so callers can stash slot refs)."
  []
  (let [inner-el   (.createElement js/document "span")
        icon-start (make-icon-start-segment!)
        label      (make-label-segment!)
        spinner    (make-spinner-segment!)
        icon-end   (make-icon-end-segment!)]
    (du/set-attr! inner-el attr-part part-inner)
    (.appendChild inner-el (:wrapper icon-start))
    (.appendChild inner-el (:wrapper label))
    (.appendChild inner-el (:wrapper spinner))
    (.appendChild inner-el (:wrapper icon-end))
    {:inner-el        inner-el
     :icon-start-slot (:slot icon-start)
     :label-slot      (:slot label)
     :spinner-slot    (:slot spinner)
     :icon-end-slot   (:slot icon-end)}))

(defn- make-button-section!
  "Build the host <button> wrapping the inner section. Returns the button
  element and the four slots the inner section produced."
  []
  (let [button-el (.createElement js/document "button")
        inner     (make-inner-section!)]
    (du/set-attr! button-el attr-part part-button)
    (.appendChild button-el (:inner-el inner))
    (assoc inner :button-el button-el)))

(defn- build-refs!
  "Build the shadow DOM and return the refs JS object. Stores nothing on
  the host — caller does that via `du/setv!`."
  [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style-el  (make-style-section!)
        {:keys [button-el icon-start-slot label-slot
                spinner-slot icon-end-slot]} (make-button-section!)
        refs      #js {}]
    (.appendChild root style-el)
    (.appendChild root button-el)
    (gobj/set refs rk-root            root)
    (gobj/set refs rk-button          button-el)
    (gobj/set refs rk-label-slot      label-slot)
    (gobj/set refs rk-icon-start-slot icon-start-slot)
    (gobj/set refs rk-icon-end-slot   icon-end-slot)
    (gobj/set refs rk-spinner-slot    spinner-slot)
    refs))

;; ── Slot probing ────────────────────────────────────────────────────────────
(defn- assigned-nodes [^js slot-el]
  (.assignedNodes slot-el #js {:flatten true}))

(defn- slot-has-content? [^js slot-el]
  (> (alength (assigned-nodes slot-el)) 0))

(defn- meaningful-node?
  "A text or element node whose trimmed textContent is non-empty.
   Used to decide whether the label slot supplies its own accessible name."
  [^js node]
  (let [t (.-nodeType node)]
    (and (or (= t js/Node.TEXT_NODE)
             (= t js/Node.ELEMENT_NODE))
         (not= "" (.trim (or (.-textContent node) ""))))))

(defn- slot-has-meaningful-text? [^js slot-el]
  (boolean (some meaningful-node? (array-seq (assigned-nodes slot-el)))))

;; ── Model reading ───────────────────────────────────────────────────────────
(defn- read-public-state [^js el]
  (model/public-state
   {:disabled (du/has-attr? el model/attr-disabled)
    :loading  (du/has-attr? el model/attr-loading)
    :pressed  (du/has-attr? el model/attr-pressed)
    :type     (du/get-attr el model/attr-type)
    :variant  (du/get-attr el model/attr-variant)
    :size     (du/get-attr el model/attr-size)
    :label    (du/get-attr el model/attr-label)}))

(defn- read-model
  "Compose the cached model from attributes + active-press state + slot-
  content flags. Hover is no longer cached — the CSS `:hover:not([disabled])`
  selectors paint hover styling directly. Focus-visible state is tracked in
  `k-focus-visible` for event dispatch but is not part of the cached model
  because nothing in `apply-model!` consumes it."
  [^js el]
  (let [^js refs            (du/getv el k-refs)
        ^js label-slot      (gobj/get refs rk-label-slot)
        ^js icon-start-slot (gobj/get refs rk-icon-start-slot)
        ^js icon-end-slot   (gobj/get refs rk-icon-end-slot)
        ^js spinner-slot    (gobj/get refs rk-spinner-slot)
        public              (read-public-state el)
        interactive?        (model/interactive? public)
        active-source       (when interactive? (du/getv el k-active-source))
        has-default-text?   (slot-has-meaningful-text? label-slot)]
    {:public           public
     :active?          (some? active-source)
     :has-icon-start?  (slot-has-content? icon-start-slot)
     :has-icon-end?    (slot-has-content? icon-end-slot)
     :has-spinner?     (slot-has-content? spinner-slot)
     :aria-label-value (model/aria-label
                        (assoc public :has-default-text? has-default-text?))}))

(defn- interactive-el? [^js el]
  (model/interactive? (read-public-state el)))

(defn- find-owner-form [^js el]
  (or (when-let [form-id (du/get-attr el attr-form)]
        (.getElementById js/document form-id))
      (.closest el selector-form)))

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- bool-attr
  "Render a boolean as the literal string \"true\"/\"false\" for a
  data-* attribute (CSS selector convention used by the host styles)."
  [v]
  (if v val-true val-false))

(defn- toggle-attr!
  "Set or remove an attribute depending on whether `v` is truthy. Routes
   through du/ so shadow-internal mutations are visible to the trace recorder."
  [^js node attr v]
  (if v
    (du/set-attr!    node attr (if (string? v) v (str v)))
    (du/remove-attr! node attr)))

(defn- apply-button-aria! [^js button-el public-state aria-label-value]
  (toggle-attr! button-el attr-aria-busy    (model/aria-busy public-state))
  (toggle-attr! button-el attr-aria-pressed (when (:pressed public-state) val-true))
  (toggle-attr! button-el attr-aria-label   aria-label-value))

(defn- apply-button-data-state! [^js button-el public-state m]
  (du/set-attr! button-el attr-data-variant        (:variant public-state))
  (du/set-attr! button-el attr-data-size           (:size    public-state))
  (du/set-attr! button-el attr-data-loading        (bool-attr (:loading  public-state)))
  (du/set-attr! button-el attr-data-disabled       (bool-attr (:disabled public-state)))
  (du/set-attr! button-el attr-data-active         (bool-attr (:active?         m)))
  (du/set-attr! button-el attr-data-has-icon-start (bool-attr (:has-icon-start? m)))
  (du/set-attr! button-el attr-data-has-icon-end   (bool-attr (:has-icon-end?   m)))
  (du/set-attr! button-el attr-data-has-spinner    (bool-attr (:has-spinner?    m))))

(defn- apply-host-data! [^js el public-state]
  (du/set-attr! el attr-data-variant (:variant public-state))
  (du/set-attr! el attr-data-size    (:size    public-state)))

(defn- apply-model! [^js el m]
  (let [^js refs   (du/getv el k-refs)
        ^js button (gobj/get refs rk-button)
        public     (:public m)]
    (du/set-attr! button attr-type (:type public))
    (set! (.-disabled button) (or (:disabled public) (:loading public)))
    (apply-button-aria!       button public (:aria-label-value m))
    (apply-button-data-state! button public m)
    (apply-host-data!         el     public)
    (du/setv! el k-model m)))

(defn- update!
  "Read → guard → apply. Safe to call after refs exist; no-op before
  the shadow DOM is set up."
  [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Press lifecycle ─────────────────────────────────────────────────────────
(defn- end-active-press! [^js el]
  (when-let [source (du/getv el k-active-source)]
    (du/setv! el k-active-source nil)
    (du/dispatch! el model/event-press-end #js {:source source})
    (update! el)))

(defn- activation-key?
  "Enter and Space activate buttons per ARIA convention."
  [^js event]
  (let [k (.-key event)]
    (or (= k key-enter) (= k key-space))))

(defn- can-start-press?
  "A press from `source` may begin only when:
  - pointer: nothing else is currently active (keyboard always wins),
  - keyboard: a keyboard press isn't already in flight (filters key
    auto-repeat, but a keyboard press DOES override an in-progress
    pointer press to match the existing behaviour)."
  [^js el source]
  (condp = source
    src-pointer  (not (du/getv el k-active-source))
    src-keyboard (not= src-keyboard (du/getv el k-active-source))))

(defn- start-press!
  "Begin a press from `source` (\"pointer\" | \"keyboard\") if the
  button is interactive and the source-specific guard passes."
  [^js el source]
  (when (and (interactive-el? el) (can-start-press? el source))
    (du/setv! el k-last-activation-src source)
    (du/setv! el k-active-source       source)
    (update! el)
    (du/dispatch! el model/event-press-start #js {:source source})))

(defn- end-press-if-source! [^js el source]
  (when (= source (du/getv el k-active-source))
    (end-active-press! el)))

(defn- maybe-submit-or-reset!
  "Dispatch the form's submit/reset behaviour for type=submit/reset
  buttons that live inside a form."
  [^js el btn-type]
  (when-let [form (find-owner-form el)]
    (cond
      (= btn-type btn-type-submit) (.requestSubmit form)
      (= btn-type btn-type-reset)  (.reset form))))

;; ── Event handlers (named — listener-spec style) ────────────────────────────
;; Handlers end in `!` per the transparency rule: each performs side effects
;; (`du/setv!`, `du/dispatch!`, `update!`).

(defn- on-pointer-enter! [^js el _e]
  (when (interactive-el? el)
    (du/dispatch! el model/event-hover-start #js {})))

(defn- emit-hover-end-if-interactive! [^js el]
  (when (interactive-el? el)
    (du/dispatch! el model/event-hover-end #js {})))

(defn- cancel-pointer-press-if-active! [^js el]
  (when (= src-pointer (du/getv el k-active-source))
    (end-active-press! el)))

(defn- on-pointer-leave! [^js el _e]
  (emit-hover-end-if-interactive!  el)
  (cancel-pointer-press-if-active! el))

(defn- on-pointer-down! [^js el _e]
  (start-press! el src-pointer))

(defn- on-pointer-up! [^js el _e]
  (end-press-if-source! el src-pointer))

(defn- on-pointer-cancel! [^js el _e]
  (end-press-if-source! el src-pointer))

(defn- on-keydown! [^js el ^js event]
  (when (activation-key? event)
    (start-press! el src-keyboard)))

(defn- on-keyup! [^js el ^js event]
  (when (activation-key? event)
    (end-press-if-source! el src-keyboard)))

(defn- on-focus! [^js el ^js _e]
  (let [^js refs   (du/getv el k-refs)
        ^js button (gobj/get refs rk-button)
        visible    (.matches button selector-focus-visible)]
    (du/setv! el k-focus-visible visible)
    (when visible
      (du/dispatch! el model/event-focus-visible #js {}))))

(defn- on-blur!
  "Combined blur handler: end any keyboard press and clear focus-visible."
  [^js el _e]
  (when (du/getv el k-active-source)
    (end-active-press! el))
  (du/setv! el k-focus-visible false))

(defn- on-click! [^js el _e]
  (when (interactive-el? el)
    (let [source (or (du/getv el k-last-activation-src) val-programmatic)]
      (du/dispatch! el model/event-press #js {:source source})
      (du/setv! el k-last-activation-src nil)
      (maybe-submit-or-reset! el (:type (read-public-state el))))))

(defn- on-slotchange! [^js el _e]
  (update! el))

;; ── Listener installation (listener-spec named pattern) ─────────────────────
;; Each entry: [refs-key event-name handler-fn].
;; add-listeners! iterates this once at connect-time. Listeners are bound to
;; shadow-DOM nodes that persist with the element, so no explicit remove path
;; is needed across disconnect/reconnect cycles.
(def ^:private listener-spec
  [[rk-button          ev-pointerenter  on-pointer-enter!]
   [rk-button          ev-pointerleave  on-pointer-leave!]
   [rk-button          ev-pointerdown   on-pointer-down!]
   [rk-button          ev-pointerup     on-pointer-up!]
   [rk-button          ev-pointercancel on-pointer-cancel!]
   [rk-button          ev-keydown       on-keydown!]
   [rk-button          ev-keyup         on-keyup!]
   [rk-button          ev-focus         on-focus!]
   [rk-button          ev-blur          on-blur!]
   [rk-button          ev-click         on-click!]
   [rk-label-slot      ev-slotchange    on-slotchange!]
   [rk-icon-start-slot ev-slotchange    on-slotchange!]
   [rk-icon-end-slot   ev-slotchange    on-slotchange!]
   [rk-spinner-slot    ev-slotchange    on-slotchange!]])

(defn- install-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)]
    (doseq [[refs-key event-name handler] listener-spec]
      (let [^js target (gobj/get refs refs-key)]
        (.addEventListener target event-name (fn [event] (handler el event)))))))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (du/setv! el k-refs (build-refs! el))
    (du/setv! el k-focus-visible       false)
    (du/setv! el k-active-source       nil)
    (du/setv! el k-last-activation-src nil)
    (install-listeners! el))
  (update! el))

(defn- disconnected! [^js el]
  ;; Reset transient interaction history so a reconnect starts clean.
  (du/setv! el k-focus-visible       false)
  (du/setv! el k-active-source       nil)
  (du/setv! el k-last-activation-src nil))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update! el)))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :form-associated?     true
                        :setup-prototype-fn   (fn install-props [proto]
                                                (du/install-properties! proto model/property-api))}))
