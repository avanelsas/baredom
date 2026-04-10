(ns baredom.components.x-kinetic-font.x-kinetic-font
  (:require
   [goog.object :as gobj]
   [baredom.components.x-kinetic-font.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs         "__xKineticFontRefs")
(def ^:private k-raf          "__xKineticFontRaf")
(def ^:private k-model        "__xKineticFontModel")
(def ^:private k-springs      "__xKineticFontSpr")
(def ^:private k-char-spans   "__xKineticFontCS")
(def ^:private k-mouse-x      "__xKineticFontMX")
(def ^:private k-mouse-y      "__xKineticFontMY")
(def ^:private k-scroll-delta "__xKineticFontSD")
(def ^:private k-last-scroll-y "__xKineticFontLSY")
(def ^:private k-last-frame   "__xKineticFontLF")
(def ^:private k-handlers     "__xKineticFontHdl")
(def ^:private k-active       "__xKineticFontAct")
(def ^:private k-css-vars     "__xKineticFontCV")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   model/css-color ":var(--x-color-text,currentColor);"
   model/css-family ":system-ui,sans-serif;"
   model/css-size ":2rem;"
   model/css-wght-min ":100;"
   model/css-wght-max ":900;"
   model/css-wdth-min ":75;"
   model/css-wdth-max ":125;"
   model/css-slnt-min ":-12;"
   model/css-slnt-max ":0;"
   model/css-opsz-min ":8;"
   model/css-opsz-max ":144;"
   model/css-skew-max ":-15;}"

   "[part=container]{"
   "color:var(" model/css-color ",currentColor);"
   "font-family:var(" model/css-family ",system-ui,sans-serif);"
   "font-size:var(" model/css-size ",2rem);"
   "line-height:1.2;"
   "will-change:font-variation-settings,transform;}"

   "[part=char]{"
   "display:inline-block;"
   "will-change:font-variation-settings,transform;}"

   "[part=char][data-ws]{"
   "white-space:pre;}"

   ".sr-only{"
   "position:absolute;width:1px;height:1px;padding:0;margin:-1px;"
   "overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0;}"

   "@media(prefers-reduced-motion:reduce){"
   "[part=container],[part=char]{"
   "font-variation-settings:normal!important;"
   "will-change:auto;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        sr-only   (.createElement js/document "span")]
    (set! (.-textContent style) style-text)
    (.setAttribute container "part" "container")
    (.setAttribute sr-only "class" "sr-only")
    (.setAttribute sr-only "part" "sr-only")
    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root sr-only)
    (gobj/set el k-refs {:root root :container container :sr-only sr-only}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs)))
  nil)

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:text-raw        (.getAttribute el model/attr-text)
    :trigger-raw     (.getAttribute el model/attr-trigger)
    :mode-raw        (.getAttribute el model/attr-mode)
    :per-char-attr   (.getAttribute el model/attr-per-char)
    :mass-raw        (.getAttribute el model/attr-mass)
    :tension-raw     (.getAttribute el model/attr-tension)
    :friction-raw    (.getAttribute el model/attr-friction)
    :intensity-raw   (.getAttribute el model/attr-intensity)
    :radius-raw      (.getAttribute el model/attr-radius)
    :font-family-raw (.getAttribute el model/attr-font-family)}))

;; ── CSS custom property reading ─────────────────────────────────────────────
(defn- parse-css-float [s default-val]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "")
        default-val
        (let [n (js/parseFloat v)]
          (if (js/isNaN n) default-val n))))
    default-val))

(defn- read-css-vars! [^js el]
  (let [^js cs (js/getComputedStyle el)]
    (gobj/set el k-css-vars
              {:wght-min (parse-css-float (.getPropertyValue cs model/css-wght-min) model/default-wght-min)
               :wght-max (parse-css-float (.getPropertyValue cs model/css-wght-max) model/default-wght-max)
               :wdth-min (parse-css-float (.getPropertyValue cs model/css-wdth-min) model/default-wdth-min)
               :wdth-max (parse-css-float (.getPropertyValue cs model/css-wdth-max) model/default-wdth-max)
               :slnt-min (parse-css-float (.getPropertyValue cs model/css-slnt-min) model/default-slnt-min)
               :slnt-max (parse-css-float (.getPropertyValue cs model/css-slnt-max) model/default-slnt-max)
               :opsz-min (parse-css-float (.getPropertyValue cs model/css-opsz-min) model/default-opsz-min)
               :opsz-max (parse-css-float (.getPropertyValue cs model/css-opsz-max) model/default-opsz-max)
               :skew-max (parse-css-float (.getPropertyValue cs model/css-skew-max) model/default-skew-max)}))
  nil)

;; ── Text rendering ──────────────────────────────────────────────────────────
(defn- build-chars! [^js el text]
  (let [{:keys [container]} (gobj/get el k-refs)
        ^js container container
        chars  (vec text)
        n      (count chars)
        spans  (js/Array. n)
        springs (js/Array. n)]
    (set! (.-textContent container) "")
    (dotimes [i n]
      (let [ch   (nth chars i)
            span (.createElement js/document "span")]
        (.setAttribute span "part" "char")
        (if (= ch " ")
          (do (.setAttribute span "data-ws" "")
              (set! (.-innerHTML span) "&nbsp;"))
          (set! (.-textContent span) ch))
        (.appendChild container span)
        (aset spans i span)
        ;; Each spring: #js [displacement, velocity]
        (aset springs i #js [0.0 0.0])))
    (gobj/set el k-char-spans spans)
    (gobj/set el k-springs springs))
  nil)

(defn- build-whole-text! [^js el text]
  (let [{:keys [container]} (gobj/get el k-refs)
        ^js container container]
    (set! (.-textContent container) text)
    (gobj/set el k-char-spans nil)
    ;; Single spring: #js [displacement, velocity]
    (gobj/set el k-springs #js [#js [0.0 0.0]]))
  nil)

;; ── Font variation application ──────────────────────────────────────────────
(defn- apply-font-variation! [^js target ^js axes skew-deg]
  (let [^js st (.-style target)]
    (.setProperty st "font-variation-settings" (model/build-variation-string axes))
    (if (zero? skew-deg)
      (.removeProperty st "transform")
      (.setProperty st "transform" (str "skewX(" (.toFixed skew-deg 1) "deg)"))))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (gobj/get el k-model)
          cv         (gobj/get el k-css-vars)
          now        (js/performance.now)
          last-frame (gobj/get el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          mass       (:mass m)
          tension    (:tension m)
          friction   (:friction m)
          intensity  (:intensity m)
          radius     (:radius m)
          trigger    (:trigger m)
          modes      (:modes m)
          wght-min   (:wght-min cv)
          wght-max   (:wght-max cv)
          wdth-min   (:wdth-min cv)
          wdth-max   (:wdth-max cv)
          slnt-min   (:slnt-min cv)
          slnt-max   (:slnt-max cv)
          opsz-min   (:opsz-min cv)
          opsz-max   (:opsz-max cv)
          skew-max   (:skew-max cv)
          has-lean?  (contains? modes "lean")
          mouse-x    (gobj/get el k-mouse-x)
          mouse-y    (gobj/get el k-mouse-y)
          scroll-d    (or (gobj/get el k-scroll-delta) 0.0)
          scroll-force (model/compute-scroll-force scroll-d)
          ^js springs (gobj/get el k-springs)
          ^js spans  (gobj/get el k-char-spans)
          per-char?  (some? spans)
          n          (.-length springs)]

      (gobj/set el k-last-frame now)
      ;; Decay scroll delta instead of resetting — keeps force warm over
      ;; multiple frames so the spring has time to respond
      (gobj/set el k-scroll-delta (* scroll-d 0.85))

      (let [all-settled (volatile! true)]
        (dotimes [i n]
          (let [;; Compute force for this spring
                force
                (cond
                  ;; Per-char mode: compute per-character cursor distance
                  ;; Read span rects fresh each frame (trivial for <50 chars)
                  (and per-char? (or (= trigger "cursor") (= trigger "both")))
                  (let [^js span (aget spans i)
                        ^js r    (.getBoundingClientRect span)
                        cx       (+ (.-left r) (* 0.5 (.-width r)))
                        cy       (+ (.-top r) (* 0.5 (.-height r)))
                        dx       (- mouse-x cx)
                        dy       (- mouse-y cy)
                        dist     (js/Math.sqrt (+ (* dx dx) (* dy dy)))
                        cursor-f (model/compute-cursor-force dist radius)]
                    (js/Math.max cursor-f scroll-force))

                  ;; Per-char mode with scroll-only trigger (uniform force)
                  (and per-char? (= trigger "scroll"))
                  scroll-force

                  ;; Whole-text mode with cursor trigger
                  (and (not per-char?) (or (= trigger "cursor") (= trigger "both")))
                  (let [{:keys [container]} (gobj/get el k-refs)
                        ^js container container
                        ^js r (.getBoundingClientRect container)
                        cx (+ (.-left r) (* 0.5 (.-width r)))
                        cy (+ (.-top r) (* 0.5 (.-height r)))
                        dx (- mouse-x cx)
                        dy (- mouse-y cy)
                        dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))
                        cursor-f (model/compute-cursor-force dist radius)]
                    (js/Math.max cursor-f scroll-force))

                  ;; Scroll-only trigger
                  (= trigger "scroll")
                  scroll-force

                  :else 0.0)

                ;; Spring target is the force value
                target force
                ^js spring (aget springs i)
                cur-disp (aget spring 0)
                cur-vel  (aget spring 1)
                result   (model/spring-step cur-disp target cur-vel dt mass tension friction)
                new-disp (aget result 0)
                new-vel  (aget result 1)]

            ;; Update spring state
            (aset spring 0 new-disp)
            (aset spring 1 new-vel)

            ;; Check if settled
            (when-not (model/spring-settled? (- new-disp target) new-vel)
              (vreset! all-settled false))

            ;; Map displacement to font axes and apply
            (let [axes    (model/map-force-to-axes new-disp modes intensity
                            wght-min wght-max wdth-min wdth-max
                            slnt-min slnt-max opsz-min opsz-max)
                  skew-deg (if has-lean?
                             (* new-disp intensity skew-max)
                             0.0)]
              (if per-char?
                (apply-font-variation! (aget spans i) axes skew-deg)
                (let [{:keys [container]} (gobj/get el k-refs)]
                  (apply-font-variation! container axes skew-deg))))))

        ;; Continue or stop — keep running while scroll delta is still decaying
        (let [still-scrolling (> (or (gobj/get el k-scroll-delta) 0.0) 0.5)]
          (if (and @all-settled (not still-scrolling))
            (do
              (gobj/set el k-raf nil)
              (gobj/set el k-scroll-delta 0.0)
              (when (gobj/get el k-active)
                (gobj/set el k-active false)
                (.dispatchEvent el
                               (js/CustomEvent.
                                model/event-spring-settle
                                #js {:bubbles true :composed true :detail #js {}}))))
            (gobj/set el k-raf
                      (js/requestAnimationFrame (fn [_] (animate! el)))))))))
  nil)

(defn- start-animation! [^js el]
  (when-not (gobj/get el k-raf)
    (when-not (gobj/get el k-active)
      (gobj/set el k-active true)
      (.dispatchEvent el
                      (js/CustomEvent.
                       model/event-spring-activate
                       #js {:bubbles true :composed true :detail #js {}})))
    (gobj/set el k-last-frame (js/performance.now))
    (gobj/set el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el)))))
  nil)

(defn- stop-animation! [^js el]
  (when-let [raf-id (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (gobj/set el k-raf nil))
  nil)

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-mousemove [^js el ^js e]
  (gobj/set el k-mouse-x (.-clientX e))
  (gobj/set el k-mouse-y (.-clientY e))
  (when-not (prefers-reduced-motion?)
    (start-animation! el))
  nil)

(defn- on-scroll [^js el]
  (let [current-y (.-scrollY js/window)
        prev-y    (or (gobj/get el k-last-scroll-y) current-y)
        delta     (- current-y prev-y)]
    (gobj/set el k-last-scroll-y current-y)
    (gobj/set el k-scroll-delta
              (+ (or (gobj/get el k-scroll-delta) 0.0) (js/Math.abs delta)))
    (when-not (prefers-reduced-motion?)
      (start-animation! el)))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [m       (gobj/get el k-model)
        trigger (:trigger m)
        hdl     #js {}]
    ;; Cursor tracking (document-level for proximity detection outside element)
    (when (or (= trigger "cursor") (= trigger "both"))
      (let [move-fn (fn [^js e] (on-mousemove el e))]
        (gobj/set hdl "move" move-fn)
        (.addEventListener js/document "pointermove" move-fn #js {:passive true})))
    ;; Scroll tracking
    (when (or (= trigger "scroll") (= trigger "both"))
      (let [scroll-fn (fn [] (on-scroll el))]
        (gobj/set hdl "scroll" scroll-fn)
        (gobj/set el k-last-scroll-y (.-scrollY js/window))
        (.addEventListener js/window "scroll" scroll-fn #js {:passive true})))
    (gobj/set el k-handlers hdl))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (gobj/get el k-handlers)]
    (when-let [move-fn (gobj/get hdl "move")]
      (.removeEventListener js/document "pointermove" move-fn))
    (when-let [scroll-fn (gobj/get hdl "scroll")]
      (.removeEventListener js/window "scroll" scroll-fn))
    (gobj/set el k-handlers nil))
  nil)

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- update-a11y! [^js el text]
  (let [{:keys [sr-only]} (gobj/get el k-refs)
        ^js sr-only sr-only]
    (set! (.-textContent sr-only) text)
    (if (= text "")
      (do (.setAttribute el "aria-hidden" "true")
          (.removeAttribute el "role"))
      (do (.removeAttribute el "aria-hidden")
          (.setAttribute el "role" "text")
          (.setAttribute el "aria-label" text))))
  nil)

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- apply-model! [^js el m]
  (let [old-m (gobj/get el k-model)]
    (gobj/set el k-model m)

    ;; Rebuild text DOM if text or per-char changed
    (when (or (not= (:text m) (:text old-m))
              (not= (:per-char? m) (:per-char? old-m)))
      (if (:per-char? m)
        (build-chars! el (:text m))
        (build-whole-text! el (:text m))))

    ;; Rebuild listeners if trigger changed
    (when (not= (:trigger m) (:trigger old-m))
      (remove-listeners! el)
      (add-listeners! el))

    ;; Update font-family override
    (let [{:keys [container]} (gobj/get el k-refs)
          ^js container container
          ff (:font-family m)]
      (if ff
        (.setProperty (.-style container) "font-family" ff)
        (.removeProperty (.-style container) "font-family")))

    ;; Re-read CSS vars (axis ranges may depend on font-family)
    (read-css-vars! el)

    ;; Update accessibility
    (update-a11y! el (:text m))

    ;; Apply rest-state font-variation-settings
    (when-not (prefers-reduced-motion?)
      (let [cv (gobj/get el k-css-vars)
            rest-axes (model/map-force-to-axes 0.0 (:modes m) (:intensity m)
                        (:wght-min cv) (:wght-max cv) (:wdth-min cv) (:wdth-max cv)
                        (:slnt-min cv) (:slnt-max cv) (:opsz-min cv) (:opsz-max cv))]
        (if (:per-char? m)
          (when-let [^js spans (gobj/get el k-char-spans)]
            (dotimes [i (.-length spans)]
              (apply-font-variation! (aget spans i) rest-axes 0.0)))
          (let [{:keys [container]} (gobj/get el k-refs)]
            (apply-font-variation! container rest-axes 0.0))))))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; text (string)
  (.defineProperty js/Object proto model/attr-text
    #js {:get (fn []
                (this-as ^js this
                  (or (.getAttribute this model/attr-text) "")))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-text)
                    (.setAttribute this model/attr-text (str v)))))
         :enumerable true :configurable true})

  ;; trigger (string)
  (.defineProperty js/Object proto model/attr-trigger
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-trigger (.getAttribute this model/attr-trigger))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-trigger)
                    (.setAttribute this model/attr-trigger (str v)))))
         :enumerable true :configurable true})

  ;; mode (string)
  (.defineProperty js/Object proto model/attr-mode
    #js {:get (fn []
                (this-as ^js this
                  (or (.getAttribute this model/attr-mode) "bulge")))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-mode)
                    (.setAttribute this model/attr-mode (str v)))))
         :enumerable true :configurable true})

  ;; perChar (boolean, maps to per-char attribute)
  (.defineProperty js/Object proto "perChar"
    #js {:get (fn []
                (this-as ^js this (.hasAttribute this model/attr-per-char)))
         :set (fn [v]
                (this-as ^js this
                  (if v
                    (.setAttribute this model/attr-per-char "")
                    (.removeAttribute this model/attr-per-char))))
         :enumerable true :configurable true})

  ;; mass (number)
  (.defineProperty js/Object proto model/attr-mass
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-mass (.getAttribute this model/attr-mass))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-mass)
                    (.setAttribute this model/attr-mass (str v)))))
         :enumerable true :configurable true})

  ;; tension (number)
  (.defineProperty js/Object proto model/attr-tension
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-tension (.getAttribute this model/attr-tension))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-tension)
                    (.setAttribute this model/attr-tension (str v)))))
         :enumerable true :configurable true})

  ;; friction (number)
  (.defineProperty js/Object proto model/attr-friction
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-friction (.getAttribute this model/attr-friction))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-friction)
                    (.setAttribute this model/attr-friction (str v)))))
         :enumerable true :configurable true})

  ;; intensity (number)
  (.defineProperty js/Object proto model/attr-intensity
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-intensity (.getAttribute this model/attr-intensity))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-intensity)
                    (.setAttribute this model/attr-intensity (str v)))))
         :enumerable true :configurable true})

  ;; radius (number)
  (.defineProperty js/Object proto model/attr-radius
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-radius (.getAttribute this model/attr-radius))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-radius)
                    (.setAttribute this model/attr-radius (str v)))))
         :enumerable true :configurable true})

  ;; fontFamily (string, maps to font-family attribute)
  (.defineProperty js/Object proto "fontFamily"
    #js {:get (fn []
                (this-as ^js this
                  (model/normalize-font-family
                   (.getAttribute this model/attr-font-family))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (.removeAttribute this model/attr-font-family)
                    (.setAttribute this model/attr-font-family (str v)))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
              (ensure-refs! this)
              (let [m (read-model this)]
                (gobj/set this k-model nil)
                (gobj/set this k-mouse-x 0.0)
                (gobj/set this k-mouse-y 0.0)
                (gobj/set this k-scroll-delta 0.0)
                (gobj/set this k-active false)
                (apply-model! this m))
              nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
              (stop-animation! this)
              (remove-listeners! this)
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
