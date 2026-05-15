(ns baredom.components.x-liquid-fill.x-liquid-fill
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-liquid-fill.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs            "__xLiquidFillRefs")
(def ^:private k-model           "__xLiquidFillModel")
(def ^:private k-raf             "__xLiquidFillRaf")
(def ^:private k-last-frame      "__xLiquidFillLF")
(def ^:private k-time            "__xLiquidFillTime")
(def ^:private k-progress        "__xLiquidFillProg")
(def ^:private k-target-progress "__xLiquidFillTP")
(def ^:private k-scroll-vel      "__xLiquidFillSV")
(def ^:private k-last-scroll     "__xLiquidFillLS")
(def ^:private k-last-scroll-t   "__xLiquidFillLST")
(def ^:private k-wave-amps       "__xLiquidFillWA")
(def ^:private k-wave-vels       "__xLiquidFillWV")
(def ^:private k-wave-phases     "__xLiquidFillWP")
(def ^:private k-width           "__xLiquidFillW")
(def ^:private k-height          "__xLiquidFillH")
(def ^:private k-handlers        "__xLiquidFillHdl")
(def ^:private k-io              "__xLiquidFillIO")
(def ^:private k-ro              "__xLiquidFillRO")
(def ^:private k-visible         "__xLiquidFillVis")
(def ^:private k-scroll-target   "__xLiquidFillST")
(def ^:private k-uid             "__xLiquidFillUid")
(def ^:private k-last-dispatch   "__xLiquidFillLD")

;; ── Forward declarations ────────────────────────────────────────────────────
(declare attach-scroll-listener!)
(declare detach-scroll-listener!)

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Unique ID counter ───────────────────────────────────────────────────────
(def ^:private uid-state #js {:n 0})
(defn- next-uid []
  (let [n (inc (.-n uid-state))]
    (set! (.-n uid-state) n)
    n))

;; ── Max wave layers ─────────────────────────────────────────────────────────
(def ^:private max-layers 5)

;; ── Settle threshold ────────────────────────────────────────────────────────
(def ^:private settle-threshold 0.05)

;; ── Min idle amplitude (keeps liquid alive) ─────────────────────────────────
(def ^:private idle-amp 2.0)

;; ── String constants ────────────────────────────────────────────────────────
(def ^:private default-viewbox "0 0 300 200")
(def ^:private id-prefix-grad  "xlf-grad-")
(def ^:private id-prefix-spec  "xlf-spec-")
(def ^:private class-wave-layer "wave-layer")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "overflow:hidden;"
   model/css-bg ":transparent;"
   model/css-border ":none;"
   model/css-radius ":0;"
   model/css-opacity ":1;"
   model/css-wave-speed ":1;"
   model/css-color-1 ":#B8860B;"
   model/css-color-2 ":#D4AF37;"
   model/css-color-3 ":#F9F295;"
   model/css-specular ":rgba(255,255,240,0.6);}"

   ":host{"
   "background:var(" model/css-bg ",transparent);"
   "border:var(" model/css-border ",none);"
   "border-radius:var(" model/css-radius ",0);}"

   ":host([mode=bar]){"
   model/css-bar-height ":14px;"
   "height:var(" model/css-bar-height ",14px);}"

   "[part=container]{"
   "position:relative;"
   "width:100%;height:100%;"
   "overflow:hidden;"
   "border-radius:inherit;}"

   "[part=svg]{"
   "display:block;"
   "width:100%;height:100%;"
   "position:absolute;inset:0;"
   "pointer-events:none;"
   "opacity:var(" model/css-opacity ",1);}"

   "[part=content]{"
   "position:relative;z-index:1;}"

   "." class-wave-layer "{"
   "transition:none;}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   model/css-bg ":transparent;"
   model/css-specular ":rgba(255,255,200,0.4);}}"

   "@media(prefers-reduced-motion:reduce){"
   "." class-wave-layer "{transition:none!important;}}"))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- find-scroll-ancestor
  "Walk up the DOM from el to find the nearest scrollable ancestor.
   Returns the element or nil (caller falls back to window)."
  [^js el]
  (loop [^js node (.-parentElement el)]
    (when node
      (let [^js cs (.getComputedStyle js/window node)
            ov-y (.getPropertyValue cs "overflow-y")
            ov-x (.getPropertyValue cs "overflow-x")]
        (if (or (= ov-y "auto") (= ov-y "scroll")
                (= ov-x "auto") (= ov-x "scroll"))
          node
          (recur (.-parentElement node)))))))

(defn- resolve-scroll-target
  "Resolve the scroll target element. Returns Element or nil (= window)."
  [^js el target-selector]
  (if (and (string? target-selector) (not= (.trim target-selector) ""))
    (.querySelector js/document target-selector)
    (find-scroll-ancestor el)))

(defn- get-scroll-info
  "Get scroll position and dimensions from a scroll target.
   target = Element or nil (window). orientation = \"vertical\" | \"horizontal\".
   Returns #js [scroll-pos scroll-size client-size]."
  [^js target orientation]
  (if (nil? target)
    ;; Window scroll
    (if (= orientation "horizontal")
      #js [(.-scrollX js/window)
           (.. js/document -documentElement -scrollWidth)
           (.-innerWidth js/window)]
      #js [(.-scrollY js/window)
           (.. js/document -documentElement -scrollHeight)
           (.-innerHeight js/window)])
    ;; Element scroll
    (if (= orientation "horizontal")
      #js [(.-scrollLeft target)
           (.-scrollWidth target)
           (.-clientWidth target)]
      #js [(.-scrollTop target)
           (.-scrollHeight target)
           (.-clientHeight target)])))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [uid     (next-uid)
        root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        container (.createElement js/document "div")
        svg     (.createElementNS js/document svg-ns "svg")
        defs    (.createElementNS js/document svg-ns "defs")
        ;; Gradient
        grad    (.createElementNS js/document svg-ns "radialGradient")
        stop1   (.createElementNS js/document svg-ns "stop")
        stop2   (.createElementNS js/document svg-ns "stop")
        stop3   (.createElementNS js/document svg-ns "stop")
        ;; Filter
        filt    (.createElementNS js/document svg-ns "filter")
        feBlur  (.createElementNS js/document svg-ns "feGaussianBlur")
        feSpec  (.createElementNS js/document svg-ns "feSpecularLighting")
        feLight (.createElementNS js/document svg-ns "fePointLight")
        feComp1 (.createElementNS js/document svg-ns "feComposite")
        feComp2 (.createElementNS js/document svg-ns "feComposite")
        ;; Wave paths (create max, hide unused)
        paths   (into-array
                 (for [_ (range max-layers)]
                   (.createElementNS js/document svg-ns "path")))
        ;; Content
        content (.createElement js/document "div")
        slot    (.createElement js/document "slot")
        grad-id (str id-prefix-grad uid)
        filt-id (str id-prefix-spec uid)]

    (set! (.-textContent style) style-text)

    (du/set-attr! container "part" "container")

    ;; SVG setup
    (du/set-attr! svg "part" "svg")
    (du/set-attr! svg "viewBox" default-viewbox)
    (du/set-attr! svg "preserveAspectRatio" "none")
    (du/set-attr! svg "aria-hidden" "true")

    ;; Gradient (radial from bottom-center — follows circular geometry)
    (du/set-attr! grad "id" grad-id)
    (du/set-attr! grad "gradientUnits" "userSpaceOnUse")
    (du/set-attr! grad "cx" "150") (du/set-attr! grad "cy" "200")
    (du/set-attr! grad "r" "200")
    (du/set-attr! grad "fx" "150") (du/set-attr! grad "fy" "200")
    (du/set-attr! stop1 "offset" "0%")
    (du/set-attr! stop1 "stop-color" (str "var(" model/css-color-1 ",#B8860B)"))
    (du/set-attr! stop2 "offset" "50%")
    (du/set-attr! stop2 "stop-color" (str "var(" model/css-color-2 ",#D4AF37)"))
    (du/set-attr! stop3 "offset" "100%")
    (du/set-attr! stop3 "stop-color" (str "var(" model/css-color-3 ",#F9F295)"))
    (.appendChild grad stop1)
    (.appendChild grad stop2)
    (.appendChild grad stop3)

    ;; Specular lighting filter
    (du/set-attr! filt "id" filt-id)
    (du/set-attr! filt "x" "-10%") (du/set-attr! filt "y" "-10%")
    (du/set-attr! filt "width" "120%") (du/set-attr! filt "height" "120%")
    (du/set-attr! feBlur "in" "SourceAlpha")
    (du/set-attr! feBlur "stdDeviation" "3")
    (du/set-attr! feBlur "result" "blur")
    (du/set-attr! feSpec "in" "blur")
    (du/set-attr! feSpec "surfaceScale" "5")
    (du/set-attr! feSpec "specularConstant" "0.75")
    (du/set-attr! feSpec "specularExponent" "20")
    (du/set-attr! feSpec "lighting-color"
                   (str "var(" model/css-specular ",rgba(255,255,240,0.6))"))
    (du/set-attr! feSpec "result" "specular")
    (.appendChild feSpec feLight)
    (du/set-attr! feLight "x" "90") (du/set-attr! feLight "y" "-50")
    (du/set-attr! feLight "z" "200")
    (du/set-attr! feComp1 "in" "specular")
    (du/set-attr! feComp1 "in2" "SourceAlpha")
    (du/set-attr! feComp1 "operator" "in")
    (du/set-attr! feComp1 "result" "specClip")
    (du/set-attr! feComp2 "in" "SourceGraphic")
    (du/set-attr! feComp2 "in2" "specClip")
    (du/set-attr! feComp2 "operator" "arithmetic")
    (du/set-attr! feComp2 "k1" "0") (du/set-attr! feComp2 "k2" "1")
    (du/set-attr! feComp2 "k3" "1") (du/set-attr! feComp2 "k4" "0")
    (.appendChild filt feBlur)
    (.appendChild filt feSpec)
    (.appendChild filt feComp1)
    (.appendChild filt feComp2)

    (.appendChild defs grad)
    (.appendChild defs filt)
    (.appendChild svg defs)

    ;; Wave paths
    (dotimes [i max-layers]
      (let [^js p (aget paths i)]
        (du/set-attr! p "class" class-wave-layer)
        (du/set-attr! p "data-layer" (str i))
        (du/set-attr! p "fill" (str "url(#" grad-id ")"))
        ;; Hide by default; active layers shown in render
        (set! (.. p -style -display) "none")
        (.appendChild svg p)))

    ;; Content
    (du/set-attr! content "part" "content")
    (.appendChild content slot)

    ;; Assemble
    (.appendChild container svg)
    (.appendChild container content)
    (.appendChild root style)
    (.appendChild root container)

    (du/setv! el k-uid uid)
    (du/setv! el k-refs
              {:root    root
               :svg     svg
               :paths   paths
               :content content
               :feLight feLight
               :grad    grad
               :filt-id filt-id
               :grad-id grad-id})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:target-raw           (du/get-attr el model/attr-target)
    :orientation-raw      (du/get-attr el model/attr-orientation)
    :mode-raw             (du/get-attr el model/attr-mode)
    :theme-raw            (du/get-attr el model/attr-theme)
    :wave-intensity-raw   (du/get-attr el model/attr-wave-intensity)
    :splash-intensity-raw (du/get-attr el model/attr-splash-intensity)
    :layers-raw           (du/get-attr el model/attr-layers)
    :disabled-attr        (du/get-attr el model/attr-disabled)}))

;; ── Wave state initialisation ───────────────────────────────────────────────
(defn- init-wave-state! [^js el n]
  (du/setv! el k-wave-amps   (js/Float64Array. n))
  (du/setv! el k-wave-vels   (js/Float64Array. n))
  (du/setv! el k-wave-phases (js/Float64Array. n)))

;; ── Path visibility ─────────────────────────────────────────────────────────
(defn- update-path-visibility! [^js el n-active]
  (let [{:keys [paths filt-id]} (du/getv el k-refs)]
    (dotimes [i max-layers]
      (let [^js p (aget paths i)]
        (if (< i n-active)
          (let [lp    (model/layer-params i n-active)
                front? (= i (dec n-active))]
            (set! (.. p -style -display) "")
            (du/set-attr! p "opacity" (str (:opacity lp)))
            (if front?
              (du/set-attr! p "filter" (str "url(#" filt-id ")"))
              (du/remove-attr! p "filter")))
          (set! (.. p -style -display) "none"))))))

;; ── Effective fill direction ─────────────────────────────────────────────────
(defn- vertical-fill? [m]
  (and (= "vertical" (:orientation m))
       (not= "bar" (:mode m))))

;; ── Render waves ────────────────────────────────────────────────────────────
(defn- render-waves! [^js el]
  (let [m       (du/getv el k-model)
        w       (du/getv el k-width)
        h       (du/getv el k-height)
        t       (du/getv el k-time)
        prog    (du/getv el k-progress)
        n       (:layers m)
        vert?   (vertical-fill? m)
        ^js wa  (du/getv el k-wave-amps)
        ^js wp  (du/getv el k-wave-phases)
        {:keys [paths]} (du/getv el k-refs)]
    (when (and w h (> w 0) (> h 0))
      (dotimes [i n]
        (let [^js p  (aget paths i)
              amp    (aget wa i)
              phase  (aget wp i)]
          ;; Hot path: rAF-driven; use untraced variants to keep the
          ;; trace recorder readable.
          (if vert?
            (let [fill-y (model/progress->fill-y prog h)
                  d      (model/wave-path-d fill-y w h t amp phase)]
              (du/set-attr-untraced! p "d" d))
            (let [fill-x (model/progress->fill-x prog w)
                  d      (model/horizontal-wave-path-d fill-x w h t amp phase)]
              (du/set-attr-untraced! p "d" d))))))))

;; ── Render static (disabled / reduced-motion) ──────────────────────────────
(defn- render-static! [^js el]
  (let [m     (du/getv el k-model)
        w     (du/getv el k-width)
        h     (du/getv el k-height)
        prog  (or (du/getv el k-progress) 0.0)
        n     (:layers m)
        vert? (vertical-fill? m)
        {:keys [paths]} (du/getv el k-refs)]
    (when (and w h (> w 0) (> h 0))
      (dotimes [i n]
        (let [^js p (aget paths i)]
          (if vert?
            (let [fill-y (model/progress->fill-y prog h)]
              (du/set-attr! p "d"
                             (str "M0," (.toFixed fill-y 2)
                                  "L" (.toFixed w 2) "," (.toFixed fill-y 2)
                                  "L" (.toFixed w 2) "," (.toFixed h 2)
                                  "L0," (.toFixed h 2) "Z")))
            (let [fill-x (model/progress->fill-x prog w)]
              (du/set-attr! p "d"
                             (str "M" (.toFixed fill-x 2) ",0"
                                  "L" (.toFixed fill-x 2) "," (.toFixed h 2)
                                  "L0," (.toFixed h 2)
                                  "L0,0Z")))))))))

;; ── Event dispatch ──────────────────────────────────────────────────────────
(defn- dispatch-progress! [^js el progress velocity]
  (du/dispatch! el model/event-progress (clj->js (model/progress-detail progress velocity))))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (cond
    (not (.-isConnected el))
    nil

    (or (:disabled? (du/getv el k-model)) (prefers-reduced-motion?))
    ;; Bail out without rescheduling — leaves k-raf cleared so a future
    ;; re-enable via start-animation! will kick the loop back on.
    (du/setv-untraced! el k-raf nil)

    :else
    (let [m           (du/getv el k-model)
          now         (js/performance.now)
          last-frame  (du/getv el k-last-frame)
          dt          (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          ;; Read CSS wave-speed multiplier
          wave-speed  (let [raw (.getPropertyValue
                                (.getComputedStyle js/window el)
                                model/css-wave-speed)
                            n (when (string? raw) (js/parseFloat (.trim raw)))]
                        (if (or (nil? n) (js/isNaN n) (<= n 0)) 1.0 n))
          n           (:layers m)
          wi          (:wave-intensity m)
          si          (:splash-intensity m)
          vel         (or (du/getv el k-scroll-vel) 0.0)
          ^js wa      (du/getv el k-wave-amps)
          ^js wv      (du/getv el k-wave-vels)
          ^js wp      (du/getv el k-wave-phases)]

      (du/setv-untraced! el k-last-frame now)

      ;; Accumulate time
      (du/setv-untraced! el k-time (+ (or (du/getv el k-time) 0.0) (* dt wave-speed)))

      ;; Smooth fill level toward target
      (let [cur-prog  (or (du/getv el k-progress) 0.0)
            tgt-prog  (or (du/getv el k-target-progress) 0.0)
            new-prog  (model/lerp cur-prog tgt-prog (* 5.0 dt))]
        (du/setv! el k-progress new-prog))

      ;; Update each wave layer's spring. 1-slot JS array avoids volatile!.
      (let [any-moving? #js [false]]
        (dotimes [i n]
          (let [lp          (model/layer-params i n)
                ;; Target amplitude: velocity-driven + idle
                target-amp  (+ (* vel wi si (:amp-mult lp) 30.0)
                               (* idle-amp wi (:amp-mult lp)))
                ;; Spring step
                result      (model/spring-step
                             (aget wa i) target-amp (aget wv i) dt
                             (:stiffness lp) (:damping lp))]
            (aset wa i (aget result 0))
            (aset wv i (aget result 1))
            ;; Accumulate phase
            (aset wp i (+ (aget wp i) (* dt wave-speed (:speed-mult lp))))
            ;; Check if still moving
            (when (> (js/Math.abs (aget wv i)) settle-threshold)
              (aset any-moving? 0 true))))

        ;; Decay scroll velocity
        (du/setv! el k-scroll-vel (* vel (js/Math.max 0.0 (- 1.0 (* 3.0 dt)))))

        ;; Render
        (render-waves! el)

        ;; Dispatch progress event (throttled)
        (let [prog      (du/getv el k-progress)
              last-disp (or (du/getv el k-last-dispatch) -1.0)]
          (when (> (js/Math.abs (- prog last-disp)) 0.005)
            (du/setv! el k-last-dispatch prog)
            (dispatch-progress! el prog vel)))

        ;; Continue or stop
        (let [prog-diff (js/Math.abs (- (or (du/getv el k-progress) 0.0)
                                        (or (du/getv el k-target-progress) 0.0)))]
          (if (or (aget any-moving? 0) (> vel 0.01) (> prog-diff 0.001))
            (du/setv-untraced! el k-raf
                      (js/requestAnimationFrame (fn [_] (animate! el))))
            ;; Settled
            (du/setv-untraced! el k-raf nil)))))))

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (du/setv-untraced! el k-last-frame (js/performance.now))
    (du/setv-untraced! el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv-untraced! el k-raf nil)))

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (du/getv el k-visible)
             (not (:disabled? (du/getv el k-model))))
    ;; Lazy re-resolve: target selector specified but not yet found
    (let [m-pre      (du/getv el k-model)
          cur-tgt    (du/getv el k-scroll-target)]
      (when (and (nil? cur-tgt) (some? (:target m-pre)))
        (when-let [resolved (resolve-scroll-target el (:target m-pre))]
          (detach-scroll-listener! el)
          (du/setv! el k-scroll-target resolved)
          (attach-scroll-listener! el))))
    (let [m           (du/getv el k-model)
          scroll-tgt  (du/getv el k-scroll-target)
          orientation (:orientation m)
          ^js info    (get-scroll-info scroll-tgt orientation)
          scroll-pos  (aget info 0)
          scroll-h    (aget info 1)
          client-h    (aget info 2)
          now         (js/performance.now)
          last-scroll (or (du/getv el k-last-scroll) scroll-pos)
          last-t      (or (du/getv el k-last-scroll-t) now)
          dt          (/ (- now last-t) 1000.0)
          progress    (model/compute-scroll-progress scroll-pos scroll-h client-h)
          velocity    (model/compute-scroll-velocity scroll-pos last-scroll dt)]

      (du/setv! el k-target-progress progress)
      (du/setv! el k-scroll-vel (js/Math.max (or (du/getv el k-scroll-vel) 0.0) velocity))
      (du/setv! el k-last-scroll scroll-pos)
      (du/setv! el k-last-scroll-t now)

      (when-not (or (:disabled? m) (prefers-reduced-motion?))
        (start-animation! el))

      ;; For disabled/reduced-motion, update immediately
      (when (or (:disabled? m) (prefers-reduced-motion?))
        (du/setv! el k-progress progress)
        (render-static! el)
        (dispatch-progress! el progress velocity)))))

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
        (let [{:keys [svg feLight grad]} (du/getv el k-refs)
              ^js svg svg
              ^js feLight feLight
              ^js grad grad
              r (js/Math.max w h)]
          (du/set-attr! svg "viewBox" (str "0 0 " w " " h))
          ;; Update specular light position
          (du/set-attr! feLight "x" (str (* w 0.3)))
          ;; Update radial gradient to match new dimensions
          (du/set-attr! grad "cx" (str (* w 0.5)))
          (du/set-attr! grad "cy" (str h))
          (du/set-attr! grad "r" (str r))
          (du/set-attr! grad "fx" (str (* w 0.5)))
          (du/set-attr! grad "fy" (str h)))
        ;; Re-render
        (let [m (du/getv el k-model)]
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-waves! el)
                (start-animation! el))))))))

;; ── IntersectionObserver ────────────────────────────────────────────────────
(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-visible (.-isIntersecting entry)]
    (du/setv! el k-visible is-visible)
    (if is-visible
      ;; Start tracking
      (let [m (du/getv el k-model)]
        (when-not (or (:disabled? m) (prefers-reduced-motion?))
          (start-animation! el)))
      ;; Stop when not visible
      (stop-animation! el))))

;; ── Scroll target management ────────────────────────────────────────────────
(defn- attach-scroll-listener! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when (and hs (not (gobj/get hs "scrollAttached")))
      (let [scroll-h  (gobj/get hs "scroll")
            scroll-tgt (du/getv el k-scroll-target)]
        (if scroll-tgt
          (.addEventListener scroll-tgt "scroll" scroll-h #js {:passive true})
          (.addEventListener js/window "scroll" scroll-h #js {:passive true}))
        (gobj/set hs "scrollAttached" true)))))

(defn- detach-scroll-listener! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when (and hs (gobj/get hs "scrollAttached"))
      (let [scroll-h  (gobj/get hs "scroll")
            scroll-tgt (du/getv el k-scroll-target)]
        (if scroll-tgt
          (.removeEventListener scroll-tgt "scroll" scroll-h)
          (.removeEventListener js/window "scroll" scroll-h))
        (gobj/set hs "scrollAttached" false)))))

(defn- retry-scroll-target! [^js el]
  (when (.-isConnected el)
    (let [m          (du/getv el k-model)
          target-sel (:target m)]
      (when (and (some? target-sel) (nil? (du/getv el k-scroll-target)))
        (when-let [resolved (resolve-scroll-target el target-sel)]
          (detach-scroll-listener! el)
          (du/setv! el k-scroll-target resolved)
          (attach-scroll-listener! el)
          (on-scroll el))))))

(defn- setup-scroll-target! [^js el]
  (let [m          (du/getv el k-model)
        target-sel (:target m)
        scroll-tgt (resolve-scroll-target el target-sel)]
    (du/setv! el k-scroll-target scroll-tgt)
    (attach-scroll-listener! el)
    (on-scroll el)
    ;; If target selector specified but not found, retry next frame
    ;; (target element may appear later in DOM parse order)
    (when (and (some? target-sel) (nil? scroll-tgt))
      (js/requestAnimationFrame (fn [_] (retry-scroll-target! el))))))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [scroll-h (fn [_e] (on-scroll el))]
    (du/setv! el k-handlers
              #js {"scroll"         scroll-h
                   "scrollAttached" false})))

(defn- remove-listeners! [^js el]
  (detach-scroll-listener! el)
  (du/setv! el k-handlers nil))

;; ── Observer setup/teardown ─────────────────────────────────────────────────
(defn- setup-observers! [^js el]
  ;; IntersectionObserver
  (let [io (js/IntersectionObserver.
            (fn [^js entries] (on-intersection el entries))
            #js {:threshold #js [0]})]
    (.observe io el)
    (du/setv! el k-io io))
  ;; ResizeObserver
  (let [ro (js/ResizeObserver.
            (fn [^js entries] (on-resize! el entries)))]
    (.observe ro el)
    (du/setv! el k-ro ro)))

(defn- teardown-observers! [^js el]
  (when-let [^js io (du/getv el k-io)]
    (.disconnect io)
    (du/setv! el k-io nil))
  (when-let [^js ro (du/getv el k-ro)]
    (.disconnect ro)
    (du/setv! el k-ro nil)))

;; ── Theme application ───────────────────────────────────────────────────────
(defn- apply-theme! [^js el theme]
  (if-let [colors (get model/theme-colors theme)]
    (let [^js s (.-style el)]
      (.setProperty s model/css-color-1  (:color-1 colors))
      (.setProperty s model/css-color-2  (:color-2 colors))
      (.setProperty s model/css-color-3  (:color-3 colors))
      (.setProperty s model/css-specular (:specular colors)))
    ;; "custom" theme: remove overrides so user CSS custom properties take effect
    (let [^js s (.-style el)]
      (.removeProperty s model/css-color-1)
      (.removeProperty s model/css-color-2)
      (.removeProperty s model/css-color-3)
      (.removeProperty s model/css-specular))))

;; ── Apply model + update-from-attrs! (render-pipeline) ─────────────────────
;; Note: k-model is written early here because several effect helpers
;; (render-waves!, render-static!, animate!, on-scroll) read the cached model
;; directly. Per-field diffs against the captured old-m gate the side effects
;; that should only re-run when their relevant fields changed.
(defn- apply-model! [^js el new-m old-m]
  ;; Re-init wave state if layers changed
  (when (or (nil? old-m) (not= (:layers new-m) (:layers old-m)))
    (init-wave-state! el (:layers new-m))
    (update-path-visibility! el (:layers new-m)))
  ;; Apply theme colors when theme changes
  (when (or (nil? old-m) (not= (:theme new-m) (:theme old-m)))
    (apply-theme! el (:theme new-m)))
  ;; Re-resolve scroll target if target attribute changed
  (when (or (nil? old-m) (not= (:target new-m) (:target old-m)))
    (detach-scroll-listener! el)
    (setup-scroll-target! el))
  ;; Re-render
  (let [w (du/getv el k-width)
        h (du/getv el k-height)]
    (when (and w h (> w 0) (> h 0))
      (if (or (:disabled? new-m) (prefers-reduced-motion?))
        (do (stop-animation! el)
            (render-static! el))
        (do (render-waves! el)
            (start-animation! el))))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (du/setv! el k-model new-m)
      (apply-model! el new-m old-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-target      model/attr-target)
  (du/define-parsed-prop! proto model/attr-orientation model/attr-orientation     model/parse-orientation)
  (du/define-parsed-prop! proto model/attr-mode        model/attr-mode            model/parse-mode)
  (du/define-parsed-prop! proto model/attr-theme       model/attr-theme           model/parse-theme)
  (du/define-parsed-prop! proto "waveIntensity"        model/attr-wave-intensity  model/parse-wave-intensity)
  (du/define-parsed-prop! proto "splashIntensity"      model/attr-splash-intensity model/parse-splash-intensity)
  (du/define-parsed-prop! proto model/attr-layers      model/attr-layers          model/parse-layers)
  (du/define-bool-prop!   proto model/attr-disabled    model/attr-disabled)

  ;; progress is read-only — no equivalent helper for a getter-only descriptor.
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/getv this k-progress) 0.0)))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-progress 0.0)
    (du/setv! el k-target-progress 0.0)
    (du/setv! el k-scroll-vel 0.0)
    (du/setv-untraced! el k-time 0.0)
    (du/setv! el k-last-dispatch -1.0)
    (du/setv! el k-visible true)
    (ensure-refs! el)
    ;; Apply theme and init wave state
    (apply-theme! el (:theme m))
    (init-wave-state! el (:layers m))
    (update-path-visibility! el (:layers m))
    ;; Listeners and observers
    (remove-listeners! el)
    (add-listeners! el)
    (setup-observers! el)
    (setup-scroll-target! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el)
  (teardown-observers! el)
  (du/setv! el k-visible false))

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
