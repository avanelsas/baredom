(ns app.components.x-pagination.x-pagination
  (:require
   [goog.object :as gobj]
   [app.components.x-pagination.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xPaginationRefs")
(def ^:private k-handlers "__xPaginationHandlers")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-pagination-gap:0.25rem;"
   "--x-pagination-button-size:2rem;"
   "--x-pagination-button-radius:0.375rem;"
   "--x-pagination-button-bg:transparent;"
   "--x-pagination-button-color:rgba(0,0,0,0.75);"
   "--x-pagination-button-border:1px solid rgba(0,0,0,0.15);"
   "--x-pagination-button-hover-bg:rgba(0,0,0,0.06);"
   "--x-pagination-button-hover-color:rgba(0,0,0,0.9);"
   "--x-pagination-current-bg:rgba(0,0,0,0.88);"
   "--x-pagination-current-color:#fff;"
   "--x-pagination-current-border:1px solid transparent;"
   "--x-pagination-disabled-opacity:0.4;"
   "--x-pagination-font-size:0.875rem;"
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
   "[part~='button']{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- make-prev-li! []
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")
        sl  (.createElement js/document "slot")]
    (.setAttribute li  "part"       "item item-prev")
    (.setAttribute btn "part"       "button button-prev")
    (.setAttribute btn "aria-label" "Previous page")
    (.setAttribute sl  "name"       model/slot-prev)
    (set! (.-textContent sl) "Prev")
    (.appendChild btn sl)
    (.appendChild li btn)
    {:li li :btn btn}))

(defn- make-next-li! []
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")
        sl  (.createElement js/document "slot")]
    (.setAttribute li  "part"       "item item-next")
    (.setAttribute btn "part"       "button button-next")
    (.setAttribute btn "aria-label" "Next page")
    (.setAttribute sl  "name"       model/slot-next)
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
    (.setAttribute nav "part" "nav")
    (.setAttribute ol  "part" "list")
    (.appendChild ol (:li prev))
    (.appendChild ol (:li nxt))
    (.appendChild nav ol)
    (.appendChild root style)
    (.appendChild root nav)
    (gobj/set el k-refs {:nav      nav
                         :ol       ol
                         :prev-li  (:li prev)
                         :prev-btn (:btn prev)
                         :next-li  (:li nxt)
                         :next-btn (:btn nxt)}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── DOM item builders ─────────────────────────────────────────────────────
(defn- make-page-button! [n current? disabled?]
  (let [li  (.createElement js/document "li")
        btn (.createElement js/document "button")]
    (.setAttribute li  "part"      "item item-page")
    (.setAttribute li  "data-page" (str n))
    (.setAttribute btn "part"      "button button-page")
    (.setAttribute btn "aria-label" (str "Page " n))
    (.setAttribute btn "data-page" (str n))
    (when current?
      (.setAttribute btn "aria-current" "page")
      (.setAttribute btn "data-current" ""))
    (when disabled?
      (.setAttribute btn "disabled" ""))
    (set! (.-textContent btn) (str n))
    (.appendChild li btn)
    li))

(defn- make-ellipsis-li! []
  (let [li   (.createElement js/document "li")
        span (.createElement js/document "span")]
    (.setAttribute li   "part"        "item item-ellipsis")
    (.setAttribute span "part"        "ellipsis")
    (.setAttribute span "aria-hidden" "true")
    (set! (.-textContent span) "…")
    (.appendChild li span)
    li))

;; ── Read model from attributes ────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:page-raw            (.getAttribute el model/attr-page)
    :total-pages-raw     (.getAttribute el model/attr-total-pages)
    :sibling-count-raw   (.getAttribute el model/attr-sibling-count)
    :boundary-count-raw  (.getAttribute el model/attr-boundary-count)
    :size-raw            (.getAttribute el model/attr-size)
    :disabled-present?   (.hasAttribute el model/attr-disabled)
    :label-raw           (.getAttribute el model/attr-label)}))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [refs     (ensure-refs! el)
        ^js ol   (:ol refs)
        ^js nav  (:nav refs)
        prev-btn (:prev-btn refs)
        next-btn (:next-btn refs)
        prev-li  (:prev-li refs)
        next-li  (:next-li refs)
        m        (read-model el)
        {:keys [page total-pages sibling-count boundary-count size label]} m
        items    (model/build-page-items page total-pages sibling-count boundary-count)]

    ;; Update nav aria-label and host data-size
    (.setAttribute nav "aria-label" label)
    (.setAttribute el  "data-size"  size)

    ;; Update prev/next disabled state
    (if (model/prev-disabled? m)
      (.setAttribute prev-btn "disabled" "")
      (.removeAttribute prev-btn "disabled"))
    (if (model/next-disabled? m)
      (.setAttribute next-btn "disabled" "")
      (.removeAttribute next-btn "disabled"))

    ;; Rebuild ol: clear, re-add prev, page items, next
    (set! (.-innerHTML ol) "")
    (.appendChild ol prev-li)
    (doseq [item items]
      (if (= :ellipsis (:type item))
        (.appendChild ol (make-ellipsis-li!))
        (.appendChild ol (make-page-button! (:n item) (= (:n item) page) (:disabled m)))))
    (.appendChild ol next-li))
  nil)

;; ── Event dispatch ────────────────────────────────────────────────────────
(defn- dispatch-page-change! [^js el page]
  (.dispatchEvent
   el
   (js/CustomEvent.
    model/event-page-change
    #js {:detail    #js {:page page}
         :bubbles   true
         :composed  true
         :cancelable false})))

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
      (let [refs     (gobj/get el k-refs)
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
    (gobj/set el k-handlers #js {:click handler}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js ol   (:ol refs)
            on-click (gobj/get hs "click")]
        (when (and ol on-click)
          (.removeEventListener ol "click" on-click)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- def-bool-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr "")
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-int-prop! [^js proto prop-name attr default]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-pos-int
                                         (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-int-prop! proto model/attr-page          model/attr-page          model/default-page)
  (def-int-prop! proto model/attr-total-pages   model/attr-total-pages   model/default-total-pages)
  (def-int-prop! proto model/attr-sibling-count  model/attr-sibling-count  model/default-sibling-count)
  (def-int-prop! proto model/attr-boundary-count model/attr-boundary-count model/default-boundary-count)
  (def-bool-prop! proto model/attr-disabled))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (render! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_attr-name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (render! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
