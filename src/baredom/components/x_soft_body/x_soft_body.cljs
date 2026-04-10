(ns baredom.components.x-soft-body.x-soft-body
  (:require
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
   model/css-border ":#e2e8f0;"
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

    (.setAttribute svg "part" "svg")
    (.setAttribute svg "viewBox" "0 0 300 200")
    (.setAttribute svg "preserveAspectRatio" "none")
    (.setAttribute svg "aria-hidden" "true")

    (.setAttribute path "part" "shape")

    (.setAttribute content "part" "content")

    (.appendChild svg path)
    (.appendChild content slot)
    (.appendChild root style)
    (.appendChild root svg)
    (.appendChild root content)

    (gobj/set el k-refs
              {:root    root
               :svg     svg
               :path    path
               :content content}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs)))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:stiffness-raw   (.getAttribute el model/attr-stiffness)
    :damping-raw     (.getAttribute el model/attr-damping)
    :radius-raw      (.getAttribute el model/attr-radius)
    :intensity-raw   (.getAttribute el model/attr-intensity)
    :grab-radius-raw (.getAttribute el model/attr-grab-radius)
    :disabled-attr   (.getAttribute el model/attr-disabled)}))

;; ── Physics initialisation ──────────────────────────────────────────────────
(defn- init-physics! [^js el w h]
  (let [m      (gobj/get el k-model)
        r      (:radius m)
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
    (gobj/set el k-rest-x rx)
    (gobj/set el k-rest-y ry)
    (gobj/set el k-cur-x cx)
    (gobj/set el k-cur-y cy)
    (gobj/set el k-vel-x vx)
    (gobj/set el k-vel-y vy))
  nil)

;; ── Render path ─────────────────────────────────────────────────────────────
(defn- render-path! [^js el]
  (let [{:keys [path]} (gobj/get el k-refs)
        ^js path path
        ^js cx   (gobj/get el k-cur-x)
        ^js cy   (gobj/get el k-cur-y)
        d        (model/points->path-d cx cy model/point-count)]
    (.setAttribute path "d" d))
  nil)

(defn- render-static! [^js el]
  (let [{:keys [path]} (gobj/get el k-refs)
        ^js path path
        w (gobj/get el k-width)
        h (gobj/get el k-height)
        m (gobj/get el k-model)
        r (:radius m)
        d (model/static-rounded-rect-d w h r)]
    (.setAttribute path "d" d))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (gobj/get el k-model)
          now        (js/performance.now)
          last-frame (gobj/get el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          n          model/point-count
          stiffness  (:stiffness m)
          damping    (:damping m)
          intensity  (:intensity m)
          grab-rad   (:grab-radius m)
          ^js rx     (gobj/get el k-rest-x)
          ^js ry     (gobj/get el k-rest-y)
          ^js cx     (gobj/get el k-cur-x)
          ^js cy     (gobj/get el k-cur-y)
          ^js vx     (gobj/get el k-vel-x)
          ^js vy     (gobj/get el k-vel-y)
          ptr-active (gobj/get el k-pointer-active)
          ptr-x      (gobj/get el k-pointer-x)
          ptr-y      (gobj/get el k-pointer-y)
          grabbed?   (gobj/get el k-grabbed)]

      (gobj/set el k-last-frame now)

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
        (gobj/set el k-raf
                  (js/requestAnimationFrame (fn [_] (animate! el))))
        ;; Settled — stop loop, snap to rest
        (do
          (dotimes [i n]
            (aset cx i (aget rx i))
            (aset cy i (aget ry i)))
          (render-path! el)
          (gobj/set el k-raf nil)))))
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
        ;; Update SVG viewBox
        (let [{:keys [svg]} (gobj/get el k-refs)
              ^js svg svg]
          (.setAttribute svg "viewBox" (str "0 0 " w " " h)))
        ;; Reinitialise physics with new dimensions
        (init-physics! el w h)
        ;; Render immediately
        (let [m (gobj/get el k-model)]
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-path! el)
                (start-animation! el)))))))
  nil)

;; ── Pointer event handlers ──────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [{:keys [svg]} (gobj/get el k-refs)
        ^js svg svg
        ^js rect (.getBoundingClientRect svg)]
    (gobj/set el k-pointer-x (- (.-clientX e) (.-left rect)))
    (gobj/set el k-pointer-y (- (.-clientY e) (.-top rect))))
  nil)

(defn- on-pointerenter [^js el]
  (gobj/set el k-pointer-active true)
  (let [m (gobj/get el k-model)]
    (when-not (or (:disabled? m) (prefers-reduced-motion?))
      (start-animation! el)))
  nil)

(defn- on-pointerleave [^js el]
  (gobj/set el k-pointer-active false)
  (gobj/set el k-grabbed false)
  nil)

(defn- on-pointerdown [^js el ^js e]
  (gobj/set el k-grabbed true)
  ;; Update pointer position from the down event
  (on-pointermove el e)
  (.dispatchEvent el
                  (js/CustomEvent.
                   model/event-grab
                   #js {:bubbles true :composed true :detail #js {}}))
  nil)

(defn- on-pointerup [^js el]
  (gobj/set el k-grabbed false)
  (.dispatchEvent el
                  (js/CustomEvent.
                   model/event-release
                   #js {:bubbles true :composed true :detail #js {}}))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [move-fn  (fn [^js e] (on-pointermove el e))
        enter-fn (fn [] (on-pointerenter el))
        leave-fn (fn [] (on-pointerleave el))
        down-fn  (fn [^js e] (on-pointerdown el e))
        up-fn    (fn [] (on-pointerup el))]
    (gobj/set el k-handlers
              #js {"move" move-fn "enter" enter-fn "leave" leave-fn
                   "down" down-fn "up" up-fn})
    (.addEventListener el "pointermove" move-fn #js {:passive true})
    (.addEventListener el "pointerenter" enter-fn)
    (.addEventListener el "pointerleave" leave-fn)
    (.addEventListener el "pointerdown" down-fn)
    (.addEventListener el "pointerup" up-fn))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (gobj/get el k-handlers)]
    (.removeEventListener el "pointermove" (gobj/get hdl "move"))
    (.removeEventListener el "pointerenter" (gobj/get hdl "enter"))
    (.removeEventListener el "pointerleave" (gobj/get hdl "leave"))
    (.removeEventListener el "pointerdown" (gobj/get hdl "down"))
    (.removeEventListener el "pointerup" (gobj/get hdl "up"))
    (gobj/set el k-handlers nil))
  nil)

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- update-from-attrs! [^js el]
  (let [m (read-model el)]
    (gobj/set el k-model m)
    ;; Re-init physics if we have dimensions (radius may have changed)
    (let [w (gobj/get el k-width)
          h (gobj/get el k-height)]
      (when (and w h (> w 0) (> h 0))
        (init-physics! el w h)
        (if (or (:disabled? m) (prefers-reduced-motion?))
          (do (stop-animation! el)
              (render-static! el))
          (do (render-path! el)
              (start-animation! el))))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; stiffness
  (.defineProperty js/Object proto model/attr-stiffness
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-stiffness
                                         (.getAttribute this model/attr-stiffness))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-stiffness)
                                          (.setAttribute this model/attr-stiffness (str v)))))
                        :enumerable true :configurable true})

  ;; damping
  (.defineProperty js/Object proto model/attr-damping
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-damping
                                         (.getAttribute this model/attr-damping))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-damping)
                                          (.setAttribute this model/attr-damping (str v)))))
                        :enumerable true :configurable true})

  ;; radius
  (.defineProperty js/Object proto model/attr-radius
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-radius
                                         (.getAttribute this model/attr-radius))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-radius)
                                          (.setAttribute this model/attr-radius (str v)))))
                        :enumerable true :configurable true})

  ;; intensity
  (.defineProperty js/Object proto model/attr-intensity
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-intensity
                                         (.getAttribute this model/attr-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-intensity)
                                          (.setAttribute this model/attr-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; grabRadius (camelCase for kebab-case attribute)
  (.defineProperty js/Object proto "grabRadius"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-grab-radius
                                         (.getAttribute this model/attr-grab-radius))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-grab-radius)
                                          (.setAttribute this model/attr-grab-radius (str v)))))
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
                       (ensure-refs! this)
                       (gobj/set this k-pointer-active false)
                       (gobj/set this k-grabbed false)
                       (gobj/set this k-pointer-x 0.0)
                       (gobj/set this k-pointer-y 0.0)
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
