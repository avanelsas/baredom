(ns baredom.components.x-organic-progress.x-organic-progress
  (:require
   [goog.object :as gobj]
   [baredom.components.x-organic-progress.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs          "__xOrganicProgressRefs")
(def ^:private k-raf           "__xOrganicProgressRaf")
(def ^:private k-model         "__xOrganicProgressModel")
(def ^:private k-segments      "__xOrganicProgressSegs")
(def ^:private k-segment-count "__xOrganicProgressSegN")
(def ^:private k-spring-pos    "__xOrganicProgressSPos")
(def ^:private k-spring-vel    "__xOrganicProgressSVel")
(def ^:private k-last-frame    "__xOrganicProgressLF")
(def ^:private k-time          "__xOrganicProgressTime")
(def ^:private k-completed     "__xOrganicProgressDone")
(def ^:private k-bloom-active  "__xOrganicProgressBloom")
(def ^:private k-bloom-time    "__xOrganicProgressBT")
(def ^:private k-lines         "__xOrganicProgressLines")
(def ^:private k-bloom-els     "__xOrganicProgressBEls")
(def ^:private k-prev-visible  "__xOrganicProgressPV")
(def ^:private k-node-els      "__xOrganicProgressNodes")
(def ^:private k-node-data     "__xOrganicProgressND")
(def ^:private k-node-count    "__xOrganicProgressNN")

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "min-height:60px;"
   "width:100%;"
   "overflow:hidden;"
   model/css-color-primary ":#22c55e;"
   model/css-color-secondary ":#16a34a;"
   model/css-bloom-color ":#f472b6;"
   model/css-bg ":transparent;"
   model/css-branch-width ":3;"
   model/css-glow ":2;"
   model/css-opacity ":1;"
   "background:var(" model/css-bg ",transparent);"
   "opacity:var(" model/css-opacity ",1);}"

   "[part=svg]{"
   "display:block;"
   "width:100%;"
   "height:100%;}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-organic-progress-color-primary:#4ade80;"
   "--x-organic-progress-color-secondary:#22c55e;"
   "--x-organic-progress-bloom-color:#f9a8d4;}}"

   "@media(prefers-reduced-motion:reduce){"
   "[part=svg]{transition:none!important;}"
   ".bloom-petal{transition:none!important;animation:none!important;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── SVG filter creation (gooey effect) ──────────────────────────────────────
(defn- create-gooey-filter!
  "Creates SVG gooey filter: blur + color-matrix threshold.
   Returns the filter element for later updates."
  [^js defs-el filter-id glow-val]
  (let [^js filter-el (.createElementNS js/document svg-ns "filter")
        ^js blur-el   (.createElementNS js/document svg-ns "feGaussianBlur")
        ^js matrix-el (.createElementNS js/document svg-ns "feColorMatrix")]

    (.setAttribute filter-el "id" filter-id)
    (.setAttribute filter-el "x" "-10%")
    (.setAttribute filter-el "y" "-10%")
    (.setAttribute filter-el "width" "120%")
    (.setAttribute filter-el "height" "120%")

    (.setAttribute blur-el "in" "SourceGraphic")
    (.setAttribute blur-el "stdDeviation" (str glow-val))
    (.setAttribute blur-el "result" "blur")

    (.setAttribute matrix-el "in" "blur")
    (.setAttribute matrix-el "type" "matrix")
    (.setAttribute matrix-el "values" "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7")

    (.appendChild filter-el blur-el)
    (.appendChild filter-el matrix-el)
    (.appendChild defs-el filter-el)

    {:filter-el filter-el
     :blur-el   blur-el
     :matrix-el matrix-el}))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [filter-id  "x-organic-progress-gooey"
        root       (.attachShadow el #js {:mode "open"})
        style      (.createElement js/document "style")
        svg        (.createElementNS js/document svg-ns "svg")
        defs-el    (.createElementNS js/document svg-ns "defs")
        branches-g (.createElementNS js/document svg-ns "g")
        nodes-g    (.createElementNS js/document svg-ns "g")
        blooms-g   (.createElementNS js/document svg-ns "g")]

    (set! (.-textContent style) style-text)

    (.setAttribute svg "part" "svg")
    (.setAttribute svg "viewBox" "0 0 800 200")
    (.setAttribute svg "preserveAspectRatio" "xMidYMid slice")
    (.setAttribute svg "aria-hidden" "true")

    (.setAttribute branches-g "part" "branches")
    (.setAttribute branches-g "filter" (str "url(#" filter-id ")"))

    (.setAttribute nodes-g "part" "nodes")

    (.setAttribute blooms-g "part" "blooms")

    (let [filter-refs (create-gooey-filter! defs-el filter-id 2)]
      (.appendChild svg defs-el)
      (.appendChild svg branches-g)
      (.appendChild svg nodes-g)
      (.appendChild svg blooms-g)
      (.appendChild root style)
      (.appendChild root svg)

      (gobj/set el k-refs
                (merge filter-refs
                       {:root       root
                        :svg        svg
                        :branches-g branches-g
                        :nodes-g    nodes-g
                        :blooms-g   blooms-g}))))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:progress-raw (.getAttribute el model/attr-progress)
    :variant-raw  (.getAttribute el model/attr-variant)
    :color-raw    (.getAttribute el model/attr-color)
    :bloom-attr   (.getAttribute el model/attr-bloom)
    :density-raw  (.getAttribute el model/attr-density)
    :seed-raw     (.getAttribute el model/attr-seed)
    :label-raw    (.getAttribute el model/attr-label)}))

;; ── CSS helpers ─────────────────────────────────────────────────────────────
(defn- resolve-color [^js el css-prop fallback]
  (let [^js cs (.getComputedStyle js/window el)
        v      (.trim (.getPropertyValue cs css-prop))]
    (if (not= v "") v fallback)))

(defn- resolve-css-float [^js el css-prop fallback]
  (let [^js cs (.getComputedStyle js/window el)
        v      (.trim (.getPropertyValue cs css-prop))]
    (if (not= v "")
      (let [n (js/parseFloat v)]
        (if (js/isNaN n) fallback n))
      fallback)))

;; ── Segment management ─────────────────────────────────────────────────────

(defn- reconcile-lines!
  "Add/remove SVG <line> elements to match segment count."
  [^js el seg-count ^js segments]
  (let [{:keys [branches-g]} (gobj/get el k-refs)
        ^js branches-g branches-g
        ^js old-lines  (or (gobj/get el k-lines) #js [])
        old-count      (.-length old-lines)
        new-lines      #js []]

    ;; Reuse existing lines, create new ones if needed
    (dotimes [i seg-count]
      (let [^js line (if (< i old-count)
                       (aget old-lines i)
                       (let [l (.createElementNS js/document svg-ns "line")]
                         (.setAttribute l "stroke-linecap" "round")
                         (.appendChild branches-g l)
                         l))
            ^js seg  (aget segments i)
            x1       (gobj/get seg "x1")
            y1       (gobj/get seg "y1")
            x2       (gobj/get seg "x2")
            y2       (gobj/get seg "y2")]
        (.setAttribute line "x1" (str x1))
        (.setAttribute line "y1" (str y1))
        (.setAttribute line "x2" (str x2))
        (.setAttribute line "y2" (str y2))
        (.setAttribute line "opacity" "0")
        (.push new-lines line)))

    ;; Remove excess lines
    (loop [i seg-count]
      (when (< i old-count)
        (.removeChild branches-g (aget old-lines i))
        (recur (inc i))))

    (gobj/set el k-lines new-lines)
    (gobj/set el k-prev-visible -1))
  nil)

(defn- update-filter-for-variant!
  "Enable gooey filter for vine, disable for honeycomb (keep lines crisp)."
  [^js el honeycomb?]
  (let [{:keys [branches-g blur-el]} (gobj/get el k-refs)
        ^js branches-g branches-g
        ^js blur-el    blur-el]
    (when branches-g
      (if honeycomb?
        ;; Honeycomb: no filter — crisp geometric lines
        (.removeAttribute branches-g "filter")
        ;; Vine: gooey organic blur
        (.setAttribute branches-g "filter" "url(#x-organic-progress-gooey)")))
    ;; Adjust blur for vine
    (when (and blur-el (not honeycomb?))
      (.setAttribute blur-el "stdDeviation" "2")))
  nil)

(defn- reconcile-nodes!
  "Add/remove SVG elements for nodes (circles)."
  [^js el ^js node-data node-count]
  (let [{:keys [nodes-g]} (gobj/get el k-refs)
        ^js nodes-g  nodes-g
        ^js old-els  (or (gobj/get el k-node-els) #js [])
        old-count    (.-length old-els)
        new-els      #js []]
    (dotimes [i node-count]
      (let [^js nd (aget node-data i)
            ^js circle (if (< i old-count)
                         (aget old-els i)
                         (let [c (.createElementNS js/document svg-ns "circle")]
                           (.appendChild nodes-g c)
                           c))
            x (gobj/get nd "x")
            y (gobj/get nd "y")]
        (.setAttribute circle "cx" (str x))
        (.setAttribute circle "cy" (str y))
        (.setAttribute circle "r" "2.5")
        (.setAttribute circle "opacity" "0")
        (.push new-els circle)))
    ;; Remove excess
    (loop [i node-count]
      (when (< i old-count)
        (let [^js el-old (aget old-els i)]
          (when (.-parentNode el-old)
            (.removeChild nodes-g el-old)))
        (recur (inc i))))
    (gobj/set el k-node-els new-els))
  nil)

(defn- clear-nodes!
  "Remove all lattice node circles."
  [^js el]
  (let [{:keys [nodes-g]} (gobj/get el k-refs)
        ^js nodes-g nodes-g
        ^js old-els (gobj/get el k-node-els)]
    (when (and nodes-g old-els)
      (dotimes [i (.-length old-els)]
        (let [^js c (aget old-els i)]
          (when (.-parentNode c)
            (.removeChild nodes-g c))))))
  (gobj/set el k-node-els nil)
  (gobj/set el k-node-data nil)
  (gobj/set el k-node-count 0)
  nil)

(defn- regenerate-segments!
  "Regenerate segments and reconcile SVG elements."
  [^js el m]
  (let [honeycomb? (= (:variant m) "honeycomb")
        iterations (get model/density->iterations (:density m) 3)
        result     (if honeycomb?
                     ;; Honeycomb: hexagonal lattice
                     (model/honeycomb-lattice-segments
                      (:seed m) (:density m) 800.0 200.0)
                     ;; Vine: L-system with organic noise
                     (let [rules    {"F" "F[+F]F[-F]F"}
                           l-string (model/l-system-iterate "F" rules iterations)
                           noise-fn (model/make-noise-fn (:seed m))]
                       (model/l-system->segments
                        l-string noise-fn (:seed m) 0.436 38.0 20.0 100.0)))
        ^js segments (aget result 0)
        seg-count    (aget result 1)]
    (gobj/set el k-segments segments)
    (gobj/set el k-segment-count seg-count)
    (reconcile-lines! el seg-count segments)
    (update-filter-for-variant! el honeycomb?)
    (clear-nodes! el)))

;; ── Apply colors and stroke widths to lines ─────────────────────────────────
(defn- apply-line-styles! [^js el]
  (let [^js lines    (gobj/get el k-lines)
        ^js segments (gobj/get el k-segments)
        total        (gobj/get el k-segment-count)
        m            (gobj/get el k-model)
        honeycomb?   (= (:variant m) "honeycomb")
        primary      (or (:color m)
                         (resolve-color el model/css-color-primary
                                        (if honeycomb? "#818cf8" "#22c55e")))
        secondary    (resolve-color el model/css-color-secondary
                                    (if honeycomb? "#a5b4fc" "#16a34a"))]
    (let [base-w   (resolve-css-float el model/css-branch-width (if honeycomb? 1.5 3.0))]
      (if honeycomb?
        ;; Honeycomb: thin uniform hex edges with depth-based opacity
        (dotimes [i total]
          (let [^js line (aget lines i)
                ^js seg  (aget segments i)
                depth    (gobj/get seg "depth")]
            (.setAttribute line "stroke-width" (str base-w))
            (.setAttribute line "stroke" (case depth 0 primary 1 secondary secondary))
            (.setAttribute line "stroke-linecap" "round")
            (.setAttribute line "stroke-opacity" (case depth 0 "0.9" 1 "0.7" "0.5"))))
        ;; Vine: organic with round caps, width tapers with depth
        (dotimes [i total]
          (let [^js line (aget lines i)
                ^js seg  (aget segments i)
                depth    (gobj/get seg "depth")
                w        (js/Math.max 0.5 (- base-w (* depth 0.6)))
                color    (if (zero? depth) primary secondary)]
            (.setAttribute line "stroke-width" (str w))
            (.setAttribute line "stroke" color)
            (.setAttribute line "stroke-linecap" "round")
            (.setAttribute line "stroke-opacity" "1"))))))
  nil)

;; ── Segment rendering (progressive reveal) ─────────────────────────────────

(defn- render-segments! [^js el]
  (let [spring-pos    (gobj/get el k-spring-pos)
        total         (gobj/get el k-segment-count)
        visible-count (js/Math.floor (* (/ spring-pos 100.0) total))
        prev-visible  (gobj/get el k-prev-visible)
        ^js lines     (gobj/get el k-lines)]
    ;; Only update lines whose visibility changed
    (when (not= visible-count prev-visible)
      (let [lo (js/Math.min visible-count (if (< prev-visible 0) 0 prev-visible))
            hi (js/Math.max visible-count (if (< prev-visible 0) total prev-visible))]
        (loop [i lo]
          (when (< i hi)
            (let [^js line (aget lines i)]
              (when line
                (.setAttribute line "opacity"
                               (if (< i visible-count) "1" "0"))))
            (recur (inc i)))))
      ;; Vine nodes: opacity toggle
      (let [^js node-els  (gobj/get el k-node-els)
            node-count    (or (gobj/get el k-node-count) 0)]
        (when (and node-els (pos? node-count))
          (let [visible-nodes (js/Math.floor (* (/ spring-pos 100.0) node-count))]
            (dotimes [i node-count]
              (let [^js c (aget node-els i)]
                (when c
                  (.setAttribute c "opacity"
                                 (if (< i visible-nodes) "1" "0"))))))))
      (gobj/set el k-prev-visible visible-count)))
  nil)

;; ── Bloom effect ────────────────────────────────────────────────────────────

(defn- start-bloom! [^js el]
  (let [^js segments (gobj/get el k-segments)
        total        (gobj/get el k-segment-count)
        {:keys [blooms-g]} (gobj/get el k-refs)
        ^js blooms-g blooms-g
        m            (gobj/get el k-model)
        bloom-color  (resolve-color el model/css-bloom-color "#f472b6")
        rng          (model/make-rng (+ (:seed m) 7))
        bloom-els    #js []]
    ;; Create bloom circles at branch tips (depth >= 2)
    (dotimes [i total]
      (let [^js seg (aget segments i)
            depth   (gobj/get seg "depth")]
        (when (>= depth 2)
          (let [circle (.createElementNS js/document svg-ns "circle")
                x2     (gobj/get seg "x2")
                y2     (gobj/get seg "y2")
                dx     (- (* 2.0 (model/rng-next! rng)) 1.0)
                dy     (- (* 2.0 (model/rng-next! rng)) 1.0)]
            (.setAttribute circle "cx" (str x2))
            (.setAttribute circle "cy" (str y2))
            (.setAttribute circle "r" "0")
            (.setAttribute circle "fill" bloom-color)
            (.setAttribute circle "opacity" "0")
            (.setAttribute circle "class" "bloom-petal")
            ;; Store scatter direction
            (gobj/set circle "__dx" dx)
            (gobj/set circle "__dy" dy)
            (.appendChild blooms-g circle)
            (.push bloom-els circle)))))
    (gobj/set el k-bloom-els bloom-els)
    (gobj/set el k-bloom-active true)
    (gobj/set el k-bloom-time 0.0))
  nil)

(defn- finish-bloom! [^js el]
  (let [{:keys [blooms-g]} (gobj/get el k-refs)
        ^js blooms-g blooms-g
        ^js bloom-els (gobj/get el k-bloom-els)]
    ;; Remove all bloom circles
    (when bloom-els
      (dotimes [i (.-length bloom-els)]
        (let [^js c (aget bloom-els i)]
          (when (.-parentNode c)
            (.removeChild blooms-g c)))))
    (gobj/set el k-bloom-els nil)
    (gobj/set el k-bloom-active false)
    ;; Dispatch bloom-end event
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-bloom-end
                     #js {:bubbles true :composed true
                          :detail #js {:progress 100}})))
  nil)

(defn- update-bloom! [^js el dt]
  (let [bloom-time (+ (gobj/get el k-bloom-time) dt)
        duration   1.5
        t          (/ bloom-time duration)]
    (gobj/set el k-bloom-time bloom-time)
    (if (> t 1.0)
      ;; Bloom finished
      (finish-bloom! el)
      ;; Animate bloom elements
      (let [^js bloom-els (gobj/get el k-bloom-els)
            scale         (* 4.0 (model/ease-out-cubic (js/Math.min t 1.0)))
            opacity       (js/Math.max 0.0 (- 1.0 (* t t)))]
        (dotimes [i (.-length bloom-els)]
          (let [^js c  (aget bloom-els i)
                dx     (* (gobj/get c "__dx") scale 8.0)
                dy     (* (gobj/get c "__dy") scale 8.0)]
            (.setAttribute c "r" (str (* scale 2.5)))
            (.setAttribute c "opacity" (str opacity))
            (.setAttribute c "transform"
                           (str "translate(" dx "," dy ")")))))))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────

(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (gobj/get el k-model)
          now        (js/performance.now)
          last-frame (gobj/get el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)]

      (gobj/set el k-last-frame now)

      ;; Update time (for indeterminate oscillation)
      (gobj/set el k-time (+ (gobj/get el k-time) dt))

      ;; Spring physics: smooth progress interpolation
      (let [target (if (:indeterminate? m)
                     ;; Oscillate between 30-70% for indeterminate
                     (+ 50.0 (* 20.0 (js/Math.sin (* (gobj/get el k-time) 1.5))))
                     (:percent m))
            current  (gobj/get el k-spring-pos)
            velocity (gobj/get el k-spring-vel)
            result   (model/spring-step current target velocity dt 120.0 12.0)]
        (gobj/set el k-spring-pos (aget result 0))
        (gobj/set el k-spring-vel (aget result 1)))

      ;; Render visible segments
      (render-segments! el)

      ;; Check for completion
      (when (and (:complete? m) (not (gobj/get el k-completed)))
        (gobj/set el k-completed true)
        (.dispatchEvent el
                        (js/CustomEvent.
                         model/event-complete
                         #js {:bubbles true :composed true
                              :detail #js {:progress 100}}))
        ;; Start bloom if enabled
        (when (:bloom? m)
          (start-bloom! el)))

      ;; Reset completed flag if progress drops below 100
      (when (and (not (:complete? m)) (gobj/get el k-completed)
                 (not (gobj/get el k-bloom-active)))
        (gobj/set el k-completed false))

      ;; Update bloom animation if active
      (when (gobj/get el k-bloom-active)
        (update-bloom! el dt))

      ;; Schedule next frame
      (gobj/set el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el))))))
  nil)

(defn- start-animation! [^js el]
  (gobj/set el k-time 0.0)
  (gobj/set el k-spring-pos 0.0)
  (gobj/set el k-spring-vel 0.0)
  (gobj/set el k-last-frame (js/performance.now))
  (gobj/set el k-completed false)
  (gobj/set el k-bloom-active false)
  (gobj/set el k-prev-visible -1)
  (gobj/set el k-raf
            (js/requestAnimationFrame (fn [_] (animate! el))))
  nil)

(defn- stop-animation! [^js el]
  (when-let [raf-id (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (gobj/set el k-raf nil))
  nil)

;; ── Static render (for reduced motion) ─────────────────────────────────────

(defn- render-static! [^js el]
  (let [m     (gobj/get el k-model)
        total (gobj/get el k-segment-count)
        pct   (if (:indeterminate? m) 50.0 (:percent m))
        visible-count (js/Math.floor (* (/ pct 100.0) total))
        ^js lines (gobj/get el k-lines)]
    (dotimes [i total]
      (let [^js line (aget lines i)]
        (when line
          (.setAttribute line "opacity"
                         (if (< i visible-count) "1" "0")))))
    ;; Vine nodes: opacity
    (let [^js node-els  (gobj/get el k-node-els)
          node-count    (or (gobj/get el k-node-count) 0)]
      (when (and node-els (pos? node-count))
        (let [visible-nodes (js/Math.floor (* (/ pct 100.0) node-count))]
          (dotimes [i node-count]
            (let [^js c (aget node-els i)]
              (when c
                (.setAttribute c "opacity"
                               (if (< i visible-nodes) "1" "0"))))))))
    (gobj/set el k-prev-visible visible-count)
    ;; Fire completion for static mode
    (when (and (:complete? m) (not (gobj/get el k-completed)))
      (gobj/set el k-completed true)
      (.dispatchEvent el
                      (js/CustomEvent.
                       model/event-complete
                       #js {:bubbles true :composed true
                            :detail #js {:progress 100}}))))
  nil)

;; ── Accessibility ───────────────────────────────────────────────────────────

(defn- set-a11y! [^js el m]
  (.setAttribute el "role" "progressbar")
  (.setAttribute el "aria-valuemin" "0")
  (.setAttribute el "aria-valuemax" "100")
  (if (:indeterminate? m)
    (do (.removeAttribute el "aria-valuenow")
        (.setAttribute el "aria-busy" "true")
        (.setAttribute el "aria-valuetext" "Loading..."))
    (do (.setAttribute el "aria-valuenow" (str (js/Math.round (:percent m))))
        (.removeAttribute el "aria-busy")
        (.setAttribute el "aria-valuetext" (:aria-valuetext m))))
  (if-let [label (:label m)]
    (.setAttribute el "aria-label" label)
    (.removeAttribute el "aria-label"))
  nil)

;; ── Update from attributes ──────────────────────────────────────────────────

(def ^:private structural-attrs
  #{model/attr-variant model/attr-density model/attr-seed})

(defn- update-from-attrs!
  "Re-read model, update ARIA, and optionally regenerate segments."
  [^js el attr-name]
  (let [m (read-model el)]
    (gobj/set el k-model m)
    (set-a11y! el m)
    ;; Regenerate segments only when structural attributes change
    (when (or (nil? attr-name) (contains? structural-attrs attr-name))
      (regenerate-segments! el m)
      (apply-line-styles! el)
      ;; Re-render current state
      (gobj/set el k-prev-visible -1)
      (when (prefers-reduced-motion?)
        (render-static! el))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────

(defn- install-property-accessors! [^js proto]
  ;; progress
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-progress
                                         (.getAttribute this model/attr-progress))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-progress)
                                          (.setAttribute this model/attr-progress (str v)))))
                        :enumerable true :configurable true})

  ;; variant
  (.defineProperty js/Object proto "variant"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-variant
                                         (.getAttribute this model/attr-variant))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-variant)
                                          (.setAttribute this model/attr-variant (str v)))))
                        :enumerable true :configurable true})

  ;; color
  (.defineProperty js/Object proto "color"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-color
                                         (.getAttribute this model/attr-color))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-color)
                                          (.setAttribute this model/attr-color (str v)))))
                        :enumerable true :configurable true})

  ;; bloom (boolean, default true)
  (.defineProperty js/Object proto "bloom"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bloom
                                         (.getAttribute this model/attr-bloom))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.removeAttribute this model/attr-bloom)
                                          (.setAttribute this model/attr-bloom "false"))))
                        :enumerable true :configurable true})

  ;; density
  (.defineProperty js/Object proto "density"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-density
                                         (.getAttribute this model/attr-density))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-density)
                                          (.setAttribute this model/attr-density (str v)))))
                        :enumerable true :configurable true})

  ;; seed
  (.defineProperty js/Object proto "seed"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-seed
                                         (.getAttribute this model/attr-seed))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-seed)
                                          (.setAttribute this model/attr-seed (str (int v))))))
                        :enumerable true :configurable true})

  ;; label
  (.defineProperty js/Object proto "label"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-label
                                         (.getAttribute this model/attr-label))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-label)
                                          (.setAttribute this model/attr-label (str v)))))
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
                       (update-from-attrs! this nil)
                       (apply-line-styles! this)
                       (if (prefers-reduced-motion?)
                         (render-static! this)
                         (start-animation! this)))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (stop-animation! this)
                     ;; Clean up bloom elements
                     (when (gobj/get this k-bloom-active)
                       (let [{:keys [blooms-g]} (gobj/get this k-refs)
                             ^js blooms-g blooms-g
                             ^js bloom-els (gobj/get this k-bloom-els)]
                         (when bloom-els
                           (dotimes [i (.-length bloom-els)]
                             (let [^js c (aget bloom-els i)]
                               (when (.-parentNode c)
                                 (.removeChild blooms-g c))))))
                       (gobj/set this k-bloom-els nil)
                       (gobj/set this k-bloom-active false))
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [attr-name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (when (gobj/get this k-refs)
                         (update-from-attrs! this attr-name)
                         (when (not (contains? structural-attrs attr-name))
                           (apply-line-styles! this))))
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
