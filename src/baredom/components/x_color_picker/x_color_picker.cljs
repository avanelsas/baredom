(ns baredom.components.x-color-picker.x-color-picker
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.components.x-color-picker.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via du/getv / du/setv!)
;; ---------------------------------------------------------------------------
(def ^:private k-refs           "__xColorPickerRefs")
(def ^:private k-model          "__xColorPickerModel")
(def ^:private k-internals      "__xColorPickerInternals")
(def ^:private k-handlers       "__xColorPickerHandlers")
(def ^:private k-dragging       "__xColorPickerDragging")
(def ^:private k-swatches-cache "__xColorPickerSwatchesCache")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ;; ── Host ──────────────────────────────────────────────────────────────
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "position:relative;"
   "--x-color-picker-width:240px;"
   "--x-color-picker-area-height:160px;"
   "--x-color-picker-strip-height:14px;"
   "--x-color-picker-swatch-size:28px;"
   "--x-color-picker-radius:var(--x-radius-md,8px);"
   "--x-color-picker-gap:10px;"
   "--x-color-picker-bg:var(--x-color-bg,#ffffff);"
   "--x-color-picker-border:var(--x-color-border,#cbd5e1);"
   "--x-color-picker-text:var(--x-color-text,#1e293b);"
   "--x-color-picker-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-color-picker-disabled-opacity:0.45;"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-color-picker-bg:var(--x-color-bg,#1e293b);"
   "--x-color-picker-border:var(--x-color-border,#475569);"
   "--x-color-picker-text:var(--x-color-text,#f1f5f9);"
   "--x-color-picker-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}}"

   ;; ── Container ─────────────────────────────────────────────────────────
   "[part=container]{"
   "position:relative;"
   "width:var(--x-color-picker-width);"
   "max-width:calc(100vw - 2rem);"
   "}"

   ;; ── Trigger (popover mode) ────────────────────────────────────────────
   "[part=trigger]{"
   "all:unset;"
   "display:none;"
   "box-sizing:border-box;"
   "width:36px;height:36px;"
   "border-radius:var(--x-color-picker-radius);"
   "border:2px solid var(--x-color-picker-border);"
   "cursor:pointer;"
   "overflow:hidden;"
   "}"
   "[part=trigger]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:2px;}"
   ":host([data-mode=popover]) [part=trigger]{display:block;}"

   "[part=trigger-swatch]{"
   "display:block;width:100%;height:100%;}"

   ;; ── Panel ─────────────────────────────────────────────────────────────
   "[part=panel]{"
   "box-sizing:border-box;"
   "background:var(--x-color-picker-bg);"
   "border:1px solid var(--x-color-picker-border);"
   "border-radius:var(--x-color-picker-radius);"
   "padding:var(--x-color-picker-gap);"
   "display:flex;flex-direction:column;"
   "gap:var(--x-color-picker-gap);"
   "box-shadow:var(--x-shadow-md,0 4px 12px rgba(0,0,0,0.1));"
   "}"
   ;; In popover mode, the panel is absolutely positioned and hidden by default
   ":host([data-mode=popover]) [part=panel]{"
   "display:none;"
   "position:absolute;"
   "z-index:1000;"
   "top:calc(100% + 8px);"
   "left:0;"
   "max-width:calc(100vw - 1rem);"
   "}"
   ":host([data-mode=popover][open]) [part=panel]{display:flex;}"

   ;; ── Color area ────────────────────────────────────────────────────────
   "[part=area]{"
   "position:relative;"
   "width:100%;"
   "height:var(--x-color-picker-area-height);"
   "border-radius:calc(var(--x-color-picker-radius) - 2px);"
   "cursor:crosshair;"
   "touch-action:none;"
   "overflow:hidden;"
   "background:"
   "linear-gradient(to bottom,transparent,#000),"
   "linear-gradient(to right,#fff,hsl(var(--_x-cp-hue,0),100%,50%));"
   "}"
   "[part=area]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:2px;}"

   "[part=area-thumb]{"
   "position:absolute;"
   "width:16px;height:16px;"
   "border-radius:50%;"
   "border:2px solid #fff;"
   "box-shadow:0 0 0 1px rgba(0,0,0,0.3),inset 0 0 0 1px rgba(0,0,0,0.3);"
   "pointer-events:none;"
   "left:var(--_x-cp-area-x,0%);"
   "top:var(--_x-cp-area-y,0%);"
   "transform:translate(-50%,-50%);"
   "}"

   ;; ── Strips container ─────────────────────────────────────────────────
   "[part=strips]{"
   "display:flex;flex-direction:column;gap:8px;}"

   ;; ── Hue strip ─────────────────────────────────────────────────────────
   "[part=hue-strip]{"
   "position:relative;"
   "width:100%;"
   "height:var(--x-color-picker-strip-height);"
   "border-radius:var(--x-color-picker-strip-height);"
   "cursor:pointer;"
   "touch-action:none;"
   "background:linear-gradient(to right,"
   "#f00 0%,#ff0 17%,#0f0 33%,#0ff 50%,#00f 67%,#f0f 83%,#f00 100%);"
   "}"
   "[part=hue-strip]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:2px;}"

   "[part=hue-thumb]{"
   "position:absolute;"
   "width:16px;height:16px;"
   "border-radius:50%;"
   "border:2px solid #fff;"
   "box-shadow:0 1px 4px rgba(0,0,0,0.3);"
   "pointer-events:none;"
   "top:50%;"
   "left:var(--_x-cp-hue-pos,0%);"
   "transform:translate(-50%,-50%);"
   "}"

   ;; ── Alpha strip ───────────────────────────────────────────────────────
   "[part=alpha-strip]{"
   "position:relative;"
   "width:100%;"
   "height:var(--x-color-picker-strip-height);"
   "border-radius:var(--x-color-picker-strip-height);"
   "cursor:pointer;"
   "touch-action:none;"
   ;; checkerboard
   "background-image:"
   "linear-gradient(45deg,#ccc 25%,transparent 25%),"
   "linear-gradient(-45deg,#ccc 25%,transparent 25%),"
   "linear-gradient(45deg,transparent 75%,#ccc 75%),"
   "linear-gradient(-45deg,transparent 75%,#ccc 75%);"
   "background-size:12px 12px;"
   "background-position:0 0,0 6px,6px -6px,-6px 0;"
   "}"
   "[part=alpha-strip]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:2px;}"

   ;; overlay gradient for alpha
   "[part=alpha-gradient]{"
   "position:absolute;inset:0;"
   "border-radius:inherit;"
   "background:linear-gradient(to right,transparent,var(--_x-cp-alpha-color,#000));"
   "}"

   "[part=alpha-thumb]{"
   "position:absolute;"
   "width:16px;height:16px;"
   "border-radius:50%;"
   "border:2px solid #fff;"
   "box-shadow:0 1px 4px rgba(0,0,0,0.3);"
   "pointer-events:none;"
   "top:50%;"
   "left:var(--_x-cp-alpha-pos,100%);"
   "transform:translate(-50%,-50%);"
   "z-index:1;"
   "}"

   ;; ── Controls row ──────────────────────────────────────────────────────
   "[part=controls]{"
   "display:flex;align-items:center;gap:6px;}"

   "[part=preview]{"
   "width:32px;height:32px;"
   "border-radius:var(--x-color-picker-radius);"
   "border:1px solid var(--x-color-picker-border);"
   "flex-shrink:0;"
   ;; checkerboard for alpha preview
   "background-image:"
   "linear-gradient(45deg,#ccc 25%,transparent 25%),"
   "linear-gradient(-45deg,#ccc 25%,transparent 25%),"
   "linear-gradient(45deg,transparent 75%,#ccc 75%),"
   "linear-gradient(-45deg,transparent 75%,#ccc 75%);"
   "background-size:8px 8px;"
   "background-position:0 0,0 4px,4px -4px,-4px 0;"
   "overflow:hidden;"
   "}"

   "[part=preview-color]{"
   "display:block;width:100%;height:100%;}"

   "[part=hex-input]{"
   "flex:1;"
   "min-width:0;"
   "box-sizing:border-box;"
   "padding:4px 8px;"
   "border:1px solid var(--x-color-picker-border);"
   "border-radius:calc(var(--x-color-picker-radius) - 2px);"
   "background:transparent;"
   "color:var(--x-color-picker-text);"
   "font-family:var(--x-font-family,inherit);"
   "font-size:var(--x-font-size-sm,0.8125rem);"
   "outline:none;"
   "}"
   "[part=hex-input]:focus-visible{"
   "border-color:var(--x-color-picker-focus-ring);"
   "box-shadow:0 0 0 2px var(--x-color-picker-focus-ring);}"

   ;; Buttons
   "[part=eyedropper],[part=copy]{"
   "all:unset;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:28px;height:28px;"
   "border-radius:calc(var(--x-color-picker-radius) - 2px);"
   "cursor:pointer;"
   "color:var(--x-color-picker-text);"
   "flex-shrink:0;"
   "}"
   "[part=eyedropper]:hover,[part=copy]:hover{"
   "background:rgba(0,0,0,0.06);}"
   "@media (prefers-color-scheme:dark){"
   "[part=eyedropper]:hover,[part=copy]:hover{"
   "background:rgba(255,255,255,0.1);}}"
   "[part=eyedropper]:focus-visible,[part=copy]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:1px;}"

   ;; ── Swatches grid ─────────────────────────────────────────────────────
   "[part=swatches]{"
   "display:grid;"
   "grid-template-columns:repeat(auto-fill,var(--x-color-picker-swatch-size));"
   "gap:4px;}"
   "[part=swatches]:empty{display:none;}"

   "[part=swatch]{"
   "all:unset;"
   "width:var(--x-color-picker-swatch-size);"
   "height:var(--x-color-picker-swatch-size);"
   "border-radius:4px;"
   "cursor:pointer;"
   "border:1px solid rgba(0,0,0,0.1);"
   "box-sizing:border-box;"
   "}"
   "[part=swatch]:focus-visible{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:1px;}"
   "[part=swatch][aria-selected=true]{"
   "outline:2px solid var(--x-color-picker-focus-ring);"
   "outline-offset:1px;}"

   ;; ── Disabled ──────────────────────────────────────────────────────────
   ":host([disabled]){opacity:var(--x-color-picker-disabled-opacity);pointer-events:none;}"

   ;; ── Reduced motion ────────────────────────────────────────────────────
   "@media (prefers-reduced-motion:reduce){"
   "[part=area-thumb],[part=hue-thumb],[part=alpha-thumb]{"
   "transition:none;}}"

   ;; ── Coarse pointer — larger touch targets ─────────────────────────────
   "@media (pointer:coarse){"
   "[part=area-thumb],[part=hue-thumb],[part=alpha-thumb]{"
   "width:24px;height:24px;}"
   "[part=trigger]{width:44px;height:44px;}"
   "[part=eyedropper],[part=copy]{width:44px;height:44px;}"
   "[part=swatch]{min-width:44px;min-height:44px;}"
   "}"))

;; ---------------------------------------------------------------------------
;; SVG icon strings
;; ---------------------------------------------------------------------------
(def ^:private eyedropper-svg
  "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M13.5 2.5a2.12 2.12 0 0 0-3 0L9 4l-.5-.5L6 6l4 4 2.5-2.5L12 7l1.5-1.5a2.12 2.12 0 0 0 0-3zM6 6L2.5 9.5a1.5 1.5 0 0 0-.4.8L1.5 14l3.7-.6a1.5 1.5 0 0 0 .8-.4L10 10\"/></svg>")

(def ^:private copy-svg
  "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect x=\"5\" y=\"5\" width=\"9\" height=\"9\" rx=\"1.5\"/><path d=\"M5 11H3.5A1.5 1.5 0 0 1 2 9.5v-6A1.5 1.5 0 0 1 3.5 2h6A1.5 1.5 0 0 1 11 3.5V5\"/></svg>")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- decorate-strip-slider!
  "Apply the standard ARIA-slider decoration used by the hue and alpha
   strips: part, role, tabindex, aria-label, aria-valuemin/max."
  [^js strip part-name aria-label valuemax]
  (du/set-attr! strip "part" part-name)
  (du/set-attr! strip "role" "slider")
  (du/set-attr! strip "tabindex" "0")
  (du/set-attr! strip "aria-label" aria-label)
  (du/set-attr! strip "aria-valuemin" "0")
  (du/set-attr! strip "aria-valuemax" valuemax))

(defn- decorate-icon-button!
  "Decorate one of the inline icon buttons (eyedropper, copy)."
  [^js btn part-name aria-label svg]
  (du/set-attr! btn "part" part-name)
  (du/set-attr! btn "type" "button")
  (du/set-attr! btn "aria-label" aria-label)
  (set! (.-innerHTML btn) svg))

(defn- make-trigger!
  "Build the popover-mode trigger button + its inner colour swatch."
  []
  (let [trigger        (make-el "button")
        trigger-swatch (make-el "span")]
    (du/set-attr! trigger "part" "trigger")
    (du/set-attr! trigger "type" "button")
    (du/set-attr! trigger "aria-label" "Pick color")
    (du/set-attr! trigger-swatch "part" "trigger-swatch")
    (.appendChild trigger trigger-swatch)
    {:trigger trigger :trigger-swatch trigger-swatch}))

(defn- make-area-section!
  "Build the saturation/value area + its draggable thumb."
  []
  (let [area  (make-el "div")
        thumb (make-el "div")]
    (du/set-attr! area "part" "area")
    (du/set-attr! area "role" "slider")
    (du/set-attr! area "tabindex" "0")
    (du/set-attr! area "aria-label" "Color")
    (du/set-attr! thumb "part" "area-thumb")
    (.appendChild area thumb)
    {:area area :area-thumb thumb}))

(defn- make-strips-section!
  "Build the hue + alpha strip column."
  []
  (let [strips         (make-el "div")
        hue-strip      (make-el "div")
        hue-thumb      (make-el "div")
        alpha-strip    (make-el "div")
        alpha-gradient (make-el "div")
        alpha-thumb    (make-el "div")]
    (du/set-attr! strips "part" "strips")
    (decorate-strip-slider! hue-strip   "hue-strip"   "Hue"     "360")
    (decorate-strip-slider! alpha-strip "alpha-strip" "Opacity" "100")
    (du/set-attr! hue-thumb      "part" "hue-thumb")
    (du/set-attr! alpha-gradient "part" "alpha-gradient")
    (du/set-attr! alpha-thumb    "part" "alpha-thumb")
    (.appendChild hue-strip hue-thumb)
    (.appendChild alpha-strip alpha-gradient)
    (.appendChild alpha-strip alpha-thumb)
    (.appendChild strips hue-strip)
    (.appendChild strips alpha-strip)
    {:strips         strips
     :hue-strip      hue-strip      :hue-thumb      hue-thumb
     :alpha-strip    alpha-strip    :alpha-gradient alpha-gradient
     :alpha-thumb    alpha-thumb}))

(defn- make-controls-section!
  "Build the controls row: preview swatch, hex input, eyedropper, copy."
  []
  (let [controls       (make-el "div")
        preview        (make-el "div")
        preview-color  (make-el "span")
        hex-input      (make-el "input")
        eyedropper-btn (make-el "button")
        copy-btn       (make-el "button")]
    (du/set-attr! controls      "part" "controls")
    (du/set-attr! preview       "part" "preview")
    (du/set-attr! preview-color "part" "preview-color")
    (du/set-attr! hex-input     "part" "hex-input")
    (du/set-attr! hex-input     "type" "text")
    (du/set-attr! hex-input     "spellcheck" "false")
    (du/set-attr! hex-input     "autocomplete" "off")
    (du/set-attr! hex-input     "aria-label" "Hex color value")
    (decorate-icon-button! eyedropper-btn "eyedropper" "Pick color from screen" eyedropper-svg)
    (decorate-icon-button! copy-btn       "copy"       "Copy color value"      copy-svg)
    (when-not (exists? js/EyeDropper)
      (set! (.-display (.-style eyedropper-btn)) "none"))
    (.appendChild preview preview-color)
    (.appendChild controls preview)
    (.appendChild controls hex-input)
    (.appendChild controls eyedropper-btn)
    (.appendChild controls copy-btn)
    {:controls       controls
     :preview        preview        :preview-color preview-color
     :hex-input      hex-input
     :eyedropper     eyedropper-btn :copy          copy-btn}))

(defn- make-swatches-section!
  "Build the preset-colour swatch listbox."
  []
  (let [swatches (make-el "div")]
    (du/set-attr! swatches "part" "swatches")
    (du/set-attr! swatches "role" "listbox")
    (du/set-attr! swatches "aria-label" "Preset colors")
    {:swatches swatches}))

(defn- make-shadow! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style-el  (make-el "style")
        container (make-el "div")
        panel     (make-el "div")
        trig      (make-trigger!)
        area-sec  (make-area-section!)
        strips    (make-strips-section!)
        ctl       (make-controls-section!)
        sw        (make-swatches-section!)]
    (set! (.-textContent style-el) style-text)
    (du/set-attr! container "part" "container")
    (du/set-attr! panel     "part" "panel")
    (.appendChild panel (:area      area-sec))
    (.appendChild panel (:strips    strips))
    (.appendChild panel (:controls  ctl))
    (.appendChild panel (:swatches  sw))
    (.appendChild container (:trigger trig))
    (.appendChild container panel)
    (.appendChild root style-el)
    (.appendChild root container)
    (let [refs #js {:container      container
                    :trigger        (:trigger trig)
                    :trigger-swatch (:trigger-swatch trig)
                    :panel          panel
                    :area           (:area area-sec)
                    :area-thumb     (:area-thumb area-sec)
                    :hue-strip      (:hue-strip strips)
                    :hue-thumb      (:hue-thumb strips)
                    :alpha-strip    (:alpha-strip strips)
                    :alpha-gradient (:alpha-gradient strips)
                    :alpha-thumb    (:alpha-thumb strips)
                    :controls       (:controls ctl)
                    :preview        (:preview ctl)
                    :preview-color  (:preview-color ctl)
                    :hex-input      (:hex-input ctl)
                    :eyedropper     (:eyedropper ctl)
                    :copy           (:copy ctl)
                    :swatches       (:swatches sw)}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read state from element attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/derive-state
   {:value    (du/get-attr el model/attr-value)
    :alpha    (du/get-attr el model/attr-alpha)
    :swatches (du/get-attr el model/attr-swatches)
    :disabled (du/get-attr el model/attr-disabled)
    :readonly (du/get-attr el model/attr-readonly)
    :name     (du/get-attr el model/attr-name)
    :mode     (du/get-attr el model/attr-mode)
    :open     (du/get-attr el model/attr-open)
    :label    (du/get-attr el model/attr-label)}))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
(defn- dispatch-input! [^js el m]
  (du/dispatch! el model/event-input
                (clj->js (model/make-detail (:hex-full m) (:h m) (:s m) (:l m) (:a m)))))

(defn- dispatch-change! [^js el m]
  (du/dispatch-cancelable! el model/event-change
                           (clj->js (model/make-detail (:hex-full m) (:h m) (:s m) (:l m) (:a m)))))

;; ---------------------------------------------------------------------------
;; Value update helper
;; ---------------------------------------------------------------------------
(defn- set-value-from-hsl!
  "Update the element's value attribute from HSL+A values."
  [^js el h s l a alpha?]
  (let [{:keys [r g b]} (model/hsl->rgb h s l)
        hex (if (and alpha? (< a 1.0))
              (model/rgba->hex8 r g b a)
              (model/rgb->hex r g b))]
    (du/set-attr! el model/attr-value hex)))

;; ---------------------------------------------------------------------------
;; Render swatches
;; ---------------------------------------------------------------------------
(defn- render-swatches! [^js el ^js swatches-el swatch-list current-hex]
  (let [cached (du/getv el k-swatches-cache)]
    ;; Only rebuild if swatches changed
    (when (not= cached swatch-list)
      (du/setv! el k-swatches-cache swatch-list)
      (set! (.-innerHTML swatches-el) "")
      (doseq [hex swatch-list]
        (let [btn (make-el "button")]
          (du/set-attr! btn "part" "swatch")
          (du/set-attr! btn "type" "button")
          (du/set-attr! btn "role" "option")
          (du/set-attr! btn "aria-label" hex)
          (du/set-attr! btn "data-color" hex)
          (set! (.-backgroundColor (.-style btn)) hex)
          (.appendChild swatches-el btn)))))
  ;; Update aria-selected on all swatch buttons
  (let [buttons (.querySelectorAll swatches-el "[part=swatch]")]
    (.forEach buttons
              (fn [^js btn]
                (du/set-attr! btn "aria-selected"
                           (if (= (.getAttribute btn "data-color") current-hex)
                             "true" "false"))))))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- swatch-color
  "Pure: produce the CSS background-color string for the preview / trigger
   swatches. Uses rgba() when an alpha channel is in play, otherwise the
   bare hex string keeps theme-aware compositing simple."
  [{:keys [h s l a hex alpha?]}]
  (if alpha?
    (let [{:keys [r g b]} (model/hsl->rgb h s l)]
      (str "rgba(" r "," g "," b "," (.toFixed a 2) ")"))
    hex))

(defn- apply-host-css-vars!
  "Mirror mode + the canonical hue onto the host: data-mode for CSS
   selectors, --_x-cp-hue for the area's gradient backdrop."
  [^js el {:keys [h mode]}]
  (du/set-attr! el "data-mode" mode)
  (.setProperty (.-style el) "--_x-cp-hue" (str (js/Math.round h))))

(defn- apply-area!
  "Update the saturation/value area: thumb position via CSS vars + ARIA
   announcement based on the current colour."
  [^js area-el {:keys [h s l a label]}]
  (let [{:keys [s v]} (model/hsl->hsv h s l)
        area-pos (model/sat-val->xy-pct s v)]
    (.setProperty (.-style area-el) "--_x-cp-area-x" (str (.toFixed (:x area-pos) 2) "%"))
    (.setProperty (.-style area-el) "--_x-cp-area-y" (str (.toFixed (:y area-pos) 2) "%"))
    (du/set-attr! area-el "aria-valuetext" (model/color-value-text h s l a))
    (when label (du/set-attr! area-el "aria-label" label))))

(defn- apply-hue-strip!
  "Update the hue strip's thumb position and ARIA value."
  [^js hue-strip {:keys [h]}]
  (.setProperty (.-style hue-strip) "--_x-cp-hue-pos"
                (str (.toFixed (model/hue->pct h) 2) "%"))
  (du/set-attr! hue-strip "aria-valuenow" (str (js/Math.round h))))

(defn- apply-alpha-strip!
  "Show or hide the alpha strip based on the model's alpha? flag.
   When shown, mirror the live colour and the alpha-thumb position."
  [^js el ^js alpha-strip {:keys [h s l a alpha?]}]
  (if alpha?
    (do (set! (.-display (.-style alpha-strip)) "")
        (.setProperty (.-style el) "--_x-cp-alpha-color"
                      (str "hsl(" (js/Math.round h) ","
                                  (js/Math.round s) "%,"
                                  (js/Math.round l) "%)"))
        (.setProperty (.-style alpha-strip) "--_x-cp-alpha-pos"
                      (str (.toFixed (model/alpha->pct a) 2) "%"))
        (du/set-attr! alpha-strip "aria-valuenow" (str (js/Math.round (* a 100)))))
    (set! (.-display (.-style alpha-strip)) "none")))

(defn- apply-preview-and-trigger!
  "Apply the swatch background to both the panel preview and the
   popover trigger. Same colour, same logic, two targets."
  [^js preview-color ^js trigger-swatch m]
  (let [bg (swatch-color m)]
    (set! (.-backgroundColor (.-style preview-color)) bg)
    (set! (.-backgroundColor (.-style trigger-swatch)) bg)))

(defn- apply-hex-input!
  "Sync the hex input's value (unless the user is mid-edit) and its
   disabled flag. The activeElement guard keeps the cursor stable
   while typing."
  [^js el ^js hex-input {:keys [hex-full disabled? readonly?]}]
  (let [^js active (.-activeElement (.-shadowRoot el))]
    (when (not (identical? active hex-input))
      (set! (.-value hex-input) hex-full)))
  (set! (.-disabled hex-input) (or disabled? readonly?)))

(defn- apply-form-value!
  "Mirror the hex (or hex-with-alpha) value into ElementInternals so
   form serialization reflects the current colour."
  [^js el {:keys [hex-full]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals hex-full)))

(defn- apply-model!
  "Project the cached model onto the shadow DOM. Caches the model at the
  tail so partial-write failures don't leave a lying cache. Called by
  update-from-attrs! and by handlers that mutate UI state imperatively
  (e.g. drag, form-reset)."
  [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js area-el        (gobj/get refs "area")
          ^js hue-strip      (gobj/get refs "hue-strip")
          ^js alpha-strip    (gobj/get refs "alpha-strip")
          ^js preview-color  (gobj/get refs "preview-color")
          ^js trigger-swatch (gobj/get refs "trigger-swatch")
          ^js hex-input      (gobj/get refs "hex-input")
          ^js swatches-el    (gobj/get refs "swatches")]
      (apply-host-css-vars!       el m)
      (apply-area!                area-el m)
      (apply-hue-strip!           hue-strip m)
      (apply-alpha-strip!         el alpha-strip m)
      (apply-preview-and-trigger! preview-color trigger-swatch m)
      (apply-hex-input!           el hex-input m)
      (render-swatches!           el swatches-el (:swatches m) (:hex m))
      (apply-form-value!          el m)
      (du/setv! el k-model m))))

(defn- update-from-attrs!
  "Read → diff → apply. Skips the apply when the normalised model is
  unchanged from the cached value."
  [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ---------------------------------------------------------------------------
;; Pointer interaction — area
;; ---------------------------------------------------------------------------
(defn- pointer-pos->area-vals
  "Read the pointer's client coords and the area element's rect, then
   delegate to the pure model transform."
  [^js area-el ^js evt]
  (let [^js rect (.getBoundingClientRect area-el)]
    (model/client-xy->sat-val (.-clientX evt) (.-clientY evt)
                              (.-left rect)   (.-top rect)
                              (.-width rect)  (.-height rect))))

(defn- pointer-pos->hue
  [^js strip-el ^js evt]
  (let [^js rect (.getBoundingClientRect strip-el)]
    (model/client-x->hue (.-clientX evt) (.-left rect) (.-width rect))))

(defn- pointer-pos->alpha
  [^js strip-el ^js evt]
  (let [^js rect (.getBoundingClientRect strip-el)]
    (model/client-x->alpha (.-clientX evt) (.-left rect) (.-width rect))))

;; ---------------------------------------------------------------------------
;; Pointer handlers
;; ---------------------------------------------------------------------------
(defn- on-area-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (model/interactable? m)
      (.preventDefault evt)
      (let [^js area-el (gobj/get (du/getv el k-refs) "area")]
        (.setPointerCapture area-el (.-pointerId evt))
        (du/setv! el k-dragging "area")
        (let [{:keys [sat val]} (pointer-pos->area-vals area-el evt)
              {:keys [h s l]} (model/hsv->hsl (:h m) sat val)]
          (set-value-from-hsl! el h s l (:a m) (:alpha? m))
          (dispatch-input! el (read-model el)))))))

(defn- on-area-pointermove! [^js el ^js evt]
  (when (= "area" (du/getv el k-dragging))
    (let [m (read-model el)
          ^js area-el (gobj/get (du/getv el k-refs) "area")
          {:keys [sat val]} (pointer-pos->area-vals area-el evt)
          {:keys [h s l]} (model/hsv->hsl (:h m) sat val)]
      (set-value-from-hsl! el h s l (:a m) (:alpha? m))
      (dispatch-input! el (read-model el)))))

(defn- on-area-pointerup! [^js el ^js _evt]
  (when (= "area" (du/getv el k-dragging))
    (du/setv! el k-dragging nil)
    (dispatch-change! el (read-model el))))

(defn- on-hue-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (model/interactable? m)
      (.preventDefault evt)
      (let [^js strip-el (gobj/get (du/getv el k-refs) "hue-strip")]
        (.setPointerCapture strip-el (.-pointerId evt))
        (du/setv! el k-dragging "hue")
        (let [h (pointer-pos->hue strip-el evt)]
          ;; Keep same saturation/lightness, change hue only
          (set-value-from-hsl! el h (:s m) (:l m) (:a m) (:alpha? m))
          (dispatch-input! el (read-model el)))))))

(defn- on-hue-pointermove! [^js el ^js evt]
  (when (= "hue" (du/getv el k-dragging))
    (let [m (read-model el)
          ^js strip-el (gobj/get (du/getv el k-refs) "hue-strip")
          h (pointer-pos->hue strip-el evt)]
      (set-value-from-hsl! el h (:s m) (:l m) (:a m) (:alpha? m))
      (dispatch-input! el (read-model el)))))

(defn- on-hue-pointerup! [^js el ^js _evt]
  (when (= "hue" (du/getv el k-dragging))
    (du/setv! el k-dragging nil)
    (dispatch-change! el (read-model el))))

(defn- on-alpha-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (model/interactable? m) (:alpha? m))
      (.preventDefault evt)
      (let [^js strip-el (gobj/get (du/getv el k-refs) "alpha-strip")]
        (.setPointerCapture strip-el (.-pointerId evt))
        (du/setv! el k-dragging "alpha")
        (let [a (pointer-pos->alpha strip-el evt)]
          (set-value-from-hsl! el (:h m) (:s m) (:l m) a true)
          (dispatch-input! el (read-model el)))))))

(defn- on-alpha-pointermove! [^js el ^js evt]
  (when (= "alpha" (du/getv el k-dragging))
    (let [m (read-model el)
          ^js strip-el (gobj/get (du/getv el k-refs) "alpha-strip")
          a (pointer-pos->alpha strip-el evt)]
      (set-value-from-hsl! el (:h m) (:s m) (:l m) a true)
      (dispatch-input! el (read-model el)))))

(defn- on-alpha-pointerup! [^js el ^js _evt]
  (when (= "alpha" (du/getv el k-dragging))
    (du/setv! el k-dragging nil)
    (dispatch-change! el (read-model el))))

;; ---------------------------------------------------------------------------
;; Keyboard handlers
;; ---------------------------------------------------------------------------
(def ^:private area-keys
  #{"ArrowUp" "ArrowDown" "ArrowLeft" "ArrowRight"})

(def ^:private strip-keys
  #{"ArrowLeft" "ArrowRight" "Home" "End"})

(defn- on-area-keydown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (model/interactable? m) (contains? area-keys (.-key evt)))
      (.preventDefault evt)
      (let [step (if (.-shiftKey evt) 10 1)
            hsv (model/hsl->hsv (:h m) (:s m) (:l m))
            new-s (case (.-key evt)
                    "ArrowRight" (model/clamp-percent (+ (:s hsv) step))
                    "ArrowLeft"  (model/clamp-percent (- (:s hsv) step))
                    (:s hsv))
            new-v (case (.-key evt)
                    "ArrowUp"   (model/clamp-percent (+ (:v hsv) step))
                    "ArrowDown" (model/clamp-percent (- (:v hsv) step))
                    (:v hsv))
            {:keys [h s l]} (model/hsv->hsl (:h m) new-s new-v)]
        (set-value-from-hsl! el h s l (:a m) (:alpha? m))
        (dispatch-input! el (read-model el))
        (dispatch-change! el (read-model el))))))

(defn- on-hue-keydown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (model/interactable? m) (contains? strip-keys (.-key evt)))
      (.preventDefault evt)
      (let [step (if (.-shiftKey evt) 10 1)
            new-h (case (.-key evt)
                    "ArrowRight" (model/clamp-hue (+ (:h m) step))
                    "ArrowLeft"  (model/clamp-hue (- (:h m) step))
                    "Home"       0
                    "End"        360
                    (:h m))]
        (set-value-from-hsl! el new-h (:s m) (:l m) (:a m) (:alpha? m))
        (dispatch-input! el (read-model el))
        (dispatch-change! el (read-model el))))))

(defn- on-alpha-keydown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (model/interactable? m) (:alpha? m) (contains? strip-keys (.-key evt)))
      (.preventDefault evt)
      (let [step (if (.-shiftKey evt) 0.1 0.01)
            new-a (case (.-key evt)
                    "ArrowRight" (model/clamp-alpha (+ (:a m) step))
                    "ArrowLeft"  (model/clamp-alpha (- (:a m) step))
                    "Home"       0.0
                    "End"        1.0
                    (:a m))]
        (set-value-from-hsl! el (:h m) (:s m) (:l m) new-a true)
        (dispatch-input! el (read-model el))
        (dispatch-change! el (read-model el))))))

;; ---------------------------------------------------------------------------
;; Hex input handler
;; ---------------------------------------------------------------------------
(defn- on-hex-input-keydown! [^js el ^js evt]
  (when (= "Enter" (.-key evt))
    (.preventDefault evt)
    (let [^js hex-input (gobj/get (du/getv el k-refs) "hex-input")
          raw (.-value hex-input)
          m (read-model el)]
      (when (model/interactable? m)
        (if (model/valid-hex? raw)
          (do (du/set-attr! el model/attr-value raw)
              (dispatch-change! el (read-model el)))
          ;; Revert to current value on invalid input
          (set! (.-value hex-input) (:hex-full m)))))))

;; ---------------------------------------------------------------------------
;; Swatch click handler
;; ---------------------------------------------------------------------------
(defn- on-swatches-click! [^js el ^js evt]
  (let [m (read-model el)]
    (when (model/interactable? m)
      (let [^js target (.-target evt)]
        (when (.hasAttribute target "data-color")
          (let [hex (.getAttribute target "data-color")]
            (du/set-attr! el model/attr-value hex)
            (dispatch-change! el (read-model el))))))))

;; ---------------------------------------------------------------------------
;; Eyedropper handler
;; ---------------------------------------------------------------------------
(defn- on-eyedropper-click! [^js el ^js _evt]
  (let [m (read-model el)]
    (when (and (model/interactable? m) (exists? js/EyeDropper))
      (let [^js picker (js/EyeDropper.)]
        (-> (.open picker)
            (.then (fn [^js result]
                     (let [hex (.-sRGBHex result)]
                       (du/set-attr! el model/attr-value hex)
                       (dispatch-change! el (read-model el)))))
            (.catch (fn [_] nil)))))))

;; ---------------------------------------------------------------------------
;; Copy handler
;; ---------------------------------------------------------------------------
(defn- on-copy-click! [^js el ^js _evt]
  (let [m (read-model el)]
    (when (.-clipboard js/navigator)
      (.writeText (.-clipboard js/navigator) (:hex-full m)))))

;; ---------------------------------------------------------------------------
;; Popover handlers
;; ---------------------------------------------------------------------------
(defn- open-popover! [^js el]
  (when-not (du/has-attr? el model/attr-open)
    (du/set-attr! el model/attr-open "")
    ;; Focus the area after opening
    (when-let [refs (du/getv el k-refs)]
      (let [^js area-el (gobj/get refs "area")]
        (js/setTimeout #(.focus area-el) 0)))))

(defn- close-popover! [^js el]
  (when (du/has-attr? el model/attr-open)
    (du/remove-attr! el model/attr-open)
    ;; Return focus to trigger
    (when-let [refs (du/getv el k-refs)]
      (let [^js trigger (gobj/get refs "trigger")]
        (.focus trigger)))))

(defn- on-trigger-click! [^js el ^js _evt]
  (let [m (read-model el)]
    (when (and (= "popover" (:mode m)) (not (:disabled? m)))
      (if (:open? m)
        (close-popover! el)
        (open-popover! el)))))

(defn- on-panel-keydown! [^js el ^js evt]
  (when (and (= "Escape" (.-key evt))
             (= "popover" (:mode (read-model el))))
    (.preventDefault evt)
    (.stopPropagation evt)
    (close-popover! el)))

(defn- on-panel-pointerdown! [^js _el ^js evt]
  ;; Prevent focus stealing from panel interactions
  (.stopPropagation evt))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- on-doc-pointerdown!
  "Close the popover when a pointerdown lands outside the host."
  [^js el ^js e]
  (when (du/has-attr? el model/attr-open)
    (when-not (.some (.composedPath e)
                     (fn [^js n] (identical? n el)))
      (close-popover! el))))

(defn- build-all-handlers
  "Build the per-instance closure map. The keys map 1:1 onto the
   `listener-spec` table; both functions iterate that table to pair
   add/remove without drift."
  [^js el]
  #js {:area-down  (fn [e] (on-area-pointerdown!   el e))
       :area-move  (fn [e] (on-area-pointermove!   el e))
       :area-up    (fn [e] (on-area-pointerup!     el e))
       :area-key   (fn [e] (on-area-keydown!       el e))
       :hue-down   (fn [e] (on-hue-pointerdown!    el e))
       :hue-move   (fn [e] (on-hue-pointermove!    el e))
       :hue-up     (fn [e] (on-hue-pointerup!      el e))
       :hue-key    (fn [e] (on-hue-keydown!        el e))
       :alpha-down (fn [e] (on-alpha-pointerdown!  el e))
       :alpha-move (fn [e] (on-alpha-pointermove!  el e))
       :alpha-up   (fn [e] (on-alpha-pointerup!    el e))
       :alpha-key  (fn [e] (on-alpha-keydown!      el e))
       :hex-key    (fn [e] (on-hex-input-keydown!  el e))
       :swatch     (fn [e] (on-swatches-click!     el e))
       :eyedrop    (fn [e] (on-eyedropper-click!   el e))
       :copy       (fn [e] (on-copy-click!         el e))
       :trigger    (fn [e] (on-trigger-click!      el e))
       :panel-key  (fn [e] (on-panel-keydown!      el e))
       :panel-pd   (fn [e] (on-panel-pointerdown!  el e))
       :doc-click  (fn [e] (on-doc-pointerdown!    el e))})

(def ^:private listener-spec
  "Single source of truth for static listener wiring. Each entry is
   [refs-key event handler-key capture?]. `:document` is the literal
   document. add-listeners! and remove-listeners! both iterate this
   vector so they cannot drift apart."
  [["area"        "pointerdown" "area-down"  false]
   ["area"        "pointermove" "area-move"  false]
   ["area"        "pointerup"   "area-up"    false]
   ["area"        "keydown"     "area-key"   false]
   ["hue-strip"   "pointerdown" "hue-down"   false]
   ["hue-strip"   "pointermove" "hue-move"   false]
   ["hue-strip"   "pointerup"   "hue-up"     false]
   ["hue-strip"   "keydown"     "hue-key"    false]
   ["alpha-strip" "pointerdown" "alpha-down" false]
   ["alpha-strip" "pointermove" "alpha-move" false]
   ["alpha-strip" "pointerup"   "alpha-up"   false]
   ["alpha-strip" "keydown"     "alpha-key"  false]
   ["hex-input"   "keydown"     "hex-key"    false]
   ["swatches"    "click"       "swatch"     false]
   ["eyedropper"  "click"       "eyedrop"    false]
   ["copy"        "click"       "copy"       false]
   ["trigger"     "click"       "trigger"    false]
   ["panel"       "keydown"     "panel-key"  false]
   ["panel"       "pointerdown" "panel-pd"   false]
   [:document     "pointerdown" "doc-click"  true]])

(defn- resolve-listener-target [^js refs target-key]
  (if (= target-key :document)
    js/document
    (gobj/get refs target-key)))

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [handlers (build-all-handlers el)]
      (doseq [[target-key event handler-key capture?] listener-spec]
        (.addEventListener (resolve-listener-target refs target-key)
                           event
                           (gobj/get handlers handler-key)
                           capture?))
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (let [refs     (du/getv el k-refs)
        handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (doseq [[target-key event handler-key capture?] listener-spec]
        (.removeEventListener (resolve-listener-target refs target-key)
                              event
                              (gobj/get handlers handler-key)
                              capture?))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (update-from-attrs! el))

(defn- form-reset! [^js el]
  (du/set-attr! el model/attr-value model/default-value)
  (update-from-attrs! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-disabled-fn       form-disabled!
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
