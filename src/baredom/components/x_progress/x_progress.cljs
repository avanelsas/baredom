(ns baredom.components.x-progress.x-progress
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-progress.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xProgressInit")
(def ^:private k-model       "__xProgressModel")
(def ^:private k-base        "__xProgressBase")
(def ^:private k-header      "__xProgressHeader")
(def ^:private k-fill        "__xProgressFill")
(def ^:private k-label-node  "__xProgressLabel")
(def ^:private k-value-node  "__xProgressValue")
(def ^:private k-completed   "__xProgressCompleted")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "box-sizing:border-box;"
   "--x-progress-border-radius:var(--x-radius-full,9999px);"
   "--x-progress-track-color:rgba(0,0,0,0.10);"
   "--x-progress-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-progress-label-color:rgba(0,0,0,0.60);"
   "--x-progress-value-color:rgba(0,0,0,0.50);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-progress-track-color:rgba(255,255,255,0.12);"
   "--x-progress-label-color:rgba(255,255,255,0.60);"
   "--x-progress-value-color:rgba(255,255,255,0.50);}}"

   ;; Size — sets height custom property
   "[part=base][data-size='sm']{--x-progress-height:4px;}"
   "[part=base][data-size='md']{--x-progress-height:8px;}"
   "[part=base][data-size='lg']{--x-progress-height:12px;}"

   ;; Variant fill-color overrides
   "[part=base][data-variant='success']{--x-progress-fill-color:var(--x-color-success,#22c55e);}"
   "[part=base][data-variant='warning']{--x-progress-fill-color:var(--x-color-warning,#f59e0b);}"
   "[part=base][data-variant='danger']{--x-progress-fill-color:var(--x-color-danger,#ef4444);}"

   ;; Base wrapper
   "[part=base]{"
   "width:100%;"
   "box-sizing:border-box;}"

   ;; Header row
   "[part=header]{"
   "display:flex;"
   "justify-content:space-between;"
   "align-items:baseline;"
   "margin-bottom:4px;}"

   "[part=label-text]{"
   "font-size:var(--x-font-size-sm,0.875rem);"
   "color:var(--x-progress-label-color);"
   "font-weight:500;}"

   "[part=value-text]{"
   "font-size:0.8125rem;"
   "color:var(--x-progress-value-color);}"

   ;; Track
   "[part=track]{"
   "height:var(--x-progress-height,8px);"
   "border-radius:var(--x-progress-border-radius,9999px);"
   "background:var(--x-progress-track-color);"
   "overflow:hidden;"
   "position:relative;}"

   ;; Fill
   "[part=fill]{"
   "height:100%;"
   "background:var(--x-progress-fill-color);"
   "border-radius:var(--x-progress-border-radius,9999px);"
   "transition:width 0.3s ease;"
   "width:0%;}"

   ;; Indeterminate animation
   "@keyframes x-progress-indeterminate{"
   "0%{transform:translateX(-100%) scaleX(0.5);}"
   "50%{transform:translateX(60%) scaleX(0.8);}"
   "100%{transform:translateX(200%) scaleX(0.5);}}"

   "[part=base][data-indeterminate='true'] [part=fill]{"
   "animation:x-progress-indeterminate 1.5s ease infinite;"
   "width:40%;"
   "transform-origin:left center;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=fill]{transition:none;}"
   "[part=base][data-indeterminate='true'] [part=fill]{"
   "animation:none;"
   "width:50%;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        base       (.createElement js/document "div")
        header     (.createElement js/document "div")
        label-node (.createElement js/document "span")
        value-node (.createElement js/document "span")
        track      (.createElement js/document "div")
        fill       (.createElement js/document "div")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute base       "part" "base")
    (.setAttribute header     "part" "header")
    (.setAttribute label-node "part" "label-text")
    (.setAttribute value-node "part" "value-text")
    (.setAttribute track      "part" "track")
    (.setAttribute fill       "part" "fill")
    (.appendChild header label-node)
    (.appendChild header value-node)
    (.appendChild track  fill)
    (.appendChild base   header)
    (.appendChild base   track)
    (.appendChild root   style-el)
    (.appendChild root   base)
    (du/setv! el k-base      base)
    (du/setv! el k-header    header)
    (du/setv! el k-fill      fill)
    (du/setv! el k-label-node label-node)
    (du/setv! el k-value-node value-node)
    (du/setv! el k-initialized true)))

;; ── Read model ────────────────────────────────────────────────────────────
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
  (let [^js base       (du/getv el k-base)
        ^js header     (du/getv el k-header)
        ^js fill       (du/getv el k-fill)
        ^js label-node (du/getv el k-label-node)
        ^js value-node (du/getv el k-value-node)
        show-header?   (or (some? label) show-value)
        was-completed  (du/getv el k-completed)
        now-complete   (and (not indeterminate) (>= value max))]

    ;; Data attributes on base — drive CSS
    (.setAttribute base "data-variant"      variant)
    (.setAttribute base "data-size"         size)
    (.setAttribute base "data-indeterminate" (if indeterminate "true" "false"))

    ;; Fill width
    (set! (.-width (.-style fill))
          (if indeterminate "40%" (str (.toFixed percent 2) "%")))

    ;; Header visibility
    (set! (.-display (.-style header))
          (if show-header? "flex" "none"))

    ;; Label text
    (set! (.-textContent label-node) (or label ""))
    (set! (.-display (.-style label-node))
          (if (some? label) "" "none"))

    ;; Value text
    (set! (.-textContent value-node)
          (if (and show-value (not indeterminate))
            (str (js/Math.round percent) "%")
            ""))
    (set! (.-display (.-style value-node))
          (if show-value "" "none"))

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

    ;; x-progress-complete event
    (when (and now-complete (not was-completed))
      (du/dispatch! el model/event-complete #js {:value value :max max}))
    (du/setv! el k-completed (boolean now-complete))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-initialized)
    (init-dom! el))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-initialized)
      (update-from-attrs! el))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-value   model/attr-value   "0")
  (du/define-string-prop! proto model/attr-max     model/attr-max     "100")
  (du/define-bool-prop!   proto "indeterminate"    model/attr-indeterminate)
  (du/define-bool-prop!   proto "showValue"        model/attr-show-value)
  (du/define-string-prop! proto model/attr-variant model/attr-variant "")
  (du/define-string-prop! proto model/attr-size    model/attr-size    "")
  (du/define-string-prop! proto model/attr-label   model/attr-label))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
