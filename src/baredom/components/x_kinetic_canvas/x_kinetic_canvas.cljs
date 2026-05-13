(ns baredom.components.x-kinetic-canvas.x-kinetic-canvas
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-kinetic-canvas.model :as model]
   [baredom.components.x-kinetic-canvas.starfield :as starfield]
   [baredom.components.x-kinetic-canvas.bubbles :as bubbles]
   [baredom.components.x-kinetic-canvas.matrix :as matrix]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs     "__xKineticCanvasRefs")
(def ^:private k-model    "__xKineticCanvasModel")
(def ^:private k-entities "__xKineticCanvasEnt")
(def ^:private k-raf      "__xKineticCanvasRaf")
(def ^:private k-lf       "__xKineticCanvasLastFrame")
(def ^:private k-time     "__xKineticCanvasTime")
(def ^:private k-io       "__xKineticCanvasIO")
(def ^:private k-ro       "__xKineticCanvasRO")
(def ^:private k-visible  "__xKineticCanvasVis")
(def ^:private k-width    "__xKineticCanvasW")
(def ^:private k-height   "__xKineticCanvasH")
(def ^:private k-colors   "__xKineticCanvasCol")
(def ^:private k-reduced  "__xKineticCanvasReduced")
(def ^:private k-mql      "__xKineticCanvasMQL")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private mk-mql      "mql")
(def ^:private mk-handler  "handler")
(def ^:private ev-change   "change")
(def ^:private ctx-2d      "2d")
(def ^:private cls-wrap    "kc-canvas-wrap")
(def ^:private cls-content "kc-content")
(def ^:private attr-aria-hidden "aria-hidden")
(def ^:private val-true    "true")

(def ^:private type-starfield "starfield")
(def ^:private type-bubbles   "bubbles")
(def ^:private type-matrix    "matrix")

(def ^:private mq-reduced-motion "(prefers-reduced-motion:reduce)")
(def ^:private color-transparent "transparent")

(def ^:private default-bg      "#f0f0f5")
(def ^:private default-color-1 "#1a1a2e")
(def ^:private default-color-2 "#2563eb")
(def ^:private default-color-3 "#7c3aed")

;; ── Type dispatch map ───────────────────────────────────────────────────────
(def ^:private type-fns
  {type-starfield {:create starfield/create-entities
                   :update starfield/update-entities!
                   :draw   starfield/draw!
                   :static starfield/draw-static!}
   type-bubbles   {:create bubbles/create-entities
                   :update bubbles/update-entities!
                   :draw   bubbles/draw!
                   :static bubbles/draw-static!}
   type-matrix    {:create matrix/create-entities
                   :update matrix/update-entities!
                   :draw   matrix/draw!
                   :static matrix/draw-static!}})

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "overflow:hidden;"
   model/css-bg ":var(--x-color-bg,#f0f0f5);"
   model/css-color-1 ":var(--x-color-text,#1a1a2e);"
   model/css-color-2 ":var(--x-color-primary,#2563eb);"
   model/css-color-3 ":var(--x-color-secondary,#7c3aed);"
   model/css-matrix-font-size ":14;}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   model/css-bg ":var(--x-color-bg,#0a0a1a);"
   model/css-color-1 ":var(--x-color-text,#ffffff);"
   model/css-color-2 ":var(--x-color-primary,#60a5fa);"
   model/css-color-3 ":var(--x-color-secondary,#c084fc);}}"

   ":host([fullscreen]){"
   "position:fixed;inset:0;z-index:var(--x-kinetic-canvas-z,-1);}"

   ".kc-canvas-wrap{"
   "position:absolute;inset:0;z-index:0;}"

   "canvas{"
   "display:block;width:100%;height:100%;}"

   ".kc-content{"
   "position:relative;z-index:1;}"))

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- reduced-motion? [^js el]
  (boolean (du/getv el k-reduced)))

(defn- read-model [^js el]
  (model/normalize
   {:type-raw        (du/get-attr el model/attr-type)
    :variant-raw     (du/get-attr el model/attr-variant)
    :speed-raw       (du/get-attr el model/attr-speed)
    :density-raw     (du/get-attr el model/attr-density)
    :fullscreen-attr (du/get-attr el model/attr-fullscreen)
    :paused-attr     (du/get-attr el model/attr-paused)}))

(defn- invalidate-colors!
  "Clear cached colors so they are re-read on next render frame."
  [^js el]
  (du/setv! el k-colors nil))

(defn- ensure-colors!
  "Read resolved CSS custom property colors from computed style if not cached.
   Returns the background color string."
  [^js el]
  (if-let [cached (du/getv el k-colors)]
    (aget cached 0)
    (let [^js cs (.getComputedStyle js/window el)
          bg (.trim (.getPropertyValue cs model/css-bg))
          c1 (.trim (.getPropertyValue cs model/css-color-1))
          c2 (.trim (.getPropertyValue cs model/css-color-2))
          c3 (.trim (.getPropertyValue cs model/css-color-3))
          bg-val (if (= bg "") default-bg bg)
          colors #js [bg-val
                      (if (= c1 "") default-color-1 c1)
                      (if (= c2 "") default-color-2 c2)
                      (if (= c3 "") default-color-3 c3)]]
      (du/setv! el k-colors colors)
      bg-val)))

;; ── DOM initialisation ──────────────────────────────────────────────────────

(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        wrap    (.createElement js/document "div")
        canvas  (.createElement js/document "canvas")
        content (.createElement js/document "div")
        slot    (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (set! (.-className wrap) cls-wrap)
    (.setAttribute canvas attr-aria-hidden val-true)
    (.appendChild wrap canvas)

    (set! (.-className content) cls-content)
    (.appendChild content slot)

    (.appendChild root style)
    (.appendChild root wrap)
    (.appendChild root content)

    (du/setv! el k-refs
              {:root   root
               :canvas canvas
               :ctx    (.getContext canvas ctx-2d)})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Entity management ───────────────────────────────────────────────────────

(defn- read-matrix-font-size [^js el]
  (let [^js cs (.getComputedStyle js/window el)
        raw (.trim (.getPropertyValue cs model/css-matrix-font-size))]
    (if (= raw "")
      matrix/default-char-size
      (let [n (js/parseInt raw 10)]
        (if (or (js/isNaN n) (<= n 0)) matrix/default-char-size n)))))

(defn- create-entities!
  "Build the entity list for the given model + cached canvas size.
   Caller passes m explicitly so this works regardless of whether k-model has
   been updated yet."
  [^js el m]
  (let [w  (du/getv el k-width)
        h  (du/getv el k-height)
        tp (:type m)]
    (when (and w h (> w 0) (> h 0))
      (let [mfs   (when (= tp type-matrix) (read-matrix-font-size el))
            fns   (get type-fns tp)
            count (model/entity-count tp (:density m) w h
                                      (or mfs matrix/default-char-size))]
        (du/setv! el k-entities
                  ((:create fns) w h count (:variant m) mfs))))))

;; ── Canvas sizing ───────────────────────────────────────────────────────────

(defn- size-canvas! [^js el w h]
  (let [{:keys [^js canvas]} (du/getv el k-refs)
        dpr (or (.-devicePixelRatio js/window) 1)]
    (set! (.-width canvas) (* w dpr))
    (set! (.-height canvas) (* h dpr))
    ;; Scale context for DPR
    (let [^js ctx (.getContext canvas ctx-2d)]
      (.setTransform ctx dpr 0 0 dpr 0 0))))

;; ── Render ──────────────────────────────────────────────────────────────────

(declare start-animation!)

(defn- render-frame! [^js el]
  (let [m         (du/getv el k-model)
        w         (du/getv el k-width)
        h         (du/getv el k-height)
        entities  (du/getv el k-entities)
        {:keys [^js ctx]} (du/getv el k-refs)]
    (when (and w h ctx entities (> w 0) (> h 0))
      (let [bg     (ensure-colors! el)
            colors (du/getv el k-colors)
            ;; Draw colors are indices 1-3, bg is index 0
            draw-c #js [(aget colors 1) (aget colors 2) (aget colors 3)]
            tp     (:type m)
            fns    (get type-fns tp)]
        ;; Clear and fill background
        (.clearRect ctx 0 0 w h)
        (when (and (some? bg) (not= bg color-transparent))
          (set! (.-fillStyle ctx) bg)
          (.fillRect ctx 0 0 w h))
        ;; Draw entities
        (if (reduced-motion? el)
          ((:static fns) ctx entities w h draw-c)
          ((:draw fns) ctx entities w h draw-c (or (du/getv el k-time) 0.0)))))))

;; ── Animation loop ──────────────────────────────────────────────────────────

(defn- animate! [^js el]
  (cond
    (not (.-isConnected el))
    nil

    (or (:paused? (du/getv el k-model)) (reduced-motion? el))
    (du/setv! el k-raf nil)

    :else
    (let [m     (du/getv el k-model)
          now   (js/performance.now)
          lf    (du/getv el k-lf)
          dt    (/ (js/Math.min (- now lf) 100.0) 1000.0)
          tp    (:type m)
          fns   (get type-fns tp)
          ents  (du/getv el k-entities)
          w     (du/getv el k-width)
          h     (du/getv el k-height)]

      (du/setv! el k-lf now)
      (du/setv! el k-time (+ (or (du/getv el k-time) 0.0) dt))

      ;; Update entities
      (when ents
        ((:update fns) ents dt (:speed m) w h (:variant m)))

      ;; Render
      (render-frame! el)

      ;; Schedule next frame
      (du/setv! el k-raf
                (js/requestAnimationFrame (fn on-raf-tick [_] (animate! el)))))))

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (du/setv! el k-lf (js/performance.now))
    (du/setv! el k-raf
              (js/requestAnimationFrame (fn on-first-frame [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv! el k-raf nil)))

;; ── Observers ───────────────────────────────────────────────────────────────

(defn- on-resize! [^js el ^js entries]
  (when (pos? (.-length entries))
    (let [^js entry (aget entries 0)
          ^js cr    (.-contentRect entry)
          w         (.-width cr)
          h         (.-height cr)]
      (when (and (> w 0) (> h 0))
        (du/setv! el k-width w)
        (du/setv! el k-height h)
        (size-canvas! el w h)
        (invalidate-colors! el)
        (create-entities! el (du/getv el k-model))
        (if (or (:paused? (du/getv el k-model)) (reduced-motion? el))
          (render-frame! el)
          (start-animation! el))))))

(defn- on-intersection [^js el ^js entries]
  (let [^js entry  (aget entries 0)
        is-visible (.-isIntersecting entry)]
    (du/setv! el k-visible is-visible)
    (let [m (du/getv el k-model)]
      (if is-visible
        (when-not (or (:paused? m) (reduced-motion? el))
          (start-animation! el))
        (stop-animation! el)))))

(defn- on-motion-change! [^js el ^js e]
  (let [reduced (.-matches e)]
    (du/setv! el k-reduced reduced)
    (invalidate-colors! el)
    (if reduced
      (do (stop-animation! el)
          (render-frame! el))
      (when (and (du/getv el k-visible)
                 (not (:paused? (du/getv el k-model))))
        (start-animation! el)))))

(defn- setup-observers! [^js el]
  ;; Motion media query listener
  (let [^js mql (.matchMedia js/window mq-reduced-motion)
        handler (fn handle-motion-change [^js e] (on-motion-change! el e))]
    (du/setv! el k-reduced (.-matches mql))
    (.addEventListener mql ev-change handler)
    (du/setv! el k-mql #js {:mql mql :handler handler}))
  (let [io (js/IntersectionObserver.
            (fn handle-intersection [^js entries] (on-intersection el entries))
            #js {:threshold #js [0]})]
    (.observe io el)
    (du/setv! el k-io io))
  (let [ro (js/ResizeObserver.
            (fn handle-resize [^js entries] (on-resize! el entries)))]
    (.observe ro el)
    (du/setv! el k-ro ro)))

(defn- teardown-observers! [^js el]
  (when-let [^js mql-obj (du/getv el k-mql)]
    (let [^js mql (aget mql-obj mk-mql)
          handler (aget mql-obj mk-handler)]
      (.removeEventListener mql ev-change handler))
    (du/setv! el k-mql nil))
  (when-let [^js io (du/getv el k-io)]
    (.disconnect io)
    (du/setv! el k-io nil))
  (when-let [^js ro (du/getv el k-ro)]
    (.disconnect ro)
    (du/setv! el k-ro nil)))

;; ── DOM patching ────────────────────────────────────────────────────────────

(defn- apply-model! [^js el new-m old-m]
  (invalidate-colors! el)
  ;; Recreate entities when type, density, or variant changes
  (when (or (nil? old-m)
            (not= (:type new-m) (:type old-m))
            (not= (:density new-m) (:density old-m))
            (not= (:variant new-m) (:variant old-m)))
    (create-entities! el new-m))
  ;; Handle animation state
  (let [w (du/getv el k-width)
        h (du/getv el k-height)]
    (when (and w h (> w 0) (> h 0))
      (if (or (:paused? new-m) (reduced-motion? el))
        (do (stop-animation! el)
            (render-frame! el))
        (do (render-frame! el)
            (start-animation! el)))))
  (du/setv! el k-model new-m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m old-m))))

;; ── Lifecycle callbacks ─────────────────────────────────────────────────────

(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-time 0.0)
    (du/setv! el k-visible true)
    (ensure-refs! el)
    (setup-observers! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (teardown-observers! el)
  (du/setv! el k-visible false))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-refs)
      (update-from-attrs! el))))

;; ── Property accessors ──────────────────────────────────────────────────────

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
