(ns baredom.components.x-liquid-fill.x-liquid-fill
  (:require
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

    (.setAttribute container "part" "container")

    ;; SVG setup
    (.setAttribute svg "part" "svg")
    (.setAttribute svg "viewBox" default-viewbox)
    (.setAttribute svg "preserveAspectRatio" "none")
    (.setAttribute svg "aria-hidden" "true")

    ;; Gradient (radial from bottom-center — follows circular geometry)
    (.setAttribute grad "id" grad-id)
    (.setAttribute grad "gradientUnits" "userSpaceOnUse")
    (.setAttribute grad "cx" "150") (.setAttribute grad "cy" "200")
    (.setAttribute grad "r" "200")
    (.setAttribute grad "fx" "150") (.setAttribute grad "fy" "200")
    (.setAttribute stop1 "offset" "0%")
    (.setAttribute stop1 "stop-color" (str "var(" model/css-color-1 ",#B8860B)"))
    (.setAttribute stop2 "offset" "50%")
    (.setAttribute stop2 "stop-color" (str "var(" model/css-color-2 ",#D4AF37)"))
    (.setAttribute stop3 "offset" "100%")
    (.setAttribute stop3 "stop-color" (str "var(" model/css-color-3 ",#F9F295)"))
    (.appendChild grad stop1)
    (.appendChild grad stop2)
    (.appendChild grad stop3)

    ;; Specular lighting filter
    (.setAttribute filt "id" filt-id)
    (.setAttribute filt "x" "-10%") (.setAttribute filt "y" "-10%")
    (.setAttribute filt "width" "120%") (.setAttribute filt "height" "120%")
    (.setAttribute feBlur "in" "SourceAlpha")
    (.setAttribute feBlur "stdDeviation" "3")
    (.setAttribute feBlur "result" "blur")
    (.setAttribute feSpec "in" "blur")
    (.setAttribute feSpec "surfaceScale" "5")
    (.setAttribute feSpec "specularConstant" "0.75")
    (.setAttribute feSpec "specularExponent" "20")
    (.setAttribute feSpec "lighting-color"
                   (str "var(" model/css-specular ",rgba(255,255,240,0.6))"))
    (.setAttribute feSpec "result" "specular")
    (.appendChild feSpec feLight)
    (.setAttribute feLight "x" "90") (.setAttribute feLight "y" "-50")
    (.setAttribute feLight "z" "200")
    (.setAttribute feComp1 "in" "specular")
    (.setAttribute feComp1 "in2" "SourceAlpha")
    (.setAttribute feComp1 "operator" "in")
    (.setAttribute feComp1 "result" "specClip")
    (.setAttribute feComp2 "in" "SourceGraphic")
    (.setAttribute feComp2 "in2" "specClip")
    (.setAttribute feComp2 "operator" "arithmetic")
    (.setAttribute feComp2 "k1" "0") (.setAttribute feComp2 "k2" "1")
    (.setAttribute feComp2 "k3" "1") (.setAttribute feComp2 "k4" "0")
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
        (.setAttribute p "class" class-wave-layer)
        (.setAttribute p "data-layer" (str i))
        (.setAttribute p "fill" (str "url(#" grad-id ")"))
        ;; Hide by default; active layers shown in render
        (set! (.. p -style -display) "none")
        (.appendChild svg p)))

    ;; Content
    (.setAttribute content "part" "content")
    (.appendChild content slot)

    ;; Assemble
    (.appendChild container svg)
    (.appendChild container content)
    (.appendChild root style)
    (.appendChild root container)

    (gobj/set el k-uid uid)
    (gobj/set el k-refs
              {:root    root
               :svg     svg
               :paths   paths
               :content content
               :feLight feLight
               :grad    grad
               :filt-id filt-id
               :grad-id grad-id}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs)))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:target-raw           (.getAttribute el model/attr-target)
    :orientation-raw      (.getAttribute el model/attr-orientation)
    :mode-raw             (.getAttribute el model/attr-mode)
    :theme-raw            (.getAttribute el model/attr-theme)
    :wave-intensity-raw   (.getAttribute el model/attr-wave-intensity)
    :splash-intensity-raw (.getAttribute el model/attr-splash-intensity)
    :layers-raw           (.getAttribute el model/attr-layers)
    :disabled-attr        (.getAttribute el model/attr-disabled)}))

;; ── Wave state initialisation ───────────────────────────────────────────────
(defn- init-wave-state! [^js el n]
  (gobj/set el k-wave-amps   (js/Float64Array. n))
  (gobj/set el k-wave-vels   (js/Float64Array. n))
  (gobj/set el k-wave-phases (js/Float64Array. n))
  nil)

;; ── Path visibility ─────────────────────────────────────────────────────────
(defn- update-path-visibility! [^js el n-active]
  (let [{:keys [paths filt-id]} (gobj/get el k-refs)]
    (dotimes [i max-layers]
      (let [^js p (aget paths i)]
        (if (< i n-active)
          (let [lp    (model/layer-params i n-active)
                front? (= i (dec n-active))]
            (set! (.. p -style -display) "")
            (.setAttribute p "opacity" (str (:opacity lp)))
            (if front?
              (.setAttribute p "filter" (str "url(#" filt-id ")"))
              (.removeAttribute p "filter")))
          (set! (.. p -style -display) "none")))))
  nil)

;; ── Effective fill direction ─────────────────────────────────────────────────
(defn- vertical-fill? [m]
  (and (= "vertical" (:orientation m))
       (not= "bar" (:mode m))))

;; ── Render waves ────────────────────────────────────────────────────────────
(defn- render-waves! [^js el]
  (let [m       (gobj/get el k-model)
        w       (gobj/get el k-width)
        h       (gobj/get el k-height)
        t       (gobj/get el k-time)
        prog    (gobj/get el k-progress)
        n       (:layers m)
        vert?   (vertical-fill? m)
        ^js wa  (gobj/get el k-wave-amps)
        ^js wp  (gobj/get el k-wave-phases)
        {:keys [paths]} (gobj/get el k-refs)]
    (when (and w h (> w 0) (> h 0))
      (dotimes [i n]
        (let [^js p  (aget paths i)
              amp    (aget wa i)
              phase  (aget wp i)]
          (if vert?
            (let [fill-y (model/progress->fill-y prog h)
                  d      (model/wave-path-d fill-y w h t amp phase)]
              (.setAttribute p "d" d))
            (let [fill-x (model/progress->fill-x prog w)
                  d      (model/horizontal-wave-path-d fill-x w h t amp phase)]
              (.setAttribute p "d" d)))))))
  nil)

;; ── Render static (disabled / reduced-motion) ──────────────────────────────
(defn- render-static! [^js el]
  (let [m     (gobj/get el k-model)
        w     (gobj/get el k-width)
        h     (gobj/get el k-height)
        prog  (or (gobj/get el k-progress) 0.0)
        n     (:layers m)
        vert? (vertical-fill? m)
        {:keys [paths]} (gobj/get el k-refs)]
    (when (and w h (> w 0) (> h 0))
      (dotimes [i n]
        (let [^js p (aget paths i)]
          (if vert?
            (let [fill-y (model/progress->fill-y prog h)]
              (.setAttribute p "d"
                             (str "M0," (.toFixed fill-y 2)
                                  "L" (.toFixed w 2) "," (.toFixed fill-y 2)
                                  "L" (.toFixed w 2) "," (.toFixed h 2)
                                  "L0," (.toFixed h 2) "Z")))
            (let [fill-x (model/progress->fill-x prog w)]
              (.setAttribute p "d"
                             (str "M" (.toFixed fill-x 2) ",0"
                                  "L" (.toFixed fill-x 2) "," (.toFixed h 2)
                                  "L0," (.toFixed h 2)
                                  "L0,0Z"))))))))
  nil)

;; ── Event dispatch ──────────────────────────────────────────────────────────
(defn- dispatch-progress! [^js el progress velocity]
  (let [^js ev (js/CustomEvent.
                model/event-progress
                #js {:detail   (clj->js (model/progress-detail progress velocity))
                     :bubbles  true
                     :composed true
                     :cancelable false})]
    (.dispatchEvent el ev))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (cond
    (not (.-isConnected el))
    nil

    (or (:disabled? (gobj/get el k-model)) (prefers-reduced-motion?))
    ;; Bail out without rescheduling — leaves k-raf cleared so a future
    ;; re-enable via start-animation! will kick the loop back on.
    (gobj/set el k-raf nil)

    :else
    (let [m           (gobj/get el k-model)
          now         (js/performance.now)
          last-frame  (gobj/get el k-last-frame)
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
          vel         (or (gobj/get el k-scroll-vel) 0.0)
          ^js wa      (gobj/get el k-wave-amps)
          ^js wv      (gobj/get el k-wave-vels)
          ^js wp      (gobj/get el k-wave-phases)]

      (gobj/set el k-last-frame now)

      ;; Accumulate time
      (gobj/set el k-time (+ (or (gobj/get el k-time) 0.0) (* dt wave-speed)))

      ;; Smooth fill level toward target
      (let [cur-prog  (or (gobj/get el k-progress) 0.0)
            tgt-prog  (or (gobj/get el k-target-progress) 0.0)
            new-prog  (model/lerp cur-prog tgt-prog (* 5.0 dt))]
        (gobj/set el k-progress new-prog))

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
        (gobj/set el k-scroll-vel (* vel (js/Math.max 0.0 (- 1.0 (* 3.0 dt)))))

        ;; Render
        (render-waves! el)

        ;; Dispatch progress event (throttled)
        (let [prog      (gobj/get el k-progress)
              last-disp (or (gobj/get el k-last-dispatch) -1.0)]
          (when (> (js/Math.abs (- prog last-disp)) 0.005)
            (gobj/set el k-last-dispatch prog)
            (dispatch-progress! el prog vel)))

        ;; Continue or stop
        (let [prog-diff (js/Math.abs (- (or (gobj/get el k-progress) 0.0)
                                        (or (gobj/get el k-target-progress) 0.0)))]
          (if (or (aget any-moving? 0) (> vel 0.01) (> prog-diff 0.001))
            (gobj/set el k-raf
                      (js/requestAnimationFrame (fn [_] (animate! el))))
            ;; Settled
            (gobj/set el k-raf nil))))))
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

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (gobj/get el k-visible)
             (not (:disabled? (gobj/get el k-model))))
    ;; Lazy re-resolve: target selector specified but not yet found
    (let [m-pre      (gobj/get el k-model)
          cur-tgt    (gobj/get el k-scroll-target)]
      (when (and (nil? cur-tgt) (some? (:target m-pre)))
        (when-let [resolved (resolve-scroll-target el (:target m-pre))]
          (detach-scroll-listener! el)
          (gobj/set el k-scroll-target resolved)
          (attach-scroll-listener! el))))
    (let [m           (gobj/get el k-model)
          scroll-tgt  (gobj/get el k-scroll-target)
          orientation (:orientation m)
          ^js info    (get-scroll-info scroll-tgt orientation)
          scroll-pos  (aget info 0)
          scroll-h    (aget info 1)
          client-h    (aget info 2)
          now         (js/performance.now)
          last-scroll (or (gobj/get el k-last-scroll) scroll-pos)
          last-t      (or (gobj/get el k-last-scroll-t) now)
          dt          (/ (- now last-t) 1000.0)
          progress    (model/compute-scroll-progress scroll-pos scroll-h client-h)
          velocity    (model/compute-scroll-velocity scroll-pos last-scroll dt)]

      (gobj/set el k-target-progress progress)
      (gobj/set el k-scroll-vel (js/Math.max (or (gobj/get el k-scroll-vel) 0.0) velocity))
      (gobj/set el k-last-scroll scroll-pos)
      (gobj/set el k-last-scroll-t now)

      (when-not (or (:disabled? m) (prefers-reduced-motion?))
        (start-animation! el))

      ;; For disabled/reduced-motion, update immediately
      (when (or (:disabled? m) (prefers-reduced-motion?))
        (gobj/set el k-progress progress)
        (render-static! el)
        (dispatch-progress! el progress velocity))))
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
        (let [{:keys [svg feLight grad]} (gobj/get el k-refs)
              ^js svg svg
              ^js feLight feLight
              ^js grad grad
              r (js/Math.max w h)]
          (.setAttribute svg "viewBox" (str "0 0 " w " " h))
          ;; Update specular light position
          (.setAttribute feLight "x" (str (* w 0.3)))
          ;; Update radial gradient to match new dimensions
          (.setAttribute grad "cx" (str (* w 0.5)))
          (.setAttribute grad "cy" (str h))
          (.setAttribute grad "r" (str r))
          (.setAttribute grad "fx" (str (* w 0.5)))
          (.setAttribute grad "fy" (str h)))
        ;; Re-render
        (let [m (gobj/get el k-model)]
          (if (or (:disabled? m) (prefers-reduced-motion?))
            (render-static! el)
            (do (render-waves! el)
                (start-animation! el)))))))
  nil)

;; ── IntersectionObserver ────────────────────────────────────────────────────
(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-visible (.-isIntersecting entry)]
    (gobj/set el k-visible is-visible)
    (if is-visible
      ;; Start tracking
      (let [m (gobj/get el k-model)]
        (when-not (or (:disabled? m) (prefers-reduced-motion?))
          (start-animation! el)))
      ;; Stop when not visible
      (stop-animation! el)))
  nil)

;; ── Scroll target management ────────────────────────────────────────────────
(defn- attach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (not (gobj/get hs "scrollAttached")))
      (let [scroll-h  (gobj/get hs "scroll")
            scroll-tgt (gobj/get el k-scroll-target)]
        (if scroll-tgt
          (.addEventListener scroll-tgt "scroll" scroll-h #js {:passive true})
          (.addEventListener js/window "scroll" scroll-h #js {:passive true}))
        (gobj/set hs "scrollAttached" true)))))

(defn- detach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (gobj/get hs "scrollAttached"))
      (let [scroll-h  (gobj/get hs "scroll")
            scroll-tgt (gobj/get el k-scroll-target)]
        (if scroll-tgt
          (.removeEventListener scroll-tgt "scroll" scroll-h)
          (.removeEventListener js/window "scroll" scroll-h))
        (gobj/set hs "scrollAttached" false)))))

(defn- retry-scroll-target! [^js el]
  (when (.-isConnected el)
    (let [m          (gobj/get el k-model)
          target-sel (:target m)]
      (when (and (some? target-sel) (nil? (gobj/get el k-scroll-target)))
        (when-let [resolved (resolve-scroll-target el target-sel)]
          (detach-scroll-listener! el)
          (gobj/set el k-scroll-target resolved)
          (attach-scroll-listener! el)
          (on-scroll el)))))
  nil)

(defn- setup-scroll-target! [^js el]
  (let [m          (gobj/get el k-model)
        target-sel (:target m)
        scroll-tgt (resolve-scroll-target el target-sel)]
    (gobj/set el k-scroll-target scroll-tgt)
    (attach-scroll-listener! el)
    (on-scroll el)
    ;; If target selector specified but not found, retry next frame
    ;; (target element may appear later in DOM parse order)
    (when (and (some? target-sel) (nil? scroll-tgt))
      (js/requestAnimationFrame (fn [_] (retry-scroll-target! el)))))
  nil)

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [scroll-h (fn [_e] (on-scroll el))]
    (gobj/set el k-handlers
              #js {"scroll"         scroll-h
                   "scrollAttached" false}))
  nil)

(defn- remove-listeners! [^js el]
  (detach-scroll-listener! el)
  (gobj/set el k-handlers nil)
  nil)

;; ── Observer setup/teardown ─────────────────────────────────────────────────
(defn- setup-observers! [^js el]
  ;; IntersectionObserver
  (let [io (js/IntersectionObserver.
            (fn [^js entries] (on-intersection el entries))
            #js {:threshold #js [0]})]
    (.observe io el)
    (gobj/set el k-io io))
  ;; ResizeObserver
  (let [ro (js/ResizeObserver.
            (fn [^js entries] (on-resize! el entries)))]
    (.observe ro el)
    (gobj/set el k-ro ro))
  nil)

(defn- teardown-observers! [^js el]
  (when-let [^js io (gobj/get el k-io)]
    (.disconnect io)
    (gobj/set el k-io nil))
  (when-let [^js ro (gobj/get el k-ro)]
    (.disconnect ro)
    (gobj/set el k-ro nil))
  nil)

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
      (.removeProperty s model/css-specular)))
  nil)

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (gobj/set el k-model new-m)
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
    (let [w (gobj/get el k-width)
          h (gobj/get el k-height)]
      (when (and w h (> w 0) (> h 0))
        (if (or (:disabled? new-m) (prefers-reduced-motion?))
          (do (stop-animation! el)
              (render-static! el))
          (do (render-waves! el)
              (start-animation! el))))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; target (string)
  (.defineProperty js/Object proto model/attr-target
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-target)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-target (str v))
                                          (.removeAttribute this model/attr-target))))
                        :enumerable true :configurable true})

  ;; orientation (string)
  (.defineProperty js/Object proto model/attr-orientation
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-orientation
                                         (.getAttribute this model/attr-orientation))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-orientation (str v))
                                          (.removeAttribute this model/attr-orientation))))
                        :enumerable true :configurable true})

  ;; mode (string)
  (.defineProperty js/Object proto model/attr-mode
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-mode
                                         (.getAttribute this model/attr-mode))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-mode (str v))
                                          (.removeAttribute this model/attr-mode))))
                        :enumerable true :configurable true})

  ;; theme (string)
  (.defineProperty js/Object proto model/attr-theme
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-theme
                                         (.getAttribute this model/attr-theme))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-theme (str v))
                                          (.removeAttribute this model/attr-theme))))
                        :enumerable true :configurable true})

  ;; waveIntensity (number, camelCase)
  (.defineProperty js/Object proto "waveIntensity"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-wave-intensity
                                         (.getAttribute this model/attr-wave-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-wave-intensity)
                                          (.setAttribute this model/attr-wave-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; splashIntensity (number, camelCase)
  (.defineProperty js/Object proto "splashIntensity"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-splash-intensity
                                         (.getAttribute this model/attr-splash-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-splash-intensity)
                                          (.setAttribute this model/attr-splash-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; layers (number)
  (.defineProperty js/Object proto model/attr-layers
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-layers
                                         (.getAttribute this model/attr-layers))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-layers)
                                          (.setAttribute this model/attr-layers (str v)))))
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
                        :enumerable true :configurable true})

  ;; progress (read-only)
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-progress) 0.0)))
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
                       (gobj/set this k-progress 0.0)
                       (gobj/set this k-target-progress 0.0)
                       (gobj/set this k-scroll-vel 0.0)
                       (gobj/set this k-time 0.0)
                       (gobj/set this k-last-dispatch -1.0)
                       (gobj/set this k-visible true)
                       (ensure-refs! this)
                       ;; Apply theme and init wave state
                       (apply-theme! this (:theme m))
                       (init-wave-state! this (:layers m))
                       (update-path-visibility! this (:layers m))
                       ;; Listeners and observers
                       (remove-listeners! this)
                       (add-listeners! this)
                       (setup-observers! this)
                       (setup-scroll-target! this))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (stop-animation! this)
                     (remove-listeners! this)
                     (teardown-observers! this)
                     (gobj/set this k-visible false)
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
