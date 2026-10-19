(ns baredom.components.x-select.x-select
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.utils.forms :as forms]
            [goog.object :as gobj]
            [baremirror.alpha.places :as places]
            [baredom.components.x-select.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via du/setv! / du/getv)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xSelectRefs")
(def ^:private k-handlers  "__xSelectHandlers")
(def ^:private k-model     "__xSelectModel")
(def ^:private k-internals "__xSelectInternals")

(def ^:private msg-value-missing "Please select an item in the list.")

;; ---------------------------------------------------------------------------
;; Attribute / part name constants
;; ---------------------------------------------------------------------------
(def ^:private attr-part             "part")
(def ^:private attr-id               "id")
(def ^:private attr-hidden           "hidden")
(def ^:private attr-role             "role")
(def ^:private attr-aria-hidden      "aria-hidden")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-aria-live        "aria-live")
(def ^:private attr-data-size        "data-size")
(def ^:private attr-data-disabled    "data-disabled")
(def ^:private attr-data-placeholder "data-placeholder")

(def ^:private part-wrapper "wrapper")
(def ^:private part-select  "select")
(def ^:private part-chevron "chevron")
(def ^:private part-error   "error")

(def ^:private id-error "error")

(def ^:private val-alert     "alert")
(def ^:private val-assertive "assertive")

(def ^:private cls-error-hidden "error-hidden")

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
   "--x-select-font-size-sm:var(--x-font-size-xs,0.75rem);"
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
   "--x-select-error-color:var(--x-color-danger,#dc2626);"
   "--x-select-shadow:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.06));"
   "--x-select-transition-duration:var(--x-transition-duration,140ms);"
   "}"
   du/control-font-rule
   du/hidden-rule
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-select-bg:var(--x-color-bg,#1f2937);"
   "--x-select-bg-disabled:var(--x-color-bg,#111827);"
   "--x-select-fg:var(--x-color-text,#f1f5f9);"
   "--x-select-border:var(--x-color-border,#374151);"
   "--x-select-border-hover:var(--x-color-border,#4b5563);"
   "--x-select-border-focus:var(--x-color-focus-ring,#60a5fa);"
   "--x-select-chevron:var(--x-color-text-muted,#94a3b8);"
   "--x-select-error-color:var(--x-color-danger,#f87171);"
   "}"
   "}"
   "[part=wrapper]{"
   "position:relative;"
   "display:flex;"
   "align-items:stretch;"
   "height:var(--x-select-height-md);"
   "border:var(--x-border-width,1px) solid var(--x-select-border);"
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
   ":host([data-invalid]) [part=wrapper]{"
   "border-color:var(--x-select-error-color);"
   "}"
   ":host([data-invalid]) [part=wrapper]:focus-within:not([data-disabled]){"
   "border-color:var(--x-select-error-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-select-error-color) 25%,transparent);"
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
   "[part=error]{"
   "display:block;"
   "margin-top:0.25rem;"
   "font-size:var(--x-font-size-sm,0.8125rem);"
   "line-height:1.4;"
   "color:var(--x-select-error-color);"
   "}"
   ".error-hidden{display:none;}"
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
        error-el   (.createElement js/document "span")
        slot-el    (.createElement js/document "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! wrapper-el attr-part        part-wrapper)
    (du/set-attr! select-el  attr-part        part-select)
    (du/set-attr! chevron-el attr-part        part-chevron)
    (du/set-attr! chevron-el attr-aria-hidden "true")

    ;; Inline error message. Mirrors x-form-field: assertive live region with
    ;; role=alert, hidden until an `error` attribute is present.
    (du/set-attr! error-el attr-part      part-error)
    (du/set-attr! error-el attr-id        id-error)
    (du/set-attr! error-el attr-role      val-alert)
    (du/set-attr! error-el attr-aria-live val-assertive)
    (.add (.-classList error-el) cls-error-hidden)

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
    (.appendChild root error-el)
    (.appendChild root slot-el)

    (let [refs #js {:root            root
                    :wrapper         wrapper-el
                    :select          select-el
                    :placeholder-opt ph-opt-el
                    :error           error-el
                    :slot            slot-el
                    :chevron         chevron-el}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- option-sources
  "The slotted options and optgroups of `el`, or nil before it has a shadow."
  [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js slot-el (gobj/get refs "slot")]
      (filterv (fn option-source? [^js node] (contains? slot-tags (.-tagName node)))
               (array-seq (.assignedElements slot-el #js {:flatten true}))))))

(defn- read-model [^js el]
  (model/normalize
   {:disabled-present? (forms/disabled? el)
    :required-present? (du/has-attr? el model/attr-required)
    :size-raw          (du/get-attr el model/attr-size)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :value-raw         (du/get-attr el model/attr-value)
    :name-raw          (du/get-attr el model/attr-name)
    :error-raw         (du/get-attr el model/attr-error)
    :option-htmls      (map (fn outer-html [^js node] (.-outerHTML node)) (option-sources el))}))

;; ---------------------------------------------------------------------------
;; Render (render-orchestrator: apply-model! reads as a phase list, caches
;; the model at the tail; update-from-attrs! gates the work behind a diff)
;; ---------------------------------------------------------------------------

(defn- apply-options!
  "Brings the options in `select-el` to `option-keys`, with a clone of the source of a new key."
  [^js el ^js select-el option-keys]
  (let [by-key (zipmap option-keys (option-sources el))]
    (places/sync! {:options {:parent select-el}}
                  {:options option-keys}
                  (fn clone-source [k] (.cloneNode ^js (by-key k) true)))))

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
  ;; markup and fall back to the hidden placeholder. A select that already shows the value keeps
  ;; its option, which matters for an empty or a repeated value.
  (when (and (some? value) (not= value (.-value select-el)))
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

;; ---------------------------------------------------------------------------
;; Form association — ElementInternals value + constraint validation
;; ---------------------------------------------------------------------------
;; Push the live selection into the form and refresh validity via the shared
;; policy. No-op when the element is not form-associated (older browsers).
(defn- option-value-present?
  "True when the inner <select> currently has an <option> with this exact value."
  [^js select-el value]
  (let [opts (.-options select-el)
        n    (.-length opts)]
    (loop [i 0]
      (cond
        (>= i n)                          false
        (= value (.-value (aget opts i))) true
        :else                             (recur (inc i))))))

(defn- selected-value
  "The value `el` holds: its value attribute while no option has it yet, else the live selection."
  [^js el ^js select-el]
  (let [attr (du/get-attr el model/attr-value "")]
    (if (and (not= attr "") (not (option-value-present? select-el attr)))
      attr
      (.-value select-el))))

(defn- sync-form-state! [^js el ^js select-el]
  (let [value (selected-value el select-el)]
    (forms/sync! (du/getv el k-internals) select-el value
                 {:has-error?      (du/has-attr? el model/attr-error)
                  :error           (du/get-attr el model/attr-error)
                  :required?       (du/has-attr? el model/attr-required)
                  :empty?          (= value "")
                  :missing-message msg-value-missing})))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js wrapper-el (gobj/get refs "wrapper")
          ^js select-el  (gobj/get refs "select")
          ^js ph-opt-el  (gobj/get refs "placeholder-opt")
          ^js error-el   (gobj/get refs "error")
          disabled?      (:disabled? m)
          name-val       (:name m)
          placeholder    (:placeholder m)
          has-error?     (:has-error? m)]
      (apply-options!        el select-el (:option-keys m))
      (apply-disabled!       select-el disabled?)
      (apply-required!       select-el (:required? m))
      (apply-name!           select-el name-val)
      (apply-placeholder!    ph-opt-el placeholder)
      (apply-value!          select-el (:value m))
      (apply-wrapper-state!  wrapper-el (:size m) disabled?)
      (apply-aria-label!     select-el name-val placeholder)
      (forms/apply-error-display! el select-el error-el m
                                  (forms/error-describedby has-error? nil))
      (sync-form-state!      el select-el)
      (du/setv! el k-model m))))

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
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Change handler — dispatches change-request then select-change
;; ---------------------------------------------------------------------------
(defn- default-value
  "The value of the first option of `sel` that the markup marks selected, or empty text."
  [^js sel]
  (or (some (fn default-selected [^js o] (when (.-defaultSelected o) (.-value o)))
            (array-seq (.-options sel)))
      ""))

(defn- shown-before
  "The value `el` showed before a change: its value attribute, else the option the markup marks
   selected."
  [^js el ^js sel]
  (or (du/get-attr el model/attr-value) (default-value sel)))

(defn- make-change-handler [^js el]
  (fn [^js _]
    (when-let [refs (du/getv el k-refs)]
      (let [^js sel    (gobj/get refs "select")
            value      (.-value sel)
            prev-value (shown-before el sel)
            label      (if (> (alength (.-selectedOptions sel)) 0)
                         (.-text (aget (.-selectedOptions sel) 0))
                         "")
            allowed?   (du/dispatch-cancelable!
                        el model/event-change-request
                        #js {:value value :label label :previousValue prev-value})]
        (if allowed?
          (do
            (du/set-attr! el model/attr-value value)
            (sync-form-state! el sel)
            (du/dispatch! el model/event-select-change #js {:value value :label label}))
          ;; <select>.value is a property with no attribute, so this write does not go
          ;; through du/.
          (do (set! (.-value sel) (shown-before el sel))
              (sync-form-state! el sel)))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js select-el  (gobj/get refs "select")
          ^js slot-el    (gobj/get refs "slot")
          change-h       (make-change-handler el)
          slotchange-h   (fn on-slotchange [^js _] (update-from-attrs! el))
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
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
;; Native form reset restores each <option> to its markup default (the
;; `selected` attribute) and drops any controlled `value` override, mirroring a
;; plain <select>. apply-model! then re-syncs the form value + validity.
(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
  (du/remove-attr! el model/attr-error)
  (when-let [refs (du/getv el k-refs)]
    (let [^js select-el (gobj/get refs "select")
          opts          (.-options select-el)]
      (dotimes [i (.-length opts)]
        (let [^js o (aget opts i)]
          (set! (.-selected o) (.-defaultSelected o))))))
  (du/setv! el k-model nil)
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
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

;; `value` must reflect the CURRENT selection — that is the native <select> contract and
;; what every other BareDOM form control honours (x-form's collect-values and direct
;; `el.value` readers depend on it). The reflecting accessor install-properties! would give
;; reads the `value` ATTRIBUTE, which is absent while only the markup selects an option, so
;; override `value` with a getter that reads `selected-value`. A value whose option has not
;; arrived stays pending in the attribute until apply-model! selects the option. The form
;; gets the same value.
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn xs-get-value []
               (this-as ^js this
                 (if-let [refs (du/getv this k-refs)]
                   (selected-value this (gobj/get refs part-select))
                   (du/get-attr this model/attr-value ""))))
        :set (fn xs-set-value [v]
               ;; Set the attribute (the controlled/pending value); apply-model! selects the
               ;; matching <option> on the inner select. Same as the reflecting setter it replaces.
               (this-as ^js this
                 (du/set-attr! this model/attr-value
                               (if (and (some? v) (not= v js/undefined)) (str v) ""))))}))

(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  (du/install-properties! proto model/property-api)   ; disabled / required / name + (reflecting) value
  (define-value-prop! proto))                          ; override `value`: getter reads the live inner <select>

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
