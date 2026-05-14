(ns baredom.components.x-table.x-table
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-table.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTableRefs")
(def ^:private k-model    "__xTableModel")
(def ^:private k-handlers "__xTableHandlers")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private row-tag       "x-table-row")
(def ^:private cell-tag      "x-table-cell")
(def ^:private selected-attr "selected")
(def ^:private stripe-attr   "data-stripe")
(def ^:private stripe-even   "even")
(def ^:private hk-sort       "sort")
(def ^:private hk-row-click  "row-click")
(def ^:private hk-row-conn   "row-conn")
(def ^:private attr-data-striped    "data-striped")
(def ^:private attr-data-bordered   "data-bordered")
(def ^:private attr-data-full-width "data-full-width")
(def ^:private attr-data-compact    "data-compact")
(def ^:private attr-aria-multiselectable "aria-multiselectable")
(def ^:private attr-aria-rowcount        "aria-rowcount")
(def ^:private select-none   "none")
(def ^:private select-single "single")
(def ^:private select-multi  "multi")
(def ^:private detail-row-index         "rowIndex")
(def ^:private detail-direction         "direction")
(def ^:private detail-previous-direction "previousDirection")

;; Events emitted by child components (x-table-cell / x-table-row) that
;; x-table listens for and re-fires / coordinates. The source of truth is
;; each child's own model, but x-table consumes them as local constants.
(def ^:private ev-cell-sort       "x-table-cell-sort")
(def ^:private ev-row-click       "x-table-row-click")
(def ^:private ev-row-connected   "x-table-row-connected")

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

    (du/setv! el k-refs {:root        root
                         :caption-div caption-div
                         :slot-el     slot-el})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:columns-raw    (du/get-attr el model/attr-columns)
    :caption-raw    (du/get-attr el model/attr-caption)
    :selectable-raw (du/get-attr el model/attr-selectable)
    :striped?       (du/has-attr? el model/attr-striped)
    :bordered?      (du/has-attr? el model/attr-bordered)
    :full-width?    (du/has-attr? el model/attr-full-width)
    :compact?       (du/has-attr? el model/attr-compact)
    :row-count-raw  (du/get-attr el model/attr-row-count)}))

;; ── Stripe helper ────────────────────────────────────────────────────────────
(defn- update-stripe-attrs! [^js el]
  ;; Walk direct x-table-row children, assigning data-stripe="even" on even
  ;; (1-indexed) rows and removing it from odd rows.
  (let [rows (array-seq (.querySelectorAll el row-tag))]
    (doseq [[^js row i] (map vector rows (range))]
      (if (zero? (mod (inc i) 2))
        (.setAttribute row stripe-attr stripe-even)
        (.removeAttribute row stripe-attr)))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────────
(defn- apply-grid-template! [^js el {:keys [columns]}]
  ;; grid-template-columns lives directly on the host so author CSS can override.
  (if columns
    (set! (.. el -style -gridTemplateColumns) columns)
    (set! (.. el -style -gridTemplateColumns) "")))

(defn- apply-aria! [^js el {:keys [selectable row-count caption]}]
  (du/set-attr! el "role" (model/role-for-selectable selectable))
  (let [ms (model/aria-multiselectable selectable)]
    (if ms
      (du/set-attr! el attr-aria-multiselectable ms)
      (du/remove-attr! el attr-aria-multiselectable)))
  (if (some? row-count)
    (du/set-attr! el attr-aria-rowcount (str row-count))
    (du/remove-attr! el attr-aria-rowcount))
  (if (not= caption "")
    (du/set-attr! el "aria-label" caption)
    (du/remove-attr! el "aria-label")))

(defn- apply-caption! [^js caption-div {:keys [caption]}]
  (if (not= caption "")
    (do (set! (.-textContent caption-div) caption)
        (.removeAttribute caption-div "hidden"))
    (do (set! (.-textContent caption-div) "")
        (.setAttribute caption-div "hidden" ""))))

(defn- apply-data-flags! [^js el {:keys [striped? bordered? full-width? compact?]}]
  (if striped?    (du/set-attr! el attr-data-striped    "") (du/remove-attr! el attr-data-striped))
  (if bordered?   (du/set-attr! el attr-data-bordered   "") (du/remove-attr! el attr-data-bordered))
  (if full-width? (du/set-attr! el attr-data-full-width "") (du/remove-attr! el attr-data-full-width))
  (if compact?    (du/set-attr! el attr-data-compact    "") (du/remove-attr! el attr-data-compact)))

(defn- apply-model! [^js el {:keys [striped?] :as m}]
  (let [{:keys [caption-div]} (ensure-refs! el)
        ^js caption-div caption-div]
    (apply-grid-template! el m)
    (apply-aria!          el m)
    (apply-caption!       caption-div m)
    (apply-data-flags!    el m)
    ;; Recompute stripe attributes on child rows when striped is on.
    (when striped?
      (update-stripe-attrs! el))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Column index helper ───────────────────────────────────────────────────────
(defn- col-index-of [^js cell]
  ;; Find the zero-based column index of this x-table-cell within its parent row.
  (let [^js row   (.-parentElement cell)
        ^js cells (.querySelectorAll row cell-tag)]
    (.indexOf (js/Array.from cells) cell)))

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-sort! [^js el col-index direction previous-direction]
  (du/dispatch-cancelable! el model/event-sort
    (clj->js (model/sort-detail col-index direction previous-direction))))

(defn- dispatch-row-select! [^js el row-index selected? selectable]
  (du/dispatch-cancelable! el model/event-row-select
    (clj->js (model/row-select-detail row-index selected? selectable))))

;; ── Selection management ─────────────────────────────────────────────────────
(defn- handle-single-select! [^js el ^js target-row]
  ;; Deselect all currently-selected rows. If the target was not already
  ;; selected, select it (toggle behavior: clicking the same row deselects).
  (let [was-selected? (.hasAttribute target-row selected-attr)
        selected-rows (array-seq (.querySelectorAll el (str row-tag "[" selected-attr "]")))]
    (doseq [^js row selected-rows]
      (.removeAttribute row selected-attr))
    (when-not was-selected?
      (.setAttribute target-row selected-attr ""))))

(defn- handle-multi-select! [^js _el ^js target-row]
  ;; Toggle the target row's selected state.
  (if (.hasAttribute target-row selected-attr)
    (.removeAttribute target-row selected-attr)
    (.setAttribute target-row selected-attr "")))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-cell-sort [^js el ^js e]
  ;; x-table-cell-sort bubbles up from a header cell. Re-fire as x-table-sort
  ;; with column index information.
  (let [^js cell    (.-target e)
        col-index   (col-index-of cell)
        direction   (gobj/get (.-detail e) detail-direction)
        prev-dir    (gobj/get (.-detail e) detail-previous-direction)]
    (when (>= col-index 0)
      ;; Stop the original cell-sort from propagating further so x-table-sort
      ;; is the canonical event for consumers above x-table.
      (.stopPropagation e)
      (dispatch-sort! el col-index direction prev-dir))))

(defn- on-row-click [^js el ^js e]
  (let [m          (or (du/getv el k-model) (read-model el))
        selectable (:selectable m)]
    (when (not= selectable select-none)
      (let [^js row      (.-target e)
            row-index    (gobj/get (.-detail e) detail-row-index)
            currently?   (.hasAttribute row selected-attr)
            will-select? (not currently?)]
        (dispatch-row-select! el row-index will-select? selectable)
        (cond
          (= selectable select-single) (handle-single-select! el row)
          (= selectable select-multi)  (handle-multi-select! el row))))))

(defn- on-row-connected [^js el ^js _e]
  ;; Re-compute stripe attributes whenever a new row connects.
  (let [m (or (du/getv el k-model) (read-model el))]
    (when (:striped? m)
      (update-stripe-attrs! el))))

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [sort-h      (fn handle-cell-sort   [e] (on-cell-sort el e))
        row-click-h (fn handle-row-click   [e] (on-row-click el e))
        row-conn-h  (fn handle-row-connect [e] (on-row-connected el e))
        handlers    #js {}]
    (.addEventListener el ev-cell-sort      sort-h)
    (.addEventListener el ev-row-click      row-click-h)
    (.addEventListener el ev-row-connected  row-conn-h)
    (gobj/set handlers hk-sort      sort-h)
    (gobj/set handlers hk-row-click row-click-h)
    (gobj/set handlers hk-row-conn  row-conn-h)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (let [sort-h      (gobj/get hs hk-sort)
          row-click-h (gobj/get hs hk-row-click)
          row-conn-h  (gobj/get hs hk-row-conn)]
      (when sort-h      (.removeEventListener el ev-cell-sort     sort-h))
      (when row-click-h (.removeEventListener el ev-row-click     row-click-h))
      (when row-conn-h  (.removeEventListener el ev-row-connected row-conn-h))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-columns    model/attr-columns    "")
  (du/define-string-prop! proto model/attr-caption    model/attr-caption    "")
  (du/define-string-prop! proto model/attr-selectable model/attr-selectable "none")
  (du/define-bool-prop!   proto model/attr-striped    model/attr-striped)
  (du/define-bool-prop!   proto model/attr-bordered   model/attr-bordered)
  (du/define-bool-prop!   proto model/attr-compact    model/attr-compact)
  (du/define-bool-prop!   proto "fullWidth"           model/attr-full-width)

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
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

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
