(ns baredom.components.x-pagination.x-pagination
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baremirror.core :as mirror]
            [goog.object :as gobj]
            [baredom.components.x-pagination.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xPaginationRefs")
(def ^:private k-handlers "__xPaginationHandlers")
(def ^:private k-model    "__xPaginationModel")

(def ^:private sel-page-button "[part~='button-page']")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-pagination-gap:0.25rem;"
   "--x-pagination-button-size:2rem;"
   "--x-pagination-button-radius:var(--x-radius-sm,0.375rem);"
   "--x-pagination-button-bg:transparent;"
   "--x-pagination-button-color:rgba(0,0,0,0.75);"
   "--x-pagination-button-border:1px solid rgba(0,0,0,0.15);"
   "--x-pagination-button-hover-bg:rgba(0,0,0,0.06);"
   "--x-pagination-button-hover-color:rgba(0,0,0,0.9);"
   "--x-pagination-current-bg:rgba(0,0,0,0.88);"
   "--x-pagination-current-color:#fff;"
   "--x-pagination-current-border:1px solid transparent;"
   "--x-pagination-disabled-opacity:0.4;"
   "--x-pagination-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-pagination-ellipsis-color:rgba(0,0,0,0.45);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-pagination-button-color:rgba(255,255,255,0.75);"
   "--x-pagination-button-border:1px solid rgba(255,255,255,0.15);"
   "--x-pagination-button-hover-bg:rgba(255,255,255,0.08);"
   "--x-pagination-button-hover-color:rgba(255,255,255,0.95);"
   "--x-pagination-current-bg:rgba(255,255,255,0.90);"
   "--x-pagination-current-color:#000;"
   "--x-pagination-ellipsis-color:rgba(255,255,255,0.40);}}"

   ":host([data-size='sm']){"
   "--x-pagination-button-size:1.75rem;"
   "--x-pagination-font-size:0.75rem;}"

   ":host([data-size='lg']){"
   "--x-pagination-button-size:2.5rem;"
   "--x-pagination-font-size:1rem;}"

   "[part~='nav']{"
   "display:block;}"

   "[part~='list']{"
   "display:flex;"
   "flex-wrap:wrap;"
   "align-items:center;"
   "list-style:none;"
   "margin:0;"
   "padding:0;"
   "gap:var(--x-pagination-gap);}"

   "[part~='button']{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "min-width:var(--x-pagination-button-size);"
   "height:var(--x-pagination-button-size);"
   "padding:0 0.5rem;"
   "border:var(--x-pagination-button-border);"
   "border-radius:var(--x-pagination-button-radius);"
   "background:var(--x-pagination-button-bg);"
   "color:var(--x-pagination-button-color);"
   "font:inherit;"
   "font-size:var(--x-pagination-font-size);"
   "cursor:pointer;"
   "user-select:none;"
   "box-sizing:border-box;"
   "transition:background 0.15s,color 0.15s;}"

   "[part~='button']:hover:not(:disabled){"
   "background:var(--x-pagination-button-hover-bg);"
   "color:var(--x-pagination-button-hover-color);}"

   "[part~='button'][data-current]{"
   "background:var(--x-pagination-current-bg);"
   "color:var(--x-pagination-current-color);"
   "border:var(--x-pagination-current-border);"
   "font-weight:600;"
   "cursor:default;}"

   "[part~='button']:disabled{"
   "opacity:var(--x-pagination-disabled-opacity);"
   "cursor:not-allowed;}"

   "[part~='ellipsis']{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "min-width:var(--x-pagination-button-size);"
   "height:var(--x-pagination-button-size);"
   "color:var(--x-pagination-ellipsis-color);"
   "font-size:var(--x-pagination-font-size);"
   "user-select:none;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part~='button']{transition:none !important;}}"

   "@media (pointer:coarse){"
   "[part~='button']{min-width:2.75rem;height:2.75rem;}"
   "}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- make-prev-li! []
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")
        sl  (.createElement js/document "slot")]
    (du/set-attr! li  "part"       "item item-prev")
    (du/set-attr! btn "part"       "button button-prev")
    (du/set-attr! btn "aria-label" "Previous page")
    (du/set-attr! sl  "name"       model/slot-prev)
    (set! (.-textContent sl) "Prev")
    (.appendChild btn sl)
    (.appendChild li btn)
    {:li li :btn btn}))

(defn- make-next-li! []
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")
        sl  (.createElement js/document "slot")]
    (du/set-attr! li  "part"       "item item-next")
    (du/set-attr! btn "part"       "button button-next")
    (du/set-attr! btn "aria-label" "Next page")
    (du/set-attr! sl  "name"       model/slot-next)
    (set! (.-textContent sl) "Next")
    (.appendChild btn sl)
    (.appendChild li btn)
    {:li li :btn btn}))

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        nav   (.createElement js/document "nav")
        ol    (.createElement js/document "ol")
        prev  (make-prev-li!)
        nxt   (make-next-li!)]
    (set! (.-textContent style) style-text)
    (du/set-attr! nav "part" "nav")
    (du/set-attr! ol  "part" "list")
    (.appendChild ol (:li prev))
    (.appendChild ol (:li nxt))
    (.appendChild nav ol)
    (.appendChild root style)
    (.appendChild root nav)
    (du/setv! el k-refs {:nav      nav
                         :ol       ol
                         :prev-li  (:li prev)
                         :prev-btn (:btn prev)
                         :next-li  (:li nxt)
                         :next-btn (:btn nxt)})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── DOM item builders ─────────────────────────────────────────────────────
(defn- make-page-li! [n]
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")]
    (du/set-attr! li  "part"      "item item-page")
    (du/set-attr! li  "data-page" (str n))
    (du/set-attr! btn "part"      "button button-page")
    (du/set-attr! btn "aria-label" (str "Page " n))
    (du/set-attr! btn "data-page" (str n))
    (set! (.-textContent btn) (str n))
    (.appendChild li btn)
    li))

(defn- apply-page-state!
  "Marks the button of a page item as current or not, and as disabled or not."
  [^js li current? disabled?]
  (let [btn (.querySelector li sel-page-button)]
    (du/set-attr-to! btn "aria-current" (when current? "page"))
    (du/set-attr-to! btn "data-current" (when current? ""))
    (du/set-attr-to! btn "disabled" (when disabled? ""))))

(defn- make-ellipsis-li! []
  (let [li   (.createElement js/document "li")
        span (.createElement js/document "span")]
    (du/set-attr! li   "part"        "item item-ellipsis")
    (du/set-attr! span "part"        "ellipsis")
    (du/set-attr! span "aria-hidden" "true")
    (set! (.-textContent span) "…")
    (.appendChild li span)
    li))

;; ── Read model from attributes ────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:page-raw            (du/get-attr el model/attr-page)
    :total-pages-raw     (du/get-attr el model/attr-total-pages)
    :sibling-count-raw   (du/get-attr el model/attr-sibling-count)
    :boundary-count-raw  (du/get-attr el model/attr-boundary-count)
    :size-raw            (du/get-attr el model/attr-size)
    :disabled-present?   (du/has-attr? el model/attr-disabled)
    :label-raw           (du/get-attr el model/attr-label)}))

(defn- item-with-key [items k]
  (first (filter (comp #{k} :key) items)))

(defn- make-item!
  "A new node for the item with key `k`."
  [items k]
  (let [{:keys [type n]} (item-with-key items k)]
    (if (= :ellipsis type)
      (make-ellipsis-li!)
      (make-page-li! n))))

(defn- apply-item! [nodes page disabled? {:keys [type n key]}]
  (when (= :page type)
    (apply-page-state! (nodes key) (= n page) disabled?)))

(defn- render-items!
  "Brings the page items of the list in step with `items`. An item that stays keeps its node."
  [^js ol ^js next-li {:keys [page disabled]} items]
  (let [nodes (mirror/sync! {:items {:parent ol :before next-li}}
                            {:items (mapv :key items)}
                            (partial make-item! items))]
    (run! (partial apply-item! nodes page disabled) items)))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- apply-model!
  [^js el {:keys [page total-pages sibling-count boundary-count size label] :as m}]
  (let [{:keys [ol nav prev-btn next-btn next-li]} (ensure-refs! el)]
    (du/set-attr-to! nav "aria-label" label)
    (du/set-attr-to! el "data-size" size)
    (du/set-attr-to! prev-btn "disabled" (when (model/prev-disabled? m) ""))
    (du/set-attr-to! next-btn "disabled" (when (model/next-disabled? m) ""))
    (render-items! ol next-li m (model/build-page-items page total-pages sibling-count boundary-count))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Event dispatch ────────────────────────────────────────────────────────
(defn- dispatch-page-change! [^js el page]
  (let [m        (read-model el)
        cur-page (:page m)
        allowed? (du/dispatch-cancelable!
                  el model/event-change-request
                  #js {:page page :previousPage cur-page})]
    (when allowed?
      (du/dispatch! el model/event-page-change #js {:page page}))))

;; ── Click handler (event delegation) ─────────────────────────────────────
(defn- find-button-in-path [^js ev]
  (let [path (.composedPath ev)
        len  (.-length path)]
    (loop [i 0]
      (when (< i len)
        (let [^js node (aget path i)
              tag      (when node (.-tagName node))]
          (if (= "BUTTON" tag)
            node
            (recur (inc i))))))))

(defn- on-click! [^js el ^js ev]
  (let [btn (find-button-in-path ev)]
    (when (and btn (not (.hasAttribute btn "disabled")))
      (let [refs     (du/getv el k-refs)
            prev-btn (:prev-btn refs)
            next-btn (:next-btn refs)
            page-attr (.getAttribute btn "data-page")]
        (cond
          (identical? btn prev-btn)
          (let [m   (read-model el)
                cur (:page m)]
            (when (> cur 1)
              (dispatch-page-change! el (dec cur))))

          (identical? btn next-btn)
          (let [m   (read-model el)
                cur (:page m)
                tot (:total-pages m)]
            (when (< cur tot)
              (dispatch-page-change! el (inc cur))))

          (some? page-attr)
          (let [n (js/parseInt page-attr 10)]
            (when (js/isFinite n)
              (dispatch-page-change! el n))))))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs    (ensure-refs! el)
        ^js ol  (:ol refs)
        handler (fn [^js ev] (on-click! el ev))]
    (.addEventListener ol "click" handler)
    (du/setv! el k-handlers #js {:click handler})))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (when-let [refs (du/getv el k-refs)]
      (let [^js ol   (:ol refs)
            on-click (gobj/get hs "click")]
        (when (and ol on-click)
          (.removeEventListener ol "click" on-click)))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ────────────────────────────────────────────────────
;; `page` is the only int prop without a 1-arg parse fn in the model
;; (model/parse-page also takes total-pages), so wrap parse-pos-int inline.
(defn- parse-page-attr [s]
  (model/parse-pos-int s model/default-page))

(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto model/attr-page           model/attr-page           parse-page-attr)
  (du/define-parsed-prop! proto model/attr-total-pages    model/attr-total-pages    model/parse-total-pages)
  (du/define-parsed-prop! proto model/attr-sibling-count  model/attr-sibling-count  model/parse-sibling-count)
  (du/define-parsed-prop! proto model/attr-boundary-count model/attr-boundary-count model/parse-boundary-count)
  (du/define-string-prop! proto model/attr-size           model/attr-size           model/default-size)
  (du/define-bool-prop!   proto model/attr-disabled       model/attr-disabled)
  (du/define-string-prop! proto model/attr-label          model/attr-label          model/default-label))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
