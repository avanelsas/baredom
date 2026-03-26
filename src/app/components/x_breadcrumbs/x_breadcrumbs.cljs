(ns app.components.x-breadcrumbs.x-breadcrumbs
  (:require
   [goog.object :as gobj]
   [app.components.x-breadcrumbs.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xBreadcrumbsRefs")
(def ^:private k-model    "__xBreadcrumbsModel")
(def ^:private k-handlers "__xBreadcrumbsHandlers")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-breadcrumbs-color:rgba(0,0,0,0.55);"
   "--x-breadcrumbs-color-current:rgba(0,0,0,0.88);"
   "--x-breadcrumbs-color-hover:rgba(0,0,0,0.80);"
   "--x-breadcrumbs-separator-color:rgba(0,0,0,0.35);"
   "--x-breadcrumbs-font-size:0.875rem;"
   "--x-breadcrumbs-gap:0.25rem;"
   "--x-breadcrumbs-disabled-opacity:0.55;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-breadcrumbs-color:rgba(255,255,255,0.50);"
   "--x-breadcrumbs-color-current:rgba(255,255,255,0.90);"
   "--x-breadcrumbs-color-hover:rgba(255,255,255,0.75);"
   "--x-breadcrumbs-separator-color:rgba(255,255,255,0.30);}}"

   ":host([data-size='sm']){--x-breadcrumbs-font-size:0.75rem;}"
   ":host([data-size='lg']){--x-breadcrumbs-font-size:1rem;}"

   ":host([disabled]){opacity:var(--x-breadcrumbs-disabled-opacity);pointer-events:none;}"

   "[part=root]{"
   "display:block;"
   "font-size:var(--x-breadcrumbs-font-size);}"

   "[part=list]{"
   "display:flex;"
   "flex-wrap:wrap;"
   "align-items:center;"
   "list-style:none;"
   "margin:0;"
   "padding:0;"
   "gap:var(--x-breadcrumbs-gap);}"

   ":host([data-wrap='false']) [part=list]{flex-wrap:nowrap;}"

   ".crumb{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-breadcrumbs-gap);}"

   ".crumb-content{"
   "color:var(--x-breadcrumbs-color);"
   "text-decoration:none;"
   "cursor:pointer;}"

   ".crumb-content:hover{"
   "color:var(--x-breadcrumbs-color-hover);}"

   ".crumb[data-current] .crumb-content{"
   "color:var(--x-breadcrumbs-color-current);"
   "font-weight:500;}"

   ;; Variant: subtle
   ":host([data-variant='subtle']) .crumb-content{"
   "opacity:0.75;}"

   ;; Variant: text (no underline affordance)
   ":host([data-variant='text']) .crumb-content{"
   "text-decoration:none;"
   "border-bottom:none;}"

   ".separator{"
   "color:var(--x-breadcrumbs-separator-color);"
   "user-select:none;"
   "aria-hidden:true;"
   "flex-shrink:0;}"

   ".ellipsis{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-breadcrumbs-gap);}"

   ".ellipsis-btn{"
   "color:var(--x-breadcrumbs-color);"
   "background:none;"
   "border:none;"
   "padding:0;"
   "cursor:pointer;"
   "font:inherit;"
   "line-height:1;}"

   ".ellipsis-btn:hover{"
   "color:var(--x-breadcrumbs-color-hover);}"

   ;; Hidden slot — only for change detection
   "slot{display:none;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=list]{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        nav     (.createElement js/document "nav")
        ol      (.createElement js/document "ol")
        slot-el (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.setAttribute nav "part" "root")
    (.setAttribute ol  "part" "list")
    (.appendChild nav ol)
    (.appendChild root style)
    (.appendChild root nav)
    (.appendChild root slot-el)
    (gobj/set el k-refs {:nav     nav
                         :ol      ol
                         :slot-el slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:separator-raw                (.getAttribute el model/attr-separator)
    :size-raw                     (.getAttribute el model/attr-size)
    :variant-raw                  (.getAttribute el model/attr-variant)
    :wrap-raw                     (.getAttribute el model/attr-wrap)
    :max-items-raw                (.getAttribute el model/attr-max-items)
    :items-before-raw             (.getAttribute el model/attr-items-before)
    :items-after-raw              (.getAttribute el model/attr-items-after)
    :disabled-present?            (.hasAttribute el model/attr-disabled)
    :preserve-aria-current-present? (.hasAttribute el model/attr-preserve-aria-current)
    :aria-label-raw               (.getAttribute el model/attr-aria-label)
    :aria-describedby-raw         (.getAttribute el model/attr-aria-describedby)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- make-separator-el [^js _el sep-text]
  (let [span (.createElement js/document "span")]
    (.setAttribute span "class"      "separator")
    (.setAttribute span "aria-hidden" "true")
    (set! (.-textContent span) sep-text)
    span))

(defn- make-ellipsis-el [^js _el sep-text]
  (let [li   (.createElement js/document "li")
        sep  (.createElement js/document "span")
        btn  (.createElement js/document "button")]
    (.setAttribute li  "class" "ellipsis")
    (.setAttribute sep "class"      "separator")
    (.setAttribute sep "aria-hidden" "true")
    (set! (.-textContent sep) sep-text)
    (.setAttribute btn "class"      "ellipsis-btn")
    (.setAttribute btn "aria-label" "Show hidden breadcrumbs")
    (set! (.-textContent btn) "…")
    (.appendChild li sep)
    (.appendChild li btn)
    li))

(defn- make-crumb-el [^js _el ^js source-child current?]
  (let [li      (.createElement js/document "li")
        wrapper (.createElement js/document "span")
        clone   (.cloneNode source-child true)]
    (.setAttribute li "class" "crumb")
    (.setAttribute wrapper "class" "crumb-content")
    (.appendChild wrapper clone)
    (.appendChild li wrapper)
    (when current?
      (.setAttribute li "data-current" "")
      (.setAttribute wrapper "aria-current" "page"))
    li))

(defn- rebuild-list! [^js el {:keys [separator max-items items-before items-after
                                      preserve-aria-current] :as _m}]
  (let [{:keys [slot-el ol]} (ensure-refs! el)
        ^js slot-el  slot-el
        ^js ol       ol
        children     (array-seq (.assignedElements slot-el))
        total        (count children)
        plan         (model/build-plan total max-items items-before items-after)
        visible      (:visible plan)
        ellipsis-at  (:ellipsis-at plan)]

    ;; Clear existing shadow crumbs
    (set! (.-innerHTML ol) "")

    ;; Build visible items with separators and ellipsis
    (doseq [[pos idx] (map-indexed vector visible)]
      (let [child    (nth children idx nil)
            last?    (= pos (dec (count visible)))
            current? (and last? (not preserve-aria-current))]
        ;; Insert ellipsis before this position if needed
        (when (= pos ellipsis-at)
          (.appendChild ol (make-ellipsis-el el separator)))
        ;; Insert separator before non-first items
        (when (and (pos? pos)
                   (not (= pos ellipsis-at)))
          (.appendChild ol (make-separator-el el separator)))
        ;; Separator after ellipsis position
        (when (= pos ellipsis-at)
          (.appendChild ol (make-separator-el el separator)))
        (when child
          (.appendChild ol (make-crumb-el el child current?)))))

    ;; Edge case: ellipsis at the very end (shouldn't normally happen)
    (when (= ellipsis-at (count visible))
      (.appendChild ol (make-ellipsis-el el separator))))
  nil)

(defn- apply-model! [^js el {:keys [size variant wrap disabled aria-label aria-describedby] :as m}]
  (let [{:keys [nav]} (ensure-refs! el)
        ^js nav nav]
    ;; Data attributes drive CSS selectors
    (.setAttribute el "data-size"    size)
    (.setAttribute el "data-variant" variant)
    (.setAttribute el "data-wrap"    (str (boolean wrap)))

    ;; ARIA on nav
    (if aria-label
      (.setAttribute nav "aria-label" aria-label)
      (.setAttribute nav "aria-label" "Breadcrumb"))
    (if aria-describedby
      (.setAttribute nav "aria-describedby" aria-describedby)
      (.removeAttribute nav "aria-describedby"))

    ;; Rebuild the crumb list
    (rebuild-list! el m)

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)]
    (apply-model! el new-m))
  nil)

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot-el]} (ensure-refs! el)
        ^js slot-el slot-el
        on-slot (fn [_] (update-from-attrs! el))]
    (when slot-el (.addEventListener slot-el "slotchange" on-slot))
    (gobj/set el k-handlers #js {:slot on-slot}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js slot-el (:slot-el refs)
            on-slot (gobj/get hs "slot")]
        (when (and slot-el on-slot)
          (.removeEventListener slot-el "slotchange" on-slot)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- def-string-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-string-prop-default! [^js proto attr default]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

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

(defn- def-int-prop! [^js proto attr default]
  (.defineProperty js/Object proto attr
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
  (def-string-prop-default! proto model/attr-separator    model/default-separator)
  (def-string-prop-default! proto model/attr-size         model/default-size)
  (def-string-prop-default! proto model/attr-variant      model/default-variant)
  (def-bool-prop!   proto model/attr-wrap)
  (def-bool-prop!   proto model/attr-disabled)
  (def-bool-prop!   proto model/attr-preserve-aria-current)
  (def-int-prop!    proto model/attr-max-items    nil)
  (def-int-prop!    proto model/attr-items-before model/default-items-before)
  (def-int-prop!    proto model/attr-items-after  model/default-items-after)
  (def-string-prop! proto model/attr-aria-label)
  (def-string-prop! proto model/attr-aria-describedby))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)   ; reconnect guard
                     (add-listeners! this)
                     (update-from-attrs! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [attr-name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       ;; disabled is managed inside apply-model!, no special case needed
                       (update-from-attrs! this))
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
