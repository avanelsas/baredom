(ns baredom.components.x-collapse.x-collapse
  (:require [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-collapse.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xCollapseRefs")
(def ^:private k-handlers "__xCollapseHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-collapse-border-radius:var(--x-radius-md, 8px);"
   "--x-collapse-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-collapse-bg:var(--x-color-surface, #ffffff);"
   "--x-collapse-trigger-bg:var(--x-color-surface-hover, #f8fafc);"
   "--x-collapse-trigger-bg-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-collapse-trigger-color:var(--x-color-text, #0f172a);"
   "--x-collapse-trigger-padding:0.75rem 1rem;"
   "--x-collapse-content-padding:1rem;"
   "--x-collapse-font-size:0.9375rem;"
   "--x-collapse-font-weight:600;"
   "--x-collapse-chevron-color:var(--x-color-text-muted, #64748b);"
   "--x-collapse-focus-ring:var(--x-color-focus-ring, #60a5fa);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-collapse-border:1px solid var(--x-color-border,#334155);"
   "--x-collapse-bg:var(--x-color-surface,#1e293b);"
   "--x-collapse-trigger-bg:var(--x-color-surface-hover,#0f172a);"
   "--x-collapse-trigger-bg-hover:var(--x-color-surface-active,#1e293b);"
   "--x-collapse-trigger-color:var(--x-color-text,#e2e8f0);"
   "--x-collapse-chevron-color:var(--x-color-text-muted, #94a3b8);"
   "--x-collapse-focus-ring:var(--x-color-focus-ring, #93c5fd);"
   "}"
   "}"
   "[part=container]{"
   "box-sizing:border-box;"
   "border:var(--x-collapse-border);"
   "border-radius:var(--x-collapse-border-radius);"
   "background:var(--x-collapse-bg);"
   "overflow:hidden;"
   "}"
   "[part=trigger]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:flex;"
   "align-items:center;"
   "justify-content:space-between;"
   "width:100%;"
   "padding:var(--x-collapse-trigger-padding);"
   "background:var(--x-collapse-trigger-bg);"
   "color:var(--x-collapse-trigger-color);"
   "font-size:var(--x-collapse-font-size);"
   "font-weight:var(--x-collapse-font-weight);"
   "cursor:pointer;"
   "user-select:none;"
   "}"
   "[part=trigger][disabled]{"
   "cursor:default;"
   "opacity:0.55;"
   "}"
   "[part=trigger]:hover:not([disabled]){"
   "background:var(--x-collapse-trigger-bg-hover);"
   "}"
   "[part=trigger]:focus-visible{"
   "outline:none;"
   "box-shadow:inset 0 0 0 2px var(--x-collapse-focus-ring);"
   "}"
   "[part=header-text]{"
   "flex:1;"
   "text-align:start;"
   "}"
   "[part=chevron]{"
   "display:inline-block;"
   "margin-inline-start:0.5rem;"
   "color:var(--x-collapse-chevron-color);"
   "transition:transform 200ms ease;"
   "flex-shrink:0;"
   "}"
   ":host([open]) [part=chevron]{"
   "transform:rotate(180deg);"
   "}"
   "[part=content]{"
   "overflow:hidden;"
   "height:0;"
   "}"
   ":host([open]) [part=content]{"
   "height:auto;"
   "}"
   "[part=content-inner]{"
   "padding:var(--x-collapse-content-padding);"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=content]{transition:none !important;}"
   "[part=chevron]{transition:none !important;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root            (.attachShadow el #js {:mode "open"})
        style-el        (make-el "style")
        container-el    (make-el "div")
        trigger-el      (make-el "button")
        header-text-el  (make-el "span")
        chevron-el      (make-el "span")
        content-el      (make-el "div")
        content-inner-el (make-el "div")
        slot-el         (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! container-el    "part" "container")
    (du/set-attr! trigger-el      "part" "trigger")
    (du/set-attr! trigger-el      "type" "button")
    (du/set-attr! trigger-el      "id"   "trigger")
    (du/set-attr! trigger-el      "aria-controls" "panel")
    (du/set-attr! header-text-el  "part" "header-text")
    (du/set-attr! chevron-el      "part" "chevron")
    (du/set-attr! chevron-el      "aria-hidden" "true")
    (du/set-attr! content-el      "part" "content")
    (du/set-attr! content-el      "id"   "panel")
    (du/set-attr! content-el      "role" "region")
    (du/set-attr! content-el      "aria-labelledby" "trigger")
    (du/set-attr! content-inner-el "part" "content-inner")
    (du/set-attr! slot-el         "name" model/slot-content)

    (set! (.-textContent chevron-el) "\u25BC")

    (.appendChild trigger-el header-text-el)
    (.appendChild trigger-el chevron-el)

    (.appendChild content-inner-el slot-el)
    (.appendChild content-el content-inner-el)

    (.appendChild container-el trigger-el)
    (.appendChild container-el content-el)

    (.appendChild root style-el)
    (.appendChild root container-el)

    (let [refs #js {:root        root
                    :trigger     trigger-el
                    :header-text header-text-el
                    :content     content-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Reduced motion check
;; ---------------------------------------------------------------------------
(defn- prefers-reduced-motion? []
  (and js/window.matchMedia
       (.-matches (.matchMedia js/window "(prefers-reduced-motion: reduce)"))))

;; ---------------------------------------------------------------------------
;; Duration helper
;; ---------------------------------------------------------------------------
(defn- get-duration-ms [^js el]
  (let [d (du/get-attr el model/attr-duration-ms)]
    (if d
      (let [n (js/parseInt d 10)]
        (if (js/isNaN n) model/default-duration-ms (max 0 (min 2000 n))))
      model/default-duration-ms)))

;; ---------------------------------------------------------------------------
;; Height animation
;; ---------------------------------------------------------------------------
(defn- start-open! [^js el ^js content]
  (if (prefers-reduced-motion?)
    (do
      (set! (.-style.height content) "")
      (set! (.-style.transition content) ""))
    (let [dur (get-duration-ms el)]
      (set! (.-style.height content) "0px")
      ;; Force reflow
      (.-offsetHeight content)
      (let [target-h (str (.-scrollHeight content) "px")]
        (set! (.-style.transition content)
              (str "height " dur "ms " "ease"))
        (set! (.-style.height content) target-h)
        (let [tid (js/setTimeout
                   (fn []
                     (set! (.-style.height content) "")
                     (set! (.-style.transition content) ""))
                   (+ dur 80))
              handler-ref #js {:fn nil}
              handler (fn []
                        (js/clearTimeout tid)
                        (set! (.-style.height content) "")
                        (set! (.-style.transition content) "")
                        (.removeEventListener
                         content "transitionend"
                         (gobj/get handler-ref "fn")))]
          (gobj/set handler-ref "fn" handler)
          (.addEventListener content "transitionend" handler))))))

(defn- start-close! [^js el ^js content]
  (if (prefers-reduced-motion?)
    (do
      (set! (.-style.height content) "0px")
      (set! (.-style.transition content) ""))
    (let [current-h (str (.-offsetHeight content) "px")
          dur       (get-duration-ms el)]
      (set! (.-style.height content) current-h)
      ;; Force reflow
      (.-offsetHeight content)
      (set! (.-style.transition content)
            (str "height " dur "ms " "ease"))
      (set! (.-style.height content) "0px")
      (let [tid (js/setTimeout
                 (fn []
                   (set! (.-style.height content) "")
                   (set! (.-style.transition content) ""))
                 (+ dur 80))
            handler-ref #js {:fn nil}
            handler (fn []
                      (js/clearTimeout tid)
                      (set! (.-style.height content) "")
                      (set! (.-style.transition content) "")
                      (.removeEventListener
                       content "transitionend"
                       (gobj/get handler-ref "fn")))]
        (gobj/set handler-ref "fn" handler)
        (.addEventListener content "transitionend" handler)))))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js trigger-el     (gobj/get refs "trigger")
          ^js header-text-el (gobj/get refs "header-text")
          open?              (du/has-attr? el model/attr-open)
          disabled?          (du/has-attr? el model/attr-disabled)
          header             (or (du/get-attr el model/attr-header) "")]

      (set! (.-textContent header-text-el) header)

      (du/set-attr! trigger-el "aria-expanded" (if open? "true" "false"))

      (if disabled?
        (do (du/set-attr! trigger-el "disabled" "")
            (du/set-attr! trigger-el "aria-disabled" "true"))
        (do (du/remove-attr! trigger-el "disabled")
            (du/remove-attr! trigger-el "aria-disabled"))))))

;; ---------------------------------------------------------------------------
;; Dispatch helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Toggle
;; ---------------------------------------------------------------------------
(defn- toggle! [^js el source]
  (when-not (du/has-attr? el model/attr-disabled)
    (let [currently-open? (du/has-attr? el model/attr-open)
          next-open?      (not currently-open?)
          detail          (model/toggle-detail next-open? source)
          allowed?        (du/dispatch-cancelable! el model/event-toggle detail)]
      (when allowed?
        (if next-open?
          (du/set-attr! el model/attr-open "")
          (du/remove-attr! el model/attr-open))
        (du/dispatch! el model/event-change #js {:open next-open?})))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- make-trigger-handler [^js el]
  (fn [^js _evt]
    (toggle! el "pointer")))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (let [key (.-key evt)]
      (when (or (= key " ") (= key "Enter"))
        (.preventDefault evt)
        (toggle! el "keyboard")))))

(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js trigger-el (gobj/get refs "trigger")
          click-h        (make-trigger-handler el)
          keydown-h      (make-keydown-handler el)
          handlers       #js {:click click-h :keydown keydown-h}]
      (.addEventListener trigger-el "click"   click-h)
      (.addEventListener trigger-el "keydown" keydown-h)
      (gobj/set el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js trigger-el (gobj/get refs "trigger")]
        (.removeEventListener trigger-el "click"   (gobj/get handlers "click"))
        (.removeEventListener trigger-el "keydown" (gobj/get handlers "keydown")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el name _old _new]
  (when (gobj/get el k-refs)
    (render! el)
    ;; Animate content when [open] attribute changes
    (when (= name model/attr-open)
      (let [refs       (gobj/get el k-refs)
            ^js content-el (when refs (gobj/get refs "content"))
            open?      (du/has-attr? el model/attr-open)]
        (when content-el
          (if open?
            (start-open! el content-el)
            (start-close! el content-el)))))))

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

    ;; Properties
    (du/define-bool-prop!   proto "open"       model/attr-open)
    (du/define-bool-prop!   proto "disabled"   model/attr-disabled)
    (du/define-string-prop! proto "header"     model/attr-header)
    (du/define-number-prop! proto "durationMs" model/attr-duration-ms model/default-duration-ms)

    ;; Public toggle() method
    (aset proto "toggle"
          (fn [] (this-as ^js this (toggle! this "programmatic"))))

    ;; Lifecycle
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
