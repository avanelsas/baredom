(ns baredom.components.x-kinetic-typography.x-kinetic-typography
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [clojure.string :as str]
            [baredom.components.x-kinetic-typography.model :as model]))

;; ── Constants ────────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")
(def ^:private xlink-ns "http://www.w3.org/1999/xlink")

;; ── Instance-field keys ──────────────────────────────────────────────────
(def ^:private k-initialized      "__xKineticTypographyInit")
(def ^:private k-model            "__xKineticTypographyModel")
(def ^:private k-container        "__xKineticTypographyContainer")
(def ^:private k-svg              "__xKineticTypographySvg")
(def ^:private k-path-el          "__xKineticTypographyPath")
(def ^:private k-path-line        "__xKineticTypographyPathLine")
(def ^:private k-text-el          "__xKineticTypographyText")
(def ^:private k-text-path        "__xKineticTypographyTextPath")
(def ^:private k-animate-el       "__xKineticTypographyAnimate")
(def ^:private k-sr-only          "__xKineticTypographySrOnly")
(def ^:private k-path-id          "__xKineticTypographyPathId")
(def ^:private k-echo-els         "__xKineticTypographyEchoEls")
(def ^:private k-crawl-viewport   "__xKineticTypographyCrawlViewport")
(def ^:private k-crawl-text       "__xKineticTypographyCrawlText")

;; ── String-literal constants ─────────────────────────────────────────────
(def ^:private attr-part            "part")
(def ^:private attr-role            "role")
(def ^:private attr-aria-hidden     "aria-hidden")
(def ^:private attr-aria-label      "aria-label")
(def ^:private attr-data-preset     "data-preset")
(def ^:private attr-data-animation  "data-animation")
(def ^:private attr-data-effect     "data-effect")
(def ^:private attr-data-direction  "data-direction")
(def ^:private val-true             "true")
(def ^:private val-crawl            "crawl")
(def ^:private val-reverse          "reverse")
(def ^:private val-none             "none")
(def ^:private val-color-shift      "color-shift")
(def ^:private effect-size-gradient "size-gradient")
(def ^:private effect-color-wave    "color-wave")
(def ^:private role-img             "img")
(def ^:private role-presentation    "presentation")

;; ── Styles ───────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-kinetic-typography-color:var(--x-color-text,currentColor);"
   "--x-kinetic-typography-font-family:system-ui,sans-serif;"
   "--x-kinetic-typography-font-size:24px;"
   "--x-kinetic-typography-font-weight:400;"
   "--x-kinetic-typography-letter-spacing:0;"
   "--x-kinetic-typography-opacity:1;"
   "--x-kinetic-typography-stroke:none;"
   "--x-kinetic-typography-stroke-width:0;"
   "--x-kinetic-typography-duration:10s;"
   "--x-kinetic-typography-timing:linear;"
   "--x-kinetic-typography-color-shift-from:var(--x-color-text,currentColor);"
   "--x-kinetic-typography-color-shift-to:var(--x-color-primary,#3b82f6);"
   "--x-kinetic-typography-path-stroke:none;"
   "--x-kinetic-typography-path-stroke-width:0;"
   "--x-kinetic-typography-crawl-perspective:400px;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-kinetic-typography-color:var(--x-color-text,rgba(255,255,255,0.87));}}"

   "[part=container]{"
   "width:100%;height:100%;position:relative;}"

   "[part=svg]{"
   "width:100%;height:100%;overflow:visible;}"

   "[part=text]{"
   "fill:var(--x-kinetic-typography-color);"
   "font-family:var(--x-kinetic-typography-font-family);"
   "font-size:var(--x-kinetic-typography-font-size);"
   "font-weight:var(--x-kinetic-typography-font-weight);"
   "letter-spacing:var(--x-kinetic-typography-letter-spacing);"
   "opacity:var(--x-kinetic-typography-opacity);"
   "stroke:var(--x-kinetic-typography-stroke);"
   "stroke-width:var(--x-kinetic-typography-stroke-width);}"

   ;; Echo text inherits base text styles
   "[part=text-echo]{"
   "fill:var(--x-kinetic-typography-color);"
   "font-family:var(--x-kinetic-typography-font-family);"
   "font-weight:var(--x-kinetic-typography-font-weight);"
   "letter-spacing:var(--x-kinetic-typography-letter-spacing);"
   "stroke:var(--x-kinetic-typography-stroke);"
   "stroke-width:var(--x-kinetic-typography-stroke-width);}"

   ;; ── Effect keyframes ──────────────────────────────────────────────

   "@keyframes xkt-opacity-wave{"
   "0%,100%{opacity:var(--x-kinetic-typography-opacity);}"
   "50%{opacity:0.3;}}"

   "@keyframes xkt-size-pulse{"
   "0%,100%{font-size:var(--x-kinetic-typography-font-size);}"
   "50%{font-size:calc(var(--x-kinetic-typography-font-size) * 1.15);}}"

   "@keyframes xkt-spacing-breathe{"
   "0%,100%{letter-spacing:var(--x-kinetic-typography-letter-spacing);}"
   "50%{letter-spacing:0.15em;}}"

   "@keyframes xkt-color-shift{"
   "0%,100%{fill:var(--x-kinetic-typography-color-shift-from);}"
   "50%{fill:var(--x-kinetic-typography-color-shift-to);}}"

   ;; Color wave — used per-tspan with staggered animation-delay
   "@keyframes xkt-color-wave{"
   "0%,100%{fill:var(--x-kinetic-typography-color-shift-from);}"
   "50%{fill:var(--x-kinetic-typography-color-shift-to);}}"

   ;; ── Effect selectors (space-separated token matching) ─────────────

   ":host([data-effect~=opacity-wave]) [part=text]{"
   "animation:xkt-opacity-wave var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=size-pulse]) [part=text]{"
   "animation:xkt-size-pulse var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=spacing-breathe]) [part=text]{"
   "animation:xkt-spacing-breathe var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=color-shift]) [part=text]{"
   "animation:xkt-color-shift var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ;; Combined effects — when multiple are active, combine animations
   ":host([data-effect~=opacity-wave][data-effect~=color-shift]) [part=text]{"
   "animation:xkt-opacity-wave var(--x-kinetic-typography-duration) ease-in-out infinite,"
   "xkt-color-shift var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=opacity-wave][data-effect~=spacing-breathe]) [part=text]{"
   "animation:xkt-opacity-wave var(--x-kinetic-typography-duration) ease-in-out infinite,"
   "xkt-spacing-breathe var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=color-shift][data-effect~=spacing-breathe]) [part=text]{"
   "animation:xkt-color-shift var(--x-kinetic-typography-duration) ease-in-out infinite,"
   "xkt-spacing-breathe var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ":host([data-effect~=opacity-wave][data-effect~=color-shift][data-effect~=spacing-breathe]) [part=text]{"
   "animation:xkt-opacity-wave var(--x-kinetic-typography-duration) ease-in-out infinite,"
   "xkt-color-shift var(--x-kinetic-typography-duration) ease-in-out infinite,"
   "xkt-spacing-breathe var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   ;; Path decorative stroke
   "[part=path-line]{"
   "fill:none;"
   "stroke:var(--x-kinetic-typography-path-stroke);"
   "stroke-width:var(--x-kinetic-typography-path-stroke-width);}"

   ;; Screen reader only
   ".sr-only{"
   "position:absolute;width:1px;height:1px;padding:0;margin:-1px;"
   "overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0;}"

   ;; ── Crawl mode ────────────────────────────────────────────────────

   "[part=crawl-viewport]{display:none;}"

   ":host([data-preset=crawl]) [part=svg]{display:none;}"
   ":host([data-preset=crawl]) [part=crawl-viewport]{"
   "display:block;"
   "perspective:var(--x-kinetic-typography-crawl-perspective);"
   "height:100%;"
   "overflow:hidden;"
   "position:relative;}"

   ":host([data-preset=crawl]) [part=crawl-text]{"
   "position:absolute;"
   "bottom:0;left:50%;"
   "transform:translateX(-50%) rotateX(25deg);"
   "transform-origin:50% 100%;"
   "text-align:center;"
   "font-family:var(--x-kinetic-typography-font-family);"
   "font-size:var(--x-kinetic-typography-font-size);"
   "font-weight:var(--x-kinetic-typography-font-weight);"
   "color:var(--x-kinetic-typography-color);"
   "letter-spacing:var(--x-kinetic-typography-letter-spacing);"
   "white-space:pre-line;"
   "line-height:1.6;"
   "width:80%;}"

   "@keyframes xkt-crawl{"
   "0%{bottom:-100%;opacity:1;}"
   "90%{opacity:1;}"
   "100%{bottom:200%;opacity:0;}}"

   ":host([data-preset=crawl][data-animation=scroll]) [part=crawl-text]{"
   "animation:xkt-crawl var(--x-kinetic-typography-duration) linear infinite;}"

   ":host([data-preset=crawl][data-animation=scroll][data-direction=reverse]) [part=crawl-text]{"
   "animation-direction:reverse;}"

   ;; Crawl color-shift (uses color instead of fill)
   ":host([data-preset=crawl][data-effect~=color-shift]) [part=crawl-text]{"
   "animation:xkt-crawl var(--x-kinetic-typography-duration) linear infinite,"
   "xkt-crawl-color-shift var(--x-kinetic-typography-duration) ease-in-out infinite;}"

   "@keyframes xkt-crawl-color-shift{"
   "0%,100%{color:var(--x-kinetic-typography-color-shift-from);}"
   "50%{color:var(--x-kinetic-typography-color-shift-to);}}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   ":host([data-effect]) [part=text]{animation:none;}"
   ":host([data-preset=crawl]) [part=crawl-text]{animation:none;}}"))

;; ── Helpers ──────────────────────────────────────────────────────────────
(defn- svg-el [tag]
  (.createElementNS js/document svg-ns tag))

(defn- prefers-reduced-motion? []
  (.-matches (.matchMedia js/window "(prefers-reduced-motion: reduce)")))

;; ── DOM initialisation ───────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root            (.attachShadow el #js {:mode "open"})
        style-el        (.createElement js/document "style")
        container       (.createElement js/document "div")
        svg             (svg-el "svg")
        defs            (svg-el "defs")
        path-el         (svg-el "path")
        path-line       (svg-el "path")
        text-el         (svg-el "text")
        text-path       (svg-el "textPath")
        sr-only         (.createElement js/document "span")
        crawl-viewport  (.createElement js/document "div")
        crawl-text      (.createElement js/document "div")
        path-id         (str "xkt-" (random-uuid))]

    (set! (.-textContent style-el) style-text)
    (.setAttribute container attr-part "container")
    (.setAttribute svg       attr-part "svg")
    (.setAttribute svg       attr-aria-hidden val-true)
    (.setAttribute svg       "focusable" "false")

    ;; Path definition in <defs>
    (.setAttribute path-el "id" path-id)
    (.appendChild defs path-el)

    ;; Decorative path line (for optional visible path)
    (.setAttribute path-line attr-part "path-line")
    (.appendChild svg path-line)

    ;; Text on path
    (.setAttribute text-el   attr-part "text")
    (.setAttribute text-path "href" (str "#" path-id))
    (.appendChild text-el text-path)

    (.appendChild svg defs)
    (.appendChild svg text-el)

    ;; Crawl mode elements
    (.setAttribute crawl-viewport attr-part "crawl-viewport")
    (.setAttribute crawl-text     attr-part "crawl-text")
    (.appendChild crawl-viewport crawl-text)

    ;; SR-only text
    (.add (.-classList sr-only) "sr-only")
    (.setAttribute sr-only attr-part "sr-only")

    (.appendChild container svg)
    (.appendChild container crawl-viewport)
    (.appendChild container sr-only)
    (.appendChild root style-el)
    (.appendChild root container)

    ;; Store refs
    (du/setv! el k-container      container)
    (du/setv! el k-svg            svg)
    (du/setv! el k-path-el        path-el)
    (du/setv! el k-path-line      path-line)
    (du/setv! el k-text-el        text-el)
    (du/setv! el k-text-path      text-path)
    (du/setv! el k-sr-only        sr-only)
    (du/setv! el k-path-id        path-id)
    (du/setv! el k-crawl-viewport crawl-viewport)
    (du/setv! el k-crawl-text     crawl-text)
    (du/setv! el k-echo-els       #js [])
    (du/setv! el k-initialized    true)))

;; ── Read model ───────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:text         (du/get-attr el model/attr-text)
    :path         (du/get-attr el model/attr-path)
    :preset       (du/get-attr el model/attr-preset)
    :animation    (du/get-attr el model/attr-animation)
    :speed        (du/get-attr el model/attr-speed)
    :direction    (du/get-attr el model/attr-direction)
    :effect       (du/get-attr el model/attr-effect)
    :font-size    (du/get-attr el model/attr-font-size)
    :start-size   (du/get-attr el model/attr-start-size)
    :end-size     (du/get-attr el model/attr-end-size)
    :repeat       (du/get-attr el model/attr-repeat)
    :echo-count   (du/get-attr el model/attr-echo-count)
    :echo-delay   (du/get-attr el model/attr-echo-delay)
    :echo-opacity (du/get-attr el model/attr-echo-opacity)
    :echo-scale   (du/get-attr el model/attr-echo-scale)}))

;; ── Build repeated text ──────────────────────────────────────────────────
(defn- build-text-content [text repeat-count]
  (if (<= repeat-count 1)
    text
    (apply str (interpose " · " (cljs.core/repeat repeat-count text)))))

;; ── Per-character tspan rendering ────────────────────────────────────────
(defn- apply-tspans!
  "Render per-character <tspan> elements for size-gradient and/or color-wave effects."
  [^js text-path-el text effects duration-s start-size end-size]
  (set! (.-textContent text-path-el) "")
  (let [chars          (vec text)
        n              (count chars)
        has-gradient   (contains? effects effect-size-gradient)
        has-color-wave (contains? effects effect-color-wave)
        grad-start     (when has-gradient (js/parseFloat start-size))
        grad-end       (when has-gradient (js/parseFloat end-size))
        grad-unit      (when has-gradient
                         (or (re-find #"[a-z%]+" (or start-size "px")) "px"))
        wave-stagger   (when has-color-wave
                         (if (> n 0) (/ duration-s n) 0))]
    (doseq [i (range n)]
      (let [tspan (svg-el "tspan")]
        ;; Size gradient
        (when (and has-gradient grad-start grad-end)
          (let [t    (if (> n 1) (/ i (dec n)) 0)
                size (+ grad-start (* t (- grad-end grad-start)))]
            (.setAttribute tspan "font-size" (str size grad-unit))))
        ;; Color wave — inline animation with staggered delay
        (when has-color-wave
          (set! (.-cssText (.-style tspan))
                (str "animation:xkt-color-wave " duration-s "s ease-in-out infinite;"
                     "animation-delay:" (* i wave-stagger) "s;")))
        (set! (.-textContent tspan) (nth chars i))
        (.appendChild text-path-el tspan)))))

;; ── SMIL animate management ──────────────────────────────────────────────
(defn- remove-animate! [^js el]
  (let [^js anim (du/getv el k-animate-el)]
    (when anim
      (.remove anim)
      (du/setv! el k-animate-el nil))))

(defn- build-animate-el
  "Create a SMIL <animate> element for startOffset with optional begin delay."
  [animation direction duration-s begin-delay-s]
  (let [anim (svg-el "animate")
        dur  (str duration-s "s")
        dir  (if (= direction val-reverse) val-reverse "normal")]
    (.setAttribute anim "attributeName" "startOffset")
    (.setAttribute anim "repeatCount" "indefinite")
    (.setAttribute anim "dur" dur)
    (when (pos? begin-delay-s)
      (.setAttribute anim "begin" (str begin-delay-s "s")))
    (case animation
      "scroll"    (do (.setAttribute anim "from" (if (= dir val-reverse) "100%" "0%"))
                      (.setAttribute anim "to"   (if (= dir val-reverse) "0%" "100%")))
      "bounce"    (do (.setAttribute anim "values" (if (= dir val-reverse) "100%;0%;100%" "0%;100%;0%"))
                      (.setAttribute anim "calcMode" "spline")
                      (.setAttribute anim "keySplines" "0.42 0 0.58 1;0.42 0 0.58 1"))
      "oscillate" (do (.setAttribute anim "values" (if (= dir val-reverse) "120%;-20%;120%" "-20%;120%;-20%"))
                      (.setAttribute anim "calcMode" "spline")
                      (.setAttribute anim "keySplines" "0.42 0 0.58 1;0.42 0 0.58 1"))
      nil)
    anim))

(defn- create-animate! [^js el ^js text-path-el animation direction duration-s]
  (remove-animate! el)
  (when (and (not= animation val-none) (not (prefers-reduced-motion?)))
    (let [anim (build-animate-el animation direction duration-s 0)]
      (.appendChild text-path-el anim)
      (du/setv! el k-animate-el anim))))

;; ── Echo management ──────────────────────────────────────────────────────
(defn- clear-echoes! [^js el]
  (let [^js echo-els (du/getv el k-echo-els)]
    (when echo-els
      (dotimes [i (.-length echo-els)]
        (let [^js entry (aget echo-els i)]
          (.remove (aget entry "textEl"))))
      (du/setv! el k-echo-els #js []))))

(defn- render-echoes!
  [^js el ^js svg display-text echo-count echo-delay echo-opacity echo-scale
   animation direction duration-s]
  (clear-echoes! el)
  (when (pos? echo-count)
    (let [path-id     (du/getv el k-path-id)
          echo-els    #js []
          add-animate (and (not= animation val-none) (not (prefers-reduced-motion?)))
          ;; Convert echo-delay (seconds) to a percentage offset along the path.
          ;; Each echo is displaced by delay/duration * 100 percent per index.
          offset-pct  (if (pos? duration-s)
                        (* (/ echo-delay duration-s) 100.0)
                        5.0)]
      (loop [i 0]
        (when (< i echo-count)
          (let [idx       (inc i)
                tel       (svg-el "text")
                tp        (svg-el "textPath")
                opacity   (.pow js/Math echo-opacity idx)
                scale-val (.pow js/Math echo-scale idx)
                ;; Displace each echo further along the path
                start-off (str "-" (* offset-pct idx) "%")]
            (.setAttribute tel attr-part "text-echo")
            (.setAttribute tel "opacity" (str opacity))
            (.setAttribute tel "font-size"
                           (str "calc(var(--x-kinetic-typography-font-size) * " scale-val ")"))
            (.setAttributeNS tp xlink-ns "href" (str "#" path-id))
            (.setAttribute tp "href" (str "#" path-id))
            (.setAttribute tp "startOffset" start-off)
            (set! (.-textContent tp) display-text)
            (when add-animate
              (let [anim (build-animate-el animation direction duration-s
                                          (* echo-delay idx))]
                (.appendChild tp anim)))
            (.appendChild tel tp)
            (.appendChild svg tel)
            (.push echo-els #js {"textEl" tel}))
          (recur (inc i))))
      (du/setv! el k-echo-els echo-els))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────
(defn- apply-crawl-mode!
  "Crawl mode: hide the SVG, write the text into the crawl div, and set
  data-* attributes for the CSS selectors that drive the marquee."
  [^js el ^js crawl-text-el {:keys [text animation direction effects]}]
  (du/set-attr! el attr-data-preset val-crawl)
  (set! (.-textContent crawl-text-el) text)
  ;; Remove SVG animation and echoes
  (remove-animate! el)
  (clear-echoes! el)
  ;; Animation data attribute for CSS selectors
  (if (not= animation val-none)
    (du/set-attr! el attr-data-animation animation)
    (du/remove-attr! el attr-data-animation))
  ;; Direction data attribute for crawl CSS
  (if (= direction val-reverse)
    (du/set-attr! el attr-data-direction val-reverse)
    (du/remove-attr! el attr-data-direction))
  ;; Only color-shift effect applies in crawl mode
  (if (contains? effects val-color-shift)
    (du/set-attr! el attr-data-effect val-color-shift)
    (du/remove-attr! el attr-data-effect)))

(defn- apply-svg-path!
  "Update the SVG viewBox, the textPath path, and the decorative path-line."
  [^js svg ^js path-el ^js path-line {:keys [view-box path-d]}]
  (when view-box (.setAttribute svg "viewBox" view-box))
  (when path-d (.setAttribute path-el "d" path-d))
  (when (and path-line path-d)
    (.setAttribute path-line "d" path-d)))

(defn- apply-text-content!
  "Render the display text — either flat textContent or per-character tspans
  for size-gradient / color-wave — then render the echo chain."
  [^js el ^js svg ^js text-path
   {:keys [text repeat effects duration-s start-size end-size
           echo-count echo-delay echo-opacity echo-scale animation direction]}]
  (let [display-text   (build-text-content text repeat)
        has-gradient   (contains? effects effect-size-gradient)
        has-color-wave (contains? effects effect-color-wave)
        needs-tspans   (or (and has-gradient start-size end-size (not= text ""))
                           (and has-color-wave (not= text "")))]
    (if needs-tspans
      (apply-tspans! text-path display-text effects duration-s start-size end-size)
      (set! (.-textContent text-path) display-text))
    (render-echoes! el svg display-text echo-count echo-delay echo-opacity
                    echo-scale animation direction duration-s)))

(defn- apply-text-font-size! [^js text-el {:keys [font-size]}]
  (if font-size
    (.setProperty (.-style text-el) "font-size" font-size)
    (.removeProperty (.-style text-el) "font-size")))

(defn- apply-svg-data-attrs!
  "Set data-effect / data-animation host attributes for path-mode CSS selectors."
  [^js el {:keys [effects animation]}]
  (let [css-effects (disj effects effect-size-gradient effect-color-wave)]
    (if (seq css-effects)
      (du/set-attr!    el attr-data-effect (str/join " " (sort css-effects)))
      (du/remove-attr! el attr-data-effect)))
  (if (not= animation val-none)
    (du/set-attr!    el attr-data-animation animation)
    (du/remove-attr! el attr-data-animation)))

(defn- apply-path-mode!
  "Path mode: SVG with text-on-path, tspans, echoes, and SMIL animation."
  [^js el ^js svg ^js path-el ^js path-line ^js text-el ^js text-path
   {:keys [animation direction duration-s] :as m}]
  (du/remove-attr! el attr-data-preset)
  (du/remove-attr! el attr-data-direction)
  (apply-svg-path!        svg path-el path-line m)
  (apply-text-content!    el svg text-path m)
  (apply-text-font-size!  text-el m)
  (create-animate!        el text-path animation direction duration-s)
  (apply-svg-data-attrs!  el m))

(defn- apply-a11y! [^js el ^js sr-only {:keys [text]}]
  (set! (.-textContent sr-only) text)
  (if (= text "")
    (do (du/set-attr! el attr-role        role-presentation)
        (du/set-attr! el attr-aria-hidden val-true))
    (do (du/set-attr! el attr-role        role-img)
        (du/set-attr! el attr-aria-label  text))))

(defn- apply-model! [^js el m]
  (let [^js svg           (du/getv el k-svg)
        ^js path-el       (du/getv el k-path-el)
        ^js path-line     (du/getv el k-path-line)
        ^js text-el       (du/getv el k-text-el)
        ^js text-path     (du/getv el k-text-path)
        ^js sr-only       (du/getv el k-sr-only)
        ^js crawl-text-el (du/getv el k-crawl-text)]
    (if (:crawl? m)
      (apply-crawl-mode! el crawl-text-el m)
      (apply-path-mode!  el svg path-el path-line text-el text-path m))
    (apply-a11y! el sr-only m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Lifecycle ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-initialized)
    (init-dom! el))
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-animate! el)
  (clear-echoes! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-initialized)
      (update-from-attrs! el))))

;; ── Property accessors ───────────────────────────────────────────────────
(defn- install-string-accessor! [^js proto attr-name prop-name default-val]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr-name) default-val)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this attr-name v)
                                          (.removeAttribute this attr-name))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (install-string-accessor! proto model/attr-text         "text"        "")
  (install-string-accessor! proto model/attr-path         "path"        nil)
  (install-string-accessor! proto model/attr-preset       "preset"      model/default-preset)
  (install-string-accessor! proto model/attr-animation    "animation"   model/default-animation)
  (install-string-accessor! proto model/attr-speed        "speed"       (str model/default-speed))
  (install-string-accessor! proto model/attr-direction    "direction"   model/default-direction)
  (install-string-accessor! proto model/attr-effect       "effect"      val-none)
  (install-string-accessor! proto model/attr-font-size    "fontSize"    nil)
  (install-string-accessor! proto model/attr-start-size   "startSize"   nil)
  (install-string-accessor! proto model/attr-end-size     "endSize"     nil)
  (install-string-accessor! proto model/attr-repeat       "repeat"      (str model/default-repeat))
  (install-string-accessor! proto model/attr-echo-count   "echoCount"   (str model/default-echo-count))
  (install-string-accessor! proto model/attr-echo-delay   "echoDelay"   (str model/default-echo-delay))
  (install-string-accessor! proto model/attr-echo-opacity "echoOpacity" (str model/default-echo-opacity))
  (install-string-accessor! proto model/attr-echo-scale   "echoScale"   (str model/default-echo-scale)))

;; ── Public API ───────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
