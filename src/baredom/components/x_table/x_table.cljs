(ns baredom.components.x-table.x-table
  (:require
   [goog.object :as gobj]
   [baredom.components.x-table.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTableRefs")
(def ^:private k-model    "__xTableModel")
(def ^:private k-handlers "__xTableHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:grid;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-table-border-color:var(--x-color-border,rgba(0,0,0,0.1));"
   "--x-table-border-radius:var(--x-radius-md,8px);"
   "--x-table-stripe-bg:rgba(0,0,0,0.025);"
   "--x-table-caption-color:var(--x-color-text,inherit);"
   "--x-table-caption-font-size:0.875rem;"
   "--x-table-caption-font-weight:600;"
   "--x-table-caption-padding:0 0 0.5rem;"
   "--x-table-compact-padding:0.25rem 0.5rem;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-table-border-color:var(--x-color-border,rgba(255,255,255,0.1));"
   "--x-table-stripe-bg:rgba(255,255,255,0.03);}}"

   ":host([data-bordered]){"
   "border:1px solid var(--x-table-border-color);"
   "border-radius:var(--x-table-border-radius);"
   "overflow:hidden;}"

   ":host([data-full-width]){"
   "width:100%;}"

   ;; When compact, override the cell padding custom property for all descendant cells
   ":host([data-compact]){"
   "--x-table-cell-padding:var(--x-table-compact-padding);}"

   ;; Striped: even slotted x-table-row elements get a stripe background
   ":host([data-striped]) ::slotted(x-table-row[data-stripe='even']){"
   "--x-table-row-bg:var(--x-table-stripe-bg);}"

   ;; Caption part
   "[part=caption]{"
   "grid-column:1 / -1;"
   "color:var(--x-table-caption-color);"
   "font-size:var(--x-table-caption-font-size);"
   "font-weight:var(--x-table-caption-font-weight);"
   "padding:var(--x-table-caption-padding);"
   "box-sizing:border-box;}"

   "[part=caption][hidden]{"
   "display:none !important;}"

   ;; Slot must be display:contents so slotted x-table-row elements
   ;; participate as direct grid items of the host grid.
   "slot{display:contents;}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style       (.createElement js/document "style")
        caption-div (.createElement js/document "div")
        slot-el     (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute caption-div "part" "caption")
    (.setAttribute caption-div "hidden" "")

    (.appendChild root style)
    (.appendChild root caption-div)
    (.appendChild root slot-el)

    (gobj/set el k-refs {:root        root
                         :caption-div caption-div
                         :slot-el     slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:columns-raw    (.getAttribute el model/attr-columns)
    :caption-raw    (.getAttribute el model/attr-caption)
    :selectable-raw (.getAttribute el model/attr-selectable)
    :striped?       (.hasAttribute el model/attr-striped)
    :bordered?      (.hasAttribute el model/attr-bordered)
    :full-width?    (.hasAttribute el model/attr-full-width)
    :compact?       (.hasAttribute el model/attr-compact)
    :row-count-raw  (.getAttribute el model/attr-row-count)}))

;; ── Stripe helper ────────────────────────────────────────────────────────────
(defn- update-stripe-attrs! [^js el]
  ;; Walk direct x-table-row children, assigning data-stripe="even" on even
  ;; (1-indexed) rows and removing it from odd rows.
  (let [^js rows (.querySelectorAll el "x-table-row")
        len (.-length rows)]
    (loop [i 0]
      (when (< i len)
        (let [^js row (aget rows i)]
          (if (zero? (mod (inc i) 2))
            (.setAttribute row "data-stripe" "even")
            (.removeAttribute row "data-stripe")))
        (recur (inc i)))))
  nil)

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el
                     {:keys [columns caption selectable
                             striped? bordered? full-width?
                             compact? row-count] :as m}]
  (let [{:keys [caption-div]} (ensure-refs! el)
        ^js caption-div caption-div]

    ;; grid-template-columns
    (if columns
      (set! (.. el -style -gridTemplateColumns) columns)
      (set! (.. el -style -gridTemplateColumns) ""))

    ;; ARIA role
    (.setAttribute el "role" (model/role-for-selectable selectable))

    ;; aria-multiselectable
    (let [ms (model/aria-multiselectable selectable)]
      (if ms
        (.setAttribute el "aria-multiselectable" ms)
        (.removeAttribute el "aria-multiselectable")))

    ;; aria-rowcount (explicit)
    (if (some? row-count)
      (.setAttribute el "aria-rowcount" (str row-count))
      (.removeAttribute el "aria-rowcount"))

    ;; aria-label from caption
    (if (not= caption "")
      (.setAttribute el "aria-label" caption)
      (.removeAttribute el "aria-label"))

    ;; Caption element visibility and text
    (if (not= caption "")
      (do (set! (.-textContent caption-div) caption)
          (.removeAttribute caption-div "hidden"))
      (do (set! (.-textContent caption-div) "")
          (.setAttribute caption-div "hidden" "")))

    ;; Data attributes for CSS targeting
    (if striped?
      (.setAttribute el "data-striped" "")
      (.removeAttribute el "data-striped"))

    (if bordered?
      (.setAttribute el "data-bordered" "")
      (.removeAttribute el "data-bordered"))

    (if full-width?
      (.setAttribute el "data-full-width" "")
      (.removeAttribute el "data-full-width"))

    (if compact?
      (.setAttribute el "data-compact" "")
      (.removeAttribute el "data-compact"))

    ;; Update stripe attributes on child rows when striped flag changes
    (when striped?
      (update-stripe-attrs! el))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Column index helper ───────────────────────────────────────────────────────
(defn- col-index-of [^js cell]
  ;; Find the zero-based column index of this x-table-cell within its parent row.
  (let [^js row   (.-parentElement cell)
        ^js cells (.querySelectorAll row "x-table-cell")]
    (.indexOf (js/Array.from cells) cell)))

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-sort! [^js el col-index direction previous-direction]
  (let [detail (clj->js (model/sort-detail col-index direction previous-direction))]
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-sort
                     #js {:detail     detail
                          :bubbles    true
                          :composed   true
                          :cancelable true})))
  nil)

(defn- dispatch-row-select! [^js el row-index selected? selectable]
  (let [detail (clj->js (model/row-select-detail row-index selected? selectable))]
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-row-select
                     #js {:detail     detail
                          :bubbles    true
                          :composed   true
                          :cancelable true})))
  nil)

;; ── Selection management ─────────────────────────────────────────────────────
(defn- handle-single-select! [^js el ^js target-row]
  ;; Deselect all currently-selected rows. If the target was not already
  ;; selected, select it (toggle behavior: clicking the same row deselects).
  (let [was-selected? (.hasAttribute target-row "selected")
        ^js selected-rows (.querySelectorAll el "x-table-row[selected]")]
    (let [len (.-length selected-rows)]
      (loop [i 0]
        (when (< i len)
          (.removeAttribute (aget selected-rows i) "selected")
          (recur (inc i)))))
    (when-not was-selected?
      (.setAttribute target-row "selected" "")))
  nil)

(defn- handle-multi-select! [^js _el ^js target-row]
  ;; Toggle the target row's selected state.
  (if (.hasAttribute target-row "selected")
    (.removeAttribute target-row "selected")
    (.setAttribute target-row "selected" ""))
  nil)

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-cell-sort [^js el ^js e]
  ;; x-table-cell-sort bubbles up from a header cell. Re-fire as x-table-sort
  ;; with column index information.
  (let [^js cell    (.-target e)
        col-index   (col-index-of cell)
        direction   (gobj/get (.-detail e) "direction")
        prev-dir    (gobj/get (.-detail e) "previousDirection")]
    (when (>= col-index 0)
      ;; Stop the original cell-sort from propagating further so x-table-sort
      ;; is the canonical event for consumers above x-table.
      (.stopPropagation e)
      (dispatch-sort! el col-index direction prev-dir)))
  nil)

(defn- on-row-click [^js el ^js e]
  (let [m          (or (gobj/get el k-model) (read-model el))
        selectable (:selectable m)]
    (when (not= selectable "none")
      (let [^js row      (.-target e)
            row-index    (gobj/get (.-detail e) "rowIndex")
            currently?   (.hasAttribute row "selected")
            will-select? (not currently?)]
        (dispatch-row-select! el row-index will-select? selectable)
        (cond
          (= selectable "single") (handle-single-select! el row)
          (= selectable "multi")  (handle-multi-select! el row)))))
  nil)

(defn- on-row-connected [^js el ^js _e]
  ;; Re-compute stripe attributes whenever a new row connects.
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (:striped? m)
      (update-stripe-attrs! el)))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [sort-h      (fn [e] (on-cell-sort el e))
        row-click-h (fn [e] (on-row-click el e))
        row-conn-h  (fn [e] (on-row-connected el e))]
    (.addEventListener el "x-table-cell-sort" sort-h)
    (.addEventListener el "x-table-row-click" row-click-h)
    (.addEventListener el "x-table-row-connected" row-conn-h)
    (gobj/set el k-handlers
              #js {:sort      sort-h
                   :row-click row-click-h
                   :row-conn  row-conn-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (let [sort-h      (gobj/get hs "sort")
            row-click-h (gobj/get hs "row-click")
            row-conn-h  (gobj/get hs "row-conn")]
        (when sort-h      (.removeEventListener el "x-table-cell-sort" sort-h))
        (when row-click-h (.removeEventListener el "x-table-row-click" row-click-h))
        (when row-conn-h  (.removeEventListener el "x-table-row-connected" row-conn-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-columns
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-columns) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-columns (str v))
                                          (.removeAttribute this model/attr-columns))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-caption
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-caption) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-caption (str v))
                                          (.removeAttribute this model/attr-caption))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-selectable
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-selectable) "none")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-selectable (str v))
                                          (.removeAttribute this model/attr-selectable))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-striped
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-striped)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-striped "")
                                          (.removeAttribute this model/attr-striped))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-bordered
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-bordered)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-bordered "")
                                          (.removeAttribute this model/attr-bordered))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-compact
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-compact)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-compact "")
                                          (.removeAttribute this model/attr-compact))))
                        :enumerable true :configurable true})

  ;; camelCase JS property for full-width
  (.defineProperty js/Object proto "fullWidth"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-full-width)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-full-width "")
                                          (.removeAttribute this model/attr-full-width))))
                        :enumerable true :configurable true})

  ;; camelCase JS property for row-count
  (.defineProperty js/Object proto "rowCount"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-row-count
                                         (.getAttribute this model/attr-row-count))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-row-count)
                                          (.setAttribute this model/attr-row-count (str (js/Math.floor v))))))
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
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
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
