(ns baredom.components.x-combobox.x-combobox
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-combobox.model :as model]))

;; ── Instance-field keys ────────────────────────────────────────────────────
(def ^:private k-refs       "__xComboboxRefs")
(def ^:private k-model      "__xComboboxModel")
(def ^:private k-handlers   "__xComboboxHandlers")
(def ^:private k-options    "__xComboboxOptions")
(def ^:private k-active-idx "__xComboboxActiveIdx")
(def ^:private k-query      "__xComboboxQuery")
(def ^:private k-listbox-id "__xComboboxListboxId")

;; ── Refs-object keys ───────────────────────────────────────────────────────
(def ^:private rk-wrapper "wrapper")
(def ^:private rk-input   "input")
(def ^:private rk-clear   "clear")
(def ^:private rk-chevron "chevron")
(def ^:private rk-panel   "panel")
(def ^:private rk-slot    "slot")

;; ── Handler-object keys ────────────────────────────────────────────────────
(def ^:private hk-input-focus     "inputFocus")
(def ^:private hk-input-input     "inputInput")
(def ^:private hk-input-keydown   "inputKeydown")
(def ^:private hk-clear-click     "clearClick")
(def ^:private hk-chevron-pdown   "chevronPointerdown")
(def ^:private hk-panel-pdown     "panelPointerdown")
(def ^:private hk-panel-click     "panelClick")
(def ^:private hk-focusout        "focusout")
(def ^:private hk-slotchange      "slotchange")
(def ^:private hk-doc-click       "docClick")

;; ── String-literal constants ───────────────────────────────────────────────
(def ^:private attr-part           "part")
(def ^:private attr-type           "type")
(def ^:private attr-role           "role")
(def ^:private attr-id             "id")
(def ^:private attr-autocomplete   "autocomplete")
(def ^:private attr-tabindex       "tabindex")
(def ^:private attr-aria-label     "aria-label")
(def ^:private attr-aria-hidden    "aria-hidden")
(def ^:private attr-aria-expanded  "aria-expanded")
(def ^:private attr-aria-selected  "aria-selected")
(def ^:private attr-aria-controls  "aria-controls")
(def ^:private attr-aria-autocomplete "aria-autocomplete")
(def ^:private attr-aria-activedescendant "aria-activedescendant")
(def ^:private attr-data-active    "data-active")
(def ^:private attr-data-value     "data-value")
(def ^:private attr-data-placement "data-placement")
(def ^:private attr-data-has-value "data-has-value")

(def ^:private part-wrapper   "wrapper")
(def ^:private part-input     "input")
(def ^:private part-clear     "clear")
(def ^:private part-chevron   "chevron")
(def ^:private part-panel     "panel")
(def ^:private part-option    "option")
(def ^:private part-empty-msg "empty-msg")

(def ^:private val-button   "button")
(def ^:private val-text     "text")
(def ^:private val-combobox "combobox")
(def ^:private val-listbox  "listbox")
(def ^:private val-option   "option")
(def ^:private val-list     "list")
(def ^:private val-off      "off")
(def ^:private val-true     "true")
(def ^:private val-false    "false")
(def ^:private val-clear-label "Clear")
(def ^:private val-clear-glyph "×")

(def ^:private ev-focus        "focus")
(def ^:private ev-input        "input")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-click        "click")
(def ^:private ev-pointerdown  "pointerdown")
(def ^:private ev-focusout     "focusout")
(def ^:private ev-slotchange   "slotchange")

(def ^:private src-focus       "focus")
(def ^:private src-input       "input")
(def ^:private src-keyboard    "keyboard")
(def ^:private src-pointer     "pointer")
(def ^:private src-escape      "escape")
(def ^:private src-select      "select")
(def ^:private src-focusout    "focusout")
(def ^:private src-outside     "outside-click")
(def ^:private src-programmatic "programmatic")

(def ^:private key-arrow-down "ArrowDown")
(def ^:private key-arrow-up   "ArrowUp")
(def ^:private key-enter      "Enter")
(def ^:private key-escape     "Escape")
(def ^:private key-home       "Home")
(def ^:private key-end        "End")

(def ^:private opt-id-prefix "x-cb-opt-")
(def ^:private listbox-id-prefix "x-cb-lb-")

(def ^:private chevron-svg
  (str "<svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\""
       " xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"
       "<path d=\"M2.5 4.5L6 8L9.5 4.5\" stroke=\"currentColor\""
       " stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/></svg>"))

;; ── ID generation ──────────────────────────────────────────────────────────
;; crypto.randomUUID() replaces the previous gobj-backed counter
;; (#js {:n 0} + (gobj/set id-state "n" ...)). The old counter pretended
;; not to be an atom but was — mutating a place in lockstep with reads.
;; This swap removes the place entirely; each instance gets a fresh UUID.
(defn- new-listbox-id []
  (str listbox-id-prefix (.. js/crypto (randomUUID))))

;; ── DOM helpers ────────────────────────────────────────────────────────────

;; ── Style ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-combobox-bg:var(--x-color-surface,#ffffff);"
   "--x-combobox-fg:var(--x-color-text,#0f172a);"
   "--x-combobox-placeholder:var(--x-color-text-muted,#94a3b8);"
   "--x-combobox-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-combobox-border-hover:1px solid var(--x-color-border,#cbd5e1);"
   "--x-combobox-border-focus:1px solid var(--x-color-focus-ring,#60a5fa);"
   "--x-combobox-radius:var(--x-radius-md,6px);"
   "--x-combobox-height:2.25rem;"
   "--x-combobox-font-size:var(--x-font-size-sm,0.9375rem);"
   "--x-combobox-padding:0 0.625rem;"
   "--x-combobox-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.05));"
   "--x-combobox-disabled-opacity:var(--x-opacity-disabled,0.55);"
   "--x-combobox-chevron-color:var(--x-color-text-muted,#64748b);"
   "--x-combobox-panel-bg:var(--x-color-bg,#ffffff);"
   "--x-combobox-panel-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-combobox-panel-radius:var(--x-radius-md,8px);"
   "--x-combobox-panel-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.12));"
   "--x-combobox-panel-max-height:16rem;"
   "--x-combobox-panel-offset:4px;"
   "--x-combobox-option-padding:0.5rem 0.625rem;"
   "--x-combobox-option-hover-bg:var(--x-color-surface-hover,#f1f5f9);"
   "--x-combobox-option-active-bg:var(--x-color-primary,#3b82f6);"
   "--x-combobox-option-active-fg:#ffffff;"
   "--x-combobox-transition-duration:var(--x-transition-duration,150ms);"
   "--x-combobox-transition-easing:var(--x-transition-easing,ease);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-combobox-bg:var(--x-color-surface,#1e293b);"
   "--x-combobox-fg:var(--x-color-text,#e2e8f0);"
   "--x-combobox-placeholder:var(--x-color-text-muted,#64748b);"
   "--x-combobox-border:1px solid var(--x-color-border,#334155);"
   "--x-combobox-border-hover:1px solid var(--x-color-border,#475569);"
   "--x-combobox-border-focus:1px solid var(--x-color-focus-ring,#93c5fd);"
   "--x-combobox-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.2));"
   "--x-combobox-chevron-color:var(--x-color-text-muted,#94a3b8);"
   "--x-combobox-panel-bg:var(--x-color-bg,#1e293b);"
   "--x-combobox-panel-border:1px solid var(--x-color-border,#334155);"
   "--x-combobox-panel-shadow:var(--x-shadow-md,0 4px 24px rgba(0,0,0,0.4));"
   "--x-combobox-option-hover-bg:var(--x-color-surface-hover,#334155);"
   "--x-combobox-option-active-bg:var(--x-color-primary,#3b82f6);"
   "}"
   "}"
   "[part=wrapper]{"
   "display:flex;align-items:center;"
   "height:var(--x-combobox-height);"
   "background:var(--x-combobox-bg);"
   "border:var(--x-combobox-border);"
   "border-radius:var(--x-combobox-radius);"
   "box-shadow:var(--x-combobox-shadow);"
   "padding:var(--x-combobox-padding);"
   "cursor:text;"
   "transition:border var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "box-shadow var(--x-combobox-transition-duration) var(--x-combobox-transition-easing);"
   "}"
   "[part=wrapper]:hover{border:var(--x-combobox-border-hover);}"
   ":host(:focus-within) [part=wrapper]{"
   "border:var(--x-combobox-border-focus);"
   "box-shadow:0 0 0 2px var(--x-combobox-focus-ring);"
   "}"
   ":host([disabled]) [part=wrapper]{"
   "opacity:var(--x-combobox-disabled-opacity);cursor:default;pointer-events:none;"
   "}"
   "[part=input]{"
   "all:unset;flex:1;min-width:0;height:100%;"
   "font-size:var(--x-combobox-font-size);font-family:inherit;"
   "color:var(--x-combobox-fg);"
   "}"
   "[part=input]::placeholder{color:var(--x-combobox-placeholder);}"
   "[part=clear]{"
   "all:unset;display:none;align-items:center;justify-content:center;"
   "width:1.25rem;height:1.25rem;font-size:0.875rem;"
   "color:var(--x-combobox-chevron-color);cursor:pointer;flex-shrink:0;border-radius:2px;"
   "}"
   "[part=clear]:hover{color:var(--x-combobox-fg);}"
   "[part=clear]:focus-visible{outline:none;box-shadow:0 0 0 2px var(--x-combobox-focus-ring);}"
   ":host([data-has-value]) [part=clear]{display:inline-flex;}"
   ":host([disabled]) [part=clear]{display:none;}"
   "[part=chevron]{"
   "display:inline-flex;align-items:center;"
   "color:var(--x-combobox-chevron-color);flex-shrink:0;margin-left:0.25rem;"
   "transition:transform 200ms ease;pointer-events:none;"
   "}"
   ":host([open]) [part=chevron]{transform:rotate(180deg);}"
   "[part=panel]{"
   "position:absolute;z-index:var(--x-z-dropdown,1000);box-sizing:border-box;"
   "width:100%;background:var(--x-combobox-panel-bg);"
   "border:var(--x-combobox-panel-border);"
   "border-radius:var(--x-combobox-panel-radius);"
   "box-shadow:var(--x-combobox-panel-shadow);"
   "padding:0.25rem 0;max-height:var(--x-combobox-panel-max-height);"
   "max-width:calc(100% - 0rem);overflow-y:auto;"
   "visibility:hidden;pointer-events:none;opacity:0;transform:scaleY(0.95);"
   "transition:"
   "opacity var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "transform var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "visibility 0s var(--x-combobox-transition-duration);"
   "}"
   ":host([open]) [part=panel]{"
   "visibility:visible;pointer-events:auto;opacity:1;transform:scaleY(1);"
   "transition:"
   "opacity var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "transform var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "visibility 0s 0s;"
   "}"
   "[part=panel][data-placement=bottom-start]{top:calc(100% + var(--x-combobox-panel-offset));left:0;transform-origin:top left;}"
   "[part=panel][data-placement=bottom-end]{top:calc(100% + var(--x-combobox-panel-offset));right:0;transform-origin:top right;}"
   "[part=panel][data-placement=top-start]{bottom:calc(100% + var(--x-combobox-panel-offset));left:0;transform-origin:bottom left;}"
   "[part=panel][data-placement=top-end]{bottom:calc(100% + var(--x-combobox-panel-offset));right:0;transform-origin:bottom right;}"
   "[part=option]{"
   "padding:var(--x-combobox-option-padding);font-size:var(--x-combobox-font-size);"
   "font-family:inherit;color:var(--x-combobox-fg);cursor:default;"
   "border-radius:4px;margin:0 0.25rem;"
   "}"
   "[part=option]:hover{background:var(--x-combobox-option-hover-bg);}"
   "[part=option][data-active]{background:var(--x-combobox-option-active-bg);color:var(--x-combobox-option-active-fg);}"
   "[part=option][data-active] b{color:inherit;}"
   "[part=option] b{font-weight:700;}"
   "[part=empty-msg]{"
   "padding:var(--x-combobox-option-padding);"
   "font-size:var(--x-combobox-font-size);color:var(--x-combobox-placeholder);"
   "font-style:italic;margin:0 0.25rem;"
   "}"
   "slot{display:none;}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none !important;}"
   "[part=wrapper]{transition:none !important;}"
   "[part=chevron]{transition:none !important;}"
   "}"
   "@media (pointer:coarse){"
   ":host{--x-combobox-height:2.75rem;}"
   "[part=clear]{width:2.75rem;height:2.75rem;}"
   "[part=option]{min-height:2.75rem;display:flex;align-items:center;}"
   "}"))

;; ── Shadow builders (shadow-builders named pattern) ────────────────────────
(defn- make-input! [lb-id]
  (let [input-el (.createElement js/document "input")]
    (du/set-attr! input-el attr-part              part-input)
    (du/set-attr! input-el attr-type              val-text)
    (du/set-attr! input-el attr-role              val-combobox)
    (du/set-attr! input-el attr-aria-expanded     val-false)
    (du/set-attr! input-el attr-aria-autocomplete val-list)
    (du/set-attr! input-el attr-aria-controls     lb-id)
    (du/set-attr! input-el attr-autocomplete      val-off)
    input-el))

(defn- make-clear! []
  (let [clear-el (.createElement js/document "button")]
    (du/set-attr! clear-el attr-part       part-clear)
    (du/set-attr! clear-el attr-type       val-button)
    (du/set-attr! clear-el attr-aria-label val-clear-label)
    (du/set-attr! clear-el attr-tabindex   "-1")
    (set! (.-textContent clear-el) val-clear-glyph)
    clear-el))

(defn- make-chevron! []
  (let [chevron-el (.createElement js/document "span")]
    (du/set-attr! chevron-el attr-part        part-chevron)
    (du/set-attr! chevron-el attr-aria-hidden val-true)
    (set! (.-innerHTML chevron-el) chevron-svg)
    chevron-el))

(defn- make-wrapper! [^js input-el ^js clear-el ^js chevron-el]
  (let [wrapper-el (.createElement js/document "div")]
    (du/set-attr! wrapper-el attr-part part-wrapper)
    (.appendChild wrapper-el input-el)
    (.appendChild wrapper-el clear-el)
    (.appendChild wrapper-el chevron-el)
    wrapper-el))

(defn- make-panel! [lb-id]
  (let [panel-el (.createElement js/document "div")]
    (du/set-attr! panel-el attr-part           part-panel)
    (du/set-attr! panel-el attr-role           val-listbox)
    (du/set-attr! panel-el attr-id             lb-id)
    (du/set-attr! panel-el attr-data-placement model/default-placement)
    panel-el))

(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        lb-id      (new-listbox-id)
        input-el   (make-input! lb-id)
        clear-el   (make-clear!)
        chevron-el (make-chevron!)
        wrapper-el (make-wrapper! input-el clear-el chevron-el)
        panel-el   (make-panel! lb-id)
        slot-el    (.createElement js/document "slot")
        refs       #js {}]

    (set! (.-textContent style-el) style-text)

    (.appendChild root style-el)
    (.appendChild root wrapper-el)
    (.appendChild root panel-el)
    (.appendChild root slot-el)

    (du/setv! el k-listbox-id lb-id)
    (du/setv! el k-options [])
    (du/setv! el k-query "")
    (du/setv! el k-active-idx 0)

    (gobj/set refs rk-wrapper wrapper-el)
    (gobj/set refs rk-input   input-el)
    (gobj/set refs rk-clear   clear-el)
    (gobj/set refs rk-chevron chevron-el)
    (gobj/set refs rk-panel   panel-el)
    (gobj/set refs rk-slot    slot-el)
    (du/setv! el k-refs refs)
    refs))

;; ── Option syncing from slot ───────────────────────────────────────────────
(defn- sync-options! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js slot-el (gobj/get refs rk-slot)
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

;; ── Model reading ──────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:value-raw         (du/get-attr el model/attr-value)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :name-raw          (du/get-attr el model/attr-name)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :required-present? (du/has-attr? el model/attr-required)
    :open-present?     (du/has-attr? el model/attr-open)
    :placement-raw     (du/get-attr el model/attr-placement)}))

;; ── Panel rendering ────────────────────────────────────────────────────────
(defn- append-option!
  [^js panel-el ^js div idx active-idx value highlight label]
  (du/set-attr! div attr-part       part-option)
  (du/set-attr! div attr-role       val-option)
  (du/set-attr! div attr-id         (str opt-id-prefix idx))
  (du/set-attr! div attr-data-value value)
  (when (= idx active-idx)
    (du/set-attr! div attr-data-active ""))
  (if highlight
    (do
      (.appendChild div (.createTextNode js/document (:before highlight)))
      (let [^js b (.createElement js/document "b")]
        (set! (.-textContent b) (:match highlight))
        (.appendChild div b))
      (.appendChild div (.createTextNode js/document (:after highlight))))
    (set! (.-textContent div) label))
  (.appendChild panel-el div))

(defn- render-empty! [^js panel-el ^js input-el]
  (let [^js msg (.createElement js/document "div")]
    (du/set-attr! msg attr-part part-empty-msg)
    (set! (.-textContent msg) model/empty-message)
    (.appendChild panel-el msg)
    (du/remove-attr! input-el attr-aria-activedescendant)))

(defn- render-option-list! [^js panel-el ^js input-el visible active-idx selected-value]
  (doseq [[idx opt] (map-indexed vector visible)]
    (let [^js div    (.createElement js/document "div")
          highlight  (model/highlight-match (:label opt) (or (du/getv panel-el "_query") ""))]
      (append-option! panel-el div idx active-idx (:value opt) highlight (:label opt))
      (when (= (:value opt) selected-value)
        (du/set-attr! div attr-aria-selected val-true))))
  (if (>= active-idx 0)
    (du/set-attr! input-el attr-aria-activedescendant (str opt-id-prefix active-idx))
    (du/remove-attr! input-el attr-aria-activedescendant)))

(defn- render-panel! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js panel-el (gobj/get refs rk-panel)
          ^js input-el (gobj/get refs rk-input)
          options      (du/getv el k-options)
          query        (or (du/getv el k-query) "")
          visible      (model/filter-options options query)
          raw-idx      (or (du/getv el k-active-idx) 0)
          active-idx   (if (empty? visible) -1 (min raw-idx (dec (count visible))))
          value        (or (du/get-attr el model/attr-value) "")]
      (set! (.-textContent panel-el) "")
      ;; Stash the query on the panel for append-option!'s highlight lookup.
      (du/setv! panel-el "_query" query)
      (if (empty? visible)
        (render-empty!       panel-el input-el)
        (render-option-list! panel-el input-el visible active-idx value)))))

;; ── DOM patching ───────────────────────────────────────────────────────────
(defn- apply-input-state! [^js input-el {:keys [placeholder disabled? open?]}]
  (set! (.-placeholder input-el) placeholder)
  (set! (.-disabled input-el)    disabled?)
  (du/set-attr! input-el attr-aria-expanded (str open?)))

(defn- apply-panel-placement! [^js panel-el {:keys [placement]}]
  (du/set-attr! panel-el attr-data-placement placement))

(defn- apply-input-value! [^js input-el {:keys [value open?]} selected-opt]
  ;; Show the selected label when the panel is closed; while open, keep
  ;; whatever the user has typed.
  (when-not open?
    (set! (.-value input-el) (if selected-opt (:label selected-opt) ""))
    ;; Use `_` to silence unused-binding lint when value is "" and there's no opt.
    (identity value)))

(defn- apply-host-data-has-value! [^js el {:keys [value]}]
  (if (not= value "")
    (du/set-attr! el attr-data-has-value "")
    (du/remove-attr! el attr-data-has-value)))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          ^js panel-el (gobj/get refs rk-panel)
          options      (du/getv el k-options)
          selected-opt (model/find-option-by-value options (:value m))]
      (apply-input-state!        input-el m)
      (apply-panel-placement!    panel-el m)
      (apply-input-value!        input-el m selected-opt)
      (apply-host-data-has-value! el m)
      ;; Re-render panel when open so highlights reflect the latest value.
      (when (:open? m)
        (render-panel! el))
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Open / close ───────────────────────────────────────────────────────────
(declare add-doc-listeners! remove-doc-listeners!)

(defn- open-panel! [^js el source]
  (when (and (not (du/has-attr? el model/attr-disabled))
             (not (du/has-attr? el model/attr-open)))
    (let [allowed? (du/dispatch-cancelable! el model/event-toggle
                                            #js {:open true :source source})]
      (when allowed?
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (du/set-attr! el model/attr-open "")
        (when-let [refs (du/getv el k-refs)]
          (.select (gobj/get refs rk-input)))))))

(defn- close-panel! [^js el source]
  (when (du/has-attr? el model/attr-open)
    (let [allowed? (du/dispatch-cancelable! el model/event-toggle
                                            #js {:open false :source source})]
      (when allowed?
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (du/remove-attr! el model/attr-open)))))

;; ── Selection ──────────────────────────────────────────────────────────────
(defn- select-option! [^js el value label]
  (let [prev-value (or (du/get-attr el model/attr-value) "")
        allowed?   (du/dispatch-cancelable!
                    el model/event-change-request
                    #js {:value value :label label :previousValue prev-value})]
    (when allowed?
      (du/set-attr! el model/attr-value value)
      (when-let [refs (du/getv el k-refs)]
        (set! (.-value (gobj/get refs rk-input)) label))
      (close-panel! el src-select)
      (du/dispatch! el model/event-change #js {:value value :label label}))))

;; ── Keyboard navigation ────────────────────────────────────────────────────
(defn- navigate! [^js el direction]
  (let [options (du/getv el k-options)
        query   (or (du/getv el k-query) "")
        visible (model/filter-options options query)
        n       (count visible)
        cur-idx (or (du/getv el k-active-idx) 0)
        new-idx (if (= direction :next)
                  (model/next-active-idx n cur-idx)
                  (model/prev-active-idx n cur-idx))]
    (du/setv! el k-active-idx new-idx)
    (render-panel! el)
    (when-let [refs (du/getv el k-refs)]
      (let [^js panel-el (gobj/get refs rk-panel)
            ^js item-el  (.querySelector panel-el (str "[" attr-data-active "]"))]
        (when item-el
          (.scrollIntoView item-el #js {:block "nearest"}))))))

(defn- select-active! [^js el]
  (let [options (du/getv el k-options)
        query   (or (du/getv el k-query) "")
        visible (model/filter-options options query)
        idx     (or (du/getv el k-active-idx) 0)]
    (when-let [opt (nth visible idx nil)]
      (select-option! el (:value opt) (:label opt)))))

;; ── Event handlers (named — listener-spec style) ───────────────────────────
(defn- on-input-focus [^js el ^js _evt]
  (open-panel! el src-focus))

(defn- on-input-input [^js el ^js evt]
  (let [^js input (.-target evt)
        q         (.-value input)]
    (du/setv! el k-query q)
    (du/setv! el k-active-idx 0)
    (when-not (du/has-attr? el model/attr-open)
      (open-panel! el src-input))
    (render-panel! el)
    (du/dispatch! el model/event-input #js {:query q})))

;; on-input-keydown orchestrates dispatch to per-key handlers. Each handler
;; is a small `defn-` that already knows whether it needs the panel open and
;; calls preventDefault when it acts; the dispatcher stays a phase list.

(defn- handle-arrow-down! [^js el ^js evt]
  (.preventDefault evt)
  (if (du/has-attr? el model/attr-open)
    (navigate!    el :next)
    (open-panel!  el src-keyboard)))

(defn- handle-arrow-up! [^js el ^js evt]
  (.preventDefault evt)
  (if (du/has-attr? el model/attr-open)
    (navigate!    el :prev)
    (open-panel!  el src-keyboard)))

(defn- handle-enter! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (select-active! el)))

(defn- handle-escape! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (close-panel! el src-escape)
    (when-let [refs (du/getv el k-refs)]
      (.focus (gobj/get refs rk-input)))))

(defn- handle-home! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (du/setv! el k-active-idx 0)
    (render-panel! el)))

(defn- handle-end! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (.preventDefault evt)
    (let [options (du/getv el k-options)
          query   (or (du/getv el k-query) "")
          n       (count (model/filter-options options query))]
      (du/setv! el k-active-idx (max 0 (dec n)))
      (render-panel! el))))

(defn- on-input-keydown [^js el ^js evt]
  (let [k (.-key evt)]
    (cond
      (= k key-arrow-down) (handle-arrow-down! el evt)
      (= k key-arrow-up)   (handle-arrow-up!   el evt)
      (= k key-enter)      (handle-enter!      el evt)
      (= k key-escape)     (handle-escape!     el evt)
      (= k key-home)       (handle-home!       el evt)
      (= k key-end)        (handle-end!        el evt))))

(defn- on-clear-click [^js el ^js _evt]
  (let [prev-value (or (du/get-attr el model/attr-value) "")
        allowed?   (du/dispatch-cancelable!
                    el model/event-change-request
                    #js {:value "" :label "" :previousValue prev-value})]
    (when allowed?
      (du/remove-attr! el model/attr-value)
      (du/remove-attr! el attr-data-has-value)
      (when-let [refs (du/getv el k-refs)]
        (let [^js input-el (gobj/get refs rk-input)]
          (set! (.-value input-el) "")
          (.focus input-el)))
      (du/dispatch! el model/event-change #js {:value "" :label ""}))))

(defn- on-chevron-pointerdown [^js el ^js evt]
  (.preventDefault evt)
  (if (du/has-attr? el model/attr-open)
    (close-panel! el src-pointer)
    (do (open-panel! el src-pointer)
        (when-let [refs (du/getv el k-refs)]
          (.focus (gobj/get refs rk-input))))))

(defn- on-panel-pointerdown [^js _el ^js evt]
  ;; Prevent blur on input when clicking an option in the panel.
  (.preventDefault evt))

(defn- on-panel-click [^js el ^js evt]
  (let [^js target (.-target evt)
        ^js opt-el (if (.hasAttribute target attr-data-value)
                     target
                     (.closest target (str "[" attr-data-value "]")))]
    (when opt-el
      (let [value (.getAttribute opt-el attr-data-value)
            label (.-textContent opt-el)]
        (select-option! el value label)))))

(defn- on-focusout [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (let [related (.-relatedTarget evt)]
      (when (or (nil? related)
                (not (.contains el related)))
        (close-panel! el src-focusout)))))

(defn- on-slotchange [^js el ^js _evt]
  ;; Slot contents changed — invalidate the cached model so the next apply
  ;; re-renders with the fresh options.
  (sync-options! el)
  (du/setv! el k-model nil)
  (update-from-attrs! el))

(defn- on-doc-click [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (let [path    (array-seq (.composedPath evt))
          inside? (some #(identical? % el) path)]
      (when-not inside?
        (close-panel! el src-outside)))))

;; ── Listener-spec named pattern ────────────────────────────────────────────
;; Each entry: [refs-key handler-key event-name handler-fn].
(def ^:private listener-spec
  [[rk-input   hk-input-focus    ev-focus       on-input-focus]
   [rk-input   hk-input-input    ev-input       on-input-input]
   [rk-input   hk-input-keydown  ev-keydown     on-input-keydown]
   [rk-clear   hk-clear-click    ev-click       on-clear-click]
   [rk-chevron hk-chevron-pdown  ev-pointerdown on-chevron-pointerdown]
   [rk-panel   hk-panel-pdown    ev-pointerdown on-panel-pointerdown]
   [rk-panel   hk-panel-click    ev-click       on-panel-click]
   [rk-slot    hk-slotchange     ev-slotchange  on-slotchange]])

;; Host listeners — attached to el directly, not to a shadow ref.
(def ^:private host-listener-spec
  [[hk-focusout ev-focusout on-focusout]])

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [handlers #js {}]
      (doseq [[refs-key handler-key event-name handler-fn] listener-spec]
        (let [^js target (gobj/get refs refs-key)
              wrapped    (fn [e] (handler-fn el e))]
          (.addEventListener target event-name wrapped)
          (gobj/set handlers handler-key wrapped)))
      (doseq [[handler-key event-name handler-fn] host-listener-spec]
        (let [wrapped (fn [e] (handler-fn el e))]
          (.addEventListener el event-name wrapped)
          (gobj/set handlers handler-key wrapped)))
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (when-let [refs (du/getv el k-refs)]
      (doseq [[refs-key handler-key event-name _] listener-spec]
        (let [^js target (gobj/get refs refs-key)
              wrapped    (gobj/get hs handler-key)]
          (when (and target wrapped)
            (.removeEventListener target event-name wrapped))))
      (doseq [[handler-key event-name _] host-listener-spec]
        (when-let [wrapped (gobj/get hs handler-key)]
          (.removeEventListener el event-name wrapped))))
    (du/setv! el k-handlers nil)))

;; ── Document listener — added/removed dynamically on open transitions ─────
(defn- add-doc-listeners! [^js el]
  (js/setTimeout
   (fn defer-doc-click-bind []
     (when (and (.-isConnected el) (du/has-attr? el model/attr-open))
       (let [wrapped (fn handle-doc-click [evt] (on-doc-click el evt))]
         ;; Stash the wrapper on the handlers object so remove can find it.
         (when-let [hs (du/getv el k-handlers)]
           (gobj/set hs hk-doc-click wrapped))
         (.addEventListener js/document ev-click wrapped))))
   0))

(defn- remove-doc-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (when-let [wrapped (gobj/get hs hk-doc-click)]
      (.removeEventListener js/document ev-click wrapped)
      (gobj/set hs hk-doc-click nil))))

;; ── Lifecycle ──────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (remove-doc-listeners! el)
  (add-listeners! el)
  (sync-options! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (remove-doc-listeners! el)
  ;; Clear cached model so a future reconnect always re-applies — slot
  ;; contents (option labels) may have changed while disconnected.
  (du/setv! el k-model nil))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-refs)
      (update-from-attrs! el)
      (when (= attr-name model/attr-open)
        (if (du/has-attr? el model/attr-open)
          (add-doc-listeners! el)
          (remove-doc-listeners! el))))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; show()/hide() public methods — .defineProperty is the shared idiom
  ;; for prototype methods (bare aset was audited out of x-button in PR #155).
  (.defineProperty js/Object proto "show"
                   #js {:value (fn cb-show []
                                 (this-as ^js this (open-panel! this src-programmatic)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "hide"
                   #js {:value (fn cb-hide []
                                 (this-as ^js this (close-panel! this src-programmatic)))
                        :writable true :configurable true}))

;; ── Public API ─────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
