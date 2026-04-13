(ns baredom.components.x-liquid-glass.x-liquid-glass
  (:require
   [goog.object :as gobj]
   [baredom.components.x-liquid-glass.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs           "__xLiquidGlassRefs")
(def ^:private k-raf            "__xLiquidGlassRaf")
(def ^:private k-model          "__xLiquidGlassModel")
(def ^:private k-time           "__xLiquidGlassTime")
(def ^:private k-last-frame     "__xLiquidGlassLF")
(def ^:private k-rest-x         "__xLiquidGlassRX")
(def ^:private k-rest-y         "__xLiquidGlassRY")
(def ^:private k-pointer-x      "__xLiquidGlassPX")
(def ^:private k-pointer-y      "__xLiquidGlassPY")
(def ^:private k-pointer-active "__xLiquidGlassPA")
(def ^:private k-ro             "__xLiquidGlassRO")
(def ^:private k-width          "__xLiquidGlassW")
(def ^:private k-height         "__xLiquidGlassH")
(def ^:private k-handlers       "__xLiquidGlassHdl")
(def ^:private k-uid            "__xLiquidGlassUID")
(def ^:private k-grad-time      "__xLiquidGlassGT")

;; ── Unique ID counter ───────────────────────────────────────────────────────
(def ^:private uid-state #js {:v 0})

(defn- next-uid []
  (let [v (gobj/get uid-state "v")]
    (gobj/set uid-state "v" (inc v))
    v))

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(defn- build-style-text [_uid]
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "isolation:isolate;"
   "min-width:120px;"
   "min-height:80px;"
   model/css-padding ":1.5rem;"
   model/css-specular-color ":rgba(255,255,255,0.6);"
   model/css-spring ":" model/spring-easing ";"
   model/css-spring-duration ":" model/spring-duration ";}"

   ;; SVG blob layer — renders the organic goo shape
   "[part=svg]{"
   "display:block;"
   "width:100%;"
   "height:100%;"
   "position:absolute;"
   "inset:0;"
   "overflow:visible;"
   "pointer-events:none;"
   "z-index:1;}"

   ;; Glass layer — backdrop blur masked to the goo blob shape
   "[part=glass]{"
   "position:absolute;"
   "inset:0;"
   "backdrop-filter:blur(var(" model/css-blur ",12px)) saturate(180%);"
   "-webkit-backdrop-filter:blur(var(" model/css-blur ",12px)) saturate(180%);"
   "z-index:0;}"

   ;; Grain texture overlay — gives the glass a tactile frosted feel
   "[part=grain]{"
   "position:absolute;"
   "inset:0;"
   "z-index:2;"
   "opacity:0.4;"
   "pointer-events:none;}"

   ;; Gradient background — sits on top of glass, behind content
   "[part=gradient]{"
   "position:absolute;"
   "inset:0;"
   "z-index:1;"
   "pointer-events:none;}"

   ;; Specular highlight overlay
   "[part=specular]{"
   "position:absolute;"
   "inset:0;"
   "z-index:1;"
   "opacity:0;"
   "pointer-events:none;"
   "transition:background var(" model/css-spring-duration "," model/spring-duration ") var(" model/css-spring "," model/spring-easing "),"
   "opacity var(" model/css-spring-duration "," model/spring-duration ") var(" model/css-spring "," model/spring-easing ");}"

   ;; Content layer — above everything
   "[part=content]{"
   "position:relative;"
   "padding:var(" model/css-padding ",1.5rem);"
   "z-index:3;}"

   ;; Enter animation with spring
   "@keyframes lg-enter{"
   "from{opacity:0;transform:scale(0.92);}"
   "to{opacity:1;transform:scale(1);}}"

   ":host([data-entering]) [part=glass],"
   ":host([data-entering]) [part=svg]{"
   "animation:lg-enter var(" model/css-spring-duration "," model/spring-duration ") var(" model/css-spring "," model/spring-easing ") both;}"

   ;; Dark mode
   "@media(prefers-color-scheme:dark){"
   ":host{"
   model/css-specular-color ":rgba(255,255,255,0.3);}}"

   ;; Reduced motion
   "@media(prefers-reduced-motion:reduce){"
   ":host([data-entering]) [part=glass],"
   ":host([data-entering]) [part=svg]{"
   "animation:none;}"
   "[part=specular]{transition:none!important;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [uid     (next-uid)
        root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        ;; SVG with goo filter and blob ellipses
        svg     (.createElementNS js/document svg-ns "svg")
        defs    (.createElementNS js/document svg-ns "defs")
        filter-el (.createElementNS js/document svg-ns "filter")
        blur-el (.createElementNS js/document svg-ns "feGaussianBlur")
        cm-el   (.createElementNS js/document svg-ns "feColorMatrix")
        g       (.createElementNS js/document svg-ns "g")
        ;; Core ellipse (the large central blob)
        core    (.createElementNS js/document svg-ns "ellipse")
        ;; Glass layer
        glass   (.createElement js/document "div")
        ;; Grain texture layer
        grain   (.createElement js/document "div")
        ;; Gradient layer
        grad    (.createElement js/document "div")
        ;; Specular layer
        spec    (.createElement js/document "div")
        ;; Content layer
        content (.createElement js/document "div")
        slot    (.createElement js/document "slot")
        filter-id (str "lg-goo-" uid)]

    (gobj/set el k-uid uid)

    (set! (.-textContent style) (build-style-text uid))

    ;; SVG setup
    (.setAttribute svg "part" "svg")
    (.setAttribute svg "viewBox" "0 0 300 200")
    (.setAttribute svg "preserveAspectRatio" "none")
    (.setAttribute svg "aria-hidden" "true")

    ;; Filter setup — blur + alpha contrast = organic goo edges
    (.setAttribute filter-el "id" filter-id)
    (.setAttribute blur-el "in" "SourceGraphic")
    (.setAttribute blur-el "stdDeviation" "10")
    (.setAttribute blur-el "result" "blur")
    (.setAttribute cm-el "in" "blur")
    (.setAttribute cm-el "mode" "matrix")
    (.setAttribute cm-el "values" "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 19 -9")

    (.appendChild filter-el blur-el)
    (.appendChild filter-el cm-el)
    (.appendChild defs filter-el)
    (.appendChild svg defs)

    ;; Filtered group — semi-transparent for glass tint
    (.setAttribute g "filter" (str "url(#" filter-id ")"))
    (.setAttribute g "opacity" "0.35")

    ;; Core ellipse
    (.setAttribute core "class" "blob-core")

    (.appendChild g core)
    (.appendChild svg g)

    ;; Edge gradient — simulates light hitting a thick glass slab edge
    (let [edge-grad (.createElementNS js/document svg-ns "linearGradient")
          edge-id   (str "lg-edge-" uid)
          stop1     (.createElementNS js/document svg-ns "stop")
          stop2     (.createElementNS js/document svg-ns "stop")
          stop3     (.createElementNS js/document svg-ns "stop")]
      (.setAttribute edge-grad "id" edge-id)
      (.setAttribute edge-grad "x1" "0") (.setAttribute edge-grad "y1" "0")
      (.setAttribute edge-grad "x2" "1") (.setAttribute edge-grad "y2" "1")
      (.setAttribute stop1 "offset" "0%")
      (.setAttribute stop1 "stop-color" "white")
      (.setAttribute stop1 "stop-opacity" "0.9")
      (.setAttribute stop2 "offset" "50%")
      (.setAttribute stop2 "stop-color" "white")
      (.setAttribute stop2 "stop-opacity" "0.15")
      (.setAttribute stop3 "offset" "100%")
      (.setAttribute stop3 "stop-color" "white")
      (.setAttribute stop3 "stop-opacity" "0.5")
      (.appendChild edge-grad stop1)
      (.appendChild edge-grad stop2)
      (.appendChild edge-grad stop3)
      (.appendChild defs edge-grad)
      (gobj/set el "__xLiquidGlassEdgeId" edge-id))

    ;; Border group — goo filter, gradient stroke for glass edge shimmer
    (let [border-g    (.createElementNS js/document svg-ns "g")
          border-core (.createElementNS js/document svg-ns "ellipse")
          edge-id     (gobj/get el "__xLiquidGlassEdgeId")]
      (.setAttribute border-g "filter" (str "url(#" filter-id ")"))
      (.setAttribute border-g "opacity" "0.7")
      (.setAttribute border-core "class" "blob-border")
      (.setAttribute border-core "fill" "none")
      (.setAttribute border-core "stroke" (str "url(#" edge-id ")"))
      (.setAttribute border-core "stroke-width" "2.5")
      (.appendChild border-g border-core)
      (.appendChild svg border-g)
      (gobj/set el "__xLiquidGlassBorderG" border-g)
      (gobj/set el "__xLiquidGlassBorderCore" border-core))

    ;; Grain filter — feTurbulence noise for tactile frosted glass texture
    (let [grain-filter (.createElementNS js/document svg-ns "filter")
          grain-id     (str "lg-grain-" uid)
          turb         (.createElementNS js/document svg-ns "feTurbulence")
          grain-cm     (.createElementNS js/document svg-ns "feColorMatrix")]
      (.setAttribute grain-filter "id" grain-id)
      (.setAttribute turb "type" "fractalNoise")
      (.setAttribute turb "baseFrequency" "0.6")
      (.setAttribute turb "numOctaves" "3")
      (.setAttribute turb "stitchTiles" "stitch")
      (.setAttribute grain-cm "type" "matrix")
      (.setAttribute grain-cm "values" "0 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 0.05 0")
      (.appendChild grain-filter turb)
      (.appendChild grain-filter grain-cm)
      (.appendChild defs grain-filter)
      (gobj/set el "__xLiquidGlassGrainId" grain-id))

    ;; Mask — goo-filtered blob shape used to clip the glass backdrop-filter
    (let [mask-el   (.createElementNS js/document svg-ns "mask")
          mask-id   (str "lg-mask-" uid)
          mask-g    (.createElementNS js/document svg-ns "g")
          mask-core (.createElementNS js/document svg-ns "ellipse")]
      (.setAttribute mask-el "id" mask-id)
      ;; maskUnits/maskContentUnits default to objectBoundingBox which
      ;; would normalise coordinates. We need userSpaceOnUse to match
      ;; the SVG viewBox coordinate system.
      (.setAttribute mask-el "maskUnits" "userSpaceOnUse")
      (.setAttribute mask-el "maskContentUnits" "userSpaceOnUse")
      (.setAttribute mask-g "filter" (str "url(#" filter-id ")"))
      (.setAttribute mask-core "fill" "white")
      (.appendChild mask-g mask-core)
      (.appendChild mask-el mask-g)
      (.appendChild defs mask-el)
      (gobj/set el "__xLiquidGlassMaskG" mask-g)
      (gobj/set el "__xLiquidGlassMaskCore" mask-core)
      (gobj/set el "__xLiquidGlassMaskSats" #js [])
      (gobj/set el "__xLiquidGlassMaskId" mask-id))

    ;; Glass — masked to blob shape via SVG mask
    (.setAttribute glass "part" "glass")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. glass -style -mask) mask-ref)
      (set! (.. glass -style -webkitMask) mask-ref))

    ;; Grain — texture overlay masked to blob shape
    (.setAttribute grain "part" "grain")
    (set! (.. grain -style -filter)
          (str "url(#" (gobj/get el "__xLiquidGlassGrainId") ")"))
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. grain -style -mask) mask-ref)
      (set! (.. grain -style -webkitMask) mask-ref))

    ;; Gradient — masked to blob shape
    (.setAttribute grad "part" "gradient")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. grad -style -mask) mask-ref)
      (set! (.. grad -style -webkitMask) mask-ref))

    ;; Specular — masked to blob shape
    (.setAttribute spec "part" "specular")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. spec -style -mask) mask-ref)
      (set! (.. spec -style -webkitMask) mask-ref))

    ;; Content
    (.setAttribute content "part" "content")
    (.appendChild content slot)

    ;; Assemble shadow DOM: glass → SVG blob → grain → gradient → specular → content
    (.appendChild root style)
    (.appendChild root glass)
    (.appendChild root svg)
    (.appendChild root grain)
    (.appendChild root grad)
    (.appendChild root spec)
    (.appendChild root content)

    ;; Start enter animation
    (.setAttribute el "data-entering" "")
    (js/setTimeout #(.removeAttribute el "data-entering") 700)

    (gobj/set el k-refs
              {:root      root
               :svg       svg
               :g         g
               :core      core
               :filter    filter-el
               :blur      blur-el
               :glass     glass
               :grain     grain
               :gradient  grad
               :specular  spec
               :content   content
               :satellites #js []}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs)))
  nil)

;; ── Satellite management ───────────────────────────────────────────────────
(defn- sync-satellites!
  "Ensure the SVG group and mask group each have exactly n satellite ellipses."
  [^js el n]
  (let [refs     (gobj/get el k-refs)
        ^js g    (:g refs)
        ^js sats (:satellites refs)
        ^js mask-g    (gobj/get el "__xLiquidGlassMaskG")
        ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
    ;; Remove excess satellites from both groups
    (loop [i (dec (.-length sats))]
      (when (>= i n)
        (.removeChild g (aget sats i))
        (when mask-g (.removeChild mask-g (aget mask-sats i)))
        (recur (dec i))))
    (when (> (.-length sats) n)
      (set! (.-length sats) n)
      (when mask-sats (set! (.-length mask-sats) n)))
    ;; Add missing satellites to both groups
    (loop [i (.-length sats)]
      (when (< i n)
        (let [sat (.createElementNS js/document svg-ns "ellipse")]
          (.setAttribute sat "class" (str "blob-sat-" i))
          (.setAttribute sat "fill" "#ffffff")
          (.appendChild g sat)
          (.push sats sat))
        ;; Mirror in mask group
        (when mask-g
          (let [msat (.createElementNS js/document svg-ns "ellipse")]
            (.setAttribute msat "fill" "white")
            (.appendChild mask-g msat)
            (.push mask-sats msat)))
        (recur (inc i)))))
  nil)

;; ── Geometry initialisation ────────────────────────────────────────────────
(defn- init-geometry! [^js el w h]
  (let [m      (gobj/get el k-model)
        n      (:blobs m)
        ;; Central ellipse covers most of the area
        cx     (/ w 2.0)
        cy     (/ h 2.0)
        ;; Core radii
        core-rx (* w 0.32)
        core-ry (* h 0.32)
        ;; Satellite orbit — at the core edge so they create visible bumps
        orbit-rx (* core-rx 0.9)
        orbit-ry (* core-ry 0.9)
        ;; Satellite size — large enough to create visible metaball bumps
        sat-rx (* core-rx 0.55)
        sat-ry (* core-ry 0.55)
        result (model/satellite-rest-positions n cx cy orbit-rx orbit-ry)]

    ;; Update core ellipse
    (let [{:keys [core]} (gobj/get el k-refs)
          ^js core core]
      (.setAttribute core "cx" (str cx))
      (.setAttribute core "cy" (str cy))
      (.setAttribute core "rx" (str core-rx))
      (.setAttribute core "ry" (str core-ry)))

    ;; Update mask core to match
    (when-let [^js mc (gobj/get el "__xLiquidGlassMaskCore")]
      (.setAttribute mc "cx" (str cx))
      (.setAttribute mc "cy" (str cy))
      (.setAttribute mc "rx" (str core-rx))
      (.setAttribute mc "ry" (str core-ry)))

    ;; Border uses slightly larger radii to encompass satellite area
    (let [outer-rx (* w 0.44)
          outer-ry (* h 0.44)]
      (when-let [^js bc (gobj/get el "__xLiquidGlassBorderCore")]
        (.setAttribute bc "cx" (str cx))
        (.setAttribute bc "cy" (str cy))
        (.setAttribute bc "rx" (str outer-rx))
        (.setAttribute bc "ry" (str outer-ry))))

    ;; Sync satellite count
    (sync-satellites! el n)

    ;; Set satellite sizes (visible + mask)
    (let [{:keys [satellites]} (gobj/get el k-refs)
          ^js sats satellites
          ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
      (dotimes [i n]
        (let [^js sat (aget sats i)]
          (.setAttribute sat "rx" (str sat-rx))
          (.setAttribute sat "ry" (str sat-ry)))
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (.setAttribute msat "rx" (str sat-rx))
            (.setAttribute msat "ry" (str sat-ry))))))

    ;; Store rest positions and geometry
    (gobj/set el k-rest-x (aget result 0))
    (gobj/set el k-rest-y (aget result 1))))

;; ── Apply model to DOM ─────────────────────────────────────────────────────
(defn- apply-model! [^js el]
  (let [m    (gobj/get el k-model)
        refs (gobj/get el k-refs)
        ^js blur-el (:blur refs)
        ^js glass   (:glass refs)
        ^js spec    (:specular refs)
        ^js core    (:core refs)
        ^js g       (:g refs)]

    ;; Update goo filter blur
    (.setAttribute blur-el "stdDeviation" (str (:goo m)))

    ;; Update backdrop blur on glass div
    (.setProperty (.-style glass) model/css-blur (str (:blur m) "px"))

    ;; Fill ellipses with white — group opacity controls translucency
    (.setAttribute core "fill" "#ffffff")
    (let [^js sats (:satellites refs)]
      (dotimes [i (.-length sats)]
        (.setAttribute ^js (aget sats i) "fill" "#ffffff")))

    ;; Mode-dependent styling
    (let [submerged? (= :submerged (:mode m))
          ^js content-el (:content refs)
          ^js grain-el   (:grain refs)
          mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (if submerged?
          (do
            ;; Content is "submerged" — sits below the glass layers
            ;; with a direct blur and mask to clip it to the blob shape.
            (set! (.. content-el -style -position) "relative")
            (set! (.. content-el -style -zIndex) "0")
            ;; Frost level scales blur, opacity, fill, grain
            (let [f          (:frost m)
                  c-blur     (* f 2.0)           ;; 0→0px, 0.5→1px, 1→2px
                  c-opacity  (- 1.0 (* f 0.5))   ;; 0→1.0, 0.5→0.75, 1→0.5
                  g-opacity  (* f 0.35)           ;; 0→0, 0.5→0.175, 1→0.35
                  gr-opacity (* f 0.2)]           ;; 0→0, 0.5→0.1, 1→0.2
              (set! (.. content-el -style -filter) (str "blur(" (.toFixed c-blur 1) "px)"))
              (set! (.. content-el -style -opacity) (str (.toFixed c-opacity 2)))
              (set! (.. content-el -style -mask) mask-ref)
              (set! (.. content-el -style -webkitMask) mask-ref)
              (.setAttribute g "opacity" (.toFixed g-opacity 2))
              (when grain-el (set! (.. grain-el -style -opacity) (.toFixed gr-opacity 2))))
            (set! (.. el -style -contain) "layout style"))
          (do
            ;; Content floats above the glass — crisp, no mask
            (set! (.. content-el -style -position) "relative")
            (set! (.. content-el -style -zIndex) "3")
            (set! (.. content-el -style -filter) "")
            (set! (.. content-el -style -opacity) "")
            (set! (.. content-el -style -mask) "")
            (set! (.. content-el -style -webkitMask) "")
            (.setAttribute g "opacity" "0.35")
            (when grain-el (set! (.. grain-el -style -opacity) "0.4"))
            (set! (.. el -style -contain) ""))))

    ;; Specular visibility
    (when-not (:specular? m)
      (set! (.. spec -style -opacity) "0")))
  nil)

;; ── Render satellites ──────────────────────────────────────────────────────
(defn- render-satellites! [^js el t]
  (let [m       (gobj/get el k-model)
        n       (:blobs m)
        speed   (:speed m)
        amp     (:amplitude m)
        w       (gobj/get el k-width)
        h       (gobj/get el k-height)
        ;; Displace relative to core radius, capped so satellites
        ;; stay within the goo filter merge range
        core-r  (* (js/Math.min w h) 0.32)
        max-disp (* amp core-r 0.6)
        ^js rx  (gobj/get el k-rest-x)
        ^js ry  (gobj/get el k-rest-y)
        {:keys [satellites]} (gobj/get el k-refs)
        ^js sats satellites
        ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
    (dotimes [i n]
      (let [result (model/displace-satellite
                    (aget rx i) (aget ry i) i t speed max-disp)
            cx-str (.toFixed (aget result 0) 2)
            cy-str (.toFixed (aget result 1) 2)
            ^js sat (aget sats i)]
        (.setAttribute sat "cx" cx-str)
        (.setAttribute sat "cy" cy-str)
        ;; Mirror to mask satellite
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (.setAttribute msat "cx" cx-str)
            (.setAttribute msat "cy" cy-str))))))
  nil)

;; ── Render specular ────────────────────────────────────────────────────────
(defn- render-specular! [^js el]
  (let [m    (gobj/get el k-model)
        refs (gobj/get el k-refs)
        ^js spec (:specular refs)]
    (when (:specular? m)
      (let [active (gobj/get el k-pointer-active)
            px     (gobj/get el k-pointer-x)
            py     (gobj/get el k-pointer-y)
            w      (gobj/get el k-width)
            h      (gobj/get el k-height)
            size   (* (:specular-size m) (js/Math.min w h))
            color  (str "var(" model/css-specular-color ",rgba(255,255,255,0.6))")]
        (if active
          (do
            (set! (.. spec -style -opacity) (str (:specular-intensity m)))
            (set! (.. spec -style -background)
                  (str "radial-gradient(ellipse " size "px " size "px at "
                       (.toFixed px 1) "px " (.toFixed py 1) "px,"
                       color " 0%,transparent 100%)")))
          (set! (.. spec -style -opacity) "0")))))
  nil)

;; ── Render gradient drift ──────────────────────────────────────────────────
(defn- render-gradient! [^js el t]
  (let [m    (gobj/get el k-model)
        refs (gobj/get el k-refs)
        ^js grad (:gradient refs)
        c1   (:color-1 m)
        c2   (:color-2 m)
        ;; Slow circular drift for the gradient layers
        ox1 (+ 30.0 (* 10.0 (js/Math.sin (* t 0.07))))
        oy1 (+ 20.0 (* 8.0 (js/Math.cos (* t 0.09))))
        ox2 (+ 70.0 (* 12.0 (js/Math.cos (* t 0.05))))
        oy2 (+ 80.0 (* 10.0 (js/Math.sin (* t 0.06))))]
    (set! (.. grad -style -background)
          (str "radial-gradient(ellipse at " (.toFixed ox1 1) "% " (.toFixed oy1 1) "%,"
               c1 " 0%,transparent 70%),"
               "radial-gradient(ellipse at " (.toFixed ox2 1) "% " (.toFixed oy2 1) "%,"
               c2 " 0%,transparent 70%)")))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [now        (js/performance.now)
          last-frame (gobj/get el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          t          (+ (gobj/get el k-time) dt)]

      (gobj/set el k-last-frame now)
      (gobj/set el k-time t)

      ;; Update satellite positions
      (render-satellites! el t)

      ;; Update specular highlight
      (render-specular! el)

      ;; Update gradient drift
      (render-gradient! el t)

      ;; Always continue — noise never settles
      (gobj/set el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el))))))
  nil)

(defn- start-animation! [^js el]
  (when-not (gobj/get el k-raf)
    (gobj/set el k-last-frame (js/performance.now))
    (gobj/set el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el)))))
  nil)

(defn- stop-animation! [^js el]
  (when-let [raf-id (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (gobj/set el k-raf nil))
  nil)

;; ── Render static (disabled / reduced-motion) ──────────────────────────────
(defn- render-static! [^js el]
  (let [m      (gobj/get el k-model)
        n      (:blobs m)
        ^js rx (gobj/get el k-rest-x)
        ^js ry (gobj/get el k-rest-y)
        {:keys [satellites]} (gobj/get el k-refs)
        ^js sats satellites
        ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
    (dotimes [i n]
      (let [cx-str (.toFixed (aget rx i) 2)
            cy-str (.toFixed (aget ry i) 2)]
        (when (< i (.-length sats))
          (let [^js sat (aget sats i)]
            (.setAttribute sat "cx" cx-str)
            (.setAttribute sat "cy" cy-str)))
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (.setAttribute msat "cx" cx-str)
            (.setAttribute msat "cy" cy-str))))))
  nil)

;; ── ResizeObserver ──────────────────────────────────────────────────────────
(defn- on-resize! [^js el ^js entries]
  (when (pos? (.-length entries))
    (let [^js entry (aget entries 0)
          ^js cr    (.-contentRect entry)
          w         (.-width cr)
          h         (.-height cr)]
      (when (and (> w 0) (> h 0))
        (gobj/set el k-width w)
        (gobj/set el k-height h)
        ;; Update SVG viewBox and mask bounds
        (let [{:keys [svg]} (gobj/get el k-refs)
              ^js svg svg]
          (.setAttribute svg "viewBox" (str "0 0 " w " " h))
          ;; Mask must cover same area for userSpaceOnUse to work
          (when-let [^js mask-el (.querySelector svg (str "#" (gobj/get el "__xLiquidGlassMaskId")))]
            (.setAttribute mask-el "x" "0")
            (.setAttribute mask-el "y" "0")
            (.setAttribute mask-el "width" (str w))
            (.setAttribute mask-el "height" (str h))))
        ;; Reinitialise geometry with new dimensions
        (init-geometry! el w h)
        (apply-model! el)
        ;; Render
        (let [m (gobj/get el k-model)]
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-satellites! el (or (gobj/get el k-time) 0.0))
                (start-animation! el)))))))
  nil)

;; ── Pointer event handlers ──────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [^js rect (.getBoundingClientRect el)]
    (gobj/set el k-pointer-x (- (.-clientX e) (.-left rect)))
    (gobj/set el k-pointer-y (- (.-clientY e) (.-top rect))))
  nil)

(defn- on-pointerenter [^js el]
  (gobj/set el k-pointer-active true)
  nil)

(defn- on-pointerleave [^js el]
  (gobj/set el k-pointer-active false)
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [move-fn  (fn [^js e] (on-pointermove el e))
        enter-fn (fn [] (on-pointerenter el))
        leave-fn (fn [] (on-pointerleave el))]
    (gobj/set el k-handlers
              #js {"move" move-fn "enter" enter-fn "leave" leave-fn})
    (.addEventListener el "pointermove" move-fn #js {:passive true})
    (.addEventListener el "pointerenter" enter-fn)
    (.addEventListener el "pointerleave" leave-fn))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (gobj/get el k-handlers)]
    (.removeEventListener el "pointermove" (gobj/get hdl "move"))
    (.removeEventListener el "pointerenter" (gobj/get hdl "enter"))
    (.removeEventListener el "pointerleave" (gobj/get hdl "leave"))
    (gobj/set el k-handlers nil))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:blobs-raw              (.getAttribute el model/attr-blobs)
    :speed-raw              (.getAttribute el model/attr-speed)
    :amplitude-raw          (.getAttribute el model/attr-amplitude)
    :blur-raw               (.getAttribute el model/attr-blur)
    :goo-raw                (.getAttribute el model/attr-goo)
    :tint-raw               (.getAttribute el model/attr-tint)
    :specular-attr          (.getAttribute el model/attr-specular)
    :specular-size-raw      (.getAttribute el model/attr-specular-size)
    :specular-intensity-raw (.getAttribute el model/attr-specular-intensity)
    :disabled-attr          (.getAttribute el model/attr-disabled)
    :mode-raw               (.getAttribute el model/attr-mode)
    :frost-raw              (.getAttribute el model/attr-frost)
    :color-1-raw            (.getAttribute el model/attr-color-1)
    :color-2-raw            (.getAttribute el model/attr-color-2)}))

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- update-from-attrs! [^js el]
  (let [m (read-model el)]
    (gobj/set el k-model m)
    (let [w (gobj/get el k-width)
          h (gobj/get el k-height)]
      (when (and w h (> w 0) (> h 0))
        (init-geometry! el w h)
        (apply-model! el)
        (if (or (:disabled? m) (prefers-reduced-motion?))
          (do (stop-animation! el)
              (render-static! el))
          (do (render-satellites! el (or (gobj/get el k-time) 0.0))
              (start-animation! el))))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; blobs (number)
  (.defineProperty js/Object proto model/attr-blobs
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-blobs
                                         (.getAttribute this model/attr-blobs))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-blobs)
                                          (.setAttribute this model/attr-blobs (str v)))))
                        :enumerable true :configurable true})

  ;; speed (number)
  (.defineProperty js/Object proto model/attr-speed
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-speed
                                         (.getAttribute this model/attr-speed))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-speed)
                                          (.setAttribute this model/attr-speed (str v)))))
                        :enumerable true :configurable true})

  ;; amplitude (number)
  (.defineProperty js/Object proto model/attr-amplitude
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-amplitude
                                         (.getAttribute this model/attr-amplitude))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-amplitude)
                                          (.setAttribute this model/attr-amplitude (str v)))))
                        :enumerable true :configurable true})

  ;; blur (number)
  (.defineProperty js/Object proto model/attr-blur
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

  ;; goo (number)
  (.defineProperty js/Object proto model/attr-goo
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-goo
                                         (.getAttribute this model/attr-goo))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-goo)
                                          (.setAttribute this model/attr-goo (str v)))))
                        :enumerable true :configurable true})

  ;; tint (string)
  (.defineProperty js/Object proto model/attr-tint
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-tint)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-tint)
                                          (.setAttribute this model/attr-tint (str v)))))
                        :enumerable true :configurable true})

  ;; specular (boolean)
  (.defineProperty js/Object proto model/attr-specular
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-specular)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-specular "")
                                          (.removeAttribute this model/attr-specular))))
                        :enumerable true :configurable true})

  ;; specularSize (camelCase for kebab-case attribute)
  (.defineProperty js/Object proto "specularSize"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-specular-size
                                         (.getAttribute this model/attr-specular-size))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-specular-size)
                                          (.setAttribute this model/attr-specular-size (str v)))))
                        :enumerable true :configurable true})

  ;; specularIntensity (camelCase for kebab-case attribute)
  (.defineProperty js/Object proto "specularIntensity"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-specular-intensity
                                         (.getAttribute this model/attr-specular-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-specular-intensity)
                                          (.setAttribute this model/attr-specular-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; disabled (boolean)
  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  ;; mode (string)
  (.defineProperty js/Object proto model/attr-mode
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [v (.getAttribute this model/attr-mode)]
                                          (if (= v "submerged") "submerged" "surface"))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-mode)
                                          (.setAttribute this model/attr-mode (str v)))))
                        :enumerable true :configurable true})

  ;; frost (number)
  (.defineProperty js/Object proto model/attr-frost
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-frost
                                         (.getAttribute this model/attr-frost))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-frost)
                                          (.setAttribute this model/attr-frost (str v)))))
                        :enumerable true :configurable true})

  ;; color1 (string, reflects color-1 attribute)
  (.defineProperty js/Object proto "color1"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-color-1)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-color-1)
                                          (.setAttribute this model/attr-color-1 (str v)))))
                        :enumerable true :configurable true})

  ;; color2 (string, reflects color-2 attribute)
  (.defineProperty js/Object proto "color2"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-color-2)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-color-2)
                                          (.setAttribute this model/attr-color-2 (str v)))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (let [m (read-model this)]
                       (gobj/set this k-model m)
                       (gobj/set this k-time 0.0)
                       (gobj/set this k-pointer-active false)
                       (gobj/set this k-pointer-x 0.0)
                       (gobj/set this k-pointer-y 0.0)
                       (gobj/set this k-grad-time 0.0)
                       (ensure-refs! this)
                       (apply-model! this)
                       ;; Set up ResizeObserver
                       (let [ro (js/ResizeObserver.
                                 (fn [^js entries] (on-resize! this entries)))]
                         (gobj/set this k-ro ro)
                         (.observe ro this))
                       ;; Add pointer event listeners
                       (remove-listeners! this)
                       (add-listeners! this))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (stop-animation! this)
                     (remove-listeners! this)
                     (when-let [^js ro (gobj/get this k-ro)]
                       (.disconnect ro)
                       (gobj/set this k-ro nil))
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (when (gobj/get this k-refs)
                         (update-from-attrs! this)))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
