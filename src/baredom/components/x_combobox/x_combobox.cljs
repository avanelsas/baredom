(ns baredom.components.x-combobox.x-combobox
  (:require [goog.object :as gobj]
            [baredom.components.x-combobox.model :as model]
            [baredom.utils.dom :as du]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs       "__xComboboxRefs")
(def ^:private k-handlers   "__xComboboxHandlers")
(def ^:private k-options    "__xComboboxOptions")
(def ^:private k-active-idx "__xComboboxActiveIdx")
(def ^:private k-query      "__xComboboxQuery")
(def ^:private k-listbox-id "__xComboboxListboxId")
(def ^:private opt-id-prefix "x-cb-opt-")

;; ---------------------------------------------------------------------------
;; UID counter
;; ---------------------------------------------------------------------------
(def ^:private id-state #js {:n 0})

(defn- next-id! []
  (let [n (inc (gobj/get id-state "n"))]
    (gobj/set id-state "n" n)
    (str "x-cb-lb-" n)))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

(defn- set-attr! [^js el attr val]
  (.setAttribute el attr val))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
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
   ;; Wrapper
   "[part=wrapper]{"
   "display:flex;"
   "align-items:center;"
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
   "[part=wrapper]:hover{"
   "border:var(--x-combobox-border-hover);"
   "}"
   ":host(:focus-within) [part=wrapper]{"
   "border:var(--x-combobox-border-focus);"
   "box-shadow:0 0 0 2px var(--x-combobox-focus-ring);"
   "}"
   ":host([disabled]) [part=wrapper]{"
   "opacity:var(--x-combobox-disabled-opacity);"
   "cursor:default;"
   "pointer-events:none;"
   "}"
   ;; Input
   "[part=input]{"
   "all:unset;"
   "flex:1;"
   "min-width:0;"
   "height:100%;"
   "font-size:var(--x-combobox-font-size);"
   "font-family:inherit;"
   "color:var(--x-combobox-fg);"
   "}"
   "[part=input]::placeholder{"
   "color:var(--x-combobox-placeholder);"
   "}"
   ;; Clear button
   "[part=clear]{"
   "all:unset;"
   "display:none;"
   "align-items:center;"
   "justify-content:center;"
   "width:1.25rem;"
   "height:1.25rem;"
   "font-size:0.875rem;"
   "color:var(--x-combobox-chevron-color);"
   "cursor:pointer;"
   "flex-shrink:0;"
   "border-radius:2px;"
   "}"
   "[part=clear]:hover{"
   "color:var(--x-combobox-fg);"
   "}"
   "[part=clear]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 2px var(--x-combobox-focus-ring);"
   "}"
   ":host([data-has-value]) [part=clear]{"
   "display:inline-flex;"
   "}"
   ":host([disabled]) [part=clear]{"
   "display:none;"
   "}"
   ;; Chevron
   "[part=chevron]{"
   "display:inline-flex;"
   "align-items:center;"
   "color:var(--x-combobox-chevron-color);"
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
   "background:var(--x-combobox-panel-bg);"
   "border:var(--x-combobox-panel-border);"
   "border-radius:var(--x-combobox-panel-radius);"
   "box-shadow:var(--x-combobox-panel-shadow);"
   "padding:0.25rem 0;"
   "max-height:var(--x-combobox-panel-max-height);"
   "max-width:calc(100% - 0rem);"
   "overflow-y:auto;"
   "visibility:hidden;"
   "pointer-events:none;"
   "opacity:0;"
   "transform:scaleY(0.95);"
   "transition:"
   "opacity var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "transform var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "visibility 0s var(--x-combobox-transition-duration);"
   "}"
   ":host([open]) [part=panel]{"
   "visibility:visible;"
   "pointer-events:auto;"
   "opacity:1;"
   "transform:scaleY(1);"
   "transition:"
   "opacity var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "transform var(--x-combobox-transition-duration) var(--x-combobox-transition-easing),"
   "visibility 0s 0s;"
   "}"
   ;; Placement
   "[part=panel][data-placement=bottom-start]{"
   "top:calc(100% + var(--x-combobox-panel-offset));"
   "left:0;"
   "transform-origin:top left;"
   "}"
   "[part=panel][data-placement=bottom-end]{"
   "top:calc(100% + var(--x-combobox-panel-offset));"
   "right:0;"
   "transform-origin:top right;"
   "}"
   "[part=panel][data-placement=top-start]{"
   "bottom:calc(100% + var(--x-combobox-panel-offset));"
   "left:0;"
   "transform-origin:bottom left;"
   "}"
   "[part=panel][data-placement=top-end]{"
   "bottom:calc(100% + var(--x-combobox-panel-offset));"
   "right:0;"
   "transform-origin:bottom right;"
   "}"
   ;; Options
   "[part=option]{"
   "padding:var(--x-combobox-option-padding);"
   "font-size:var(--x-combobox-font-size);"
   "font-family:inherit;"
   "color:var(--x-combobox-fg);"
   "cursor:default;"
   "border-radius:4px;"
   "margin:0 0.25rem;"
   "}"
   "[part=option]:hover{"
   "background:var(--x-combobox-option-hover-bg);"
   "}"
   "[part=option][data-active]{"
   "background:var(--x-combobox-option-active-bg);"
   "color:var(--x-combobox-option-active-fg);"
   "}"
   "[part=option][data-active] b{"
   "color:inherit;"
   "}"
   "[part=option] b{"
   "font-weight:700;"
   "}"
   "[part=empty-msg]{"
   "padding:var(--x-combobox-option-padding);"
   "font-size:var(--x-combobox-font-size);"
   "color:var(--x-combobox-placeholder);"
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
   ":host{--x-combobox-height:2.75rem;}"
   "[part=clear]{width:2.75rem;height:2.75rem;}"
   "[part=option]{min-height:2.75rem;display:flex;align-items:center;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        wrapper-el (make-el "div")
        input-el   (make-el "input")
        clear-el   (make-el "button")
        chevron-el (make-el "span")
        panel-el   (make-el "div")
        slot-el    (make-el "slot")
        lb-id      (next-id!)]

    (set! (.-textContent style-el) style-text)

    (set-attr! wrapper-el "part" "wrapper")

    (set-attr! input-el "part"              "input")
    (set-attr! input-el "type"              "text")
    (set-attr! input-el "role"              "combobox")
    (set-attr! input-el "aria-expanded"     "false")
    (set-attr! input-el "aria-autocomplete" "list")
    (set-attr! input-el "aria-controls"     lb-id)
    (set-attr! input-el "autocomplete"      "off")

    (set-attr! clear-el "part"       "clear")
    (set-attr! clear-el "type"       "button")
    (set-attr! clear-el "aria-label" "Clear")
    (set-attr! clear-el "tabindex"   "-1")
    (set! (.-textContent clear-el) "\u00d7")

    (set-attr! chevron-el "part"        "chevron")
    (set-attr! chevron-el "aria-hidden" "true")
    (set! (.-innerHTML chevron-el)
          (str "<svg width=\"12\" height=\"12\" viewBox=\"0 0 12 12\" fill=\"none\""
               " xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"
               "<path d=\"M2.5 4.5L6 8L9.5 4.5\" stroke=\"currentColor\""
               " stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/></svg>"))

    (set-attr! panel-el "part" "panel")
    (set-attr! panel-el "role" "listbox")
    (set-attr! panel-el "id"   lb-id)
    (set-attr! panel-el "data-placement" model/default-placement)

    (.appendChild wrapper-el input-el)
    (.appendChild wrapper-el clear-el)
    (.appendChild wrapper-el chevron-el)

    (.appendChild root style-el)
    (.appendChild root wrapper-el)
    (.appendChild root panel-el)
    (.appendChild root slot-el)

    (du/setv! el k-listbox-id lb-id)
    (du/setv! el k-options [])
    (du/setv! el k-query "")
    (du/setv! el k-active-idx 0)

    (let [refs #js {:wrapper wrapper-el
                    :input   input-el
                    :clear   clear-el
                    :chevron chevron-el
                    :panel   panel-el
                    :slot    slot-el}]
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
    :placement-raw     (du/get-attr el model/attr-placement)}))

;; ---------------------------------------------------------------------------
;; Panel rendering
;; ---------------------------------------------------------------------------
(defn- render-panel! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js panel-el (gobj/get refs "panel")
          ^js input-el (gobj/get refs "input")
          options      (du/getv el k-options)
          query        (or (du/getv el k-query) "")
          visible      (model/filter-options options query)
          raw-idx      (or (du/getv el k-active-idx) 0)
          active-idx   (if (empty? visible) -1 (min raw-idx (dec (count visible))))
          value        (or (du/get-attr el model/attr-value) "")]

      (set! (.-textContent panel-el) "")

      (if (empty? visible)
        (let [^js msg (make-el "div")]
          (set-attr! msg "part" "empty-msg")
          (set! (.-textContent msg) model/empty-message)
          (.appendChild panel-el msg)
          (.removeAttribute input-el "aria-activedescendant"))
        (do
          (doseq [[idx opt] (map-indexed vector visible)]
            (let [^js div    (make-el "div")
                  opt-id     (str opt-id-prefix idx)
                  highlight  (model/highlight-match (:label opt) query)]
              (set-attr! div "part"       "option")
              (set-attr! div "role"       "option")
              (set-attr! div "id"         opt-id)
              (set-attr! div "data-value" (:value opt))
              (when (= (:value opt) value)
                (set-attr! div "aria-selected" "true"))
              (when (= idx active-idx)
                (set-attr! div "data-active" ""))
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
            (set-attr! input-el "aria-activedescendant"
                       (str opt-id-prefix active-idx))
            (.removeAttribute input-el "aria-activedescendant")))))))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [{:keys [value placeholder disabled? open? placement]}
          (read-model el)
          ^js input-el   (gobj/get refs "input")
          ^js panel-el   (gobj/get refs "panel")
          options        (du/getv el k-options)
          selected-opt   (model/find-option-by-value options value)]

      (set! (.-placeholder input-el) placeholder)
      (set! (.-disabled input-el) disabled?)
      (set-attr! input-el "aria-expanded" (str open?))

      (set-attr! panel-el "data-placement" placement)

      ;; Show selected label when panel is closed
      (when-not open?
        (set! (.-value input-el) (if selected-opt (:label selected-opt) "")))

      ;; data-has-value drives clear button visibility via CSS
      (if (not= value "")
        (set-attr! el "data-has-value" "")
        (.removeAttribute el "data-has-value")))))

;; ---------------------------------------------------------------------------
;; Dispatch helpers
;; ---------------------------------------------------------------------------
(defn- dispatch-cancelable! [^js el event-name detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable true})]
    (.dispatchEvent el ev)
    (not (.-defaultPrevented ev))))

(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail     detail
         :bubbles    true
         :composed   true
         :cancelable false})))

;; ---------------------------------------------------------------------------
;; Open / Close (forward declarations)
;; ---------------------------------------------------------------------------
(declare add-doc-listeners!)
(declare remove-doc-listeners!)

(defn- open-panel! [^js el source]
  (when (and (not (du/has-attr? el model/attr-disabled))
             (not (du/has-attr? el model/attr-open)))
    (let [allowed? (dispatch-cancelable! el model/event-toggle
                                         #js {:open true :source source})]
      (when allowed?
        (.setAttribute el model/attr-open "")
        ;; Reset query and active index
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        ;; Select input text so user can type immediately
        (when-let [refs (du/getv el k-refs)]
          (let [^js input-el (gobj/get refs "input")]
            (.select input-el)))
        (render-panel! el)
        (add-doc-listeners! el)))))

(defn- close-panel! [^js el source]
  (when (du/has-attr? el model/attr-open)
    (let [allowed? (dispatch-cancelable! el model/event-toggle
                                         #js {:open false :source source})]
      (when allowed?
        (.removeAttribute el model/attr-open)
        (du/setv! el k-query "")
        (du/setv! el k-active-idx 0)
        (remove-doc-listeners! el)
        (render! el)))))

;; ---------------------------------------------------------------------------
;; Selection
;; ---------------------------------------------------------------------------
(defn- select-option! [^js el value label]
  (let [prev-value (or (du/get-attr el model/attr-value) "")
        allowed?   (dispatch-cancelable!
                    el model/event-change-request
                    #js {:value value :label label :previousValue prev-value})]
    (when allowed?
      (.setAttribute el model/attr-value value)
      (when-let [refs (du/getv el k-refs)]
        (set! (.-value (gobj/get refs "input")) label))
      (close-panel! el "select")
      (dispatch! el model/event-change #js {:value value :label label}))))

;; ---------------------------------------------------------------------------
;; Keyboard navigation
;; ---------------------------------------------------------------------------
(defn- navigate! [^js el direction]
  (let [options    (du/getv el k-options)
        query      (or (du/getv el k-query) "")
        visible    (model/filter-options options query)
        n          (count visible)
        cur-idx    (or (du/getv el k-active-idx) 0)
        new-idx    (if (= direction :next)
                     (model/next-active-idx n cur-idx)
                     (model/prev-active-idx n cur-idx))]
    (du/setv! el k-active-idx new-idx)
    (render-panel! el)
    ;; Scroll active option into view
    (when-let [refs (du/getv el k-refs)]
      (let [^js panel-el (gobj/get refs "panel")
            ^js item-el  (.querySelector panel-el "[data-active]")]
        (when item-el
          (.scrollIntoView item-el #js {:block "nearest"}))))))

(defn- select-active! [^js el]
  (let [options   (du/getv el k-options)
        query     (or (du/getv el k-query) "")
        visible   (model/filter-options options query)
        idx       (or (du/getv el k-active-idx) 0)]
    (when-let [opt (nth visible idx nil)]
      (select-option! el (:value opt) (:label opt)))))

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
            (dispatch! el model/event-input #js {:query q})))

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

              (= key "Home")
              (when (du/has-attr? el model/attr-open)
                (.preventDefault evt)
                (du/setv! el k-active-idx 0)
                (render-panel! el))

              (= key "End")
              (when (du/has-attr? el model/attr-open)
                (.preventDefault evt)
                (let [options (du/getv el k-options)
                      query   (or (du/getv el k-query) "")
                      n       (count (model/filter-options options query))]
                  (du/setv! el k-active-idx (max 0 (dec n)))
                  (render-panel! el))))))

        on-clear-click
        (fn [^js _evt]
          (let [prev-value (or (du/get-attr el model/attr-value) "")
                allowed?   (dispatch-cancelable!
                            el model/event-change-request
                            #js {:value "" :label "" :previousValue prev-value})]
            (when allowed?
              (.removeAttribute el model/attr-value)
              (.removeAttribute el "data-has-value")
              (when-let [refs (du/getv el k-refs)]
                (let [^js input-el (gobj/get refs "input")]
                  (set! (.-value input-el) "")
                  (.focus input-el)))
              (dispatch! el model/event-change #js {:value "" :label ""}))))

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
          ;; Prevent blur on input when clicking option
          (.preventDefault evt))

        on-panel-click
        (fn [^js evt]
          (let [^js target (.-target evt)
                ^js opt-el (if (.hasAttribute target "data-value")
                             target
                             (.closest target "[data-value]"))]
            (when opt-el
              (let [value (.getAttribute opt-el "data-value")
                    label (.-textContent opt-el)]
                (select-option! el value label)))))

        on-focusout
        (fn [^js evt]
          (when (du/has-attr? el model/attr-open)
            (let [related (.-relatedTarget evt)]
              (when (or (nil? related)
                        (not (.contains el related)))
                (close-panel! el "focusout")))))

        on-slotchange
        (fn [^js _evt]
          (sync-options! el)
          (when (du/has-attr? el model/attr-open)
            (render-panel! el)))

        on-doc-click
        (fn [^js evt]
          (when (du/has-attr? el model/attr-open)
            (let [path    (array-seq (.composedPath evt))
                  inside? (some #(identical? % el) path)]
              (when-not inside?
                (close-panel! el "outside-click")))))]

    #js {:inputFocus       on-input-focus
         :inputInput       on-input-input
         :inputKeydown     on-input-keydown
         :clearClick       on-clear-click
         :chevronPointerdown on-chevron-pointerdown
         :panelPointerdown on-panel-pointerdown
         :panelClick       on-panel-click
         :focusout         on-focusout
         :slotchange       on-slotchange
         :docClick         on-doc-click}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-static-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el   (gobj/get refs "input")
            ^js clear-el   (gobj/get refs "clear")
            ^js chevron-el (gobj/get refs "chevron")
            ^js panel-el   (gobj/get refs "panel")
            ^js slot-el    (gobj/get refs "slot")]
        (.addEventListener input-el   "focus"       (gobj/get handlers "inputFocus"))
        (.addEventListener input-el   "input"       (gobj/get handlers "inputInput"))
        (.addEventListener input-el   "keydown"     (gobj/get handlers "inputKeydown"))
        (.addEventListener clear-el   "click"       (gobj/get handlers "clearClick"))
        (.addEventListener chevron-el "pointerdown" (gobj/get handlers "chevronPointerdown"))
        (.addEventListener panel-el   "pointerdown" (gobj/get handlers "panelPointerdown"))
        (.addEventListener panel-el   "click"       (gobj/get handlers "panelClick"))
        (.addEventListener el         "focusout"    (gobj/get handlers "focusout"))
        (.addEventListener slot-el    "slotchange"  (gobj/get handlers "slotchange"))))))

(defn- remove-static-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el   (gobj/get refs "input")
            ^js clear-el   (gobj/get refs "clear")
            ^js chevron-el (gobj/get refs "chevron")
            ^js panel-el   (gobj/get refs "panel")
            ^js slot-el    (gobj/get refs "slot")]
        (.removeEventListener input-el   "focus"       (gobj/get handlers "inputFocus"))
        (.removeEventListener input-el   "input"       (gobj/get handlers "inputInput"))
        (.removeEventListener input-el   "keydown"     (gobj/get handlers "inputKeydown"))
        (.removeEventListener clear-el   "click"       (gobj/get handlers "clearClick"))
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
  (render! el))

(defn- disconnected! [^js el]
  (remove-static-listeners! el)
  (remove-doc-listeners! el))

(defn- attribute-changed! [^js el attr-name _old _new]
  (when (du/getv el k-refs)
    (render! el)
    (when (= attr-name model/attr-open)
      (if (du/has-attr? el model/attr-open)
        (add-doc-listeners! el)
        (remove-doc-listeners! el)))))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (du/has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this
                              (du/set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (du/get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (.setAttribute this attr-name (str v))
                                (.removeAttribute this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Reflected properties
    (define-string-prop! proto "value"       model/attr-value)
    (define-string-prop! proto "placeholder" model/attr-placeholder)
    (define-string-prop! proto "name"        model/attr-name)
    (define-string-prop! proto "placement"   model/attr-placement)
    (define-bool-prop!   proto "disabled"    model/attr-disabled)
    (define-bool-prop!   proto "required"    model/attr-required)
    (define-bool-prop!   proto "open"        model/attr-open)

    ;; Public methods
    (aset proto "show"
          (fn [] (this-as ^js this (open-panel! this "programmatic"))))

    (aset proto "hide"
          (fn [] (this-as ^js this (close-panel! this "programmatic"))))

    ;; Lifecycle callbacks
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
