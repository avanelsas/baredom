(ns app.components.x-progress.x-progress
  (:require
   [goog.object :as gobj]
   [app.components.x-progress.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xProgressInit")
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
   "--x-progress-border-radius:9999px;"
   "--x-progress-track-color:rgba(0,0,0,0.10);"
   "--x-progress-fill-color:#3b82f6;"
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
   "[part=base][data-variant='success']{--x-progress-fill-color:#22c55e;}"
   "[part=base][data-variant='warning']{--x-progress-fill-color:#f59e0b;}"
   "[part=base][data-variant='danger']{--x-progress-fill-color:#ef4444;}"

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
   "font-size:0.875rem;"
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
    (gobj/set el k-base      base)
    (gobj/set el k-header    header)
    (gobj/set el k-fill      fill)
    (gobj/set el k-label-node label-node)
    (gobj/set el k-value-node value-node)
    (gobj/set el k-initialized true))
  nil)

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:value         (.getAttribute el model/attr-value)
   :max           (.getAttribute el model/attr-max)
   :variant       (.getAttribute el model/attr-variant)
   :size          (.getAttribute el model/attr-size)
   :label         (.getAttribute el model/attr-label)
   :show-value    (.hasAttribute el model/attr-show-value)
   :indeterminate (.hasAttribute el model/attr-indeterminate)})

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [value max percent variant size label
                show-value indeterminate aria-valuetext]}
        (model/derive-state (read-inputs el))
        ^js base       (gobj/get el k-base)
        ^js header     (gobj/get el k-header)
        ^js fill       (gobj/get el k-fill)
        ^js label-node (gobj/get el k-label-node)
        ^js value-node (gobj/get el k-value-node)
        show-header?   (or (some? label) show-value)
        was-completed  (gobj/get el k-completed)
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
    (.setAttribute el "role"          "progressbar")
    (.setAttribute el "aria-valuemin" "0")
    (if indeterminate
      (do
        (.removeAttribute el "aria-valuenow")
        (.setAttribute    el "aria-busy"      "true")
        (.setAttribute    el "aria-valuetext" aria-valuetext))
      (do
        (.setAttribute el "aria-valuenow"  (str value))
        (.setAttribute el "aria-valuemax"  (str max))
        (.setAttribute el "aria-valuetext" aria-valuetext)
        (.removeAttribute el "aria-busy")))

    (if (some? label)
      (.setAttribute el "aria-label" label)
      (.removeAttribute el "aria-label"))

    ;; x-progress-complete event
    (when (and now-complete (not was-completed))
      (.dispatchEvent el
                      (js/CustomEvent. model/event-complete
                                       #js {:bubbles true
                                            :composed true
                                            :detail #js {:value value :max max}})))
    (gobj/set el k-completed (boolean now-complete)))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (render! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (render! el)))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; value — string, reflects to attr
  (.defineProperty js/Object proto model/attr-value
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-value) "0")))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-value (str v))))
                        :enumerable true :configurable true})
  ;; max — string, reflects to attr
  (.defineProperty js/Object proto model/attr-max
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-max) "100")))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-max (str v))))
                        :enumerable true :configurable true})
  ;; indeterminate — boolean, reflects to attr
  (.defineProperty js/Object proto "indeterminate"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-indeterminate)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-indeterminate "")
                                          (.removeAttribute this model/attr-indeterminate))))
                        :enumerable true :configurable true})
  ;; showValue — boolean, reflects to show-value attr
  (.defineProperty js/Object proto "showValue"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-show-value)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-show-value "")
                                          (.removeAttribute this model/attr-show-value))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (connected! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            nil))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v]
            (this-as ^js this
                     (attribute-changed! this n o v)
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
