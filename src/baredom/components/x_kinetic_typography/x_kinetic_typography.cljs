(ns baredom.components.x-kinetic-typography.x-kinetic-typography
  (:require [clojure.string :as str]
            [goog.object :as gobj]
            [baredom.components.x-kinetic-typography.model :as model]))

;; ── Constants ────────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Instance-field keys ──────────────────────────────────────────────────
(def ^:private k-initialized      "__xKineticTypographyInit")
(def ^:private k-container        "__xKineticTypographyContainer")
(def ^:private k-svg              "__xKineticTypographySvg")
(def ^:private k-path-el          "__xKineticTypographyPath")
(def ^:private k-text-el          "__xKineticTypographyText")
(def ^:private k-text-path        "__xKineticTypographyTextPath")
(def ^:private k-animate-el       "__xKineticTypographyAnimate")
(def ^:private k-sr-only          "__xKineticTypographySrOnly")
(def ^:private k-path-id          "__xKineticTypographyPathId")
(def ^:private k-echo-els         "__xKineticTypographyEchoEls")
(def ^:private k-crawl-viewport   "__xKineticTypographyCrawlViewport")
(def ^:private k-crawl-text       "__xKineticTypographyCrawlText")

;; ── Styles ───────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-kinetic-typography-color:currentColor;"
   "--x-kinetic-typography-font-family:system-ui,sans-serif;"
   "--x-kinetic-typography-font-size:24px;"
   "--x-kinetic-typography-font-weight:400;"
   "--x-kinetic-typography-letter-spacing:0;"
   "--x-kinetic-typography-opacity:1;"
   "--x-kinetic-typography-stroke:none;"
   "--x-kinetic-typography-stroke-width:0;"
   "--x-kinetic-typography-duration:10s;"
   "--x-kinetic-typography-timing:linear;"
   "--x-kinetic-typography-color-shift-from:currentColor;"
   "--x-kinetic-typography-color-shift-to:var(--x-color-primary,#3b82f6);"
   "--x-kinetic-typography-path-stroke:none;"
   "--x-kinetic-typography-path-stroke-width:0;"
   "--x-kinetic-typography-crawl-perspective:400px;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-kinetic-typography-color:rgba(255,255,255,0.87);}}"

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
    (.setAttribute container "part" "container")
    (.setAttribute svg "part" "svg")
    (.setAttribute svg "aria-hidden" "true")
    (.setAttribute svg "focusable" "false")

    ;; Path definition in <defs>
    (.setAttribute path-el "id" path-id)
    (.appendChild defs path-el)

    ;; Decorative path line (for optional visible path)
    (.setAttribute path-line "part" "path-line")
    (.appendChild svg path-line)

    ;; Text on path
    (.setAttribute text-el "part" "text")
    (.setAttribute text-path "href" (str "#" path-id))
    (.appendChild text-el text-path)

    (.appendChild svg defs)
    (.appendChild svg text-el)

    ;; Crawl mode elements
    (.setAttribute crawl-viewport "part" "crawl-viewport")
    (.setAttribute crawl-text "part" "crawl-text")
    (.appendChild crawl-viewport crawl-text)

    ;; SR-only text
    (.add (.-classList sr-only) "sr-only")
    (.setAttribute sr-only "part" "sr-only")

    (.appendChild container svg)
    (.appendChild container crawl-viewport)
    (.appendChild container sr-only)
    (.appendChild root style-el)
    (.appendChild root container)

    ;; Store refs
    (gobj/set el k-container      container)
    (gobj/set el k-svg            svg)
    (gobj/set el k-path-el        path-el)
    (gobj/set el k-text-el        text-el)
    (gobj/set el k-text-path      text-path)
    (gobj/set el k-sr-only        sr-only)
    (gobj/set el k-path-id        path-id)
    (gobj/set el k-crawl-viewport crawl-viewport)
    (gobj/set el k-crawl-text     crawl-text)
    (gobj/set el k-echo-els       #js [])
    (gobj/set el k-initialized    true))
  nil)

;; ── Read inputs ──────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:text         (.getAttribute el model/attr-text)
   :path         (.getAttribute el model/attr-path)
   :preset       (.getAttribute el model/attr-preset)
   :animation    (.getAttribute el model/attr-animation)
   :speed        (.getAttribute el model/attr-speed)
   :direction    (.getAttribute el model/attr-direction)
   :effect       (.getAttribute el model/attr-effect)
   :font-size    (.getAttribute el model/attr-font-size)
   :start-size   (.getAttribute el model/attr-start-size)
   :end-size     (.getAttribute el model/attr-end-size)
   :repeat       (.getAttribute el model/attr-repeat)
   :echo-count   (.getAttribute el model/attr-echo-count)
   :echo-delay   (.getAttribute el model/attr-echo-delay)
   :echo-opacity (.getAttribute el model/attr-echo-opacity)
   :echo-scale   (.getAttribute el model/attr-echo-scale)})

;; ── Build repeated text ──────────────────────────────────────────────────
(defn- build-text-content [text repeat-count]
  (if (<= repeat-count 1)
    text
    (apply str (interpose " \u00B7 " (cljs.core/repeat repeat-count text)))))

;; ── Per-character tspan rendering ────────────────────────────────────────
(defn- apply-tspans!
  "Render per-character <tspan> elements for size-gradient and/or color-wave effects."
  [^js text-path-el text effects duration-s start-size end-size]
  (set! (.-textContent text-path-el) "")
  (let [chars          (vec text)
        n              (count chars)
        has-gradient   (contains? effects "size-gradient")
        has-color-wave (contains? effects "color-wave")
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
  (let [^js anim (gobj/get el k-animate-el)]
    (when anim
      (.remove anim)
      (gobj/set el k-animate-el nil))))

(defn- build-animate-el
  "Create a SMIL <animate> element for startOffset with optional begin delay."
  [animation direction duration-s begin-delay-s]
  (let [anim (svg-el "animate")
        dur  (str duration-s "s")
        dir  (if (= direction "reverse") "reverse" "normal")]
    (.setAttribute anim "attributeName" "startOffset")
    (.setAttribute anim "repeatCount" "indefinite")
    (.setAttribute anim "dur" dur)
    (when (pos? begin-delay-s)
      (.setAttribute anim "begin" (str begin-delay-s "s")))
    (case animation
      "scroll"    (do (.setAttribute anim "from" (if (= dir "reverse") "100%" "0%"))
                      (.setAttribute anim "to"   (if (= dir "reverse") "0%" "100%")))
      "bounce"    (do (.setAttribute anim "values" (if (= dir "reverse") "100%;0%;100%" "0%;100%;0%"))
                      (.setAttribute anim "calcMode" "spline")
                      (.setAttribute anim "keySplines" "0.42 0 0.58 1;0.42 0 0.58 1"))
      "oscillate" (do (.setAttribute anim "values" (if (= dir "reverse") "120%;-20%;120%" "-20%;120%;-20%"))
                      (.setAttribute anim "calcMode" "spline")
                      (.setAttribute anim "keySplines" "0.42 0 0.58 1;0.42 0 0.58 1"))
      nil)
    anim))

(defn- create-animate! [^js el ^js text-path-el animation direction duration-s]
  (remove-animate! el)
  (when (and (not= animation "none") (not (prefers-reduced-motion?)))
    (let [anim (build-animate-el animation direction duration-s 0)]
      (.appendChild text-path-el anim)
      (gobj/set el k-animate-el anim))))

;; ── Echo management ──────────────────────────────────────────────────────
(defn- clear-echoes! [^js el ^js svg]
  (let [^js echo-els (gobj/get el k-echo-els)]
    (when echo-els
      (dotimes [i (.-length echo-els)]
        (let [^js entry (aget echo-els i)]
          (.remove (aget entry "textEl"))))
      (gobj/set el k-echo-els #js []))))

(defn- render-echoes!
  [^js el ^js svg display-text echo-count echo-delay echo-opacity echo-scale
   animation direction duration-s font-size]
  (clear-echoes! el svg)
  (when (pos? echo-count)
    (let [path-id     (gobj/get el k-path-id)
          echo-els    #js []
          add-animate (and (not= animation "none") (not (prefers-reduced-motion?)))
          ;; Convert echo-delay (seconds) to a percentage offset along the path.
          ;; Each echo is displaced by delay/duration * 100 percent per index.
          offset-pct  (if (pos? duration-s)
                        (* (/ echo-delay duration-s) 100.0)
                        5.0)]
      (loop [i 0]
        (when (< i echo-count)
          (let [idx       (inc i)
                tel       (.createElementNS js/document svg-ns "text")
                tp        (.createElementNS js/document svg-ns "textPath")
                opacity   (.pow js/Math echo-opacity idx)
                scale-val (.pow js/Math echo-scale idx)
                ;; Displace each echo further along the path
                start-off (str "-" (* offset-pct idx) "%")]
            (.setAttribute tel "part" "text-echo")
            (.setAttribute tel "opacity" (str opacity))
            (.setAttribute tel "font-size"
                           (str "calc(var(--x-kinetic-typography-font-size) * " scale-val ")"))
            (.setAttributeNS tp "http://www.w3.org/1999/xlink" "href" (str "#" path-id))
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
      (gobj/set el k-echo-els echo-els))))

;; ── Render ───────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [text path-d view-box crawl? animation direction speed duration-s
                repeat effects font-size start-size end-size preset
                echo-count echo-delay echo-opacity echo-scale]}
        (model/derive-state (read-inputs el))

        ^js svg           (gobj/get el k-svg)
        ^js path-el       (gobj/get el k-path-el)
        ^js text-el       (gobj/get el k-text-el)
        ^js text-path     (gobj/get el k-text-path)
        ^js sr-only       (gobj/get el k-sr-only)
        ^js crawl-text-el (gobj/get el k-crawl-text)
        ^js path-line     (.querySelector (.-shadowRoot el) "[part=path-line]")]

    (if crawl?
      ;; ── Crawl mode ──────────────────────────────────────────────────
      (do
        (.setAttribute el "data-preset" "crawl")
        (set! (.-textContent crawl-text-el) text)
        ;; Remove SVG animation and echoes
        (remove-animate! el)
        (clear-echoes! el svg)
        ;; Animation data attribute for CSS selectors
        (if (not= animation "none")
          (.setAttribute el "data-animation" animation)
          (.removeAttribute el "data-animation"))
        ;; Direction data attribute for crawl CSS
        (if (= direction "reverse")
          (.setAttribute el "data-direction" "reverse")
          (.removeAttribute el "data-direction"))
        ;; Only color-shift effect applies in crawl mode
        (if (contains? effects "color-shift")
          (.setAttribute el "data-effect" "color-shift")
          (.removeAttribute el "data-effect")))

      ;; ── Path mode (SVG) ─────────────────────────────────────────────
      (do
        (.removeAttribute el "data-preset")
        (.removeAttribute el "data-direction")

        ;; Update SVG viewBox and path
        (when view-box (.setAttribute svg "viewBox" view-box))
        (when path-d (.setAttribute path-el "d" path-d))

        ;; Update decorative path line
        (when (and path-line path-d)
          (.setAttribute path-line "d" path-d))

        ;; Update text content
        (let [display-text    (build-text-content text repeat)
              has-gradient    (contains? effects "size-gradient")
              has-color-wave  (contains? effects "color-wave")
              needs-tspans    (or (and has-gradient start-size end-size (not= text ""))
                                  (and has-color-wave (not= text "")))]
          (if needs-tspans
            (apply-tspans! text-path display-text effects duration-s start-size end-size)
            (set! (.-textContent text-path) display-text))

          ;; Echo rendering
          (render-echoes! el svg display-text echo-count echo-delay echo-opacity
                          echo-scale animation direction duration-s font-size))

        ;; Font size override
        (if font-size
          (.setProperty (.-style text-el) "font-size" font-size)
          (.removeProperty (.-style text-el) "font-size"))

        ;; SMIL animation
        (create-animate! el text-path animation direction duration-s)

        ;; Effect data attribute for CSS selectors
        ;; (color-wave is applied per-tspan, not via host selector, but we still set it
        ;; on data-effect so it can be detected; CSS won't target it though)
        (let [css-effects (disj effects "size-gradient" "color-wave")]
          (if (seq css-effects)
            (.setAttribute el "data-effect" (str/join " " (sort css-effects)))
            (.removeAttribute el "data-effect")))

        ;; Animation data attribute
        (if (not= animation "none")
          (.setAttribute el "data-animation" animation)
          (.removeAttribute el "data-animation"))))

    ;; ── Common (both modes) ───────────────────────────────────────────
    ;; Screen reader text
    (set! (.-textContent sr-only) text)

    ;; Accessibility
    (if (= text "")
      (do (.setAttribute el "role" "presentation")
          (.setAttribute el "aria-hidden" "true"))
      (do (.setAttribute el "role" "img")
          (.setAttribute el "aria-label" text))))
  nil)

;; ── Lifecycle ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (render! el)
  nil)

(defn- disconnected! [^js el]
  (remove-animate! el)
  (let [^js svg (gobj/get el k-svg)]
    (when svg (clear-echoes! el svg)))
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (render! el)))
  nil)

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
  (install-string-accessor! proto model/attr-effect       "effect"      "none")
  (install-string-accessor! proto model/attr-font-size    "fontSize"    nil)
  (install-string-accessor! proto model/attr-start-size   "startSize"   nil)
  (install-string-accessor! proto model/attr-end-size     "endSize"     nil)
  (install-string-accessor! proto model/attr-repeat       "repeat"      (str model/default-repeat))
  (install-string-accessor! proto model/attr-echo-count   "echoCount"   (str model/default-echo-count))
  (install-string-accessor! proto model/attr-echo-delay   "echoDelay"   (str model/default-echo-delay))
  (install-string-accessor! proto model/attr-echo-opacity "echoOpacity" (str model/default-echo-opacity))
  (install-string-accessor! proto model/attr-echo-scale   "echoScale"   (str model/default-echo-scale)))

;; ── Element class ────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (connected! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (disconnected! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v]
            (this-as ^js this
                     (attribute-changed! this n o v)
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
