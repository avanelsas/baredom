(ns baredom.components.x-navbar.x-navbar
  (:require
   [goog.object :as gobj]
   [baredom.components.x-navbar.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs          "__xNavbarRefs")
(def ^:private k-focus-visible "__xNavbarFocusVisible")

;; ── DOM helpers ──────────────────────────────────────────────────────────────

(defn- has-attr?
  [^js el attr-name]
  (.hasAttribute el attr-name))

(defn- get-attr
  [^js el attr-name]
  (.getAttribute el attr-name))

(defn- set-bool-attr!
  [^js el attr-name value]
  (if value
    (.setAttribute el attr-name "")
    (.removeAttribute el attr-name)))

(defn- read-public-state
  [^js el]
  (model/public-state
   {:label       (get-attr el model/attr-label)
    :orientation (get-attr el model/attr-orientation)
    :variant     (get-attr el model/attr-variant)
    :sticky      (has-attr? el model/attr-sticky)
    :elevated    (has-attr? el model/attr-elevated)
    :breakpoint  (get-attr el model/attr-breakpoint)
    :alignment   (get-attr el model/attr-alignment)}))

(defn- dispatch!
  [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail detail
         :bubbles true
         :composed true
         :cancelable false})))

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

;; ── Render ───────────────────────────────────────────────────────────────────

(defn- render!
  [^js el ^js refs]
  (let [base-el      (gobj/get refs "base")
        bar-el       (gobj/get refs "bar")
        brand-el     (gobj/get refs "brand")
        start-el     (gobj/get refs "start")
        actions-el   (gobj/get refs "actions")
        toggle-el    (gobj/get refs "toggle")
        end-el       (gobj/get refs "end")
        brand-slot   (gobj/get refs "brand-slot")
        start-slot   (gobj/get refs "start-slot")
        actions-slot (gobj/get refs "actions-slot")
        toggle-slot  (gobj/get refs "toggle-slot")
        end-slot     (gobj/get refs "end-slot")
        pub          (read-public-state el)
        has-brand?   (slot-has-content? brand-slot)
        has-start?   (slot-has-content? start-slot)
        has-actions? (slot-has-content? actions-slot)
        has-toggle?  (slot-has-content? toggle-slot)
        has-end?     (slot-has-content? end-slot)
        label        (model/landmark-label pub)]

    (if label
      (.setAttribute base-el "aria-label" label)
      (.removeAttribute base-el "aria-label"))

    (.setAttribute base-el "data-variant" (:variant pub))
    (.setAttribute base-el "data-orientation" (:orientation pub))
    (.setAttribute base-el "data-breakpoint" (:breakpoint pub))
    (.setAttribute bar-el "data-alignment" (:alignment pub))

    (.setAttribute brand-el   "data-has-brand"   (if has-brand?   "true" "false"))
    (.setAttribute start-el   "data-has-start"   (if has-start?   "true" "false"))
    (.setAttribute actions-el "data-has-actions"  (if has-actions? "true" "false"))
    (.setAttribute toggle-el  "data-has-toggle"   (if has-toggle?  "true" "false"))
    (.setAttribute end-el     "data-has-end"      (if has-end?     "true" "false"))))

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
         (dispatch! el
                    model/event-navigate
                    #js {:href   (or (.getAttribute ^js anchor "href") "")
                         :source source}))
       (when (target-in-brand-slot? refs target)
         (dispatch! el
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
           (.setAttribute el "data-focus-visible-within" "true")
           (dispatch! el model/event-focus-visible #js {}))))))

  (.addEventListener
   base-el
   "focusout"
   (fn [^js event]
     (let [related (.-relatedTarget event)]
       (when-not (and related (.contains el ^js related))
         (gobj/set el k-focus-visible false)
         (.removeAttribute el "data-focus-visible-within"))))))

;; ── Slot change wiring ───────────────────────────────────────────────────────

(defn- setup-slots!
  [^js el ^js refs]
  (let [rerender (fn [_] (render! el refs))]
    (.addEventListener (gobj/get refs "brand-slot")   "slotchange" rerender)
    (.addEventListener (gobj/get refs "start-slot")   "slotchange" rerender)
    (.addEventListener (gobj/get refs "default-slot") "slotchange" rerender)
    (.addEventListener (gobj/get refs "actions-slot") "slotchange" rerender)
    (.addEventListener (gobj/get refs "toggle-slot")  "slotchange" rerender)
    (.addEventListener (gobj/get refs "end-slot")     "slotchange" rerender)))

;; ── Lifecycle callbacks ──────────────────────────────────────────────────────

(defn- connected!
  [^js el]
  (let [first? (nil? (gobj/get el k-refs))
        refs   (ensure-refs! el)]
    (when first?
      (gobj/set el k-focus-visible false)
      (setup-delegated-events! el (gobj/get refs "base"))
      (setup-slots! el refs))
    (render! el refs)))

(defn- disconnected!
  [^js el]
  (gobj/set el k-focus-visible false))

(defn- attribute-changed!
  [^js el _name _old-value _new-value]
  (when-let [refs (gobj/get el k-refs)]
    (render! el refs)))

;; ── Property accessors ───────────────────────────────────────────────────────

(defn- install-bool-prop!
  [^js proto prop-name attr-name]
  (.defineProperty
   js/Object
   proto
   prop-name
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as ^js this
                        (has-attr? this attr-name)))
        :set (fn [value]
               (this-as ^js this
                        (set-bool-attr! this attr-name (boolean value))))}))

(defn- install-string-prop!
  [^js proto prop-name attr-name]
  (.defineProperty
   js/Object
   proto
   prop-name
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as ^js this
                        (or (get-attr this attr-name) "")))
        :set (fn [v]
               (this-as ^js this
                        (if v
                          (.setAttribute this attr-name (str v))
                          (.removeAttribute this attr-name))))}))

(defn- install-property-accessors!
  [^js proto]
  (install-bool-prop! proto "sticky" model/attr-sticky)
  (install-bool-prop! proto "elevated" model/attr-elevated)
  (install-string-prop! proto "label" model/attr-label)
  (install-string-prop! proto "variant" model/attr-variant)
  (install-string-prop! proto "orientation" model/attr-orientation)
  (install-string-prop! proto "alignment" model/attr-alignment)
  (install-string-prop! proto "breakpoint" model/attr-breakpoint))

;; ── Element class (js* pattern — the only permitted approach) ────────────────

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this (connected! this))))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this (disconnected! this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v]
            (this-as ^js this (attribute-changed! this n o v))))

    (install-property-accessors! (.-prototype klass))

    klass))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
