(ns baredom.components.x-avatar-group.x-avatar-group
  (:require
   [goog.object :as gobj]
   [baredom.components.x-avatar-group.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xAvatarGroupRefs")
(def ^:private k-model    "__xAvatarGroupModel")
(def ^:private k-handlers "__xAvatarGroupHandlers")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "flex-direction:row;"
   "color-scheme:light dark;"
   "--x-avatar-group-overflow-bg:rgba(0,0,0,0.08);"
   "--x-avatar-group-overflow-color:rgba(0,0,0,0.70);"
   "--x-avatar-group-overflow-border:rgba(0,0,0,0.14);"
   "--x-avatar-group-overflow-ring:#ffffff;"
   "--x-avatar-group-size:32px;"
   "--x-avatar-group-font-size:0.75rem;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-avatar-group-overflow-bg:rgba(255,255,255,0.10);"
   "--x-avatar-group-overflow-color:rgba(255,255,255,0.75);"
   "--x-avatar-group-overflow-border:rgba(255,255,255,0.18);"
   "--x-avatar-group-overflow-ring:#0b0c0f;}}"

   ":host([data-size='xs']){--x-avatar-group-size:20px;--x-avatar-group-font-size:0.625rem;}"
   ":host([data-size='sm']){--x-avatar-group-size:24px;--x-avatar-group-font-size:0.6875rem;}"
   ":host([data-size='md']){--x-avatar-group-size:32px;--x-avatar-group-font-size:0.75rem;}"
   ":host([data-size='lg']){--x-avatar-group-size:40px;--x-avatar-group-font-size:0.875rem;}"
   ":host([data-size='xl']){--x-avatar-group-size:48px;--x-avatar-group-font-size:1rem;}"

   ;; RTL reverses stacking order (last avatar on left, first on right)
   ":host([data-direction='rtl']){flex-direction:row-reverse;}"

   "[part=overflow]{"
   "display:none;"
   "flex-shrink:0;"
   "width:var(--x-avatar-group-size);"
   "height:var(--x-avatar-group-size);"
   "border-radius:999px;"
   "background:var(--x-avatar-group-overflow-bg);"
   "color:var(--x-avatar-group-overflow-color);"
   "border:1px solid var(--x-avatar-group-overflow-border);"
   "box-shadow:0 0 0 2px var(--x-avatar-group-overflow-ring);"
   "font-size:var(--x-avatar-group-font-size);"
   "font-weight:600;"
   "line-height:1;"
   "align-items:center;"
   "justify-content:center;"
   "user-select:none;"
   "box-sizing:border-box;"
   "white-space:nowrap;}"

   "::slotted(x-avatar){flex-shrink:0;}"

   "@media (prefers-reduced-motion:reduce){"
   "::slotted(x-avatar){transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style      (.createElement js/document "style")
        slot-el    (.createElement js/document "slot")
        overflow   (.createElement js/document "span")]
    (set! (.-textContent style) style-text)
    (.setAttribute overflow "part" "overflow")
    (.setAttribute overflow "aria-hidden" "true")
    (.appendChild root style)
    (.appendChild root slot-el)
    (.appendChild root overflow)
    (gobj/set el k-refs {:slot-el  slot-el
                         :overflow overflow}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:size-raw          (.getAttribute el model/attr-size)
    :overlap-raw       (.getAttribute el model/attr-overlap)
    :max-raw           (.getAttribute el model/attr-max)
    :direction-raw     (.getAttribute el model/attr-direction)
    :disabled-present? (.hasAttribute el model/attr-disabled)
    :label-raw         (.getAttribute el model/attr-label)}))

;; ── Layout application ────────────────────────────────────────────────────
(defn- apply-layout! [^js el {:keys [size overlap max direction disabled label] :as m}]
  (let [{:keys [slot-el overflow]} (ensure-refs! el)
        ^js slot-el  slot-el
        ^js overflow overflow
        children     (array-seq (.assignedElements slot-el))
        total        (count children)
        [vis-count hidden-count] (model/compute-visible-hidden total max)
        margin       (get model/overlap-margin overlap "0px")]

    ;; Data attributes drive CSS selectors
    (.setAttribute el "data-size"      size)
    (.setAttribute el "data-overlap"   overlap)
    (.setAttribute el "data-direction" direction)

    ;; ARIA on host
    (.setAttribute el "role" "group")
    (if label
      (.setAttribute el "aria-label" label)
      (.removeAttribute el "aria-label"))
    (if disabled
      (.setAttribute el "aria-disabled" "true")
      (.removeAttribute el "aria-disabled"))

    ;; Apply size, disabled, overlap margin to each child avatar
    (doseq [[^js child idx] (map vector children (range))]
      (if (< idx vis-count)
        (do
          ;; Show child
          (set! (.. child -style -display) "")
          ;; Propagate size
          (.setAttribute child "size" size)
          ;; Propagate disabled
          (if disabled
            (.setAttribute child "disabled" "")
            (.removeAttribute child "disabled"))
          ;; Overlap margin (not on first child in display order)
          (set! (.. child -style -marginInlineStart)
                (if (pos? idx) margin "0px")))
        ;; Hide overflow children
        (set! (.. child -style -display) "none")))

    ;; Overflow bubble
    (if (pos? hidden-count)
      (do
        (set! (.-textContent overflow) (str "+" hidden-count))
        (.setAttribute overflow "role"       "img")
        (.setAttribute overflow "aria-label" (str hidden-count " more"))
        (set! (.. overflow -style -display)          "inline-flex")
        (set! (.. overflow -style -marginInlineStart) margin))
      (do
        (set! (.-textContent overflow) "")
        (set! (.. overflow -style -display) "none")))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-layout! el new-m)))
  nil)

(defn- refresh-layout! [^js el]
  ;; Force re-apply even when model hasn't changed (children changed)
  (apply-layout! el (read-model el))
  nil)

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot-el]} (ensure-refs! el)
        ^js slot-el slot-el
        on-slot (fn [_] (refresh-layout! el))]
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

(defn- def-int-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-max (.getAttribute this attr))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-string-prop-default! proto model/attr-size      model/default-size)
  (def-string-prop-default! proto model/attr-overlap   model/default-overlap)
  (def-string-prop-default! proto model/attr-direction model/default-direction)
  (def-string-prop! proto model/attr-label)
  (def-bool-prop!   proto model/attr-disabled)
  (def-int-prop!    proto model/attr-max))

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
                     (refresh-layout! this)
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
