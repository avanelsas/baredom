(ns baredom.components.x-dropdown.x-dropdown
  (:require [goog.object :as gobj]
            [baredom.components.x-dropdown.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xDropdownRefs")
(def ^:private k-handlers "__xDropdownHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-dropdown-trigger-bg:#f8fafc;"
   "--x-dropdown-trigger-bg-hover:#f1f5f9;"
   "--x-dropdown-trigger-bg-active:#e2e8f0;"
   "--x-dropdown-trigger-color:#0f172a;"
   "--x-dropdown-trigger-border:1px solid #e2e8f0;"
   "--x-dropdown-trigger-radius:6px;"
   "--x-dropdown-trigger-padding:0 0.75rem;"
   "--x-dropdown-trigger-height:2.25rem;"
   "--x-dropdown-trigger-font-size:0.9375rem;"
   "--x-dropdown-trigger-font-weight:500;"
   "--x-dropdown-chevron-color:#64748b;"
   "--x-dropdown-focus-ring:#60a5fa;"
   "--x-dropdown-panel-bg:#ffffff;"
   "--x-dropdown-panel-border:1px solid #e2e8f0;"
   "--x-dropdown-panel-radius:8px;"
   "--x-dropdown-panel-shadow:0 4px 16px rgba(0,0,0,0.12);"
   "--x-dropdown-panel-padding:0.25rem;"
   "--x-dropdown-panel-min-width:10rem;"
   "--x-dropdown-panel-max-height:20rem;"
   "--x-dropdown-panel-offset:4px;"
   "--x-dropdown-transition-duration:150ms;"
   "--x-dropdown-transition-easing:ease;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-dropdown-trigger-bg:#1e293b;"
   "--x-dropdown-trigger-bg-hover:#334155;"
   "--x-dropdown-trigger-bg-active:#475569;"
   "--x-dropdown-trigger-color:#e2e8f0;"
   "--x-dropdown-trigger-border:1px solid #334155;"
   "--x-dropdown-chevron-color:#94a3b8;"
   "--x-dropdown-focus-ring:#93c5fd;"
   "--x-dropdown-panel-bg:#1e293b;"
   "--x-dropdown-panel-border:1px solid #334155;"
   "--x-dropdown-panel-shadow:0 4px 24px rgba(0,0,0,0.4);"
   "}"
   "}"
   "[part=trigger]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "gap:0.375rem;"
   "height:var(--x-dropdown-trigger-height);"
   "padding:var(--x-dropdown-trigger-padding);"
   "background:var(--x-dropdown-trigger-bg);"
   "color:var(--x-dropdown-trigger-color);"
   "border:var(--x-dropdown-trigger-border);"
   "border-radius:var(--x-dropdown-trigger-radius);"
   "font-size:var(--x-dropdown-trigger-font-size);"
   "font-weight:var(--x-dropdown-trigger-font-weight);"
   "font-family:inherit;"
   "cursor:pointer;"
   "user-select:none;"
   "white-space:nowrap;"
   "}"
   "[part=trigger]:hover:not(:disabled){"
   "background:var(--x-dropdown-trigger-bg-hover);"
   "}"
   "[part=trigger]:active:not(:disabled){"
   "background:var(--x-dropdown-trigger-bg-active);"
   "}"
   "[part=trigger]:disabled{"
   "cursor:default;"
   "opacity:0.55;"
   "}"
   "[part=trigger]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 2px var(--x-dropdown-focus-ring);"
   "}"
   "[part=trigger-label]{"
   "flex:1;"
   "}"
   "[part=chevron]{"
   "display:inline-block;"
   "color:var(--x-dropdown-chevron-color);"
   "font-style:normal;"
   "flex-shrink:0;"
   "transition:transform 200ms ease;"
   "}"
   ":host([open]) [part=chevron]{"
   "transform:rotate(180deg);"
   "}"
   "[part=panel]{"
   "position:absolute;"
   "z-index:1000;"
   "box-sizing:border-box;"
   "background:var(--x-dropdown-panel-bg);"
   "border:var(--x-dropdown-panel-border);"
   "border-radius:var(--x-dropdown-panel-radius);"
   "box-shadow:var(--x-dropdown-panel-shadow);"
   "padding:var(--x-dropdown-panel-padding);"
   "min-width:var(--x-dropdown-panel-min-width);"
   "max-width:calc(100vw - 1rem);"
   "max-height:var(--x-dropdown-panel-max-height);"
   "overflow-y:auto;"
   "visibility:hidden;"
   "pointer-events:none;"
   "opacity:0;"
   "transform:scaleY(0.95);"
   "transition:"
   "opacity var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"
   "transform var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"
   "visibility 0s var(--x-dropdown-transition-duration);"
   "}"
   ":host([open]) [part=panel]{"
   "visibility:visible;"
   "pointer-events:auto;"
   "opacity:1;"
   "transform:scaleY(1);"
   "transition:"
   "opacity var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"
   "transform var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"
   "visibility 0s 0s;"
   "}"
   "[part=panel][data-placement=bottom-start]{"
   "top:calc(100% + var(--x-dropdown-panel-offset));"
   "left:0;"
   "transform-origin:top left;"
   "}"
   "[part=panel][data-placement=bottom-end]{"
   "top:calc(100% + var(--x-dropdown-panel-offset));"
   "right:0;"
   "transform-origin:top right;"
   "}"
   "[part=panel][data-placement=top-start]{"
   "bottom:calc(100% + var(--x-dropdown-panel-offset));"
   "left:0;"
   "transform-origin:bottom left;"
   "}"
   "[part=panel][data-placement=top-end]{"
   "bottom:calc(100% + var(--x-dropdown-panel-offset));"
   "right:0;"
   "transform-origin:bottom right;"
   "}"
   "[part=panel-inner]{"
   "display:contents;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none !important;}"
   "[part=chevron]{transition:none !important;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

(defn- set-attr! [^js el attr val]
  (.setAttribute el attr val))

(defn- remove-attr! [^js el attr]
  (.removeAttribute el attr))

(defn- has-attr? [^js el attr]
  (.hasAttribute el attr))

(defn- get-attr [^js el attr]
  (.getAttribute el attr))

(defn- set-bool-attr! [^js el attr value]
  (if value (set-attr! el attr "") (remove-attr! el attr)))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        trigger-el (make-el "button")
        label-el   (make-el "span")
        chevron-el (make-el "span")
        panel-el   (make-el "div")
        inner-el   (make-el "div")
        slot-el    (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (set-attr! trigger-el "part"          "trigger")
    (set-attr! trigger-el "type"          "button")
    (set-attr! trigger-el "aria-haspopup" "true")
    (set-attr! trigger-el "aria-expanded" "false")
    (set-attr! trigger-el "aria-controls" "panel")

    (set-attr! label-el   "part"       "trigger-label")

    (set-attr! chevron-el "part"       "chevron")
    (set-attr! chevron-el "aria-hidden" "true")
    (set! (.-textContent chevron-el) "\u25BE")

    (set-attr! panel-el "part"           "panel")
    (set-attr! panel-el "id"             "panel")
    (set-attr! panel-el "data-placement" model/default-placement)

    (set-attr! inner-el "part" "panel-inner")

    (.appendChild trigger-el label-el)
    (.appendChild trigger-el chevron-el)

    (.appendChild inner-el slot-el)
    (.appendChild panel-el inner-el)

    (.appendChild root style-el)
    (.appendChild root trigger-el)
    (.appendChild root panel-el)

    (let [refs #js {:trigger trigger-el
                    :label   label-el
                    :chevron chevron-el
                    :panel   panel-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js trigger-el (gobj/get refs "trigger")
          ^js label-el   (gobj/get refs "label")
          ^js panel-el   (gobj/get refs "panel")
          open?          (has-attr? el model/attr-open)
          disabled?      (has-attr? el model/attr-disabled)
          label          (or (get-attr el model/attr-label) "")
          placement      (model/normalize-placement (get-attr el model/attr-placement))]

      (set! (.-textContent label-el) label)
      (set-attr! trigger-el "aria-expanded" (if open? "true" "false"))
      (set-bool-attr! trigger-el "disabled" disabled?)
      (set-attr! panel-el "data-placement" placement))))

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
;; Toggle
;; ---------------------------------------------------------------------------
(defn- toggle! [^js el source]
  (when-not (has-attr? el model/attr-disabled)
    (let [currently-open? (has-attr? el model/attr-open)
          next-open?      (not currently-open?)
          detail          (model/toggle-detail next-open? source)
          allowed?        (dispatch-cancelable! el model/event-toggle detail)]
      (when allowed?
        (if next-open?
          (set-attr! el model/attr-open "")
          (remove-attr! el model/attr-open))
        (dispatch! el model/event-change #js {:open next-open?})))))

;; ---------------------------------------------------------------------------
;; Document-level listener management
;; ---------------------------------------------------------------------------
(defn- add-doc-listeners! [^js el]
  (when-let [handlers (gobj/get el k-handlers)]
    (let [doc-click-h   (gobj/get handlers "docClick")
          doc-keydown-h (gobj/get handlers "docKeydown")]
      ;; Delay by one tick so the opening click does not immediately re-close
      (js/setTimeout
       (fn []
         (when (has-attr? el model/attr-open)
           (.addEventListener js/document "click"   doc-click-h)
           (.addEventListener js/document "keydown" doc-keydown-h)))
       0))))

(defn- remove-doc-listeners! [^js el]
  (when-let [handlers (gobj/get el k-handlers)]
    (let [doc-click-h   (gobj/get handlers "docClick")
          doc-keydown-h (gobj/get handlers "docKeydown")]
      (.removeEventListener js/document "click"   doc-click-h)
      (.removeEventListener js/document "keydown" doc-keydown-h))))

;; ---------------------------------------------------------------------------
;; Handler construction
;; ---------------------------------------------------------------------------
(defn- make-handlers [^js el]
  (let [trigger-click-h
        (fn [^js _evt]
          (toggle! el "pointer"))

        trigger-keydown-h
        (fn [^js evt]
          (let [key (.-key evt)]
            (when (or (= key " ") (= key "Enter"))
              (.preventDefault evt)
              (toggle! el "keyboard"))))

        host-focusout-h
        (fn [^js evt]
          (when (has-attr? el model/attr-open)
            (let [related (.-relatedTarget evt)]
              (when (or (nil? related)
                        (not (.contains el related)))
                (toggle! el "focusout")))))

        doc-click-h
        (fn [^js evt]
          (when (has-attr? el model/attr-open)
            (let [path    (array-seq (.composedPath evt))
                  inside? (some #(identical? % el) path)]
              (when-not inside?
                (toggle! el "outside-click")))))

        doc-keydown-h
        (fn [^js evt]
          (when (and (= (.-key evt) "Escape")
                     (has-attr? el model/attr-open))
            (toggle! el "escape")))]

    #js {:triggerClick   trigger-click-h
         :triggerKeydown trigger-keydown-h
         :hostFocusout   host-focusout-h
         :docClick       doc-click-h
         :docKeydown     doc-keydown-h}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-static-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js trigger-el (gobj/get refs "trigger")]
        (.addEventListener trigger-el "click"    (gobj/get handlers "triggerClick"))
        (.addEventListener trigger-el "keydown"  (gobj/get handlers "triggerKeydown"))
        (.addEventListener el         "focusout" (gobj/get handlers "hostFocusout"))))))

(defn- remove-static-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js trigger-el (gobj/get refs "trigger")]
        (.removeEventListener trigger-el "click"    (gobj/get handlers "triggerClick"))
        (.removeEventListener trigger-el "keydown"  (gobj/get handlers "triggerKeydown"))
        (.removeEventListener el         "focusout" (gobj/get handlers "hostFocusout"))))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (remove-static-listeners! el)
  (remove-doc-listeners! el)
  (gobj/set el k-handlers (make-handlers el))
  (add-static-listeners! el)
  (when (has-attr? el model/attr-open)
    (add-doc-listeners! el))
  (render! el))

(defn- disconnected! [^js el]
  (remove-static-listeners! el)
  (remove-doc-listeners! el))

(defn- attribute-changed! [^js el name _old _new]
  (when (gobj/get el k-refs)
    (render! el)
    (when (= name model/attr-open)
      (if (has-attr? el model/attr-open)
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
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (set-attr! this attr-name (str v))
                                (remove-attr! this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Reflected attribute properties
    (define-bool-prop!   proto "open"      model/attr-open)
    (define-bool-prop!   proto "disabled"  model/attr-disabled)
    (define-string-prop! proto "label"     model/attr-label)
    (define-string-prop! proto "placement" model/attr-placement)

    ;; Public methods
    ;; Note: open/close are named show/hide to avoid conflict with the open property
    (aset proto "show"
          (fn [] (this-as ^js this
                          (when-not (has-attr? this model/attr-open)
                            (toggle! this "programmatic")))))

    (aset proto "hide"
          (fn [] (this-as ^js this
                          (when (has-attr? this model/attr-open)
                            (toggle! this "programmatic")))))

    (aset proto "toggle"
          (fn [] (this-as ^js this (toggle! this "programmatic"))))

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
