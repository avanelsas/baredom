(ns baredom.components.x-collapse.x-collapse
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-collapse.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xCollapseRefs")
(def ^:private k-model    "__xCollapseModel")
(def ^:private k-handlers "__xCollapseHandlers")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part            "part")
(def ^:private attr-type            "type")
(def ^:private attr-id              "id")
(def ^:private attr-name            "name")
(def ^:private attr-role            "role")
(def ^:private attr-disabled        "disabled")
(def ^:private attr-aria-controls   "aria-controls")
(def ^:private attr-aria-labelledby "aria-labelledby")
(def ^:private attr-aria-expanded   "aria-expanded")
(def ^:private attr-aria-hidden     "aria-hidden")
(def ^:private attr-aria-disabled   "aria-disabled")

(def ^:private part-container     "container")
(def ^:private part-trigger       "trigger")
(def ^:private part-header-text   "header-text")
(def ^:private part-chevron       "chevron")
(def ^:private part-content       "content")
(def ^:private part-content-inner "content-inner")

(def ^:private id-trigger "trigger")
(def ^:private id-panel   "panel")
(def ^:private val-button "button")
(def ^:private val-region "region")
(def ^:private val-true   "true")
(def ^:private val-false  "false")
(def ^:private val-chevron-glyph "▼")

(def ^:private ev-click         "click")
(def ^:private ev-keydown       "keydown")
(def ^:private ev-transitionend "transitionend")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

(def ^:private rk-trigger     "trigger")
(def ^:private rk-header-text "header-text")
(def ^:private rk-content     "content")

(def ^:private src-pointer      "pointer")
(def ^:private src-keyboard     "keyboard")
(def ^:private src-programmatic "programmatic")

(def ^:private key-space "Space")
(def ^:private key-spc   " ")
(def ^:private key-enter "Enter")

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

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root            (.attachShadow el #js {:mode "open"})
        style-el        (.createElement js/document "style")
        container-el    (.createElement js/document "div")
        trigger-el      (.createElement js/document "button")
        header-text-el  (.createElement js/document "span")
        chevron-el      (.createElement js/document "span")
        content-el      (.createElement js/document "div")
        content-inner-el (.createElement js/document "div")
        slot-el         (.createElement js/document "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! container-el     attr-part          part-container)
    (du/set-attr! trigger-el       attr-part          part-trigger)
    (du/set-attr! trigger-el       attr-type          val-button)
    (du/set-attr! trigger-el       attr-id            id-trigger)
    (du/set-attr! trigger-el       attr-aria-controls id-panel)
    (du/set-attr! header-text-el   attr-part          part-header-text)
    (du/set-attr! chevron-el       attr-part          part-chevron)
    (du/set-attr! chevron-el       attr-aria-hidden   val-true)
    (du/set-attr! content-el       attr-part          part-content)
    (du/set-attr! content-el       attr-id            id-panel)
    (du/set-attr! content-el       attr-role          val-region)
    (du/set-attr! content-el       attr-aria-labelledby id-trigger)
    (du/set-attr! content-inner-el attr-part          part-content-inner)
    (du/set-attr! slot-el          attr-name          model/slot-content)

    (set! (.-textContent chevron-el) val-chevron-glyph)

    (.appendChild trigger-el header-text-el)
    (.appendChild trigger-el chevron-el)

    (.appendChild content-inner-el slot-el)
    (.appendChild content-el content-inner-el)

    (.appendChild container-el trigger-el)
    (.appendChild container-el content-el)

    (.appendChild root style-el)
    (.appendChild root container-el)

    (let [refs #js {}]
      (gobj/set refs "root"        root)
      (gobj/set refs rk-trigger     trigger-el)
      (gobj/set refs rk-header-text header-text-el)
      (gobj/set refs rk-content     content-el)
      (du/setv! el k-refs refs)
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
(defn- clear-anim-styles! [^js content]
  (set! (.-style.height content) "")
  (set! (.-style.transition content) ""))

(defn- animate-height!
  "Animate `content` height from `from-h` to `to-h` over `dur` ms.
  Cleans up inline height/transition on transitionend (or after a
  duration+80ms safety timeout, whichever fires first)."
  [^js content from-h to-h dur]
  (set! (.-style.height content) from-h)
  ;; Force reflow so the height we just set becomes the transition start.
  (.-offsetHeight content)
  (set! (.-style.transition content) (str "height " dur "ms ease"))
  (set! (.-style.height content) to-h)
  (let [tid (js/setTimeout
             (fn on-animate-timeout [] (clear-anim-styles! content))
             (+ dur 80))]
    (letfn [(on-end []
              (js/clearTimeout tid)
              (clear-anim-styles! content)
              (.removeEventListener content ev-transitionend on-end))]
      (.addEventListener content ev-transitionend on-end))))

(defn- start-open! [^js el ^js content]
  (if (prefers-reduced-motion?)
    (clear-anim-styles! content)
    (let [target-h (do (set! (.-style.height content) "0px")
                       (.-offsetHeight content)
                       (str (.-scrollHeight content) "px"))]
      (animate-height! content "0px" target-h (get-duration-ms el)))))

(defn- start-close! [^js el ^js content]
  (if (prefers-reduced-motion?)
    (do (set! (.-style.height content) "0px")
        (set! (.-style.transition content) ""))
    (let [current-h (str (.-offsetHeight content) "px")]
      (animate-height! content current-h "0px" (get-duration-ms el)))))

;; ---------------------------------------------------------------------------
;; Model reading
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  {:open?     (du/has-attr? el model/attr-open)
   :disabled? (du/has-attr? el model/attr-disabled)
   :header    (or (du/get-attr el model/attr-header) "")})

;; ---------------------------------------------------------------------------
;; DOM patching
;; ---------------------------------------------------------------------------
(defn- apply-trigger-state! [^js trigger-el {:keys [open? disabled?]}]
  (du/set-attr! trigger-el attr-aria-expanded (if open? val-true val-false))
  (if disabled?
    (do (du/set-attr! trigger-el attr-disabled "")
        (du/set-attr! trigger-el attr-aria-disabled val-true))
    (do (du/remove-attr! trigger-el attr-disabled)
        (du/remove-attr! trigger-el attr-aria-disabled))))

(defn- apply-header-text! [^js header-text-el {:keys [header]}]
  (set! (.-textContent header-text-el) header))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js trigger-el     (gobj/get refs rk-trigger)
          ^js header-text-el (gobj/get refs rk-header-text)]
      (apply-header-text!    header-text-el m)
      (apply-trigger-state!  trigger-el m)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

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
;; Event handlers (named)
;; ---------------------------------------------------------------------------
(defn- on-trigger-click [^js el ^js _evt]
  (toggle! el src-pointer))

(defn- on-trigger-keydown [^js el ^js evt]
  (let [k (.-key evt)]
    (when (or (= k key-spc) (= k key-enter) (= k key-space))
      (.preventDefault evt)
      (toggle! el src-keyboard))))

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js trigger-el (gobj/get refs rk-trigger)
          click-h        (fn handle-trigger-click   [evt] (on-trigger-click   el evt))
          keydown-h      (fn handle-trigger-keydown [evt] (on-trigger-keydown el evt))
          handlers       #js {}]
      (.addEventListener trigger-el ev-click   click-h)
      (.addEventListener trigger-el ev-keydown keydown-h)
      (gobj/set handlers hk-click   click-h)
      (gobj/set handlers hk-keydown keydown-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js trigger-el (gobj/get refs rk-trigger)]
        (.removeEventListener trigger-el ev-click   (gobj/get handlers hk-click))
        (.removeEventListener trigger-el ev-keydown (gobj/get handlers hk-keydown)))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)
    ;; Animate content when [open] attribute toggles.
    (when (= attr-name model/attr-open)
      (when-let [refs (du/getv el k-refs)]
        (let [^js content-el (gobj/get refs rk-content)
              open?          (du/has-attr? el model/attr-open)]
          (when content-el
            (if open?
              (start-open! el content-el)
              (start-close! el content-el))))))))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; toggle() — public method. .defineProperty with a value descriptor is
  ;; the shared idiom for adding prototype methods (matches x-cancel-dialogue
  ;; and x-alert); bare aset on the prototype is the bespoke pattern audited
  ;; out of x-button (PR #155).
  (.defineProperty js/Object proto "toggle"
                   #js {:value (fn carousel-toggle []
                                 (this-as ^js this (toggle! this src-programmatic)))
                        :writable true
                        :configurable true}))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
