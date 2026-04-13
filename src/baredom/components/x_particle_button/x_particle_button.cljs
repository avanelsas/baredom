(ns baredom.components.x-particle-button.x-particle-button
  (:require [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.components.x-particle-button.model :as model]))

;; ── Constants ───────────────────────────────────────────────────────────────
(def ^:private canvas-pad 24)

;; ── Instance field keys ─────────────────────────────────────────────────────
(def ^:private k-state "__xPBState")
(def ^:private k-hover "__xPBHover")
(def ^:private k-focus-visible "__xPBFocusVisible")
(def ^:private k-active-source "__xPBActiveSource")
(def ^:private k-last-activation "__xPBLastActivation")
(def ^:private k-canvas "__xPBCanvas")
(def ^:private k-ctx "__xPBCtx")
(def ^:private k-particles "__xPBParticles")
(def ^:private k-raf "__xPBRaf")
(def ^:private k-phase "__xPBPhase")
(def ^:private k-resize-observer "__xPBResizeObs")
(def ^:private k-last-time "__xPBLastTime")
(def ^:private k-canvas-w "__xPBCanvasW")
(def ^:private k-canvas-h "__xPBCanvasH")
(def ^:private k-press-x "__xPBPressX")
(def ^:private k-press-y "__xPBPressY")
(def ^:private k-pointer-mx "__xPBPointerMX")
(def ^:private k-pointer-my "__xPBPointerMY")
(def ^:private k-color-palette "__xPBColorPalette")
(def ^:private k-hover-accum "__xPBHoverAccum")
(def ^:private k-burst-frame "__xPBBurstFrame")
(def ^:private k-burst-start "__xPBBurstStart")
(def ^:private k-pad "__xPBPad")
(def ^:private k-hover-start "__xPBHoverStart")

;; ── Instance field helpers ──────────────────────────────────────────────────
(defn- gp [^js el k] (gobj/get el k))
(defn- sp! [^js el k v] (gobj/set el k v))

(defn- get-el-state [^js el] (gp el k-state))
(defn- set-el-state! [^js el v] (sp! el k-state v))

(defn- get-hover [^js el] (= true (gp el k-hover)))
(defn- set-hover! [^js el v] (sp! el k-hover (boolean v)))

(defn- get-focus-visible [^js el] (= true (gp el k-focus-visible)))
(defn- set-focus-visible! [^js el v] (sp! el k-focus-visible (boolean v)))

(defn- get-active-source [^js el] (gp el k-active-source))
(defn- set-active-source! [^js el v] (sp! el k-active-source v))

(defn- get-last-activation-source [^js el] (gp el k-last-activation))
(defn- set-last-activation-source! [^js el v] (sp! el k-last-activation v))

(defn- get-phase [^js el] (gp el k-phase))

(defn- set-phase! [^js el v]
  (let [prev (gp el k-phase)]
    (sp! el k-phase v)
    ;; Sync data-phase attribute on button for CSS distortion
    (when-let [state (gp el k-state)]
      (let [^js btn (aget state "button")]
        (when btn
          (if (= :idle v)
            (.removeAttribute btn "data-phase")
            (.setAttribute btn "data-phase" (name v)))
          ;; Clear JS-driven hover blur when leaving hover-emit
          (when (and (= :hover-emit prev) (not= :hover-emit v))
            (.removeProperty (.-style btn) "filter")))))))

;; ── Utilities ───────────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- find-owner-form [^js el]
  (or (when-let [form-id (.getAttribute el "form")]
        (.getElementById js/document form-id))
      (.closest el "form")))

(defn- read-public-state [^js el]
  (model/public-state
   {:disabled (du/has-attr? el model/attr-disabled)
    :loading (du/has-attr? el model/attr-loading)
    :pressed (du/has-attr? el model/attr-pressed)
    :type (du/get-attr el model/attr-type)
    :variant (du/get-attr el model/attr-variant)
    :size (du/get-attr el model/attr-size)
    :label (du/get-attr el model/attr-label)
    :mode (du/get-attr el model/attr-mode)
    :particle-count (du/get-attr el model/attr-particle-count)
    :intensity (du/get-attr el model/attr-intensity)
    :particle-size (du/get-attr el model/attr-particle-size)
    :reassemble-speed (du/get-attr el model/attr-reassemble-speed)}))

(defn- interactive-el? [^js el]
  (model/interactive? (read-public-state el)))

(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail detail
         :bubbles true
         :composed true
         :cancelable false})))

;; ── Slot helpers ────────────────────────────────────────────────────────────
(defn- assigned-nodes [^js slot-el]
  (.assignedNodes slot-el #js {:flatten true}))

(defn- slot-has-content? [^js slot-el]
  (> (alength (assigned-nodes slot-el)) 0))

(defn- meaningful-text-node? [^js node]
  (and (= (.-nodeType node) js/Node.TEXT_NODE)
       (not= "" (.trim (or (.-textContent node) "")))))

(defn- meaningful-element-node? [^js node]
  (and (= (.-nodeType node) js/Node.ELEMENT_NODE)
       (not= "" (.trim (or (.-textContent node) "")))))

(defn- slot-has-meaningful-text? [^js slot-el]
  (let [nodes (assigned-nodes slot-el)]
    (loop [idx 0]
      (if (< idx (alength nodes))
        (let [node (aget nodes idx)]
          (if (or (meaningful-text-node? node)
                  (meaningful-element-node? node))
            true
            (recur (inc idx))))
        false))))

;; ── Color parsing (offscreen canvas + HSL palette) ──────────────────────────
(def ^:private color-parse-ctx
  (let [^js c (.createElement js/document "canvas")]
    (set! (.-width c) 1)
    (set! (.-height c) 1)
    (.getContext c "2d")))

(defn- parse-rgb
  "Parse any CSS color string to [r g b] (0-255) using offscreen canvas."
  [color-str]
  (let [^js ctx color-parse-ctx]
    (set! (.-fillStyle ctx) "#000")
    (set! (.-fillStyle ctx) color-str)
    (.fillRect ctx 0 0 1 1)
    (let [^js data (.-data (.getImageData ctx 0 0 1 1))]
      [(aget data 0) (aget data 1) (aget data 2)])))

(defn- rgb->hsl
  "Convert RGB (0-255) to HSL (h: 0-360, s: 0-1, l: 0-1)."
  [r g b]
  (let [r (/ r 255.0)
        g (/ g 255.0)
        b (/ b 255.0)
        mx (js/Math.max r g b)
        mn (js/Math.min r g b)
        l (/ (+ mx mn) 2.0)]
    (if (= mx mn)
      [0.0 0.0 l]
      (let [d (- mx mn)
            s (if (> l 0.5)
                (/ d (- 2.0 mx mn))
                (/ d (+ mx mn)))
            h (cond
                (= mx r) (/ (+ (/ (- g b) d) (if (< g b) 6.0 0.0)) 6.0)
                (= mx g) (/ (+ (/ (- b r) d) 2.0) 6.0)
                :else     (/ (+ (/ (- r g) d) 4.0) 6.0))]
        [(* h 360.0) s l]))))

(defn- hue->rgb [p q t]
  (let [t (cond (< t 0) (+ t 1.0) (> t 1.0) (- t 1.0) :else t)]
    (cond
      (< t (/ 1.0 6.0)) (+ p (* (- q p) 6.0 t))
      (< t 0.5) q
      (< t (/ 2.0 3.0)) (+ p (* (- q p) (- (/ 2.0 3.0) t) 6.0))
      :else p)))

(defn- hsl->rgb-str
  "Convert HSL (h: 0-360, s: 0-1, l: 0-1) to 'rgb(r,g,b)' string."
  [h s l]
  (let [h (/ (js/Math.round (mod h 360)) 360.0)
        l (js/Math.max 0.0 (js/Math.min 1.0 l))
        s (js/Math.max 0.0 (js/Math.min 1.0 s))]
    (if (= 0.0 s)
      (let [v (js/Math.round (* l 255))]
        (str "rgb(" v "," v "," v ")"))
      (let [q (if (< l 0.5) (* l (+ 1.0 s)) (- (+ l s) (* l s)))
            p (- (* 2.0 l) q)
            r (js/Math.round (* 255 (hue->rgb p q (+ h (/ 1.0 3.0)))))
            g (js/Math.round (* 255 (hue->rgb p q h)))
            b (js/Math.round (* 255 (hue->rgb p q (- h (/ 1.0 3.0)))))]
        (str "rgb(" r "," g "," b ")")))))

(defn- extract-color-palette!
  "Sample button's computed bg color and generate a 5-color HSL palette."
  [^js el]
  (let [state (get-el-state el)
        ^js button-el (aget state "button")
        ^js cs (js/getComputedStyle button-el)
        [r g b] (parse-rgb (.-backgroundColor cs))
        [h s l] (rgb->hsl r g b)
        ;; 0: Base — exact bg color
        base (hsl->rgb-str h s l)
        ;; 1: Highlight — brighter, slightly desaturated
        highlight (hsl->rgb-str h (js/Math.max 0 (- s 0.10)) (js/Math.min 1.0 (+ l 0.25)))
        ;; 2: Warm shift — hue rotated +15°, same lightness
        warm (hsl->rgb-str (+ h 15) s l)
        ;; 3: Cool shift — hue rotated -15°, slightly brighter
        cool (hsl->rgb-str (- h 15) s (js/Math.min 1.0 (+ l 0.05)))
        ;; 4: White-hot — very light tint of base hue
        white-hot (hsl->rgb-str h (* s 0.3) (js/Math.min 1.0 (+ l 0.45)))]
    (sp! el k-color-palette #js [base highlight warm cool white-hot])))

;; ── Style ───────────────────────────────────────────────────────────────────
(defn- style-text []
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-particle-button-radius:var(--x-radius-md,0.75rem);"
   "--x-particle-button-gap:0.5rem;"
   "--x-particle-button-padding-inline:0.95rem;"
   "--x-particle-button-height-sm:2rem;"
   "--x-particle-button-height-md:2.5rem;"
   "--x-particle-button-height-lg:3rem;"
   "--x-particle-button-font-size-sm:0.875rem;"
   "--x-particle-button-font-size-md:0.9375rem;"
   "--x-particle-button-font-size-lg:1rem;"
   "--x-particle-button-font-weight:600;"
   "--x-particle-button-icon-size-sm:0.875rem;"
   "--x-particle-button-icon-size-md:1rem;"
   "--x-particle-button-icon-size-lg:1.125rem;"
   "--x-particle-button-spinner-size:1em;"
   "--x-particle-button-spinner-stroke:2px;"
   "--x-particle-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08),0 1px 1px rgba(15,23,42,0.04));"
   "--x-particle-button-shadow-hover:var(--x-shadow-md,0 4px 10px rgba(15,23,42,0.10),0 2px 4px rgba(15,23,42,0.06));"
   "--x-particle-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08));"
   "--x-particle-button-bg:var(--x-color-primary,#2563eb);"
   "--x-particle-button-bg-hover:var(--x-color-primary-hover,#1d4ed8);"
   "--x-particle-button-bg-active:var(--x-color-primary-active,#1e40af);"
   "--x-particle-button-bg-disabled:#cbd5e1;"
   "--x-particle-button-fg:#ffffff;"
   "--x-particle-button-fg-disabled:#ffffff;"
   "--x-particle-button-border:transparent;"
   "--x-particle-button-secondary-bg:var(--x-color-surface,#ffffff);"
   "--x-particle-button-secondary-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-particle-button-secondary-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-particle-button-secondary-fg:var(--x-color-text,#0f172a);"
   "--x-particle-button-secondary-border:var(--x-color-border,#cbd5e1);"
   "--x-particle-button-tertiary-bg:var(--x-color-surface-active,#f1f5f9);"
   "--x-particle-button-tertiary-bg-hover:var(--x-color-surface-active,#e2e8f0);"
   "--x-particle-button-tertiary-bg-active:var(--x-color-border,#cbd5e1);"
   "--x-particle-button-tertiary-fg:var(--x-color-text,#0f172a);"
   "--x-particle-button-ghost-bg:transparent;"
   "--x-particle-button-ghost-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-particle-button-ghost-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-particle-button-ghost-fg:var(--x-color-text-muted,#334155);"
   "--x-particle-button-danger-bg:var(--x-color-danger,#dc2626);"
   "--x-particle-button-danger-bg-hover:#b91c1c;"
   "--x-particle-button-danger-bg-active:#991b1b;"
   "--x-particle-button-danger-fg:#ffffff;"
   "--x-particle-button-success-bg:var(--x-color-success,#16a34a);"
   "--x-particle-button-success-bg-hover:#15803d;"
   "--x-particle-button-success-bg-active:#166534;"
   "--x-particle-button-success-fg:#ffffff;"
   "--x-particle-button-warning-bg:var(--x-color-warning,#d97706);"
   "--x-particle-button-warning-bg-hover:#b45309;"
   "--x-particle-button-warning-bg-active:#92400e;"
   "--x-particle-button-warning-fg:#ffffff;"
   "--x-particle-button-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-particle-button-transition-duration:var(--x-transition-duration,140ms);"
   "--x-particle-button-transition-easing:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "}"

   ;; Dark mode
   "@media (prefers-color-scheme: dark){"
   ":host{"
   "--x-particle-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.35),0 1px 1px rgba(0,0,0,0.2));"
   "--x-particle-button-shadow-hover:var(--x-shadow-md,0 6px 14px rgba(0,0,0,0.35),0 2px 6px rgba(0,0,0,0.24));"
   "--x-particle-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.3));"
   "--x-particle-button-bg:var(--x-color-primary,#3b82f6);"
   "--x-particle-button-bg-hover:var(--x-color-primary-hover,#2563eb);"
   "--x-particle-button-bg-active:var(--x-color-primary-active,#1d4ed8);"
   "--x-particle-button-bg-disabled:#334155;"
   "--x-particle-button-fg:#eff6ff;"
   "--x-particle-button-fg-disabled:#94a3b8;"
   "--x-particle-button-secondary-bg:var(--x-color-surface,#111827);"
   "--x-particle-button-secondary-bg-hover:var(--x-color-surface-hover,#1f2937);"
   "--x-particle-button-secondary-bg-active:var(--x-color-surface-active,#273449);"
   "--x-particle-button-secondary-fg:var(--x-color-text,#e5e7eb);"
   "--x-particle-button-secondary-border:var(--x-color-border,#374151);"
   "--x-particle-button-tertiary-bg:var(--x-color-surface-active,#1e293b);"
   "--x-particle-button-tertiary-bg-hover:var(--x-color-surface-active,#273449);"
   "--x-particle-button-tertiary-bg-active:var(--x-color-border,#334155);"
   "--x-particle-button-tertiary-fg:var(--x-color-text,#e5e7eb);"
   "--x-particle-button-ghost-bg:transparent;"
   "--x-particle-button-ghost-bg-hover:var(--x-color-surface-hover,#0f172a);"
   "--x-particle-button-ghost-bg-active:var(--x-color-surface-active,#172033);"
   "--x-particle-button-ghost-fg:var(--x-color-text-muted,#cbd5e1);"
   "--x-particle-button-danger-bg:var(--x-color-danger,#ef4444);"
   "--x-particle-button-danger-bg-hover:#dc2626;"
   "--x-particle-button-danger-bg-active:#b91c1c;"
   "--x-particle-button-danger-fg:#ffffff;"
   "--x-particle-button-success-bg:var(--x-color-success,#22c55e);"
   "--x-particle-button-success-bg-hover:#16a34a;"
   "--x-particle-button-success-bg-active:#15803d;"
   "--x-particle-button-success-fg:#ffffff;"
   "--x-particle-button-warning-bg:var(--x-color-warning,#f59e0b);"
   "--x-particle-button-warning-bg-hover:#d97706;"
   "--x-particle-button-warning-bg-active:#b45309;"
   "--x-particle-button-warning-fg:#ffffff;"
   "--x-particle-button-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"

   ;; Keyframes
   "@keyframes x-particle-button-spin{"
   "from{transform:rotate(0deg);}"
   "to{transform:rotate(360deg);}"
   "}"
   "@keyframes x-pb-glow-pulse{"
   "0%{box-shadow:0 0 0 0 rgba(255,255,255,0.3),var(--x-particle-button-shadow);}"
   "50%{box-shadow:0 0 12px 4px rgba(255,255,255,0.15),var(--x-particle-button-shadow);}"
   "100%{box-shadow:0 0 0 0 rgba(255,255,255,0),var(--x-particle-button-shadow);}"
   "}"

   ;; Button base — premium feel
   "button{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "position:relative;"
   "inline-size:100%;"
   "min-inline-size:0;"
   "min-block-size:var(--x-particle-button-height-md);"
   "padding-inline:var(--x-particle-button-padding-inline);"
   "border-radius:var(--x-particle-button-radius);"
   "border:1px solid var(--x-particle-button-border);"
   "background:var(--x-particle-button-bg);"
   "color:var(--x-particle-button-fg);"
   "font-size:var(--x-particle-button-font-size-md);"
   "font-weight:var(--x-particle-button-font-weight);"
   "line-height:1;"
   "cursor:pointer;"
   "user-select:none;"
   "overflow:hidden;"
   "box-shadow:var(--x-particle-button-shadow);"
   "transition:"
   "background var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "border-color var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "color var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "transform var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "box-shadow var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "opacity var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing),"
   "filter var(--x-particle-button-transition-duration) var(--x-particle-button-transition-easing);"
   "}"
   "button[disabled]{cursor:default;box-shadow:none;}"
   "button:focus-visible{outline:none;box-shadow:0 0 0 3px var(--x-particle-button-focus-ring),var(--x-particle-button-shadow);}"

   ;; Hover: lift up — premium feel
   "button[data-hover='true']:not([disabled]){transform:translateY(-1px);box-shadow:var(--x-particle-button-shadow-hover);}"
   ;; Active: compress — tactile feel
   "button[data-active='true']:not([disabled]){transform:translateY(1px) scale(0.97);box-shadow:var(--x-particle-button-shadow-active);}"
   ;; Glow pulse on click
   "button[data-glow-pulse='true']{animation:x-pb-glow-pulse 400ms ease-out;}"

   ;; Sizes
   "button[data-size='sm']{min-block-size:var(--x-particle-button-height-sm);font-size:var(--x-particle-button-font-size-sm);}"
   "button[data-size='md']{min-block-size:var(--x-particle-button-height-md);font-size:var(--x-particle-button-font-size-md);}"
   "button[data-size='lg']{min-block-size:var(--x-particle-button-height-lg);font-size:var(--x-particle-button-font-size-lg);}"

   ;; Variant colors
   "button[data-variant='primary']{background:var(--x-particle-button-bg);color:var(--x-particle-button-fg);border-color:var(--x-particle-button-border);}"
   "button[data-variant='primary'][data-hover='true']:not([disabled]){background:var(--x-particle-button-bg-hover);}"
   "button[data-variant='primary'][data-active='true']:not([disabled]){background:var(--x-particle-button-bg-active);}"

   "button[data-variant='secondary']{background:var(--x-particle-button-secondary-bg);color:var(--x-particle-button-secondary-fg);border-color:var(--x-particle-button-secondary-border);}"
   "button[data-variant='secondary'][data-hover='true']:not([disabled]){background:var(--x-particle-button-secondary-bg-hover);}"
   "button[data-variant='secondary'][data-active='true']:not([disabled]){background:var(--x-particle-button-secondary-bg-active);}"

   "button[data-variant='tertiary']{background:var(--x-particle-button-tertiary-bg);color:var(--x-particle-button-tertiary-fg);border-color:transparent;box-shadow:none;}"
   "button[data-variant='tertiary'][data-hover='true']:not([disabled]){background:var(--x-particle-button-tertiary-bg-hover);}"
   "button[data-variant='tertiary'][data-active='true']:not([disabled]){background:var(--x-particle-button-tertiary-bg-active);}"

   "button[data-variant='ghost']{background:var(--x-particle-button-ghost-bg);color:var(--x-particle-button-ghost-fg);border-color:transparent;box-shadow:none;}"
   "button[data-variant='ghost'][data-hover='true']:not([disabled]){background:var(--x-particle-button-ghost-bg-hover);}"
   "button[data-variant='ghost'][data-active='true']:not([disabled]){background:var(--x-particle-button-ghost-bg-active);}"

   "button[data-variant='danger']{background:var(--x-particle-button-danger-bg);color:var(--x-particle-button-danger-fg);border-color:transparent;}"
   "button[data-variant='danger'][data-hover='true']:not([disabled]){background:var(--x-particle-button-danger-bg-hover);}"
   "button[data-variant='danger'][data-active='true']:not([disabled]){background:var(--x-particle-button-danger-bg-active);}"

   "button[data-variant='success']{background:var(--x-particle-button-success-bg);color:var(--x-particle-button-success-fg);border-color:transparent;}"
   "button[data-variant='success'][data-hover='true']:not([disabled]){background:var(--x-particle-button-success-bg-hover);}"
   "button[data-variant='success'][data-active='true']:not([disabled]){background:var(--x-particle-button-success-bg-active);}"

   "button[data-variant='warning']{background:var(--x-particle-button-warning-bg);color:var(--x-particle-button-warning-fg);border-color:transparent;}"
   "button[data-variant='warning'][data-hover='true']:not([disabled]){background:var(--x-particle-button-warning-bg-hover);}"
   "button[data-variant='warning'][data-active='true']:not([disabled]){background:var(--x-particle-button-warning-bg-active);}"

   ;; Disabled / loading
   "button[data-loading='true']{cursor:progress;}"
   "button[data-disabled='true']{background:var(--x-particle-button-bg-disabled);color:var(--x-particle-button-fg-disabled);border-color:transparent;opacity:0.72;}"

   ;; Material overlay — specular highlight + glow
   "[part='material-overlay']{"
   "position:absolute;inset:0;border-radius:inherit;pointer-events:none;z-index:1;"
   "background:radial-gradient(circle at var(--_mx,50%) var(--_my,50%),rgba(255,255,255,0.12) 0%,transparent 60%);"
   "opacity:0;transition:opacity 200ms ease;"
   "}"
   "button[data-hover='true']:not([disabled]) [part='material-overlay']{opacity:1;}"
   "button[data-active='true']:not([disabled]) [part='material-overlay']{opacity:0.7;}"

   ;; Inner layout
   "[part='inner']{display:inline-flex;align-items:center;justify-content:center;gap:var(--x-particle-button-gap);min-inline-size:0;position:relative;z-index:2;}"
   "[part='label']{display:inline-flex;align-items:center;justify-content:center;min-inline-size:0;white-space:nowrap;}"
   "[part='icon-start'],[part='icon-end'],[part='spinner']{display:inline-flex;align-items:center;justify-content:center;flex:none;}"

   ;; Icon sizing
   "button[data-size='sm'] [part='icon-start'],button[data-size='sm'] [part='icon-end']{inline-size:var(--x-particle-button-icon-size-sm);block-size:var(--x-particle-button-icon-size-sm);}"
   "button[data-size='md'] [part='icon-start'],button[data-size='md'] [part='icon-end']{inline-size:var(--x-particle-button-icon-size-md);block-size:var(--x-particle-button-icon-size-md);}"
   "button[data-size='lg'] [part='icon-start'],button[data-size='lg'] [part='icon-end']{inline-size:var(--x-particle-button-icon-size-lg);block-size:var(--x-particle-button-icon-size-lg);}"

   ;; Spinner
   "[part='spinner']{inline-size:var(--x-particle-button-spinner-size);block-size:var(--x-particle-button-spinner-size);position:relative;}"
   "[part='spinner-slot']{display:inline-flex;align-items:center;justify-content:center;}"
   "[part='spinner-fallback']{"
   "display:none;inline-size:100%;block-size:100%;box-sizing:border-box;"
   "border-radius:999px;border:var(--x-particle-button-spinner-stroke) solid currentColor;"
   "border-inline-end-color:transparent;animation:x-particle-button-spin 0.7s linear infinite;opacity:0.9;"
   "}"

   ;; Slot visibility
   "button[data-has-icon-start='false'] [part='icon-start']{display:none;}"
   "button[data-has-icon-end='false'] [part='icon-end']{display:none;}"
   "button[data-loading='false'] [part='spinner']{display:none;}"
   "button[data-loading='true'][data-has-spinner='false'] [part='spinner-fallback']{display:inline-block;}"
   "button[data-loading='true'][data-has-spinner='true'] [part='spinner-fallback']{display:none;}"

   ;; Canvas overlay — sits outside button, extends beyond edges for particle escape
   "[part='canvas']{position:absolute;inset:-24px;inline-size:calc(100% + 48px);block-size:calc(100% + 48px);pointer-events:none;z-index:3;}"

   ;; Disperse mode: slight inner content fade during burst
   "button[data-mode='disperse'][data-phase='press-burst'] [part='inner']{opacity:0.85;transition:opacity 200ms ease;}"

   ;; Surface destabilization — button distorts when emitting particles
   ;; hover-emit blur is driven by JS (ramps over time via inline style)
   "button[data-phase='press-burst']:not([disabled]){filter:blur(0.5px) brightness(1.05);}"
   "button[data-phase='press-reform']:not([disabled]){filter:blur(0.4px) brightness(1.03);}"
   "button[data-mode='disperse'][data-phase='press-burst']:not([disabled]){filter:blur(1px) brightness(1.08);}"

   ;; Reduced motion
   "@media (prefers-reduced-motion: reduce){"
   "button{transition:none;filter:none !important;}"
   "[part='spinner-fallback']{animation:none;}"
   "[part='canvas']{display:none;}"
   "[part='material-overlay']{display:none;}"
   "button[data-glow-pulse='true']{animation:none;}"
   "}"

   ;; Coarse pointer — touch targets
   "@media (pointer:coarse){"
   "button{min-block-size:2.75rem;}"
   "button[data-size='sm']{min-block-size:2.75rem;}"
   "}"
   ))

;; ── DOM creation ────────────────────────────────────────────────────────────
(defn- create-el [tag]
  (.createElement js/document tag))

(defn- create-shadow! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-el (create-el "style")
        button-el (create-el "button")
        overlay-el (create-el "span")
        canvas-el (create-el "canvas")
        inner-el (create-el "span")
        icon-start-el (create-el "span")
        label-el (create-el "span")
        spinner-el (create-el "span")
        spinner-fallback-el (create-el "span")
        icon-end-el (create-el "span")
        default-slot-el (create-el "slot")
        icon-start-slot-el (create-el "slot")
        icon-end-slot-el (create-el "slot")
        spinner-slot-el (create-el "slot")]

    (set! (.-textContent style-el) (style-text))

    (.setAttribute button-el "part" "button")
    (.setAttribute overlay-el "part" "material-overlay")
    (.setAttribute overlay-el "aria-hidden" "true")
    (.setAttribute canvas-el "part" "canvas")
    (.setAttribute canvas-el "aria-hidden" "true")
    (.setAttribute inner-el "part" "inner")
    (.setAttribute icon-start-el "part" "icon-start")
    (.setAttribute label-el "part" "label")
    (.setAttribute spinner-el "part" "spinner")
    (.setAttribute spinner-slot-el "part" "spinner-slot")
    (.setAttribute spinner-fallback-el "part" "spinner-fallback")
    (.setAttribute icon-end-el "part" "icon-end")

    (.setAttribute icon-start-slot-el "name" model/slot-icon-start)
    (.setAttribute icon-end-slot-el "name" model/slot-icon-end)
    (.setAttribute spinner-slot-el "name" model/slot-spinner)

    (.setAttribute spinner-el "aria-hidden" "true")
    (.setAttribute spinner-fallback-el "aria-hidden" "true")

    (.appendChild icon-start-el icon-start-slot-el)
    (.appendChild label-el default-slot-el)
    (.appendChild spinner-el spinner-slot-el)
    (.appendChild spinner-el spinner-fallback-el)
    (.appendChild icon-end-el icon-end-slot-el)

    (.appendChild inner-el icon-start-el)
    (.appendChild inner-el label-el)
    (.appendChild inner-el spinner-el)
    (.appendChild inner-el icon-end-el)

    ;; Overlay and inner inside button; canvas outside for overflow
    (.appendChild button-el overlay-el)
    (.appendChild button-el inner-el)

    (.appendChild root style-el)
    (.appendChild root button-el)
    (.appendChild root canvas-el)

    (sp! el k-canvas canvas-el)
    (sp! el k-ctx (.getContext canvas-el "2d"))

    #js {:root root
         :style style-el
         :button button-el
         :overlay overlay-el
         :canvas canvas-el
         :inner inner-el
         :label-slot default-slot-el
         :icon-start-slot icon-start-slot-el
         :icon-end-slot icon-end-slot-el
         :spinner-slot spinner-slot-el}))

;; ── Render ──────────────────────────────────────────────────────────────────
(defn- render! [^js el state]
  (let [button-el (aget state "button")
        label-slot-el (aget state "label-slot")
        icon-start-slot-el (aget state "icon-start-slot")
        icon-end-slot-el (aget state "icon-end-slot")
        spinner-slot-el (aget state "spinner-slot")
        ps (read-public-state el)
        has-default-text? (slot-has-meaningful-text? label-slot-el)
        has-icon-start? (slot-has-content? icon-start-slot-el)
        has-icon-end? (slot-has-content? icon-end-slot-el)
        has-spinner? (slot-has-content? spinner-slot-el)
        hover? (get-hover el)
        active? (some? (get-active-source el))
        focus-visible? (get-focus-visible el)
        aria-label-value (model/aria-label
                          (assoc ps :has-default-text? has-default-text?))]

    (.setAttribute button-el "type" (:type ps))
    (set! (.-disabled button-el) (or (:disabled ps) (:loading ps)))

    (if-let [v (model/aria-busy ps)]
      (.setAttribute button-el "aria-busy" v)
      (.removeAttribute button-el "aria-busy"))

    (if (:pressed ps)
      (.setAttribute button-el "aria-pressed" "true")
      (.removeAttribute button-el "aria-pressed"))

    (if aria-label-value
      (.setAttribute button-el "aria-label" aria-label-value)
      (.removeAttribute button-el "aria-label"))

    (.setAttribute button-el "data-variant" (:variant ps))
    (.setAttribute button-el "data-size" (:size ps))
    (.setAttribute button-el "data-mode" (:mode ps))
    (.setAttribute button-el "data-loading" (if (:loading ps) "true" "false"))
    (.setAttribute button-el "data-disabled" (if (:disabled ps) "true" "false"))
    (.setAttribute button-el "data-hover" (if hover? "true" "false"))
    (.setAttribute button-el "data-active" (if active? "true" "false"))
    (.setAttribute button-el "data-focus-visible" (if focus-visible? "true" "false"))
    (.setAttribute button-el "data-has-icon-start" (if has-icon-start? "true" "false"))
    (.setAttribute button-el "data-has-icon-end" (if has-icon-end? "true" "false"))
    (.setAttribute button-el "data-has-spinner" (if has-spinner? "true" "false"))

    (.setAttribute el "data-variant" (:variant ps))
    (.setAttribute el "data-size" (:size ps))
    (.setAttribute el "data-mode" (:mode ps))))

;; ── Particle pool ───────────────────────────────────────────────────────────
(defn- init-particle-pool! [^js el]
  (let [ps (read-public-state el)
        ;; Pool size: max concurrent particles (2x burst count for overlap)
        pool-size (* 2 (:particle-count ps))
        particles (js/Array.)]
    (dotimes [_ pool-size]
      (.push particles
             #js {:alive false
                  :type "d" :x 0 :y 0 :vx 0 :vy 0
                  :size 1 :sizeY 1 :opacity 0 :maxOpacity 0
                  :lifetime 500 :age 0 :colorIdx 0 :blur 0
                  :homeX 0 :homeY 0
                  :tx0 0 :ty0 0 :tx1 0 :ty1 0 :tx2 0 :ty2 0 :tlen 0}))
    (sp! el k-particles particles)))

(defn- find-dead-particle [^js particles]
  (let [len (alength particles)]
    (loop [i 0]
      (when (< i len)
        (let [p (aget particles i)]
          (if-not (aget p "alive")
            p
            (recur (inc i))))))))

(defn- emit-particle!
  "Initialize a dead particle and mark it alive."
  [^js el ptype x y vx vy]
  (let [particles (gp el k-particles)]
    (when particles
      (when-let [p (find-dead-particle particles)]
        (let [ps (read-public-state el)
              base-size (:particle-size ps)
              mode (:mode ps)
              [min-s max-s] (model/particle-size-range ptype base-size mode)
              [min-lt max-lt] (model/particle-lifetime-range ptype mode)
              [min-op max-op] (model/particle-opacity-range ptype mode)
              sz (+ min-s (* (js/Math.random) (- max-s min-s)))
              lt (+ min-lt (* (js/Math.random) (- max-lt min-lt)))
              op (+ min-op (* (js/Math.random) (- max-op min-op)))
              ;; 10-15% chance of lingering (1.5x lifetime)
              linger? (< (js/Math.random) 0.12)
              color-r (js/Math.random)
              cidx (cond (< color-r 0.40) 0    ;; base
                         (< color-r 0.60) 1    ;; highlight
                         (< color-r 0.75) 2    ;; warm shift
                         (< color-r 0.90) 3    ;; cool shift
                         :else 4)]             ;; white-hot
          (aset p "alive" true)
          (aset p "type" ptype)
          (aset p "x" x)
          (aset p "y" y)
          (aset p "vx" vx)
          (aset p "vy" vy)
          (aset p "size" sz)
          (aset p "sizeY" (if (= ptype model/type-streak)
                            (* sz (+ 3.0 (* (js/Math.random) 3.0)))
                            sz))
          (aset p "opacity" op)
          (aset p "maxOpacity" op)
          (aset p "lifetime" (if linger? (* lt 1.5) lt))
          (aset p "age" 0)
          (aset p "colorIdx" cidx)
          (aset p "blur" (if (and (= ptype model/type-dust) (< (js/Math.random) 0.3)) 1.0 0.0))
          (aset p "homeX" x)
          (aset p "homeY" y)
          ;; Trail for streaks
          (aset p "tx0" x) (aset p "ty0" y)
          (aset p "tx1" x) (aset p "ty1" y)
          (aset p "tx2" x) (aset p "ty2" y)
          (aset p "tlen" 0)
          p)))))

;; ── Emission functions ──────────────────────────────────────────────────────
(defn- emit-hover-particles! [^js el dt-ms]
  (let [ps (read-public-state el)
        mode-cfg (model/mode-physics (:mode ps))
        rate (:hover-emit-rate mode-cfg)
        accum (+ (or (gp el k-hover-accum) 0.0) (* rate (/ dt-ms 16.667)))
        w (or (gp el k-canvas-w) 100)
        h (or (gp el k-canvas-h) 40)]
    (sp! el k-hover-accum (- accum (js/Math.floor accum)))
    (dotimes [_ (js/Math.floor accum)]
      (let [ptype (model/pick-particle-type (js/Math.random) mode-cfg)
            ;; Emit from edge or surface
            [ex ey nx ny] (if (and (= :edge-and-surface (:hover-source mode-cfg))
                                   (< (js/Math.random) 0.3))
                            ;; Surface point
                            [(* (js/Math.random) w)
                             (* (js/Math.random) h)
                             0.0 -1.0]
                            ;; Edge point
                            (model/random-edge-point w h (js/Math.random)))
            drift-vy (:hover-drift-vy mode-cfg)
            drift-vx-range (:hover-drift-vx-range mode-cfg)
            vx (+ (* nx 0.5) (* (- (js/Math.random) 0.5) drift-vx-range))
            vy (+ (* ny 0.5) drift-vy (* (js/Math.random) 0.3))]
        (emit-particle! el ptype ex ey vx vy)))))

(defn- emit-press-burst! [^js el]
  (let [ps (read-public-state el)
        mode-cfg (model/mode-physics (:mode ps))
        intensity-factor (/ (:intensity ps) 50.0)
        base-speed (* (:press-base-speed mode-cfg) intensity-factor)
        spread (:press-spread mode-cfg)
        min-count (:press-burst-count-min mode-cfg)
        max-count (:press-burst-count-max mode-cfg)
        count (+ min-count (js/Math.round (* (js/Math.random) (- max-count min-count))))
        px (or (gp el k-press-x) (/ (or (gp el k-canvas-w) 100) 2))
        py (or (gp el k-press-y) (/ (or (gp el k-canvas-h) 40) 2))
        w (or (gp el k-canvas-w) 100)
        h (or (gp el k-canvas-h) 40)]
    (dotimes [_ count]
      (let [ptype (model/pick-particle-type (js/Math.random) mode-cfg)
            [vx vy] (model/press-burst-velocity
                     px py w h base-speed spread
                     (js/Math.random) (js/Math.random))
            ;; Spawn near press point with slight scatter
            scatter 6.0
            sx (+ px (* (- (js/Math.random) 0.5) scatter))
            sy (+ py (* (- (js/Math.random) 0.5) scatter))]
        (emit-particle! el ptype sx sy vx vy)))))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- stop-animation! [^js el]
  (when-let [raf (gp el k-raf)]
    (js/cancelAnimationFrame raf)
    (sp! el k-raf nil)))

(declare animate!)

(defn- start-animation! [^js el]
  (when-not (gp el k-raf)
    (sp! el k-last-time (js/performance.now))
    (sp! el k-raf (js/requestAnimationFrame (fn [_] (animate! el))))))

(defn- count-alive [^js particles]
  (let [len (alength particles)]
    (loop [i 0 n 0]
      (if (< i len)
        (recur (inc i) (if (aget (aget particles i) "alive") (inc n) n))
        n))))

(defn- update-particles! [^js el dt-ms]
  (let [particles (gp el k-particles)
        ps (read-public-state el)
        mode-cfg (model/mode-physics (:mode ps))
        gravity (:gravity mode-cfg)
        friction (:friction mode-cfg)
        reform-spring (:reform-spring mode-cfg)
        phase (get-phase el)
        reforming? (= :press-reform phase)]
    (when particles
      (let [len (alength particles)]
        (dotimes [i len]
          (let [p (aget particles i)]
            (when (aget p "alive")
              (let [age (+ (aget p "age") dt-ms)
                    lifetime (aget p "lifetime")]
                (aset p "age" age)
                (if (>= age lifetime)
                  ;; Kill particle
                  (aset p "alive" false)
                  (let [vx (* (aget p "vx") friction)
                        vy (* (+ (aget p "vy") gravity) friction)
                        ;; Reform spring (disperse mode)
                        vx (if reforming?
                             (+ vx (* (- (aget p "homeX") (aget p "x")) reform-spring))
                             vx)
                        vy (if reforming?
                             (+ vy (* (- (aget p "homeY") (aget p "y")) reform-spring))
                             vy)]
                    (aset p "vx" vx)
                    (aset p "vy" vy)
                    (aset p "x" (+ (aget p "x") (* vx (/ dt-ms 16.667))))
                    (aset p "y" (+ (aget p "y") (* vy (/ dt-ms 16.667))))
                    ;; Fade based on age
                    (let [life-frac (/ age lifetime)
                          fade (if (> life-frac 0.6)
                                 (- 1.0 (/ (- life-frac 0.6) 0.4))
                                 1.0)]
                      (aset p "opacity" (* (aget p "maxOpacity") fade)))
                    ;; Update trail for streaks
                    (when (= model/type-streak (aget p "type"))
                      (let [tlen (aget p "tlen")]
                        (aset p "tx2" (aget p "tx1"))
                        (aset p "ty2" (aget p "ty1"))
                        (aset p "tx1" (aget p "tx0"))
                        (aset p "ty1" (aget p "ty0"))
                        (aset p "tx0" (aget p "x"))
                        (aset p "ty0" (aget p "y"))
                        (when (< tlen 3) (aset p "tlen" (inc tlen)))))))))))))))

(defn- draw-particles! [^js el]
  (let [^js ctx (gp el k-ctx)
        ^js canvas (gp el k-canvas)
        cw (.-width canvas)
        ch (.-height canvas)
        dpr (js/Math.min 2.0 js/devicePixelRatio)
        pad (* (or (gp el k-pad) canvas-pad) dpr)
        particles (gp el k-particles)
        palette (gp el k-color-palette)
        ps (read-public-state el)
        mode-cfg (model/mode-physics (:mode ps))
        glow? (:glow-render mode-cfg)
        additive? (:additive-core mode-cfg)]
    (.clearRect ctx 0 0 cw ch)
    (when (and particles palette)
      (dotimes [i (alength particles)]
        (let [p (aget particles i)]
          (when (aget p "alive")
            (let [;; Offset by padding so (0,0) in particle space maps to pad in canvas space
                  x (+ (* (aget p "x") dpr) pad)
                  y (+ (* (aget p "y") dpr) pad)
                  sz (* (aget p "size") dpr)
                  opacity (aget p "opacity")
                  color (aget palette (aget p "colorIdx"))
                  ptype (aget p "type")]
              (set! (.-fillStyle ctx) color)
              (case ptype
                ;; Dust: circle with optional glow halo (ember) or bright core (spark)
                "d"
                (do
                  ;; Glow halo for ember mode — soft outer ring
                  (when glow?
                    (set! (.-globalAlpha ctx) (* opacity 0.10))
                    (.beginPath ctx)
                    (.arc ctx x y (* sz 2.5) 0 (* 2 js/Math.PI))
                    (.fill ctx)
                    (set! (.-globalAlpha ctx) (* opacity 0.20))
                    (.beginPath ctx)
                    (.arc ctx x y (* sz 1.6) 0 (* 2 js/Math.PI))
                    (.fill ctx))
                  ;; Main dot
                  (set! (.-globalAlpha ctx) opacity)
                  (.beginPath ctx)
                  (.arc ctx x y sz 0 (* 2 js/Math.PI))
                  (.fill ctx)
                  ;; Bright core for spark mode
                  (when additive?
                    (set! (.-globalAlpha ctx) (js/Math.min 1.0 (* opacity 1.5)))
                    (set! (.-fillStyle ctx) "#ffffff")
                    (.beginPath ctx)
                    (.arc ctx x y (* sz 0.3) 0 (* 2 js/Math.PI))
                    (.fill ctx)
                    (set! (.-fillStyle ctx) color))
                  ;; Soft blur halo for blurry dust particles
                  (when (and (not glow?) (> (aget p "blur") 0))
                    (set! (.-globalAlpha ctx) (* opacity 0.15))
                    (.beginPath ctx)
                    (.arc ctx x y (* sz 1.5) 0 (* 2 js/Math.PI))
                    (.fill ctx)))

                ;; Fragment: rounded rect
                "f"
                (let [half-sz sz
                      r (* sz 0.3)]
                  ;; Glow for ember fragments
                  (when glow?
                    (set! (.-globalAlpha ctx) (* opacity 0.08))
                    (.beginPath ctx)
                    (.arc ctx x y (* half-sz 2) 0 (* 2 js/Math.PI))
                    (.fill ctx))
                  (set! (.-globalAlpha ctx) opacity)
                  (.beginPath ctx)
                  (.roundRect ctx (- x half-sz) (- y half-sz) (* half-sz 2) (* half-sz 2) r)
                  (.fill ctx))

                ;; Streak: elongated rect with trail
                "s"
                (let [sy (* (aget p "sizeY") dpr)
                      tlen (aget p "tlen")
                      ;; Offset trail positions by pad too
                      tx1 (+ (* (aget p "tx1") dpr) pad)
                      ty1 (+ (* (aget p "ty1") dpr) pad)
                      tx2 (+ (* (aget p "tx2") dpr) pad)
                      ty2 (+ (* (aget p "ty2") dpr) pad)]
                  ;; Trail with decreasing opacity
                  (when (> tlen 0)
                    (set! (.-globalAlpha ctx) (* opacity 0.15))
                    (.fillRect ctx (- tx2 (* sz 0.5)) (- ty2 (* sy 0.5)) sz sy)
                    (when (> tlen 1)
                      (set! (.-globalAlpha ctx) (* opacity 0.35))
                      (.fillRect ctx (- tx1 (* sz 0.5)) (- ty1 (* sy 0.5)) sz sy)))
                  ;; Current position
                  (set! (.-globalAlpha ctx) opacity)
                  (.fillRect ctx (- x (* sz 0.5)) (- y (* sy 0.5)) sz sy)
                  ;; Bright core for spark streaks
                  (when additive?
                    (set! (.-globalAlpha ctx) (js/Math.min 1.0 (* opacity 1.3)))
                    (set! (.-fillStyle ctx) "#ffffff")
                    (.fillRect ctx (- x (* sz 0.3)) (- y (* sy 0.3)) (* sz 0.6) (* sy 0.6))
                    (set! (.-fillStyle ctx) color)))

                ;; fallback
                nil))))))
    (set! (.-globalAlpha ctx) 1.0)))

(defn- animate! [^js el]
  (sp! el k-raf nil)
  (when (.-isConnected el)
    (let [phase (get-phase el)
          now (js/performance.now)
          last-time (or (gp el k-last-time) now)
          dt-ms (js/Math.min 100.0 (- now last-time))
          particles (gp el k-particles)]
      (sp! el k-last-time now)

      ;; Emit new particles based on phase
      (case phase
        :hover-emit
        (when-not (prefers-reduced-motion?)
          (emit-hover-particles! el dt-ms)
          ;; Ramp blur over hover duration: 0 → 1.5px over ~2s
          (when-let [state (get-el-state el)]
            (let [hover-start (or (gp el k-hover-start) now)
                  elapsed-s (/ (- now hover-start) 1000.0)
                  ;; Ease-out curve: fast initial ramp, slows toward max
                  blur (js/Math.min 1.5 (* 1.5 (- 1.0 (js/Math.exp (* -1.5 elapsed-s)))))
                  brightness (+ 1.0 (* 0.04 (/ blur 1.5)))
                  ^js btn (aget state "button")]
              (.setProperty (.-style btn) "filter"
                            (str "blur(" (.toFixed blur 2) "px) brightness(" (.toFixed brightness 3) ")")))))

        :press-burst
        ;; Burst emits once (on first frame), then transitions
        (when (= 0 (or (gp el k-burst-frame) 0))
          (sp! el k-burst-frame 1)
          (emit-press-burst! el))

        nil)

      ;; Update all alive particles
      (update-particles! el dt-ms)

      ;; Draw
      (draw-particles! el)

      ;; Phase transitions based on alive count
      (let [alive (if particles (count-alive particles) 0)
            emitting? (#{:hover-emit :press-burst} phase)]

        ;; press-burst → settling (or press-reform for disperse)
        (when (and (= :press-burst phase)
                   (> (or (gp el k-burst-frame) 0) 0))
          (let [ps (read-public-state el)
                elapsed (- now (or (gp el k-burst-start) now))]
            (when (> elapsed 50)
              (if (= "disperse" (:mode ps))
                (set-phase! el :press-reform)
                (set-phase! el :settling)))))

        ;; press-reform → settling (when all particles nearly home or dead)
        (when (= :press-reform phase)
          (let [elapsed (- now (or (gp el k-burst-start) now))
                ps (read-public-state el)]
            (when (> elapsed (:reassemble-speed ps))
              (set-phase! el :settling)
              (dispatch! el model/event-reform
                         #js {:mode (:mode ps)
                              :duration (js/Math.round elapsed)}))))

        ;; settling → idle (when all particles dead)
        (when (and (= :settling phase) (= 0 alive))
          (set-phase! el :idle)
          (stop-animation! el))

        ;; Continue animation if still active
        (when (and (not= :idle phase)
                   (or emitting? (> alive 0)))
          (sp! el k-raf (js/requestAnimationFrame (fn [_] (animate! el)))))

        ;; Also continue if settling with alive particles
        (when (and (= :settling phase) (> alive 0) (not (gp el k-raf)))
          (sp! el k-raf (js/requestAnimationFrame (fn [_] (animate! el))))))
      nil)))


;; ── Canvas sizing ───────────────────────────────────────────────────────────
(defn- resize-canvas! [^js el w h]
  (let [^js canvas (gp el k-canvas)
        dpr (js/Math.min 2.0 js/devicePixelRatio)
        ;; Canvas is larger than button by canvas-pad on each side
        total-w (+ w (* 2 canvas-pad))
        total-h (+ h (* 2 canvas-pad))
        cw (js/Math.floor (* total-w dpr))
        ch (js/Math.floor (* total-h dpr))]
    (when (and (pos? cw) (pos? ch))
      (set! (.-width canvas) cw)
      (set! (.-height canvas) ch)
      (sp! el k-canvas-w w)
      (sp! el k-canvas-h h)
      (sp! el k-pad canvas-pad))))

(defn- setup-resize-observer! [^js el ^js button-el]
  (let [ro (js/ResizeObserver.
            (fn [entries]
              (when (pos? (alength entries))
                (let [^js entry (aget entries 0)
                      ^js cr (.-contentRect entry)
                      w (.-width cr)
                      h (.-height cr)]
                  (when (and (> w 0) (> h 0))
                    (resize-canvas! el w h)
                    (extract-color-palette! el))))))]
    (.observe ro button-el)
    (sp! el k-resize-observer ro)))

;; ── Interaction setup ───────────────────────────────────────────────────────
(defn- end-active-press! [^js el]
  (let [source (get-active-source el)]
    (when source
      (set-active-source! el nil)
      (dispatch! el model/event-press-end #js {:source source})
      (when-let [state (get-el-state el)]
        (render! el state)))))

(defn- sync-noninteractive-state! [^js el]
  (when-not (interactive-el? el)
    (set-hover! el false)
    (set-active-source! el nil)
    (when (not= :idle (get-phase el))
      (set-phase! el :settling))))

(defn- trigger-glow-pulse! [^js el]
  (when-let [state (get-el-state el)]
    (let [^js button-el (aget state "button")]
      (.setAttribute button-el "data-glow-pulse" "true")
      (.addEventListener button-el "animationend"
                         (fn remove-glow [_]
                           (.removeAttribute button-el "data-glow-pulse")
                           (.removeEventListener button-el "animationend" remove-glow))
                         #js {:once true}))))

(defn- setup-pointer-tracking! [^js el ^js button-el]
  (.addEventListener
   button-el
   "pointermove"
   (fn [^js e]
     (let [^js rect (.getBoundingClientRect button-el)
           mx (-> (- (.-clientX e) (.-left rect))
                  (/ (.-width rect))
                  (* 100))
           my (-> (- (.-clientY e) (.-top rect))
                  (/ (.-height rect))
                  (* 100))]
       (.setProperty (.-style button-el) "--_mx" (str mx "%"))
       (.setProperty (.-style button-el) "--_my" (str my "%"))
       (sp! el k-pointer-mx mx)
       (sp! el k-pointer-my my)))
   #js {:passive true}))

(defn- setup-hover! [^js el ^js button-el]
  (.addEventListener
   button-el
   "pointerenter"
   (fn [_]
     (when (interactive-el? el)
       (when-not (get-hover el)
         (set-hover! el true)
         (render! el (get-el-state el))
         (dispatch! el model/event-hover-start #js {})
         (when-not (prefers-reduced-motion?)
           (when (#{:idle :settling} (get-phase el))
             (set-phase! el :hover-emit)
             (sp! el k-hover-accum 0.0)
             (sp! el k-hover-start (js/performance.now))
             (start-animation! el)))))))

  (.addEventListener
   button-el
   "pointerleave"
   (fn [_]
     (when (get-hover el)
       (set-hover! el false)
       (render! el (get-el-state el))
       (when (interactive-el? el)
         (dispatch! el model/event-hover-end #js {}))
       (when (= :hover-emit (get-phase el))
         (set-phase! el :settling)
         ;; Clear the ramping blur inline style
         (when-let [state (get-el-state el)]
           (.removeProperty (.-style (aget state "button")) "filter"))))
     (when (= "pointer" (get-active-source el))
       (end-active-press! el)))))

(defn- setup-press! [^js el ^js button-el]
  (.addEventListener
   button-el
   "pointerdown"
   (fn [^js event]
     (when (interactive-el? el)
       ;; Capture press point
       (let [^js rect (.getBoundingClientRect button-el)
             px (- (.-clientX event) (.-left rect))
             py (- (.-clientY event) (.-top rect))]
         (sp! el k-press-x px)
         (sp! el k-press-y py))
       (when-not (get-active-source el)
         (set-last-activation-source! el "pointer")
         (set-active-source! el "pointer")
         (render! el (get-el-state el))
         (dispatch! el model/event-press-start #js {:source "pointer"})))))

  (.addEventListener
   button-el
   "pointerup"
   (fn [_]
     (when (= "pointer" (get-active-source el))
       (end-active-press! el))))

  (.addEventListener
   button-el
   "pointercancel"
   (fn [_]
     (when (= "pointer" (get-active-source el))
       (end-active-press! el))))

  (.addEventListener
   button-el
   "keydown"
   (fn [^js event]
     (let [key (.-key event)]
       (when (and (interactive-el? el)
                  (or (= key "Enter") (= key " ")))
         (when-not (= "keyboard" (get-active-source el))
           ;; Use center as press point for keyboard
           (sp! el k-press-x (/ (or (gp el k-canvas-w) 100) 2))
           (sp! el k-press-y (/ (or (gp el k-canvas-h) 40) 2))
           (set-last-activation-source! el "keyboard")
           (set-active-source! el "keyboard")
           (render! el (get-el-state el))
           (dispatch! el model/event-press-start #js {:source "keyboard"}))))))

  (.addEventListener
   button-el
   "keyup"
   (fn [^js event]
     (let [key (.-key event)]
       (when (and (= "keyboard" (get-active-source el))
                  (or (= key "Enter") (= key " ")))
         (end-active-press! el)))))

  (.addEventListener
   button-el
   "blur"
   (fn [_]
     (when (get-active-source el)
       (end-active-press! el))))

  (.addEventListener
   button-el
   "click"
   (fn [_]
     (when (interactive-el? el)
       (let [source (or (get-last-activation-source el) "programmatic")
             ps (read-public-state el)]
         (dispatch! el model/event-press #js {:source source})
         ;; Glow pulse
         (trigger-glow-pulse! el)
         ;; Particle burst
         (when-not (prefers-reduced-motion?)
           (when (not (#{:press-burst :press-reform} (get-phase el)))
             (set-phase! el :press-burst)
             (sp! el k-burst-frame 0)
             (sp! el k-burst-start (js/performance.now))
             (dispatch! el model/event-burst
                        #js {:mode (:mode ps)
                             :press-x (or (gp el k-press-x) 0)
                             :press-y (or (gp el k-press-y) 0)})
             (start-animation! el)))
         (set-last-activation-source! el nil)
         (let [btn-type (:type ps)
               form (find-owner-form el)]
           (when form
             (cond
               (= btn-type "submit") (.requestSubmit form)
               (= btn-type "reset")  (.reset form)))))))))

(defn- setup-focus! [^js el ^js button-el]
  (.addEventListener
   button-el
   "focus"
   (fn [_]
     (let [visible (.matches button-el ":focus-visible")]
       (set-focus-visible! el visible)
       (render! el (get-el-state el))
       (when visible
         (dispatch! el model/event-focus-visible #js {})))))

  (.addEventListener
   button-el
   "blur"
   (fn [_]
     (set-focus-visible! el false)
     (render! el (get-el-state el)))))

(defn- setup-slots! [^js el state]
  (let [rerender (fn [_] (render! el state))]
    (.addEventListener (aget state "label-slot") "slotchange" rerender)
    (.addEventListener (aget state "icon-start-slot") "slotchange" rerender)
    (.addEventListener (aget state "icon-end-slot") "slotchange" rerender)
    (.addEventListener (aget state "spinner-slot") "slotchange" rerender)))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (get-el-state el)
    (let [state (create-shadow! el)
          button-el (aget state "button")]
      (set-hover! el false)
      (set-focus-visible! el false)
      (set-active-source! el nil)
      (set-last-activation-source! el nil)
      (set-phase! el :idle)
      (sp! el k-hover-accum 0.0)
      (sp! el k-burst-frame 0)
      (init-particle-pool! el)
      (setup-pointer-tracking! el button-el)
      (setup-hover! el button-el)
      (setup-press! el button-el)
      (setup-focus! el button-el)
      (setup-slots! el state)
      (setup-resize-observer! el button-el)
      (set-el-state! el state)))
  (sync-noninteractive-state! el)
  (render! el (get-el-state el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (set-phase! el :idle)
  (when-let [ro (gp el k-resize-observer)]
    (.disconnect ro)
    (sp! el k-resize-observer nil))
  (set-hover! el false)
  (set-focus-visible! el false)
  (set-active-source! el nil)
  (set-last-activation-source! el nil))

(defn- attribute-changed! [^js el _name _old-value _new-value]
  (when-let [state (get-el-state el)]
    (sync-noninteractive-state! el)
    (render! el state)
    ;; Re-extract colors on variant change
    (when (gp el k-canvas-w)
      (extract-color-palette! el))))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- define-bool-prop! [proto prop-name attr-name]
  (.defineProperty
   js/Object
   proto
   prop-name
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (du/has-attr? this attr-name)))
        :set (fn [value]
               (this-as this
                        (du/set-bool-attr! this attr-name (boolean value))))}))

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-formAssociated klass) true)
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (disconnected! this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (define-bool-prop! (.-prototype klass) "disabled" model/attr-disabled)
    (define-bool-prop! (.-prototype klass) "loading"  model/attr-loading)
    (define-bool-prop! (.-prototype klass) "pressed"  model/attr-pressed)

    klass))

(defn define-element! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (define-element!))
