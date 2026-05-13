(ns baredom.components.x-table-row.x-table-row
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-table-row.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTableRowRefs")
(def ^:private k-model    "__xTableRowModel")
(def ^:private k-handlers "__xTableRowHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:grid;"
   "grid-template-columns:subgrid;"
   "grid-column:1 / -1;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-table-row-bg:transparent;"
   "--x-table-row-hover-bg:rgba(0,0,0,0.03);"
   "--x-table-row-selected-bg:rgba(59,130,246,0.08);"
   "--x-table-row-selected-hover-bg:rgba(59,130,246,0.12);"
   "--x-table-row-focus-ring:var(--x-color-focus-ring,rgba(59,130,246,0.5));"
   "--x-table-row-transition-duration:var(--x-transition-duration,150ms);"
   "--x-table-row-disabled-opacity:0.45;"
   "--x-table-row-cursor:pointer;"
   "background:var(--x-table-row-bg);"
   "transition:background var(--x-table-row-transition-duration) ease;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-table-row-hover-bg:rgba(255,255,255,0.04);"
   "--x-table-row-selected-bg:rgba(99,160,255,0.12);"
   "--x-table-row-selected-hover-bg:rgba(99,160,255,0.16);}}"

   ":host([data-selected]){"
   "background:var(--x-table-row-selected-bg);}"

   ":host([data-interactive]){"
   "cursor:var(--x-table-row-cursor);}"

   ":host([data-interactive]:hover){"
   "background:var(--x-table-row-hover-bg);}"

   ":host([data-interactive][data-selected]:hover){"
   "background:var(--x-table-row-selected-hover-bg);}"

   ":host([data-interactive]:focus){"
   "outline:none;}"

   ":host([data-interactive]:focus-visible){"
   "outline:2px solid var(--x-table-row-focus-ring);"
   "outline-offset:-2px;}"

   ":host([disabled]){"
   "opacity:var(--x-table-row-disabled-opacity);"
   "pointer-events:none;}"

   ;; Critical: slot must be display:contents so slotted x-table-cells
   ;; participate as direct grid items of the host subgrid.
   "slot{display:contents;}"

   "@media (prefers-reduced-motion:reduce){"
   ":host{transition:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        slot-el (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.appendChild root style)
    (.appendChild root slot-el)

    (gobj/set el k-refs {:root root :slot-el slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:selected?    (du/has-attr? el model/attr-selected)
    :disabled?    (du/has-attr? el model/attr-disabled)
    :interactive? (du/has-attr? el model/attr-interactive)
    :row-index-raw (du/get-attr el model/attr-row-index)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-host-attrs! [^js el {:keys [selected? disabled? interactive? row-index] :as m}]
  (du/set-attr! el "role" "row")

  (let [aria-sel (model/aria-selected-value m)]
    (if aria-sel
      (du/set-attr! el "aria-selected" aria-sel)
      (du/remove-attr! el "aria-selected")))

  (if disabled?
    (du/set-attr! el "aria-disabled" "true")
    (du/remove-attr! el "aria-disabled"))

  (if (some? row-index)
    (du/set-attr! el "aria-rowindex" (str row-index))
    (du/remove-attr! el "aria-rowindex"))

  (if selected?
    (du/set-attr! el "data-selected" "")
    (du/remove-attr! el "data-selected"))

  (if interactive?
    (du/set-attr! el "data-interactive" "")
    (du/remove-attr! el "data-interactive"))

  (set! (.-tabIndex el) (if (model/interactive-eligible? m) 0 -1))
  nil)

(defn- apply-model! [^js el m]
  (ensure-refs! el)
  (apply-host-attrs! el m)
  (gobj/set el k-model m)
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-click! [^js el]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (du/dispatch-cancelable! el model/event-click (clj->js (model/click-detail m))))
  nil)

(defn- dispatch-connected! [^js el m]
  (du/dispatch! el model/event-connected (clj->js (model/connected-detail m)))
  nil)

(defn- dispatch-disconnected! [^js _el]
  (du/dispatch-document! model/event-disconnected #js {})
  nil)

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-click [^js el ^js _e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (model/interactive-eligible? m)
      (dispatch-click! el)))
  nil)

(defn- on-keydown [^js el ^js e]
  (let [key (.-key e)]
    (when (or (= key "Enter") (= key " "))
      (let [m (or (gobj/get el k-model) (read-model el))]
        (when (model/interactive-eligible? m)
          (.preventDefault e)
          (dispatch-click! el)))))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [click-h   (fn [e] (on-click el e))
        keydown-h (fn [e] (on-keydown el e))]
    (.addEventListener el "click" click-h)
    (.addEventListener el "keydown" keydown-h)
    (gobj/set el k-handlers #js {:click click-h :keydown keydown-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (let [click-h   (gobj/get hs "click")
            keydown-h (gobj/get hs "keydown")]
        (when click-h   (.removeEventListener el "click" click-h))
        (when keydown-h (.removeEventListener el "keydown" keydown-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-bool-prop! proto model/attr-selected    model/attr-selected)
  (du/define-bool-prop! proto model/attr-disabled    model/attr-disabled)
  (du/define-bool-prop! proto model/attr-interactive model/attr-interactive)

  ;; camelCase JS property for row-index attribute
  (.defineProperty js/Object proto "rowIndex"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-row-index
                                         (.getAttribute this model/attr-row-index))))
                        :set (fn [v]
                               (this-as ^js this
                                        (let [n (js/parseInt v 10)]
                                          (if (and (not (js/isNaN n)) (pos? n))
                                            (.setAttribute this model/attr-row-index (str n))
                                            (.removeAttribute this model/attr-row-index)))))
                        :enumerable true :configurable true}))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el)
  (let [m (or (gobj/get el k-model) (read-model el))]
  (dispatch-connected! el m))
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (dispatch-disconnected! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
