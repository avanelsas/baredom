(ns baredom.components.x-timeline.x-timeline
  (:require
   [goog.object :as gobj]
   [baredom.components.x-timeline.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs     "__xTimelineRefs")
(def ^:private k-model    "__xTimelineModel")
(def ^:private k-handlers "__xTimelineHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:flex;"
   "flex-direction:column;"
   "color-scheme:light dark;"
   "--x-timeline-gap:0;"
   "--x-timeline-label-color:inherit;"
   "--x-timeline-label-font-size:0.875rem;"
   "--x-timeline-label-font-weight:600;"
   "--x-timeline-label-padding:0 0 0.5rem;}"

   "slot{display:contents;}"

   "[part=label]{"
   "color:var(--x-timeline-label-color);"
   "font-size:var(--x-timeline-label-font-size);"
   "font-weight:var(--x-timeline-label-font-weight);"
   "padding:var(--x-timeline-label-padding);"
   "box-sizing:border-box;}"

   "[part=label][hidden]{"
   "display:none !important;}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        label-div (.createElement js/document "div")
        slot-el   (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute label-div "part" "label")
    (.setAttribute label-div "hidden" "")

    (.appendChild root style)
    (.appendChild root label-div)
    (.appendChild root slot-el)

    (gobj/set el k-refs {:root      root
                         :label-div label-div
                         :slot-el   slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:label-raw    (.getAttribute el model/attr-label)
    :position-raw (.getAttribute el model/attr-position)
    :striped?     (.hasAttribute el model/attr-striped)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [label] :as m}]
  (let [{:keys [label-div]} (ensure-refs! el)
        ^js label-div label-div]

    ;; ARIA
    (.setAttribute el "role" "list")
    (if (not= label "")
      (.setAttribute el "aria-label" label)
      (.removeAttribute el "aria-label"))

    ;; Label caption
    (if (not= label "")
      (do (set! (.-textContent label-div) label)
          (.removeAttribute label-div "hidden"))
      (do (set! (.-textContent label-div) "")
          (.setAttribute label-div "hidden" "")))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Child update (core coordinator) ─────────────────────────────────────────
(defn- update-items! [^js el]
  (let [{:keys [position striped?]} (or (gobj/get el k-model) (read-model el))
        ^js items (.querySelectorAll el model/child-tag)
        len       (.-length items)]
    (loop [i 0]
      (when (< i len)
        (let [^js item (aget items i)
              last?    (= i (dec len))]
          (.setAttribute item "data-index" (str i))
          (if last?
            (.setAttribute item "data-last" "")
            (.removeAttribute item "data-last"))
          (.setAttribute item "data-position" (model/item-position position i))
          (if striped?
            (.setAttribute item "data-striped" "")
            (.removeAttribute item "data-striped")))
        (recur (inc i)))))
  nil)

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch-select! [^js el index status label]
  (let [detail (clj->js (model/select-detail index status label))]
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-select
                     #js {:detail     detail
                          :bubbles    true
                          :composed   true
                          :cancelable false})))
  nil)

;; ── Event handlers ────────────────────────────────────────────────────────────
(defn- on-item-connected [^js el ^js _e]
  (update-items! el)
  nil)

(defn- on-item-disconnected [^js el ^js _e]
  ;; Guard: document listener may fire after x-timeline itself has disconnected.
  (when (.-isConnected el)
    (update-items! el))
  nil)

(defn- on-item-click [^js el ^js e]
  (.stopPropagation e)
  (let [^js target   (.-target e)
        index-str    (.getAttribute target "data-index")
        index        (when (string? index-str)
                       (let [n (js/parseInt index-str 10)]
                         (when-not (js/isNaN n) n)))
        ^js detail   (.-detail e)
        status       (gobj/get detail "status")
        label        (gobj/get detail "label")]
    (dispatch-select! el index status label))
  nil)

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [conn-h  (fn [e] (on-item-connected el e))
        click-h (fn [e] (on-item-click el e))
        doc-h   (fn [e] (on-item-disconnected el e))]
    (.addEventListener el model/child-event-connected conn-h)
    (.addEventListener el model/child-event-click     click-h)
    (.addEventListener js/document model/child-event-disconnected doc-h)
    (gobj/set el k-handlers
              #js {"item-connected"        conn-h
                   "item-click"            click-h
                   "item-disconnected-doc" doc-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (let [conn-h  (gobj/get hs "item-connected")
            click-h (gobj/get hs "item-click")
            doc-h   (gobj/get hs "item-disconnected-doc")]
        (when conn-h  (.removeEventListener el model/child-event-connected conn-h))
        (when click-h (.removeEventListener el model/child-event-click click-h))
        (when doc-h   (.removeEventListener js/document model/child-event-disconnected doc-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-label
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-label (str v))
                                          (.removeAttribute this model/attr-label))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-position
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-position (.getAttribute this model/attr-position))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-position (str v))
                                          (.removeAttribute this model/attr-position))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-striped
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-striped)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-striped "")
                                          (.removeAttribute this model/attr-striped))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (update-from-attrs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (update-items! this)
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
                       (update-from-attrs! this)
                       (update-items! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
