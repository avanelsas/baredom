(ns baredom.components.x-fieldset.x-fieldset
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-fieldset.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs "__xFieldsetRefs")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-fieldset-border-color:var(--x-color-border, #d1d5db);"
   "--x-fieldset-border-width:1px;"
   "--x-fieldset-border-radius:var(--x-radius-md, 8px);"
   "--x-fieldset-padding:1rem;"
   "--x-fieldset-gap:0.75rem;"
   "--x-fieldset-bg:var(--x-color-surface,transparent);"
   "--x-fieldset-legend-color:var(--x-color-text-muted,#374151);"
   "--x-fieldset-legend-font-size:var(--x-font-size-sm, 0.875rem);"
   "--x-fieldset-legend-font-weight:600;"
   "--x-fieldset-legend-padding:0 0.375rem;"
   "--x-fieldset-disabled-opacity:0.45;"
   "--x-fieldset-input-bg:var(--x-color-surface,#ffffff);"
   "--x-fieldset-input-color:var(--x-color-text,#0f172a);"
   "--x-fieldset-input-border:var(--x-color-border,#d1d5db);"
   "--x-fieldset-input-radius:var(--x-radius-sm,0.375rem);"
   "--x-fieldset-input-focus:var(--x-color-focus-ring,#60a5fa);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-fieldset-border-color:var(--x-color-border, #374151);"
   "--x-fieldset-legend-color:var(--x-color-text-muted,#d1d5db);"
   "--x-fieldset-input-bg:var(--x-color-surface,#1f2937);"
   "--x-fieldset-input-color:var(--x-color-text,#e5e7eb);"
   "--x-fieldset-input-border:var(--x-color-border,#374151);"
   "--x-fieldset-input-focus:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "[part=root]{"
   "display:block;"
   "border:var(--x-fieldset-border-width) solid var(--x-fieldset-border-color);"
   "border-radius:var(--x-fieldset-border-radius);"
   "padding:var(--x-fieldset-padding);"
   "background:var(--x-fieldset-bg);"
   "position:relative;"
   "}"
   ":host([data-disabled]) [part=root]{"
   "opacity:var(--x-fieldset-disabled-opacity);"
   "}"
   "[part=legend]{"
   "display:block;"
   "position:absolute;"
   "top:calc(-0.5em);"
   "left:calc(var(--x-fieldset-border-radius) - 0.375rem);"
   "padding:var(--x-fieldset-legend-padding);"
   "color:var(--x-fieldset-legend-color);"
   "font-size:var(--x-fieldset-legend-font-size);"
   "font-weight:var(--x-fieldset-legend-font-weight);"
   "background:inherit;"
   "line-height:1;"
   "white-space:nowrap;"
   "}"
   "[part=legend][hidden]{"
   "display:none;"
   "}"
   "[part=content]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-fieldset-gap);"
   "}"
   ))

(def ^:private light-dom-style-text
  (str
   "x-fieldset input,"
   "x-fieldset select,"
   "x-fieldset textarea{"
   "background:var(--x-fieldset-input-bg);"
   "color:var(--x-fieldset-input-color);"
   "border:1px solid var(--x-fieldset-input-border);"
   "border-radius:var(--x-fieldset-input-radius);"
   "padding:0.5rem 0.75rem;"
   "font:inherit;"
   "outline:none;"
   "box-sizing:border-box;"
   "}"
   "x-fieldset input:focus,"
   "x-fieldset select:focus,"
   "x-fieldset textarea:focus{"
   "border-color:var(--x-fieldset-input-focus);"
   "box-shadow:0 0 0 2px var(--x-fieldset-input-focus);"
   "}"))

(def ^:private light-style-id "x-fieldset-input-styles")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [^js tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        root-el    (make-el "div")
        legend-el  (make-el "div")
        content-el (make-el "div")
        slot-el    (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! root-el    "part" "root")
    (du/set-attr! root-el    "role" "group")
    (du/set-attr! legend-el  "part" "legend")
    (du/set-attr! legend-el  "id"   "x-fieldset-legend")
    (du/set-attr! content-el "part" "content")

    (.appendChild content-el slot-el)
    (.appendChild root-el legend-el)
    (.appendChild root-el content-el)
    (.appendChild root style-el)
    (.appendChild root root-el)

    ;; Inject shared light-DOM style for native inputs (once per document)
    (when-not (.getElementById js/document light-style-id)
      (let [ls (make-el "style")]
        (du/set-attr! ls "id" light-style-id)
        (set! (.-textContent ls) light-dom-style-text)
        (.appendChild (.-head js/document) ls)))

    (let [refs #js {:root root :root-el root-el :legend-el legend-el :content-el content-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:legend-raw          (du/get-attr el model/attr-legend)
    :disabled-present?   (du/has-attr? el model/attr-disabled)
    :aria-label-raw      (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw (du/get-attr el model/attr-aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js root-el    (gobj/get refs "root-el")
          ^js legend-el  (gobj/get refs "legend-el")
          ^js content-el (gobj/get refs "content-el")
          m              (read-model el)
          legend        (:legend m)
          visible?      (:legend-visible? m)
          disabled?     (:disabled? m)]

      ;; Legend text and visibility
      (set! (.-textContent legend-el) legend)
      (if visible?
        (du/remove-attr! legend-el "hidden")
        (du/set-attr! legend-el "hidden" ""))

      ;; aria-labelledby vs aria-label on root[part=root]
      (if-let [aria-lbl (:aria-label m)]
        (do
          (du/set-attr! root-el "aria-label" aria-lbl)
          (du/remove-attr! root-el "aria-labelledby"))
        (do
          (du/remove-attr! root-el "aria-label")
          (if visible?
            (du/set-attr! root-el "aria-labelledby" "x-fieldset-legend")
            (du/remove-attr! root-el "aria-labelledby"))))

      ;; aria-describedby
      (if-let [v (:aria-describedby m)]
        (du/set-attr! root-el "aria-describedby" v)
        (du/remove-attr! root-el "aria-describedby"))

      ;; data-disabled on host for CSS hooks
      (du/set-bool-attr! el "data-disabled" disabled?)
      ;; inert on content-el blocks all interaction with slotted children
      (du/set-bool-attr! content-el "inert" disabled?))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (render! el))

(defn- disconnected! [^js _el])

(defn- attribute-changed! [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto "legend" model/attr-legend)
  (du/define-bool-prop! proto "disabled" model/attr-disabled))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
