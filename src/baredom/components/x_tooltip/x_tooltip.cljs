(ns baredom.components.x-tooltip.x-tooltip
  (:require [goog.object :as gobj]
            [baredom.components.x-tooltip.model :as model]
            [baredom.utils.dom :as du]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs       "__xTooltipRefs")
(def ^:private k-handlers   "__xTooltipHandlers")
(def ^:private k-show-timer "__xTooltipShowTimer")
(def ^:private k-tooltip-id "__xTooltipId")
(def ^:private k-trigger-el "__xTooltipTriggerEl")

;; ---------------------------------------------------------------------------
;; UID counter
;; ---------------------------------------------------------------------------
(def ^:private id-state #js {:n 0})

(defn- next-id! []
  (let [n (inc (gobj/get id-state "n"))]
    (gobj/set id-state "n" n)
    (str "x-tt-" n)))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-tooltip-bg:var(--x-color-bg,#ffffff);"
   "--x-tooltip-text:var(--x-color-text,#0f172a);"
   "--x-tooltip-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-tooltip-padding:var(--x-space-xs,4px) var(--x-space-sm,8px);"
   "--x-tooltip-radius:var(--x-radius-md,6px);"
   "--x-tooltip-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.12));"
   "--x-tooltip-z:var(--x-z-dropdown,1000);"
   "--x-tooltip-max-width:min(200px,calc(100% - 2rem));"
   "--x-tooltip-font-size:var(--x-font-size-xs,0.75rem);"
   "--x-tooltip-arrow-size:6px;"
   "--x-tooltip-offset:4px;"
   "--x-tooltip-transition-duration:var(--x-transition-duration,150ms);"
   "--x-tooltip-transition-easing:var(--x-transition-easing,ease);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-tooltip-bg:var(--x-color-bg,#1e293b);"
   "--x-tooltip-text:var(--x-color-text,#e2e8f0);"
   "--x-tooltip-border:1px solid var(--x-color-border,#334155);"
   "--x-tooltip-shadow:0 4px 24px rgba(0,0,0,0.4);"
   "}"
   "}"
   "[part=trigger]{"
   "display:inline-block;"
   "}"
   ;; Panel — hidden by default
   "[part=panel]{"
   "position:absolute;"
   "z-index:var(--x-tooltip-z);"
   "box-sizing:border-box;"
   "background:var(--x-tooltip-bg);"
   "color:var(--x-tooltip-text);"
   "border:var(--x-tooltip-border);"
   "padding:var(--x-tooltip-padding);"
   "border-radius:var(--x-tooltip-radius);"
   "box-shadow:var(--x-tooltip-shadow);"
   "max-width:var(--x-tooltip-max-width);"
   "font-size:var(--x-tooltip-font-size);"
   "font-family:inherit;"
   "line-height:1.4;"
   "white-space:normal;"
   "word-wrap:break-word;"
   "pointer-events:none;"
   "visibility:hidden;"
   "opacity:0;"
   "transition:"
   "opacity var(--x-tooltip-transition-duration) var(--x-tooltip-transition-easing),"
   "transform var(--x-tooltip-transition-duration) var(--x-tooltip-transition-easing),"
   "visibility 0s var(--x-tooltip-transition-duration);"
   "}"
   ;; Open state
   ":host([open]) [part=panel]{"
   "visibility:visible;"
   "opacity:1;"
   "transition:"
   "opacity var(--x-tooltip-transition-duration) var(--x-tooltip-transition-easing),"
   "transform var(--x-tooltip-transition-duration) var(--x-tooltip-transition-easing),"
   "visibility 0s 0s;"
   "}"
   ;; Placement: top (default)
   "[part=panel][data-placement=top]{"
   "bottom:calc(100% + var(--x-tooltip-arrow-size) + var(--x-tooltip-offset));"
   "left:50%;"
   "transform:translateX(-50%) translateY(4px);"
   "transform-origin:bottom center;"
   "}"
   ":host([open]) [part=panel][data-placement=top]{"
   "transform:translateX(-50%) translateY(0);"
   "}"
   ;; Placement: bottom
   "[part=panel][data-placement=bottom]{"
   "top:calc(100% + var(--x-tooltip-arrow-size) + var(--x-tooltip-offset));"
   "left:50%;"
   "transform:translateX(-50%) translateY(-4px);"
   "transform-origin:top center;"
   "}"
   ":host([open]) [part=panel][data-placement=bottom]{"
   "transform:translateX(-50%) translateY(0);"
   "}"
   ;; Placement: left
   "[part=panel][data-placement=left]{"
   "right:calc(100% + var(--x-tooltip-arrow-size) + var(--x-tooltip-offset));"
   "top:50%;"
   "transform:translateY(-50%) translateX(4px);"
   "transform-origin:center right;"
   "}"
   ":host([open]) [part=panel][data-placement=left]{"
   "transform:translateY(-50%) translateX(0);"
   "}"
   ;; Placement: right
   "[part=panel][data-placement=right]{"
   "left:calc(100% + var(--x-tooltip-arrow-size) + var(--x-tooltip-offset));"
   "top:50%;"
   "transform:translateY(-50%) translateX(-4px);"
   "transform-origin:center left;"
   "}"
   ":host([open]) [part=panel][data-placement=right]{"
   "transform:translateY(-50%) translateX(0);"
   "}"
   ;; Arrow
   "[part=arrow]{"
   "position:absolute;"
   "width:var(--x-tooltip-arrow-size);"
   "height:var(--x-tooltip-arrow-size);"
   "background:var(--x-tooltip-bg);"
   "border:var(--x-tooltip-border);"
   "transform:rotate(45deg);"
   "pointer-events:none;"
   "}"
   ;; Arrow positions per placement
   "[part=panel][data-placement=top] [part=arrow]{"
   "bottom:calc(var(--x-tooltip-arrow-size) / -2);"
   "left:calc(50% - var(--x-tooltip-arrow-size) / 2);"
   "border-top:none;"
   "border-left:none;"
   "}"
   "[part=panel][data-placement=bottom] [part=arrow]{"
   "top:calc(var(--x-tooltip-arrow-size) / -2);"
   "left:calc(50% - var(--x-tooltip-arrow-size) / 2);"
   "border-bottom:none;"
   "border-right:none;"
   "}"
   "[part=panel][data-placement=left] [part=arrow]{"
   "right:calc(var(--x-tooltip-arrow-size) / -2);"
   "top:calc(50% - var(--x-tooltip-arrow-size) / 2);"
   "border-bottom:none;"
   "border-left:none;"
   "}"
   "[part=panel][data-placement=right] [part=arrow]{"
   "left:calc(var(--x-tooltip-arrow-size) / -2);"
   "top:calc(50% - var(--x-tooltip-arrow-size) / 2);"
   "border-top:none;"
   "border-right:none;"
   "}"
   ;; Body
   "[part=body]{"
   "position:relative;"
   "}"
   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none !important;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style-el     (make-el "style")
        trigger-el   (make-el "span")
        trigger-slot (make-el "slot")
        panel-el     (make-el "div")
        arrow-el     (make-el "div")
        body-el      (make-el "div")
        text-el      (make-el "span")
        content-slot (make-el "slot")
        tid          (next-id!)]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! trigger-el "part" "trigger")

    (du/set-attr! panel-el "part"           "panel")
    (du/set-attr! panel-el "role"           "tooltip")
    (du/set-attr! panel-el "id"             tid)
    (du/set-attr! panel-el "aria-hidden"    "true")
    (du/set-attr! panel-el "data-placement" model/default-placement)

    (du/set-attr! arrow-el "part"        "arrow")
    (du/set-attr! arrow-el "aria-hidden" "true")

    (du/set-attr! body-el "part" "body")

    (du/set-attr! text-el "part" "text")

    (du/set-attr! content-slot "name" "content")

    (.appendChild trigger-el trigger-slot)

    (.appendChild body-el text-el)
    (.appendChild body-el content-slot)

    (.appendChild panel-el arrow-el)
    (.appendChild panel-el body-el)

    (.appendChild root style-el)
    (.appendChild root trigger-el)
    (.appendChild root panel-el)

    (du/setv! el k-tooltip-id tid)

    (let [refs #js {:trigger     trigger-el
                    :triggerSlot trigger-slot
                    :panel       panel-el
                    :arrow       arrow-el
                    :body        body-el
                    :text        text-el
                    :contentSlot content-slot}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Model reading
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:text-raw          (du/get-attr el model/attr-text)
    :placement-raw     (du/get-attr el model/attr-placement)
    :delay-raw         (du/get-attr el model/attr-delay)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :open-present?     (du/has-attr? el model/attr-open)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [{:keys [text placement open?]} (read-model el)
          ^js panel-el (gobj/get refs "panel")
          ^js text-el  (gobj/get refs "text")]

      (du/set-attr! panel-el "data-placement" placement)
      (du/set-attr! panel-el "aria-hidden" (str (not open?)))

      (set! (.-textContent text-el) text))))

;; ---------------------------------------------------------------------------
;; Dispatch helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Timer management
;; ---------------------------------------------------------------------------
(defn- clear-show-timer! [^js el]
  (when-let [tid (du/getv el k-show-timer)]
    (js/clearTimeout tid)
    (du/setv! el k-show-timer nil)))

;; ---------------------------------------------------------------------------
;; Open / Close
;; ---------------------------------------------------------------------------
(defn- do-show!
  "Shows the tooltip. When immediate? is true, bypasses the delay."
  [^js el immediate?]
  (when-not (du/has-attr? el model/attr-disabled)
    (clear-show-timer! el)
    (if (or immediate? (du/has-attr? el model/attr-open))
      (when-not (du/has-attr? el model/attr-open)
        (.setAttribute el model/attr-open "")
        (du/dispatch! el model/event-show #js {}))
      (let [{:keys [delay]} (read-model el)]
        (if (zero? delay)
          (do (.setAttribute el model/attr-open "")
              (du/dispatch! el model/event-show #js {}))
          (du/setv! el k-show-timer
                    (js/setTimeout
                     (fn []
                       (du/setv! el k-show-timer nil)
                       (when (.-isConnected el)
                         (.setAttribute el model/attr-open "")
                         (du/dispatch! el model/event-show #js {})))
                     delay)))))))

(defn- do-hide! [^js el]
  (clear-show-timer! el)
  (when (du/has-attr? el model/attr-open)
    (.removeAttribute el model/attr-open)
    (du/dispatch! el model/event-hide #js {})))

;; ---------------------------------------------------------------------------
;; aria-describedby management
;; ---------------------------------------------------------------------------
(defn- update-trigger-a11y! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js slot     (gobj/get refs "triggerSlot")
          ^js elements (.assignedElements slot)
          tid          (du/getv el k-tooltip-id)]
      ;; Clean up old trigger
      (when-let [^js old-trigger (du/getv el k-trigger-el)]
        (.removeAttribute old-trigger "aria-describedby")
        (du/setv! el k-trigger-el nil))
      ;; Set on new trigger
      (when (pos? (.-length elements))
        (let [^js trigger (aget elements 0)]
          (du/set-attr! trigger "aria-describedby" tid)
          (du/setv! el k-trigger-el trigger))))))

(defn- cleanup-trigger-a11y! [^js el]
  (when-let [^js old-trigger (du/getv el k-trigger-el)]
    (.removeAttribute old-trigger "aria-describedby")
    (du/setv! el k-trigger-el nil)))

;; ---------------------------------------------------------------------------
;; Handler construction
;; ---------------------------------------------------------------------------
(defn- make-handlers [^js el]
  (let [pointerenter-h
        (fn [^js _evt]
          (do-show! el false))

        pointerleave-h
        (fn [^js _evt]
          (do-hide! el))

        focusin-h
        (fn [^js _evt]
          (do-show! el true))

        focusout-h
        (fn [^js evt]
          (let [related (.-relatedTarget evt)]
            (when (or (nil? related)
                      (not (.contains el related)))
              (do-hide! el))))

        keydown-h
        (fn [^js evt]
          (when (and (= (.-key evt) "Escape")
                     (du/has-attr? el model/attr-open))
            (do-hide! el)))

        slotchange-h
        (fn [^js _evt]
          (update-trigger-a11y! el))]

    #js {:pointerenter pointerenter-h
         :pointerleave pointerleave-h
         :focusin      focusin-h
         :focusout     focusout-h
         :keydown      keydown-h
         :slotchange   slotchange-h}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js trigger-slot (gobj/get refs "triggerSlot")]
        (.addEventListener el "pointerenter" (gobj/get handlers "pointerenter"))
        (.addEventListener el "pointerleave" (gobj/get handlers "pointerleave"))
        (.addEventListener el "focusin"      (gobj/get handlers "focusin"))
        (.addEventListener el "focusout"     (gobj/get handlers "focusout"))
        (.addEventListener el "keydown"      (gobj/get handlers "keydown"))
        (.addEventListener trigger-slot "slotchange" (gobj/get handlers "slotchange"))))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js trigger-slot (gobj/get refs "triggerSlot")]
        (.removeEventListener el "pointerenter" (gobj/get handlers "pointerenter"))
        (.removeEventListener el "pointerleave" (gobj/get handlers "pointerleave"))
        (.removeEventListener el "focusin"      (gobj/get handlers "focusin"))
        (.removeEventListener el "focusout"     (gobj/get handlers "focusout"))
        (.removeEventListener el "keydown"      (gobj/get handlers "keydown"))
        (.removeEventListener trigger-slot "slotchange" (gobj/get handlers "slotchange"))))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (du/setv! el k-handlers (make-handlers el))
  (add-listeners! el)
  (render! el)
  (update-trigger-a11y! el))

(defn- disconnected! [^js el]
  (clear-show-timer! el)
  (remove-listeners! el)
  (cleanup-trigger-a11y! el))

(defn- attribute-changed! [^js el _name _old _new]
  (when (du/getv el k-refs)
    (render! el)
    ;; If disabled while open, hide
    (when (and (du/has-attr? el model/attr-disabled)
               (du/has-attr? el model/attr-open))
      (do-hide! el))))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Reflected attribute properties
    (du/define-string-prop! proto "text"      model/attr-text)
    (du/define-string-prop! proto "placement" model/attr-placement)
    (du/define-number-prop! proto "delay" model/attr-delay model/default-delay)
    (du/define-bool-prop!   proto "disabled"  model/attr-disabled)
    (du/define-bool-prop!   proto "open"      model/attr-open)

    ;; Public methods
    (aset proto "show"
          (fn [] (this-as ^js this (do-show! this true))))

    (aset proto "hide"
          (fn [] (this-as ^js this (do-hide! this))))

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
