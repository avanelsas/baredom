(ns baredom.components.x-organic-progress.x-organic-progress
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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
(def ^:private k-bloom-dx      "__xOrganicProgressBDx")
(def ^:private k-bloom-dy      "__xOrganicProgressBDy")

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
   model/css-color-primary ":var(--x-color-success,#22c55e);"
   model/css-color-secondary ":var(--x-color-success,#16a34a);"
   model/css-bloom-color ":var(--x-color-danger,#f472b6);"
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

   "[part=nodes] circle{"
   "fill:var(" model/css-color-primary ",#22c55e);}"

   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-organic-progress-color-primary:var(--x-color-success,#4ade80);"
   "--x-organic-progress-color-secondary:var(--x-color-success,#22c55e);"
   "--x-organic-progress-bloom-color:var(--x-color-danger,#f9a8d4);}}"

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

    (du/set-attr! filter-el "id" filter-id)
    (du/set-attr! filter-el "x" "-10%")
    (du/set-attr! filter-el "y" "-10%")
    (du/set-attr! filter-el "width" "120%")
    (du/set-attr! filter-el "height" "120%")

    (du/set-attr! blur-el "in" "SourceGraphic")
    (du/set-attr! blur-el "stdDeviation" (str glow-val))
    (du/set-attr! blur-el "result" "blur")

    (du/set-attr! matrix-el "in" "blur")
    (du/set-attr! matrix-el "type" "matrix")
    (du/set-attr! matrix-el "values" "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7")

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

    (du/set-attr! svg "part" "svg")
    (du/set-attr! svg "viewBox" "0 0 800 200")
    (du/set-attr! svg "preserveAspectRatio" "xMidYMid slice")
    (du/set-attr! svg "aria-hidden" "true")

    (du/set-attr! branches-g "part" "branches")
    (du/set-attr! branches-g "filter" (str "url(#" filter-id ")"))

    (du/set-attr! nodes-g "part" "nodes")

    (du/set-attr! blooms-g "part" "blooms")

    (let [filter-refs (create-gooey-filter! defs-el filter-id 2)]
      (.appendChild svg defs-el)
      (.appendChild svg branches-g)
      (.appendChild svg nodes-g)
      (.appendChild svg blooms-g)
      (.appendChild root style)
      (.appendChild root svg)

      (du/setv! el k-refs
                (merge filter-refs
                       {:root       root
                        :svg        svg
                        :branches-g branches-g
                        :nodes-g    nodes-g
                        :blooms-g   blooms-g})))))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:progress-raw (du/get-attr el model/attr-progress)
    :variant-raw  (du/get-attr el model/attr-variant)
    :color-raw    (du/get-attr el model/attr-color)
    :bloom-attr   (du/get-attr el model/attr-bloom)
    :density-raw  (du/get-attr el model/attr-density)
    :seed-raw     (du/get-attr el model/attr-seed)
    :label-raw    (du/get-attr el model/attr-label)}))

;; ── CSS helpers ─────────────────────────────────────────────────────────────
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
  (let [{:keys [branches-g]} (du/getv el k-refs)
        ^js branches-g branches-g
        ^js old-lines  (or (du/getv el k-lines) #js [])
        old-count      (.-length old-lines)
        new-lines      #js []]

    ;; Reuse existing lines, create new ones if needed
    (dotimes [i seg-count]
      (let [^js line (if (< i old-count)
                       (aget old-lines i)
                       (let [l (.createElementNS js/document svg-ns "line")]
                         (du/set-attr! l "stroke-linecap" "round")
                         (.appendChild branches-g l)
                         l))
            ^js seg  (aget segments i)
            x1       (gobj/get seg "x1")
            y1       (gobj/get seg "y1")
            x2       (gobj/get seg "x2")
            y2       (gobj/get seg "y2")]
        (du/set-attr! line "x1" (str x1))
        (du/set-attr! line "y1" (str y1))
        (du/set-attr! line "x2" (str x2))
        (du/set-attr! line "y2" (str y2))
        (du/set-attr! line "opacity" "0")
        (.push new-lines line)))

    ;; Remove excess lines
    (loop [i seg-count]
      (when (< i old-count)
        (.removeChild branches-g (aget old-lines i))
        (recur (inc i))))

    (du/setv! el k-lines new-lines)
    (du/setv! el k-prev-visible -1)))

(defn- update-filter-for-variant!
  "Enable gooey filter for vine, disable for honeycomb (keep lines crisp)."
  [^js el honeycomb?]
  (let [{:keys [branches-g blur-el]} (du/getv el k-refs)
        ^js branches-g branches-g
        ^js blur-el    blur-el]
    (when branches-g
      (if honeycomb?
        ;; Honeycomb: no filter — crisp geometric lines
        (du/remove-attr! branches-g "filter")
        ;; Vine: gooey organic blur
        (du/set-attr! branches-g "filter" "url(#x-organic-progress-gooey)")))
    ;; Adjust blur for vine
    (when (and blur-el (not honeycomb?))
      (du/set-attr! blur-el "stdDeviation" "2"))))

(defn- clear-nodes!
  "Remove all lattice node circles."
  [^js el]
  (let [{:keys [nodes-g]} (du/getv el k-refs)
        ^js nodes-g nodes-g
        ^js old-els (du/getv el k-node-els)]
    (when (and nodes-g old-els)
      (dotimes [i (.-length old-els)]
        (let [^js c (aget old-els i)]
          (when (.-parentNode c)
            (.removeChild nodes-g c))))))
  (du/setv! el k-node-els nil)
  (du/setv! el k-node-data nil)
  (du/setv! el k-node-count 0))

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
    (du/setv! el k-segments segments)
    (du/setv! el k-segment-count seg-count)
    (reconcile-lines! el seg-count segments)
    (update-filter-for-variant! el honeycomb?)
    (clear-nodes! el)))

;; ── Apply colors and stroke widths to lines ─────────────────────────────────
(defn- apply-line-styles! [^js el]
  (let [^js lines    (du/getv el k-lines)
        ^js segments (du/getv el k-segments)
        total        (du/getv el k-segment-count)
        m            (du/getv el k-model)
        honeycomb?   (= (:variant m) "honeycomb")
        ;; Use CSS var() references so strokes respond to theme changes.
        ;; When a custom color attribute is set, use it directly; otherwise
        ;; reference the component's CSS custom properties which chain to
        ;; global theme tokens.
        color-attr   (:color m)
        primary-css  (if color-attr
                       color-attr
                       (str "var(" model/css-color-primary ","
                            (if honeycomb? "#818cf8" "#22c55e") ")"))
        secondary-css (str "var(" model/css-color-secondary ","
                           (if honeycomb? "#a5b4fc" "#16a34a") ")")
        base-w   (resolve-css-float el model/css-branch-width (if honeycomb? 1.5 3.0))]
    (if honeycomb?
        ;; Honeycomb: thin uniform hex edges with depth-based opacity
        (dotimes [i total]
          (let [^js line (aget lines i)
                ^js seg  (aget segments i)
                depth    (gobj/get seg "depth")]
            (du/set-attr! line "stroke-width" (str base-w))
            (set! (.. line -style -stroke)
                  (case depth 0 primary-css 1 secondary-css secondary-css))
            (du/set-attr! line "stroke-linecap" "round")
            (du/set-attr! line "stroke-opacity" (case depth 0 "0.9" 1 "0.7" "0.5"))))
        ;; Vine: organic with round caps, width tapers with depth
        (dotimes [i total]
          (let [^js line (aget lines i)
                ^js seg  (aget segments i)
                depth    (gobj/get seg "depth")
                w        (js/Math.max 0.5 (- base-w (* depth 0.6)))
                color    (if (zero? depth) primary-css secondary-css)]
            (du/set-attr! line "stroke-width" (str w))
            (set! (.. line -style -stroke) color)
            (du/set-attr! line "stroke-linecap" "round")
            (du/set-attr! line "stroke-opacity" "1"))))))

;; ── Segment rendering (progressive reveal) ─────────────────────────────────

(defn- render-segments! [^js el]
  (let [spring-pos    (du/getv el k-spring-pos)
        total         (du/getv el k-segment-count)
        visible-count (js/Math.floor (* (/ spring-pos 100.0) total))
        prev-visible  (du/getv el k-prev-visible)
        ^js lines     (du/getv el k-lines)]
    ;; Only update lines whose visibility changed
    (when (not= visible-count prev-visible)
      (let [lo (js/Math.min visible-count (if (< prev-visible 0) 0 prev-visible))
            hi (js/Math.max visible-count (if (< prev-visible 0) total prev-visible))]
        (loop [i lo]
          (when (< i hi)
            (let [^js line (aget lines i)]
              (when line
                (du/set-attr! line "opacity"
                               (if (< i visible-count) "1" "0"))))
            (recur (inc i)))))
      ;; Vine nodes: opacity toggle
      (let [^js node-els  (du/getv el k-node-els)
            node-count    (or (du/getv el k-node-count) 0)]
        (when (and node-els (pos? node-count))
          (let [visible-nodes (js/Math.floor (* (/ spring-pos 100.0) node-count))]
            (dotimes [i node-count]
              (let [^js c (aget node-els i)]
                (when c
                  (du/set-attr! c "opacity"
                                 (if (< i visible-nodes) "1" "0"))))))))
      (du/setv! el k-prev-visible visible-count))))

;; ── Bloom effect ────────────────────────────────────────────────────────────

(defn- start-bloom! [^js el]
  (let [^js segments (du/getv el k-segments)
        total        (du/getv el k-segment-count)
        {:keys [blooms-g]} (du/getv el k-refs)
        ^js blooms-g blooms-g
        m            (du/getv el k-model)
        bloom-color  (str "var(" model/css-bloom-color ",#f472b6)")
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
            (du/set-attr! circle "cx" (str x2))
            (du/set-attr! circle "cy" (str y2))
            (du/set-attr! circle "r" "0")
            (set! (.. circle -style -fill) bloom-color)
            (du/set-attr! circle "opacity" "0")
            (du/set-attr! circle "class" "bloom-petal")
            ;; Store scatter direction
            (gobj/set circle k-bloom-dx dx)
            (gobj/set circle k-bloom-dy dy)
            (.appendChild blooms-g circle)
            (.push bloom-els circle)))))
    (du/setv! el k-bloom-els bloom-els)
    (du/setv! el k-bloom-active true)
    (du/setv! el k-bloom-time 0.0)))

(defn- finish-bloom! [^js el]
  (let [{:keys [blooms-g]} (du/getv el k-refs)
        ^js blooms-g blooms-g
        ^js bloom-els (du/getv el k-bloom-els)]
    ;; Remove all bloom circles
    (when bloom-els
      (dotimes [i (.-length bloom-els)]
        (let [^js c (aget bloom-els i)]
          (when (.-parentNode c)
            (.removeChild blooms-g c)))))
    (du/setv! el k-bloom-els nil)
    (du/setv! el k-bloom-active false)
    ;; Dispatch bloom-end event
    (du/dispatch! el model/event-bloom-end #js {:progress 100})))

(defn- update-bloom! [^js el dt]
  (let [bloom-time (+ (du/getv el k-bloom-time) dt)
        duration   1.5
        t          (/ bloom-time duration)]
    (du/setv! el k-bloom-time bloom-time)
    (if (> t 1.0)
      ;; Bloom finished
      (finish-bloom! el)
      ;; Animate bloom elements
      (let [^js bloom-els (du/getv el k-bloom-els)
            scale         (* 4.0 (model/ease-out-cubic (js/Math.min t 1.0)))
            opacity       (js/Math.max 0.0 (- 1.0 (* t t)))]
        (dotimes [i (.-length bloom-els)]
          (let [^js c  (aget bloom-els i)
                dx     (* (gobj/get c k-bloom-dx) scale 8.0)
                dy     (* (gobj/get c k-bloom-dy) scale 8.0)]
            (du/set-attr! c "r" (str (* scale 2.5)))
            (du/set-attr! c "opacity" (str opacity))
            (du/set-attr! c "transform"
                           (str "translate(" dx "," dy ")"))))))))

;; ── Animation loop ──────────────────────────────────────────────────────────

(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [m          (du/getv el k-model)
          now        (js/performance.now)
          last-frame (du/getv el k-last-frame)
          dt         (/ (js/Math.min (- now last-frame) 100.0) 1000.0)]

      (du/setv! el k-last-frame now)

      ;; Update time (for indeterminate oscillation)
      (du/setv! el k-time (+ (du/getv el k-time) dt))

      ;; Spring physics: smooth progress interpolation
      (let [target (if (:indeterminate? m)
                     ;; Oscillate between 30-70% for indeterminate
                     (+ 50.0 (* 20.0 (js/Math.sin (* (du/getv el k-time) 1.5))))
                     (:percent m))
            current  (du/getv el k-spring-pos)
            velocity (du/getv el k-spring-vel)
            result   (model/spring-step current target velocity dt 120.0 12.0)]
        (du/setv! el k-spring-pos (aget result 0))
        (du/setv! el k-spring-vel (aget result 1)))

      ;; Render visible segments
      (render-segments! el)

      ;; Check for completion
      (when (and (:complete? m) (not (du/getv el k-completed)))
        (du/setv! el k-completed true)
        (du/dispatch! el model/event-complete #js {:progress 100})
        ;; Start bloom if enabled
        (when (:bloom? m)
          (start-bloom! el)))

      ;; Reset completed flag if progress drops below 100
      (when (and (not (:complete? m)) (du/getv el k-completed)
                 (not (du/getv el k-bloom-active)))
        (du/setv! el k-completed false))

      ;; Update bloom animation if active
      (when (du/getv el k-bloom-active)
        (update-bloom! el dt))

      ;; Schedule next frame
      (du/setv! el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el)))))))

(defn- start-animation! [^js el]
  (du/setv! el k-time 0.0)
  (du/setv! el k-spring-pos 0.0)
  (du/setv! el k-spring-vel 0.0)
  (du/setv! el k-last-frame (js/performance.now))
  (du/setv! el k-completed false)
  (du/setv! el k-bloom-active false)
  (du/setv! el k-prev-visible -1)
  (du/setv! el k-raf
            (js/requestAnimationFrame (fn [_] (animate! el)))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv! el k-raf nil)))

;; ── Static render (for reduced motion) ─────────────────────────────────────

(defn- render-static! [^js el]
  (let [m     (du/getv el k-model)
        total (du/getv el k-segment-count)
        pct   (if (:indeterminate? m) 50.0 (:percent m))
        visible-count (js/Math.floor (* (/ pct 100.0) total))
        ^js lines (du/getv el k-lines)]
    (dotimes [i total]
      (let [^js line (aget lines i)]
        (when line
          (du/set-attr! line "opacity"
                         (if (< i visible-count) "1" "0")))))
    ;; Vine nodes: opacity
    (let [^js node-els  (du/getv el k-node-els)
          node-count    (or (du/getv el k-node-count) 0)]
      (when (and node-els (pos? node-count))
        (let [visible-nodes (js/Math.floor (* (/ pct 100.0) node-count))]
          (dotimes [i node-count]
            (let [^js c (aget node-els i)]
              (when c
                (du/set-attr! c "opacity"
                               (if (< i visible-nodes) "1" "0"))))))))
    (du/setv! el k-prev-visible visible-count)
    ;; Fire completion for static mode
    (when (and (:complete? m) (not (du/getv el k-completed)))
      (du/setv! el k-completed true)
      (du/dispatch! el model/event-complete #js {:progress 100}))))

;; ── Accessibility ───────────────────────────────────────────────────────────

(defn- set-a11y! [^js el m]
  (du/set-attr! el "role" "progressbar")
  (du/set-attr! el "aria-valuemin" "0")
  (du/set-attr! el "aria-valuemax" "100")
  (if (:indeterminate? m)
    (do (du/remove-attr! el "aria-valuenow")
        (du/set-attr! el "aria-busy" "true")
        (du/set-attr! el "aria-valuetext" "Loading..."))
    (do (du/set-attr! el "aria-valuenow" (str (js/Math.round (:percent m))))
        (du/remove-attr! el "aria-busy")
        (du/set-attr! el "aria-valuetext" (:aria-valuetext m))))
  (if-let [label (:label m)]
    (du/set-attr! el "aria-label" label)
    (du/remove-attr! el "aria-label")))

;; ── Update from attributes ──────────────────────────────────────────────────

(defn- structural-change?
  "Did variant/density/seed change between two model snapshots?"
  [old-m new-m]
  (or (nil? old-m)
      (not= (:variant new-m) (:variant old-m))
      (not= (:density new-m) (:density old-m))
      (not= (:seed    new-m) (:seed    old-m))))

(defn- apply-model!
  "Apply derived state to DOM. Regenerates segments only when structural
  fields (variant/density/seed) change; non-structural changes (progress,
  color, bloom, label) just re-apply line styles. Caches the new model at
  the tail."
  [^js el new-m old-m]
  (set-a11y! el new-m)
  (when (structural-change? old-m new-m)
    (regenerate-segments! el new-m)
    (du/setv! el k-prev-visible -1)
    (when (prefers-reduced-motion?)
      (render-static! el)))
  (apply-line-styles! el)
  (du/setv! el k-model new-m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m old-m))))

;; ── Property accessors ──────────────────────────────────────────────────────

(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto "progress" model/attr-progress model/parse-progress)
  (du/define-parsed-prop! proto "variant"  model/attr-variant  model/parse-variant)
  (du/define-parsed-prop! proto "color"    model/attr-color    model/parse-color)
  (du/define-parsed-prop! proto "density"  model/attr-density  model/parse-density)
  (du/define-parsed-prop! proto "seed"     model/attr-seed     model/parse-seed)
  (du/define-parsed-prop! proto "label"    model/attr-label    model/parse-label)

  ;; bloom is an opt-out boolean: absent attribute = true (default).
  ;; Setter writes "false" to disable, removes to re-enable.
  ;; define-parsed-prop! doesn't fit this inverted semantics.
  (.defineProperty js/Object proto "bloom"
    #js {:get (fn xop-get-bloom []
                (this-as ^js this
                  (model/parse-bloom (.getAttribute this model/attr-bloom))))
         :set (fn xop-set-bloom [v]
                (this-as ^js this
                  (if v
                    (du/remove-attr! this model/attr-bloom)
                    (du/set-attr! this model/attr-bloom "false"))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────

(defn- connected! [^js el]
  (ensure-refs! el)
  (update-from-attrs! el)
  (if (prefers-reduced-motion?)
    (render-static! el)
    (start-animation! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  ;; Clean up bloom elements
  (when (du/getv el k-bloom-active)
    (let [{:keys [blooms-g]} (du/getv el k-refs)
          ^js blooms-g blooms-g
          ^js bloom-els (du/getv el k-bloom-els)]
      (when bloom-els
        (dotimes [i (.-length bloom-els)]
          (let [^js c (aget bloom-els i)]
            (when (.-parentNode c)
              (.removeChild blooms-g c))))))
    (du/setv! el k-bloom-els nil)
    (du/setv! el k-bloom-active false)))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
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
