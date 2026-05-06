(ns baredom.components.x-confetti.x-confetti
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-confetti.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs       "__xConfettiRefs")
(def ^:private k-model      "__xConfettiModel")
(def ^:private k-particles  "__xConfettiParticles")
(def ^:private k-raf        "__xConfettiRaf")
(def ^:private k-last-time  "__xConfettiLastTime")
(def ^:private k-canvas-w   "__xConfettiCanvasW")
(def ^:private k-canvas-h   "__xConfettiCanvasH")
(def ^:private k-resize-obs "__xConfettiResizeObs")
(def ^:private k-burst-start "__xConfettiBurstStart")
(def ^:private k-auto-fired "__xConfettiAutoFired")

;; Particle field keys (string-keyed JS objects — Closure Advanced safe via aget).
(def ^:private p-x       "x")
(def ^:private p-y       "y")
(def ^:private p-vx      "vx")
(def ^:private p-vy      "vy")
(def ^:private p-rot     "rot")
(def ^:private p-vrot    "vrot")
(def ^:private p-size    "size")
(def ^:private p-color   "color")
(def ^:private p-shape   "shape")
(def ^:private p-life    "life")
(def ^:private p-max     "max")
(def ^:private p-alive   "alive")
(def ^:private p-gravity "g")

;; ── Constants ────────────────────────────────────────────────────────────────
(def ^:private max-dpr 2.0)
(def ^:private despawn-margin 80)
(def ^:private drag-x 0.985)
(def ^:private drag-y 0.998)
(def ^:private frame-ref-ms 16.6667)

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "pointer-events:none;"
   "}"

   ":host([data-mode='overlay']){"
   "position:fixed;"
   "inset:0;"
   "z-index:var(--x-confetti-z-index,9999);"
   "}"

   ":host([data-mode='inline']){"
   "position:absolute;"
   "inset:0;"
   "width:100%;"
   "height:100%;"
   "}"

   "[part='canvas-wrap']{"
   "position:absolute;"
   "inset:0;"
   "width:100%;"
   "height:100%;"
   "pointer-events:none;"
   "overflow:hidden;"
   "}"

   "canvas{"
   "display:block;"
   "width:100%;"
   "height:100%;"
   "}"))

;; ── Reduced-motion helper ────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean
   (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── CSS var() resolver ──────────────────────────────────────────────────────
;; Canvas `fillStyle` does not understand `var(--name, fallback)`. To keep the
;; theme-token defaults working, resolve such strings against the host's
;; computed style at fire-time. Plain colors pass through unchanged.
(defn- resolve-css-color [^js host ^js color]
  (if (and (string? color) (.startsWith color "var("))
    (let [^js inner (.slice color 4 (dec (.-length color)))
          comma    (.indexOf inner ",")
          var-name (if (>= comma 0)
                     (.trim (.slice inner 0 comma))
                     (.trim inner))
          fallback (if (>= comma 0)
                     (.trim (.slice inner (inc comma)))
                     "")
          ^js cs   (when host (.getComputedStyle js/window host))
          raw      (when cs (.getPropertyValue cs var-name))
          computed (when (string? raw) (.trim raw))]
      (if (and (string? computed) (not= "" computed))
        computed
        (if (= "" fallback) "#888" fallback)))
    color))

;; ── Shadow DOM init ──────────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root   (.attachShadow el #js {:mode "open"})
        style  (.createElement js/document "style")
        wrap   (.createElement js/document "div")
        canvas (.createElement js/document "canvas")
        ctx    (.getContext canvas "2d")]
    (set! (.-textContent style) style-text)
    (.setAttribute wrap "part" "canvas-wrap")
    (.setAttribute canvas "part" "canvas")
    (.appendChild wrap canvas)
    (.appendChild root style)
    (.appendChild root wrap)
    (du/setv! el k-refs
              {:root   root
               :wrap   wrap
               :canvas canvas
               :ctx    ctx})
    (du/setv! el k-particles #js [])
    (du/setv! el k-canvas-w 0)
    (du/setv! el k-canvas-h 0))
  nil)

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

(defn- get-ctx ^js [^js el]
  (:ctx (du/getv el k-refs)))

(defn- get-canvas ^js [^js el]
  (:canvas (du/getv el k-refs)))

;; ── Canvas sizing ────────────────────────────────────────────────────────────
(defn- resize-canvas! [^js el w h]
  (let [^js canvas (get-canvas el)
        ^js ctx    (get-ctx el)
        dpr        (js/Math.min max-dpr (or js/devicePixelRatio 1))
        cw         (js/Math.max 1 (js/Math.floor (* w dpr)))
        ch         (js/Math.max 1 (js/Math.floor (* h dpr)))]
    (when (and canvas ctx (pos? w) (pos? h))
      (set! (.-width canvas) cw)
      (set! (.-height canvas) ch)
      (.setTransform ctx 1 0 0 1 0 0)
      (.scale ctx dpr dpr)
      (du/setv! el k-canvas-w w)
      (du/setv! el k-canvas-h h)))
  nil)

(defn- setup-resize-observer! [^js el]
  (let [ro (js/ResizeObserver.
            (fn [^js entries]
              (when (pos? (alength entries))
                (let [^js entry (aget entries 0)
                      ^js cr    (.-contentRect entry)
                      w         (.-width cr)
                      h         (.-height cr)]
                  (when (and (pos? w) (pos? h))
                    (resize-canvas! el w h))))))]
    (.observe ro el)
    (du/setv! el k-resize-obs ro))
  nil)

(defn- teardown-resize-observer! [^js el]
  (when-let [^js ro (du/getv el k-resize-obs)]
    (.disconnect ro)
    (du/setv! el k-resize-obs nil))
  nil)

;; ── Model read/apply ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:mode-raw     (du/get-attr el model/attr-mode)
    :origin-raw   (du/get-attr el model/attr-origin)
    :count-raw    (du/get-attr el model/attr-count)
    :spread-raw   (du/get-attr el model/attr-spread)
    :velocity-raw (du/get-attr el model/attr-velocity)
    :gravity-raw  (du/get-attr el model/attr-gravity)
    :duration-raw (du/get-attr el model/attr-duration)
    :colors-raw   (du/get-attr el model/attr-colors)
    :shapes-raw   (du/get-attr el model/attr-shapes)
    :auto-fire?   (du/has-attr? el model/attr-auto-fire)
    :disabled?    (du/has-attr? el model/attr-disabled)}))

(defn- apply-host-attrs! [^js el m]
  ;; data-mode drives host position/z-index via shadow CSS.
  (du/set-attr! el "data-mode" (:mode m))
  (du/set-attr! el "aria-hidden" "true")
  (du/set-attr! el "tabindex" "-1")
  nil)

(defn- apply-model! [^js el m]
  (ensure-refs! el)
  (apply-host-attrs! el m)
  (du/setv! el k-model m)
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

(defn- current-model [^js el]
  (or (du/getv el k-model)
      (let [m (read-model el)]
        (apply-model! el m)
        m)))

;; ── Particle creation ────────────────────────────────────────────────────────
(defn- pick-from-vec [v]
  (let [n (count v)]
    (if (zero? n) nil (nth v (js/Math.floor (* (js/Math.random) n))))))

(defn- spawn-particle!
  [^js el origin ox oy spread velocity gravity duration colors shapes]
  (let [w        (or (du/getv el k-canvas-w) 0)
        h        (or (du/getv el k-canvas-h) 0)
        [bx by]  (model/origin-point origin w h ox oy)
        ;; small random scatter near the origin
        sx       (+ bx (* (- (js/Math.random) 0.5) 60))
        sy       (+ by (* (- (js/Math.random) 0.5) 30))
        angle    (model/launch-angle-rad origin spread (js/Math.random))
        speed    (* velocity (+ 0.7 (* (js/Math.random) 0.5)))
        vx       (* speed (js/Math.cos angle))
        vy       (* speed (js/Math.sin angle))
        rot      (* 2.0 js/Math.PI (js/Math.random))
        vrot     (* (- (js/Math.random) 0.5) 0.6)
        size     (+ 5 (* (js/Math.random) 6))
        color    (or (pick-from-vec colors) "#ffffff")
        shape    (or (pick-from-vec shapes) "square")
        p        #js {}]
    (aset p p-x sx)
    (aset p p-y sy)
    (aset p p-vx vx)
    (aset p p-vy vy)
    (aset p p-rot rot)
    (aset p p-vrot vrot)
    (aset p p-size size)
    (aset p p-color color)
    (aset p p-shape shape)
    (aset p p-life duration)
    (aset p p-max  duration)
    (aset p p-gravity gravity)
    (aset p p-alive true)
    p))

(defn- spawn-burst!
  "Add `count` new particles to the live array."
  [^js el count origin ox oy spread velocity gravity duration colors shapes]
  (let [^js arr (du/getv el k-particles)]
    (dotimes [_ count]
      (.push arr (spawn-particle! el origin ox oy spread velocity gravity duration colors shapes))))
  nil)

;; ── RAF loop ─────────────────────────────────────────────────────────────────
(declare animate!)

(defn- stop-animation! [^js el]
  (when-let [raf (du/getv el k-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv! el k-raf nil))
  nil)

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (du/setv! el k-last-time (js/performance.now))
    (du/setv! el k-burst-start (js/performance.now))
    (du/setv! el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el)))))
  nil)

(defn- step-particles! [^js el dt-frames]
  (let [^js arr (du/getv el k-particles)
        h       (or (du/getv el k-canvas-h) 0)
        len     (alength arr)]
    (loop [i 0]
      (when (< i len)
        (let [^js p (aget arr i)]
          (when (aget p p-alive)
            (let [vx   (aget p p-vx)
                  vy   (+ (aget p p-vy) (* (aget p p-gravity) dt-frames))
                  vy'  (* vy (js/Math.pow drag-y dt-frames))
                  vx'  (* vx (js/Math.pow drag-x dt-frames))
                  x    (+ (aget p p-x) (* vx' dt-frames))
                  y    (+ (aget p p-y) (* vy' dt-frames))
                  rot  (+ (aget p p-rot) (* (aget p p-vrot) dt-frames))
                  life (- (aget p p-life) (* dt-frames frame-ref-ms))]
              (aset p p-vx vx')
              (aset p p-vy vy')
              (aset p p-x x)
              (aset p p-y y)
              (aset p p-rot rot)
              (aset p p-life life)
              (when (or (<= life 0) (> y (+ h despawn-margin)))
                (aset p p-alive false)))))
        (recur (inc i)))))
  nil)

(defn- compact-particles! [^js el]
  (let [^js arr  (du/getv el k-particles)
        len      (alength arr)
        ^js kept #js []]
    (loop [i 0]
      (when (< i len)
        (let [^js p (aget arr i)]
          (when (aget p p-alive)
            (.push kept p)))
        (recur (inc i))))
    (du/setv! el k-particles kept)
    (alength kept)))

(defn- draw-shape! [^js ctx shape size]
  (cond
    (= shape "square")
    (.fillRect ctx (- (* size 0.5)) (- (* size 0.5)) size size)

    (= shape "ribbon")
    (let [w size
          h (js/Math.max 1.5 (* size 0.35))]
      (.fillRect ctx (- (* w 0.5)) (- (* h 0.5)) w h))

    (= shape "circle")
    (do
      (.beginPath ctx)
      (.arc ctx 0 0 (* size 0.5) 0 (* 2.0 js/Math.PI))
      (.fill ctx))

    (= shape "star")
    (let [outer (* size 0.6)
          inner (* outer 0.45)]
      (.beginPath ctx)
      (loop [i 0]
        (when (< i 10)
          (let [r (if (zero? (mod i 2)) outer inner)
                a (* (/ js/Math.PI 5.0) i)
                x (* r (js/Math.cos a))
                y (* r (js/Math.sin a))]
            (if (zero? i)
              (.moveTo ctx x y)
              (.lineTo ctx x y)))
          (recur (inc i))))
      (.closePath ctx)
      (.fill ctx))

    :else
    (.fillRect ctx (- (* size 0.5)) (- (* size 0.5)) size size)))

(defn- draw-particles! [^js el]
  (let [^js ctx (get-ctx el)
        ^js arr (du/getv el k-particles)
        w       (or (du/getv el k-canvas-w) 0)
        h       (or (du/getv el k-canvas-h) 0)]
    (when (and ctx (pos? w) (pos? h))
      (.clearRect ctx 0 0 w h)
      (let [len (alength arr)]
        (loop [i 0]
          (when (< i len)
            (let [^js p (aget arr i)]
              (when (aget p p-alive)
                (let [life      (aget p p-life)
                      max-life  (aget p p-max)
                      fade-from (* max-life 0.25)
                      opacity   (if (< life fade-from)
                                  (js/Math.max 0 (/ life fade-from))
                                  1.0)]
                  (.save ctx)
                  (.translate ctx (aget p p-x) (aget p p-y))
                  (.rotate ctx (aget p p-rot))
                  (set! (.-globalAlpha ctx) opacity)
                  (set! (.-fillStyle ctx) (aget p p-color))
                  (draw-shape! ctx (aget p p-shape) (aget p p-size))
                  (.restore ctx))))
            (recur (inc i))))
        (set! (.-globalAlpha ctx) 1.0))))
  nil)

(defn- animate! [^js el]
  (du/setv! el k-raf nil)
  (when (.-isConnected el)
    (let [now       (js/performance.now)
          last-time (or (du/getv el k-last-time) now)
          dt-ms     (js/Math.min 100.0 (- now last-time))
          dt-frames (/ dt-ms frame-ref-ms)]
      (du/setv! el k-last-time now)
      (step-particles! el dt-frames)
      (let [alive (compact-particles! el)]
        (draw-particles! el)
        (if (pos? alive)
          (du/setv! el k-raf
                    (js/requestAnimationFrame (fn [_] (animate! el))))
          (let [start    (or (du/getv el k-burst-start) now)
                duration (js/Math.round (- now start))]
            (du/setv! el k-burst-start nil)
            (du/dispatch! el model/event-end #js {:duration duration}))))))
  nil)

;; ── fire() ──────────────────────────────────────────────────────────────────
(defn- opt-num [^js opts key fallback]
  (let [val (when opts (aget opts (name key)))]
    (if (or (nil? val) (= val js/undefined))
      fallback
      (let [n (js/parseFloat val)]
        (if (js/isNaN n) fallback n)))))

(defn- opt-str [^js opts key fallback]
  (let [val (when opts (aget opts (name key)))]
    (if (or (nil? val) (= val js/undefined))
      fallback
      (str val))))

(defn- opt-num-clamped [^js opts key fallback lo hi]
  (-> (opt-num opts key fallback)
      (js/Math.max lo)
      (js/Math.min hi)))

(defn- opt-int-clamped [^js opts key fallback lo hi]
  (js/Math.round (opt-num-clamped opts key fallback lo hi)))

(defn- opt-coord [^js opts key]
  (when opts
    (let [v (aget opts (name key))]
      (when (and (some? v) (not (js/isNaN v)))
        (js/parseFloat v)))))

(defn- opt-colors [^js opts fallback]
  (let [^js raw (when opts (aget opts "colors"))]
    (cond
      (nil? raw)             fallback
      (string? raw)          (model/parse-colors raw)
      (js/Array.isArray raw) (vec (array-seq raw))
      :else                  fallback)))

(defn- opt-shapes [^js opts fallback]
  (let [^js raw (when opts (aget opts "shapes"))]
    (cond
      (nil? raw)             fallback
      (string? raw)          (model/parse-shapes raw)
      (js/Array.isArray raw) (model/parse-shapes (.join raw ","))
      :else                  fallback)))

(defn- merged-burst-config [^js el ^js opts]
  (let [m (current-model el)]
    {:count    (opt-int-clamped opts :count    (:count m)    model/count-min    model/count-max)
     :origin   (model/parse-origin (opt-str opts :origin (:origin m)))
     :origin-x (opt-coord opts :originX)
     :origin-y (opt-coord opts :originY)
     :spread   (opt-num-clamped opts :spread   (:spread m)   model/spread-min   model/spread-max)
     :velocity (opt-num-clamped opts :velocity (:velocity m) model/velocity-min model/velocity-max)
     :gravity  (opt-num-clamped opts :gravity  (:gravity m)  model/gravity-min  model/gravity-max)
     :duration (opt-num-clamped opts :duration (:duration m) model/duration-min model/duration-max)
     :colors   (opt-colors opts (:colors m))
     :shapes   (opt-shapes opts (:shapes m))}))

(defn- do-fire! [^js el ^js opts]
  (ensure-refs! el)
  (update-from-attrs! el)
  (let [m (current-model el)]
    (when-not (:disabled? m)
      (let [cfg (merged-burst-config el opts)]
        (du/dispatch! el model/event-fire
                      #js {:count  (:count cfg)
                           :origin (:origin cfg)})
        (if (prefers-reduced-motion?)
          ;; Reduced motion: skip simulation, dispatch end immediately.
          (du/dispatch! el model/event-end #js {:duration 0})
          (let [resolved-colors (mapv #(resolve-css-color el %) (:colors cfg))]
            (spawn-burst! el
                          (:count cfg)
                          (:origin cfg)
                          (:origin-x cfg)
                          (:origin-y cfg)
                          (:spread cfg)
                          (:velocity cfg)
                          (:gravity cfg)
                          (:duration cfg)
                          resolved-colors
                          (:shapes cfg))
            (start-animation! el))))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (.defineProperty
   js/Object proto "fire"
   #js {:value (fn [opts]
                 (this-as ^js this
                          (do-fire! this opts)))
        :writable     true
        :configurable true}))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- maybe-auto-fire! [^js el]
  (when (and (du/has-attr? el model/attr-auto-fire)
             (not (du/has-attr? el model/attr-disabled))
             (not (du/getv el k-auto-fired)))
    (du/setv! el k-auto-fired true)
    (do-fire! el nil))
  nil)

(defn- measure-and-size! [^js el]
  ;; Synchronously prime the canvas size before any imperative fire() can run.
  ;; ResizeObserver fires asynchronously, so without this auto-fire (and any
  ;; same-tick fire() call) would spawn particles into a 0x0 canvas.
  (let [^js rect (.getBoundingClientRect el)
        w        (.-width rect)
        h        (.-height rect)]
    (when (and (pos? w) (pos? h))
      (resize-canvas! el w h)))
  nil)

(defn- connected! [^js el]
  (ensure-refs! el)
  (update-from-attrs! el)
  (measure-and-size! el)
  (setup-resize-observer! el)
  (maybe-auto-fire! el)
  nil)

(defn- disconnected! [^js el]
  (stop-animation! el)
  (teardown-resize-observer! el)
  ;; Drop in-flight particles — coordinates would be stale on reconnect.
  (du/setv! el k-particles #js [])
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el))
  nil)

;; ── Public API ──────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
