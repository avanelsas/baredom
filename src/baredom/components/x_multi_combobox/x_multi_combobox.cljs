(ns baredom.components.x-multi-combobox.x-multi-combobox
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [baredom.components.x-multi-combobox.model :as model]
            [baredom.components.x-chip.x-chip :as x-chip]
            [baredom.utils.dom :as du]
            [clojure.string :as str]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs       "__xMultiComboboxRefs")
(def ^:private k-handlers   "__xMultiComboboxHandlers")
(def ^:private k-options    "__xMultiComboboxOptions")
(def ^:private k-active-idx "__xMultiComboboxActiveIdx")
(def ^:private k-query      "__xMultiComboboxQuery")
(def ^:private k-listbox-id "__xMultiComboboxListboxId")
(def ^:private k-model      "__xMultiComboboxModel")
(def ^:private opt-id-prefix "x-mcb-opt-")

;; ---------------------------------------------------------------------------
;; UID counter
;; ---------------------------------------------------------------------------
(def ^:private id-state #js {:n 0})

(defn- next-id! []
  (let [n (inc (gobj/get id-state "n"))]
    (gobj/set id-state "n" n)
    (str "x-mcb-lb-" n)))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

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
   "--x-multi-combobox-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-multi-combobox-border-hover:1px solid var(--x-color-border,#cbd5e1);"
   "--x-multi-combobox-border-focus:1px solid var(--x-color-focus-ring,#60a5fa);"
   "--x-multi-combobox-radius:var(--x-radius-md,6px);"
   "--x-multi-combobox-min-height:2.25rem;"
   "--x-multi-combobox-font-size:var(--x-font-size-sm,0.9375rem);"
   "--x-multi-combobox-padding:0.25rem 0.5rem;"
   "--x-multi-combobox-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-multi-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.05));"
   "--x-multi-combobox-disabled-opacity:var(--x-opacity-disabled,0.55);"
   "--x-multi-combobox-chevron-color:var(--x-color-text-muted,#64748b);"
   "--x-multi-combobox-panel-bg:var(--x-color-bg,#ffffff);"
   "--x-multi-combobox-panel-border:1px solid var(--x-color-border,#e2e8f0);"
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
   ;; Dark mode
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-multi-combobox-bg:var(--x-color-surface,#1e293b);"
   "--x-multi-combobox-fg:var(--x-color-text,#e2e8f0);"
   "--x-multi-combobox-placeholder:var(--x-color-text-muted,#64748b);"
   "--x-multi-combobox-border:1px solid var(--x-color-border,#334155);"
   "--x-multi-combobox-border-hover:1px solid var(--x-color-border,#475569);"
   "--x-multi-combobox-border-focus:1px solid var(--x-color-focus-ring,#93c5fd);"
   "--x-multi-combobox-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-multi-combobox-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.2));"
   "--x-multi-combobox-chevron-color:var(--x-color-text-muted,#94a3b8);"
   "--x-multi-combobox-panel-bg:var(--x-color-bg,#1e293b);"
   "--x-multi-combobox-panel-border:1px solid var(--x-color-border,#334155);"
   "--x-multi-combobox-panel-shadow:var(--x-shadow-md,0 4px 24px rgba(0,0,0,0.4));"
   "--x-multi-combobox-option-hover-bg:var(--x-color-surface-hover,#334155);"
   "--x-multi-combobox-option-active-bg:var(--x-color-primary,#3b82f6);"
   "}"
   "}"
   ;; Wrapper
   "[part=wrapper]{"
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
   "transition:transform 200ms ease;"
   "pointer-events:none;"
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
   "border-radius:4px;"
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
   "font-weight:700;"
   "}"
   "[part=empty-msg]{"
   "padding:var(--x-multi-combobox-option-padding);"
   "font-size:var(--x-multi-combobox-font-size);"
   "color:var(--x-multi-combobox-placeholder);"
   "font-style:italic;"
   "margin:0 0.25rem;"
   "}"
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
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style-el    (make-el "style")
        wrapper-el  (make-el "div")
        chip-area   (make-el "div")
        input-el    (make-el "input")
        chevron-el  (make-el "span")
        panel-el    (make-el "div")
        slot-el     (make-el "slot")
        lb-id       (next-id!)]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! wrapper-el "part" "wrapper")

    (du/set-attr! chip-area "part"       "chip-area")
    (du/set-attr! chip-area "role"       "group")
    (du/set-attr! chip-area "aria-label" "Selected values")

    (du/set-attr! input-el "part"              "input")
    (du/set-attr! input-el "type"              "text")
    (du/set-attr! input-el "role"              "combobox")
    (du/set-attr! input-el "aria-expanded"     "false")
    (du/set-attr! input-el "aria-autocomplete" "list")
    (du/set-attr! input-el "aria-controls"     lb-id)
    (du/set-attr! input-el "autocomplete"      "off")

    (du/set-attr! chevron-el "part"        "chevron")
    (du/set-attr! chevron-el "aria-hidden" "true")
    (set! (.-innerHTML chevron-el)
          (str "<svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\""
               " xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"
               "<path d=\"M2.5 4.5L6 8L9.5 4.5\" stroke=\"currentColor\""
               " stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/></svg>"))

    (du/set-attr! panel-el "part"               "panel")
    (du/set-attr! panel-el "role"               "listbox")
    (du/set-attr! panel-el "id"                 lb-id)
    (du/set-attr! panel-el "aria-multiselectable" "true")
    (du/set-attr! panel-el "data-placement"     model/default-placement)

    (.appendChild chip-area input-el)
    (.appendChild wrapper-el chip-area)
    (.appendChild wrapper-el chevron-el)

    (.appendChild root style-el)
    (.appendChild root wrapper-el)
    (.appendChild root panel-el)
    (.appendChild root slot-el)

    (du/setv! el k-listbox-id lb-id)
    (du/setv! el k-options [])
    (du/setv! el k-query "")
    (du/setv! el k-active-idx 0)

    (let [refs #js {:wrapper  wrapper-el
                    :chipArea chip-area
                    :input    input-el
                    :chevron  chevron-el
                    :panel    panel-el
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
    :max-raw           (du/get-attr el model/attr-max)}))

;; ---------------------------------------------------------------------------
;; Chip rendering
;; ---------------------------------------------------------------------------
(defn- render-chips! [^js el value-set disabled?]
  (when-let [refs (du/getv el k-refs)]
    (let [^js chip-area (gobj/get refs "chipArea")
          ^js input-el  (gobj/get refs "input")
          options       (du/getv el k-options)]

      ;; Remove existing chips (keep input)
      (loop []
        (let [^js first-child (.-firstChild chip-area)]
          (when (and first-child (not (identical? first-child input-el)))
            (.removeChild chip-area first-child)
            (recur))))

      ;; Create x-chip for each selected value
      (doseq [v (sort value-set)]
        (let [opt   (model/find-option-by-value options v)
              label (if opt (:label opt) v)
              ^js chip (make-el "x-chip")]
          (du/set-attr! chip "label" label)
          (du/set-attr! chip "value" v)
          (du/set-attr! chip "removable" "")
          (when disabled?
            (du/set-attr! chip "disabled" ""))
          (.insertBefore chip-area chip input-el))))))

;; ---------------------------------------------------------------------------
;; Panel rendering
;; ---------------------------------------------------------------------------
(defn- render-panel! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js panel-el (gobj/get refs "panel")
          ^js input-el (gobj/get refs "input")
          options      (du/getv el k-options)
          query        (or (du/getv el k-query) "")
          m            (read-model el)
          value-set    (:value m)
          max-val      (:max m)
          at-max?      (model/max-reached? value-set max-val)
          visible      (model/filter-options options query value-set)
          raw-idx      (or (du/getv el k-active-idx) 0)
          active-idx   (if (empty? visible) -1 (min raw-idx (dec (count visible))))]

      (set! (.-textContent panel-el) "")

      (if (empty? visible)
        (let [^js msg (make-el "div")]
          (du/set-attr! msg "part" "empty-msg")
          (set! (.-textContent msg) model/empty-message)
          (.appendChild panel-el msg)
          (.removeAttribute input-el "aria-activedescendant"))
        (do
          (doseq [[idx opt] (map-indexed vector visible)]
            (let [^js div    (make-el "div")
                  opt-id     (str opt-id-prefix idx)
                  highlight  (model/highlight-match (:label opt) query)]
              (du/set-attr! div "part"       "option")
              (du/set-attr! div "role"       "option")
              (du/set-attr! div "id"         opt-id)
              (du/set-attr! div "data-value" (:value opt))
              (when at-max?
                (du/set-attr! div "data-disabled" "")
                (du/set-attr! div "aria-disabled" "true"))
              (when (= idx active-idx)
                (du/set-attr! div "data-active" ""))
              (if highlight
                (do
                  (.appendChild div (.createTextNode js/document (:before highlight)))
                  (let [^js b (make-el "b")]
                    (set! (.-textContent b) (:match highlight))
                    (.appendChild div b))
                  (.appendChild div (.createTextNode js/document (:after highlight))))
                (set! (.-textContent div) (:label opt)))
              (.appendChild panel-el div)))
          (if (>= active-idx 0)
            (du/set-attr! input-el "aria-activedescendant"
                       (str opt-id-prefix active-idx))
            (.removeAttribute input-el "aria-activedescendant")))))))

;; ---------------------------------------------------------------------------
;; Render pipeline (apply-model! + update-from-attrs!)
;; ---------------------------------------------------------------------------
(defn- apply-model! [^js el {:keys [value placeholder disabled? open? placement] :as m}]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          ^js panel-el (gobj/get refs "panel")]

      ;; Show placeholder only when no chips
      (set! (.-placeholder input-el)
            (if (empty? value) placeholder ""))
      (set! (.-disabled input-el) disabled?)
      (du/set-attr! input-el "aria-expanded" (str open?))

      (du/set-attr! panel-el "data-placement" placement)

      ;; Clear input text when panel closes
      (when-not open?
        (set! (.-value input-el) ""))

      (render-chips! el value disabled?)

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
        (.setAttribute el model/attr-open "")))))

(defn- close-panel! [^js el source]
  (when (du/has-attr? el model/attr-open)
    (let [allowed? (du/dispatch-cancelable! el model/event-toggle
                                         #js {:open false :source source})]
      (when allowed?
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (.removeAttribute el model/attr-open)))))

;; ---------------------------------------------------------------------------
;; Add / Remove items
;; ---------------------------------------------------------------------------
(defn- add-item! [^js el item-value]
  (let [m         (read-model el)
        value-set (:value m)
        max-val   (:max m)]
    (when-not (or (contains? value-set item-value)
                  (model/max-reached? value-set max-val))
      (let [new-set  (conj value-set item-value)
            new-arr  (to-array (sort new-set))
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
          (.setAttribute el model/attr-value (model/serialize-value new-set))
          (du/dispatch! el model/event-change #js {:value (to-array (sort new-set))}))))))

(defn- remove-item! [^js el item-value]
  (let [value-set (:value (read-model el))]
    (when (contains? value-set item-value)
      (let [new-set  (disj value-set item-value)
            new-arr  (to-array (sort new-set))
            allowed? (du/dispatch-cancelable!
                      el model/event-change-request
                      #js {:value new-arr :action "remove" :item item-value})]
        (when allowed?
          (if (empty? new-set)
            (.removeAttribute el model/attr-value)
            (.setAttribute el model/attr-value (model/serialize-value new-set)))
          (du/dispatch! el model/event-change #js {:value (to-array (sort new-set))}))))))

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
;; Handler construction
;; ---------------------------------------------------------------------------
(defn- make-handlers [^js el]
  (let [on-input-focus
        (fn [^js _evt]
          (open-panel! el "focus"))

        on-input-input
        (fn [^js evt]
          (let [^js input (.-target evt)
                q         (.-value input)]
            (du/setv! el k-query q)
            (du/setv! el k-active-idx 0)
            (when-not (du/has-attr? el model/attr-open)
              (open-panel! el "input"))
            (render-panel! el)
            (du/dispatch! el model/event-input #js {:query q})))

        on-input-keydown
        (fn [^js evt]
          (let [key (.-key evt)]
            (cond
              (= key "ArrowDown")
              (do (.preventDefault evt)
                  (if (du/has-attr? el model/attr-open)
                    (navigate! el :next)
                    (open-panel! el "keyboard")))

              (= key "ArrowUp")
              (do (.preventDefault evt)
                  (if (du/has-attr? el model/attr-open)
                    (navigate! el :prev)
                    (open-panel! el "keyboard")))

              (= key "Enter")
              (do (.preventDefault evt)
                  (when (du/has-attr? el model/attr-open)
                    (select-active! el)))

              (= key "Escape")
              (when (du/has-attr? el model/attr-open)
                (.preventDefault evt)
                (close-panel! el "escape")
                (when-let [refs (du/getv el k-refs)]
                  (.focus (gobj/get refs "input"))))

              (= key "Backspace")
              (when-let [refs (du/getv el k-refs)]
                (let [^js input-el (gobj/get refs "input")]
                  (when (= "" (.-value input-el))
                    (let [value-set (:value (read-model el))]
                      (when (seq value-set)
                        (let [last-val (last (sort value-set))]
                          (remove-item! el last-val)))))))

              (= key "Home")
              (when (du/has-attr? el model/attr-open)
                (.preventDefault evt)
                (du/setv! el k-active-idx 0)
                (render-panel! el))

              (= key "End")
              (when (du/has-attr? el model/attr-open)
                (.preventDefault evt)
                (let [options   (du/getv el k-options)
                      query     (or (du/getv el k-query) "")
                      value-set (:value (read-model el))
                      n         (count (model/filter-options options query value-set))]
                  (du/setv! el k-active-idx (max 0 (dec n)))
                  (render-panel! el))))))

        on-chevron-pointerdown
        (fn [^js evt]
          (.preventDefault evt)
          (if (du/has-attr? el model/attr-open)
            (close-panel! el "pointer")
            (do
              (open-panel! el "pointer")
              (when-let [refs (du/getv el k-refs)]
                (.focus (gobj/get refs "input"))))))

        on-panel-pointerdown
        (fn [^js evt]
          (.preventDefault evt))

        on-panel-click
        (fn [^js evt]
          (let [^js target (.-target evt)
                ^js opt-el (if (.hasAttribute target "data-value")
                             target
                             (.closest target "[data-value]"))]
            (when (and opt-el (not (.hasAttribute opt-el "data-disabled")))
              (let [value (.getAttribute opt-el "data-value")]
                (add-item! el value)))))

        on-chip-remove
        (fn [^js evt]
          (let [^js detail (.-detail evt)
                chip-value (gobj/get detail "value")]
            (when chip-value
              (remove-item! el chip-value))))

        on-focusout
        (fn [^js evt]
          (when (du/has-attr? el model/attr-open)
            (let [related (.-relatedTarget evt)]
              (when (or (nil? related)
                        (not (.contains el related)))
                (close-panel! el "focusout")))))

        on-slotchange
        (fn [^js _evt]
          ;; Slot contents changed — option labels may differ. Invalidate the
          ;; cached model so the next apply re-renders chips and panel.
          (sync-options! el)
          (du/setv! el k-model nil)
          (update-from-attrs! el))

        on-doc-click
        (fn [^js evt]
          (when (du/has-attr? el model/attr-open)
            (let [path    (array-seq (.composedPath evt))
                  inside? (some #(identical? % el) path)]
              (when-not inside?
                (close-panel! el "outside-click")))))]

    #js {:inputFocus         on-input-focus
         :inputInput         on-input-input
         :inputKeydown       on-input-keydown
         :chevronPointerdown on-chevron-pointerdown
         :panelPointerdown   on-panel-pointerdown
         :panelClick         on-panel-click
         :chipRemove         on-chip-remove
         :focusout           on-focusout
         :slotchange         on-slotchange
         :docClick           on-doc-click}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-static-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el   (gobj/get refs "input")
            ^js chip-area  (gobj/get refs "chipArea")
            ^js chevron-el (gobj/get refs "chevron")
            ^js panel-el   (gobj/get refs "panel")
            ^js slot-el    (gobj/get refs "slot")]
        (.addEventListener input-el   "focus"       (gobj/get handlers "inputFocus"))
        (.addEventListener input-el   "input"       (gobj/get handlers "inputInput"))
        (.addEventListener input-el   "keydown"     (gobj/get handlers "inputKeydown"))
        (.addEventListener chip-area  "x-chip-remove" (gobj/get handlers "chipRemove"))
        (.addEventListener chevron-el "pointerdown" (gobj/get handlers "chevronPointerdown"))
        (.addEventListener panel-el   "pointerdown" (gobj/get handlers "panelPointerdown"))
        (.addEventListener panel-el   "click"       (gobj/get handlers "panelClick"))
        (.addEventListener el         "focusout"    (gobj/get handlers "focusout"))
        (.addEventListener slot-el    "slotchange"  (gobj/get handlers "slotchange"))))))

(defn- remove-static-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el   (gobj/get refs "input")
            ^js chip-area  (gobj/get refs "chipArea")
            ^js chevron-el (gobj/get refs "chevron")
            ^js panel-el   (gobj/get refs "panel")
            ^js slot-el    (gobj/get refs "slot")]
        (.removeEventListener input-el   "focus"       (gobj/get handlers "inputFocus"))
        (.removeEventListener input-el   "input"       (gobj/get handlers "inputInput"))
        (.removeEventListener input-el   "keydown"     (gobj/get handlers "inputKeydown"))
        (.removeEventListener chip-area  "x-chip-remove" (gobj/get handlers "chipRemove"))
        (.removeEventListener chevron-el "pointerdown" (gobj/get handlers "chevronPointerdown"))
        (.removeEventListener panel-el   "pointerdown" (gobj/get handlers "panelPointerdown"))
        (.removeEventListener panel-el   "click"       (gobj/get handlers "panelClick"))
        (.removeEventListener el         "focusout"    (gobj/get handlers "focusout"))
        (.removeEventListener slot-el    "slotchange"  (gobj/get handlers "slotchange"))))))

(defn- add-doc-listeners! [^js el]
  (when-let [handlers (du/getv el k-handlers)]
    (js/setTimeout
     (fn []
       (when (and (.-isConnected el) (du/has-attr? el model/attr-open))
         (.addEventListener js/document "click" (gobj/get handlers "docClick"))))
     0)))

(defn- remove-doc-listeners! [^js el]
  (when-let [handlers (du/getv el k-handlers)]
    (.removeEventListener js/document "click" (gobj/get handlers "docClick"))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
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
  ;; Custom value accessor: getter returns JS array, setter accepts array or string
  (.defineProperty js/Object proto "value"
    #js {:configurable true
         :enumerable true
         :get (fn []
                (this-as ^js this
                  (to-array (sort (model/parse-value (du/get-attr this model/attr-value))))))
         :set (fn [v]
                (this-as ^js this
                  (let [s (cond
                            (string? v) v
                            (array? v)  (str/join "," (js->clj v))
                            :else       "")]
                    (if (= s "")
                      (.removeAttribute this model/attr-value)
                      (.setAttribute this model/attr-value s)))))})
  ;; Standard reflectors
  (du/define-string-prop! proto "placeholder" model/attr-placeholder)
  (du/define-string-prop! proto "name"        model/attr-name)
  (du/define-string-prop! proto "placement"   model/attr-placement)
  (du/define-bool-prop!   proto "disabled"    model/attr-disabled)
  (du/define-bool-prop!   proto "required"    model/attr-required)
  (du/define-bool-prop!   proto "open"        model/attr-open)
  (du/define-number-prop! proto "max"         model/attr-max nil)
  ;; Methods
  (aset proto "show"
        (fn [] (this-as ^js this (open-panel! this "programmatic"))))
  (aset proto "hide"
        (fn [] (this-as ^js this (close-panel! this "programmatic")))))

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
     :setup-prototype-fn     install-property-accessors!}))
