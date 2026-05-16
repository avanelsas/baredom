(ns baredom.components.x-select.x-select
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-select.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via du/setv! / du/getv)
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xSelectRefs")
(def ^:private k-handlers "__xSelectHandlers")
(def ^:private k-model    "__xSelectModel")

;; ---------------------------------------------------------------------------
;; Attribute / part name constants
;; ---------------------------------------------------------------------------
(def ^:private attr-part             "part")
(def ^:private attr-hidden           "hidden")
(def ^:private attr-aria-hidden      "aria-hidden")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-data-size        "data-size")
(def ^:private attr-data-disabled    "data-disabled")
(def ^:private attr-data-placeholder "data-placeholder")

(def ^:private part-wrapper "wrapper")
(def ^:private part-select  "select")
(def ^:private part-chevron "chevron")

(def ^:private default-aria-label "select")

;; Slot tags the component accepts. `.tagName` returns canonical UPPER-CASE
;; in HTML documents, so compare directly without lower-casing per node.
(def ^:private slot-tags #{"OPTION" "OPTGROUP"})

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
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        wrapper-el (.createElement js/document "div")
        select-el  (.createElement js/document "select")
        ph-opt-el  (.createElement js/document "option")
        chevron-el (.createElement js/document "span")
        slot-el    (.createElement js/document "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! wrapper-el attr-part        part-wrapper)
    (du/set-attr! select-el  attr-part        part-select)
    (du/set-attr! chevron-el attr-part        part-chevron)
    (du/set-attr! chevron-el attr-aria-hidden "true")

    ;; Placeholder option: empty value, hidden by default, disabled, selected.
    ;; `disabled` and `hidden` go through du/ as attributes (visible to the
    ;; trace recorder). `.selected` stays as a property write: <option>'s
    ;; `selected` attribute reflects only the *default* state, not the live
    ;; selection — setting the attribute would not produce the same effect.
    (du/set-attr! ph-opt-el attr-data-placeholder "")
    (du/set-attr! ph-opt-el model/attr-value      "")
    (du/set-attr! ph-opt-el attr-hidden           "")
    (du/set-attr! ph-opt-el model/attr-disabled   "")
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
      (du/setv! el k-refs refs)
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
;; Render (render-orchestrator: apply-model! reads as a phase list, caches
;; the model at the tail; update-from-attrs! gates the work behind a diff)
;; ---------------------------------------------------------------------------

(defn- apply-disabled! [^js select-el disabled?]
  (if disabled?
    (du/set-attr!    select-el model/attr-disabled "")
    (du/remove-attr! select-el model/attr-disabled)))

(defn- apply-required! [^js select-el required?]
  (if required?
    (du/set-attr!    select-el model/attr-required "")
    (du/remove-attr! select-el model/attr-required)))

(defn- apply-name! [^js select-el name-val]
  (if (and (string? name-val) (not= name-val ""))
    (du/set-attr!    select-el model/attr-name name-val)
    (du/remove-attr! select-el model/attr-name)))

(defn- apply-placeholder! [^js ph-opt-el placeholder]
  ;; Placeholder option visibility. `hidden` reflects via attribute on
  ;; HTMLElement, so the attribute path is equivalent to .hidden = bool.
  (if (and (string? placeholder) (not= placeholder ""))
    (do (set! (.-textContent ph-opt-el) placeholder)
        (du/remove-attr! ph-opt-el attr-hidden))
    (du/set-attr! ph-opt-el attr-hidden "")))

(defn- apply-value! [^js select-el value]
  ;; <select>.value is property-only (no `value` attribute on <select>), so
  ;; this stays a property write. some? guard: writing "" on every render
  ;; would deselect the slotted <option selected> the user provided in
  ;; markup and fall back to the hidden placeholder.
  (when (some? value)
    (set! (.-value select-el) value)))

(defn- apply-wrapper-state! [^js wrapper-el size disabled?]
  (du/set-attr! wrapper-el attr-data-size size)
  (if disabled?
    (du/set-attr!    wrapper-el attr-data-disabled "")
    (du/remove-attr! wrapper-el attr-data-disabled)))

(defn- apply-aria-label! [^js select-el name-val placeholder]
  (if (or (nil? name-val) (= name-val ""))
    (du/set-attr!    select-el attr-aria-label (or placeholder default-aria-label))
    (du/remove-attr! select-el attr-aria-label)))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js wrapper-el (gobj/get refs "wrapper")
          ^js select-el  (gobj/get refs "select")
          ^js ph-opt-el  (gobj/get refs "placeholder-opt")
          disabled?      (:disabled? m)
          name-val       (:name m)
          placeholder    (:placeholder m)]
      (apply-disabled!       select-el disabled?)
      (apply-required!       select-el (:required? m))
      (apply-name!           select-el name-val)
      (apply-placeholder!    ph-opt-el placeholder)
      (apply-value!          select-el (:value m))
      (apply-wrapper-state!  wrapper-el (:size m) disabled?)
      (apply-aria-label!     select-el name-val placeholder)
      (du/setv! el k-model m))))

;; Unconditional re-render — `sync-options!` invalidates non-attribute state
;; (the option list inside <select>) and must re-apply the model regardless
;; of whether the attribute-derived model has changed.
(defn- render! [^js el]
  (apply-model! el (read-model el)))

;; Guarded entry point for attribute changes — diff against the cached
;; model so a no-op attributeChangedCallback does no DOM work.
(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ---------------------------------------------------------------------------
;; Option sync from slot
;; ---------------------------------------------------------------------------
(defn- sync-options! [^js el]
  (when-let [refs (du/getv el k-refs)]
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
        (when (contains? slot-tags (.-tagName node))
          (.appendChild select-el (.cloneNode node true))))
      ;; Invalidate cached model so render! re-applies value to new options
      (du/setv! el k-model nil)
      (render! el))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Change handler — dispatches change-request then select-change
;; ---------------------------------------------------------------------------
(defn- make-change-handler [^js el]
  (fn [^js _]
    (when-let [refs (du/getv el k-refs)]
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
          ;; Revert native select to previous value. <select>.value is
          ;; property-only — no attribute counterpart — so this stays as a
          ;; property write rather than going through du/.
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
  (when-let [refs (du/getv el k-refs)]
    (let [^js select-el  (gobj/get refs "select")
          ^js slot-el    (gobj/get refs "slot")
          change-h       (make-change-handler el)
          slotchange-h   (make-slotchange-handler el)
          handlers       #js {:change change-h :slotchange slotchange-h}]
      (.addEventListener select-el "change"     change-h)
      (.addEventListener slot-el   "slotchange" slotchange-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js select-el (gobj/get refs "select")
            ^js slot-el   (gobj/get refs "slot")]
        (.removeEventListener select-el "change"     (gobj/get handlers "change"))
        (.removeEventListener slot-el   "slotchange" (gobj/get handlers "slotchange")))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs) (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (sync-options! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
