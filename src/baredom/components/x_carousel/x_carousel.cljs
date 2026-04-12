(ns baredom.components.x-carousel.x-carousel
  (:require [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.utils.model :as mu]
            [baredom.components.x-carousel.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs           "__xCarouselRefs")
(def ^:private k-handlers       "__xCarouselHandlers")
(def ^:private k-init           "__xCarouselInit")
(def ^:private k-dragging       "__xCarouselDragging")
(def ^:private k-drag-start     "__xCarouselDragStart")
(def ^:private k-drag-start-t   "__xCarouselDragStartT")
(def ^:private k-drag-current   "__xCarouselDragCurrent")
(def ^:private k-slide-count    "__xCarouselSlideCount")
(def ^:private k-autoplay-timer "__xCarouselAutoTimer")
(def ^:private k-paused         "__xCarouselPaused")
(def ^:private k-dot-count      "__xCarouselDotCount")

;; ── SVG arrows ──────────────────────────────────────────────────────────────
(def ^:private svg-prev
  "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"15 18 9 12 15 6\"></polyline></svg>")

(def ^:private svg-next
  "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"9 6 15 12 9 18\"></polyline></svg>")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ;; ── Host ──────────────────────────────────────────────────────────────
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

   ;; ── Viewport ──────────────────────────────────────────────────────────
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

   ;; ── Track (slide mode, horizontal) ────────────────────────────────────
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

   ;; ── Vertical direction ────────────────────────────────────────────────
   ":host([data-direction=vertical]) [part=track]{"
   "flex-direction:column;"
   "touch-action:pan-x pinch-zoom;}"

   ;; ── Slotted slides (horizontal) ──────────────────────────────────────
   ;; min-width + max-width together force the exact width we need.
   ;; max-width is critical: it caps slides even when their own styles
   ;; set width:100% (which resolves to the full viewport width).
   "::slotted(*){"
   "min-width:calc(100% - var(--_peek,0px) * 2);"
   "max-width:calc(100% - var(--_peek,0px) * 2);"
   "flex-shrink:0;"
   "box-sizing:border-box;"
   "overflow:hidden;}"

   ;; ── Slotted slides (vertical) ────────────────────────────────────────
   ":host([data-direction=vertical]) ::slotted(*){"
   "min-width:auto;max-width:none;"
   "min-height:calc(100% - var(--_peek,0px) * 2);"
   "max-height:calc(100% - var(--_peek,0px) * 2);"
   "}"

   ;; ── Fade mode ─────────────────────────────────────────────────────────
   ":host([data-transition=fade]) [part=track]{"
   "display:grid;"
   "grid-template-columns:1fr;"
   "grid-template-rows:1fr;"
   "transition:none;}"

   ":host([data-transition=fade]) ::slotted(*){"
   "grid-area:1/1;"
   "min-width:100%;max-width:100%;"
   "transition:opacity var(--x-carousel-transition-duration) ease;}"

   ;; ── Arrow buttons ─────────────────────────────────────────────────────
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

   ;; ── Vertical arrow positioning ────────────────────────────────────────
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

   ;; ── Dots ──────────────────────────────────────────────────────────────
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

   ;; ── Screen-reader only ────────────────────────────────────────────────
   ".sr-only{"
   "position:absolute;width:1px;height:1px;"
   "padding:0;margin:-1px;overflow:hidden;"
   "clip:rect(0,0,0,0);white-space:nowrap;border:0;}"

   ;; ── Disabled ──────────────────────────────────────────────────────────
   ":host([disabled]){"
   "opacity:var(--x-carousel-disabled-opacity);"
   "pointer-events:none;}"

   ;; ── Reduced motion ────────────────────────────────────────────────────
   "@media(prefers-reduced-motion:reduce){"
   "[part=track]{transition-duration:0ms !important;}"
   ":host([data-transition=fade]) ::slotted(*){transition-duration:0ms !important;}"
   "[part=prev-btn],[part=next-btn],[part=dot]{transition:none !important;}}"

   ;; ── Touch targets ────────────────────────────────────────────────────
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

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style-el  (.createElement js/document "style")
        viewport  (.createElement js/document "div")
        track     (.createElement js/document "div")
        slot-el   (.createElement js/document "slot")
        prev-btn  (.createElement js/document "button")
        next-btn  (.createElement js/document "button")
        dots-el   (.createElement js/document "div")
        live-el   (.createElement js/document "div")]

    (set! (.-textContent style-el) style-text)

    (.setAttribute viewport "part" "viewport")
    (.setAttribute viewport "tabindex" "0")

    (.setAttribute track "part" "track")

    (.appendChild track slot-el)

    (.setAttribute prev-btn "part" "prev-btn")
    (.setAttribute prev-btn "type" "button")
    (.setAttribute prev-btn "aria-label" "Previous slide")
    (set! (.-innerHTML prev-btn) svg-prev)

    (.setAttribute next-btn "part" "next-btn")
    (.setAttribute next-btn "type" "button")
    (.setAttribute next-btn "aria-label" "Next slide")
    (set! (.-innerHTML next-btn) svg-next)

    (.appendChild viewport track)
    (.appendChild viewport prev-btn)
    (.appendChild viewport next-btn)

    (.setAttribute dots-el "part" "dots")
    (.setAttribute dots-el "role" "tablist")
    (.setAttribute dots-el "aria-label" "Slide indicators")

    (.setAttribute live-el "aria-live" "polite")
    (.setAttribute live-el "aria-atomic" "true")
    (set! (.-className live-el) "sr-only")

    (.appendChild root style-el)
    (.appendChild root viewport)
    (.appendChild root dots-el)
    (.appendChild root live-el)

    (du/setv! el k-refs
              #js {"root"     root
                   "viewport" viewport
                   "track"    track
                   "slot"     slot-el
                   "prev"     prev-btn
                   "next"     next-btn
                   "dots"     dots-el
                   "live"     live-el}))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
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

;; ── Autoplay ────────────────────────────────────────────────────────────────
(declare go-to!)

(defn- stop-autoplay! [^js el]
  (when-let [tid (du/getv el k-autoplay-timer)]
    (js/clearInterval tid)
    (du/setv! el k-autoplay-timer nil))
  nil)

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
                 (fn []
                   (when (.-isConnected el)
                     (let [m2 (read-model el)]
                       (go-to! el (model/next-index m2) "autoplay"))))
                 (:interval m)))))
  nil)

;; ── Dots rebuild ────────────────────────────────────────────────────────────
(defn- rebuild-dots! [^js el ^js dots-el slide-count current]
  (let [prev-count (or (du/getv el k-dot-count) -1)]
    ;; Only rebuild DOM if count changed
    (when (not= prev-count slide-count)
      (set! (.-innerHTML dots-el) "")
      (dotimes [i slide-count]
        (let [^js dot (.createElement js/document "button")]
          (.setAttribute dot "part" "dot")
          (.setAttribute dot "type" "button")
          (.setAttribute dot "role" "tab")
          (.setAttribute dot "aria-label" (str "Go to slide " (inc i)))
          (.setAttribute dot "data-index" (str i))
          (.appendChild dots-el dot)))
      (du/setv! el k-dot-count slide-count))
    ;; Update active state
    (let [children (.-children dots-el)]
      (dotimes [i (.-length children)]
        (let [^js dot (aget children i)]
          (.setAttribute dot "aria-current" (str (= i current)))
          (.setAttribute dot "aria-selected" (str (= i current)))))))
  nil)

;; ── Fade mode helpers ───────────────────────────────────────────────────────
(defn- update-fade-active! [^js el current]
  (let [refs (du/getv el k-refs)
        ^js slot-el (gobj/get refs "slot")
        children (.assignedElements slot-el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -opacity) (if (= i current) "1" "0")))))
  nil)

(defn- clear-fade-styles! [^js el]
  (let [refs (du/getv el k-refs)
        ^js slot-el (gobj/get refs "slot")
        children (.assignedElements slot-el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -opacity) ""))))
  nil)

;; ── Render ──────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (when (du/initialized? el k-init)
    (let [m          (read-model el)
          refs       (du/getv el k-refs)
          ^js track  (gobj/get refs "track")
          ^js prev   (gobj/get refs "prev")
          ^js nxt    (gobj/get refs "next")
          ^js dots   (gobj/get refs "dots")
          ^js live   (gobj/get refs "live")
          ^js vp     (gobj/get refs "viewport")
          current    (:current m)
          sc         (:slide-count m)
          transition (:transition m)
          direction  (:direction m)
          peek-val   (:peek m)
          vertical?  (= direction "vertical")]

      ;; Host data attributes for CSS
      (.setAttribute el "data-direction" direction)
      (.setAttribute el "data-transition" transition)

      ;; ARIA on host
      (.setAttribute el "role" "region")
      (.setAttribute el "aria-roledescription" "carousel")
      (if-let [label (:aria-label m)]
        (.setAttribute el "aria-label" label)
        (when-not (du/has-attr? el "aria-label")
          (.setAttribute el "aria-label" "Carousel")))

      ;; ARIA orientation on dots
      (.setAttribute dots "aria-orientation" direction)

      ;; Set --_peek CSS variable on host (drives ::slotted(*) sizing)
      (.setProperty (.-style el) "--_peek" peek-val)

      ;; Track transform (slide mode only)
      ;; Formula: translate{axis}(calc(-current * (100% - peek*2) + peek))
      ;; When peek=0px this simplifies to translate{axis}(-current * 100%)
      ;; 100% in transform = track width = viewport width (track is block child)
      (if (= transition "slide")
        (do
          (let [axis (if vertical? "Y" "X")]
            (if (= peek-val "0px")
              (set! (.. track -style -transform)
                    (str "translate" axis "(" (* current -100) "%)"))
              (set! (.. track -style -transform)
                    (str "translate" axis "(calc("
                         (* current -1) " * (100% - " peek-val " * 2) + "
                         peek-val "))"))))
          ;; Clear fade inline styles if switching from fade to slide
          (clear-fade-styles! el))
        ;; Fade mode — set opacity inline on slides
        (do
          (set! (.. track -style -transform) "")
          (update-fade-active! el current)))

      ;; Arrow visibility
      (if (model/show-arrows? m)
        (do (set! (.. prev -style -display) "flex")
            (set! (.. nxt -style -display) "flex")
            (set! (.-disabled prev) (not (model/can-go-prev? m)))
            (set! (.-disabled nxt) (not (model/can-go-next? m))))
        (do (set! (.. prev -style -display) "none")
            (set! (.. nxt -style -display) "none")))

      ;; Dots
      (if (model/show-dots? m)
        (do (set! (.. dots -style -display) "flex")
            (rebuild-dots! el dots sc current))
        (set! (.. dots -style -display) "none"))

      ;; Live region
      (when (> sc 0)
        (set! (.-textContent live)
              (str "Slide " (inc current) " of " sc)))

      ;; Autoplay live region adjustment
      (if (:autoplay? m)
        (.setAttribute live "aria-live" "off")
        (.setAttribute live "aria-live" "polite"))))
  nil)

;; ── Navigation ──────────────────────────────────────────────────────────────
(defn- go-to! [^js el index reason]
  (let [m       (read-model el)
        sc      (:slide-count m)
        target  (model/clamp-index index sc)
        current (:current m)]
    (when (and (not= target current) (> sc 0))
      (let [^js evt (js/CustomEvent.
                     model/event-change
                     #js {:detail    #js {:index         target
                                          :previousIndex current
                                          :reason        reason}
                          :bubbles   true
                          :composed  true
                          :cancelable true})]
        (when (.dispatchEvent el evt)
          (.setAttribute el model/attr-current (str target))))))
  nil)

;; ── Pointer drag ────────────────────────────────────────────────────────────
(defn- on-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (and (not (:disabled? m))
               (= (:transition m) "slide")
               (> (:slide-count m) 1))
      (.preventDefault evt)
      (let [refs  (du/getv el k-refs)
            ^js track (gobj/get refs "track")
            vertical? (= (:direction m) "vertical")
            coord (if vertical? (.-clientY evt) (.-clientX evt))]
        (.setPointerCapture track (.-pointerId evt))
        (.add (.-classList track) "dragging")
        (du/setv! el k-dragging true)
        (du/setv! el k-drag-start coord)
        (du/setv! el k-drag-start-t (js/Date.now))
        (du/setv! el k-drag-current coord)
        ;; Pause autoplay during drag
        (du/setv! el k-paused true)
        (stop-autoplay! el))))
  nil)

(defn- on-pointermove! [^js el ^js evt]
  (when (du/getv el k-dragging)
    (let [m         (read-model el)
          vertical? (= (:direction m) "vertical")
          coord     (if vertical? (.-clientY evt) (.-clientX evt))
          start     (du/getv el k-drag-start)
          delta     (- coord start)
          refs      (du/getv el k-refs)
          ^js track (gobj/get refs "track")
          current   (:current m)
          axis      (if vertical? "Y" "X")
          peek-val  (:peek m)]
      (du/setv! el k-drag-current coord)
      ;; Use same formula as render but add pixel drag offset
      (if (= peek-val "0px")
        (set! (.. track -style -transform)
              (str "translate" axis "(calc(" (* current -100) "% + " delta "px))"))
        (set! (.. track -style -transform)
              (str "translate" axis "(calc("
                   (* current -1) " * (100% - " peek-val " * 2) + "
                   peek-val " + " delta "px))")))))
  nil)

(defn- on-pointerup! [^js el ^js _evt]
  (when (du/getv el k-dragging)
    (du/setv! el k-dragging false)
    (let [refs      (du/getv el k-refs)
          ^js track (gobj/get refs "track")
          start     (du/getv el k-drag-start)
          current   (du/getv el k-drag-current)
          delta     (- current start)
          elapsed   (- (js/Date.now) (du/getv el k-drag-start-t))
          velocity  (if (pos? elapsed) (/ delta elapsed) 0)
          snap      (model/snap-direction delta velocity)
          m         (read-model el)]
      (.remove (.-classList track) "dragging")
      (case snap
        :prev (go-to! el (model/prev-index m) "drag")
        :next (go-to! el (model/next-index m) "drag")
        (render! el))
      ;; Resume autoplay
      (du/setv! el k-paused false)
      (start-autoplay! el)))
  nil)

;; ── Keyboard ────────────────────────────────────────────────────────────────
(defn- on-keydown! [^js el ^js evt]
  (let [m   (read-model el)
        key (.-key evt)
        dir (:direction m)]
    (when (and (not (:disabled? m)) (> (:slide-count m) 1))
      (cond
        (model/prev-key? dir key)
        (do (.preventDefault evt)
            (go-to! el (model/prev-index m) "keyboard"))

        (model/next-key? dir key)
        (do (.preventDefault evt)
            (go-to! el (model/next-index m) "keyboard"))

        (= key "Home")
        (do (.preventDefault evt)
            (go-to! el 0 "keyboard"))

        (= key "End")
        (do (.preventDefault evt)
            (go-to! el (dec (:slide-count m)) "keyboard")))))
  nil)

;; ── Dot click (event delegation) ────────────────────────────────────────────
(defn- on-dot-click! [^js el ^js evt]
  (let [^js target (.-target evt)]
    (when-let [idx-str (.getAttribute target "data-index")]
      (let [idx (js/parseInt idx-str 10)]
        (when-not (js/isNaN idx)
          (go-to! el idx "dot")))))
  nil)

;; ── Arrow clicks ────────────────────────────────────────────────────────────
(defn- on-prev-click! [^js el ^js _evt]
  (let [m (read-model el)]
    (go-to! el (model/prev-index m) "arrow"))
  nil)

(defn- on-next-click! [^js el ^js _evt]
  (let [m (read-model el)]
    (go-to! el (model/next-index m) "arrow"))
  nil)

;; ── Autoplay pause/resume on hover & focus ──────────────────────────────────
(defn- on-pointerenter! [^js el ^js _evt]
  (du/setv! el k-paused true)
  (stop-autoplay! el)
  nil)

(defn- on-pointerleave! [^js el ^js _evt]
  (du/setv! el k-paused false)
  (start-autoplay! el)
  nil)

(defn- on-focusin! [^js el ^js _evt]
  (du/setv! el k-paused true)
  (stop-autoplay! el)
  nil)

(defn- on-focusout! [^js el ^js evt]
  ;; Only resume if focus left the component entirely
  (when-not (.contains el (.-relatedTarget evt))
    (du/setv! el k-paused false)
    (start-autoplay! el))
  nil)

;; ── Slotchange ──────────────────────────────────────────────────────────────
(defn- on-slotchange! [^js el]
  (let [refs      (du/getv el k-refs)
        ^js slot  (gobj/get refs "slot")
        children  (.assignedElements slot)
        cnt       (.-length children)]
    (du/setv! el k-slide-count cnt)
    ;; Clamp current index
    (let [m       (read-model el)
          clamped (model/clamp-index (:current m) cnt)]
      (when (not= clamped (:current m))
        (.setAttribute el model/attr-current (str clamped))))
    (render! el)
    (start-autoplay! el))
  nil)

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs      (du/getv el k-refs)
        ^js track (gobj/get refs "track")
        ^js slot  (gobj/get refs "slot")
        ^js prev  (gobj/get refs "prev")
        ^js nxt   (gobj/get refs "next")
        ^js dots  (gobj/get refs "dots")
        ^js vp    (gobj/get refs "viewport")

        h-down    (fn [e] (on-pointerdown! el e))
        h-move    (fn [e] (on-pointermove! el e))
        h-up      (fn [e] (on-pointerup! el e))
        h-key     (fn [e] (on-keydown! el e))
        h-dot     (fn [e] (on-dot-click! el e))
        h-prev    (fn [e] (on-prev-click! el e))
        h-next    (fn [e] (on-next-click! el e))
        h-slot    (fn [_] (on-slotchange! el))
        h-enter   (fn [e] (on-pointerenter! el e))
        h-leave   (fn [e] (on-pointerleave! el e))
        h-focusin (fn [e] (on-focusin! el e))
        h-focusout (fn [e] (on-focusout! el e))]

    (.addEventListener track "pointerdown" h-down)
    (.addEventListener track "pointermove" h-move)
    (.addEventListener track "pointerup"   h-up)
    (.addEventListener vp    "keydown"     h-key)
    (.addEventListener dots  "click"       h-dot)
    (.addEventListener prev  "click"       h-prev)
    (.addEventListener nxt   "click"       h-next)
    (.addEventListener slot  "slotchange"  h-slot)
    (.addEventListener el    "pointerenter" h-enter)
    (.addEventListener el    "pointerleave" h-leave)
    (.addEventListener el    "focusin"      h-focusin)
    (.addEventListener el    "focusout"     h-focusout)

    (du/setv! el k-handlers
              #js {"down"     h-down
                   "move"     h-move
                   "up"       h-up
                   "key"      h-key
                   "dot"      h-dot
                   "prev"     h-prev
                   "next"     h-next
                   "slot"     h-slot
                   "enter"    h-enter
                   "leave"    h-leave
                   "focusin"  h-focusin
                   "focusout" h-focusout}))
  nil)

(defn- remove-listeners! [^js el]
  (stop-autoplay! el)
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js track (gobj/get refs "track")
            ^js slot  (gobj/get refs "slot")
            ^js prev  (gobj/get refs "prev")
            ^js nxt   (gobj/get refs "next")
            ^js dots  (gobj/get refs "dots")
            ^js vp    (gobj/get refs "viewport")]
        (.removeEventListener track "pointerdown"  (gobj/get hs "down"))
        (.removeEventListener track "pointermove"  (gobj/get hs "move"))
        (.removeEventListener track "pointerup"    (gobj/get hs "up"))
        (.removeEventListener vp    "keydown"      (gobj/get hs "key"))
        (.removeEventListener dots  "click"        (gobj/get hs "dot"))
        (.removeEventListener prev  "click"        (gobj/get hs "prev"))
        (.removeEventListener nxt   "click"        (gobj/get hs "next"))
        (.removeEventListener slot  "slotchange"   (gobj/get hs "slot"))
        (.removeEventListener el    "pointerenter" (gobj/get hs "enter"))
        (.removeEventListener el    "pointerleave" (gobj/get hs "leave"))
        (.removeEventListener el    "focusin"      (gobj/get hs "focusin"))
        (.removeEventListener el    "focusout"     (gobj/get hs "focusout")))))
  (du/setv! el k-handlers nil)
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; Boolean properties (presence = true)
  (doseq [attr [model/attr-autoplay model/attr-loop model/attr-disabled]]
    (.defineProperty js/Object proto attr
                     #js {:get (fn []
                                 (this-as ^js this
                                          (.hasAttribute this attr)))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if v
                                            (.setAttribute this attr "")
                                            (.removeAttribute this attr))))
                          :enumerable true :configurable true}))

  ;; Default-true boolean properties (arrows, dots)
  (doseq [attr [model/attr-arrows model/attr-dots]]
    (.defineProperty js/Object proto attr
                     #js {:get (fn []
                                 (this-as ^js this
                                          (model/parse-bool-default-true
                                           (.getAttribute this attr))))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if v
                                            (.removeAttribute this attr)
                                            (.setAttribute this attr "false"))))
                          :enumerable true :configurable true}))

  ;; currentSlide -> current attribute
  (.defineProperty js/Object proto "currentSlide"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-non-neg-int
                                         (.getAttribute this model/attr-current)
                                         model/default-current)))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-current
                                                       (str (int v)))))
                        :enumerable true :configurable true})

  ;; interval
  (.defineProperty js/Object proto model/attr-interval
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-pos-int
                                         (.getAttribute this model/attr-interval)
                                         model/default-interval 100)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-interval)
                                          (.setAttribute this model/attr-interval
                                                         (str (int v))))))
                        :enumerable true :configurable true})

  ;; transition
  (.defineProperty js/Object proto model/attr-transition
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-transition
                                         (.getAttribute this model/attr-transition))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-transition (str v))
                                          (.removeAttribute this model/attr-transition))))
                        :enumerable true :configurable true})

  ;; direction
  (.defineProperty js/Object proto model/attr-direction
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-direction
                                         (.getAttribute this model/attr-direction))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-direction (str v))
                                          (.removeAttribute this model/attr-direction))))
                        :enumerable true :configurable true})

  ;; peek
  (.defineProperty js/Object proto model/attr-peek
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-peek
                                         (.getAttribute this model/attr-peek))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-peek (str v))
                                          (.removeAttribute this model/attr-peek))))
                        :enumerable true :configurable true})

  ;; slideCount (read-only)
  (.defineProperty js/Object proto "slideCount"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/getv this k-slide-count) 0)))
                        :enumerable true :configurable true})

  ;; Public methods
  (gobj/set proto "next"
            (fn [] (this-as ^js this
                            (let [m (read-model this)]
                              (go-to! this (model/next-index m) "api")))))

  (gobj/set proto "previous"
            (fn [] (this-as ^js this
                            (let [m (read-model this)]
                              (go-to! this (model/prev-index m) "api")))))

  (gobj/set proto "goTo"
            (fn [idx] (this-as ^js this
                               (go-to! this idx "api")))))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/initialized? el k-init)
    (init-dom! el)
    (du/mark-initialized! el k-init))
  (remove-listeners! el)
  (add-listeners! el)
  ;; Trigger initial slotchange read
  (let [refs (du/getv el k-refs)
        ^js slot (gobj/get refs "slot")
        children (.assignedElements slot)]
    (du/setv! el k-slide-count (.-length children)))
  (render! el)
  (start-autoplay! el)
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  nil)

(defn- attribute-changed! [^js el _name _old _new]
  (when (du/initialized? el k-init)
    (render! el)
    (when (.-isConnected el)
      (start-autoplay! el)))
  nil)

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")
        proto (.-prototype klass)]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback proto)
          (fn []
            (this-as ^js this (connected! this))))

    (set! (.-disconnectedCallback proto)
          (fn []
            (this-as ^js this (disconnected! this))))

    (set! (.-attributeChangedCallback proto)
          (fn [n o v]
            (this-as ^js this (attribute-changed! this n o v))))

    (install-property-accessors! proto)
    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
