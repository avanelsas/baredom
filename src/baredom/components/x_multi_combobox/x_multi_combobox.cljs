(ns baredom.components.x-multi-combobox.x-multi-combobox
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [baredom.components.x-multi-combobox.model :as model]
            [baredom.components.x-chip.x-chip :as x-chip]
            [baredom.utils.dom :as du]
            [baredom.utils.forms :as forms]
            [baredom.utils.overlay :as overlay]
            [baremirror.core :as mirror]
            [clojure.string :as str]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs       "__xMultiComboboxRefs")
(def ^:private k-handlers   "__xMultiComboboxHandlers")
(def ^:private k-options    "__xMultiComboboxOptions")
(def ^:private k-active-idx "__xMultiComboboxActiveIdx")
(def ^:private k-query      "__xMultiComboboxQuery")
(def ^:private k-model      "__xMultiComboboxModel")
(def ^:private k-doc-deferral "__xMultiComboboxDocDeferral")
(def ^:private k-internals  "__xMultiComboboxInternals")

(def ^:private msg-value-missing "Please select at least one item.")

;; ---------------------------------------------------------------------------
;; Attribute / part / role name constants
;; ---------------------------------------------------------------------------
(def ^:private attr-part                  "part")
(def ^:private attr-role                  "role")
(def ^:private attr-id                    "id")
(def ^:private attr-type                  "type")
(def ^:private attr-autocomplete          "autocomplete")
(def ^:private attr-data-value            "data-value")
(def ^:private attr-data-active           "data-active")
(def ^:private attr-data-disabled         "data-disabled")
(def ^:private attr-data-placement        "data-placement")
(def ^:private attr-aria-label            "aria-label")
(def ^:private attr-aria-hidden           "aria-hidden")
(def ^:private attr-aria-live             "aria-live")
(def ^:private attr-aria-expanded         "aria-expanded")
(def ^:private attr-aria-disabled         "aria-disabled")
(def ^:private attr-aria-controls         "aria-controls")
(def ^:private attr-aria-autocomplete     "aria-autocomplete")
(def ^:private attr-aria-multiselectable  "aria-multiselectable")
(def ^:private attr-aria-activedescendant "aria-activedescendant")

(def ^:private part-wrapper   "wrapper")
(def ^:private part-chip-area "chip-area")
(def ^:private part-input     "input")
(def ^:private part-chevron   "chevron")
(def ^:private part-panel     "panel")
(def ^:private part-option    "option")
(def ^:private part-empty-msg "empty-msg")
(def ^:private part-error     "error")

(def ^:private id-error "error")
(def ^:private cls-error-hidden "error-hidden")

(def ^:private role-combobox "combobox")
(def ^:private role-listbox  "listbox")
(def ^:private role-option   "option")
(def ^:private role-group    "group")

;; ---------------------------------------------------------------------------
;; UID counter
;; ---------------------------------------------------------------------------
(def ^:private id-state #js {:n 0})

(defn- next-id! []
  (let [n (inc (gobj/get id-state "n"))]
    (gobj/set id-state "n" n)
    (str "x-mcb-lb-" n)))

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------
(defn- sorted-value-array
  "Returns the value set as a sorted JS array — the shape consumers expect
   in CustomEvent details and the `value` property getter."
  [value-set]
  (to-array (sort value-set)))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-multi-combobox-bg:var(--x-color-surface,#ffffff);"
   "--x-multi-combobox-fg:var(--x-color-text,#0f172a);"
   "--x-multi-combobox-placeholder:var(--x-color-text-muted,#94a3b8);"
   "--x-multi-combobox-border:var(--x-border-width,1px) solid var(--x-color-border,#e2e8f0);"
   "--x-multi-combobox-border-hover:var(--x-border-width,1px) solid var(--x-color-border,#cbd5e1);"
   "--x-multi-combobox-border-focus:var(--x-border-width,1px) solid var(--x-color-focus-ring,#60a5fa);"
   "--x-multi-combobox-radius:var(--x-radius-md,6px);"
   "--x-multi-combobox-min-height:2.25rem;"
   "--x-multi-combobox-font-size:var(--x-font-size-sm,0.9375rem);"
   "--x-multi-combobox-padding:0.25rem 0.5rem;"
   "--x-multi-combobox-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-multi-combobox-error-color:var(--x-color-danger,#dc2626);"
   "--x-multi-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.05));"
   "--x-multi-combobox-disabled-opacity:var(--x-opacity-disabled,0.55);"
   "--x-multi-combobox-chevron-color:var(--x-color-text-muted,#64748b);"
   "--x-multi-combobox-panel-bg:var(--x-color-bg,#ffffff);"
   "--x-multi-combobox-panel-border:var(--x-border-width,1px) solid var(--x-color-border,#e2e8f0);"
   "--x-multi-combobox-panel-radius:var(--x-radius-md,8px);"
   "--x-multi-combobox-panel-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.12));"
   "--x-multi-combobox-panel-max-height:16rem;"
   "--x-multi-combobox-panel-offset:4px;"
   "--x-multi-combobox-option-padding:0.5rem 0.625rem;"
   "--x-multi-combobox-option-hover-bg:var(--x-color-surface-hover,#f1f5f9);"
   "--x-multi-combobox-option-active-bg:var(--x-color-primary,#3b82f6);"
   "--x-multi-combobox-option-active-fg:#ffffff;"
   "--x-multi-combobox-transition-duration:var(--x-transition-duration,150ms);"
   "--x-multi-combobox-transition-easing:var(--x-transition-easing,ease);"
   "--x-multi-combobox-chip-gap:0.25rem;"
   "}"
   du/control-font-rule
   du/hidden-rule
   ;; Dark mode
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-multi-combobox-bg:var(--x-color-surface,#1e293b);"
   "--x-multi-combobox-fg:var(--x-color-text,#e2e8f0);"
   "--x-multi-combobox-placeholder:var(--x-color-text-muted,#64748b);"
   "--x-multi-combobox-border:var(--x-border-width,1px) solid var(--x-color-border,#334155);"
   "--x-multi-combobox-border-hover:var(--x-border-width,1px) solid var(--x-color-border,#475569);"
   "--x-multi-combobox-border-focus:var(--x-border-width,1px) solid var(--x-color-focus-ring,#93c5fd);"
   "--x-multi-combobox-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-multi-combobox-error-color:var(--x-color-danger,#f87171);"
   "--x-multi-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.2));"
   "--x-multi-combobox-chevron-color:var(--x-color-text-muted,#94a3b8);"
   "--x-multi-combobox-panel-bg:var(--x-color-bg,#1e293b);"
   "--x-multi-combobox-panel-border:var(--x-border-width,1px) solid var(--x-color-border,#334155);"
   "--x-multi-combobox-panel-shadow:var(--x-shadow-md,0 4px 24px rgba(0,0,0,0.4));"
   "--x-multi-combobox-option-hover-bg:var(--x-color-surface-hover,#334155);"
   "--x-multi-combobox-option-active-bg:var(--x-color-primary,#3b82f6);"
   "}"
   "}"
   ;; Wrapper
   "[part=wrapper]{"
   "position:relative;"
   "display:flex;"
   "align-items:center;"
   "min-height:var(--x-multi-combobox-min-height);"
   "background:var(--x-multi-combobox-bg);"
   "border:var(--x-multi-combobox-border);"
   "border-radius:var(--x-multi-combobox-radius);"
   "box-shadow:var(--x-multi-combobox-shadow);"
   "padding:var(--x-multi-combobox-padding);"
   "cursor:text;"
   "transition:border var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing),"
   "box-shadow var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing);"
   "}"
   "[part=wrapper]:hover{"
   "border:var(--x-multi-combobox-border-hover);"
   "}"
   ":host(:focus-within) [part=wrapper]{"
   "border:var(--x-multi-combobox-border-focus);"
   "box-shadow:0 0 0 2px var(--x-multi-combobox-focus-ring);"
   "}"
   ":host([data-invalid]) [part=wrapper]{border:var(--x-border-width,1px) solid var(--x-multi-combobox-error-color);}"
   ":host([data-invalid]:focus-within) [part=wrapper]{"
   "border:var(--x-border-width,1px) solid var(--x-multi-combobox-error-color);"
   "box-shadow:0 0 0 2px color-mix(in srgb,var(--x-multi-combobox-error-color) 45%,transparent);"
   "}"
   ":host([disabled]) [part=wrapper]{"
   "opacity:var(--x-multi-combobox-disabled-opacity);"
   "cursor:default;"
   "pointer-events:none;"
   "}"
   ;; Chip area
   "[part=chip-area]{"
   "display:flex;"
   "flex-wrap:wrap;"
   "align-items:center;"
   "gap:var(--x-multi-combobox-chip-gap);"
   "flex:1;"
   "min-width:0;"
   "}"
   ;; Input
   "[part=input]{"
   "all:unset;"
   "flex:1;"
   "min-width:4rem;"
   "height:1.5rem;"
   "font-size:var(--x-multi-combobox-font-size);"
   "font-family:inherit;"
   "color:var(--x-multi-combobox-fg);"
   "}"
   "[part=input]::placeholder{"
   "color:var(--x-multi-combobox-placeholder);"
   "}"
   ;; Chevron
   "[part=chevron]{"
   "display:inline-flex;"
   "align-items:center;"
   "color:var(--x-multi-combobox-chevron-color);"
   "flex-shrink:0;"
   "margin-left:0.25rem;"
   "transition:transform var(--x-transition-duration,200ms) var(--x-transition-easing,ease);"
   "cursor:pointer;"
   "}"
   ":host([open]) [part=chevron]{"
   "transform:rotate(180deg);"
   "}"
   ;; Panel
   "[part=panel]{"
   "position:absolute;"
   "z-index:var(--x-z-dropdown,1000);"
   "box-sizing:border-box;"
   "width:100%;"
   "background:var(--x-multi-combobox-panel-bg);"
   "border:var(--x-multi-combobox-panel-border);"
   "border-radius:var(--x-multi-combobox-panel-radius);"
   "box-shadow:var(--x-multi-combobox-panel-shadow);"
   "padding:0.25rem 0;"
   "max-height:var(--x-multi-combobox-panel-max-height);"
   "max-width:calc(100vw - 1rem);"
   "overflow-y:auto;"
   "visibility:hidden;"
   "pointer-events:none;"
   "opacity:0;"
   "transform:scaleY(0.95);"
   "transition:"
   "opacity var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing),"
   "transform var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing),"
   "visibility 0s var(--x-multi-combobox-transition-duration);"
   "}"
   ":host([open]) [part=panel]{"
   "visibility:visible;"
   "pointer-events:auto;"
   "opacity:1;"
   "transform:scaleY(1);"
   "transition:"
   "opacity var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing),"
   "transform var(--x-multi-combobox-transition-duration) var(--x-multi-combobox-transition-easing),"
   "visibility 0s 0s;"
   "}"
   ;; Placement
   "[part=panel][data-placement=bottom-start]{"
   "top:calc(100% + var(--x-multi-combobox-panel-offset));"
   "left:0;"
   "transform-origin:top left;"
   "}"
   "[part=panel][data-placement=bottom-end]{"
   "top:calc(100% + var(--x-multi-combobox-panel-offset));"
   "right:0;"
   "transform-origin:top right;"
   "}"
   "[part=panel][data-placement=top-start]{"
   "bottom:calc(100% + var(--x-multi-combobox-panel-offset));"
   "left:0;"
   "transform-origin:bottom left;"
   "}"
   "[part=panel][data-placement=top-end]{"
   "bottom:calc(100% + var(--x-multi-combobox-panel-offset));"
   "right:0;"
   "transform-origin:bottom right;"
   "}"
   ;; Options
   "[part=option]{"
   "padding:var(--x-multi-combobox-option-padding);"
   "font-size:var(--x-multi-combobox-font-size);"
   "font-family:inherit;"
   "color:var(--x-multi-combobox-fg);"
   "cursor:default;"
   "border-radius:var(--x-radius-sm,4px);"
   "margin:0 0.25rem;"
   "}"
   "[part=option]:hover{"
   "background:var(--x-multi-combobox-option-hover-bg);"
   "}"
   "[part=option][data-active]{"
   "background:var(--x-multi-combobox-option-active-bg);"
   "color:var(--x-multi-combobox-option-active-fg);"
   "}"
   "[part=option][data-active] b{"
   "color:inherit;"
   "}"
   "[part=option][data-disabled]{"
   "opacity:0.4;"
   "pointer-events:none;"
   "}"
   "[part=option] b{"
   "font-weight:var(--x-font-weight-bold,700);"
   "}"
   "[part=empty-msg]{"
   "padding:var(--x-multi-combobox-option-padding);"
   "font-size:var(--x-multi-combobox-font-size);"
   "color:var(--x-multi-combobox-placeholder);"
   "font-style:italic;"
   "margin:0 0.25rem;"
   "}"
   ;; Inline error message
   "[part=error]{"
   "display:block;"
   "margin-top:0.25rem;"
   "font-size:var(--x-font-size-sm,0.8125rem);"
   "line-height:1.4;"
   "color:var(--x-multi-combobox-error-color);"
   "}"
   ".error-hidden{display:none;}"
   ;; Hidden slot
   "slot{display:none;}"
   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none !important;}"
   "[part=wrapper]{transition:none !important;}"
   "[part=chevron]{transition:none !important;}"
   "}"
   ;; Coarse pointer: bigger touch targets
   "@media (pointer:coarse){"
   ":host{--x-multi-combobox-min-height:2.75rem;}"
   "[part=option]{min-height:2.75rem;display:flex;align-items:center;}"
   "[part=chevron]{width:2.75rem;height:2.75rem;justify-content:center;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction — shadow-builders pattern
;; ---------------------------------------------------------------------------
(def ^:private chevron-svg-html
  (str "<svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\""
       " xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"
       "<path d=\"M2.5 4.5L6 8L9.5 4.5\" stroke=\"currentColor\""
       " stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/></svg>"))

(defn- make-style! ^js []
  (let [^js style-el (.createElement js/document "style")]
    (set! (.-textContent style-el) style-text)
    style-el))

(defn- make-chip-area! ^js []
  (let [^js chip-area (.createElement js/document "div")]
    (du/set-attr! chip-area attr-part       part-chip-area)
    (du/set-attr! chip-area attr-role       role-group)
    (du/set-attr! chip-area attr-aria-label "Selected values")
    chip-area))

(defn- make-input! ^js [lb-id]
  (let [^js input-el (.createElement js/document "input")]
    (du/set-attr! input-el attr-part              part-input)
    (du/set-attr! input-el attr-type              "text")
    (du/set-attr! input-el attr-role              role-combobox)
    (du/set-attr! input-el attr-aria-expanded     "false")
    (du/set-attr! input-el attr-aria-autocomplete "list")
    (du/set-attr! input-el attr-aria-controls     lb-id)
    (du/set-attr! input-el attr-autocomplete      "off")
    input-el))

(defn- make-chevron! ^js []
  (let [^js chevron-el (.createElement js/document "span")]
    (du/set-attr! chevron-el attr-part        part-chevron)
    (du/set-attr! chevron-el attr-aria-hidden "true")
    (set! (.-innerHTML chevron-el) chevron-svg-html)
    chevron-el))

(defn- make-wrapper! ^js [^js chip-area ^js input-el ^js chevron-el]
  (let [^js wrapper-el (.createElement js/document "div")]
    (du/set-attr! wrapper-el attr-part part-wrapper)
    (.appendChild chip-area input-el)
    (.appendChild wrapper-el chip-area)
    (.appendChild wrapper-el chevron-el)
    wrapper-el))

(defn- make-panel! ^js [lb-id]
  (let [^js panel-el (.createElement js/document "div")]
    (du/set-attr! panel-el attr-part                 part-panel)
    (du/set-attr! panel-el attr-role                 role-listbox)
    (du/set-attr! panel-el attr-id                   lb-id)
    (du/set-attr! panel-el attr-aria-multiselectable "true")
    (du/set-attr! panel-el attr-data-placement       model/default-placement)
    panel-el))

;; Inline error message below the control. Mirrors x-form-field: an assertive
;; live region with role=alert, hidden until an `error` attribute is set.
(defn- make-error! ^js []
  (let [^js error-el (.createElement js/document "span")]
    (du/set-attr! error-el attr-part      part-error)
    (du/set-attr! error-el attr-id        id-error)
    (du/set-attr! error-el attr-role      "alert")
    (du/set-attr! error-el attr-aria-live "assertive")
    (.add (.-classList error-el) cls-error-hidden)
    error-el))

(defn- init-instance-fields! [^js el _lb-id]
  (du/setv! el k-options [])
  (du/setv! el k-query "")
  (du/setv! el k-active-idx 0))

(defn- make-shadow! [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        lb-id         (next-id!)
        ^js style-el  (make-style!)
        ^js chip-area (make-chip-area!)
        ^js input-el  (make-input! lb-id)
        ^js chevron-el (make-chevron!)
        ^js wrapper-el (make-wrapper! chip-area input-el chevron-el)
        ^js panel-el  (make-panel! lb-id)
        ^js error-el  (make-error!)
        ^js slot-el   (.createElement js/document "slot")]

    ;; Panel is nested inside the wrapper (the positioned anchor) so the inline
    ;; error span below the wrapper never displaces the open dropdown.
    (.appendChild wrapper-el panel-el)
    (.appendChild root style-el)
    (.appendChild root wrapper-el)
    (.appendChild root error-el)
    (.appendChild root slot-el)

    (init-instance-fields! el lb-id)

    (let [refs #js {:wrapper  wrapper-el
                    :chipArea chip-area
                    :input    input-el
                    :chevron  chevron-el
                    :panel    panel-el
                    :error    error-el
                    :slot     slot-el}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Option syncing from slot
;; ---------------------------------------------------------------------------
(defn- sync-options! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js slot-el (gobj/get refs "slot")
          ^js nodes   (.assignedElements slot-el #js {:flatten true})
          opts (into []
                     (comp
                      (filter (fn [^js node]
                                (= "OPTION" (.-tagName node))))
                      (map (fn [^js node]
                             {:value (or (.getAttribute node "value") (.-textContent node))
                              :label (.-textContent node)})))
                     (array-seq nodes))]
      (du/setv! el k-options opts))))

;; ---------------------------------------------------------------------------
;; Model reading
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:value-raw         (du/get-attr el model/attr-value)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :name-raw          (du/get-attr el model/attr-name)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :required-present? (du/has-attr? el model/attr-required)
    :open-present?     (du/has-attr? el model/attr-open)
    :placement-raw     (du/get-attr el model/attr-placement)
    :max-raw           (du/get-attr el model/attr-max)
    :error-raw         (du/get-attr el model/attr-error)}))

;; ---------------------------------------------------------------------------
;; Chip rendering
;; ---------------------------------------------------------------------------
(defn- chip-containers [refs]
  {:chips {:parent (gobj/get refs "chipArea")
           :before (gobj/get refs "input")}})

(defn- chip-nodes
  "The node of each chip, by value."
  [refs]
  (:nodes (mirror/read-places (chip-containers refs))))

(defn- chip-of
  "The chip that shows `value`."
  [^js el value]
  (get (chip-nodes (du/getv el k-refs)) value))

(defn- make-chip! [value]
  (doto (.createElement js/document "x-chip")
    (du/set-attr! "removable" "")
    (du/set-attr! "value" value)))

(defn- apply-chip!
  "Brings the chip for `value` in step with its option and with `disabled?`."
  [options disabled? nodes value]
  (let [chip (nodes value)]
    (du/set-attr-to! chip "label" (model/chip-label options value))
    (du/set-attr-to! chip "disabled" (when disabled? ""))))

(defn- keep-focus!
  "Moves focus to the input when `chip` holds it."
  [^js el ^js chip]
  (when (identical? chip (.-activeElement (.-shadowRoot el)))
    (.focus (gobj/get (du/getv el k-refs) "input"))))

(defn- leaving-chips
  "The chips whose value is not in `value-set`."
  [refs value-set]
  (vals (apply dissoc (chip-nodes refs) value-set)))

(defn- render-chips!
  "Brings the chips in step with `value-set`. A chip that stays keeps its node."
  [^js el value-set disabled?]
  (when-let [refs (du/getv el k-refs)]
    (run! (partial keep-focus! el) (leaving-chips refs value-set))
    (let [values (vec (sort value-set))
          nodes  (mirror/sync! (chip-containers refs) {:chips values} make-chip!)]
      (run! (partial apply-chip! (du/getv el k-options) disabled? nodes) values))))

;; ---------------------------------------------------------------------------
;; Panel rendering — render-orchestrator pattern
;; ---------------------------------------------------------------------------
(defn- make-empty-msg! []
  (let [^js msg (.createElement js/document "div")]
    (du/set-attr! msg attr-part part-empty-msg)
    msg))

(defn- make-option!
  "The node of an option: its label as text, a bold match and text, with no values."
  []
  (let [^js div (.createElement js/document "div")]
    (du/set-attr! div attr-part part-option)
    (du/set-attr! div attr-role role-option)
    (.append div "" (.createElement js/document "b") "")
    div))

(defn- make-panel-item!
  "The node of a panel item: its fixed structure. `apply-panel-item!` gives it its values."
  [item-key]
  (if (= item-key model/empty-key)
    (make-empty-msg!)
    (make-option!)))

(defn- apply-option!
  [^js div {:keys [id value active disabled aria-disabled before match after]}]
  (du/set-attr-to! div attr-id id)
  (du/set-attr-to! div attr-data-value value)
  (du/set-attr-to! div attr-data-active active)
  (du/set-attr-to! div attr-data-disabled disabled)
  (du/set-attr-to! div attr-aria-disabled aria-disabled)
  (du/set-text-to! (.-firstChild div) before)
  (du/set-text-to! (.-firstElementChild div) match)
  (du/set-text-to! (.-lastChild div) after))

(defn- apply-panel-item!
  "Writes what `item` shows onto its node in `nodes`."
  [nodes {item-key :key :keys [text] :as item}]
  (let [^js node (nodes item-key)]
    (if (= item-key model/empty-key)
      (du/set-text-to! node text)
      (apply-option! node item))))

(defn- render-panel!
  "Brings the panel to the options that match the query. An option that stays keeps its node."
  [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js panel-el (gobj/get refs "panel")
          ^js input-el (gobj/get refs "input")
          {:keys [items active-id]}
          (model/shown-panel (du/getv el k-options)
                             (or (du/getv el k-query) "")
                             (du/getv el k-active-idx)
                             (read-model el))
          nodes (mirror/sync! {:panel {:parent panel-el}} {:panel (mapv :key items)}
                              make-panel-item!)]
      (run! (partial apply-panel-item! nodes) items)
      (du/set-attr-to! input-el attr-aria-activedescendant active-id))))

;; ---------------------------------------------------------------------------
;; Render pipeline (apply-model! + update-from-attrs!)
;; ---------------------------------------------------------------------------
;; Form association — push the selected set (comma-serialized) into the form and
;; refresh validity via the shared policy. No-op when not form-associated.
(defn- sync-form-state! [^js el ^js input-el {:keys [value has-error?]}]
  (forms/sync! (du/getv el k-internals) input-el (model/serialize-value value)
               {:has-error?      has-error?
                :error           (du/get-attr el model/attr-error)
                :required?       (du/has-attr? el model/attr-required)
                :empty?          (empty? value)
                :missing-message msg-value-missing}))

(defn- apply-model! [^js el {:keys [value placeholder disabled? open? placement] :as m}]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          ^js panel-el (gobj/get refs "panel")
          ^js error-el (gobj/get refs "error")]

      ;; Show placeholder only when no chips
      (set! (.-placeholder input-el)
            (if (empty? value) placeholder ""))
      (set! (.-disabled input-el) disabled?)
      (du/set-attr! input-el attr-aria-expanded (str open?))

      (du/set-attr! panel-el attr-data-placement placement)

      ;; Clear input text when panel closes
      (when-not open?
        (set! (.-value input-el) ""))

      (render-chips! el value disabled?)

      (forms/apply-error-display! el input-el error-el m
                                  (forms/error-describedby (:has-error? m) nil))
      (sync-form-state!     el input-el m)

      ;; Re-render panel when open so option list reflects new value/max
      (when open?
        (render-panel! el))

      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ---------------------------------------------------------------------------
;; Open / Close (forward declarations)
;; ---------------------------------------------------------------------------
(declare add-doc-listeners!)
(declare remove-doc-listeners!)

(defn- open-panel! [^js el source]
  (when (and (not (du/has-attr? el model/attr-disabled))
             (not (du/has-attr? el model/attr-open)))
    (let [allowed? (du/dispatch-cancelable! el model/event-toggle
                                         #js {:open true :source source})]
      (when allowed?
        ;; Reset internal state before setAttribute so the synchronous
        ;; attribute-changed! → apply-model! → render-panel! sees cleared state.
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (du/set-attr! el model/attr-open "")))))

(defn- close-panel! [^js el source]
  (when (du/has-attr? el model/attr-open)
    (let [allowed? (du/dispatch-cancelable! el model/event-toggle
                                         #js {:open false :source source})]
      (when allowed?
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (du/remove-attr! el model/attr-open)))))

;; ---------------------------------------------------------------------------
;; Add / Remove items
;; ---------------------------------------------------------------------------
(defn- commit-values!
  "Writes `value-set` to the host and reports the change."
  [^js el value-set]
  (if (empty? value-set)
    (du/remove-attr! el model/attr-value)
    (du/set-attr! el model/attr-value (model/serialize-value value-set)))
  (du/dispatch! el model/event-change #js {:value (sorted-value-array value-set)}))

(defn- add-item! [^js el item-value]
  (let [m         (read-model el)
        value-set (:value m)
        max-val   (:max m)]
    (when-not (or (contains? value-set item-value)
                  (model/max-reached? value-set max-val))
      (let [new-set  (conj value-set item-value)
            new-arr  (sorted-value-array new-set)
            allowed? (du/dispatch-cancelable!
                      el model/event-change-request
                      #js {:value new-arr :action "add" :item item-value})]
        (when allowed?
          ;; Reset internal state before setAttribute so the synchronous
          ;; attribute-changed! → apply-model! sees the cleared query.
          (du/setv! el k-query "")
          (du/setv! el k-active-idx 0)
          (when-let [refs (du/getv el k-refs)]
            (set! (.-value (gobj/get refs "input")) ""))
          (commit-values! el new-set))))))

(defn- selected? [^js el item-value]
  (contains? (:value (read-model el)) item-value))

(defn- request-removal!
  "Asks to remove the selected `item-value`. Returns the values that remain, or nil when refused."
  [^js el item-value]
  (let [remaining (disj (:value (read-model el)) item-value)]
    (when (du/dispatch-cancelable!
           el model/event-change-request
           #js {:value (sorted-value-array remaining) :action "remove" :item item-value})
      remaining)))

(defn- let-chip-leave!
  "Gives `chip` up and writes the values that remain. The chip stays in the page to fade."
  [^js el ^js chip remaining]
  (keep-focus! el chip)
  (mirror/release! chip)
  (commit-values! el remaining))

(defn- remove-item!
  "Removes the selected `item-value` and makes its chip fade, unless the request is refused."
  [^js el item-value]
  (when (selected? el item-value)
    (when-some [remaining (request-removal! el item-value)]
      (let [chip (chip-of el item-value)]
        (let-chip-leave! el chip remaining)
        (x-chip/exit! chip)))))

;; ---------------------------------------------------------------------------
;; Keyboard navigation
;; ---------------------------------------------------------------------------
(defn- navigate! [^js el direction]
  (let [options    (du/getv el k-options)
        query      (or (du/getv el k-query) "")
        value-set  (:value (read-model el))
        visible    (model/filter-options options query value-set)
        n          (count visible)
        cur-idx    (or (du/getv el k-active-idx) 0)
        new-idx    (if (= direction :next)
                     (model/next-active-idx n cur-idx)
                     (model/prev-active-idx n cur-idx))]
    (du/setv! el k-active-idx new-idx)
    (render-panel! el)
    (when-let [refs (du/getv el k-refs)]
      (let [^js panel-el (gobj/get refs "panel")
            ^js item-el  (.querySelector panel-el "[data-active]")]
        (when item-el
          (.scrollIntoView item-el #js {:block "nearest"}))))))

(defn- select-active! [^js el]
  (let [options   (du/getv el k-options)
        query     (or (du/getv el k-query) "")
        value-set (:value (read-model el))
        visible   (model/filter-options options query value-set)
        idx       (or (du/getv el k-active-idx) 0)]
    (when-let [opt (nth visible idx nil)]
      (when-not (du/has-attr? el model/attr-disabled)
        (add-item! el (:value opt))))))

;; ---------------------------------------------------------------------------
;; Keydown — each key is one named effect, dispatched via key-handlers map
;; ---------------------------------------------------------------------------
(defn- key-arrow-down! [^js el ^js evt]
  (.preventDefault evt)
  (if (du/has-attr? el model/attr-open)
    (navigate! el :next)
    (open-panel! el "keyboard")))

(defn- key-arrow-up! [^js el ^js evt]
  (.preventDefault evt)
  (if (du/has-attr? el model/attr-open)
    (navigate! el :prev)
    (open-panel! el "keyboard")))

(defn- key-enter! [^js el ^js evt]
  (.preventDefault evt)
  (when (du/has-attr? el model/attr-open)
    (select-active! el)))

(defn- key-escape! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (close-panel! el "escape")
    (when-let [refs (du/getv el k-refs)]
      (.focus (gobj/get refs "input")))))

(defn- key-backspace! [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")]
      (when (= "" (.-value input-el))
        (when-let [last-val (model/last-value (:value (read-model el)))]
          (remove-item! el last-val))))))

(defn- key-home! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (du/setv! el k-active-idx 0)
    (render-panel! el)))

(defn- key-end! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (let [options   (du/getv el k-options)
          query     (or (du/getv el k-query) "")
          value-set (:value (read-model el))
          n         (count (model/filter-options options query value-set))]
      (du/setv! el k-active-idx (max 0 (dec n)))
      (render-panel! el))))

(def ^:private key-handlers
  {"ArrowDown" key-arrow-down!
   "ArrowUp"   key-arrow-up!
   "Enter"     key-enter!
   "Escape"    key-escape!
   "Backspace" key-backspace!
   "Home"      key-home!
   "End"       key-end!})

;; ---------------------------------------------------------------------------
;; Handler factories — each returns a closure over the host element
;; ---------------------------------------------------------------------------
(defn- on-input-focus [^js el]
  (fn [^js _evt]
    (open-panel! el "focus")))

(defn- on-input-input [^js el]
  (fn [^js evt]
    (let [^js input (.-target evt)
          q         (.-value input)]
      (du/setv! el k-query q)
      (du/setv! el k-active-idx 0)
      (when-not (du/has-attr? el model/attr-open)
        (open-panel! el "input"))
      (render-panel! el)
      (du/dispatch! el model/event-input #js {:query q}))))

(defn- on-input-keydown [^js el]
  (fn [^js evt]
    (when-let [handler (get key-handlers (.-key evt))]
      (handler el evt))))

(defn- on-chevron-pointerdown [^js el]
  (fn [^js evt]
    (.preventDefault evt)
    (if (du/has-attr? el model/attr-open)
      (close-panel! el "pointer")
      (do
        (open-panel! el "pointer")
        (when-let [refs (du/getv el k-refs)]
          (.focus (gobj/get refs "input")))))))

(defn- on-panel-pointerdown [^js _el]
  (fn [^js evt]
    (.preventDefault evt)))

(defn- on-panel-click [^js el]
  (fn [^js evt]
    (let [^js target (.-target evt)
          ^js opt-el (if (.hasAttribute target attr-data-value)
                       target
                       (.closest target "[data-value]"))]
      (when (and opt-el (not (.hasAttribute opt-el attr-data-disabled)))
        (let [value (.getAttribute opt-el attr-data-value)]
          (add-item! el value))))))

(defn- on-chip-remove [^js el]
  (fn [^js evt]
    (let [^js detail (.-detail evt)
          chip-value (gobj/get detail "value")]
      (when (and chip-value (selected? el chip-value))
        (if-some [remaining (request-removal! el chip-value)]
          (let-chip-leave! el (.-target evt) remaining)
          (.preventDefault evt))))))

(defn- on-focusout [^js el]
  (fn [^js evt]
    (when (du/has-attr? el model/attr-open)
      (let [related (.-relatedTarget evt)]
        (when (or (nil? related)
                  (not (.contains el related)))
          (close-panel! el "focusout"))))))

(defn- on-slotchange [^js el]
  ;; Slot contents changed — option labels may differ. Invalidate the
  ;; cached model so the next apply re-renders chips and panel.
  (fn [^js _evt]
    (sync-options! el)
    (du/setv! el k-model nil)
    (update-from-attrs! el)))

(defn- on-doc-click [^js el]
  (fn [^js evt]
    (when (du/has-attr? el model/attr-open)
      (let [path    (array-seq (.composedPath evt))
            inside? (some #(identical? % el) path)]
        (when-not inside?
          (close-panel! el "outside-click"))))))

(defn- make-handlers [^js el]
  #js {:inputFocus         (on-input-focus         el)
       :inputInput         (on-input-input         el)
       :inputKeydown       (on-input-keydown       el)
       :chevronPointerdown (on-chevron-pointerdown el)
       :panelPointerdown   (on-panel-pointerdown   el)
       :panelClick         (on-panel-click         el)
       :chipRemove         (on-chip-remove         el)
       :focusout           (on-focusout            el)
       :slotchange         (on-slotchange          el)
       :docClick           (on-doc-click           el)})

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
;; Each entry: [refs-key event-name handler-key]. refs-key nil means the host
;; element itself. Both add/remove iterate the same spec so they cannot drift.
(def ^:private listener-spec
  [["input"    "focus"         "inputFocus"]
   ["input"    "input"         "inputInput"]
   ["input"    "keydown"       "inputKeydown"]
   ["chipArea" "x-chip-remove" "chipRemove"]
   ["chevron"  "pointerdown"   "chevronPointerdown"]
   ["panel"    "pointerdown"   "panelPointerdown"]
   ["panel"    "click"         "panelClick"]
   [nil        "focusout"      "focusout"]
   ["slot"     "slotchange"    "slotchange"]])

(defn- iter-listeners! [^js el op]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (doseq [[refs-key event handler-key] listener-spec]
        (let [target (if refs-key (gobj/get refs refs-key) el)]
          (op target event (gobj/get handlers handler-key)))))))

(defn- add-static-listeners! [^js el]
  (iter-listeners! el (fn [^js t event handler] (.addEventListener    t event handler))))

(defn- remove-static-listeners! [^js el]
  (iter-listeners! el (fn [^js t event handler] (.removeEventListener t event handler))))

(defn- add-doc-listeners! [^js el]
  (when-let [handlers (du/getv el k-handlers)]
    ;; Defer to the next macrotask: the same pointerdown that opened the panel
    ;; would otherwise be caught by this listener and immediately close it.
    ;; Tracked + cancellable via overlay/defer-doc-listener!.
    (overlay/defer-doc-listener! el k-doc-deferral
      (fn []
        (when (du/has-attr? el model/attr-open)
          (.addEventListener js/document "click" (gobj/get handlers "docClick")))))))

(defn- remove-doc-listeners! [^js el]
  (overlay/cancel-deferred-doc-listener! el k-doc-deferral)
  (when-let [handlers (du/getv el k-handlers)]
    (.removeEventListener js/document "click" (gobj/get handlers "docClick"))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (update-from-attrs! el))

;; Reset clears the selection — mirroring x-form-field's reset-to-empty.
;; apply-model! then re-syncs the (now empty) form value + validity.
(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
  (du/remove-attr! el model/attr-error)
  (du/setv! el k-model nil)
  (update-from-attrs! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-static-listeners! el)
  (remove-doc-listeners! el)
  (du/setv! el k-handlers (make-handlers el))
  (add-static-listeners! el)
  (sync-options! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-static-listeners! el)
  (remove-doc-listeners! el)
  ;; Clear cached model so a future reconnect always re-applies — slot
  ;; contents (option labels) may have changed while disconnected.
  (du/setv! el k-model nil))

(defn- attribute-changed! [^js el attr-name _old _new]
  (when (du/getv el k-refs)
    (update-from-attrs! el)
    (when (= attr-name model/attr-open)
      (if (du/has-attr? el model/attr-open)
        (add-doc-listeners! el)
        (remove-doc-listeners! el)))))

;; ---------------------------------------------------------------------------
;; Property accessors
;; ---------------------------------------------------------------------------
(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  ;; Custom value accessor: getter returns JS array, setter accepts array or string.
  ;; Setter routes through du/ so programmatic `el.value = […]` is visible to
  ;; the trace recorder (x-trace-history) like every other attribute mutation.
  (.defineProperty js/Object proto "value"
    #js {:configurable true
         :enumerable true
         :get (fn []
                (this-as ^js this
                  (sorted-value-array (model/parse-value (du/get-attr this model/attr-value)))))
         :set (fn [v]
                (this-as ^js this
                  (let [s (cond
                            (string? v) v
                            (array? v)  (str/join model/value-separator (js->clj v))
                            :else       "")]
                    (if (= s "")
                      (du/remove-attr! this model/attr-value)
                      (du/set-attr!    this model/attr-value s)))))})
  ;; Standard reflectors
  (du/define-string-prop! proto "placeholder" model/attr-placeholder)
  (du/define-string-prop! proto "name"        model/attr-name)
  (du/define-string-prop! proto "placement"   model/attr-placement)
  (du/define-string-prop! proto "error"       model/attr-error)
  (du/define-bool-prop!   proto "disabled"    model/attr-disabled)
  (du/define-bool-prop!   proto "required"    model/attr-required)
  (du/define-bool-prop!   proto "open"        model/attr-open)
  (du/define-number-prop! proto "max"         model/attr-max nil)
  ;; Methods (Tier-2 .defineProperty :value descriptors)
  (.defineProperty js/Object proto "show"
    #js {:value (fn xmc-show []
                  (this-as ^js this (open-panel! this "programmatic")))
         :writable true :configurable true})
  (.defineProperty js/Object proto "hide"
    #js {:value (fn xmc-hide []
                  (this-as ^js this (close-panel! this "programmatic")))
         :writable true :configurable true}))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------
(defn init! []
  ;; Ensure x-chip is registered (needed for chip composition)
  (x-chip/init!)
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-disabled-fn       form-disabled!
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
