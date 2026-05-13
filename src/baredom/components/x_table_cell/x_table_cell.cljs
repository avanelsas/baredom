(ns baredom.components.x-table-cell.x-table-cell
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-table-cell.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTableCellRefs")
(def ^:private k-model    "__xTableCellModel")
(def ^:private k-handlers "__xTableCellHandlers")

;; ── Sort icon SVG constants ──────────────────────────────────────────────────
;; All icons use fill="currentColor" so CSS color drives the icon colour.

(def ^:private svg-sort-neutral
  "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 3L5 7h6L8 3zm0 10l3-4H5l3 4z\"/></svg>")

(def ^:private svg-sort-asc
  "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 4L4 10h8L8 4z\"/></svg>")

(def ^:private svg-sort-desc
  "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 12l4-6H4l4 6z\"/></svg>")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-table-cell-padding:0.5rem 0.75rem;"
   "--x-table-cell-bg:transparent;"
   "--x-table-cell-header-bg:rgba(0,0,0,0.04);"
   "--x-table-cell-border-color:var(--x-color-border,rgba(0,0,0,0.1));"
   "--x-table-cell-border-width:1px;"
   "--x-table-cell-color:var(--x-color-text,inherit);"
   "--x-table-cell-header-color:var(--x-color-text,inherit);"
   "--x-table-cell-font-size:inherit;"
   "--x-table-cell-header-font-weight:600;"
   "--x-table-cell-min-width:0;"
   "--x-table-cell-max-width:none;"
   "--x-table-cell-sort-color:var(--x-color-text-muted,rgba(0,0,0,0.4));"
   "--x-table-cell-sort-hover-color:var(--x-color-text,rgba(0,0,0,0.7));"
   "--x-table-cell-focus-ring:var(--x-color-focus-ring,rgba(59,130,246,0.5));"
   "--x-table-cell-transition-duration:var(--x-transition-duration,150ms);"
   "--x-table-cell-sticky-bg:var(--x-color-bg,#ffffff);"
   "--x-table-cell-disabled-opacity:0.45;"
   "min-width:var(--x-table-cell-min-width);"
   "max-width:var(--x-table-cell-max-width);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-table-cell-header-bg:rgba(255,255,255,0.06);"
   "--x-table-cell-border-color:var(--x-color-border,rgba(255,255,255,0.1));"
   "--x-table-cell-sort-color:var(--x-color-text-muted,rgba(255,255,255,0.4));"
   "--x-table-cell-sort-hover-color:var(--x-color-text,rgba(255,255,255,0.7));"
   "--x-table-cell-sticky-bg:var(--x-color-bg,#1f2937);}}"

   ":host([data-sticky='start']){"
   "position:sticky;"
   "inset-inline-start:0;"
   "background:var(--x-table-cell-sticky-bg);"
   "z-index:1;}"

   ":host([data-sticky='end']){"
   "position:sticky;"
   "inset-inline-end:0;"
   "background:var(--x-table-cell-sticky-bg);"
   "z-index:1;}"

   ":host([disabled]){"
   "opacity:var(--x-table-cell-disabled-opacity);}"

   "[part=cell]{"
   "display:flex;"
   "align-items:center;"
   "justify-content:flex-start;"
   "padding:var(--x-table-cell-padding);"
   "background:var(--x-table-cell-bg);"
   "border-bottom:var(--x-table-cell-border-width) solid var(--x-table-cell-border-color);"
   "color:var(--x-table-cell-color);"
   "font-size:var(--x-table-cell-font-size);"
   "min-width:0;"
   ;; Fill the host's height so border-bottom sits at the host's bottom edge.
   ;; When x-table-row's subgrid stretches the host to the row height, this
   ;; keeps border-bottoms aligned across columns regardless of per-cell
   ;; intrinsic content height (e.g. header cell with sort button vs plain).
   "height:100%;"
   "box-sizing:border-box;}"

   ":host([data-type='header']) [part=cell]{"
   "background:var(--x-table-cell-header-bg);"
   "color:var(--x-table-cell-header-color);"
   "font-weight:var(--x-table-cell-header-font-weight);}"

   ":host([data-align='start']) [part=cell]{justify-content:flex-start;}"
   ":host([data-align='center']) [part=cell]{justify-content:center;}"
   ":host([data-align='end']) [part=cell]{justify-content:flex-end;}"

   ":host([data-valign='top']) [part=cell]{align-items:flex-start;}"
   ":host([data-valign='middle']) [part=cell]{align-items:center;}"
   ":host([data-valign='bottom']) [part=cell]{align-items:flex-end;}"

   "[part=content]{"
   "flex:1 1 auto;"
   "min-width:0;}"

   ":host([data-truncate]) [part=content]{"
   "overflow:hidden;"
   "text-overflow:ellipsis;"
   "white-space:nowrap;}"

   "[part=sort-btn]{"
   "flex:0 0 auto;"
   "appearance:none;"
   "border:0;"
   "background:transparent;"
   "padding:2px;"
   "margin:0 0 0 4px;"
   "cursor:pointer;"
   "color:var(--x-table-cell-sort-color);"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "border-radius:4px;"
   "transition:color var(--x-table-cell-transition-duration) ease;"
   "line-height:1;}"

   "[part=sort-btn]:hover{"
   "color:var(--x-table-cell-sort-hover-color);}"

   "[part=sort-btn]:focus{"
   "outline:none;}"

   "[part=sort-btn]:focus-visible{"
   "outline:2px solid var(--x-table-cell-focus-ring);"
   "outline-offset:2px;}"

   "[part=sort-btn][disabled]{"
   "cursor:default;"
   "pointer-events:none;"
   "opacity:0.4;}"

   "[part=sort-btn][hidden]{"
   "display:none !important;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=sort-btn]{transition:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root          (.attachShadow el #js {:mode "open"})
        style         (.createElement js/document "style")
        cell          (.createElement js/document "div")
        content       (.createElement js/document "span")
        default-slot  (.createElement js/document "slot")
        sort-btn      (.createElement js/document "button")
        sort-icon-slot (.createElement js/document "slot")
        sort-icon-default (.createElement js/document "span")]

    (set! (.-textContent style) style-text)

    (.setAttribute cell "part" "cell")

    (.setAttribute content "part" "content")
    (.appendChild content default-slot)

    (.setAttribute sort-btn "part" "sort-btn")
    (.setAttribute sort-btn "type" "button")

    (.setAttribute sort-icon-slot "name" model/slot-sort-icon)
    (.setAttribute sort-icon-default "part" "sort-icon-default")
    (.setAttribute sort-icon-default "aria-hidden" "true")
    (set! (.-innerHTML sort-icon-default) svg-sort-neutral)
    (.appendChild sort-icon-slot sort-icon-default)
    (.appendChild sort-btn sort-icon-slot)

    (.appendChild cell content)
    (.appendChild cell sort-btn)
    (.appendChild root style)
    (.appendChild root cell)

    (gobj/set el k-refs
              {:root              root
               :cell              cell
               :content           content
               :sort-btn          sort-btn
               :sort-icon-default sort-icon-default}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:type-raw           (du/get-attr el model/attr-type)
    :scope-raw          (du/get-attr el model/attr-scope)
    :align-raw          (du/get-attr el model/attr-align)
    :valign-raw         (du/get-attr el model/attr-valign)
    :col-span-raw       (du/get-attr el model/attr-col-span)
    :row-span-raw       (du/get-attr el model/attr-row-span)
    :truncate?          (du/has-attr? el model/attr-truncate)
    :sticky-raw         (du/get-attr el model/attr-sticky)
    :sortable?          (du/has-attr? el model/attr-sortable)
    :sort-direction-raw (du/get-attr el model/attr-sort-direction)
    :disabled?          (du/has-attr? el model/attr-disabled)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-span! [^js el col-span row-span]
  (set! (.. el -style -gridColumn) (if (> col-span 1) (str "span " col-span) ""))
  (set! (.. el -style -gridRow)    (if (> row-span 1) (str "span " row-span) ""))
  nil)

(defn- apply-host-attrs! [^js el {:keys [type align valign sticky truncate?
                                          disabled?] :as m}]
  (du/set-attr! el "role" (model/role-for-cell m))

  (let [aria-sort (model/aria-sort-value m)]
    (if aria-sort
      (du/set-attr! el "aria-sort" aria-sort)
      (du/remove-attr! el "aria-sort")))

  (if disabled?
    (du/set-attr! el "aria-disabled" "true")
    (du/remove-attr! el "aria-disabled"))

  (du/set-attr! el "data-type" type)
  (du/set-attr! el "data-align" align)
  (du/set-attr! el "data-valign" valign)

  (if (= sticky "none")
    (du/remove-attr! el "data-sticky")
    (du/set-attr! el "data-sticky" sticky))

  (if truncate?
    (du/set-attr! el "data-truncate" "")
    (du/remove-attr! el "data-truncate"))
  nil)

(defn- sort-icon-svg [{:keys [sort-direction]}]
  (case sort-direction
    "asc"  svg-sort-asc
    "desc" svg-sort-desc
    svg-sort-neutral))

(defn- apply-model! [^js el {:keys [col-span row-span disabled? sort-direction] :as m}]
  (let [{:keys [sort-btn sort-icon-default]} (ensure-refs! el)
        ^js sort-btn          sort-btn
        ^js sort-icon-default sort-icon-default
        visible?  (model/sort-btn-visible? m)]

    (apply-host-attrs! el m)
    (apply-span! el col-span row-span)

    (set! (.-hidden sort-btn) (not visible?))
    (set! (.-tabIndex sort-btn)
          (js/parseInt (model/sort-btn-tabindex m) 10))
    (.setAttribute sort-btn "aria-label" (model/sort-btn-aria-label sort-direction))
    (set! (.-disabled sort-btn) (boolean disabled?))

    (set! (.-innerHTML sort-icon-default) (sort-icon-svg m))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-sort! [^js el]
  (let [current  (model/parse-sort-direction (du/get-attr el model/attr-sort-direction))
        next-dir (model/next-sort-direction current)]
    (du/dispatch-cancelable! el model/event-sort
      #js {:direction next-dir :previousDirection current}))
  nil)

(defn- dispatch-connected! [^js el m]
  (du/dispatch! el model/event-connected (clj->js (model/connected-detail m)))
  nil)

(defn- dispatch-disconnected! [^js el]
  (du/dispatch! el model/event-disconnected #js {})
  nil)

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-sort-click [^js el ^js _e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (and (model/sort-btn-visible? m) (not (:disabled? m)))
      (dispatch-sort! el)))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [sort-btn]} (ensure-refs! el)
        ^js sort-btn sort-btn
        sort-click-h (fn [e] (on-sort-click el e))]
    (.addEventListener sort-btn "click" sort-click-h)
    (gobj/set el k-handlers #js {:sort-click sort-click-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [{:keys [sort-btn]} refs
            ^js sort-btn sort-btn
            h (gobj/get hs "sort-click")]
        (when h (.removeEventListener sort-btn "click" h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-type     model/attr-type     "data")
  (du/define-string-prop! proto model/attr-scope    model/attr-scope    "col")
  (du/define-string-prop! proto model/attr-align    model/attr-align    "start")
  (du/define-string-prop! proto model/attr-valign   model/attr-valign   "middle")
  (du/define-bool-prop!   proto model/attr-truncate model/attr-truncate)
  (du/define-string-prop! proto model/attr-sticky   model/attr-sticky   "none")
  (du/define-bool-prop!   proto model/attr-sortable model/attr-sortable)
  (du/define-bool-prop!   proto model/attr-disabled model/attr-disabled)

  ;; camelCase JS properties for kebab-case attributes
  (.defineProperty js/Object proto "colSpan"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-span
                                         (.getAttribute this model/attr-col-span))))
                        :set (fn [v]
                               (this-as ^js this
                                        (let [n (js/parseInt v 10)]
                                          (if (and (not (js/isNaN n)) (pos? n))
                                            (.setAttribute this model/attr-col-span (str n))
                                            (.removeAttribute this model/attr-col-span)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "rowSpan"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-span
                                         (.getAttribute this model/attr-row-span))))
                        :set (fn [v]
                               (this-as ^js this
                                        (let [n (js/parseInt v 10)]
                                          (if (and (not (js/isNaN n)) (pos? n))
                                            (.setAttribute this model/attr-row-span (str n))
                                            (.removeAttribute this model/attr-row-span)))))
                        :enumerable true :configurable true})

  (du/define-string-prop! proto "sortDirection" model/attr-sort-direction "none"))

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
