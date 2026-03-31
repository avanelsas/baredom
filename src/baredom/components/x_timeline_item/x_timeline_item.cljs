(ns baredom.components.x-timeline-item.x-timeline-item
  (:require
   [goog.object :as gobj]
   [baredom.components.x-timeline-item.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ─────────────────────────────────
(def ^:private k-refs       "__xTimelineItemRefs")
(def ^:private k-model      "__xTimelineItemModel")
(def ^:private k-handlers   "__xTimelineItemHandlers")
(def ^:private k-entered    "__xTimelineItemEntered")
;; k-parent-pos tracks data-position only when set by an external caller (parent
;; coordinator). k-self-pos is a reentrancy flag set while apply-model! writes
;; data-position so the resulting attributeChangedCallback is ignored.
(def ^:private k-parent-pos "__xTimelineItemParentPos")
(def ^:private k-self-pos   "__xTimelineItemSelfPos")

;; ── Styles ────────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-timeline-item-marker-size:2rem;"
   "--x-timeline-item-marker-bg:rgba(0,0,0,0.06);"
   "--x-timeline-item-marker-color:rgba(0,0,0,0.45);"
   "--x-timeline-item-connector-color:rgba(0,0,0,0.12);"
   "--x-timeline-item-connector-width:2px;"
   "--x-timeline-item-gap:0.75rem;"
   "--x-timeline-item-label-width:6rem;"
   "--x-timeline-item-label-color:rgba(0,0,0,0.5);"
   "--x-timeline-item-label-font-size:0.8125rem;"
   "--x-timeline-item-title-font-size:0.9375rem;"
   "--x-timeline-item-stripe-bg:rgba(0,0,0,0.025);"
   "--x-timeline-item-motion:150ms;"
   "--x-timeline-item-motion-ease:cubic-bezier(0.2,0,0,1);"
   "--x-timeline-item-enter-duration:160ms;"
   "--x-timeline-item-disabled-opacity:0.45;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-timeline-item-marker-bg:rgba(255,255,255,0.10);"
   "--x-timeline-item-marker-color:rgba(255,255,255,0.55);"
   "--x-timeline-item-connector-color:rgba(255,255,255,0.15);"
   "--x-timeline-item-label-color:rgba(255,255,255,0.5);"
   "--x-timeline-item-stripe-bg:rgba(255,255,255,0.03);}}"

   ":host([data-status='active']){"
   "--x-timeline-item-marker-bg:rgba(0,102,204,1);"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:rgba(0,102,204,0.4);}"

   ":host([data-status='complete']){"
   "--x-timeline-item-marker-bg:rgba(16,140,72,1);"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:rgba(16,140,72,0.5);}"

   ":host([data-status='error']){"
   "--x-timeline-item-marker-bg:rgba(190,20,40,1);"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:rgba(190,20,40,0.4);}"

   ":host([data-status='warning']){"
   "--x-timeline-item-marker-bg:rgba(204,120,0,1);"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:rgba(204,120,0,0.4);}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-status='active']){"
   "--x-timeline-item-marker-bg:rgba(80,160,255,1);"
   "--x-timeline-item-connector-color:rgba(80,160,255,0.4);}"
   ":host([data-status='complete']){"
   "--x-timeline-item-marker-bg:rgba(60,210,120,1);"
   "--x-timeline-item-connector-color:rgba(60,210,120,0.5);}"
   ":host([data-status='error']){"
   "--x-timeline-item-marker-bg:rgba(255,90,110,1);"
   "--x-timeline-item-connector-color:rgba(255,90,110,0.4);}"
   ":host([data-status='warning']){"
   "--x-timeline-item-marker-bg:rgba(255,190,90,1);"
   "--x-timeline-item-connector-color:rgba(255,190,90,0.4);}}"

   ":host([data-striped]){"
   "background:var(--x-timeline-item-stripe-bg);"
   "border-radius:4px;}"

   ":host([disabled]){"
   "opacity:var(--x-timeline-item-disabled-opacity);"
   "pointer-events:none;}"

   ":host(:not([disabled])){cursor:pointer;}"

   ":host(:not([disabled])):focus{outline:none;}"
   ":host(:not([disabled])):focus-visible{"
   "outline:2px solid rgba(0,102,204,0.55);"
   "outline-offset:2px;}"

   "@media (prefers-color-scheme:dark){"
   ":host(:not([disabled])):focus-visible{"
   "outline-color:rgba(80,160,255,0.7);}}"

   ".timeline-item{"
   "display:grid;"
   "grid-template-columns:var(--x-timeline-item-label-width) var(--x-timeline-item-marker-size) 1fr;"
   "grid-template-areas:'label track content';"
   "align-items:start;"
   "gap:0 var(--x-timeline-item-gap);}"

   ":host([data-position='end']) .timeline-item{"
   "grid-template-columns:1fr var(--x-timeline-item-marker-size) var(--x-timeline-item-label-width);"
   "grid-template-areas:'content track label';}"

   ".label-col{"
   "grid-area:label;"
   "display:flex;"
   "align-items:center;"
   "justify-content:flex-end;"
   "min-height:var(--x-timeline-item-marker-size);"
   "font-size:var(--x-timeline-item-label-font-size);"
   "color:var(--x-timeline-item-label-color);"
   "overflow:hidden;"
   "min-width:0;}"

   ":host([data-position='end']) .label-col{"
   "justify-content:flex-start;}"

   ".track-col{"
   "grid-area:track;"
   "display:flex;"
   "flex-direction:column;"
   "align-items:center;}"

   ".marker{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-timeline-item-marker-size);"
   "height:var(--x-timeline-item-marker-size);"
   "border-radius:999px;"
   "background:var(--x-timeline-item-marker-bg);"
   "color:var(--x-timeline-item-marker-color);"
   "font-size:0.875em;"
   "flex-shrink:0;"
   "transition:"
   "background var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease),"
   "color var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease);}"

   ".connector{"
   "flex:1;"
   "width:var(--x-timeline-item-connector-width);"
   "background:var(--x-timeline-item-connector-color);"
   "min-height:1rem;"
   "transition:background var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease);}"

   ":host([data-connector='dashed']) .connector{"
   "background:repeating-linear-gradient("
   "to bottom,"
   "var(--x-timeline-item-connector-color) 0,"
   "var(--x-timeline-item-connector-color) 4px,"
   "transparent 4px,"
   "transparent 8px);}"

   ":host([data-connector='none']) .connector,"
   ":host([data-last]) .connector{"
   "display:none;}"

   ".content-col{"
   "grid-area:content;"
   "display:flex;"
   "flex-direction:column;"
   "min-width:0;"
   "padding-bottom:var(--x-timeline-item-gap);}"

   ".title{"
   "font-weight:600;"
   "font-size:var(--x-timeline-item-title-font-size);"
   "line-height:1.25;"
   "min-height:var(--x-timeline-item-marker-size);"
   "display:flex;"
   "align-items:center;"
   "overflow-wrap:anywhere;"
   "min-width:0;}"

   ".title[hidden]{display:none;}"
   ".body{margin-top:0.25rem;}"
   ".actions{margin-top:0.5rem;}"

   ":host([data-entering]){"
   "animation:x-timeline-item-enter var(--x-timeline-item-enter-duration)"
   " var(--x-timeline-item-motion-ease) both;}"

   "@keyframes x-timeline-item-enter{"
   "from{opacity:0;transform:translateX(-4px);}"
   "to{opacity:1;transform:translateX(0);}}"

   ":host([data-position='end'][data-entering]){"
   "animation-name:x-timeline-item-enter-end;}"

   "@keyframes x-timeline-item-enter-end{"
   "from{opacity:0;transform:translateX(4px);}"
   "to{opacity:1;transform:translateX(0);}}"

   "@media (prefers-reduced-motion:reduce){"
   ":host([data-entering]){animation:none !important;}"
   ".marker,.connector{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style        (.createElement js/document "style")
        item-div     (.createElement js/document "div")
        label-col    (.createElement js/document "div")
        label-slot   (.createElement js/document "slot")
        label-text   (.createElement js/document "span")
        track-col    (.createElement js/document "div")
        marker       (.createElement js/document "div")
        icon-slot    (.createElement js/document "slot")
        default-icon (.createElement js/document "span")
        connector    (.createElement js/document "div")
        content-col  (.createElement js/document "div")
        title-el     (.createElement js/document "div")
        body-el      (.createElement js/document "div")
        default-slot (.createElement js/document "slot")
        actions-el   (.createElement js/document "div")
        actions-slot (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute item-div "class" "timeline-item")

    (.setAttribute label-col "class" "label-col")
    (.setAttribute label-slot "name" "label")
    (.setAttribute label-text "class" "label-text")
    (.appendChild label-slot label-text)
    (.appendChild label-col label-slot)

    (.setAttribute track-col "class" "track-col")
    (.setAttribute marker "class" "marker")
    (.setAttribute icon-slot "name" "icon")
    (.setAttribute default-icon "class" "default-icon")
    (.setAttribute default-icon "aria-hidden" "true")
    (.appendChild icon-slot default-icon)
    (.appendChild marker icon-slot)
    (.setAttribute connector "class" "connector")
    (.appendChild track-col marker)
    (.appendChild track-col connector)

    (.setAttribute content-col "class" "content-col")
    (.setAttribute title-el "class" "title")
    (.setAttribute body-el "class" "body")
    (.appendChild body-el default-slot)
    (.setAttribute actions-el "class" "actions")
    (.setAttribute actions-slot "name" "actions")
    (.appendChild actions-el actions-slot)
    (.appendChild content-col title-el)
    (.appendChild content-col body-el)
    (.appendChild content-col actions-el)

    (.appendChild item-div label-col)
    (.appendChild item-div track-col)
    (.appendChild item-div content-col)

    (.appendChild root style)
    (.appendChild root item-div)

    (gobj/set el k-refs
              {:root         root
               :item-div     item-div
               :label-col    label-col
               :label-slot   label-slot
               :label-text   label-text
               :track-col    track-col
               :marker       marker
               :icon-slot    icon-slot
               :default-icon default-icon
               :connector    connector
               :content-col  content-col
               :title-el     title-el
               :body-el      body-el
               :default-slot default-slot
               :actions-el   actions-el
               :actions-slot actions-slot}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:label-raw         (.getAttribute el model/attr-label)
    :title-raw         (.getAttribute el model/attr-title)
    :status-raw        (.getAttribute el model/attr-status)
    :icon-present?     (.hasAttribute el model/attr-icon)
    :icon-raw          (.getAttribute el model/attr-icon)
    :connector-raw     (.getAttribute el model/attr-connector)
    :position-raw      (.getAttribute el model/attr-position)
    :disabled?         (.hasAttribute el model/attr-disabled)
    :data-last?        (.hasAttribute el model/data-attr-last)
    :data-index-raw    (.getAttribute el model/data-attr-index)
    :data-position-raw (gobj/get el k-parent-pos)
    :data-striped?     (.hasAttribute el model/data-attr-striped)}))

;; ── DOM patching ──────────────────────────────────────────────────────────────
(defn- apply-model! [^js el
                     {:keys [label title status effective-position
                              disabled? marker-icon marker-aria] :as m}]
  (let [{:keys [label-text marker title-el default-icon]}
        (ensure-refs! el)
        ^js label-text   label-text
        ^js marker       marker
        ^js title-el     title-el
        ^js default-icon default-icon]

    ;; Host data attributes for CSS targeting
    (.setAttribute el "data-status" (model/status->attr status))
    (gobj/set el k-self-pos true)
    (.setAttribute el "data-position" (name effective-position))
    (gobj/set el k-self-pos nil)
    (if (= (:connector m) :dashed)
      (.setAttribute el "data-connector" "dashed")
      (if (= (:connector m) :none)
        (.setAttribute el "data-connector" "none")
        (.removeAttribute el "data-connector")))

    ;; ARIA and interactivity
    (.setAttribute el "role" "listitem")

    (if disabled?
      (do (.setAttribute el "aria-disabled" "true")
          (set! (.-tabIndex el) -1))
      (do (.removeAttribute el "aria-disabled")
          (set! (.-tabIndex el) 0)))

    ;; Host aria-label: prefer title, then label
    (let [host-label (cond
                       (not= title "") title
                       (not= label "") label
                       :else           nil)]
      (if host-label
        (.setAttribute el "aria-label" host-label)
        (.removeAttribute el "aria-label")))

    ;; Marker aria-label
    (.setAttribute marker "aria-label" marker-aria)

    ;; Label text fallback
    (set! (.-textContent label-text) label)

    ;; Default icon
    (if (some? marker-icon)
      (set! (.-textContent default-icon) marker-icon)
      (set! (.-textContent default-icon) ""))

    ;; Title element
    (if (not= title "")
      (do (.removeAttribute title-el "hidden")
          (set! (.-textContent title-el) title))
      (do (.setAttribute title-el "hidden" "")
          (set! (.-textContent title-el) "")))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Enter animation ───────────────────────────────────────────────────────────
(defn- start-enter! [^js el]
  (when-not (gobj/get el k-entered)
    (gobj/set el k-entered true)
    (.setAttribute el "data-entering" "")
    (letfn [(on-end [^js e]
              (when (= (.-target e) el)
                (.removeEventListener el "animationend" on-end)
                (.removeAttribute el "data-entering")))]
      (.addEventListener el "animationend" on-end)))
  nil)

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch-click! [^js el]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (.dispatchEvent el
                    (js/CustomEvent.
                     model/event-click
                     #js {:detail     (clj->js (model/click-detail m))
                          :bubbles    true
                          :composed   true
                          :cancelable true})))
  nil)

(defn- dispatch-connected! [^js el m]
  (.dispatchEvent el
                  (js/CustomEvent.
                   model/event-connected
                   #js {:detail     (clj->js (model/connected-detail m))
                        :bubbles    true
                        :composed   true
                        :cancelable false}))
  nil)

(defn- dispatch-disconnected! [^js _el m]
  (.dispatchEvent js/document
                  (js/CustomEvent.
                   model/event-disconnected
                   #js {:detail     (clj->js (model/disconnected-detail m))
                        :bubbles    false
                        :composed   false
                        :cancelable false}))
  nil)

;; ── Event handlers ────────────────────────────────────────────────────────────
(defn- on-click [^js el ^js _e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (model/interactive-eligible? m)
      (dispatch-click! el)))
  nil)

(defn- on-keydown [^js el ^js e]
  (let [key (.-key e)]
    (when (or (= key "Enter") (= key " "))
      (let [m (or (gobj/get el k-model) (read-model el))]
        (when (model/interactive-eligible? m)
          (.preventDefault e)
          (dispatch-click! el)))))
  nil)

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [click-h   (fn [e] (on-click el e))
        keydown-h (fn [e] (on-keydown el e))]
    (.addEventListener el "click" click-h)
    (.addEventListener el "keydown" keydown-h)
    (gobj/set el k-handlers #js {:click click-h :keydown keydown-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (let [click-h   (gobj/get hs "click")
            keydown-h (gobj/get hs "keydown")]
        (when click-h   (.removeEventListener el "click" click-h))
        (when keydown-h (.removeEventListener el "keydown" keydown-h)))))
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

  ;; Override HTMLElement.title (tooltip) — intentional, this component
  ;; uses "title" for its heading text
  (.defineProperty js/Object proto model/attr-title
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-title) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-title (str v))
                                          (.removeAttribute this model/attr-title))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-status
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-status) "pending")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-status (str v))
                                          (.removeAttribute this model/attr-status))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-icon
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this model/attr-icon)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-icon (str v))
                                          (.removeAttribute this model/attr-icon))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-connector
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-connector) "solid")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-connector (str v))
                                          (.removeAttribute this model/attr-connector))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-position
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-position) "start")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-position (str v))
                                          (.removeAttribute this model/attr-position))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     ;; Initialise k-parent-pos from any data-position already
                     ;; present on the element (e.g., set in HTML before connect)
                     (let [dp (.getAttribute this model/data-attr-position)]
                       (when dp (gobj/set this k-parent-pos dp)))
                     (update-from-attrs! this)
                     (start-enter! this)
                     (let [m (or (gobj/get this k-model) (read-model this))]
                       (dispatch-connected! this m))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (let [m (or (gobj/get this k-model) (read-model this))]
                       (remove-listeners! this)
                       (gobj/set this k-entered nil)
                       (dispatch-disconnected! this m))
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [attr-name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (if (and (= attr-name model/data-attr-position)
                                (gobj/get this k-self-pos))
                         nil ;; self-write: ignore to break the feedback loop
                         (do
                           (when (= attr-name model/data-attr-position)
                             (gobj/set this k-parent-pos new-val))
                           (update-from-attrs! this))))
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
