(ns baredom.components.x-table.x-table
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-table.model :as model]
            [baredom.components.x-table-row.model :as row-model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTableRefs")
(def ^:private k-model    "__xTableModel")
(def ^:private k-handlers "__xTableHandlers")
(def ^:private k-observer "__xTableObserver")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private cell-tag      "x-table-cell")
(def ^:private sel-rows      (str ":scope > " row-model/tag-name))
(def ^:private hk-sort       "sort")
(def ^:private hk-row-click  "row-click")
(def ^:private attr-data-striped    "data-striped")
(def ^:private attr-data-bordered   "data-bordered")
(def ^:private attr-data-full-width "data-full-width")
(def ^:private attr-data-compact    "data-compact")
(def ^:private attr-aria-multiselectable "aria-multiselectable")
(def ^:private attr-aria-rowcount        "aria-rowcount")
(def ^:private detail-row-index         "rowIndex")
(def ^:private detail-direction         "direction")
(def ^:private detail-previous-direction "previousDirection")

;; Events emitted by child components (x-table-cell / x-table-row) that
;; x-table listens for and re-fires / coordinates. The source of truth is
;; each child's own model, but x-table consumes them as local constants.
(def ^:private ev-cell-sort       "x-table-cell-sort")
(def ^:private ev-row-click       "x-table-row-click")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:grid;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-table-border-color:var(--x-color-border,rgba(0,0,0,0.1));"
   "--x-table-border-radius:var(--x-radius-md,8px);"
   "--x-table-stripe-bg:color-mix(in srgb,var(--x-color-text,#000) 2.5%,transparent);"
   "--x-table-caption-color:var(--x-color-text,inherit);"
   "--x-table-caption-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-table-caption-font-weight:var(--x-font-weight-semibold,600);"
   "--x-table-caption-padding:0 0 0.5rem;"
   "--x-table-compact-padding:0.25rem 0.5rem;}"
   du/hidden-rule

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-table-border-color:var(--x-color-border,rgba(255,255,255,0.1));"
   "--x-table-stripe-bg:color-mix(in srgb,var(--x-color-text,#fff) 3%,transparent);}}"

   ":host([data-bordered]){"
   "border:var(--x-border-width,1px) solid var(--x-table-border-color);"
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
    :row-count-raw  (du/get-attr el model/attr-row-count)
    :value-raw      (du/get-attr el model/attr-value)
    :value-format-raw (du/get-attr el model/attr-value-format)}))

(defn- current-model [^js el]
  (or (du/getv el k-model) (read-model el)))

;; ── Rows ─────────────────────────────────────────────────────────────────────
(defn- direct-rows [^js el]
  (array-seq (.querySelectorAll el sel-rows)))

(defn- value-of-row
  "The value that names `row` in the selection, or nil."
  [^js row]
  (row-model/row-value (du/get-attr row row-model/attr-value)
                       (du/get-attr row row-model/attr-row-index)))

(defn- marked? [^js row]
  (du/has-attr? row row-model/attr-selected))

(defn- marked-values
  "The values of the direct rows marked selected, in order."
  [^js el]
  (into [] (comp (filter marked?) (keep value-of-row)) (direct-rows el)))

(defn- current-selection
  "The row values selected now: those `value` names, or those of the rows marked selected when
   the table has no `value`."
  [^js el {:keys [selectable value-present? selection]}]
  (if value-present?
    selection
    (model/seed-selection selectable (marked-values el))))

;; ── Stripe helper ────────────────────────────────────────────────────────────
(defn- update-stripe-attrs!
  "Writes the stripe of each direct row where it differs."
  [^js el]
  (doseq [[i ^js row] (map-indexed vector (direct-rows el))]
    (du/set-attr-to! row model/row-attr-stripe (model/row-stripe i))))

;; ── Selection ────────────────────────────────────────────────────────────────
(defn- show-selection!
  "Marks each direct row selected when `selection` holds its value."
  [^js el selection]
  (doseq [^js row (direct-rows el)]
    (du/set-bool-attr-to! row row-model/attr-selected (contains? selection (value-of-row row)))))

(defn- apply-selection!
  "Shows the selection that `value` names. A removed `value` selects no row."
  [^js el old-m m]
  (cond
    (:value-present? m)     (show-selection! el (:selection m))
    (:value-present? old-m) (show-selection! el #{})))

;; ── Child watch ──────────────────────────────────────────────────────────────
(defn- on-rows-changed!
  "Stripes the rows and shows the selection again after rows come, go or move."
  [^js el]
  (let [m (current-model el)]
    (when (:striped? m)
      (update-stripe-attrs! el))
    (apply-selection! el nil m)))

(defn- direct-child? [^js el ^js node]
  (identical? el (.-parentElement node)))

(defn- row-record?
  "True when the mutation record `r` is of a direct row of `el`."
  [^js el ^js r]
  (direct-child? el (.-target r)))

(defn- on-row-mutations!
  "Shows the selection again after the value of a direct row changes."
  [^js el records]
  (when (some (partial row-record? el) (array-seq records))
    (apply-selection! el nil (current-model el))))

(defn- observe-rows! [^js el]
  (let [observer (js/MutationObserver. (fn on-mutations [records] (on-row-mutations! el records)))]
    (.observe observer el #js {:attributes      true
                               :subtree         true
                               :attributeFilter #js [row-model/attr-value row-model/attr-row-index]})
    (du/setv! el k-observer observer)))

(defn- unobserve-rows! [^js el]
  (some-> ^js (du/getv el k-observer) .disconnect)
  (du/setv! el k-observer nil))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style       (.createElement js/document "style")
        caption-div (.createElement js/document "div")
        slot-el     (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (du/set-attr! caption-div "part" "caption")
    (du/set-attr! caption-div "hidden" "")

    (.appendChild root style)
    (.appendChild root caption-div)
    (.appendChild root slot-el)
    ;; The slot lives as long as the shadow tree, so its listener needs no remove path.
    (.addEventListener slot-el "slotchange" (fn on-slotchange [_] (on-rows-changed! el)))

    (du/setv! el k-refs {:root        root
                         :caption-div caption-div
                         :slot-el     slot-el})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

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
        (du/remove-attr! caption-div "hidden"))
    (do (set! (.-textContent caption-div) "")
        (du/set-attr! caption-div "hidden" ""))))

(defn- apply-data-flags! [^js el {:keys [striped? bordered? full-width? compact?]}]
  (if striped?    (du/set-attr! el attr-data-striped    "") (du/remove-attr! el attr-data-striped))
  (if bordered?   (du/set-attr! el attr-data-bordered   "") (du/remove-attr! el attr-data-bordered))
  (if full-width? (du/set-attr! el attr-data-full-width "") (du/remove-attr! el attr-data-full-width))
  (if compact?    (du/set-attr! el attr-data-compact    "") (du/remove-attr! el attr-data-compact)))

(defn- apply-stripes!
  "Stripes the rows when `striped?` turns on from `old-m` to `m`."
  [^js el old-m m]
  (when (and (:striped? m) (not (:striped? old-m)))
    (update-stripe-attrs! el)))

(defn- warn-invalid-value!
  "Warns when `value` is not a JSON array of strings in json format."
  [{:keys [value-warning]}]
  (when value-warning
    (js/console.warn value-warning)))

(defn- apply-model! [^js el old-m m]
  (let [{:keys [caption-div]} (ensure-refs! el)
        ^js caption-div caption-div]
    (apply-grid-template! el m)
    (apply-aria!          el m)
    (apply-caption!       caption-div m)
    (apply-data-flags!    el m)
    (apply-stripes!       el old-m m)
    (apply-selection!     el old-m m)
    (warn-invalid-value!  m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el old-m new-m))))

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

(defn- dispatch-row-select! [^js el row-index selected? selectable value]
  (du/dispatch-cancelable! el model/event-row-select
    (clj->js (model/row-select-detail row-index selected? selectable value))))

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

(defn- request-selection!
  "Asks for the selection that a click on the row with the value `row-value` gives, and writes it
   to `value` when the request is not refused."
  [^js el ^js e {:keys [selectable value-format] :as m} row-value]
  (let [selection (model/next-selection selectable (current-selection el m) row-value)
        text      (model/selection-text value-format selection)]
    (when (dispatch-row-select! el (gobj/get (.-detail e) detail-row-index)
                                (contains? selection row-value) selectable text)
      (du/set-attr! el model/attr-value text))))

(defn- on-row-click
  "Asks for the selection that a click on a direct row gives. A row whose value the list cannot
   hold asks for nothing and warns."
  [^js el ^js e]
  (let [{:keys [selectable value-format] :as m} (current-model el)
        ^js row   (.-target e)
        row-value (value-of-row row)]
    (when (and (not= selectable model/selectable-none) (some? row-value)
               (direct-child? el row) (not (.-defaultPrevented e)))
      (if (model/holdable? value-format row-value)
        (request-selection! el e m row-value)
        (js/console.warn (model/unholdable-message row-value))))))

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [sort-h      (fn handle-cell-sort   [e] (on-cell-sort el e))
        row-click-h (fn handle-row-click   [e] (on-row-click el e))
        handlers    #js {}]
    (.addEventListener el ev-cell-sort      sort-h)
    (.addEventListener el ev-row-click      row-click-h)
    (gobj/set handlers hk-sort      sort-h)
    (gobj/set handlers hk-row-click row-click-h)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (let [sort-h      (gobj/get hs hk-sort)
          row-click-h (gobj/get hs hk-row-click)]
      (when sort-h      (.removeEventListener el ev-cell-sort     sort-h))
      (when row-click-h (.removeEventListener el ev-row-click     row-click-h))))
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
  (du/define-string-prop! proto model/attr-value      model/attr-value      "")
  (du/define-string-prop! proto "valueFormat"         model/attr-value-format)

  ;; camelCase JS property for row-count
  (.defineProperty js/Object proto "rowCount"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-row-count
                                         (.getAttribute this model/attr-row-count))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (du/remove-attr! this model/attr-row-count)
                                          (du/set-attr! this model/attr-row-count (str (js/Math.floor v))))))
                        :enumerable true :configurable true}))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (unobserve-rows! el)
  (observe-rows! el)
  (update-from-attrs! el)
  (on-rows-changed! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (unobserve-rows! el))

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
