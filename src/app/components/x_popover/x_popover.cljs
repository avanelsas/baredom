(ns app.components.x-popover.x-popover
  (:require [goog.object :as gobj]
            [app.components.x-popover.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xPopoverRefs")
(def ^:private k-handlers "__xPopoverHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-popover-panel-bg:#ffffff;"
   "--x-popover-panel-border:1px solid #e2e8f0;"
   "--x-popover-panel-radius:8px;"
   "--x-popover-panel-shadow:0 4px 16px rgba(0,0,0,0.12);"
   "--x-popover-panel-min-width:12rem;"
   "--x-popover-panel-max-width:24rem;"
   "--x-popover-panel-max-height:24rem;"
   "--x-popover-panel-offset:4px;"
   "--x-popover-panel-z:1000;"
   "--x-popover-header-padding:0.625rem 0.75rem 0.625rem 0.875rem;"
   "--x-popover-header-border:1px solid #e2e8f0;"
   "--x-popover-heading-color:#0f172a;"
   "--x-popover-heading-font-size:0.9375rem;"
   "--x-popover-heading-font-weight:600;"
   "--x-popover-close-bg:transparent;"
   "--x-popover-close-bg-hover:#f1f5f9;"
   "--x-popover-close-color:#64748b;"
   "--x-popover-close-color-hover:#0f172a;"
   "--x-popover-close-radius:4px;"
   "--x-popover-close-size:1.5rem;"
   "--x-popover-focus-ring:#60a5fa;"
   "--x-popover-body-padding:0.875rem;"
   "--x-popover-body-color:#334155;"
   "--x-popover-body-font-size:0.9375rem;"
   "--x-popover-footer-padding:0.625rem 0.875rem;"
   "--x-popover-footer-border:1px solid #e2e8f0;"
   "--x-popover-arrow-size:8px;"
   "--x-popover-arrow-bg:#ffffff;"
   "--x-popover-arrow-border:#e2e8f0;"
   "--x-popover-transition-duration:150ms;"
   "--x-popover-transition-easing:ease;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-popover-panel-bg:#1e293b;"
   "--x-popover-panel-border:1px solid #334155;"
   "--x-popover-panel-shadow:0 4px 24px rgba(0,0,0,0.4);"
   "--x-popover-header-border:1px solid #334155;"
   "--x-popover-heading-color:#e2e8f0;"
   "--x-popover-close-bg-hover:#334155;"
   "--x-popover-close-color:#94a3b8;"
   "--x-popover-close-color-hover:#e2e8f0;"
   "--x-popover-focus-ring:#93c5fd;"
   "--x-popover-body-color:#cbd5e1;"
   "--x-popover-footer-border:1px solid #334155;"
   "--x-popover-arrow-bg:#1e293b;"
   "--x-popover-arrow-border:#334155;"
   "}"
   "}"
   "[part=trigger]{"
   "display:inline-block;"
   "}"
   "[part=panel]{"
   "position:absolute;"
   "z-index:var(--x-popover-panel-z);"
   "box-sizing:border-box;"
   "background:var(--x-popover-panel-bg);"
   "border:var(--x-popover-panel-border);"
   "border-radius:var(--x-popover-panel-radius);"
   "box-shadow:var(--x-popover-panel-shadow);"
   "min-width:var(--x-popover-panel-min-width);"
   "max-width:var(--x-popover-panel-max-width);"
   "max-height:var(--x-popover-panel-max-height);"
   "overflow-y:auto;"
   "visibility:hidden;"
   "pointer-events:none;"
   "opacity:0;"
   "transform:scale(0.96) translateY(-4px);"
   "transition:"
   "opacity var(--x-popover-transition-duration) var(--x-popover-transition-easing),"
   "transform var(--x-popover-transition-duration) var(--x-popover-transition-easing),"
   "visibility 0s var(--x-popover-transition-duration);"
   "}"
   ":host([open]) [part=panel]{"
   "visibility:visible;"
   "pointer-events:auto;"
   "opacity:1;"
   "transform:scale(1) translateY(0);"
   "transition:"
   "opacity var(--x-popover-transition-duration) var(--x-popover-transition-easing),"
   "transform var(--x-popover-transition-duration) var(--x-popover-transition-easing),"
   "visibility 0s 0s;"
   "}"
   "[part=panel][data-placement=bottom-start]{"
   "top:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"
   "left:0;"
   "transform-origin:top left;"
   "}"
   "[part=panel][data-placement=bottom-end]{"
   "top:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"
   "right:0;"
   "transform-origin:top right;"
   "}"
   "[part=panel][data-placement=top-start]{"
   "bottom:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"
   "left:0;"
   "transform-origin:bottom left;"
   "}"
   "[part=panel][data-placement=top-end]{"
   "bottom:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"
   "right:0;"
   "transform-origin:bottom right;"
   "}"
   "[part=arrow]{"
   "position:absolute;"
   "width:var(--x-popover-arrow-size);"
   "height:var(--x-popover-arrow-size);"
   "background:var(--x-popover-arrow-bg);"
   "transform:rotate(45deg);"
   "pointer-events:none;"
   "}"
   "[part=panel][data-placement=bottom-start] [part=arrow],"
   "[part=panel][data-placement=bottom-end] [part=arrow]{"
   "top:calc(var(--x-popover-arrow-size) / -2 - 1px);"
   "border-left:1px solid var(--x-popover-arrow-border);"
   "border-top:1px solid var(--x-popover-arrow-border);"
   "}"
   "[part=panel][data-placement=bottom-start] [part=arrow]{"
   "left:1rem;"
   "}"
   "[part=panel][data-placement=bottom-end] [part=arrow]{"
   "right:1rem;"
   "}"
   "[part=panel][data-placement=top-start] [part=arrow],"
   "[part=panel][data-placement=top-end] [part=arrow]{"
   "bottom:calc(var(--x-popover-arrow-size) / -2 - 1px);"
   "border-right:1px solid var(--x-popover-arrow-border);"
   "border-bottom:1px solid var(--x-popover-arrow-border);"
   "}"
   "[part=panel][data-placement=top-start] [part=arrow]{"
   "left:1rem;"
   "}"
   "[part=panel][data-placement=top-end] [part=arrow]{"
   "right:1rem;"
   "}"
   "[part=header]{"
   "display:flex;"
   "align-items:center;"
   "justify-content:space-between;"
   "padding:var(--x-popover-header-padding);"
   "border-bottom:var(--x-popover-header-border);"
   "gap:0.5rem;"
   "}"
   "[part=heading]{"
   "flex:1;"
   "font-size:var(--x-popover-heading-font-size);"
   "font-weight:var(--x-popover-heading-font-weight);"
   "color:var(--x-popover-heading-color);"
   "font-family:inherit;"
   "}"
   "[part=close-button]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-popover-close-size);"
   "height:var(--x-popover-close-size);"
   "background:var(--x-popover-close-bg);"
   "color:var(--x-popover-close-color);"
   "border-radius:var(--x-popover-close-radius);"
   "cursor:pointer;"
   "flex-shrink:0;"
   "transition:background 100ms ease,color 100ms ease;"
   "}"
   "[part=close-button]:hover{"
   "background:var(--x-popover-close-bg-hover);"
   "color:var(--x-popover-close-color-hover);"
   "}"
   "[part=close-button]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 2px var(--x-popover-focus-ring);"
   "}"
   "[part=close-button][hidden]{display:none;}"
   "[part=body]{"
   "padding:var(--x-popover-body-padding);"
   "color:var(--x-popover-body-color);"
   "font-size:var(--x-popover-body-font-size);"
   "font-family:inherit;"
   "}"
   "[part=footer]{"
   "padding:var(--x-popover-footer-padding);"
   "border-top:var(--x-popover-footer-border);"
   "}"
   "[part=footer]:not(:has(slot[name=footer] > *)){"
   "display:none;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none !important;}"
   "[part=close-button]{transition:none !important;}"
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
  (let [root         (.attachShadow el #js {:mode "open"})
        style-el     (make-el "style")
        trigger-el   (make-el "span")
        trigger-slot (make-el "slot")
        panel-el     (make-el "div")
        arrow-el     (make-el "div")
        header-el    (make-el "div")
        heading-el   (make-el "span")
        close-btn    (make-el "button")
        body-el      (make-el "div")
        body-slot    (make-el "slot")
        footer-el    (make-el "div")
        footer-slot  (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (set-attr! trigger-el   "part" "trigger")
    (set-attr! trigger-slot "name" "trigger")

    (set-attr! panel-el "part"           "panel")
    (set-attr! panel-el "role"           "dialog")
    (set-attr! panel-el "inert"           "")
    (set-attr! panel-el "data-placement" model/default-placement)

    (set-attr! arrow-el "part"        "arrow")
    (set-attr! arrow-el "aria-hidden" "true")

    (set-attr! header-el "part" "header")

    (set-attr! heading-el "part" "heading")
    (set-attr! heading-el "id"   "popover-heading")

    (set-attr! close-btn "part"       "close-button")
    (set-attr! close-btn "type"       "button")
    (set-attr! close-btn "aria-label" model/default-close-label)
    (set! (.-innerHTML close-btn)
          (str "<svg width=\"14\" height=\"14\" viewBox=\"0 0 14 14\" fill=\"none\""
               " xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"
               "<path d=\"M1 1L13 13M13 1L1 13\" stroke=\"currentColor\""
               " stroke-width=\"2\" stroke-linecap=\"round\"/></svg>"))

    (set-attr! body-el    "part" "body")
    (set-attr! footer-el  "part" "footer")
    (set-attr! footer-slot "name" "footer")

    (.appendChild trigger-el trigger-slot)

    (.appendChild header-el heading-el)
    (.appendChild header-el close-btn)

    (.appendChild body-el body-slot)

    (.appendChild footer-el footer-slot)

    (.appendChild panel-el arrow-el)
    (.appendChild panel-el header-el)
    (.appendChild panel-el body-el)
    (.appendChild panel-el footer-el)

    (.appendChild root style-el)
    (.appendChild root trigger-el)
    (.appendChild root panel-el)

    (let [refs #js {:trigger  trigger-el
                    :panel    panel-el
                    :heading  heading-el
                    :closeBtn close-btn
                    :body     body-el
                    :footer   footer-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js panel-el   (gobj/get refs "panel")
          ^js heading-el (gobj/get refs "heading")
          ^js close-btn  (gobj/get refs "closeBtn")
          open?          (has-attr? el model/attr-open)
          no-close?      (has-attr? el model/attr-no-close)
          heading        (or (get-attr el model/attr-heading) "")
          close-label    (or (get-attr el model/attr-close-label) model/default-close-label)
          placement      (model/normalize-placement (get-attr el model/attr-placement))]

      (set-attr! panel-el "data-placement" placement)
      (set-bool-attr! panel-el "inert" (not open?))

      (if (not= heading "")
        (set-attr! panel-el "aria-labelledby" "popover-heading")
        (remove-attr! panel-el "aria-labelledby"))

      (set! (.-textContent heading-el) heading)
      (set-attr! close-btn "aria-label" close-label)
      (set-bool-attr! close-btn "hidden" no-close?))))

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
;; Open / Close
;; ---------------------------------------------------------------------------
(defn- do-open! [^js el source]
  (when (and (not (has-attr? el model/attr-disabled))
             (not (has-attr? el model/attr-open)))
    (let [detail   (model/toggle-detail true source)
          allowed? (dispatch-cancelable! el model/event-toggle detail)]
      (when allowed?
        (set-attr! el model/attr-open "")
        (dispatch! el model/event-change #js {:open true})))))

(defn- do-close! [^js el source]
  (when (and (not (has-attr? el model/attr-disabled))
             (has-attr? el model/attr-open))
    (let [detail   (model/toggle-detail false source)
          allowed? (dispatch-cancelable! el model/event-toggle detail)]
      (when allowed?
        (remove-attr! el model/attr-open)
        (dispatch! el model/event-change #js {:open false})))))

(defn- toggle! [^js el source]
  (if (has-attr? el model/attr-open)
    (do-close! el source)
    (do-open! el source)))

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

        close-btn-click-h
        (fn [^js _evt]
          (do-close! el "close-button"))

        host-focusout-h
        (fn [^js evt]
          (when (has-attr? el model/attr-open)
            (let [related (.-relatedTarget evt)]
              (when (or (nil? related)
                        (not (.contains el related)))
                (do-close! el "focusout")))))

        doc-click-h
        (fn [^js evt]
          (when (has-attr? el model/attr-open)
            (let [path    (array-seq (.composedPath evt))
                  inside? (some #(identical? % el) path)]
              (when-not inside?
                (do-close! el "outside-click")))))

        doc-keydown-h
        (fn [^js evt]
          (when (and (= (.-key evt) "Escape")
                     (has-attr? el model/attr-open))
            (do-close! el "escape")))]

    #js {:triggerClick  trigger-click-h
         :closeBtnClick close-btn-click-h
         :hostFocusout  host-focusout-h
         :docClick      doc-click-h
         :docKeydown    doc-keydown-h}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-static-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js trigger-el (gobj/get refs "trigger")
            ^js close-btn  (gobj/get refs "closeBtn")]
        (.addEventListener trigger-el "click"    (gobj/get handlers "triggerClick"))
        (.addEventListener close-btn  "click"    (gobj/get handlers "closeBtnClick"))
        (.addEventListener el         "focusout" (gobj/get handlers "hostFocusout"))))))

(defn- remove-static-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js trigger-el (gobj/get refs "trigger")
            ^js close-btn  (gobj/get refs "closeBtn")]
        (.removeEventListener trigger-el "click"    (gobj/get handlers "triggerClick"))
        (.removeEventListener close-btn  "click"    (gobj/get handlers "closeBtnClick"))
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
    (define-bool-prop!   proto "open"       model/attr-open)
    (define-bool-prop!   proto "disabled"   model/attr-disabled)
    (define-bool-prop!   proto "noClose"    model/attr-no-close)
    (define-string-prop! proto "placement"  model/attr-placement)
    (define-string-prop! proto "heading"    model/attr-heading)
    (define-string-prop! proto "closeLabel" model/attr-close-label)

    ;; Public methods
    (aset proto "show"
          (fn [] (this-as ^js this (do-open! this "programmatic"))))

    (aset proto "hide"
          (fn [] (this-as ^js this (do-close! this "programmatic"))))

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
