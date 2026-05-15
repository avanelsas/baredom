(ns baredom.components.x-carousel.x-carousel
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-carousel.model :as model]))

;; ── Instance-field keys ────────────────────────────────────────────────────
(def ^:private k-refs           "__xCarouselRefs")
(def ^:private k-handlers       "__xCarouselHandlers")
(def ^:private k-model          "__xCarouselModel")
(def ^:private k-init           "__xCarouselInit")
(def ^:private k-dragging       "__xCarouselDragging")
(def ^:private k-drag-start     "__xCarouselDragStart")
(def ^:private k-drag-start-t   "__xCarouselDragStartT")
(def ^:private k-drag-current   "__xCarouselDragCurrent")
(def ^:private k-slide-count    "__xCarouselSlideCount")
(def ^:private k-autoplay-timer "__xCarouselAutoTimer")
(def ^:private k-paused         "__xCarouselPaused")
(def ^:private k-dot-count      "__xCarouselDotCount")

;; ── Refs-object keys ───────────────────────────────────────────────────────
(def ^:private rk-root     "root")
(def ^:private rk-viewport "viewport")
(def ^:private rk-track    "track")
(def ^:private rk-slot     "slot")
(def ^:private rk-prev     "prev")
(def ^:private rk-next     "next")
(def ^:private rk-dots     "dots")
(def ^:private rk-live     "live")

;; ── Handler-object keys ────────────────────────────────────────────────────
(def ^:private hk-down     "down")
(def ^:private hk-move     "move")
(def ^:private hk-up       "up")
(def ^:private hk-key      "key")
(def ^:private hk-dot      "dot")
(def ^:private hk-prev     "prev")
(def ^:private hk-next     "next")
(def ^:private hk-slot     "slot")
(def ^:private hk-enter    "enter")
(def ^:private hk-leave    "leave")
(def ^:private hk-focusin  "focusin")
(def ^:private hk-focusout "focusout")

;; ── String-literal constants ───────────────────────────────────────────────
(def ^:private attr-part                "part")
(def ^:private attr-type                "type")
(def ^:private attr-role                "role")
(def ^:private attr-tabindex            "tabindex")
(def ^:private attr-aria-label          "aria-label")
(def ^:private attr-aria-current        "aria-current")
(def ^:private attr-aria-selected       "aria-selected")
(def ^:private attr-aria-live           "aria-live")
(def ^:private attr-aria-atomic         "aria-atomic")
(def ^:private attr-aria-orientation    "aria-orientation")
(def ^:private attr-aria-roledescription "aria-roledescription")
(def ^:private attr-data-direction      "data-direction")
(def ^:private attr-data-transition     "data-transition")
(def ^:private attr-data-index          "data-index")

(def ^:private part-viewport "viewport")
(def ^:private part-track    "track")
(def ^:private part-prev-btn "prev-btn")
(def ^:private part-next-btn "next-btn")
(def ^:private part-dots     "dots")
(def ^:private part-dot      "dot")

(def ^:private cls-dragging "dragging")
(def ^:private cls-sr-only  "sr-only")

(def ^:private css-var-peek "--_peek")

(def ^:private val-true                "true")
(def ^:private val-false               "false")
(def ^:private val-zero-peek           "0px")
(def ^:private val-button              "button")
(def ^:private val-region              "region")
(def ^:private val-carousel            "carousel")
(def ^:private val-tablist             "tablist")
(def ^:private val-tab                 "tab")
(def ^:private val-polite              "polite")
(def ^:private val-off                 "off")
(def ^:private val-vertical            "vertical")
(def ^:private val-slide               "slide")
(def ^:private val-default-label       "Carousel")
(def ^:private val-slide-indicators    "Slide indicators")
(def ^:private val-prev-label          "Previous slide")
(def ^:private val-next-label          "Next slide")

(def ^:private reason-autoplay "autoplay")
(def ^:private reason-drag     "drag")
(def ^:private reason-keyboard "keyboard")
(def ^:private reason-dot      "dot")
(def ^:private reason-arrow    "arrow")
(def ^:private reason-api      "api")

(def ^:private ev-pointerdown  "pointerdown")
(def ^:private ev-pointermove  "pointermove")
(def ^:private ev-pointerup    "pointerup")
(def ^:private ev-pointerenter "pointerenter")
(def ^:private ev-pointerleave "pointerleave")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-click        "click")
(def ^:private ev-slotchange   "slotchange")
(def ^:private ev-focusin      "focusin")
(def ^:private ev-focusout     "focusout")

(def ^:private key-home "Home")
(def ^:private key-end  "End")

;; ── SVG arrows ─────────────────────────────────────────────────────────────
(def ^:private svg-prev
  "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"15 18 9 12 15 6\"></polyline></svg>")

(def ^:private svg-next
  "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"9 6 15 12 9 18\"></polyline></svg>")

;; ── Styles ─────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "position:relative;"
   "--x-carousel-height:300px;"
   "--x-carousel-arrow-size:40px;"
   "--x-carousel-arrow-bg:var(--x-color-surface,rgba(255,255,255,0.9));"
   "--x-carousel-arrow-color:var(--x-color-text,#1e293b);"
   "--x-carousel-arrow-hover-bg:var(--x-color-surface-hover,rgba(255,255,255,1));"
   "--x-carousel-dot-size:10px;"
   "--x-carousel-dot-color:var(--x-color-border,#cbd5e1);"
   "--x-carousel-dot-active-color:var(--x-color-primary,#3b82f6);"
   "--x-carousel-transition-duration:var(--x-transition-duration,300ms);"
   "--x-carousel-radius:var(--x-radius-md,8px);"
   "--x-carousel-gap:16px;"
   "--x-carousel-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-carousel-disabled-opacity:0.5;"
   "}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-carousel-arrow-bg:var(--x-color-surface,rgba(30,41,59,0.9));"
   "--x-carousel-arrow-color:var(--x-color-text,#f1f5f9);"
   "--x-carousel-arrow-hover-bg:var(--x-color-surface-hover,rgba(30,41,59,1));"
   "--x-carousel-dot-color:var(--x-color-border,#475569);"
   "--x-carousel-dot-active-color:var(--x-color-primary,#60a5fa);"
   "--x-carousel-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}}"

   "[part=viewport]{"
   "position:relative;"
   "overflow:hidden;"
   "height:var(--x-carousel-height);"
   "border-radius:var(--x-carousel-radius);"
   "}"
   "[part=viewport]:focus{outline:none;}"
   "[part=viewport]:focus-visible{"
   "outline:2px solid var(--x-carousel-focus-ring);"
   "outline-offset:2px;}"

   "[part=track]{"
   "display:flex;"
   "flex-direction:row;"
   "height:100%;"
   "transition:transform var(--x-carousel-transition-duration) ease;"
   "will-change:transform;"
   "touch-action:pan-y pinch-zoom;"
   "}"
   "[part=track].dragging{"
   "transition:none !important;"
   "cursor:grabbing;}"

   ":host([data-direction=vertical]) [part=track]{"
   "flex-direction:column;"
   "touch-action:pan-x pinch-zoom;}"

   "::slotted(*){"
   "min-width:calc(100% - var(--_peek,0px) * 2);"
   "max-width:calc(100% - var(--_peek,0px) * 2);"
   "flex-shrink:0;"
   "box-sizing:border-box;"
   "overflow:hidden;}"

   ":host([data-direction=vertical]) ::slotted(*){"
   "min-width:auto;max-width:none;"
   "min-height:calc(100% - var(--_peek,0px) * 2);"
   "max-height:calc(100% - var(--_peek,0px) * 2);"
   "}"

   ":host([data-transition=fade]) [part=track]{"
   "display:grid;"
   "grid-template-columns:1fr;"
   "grid-template-rows:1fr;"
   "transition:none;}"

   ":host([data-transition=fade]) ::slotted(*){"
   "grid-area:1/1;"
   "min-width:100%;max-width:100%;"
   "transition:opacity var(--x-carousel-transition-duration) ease;}"

   "[part=prev-btn],[part=next-btn]{"
   "position:absolute;"
   "top:50%;transform:translateY(-50%);"
   "width:var(--x-carousel-arrow-size);"
   "height:var(--x-carousel-arrow-size);"
   "border-radius:50%;"
   "border:none;"
   "background:var(--x-carousel-arrow-bg);"
   "color:var(--x-carousel-arrow-color);"
   "cursor:pointer;"
   "display:flex;align-items:center;justify-content:center;"
   "padding:0;z-index:1;"
   "box-shadow:var(--x-shadow-sm,0 1px 3px rgba(0,0,0,0.1));"
   "transition:background 150ms ease,opacity 150ms ease,transform 150ms ease;"
   "opacity:0.85;}"
   "[part=prev-btn]:hover,[part=next-btn]:hover{"
   "background:var(--x-carousel-arrow-hover-bg);opacity:1;}"
   "[part=prev-btn]:active,[part=next-btn]:active{transform:translateY(-50%) scale(0.95);}"
   "[part=prev-btn]:focus,[part=next-btn]:focus{outline:none;}"
   "[part=prev-btn]:focus-visible,[part=next-btn]:focus-visible{"
   "outline:2px solid var(--x-carousel-focus-ring);outline-offset:2px;}"
   "[part=prev-btn]:disabled,[part=next-btn]:disabled{"
   "opacity:0.3;cursor:default;pointer-events:none;}"
   "[part=prev-btn]{left:8px;}"
   "[part=next-btn]{right:8px;}"
   "[part=prev-btn] svg,[part=next-btn] svg{"
   "width:60%;height:60%;pointer-events:none;}"

   ":host([data-direction=vertical]) [part=prev-btn],"
   ":host([data-direction=vertical]) [part=next-btn]{"
   "top:auto;left:50%;right:auto;"
   "transform:translateX(-50%) rotate(90deg);}"
   ":host([data-direction=vertical]) [part=prev-btn]{top:8px;bottom:auto;}"
   ":host([data-direction=vertical]) [part=next-btn]{bottom:8px;top:auto;}"
   ":host([data-direction=vertical]) [part=prev-btn]:active{"
   "transform:translateX(-50%) rotate(90deg) scale(0.95);}"
   ":host([data-direction=vertical]) [part=next-btn]:active{"
   "transform:translateX(-50%) rotate(90deg) scale(0.95);}"

   "[part=dots]{"
   "display:flex;justify-content:center;align-items:center;"
   "gap:8px;padding:12px 0;}"
   "[part=dot]{"
   "all:unset;"
   "width:var(--x-carousel-dot-size);"
   "height:var(--x-carousel-dot-size);"
   "border-radius:50%;"
   "background:var(--x-carousel-dot-color);"
   "cursor:pointer;"
   "transition:background 150ms ease,transform 150ms ease;}"
   "[part=dot]:hover{transform:scale(1.3);}"
   "[part=dot]:focus-visible{"
   "outline:2px solid var(--x-carousel-focus-ring);outline-offset:2px;}"
   "[part=dot][aria-current=true]{"
   "background:var(--x-carousel-dot-active-color);}"

   ".sr-only{"
   "position:absolute;width:1px;height:1px;"
   "padding:0;margin:-1px;overflow:hidden;"
   "clip:rect(0,0,0,0);white-space:nowrap;border:0;}"

   ":host([disabled]){"
   "opacity:var(--x-carousel-disabled-opacity);"
   "pointer-events:none;}"

   "@media(prefers-reduced-motion:reduce){"
   "[part=track]{transition-duration:0ms !important;}"
   ":host([data-transition=fade]) ::slotted(*){transition-duration:0ms !important;}"
   "[part=prev-btn],[part=next-btn],[part=dot]{transition:none !important;}}"

   "@media(pointer:coarse){"
   "[part=prev-btn],[part=next-btn]{"
   "width:max(var(--x-carousel-arrow-size),44px);"
   "height:max(var(--x-carousel-arrow-size),44px);}"
   "[part=dot]{"
   "width:max(var(--x-carousel-dot-size),44px);"
   "height:max(var(--x-carousel-dot-size),44px);"
   "background-clip:content-box;"
   "padding:calc((44px - var(--x-carousel-dot-size)) / 2);"
   "box-sizing:content-box;}"
   "}"))

;; ── DOM initialisation ─────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        viewport (.createElement js/document "div")
        track    (.createElement js/document "div")
        slot-el  (.createElement js/document "slot")
        prev-btn (.createElement js/document "button")
        next-btn (.createElement js/document "button")
        dots-el  (.createElement js/document "div")
        live-el  (.createElement js/document "div")
        refs     #js {}]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! viewport attr-part     part-viewport)
    (du/set-attr! viewport attr-tabindex "0")

    (du/set-attr! track attr-part part-track)
    (.appendChild track slot-el)

    (du/set-attr! prev-btn attr-part       part-prev-btn)
    (du/set-attr! prev-btn attr-type       val-button)
    (du/set-attr! prev-btn attr-aria-label val-prev-label)
    (set! (.-innerHTML prev-btn) svg-prev)

    (du/set-attr! next-btn attr-part       part-next-btn)
    (du/set-attr! next-btn attr-type       val-button)
    (du/set-attr! next-btn attr-aria-label val-next-label)
    (set! (.-innerHTML next-btn) svg-next)

    (.appendChild viewport track)
    (.appendChild viewport prev-btn)
    (.appendChild viewport next-btn)

    (du/set-attr! dots-el attr-part       part-dots)
    (du/set-attr! dots-el attr-role       val-tablist)
    (du/set-attr! dots-el attr-aria-label val-slide-indicators)

    (du/set-attr! live-el attr-aria-live   val-polite)
    (du/set-attr! live-el attr-aria-atomic val-true)
    (set! (.-className live-el) cls-sr-only)

    (.appendChild root style-el)
    (.appendChild root viewport)
    (.appendChild root dots-el)
    (.appendChild root live-el)

    (gobj/set refs rk-root     root)
    (gobj/set refs rk-viewport viewport)
    (gobj/set refs rk-track    track)
    (gobj/set refs rk-slot     slot-el)
    (gobj/set refs rk-prev     prev-btn)
    (gobj/set refs rk-next     next-btn)
    (gobj/set refs rk-dots     dots-el)
    (gobj/set refs rk-live     live-el)
    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

;; ── Model reading ──────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:autoplay-present? (du/has-attr? el model/attr-autoplay)
    :interval-raw      (du/get-attr el model/attr-interval)
    :loop-present?     (du/has-attr? el model/attr-loop)
    :arrows-raw        (du/get-attr el model/attr-arrows)
    :dots-raw          (du/get-attr el model/attr-dots)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :current-raw       (du/get-attr el model/attr-current)
    :transition-raw    (du/get-attr el model/attr-transition)
    :direction-raw     (du/get-attr el model/attr-direction)
    :peek-raw          (du/get-attr el model/attr-peek)
    :aria-label-raw    (du/get-attr el model/attr-aria-label)
    :slide-count       (or (du/getv el k-slide-count) 0)}))

;; ── Autoplay ───────────────────────────────────────────────────────────────
(declare go-to!)

(defn- stop-autoplay! [^js el]
  (when-let [tid (du/getv el k-autoplay-timer)]
    (js/clearInterval tid)
    (du/setv! el k-autoplay-timer nil)))

(defn- start-autoplay! [^js el]
  (stop-autoplay! el)
  (let [m (read-model el)]
    (when (and (:autoplay? m)
               (not (du/getv el k-paused))
               (not (:disabled? m))
               (> (:slide-count m) 1)
               (.-isConnected el))
      (du/setv! el k-autoplay-timer
                (js/setInterval
                 (fn on-autoplay-tick []
                   (when (.-isConnected el)
                     (let [m2 (read-model el)]
                       (go-to! el (model/next-index m2) reason-autoplay))))
                 (:interval m))))))

;; ── Dots rebuild ───────────────────────────────────────────────────────────
(defn- rebuild-dots! [^js el ^js dots-el slide-count current]
  (let [prev-count (or (du/getv el k-dot-count) -1)]
    (when (not= prev-count slide-count)
      (set! (.-innerHTML dots-el) "")
      (dotimes [i slide-count]
        (let [^js dot (.createElement js/document "button")]
          (du/set-attr! dot attr-part       part-dot)
          (du/set-attr! dot attr-type       val-button)
          (du/set-attr! dot attr-role       val-tab)
          (du/set-attr! dot attr-aria-label (str "Go to slide " (inc i)))
          (du/set-attr! dot attr-data-index (str i))
          (.appendChild dots-el dot)))
      (du/setv! el k-dot-count slide-count))
    (let [children (.-children dots-el)]
      (dotimes [i (.-length children)]
        (let [^js dot (aget children i)]
          (du/set-attr! dot attr-aria-current  (str (= i current)))
          (du/set-attr! dot attr-aria-selected (str (= i current))))))))

;; ── Fade-mode helpers ──────────────────────────────────────────────────────
(defn- update-fade-active! [^js el current]
  (let [^js slot-el (gobj/get (du/getv el k-refs) rk-slot)
        children    (.assignedElements slot-el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -opacity) (if (= i current) "1" "0"))))))

(defn- clear-fade-styles! [^js el]
  (let [^js slot-el (gobj/get (du/getv el k-refs) rk-slot)
        children    (.assignedElements slot-el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -opacity) "")))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ───────
(defn- apply-host-data! [^js el {:keys [direction transition peek]}]
  (du/set-attr! el attr-data-direction  direction)
  (du/set-attr! el attr-data-transition transition)
  (.setProperty (.-style el) css-var-peek peek))

(defn- apply-host-aria! [^js el {:keys [aria-label]}]
  (du/set-attr! el attr-role               val-region)
  (du/set-attr! el attr-aria-roledescription val-carousel)
  (if aria-label
    (du/set-attr! el attr-aria-label aria-label)
    (when-not (du/has-attr? el attr-aria-label)
      (du/set-attr! el attr-aria-label val-default-label))))

(defn- track-transform-str [vertical? peek-val current]
  (let [axis (if vertical? "Y" "X")]
    (if (= peek-val val-zero-peek)
      (str "translate" axis "(" (* current -100) "%)")
      (str "translate" axis "(calc("
           (* current -1) " * (100% - " peek-val " * 2) + "
           peek-val "))"))))

(defn- apply-track! [^js el ^js track {:keys [current transition direction peek] :as _m}]
  (let [vertical? (= direction val-vertical)]
    (if (= transition val-slide)
      (do
        (set! (.. track -style -transform) (track-transform-str vertical? peek current))
        (clear-fade-styles! el))
      (do
        (set! (.. track -style -transform) "")
        (update-fade-active! el current)))))

(defn- apply-arrows! [^js prev-btn ^js next-btn m]
  (if (model/show-arrows? m)
    (do (set! (.. prev-btn -style -display) "flex")
        (set! (.. next-btn -style -display) "flex")
        (set! (.-disabled prev-btn) (not (model/can-go-prev? m)))
        (set! (.-disabled next-btn) (not (model/can-go-next? m))))
    (do (set! (.. prev-btn -style -display) "none")
        (set! (.. next-btn -style -display) "none"))))

(defn- apply-dots! [^js el ^js dots-el {:keys [current slide-count direction] :as m}]
  (du/set-attr! dots-el attr-aria-orientation direction)
  (if (model/show-dots? m)
    (do (set! (.. dots-el -style -display) "flex")
        (rebuild-dots! el dots-el slide-count current))
    (set! (.. dots-el -style -display) "none")))

(defn- apply-live! [^js live-el {:keys [autoplay? current slide-count]}]
  (when (> slide-count 0)
    (set! (.-textContent live-el)
          (str "Slide " (inc current) " of " slide-count)))
  (du/set-attr! live-el attr-aria-live (if autoplay? val-off val-polite)))

(defn- apply-model! [^js el m]
  (let [^js refs     (ensure-refs! el)
        ^js track    (gobj/get refs rk-track)
        ^js prev-btn (gobj/get refs rk-prev)
        ^js next-btn (gobj/get refs rk-next)
        ^js dots-el  (gobj/get refs rk-dots)
        ^js live-el  (gobj/get refs rk-live)]
    (apply-host-data! el m)
    (apply-host-aria! el m)
    (apply-track!     el track m)
    (apply-arrows!    prev-btn next-btn m)
    (apply-dots!      el dots-el m)
    (apply-live!      live-el m)
    ;; Cache write at the END so partial DOM writes don't leave a lying cache.
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (when (du/initialized? el k-init)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Navigation ─────────────────────────────────────────────────────────────
(defn- go-to! [^js el index reason]
  (let [m       (read-model el)
        sc      (:slide-count m)
        target  (model/clamp-index index sc)
        current (:current m)]
    (when (and (not= target current) (> sc 0))
      (when (du/dispatch-cancelable! el model/event-change
              #js {:index         target
                   :previousIndex current
                   :reason        reason})
        (du/set-attr! el model/attr-current (str target))))))

;; ── Event handlers ─────────────────────────────────────────────────────────
(defn- on-pointerdown [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (not (:disabled? m))
               (= (:transition m) val-slide)
               (> (:slide-count m) 1))
      (.preventDefault evt)
      (let [^js track (gobj/get (du/getv el k-refs) rk-track)
            vertical? (= (:direction m) val-vertical)
            coord     (if vertical? (.-clientY evt) (.-clientX evt))]
        (.setPointerCapture track (.-pointerId evt))
        (.add (.-classList track) cls-dragging)
        (du/setv! el k-dragging true)
        (du/setv! el k-drag-start coord)
        (du/setv! el k-drag-start-t (js/Date.now))
        (du/setv! el k-drag-current coord)
        (du/setv! el k-paused true)
        (stop-autoplay! el)))))

(defn- on-pointermove [^js el ^js evt]
  (when (du/getv el k-dragging)
    (let [m         (read-model el)
          vertical? (= (:direction m) val-vertical)
          coord     (if vertical? (.-clientY evt) (.-clientX evt))
          start     (du/getv el k-drag-start)
          delta     (- coord start)
          ^js track (gobj/get (du/getv el k-refs) rk-track)
          current   (:current m)
          axis      (if vertical? "Y" "X")
          peek-val  (:peek m)]
      (du/setv! el k-drag-current coord)
      (if (= peek-val val-zero-peek)
        (set! (.. track -style -transform)
              (str "translate" axis "(calc(" (* current -100) "% + " delta "px))"))
        (set! (.. track -style -transform)
              (str "translate" axis "(calc("
                   (* current -1) " * (100% - " peek-val " * 2) + "
                   peek-val " + " delta "px))"))))))

(defn- on-pointerup [^js el ^js _evt]
  (when (du/getv el k-dragging)
    (du/setv! el k-dragging false)
    (let [^js track (gobj/get (du/getv el k-refs) rk-track)
          start     (du/getv el k-drag-start)
          current   (du/getv el k-drag-current)
          delta     (- current start)
          elapsed   (- (js/Date.now) (du/getv el k-drag-start-t))
          velocity  (if (pos? elapsed) (/ delta elapsed) 0)
          snap      (model/snap-direction delta velocity)
          m         (read-model el)]
      (.remove (.-classList track) cls-dragging)
      (case snap
        :prev (go-to! el (model/prev-index m) reason-drag)
        :next (go-to! el (model/next-index m) reason-drag)
        ;; No snap — reapply the current model to undo the drag translation.
        (apply-model! el m))
      (du/setv! el k-paused false)
      (start-autoplay! el))))

(defn- on-keydown [^js el ^js evt]
  (let [m   (read-model el)
        k   (.-key evt)
        dir (:direction m)]
    (when (and (not (:disabled? m)) (> (:slide-count m) 1))
      (cond
        (model/prev-key? dir k)
        (do (.preventDefault evt)
            (go-to! el (model/prev-index m) reason-keyboard))

        (model/next-key? dir k)
        (do (.preventDefault evt)
            (go-to! el (model/next-index m) reason-keyboard))

        (= k key-home)
        (do (.preventDefault evt)
            (go-to! el 0 reason-keyboard))

        (= k key-end)
        (do (.preventDefault evt)
            (go-to! el (dec (:slide-count m)) reason-keyboard))))))

(defn- on-dot-click [^js el ^js evt]
  (let [^js target (.-target evt)]
    (when-let [idx-str (.getAttribute target attr-data-index)]
      (let [idx (js/parseInt idx-str 10)]
        (when-not (js/isNaN idx)
          (go-to! el idx reason-dot))))))

(defn- on-prev-click [^js el ^js _evt]
  (let [m (read-model el)]
    (go-to! el (model/prev-index m) reason-arrow)))

(defn- on-next-click [^js el ^js _evt]
  (let [m (read-model el)]
    (go-to! el (model/next-index m) reason-arrow)))

(defn- on-pointerenter [^js el ^js _evt]
  (du/setv! el k-paused true)
  (stop-autoplay! el))

(defn- on-pointerleave [^js el ^js _evt]
  (du/setv! el k-paused false)
  (start-autoplay! el))

(defn- on-focusin [^js el ^js _evt]
  (du/setv! el k-paused true)
  (stop-autoplay! el))

(defn- on-focusout [^js el ^js evt]
  (when-not (.contains el (.-relatedTarget evt))
    (du/setv! el k-paused false)
    (start-autoplay! el)))

(defn- on-slotchange [^js el ^js _evt]
  (let [^js slot (gobj/get (du/getv el k-refs) rk-slot)
        cnt      (.-length (.assignedElements slot))]
    (du/setv! el k-slide-count cnt)
    (let [m       (read-model el)
          clamped (model/clamp-index (:current m) cnt)]
      (when (not= clamped (:current m))
        (du/set-attr! el model/attr-current (str clamped))))
    (update-from-attrs! el)
    (start-autoplay! el)))

;; ── Listener-spec named pattern ────────────────────────────────────────────
;; [refs-key handler-key event-name handler-fn]
(def ^:private listener-spec
  [[rk-track    hk-down     ev-pointerdown  on-pointerdown]
   [rk-track    hk-move     ev-pointermove  on-pointermove]
   [rk-track    hk-up       ev-pointerup    on-pointerup]
   [rk-viewport hk-key      ev-keydown      on-keydown]
   [rk-dots     hk-dot      ev-click        on-dot-click]
   [rk-prev     hk-prev     ev-click        on-prev-click]
   [rk-next     hk-next     ev-click        on-next-click]
   [rk-slot     hk-slot     ev-slotchange   on-slotchange]])

;; Host listeners — attached to the host element rather than a shadow ref.
;; Kept separate so the same spec iteration works for both kinds of target.
(def ^:private host-listener-spec
  [[hk-enter    ev-pointerenter on-pointerenter]
   [hk-leave    ev-pointerleave on-pointerleave]
   [hk-focusin  ev-focusin      on-focusin]
   [hk-focusout ev-focusout     on-focusout]])

(defn- add-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)
        handlers #js {}]
    (doseq [[refs-key handler-key event-name handler-fn] listener-spec]
      (let [^js target (gobj/get refs refs-key)
            wrapped    (fn [e] (handler-fn el e))]
        (.addEventListener target event-name wrapped)
        (gobj/set handlers handler-key wrapped)))
    (doseq [[handler-key event-name handler-fn] host-listener-spec]
      (let [wrapped (fn [e] (handler-fn el e))]
        (.addEventListener el event-name wrapped)
        (gobj/set handlers handler-key wrapped)))
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (stop-autoplay! el)
  (when-let [^js hs (du/getv el k-handlers)]
    (when-let [^js refs (du/getv el k-refs)]
      (doseq [[refs-key handler-key event-name _] listener-spec]
        (let [^js target (gobj/get refs refs-key)
              wrapped    (gobj/get hs handler-key)]
          (when (and target wrapped)
            (.removeEventListener target event-name wrapped))))
      (doseq [[handler-key event-name _] host-listener-spec]
        (when-let [wrapped (gobj/get hs handler-key)]
          (.removeEventListener el event-name wrapped)))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ─────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; Boolean properties (presence = true)
  (doseq [attr [model/attr-autoplay model/attr-loop model/attr-disabled]]
    (du/define-bool-prop! proto attr attr))

  ;; Default-true boolean properties: arrows, dots — Tier 2 because the
  ;; absent attribute reads as true, and writing false stores the literal
  ;; "false" string rather than removing the attribute. du/define-bool-prop!
  ;; would treat the missing attribute as false (the opposite of the
  ;; intended default).
  (doseq [attr [model/attr-arrows model/attr-dots]]
    (.defineProperty js/Object proto attr
                     #js {:get (fn []
                                 (this-as ^js this
                                   (model/parse-bool-default-true
                                    (.getAttribute this attr))))
                          :set (fn [v]
                                 (this-as ^js this
                                   (if v
                                     (du/remove-attr! this attr)
                                     (du/set-attr! this attr val-false))))
                          :enumerable true :configurable true}))

  (.defineProperty js/Object proto "currentSlide"
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-non-neg-int
                                  (.getAttribute this model/attr-current)
                                  model/default-current)))
                        :set (fn [v]
                               (this-as ^js this
                                 (du/set-attr! this model/attr-current
                                                (str (int v)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-interval
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-pos-int
                                  (.getAttribute this model/attr-interval)
                                  model/default-interval 100)))
                        :set (fn [v]
                               (this-as ^js this
                                 (if (nil? v)
                                   (du/remove-attr! this model/attr-interval)
                                   (du/set-attr! this model/attr-interval
                                                  (str (int v))))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-transition
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-transition
                                  (.getAttribute this model/attr-transition))))
                        :set (fn [v]
                               (this-as ^js this
                                 (if v
                                   (du/set-attr! this model/attr-transition (str v))
                                   (du/remove-attr! this model/attr-transition))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-direction
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-direction
                                  (.getAttribute this model/attr-direction))))
                        :set (fn [v]
                               (this-as ^js this
                                 (if v
                                   (du/set-attr! this model/attr-direction (str v))
                                   (du/remove-attr! this model/attr-direction))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-peek
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-peek
                                  (.getAttribute this model/attr-peek))))
                        :set (fn [v]
                               (this-as ^js this
                                 (if v
                                   (du/set-attr! this model/attr-peek (str v))
                                   (du/remove-attr! this model/attr-peek))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "slideCount"
                   #js {:get (fn []
                               (this-as ^js this
                                 (or (du/getv this k-slide-count) 0)))
                        :enumerable true :configurable true})

  (aset proto "next"
        (fn carousel-next []
          (this-as ^js this
            (let [m (read-model this)]
              (go-to! this (model/next-index m) reason-api)))))

  (aset proto "previous"
        (fn carousel-previous []
          (this-as ^js this
            (let [m (read-model this)]
              (go-to! this (model/prev-index m) reason-api)))))

  (aset proto "goTo"
        (fn carousel-go-to [idx]
          (this-as ^js this
            (go-to! this idx reason-api)))))

;; ── Lifecycle ──────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/initialized? el k-init)
    (init-dom! el)
    (du/mark-initialized! el k-init))
  (remove-listeners! el)
  (add-listeners! el)
  ;; Prime slide-count before the first render so apply-arrows!/apply-dots!
  ;; see the right number on the first pass.
  (let [^js slot (gobj/get (du/getv el k-refs) rk-slot)]
    (du/setv! el k-slide-count (.-length (.assignedElements slot))))
  (update-from-attrs! el)
  (start-autoplay! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)
    (when (.-isConnected el)
      (start-autoplay! el))))

;; ── Public API ─────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
