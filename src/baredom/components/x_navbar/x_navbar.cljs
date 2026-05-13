(ns baredom.components.x-navbar.x-navbar
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-navbar.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs          "__xNavbarRefs")
(def ^:private k-model         "__xNavbarModel")
(def ^:private k-focus-visible "__xNavbarFocusVisible")

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
  (node-in-assigned-nodes? target (assigned-nodes (gobj/get refs "brand-slot"))))

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

(defn- create-el
  [tag]
  (.createElement js/document tag))

(defn- create-shadow!
  [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (create-el "style")
        nav-el     (create-el "nav")
        bar-el     (create-el "div")
        brand-el   (create-el "div")
        start-el   (create-el "div")
        nav-wrap   (create-el "div")
        actions-el (create-el "div")
        toggle-el  (create-el "div")
        end-el     (create-el "div")
        brand-slot   (create-el "slot")
        start-slot   (create-el "slot")
        default-slot (create-el "slot")
        actions-slot (create-el "slot")
        toggle-slot  (create-el "slot")
        end-slot     (create-el "slot")]

    (set! (.-textContent style-el) style-text)

    (.setAttribute nav-el     "part" "base")
    (.setAttribute bar-el     "part" "bar")
    (.setAttribute brand-el   "part" "brand")
    (.setAttribute start-el   "part" "start")
    (.setAttribute nav-wrap   "part" "nav")
    (.setAttribute actions-el "part" "actions")
    (.setAttribute toggle-el  "part" "toggle")
    (.setAttribute end-el     "part" "end")

    (.setAttribute brand-slot   "name" model/slot-brand)
    (.setAttribute start-slot   "name" model/slot-start)
    (.setAttribute actions-slot "name" model/slot-actions)
    (.setAttribute toggle-slot  "name" model/slot-toggle)
    (.setAttribute end-slot     "name" model/slot-end)

    (.appendChild brand-el   brand-slot)
    (.appendChild start-el   start-slot)
    (.appendChild nav-wrap   default-slot)
    (.appendChild actions-el actions-slot)
    (.appendChild toggle-el  toggle-slot)
    (.appendChild end-el     end-slot)

    (.appendChild bar-el brand-el)
    (.appendChild bar-el start-el)
    (.appendChild bar-el nav-wrap)
    (.appendChild bar-el actions-el)
    (.appendChild bar-el toggle-el)
    (.appendChild bar-el end-el)

    (.appendChild nav-el bar-el)

    (.appendChild root style-el)
    (.appendChild root nav-el)

    #js {:root         root
         :base         nav-el
         :bar          bar-el
         :brand        brand-el
         :start        start-el
         :nav          nav-wrap
         :actions      actions-el
         :toggle       toggle-el
         :end          end-el
         :brand-slot   brand-slot
         :start-slot   start-slot
         :default-slot default-slot
         :actions-slot actions-slot
         :toggle-slot  toggle-slot
         :end-slot     end-slot}))

;; ── Ensure refs (lazy init) ──────────────────────────────────────────────────

(defn- ensure-refs!
  [^js el]
  (or (gobj/get el k-refs)
      (let [refs (create-shadow! el)]
        (gobj/set el k-refs refs)
        refs)))

;; ── DOM patching (render-orchestrator: cache-at-tail render-pipeline) ───────

(defn- apply-attr-state!
  "Apply DOM state derived from the host attributes (aria-label,
  data-variant, data-orientation, data-breakpoint, data-alignment)."
  [^js refs m]
  (let [^js base-el (gobj/get refs "base")
        ^js bar-el  (gobj/get refs "bar")
        label       (model/landmark-label m)]
    (if label
      (.setAttribute base-el "aria-label" label)
      (.removeAttribute base-el "aria-label"))
    (.setAttribute base-el "data-variant"     (:variant m))
    (.setAttribute base-el "data-orientation" (:orientation m))
    (.setAttribute base-el "data-breakpoint"  (:breakpoint m))
    (.setAttribute bar-el  "data-alignment"   (:alignment m))))

(defn- apply-slot-state!
  "Apply DOM state derived from slot content (data-has-* flags). Called
  from apply-model! and from the slotchange handler — slot state isn't in
  the cached model, so the diff guard cannot gate this path."
  [^js refs]
  (let [^js brand-el   (gobj/get refs "brand")
        ^js start-el   (gobj/get refs "start")
        ^js actions-el (gobj/get refs "actions")
        ^js toggle-el  (gobj/get refs "toggle")
        ^js end-el     (gobj/get refs "end")]
    (.setAttribute brand-el   "data-has-brand"   (if (slot-has-content? (gobj/get refs "brand-slot"))   "true" "false"))
    (.setAttribute start-el   "data-has-start"   (if (slot-has-content? (gobj/get refs "start-slot"))   "true" "false"))
    (.setAttribute actions-el "data-has-actions" (if (slot-has-content? (gobj/get refs "actions-slot")) "true" "false"))
    (.setAttribute toggle-el  "data-has-toggle"  (if (slot-has-content? (gobj/get refs "toggle-slot"))  "true" "false"))
    (.setAttribute end-el     "data-has-end"     (if (slot-has-content? (gobj/get refs "end-slot"))     "true" "false"))))

(defn- apply-model! [^js el ^js refs m]
  (apply-attr-state! refs m)
  (apply-slot-state! refs)
  (gobj/set el k-model m))

(defn- update-from-attrs! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [new-m (read-public-state el)
          old-m (gobj/get el k-model)]
      (when (not= new-m old-m)
        (apply-model! el refs new-m)))))

;; ── Event wiring ─────────────────────────────────────────────────────────────

(defn- setup-delegated-events!
  [^js el ^js base-el]
  (.addEventListener
   base-el
   "click"
   (fn [^js event]
     (let [refs   (gobj/get el k-refs)
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
                    #js {:source source})))))

  (.addEventListener
   base-el
   "focusin"
   (fn [^js event]
     (let [target (.-target event)]
       (when (and target (.matches ^js target ":focus-visible"))
         (when-not (gobj/get el k-focus-visible)
           (gobj/set el k-focus-visible true)
           (du/set-attr! el "data-focus-visible-within" "true")
           (du/dispatch! el model/event-focus-visible #js {}))))))

  (.addEventListener
   base-el
   "focusout"
   (fn [^js event]
     (let [related (.-relatedTarget event)]
       (when-not (and related (.contains el ^js related))
         (gobj/set el k-focus-visible false)
         (du/remove-attr! el "data-focus-visible-within"))))))

;; ── Slot change wiring ───────────────────────────────────────────────────────

(defn- setup-slots!
  [^js _el ^js refs]
  (let [on-slotchange (fn handle-slotchange [_] (apply-slot-state! refs))]
    (.addEventListener (gobj/get refs "brand-slot")   "slotchange" on-slotchange)
    (.addEventListener (gobj/get refs "start-slot")   "slotchange" on-slotchange)
    (.addEventListener (gobj/get refs "default-slot") "slotchange" on-slotchange)
    (.addEventListener (gobj/get refs "actions-slot") "slotchange" on-slotchange)
    (.addEventListener (gobj/get refs "toggle-slot")  "slotchange" on-slotchange)
    (.addEventListener (gobj/get refs "end-slot")     "slotchange" on-slotchange)))

;; ── Lifecycle callbacks ──────────────────────────────────────────────────────

(defn- connected!
  [^js el]
  (let [first? (nil? (gobj/get el k-refs))
        refs   (ensure-refs! el)]
    (when first?
      (gobj/set el k-focus-visible false)
      (setup-delegated-events! el (gobj/get refs "base"))
      (setup-slots! el refs))
    ;; On initial mount k-model is nil so the diff guard fires apply-model!
    ;; once; subsequent reconnects with unchanged attrs skip the DOM work.
    (update-from-attrs! el)))

(defn- disconnected!
  [^js el]
  (gobj/set el k-focus-visible false))

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
