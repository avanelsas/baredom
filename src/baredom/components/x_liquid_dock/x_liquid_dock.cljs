(ns baredom.components.x-liquid-dock.x-liquid-dock
  (:require
[baredom.utils.component :as component]
               [goog.object :as gobj]
   [baredom.components.x-liquid-dock.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs         "__xLiquidDockRefs")
(def ^:private k-raf          "__xLiquidDockRaf")
(def ^:private k-model        "__xLiquidDockModel")
(def ^:private k-mouse        "__xLiquidDockMouse")
(def ^:private k-blobs        "__xLiquidDockBlobs")
(def ^:private k-blob-state   "__xLiquidDockBlobSt")
(def ^:private k-items-rect   "__xLiquidDockItemsR")
(def ^:private k-handlers     "__xLiquidDockHdl")
(def ^:private k-uid          "__xLiquidDockUid")
(def ^:private k-noise-seed   "__xLiquidDockSeed")
(def ^:private k-ro           "__xLiquidDockRO")
(def ^:private k-focus-idx    "__xLiquidDockFocus")
(def ^:private k-ripple-burst "__xLiquidDockBurst")

;; ── UID counter ─────────────────────────────────────────────────────────────
(def ^:private uid-counter #js [0])

(defn- next-uid []
  (let [n (aget uid-counter 0)]
    (aset uid-counter 0 (inc n))
    n))

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ;; ── Host ──
   ":host{"
   "display:block;"
   "position:fixed;"
   "z-index:var(" model/css-z-index ",50);"
   "pointer-events:auto;"
   model/css-color ":var(--x-color-primary,#6366f1);"
   "}"

   ;; ── Position variants ──
   ":host(:not([position])),"
   ":host([position=\"bottom\"]){"
   "bottom:12px;left:50%;transform:translateX(-50%);}"

   ":host([position=\"top\"]){"
   "top:12px;left:50%;transform:translateX(-50%);}"

   ":host([position=\"left\"]){"
   "left:12px;top:50%;transform:translateY(-50%);}"

   ":host([position=\"right\"]){"
   "right:12px;top:50%;transform:translateY(-50%);}"

   ;; ── Dock ──
   "[part=dock]{"
   "display:flex;"
   "align-items:center;"
   "gap:var(" model/css-gap ",8px);"
   "padding:var(" model/css-padding ",8px);"
   "border-radius:var(" model/css-radius ",20px);"
   "background:var(" model/css-bg ",rgba(255,255,255,0.88));"
   "border:1px solid var(" model/css-border ",rgba(148,163,184,0.22));"
   "box-shadow:var(" model/css-shadow ",0 8px 24px rgba(15,23,42,0.12));"
   "backdrop-filter:blur(14px);"
   "-webkit-backdrop-filter:blur(14px);"
   "position:relative;"
   "outline:none;"
   "}"

   ;; ── Vertical layout ──
   ":host([position=\"left\"]) [part=dock],"
   ":host([position=\"right\"]) [part=dock]{"
   "flex-direction:column;}"

   ;; ── SVG (hidden, holds filter defs) ──
   "[part=filter-svg]{"
   "position:absolute;"
   "width:0;height:0;"
   "overflow:hidden;"
   "pointer-events:none;"
   "}"

   ;; ── Liquid layer (blobs with SVG gooey filter) ──
   "[part=liquid-layer]{"
   "position:absolute;"
   "inset:0;"
   "border-radius:inherit;"
   "overflow:hidden;"
   "pointer-events:none;"
   "}"

   ;; ── Blob base ──
   ".blob{"
   "position:absolute;"
   "border-radius:50%;"
   "will-change:transform;"
   "pointer-events:none;"
   "background:var(" model/css-color ",#6366f1);"
   "top:0;left:0;"
   "}"

   ;; ── Rest blob: the liquid mass at resting position ──
   ".blob-rest{"
   "opacity:0.65;"
   "}"

   ;; ── Phantom blob: rises on hover to create viscous neck ──
   ".blob-phantom{"
   "opacity:0.5;"
   "}"

   ;; ── Items layer (above liquid, holds slotted content) ──
   "[part=items]{"
   "display:flex;"
   "align-items:center;"
   "gap:inherit;"
   "position:relative;"
   "z-index:1;"
   "}"

   ":host([position=\"left\"]) [part=items],"
   ":host([position=\"right\"]) [part=items]{"
   "flex-direction:column;}"

   ;; ── Slotted items ──
   "::slotted(*){"
   "width:var(" model/css-item-size ",48px);"
   "height:var(" model/css-item-size ",48px);"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "cursor:pointer;"
   "will-change:transform;"
   "position:relative;"
   "z-index:1;"
   "border:none;"
   "background:transparent;"
   "color:inherit;"
   "font-size:1.4em;"
   "transition:none;"
   "border-radius:50%;"
   "}"

   "::slotted(:hover){"
   "background:color-mix(in srgb,var(" model/css-color ",#6366f1) 12%,transparent);"
   "}"

   "::slotted(:focus-visible){"
   "outline:2px solid var(" model/css-color ",#6366f1);"
   "outline-offset:2px;"
   "}"

   ;; ── Dark mode ──
   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-liquid-dock-bg:rgba(15,23,42,0.84);"
   "--x-liquid-dock-border:rgba(51,65,85,0.9);"
   "--x-liquid-dock-shadow:var(--x-shadow-lg,0 14px 36px rgba(0,0,0,0.35));"
   "--x-liquid-dock-color:var(--x-color-primary,#818cf8);"
   "}}"

   ;; ── Reduced motion ──
   "@media(prefers-reduced-motion:reduce){"
   ".blob{transition:none!important;}"
   "::slotted(*){transition:none!important;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── SVG filter creation ─────────────────────────────────────────────────────
(defn- create-svg-filter!
  "Creates the gooey SVG filter chain and appends it to the SVG defs.
   Returns a map of element refs for later attribute updates."
  [^js svg filter-id {:keys [blur threshold ripple-scale]}]
  (let [^js defs      (.createElementNS js/document svg-ns "defs")
        ^js filter-el (.createElementNS js/document svg-ns "filter")
        ^js blur-el   (.createElementNS js/document svg-ns "feGaussianBlur")
        ^js matrix-el (.createElementNS js/document svg-ns "feColorMatrix")
        ^js turb-el   (.createElementNS js/document svg-ns "feTurbulence")
        ^js disp-el   (.createElementNS js/document svg-ns "feDisplacementMap")]

    ;; Filter bounds — oversized to prevent clipping
    (.setAttribute filter-el "id" filter-id)
    (.setAttribute filter-el "x" "-20%")
    (.setAttribute filter-el "y" "-20%")
    (.setAttribute filter-el "width" "140%")
    (.setAttribute filter-el "height" "140%")

    ;; Stage 1: Gaussian blur (creates the gooey merge)
    (.setAttribute blur-el "in" "SourceGraphic")
    (.setAttribute blur-el "stdDeviation" (str blur))
    (.setAttribute blur-el "result" "blur")

    ;; Stage 2: Color matrix threshold (sharpens merged blobs)
    (.setAttribute matrix-el "in" "blur")
    (.setAttribute matrix-el "type" "matrix")
    (.setAttribute matrix-el "values" threshold)
    (.setAttribute matrix-el "result" "goo")

    ;; Stage 3: Turbulence (for ripple displacement)
    (.setAttribute turb-el "type" "fractalNoise")
    (.setAttribute turb-el "baseFrequency" "0.015")
    (.setAttribute turb-el "numOctaves" "2")
    (.setAttribute turb-el "seed" "0")
    (.setAttribute turb-el "result" "noise")

    ;; Stage 4: Displacement map (ripple effect on the goo)
    (.setAttribute disp-el "in" "goo")
    (.setAttribute disp-el "in2" "noise")
    (.setAttribute disp-el "scale" (str ripple-scale))
    (.setAttribute disp-el "xChannelSelector" "R")
    (.setAttribute disp-el "yChannelSelector" "G")

    (.appendChild filter-el blur-el)
    (.appendChild filter-el matrix-el)
    (.appendChild filter-el turb-el)
    (.appendChild filter-el disp-el)
    (.appendChild defs filter-el)
    (.appendChild svg defs)

    {:filter-el filter-el
     :blur-el   blur-el
     :matrix-el matrix-el
     :turb-el   turb-el
     :disp-el   disp-el}))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [m         (gobj/get el k-model)
        uid       (next-uid)
        filter-id (str "goo-" uid)
        root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        nav       (.createElement js/document "nav")
        svg       (.createElementNS js/document svg-ns "svg")
        liquid    (.createElement js/document "div")
        items-div (.createElement js/document "div")
        slot-el   (.createElement js/document "slot")]

    (gobj/set el k-uid uid)

    (set! (.-textContent style) style-text)

    ;; Nav — dock container
    (.setAttribute nav "part" "dock")
    (.setAttribute nav "role" "navigation")
    (.setAttribute nav "aria-label" "Navigation dock")

    ;; Hidden SVG for filter definitions
    (.setAttribute svg "part" "filter-svg")
    (.setAttribute svg "aria-hidden" "true")
    (.setAttribute svg "role" "presentation")

    ;; Liquid layer
    (.setAttribute liquid "part" "liquid-layer")
    (set! (.. liquid -style -filter) (str "url(#" filter-id ")"))

    ;; Items layer
    (.setAttribute items-div "part" "items")
    (.appendChild items-div slot-el)

    (let [filter-refs (create-svg-filter! svg filter-id m)]
      (.appendChild nav svg)
      (.appendChild nav liquid)
      (.appendChild nav items-div)
      (.appendChild root style)
      (.appendChild root nav)

      (gobj/set el k-refs
                (merge filter-refs
                       {:root      root
                        :nav       nav
                        :svg       svg
                        :liquid    liquid
                        :items-div items-div
                        :slot      slot-el}))))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs)))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:position-raw        (.getAttribute el model/attr-position)
    :gap-raw             (.getAttribute el model/attr-gap)
    :blur-raw            (.getAttribute el model/attr-blur)
    :threshold-raw       (.getAttribute el model/attr-threshold)
    :ripple-scale-raw    (.getAttribute el model/attr-ripple-scale)
    :ripple-speed-raw    (.getAttribute el model/attr-ripple-speed)
    :color-raw           (.getAttribute el model/attr-color)
    :magnet-radius-raw   (.getAttribute el model/attr-magnet-radius)
    :magnet-strength-raw (.getAttribute el model/attr-magnet-strength)
    :bob-intensity-raw   (.getAttribute el model/attr-bob-intensity)
    :disabled-attr       (.getAttribute el model/attr-disabled)}))

;; ── Blob management ─────────────────────────────────────────────────────────
;; Each slotted item gets TWO blobs:
;;   blob[i*2]   = rest blob (stationary at item center, liquid mass)
;;   blob[i*2+1] = phantom blob (rises on hover, creates viscous neck)

(defn- get-assigned-elements [^js el]
  (let [refs (gobj/get el k-refs)]
    (when-let [^js slot (:slot refs)]
      (.assignedElements slot))))

(defn- reconcile-blobs!
  "Ensure two blob divs per slotted child (rest + phantom). Add/remove as needed."
  [^js el]
  (let [{:keys [liquid]} (gobj/get el k-refs)
        ^js liquid      liquid
        ^js assigned    (or (get-assigned-elements el) #js [])
        n               (.-length assigned)
        needed          (* n 2)
        ^js blob-arr    (or (gobj/get el k-blobs) #js [])
        ^js state-arr   (or (gobj/get el k-blob-state) #js [])
        current         (.-length blob-arr)
        rest-size       52   ;; slightly larger than item for liquid mass feel
        phantom-size    44]  ;; slightly smaller for the rising phantom

    ;; Add missing blobs
    (loop [i current]
      (when (< i needed)
        (let [div   (.createElement js/document "div")
              rest? (even? i)]
          (set! (.-className div) (if rest? "blob blob-rest" "blob blob-phantom"))
          (let [size (if rest? rest-size phantom-size)]
            (set! (.. div -style -width) (str size "px"))
            (set! (.. div -style -height) (str size "px")))
          (.appendChild liquid div)
          (.push blob-arr div)
          ;; State: per-item pair. Rest blobs don't need much state.
          ;; Phantom blobs track current lerped offset.
          (.push state-arr (if rest?
                             #js {:type "rest"}
                             #js {:type "phantom"
                                  :cpDx 0 :cpDy 0 :cpScale 0.6
                                  :cLift 0 :cRot 0 :cScale 1.0})))
        (recur (inc i))))

    ;; Remove excess blobs
    (loop [i current]
      (when (> i needed)
        (let [^js div (.pop blob-arr)]
          (.pop state-arr)
          (when (.-parentNode div)
            (.removeChild (.-parentNode div) div)))
        (recur (dec i))))

    (gobj/set el k-blobs blob-arr)
    (gobj/set el k-blob-state state-arr))
  nil)

;; ── Item position caching ───────────────────────────────────────────────────
(defn- cache-item-rects!
  "Cache the center position of each slotted item relative to the dock."
  [^js el]
  (let [{:keys [nav]} (gobj/get el k-refs)
        ^js nav       nav
        ^js assigned  (or (get-assigned-elements el) #js [])
        n             (.-length assigned)
        ^js rects     #js []]
    (when (and nav (pos? n))
      (let [^js dock-rect (.getBoundingClientRect nav)]
        (dotimes [i n]
          (let [^js item      (aget assigned i)
                ^js item-rect (.getBoundingClientRect item)
                cx            (- (+ (.-left item-rect) (/ (.-width item-rect) 2))
                                 (.-left dock-rect))
                cy            (- (+ (.-top item-rect) (/ (.-height item-rect) 2))
                                 (.-top dock-rect))]
            (.push rects #js {:cx cx :cy cy})))))
    (gobj/set el k-items-rect rects))
  nil)

;; ── Filter updates ──────────────────────────────────────────────────────────
(defn- update-filter!
  "Updates SVG filter elements to reflect current model."
  [^js el {:keys [blur threshold ripple-scale]}]
  (let [refs (gobj/get el k-refs)]
    (when-let [^js blur-el (:blur-el refs)]
      (.setAttribute blur-el "stdDeviation" (str blur)))
    (when-let [^js matrix-el (:matrix-el refs)]
      (.setAttribute matrix-el "values" threshold))
    (when-let [^js disp-el (:disp-el refs)]
      (.setAttribute disp-el "scale" (str ripple-scale))))
  nil)

;; ── Host style sync ─────────────────────────────────────────────────────────
(defn- apply-host-style!
  "Push model values onto the host as CSS custom properties."
  [^js el {:keys [color gap]}]
  (if color
    (.setProperty (.-style el) model/css-color color)
    (.removeProperty (.-style el) model/css-color))
  (.setProperty (.-style el) model/css-gap (str gap "px")))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m             (gobj/get el k-model)
          ^js mouse     (gobj/get el k-mouse)
          ^js blob-arr  (gobj/get el k-blobs)
          ^js state-arr (gobj/get el k-blob-state)
          ^js rects     (gobj/get el k-items-rect)
          ^js assigned  (or (get-assigned-elements el) #js [])
          n             (min (.-length rects)
                             (.-length assigned))
          mx            (gobj/get mouse "x")
          my            (gobj/get mouse "y")
          active?       (gobj/get mouse "active")
          mag-r         (:magnet-radius m)
          mag-s         (:magnet-strength m)
          bob-int       (:bob-intensity m)
          position      (:position m)
          active-scale  (+ 1.0 (* mag-s 0.8))
          lerp-speed    0.10
          phase         (or (gobj/get el k-noise-seed) 0)
          now           (.now js/Date)
          burst-time    (or (gobj/get el k-ripple-burst) 0)
          burst-active? (< (- now burst-time) 300)]

      ;; Ripple burst: temporarily boost displacement scale
      (when burst-active?
        (let [burst-progress (/ (- now burst-time) 300.0)
              burst-scale    (* (- 1.0 burst-progress) 20.0)
              base-scale     (:ripple-scale m)
              refs           (gobj/get el k-refs)]
          (when-let [^js disp-el (:disp-el refs)]
            (.setAttribute disp-el "scale" (str (+ base-scale burst-scale))))))

      ;; Update each item pair
      (dotimes [i n]
        (let [^js rect    (aget rects i)
              cx          (gobj/get rect "cx")
              cy          (gobj/get rect "cy")
              rest-idx    (* i 2)
              phantom-idx (+ rest-idx 1)]

          ;; Skip if blobs aren't ready
          (when (< phantom-idx (.-length blob-arr))
            (let [;; Compute influence from cursor proximity
                  inf         (if active?
                                (model/compute-item-influence cx cy mx my mag-r mag-s active-scale)
                                {:influence 0.0 :scale 1.0 :tx 0.0 :ty 0.0})
                  influence   (:influence inf)

                  ;; ── Rest blob: stationary at item center ──
                  ^js rest-blob (aget blob-arr rest-idx)
                  rest-tx       (str "translate(" (- cx 26) "px," (- cy 26) "px)")
                  _             (set! (.. rest-blob -style -transform) rest-tx)

                  ;; ── Phantom blob: rises on hover ──
                  phantom       (model/compute-phantom-offset influence position)
                  ^js pst       (aget state-arr phantom-idx)
                  t-pdx         (:dx phantom)
                  t-pdy         (:dy phantom)
                  t-pscale      (:scale phantom)

                  ;; Lerp phantom state
                  c-pdx         (model/lerp (gobj/get pst "cpDx") t-pdx lerp-speed)
                  c-pdy         (model/lerp (gobj/get pst "cpDy") t-pdy lerp-speed)
                  c-pscale      (model/lerp (gobj/get pst "cpScale") t-pscale lerp-speed)
                  _             (gobj/set pst "cpDx" c-pdx)
                  _             (gobj/set pst "cpDy" c-pdy)
                  _             (gobj/set pst "cpScale" c-pscale)

                  ^js phantom-blob (aget blob-arr phantom-idx)
                  phantom-tx    (str "translate("
                                     (+ (- cx 22) c-pdx) "px,"
                                     (+ (- cy 22) c-pdy) "px)"
                                     " scale(" c-pscale ")")
                  _             (set! (.. phantom-blob -style -transform) phantom-tx)

                  ;; ── Icon bob + tilt ──
                  ;; Compute cursor direction relative to icon (for tilt)
                  dx-sign       (if active?
                                  (let [dx (- mx cx)]
                                    (cond (> dx 1.0) 1.0
                                          (< dx -1.0) -1.0
                                          :else 0.0))
                                  0.0)
                  bob           (model/compute-bob-tilt influence dx-sign position bob-int)

                  ;; Lerp icon state
                  c-lift        (model/lerp (gobj/get pst "cLift") (:lift bob) lerp-speed)
                  c-rot         (model/lerp (gobj/get pst "cRot") (:rotation bob) lerp-speed)
                  c-scale       (model/lerp (gobj/get pst "cScale") (:scale bob) lerp-speed)
                  _             (gobj/set pst "cLift" c-lift)
                  _             (gobj/set pst "cRot" c-rot)
                  _             (gobj/set pst "cScale" c-scale)

                  ;; ── Ambient wobble: icons bob gently with the ripple ──
                  ;; Each icon gets a unique phase offset (golden angle)
                  ;; so they move independently like buoys on a surface.
                  ;; Wobble is reduced when hover influence is high (hover
                  ;; takes over the motion).
                  item-phase    (+ phase (* i 2.39996))
                  idle-factor   (- 1.0 (* influence 0.7))
                  wobble-y      (* (js/Math.sin item-phase) 1.8 idle-factor bob-int)
                  wobble-x      (* (js/Math.cos (* item-phase 0.7)) 0.8 idle-factor bob-int)
                  wobble-rot    (* (js/Math.sin (* item-phase 1.3)) 1.2 idle-factor bob-int)

                  ;; Combine hover transform + ambient wobble
                  ^js item      (aget assigned i)
                  vertical?     (:vertical? m)
                  total-lift    (+ c-lift (if vertical? wobble-x wobble-y))
                  total-rot     (+ c-rot wobble-rot)
                  item-tx       (if vertical?
                                  (str "translate(" total-lift "px," wobble-y "px)"
                                       " rotate(" total-rot "deg)"
                                       " scale(" c-scale ")")
                                  (str "translate(" wobble-x "px," total-lift "px)"
                                       " rotate(" total-rot "deg)"
                                       " scale(" c-scale ")"))]

              (set! (.. item -style -transform) item-tx)))))

      ;; Animate ripple by smoothly oscillating baseFrequency
      ;; (feTurbulence seed is integer-only and causes discrete jumps)
      (let [phase     (gobj/get el k-noise-seed)
            new-phase (+ phase (:ripple-speed m))
            refs      (gobj/get el k-refs)
            ;; Oscillate baseFrequency around 0.015 using sine for smooth looping
            freq      (+ 0.012 (* 0.006 (js/Math.sin new-phase)))]
        (gobj/set el k-noise-seed new-phase)
        (when-let [^js turb-el (:turb-el refs)]
          (.setAttribute turb-el "baseFrequency" (str freq))))

      ;; Reset burst displacement when done
      (when (and (not burst-active?) (> burst-time 0))
        (let [refs (gobj/get el k-refs)]
          (when-let [^js disp-el (:disp-el refs)]
            (.setAttribute disp-el "scale" (str (:ripple-scale m)))))
        (gobj/set el k-ripple-burst 0))

      ;; Schedule next frame
      (gobj/set el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el))))))
  nil)

(defn- start-animation! [^js el]
  (when-not (prefers-reduced-motion?)
    (let [m (gobj/get el k-model)]
      (when-not (:disabled? m)
        (gobj/set el k-noise-seed 0)
        (gobj/set el k-raf
                  (js/requestAnimationFrame (fn [_] (animate! el)))))))
  nil)

(defn- stop-animation! [^js el]
  (when-let [raf-id (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (gobj/set el k-raf nil))
  nil)

;; ── Reset item transforms ──────────────────────────────────────────────────
(defn- reset-item-transforms! [^js el]
  (when-let [^js assigned (get-assigned-elements el)]
    (dotimes [i (.-length assigned)]
      (let [^js item (aget assigned i)]
        (set! (.. item -style -transform) ""))))
  nil)

;; ── Event dispatching ───────────────────────────────────────────────────────
(defn- dispatch-select! [^js el index ^js item source]
  ;; Trigger ripple burst
  (gobj/set el k-ripple-burst (.now js/Date))
  (let [detail (model/select-detail index item source)
        event  (js/CustomEvent.
                model/event-select
                #js {:bubbles true :composed true :cancelable true
                     :detail (clj->js detail)})]
    (.dispatchEvent el event)))

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [{:keys [nav]} (gobj/get el k-refs)
        ^js nav nav]
    (when nav
      (let [^js rect  (.getBoundingClientRect nav)
            ^js mouse (gobj/get el k-mouse)]
        (gobj/set mouse "x" (- (.-clientX e) (.-left rect)))
        (gobj/set mouse "y" (- (.-clientY e) (.-top rect)))
        (gobj/set mouse "active" true))))
  nil)

(defn- on-pointerleave [^js el]
  (let [^js mouse (gobj/get el k-mouse)]
    (gobj/set mouse "active" false))
  nil)

(defn- find-item-index
  "Find the index of a clicked element among assigned slot elements."
  [^js target ^js assigned]
  (let [n (.-length assigned)]
    (loop [i 0]
      (when (< i n)
        (if (= target (aget assigned i))
          i
          (recur (inc i)))))))

(defn- on-click [^js el ^js e]
  (let [m (gobj/get el k-model)]
    (when-not (:disabled? m)
      (when-let [^js assigned (get-assigned-elements el)]
        (let [^js target (.-target e)]
          (when-let [idx (find-item-index target assigned)]
            (dispatch-select! el idx (aget assigned idx) "pointer"))))))
  nil)

(defn- on-keydown [^js el ^js e]
  (let [m (gobj/get el k-model)]
    (when-not (:disabled? m)
      (let [^js assigned (get-assigned-elements el)
            n            (when assigned (.-length assigned))
            key          (.-key e)
            vertical?    (:vertical? m)
            prev-key     (if vertical? "ArrowUp" "ArrowLeft")
            next-key     (if vertical? "ArrowDown" "ArrowRight")
            focus-idx    (or (gobj/get el k-focus-idx) -1)]
        (when (and n (pos? n))
          (cond
            (= key prev-key)
            (do (.preventDefault e)
                (let [new-idx (if (< focus-idx 1) (dec n) (dec focus-idx))]
                  (gobj/set el k-focus-idx new-idx)
                  (.focus ^js (aget assigned new-idx))))

            (= key next-key)
            (do (.preventDefault e)
                (let [new-idx (if (>= focus-idx (dec n)) 0 (inc focus-idx))]
                  (gobj/set el k-focus-idx new-idx)
                  (.focus ^js (aget assigned new-idx))))

            (or (= key "Enter") (= key " "))
            (when (>= focus-idx 0)
              (.preventDefault e)
              (dispatch-select! el focus-idx (aget assigned focus-idx) "keyboard")))))))
  nil)

(defn- on-slotchange [^js el]
  ;; Reconcile blobs to match new slotted children
  (reconcile-blobs! el)
  ;; Ensure items are focusable for keyboard nav
  (when-let [^js assigned (get-assigned-elements el)]
    (dotimes [i (.-length assigned)]
      (let [^js item (aget assigned i)]
        (when (nil? (.getAttribute item "tabindex"))
          (.setAttribute item "tabindex" "0")))))
  ;; Defer position caching until layout is stable
  (js/requestAnimationFrame (fn [_] (cache-item-rects! el)))
  nil)

(defn- on-resize! [^js el]
  (cache-item-rects! el)
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [h-move   (fn [e] (on-pointermove el e))
        h-leave  (fn [_] (on-pointerleave el))
        h-click  (fn [e] (on-click el e))
        h-key    (fn [e] (on-keydown el e))
        h-slot   (fn [_] (on-slotchange el))
        {:keys [slot]} (gobj/get el k-refs)
        ^js slot slot]
    (.addEventListener el "pointermove" h-move)
    (.addEventListener el "pointerleave" h-leave)
    (.addEventListener el "click" h-click)
    (.addEventListener el "keydown" h-key)
    (when slot
      (.addEventListener slot "slotchange" h-slot))
    (gobj/set el k-handlers
              {:move h-move :leave h-leave :click h-click
               :key h-key :slot h-slot}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [handlers (gobj/get el k-handlers)]
    (.removeEventListener el "pointermove" (:move handlers))
    (.removeEventListener el "pointerleave" (:leave handlers))
    (.removeEventListener el "click" (:click handlers))
    (.removeEventListener el "keydown" (:key handlers))
    (when-let [{:keys [slot]} (gobj/get el k-refs)]
      (when slot
        (.removeEventListener ^js slot "slotchange" (:slot handlers))))
    (gobj/set el k-handlers nil))
  nil)

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- set-a11y! [^js el]
  (when-not (.hasAttribute el "role")
    (.setAttribute el "role" "navigation"))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; position
  (.defineProperty js/Object proto "position"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-position
                                         (.getAttribute this model/attr-position))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-position)
                                          (.setAttribute this model/attr-position (str v)))))
                        :enumerable true :configurable true})

  ;; gap
  (.defineProperty js/Object proto "gap"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-gap
                                         (.getAttribute this model/attr-gap))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-gap)
                                          (.setAttribute this model/attr-gap (str v)))))
                        :enumerable true :configurable true})

  ;; blur
  (.defineProperty js/Object proto "blur"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-blur
                                         (.getAttribute this model/attr-blur))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-blur)
                                          (.setAttribute this model/attr-blur (str v)))))
                        :enumerable true :configurable true})

  ;; threshold
  (.defineProperty js/Object proto "threshold"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-threshold
                                         (.getAttribute this model/attr-threshold))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-threshold)
                                          (.setAttribute this model/attr-threshold (str v)))))
                        :enumerable true :configurable true})

  ;; rippleScale → ripple-scale
  (.defineProperty js/Object proto "rippleScale"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-ripple-scale
                                         (.getAttribute this model/attr-ripple-scale))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-ripple-scale)
                                          (.setAttribute this model/attr-ripple-scale (str v)))))
                        :enumerable true :configurable true})

  ;; rippleSpeed → ripple-speed
  (.defineProperty js/Object proto "rippleSpeed"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-ripple-speed
                                         (.getAttribute this model/attr-ripple-speed))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-ripple-speed)
                                          (.setAttribute this model/attr-ripple-speed (str v)))))
                        :enumerable true :configurable true})

  ;; color
  (.defineProperty js/Object proto "color"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-color
                                         (.getAttribute this model/attr-color))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-color)
                                          (.setAttribute this model/attr-color (str v)))))
                        :enumerable true :configurable true})

  ;; magnetRadius → magnet-radius
  (.defineProperty js/Object proto "magnetRadius"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-magnet-radius
                                         (.getAttribute this model/attr-magnet-radius))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-magnet-radius)
                                          (.setAttribute this model/attr-magnet-radius (str v)))))
                        :enumerable true :configurable true})

  ;; magnetStrength → magnet-strength
  (.defineProperty js/Object proto "magnetStrength"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-magnet-strength
                                         (.getAttribute this model/attr-magnet-strength))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-magnet-strength)
                                          (.setAttribute this model/attr-magnet-strength (str v)))))
                        :enumerable true :configurable true})

  ;; bobIntensity → bob-intensity
  (.defineProperty js/Object proto "bobIntensity"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bob-intensity
                                         (.getAttribute this model/attr-bob-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-bob-intensity)
                                          (.setAttribute this model/attr-bob-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; disabled (boolean)
  (.defineProperty js/Object proto "disabled"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true}))

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- update-from-attrs! [^js el]
  (let [m (read-model el)]
    (gobj/set el k-model m)
    (when (gobj/get el k-refs)
      (update-filter! el m)
      (apply-host-style! el m)
      (cache-item-rects! el)
      (if (:disabled? m)
        (do (stop-animation! el)
            (reset-item-transforms! el))
        (when-not (gobj/get el k-raf)
          (start-animation! el))))))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
  (gobj/set el k-model m)
  (gobj/set el k-mouse #js {:x 0 :y 0 :active false})
  (gobj/set el k-blobs #js [])
  (gobj/set el k-blob-state #js [])
  (gobj/set el k-items-rect #js [])
  (gobj/set el k-focus-idx -1)
  (gobj/set el k-ripple-burst 0)
  (ensure-refs! el)
  (apply-host-style! el m)
  (set-a11y! el)
  (remove-listeners! el)
  (add-listeners! el)
  ;; ResizeObserver (clean up any prior observer on reconnect)
  (when-let [^js old-ro (gobj/get el k-ro)]
  (.disconnect old-ro))
  (let [ro (js/ResizeObserver.
  (fn [_] (on-resize! el)))]
  (gobj/set el k-ro ro)
  (.observe ro el))
  ;; Defer blob/rect setup to after slot assignment
  (js/requestAnimationFrame
  (fn [_]
  (reconcile-blobs! el)
  (cache-item-rects! el)
  (start-animation! el))))
  nil)

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el)
  (reset-item-transforms! el)
  (when-let [^js ro (gobj/get el k-ro)]
  (.disconnect ro)
  (gobj/set el k-ro nil))
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
  (update-from-attrs! el))
  nil)

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
