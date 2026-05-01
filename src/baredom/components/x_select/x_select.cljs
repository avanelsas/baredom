(ns baredom.components.x-select.x-select
  (:require [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-select.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xSelectRefs")
(def ^:private k-handlers "__xSelectHandlers")
(def ^:private k-model    "__xSelectModel")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "--x-select-height-sm:2rem;"
   "--x-select-height-md:2.5rem;"
   "--x-select-height-lg:3rem;"
   "--x-select-radius:var(--x-radius-md,0.5rem);"
   "--x-select-font-size-sm:0.75rem;"
   "--x-select-font-size-md:var(--x-font-size-sm,0.875rem);"
   "--x-select-font-size-lg:var(--x-font-size-base,1rem);"
   "--x-select-padding-inline:0.75rem;"
   "--x-select-bg:var(--x-color-bg,#ffffff);"
   "--x-select-bg-disabled:var(--x-color-bg,#f8fafc);"
   "--x-select-fg:var(--x-color-text,#0f172a);"
   "--x-select-fg-disabled:var(--x-color-text-muted,#94a3b8);"
   "--x-select-placeholder-fg:var(--x-color-text-muted,#94a3b8);"
   "--x-select-border:var(--x-color-border,#cbd5e1);"
   "--x-select-border-hover:var(--x-color-border,#94a3b8);"
   "--x-select-border-focus:var(--x-color-primary,#3b82f6);"
   "--x-select-chevron:var(--x-color-text-muted,#64748b);"
   "--x-select-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-select-shadow:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.06));"
   "--x-select-transition-duration:var(--x-transition-duration,140ms);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-select-bg:var(--x-color-bg,#1f2937);"
   "--x-select-bg-disabled:var(--x-color-bg,#111827);"
   "--x-select-fg:var(--x-color-text,#f1f5f9);"
   "--x-select-border:var(--x-color-border,#374151);"
   "--x-select-border-hover:var(--x-color-border,#4b5563);"
   "--x-select-border-focus:var(--x-color-focus-ring,#60a5fa);"
   "--x-select-chevron:var(--x-color-text-muted,#94a3b8);"
   "}"
   "}"
   "[part=wrapper]{"
   "position:relative;"
   "display:flex;"
   "align-items:stretch;"
   "height:var(--x-select-height-md);"
   "border:1px solid var(--x-select-border);"
   "border-radius:var(--x-select-radius);"
   "background:var(--x-select-bg);"
   "box-shadow:var(--x-select-shadow);"
   "transition:border-color var(--x-select-transition-duration) ease,"
   "box-shadow var(--x-select-transition-duration) ease;"
   "overflow:hidden;"
   "font-size:var(--x-select-font-size-md);"
   "}"
   "[part=wrapper][data-size=sm]{"
   "height:var(--x-select-height-sm);"
   "font-size:var(--x-select-font-size-sm);"
   "}"
   "[part=wrapper][data-size=lg]{"
   "height:var(--x-select-height-lg);"
   "font-size:var(--x-select-font-size-lg);"
   "}"
   "[part=wrapper][data-disabled]{"
   "background:var(--x-select-bg-disabled);"
   "opacity:0.6;"
   "pointer-events:none;"
   "}"
   "[part=wrapper]:hover:not([data-disabled]){"
   "border-color:var(--x-select-border-hover);"
   "}"
   "[part=wrapper]:focus-within:not([data-disabled]){"
   "border-color:var(--x-select-border-focus);"
   "box-shadow:0 0 0 3px var(--x-select-focus-ring);"
   "}"
   "[part=select]{"
   "appearance:none;"
   "-webkit-appearance:none;"
   "flex:1;"
   "min-width:0;"
   "background:transparent;"
   "border:none;"
   "outline:none;"
   "color:var(--x-select-fg);"
   "font-size:inherit;"
   "font-family:inherit;"
   "padding-inline:var(--x-select-padding-inline);"
   "padding-inline-end:calc(var(--x-select-padding-inline) + 1.75rem);"
   "cursor:pointer;"
   "width:100%;"
   "}"
   "[part=select]:disabled{"
   "color:var(--x-select-fg-disabled);"
   "cursor:default;"
   "}"
   "[part=select] option[data-placeholder]{"
   "color:var(--x-select-placeholder-fg);"
   "}"
   "[part=chevron]{"
   "position:absolute;"
   "right:var(--x-select-padding-inline);"
   "top:50%;"
   "transform:translateY(-50%);"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "pointer-events:none;"
   "color:var(--x-select-chevron);"
   "width:1rem;"
   "height:1rem;"
   "}"
   "slot{"
   "display:none;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=wrapper]{transition:none;}"
   "}"))

(def ^:private chevron-svg
  (str "<svg xmlns=\"http://www.w3.org/2000/svg\""
       " width=\"16\" height=\"16\" viewBox=\"0 0 16 16\""
       " fill=\"none\" stroke=\"currentColor\""
       " stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\""
       " aria-hidden=\"true\">"
       "<polyline points=\"4 6 8 10 12 6\"/>"
       "</svg>"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        wrapper-el (make-el "div")
        select-el  (make-el "select")
        ph-opt-el  (make-el "option")
        chevron-el (make-el "span")
        slot-el    (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! wrapper-el "part" "wrapper")
    (du/set-attr! select-el  "part" "select")
    (du/set-attr! chevron-el "part" "chevron")
    (du/set-attr! chevron-el "aria-hidden" "true")

    ;; Placeholder option: empty value, hidden by default, disabled, selected
    (du/set-attr! ph-opt-el "data-placeholder" "")
    (du/set-attr! ph-opt-el "value" "")
    (set! (.-hidden ph-opt-el) true)
    (set! (.-disabled ph-opt-el) true)
    (set! (.-selected ph-opt-el) true)

    ;; Inline chevron SVG
    (set! (.-innerHTML chevron-el) chevron-svg)

    (.appendChild select-el ph-opt-el)
    (.appendChild wrapper-el select-el)
    (.appendChild wrapper-el chevron-el)
    (.appendChild root style-el)
    (.appendChild root wrapper-el)
    (.appendChild root slot-el)

    (let [refs #js {:root            root
                    :wrapper         wrapper-el
                    :select          select-el
                    :placeholder-opt ph-opt-el
                    :slot            slot-el
                    :chevron         chevron-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:disabled-present? (du/has-attr? el model/attr-disabled)
    :required-present? (du/has-attr? el model/attr-required)
    :size-raw          (du/get-attr el model/attr-size)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :value-raw         (du/get-attr el model/attr-value)
    :name-raw          (du/get-attr el model/attr-name)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- apply-model! [^js _el refs m]
  (let [^js wrapper-el (gobj/get refs "wrapper")
        ^js select-el  (gobj/get refs "select")
        ^js ph-opt-el  (gobj/get refs "placeholder-opt")
        disabled?      (:disabled? m)
        required?      (:required? m)
        size           (:size m)
        placeholder    (:placeholder m)
        value          (:value m)
        name-val       (:name m)]

    ;; Sync disabled to internal select
    (set! (.-disabled select-el) disabled?)

    ;; Sync required
    (if required?
      (du/set-attr! select-el model/attr-required "")
      (du/remove-attr! select-el model/attr-required))

    ;; Sync name
    (if (and (string? name-val) (not= name-val ""))
      (du/set-attr! select-el model/attr-name name-val)
      (du/remove-attr! select-el model/attr-name))

    ;; Placeholder option visibility
    (if (and (string? placeholder) (not= placeholder ""))
      (do
        (set! (.-textContent ph-opt-el) placeholder)
        (set! (.-hidden ph-opt-el) false))
      (set! (.-hidden ph-opt-el) true))

    ;; Sync value to internal select
    (set! (.-value select-el) (or value ""))

    ;; data-size on wrapper for CSS size selectors
    (du/set-attr! wrapper-el "data-size" size)

    ;; data-disabled on wrapper for CSS disabled styles
    (if disabled?
      (du/set-attr! wrapper-el "data-disabled" "")
      (du/remove-attr! wrapper-el "data-disabled"))

    ;; aria-label on internal select when no name attr present
    (if (or (nil? name-val) (= name-val ""))
      (du/set-attr! select-el "aria-label" (or placeholder "select"))
      (du/remove-attr! select-el "aria-label"))))

(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [m     (read-model el)
          old-m (gobj/get el k-model)]
      (when (not= m old-m)
        (gobj/set el k-model m)
        (apply-model! el refs m)))))

;; ---------------------------------------------------------------------------
;; Option sync from slot
;; ---------------------------------------------------------------------------
(defn- sync-options! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js select-el (gobj/get refs "select")
          ^js ph-opt-el (gobj/get refs "placeholder-opt")
          ^js slot-el   (gobj/get refs "slot")
          assigned      (.assignedElements slot-el #js {:flatten true})]
      ;; Remove all children of select except the placeholder option
      (loop []
        (let [^js last-child (.-lastChild select-el)]
          (when (and last-child (not (identical? last-child ph-opt-el)))
            (.removeChild select-el last-child)
            (recur))))
      ;; Append clones of assigned option/optgroup elements
      (doseq [^js node (array-seq assigned)]
        (let [tag-lower (.toLowerCase (.-tagName node))]
          (when (or (= tag-lower "option") (= tag-lower "optgroup"))
            (.appendChild select-el (.cloneNode node true)))))
      ;; Invalidate cached model so render! re-applies value to new options
      (gobj/set el k-model nil)
      (render! el))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Change handler — dispatches change-request then select-change
;; ---------------------------------------------------------------------------
(defn- make-change-handler [^js el]
  (fn [^js _]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js sel    (gobj/get refs "select")
            value      (.-value sel)
            prev-value (or (du/get-attr el model/attr-value) "")
            label      (if (> (alength (.-selectedOptions sel)) 0)
                         (.-text (aget (.-selectedOptions sel) 0))
                         "")
            allowed?   (du/dispatch-cancelable!
                        el model/event-change-request
                        #js {:value value :label label :previousValue prev-value})]
        (if allowed?
          (du/dispatch! el model/event-select-change #js {:value value :label label})
          ;; Revert native select to previous value
          (set! (.-value sel) prev-value))))))

;; ---------------------------------------------------------------------------
;; Slotchange handler
;; ---------------------------------------------------------------------------
(defn- make-slotchange-handler [^js el]
  (fn [^js _] (sync-options! el)))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js select-el  (gobj/get refs "select")
          ^js slot-el    (gobj/get refs "slot")
          change-h       (make-change-handler el)
          slotchange-h   (make-slotchange-handler el)
          handlers       #js {:change change-h :slotchange slotchange-h}]
      (.addEventListener select-el "change"     change-h)
      (.addEventListener slot-el   "slotchange" slotchange-h)
      (gobj/set el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js select-el (gobj/get refs "select")
            ^js slot-el   (gobj/get refs "slot")]
        (.removeEventListener select-el "change"     (gobj/get handlers "change"))
        (.removeEventListener slot-el   "slotchange" (gobj/get handlers "slotchange")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs) (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (sync-options! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (render! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (du/has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (du/set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (du/get-attr this attr-name) nil)))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (du/set-attr! this attr-name (str v))
                                (du/remove-attr! this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    (define-bool-prop!   proto "disabled" model/attr-disabled)
    (define-bool-prop!   proto "required" model/attr-required)
    (define-string-prop! proto "value"    model/attr-value)

    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))
    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))
    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
