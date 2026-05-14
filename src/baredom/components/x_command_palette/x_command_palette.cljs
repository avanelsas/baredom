(ns baredom.components.x-command-palette.x-command-palette
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-command-palette.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs       "__xCommandPaletteRefs")
(def ^:private k-model      "__xCommandPaletteModel")
(def ^:private k-handlers   "__xCommandPaletteHandlers")
(def ^:private k-items      "__xCommandPaletteItems")
(def ^:private k-query      "__xCommandPaletteQuery")
(def ^:private k-active-idx "__xCommandPaletteActiveIdx")

;; ── Refs-object keys ────────────────────────────────────────────────────────
(def ^:private rk-overlay   "overlay")
(def ^:private rk-panel     "panel")
(def ^:private rk-input     "input")
(def ^:private rk-clear-btn "clear-btn")
(def ^:private rk-list      "list")
(def ^:private rk-empty     "empty")

;; ── Handler-object keys ─────────────────────────────────────────────────────
(def ^:private hk-input   "input")
(def ^:private hk-keydown "keydown")
(def ^:private hk-scrim   "scrim")
(def ^:private hk-list    "list")
(def ^:private hk-clear   "clear")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-part            "part")
(def ^:private attr-type            "type")
(def ^:private attr-role            "role")
(def ^:private attr-id              "id")
(def ^:private attr-hidden          "hidden")
(def ^:private attr-disabled        "disabled")
(def ^:private attr-placeholder     "placeholder")
(def ^:private attr-autocomplete    "autocomplete")
(def ^:private attr-autocorrect     "autocorrect")
(def ^:private attr-spellcheck      "spellcheck")
(def ^:private attr-aria-modal      "aria-modal")
(def ^:private attr-aria-label      "aria-label")
(def ^:private attr-aria-hidden     "aria-hidden")
(def ^:private attr-aria-expanded   "aria-expanded")
(def ^:private attr-aria-disabled   "aria-disabled")
(def ^:private attr-aria-selected   "aria-selected")
(def ^:private attr-aria-controls   "aria-controls")
(def ^:private attr-aria-autocomplete "aria-autocomplete")
(def ^:private attr-aria-activedescendant "aria-activedescendant")
(def ^:private attr-tabindex        "tabindex")
(def ^:private attr-data-id         "data-id")
(def ^:private attr-data-idx        "data-idx")

(def ^:private part-overlay      "overlay")
(def ^:private part-panel        "panel")
(def ^:private part-search-wrap  "search-wrap")
(def ^:private part-search-icon  "search-icon")
(def ^:private part-input        "input")
(def ^:private part-clear-btn    "clear-btn")
(def ^:private part-list-wrap    "list-wrap")
(def ^:private part-list         "list")
(def ^:private part-item         "item")
(def ^:private part-group-header "group-header")
(def ^:private part-empty        "empty")

(def ^:private val-true   "true")
(def ^:private val-false  "false")
(def ^:private val-button "button")
(def ^:private val-search "search")
(def ^:private val-dialog "dialog")
(def ^:private val-combobox "combobox")
(def ^:private val-list   "list")
(def ^:private val-listbox "listbox")
(def ^:private val-option "option")
(def ^:private val-off    "off")
(def ^:private val-default-label "Command palette")
(def ^:private val-results "Results")
(def ^:private val-clear   "Clear")
(def ^:private val-search-glyph "🔍")

(def ^:private list-id      "x-cp-list")
(def ^:private item-id-pref "x-cp-item-")

(def ^:private ev-click   "click")
(def ^:private ev-input   "input")
(def ^:private ev-keydown "keydown")

(def ^:private key-arrow-down "ArrowDown")
(def ^:private key-arrow-up   "ArrowUp")
(def ^:private key-enter      "Enter")
(def ^:private key-escape     "Escape")

;; ── Helpers ────────────────────────────────────────────────────────────────

(defn- read-model [^js el]
  (model/normalize
   {:open-present?       (du/has-attr? el model/attr-open)
    :modal-raw           (du/get-attr el model/attr-modal)
    :dismissible-raw     (du/get-attr el model/attr-dismissible)
    :disabled-raw        (du/get-attr el model/attr-disabled)
    :no-scrim-raw        (du/get-attr el model/attr-no-scrim)
    :close-on-scrim-raw  (du/get-attr el model/attr-close-on-scrim)
    :close-on-escape-raw (du/get-attr el model/attr-close-on-escape)
    :label-raw           (du/get-attr el model/attr-label)
    :placeholder-raw     (du/get-attr el model/attr-placeholder)
    :empty-text-raw      (du/get-attr el model/attr-empty-text)}))

;; ── Style ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:contents;}"
   "[part=overlay]{"
   "display:none;position:fixed;inset:0;"
   "background:var(--x-command-palette-backdrop,rgba(0,0,0,0.45));"
   "z-index:var(--x-command-palette-z,800);"
   "}"
   "[part=panel]{"
   "display:none;position:fixed;"
   "top:20vh;left:50%;transform:translateX(-50%);"
   "width:var(--x-command-palette-width,560px);"
   "max-width:calc(100vw - 2rem);"
   "max-height:60dvh;"
   "z-index:calc(var(--x-command-palette-z,800) + 1);"
   "flex-direction:column;"
   "border-radius:var(--x-command-palette-radius,var(--x-radius-lg,12px));"
   "background:var(--x-command-palette-bg,var(--x-color-bg,#fff));"
   "box-shadow:var(--x-command-palette-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.25),0 4px 16px rgba(0,0,0,0.12)));"
   "overflow:hidden;"
   "}"
   ":host([open]) [part=overlay]{display:block;}"
   ":host([open]) [part=panel]{display:flex;}"
   "[part=search-wrap]{"
   "display:flex;align-items:center;gap:8px;"
   "padding:12px 16px;"
   "border-bottom:1px solid var(--x-command-palette-divider,rgba(0,0,0,0.08));"
   "}"
   "[part=search-icon]{"
   "flex:none;display:inline-flex;align-items:center;justify-content:center;"
   "width:18px;height:18px;"
   "color:var(--x-command-palette-icon-color,var(--x-color-text-muted,#64748b));"
   "}"
   "[part=input]{"
   "flex:1;border:none;outline:none;background:transparent;"
   "font-size:1rem;line-height:1.5;"
   "color:var(--x-command-palette-text,inherit);"
   "}"
   "[part=input]::placeholder{"
   "color:var(--x-command-palette-placeholder,var(--x-color-text-muted,#94a3b8));"
   "}"
   "[part=clear-btn]{"
   "all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"
   "width:24px;height:24px;border-radius:50%;"
   "color:var(--x-command-palette-icon-color,var(--x-color-text-muted,#64748b));"
   "}"
   "[part=clear-btn]:focus-visible{outline:2px solid var(--x-command-palette-focus-ring,var(--x-color-focus-ring,#60a5fa));}"
   "[part=clear-btn][hidden]{display:none;}"
   "[part=list-wrap]{"
   "flex:1;overflow-y:auto;overscroll-behavior:contain;"
   "padding:8px 0;"
   "}"
   "[part=list]{display:flex;flex-direction:column;}"
   "[part=item]{"
   "padding:10px 16px;cursor:default;"
   "font-size:var(--x-font-size-sm,0.9375rem);line-height:1.4;"
   "color:var(--x-command-palette-item-text,inherit);"
   "border-radius:0;"
   "outline:none;"
   "}"
   "[part=item]:hover,[part=item]:focus{"
   "background:var(--x-command-palette-item-hover,var(--x-color-surface-active,#f1f5f9));"
   "}"
   "[part=item][aria-selected=true]{"
   "background:var(--x-command-palette-item-active,#e0e7ff);"
   "color:var(--x-command-palette-item-active-text,#3730a3);"
   "}"
   "[part=item][aria-disabled=true]{opacity:0.45;cursor:default;pointer-events:none;}"
   "[part=group-header]{"
   "padding:8px 16px 4px;"
   "font-size:0.75rem;font-weight:600;letter-spacing:0.05em;text-transform:uppercase;"
   "color:var(--x-command-palette-group-text,var(--x-color-text-muted,#94a3b8));"
   "}"
   "[part=empty]{"
   "padding:32px 16px;text-align:center;"
   "color:var(--x-command-palette-empty-text,var(--x-color-text-muted,#94a3b8));"
   "font-size:0.9375rem;"
   "}"
   "[part=empty][hidden]{display:none;}"
   "@media (prefers-color-scheme:dark){"
   "[part=panel]{background:var(--x-command-palette-bg,var(--x-color-bg,#1e293b));box-shadow:var(--x-command-palette-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.6),0 4px 16px rgba(0,0,0,0.4)));}"
   "[part=search-wrap]{border-bottom-color:var(--x-command-palette-divider,rgba(255,255,255,0.08));}"
   "[part=item]:hover,[part=item]:focus{background:var(--x-command-palette-item-hover,var(--x-color-text-muted,#334155));}"
   "[part=item][aria-selected=true]{background:var(--x-command-palette-item-active,#312e81);color:var(--x-command-palette-item-active-text,#c7d2fe);}"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel],[part=overlay]{transition:none;}"
   "}"))

;; ── Shadow builders ────────────────────────────────────────────────────────
(defn- make-overlay! []
  (let [overlay (.createElement js/document "div")]
    (du/set-attr! overlay attr-part part-overlay)
    overlay))

(defn- make-search-section! []
  (let [search-wrap (.createElement js/document "div")
        search-icon (.createElement js/document "span")
        input-el    (.createElement js/document "input")
        clear-btn   (.createElement js/document "button")]
    (du/set-attr! search-wrap attr-part part-search-wrap)

    (du/set-attr! search-icon attr-part        part-search-icon)
    (du/set-attr! search-icon attr-aria-hidden val-true)
    (set! (.-textContent search-icon) val-search-glyph)

    (du/set-attr! input-el attr-part              part-input)
    (du/set-attr! input-el attr-type              val-search)
    (du/set-attr! input-el attr-role              val-combobox)
    (du/set-attr! input-el attr-autocomplete      val-off)
    (du/set-attr! input-el attr-autocorrect       val-off)
    (du/set-attr! input-el attr-spellcheck        val-false)
    (du/set-attr! input-el attr-aria-autocomplete val-list)
    (du/set-attr! input-el attr-aria-expanded     val-false)
    (du/set-attr! input-el attr-aria-controls     list-id)

    (du/set-attr! clear-btn attr-part       part-clear-btn)
    (du/set-attr! clear-btn attr-type       val-button)
    (du/set-attr! clear-btn attr-aria-label val-clear)
    (du/set-attr! clear-btn attr-hidden     "")

    (.appendChild search-wrap search-icon)
    (.appendChild search-wrap input-el)
    (.appendChild search-wrap clear-btn)
    {:search-wrap search-wrap :input input-el :clear-btn clear-btn}))

(defn- make-list-section! []
  (let [list-wrap (.createElement js/document "div")
        list-el   (.createElement js/document "div")
        empty-el  (.createElement js/document "div")]
    (du/set-attr! list-wrap attr-part part-list-wrap)

    (du/set-attr! list-el attr-part       part-list)
    (du/set-attr! list-el attr-id         list-id)
    (du/set-attr! list-el attr-role       val-listbox)
    (du/set-attr! list-el attr-aria-label val-results)

    (du/set-attr! empty-el attr-part   part-empty)
    (du/set-attr! empty-el attr-hidden "")

    (.appendChild list-wrap list-el)
    (.appendChild list-wrap empty-el)
    {:list-wrap list-wrap :list list-el :empty empty-el}))

(defn- make-panel! [search-parts list-parts]
  (let [panel (.createElement js/document "div")]
    (du/set-attr! panel attr-part       part-panel)
    (du/set-attr! panel attr-role       val-dialog)
    (du/set-attr! panel attr-aria-modal val-true)
    (.appendChild panel (:search-wrap search-parts))
    (.appendChild panel (:list-wrap   list-parts))
    panel))

(defn- make-shadow! [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        ^js style     (.createElement js/document "style")
        ^js overlay   (make-overlay!)
        search-parts  (make-search-section!)
        list-parts    (make-list-section!)
        ^js panel     (make-panel! search-parts list-parts)
        refs          #js {}]

    (set! (.-textContent style) style-text)

    (.appendChild root style)
    (.appendChild root overlay)
    (.appendChild root panel)

    (gobj/set refs rk-overlay   overlay)
    (gobj/set refs rk-panel     panel)
    (gobj/set refs rk-input     (:input search-parts))
    (gobj/set refs rk-clear-btn (:clear-btn search-parts))
    (gobj/set refs rk-list      (:list list-parts))
    (gobj/set refs rk-empty     (:empty list-parts))
    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (make-shadow! el)))

;; ── Item rendering ─────────────────────────────────────────────────────────
(defn- append-group-header! [^js list-el group]
  (let [^js header (.createElement js/document "div")]
    (du/set-attr! header attr-part        part-group-header)
    (du/set-attr! header attr-aria-hidden val-true)
    (set! (.-textContent header) group)
    (.appendChild list-el header)))

(defn- append-item! [^js list-el idx item active-idx]
  (let [^js div (.createElement js/document "div")]
    (du/set-attr! div attr-part     part-item)
    (du/set-attr! div attr-role     val-option)
    (du/set-attr! div attr-id       (str item-id-pref idx))
    (du/set-attr! div attr-data-id  (str (:id item)))
    (du/set-attr! div attr-data-idx (str idx))
    (du/set-attr! div attr-tabindex "-1")
    (when (:disabled? item)
      (du/set-attr! div attr-aria-disabled val-true))
    (set! (.-textContent div) (:label item))
    (when (= idx active-idx)
      (du/set-attr! div attr-aria-selected val-true))
    (.appendChild list-el div)))

(defn- render-empty! [^js empty-el ^js input-el]
  (du/remove-attr! empty-el attr-hidden)
  (when input-el (du/remove-attr! input-el attr-aria-activedescendant)))

(defn- render-item-list! [^js empty-el ^js list-el ^js input-el visible active-idx]
  (du/set-attr! empty-el attr-hidden "")
  (loop [remaining (map-indexed vector visible)
         last-group nil]
    (when-let [[idx item] (first remaining)]
      (let [g (:group item)]
        (when (and g (not= g last-group))
          (append-group-header! list-el g))
        (append-item! list-el idx item active-idx)
        (recur (rest remaining) (or g last-group)))))
  (when input-el
    (du/set-attr! input-el attr-aria-activedescendant (str item-id-pref active-idx))))

(defn- render-items! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js list-el  (gobj/get refs rk-list)
          ^js empty-el (gobj/get refs rk-empty)
          ^js input-el (gobj/get refs rk-input)
          items-js     (du/getv el k-items)
          query        (or (du/getv el k-query) "")
          items        (model/normalize-items items-js)
          {:keys [visible]} (model/filter-items items query)
          active-idx   (or (du/getv el k-active-idx) 0)]
      (set! (.-textContent list-el) "")
      (if (empty? visible)
        (render-empty! empty-el input-el)
        (render-item-list! empty-el list-el input-el visible active-idx)))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ────────
(defn- apply-panel-aria! [^js panel {:keys [label]}]
  (du/set-attr! panel attr-aria-label (or label val-default-label)))

(defn- apply-scrim! [^js overlay {:keys [scrim?]}]
  (if scrim?
    (du/remove-attr! overlay attr-hidden)
    (du/set-attr! overlay attr-hidden "")))

(defn- apply-input-state! [^js input {:keys [placeholder open? disabled?]}]
  (du/set-attr! input attr-placeholder    placeholder)
  (du/set-attr! input attr-aria-expanded  (if open? val-true val-false))
  (if disabled?
    (do (du/set-attr! input attr-disabled       "")
        (du/set-attr! input attr-aria-disabled  val-true))
    (do (du/remove-attr! input attr-disabled)
        (du/remove-attr! input attr-aria-disabled))))

(defn- apply-empty-text! [^js empty-el {:keys [empty-text]}]
  (set! (.-textContent empty-el) empty-text))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js panel    (gobj/get refs rk-panel)
          ^js overlay  (gobj/get refs rk-overlay)
          ^js input    (gobj/get refs rk-input)
          ^js empty-el (gobj/get refs rk-empty)]
      (apply-panel-aria!   panel m)
      (apply-scrim!        overlay m)
      (apply-input-state!  input m)
      (apply-empty-text!   empty-el m)
      (render-items! el)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Open / close ───────────────────────────────────────────────────────────
(defn- do-open! [^js el]
  (du/set-attr! el model/attr-open "")
  (let [refs      (du/getv el k-refs)
        ^js input (when refs (gobj/get refs rk-input))]
    (when input (.focus input)))
  (update-from-attrs! el)
  (du/dispatch! el model/event-open #js {}))

(defn- do-close! [^js el]
  (du/remove-attr! el model/attr-open)
  (let [refs      (du/getv el k-refs)
        ^js input (when refs (gobj/get refs rk-input))]
    (when input (du/remove-attr! input attr-aria-activedescendant)))
  (update-from-attrs! el)
  (du/dispatch! el model/event-close #js {}))

(defn- request-open! [^js el]
  (when-not (du/has-attr? el model/attr-open)
    (when (du/dispatch-cancelable! el model/event-open-request #js {})
      (do-open! el))))

(defn- request-close! [^js el]
  (when (du/has-attr? el model/attr-open)
    (when (du/dispatch-cancelable! el model/event-close-request #js {})
      (do-close! el))))

;; ── Keyboard navigation ────────────────────────────────────────────────────
(defn- active-visible [^js el]
  (let [items-js (du/getv el k-items)
        query    (or (du/getv el k-query) "")
        items    (model/normalize-items items-js)
        {:keys [visible]} (model/filter-items items query)]
    visible))

(defn- move-active! [^js el direction]
  (let [visible (active-visible el)
        cur-idx (or (du/getv el k-active-idx) 0)
        new-idx (if (= direction :down)
                  (model/next-active-idx visible cur-idx)
                  (model/prev-active-idx visible cur-idx))]
    (du/setv! el k-active-idx new-idx)
    (render-items! el)
    (let [refs        (du/getv el k-refs)
          ^js list-el (when refs (gobj/get refs rk-list))
          ^js item-el (when list-el
                        (.querySelector list-el (str "[" attr-data-idx "='" new-idx "']")))]
      (when item-el
        (.scrollIntoView item-el #js {:block "nearest"})))))

(defn- select-item! [^js el item]
  (when (and item (not (:disabled? item)))
    (when (du/dispatch-cancelable! el model/event-select-request
                                   #js {:item (clj->js item)})
      (du/dispatch! el model/event-select #js {:item (clj->js item)})
      (request-close! el))))

(defn- select-active! [^js el]
  (let [visible (active-visible el)
        cur-idx (or (du/getv el k-active-idx) 0)]
    (select-item! el (nth visible cur-idx nil))))

;; ── Event handlers ─────────────────────────────────────────────────────────
(defn- on-input-input [^js el ^js _e]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs rk-input))
        ^js clr (when refs (gobj/get refs rk-clear-btn))
        q       (when inp (.-value inp))]
    (du/setv! el k-query (or q ""))
    (du/setv! el k-active-idx 0)
    (when clr
      (if (and q (not= q ""))
        (du/remove-attr! clr attr-hidden)
        (du/set-attr! clr attr-hidden "")))
    (du/dispatch! el model/event-query-change #js {:query (or q "")})
    (render-items! el)))

(defn- on-input-keydown [^js el ^js e]
  (let [k (.-key e)]
    (cond
      (= k key-arrow-down) (do (.preventDefault e) (move-active! el :down))
      (= k key-arrow-up)   (do (.preventDefault e) (move-active! el :up))
      (= k key-enter)      (do (.preventDefault e) (select-active! el))
      (= k key-escape)     (let [m (read-model el)]
                             (when (:close-on-escape? m)
                               (request-close! el))))))

(defn- on-scrim-click [^js el ^js _e]
  (let [m (read-model el)]
    (when (:close-on-scrim? m)
      (request-close! el))))

(defn- on-list-click [^js el ^js e]
  (let [^js target  (.-target e)
        ^js item-el (.closest target (str "[" attr-part "=" part-item "]"))]
    (when item-el
      (let [idx     (js/parseInt (du/get-attr item-el attr-data-idx) 10)
            visible (active-visible el)]
        (select-item! el (nth visible idx nil))))))

(defn- on-clear-click [^js el ^js _e]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs rk-input))
        ^js clr (when refs (gobj/get refs rk-clear-btn))]
    (when inp (set! (.-value inp) ""))
    (when clr (du/set-attr! clr attr-hidden ""))
    (du/setv! el k-query "")
    (du/setv! el k-active-idx 0)
    (du/dispatch! el model/event-query-change #js {:query ""})
    (render-items! el)
    (when inp (.focus inp))))

(defn- add-listeners! [^js el]
  (let [refs        (du/getv el k-refs)
        ^js input   (gobj/get refs rk-input)
        ^js overlay (gobj/get refs rk-overlay)
        ^js list-el (gobj/get refs rk-list)
        ^js clr     (gobj/get refs rk-clear-btn)
        h-input     (fn handle-input-input  [e] (on-input-input  el e))
        h-keydown   (fn handle-input-key    [e] (on-input-keydown el e))
        h-scrim     (fn handle-scrim-click  [e] (on-scrim-click  el e))
        h-list      (fn handle-list-click   [e] (on-list-click   el e))
        h-clear     (fn handle-clear-click  [e] (on-clear-click  el e))
        handlers    #js {}]
    (.addEventListener input   ev-input   h-input)
    (.addEventListener input   ev-keydown h-keydown)
    (.addEventListener overlay ev-click   h-scrim)
    (.addEventListener list-el ev-click   h-list)
    (.addEventListener clr     ev-click   h-clear)
    (gobj/set handlers hk-input   h-input)
    (gobj/set handlers hk-keydown h-keydown)
    (gobj/set handlers hk-scrim   h-scrim)
    (gobj/set handlers hk-list    h-list)
    (gobj/set handlers hk-clear   h-clear)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [refs     (du/getv el k-refs)
        handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (let [^js input   (gobj/get refs rk-input)
            ^js overlay (gobj/get refs rk-overlay)
            ^js list-el (gobj/get refs rk-list)
            ^js clr     (gobj/get refs rk-clear-btn)]
        (.removeEventListener input   ev-input   (gobj/get handlers hk-input))
        (.removeEventListener input   ev-keydown (gobj/get handlers hk-keydown))
        (.removeEventListener overlay ev-click   (gobj/get handlers hk-scrim))
        (.removeEventListener list-el ev-click   (gobj/get handlers hk-list))
        (.removeEventListener clr     ev-click   (gobj/get handlers hk-clear))))))

;; ── Lifecycle ──────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)
    ;; Focus the input on every transition into "open" (whether via property,
    ;; method, or setAttribute).
    (when (and (= attr-name model/attr-open) (some? new-val))
      (let [refs      (du/getv el k-refs)
            ^js input (when refs (gobj/get refs rk-input))]
        (when input
          (js/requestAnimationFrame (fn focus-input-after-raf [] (.focus input))))))))

;; ── Property API ───────────────────────────────────────────────────────────
(defn- define-items-prop! [^js proto]
  (.defineProperty js/Object proto "items"
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                 (or (gobj/get this k-items) #js [])))
                        :set (fn [v]
                               (this-as ^js this
                                 (gobj/set this k-items v)
                                 (gobj/set this k-active-idx 0)
                                 (render-items! this)))}))

(defn- define-methods! [^js proto]
  ;; `show()` matches the HTMLDialogElement imperative-open convention.
  ;; Using "open" here would collide with the `open` boolean property
  ;; (the .defineProperty :value descriptor would shadow the accessor
  ;; at runtime, breaking `el.open = true`).
  (.defineProperty js/Object proto "show"
                   #js {:configurable true
                        :writable     true
                        :value        (fn cp-show [] (this-as ^js this (request-open! this)))})
  (.defineProperty js/Object proto "close"
                   #js {:configurable true
                        :writable     true
                        :value        (fn cp-close [] (this-as ^js this (request-close! this)))})
  (.defineProperty js/Object proto "toggle"
                   #js {:configurable true
                        :writable     true
                        :value        (fn cp-toggle []
                                        (this-as ^js this
                                          (if (.hasAttribute this model/attr-open)
                                            (request-close! this)
                                            (request-open!  this))))}))

(defn- install-property-accessors! [^js proto]
  (du/define-bool-prop! proto "open"     model/attr-open)
  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (define-items-prop! proto)
  (define-methods!    proto))

;; ── Public API ─────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
