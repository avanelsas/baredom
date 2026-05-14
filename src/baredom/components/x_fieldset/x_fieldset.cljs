(ns baredom.components.x-fieldset.x-fieldset
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-fieldset.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xFieldsetRefs")
(def ^:private k-model "__xFieldsetModel")

;; ── Refs / string-literal constants ───────────────────────────────────────
(def ^:private rk-root-el    "root-el")
(def ^:private rk-legend-el  "legend-el")
(def ^:private rk-content-el "content-el")

(def ^:private attr-part            "part")
(def ^:private attr-role            "role")
(def ^:private attr-id              "id")
(def ^:private attr-hidden          "hidden")
(def ^:private attr-inert           "inert")
(def ^:private attr-aria-label      "aria-label")
(def ^:private attr-aria-labelledby "aria-labelledby")
(def ^:private attr-aria-describedby "aria-describedby")
(def ^:private attr-data-disabled   "data-disabled")

(def ^:private part-root    "root")
(def ^:private part-legend  "legend")
(def ^:private part-content "content")

(def ^:private val-role-group "group")
(def ^:private id-legend      "x-fieldset-legend")

;; ── Styles ────────────────────────────────────────────────────────────────
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
   "}"))

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

;; ── DOM helpers ───────────────────────────────────────────────────────────

(defn- ensure-light-dom-styles!
  "Inject the shared light-DOM <style> into document.head exactly once.
  Idempotent — safe to call from every instance's connected!. Hoisted out
  of make-shadow! so the per-instance shadow build doesn't conflate the
  one-document-wide effect with the per-instance setup."
  []
  (when-not (.getElementById js/document light-style-id)
    (let [ls (.createElement js/document "style")]
      (du/set-attr! ls attr-id light-style-id)
      (set! (.-textContent ls) light-dom-style-text)
      (.appendChild (.-head js/document) ls))))

;; ── Shadow DOM construction ───────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        root-el    (.createElement js/document "div")
        legend-el  (.createElement js/document "div")
        content-el (.createElement js/document "div")
        slot-el    (.createElement js/document "slot")
        refs       #js {}]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! root-el    attr-part part-root)
    (du/set-attr! root-el    attr-role val-role-group)
    (du/set-attr! legend-el  attr-part part-legend)
    (du/set-attr! legend-el  attr-id   id-legend)
    (du/set-attr! content-el attr-part part-content)

    (.appendChild content-el slot-el)
    (.appendChild root-el legend-el)
    (.appendChild root-el content-el)
    (.appendChild root style-el)
    (.appendChild root root-el)

    (gobj/set refs "root"        root)
    (gobj/set refs rk-root-el    root-el)
    (gobj/set refs rk-legend-el  legend-el)
    (gobj/set refs rk-content-el content-el)
    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (make-shadow! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:legend-raw           (du/get-attr el model/attr-legend)
    :disabled-present?    (du/has-attr? el model/attr-disabled)
    :aria-label-raw       (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw (du/get-attr el model/attr-aria-describedby)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────
(defn- apply-legend! [^js legend-el {:keys [legend legend-visible?]}]
  (set! (.-textContent legend-el) legend)
  (if legend-visible?
    (du/remove-attr! legend-el attr-hidden)
    (du/set-attr!    legend-el attr-hidden "")))

(defn- apply-host-aria! [^js root-el {:keys [aria-label aria-describedby legend-visible?]}]
  (if aria-label
    (do (du/set-attr!    root-el attr-aria-label      aria-label)
        (du/remove-attr! root-el attr-aria-labelledby))
    (do (du/remove-attr! root-el attr-aria-label)
        (if legend-visible?
          (du/set-attr!    root-el attr-aria-labelledby id-legend)
          (du/remove-attr! root-el attr-aria-labelledby))))
  (if aria-describedby
    (du/set-attr!    root-el attr-aria-describedby aria-describedby)
    (du/remove-attr! root-el attr-aria-describedby)))

(defn- apply-disabled-state! [^js el ^js content-el {:keys [disabled?]}]
  (du/set-bool-attr! el         attr-data-disabled disabled?)
  ;; inert on content-el blocks all interaction with slotted children.
  (du/set-bool-attr! content-el attr-inert         disabled?))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js root-el    (gobj/get refs rk-root-el)
          ^js legend-el  (gobj/get refs rk-legend-el)
          ^js content-el (gobj/get refs rk-content-el)]
      (apply-legend!         legend-el m)
      (apply-host-aria!      root-el   m)
      (apply-disabled-state! el        content-el m)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-light-dom-styles!)
  (ensure-refs! el)
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
