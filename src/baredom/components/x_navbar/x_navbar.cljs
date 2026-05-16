(ns baredom.components.x-navbar.x-navbar
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-navbar.model :as model]))

;; ── Instance-field keys (du/setv!, du/getv) ─────────────────────────────────
(def ^:private k-refs          "__xNavbarRefs")
(def ^:private k-model         "__xNavbarModel")
(def ^:private k-focus-visible "__xNavbarFocusVisible")

;; ── Refs JS-object keys ──────────────────────────────────────────────────────
(def ^:private rk-base         "base")
(def ^:private rk-bar          "bar")
(def ^:private rk-brand        "brand")
(def ^:private rk-start        "start")
(def ^:private rk-nav          "nav")
(def ^:private rk-actions      "actions")
(def ^:private rk-toggle       "toggle")
(def ^:private rk-end          "end")
(def ^:private rk-brand-slot   "brand-slot")
(def ^:private rk-start-slot   "start-slot")
(def ^:private rk-default-slot "default-slot")
(def ^:private rk-actions-slot "actions-slot")
(def ^:private rk-toggle-slot  "toggle-slot")
(def ^:private rk-end-slot     "end-slot")

;; ── Event-name constants ─────────────────────────────────────────────────────
(def ^:private ev-click       "click")
(def ^:private ev-focusin     "focusin")
(def ^:private ev-focusout    "focusout")
(def ^:private ev-slotchange  "slotchange")

(def ^:private attr-data-focus-visible-within "data-focus-visible-within")

;; ── DOM helpers ──────────────────────────────────────────────────────────────

(defn- read-public-state
  [^js el]
  (model/public-state
   {:label       (du/get-attr el model/attr-label)
    :orientation (du/get-attr el model/attr-orientation)
    :variant     (du/get-attr el model/attr-variant)
    :sticky      (du/has-attr? el model/attr-sticky)
    :elevated    (du/has-attr? el model/attr-elevated)
    :breakpoint  (du/get-attr el model/attr-breakpoint)
    :alignment   (du/get-attr el model/attr-alignment)}))

(defn- assigned-nodes
  [^js slot-el]
  (.assignedNodes slot-el #js {:flatten true}))

(defn- slot-has-content?
  [^js slot-el]
  (> (alength (assigned-nodes slot-el)) 0))

(defn- source-from-event
  [^js event]
  (cond
    (instance? js/PointerEvent event) "pointer"
    (instance? js/MouseEvent event)   "pointer"
    (instance? js/KeyboardEvent event) "keyboard"
    :else "programmatic"))

(defn- closest-anchor
  [^js node]
  (when node
    (.closest node "a")))

(defn- node-in-assigned-nodes?
  [^js target nodes]
  (loop [idx 0]
    (if (< idx (alength nodes))
      (let [node (aget nodes idx)]
        (if (or (= node target)
                (and (= (.-nodeType ^js node) js/Node.ELEMENT_NODE)
                     (.contains ^js node target)))
          true
          (recur (inc idx))))
      false)))

(defn- target-in-brand-slot?
  [^js refs ^js target]
  (node-in-assigned-nodes? target (assigned-nodes (gobj/get refs rk-brand-slot))))

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-navbar-height:4rem;"
   "--x-navbar-padding-inline:1rem;"
   "--x-navbar-gap:0.75rem;"
   "--x-navbar-bg:var(--x-color-surface,rgba(255,255,255,0.88));"
   "--x-navbar-color:var(--x-color-text,#0f172a);"
   "--x-navbar-border:var(--x-color-border,rgba(148,163,184,0.22));"
   "--x-navbar-shadow:var(--x-shadow-md,0 8px 24px rgba(15,23,42,0.08));"
   "--x-navbar-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-navbar-z-index:40;"
   "--x-navbar-radius:0;"
   "--x-navbar-transition-duration:var(--x-transition-duration,180ms);"
   "--x-navbar-transition-easing:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "--x-navbar-align-items:center;"
   "}"
   "@media (prefers-color-scheme: dark){"
   ":host{"
   "--x-navbar-bg:var(--x-color-surface,rgba(15,23,42,0.84));"
   "--x-navbar-color:var(--x-color-text,#e5e7eb);"
   "--x-navbar-border:var(--x-color-border,rgba(51,65,85,0.9));"
   "--x-navbar-shadow:var(--x-shadow-md,0 14px 36px rgba(0,0,0,0.35));"
   "--x-navbar-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   ":host([hidden]){display:none;}"
   "[part='base']{"
   "display:block;"
   "color:var(--x-navbar-color);"
   "background:var(--x-navbar-bg);"
   "border:0;"
   "border-bottom:1px solid var(--x-navbar-border);"
   "border-radius:var(--x-navbar-radius);"
   "box-shadow:none;"
   "backdrop-filter:blur(14px);"
   "-webkit-backdrop-filter:blur(14px);"
   "transition:"
   "background var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"
   "border-color var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"
   "box-shadow var(--x-navbar-transition-duration) var(--x-navbar-transition-easing);"
   "}"
   ":host([sticky]) [part='base']{"
   "position:sticky;"
   "top:0;"
   "z-index:var(--x-navbar-z-index);"
   "}"
   ":host([elevated]) [part='base']{"
   "box-shadow:var(--x-navbar-shadow);"
   "}"
   "[part='base'][data-variant='subtle']{"
   "background:transparent;"
   "border-bottom-color:transparent;"
   "backdrop-filter:none;"
   "-webkit-backdrop-filter:none;"
   "box-shadow:none;"
   "}"
   "[part='base'][data-variant='inverted']{"
   "background:#0f172a;"
   "color:#f8fafc;"
   "border-bottom-color:#1e293b;"
   "}"
   "@media (prefers-color-scheme: dark){"
   "[part='base'][data-variant='inverted']{"
   "background:#020617;"
   "color:#f8fafc;"
   "border-bottom-color:#172033;"
   "}"
   "}"
   "[part='base'][data-variant='transparent']{"
   "background:transparent;"
   "border-bottom-color:transparent;"
   "box-shadow:none;"
   "backdrop-filter:none;"
   "-webkit-backdrop-filter:none;"
   "}"
   "[part='bar']{"
   "display:grid;"
   "grid-template-columns:auto auto 1fr auto auto auto;"
   "align-items:var(--x-navbar-align-items, center);"
   "gap:var(--x-navbar-gap);"
   "min-block-size:var(--x-navbar-height);"
   "padding-inline:var(--x-navbar-padding-inline);"
   "}"
   "[part='bar'][data-alignment='start']{justify-items:start;}"
   "[part='bar'][data-alignment='center']{justify-items:center;}"
   "[part='bar'][data-alignment='space-between']{justify-items:stretch;}"
   "[part='base'][data-orientation='vertical'] [part='bar']{"
   "grid-template-columns:1fr;"
   "grid-auto-rows:auto;"
   "align-items:var(--x-navbar-align-items, center);"
   "padding-block:0.75rem;"
   "}"
   "[part='brand'],[part='start'],[part='nav'],[part='actions'],[part='toggle'],[part='end']{"
   "display:flex;"
   "align-items:inherit;"
   "min-inline-size:0;"
   "}"
   "[part='nav']{gap:var(--x-navbar-gap);}"
   "[part='actions'],[part='end'],[part='toggle']{justify-self:end;}"
   "[part='base'][data-orientation='vertical'] [part='actions'],"
   "[part='base'][data-orientation='vertical'] [part='end'],"
   "[part='base'][data-orientation='vertical'] [part='toggle']{justify-self:start;}"
   "[part='brand'][data-has-brand='false'],"
   "[part='start'][data-has-start='false'],"
   "[part='actions'][data-has-actions='false'],"
   "[part='toggle'][data-has-toggle='false'],"
   "[part='end'][data-has-end='false']{display:none;}"
   "slot[name='brand'],slot[name='start'],slot[name='actions'],slot[name='toggle'],slot[name='end']{display:contents;}"
   "slot{display:contents;}"
   ":host(:focus-within) [part='base']{box-shadow:var(--x-navbar-shadow);}"
   ":host([data-focus-visible-within='true']) [part='base']{"
   "box-shadow:0 0 0 2px var(--x-navbar-focus-ring), var(--x-navbar-shadow);"
   "}"
   "@media (max-width:640px){"
   ":host(:not([orientation=vertical])) [part='bar']{"
   "display:flex;"
   "align-items:center;"
   "gap:var(--x-navbar-gap);"
   "min-block-size:var(--x-navbar-height);"
   "padding-inline:var(--x-navbar-padding-inline);"
   "}"
   ":host(:not([orientation=vertical])) [part='brand']{flex:1;}"
   ":host(:not([orientation=vertical])) [part='start'],"
   ":host(:not([orientation=vertical])) [part='nav'],"
   ":host(:not([orientation=vertical])) [part='actions'],"
   ":host(:not([orientation=vertical])) [part='end']{display:none;}"
   "}"
   "@media (prefers-reduced-motion: reduce){"
   "[part='base']{transition:none;}"
   "}"))

;; ── Shadow DOM construction ──────────────────────────────────────────────────
;; Each per-section builder creates + decorates + assembles + returns its
;; element(s). `make-bar-layout!` composes the six segment sections into the
;; grid bar; `assemble-shadow!` wraps the bar in <nav>, attaches it to the
;; host shadow, and returns the refs JS object.

(defn- make-style-section! []
  (let [el (.createElement js/document "style")]
    (set! (.-textContent el) style-text)
    el))

(defn- make-slot-section!
  "Generic builder for a slotted wrapper. Returns
  `{:wrapper <div> :slot <slot>}`. `slot-name` may be nil for the default
  (unnamed) slot — used by the central `nav` section."
  [part-name slot-name]
  (let [wrapper (.createElement js/document "div")
        slot    (.createElement js/document "slot")]
    (du/set-attr! wrapper "part" part-name)
    (when slot-name
      (du/set-attr! slot "name" slot-name))
    (.appendChild wrapper slot)
    {:wrapper wrapper :slot slot}))

(defn- make-bar-layout!
  "Build the grid bar containing the six segment sections in left-to-right
  order. Returns the bar element together with every segment's wrapper and
  slot, so the caller can build the refs object."
  []
  (let [bar-el (.createElement js/document "div")
        brand   (make-slot-section! "brand"   model/slot-brand)
        start   (make-slot-section! "start"   model/slot-start)
        nav     (make-slot-section! "nav"     nil)
        actions (make-slot-section! "actions" model/slot-actions)
        toggle  (make-slot-section! "toggle"  model/slot-toggle)
        end     (make-slot-section! "end"     model/slot-end)]
    (du/set-attr! bar-el "part" "bar")
    (.appendChild bar-el (:wrapper brand))
    (.appendChild bar-el (:wrapper start))
    (.appendChild bar-el (:wrapper nav))
    (.appendChild bar-el (:wrapper actions))
    (.appendChild bar-el (:wrapper toggle))
    (.appendChild bar-el (:wrapper end))
    {:bar     bar-el
     :brand   brand   :start   start   :nav     nav
     :actions actions :toggle  toggle  :end     end}))

(defn- assemble-shadow! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (make-style-section!)
        nav-el   (.createElement js/document "nav")
        {:keys [bar brand start nav actions toggle end]} (make-bar-layout!)
        refs     #js {}]
    (du/set-attr! nav-el "part" "base")
    (.appendChild nav-el bar)
    (.appendChild root style-el)
    (.appendChild root nav-el)
    (gobj/set refs rk-base         nav-el)
    (gobj/set refs rk-bar          bar)
    (gobj/set refs rk-brand        (:wrapper brand))
    (gobj/set refs rk-start        (:wrapper start))
    (gobj/set refs rk-nav          (:wrapper nav))
    (gobj/set refs rk-actions      (:wrapper actions))
    (gobj/set refs rk-toggle       (:wrapper toggle))
    (gobj/set refs rk-end          (:wrapper end))
    (gobj/set refs rk-brand-slot   (:slot brand))
    (gobj/set refs rk-start-slot   (:slot start))
    (gobj/set refs rk-default-slot (:slot nav))
    (gobj/set refs rk-actions-slot (:slot actions))
    (gobj/set refs rk-toggle-slot  (:slot toggle))
    (gobj/set refs rk-end-slot     (:slot end))
    refs))

;; ── Ensure refs (lazy init) ──────────────────────────────────────────────────

(defn- ensure-refs!
  [^js el]
  (or (du/getv el k-refs)
      (let [refs (assemble-shadow! el)]
        (du/setv! el k-refs refs)
        refs)))

;; ── DOM patching (render-orchestrator: cache-at-tail render-pipeline) ───────

(defn- apply-attr-state!
  "Apply DOM state derived from the host attributes (aria-label,
  data-variant, data-orientation, data-breakpoint, data-alignment)."
  [^js refs m]
  (let [^js base-el (gobj/get refs rk-base)
        ^js bar-el  (gobj/get refs rk-bar)
        label       (model/landmark-label m)]
    (if label
      (du/set-attr! base-el "aria-label" label)
      (du/remove-attr! base-el "aria-label"))
    (du/set-attr! base-el "data-variant"     (:variant m))
    (du/set-attr! base-el "data-orientation" (:orientation m))
    (du/set-attr! base-el "data-breakpoint"  (:breakpoint m))
    (du/set-attr! bar-el  "data-alignment"   (:alignment m))))

(defn- apply-slot-state!
  "Apply DOM state derived from slot content (data-has-* flags). Called
  from apply-model! and from the slotchange handler — slot state isn't in
  the cached model, so the diff guard cannot gate this path."
  [^js refs]
  (let [^js brand-el   (gobj/get refs rk-brand)
        ^js start-el   (gobj/get refs rk-start)
        ^js actions-el (gobj/get refs rk-actions)
        ^js toggle-el  (gobj/get refs rk-toggle)
        ^js end-el     (gobj/get refs rk-end)]
    (du/set-attr! brand-el   "data-has-brand"   (if (slot-has-content? (gobj/get refs rk-brand-slot))   "true" "false"))
    (du/set-attr! start-el   "data-has-start"   (if (slot-has-content? (gobj/get refs rk-start-slot))   "true" "false"))
    (du/set-attr! actions-el "data-has-actions" (if (slot-has-content? (gobj/get refs rk-actions-slot)) "true" "false"))
    (du/set-attr! toggle-el  "data-has-toggle"  (if (slot-has-content? (gobj/get refs rk-toggle-slot))  "true" "false"))
    (du/set-attr! end-el     "data-has-end"     (if (slot-has-content? (gobj/get refs rk-end-slot))     "true" "false"))))

(defn- apply-model! [^js el ^js refs m]
  (apply-attr-state! refs m)
  (apply-slot-state! refs)
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [new-m (read-public-state el)
          old-m (du/getv el k-model)]
      (when (not= new-m old-m)
        (apply-model! el refs new-m)))))

;; ── Event handlers (named — listener-spec style) ────────────────────────────
;; Each handler takes `[^js el ^js event]` and is referenced from
;; `listener-spec` below. Slot listeners and base-delegated listeners both
;; bind to shadow-DOM nodes whose lifetime matches the host element, so no
;; explicit remove path is needed across disconnect/reconnect.

(defn- on-base-click [^js el ^js event]
  (let [refs   (du/getv el k-refs)
        target (.-target event)
        anchor (closest-anchor target)
        source (source-from-event event)]
    (when anchor
      (du/dispatch! el
                    model/event-navigate
                    #js {:href   (or (.getAttribute ^js anchor "href") "")
                         :source source}))
    (when (target-in-brand-slot? refs target)
      (du/dispatch! el
                    model/event-brand-activate
                    #js {:source source}))))

(defn- on-base-focusin [^js el ^js event]
  (let [target (.-target event)]
    (when (and target (.matches ^js target ":focus-visible"))
      (when-not (du/getv el k-focus-visible)
        (du/setv! el k-focus-visible true)
        (du/set-attr! el attr-data-focus-visible-within "true")
        (du/dispatch! el model/event-focus-visible #js {})))))

(defn- on-base-focusout [^js el ^js event]
  (let [related (.-relatedTarget event)]
    (when-not (and related (.contains el ^js related))
      (du/setv! el k-focus-visible false)
      (du/remove-attr! el attr-data-focus-visible-within))))

(defn- on-slotchange [^js el _event]
  (when-let [refs (du/getv el k-refs)]
    (apply-slot-state! refs)))

;; ── Listener installation (listener-spec named pattern) ─────────────────────
;; Each entry: `[refs-key event-name handler-fn]`. install-listeners! iterates
;; this once at first-connect; the spec covers both the three base-delegated
;; listeners and the six slotchange listeners under one uniform path.
(def ^:private listener-spec
  [[rk-base         ev-click      on-base-click]
   [rk-base         ev-focusin    on-base-focusin]
   [rk-base         ev-focusout   on-base-focusout]
   [rk-brand-slot   ev-slotchange on-slotchange]
   [rk-start-slot   ev-slotchange on-slotchange]
   [rk-default-slot ev-slotchange on-slotchange]
   [rk-actions-slot ev-slotchange on-slotchange]
   [rk-toggle-slot  ev-slotchange on-slotchange]
   [rk-end-slot     ev-slotchange on-slotchange]])

(defn- install-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)]
    (doseq [[refs-key event-name handler] listener-spec]
      (let [^js target (gobj/get refs refs-key)]
        (.addEventListener target event-name (fn [event] (handler el event)))))))

;; ── Lifecycle callbacks ──────────────────────────────────────────────────────

(defn- connected!
  [^js el]
  (let [first? (nil? (du/getv el k-refs))]
    (ensure-refs! el)
    (when first?
      (du/setv! el k-focus-visible false)
      (install-listeners! el))
    ;; On initial mount k-model is nil so the diff guard fires apply-model!
    ;; once; subsequent reconnects with unchanged attrs skip the DOM work.
    (update-from-attrs! el)))

(defn- disconnected!
  [^js el]
  (du/setv! el k-focus-visible false))

(defn- attribute-changed!
  [^js el _name old-value new-value]
  (when (not= old-value new-value)
    (update-from-attrs! el)))

;; ── Property accessors ───────────────────────────────────────────────────────

(defn- install-property-accessors!
  [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
