(ns baredom.components.x-badge.x-badge
  (:require
   [goog.object :as gobj]
   [baredom.components.x-badge.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xBadgeRefs")
(def ^:private k-model    "__xBadgeModel")
(def ^:private k-handlers "__xBadgeHandlers")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-flex;"
   "vertical-align:middle;"
   "align-items:center;"
   "justify-content:center;"
   "color-scheme:light dark;"
   "--x-badge-bg:rgba(0,0,0,0.08);"
   "--x-badge-color:rgba(0,0,0,0.80);"
   "--x-badge-border:rgba(0,0,0,0.12);"
   "--x-badge-font-size:0.75rem;"
   "--x-badge-height:1.25rem;"
   "--x-badge-padding:0 0.375rem;"
   "--x-badge-radius:0.25rem;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-badge-bg:rgba(255,255,255,0.12);"
   "--x-badge-color:rgba(255,255,255,0.88);"
   "--x-badge-border:rgba(255,255,255,0.14);}}"

   ;; Variant overrides
   ":host([data-variant='info']){"
   "--x-badge-bg:rgba(0,102,204,0.12);"
   "--x-badge-color:rgba(0,60,140,0.90);"
   "--x-badge-border:rgba(0,102,204,0.25);}"

   ":host([data-variant='success']){"
   "--x-badge-bg:rgba(16,140,72,0.12);"
   "--x-badge-color:rgba(0,90,45,0.90);"
   "--x-badge-border:rgba(16,140,72,0.25);}"

   ":host([data-variant='warning']){"
   "--x-badge-bg:rgba(204,120,0,0.12);"
   "--x-badge-color:rgba(140,70,0,0.90);"
   "--x-badge-border:rgba(204,120,0,0.25);}"

   ":host([data-variant='error']){"
   "--x-badge-bg:rgba(190,20,40,0.12);"
   "--x-badge-color:rgba(140,0,20,0.90);"
   "--x-badge-border:rgba(190,20,40,0.25);}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-variant='info']){"
   "--x-badge-bg:rgba(80,160,255,0.18);"
   "--x-badge-color:rgba(210,235,255,0.95);"
   "--x-badge-border:rgba(80,160,255,0.35);}"
   ":host([data-variant='success']){"
   "--x-badge-bg:rgba(60,210,120,0.18);"
   "--x-badge-color:rgba(200,255,230,0.95);"
   "--x-badge-border:rgba(60,210,120,0.35);}"
   ":host([data-variant='warning']){"
   "--x-badge-bg:rgba(255,190,90,0.18);"
   "--x-badge-color:rgba(255,235,180,0.95);"
   "--x-badge-border:rgba(255,190,90,0.35);}"
   ":host([data-variant='error']){"
   "--x-badge-bg:rgba(255,90,110,0.18);"
   "--x-badge-color:rgba(255,210,215,0.95);"
   "--x-badge-border:rgba(255,90,110,0.35);}}"

   ;; Size — sm
   ":host([data-size='sm']){"
   "--x-badge-font-size:0.6875rem;"
   "--x-badge-height:1rem;"
   "--x-badge-padding:0 0.25rem;"
   "--x-badge-radius:0.1875rem;}"

   ;; Base element
   "[part=base]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "min-height:var(--x-badge-height);"
   "padding:var(--x-badge-padding);"
   "border-radius:var(--x-badge-radius);"
   "border:1px solid var(--x-badge-border);"
   "background:var(--x-badge-bg);"
   "color:var(--x-badge-color);"
   "font-size:var(--x-badge-font-size);"
   "font-weight:600;"
   "line-height:1;"
   "white-space:nowrap;"
   "user-select:none;"
   "box-sizing:border-box;}"

   ;; Pill modifier
   ":host([data-pill]) [part=base]{border-radius:999px;}"

   ;; Dot mode — equal width/height, no padding, no label
   ":host([data-dot]) [part=base]{"
   "min-width:var(--x-badge-height);"
   "padding:0;}"
   ":host([data-mode='dot']) [part=label],"
   ":host([data-mode='empty']) [part=base]{"
   "display:none;}"

   ;; Slot content — hidden by default; shown only in :slot mode
   "slot{display:none;}"
   ":host([data-mode='slot']) slot{display:contents;}"
   ":host([data-mode='slot']) [part=label]{display:none;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=base]{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (.createElement js/document "style")
        base     (.createElement js/document "div")
        slot-el  (.createElement js/document "slot")
        label-el (.createElement js/document "span")]
    (set! (.-textContent style) style-text)
    (.setAttribute base     "part" "base")
    (.setAttribute base     "role" "status")
    (.setAttribute label-el "part" "label")
    (.appendChild base slot-el)
    (.appendChild base label-el)
    (.appendChild root style)
    (.appendChild root base)
    (gobj/set el k-refs {:base     base
                         :slot-el  slot-el
                         :label-el label-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  ;; No flatten — only user-slotted nodes, not fallback content
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- read-model [^js el]
  (let [{:keys [slot-el]} (ensure-refs! el)]
    (model/normalize
     {:variant-raw          (.getAttribute el model/attr-variant)
      :size-raw             (.getAttribute el model/attr-size)
      :pill-raw             (.getAttribute el model/attr-pill)
      :dot-raw              (.getAttribute el model/attr-dot)
      :count-raw            (.getAttribute el model/attr-count)
      :max-raw              (.getAttribute el model/attr-max)
      :text-raw             (.getAttribute el model/attr-text)
      :aria-label-raw       (.getAttribute el model/attr-aria-label)
      :aria-describedby-raw (.getAttribute el model/attr-aria-describedby)
      :has-slot?            (slot-has-content? slot-el)})))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [variant size pill dot aria-label aria-describedby] :as m}]
  (let [{:keys [base label-el]} (ensure-refs! el)
        ^js base     base
        ^js label-el label-el
        mode (model/compute-mode m)
        txt  (model/display-text m)]
    ;; Data attributes drive CSS selectors
    (.setAttribute el "data-variant" variant)
    (.setAttribute el "data-size"    size)
    (.setAttribute el "data-mode"    (name mode))
    (if pill (.setAttribute el "data-pill" "") (.removeAttribute el "data-pill"))
    (if dot  (.setAttribute el "data-dot"  "") (.removeAttribute el "data-dot"))
    ;; Label text
    (set! (.-textContent label-el) (or txt ""))
    ;; ARIA on base
    (if aria-label
      (.setAttribute base "aria-label" aria-label)
      (.removeAttribute base "aria-label"))
    (if aria-describedby
      (.setAttribute base "aria-describedby" aria-describedby)
      (.removeAttribute base "aria-describedby"))
    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
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
                                        (model/parse-int-attr
                                         (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-string-prop-default! proto model/attr-variant model/default-variant)
  (def-string-prop-default! proto model/attr-size    model/default-size)
  (def-bool-prop!   proto model/attr-pill)
  (def-bool-prop!   proto model/attr-dot)
  (def-string-prop! proto model/attr-text)
  (def-string-prop! proto model/attr-aria-label)
  (def-string-prop! proto model/attr-aria-describedby)
  (def-int-prop!    proto model/attr-count nil)
  (def-int-prop!    proto model/attr-max   model/default-max)
  ;; displayText — computed read-only property
  (.defineProperty js/Object proto "displayText"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/display-text (read-model this))))
                        :enumerable true :configurable true}))

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
          (fn [_attr old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
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
