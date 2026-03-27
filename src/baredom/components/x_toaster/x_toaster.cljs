(ns baredom.components.x-toaster.x-toaster
  (:require
   [goog.object :as gobj]
   [baredom.components.x-toaster.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xToasterRefs")
(def ^:private k-model    "__xToasterModel")
(def ^:private k-handlers "__xToasterHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ;; Host: fixed overlay, flex container, no pointer events (pass-through background)
   ":host{"
   "display:flex;"
   "position:fixed;"
   "z-index:var(--x-toaster-z-index,9000);"
   "gap:var(--x-toaster-gap,8px);"
   "width:max-content;"
   "max-width:var(--x-toaster-max-width,480px);"
   "box-sizing:border-box;"
   "pointer-events:none;"
   "color-scheme:light dark;}"

   ;; Restore pointer events on slotted toasts so they are interactive
   "::slotted(" model/child-tag "){"
   "pointer-events:auto;}"

   ;; Slot display:contents so slotted children participate in host flex layout
   "slot{display:contents;}"

   ;; ── Position: top-start ──────────────────────────────────────────────────
   ":host([data-position='top-start']){"
   "top:var(--x-toaster-inset,16px);"
   "inset-inline-start:var(--x-toaster-inset,16px);"
   "flex-direction:column-reverse;}"

   ;; ── Position: top-center ─────────────────────────────────────────────────
   ":host([data-position='top-center']){"
   "top:var(--x-toaster-inset,16px);"
   "left:50%;"
   "transform:translateX(-50%);"
   "flex-direction:column-reverse;"
   "align-items:center;}"

   ;; ── Position: top-end (default) ──────────────────────────────────────────
   ":host([data-position='top-end']){"
   "top:var(--x-toaster-inset,16px);"
   "inset-inline-end:var(--x-toaster-inset,16px);"
   "flex-direction:column-reverse;}"

   ;; ── Position: bottom-start ───────────────────────────────────────────────
   ":host([data-position='bottom-start']){"
   "bottom:var(--x-toaster-inset,16px);"
   "inset-inline-start:var(--x-toaster-inset,16px);"
   "flex-direction:column;}"

   ;; ── Position: bottom-center ──────────────────────────────────────────────
   ":host([data-position='bottom-center']){"
   "bottom:var(--x-toaster-inset,16px);"
   "left:50%;"
   "transform:translateX(-50%);"
   "flex-direction:column;"
   "align-items:center;}"

   ;; ── Position: bottom-end ─────────────────────────────────────────────────
   ":host([data-position='bottom-end']){"
   "bottom:var(--x-toaster-inset,16px);"
   "inset-inline-end:var(--x-toaster-inset,16px);"
   "flex-direction:column;}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        slot-el (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.appendChild root style)
    (.appendChild root slot-el)
    (gobj/set el k-refs {:root    root
                         :slot-el slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:position-raw   (.getAttribute el model/attr-position)
    :max-toasts-raw (.getAttribute el model/attr-max-toasts)
    :label-raw      (.getAttribute el model/attr-label)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [position label] :as m}]
  (ensure-refs! el)
  (.setAttribute el "role"       "region")
  (.setAttribute el "aria-label" label)
  (.setAttribute el "data-position" position)
  (gobj/set el k-model m)
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch-toaster-dismiss! [^js el ^js child-detail]
  (let [type    (gobj/get child-detail "type")
        reason  (gobj/get child-detail "reason")
        heading (gobj/get child-detail "heading")
        message (gobj/get child-detail "message")
        detail  (clj->js (model/dismiss-detail type reason heading message))
        event   (js/CustomEvent.
                 model/event-dismiss
                 #js {:detail     detail
                      :bubbles    true
                      :composed   true
                      :cancelable true})]
    (.dispatchEvent el event)))

;; ── Dismiss coordination ──────────────────────────────────────────────────────
(defn- on-toast-dismiss [^js el ^js e]
  (let [^js detail (.-detail e)
        reason     (gobj/get detail "reason")]
    (when (not= reason model/child-dismiss-reason-toaster)
      ;; Take ownership — stop x-toast from self-removing
      (.preventDefault e)
      ;; Forward as x-toaster-dismiss (cancelable by consumers)
      (let [not-prevented (dispatch-toaster-dismiss! el detail)]
        (when not-prevented
          ;; Tell the toast to actually dismiss now — it will animate exit and remove itself
          (.dismiss (.-target e) model/child-dismiss-reason-toaster)))))
  nil)

;; ── max-toasts enforcement ────────────────────────────────────────────────────
(defn- evict-oldest! [^js el max-toasts]
  (let [^js children (.querySelectorAll el model/child-tag)
        count        (.-length children)]
    (when (>= count max-toasts)
      (let [oldest-count (- count max-toasts -1)]
        (loop [i 0]
          (when (< i oldest-count)
            (let [^js toast (aget children i)]
              (.dismiss toast model/child-dismiss-reason-toaster))
            (recur (inc i))))))
    nil))

;; ── toast() method implementation ─────────────────────────────────────────────
(defn- do-toast! [^js el opts]
  (let [m          (or (gobj/get el k-model) (read-model el))
        max-toasts (:max-toasts m)]
    (evict-oldest! el max-toasts)
    (let [^js toast (.createElement js/document model/child-tag)]
      ;; Map options to x-toast attributes
      (let [t (gobj/get opts "type")]
        (when t (.setAttribute toast "type" t)))
      (let [h (gobj/get opts "heading")]
        (when h (.setAttribute toast "heading" h)))
      (let [msg (gobj/get opts "message")]
        (when msg (.setAttribute toast "message" msg)))
      (let [icon (gobj/get opts "icon")]
        (when icon (.setAttribute toast "icon" icon)))
      (let [d (gobj/get opts "dismissible")]
        (when (false? d) (.setAttribute toast "dismissible" "false")))
      (let [tms (gobj/get opts "timeoutMs")]
        (when tms (.setAttribute toast "timeout-ms" (str tms))))
      (let [sp (gobj/get opts "showProgress")]
        (when sp (.setAttribute toast "show-progress" "")))
      (.appendChild el toast)
      toast)))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [dismiss-h (fn [^js e] (on-toast-dismiss el e))]
    (.addEventListener el model/child-event-dismiss dismiss-h)
    (gobj/set el k-handlers #js {"dismiss" dismiss-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (let [dismiss-h (gobj/get hs "dismiss")]
        (when dismiss-h
          (.removeEventListener el model/child-event-dismiss dismiss-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js klass]
  (let [^js proto (.-prototype klass)]

    (.defineProperty js/Object proto model/attr-position
                     #js {:get (fn []
                                 (this-as ^js this
                                          (model/parse-position
                                           (.getAttribute this model/attr-position))))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if v
                                            (.setAttribute this model/attr-position (str v))
                                            (.removeAttribute this model/attr-position))))
                          :enumerable true :configurable true})

    (.defineProperty js/Object proto model/attr-label
                     #js {:get (fn []
                                 (this-as ^js this
                                          (or (.getAttribute this model/attr-label) "Notifications")))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if (and v (not= v ""))
                                            (.setAttribute this model/attr-label (str v))
                                            (.removeAttribute this model/attr-label))))
                          :enumerable true :configurable true})

    ;; camelCase JS property for max-toasts
    (.defineProperty js/Object proto "maxToasts"
                     #js {:get (fn []
                                 (this-as ^js this
                                          (or (model/parse-max-toasts
                                               (.getAttribute this model/attr-max-toasts))
                                              5)))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if (nil? v)
                                            (.removeAttribute this model/attr-max-toasts)
                                            (.setAttribute this model/attr-max-toasts
                                                           (str (js/Math.floor v))))))
                          :enumerable true :configurable true})

    ;; toast(options) method
    (.defineProperty js/Object proto "toast"
                     #js {:value (fn [opts]
                                   (this-as ^js this
                                            (do-toast! this (or opts #js {}))))
                          :writable true :configurable true})))

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

    (install-property-accessors! klass)
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
