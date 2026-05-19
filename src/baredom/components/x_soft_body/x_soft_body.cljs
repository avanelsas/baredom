(ns baredom.components.x-soft-body.x-soft-body
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-soft-body.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs           "__xSoftBodyRefs")
(def ^:private k-raf            "__xSoftBodyRaf")
(def ^:private k-model          "__xSoftBodyModel")
(def ^:private k-rest-x         "__xSoftBodyRX")
(def ^:private k-rest-y         "__xSoftBodyRY")
(def ^:private k-cur-x          "__xSoftBodyCX")
(def ^:private k-cur-y          "__xSoftBodyCY")
(def ^:private k-vel-x          "__xSoftBodyVX")
(def ^:private k-vel-y          "__xSoftBodyVY")
(def ^:private k-last-frame     "__xSoftBodyLF")
(def ^:private k-pointer-x      "__xSoftBodyPX")
(def ^:private k-pointer-y      "__xSoftBodyPY")
(def ^:private k-pointer-active "__xSoftBodyPA")
(def ^:private k-grabbed        "__xSoftBodyGrab")
(def ^:private k-ro             "__xSoftBodyRO")
(def ^:private k-width          "__xSoftBodyW")
(def ^:private k-height         "__xSoftBodyH")
(def ^:private k-handlers       "__xSoftBodyHdl")

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "background:none;"
   "overflow:visible;"
   model/css-bg ":var(--x-color-surface,#ffffff);"
   model/css-border ":var(--x-color-border,#e2e8f0);"
   model/css-border-width ":1.5;"
   model/css-shadow ":var(--x-shadow-sm,0 1px 3px rgba(0,0,0,0.1));"
   model/css-padding ":1rem;}"

   "[part=svg]{"
   "display:block;"
   "width:100%;"
   "height:100%;"
   "position:absolute;"
   "inset:0;"
   "overflow:visible;"
   "pointer-events:none;}"

   "[part=shape]{"
   "fill:var(" model/css-bg ",#ffffff);"
   "stroke:var(" model/css-border ",#e2e8f0);"
   "stroke-width:var(" model/css-border-width ",1.5);"
   "filter:drop-shadow(var(" model/css-shadow ",0 1px 3px rgba(0,0,0,0.1)));}"

   "[part=content]{"
   "position:relative;"
   "background:none;"
   "padding:var(" model/css-padding ",1rem);"
   "z-index:1;}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   model/css-bg ":var(--x-color-surface,#1e293b);"
   model/css-border ":var(--x-color-border,#475569);"
   model/css-shadow ":var(--x-shadow-sm,0 1px 3px rgba(0,0,0,0.3));}}"

   "@media(prefers-reduced-motion:reduce){"
   "[part=shape]{transition:none!important;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── Velocity threshold for settling ─────────────────────────────────────────
(def ^:private settle-threshold 0.1)

(defn- settled?
  "Returns true when all spring velocities are below threshold."
  [^js vx ^js vy n]
  (loop [i 0]
    (if (>= i n)
      true
      (if (or (> (js/Math.abs (aget vx i)) settle-threshold)
              (> (js/Math.abs (aget vy i)) settle-threshold))
        false
        (recur (inc i))))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        svg     (.createElementNS js/document svg-ns "svg")
        path    (.createElementNS js/document svg-ns "path")
        content (.createElement js/document "div")
        slot    (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (du/set-attr! svg "part" "svg")
    (du/set-attr! svg "viewBox" "0 0 300 200")
    (du/set-attr! svg "preserveAspectRatio" "none")
    (du/set-attr! svg "aria-hidden" "true")

    (du/set-attr! path "part" "shape")

    (du/set-attr! content "part" "content")

    (.appendChild svg path)
    (.appendChild content slot)
    (.appendChild root style)
    (.appendChild root svg)
    (.appendChild root content)

    (du/setv! el k-refs
              {:root    root
               :svg     svg
               :path    path
               :content content})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:stiffness-raw   (du/get-attr el model/attr-stiffness)
    :damping-raw     (du/get-attr el model/attr-damping)
    :radius-raw      (du/get-attr el model/attr-radius)
    :intensity-raw   (du/get-attr el model/attr-intensity)
    :grab-radius-raw (du/get-attr el model/attr-grab-radius)
    :disabled-attr   (du/get-attr el model/attr-disabled)}))

;; ── Physics initialisation ──────────────────────────────────────────────────
(defn- init-physics!
  "Generate fresh rest-point arrays for a (w,h,radius) configuration.
   Takes the radius from `m` rather than the cached `k-model` so callers
   can pass the freshly-read model before the cache write."
  [^js el w h m]
  (let [r      (:radius m)
        result (model/generate-rest-points w h r)
        ^js rx (aget result 0)
        ^js ry (aget result 1)
        n      model/point-count
        cx     (js/Float64Array. n)
        cy     (js/Float64Array. n)
        vx     (js/Float64Array. n)
        vy     (js/Float64Array. n)]
    ;; Copy rest positions to current positions
    (dotimes [i n]
      (aset cx i (aget rx i))
      (aset cy i (aget ry i))
      (aset vx i 0.0)
      (aset vy i 0.0))
    (du/setv! el k-rest-x rx)
    (du/setv! el k-rest-y ry)
    (du/setv! el k-cur-x cx)
    (du/setv! el k-cur-y cy)
    (du/setv! el k-vel-x vx)
    (du/setv! el k-vel-y vy)))

;; ── Render path ─────────────────────────────────────────────────────────────
(defn- render-path! [^js el]
  (let [{:keys [path]} (du/getv el k-refs)
        ^js path path
        ^js cx   (du/getv el k-cur-x)
        ^js cy   (du/getv el k-cur-y)
        d        (model/points->path-d cx cy model/point-count)]
    ;; Hot path: rAF-driven, ~60 writes/sec on the soft-body path "d".
    (du/set-attr-untraced! path "d" d)))

(defn- render-static! [^js el]
  (let [{:keys [path]} (du/getv el k-refs)
        ^js path path
        w (du/getv el k-width)
        h (du/getv el k-height)
        m (du/getv el k-model)
        r (:radius m)
        d (model/static-rounded-rect-d w h r)]
    (du/set-attr! path "d" d)))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (du/getv el k-model)
          now        (js/performance.now)
          last-frame (du/getv el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          n          model/point-count
          stiffness  (:stiffness m)
          damping    (:damping m)
          intensity  (:intensity m)
          grab-rad   (:grab-radius m)
          ^js rx     (du/getv el k-rest-x)
          ^js ry     (du/getv el k-rest-y)
          ^js cx     (du/getv el k-cur-x)
          ^js cy     (du/getv el k-cur-y)
          ^js vx     (du/getv el k-vel-x)
          ^js vy     (du/getv el k-vel-y)
          ptr-active (du/getv el k-pointer-active)
          ptr-x      (du/getv el k-pointer-x)
          ptr-y      (du/getv el k-pointer-y)
          grabbed?   (du/getv el k-grabbed)]

      (du/setv-untraced! el k-last-frame now)

      ;; Update each control point
      (dotimes [i n]
        (let [rest-xi (aget rx i)
              rest-yi (aget ry i)
              ;; Compute pointer-induced target offset
              offset  (if ptr-active
                        (model/compute-pointer-offset
                         rest-xi rest-yi ptr-x ptr-y grab-rad intensity grabbed?)
                        #js [0.0 0.0])
              target-x (+ rest-xi (aget offset 0))
              target-y (+ rest-yi (aget offset 1))
              ;; Spring step X
              res-x    (model/spring-step (aget cx i) target-x (aget vx i) dt stiffness damping)
              ;; Spring step Y
              res-y    (model/spring-step (aget cy i) target-y (aget vy i) dt stiffness damping)]
          (aset cx i (aget res-x 0))
          (aset vx i (aget res-x 1))
          (aset cy i (aget res-y 0))
          (aset vy i (aget res-y 1))))

      ;; Render the updated path
      (render-path! el)

      ;; Continue loop if still active or not settled
      (if (or ptr-active (not (settled? vx vy n)))
        (du/setv-untraced! el k-raf
                  (js/requestAnimationFrame (fn animate-tick [_] (animate! el))))
        ;; Settled — stop loop, snap to rest
        (do
          (dotimes [i n]
            (aset cx i (aget rx i))
            (aset cy i (aget ry i)))
          (render-path! el)
          (du/setv-untraced! el k-raf nil))))))

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (du/setv-untraced! el k-last-frame (js/performance.now))
    (du/setv-untraced! el k-raf
              (js/requestAnimationFrame (fn animate-first-frame [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv-untraced! el k-raf nil)))

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
        ;; Update SVG viewBox
        (let [{:keys [svg]} (du/getv el k-refs)
              ^js svg svg]
          (du/set-attr! svg "viewBox" (str "0 0 " w " " h)))
        ;; Reinitialise physics with new dimensions
        (let [m (du/getv el k-model)]
          (init-physics! el w h m)
          ;; Render immediately
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-path! el)
                (start-animation! el))))))))

;; ── Pointer event handlers ──────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [{:keys [svg]} (du/getv el k-refs)
        ^js svg svg
        ^js rect (.getBoundingClientRect svg)]
    (du/setv! el k-pointer-x (- (.-clientX e) (.-left rect)))
    (du/setv! el k-pointer-y (- (.-clientY e) (.-top rect)))))

(defn- on-pointerenter [^js el]
  (du/setv! el k-pointer-active true)
  (let [m (du/getv el k-model)]
    (when-not (or (:disabled? m) (prefers-reduced-motion?))
      (start-animation! el))))

(defn- on-pointerleave [^js el]
  (du/setv! el k-pointer-active false)
  (du/setv! el k-grabbed false))

(defn- on-pointerdown [^js el ^js e]
  (du/setv! el k-grabbed true)
  ;; Update pointer position from the down event
  (on-pointermove el e)
  (du/dispatch! el model/event-grab #js {}))

(defn- on-pointerup [^js el]
  (du/setv! el k-grabbed false)
  (du/dispatch! el model/event-release #js {}))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [move-fn  (fn [^js e] (on-pointermove el e))
        enter-fn (fn [] (on-pointerenter el))
        leave-fn (fn [] (on-pointerleave el))
        down-fn  (fn [^js e] (on-pointerdown el e))
        up-fn    (fn [] (on-pointerup el))]
    (du/setv! el k-handlers
              #js {"move" move-fn "enter" enter-fn "leave" leave-fn
                   "down" down-fn "up" up-fn})
    (.addEventListener el "pointermove" move-fn #js {:passive true})
    (.addEventListener el "pointerenter" enter-fn)
    (.addEventListener el "pointerleave" leave-fn)
    (.addEventListener el "pointerdown" down-fn)
    (.addEventListener el "pointerup" up-fn)))

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (du/getv el k-handlers)]
    (.removeEventListener el "pointermove" (gobj/get hdl "move"))
    (.removeEventListener el "pointerenter" (gobj/get hdl "enter"))
    (.removeEventListener el "pointerleave" (gobj/get hdl "leave"))
    (.removeEventListener el "pointerdown" (gobj/get hdl "down"))
    (.removeEventListener el "pointerup" (gobj/get hdl "up"))
    (du/setv! el k-handlers nil)))

;; ── Update from attributes ──────────────────────────────────────────────────
;; ── Apply model (cache-at-tail render-pipeline) ───────────────────────────
(defn- apply-model! [^js el m]
  ;; Re-init physics if we have dimensions (radius may have changed)
  (let [w (du/getv el k-width)
        h (du/getv el k-height)]
    (when (and w h (> w 0) (> h 0))
      (init-physics! el w h m)
      (if (or (:disabled? m) (prefers-reduced-motion?))
        (do (stop-animation! el)
            (render-static! el))
        (do (render-path! el)
            (start-animation! el)))))
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto model/attr-stiffness model/attr-stiffness   model/parse-stiffness)
  (du/define-parsed-prop! proto model/attr-damping   model/attr-damping     model/parse-damping)
  (du/define-parsed-prop! proto model/attr-radius    model/attr-radius      model/parse-radius)
  (du/define-parsed-prop! proto model/attr-intensity model/attr-intensity   model/parse-intensity)
  (du/define-parsed-prop! proto "grabRadius"         model/attr-grab-radius model/parse-grab-radius)
  (du/define-bool-prop!   proto model/attr-disabled  model/attr-disabled))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (ensure-refs! el)
    (du/setv! el k-pointer-active false)
    (du/setv! el k-grabbed false)
    (du/setv! el k-pointer-x 0.0)
    (du/setv! el k-pointer-y 0.0)
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
