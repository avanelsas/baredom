(ns baredom.components.x-table-row.x-table-row
  (:require
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
   "--x-table-row-focus-ring:rgba(59,130,246,0.5);"
   "--x-table-row-transition-duration:150ms;"
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
   {:selected?    (.hasAttribute el model/attr-selected)
    :disabled?    (.hasAttribute el model/attr-disabled)
    :interactive? (.hasAttribute el model/attr-interactive)
    :row-index-raw (.getAttribute el model/attr-row-index)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-host-attrs! [^js el {:keys [selected? disabled? interactive? row-index] :as m}]
  (.setAttribute el "role" "row")

  (let [aria-sel (model/aria-selected-value m)]
    (if aria-sel
      (.setAttribute el "aria-selected" aria-sel)
      (.removeAttribute el "aria-selected")))

  (if disabled?
    (.setAttribute el "aria-disabled" "true")
    (.removeAttribute el "aria-disabled"))

  (if (some? row-index)
    (.setAttribute el "aria-rowindex" (str row-index))
    (.removeAttribute el "aria-rowindex"))

  (if selected?
    (.setAttribute el "data-selected" "")
    (.removeAttribute el "data-selected"))

  (if interactive?
    (.setAttribute el "data-interactive" "")
    (.removeAttribute el "data-interactive"))

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
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-click
                     #js {:detail     (clj->js (model/click-detail m))
                          :bubbles    true
                          :composed   true
                          :cancelable true})))
  nil)

(defn- dispatch-connected! [^js el m]
  (.dispatchEvent el
                  (js/CustomEvent.
                   model/event-connected
                   #js {:detail     (clj->js (model/connected-detail m))
                        :bubbles    true
                        :composed   true
                        :cancelable false}))
  nil)

(defn- dispatch-disconnected! [^js _el]
  (.dispatchEvent js/document
                  (js/CustomEvent.
                   model/event-disconnected
                   #js {:detail     #js {}
                        :bubbles    false
                        :composed   false
                        :cancelable false}))
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
  (.defineProperty js/Object proto model/attr-selected
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-selected)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-selected "")
                                          (.removeAttribute this model/attr-selected))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-interactive
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-interactive)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-interactive "")
                                          (.removeAttribute this model/attr-interactive))))
                        :enumerable true :configurable true})

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
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (update-from-attrs! this)
                     (let [m (or (gobj/get this k-model) (read-model this))]
                       (dispatch-connected! this m))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     (dispatch-disconnected! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
