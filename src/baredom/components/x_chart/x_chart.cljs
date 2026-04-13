(ns baredom.components.x-chart.x-chart
  (:require [baredom.components.x-chart.model :as model]
            [goog.object :as gobj]))

;; ---- Instance field keys ----

(def ^:private k-refs     "__xChartRefs")
(def ^:private k-handlers "__xChartHandlers")
(def ^:private k-data     "__xChartData")

;; ---- SVG namespace ----

(def ^:private svg-ns "http://www.w3.org/2000/svg")

(defn- make-el [tag]
  (.createElement js/document tag))

(defn- make-svg-el [tag]
  (.createElementNS js/document svg-ns tag))

;; ---- Styles ----

(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "contain:content;"
   "--x-chart-series-1:rgba(0,102,204,0.95);"
   "--x-chart-series-2:rgba(16,140,72,0.95);"
   "--x-chart-series-3:rgba(190,20,40,0.95);"
   "--x-chart-series-4:rgba(204,120,0,0.95);"
   "--x-chart-border:var(--x-color-border,rgba(0,0,0,0.1));"
   "--x-chart-grid:var(--x-color-border,rgba(0,0,0,0.08));"
   "--x-chart-axis-label:var(--x-color-text-muted,rgba(0,0,0,0.5));"
   "--x-chart-skeleton:linear-gradient(90deg,rgba(0,0,0,0.06) 25%,rgba(0,0,0,0.03) 50%,rgba(0,0,0,0.06) 75%);"
   "--x-chart-tooltip-bg:var(--x-color-bg,rgba(255,255,255,0.96));"
   "--x-chart-tooltip-border:var(--x-color-border,rgba(0,0,0,0.12));"
   "--x-chart-tooltip-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.14));"
   "--x-chart-focus-ring:var(--x-color-focus-ring,rgba(0,102,204,0.55));"
   "--x-chart-radius:var(--x-radius-md,0.75rem);"
   "--x-chart-tooltip-radius:var(--x-radius-sm,0.5rem);"
   "--x-chart-tooltip-padding:0.45rem 0.7rem;"
   "--x-chart-tooltip-font-size:0.8125rem;"
   "--x-chart-tooltip-header-color:var(--x-color-text-muted,rgba(0,0,0,0.5));"
   "--x-chart-tooltip-label-color:var(--x-color-text-muted,rgba(0,0,0,0.65));"
   "--x-chart-tooltip-value-color:var(--x-color-text,rgba(0,0,0,0.9));"
   "--x-chart-tooltip-swatch-size:8px;"
   "--x-chart-tooltip-gap:0.35rem;"
   "--x-chart-crosshair-color:var(--x-color-border,rgba(0,0,0,0.18));"
   "--x-chart-crosshair-width:1;"
   "}"
   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-chart-series-1:rgba(120,190,255,0.95);"
   "--x-chart-series-2:rgba(80,230,150,0.92);"
   "--x-chart-series-3:rgba(255,100,110,0.93);"
   "--x-chart-series-4:rgba(255,190,60,0.93);"
   "--x-chart-border:var(--x-color-border,rgba(255,255,255,0.1));"
   "--x-chart-grid:var(--x-color-border,rgba(255,255,255,0.08));"
   "--x-chart-axis-label:var(--x-color-text-muted,rgba(255,255,255,0.45));"
   "--x-chart-skeleton:linear-gradient(90deg,rgba(255,255,255,0.06) 25%,rgba(255,255,255,0.03) 50%,rgba(255,255,255,0.06) 75%);"
   "--x-chart-tooltip-bg:var(--x-color-bg,rgba(30,30,35,0.97));"
   "--x-chart-tooltip-border:var(--x-color-border,rgba(255,255,255,0.12));"
   "--x-chart-tooltip-shadow:var(--x-shadow-md,0 4px 20px rgba(0,0,0,0.45));"
   "--x-chart-focus-ring:var(--x-color-focus-ring,rgba(120,190,255,0.55));"
   "--x-chart-tooltip-header-color:var(--x-color-text-muted,rgba(255,255,255,0.45));"
   "--x-chart-tooltip-label-color:var(--x-color-text-muted,rgba(255,255,255,0.6));"
   "--x-chart-tooltip-value-color:var(--x-color-text,rgba(255,255,255,0.9));"
   "--x-chart-crosshair-color:var(--x-color-border,rgba(255,255,255,0.2));"
   "}"
   "}"
   "[part=container]{"
   "position:relative;"
   "border:1px solid var(--x-chart-border);"
   "border-radius:var(--x-chart-radius);"
   "overflow:hidden;"
   "outline:none;"
   "}"
   "[part=container]:focus-visible{"
   "box-shadow:0 0 0 3px var(--x-chart-focus-ring);"
   "}"
   "[part=svg]{display:block;width:100%;height:auto;overflow:visible;}"
   ":host([loading]) [part=container]{"
   "background:var(--x-chart-skeleton);"
   "background-size:200% 100%;"
   "animation:x-chart-shimmer 1.4s ease infinite;"
   "}"
   "@keyframes x-chart-shimmer{"
   "0%{background-position:200% 0;}"
   "100%{background-position:-200% 0;}"
   "}"
   "@media(prefers-reduced-motion:reduce){"
   ":host([loading]) [part=container]{animation:none;}"
   "}"
   "[part=sr-only]{"
   "position:absolute;width:1px;height:1px;padding:0;"
   "margin:-1px;overflow:hidden;clip:rect(0,0,0,0);"
   "white-space:nowrap;border:0;"
   "}"
   "[part=tooltip]{"
   "visibility:hidden;"
   "opacity:0;"
   "position:absolute;"
   "pointer-events:none;"
   "background:var(--x-chart-tooltip-bg);"
   "border:1px solid var(--x-chart-tooltip-border);"
   "box-shadow:var(--x-chart-tooltip-shadow);"
   "border-radius:var(--x-chart-tooltip-radius);"
   "padding:var(--x-chart-tooltip-padding);"
   "font-size:var(--x-chart-tooltip-font-size);"
   "line-height:1.5;"
   "white-space:nowrap;"
   "z-index:10;"
   "}"
   "[part=tooltip][data-visible=true]{visibility:visible;opacity:1;}"
   "@media(prefers-reduced-motion:no-preference){"
   "[part=tooltip]{transition:opacity 0.1s ease,left 0.08s ease,top 0.08s ease;}"
   "}"
   "[part=tooltip-header]{"
   "font-size:0.75rem;"
   "color:var(--x-chart-tooltip-header-color);"
   "margin-bottom:0.25rem;"
   "}"
   "[part=tooltip-body]{display:flex;flex-direction:column;gap:var(--x-chart-tooltip-gap);}"
   "[part=tooltip-row]{display:flex;align-items:center;gap:0.4rem;}"
   "[part=tooltip-row][data-hidden=true]{display:none;}"
   "[part=tooltip-swatch]{"
   "display:inline-block;"
   "width:var(--x-chart-tooltip-swatch-size);"
   "height:var(--x-chart-tooltip-swatch-size);"
   "border-radius:50%;"
   "flex-shrink:0;"
   "}"
   "[part=tooltip-label]{color:var(--x-chart-tooltip-label-color);}"
   "[part=tooltip-value]{color:var(--x-chart-tooltip-value-color);font-weight:600;margin-left:auto;padding-left:0.5rem;}"))

;; ---- Read model from element ----

(defn- has-attr? [^js el attr] (.hasAttribute el attr))
(defn- get-attr  [^js el attr] (.getAttribute  el attr))

(defn- read-model [^js el]
  (model/normalize
   {:type-raw         (get-attr el model/attr-type)
    :data-raw         (get-attr el model/attr-data)
    :height-raw       (get-attr el model/attr-height)
    :padding-raw      (get-attr el model/attr-padding)
    :grid-present?    (when (has-attr? el model/attr-grid)
                        (.getAttribute el model/attr-grid))
    :axes-raw         (when (has-attr? el model/attr-axes)
                        (.getAttribute el model/attr-axes))
    :tooltip-present? (when (has-attr? el model/attr-tooltip)
                        (.getAttribute el model/attr-tooltip))
    :cursor-raw       (get-attr el model/attr-cursor)
    :disabled-present? (when (has-attr? el model/attr-disabled)
                         (.getAttribute el model/attr-disabled))
    :loading-present?  (when (has-attr? el model/attr-loading)
                         (.getAttribute el model/attr-loading))
    :selected-raw      (get-attr el model/attr-selected)
    :x-format-raw      (get-attr el model/attr-x-format)
    :y-format-raw      (get-attr el model/attr-y-format)}))

;; ---- Event dispatch ----

(defn- dispatch! [^js el event-name cancelable? detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail    detail
         :bubbles   true
         :composed  true
         :cancelable cancelable?})))

;; ---- SVG coordinate math ----

(defn- label-width [] 36)
(defn- label-height [] 18)

(defn- plot-bounds [W H padding]
  (let [lw (label-width)
        lh (label-height)
        x0 (+ padding lw)
        y0 padding
        x1 (- W padding)
        y1 (- H padding lh)]
    {:x0 x0 :y0 y0 :x1 x1 :y1 y1}))

(defn- scale-y [y [mn mx] y0 y1]
  (let [span (- mx mn)]
    (if (zero? span)
      (/ (+ y0 y1) 2)
      (+ y0 (* (- y1 y0) (/ (- mx y) span))))))

(defn- scale-x-numeric [x [mn mx] x0 x1]
  (let [span (- mx mn)]
    (if (zero? span)
      (/ (+ x0 x1) 2)
      (+ x0 (* (/ (- x mn) span) (- x1 x0))))))

(defn- scale-x-category [i n x0 x1]
  (if (<= n 1)
    (/ (+ x0 x1) 2)
    (+ x0 (* (/ i (dec n)) (- x1 x0)))))

;; ---- SVG element helpers ----

(defn- set-attr! [^js el attr val]
  (.setAttribute el attr val))

(defn- svg-line! [x1 y1 x2 y2 stroke opacity]
  (let [^js el (make-svg-el "line")]
    (set-attr! el "x1" (str x1))
    (set-attr! el "y1" (str y1))
    (set-attr! el "x2" (str x2))
    (set-attr! el "y2" (str y2))
    (set-attr! el "stroke" stroke)
    (set-attr! el "stroke-opacity" (str opacity))
    (set-attr! el "stroke-width" "1")
    el))

(defn- svg-text! [x y text anchor font-size fill]
  (let [^js el (make-svg-el "text")]
    (set-attr! el "x" (str x))
    (set-attr! el "y" (str y))
    (set-attr! el "text-anchor" anchor)
    (set-attr! el "font-size" (str font-size))
    (set-attr! el "fill" fill)
    (set! (.-textContent el) text)
    el))

(defn- series-color-var [idx]
  (str "var(--x-chart-series-" (inc (mod idx 4)) ")"))

;; ---- Path builders ----

(defn- build-line-d [pts]
  (when (seq pts)
    (let [[f & rest-pts] pts]
      (str "M " (:px f) "," (:py f)
           (apply str (map (fn [p] (str " L " (:px p) "," (:py p))) rest-pts))))))

(defn- build-area-d [pts baseline-y]
  (when (seq pts)
    (let [line-d (build-line-d pts)
          last-p (last pts)
          first-p (first pts)]
      (str line-d
           " L " (:px last-p) "," baseline-y
           " L " (:px first-p) "," baseline-y
           " Z"))))

;; ---- Draw helpers ----

(defn- remove-children! [^js el]
  (loop []
    (when-let [c (.-lastChild el)]
      (.removeChild el c)
      (recur))))

(defn- draw-grid! [^js svg {:keys [y-ticks y-domain]} {:keys [x0 y0 x1 y1]}]
  (doseq [tick y-ticks]
    (let [py (scale-y tick y-domain y0 y1)
          ^js line (svg-line! x0 py x1 py "var(--x-chart-grid)" 1)]
      (.appendChild svg line))))

(defn- draw-axes! [^js svg {:keys [y-ticks y-domain series x-kind y-fmt x-fmt]} {:keys [x0 y0 x1 y1]} padding]
  ;; Y-axis labels
  (doseq [tick y-ticks]
    (let [py  (scale-y tick y-domain y0 y1)
          lbl (model/format-value tick y-fmt)
          ^js txt (svg-text! (- x0 4) (+ py 4) lbl "end" 10 "var(--x-chart-axis-label)")]
      (.appendChild svg txt)))
  ;; X-axis labels — only for first series
  (when-let [s (first series)]
    (let [pts  (:data s)
          n    (count pts)
          xdom (model/domain-x series)
          step (max 1 (int (js/Math.ceil (/ n 5))))]
      (doall
        (map-indexed
         (fn [i pt]
           (when (zero? (mod i step))
             (let [px (if (= x-kind "category")
                        (scale-x-category i (- (second xdom) (first xdom)) x0 x1)
                        (scale-x-numeric (:x pt) xdom x0 x1))
                   label (if (= x-kind "category")
                           (str (:x pt))
                           (model/format-value (:x pt) x-fmt))
                   ^js txt (svg-text! px (+ y1 padding 4) label "middle" 10 "var(--x-chart-axis-label)")]
               (.appendChild svg txt))))
         pts)))))

(defn- compute-series-pts [s series-idx kind x-dom {:keys [x0 y0 x1 y1]} y-domain]
  (map-indexed
   (fn [i pt]
     (let [px (if (= kind "category")
                (let [n (max 1 (count (:data s)))]
                  (scale-x-category i (dec n) x0 x1))
                (scale-x-numeric (:x pt) x-dom x0 x1))
           py (scale-y (:y pt) y-domain y0 y1)]
       (assoc pt :px px :py py :si series-idx :i i)))
   (:data s)))

(defn- draw-line-series! [^js svg _series-idx pts color]
  (when-let [d (build-line-d pts)]
    (let [^js path (make-svg-el "path")]
      (set-attr! path "d" d)
      (set-attr! path "fill" "none")
      (set-attr! path "stroke" color)
      (set-attr! path "stroke-width" "2")
      (set-attr! path "stroke-linejoin" "round")
      (set-attr! path "stroke-linecap" "round")
      (.appendChild svg path))))

(defn- draw-area-series! [^js svg _series-idx pts color baseline-y]
  (when-let [d (build-area-d pts baseline-y)]
    (let [^js path (make-svg-el "path")]
      (set-attr! path "d" d)
      (set-attr! path "fill" color)
      (set-attr! path "fill-opacity" "0.18")
      (set-attr! path "stroke" color)
      (set-attr! path "stroke-width" "2")
      (set-attr! path "stroke-linejoin" "round")
      (set-attr! path "stroke-linecap" "round")
      (.appendChild svg path))))

(defn- draw-bar-series! [^js svg series-idx pts color {:keys [x0 x1]} total-series]
  (let [n      (count pts)
        bw-raw (if (zero? n) 0 (/ (- x1 x0) n))
        gap    (* bw-raw 0.08)
        sw     (/ (- bw-raw (* gap 2)) total-series)
        offset (* series-idx sw)]
    (doseq [pt pts]
      (let [bx (+ (:px pt) (- (/ bw-raw 2)) gap offset)
            by (min (:py pt) (:y0-baseline pt))
            bh (js/Math.abs (- (:py pt) (:y0-baseline pt)))
            ^js rect (make-svg-el "rect")]
        (set-attr! rect "x" (str bx))
        (set-attr! rect "y" (str by))
        (set-attr! rect "width" (str (max 1 sw)))
        (set-attr! rect "height" (str (max 0 bh)))
        (set-attr! rect "fill" color)
        (set-attr! rect "rx" "2")
        (.appendChild svg rect)))))

;; ---- Tooltip ----

(defn- build-hover-data
  "Build hover-data map from a seq of hit points, x/y format specs, and series vec."
  [pts x-fmt y-fmt series]
  (when (seq pts)
    (let [first-pt  (first pts)
          x-label   (if (string? (:x first-pt))
                      (str (:x first-pt))
                      (model/format-value (:x first-pt) x-fmt))
          ;; series color lookup by id
          color-map (into {} (map-indexed (fn [i s] [(:id s) (or (:color s) (series-color-var i))]) series))
          rows      (mapv (fn [pt]
                            {:color  (get color-map (:id pt) (series-color-var (:si pt 0)))
                             :label  (str (:label pt))
                             :value  (model/format-value (:y pt) y-fmt)
                             :px     (:px pt)
                             :py     (:py pt)})
                          pts)]
      {:x-label  x-label
       :rows     rows
       :px       (:px first-pt)
       :py       (:py first-pt)
       :dot-pts  (mapv (fn [pt]
                         {:px    (:px pt)
                          :py    (:py pt)
                          :color (get color-map (:id pt) (series-color-var (:si pt 0)))})
                       pts)})))

(defn- show-tooltip! [^js refs hover-data W H]
  (let [^js tooltip-el (gobj/get refs "tooltip")]
    (when (and tooltip-el hover-data)
      (let [{:keys [x-label rows px py dot-pts]} hover-data
            ^js header (gobj/get refs "tooltip-header")
            ^js body   (gobj/get refs "tooltip-body")
            row-els    (gobj/get refs "tooltip-rows")
            dot-els    (gobj/get refs "dots")
            ^js xhair  (gobj/get refs "crosshair")]

        ;; Update header
        (when header (set! (.-textContent header) x-label))

        ;; Update rows — show used, hide rest
        (when (and body row-els)
          (dotimes [i model/max-tooltip-rows]
            (let [^js row-el (aget row-els i)]
              (when row-el
                (if-let [row (nth rows i nil)]
                  (do
                    (set-attr! row-el "data-hidden" "false")
                    (let [^js swatch (.-firstChild row-el)
                          ^js label  (when swatch (.-nextSibling swatch))
                          ^js value  (when label  (.-nextSibling label))]
                      (when swatch (set! (.. swatch -style -backgroundColor) (:color row)))
                      (when label  (set! (.-textContent label) (:label row)))
                      (when value  (set! (.-textContent value) (:value row)))))
                  (set-attr! row-el "data-hidden" "true"))))))

        ;; Update dot positions and visibility
        (when dot-els
          (dotimes [i model/max-tooltip-rows]
            (let [^js dot (aget dot-els i)]
              (when dot
                (if-let [dp (nth dot-pts i nil)]
                  (do
                    (set-attr! dot "cx" (str (:px dp)))
                    (set-attr! dot "cy" (str (:py dp)))
                    (set-attr! dot "fill" (:color dp))
                    (set-attr! dot "visibility" "visible"))
                  (set-attr! dot "visibility" "hidden"))))))

        ;; Show crosshair
        (when xhair (set-attr! xhair "visibility" "visible"))
        (when xhair (set-attr! xhair "x1" (str px)))
        (when xhair (set-attr! xhair "x2" (str px)))

        ;; Position and show tooltip
        (set-attr! tooltip-el "data-visible" "true")
        (let [tw (.-offsetWidth tooltip-el)
              th (.-offsetHeight tooltip-el)
              {:keys [left top]} (model/tooltip-position px py tw th W H
                                                         model/tooltip-edge-pad
                                                         model/tooltip-offset)]
          (set! (.. tooltip-el -style -left) (str left "px"))
          (set! (.. tooltip-el -style -top)  (str top "px")))))))

(defn- hide-tooltip! [^js refs]
  (let [^js tooltip-el (gobj/get refs "tooltip")
        ^js xhair      (gobj/get refs "crosshair")
        dot-els        (gobj/get refs "dots")]
    (when tooltip-el (set-attr! tooltip-el "data-visible" "false"))
    (when xhair      (set-attr! xhair "visibility" "hidden"))
    (when dot-els
      (dotimes [i model/max-tooltip-rows]
        (let [^js dot (aget dot-els i)]
          (when dot (set-attr! dot "visibility" "hidden")))))))

;; ---- Hit area & mouse interaction ----

(defn- find-nearest-pt [all-pts mx my]
  (reduce
   (fn [best pt]
     (let [dx (- (:px pt) mx)
           dy (- (:py pt) my)
           d  (+ (* dx dx) (* dy dy))]
       (if (or (nil? best) (< d (:d best)))
         (assoc pt :d d)
         best)))
   nil
   all-pts))

(defn- add-mouse-listeners! [^js el ^js hit-el all-pts ^js refs W H cursor show-tooltip? x-fmt y-fmt series]
  (when-not (= cursor "none")
    (.addEventListener
     hit-el "pointermove"
     (fn [^js ev]
       (let [rect (.getBoundingClientRect hit-el)
             mx   (- (.-clientX ev) (.-x rect))
             my   (- (.-clientY ev) (.-y rect))
             pts  (if (= cursor "x")
                    (model/find-pts-at-x all-pts mx)
                    (let [p (find-nearest-pt all-pts mx my)]
                      (when p [p])))
             pt   (first pts)]
         (when pt
           (when show-tooltip?
             (let [hd (build-hover-data pts x-fmt y-fmt series)]
               (show-tooltip! refs hd W H)))
           (dispatch! el model/event-hover false
                      #js {:seriesId (:id pt)
                           :index    (:i pt)
                           :x        (:x pt)
                           :y        (:y pt)
                           :value    (:y pt)})))))
    (.addEventListener
     hit-el "pointerleave"
     (fn [_]
       (when show-tooltip? (hide-tooltip! refs))))
    (.addEventListener
     hit-el "click"
     (fn [^js ev]
       (let [rect (.getBoundingClientRect hit-el)
             mx   (- (.-clientX ev) (.-x rect))
             my   (- (.-clientY ev) (.-y rect))
             pt   (find-nearest-pt all-pts mx my)]
         (when pt
           (dispatch! el model/event-select false
                      #js {:seriesId (:id pt)
                           :index    (:i pt)
                           :x        (:x pt)
                           :y        (:y pt)
                           :value    (:y pt)})))))))

;; ---- Keyboard navigation ----

(defn- add-keyboard-listener! [^js el ^js container]
  (.addEventListener
   container "keydown"
   (fn [^js ev]
     (let [key (.-key ev)
           m   (read-model el)
           {:keys [series disabled? selected cursor]} m]
       (when (and (not disabled?) (not= cursor "none") (seq series))
         (let [series-v (vec series)]
           (if (nil? selected)
             ;; No selection yet: any arrow key initializes at s[0]:0
             (when (#{"ArrowRight" "ArrowLeft" "ArrowDown" "ArrowUp"} key)
               (.preventDefault ev)
               (.setAttribute el model/attr-selected
                              (str (:id (first series-v)) ":0")))
             ;; Selection exists: navigate from current position
             (let [n-series (count series-v)
                   si       (or (first (keep-indexed
                                        (fn [i s] (when (= (:id s) (:series-id selected)) i))
                                        series-v))
                                0)
                   s        (nth series-v si)
                   n-pts    (count (:data s))
                   pt-i     (:index selected)]
               (case key
                 "ArrowRight"
                 (do (.preventDefault ev)
                   (let [new-i (min (dec n-pts) (inc pt-i))]
                     (.setAttribute el model/attr-selected (str (:id s) ":" new-i))))
                 "ArrowLeft"
                 (do (.preventDefault ev)
                   (let [new-i (max 0 (dec pt-i))]
                     (.setAttribute el model/attr-selected (str (:id s) ":" new-i))))
                 "ArrowDown"
                 (do (.preventDefault ev)
                   (let [new-si (mod (inc si) n-series)
                         new-s  (nth series-v new-si)
                         new-i  (min pt-i (dec (max 1 (count (:data new-s)))))]
                     (.setAttribute el model/attr-selected (str (:id new-s) ":" new-i))))
                 "ArrowUp"
                 (do (.preventDefault ev)
                   (let [new-si (mod (+ si (dec n-series)) n-series)
                         new-s  (nth series-v new-si)
                         new-i  (min pt-i (dec (max 1 (count (:data new-s)))))]
                     (.setAttribute el model/attr-selected (str (:id new-s) ":" new-i))))
                 ("Enter" " ")
                 (do (.preventDefault ev)
                   (let [pt (nth (:data s) pt-i nil)]
                     (when pt
                       (dispatch! el model/event-select false
                                  #js {:seriesId (:id s)
                                       :index    pt-i
                                       :x        (:x pt)
                                       :y        (:y pt)
                                       :value    (:y pt)}))))
                 nil)))))))))

;; ---- Main render ----

(defn- chart-width [^js _el refs]
  (let [^js container (gobj/get refs "container")
        w (if container (.-clientWidth container) 0)]
    (if (pos? w) w 400)))

(defn- render! [^js el]
  (let [^js refs    (gobj/get el k-refs)]
    (when refs
      (let [^js svg    (gobj/get refs "svg")
            ^js sr-el  (gobj/get refs "sr")
            m          (read-model el)
            {:keys [type height padding series x-kind y-domain
                    grid? axes? tooltip? cursor loading? disabled? x-fmt y-fmt]} m
            W          (chart-width el refs)
            H          height
            bounds     (plot-bounds W H padding)
            {:keys [x0 y0 x1 y1]} bounds
            x-dom      (model/domain-x series)
            baseline-y (scale-y 0 y-domain y0 y1)]

        ;; Update viewBox
        (set-attr! svg "viewBox" (str "0 0 " W " " H))
        (set-attr! svg "width"   (str W))
        (set-attr! svg "height"  (str H))

        ;; Clear SVG (crosshair/dots are re-created each render)
        (remove-children! svg)
        (gobj/set refs "crosshair" nil)
        (gobj/set refs "dots"      nil)

        (when-not loading?
          ;; Grid
          (when grid? (draw-grid! svg m bounds))

          ;; Compute all point coords for all series
          (let [all-pts (vec
                         (mapcat
                          (fn [s i]
                            (let [pts (compute-series-pts s i x-kind x-dom bounds y-domain)]
                              (map (fn [pt] (assoc pt :id (:id s) :label (:label s) :y0-baseline baseline-y)) pts)))
                          series
                          (range)))]

            ;; Draw series
            (doall
              (map-indexed
               (fn [i s]
                 (let [color (or (:color s) (series-color-var i))
                       pts   (filter #(= (:id %) (:id s)) all-pts)]
                   (case type
                     "bar"  (draw-bar-series! svg i pts color bounds (count series))
                     "area" (do
                              (draw-area-series! svg i pts color baseline-y)
                              (draw-line-series! svg i pts color))
                     (draw-line-series! svg i pts color))))
               series))

            ;; Axes
            (when axes? (draw-axes! svg m bounds padding))

            ;; Crosshair line (hidden initially, shown on hover)
            (let [^js xhair (make-svg-el "line")]
              (set-attr! xhair "x1"           (str x0))
              (set-attr! xhair "y1"           (str y0))
              (set-attr! xhair "x2"           (str x0))
              (set-attr! xhair "y2"           (str y1))
              (set-attr! xhair "stroke"       "var(--x-chart-crosshair-color)")
              (set-attr! xhair "stroke-width" "var(--x-chart-crosshair-width)")
              (set-attr! xhair "visibility"   "hidden")
              (.appendChild svg xhair)
              (gobj/set refs "crosshair" xhair))

            ;; Dot indicators (one per max-tooltip-rows slots, hidden initially)
            (let [dot-arr (js/Array. model/max-tooltip-rows)]
              (dotimes [i model/max-tooltip-rows]
                (let [^js dot (make-svg-el "circle")]
                  (set-attr! dot "r"          (str model/dot-r))
                  (set-attr! dot "stroke"     "var(--x-chart-tooltip-bg)")
                  (set-attr! dot "stroke-width" "2")
                  (set-attr! dot "visibility" "hidden")
                  (.appendChild svg dot)
                  (aset dot-arr i dot)))
              (gobj/set refs "dots" dot-arr))

            ;; Hit area (on top of crosshair/dots)
            (let [^js hit (make-svg-el "rect")]
              (set-attr! hit "x"      (str x0))
              (set-attr! hit "y"      (str y0))
              (set-attr! hit "width"  (str (max 0 (- x1 x0))))
              (set-attr! hit "height" (str (max 0 (- y1 y0))))
              (set-attr! hit "fill"   "transparent")
              (set-attr! hit "style"  "cursor:crosshair;")
              (when-not disabled?
                (add-mouse-listeners! el hit all-pts refs W H cursor
                                      tooltip? x-fmt y-fmt series))
              (.appendChild svg hit)))

          ;; SR text — announce selected point when present, else series count
          (let [sel (:selected m)]
            (if (and sel (seq series))
              (let [s  (first (filter #(= (:id %) (:series-id sel)) series))
                    pt (when s (nth (:data s) (:index sel) nil))]
                (set! (.-textContent sr-el)
                  (if (and s pt)
                    (str (:label s) " "
                         (model/format-value (:y pt) y-fmt)
                         " at "
                         (if (string? (:x pt))
                           (:x pt)
                           (model/format-value (:x pt) x-fmt)))
                    (str (count series) " series chart"))))
              (set! (.-textContent sr-el)
                (str (count series) " series chart")))))

        ;; Hide tooltip/indicators on loading
        (when loading? (hide-tooltip! refs))))))

;; ---- Shadow DOM creation ----

(defn- make-tooltip-row! []
  (let [^js row    (make-el "div")
        ^js swatch (make-el "span")
        ^js label  (make-el "span")
        ^js value  (make-el "span")]
    (set-attr! row    "part"       "tooltip-row")
    (set-attr! row    "data-hidden" "true")
    (set-attr! swatch "part"       "tooltip-swatch")
    (set-attr! label  "part"       "tooltip-label")
    (set-attr! value  "part"       "tooltip-value")
    (.appendChild row swatch)
    (.appendChild row label)
    (.appendChild row value)
    row))

(defn- make-shadow! [^js el]
  (let [^js root       (.attachShadow el #js {:mode "open"})
        ^js style-el   (make-el "style")
        ^js container  (make-el "div")
        ^js svg        (make-svg-el "svg")
        ^js sr-el      (make-el "div")
        ^js tooltip-el (make-el "div")
        ^js header-el  (make-el "div")
        ^js body-el    (make-el "div")
        row-arr        (js/Array. model/max-tooltip-rows)]

    (set! (.-textContent style-el) style-text)

    (set-attr! container "part"     "container")
    (set-attr! container "tabindex" "0")

    (set-attr! svg "part"        "svg")
    (set-attr! svg "aria-hidden" "true")
    (set-attr! svg "role"        "img")

    (set-attr! sr-el "part"      "sr-only")
    (set-attr! sr-el "role"      "status")
    (set-attr! sr-el "aria-live" "polite")

    (set-attr! tooltip-el "part"         "tooltip")
    (set-attr! tooltip-el "role"         "tooltip")
    (set-attr! tooltip-el "aria-hidden"  "true")
    (set-attr! tooltip-el "data-visible" "false")

    (set-attr! header-el "part" "tooltip-header")
    (set-attr! body-el   "part" "tooltip-body")

    ;; Pre-build max-tooltip-rows row elements
    (dotimes [i model/max-tooltip-rows]
      (let [^js row (make-tooltip-row!)]
        (.appendChild body-el row)
        (aset row-arr i row)))

    (.appendChild tooltip-el header-el)
    (.appendChild tooltip-el body-el)

    (.appendChild container svg)
    (.appendChild container sr-el)
    (.appendChild container tooltip-el)

    (.appendChild root style-el)
    (.appendChild root container)

    (let [refs #js {}]
      (gobj/set refs "root"           root)
      (gobj/set refs "container"      container)
      (gobj/set refs "svg"            svg)
      (gobj/set refs "sr"             sr-el)
      (gobj/set refs "tooltip"        tooltip-el)
      (gobj/set refs "tooltip-header" header-el)
      (gobj/set refs "tooltip-body"   body-el)
      (gobj/set refs "tooltip-rows"   row-arr)
      (gobj/set refs "crosshair"      nil)
      (gobj/set refs "dots"           nil)
      (gobj/set el k-refs refs))

    ;; ResizeObserver
    (let [^js ro (js/ResizeObserver.
                  (fn [_]
                    (render! el)))]
      (.observe ro container)
      (gobj/set (gobj/get el k-refs) "ro" ro))

    ;; Keyboard navigation
    (add-keyboard-listener! el container)))

;; ---- Lifecycle ----

(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (render! el))

(defn- disconnected! [^js el]
  (let [^js refs (gobj/get el k-refs)]
    (when refs
      (let [^js ro (gobj/get refs "ro")]
        (when ro (.disconnect ro))))))

(defn- attribute-changed! [^js el _name _old _new]
  (when (gobj/get el k-refs)
    (render! el)))

;; ---- Property descriptors ----

(defn- define-str-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (.getAttribute this attr-name)))
        :set (fn [v]
               (this-as ^js this
                        (if (some? v)
                          (.setAttribute this attr-name (str v))
                          (.removeAttribute this attr-name))))}))

(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (.hasAttribute this attr-name)))
        :set (fn [v]
               (this-as ^js this
                        (if v
                          (.setAttribute this attr-name "")
                          (.removeAttribute this attr-name))))}))

(defn- define-num-prop! [^js proto prop-name attr-name default-val]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn []
               (this-as ^js this
                        (model/parse-int-pos (.getAttribute this attr-name) default-val)))
        :set (fn [v]
               (this-as ^js this
                        (if (some? v)
                          (.setAttribute this attr-name (str (int v)))
                          (.removeAttribute this attr-name))))}))

(defn- define-data-prop! [^js proto]
  (.defineProperty
   js/Object proto "data"
   #js {:configurable true
        :enumerable   true
        :get (fn []
               (this-as ^js this
                        (let [cached (gobj/get this k-data)]
                          (or cached
                              (let [raw (.getAttribute this model/attr-data)]
                                (model/parse-series-data raw))))))
        :set (fn [v]
               (this-as ^js this
                        (gobj/set this k-data v)
                        (if (array? v)
                          (.setAttribute this model/attr-data (js/JSON.stringify v))
                          (.removeAttribute this model/attr-data))))}))

;; ---- Element class + registration ----

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
      (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback (.-prototype klass))
      (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
      (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (let [proto (.-prototype klass)]
      (define-str-prop!  proto "type"     model/attr-type)
      (define-str-prop!  proto "cursor"   model/attr-cursor)
      (define-str-prop!  proto "selected" model/attr-selected)
      (define-num-prop!  proto "height"   model/attr-height  model/default-height)
      (define-num-prop!  proto "padding"  model/attr-padding model/default-padding)
      (define-bool-prop! proto "grid"     model/attr-grid)
      (define-bool-prop! proto "axes"     model/attr-axes)
      (define-bool-prop! proto "tooltip"  model/attr-tooltip)
      (define-bool-prop! proto "disabled" model/attr-disabled)
      (define-bool-prop! proto "loading"  model/attr-loading)
      (define-data-prop! proto))

    klass))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
