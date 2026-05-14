(ns baredom.components.x-neural-glow.x-neural-glow
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-neural-glow.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs        "__xNeuralRefs")
(def ^:private k-raf         "__xNeuralRaf")
(def ^:private k-model       "__xNeuralModel")
(def ^:private k-time        "__xNeuralTime")
(def ^:private k-last-frame  "__xNeuralLastFrame")
(def ^:private k-activity    "__xNeuralActivity")
(def ^:private k-impulse     "__xNeuralImpulse")
(def ^:private k-handlers    "__xNeuralHandlers")
(def ^:private k-uniforms    "__xNeuralUniforms")
(def ^:private k-program     "__xNeuralProgram")
(def ^:private k-orb-pos     "__xNeuralOrbPos")
(def ^:private k-orb-base    "__xNeuralOrbBase")
(def ^:private k-orb-phases  "__xNeuralOrbPhases")
(def ^:private k-drift-time  "__xNeuralDriftTime")
(def ^:private k-last-ptr    "__xNeuralLastPtr")
(def ^:private k-last-scroll "__xNeuralLastScroll")
(def ^:private k-color-cache "__xNeuralColorCache")

;; ── Style ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "width:100%;"
   "height:100%;"
   "min-height:200px;"
   "overflow:hidden;"
   "z-index:var(" model/css-z-index ",0);"
   "inset:var(" model/css-inset ",auto);}"

   "canvas{"
   "display:block;"
   "width:100%;"
   "height:100%;"
   "opacity:var(" model/css-opacity ",0.8);"
   "mix-blend-mode:var(" model/css-blend-mode ",normal);}"

   "@media(prefers-reduced-motion:reduce){"
   "canvas{opacity:var(" model/css-opacity ",0.6);}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── CSS color → RGB float array ─────────────────────────────────────────────
(def ^:private color-canvas-ctx
  (let [^js c (.createElement js/document "canvas")]
    (set! (.-width c) 1)
    (set! (.-height c) 1)
    (.getContext c "2d")))

(defn- css-color-to-rgb
  "Converts any CSS color string to [r g b] floats in [0, 1].
   Uses an offscreen canvas for browser-native parsing."
  [color-str]
  (let [^js ctx color-canvas-ctx]
    (set! (.-fillStyle ctx) "#000000")
    (set! (.-fillStyle ctx) color-str)
    (let [style (.-fillStyle ctx)]
      (if (and (string? style) (= (aget style 0) "#"))
        ;; Hex format
        (let [hex (.substring style 1)
              r (js/parseInt (.substring hex 0 2) 16)
              g (js/parseInt (.substring hex 2 4) 16)
              b (js/parseInt (.substring hex 4 6) 16)]
          #js [(/ r 255.0) (/ g 255.0) (/ b 255.0)])
        ;; Try rgb(...) format
        (if-let [m (.match style #"rgba?\((\d+),\s*(\d+),\s*(\d+)")]
          (let [r (js/parseInt (aget m 1) 10)
                g (js/parseInt (aget m 2) 10)
                b (js/parseInt (aget m 3) 10)]
            #js [(/ r 255.0) (/ g 255.0) (/ b 255.0)])
          #js [0.0 0.0 0.0])))))

(defn- get-color-rgb
  "Get RGB for a color string, using a cache on the element instance."
  [^js el color-str]
  (let [^js cache (du/getv el k-color-cache)]
    (if-let [cached (gobj/get cache color-str)]
      cached
      (let [rgb (css-color-to-rgb color-str)]
        (gobj/set cache color-str rgb)
        rgb))))

;; ── WebGL helpers ───────────────────────────────────────────────────────────
(defn- compile-shader [^js gl type source]
  (let [shader (.createShader gl type)]
    (.shaderSource gl shader source)
    (.compileShader gl shader)
    (if (.getShaderParameter gl shader (.-COMPILE_STATUS gl))
      shader
      (do (js/console.warn "x-neural-glow shader error:" (.getShaderInfoLog gl shader))
          (.deleteShader gl shader)))))

(defn- create-program [^js gl ^js vs ^js fs]
  (let [program (.createProgram gl)]
    (.attachShader gl program vs)
    (.attachShader gl program fs)
    (.linkProgram gl program)
    (if (.getProgramParameter gl program (.-LINK_STATUS gl))
      program
      (do (js/console.warn "x-neural-glow link error:" (.getProgramInfoLog gl program))
          (.deleteProgram gl program)))))

(defn- init-webgl!
  "Initialise WebGL: compile shaders, create program, set up quad buffer,
   cache uniform locations. Returns the GL context on success, nil on failure."
  [^js el ^js canvas]
  (let [^js gl (or (.getContext canvas "webgl" #js {:alpha true :premultipliedAlpha false})
                    (.getContext canvas "experimental-webgl" #js {:alpha true :premultipliedAlpha false}))]
    (when gl
      (let [^js vs (compile-shader gl (.-VERTEX_SHADER gl) model/vertex-shader-source)
            ^js fs (compile-shader gl (.-FRAGMENT_SHADER gl) model/fragment-shader-source)]
        (when (and vs fs)
          (let [^js program (create-program gl vs fs)]
            (when program
              (.useProgram gl program)

              ;; Full-screen quad
              (let [^js buffer (.createBuffer gl)
                    positions  #js [-1 -1  1 -1  -1 1  1 1]]
                (.bindBuffer gl (.-ARRAY_BUFFER gl) buffer)
                (.bufferData gl (.-ARRAY_BUFFER gl)
                             (js/Float32Array. positions)
                             (.-STATIC_DRAW gl))
                (let [a-pos (.getAttribLocation gl program "a_position")]
                  (.enableVertexAttribArray gl a-pos)
                  (.vertexAttribPointer gl a-pos 2 (.-FLOAT gl) false 0 0)))

              ;; Cache uniform locations
              (let [uniforms #js {}]
                (doseq [name ["u_resolution" "u_time" "u_activity" "u_pulse_speed"
                              "u_rest_rate" "u_orb_count" "u_orb_size"
                              "u_connection_dist" "u_color_primary" "u_color_secondary"
                              "u_color_background" "u_opacity"]]
                  (gobj/set uniforms name (.getUniformLocation gl program name)))
                ;; Array uniforms
                (dotimes [i 50]
                  (let [pos-name (str "u_orb_positions[" i "]")
                        phase-name (str "u_orb_phases[" i "]")]
                    (gobj/set uniforms pos-name (.getUniformLocation gl program pos-name))
                    (gobj/set uniforms phase-name (.getUniformLocation gl program phase-name))))
                (du/setv! el k-uniforms uniforms))

              (du/setv! el k-program program)

              ;; Enable blending for alpha
              (.enable gl (.-BLEND gl))
              (.blendFunc gl (.-SRC_ALPHA gl) (.-ONE_MINUS_SRC_ALPHA gl))

              gl)))))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root   (.attachShadow el #js {:mode "open"})
        style  (.createElement js/document "style")
        canvas (.createElement js/document "canvas")]
    (set! (.-textContent style) style-text)
    (du/set-attr! canvas "part" "canvas")
    (.appendChild root style)
    (.appendChild root canvas)
    (let [^js gl (init-webgl! el canvas)]
      (du/setv! el k-refs {:root root :canvas canvas :gl gl}))))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:orb-count-raw           (du/get-attr el model/attr-orb-count)
    :color-primary-raw       (du/get-attr el model/attr-color-primary)
    :color-secondary-raw     (du/get-attr el model/attr-color-secondary)
    :color-background-raw    (du/get-attr el model/attr-color-background)
    :pulse-speed-raw         (du/get-attr el model/attr-pulse-speed)
    :rest-rate-raw           (du/get-attr el model/attr-rest-rate)
    :connection-distance-raw (du/get-attr el model/attr-connection-distance)
    :orb-size-raw            (du/get-attr el model/attr-orb-size)
    :opacity-raw             (du/get-attr el model/attr-opacity)
    :interactive-raw         (du/get-attr el model/attr-interactive)}))

;; ── Orb management ──────────────────────────────────────────────────────────
(defn- init-orbs! [^js el n]
  (let [base   (model/init-orb-positions n)
        phases (model/init-orb-phases n)
        pos    #js []]
    ;; Copy base positions as current positions
    (dotimes [i n]
      (let [bp (aget base i)]
        (.push pos #js [(aget bp 0) (aget bp 1)])))
    (du/setv! el k-orb-base base)
    (du/setv! el k-orb-phases phases)
    (du/setv! el k-orb-pos pos)))

(defn- reconcile-orbs!
  "Adjust orb arrays when orb-count changes."
  [^js el new-count]
  (let [^js base   (du/getv el k-orb-base)
        ^js phases (du/getv el k-orb-phases)
        ^js pos    (du/getv el k-orb-pos)
        current    (.-length base)
        two-pi     (* 2.0 js/Math.PI)]
    (when (not= current new-count)
      (if (> new-count current)
        ;; Add new orbs
        (dotimes [_ (- new-count current)]
          (let [x (js/Math.random)
                y (js/Math.random)]
            (.push base #js [x y])
            (.push pos #js [x y])
            (.push phases (* (js/Math.random) two-pi))))
        ;; Remove excess orbs
        (do
          (set! (.-length base) new-count)
          (set! (.-length pos) new-count)
          (set! (.-length phases) new-count))))))

;; ── Pseudo-noise for orb drift (sum-of-sines) ──────────────────────────────
(defn- pseudo-noise
  "Organic-looking noise from sum of sines. Returns value in ~[-1, 1]."
  [x y t]
  (+ (* (js/Math.sin (+ (* x 1.7) (* t 0.3)))
        (js/Math.cos (+ (* y 2.3) (* t 0.5)))
        0.5)
     (* (js/Math.sin (- (* x 3.1) (* t 0.7)))
        (js/Math.cos (+ (* y 1.3) (* t 0.2)))
        0.25)
     (* (js/Math.sin (+ (* x 5.3) (* t 0.4)))
        (js/Math.cos (- (* y 4.7) (* t 0.6)))
        0.125)))

;; ── Update uniforms ─────────────────────────────────────────────────────────
(defn- update-uniforms!
  [^js el ^js gl m]
  (let [^js uniforms (du/getv el k-uniforms)
        ^js canvas   (:canvas (du/getv el k-refs))
        w            (.-width canvas)
        h            (.-height canvas)
        time         (du/getv el k-time)
        activity     (du/getv el k-activity)
        ^js pos      (du/getv el k-orb-pos)
        ^js phases   (du/getv el k-orb-phases)
        n            (:orb-count m)
        rgb-primary  (get-color-rgb el (:color-primary m))
        rgb-secondary (get-color-rgb el (:color-secondary m))
        rgb-bg       (get-color-rgb el (:color-background m))]

    (.uniform2f gl (gobj/get uniforms "u_resolution") w h)
    (.uniform1f gl (gobj/get uniforms "u_time") time)
    (.uniform1f gl (gobj/get uniforms "u_activity") activity)
    (.uniform1f gl (gobj/get uniforms "u_pulse_speed") (:pulse-speed m))
    (.uniform1f gl (gobj/get uniforms "u_rest_rate") (:rest-rate m))
    (.uniform1i gl (gobj/get uniforms "u_orb_count") n)
    (.uniform1f gl (gobj/get uniforms "u_orb_size") (:orb-size m))
    (.uniform1f gl (gobj/get uniforms "u_connection_dist") (:connection-distance m))
    (.uniform3f gl (gobj/get uniforms "u_color_primary")
                (aget rgb-primary 0) (aget rgb-primary 1) (aget rgb-primary 2))
    (.uniform3f gl (gobj/get uniforms "u_color_secondary")
                (aget rgb-secondary 0) (aget rgb-secondary 1) (aget rgb-secondary 2))
    (.uniform3f gl (gobj/get uniforms "u_color_background")
                (aget rgb-bg 0) (aget rgb-bg 1) (aget rgb-bg 2))
    (.uniform1f gl (gobj/get uniforms "u_opacity") (:opacity m))

    ;; Per-orb uniforms
    (dotimes [i n]
      (let [^js p (aget pos i)]
        (.uniform2f gl (gobj/get uniforms (str "u_orb_positions[" i "]"))
                    (aget p 0) (aget p 1)))
      (.uniform1f gl (gobj/get uniforms (str "u_orb_phases[" i "]"))
                  (aget phases i)))))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [refs (du/getv el k-refs)
          ^js gl (:gl refs)]
      (when gl
        (let [^js canvas (:canvas refs)
              m          (du/getv el k-model)
              now        (js/performance.now)
              last-frame (du/getv el k-last-frame)
              dt         (/ (- now last-frame) 1000.0)
              ;; Clamp dt to prevent huge jumps
              dt         (js/Math.min dt 0.1)]

          (du/setv-untraced! el k-last-frame now)

          ;; Update time
          (when-not (prefers-reduced-motion?)
            (du/setv-untraced! el k-time (+ (du/getv el k-time) dt)))

          ;; Decay activity
          (let [activity    (du/getv el k-activity)
                impulse     (du/getv el k-impulse)
                target      (if (> impulse 0.01) 1.0 0.0)
                decay-speed (if (> impulse 0.01) 8.0 2.0)
                new-activity (model/lerp activity target (js/Math.min 1.0 (* decay-speed dt)))]
            (du/setv! el k-activity new-activity)
            (du/setv! el k-impulse (* impulse (js/Math.max 0.0 (- 1.0 (* 5.0 dt))))))

          ;; Update orb positions (noise-based drift)
          (when-not (prefers-reduced-motion?)
            (let [^js base    (du/getv el k-orb-base)
                  ^js pos     (du/getv el k-orb-pos)
                  n           (:orb-count m)
                  activity    (du/getv el k-activity)
                  drift-speed (+ 0.3 (* 0.7 activity))
                  drift-time  (+ (du/getv el k-drift-time) (* dt drift-speed))]
              (du/setv! el k-drift-time drift-time)
              (dotimes [i n]
                (let [^js bp (aget base i)
                      bx     (aget bp 0)
                      by     (aget bp 1)
                      seed-x (+ (* bx 3.0) (* (float i) 1.37))
                      seed-y (+ (* by 3.0) (* (float i) 2.41))
                      nx     (pseudo-noise seed-x seed-y drift-time)
                      ny     (pseudo-noise (+ seed-y 100.0) seed-x drift-time)
                      new-x  (+ bx (* nx 0.08))
                      new-y  (+ by (* ny 0.08))
                      ;; Clamp to [0, 1]
                      new-x  (js/Math.min 1.0 (js/Math.max 0.0 new-x))
                      new-y  (js/Math.min 1.0 (js/Math.max 0.0 new-y))
                      ^js p  (aget pos i)]
                  (aset p 0 new-x)
                  (aset p 1 new-y)))))

          ;; Resize canvas if needed (use host element dimensions)
          (let [dpr      (js/Math.min 2.0 js/devicePixelRatio)
                cw       (.-clientWidth el)
                ch       (.-clientHeight el)
                target-w (js/Math.max 1 (js/Math.floor (* cw dpr)))
                target-h (js/Math.max 1 (js/Math.floor (* ch dpr)))]
            (when (or (not= (.-width canvas) target-w)
                      (not= (.-height canvas) target-h))
              (set! (.-width canvas) target-w)
              (set! (.-height canvas) target-h)
              (.viewport gl 0 0 target-w target-h))

            ;; Only draw when canvas has real dimensions
            (when (and (> cw 0) (> ch 0))
              (update-uniforms! el gl m)
              (.drawArrays gl (.-TRIANGLE_STRIP gl) 0 4)))))

      ;; Schedule next frame
      (du/setv-untraced! el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el)))))))

(defn- start-animation! [^js el]
  (du/setv-untraced! el k-time 0.0)
  (du/setv-untraced! el k-last-frame (js/performance.now))
  (du/setv! el k-activity 0.0)
  (du/setv! el k-impulse 0.0)
  (du/setv-untraced! el k-raf
            (js/requestAnimationFrame (fn [_] (animate! el)))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv-untraced! el k-raf nil)))

;; ── Activity event handlers ─────────────────────────────────────────────────
(defn- on-scroll [^js el _e]
  (let [scroll-y (.-scrollY js/window)
        last-y   (du/getv el k-last-scroll)]
    (du/setv! el k-last-scroll scroll-y)
    (when (some? last-y)
      (let [delta (js/Math.abs (- scroll-y last-y))
            impulse (js/Math.min 1.0 (/ delta 100.0))]
        (du/setv! el k-impulse
                  (js/Math.min 1.0 (+ (du/getv el k-impulse) impulse)))))))

(defn- on-pointermove [^js el ^js e]
  (let [^js last-ptr (du/getv el k-last-ptr)
        cx           (.-clientX e)
        cy           (.-clientY e)]
    (if last-ptr
      (do
        (let [dx (- cx (gobj/get last-ptr "x"))
              dy (- cy (gobj/get last-ptr "y"))
              velocity (js/Math.sqrt (+ (* dx dx) (* dy dy)))
              impulse  (js/Math.min 1.0 (/ velocity 50.0))]
          (du/setv! el k-impulse
                    (js/Math.min 1.0 (+ (du/getv el k-impulse) impulse))))
        (gobj/set last-ptr "x" cx)
        (gobj/set last-ptr "y" cy))
      ;; First event: store position, skip velocity calculation
      (du/setv! el k-last-ptr #js {"x" cx "y" cy}))))

(defn- on-keydown [^js el _e]
  (du/setv! el k-impulse
            (js/Math.min 1.0 (+ (du/getv el k-impulse) 0.3))))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [m (du/getv el k-model)]
    (when (:interactive? m)
      (let [scroll-fn  (fn [e] (on-scroll el e))
            pointer-fn (fn [e] (on-pointermove el e))
            key-fn     (fn [e] (on-keydown el e))
            handlers   #js {:scroll scroll-fn :pointer pointer-fn :key key-fn}]
        (.addEventListener js/window "scroll" scroll-fn #js {:passive true})
        (.addEventListener js/window "pointermove" pointer-fn #js {:passive true})
        (.addEventListener js/window "keydown" key-fn #js {:passive true})
        (du/setv! el k-handlers handlers)))))

(defn- remove-listeners! [^js el]
  (when-let [^js handlers (du/getv el k-handlers)]
    (when-let [f (gobj/get handlers "scroll")]
      (.removeEventListener js/window "scroll" f))
    (when-let [f (gobj/get handlers "pointer")]
      (.removeEventListener js/window "pointermove" f))
    (when-let [f (gobj/get handlers "key")]
      (.removeEventListener js/window "keydown" f))
    (du/setv! el k-handlers nil)))

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- set-a11y! [^js el]
  (du/set-attr! el "aria-hidden" "true")
  (du/set-attr! el "role" "presentation"))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto "orbCount"           model/attr-orb-count            model/parse-orb-count)
  (du/define-parsed-prop! proto "colorPrimary"       model/attr-color-primary        model/parse-color-primary)
  (du/define-parsed-prop! proto "colorSecondary"     model/attr-color-secondary      model/parse-color-secondary)
  (du/define-parsed-prop! proto "colorBackground"    model/attr-color-background     model/parse-color-background)
  (du/define-parsed-prop! proto "pulseSpeed"         model/attr-pulse-speed          model/parse-pulse-speed)
  (du/define-parsed-prop! proto "restRate"           model/attr-rest-rate            model/parse-rest-rate)
  (du/define-parsed-prop! proto "connectionDistance" model/attr-connection-distance  model/parse-connection-distance)
  (du/define-parsed-prop! proto "orbSize"            model/attr-orb-size             model/parse-orb-size)
  (du/define-parsed-prop! proto "opacity"            model/attr-opacity              model/parse-opacity)

  ;; interactive is an opt-out boolean: absent attribute = true (default).
  ;; Setter writes "false" to disable, removes attribute to re-enable.
  ;; define-parsed-prop! doesn't fit this inverted semantics.
  (.defineProperty js/Object proto "interactive"
    #js {:get (fn xng-get-interactive []
                (this-as ^js this
                  (model/parse-interactive (.getAttribute this model/attr-interactive))))
         :set (fn xng-set-interactive [v]
                (this-as ^js this
                  (if v
                    (du/remove-attr! this model/attr-interactive)
                    (du/set-attr! this model/attr-interactive "false"))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-color-cache #js {})
    (du/setv! el k-drift-time 0.0)
    (du/setv! el k-last-ptr nil)
    (du/setv! el k-last-scroll nil)
    (ensure-refs! el)
    (init-orbs! el (:orb-count m))
    (set-a11y! el)
    (remove-listeners! el)
    (add-listeners! el)
    (start-animation! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el))

;; ── Apply model + update-from-attrs! (cache-at-tail render-pipeline) ────────
(defn- apply-model! [^js el m]
  (du/setv! el k-color-cache #js {})
  (when (du/getv el k-refs)
    (reconcile-orbs! el (:orb-count m))
    ;; Re-evaluate listeners if interactive changed
    (remove-listeners! el)
    (when (.-isConnected el)
      (add-listeners! el)))
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
