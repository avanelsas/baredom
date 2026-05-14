(ns baredom.components.x-liquid-glass.x-liquid-glass
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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

    (du/setv! el k-uid uid)

    (set! (.-textContent style) (build-style-text uid))

    ;; SVG setup
    (du/set-attr! svg "part" "svg")
    (du/set-attr! svg "viewBox" "0 0 300 200")
    (du/set-attr! svg "preserveAspectRatio" "none")
    (du/set-attr! svg "aria-hidden" "true")

    ;; Filter setup — blur + alpha contrast = organic goo edges
    (du/set-attr! filter-el "id" filter-id)
    (du/set-attr! blur-el "in" "SourceGraphic")
    (du/set-attr! blur-el "stdDeviation" "10")
    (du/set-attr! blur-el "result" "blur")
    (du/set-attr! cm-el "in" "blur")
    (du/set-attr! cm-el "mode" "matrix")
    (du/set-attr! cm-el "values" "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 19 -9")

    (.appendChild filter-el blur-el)
    (.appendChild filter-el cm-el)
    (.appendChild defs filter-el)
    (.appendChild svg defs)

    ;; Filtered group — semi-transparent for glass tint
    (du/set-attr! g "filter" (str "url(#" filter-id ")"))
    (du/set-attr! g "opacity" "0.35")

    ;; Core ellipse
    (du/set-attr! core "class" "blob-core")

    (.appendChild g core)
    (.appendChild svg g)

    ;; Edge gradient — simulates light hitting a thick glass slab edge
    (let [edge-grad (.createElementNS js/document svg-ns "linearGradient")
          edge-id   (str "lg-edge-" uid)
          stop1     (.createElementNS js/document svg-ns "stop")
          stop2     (.createElementNS js/document svg-ns "stop")
          stop3     (.createElementNS js/document svg-ns "stop")]
      (du/set-attr! edge-grad "id" edge-id)
      (du/set-attr! edge-grad "x1" "0") (du/set-attr! edge-grad "y1" "0")
      (du/set-attr! edge-grad "x2" "1") (du/set-attr! edge-grad "y2" "1")
      (du/set-attr! stop1 "offset" "0%")
      (du/set-attr! stop1 "stop-color" "white")
      (du/set-attr! stop1 "stop-opacity" "0.9")
      (du/set-attr! stop2 "offset" "50%")
      (du/set-attr! stop2 "stop-color" "white")
      (du/set-attr! stop2 "stop-opacity" "0.15")
      (du/set-attr! stop3 "offset" "100%")
      (du/set-attr! stop3 "stop-color" "white")
      (du/set-attr! stop3 "stop-opacity" "0.5")
      (.appendChild edge-grad stop1)
      (.appendChild edge-grad stop2)
      (.appendChild edge-grad stop3)
      (.appendChild defs edge-grad)
      (gobj/set el "__xLiquidGlassEdgeId" edge-id))

    ;; Border group — goo filter, gradient stroke for glass edge shimmer
    (let [border-g    (.createElementNS js/document svg-ns "g")
          border-core (.createElementNS js/document svg-ns "ellipse")
          edge-id     (gobj/get el "__xLiquidGlassEdgeId")]
      (du/set-attr! border-g "filter" (str "url(#" filter-id ")"))
      (du/set-attr! border-g "opacity" "0.7")
      (du/set-attr! border-core "class" "blob-border")
      (du/set-attr! border-core "fill" "none")
      (du/set-attr! border-core "stroke" (str "url(#" edge-id ")"))
      (du/set-attr! border-core "stroke-width" "2.5")
      (.appendChild border-g border-core)
      (.appendChild svg border-g)
      (gobj/set el "__xLiquidGlassBorderG" border-g)
      (gobj/set el "__xLiquidGlassBorderCore" border-core))

    ;; Grain filter — feTurbulence noise for tactile frosted glass texture
    (let [grain-filter (.createElementNS js/document svg-ns "filter")
          grain-id     (str "lg-grain-" uid)
          turb         (.createElementNS js/document svg-ns "feTurbulence")
          grain-cm     (.createElementNS js/document svg-ns "feColorMatrix")]
      (du/set-attr! grain-filter "id" grain-id)
      (du/set-attr! turb "type" "fractalNoise")
      (du/set-attr! turb "baseFrequency" "0.6")
      (du/set-attr! turb "numOctaves" "3")
      (du/set-attr! turb "stitchTiles" "stitch")
      (du/set-attr! grain-cm "type" "matrix")
      (du/set-attr! grain-cm "values" "0 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 0.05 0")
      (.appendChild grain-filter turb)
      (.appendChild grain-filter grain-cm)
      (.appendChild defs grain-filter)
      (gobj/set el "__xLiquidGlassGrainId" grain-id))

    ;; Mask — goo-filtered blob shape used to clip the glass backdrop-filter
    (let [mask-el   (.createElementNS js/document svg-ns "mask")
          mask-id   (str "lg-mask-" uid)
          mask-g    (.createElementNS js/document svg-ns "g")
          mask-core (.createElementNS js/document svg-ns "ellipse")]
      (du/set-attr! mask-el "id" mask-id)
      ;; maskUnits/maskContentUnits default to objectBoundingBox which
      ;; would normalise coordinates. We need userSpaceOnUse to match
      ;; the SVG viewBox coordinate system.
      (du/set-attr! mask-el "maskUnits" "userSpaceOnUse")
      (du/set-attr! mask-el "maskContentUnits" "userSpaceOnUse")
      (du/set-attr! mask-g "filter" (str "url(#" filter-id ")"))
      (du/set-attr! mask-core "fill" "white")
      (.appendChild mask-g mask-core)
      (.appendChild mask-el mask-g)
      (.appendChild defs mask-el)
      (gobj/set el "__xLiquidGlassMaskG" mask-g)
      (gobj/set el "__xLiquidGlassMaskCore" mask-core)
      (gobj/set el "__xLiquidGlassMaskSats" #js [])
      (gobj/set el "__xLiquidGlassMaskId" mask-id))

    ;; Glass — masked to blob shape via SVG mask
    (du/set-attr! glass "part" "glass")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. glass -style -mask) mask-ref)
      (set! (.. glass -style -webkitMask) mask-ref))

    ;; Grain — texture overlay masked to blob shape
    (du/set-attr! grain "part" "grain")
    (set! (.. grain -style -filter)
          (str "url(#" (gobj/get el "__xLiquidGlassGrainId") ")"))
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. grain -style -mask) mask-ref)
      (set! (.. grain -style -webkitMask) mask-ref))

    ;; Gradient — masked to blob shape
    (du/set-attr! grad "part" "gradient")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. grad -style -mask) mask-ref)
      (set! (.. grad -style -webkitMask) mask-ref))

    ;; Specular — masked to blob shape
    (du/set-attr! spec "part" "specular")
    (let [mask-ref (str "url(#" (gobj/get el "__xLiquidGlassMaskId") ")")]
      (set! (.. spec -style -mask) mask-ref)
      (set! (.. spec -style -webkitMask) mask-ref))

    ;; Content
    (du/set-attr! content "part" "content")
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
    (du/set-attr! el "data-entering" "")
    (js/setTimeout #(du/remove-attr! el "data-entering") 700)

    (du/setv! el k-refs
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
               :satellites #js []})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Satellite management ───────────────────────────────────────────────────
(defn- sync-satellites!
  "Ensure the SVG group and mask group each have exactly n satellite ellipses."
  [^js el n]
  (let [refs     (du/getv el k-refs)
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
          (du/set-attr! sat "class" (str "blob-sat-" i))
          (du/set-attr! sat "fill" "#ffffff")
          (.appendChild g sat)
          (.push sats sat))
        ;; Mirror in mask group
        (when mask-g
          (let [msat (.createElementNS js/document svg-ns "ellipse")]
            (du/set-attr! msat "fill" "white")
            (.appendChild mask-g msat)
            (.push mask-sats msat)))
        (recur (inc i))))))

;; ── Geometry initialisation ────────────────────────────────────────────────
(defn- init-geometry! [^js el w h]
  (let [m      (du/getv el k-model)
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
    (let [{:keys [core]} (du/getv el k-refs)
          ^js core core]
      (du/set-attr! core "cx" (str cx))
      (du/set-attr! core "cy" (str cy))
      (du/set-attr! core "rx" (str core-rx))
      (du/set-attr! core "ry" (str core-ry)))

    ;; Update mask core to match
    (when-let [^js mc (gobj/get el "__xLiquidGlassMaskCore")]
      (du/set-attr! mc "cx" (str cx))
      (du/set-attr! mc "cy" (str cy))
      (du/set-attr! mc "rx" (str core-rx))
      (du/set-attr! mc "ry" (str core-ry)))

    ;; Border uses slightly larger radii to encompass satellite area
    (let [outer-rx (* w 0.44)
          outer-ry (* h 0.44)]
      (when-let [^js bc (gobj/get el "__xLiquidGlassBorderCore")]
        (du/set-attr! bc "cx" (str cx))
        (du/set-attr! bc "cy" (str cy))
        (du/set-attr! bc "rx" (str outer-rx))
        (du/set-attr! bc "ry" (str outer-ry))))

    ;; Sync satellite count
    (sync-satellites! el n)

    ;; Set satellite sizes (visible + mask)
    (let [{:keys [satellites]} (du/getv el k-refs)
          ^js sats satellites
          ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
      (dotimes [i n]
        (let [^js sat (aget sats i)]
          (du/set-attr! sat "rx" (str sat-rx))
          (du/set-attr! sat "ry" (str sat-ry)))
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (du/set-attr! msat "rx" (str sat-rx))
            (du/set-attr! msat "ry" (str sat-ry))))))

    ;; Store rest positions and geometry
    (du/setv! el k-rest-x (aget result 0))
    (du/setv! el k-rest-y (aget result 1))))

;; ── Apply model to DOM ─────────────────────────────────────────────────────
(defn- apply-model! [^js el m]
  (let [refs (du/getv el k-refs)
        ^js blur-el (:blur refs)
        ^js glass   (:glass refs)
        ^js spec    (:specular refs)
        ^js core    (:core refs)
        ^js g       (:g refs)]

    ;; Update goo filter blur
    (du/set-attr! blur-el "stdDeviation" (str (:goo m)))

    ;; Update backdrop blur on glass div
    (.setProperty (.-style glass) model/css-blur (str (:blur m) "px"))

    ;; Fill ellipses with white — group opacity controls translucency
    (du/set-attr! core "fill" "#ffffff")
    (let [^js sats (:satellites refs)]
      (dotimes [i (.-length sats)]
        (du/set-attr! ^js (aget sats i) "fill" "#ffffff")))

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
              (du/set-attr! g "opacity" (.toFixed g-opacity 2))
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
            (du/set-attr! g "opacity" "0.35")
            (when grain-el (set! (.. grain-el -style -opacity) "0.4"))
            (set! (.. el -style -contain) ""))))

    ;; Specular visibility
    (when-not (:specular? m)
      (set! (.. spec -style -opacity) "0"))))

;; ── Render satellites ──────────────────────────────────────────────────────
(defn- render-satellites! [^js el t]
  (let [m       (du/getv el k-model)
        n       (:blobs m)
        speed   (:speed m)
        amp     (:amplitude m)
        w       (du/getv el k-width)
        h       (du/getv el k-height)
        ;; Displace relative to core radius, capped so satellites
        ;; stay within the goo filter merge range
        core-r  (* (js/Math.min w h) 0.32)
        max-disp (* amp core-r 0.6)
        ^js rx  (du/getv el k-rest-x)
        ^js ry  (du/getv el k-rest-y)
        {:keys [satellites]} (du/getv el k-refs)
        ^js sats satellites
        ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
    (dotimes [i n]
      (let [result (model/displace-satellite
                    (aget rx i) (aget ry i) i t speed max-disp)
            cx-str (.toFixed (aget result 0) 2)
            cy-str (.toFixed (aget result 1) 2)
            ^js sat (aget sats i)]
        ;; Hot path: rAF-driven, ~120 writes/sec across these four lines.
        ;; Use untraced variants to keep the trace recorder readable —
        ;; setup attribute writes elsewhere in this file still go through
        ;; the traced set-attr! since they fire once at connect.
        (du/set-attr-untraced! sat "cx" cx-str)
        (du/set-attr-untraced! sat "cy" cy-str)
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (du/set-attr-untraced! msat "cx" cx-str)
            (du/set-attr-untraced! msat "cy" cy-str)))))))

;; ── Render specular ────────────────────────────────────────────────────────
(defn- render-specular! [^js el]
  (let [m    (du/getv el k-model)
        refs (du/getv el k-refs)
        ^js spec (:specular refs)]
    (when (:specular? m)
      (let [active (du/getv el k-pointer-active)
            px     (du/getv el k-pointer-x)
            py     (du/getv el k-pointer-y)
            w      (du/getv el k-width)
            h      (du/getv el k-height)
            size   (* (:specular-size m) (js/Math.min w h))
            color  (str "var(" model/css-specular-color ",rgba(255,255,255,0.6))")]
        (if active
          (do
            (set! (.. spec -style -opacity) (str (:specular-intensity m)))
            (set! (.. spec -style -background)
                  (str "radial-gradient(ellipse " size "px " size "px at "
                       (.toFixed px 1) "px " (.toFixed py 1) "px,"
                       color " 0%,transparent 100%)")))
          (set! (.. spec -style -opacity) "0"))))))

;; ── Render gradient drift ──────────────────────────────────────────────────
(defn- render-gradient! [^js el t]
  (let [m    (du/getv el k-model)
        refs (du/getv el k-refs)
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
               c2 " 0%,transparent 70%)"))))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [now        (js/performance.now)
          last-frame (du/getv el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          t          (+ (du/getv el k-time) dt)]

      (du/setv! el k-last-frame now)
      (du/setv! el k-time t)

      ;; Update satellite positions
      (render-satellites! el t)

      ;; Update specular highlight
      (render-specular! el)

      ;; Update gradient drift
      (render-gradient! el t)

      ;; Always continue — noise never settles
      (du/setv! el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el)))))))

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (du/setv! el k-last-frame (js/performance.now))
    (du/setv! el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv! el k-raf nil)))

;; ── Render static (disabled / reduced-motion) ──────────────────────────────
(defn- render-static! [^js el]
  (let [m      (du/getv el k-model)
        n      (:blobs m)
        ^js rx (du/getv el k-rest-x)
        ^js ry (du/getv el k-rest-y)
        {:keys [satellites]} (du/getv el k-refs)
        ^js sats satellites
        ^js mask-sats (gobj/get el "__xLiquidGlassMaskSats")]
    (dotimes [i n]
      (let [cx-str (.toFixed (aget rx i) 2)
            cy-str (.toFixed (aget ry i) 2)]
        (when (< i (.-length sats))
          (let [^js sat (aget sats i)]
            (du/set-attr! sat "cx" cx-str)
            (du/set-attr! sat "cy" cy-str)))
        (when (and mask-sats (< i (.-length mask-sats)))
          (let [^js msat (aget mask-sats i)]
            (du/set-attr! msat "cx" cx-str)
            (du/set-attr! msat "cy" cy-str)))))))

;; ── ResizeObserver ──────────────────────────────────────────────────────────
(defn- on-resize! [^js el ^js entries]
  (when (pos? (.-length entries))
    (let [^js entry (aget entries 0)
          ^js cr    (.-contentRect entry)
          w         (.-width cr)
          h         (.-height cr)]
      (when (and (> w 0) (> h 0))
        (du/setv! el k-width w)
        (du/setv! el k-height h)
        ;; Update SVG viewBox and mask bounds
        (let [{:keys [svg]} (du/getv el k-refs)
              ^js svg svg]
          (du/set-attr! svg "viewBox" (str "0 0 " w " " h))
          ;; Mask must cover same area for userSpaceOnUse to work
          (when-let [^js mask-el (.querySelector svg (str "#" (gobj/get el "__xLiquidGlassMaskId")))]
            (du/set-attr! mask-el "x" "0")
            (du/set-attr! mask-el "y" "0")
            (du/set-attr! mask-el "width" (str w))
            (du/set-attr! mask-el "height" (str h))))
        ;; Reinitialise geometry with new dimensions
        (init-geometry! el w h)
        (let [m (du/getv el k-model)]
          (apply-model! el m)
          ;; Render
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-satellites! el (or (du/getv el k-time) 0.0))
                (start-animation! el))))))))

;; ── Pointer event handlers ──────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [^js rect (.getBoundingClientRect el)]
    (du/setv! el k-pointer-x (- (.-clientX e) (.-left rect)))
    (du/setv! el k-pointer-y (- (.-clientY e) (.-top rect)))))

(defn- on-pointerenter [^js el]
  (du/setv! el k-pointer-active true))

(defn- on-pointerleave [^js el]
  (du/setv! el k-pointer-active false))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [move-fn  (fn [^js e] (on-pointermove el e))
        enter-fn (fn [] (on-pointerenter el))
        leave-fn (fn [] (on-pointerleave el))]
    (du/setv! el k-handlers
              #js {"move" move-fn "enter" enter-fn "leave" leave-fn})
    (.addEventListener el "pointermove" move-fn #js {:passive true})
    (.addEventListener el "pointerenter" enter-fn)
    (.addEventListener el "pointerleave" leave-fn)))

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (du/getv el k-handlers)]
    (.removeEventListener el "pointermove" (gobj/get hdl "move"))
    (.removeEventListener el "pointerenter" (gobj/get hdl "enter"))
    (.removeEventListener el "pointerleave" (gobj/get hdl "leave"))
    (du/setv! el k-handlers nil)))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:blobs-raw              (du/get-attr el model/attr-blobs)
    :speed-raw              (du/get-attr el model/attr-speed)
    :amplitude-raw          (du/get-attr el model/attr-amplitude)
    :blur-raw               (du/get-attr el model/attr-blur)
    :goo-raw                (du/get-attr el model/attr-goo)
    :tint-raw               (du/get-attr el model/attr-tint)
    :specular-attr          (du/get-attr el model/attr-specular)
    :specular-size-raw      (du/get-attr el model/attr-specular-size)
    :specular-intensity-raw (du/get-attr el model/attr-specular-intensity)
    :disabled-attr          (du/get-attr el model/attr-disabled)
    :mode-raw               (du/get-attr el model/attr-mode)
    :frost-raw              (du/get-attr el model/attr-frost)
    :color-1-raw            (du/get-attr el model/attr-color-1)
    :color-2-raw            (du/get-attr el model/attr-color-2)}))

;; ── Update from attributes (render-pipeline with cache-at-tail) ───────────
(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (let [w (du/getv el k-width)
            h (du/getv el k-height)]
        (when (and w h (> w 0) (> h 0))
          (init-geometry! el w h)
          (apply-model! el new-m)
          (if (or (:disabled? new-m) (prefers-reduced-motion?))
            (do (stop-animation! el)
                (render-static! el))
            (do (render-satellites! el (or (du/getv el k-time) 0.0))
                (start-animation! el)))))
      (du/setv! el k-model new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- parse-mode-prop [v]
  (if (= v "submerged") "submerged" "surface"))

(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto model/attr-blobs         model/attr-blobs              model/parse-blobs)
  (du/define-parsed-prop! proto model/attr-speed         model/attr-speed              model/parse-speed)
  (du/define-parsed-prop! proto model/attr-amplitude     model/attr-amplitude          model/parse-amplitude)
  (du/define-parsed-prop! proto model/attr-blur          model/attr-blur               model/parse-blur)
  (du/define-parsed-prop! proto model/attr-goo           model/attr-goo                model/parse-goo)
  (du/define-string-prop! proto model/attr-tint          model/attr-tint)
  (du/define-bool-prop!   proto model/attr-specular      model/attr-specular)
  (du/define-parsed-prop! proto "specularSize"           model/attr-specular-size      model/parse-specular-size)
  (du/define-parsed-prop! proto "specularIntensity"      model/attr-specular-intensity model/parse-specular-intensity)
  (du/define-bool-prop!   proto model/attr-disabled      model/attr-disabled)
  (du/define-parsed-prop! proto model/attr-mode          model/attr-mode               parse-mode-prop)
  (du/define-parsed-prop! proto model/attr-frost         model/attr-frost              model/parse-frost)
  (du/define-string-prop! proto "color1"                 model/attr-color-1)
  (du/define-string-prop! proto "color2"                 model/attr-color-2))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-time 0.0)
    (du/setv! el k-pointer-active false)
    (du/setv! el k-pointer-x 0.0)
    (du/setv! el k-pointer-y 0.0)
    (du/setv! el k-grad-time 0.0)
    (ensure-refs! el)
    (apply-model! el m)
    ;; Set up ResizeObserver
    (let [ro (js/ResizeObserver.
              (fn [^js entries] (on-resize! el entries)))]
      (du/setv! el k-ro ro)
      (.observe ro el))
    ;; Add pointer event listeners
    (remove-listeners! el)
    (add-listeners! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el)
  (when-let [^js ro (du/getv el k-ro)]
    (.disconnect ro)
    (du/setv! el k-ro nil)))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-refs)
      (update-from-attrs! el))))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
