(ns baredom.components.x-kinetic-font.x-kinetic-font
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private trigger-cursor "cursor")
(def ^:private trigger-scroll "scroll")
(def ^:private trigger-both   "both")
(def ^:private mode-lean      "lean")

(def ^:private ev-pointermove "pointermove")
(def ^:private ev-scroll      "scroll")
(def ^:private hk-move        "move")
(def ^:private hk-scroll      "scroll")

(def ^:private attr-aria-hidden "aria-hidden")
(def ^:private attr-aria-label  "aria-label")
(def ^:private attr-role        "role")
(def ^:private role-text        "text")
(def ^:private val-true         "true")

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
    (du/set-attr! container "part" "container")
    (du/set-attr! sr-only "class" "sr-only")
    (du/set-attr! sr-only "part" "sr-only")
    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root sr-only)
    (du/setv! el k-refs {:root root :container container :sr-only sr-only})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:text-raw        (du/get-attr el model/attr-text)
    :trigger-raw     (du/get-attr el model/attr-trigger)
    :mode-raw        (du/get-attr el model/attr-mode)
    :per-char-attr   (du/get-attr el model/attr-per-char)
    :mass-raw        (du/get-attr el model/attr-mass)
    :tension-raw     (du/get-attr el model/attr-tension)
    :friction-raw    (du/get-attr el model/attr-friction)
    :intensity-raw   (du/get-attr el model/attr-intensity)
    :radius-raw      (du/get-attr el model/attr-radius)
    :font-family-raw (du/get-attr el model/attr-font-family)}))

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
    (du/setv! el k-css-vars
              {:wght-min (parse-css-float (.getPropertyValue cs model/css-wght-min) model/default-wght-min)
               :wght-max (parse-css-float (.getPropertyValue cs model/css-wght-max) model/default-wght-max)
               :wdth-min (parse-css-float (.getPropertyValue cs model/css-wdth-min) model/default-wdth-min)
               :wdth-max (parse-css-float (.getPropertyValue cs model/css-wdth-max) model/default-wdth-max)
               :slnt-min (parse-css-float (.getPropertyValue cs model/css-slnt-min) model/default-slnt-min)
               :slnt-max (parse-css-float (.getPropertyValue cs model/css-slnt-max) model/default-slnt-max)
               :opsz-min (parse-css-float (.getPropertyValue cs model/css-opsz-min) model/default-opsz-min)
               :opsz-max (parse-css-float (.getPropertyValue cs model/css-opsz-max) model/default-opsz-max)
               :skew-max (parse-css-float (.getPropertyValue cs model/css-skew-max) model/default-skew-max)})))

;; ── Text rendering ──────────────────────────────────────────────────────────
(defn- build-chars! [^js el text]
  (let [{:keys [container]} (du/getv el k-refs)
        ^js container container
        chars  (vec text)
        n      (count chars)
        spans  (js/Array. n)
        springs (js/Array. n)]
    (set! (.-textContent container) "")
    (dotimes [i n]
      (let [ch   (nth chars i)
            span (.createElement js/document "span")]
        (du/set-attr! span "part" "char")
        (if (= ch " ")
          (do (du/set-attr! span "data-ws" "")
              ;; U+00A0 NO-BREAK SPACE. Setting via textContent avoids
              ;; the only innerHTML write in the codebase; the literal
              ;; non-breaking space renders identically to "&nbsp;".
              (set! (.-textContent span) " "))
          (set! (.-textContent span) ch))
        (.appendChild container span)
        (aset spans i span)
        ;; Each spring: #js [displacement, velocity]
        (aset springs i #js [0.0 0.0])))
    (du/setv! el k-char-spans spans)
    (du/setv! el k-springs springs)))

(defn- build-whole-text! [^js el text]
  (let [{:keys [container]} (du/getv el k-refs)
        ^js container container]
    (set! (.-textContent container) text)
    (du/setv! el k-char-spans nil)
    ;; Single spring: #js [displacement, velocity]
    (du/setv! el k-springs #js [#js [0.0 0.0]])))

;; ── Font variation application ──────────────────────────────────────────────
(defn- apply-font-variation! [^js target ^js axes skew-deg]
  (let [^js st (.-style target)]
    (.setProperty st "font-variation-settings" (model/build-variation-string axes))
    (if (zero? skew-deg)
      (.removeProperty st "transform")
      (.setProperty st "transform" (str "skewX(" (.toFixed skew-deg 1) "deg)")))))

;; ── Animation loop (decomposed into compute / step / apply / tick) ─────────
(defn- cursor-force-from-rect
  "Compute the cursor force for a given element's bounding rect."
  [^js rect mouse-x mouse-y radius]
  (let [cx       (+ (.-left rect) (* 0.5 (.-width rect)))
        cy       (+ (.-top rect)  (* 0.5 (.-height rect)))
        dx       (- mouse-x cx)
        dy       (- mouse-y cy)
        dist     (js/Math.sqrt (+ (* dx dx) (* dy dy)))]
    (model/compute-cursor-force dist radius)))

(defn- compute-force-for-spring
  "Combine per-spring cursor force (if applicable) with the global scroll force."
  [^js el ^js spans i per-char? trigger mouse-x mouse-y radius scroll-force]
  (let [cursor-trigger? (or (= trigger trigger-cursor) (= trigger trigger-both))]
    (cond
      (and per-char? cursor-trigger?)
      (let [^js span (aget spans i)
            ^js rect (.getBoundingClientRect span)
            cf       (cursor-force-from-rect rect mouse-x mouse-y radius)]
        (js/Math.max cf scroll-force))

      (and per-char? (= trigger trigger-scroll))
      scroll-force

      (and (not per-char?) cursor-trigger?)
      (let [{:keys [container]} (du/getv el k-refs)
            ^js container container
            ^js rect (.getBoundingClientRect container)
            cf       (cursor-force-from-rect rect mouse-x mouse-y radius)]
        (js/Math.max cf scroll-force))

      (= trigger trigger-scroll)
      scroll-force

      :else 0.0)))

(defn- step-spring!
  "Integrate one spring step. Mutates the spring array and returns the new
   [disp vel] pair as a JS array."
  [^js spring target dt mass tension friction]
  (let [cur-disp (aget spring 0)
        cur-vel  (aget spring 1)
        result   (model/spring-step cur-disp target cur-vel dt mass tension friction)
        new-disp (aget result 0)
        new-vel  (aget result 1)]
    (aset spring 0 new-disp)
    (aset spring 1 new-vel)
    result))

(defn- apply-spring-output!
  "Map displacement to font axes and apply to the per-char span or whole-text
   container."
  [^js el ^js spans i per-char? new-disp
   {:keys [modes intensity wght-min wght-max wdth-min wdth-max
           slnt-min slnt-max opsz-min opsz-max skew-max has-lean?]}]
  (let [axes     (model/map-force-to-axes new-disp modes intensity
                   wght-min wght-max wdth-min wdth-max
                   slnt-min slnt-max opsz-min opsz-max)
        skew-deg (if has-lean? (* new-disp intensity skew-max) 0.0)]
    (if per-char?
      (apply-font-variation! (aget spans i) axes skew-deg)
      (let [{:keys [container]} (du/getv el k-refs)]
        (apply-font-variation! container axes skew-deg)))))

(declare animate!)

(defn- tick-or-stop!
  "After processing all springs, either schedule the next frame or settle the
   animation if all springs are at rest and scroll delta has decayed."
  [^js el all-settled]
  (let [still-scrolling? (> (or (du/getv el k-scroll-delta) 0.0) 0.5)]
    (if (and (aget all-settled 0) (not still-scrolling?))
      (do
        (du/setv-untraced! el k-raf nil)
        (du/setv! el k-scroll-delta 0.0)
        (when (du/getv el k-active)
          (du/setv! el k-active false)
          (du/dispatch! el model/event-spring-settle #js {})))
      (du/setv-untraced! el k-raf
                (js/requestAnimationFrame (fn on-raf-tick [_] (animate! el)))))))

(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (du/getv el k-model)
          cv         (du/getv el k-css-vars)
          now        (js/performance.now)
          last-frame (du/getv el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)
          mass       (:mass m)
          tension    (:tension m)
          friction   (:friction m)
          intensity  (:intensity m)
          radius     (:radius m)
          trigger    (:trigger m)
          modes      (:modes m)
          axis-ctx   (assoc cv :modes modes :intensity intensity
                            :has-lean? (contains? modes mode-lean))
          mouse-x    (du/getv el k-mouse-x)
          mouse-y    (du/getv el k-mouse-y)
          scroll-d   (or (du/getv el k-scroll-delta) 0.0)
          scroll-force (model/compute-scroll-force scroll-d)
          ^js springs (du/getv el k-springs)
          ^js spans  (du/getv el k-char-spans)
          per-char?  (some? spans)
          n          (.-length springs)
          all-settled #js [true]]

      (du/setv-untraced! el k-last-frame now)
      ;; Decay scroll delta instead of resetting — keeps force warm over
      ;; multiple frames so the spring has time to respond.
      (du/setv! el k-scroll-delta (* scroll-d 0.85))

      ;; 1-slot JS array avoids volatile! while letting the dotimes body mutate.
      (dotimes [i n]
        (let [^js spring (aget springs i)
              target     (compute-force-for-spring el spans i per-char?
                                                   trigger mouse-x mouse-y
                                                   radius scroll-force)
              result     (step-spring! spring target dt mass tension friction)
              new-disp   (aget result 0)
              new-vel    (aget result 1)]
          (when-not (model/spring-settled? (- new-disp target) new-vel)
            (aset all-settled 0 false))
          (apply-spring-output! el spans i per-char? new-disp axis-ctx)))

      (tick-or-stop! el all-settled))))

(defn- start-animation! [^js el]
  (when-not (du/getv el k-raf)
    (when-not (du/getv el k-active)
      (du/setv! el k-active true)
      (du/dispatch! el model/event-spring-activate #js {}))
    (du/setv-untraced! el k-last-frame (js/performance.now))
    (du/setv-untraced! el k-raf
              (js/requestAnimationFrame (fn on-first-frame [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv-untraced! el k-raf nil)))

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-mousemove [^js el ^js e]
  (du/setv! el k-mouse-x (.-clientX e))
  (du/setv! el k-mouse-y (.-clientY e))
  (when-not (prefers-reduced-motion?)
    (start-animation! el)))

(defn- on-scroll [^js el]
  (let [current-y (.-scrollY js/window)
        prev-y    (or (du/getv el k-last-scroll-y) current-y)
        delta     (- current-y prev-y)]
    (du/setv! el k-last-scroll-y current-y)
    (du/setv! el k-scroll-delta
              (+ (or (du/getv el k-scroll-delta) 0.0) (js/Math.abs delta)))
    (when-not (prefers-reduced-motion?)
      (start-animation! el))))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el m]
  (let [trigger (:trigger m)
        hdl     #js {}]
    ;; Cursor tracking (document-level for proximity detection outside element)
    (when (or (= trigger trigger-cursor) (= trigger trigger-both))
      (let [move-fn (fn handle-pointermove [^js e] (on-mousemove el e))]
        (gobj/set hdl hk-move move-fn)
        (.addEventListener js/document ev-pointermove move-fn #js {:passive true})))
    ;; Scroll tracking
    (when (or (= trigger trigger-scroll) (= trigger trigger-both))
      (let [scroll-fn (fn handle-scroll [] (on-scroll el))]
        (gobj/set hdl hk-scroll scroll-fn)
        (du/setv! el k-last-scroll-y (.-scrollY js/window))
        (.addEventListener js/window ev-scroll scroll-fn #js {:passive true})))
    (du/setv! el k-handlers hdl)))

(defn- remove-listeners! [^js el]
  (when-let [^js hdl (du/getv el k-handlers)]
    (when-let [move-fn (gobj/get hdl hk-move)]
      (.removeEventListener js/document ev-pointermove move-fn))
    (when-let [scroll-fn (gobj/get hdl hk-scroll)]
      (.removeEventListener js/window ev-scroll scroll-fn))
    (du/setv! el k-handlers nil)))

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- update-a11y! [^js el text]
  (let [{:keys [sr-only]} (du/getv el k-refs)
        ^js sr-only sr-only]
    (set! (.-textContent sr-only) text)
    (if (= text "")
      (do (du/set-attr!    el attr-aria-hidden val-true)
          (du/remove-attr! el attr-role))
      (do (du/remove-attr! el attr-aria-hidden)
          (du/set-attr!    el attr-role       role-text)
          (du/set-attr!    el attr-aria-label text)))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ────────
;; apply-model! reads as: rebuild text DOM if text/per-char changed → rebuild
;; listeners if trigger changed → apply font-family override → re-read CSS
;; vars → update a11y → apply rest-state axes → cache model. Each phase is
;; guarded individually so a partial diff only re-runs the parts that
;; actually changed.

(defn- rebuild-text-dom! [^js el m]
  (if (:per-char? m)
    (build-chars!      el (:text m))
    (build-whole-text! el (:text m))))

(defn- rebuild-listeners! [^js el m]
  (remove-listeners! el)
  (add-listeners!    el m))

(defn- apply-font-family! [^js el {:keys [font-family]}]
  (let [{:keys [container]} (du/getv el k-refs)
        ^js container container]
    (if font-family
      (.setProperty    (.-style container) "font-family" font-family)
      (.removeProperty (.-style container) "font-family"))))

(defn- apply-rest-state-axes! [^js el m]
  (when-not (prefers-reduced-motion?)
    (let [cv         (du/getv el k-css-vars)
          rest-axes  (model/map-force-to-axes 0.0 (:modes m) (:intensity m)
                       (:wght-min cv) (:wght-max cv) (:wdth-min cv) (:wdth-max cv)
                       (:slnt-min cv) (:slnt-max cv) (:opsz-min cv) (:opsz-max cv))]
      (if (:per-char? m)
        (when-let [^js spans (du/getv el k-char-spans)]
          (dotimes [i (.-length spans)]
            (apply-font-variation! (aget spans i) rest-axes 0.0)))
        (let [{:keys [container]} (du/getv el k-refs)]
          (apply-font-variation! container rest-axes 0.0))))))

(defn- apply-model! [^js el m]
  (let [old-m (du/getv el k-model)]
    (when (or (not= (:text m)      (:text old-m))
              (not= (:per-char? m) (:per-char? old-m)))
      (rebuild-text-dom!    el m))
    (when (not= (:trigger m) (:trigger old-m))
      (rebuild-listeners!   el m))
    (apply-font-family!     el m)
    (read-css-vars!         el)
    (update-a11y!           el (:text m))
    (apply-rest-state-axes! el m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-text model/attr-text "")
  (du/define-string-prop! proto model/attr-mode model/attr-mode "bulge")
  (du/define-bool-prop!   proto "perChar"       model/attr-per-char)

  ;; trigger (string with model/parse-trigger getter)
  (.defineProperty js/Object proto model/attr-trigger
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-trigger (.getAttribute this model/attr-trigger))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-trigger)
                    (du/set-attr! this model/attr-trigger (str v)))))
         :enumerable true :configurable true})

  ;; mass (number)
  (.defineProperty js/Object proto model/attr-mass
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-mass (.getAttribute this model/attr-mass))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-mass)
                    (du/set-attr! this model/attr-mass (str v)))))
         :enumerable true :configurable true})

  ;; tension (number)
  (.defineProperty js/Object proto model/attr-tension
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-tension (.getAttribute this model/attr-tension))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-tension)
                    (du/set-attr! this model/attr-tension (str v)))))
         :enumerable true :configurable true})

  ;; friction (number)
  (.defineProperty js/Object proto model/attr-friction
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-friction (.getAttribute this model/attr-friction))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-friction)
                    (du/set-attr! this model/attr-friction (str v)))))
         :enumerable true :configurable true})

  ;; intensity (number)
  (.defineProperty js/Object proto model/attr-intensity
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-intensity (.getAttribute this model/attr-intensity))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-intensity)
                    (du/set-attr! this model/attr-intensity (str v)))))
         :enumerable true :configurable true})

  ;; radius (number)
  (.defineProperty js/Object proto model/attr-radius
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-radius (.getAttribute this model/attr-radius))))
         :set (fn [v]
                (this-as ^js this
                  (if (nil? v)
                    (du/remove-attr! this model/attr-radius)
                    (du/set-attr! this model/attr-radius (str v)))))
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
                    (du/remove-attr! this model/attr-font-family)
                    (du/set-attr! this model/attr-font-family (str v)))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (let [m (read-model el)]
    (du/setv! el k-model nil)
    (du/setv! el k-mouse-x 0.0)
    (du/setv! el k-mouse-y 0.0)
    (du/setv! el k-scroll-delta 0.0)
    (du/setv! el k-active false)
    (apply-model! el m)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el))

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
