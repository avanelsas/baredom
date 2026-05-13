(ns baredom.components.x-progress-circle.x-progress-circle
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-progress-circle.model :as model]))

;; ── Constants ─────────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")
(def ^:private circumference 100.0)

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xProgressCircleInit")
(def ^:private k-model       "__xProgressCircleModel")
(def ^:private k-base        "__xProgressCircleBase")
(def ^:private k-fill        "__xProgressCircleFill")
(def ^:private k-value-node  "__xProgressCircleValueNode")
(def ^:private k-completed   "__xProgressCircleCompleted")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-flex;"
   "color-scheme:light dark;"
   "box-sizing:border-box;"
   "--x-progress-circle-size:64px;"
   "--x-progress-circle-track-color:rgba(0,0,0,0.10);"
   "--x-progress-circle-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-progress-circle-stroke-width:2.8;"
   "--x-progress-circle-value-color:rgba(0,0,0,0.50);"
   "--x-progress-circle-transition-duration:var(--x-transition-duration,0.3s);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-progress-circle-track-color:rgba(255,255,255,0.12);"
   "--x-progress-circle-value-color:rgba(255,255,255,0.50);}}"

   ;; Sizes
   "[part=base][data-size='sm']{--x-progress-circle-size:40px;}"
   "[part=base][data-size='md']{--x-progress-circle-size:64px;}"
   "[part=base][data-size='lg']{--x-progress-circle-size:96px;}"

   ;; Variant fill-color overrides
   "[part=base][data-variant='success']{--x-progress-circle-fill-color:var(--x-color-success,#22c55e);}"
   "[part=base][data-variant='warning']{--x-progress-circle-fill-color:var(--x-color-warning,#f59e0b);}"
   "[part=base][data-variant='danger']{--x-progress-circle-fill-color:var(--x-color-danger,#ef4444);}"

   ;; Base wrapper
   "[part=base]{"
   "position:relative;"
   "display:inline-flex;"
   "width:var(--x-progress-circle-size);"
   "height:var(--x-progress-circle-size);}"

   ;; SVG
   "[part=svg]{"
   "width:100%;"
   "height:100%;}"

   ;; Track circle
   "[part=track]{"
   "fill:none;"
   "stroke:var(--x-progress-circle-track-color);"
   "stroke-width:var(--x-progress-circle-stroke-width);}"

   ;; Fill circle
   "[part=fill]{"
   "fill:none;"
   "stroke:var(--x-progress-circle-fill-color);"
   "stroke-width:var(--x-progress-circle-stroke-width);"
   "stroke-linecap:round;"
   "transform:rotate(-90deg);"
   "transform-origin:center;"
   "transition:stroke-dashoffset var(--x-progress-circle-transition-duration) ease;}"

   ;; Center overlay
   "[part=center]{"
   "position:absolute;"
   "inset:0;"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;}"

   ;; Value text
   "[part=value-text]{"
   "font-size:calc(var(--x-progress-circle-size) * 0.22);"
   "color:var(--x-progress-circle-value-color);"
   "font-weight:500;"
   "line-height:1;}"

   ;; Indeterminate animation
   "@keyframes x-progress-circle-spin{"
   "from{transform:rotate(-90deg);}"
   "to{transform:rotate(270deg);}}"

   "[part=base][data-indeterminate='true'] [part=fill]{"
   "animation:x-progress-circle-spin 1.2s linear infinite;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=fill]{transition:none;}"
   "[part=base][data-indeterminate='true'] [part=fill]{"
   "animation:none;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        base       (.createElement js/document "div")
        svg        (.createElementNS js/document svg-ns "svg")
        track      (.createElementNS js/document svg-ns "circle")
        fill       (.createElementNS js/document svg-ns "circle")
        center     (.createElement js/document "div")
        value-node (.createElement js/document "span")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute base  "part" "base")
    (.setAttribute svg   "part" "svg")
    (.setAttribute svg   "viewBox" "0 0 36 36")
    (.setAttribute svg   "aria-hidden" "true")
    (.setAttribute svg   "focusable" "false")
    (.setAttribute track "part" "track")
    (.setAttribute track "cx" "18")
    (.setAttribute track "cy" "18")
    (.setAttribute track "r"  "15.9155")
    (.setAttribute fill  "part" "fill")
    (.setAttribute fill  "cx" "18")
    (.setAttribute fill  "cy" "18")
    (.setAttribute fill  "r"  "15.9155")
    (.setAttribute center     "part" "center")
    (.setAttribute value-node "part" "value-text")
    (.appendChild svg    track)
    (.appendChild svg    fill)
    (.appendChild center value-node)
    (.appendChild base   svg)
    (.appendChild base   center)
    (.appendChild root   style-el)
    (.appendChild root   base)
    (gobj/set el k-base       base)
    (gobj/set el k-fill       fill)
    (gobj/set el k-value-node value-node)
    (gobj/set el k-initialized true)))

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:value         (du/get-attr el model/attr-value)
    :max           (du/get-attr el model/attr-max)
    :variant       (du/get-attr el model/attr-variant)
    :size          (du/get-attr el model/attr-size)
    :label         (du/get-attr el model/attr-label)
    :show-value    (du/has-attr? el model/attr-show-value)
    :indeterminate (du/has-attr? el model/attr-indeterminate)}))

;; ── Apply model (cache-at-tail render-pipeline) ───────────────────────────
(defn- apply-model! [^js el {:keys [value max percent variant size label
                                    show-value indeterminate aria-valuetext]
                             :as m}]
  (let [^js base       (gobj/get el k-base)
        ^js fill       (gobj/get el k-fill)
        ^js value-node (gobj/get el k-value-node)
        was-completed  (gobj/get el k-completed)
        now-complete   (and (not indeterminate) (>= value max))
        offset         (if indeterminate
                         (* circumference 0.75)
                         (* circumference (- 1 (/ percent 100))))]

    ;; Data attributes on base — drive CSS
    (.setAttribute base "data-variant"       variant)
    (.setAttribute base "data-size"          size)
    (.setAttribute base "data-indeterminate" (if indeterminate "true" "false"))

    ;; SVG stroke attributes on fill circle
    (.setAttribute fill "stroke-dasharray"  (str circumference))
    (.setAttribute fill "stroke-dashoffset" (str offset))

    ;; Value text
    (set! (.-textContent value-node)
          (if (and show-value (not indeterminate))
            (str (js/Math.round percent) "%")
            ""))
    (set! (.-display (.-style value-node))
          (if (and show-value (not indeterminate)) "" "none"))

    ;; ARIA on host
    (du/set-attr! el "role"          "progressbar")
    (du/set-attr! el "aria-valuemin" "0")
    (if indeterminate
      (do
        (du/remove-attr! el "aria-valuenow")
        (.setAttribute    el "aria-busy"      "true")
        (.setAttribute    el "aria-valuetext" aria-valuetext))
      (do
        (du/set-attr! el "aria-valuenow"  (str value))
        (du/set-attr! el "aria-valuemax"  (str max))
        (du/set-attr! el "aria-valuetext" aria-valuetext)
        (du/remove-attr! el "aria-busy")))

    (if (some? label)
      (du/set-attr! el "aria-label" label)
      (du/remove-attr! el "aria-label"))

    ;; x-progress-circle-complete event
    (when (and now-complete (not was-completed))
      (du/dispatch! el model/event-complete #js {:value value :max max}))
    (gobj/set el k-completed (boolean now-complete))
    (gobj/set el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (update-from-attrs! el))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-value
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-value) "0")))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-value (str v))))
                        :enumerable true :configurable true})
  (.defineProperty js/Object proto model/attr-max
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-max) "100")))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-max (str v))))
                        :enumerable true :configurable true})
  (du/define-bool-prop! proto "indeterminate" model/attr-indeterminate)
  (du/define-bool-prop! proto "showValue"     model/attr-show-value))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
