(ns baredom.components.x-timeline-item.x-timeline-item
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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
   "--x-timeline-item-marker-bg:var(--x-color-border,rgba(0,0,0,0.06));"
   "--x-timeline-item-marker-color:var(--x-color-text-muted,rgba(0,0,0,0.45));"
   "--x-timeline-item-connector-color:var(--x-color-border,rgba(0,0,0,0.12));"
   "--x-timeline-item-connector-width:2px;"
   "--x-timeline-item-gap:0.75rem;"
   "--x-timeline-item-label-width:6rem;"
   "--x-timeline-item-label-color:var(--x-color-text-muted,rgba(0,0,0,0.5));"
   "--x-timeline-item-label-font-size:var(--x-font-size-sm,0.8125rem);"
   "--x-timeline-item-title-font-size:0.9375rem;"
   "--x-timeline-item-stripe-bg:var(--x-color-surface,rgba(0,0,0,0.025));"
   "--x-timeline-item-motion:var(--x-transition-duration,150ms);"
   "--x-timeline-item-motion-ease:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "--x-timeline-item-enter-duration:160ms;"
   "--x-timeline-item-disabled-opacity:0.45;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-timeline-item-marker-bg:var(--x-color-border,rgba(255,255,255,0.10));"
   "--x-timeline-item-marker-color:var(--x-color-text-muted,rgba(255,255,255,0.55));"
   "--x-timeline-item-connector-color:var(--x-color-border,rgba(255,255,255,0.15));"
   "--x-timeline-item-label-color:var(--x-color-text-muted,rgba(255,255,255,0.5));"
   "--x-timeline-item-stripe-bg:var(--x-color-surface,rgba(255,255,255,0.03));}}"

   ":host([data-status='active']){"
   "--x-timeline-item-marker-bg:var(--x-color-primary,rgba(0,102,204,1));"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:var(--x-color-primary,rgba(0,102,204,0.4));}"

   ":host([data-status='complete']){"
   "--x-timeline-item-marker-bg:var(--x-color-success,rgba(16,140,72,1));"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:var(--x-color-success,rgba(16,140,72,0.5));}"

   ":host([data-status='error']){"
   "--x-timeline-item-marker-bg:var(--x-color-danger,rgba(190,20,40,1));"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:var(--x-color-danger,rgba(190,20,40,0.4));}"

   ":host([data-status='warning']){"
   "--x-timeline-item-marker-bg:var(--x-color-warning,rgba(204,120,0,1));"
   "--x-timeline-item-marker-color:#fff;"
   "--x-timeline-item-connector-color:var(--x-color-warning,rgba(204,120,0,0.4));}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-status='active']){"
   "--x-timeline-item-marker-bg:var(--x-color-primary,rgba(80,160,255,1));"
   "--x-timeline-item-connector-color:var(--x-color-primary,rgba(80,160,255,0.4));}"
   ":host([data-status='complete']){"
   "--x-timeline-item-marker-bg:var(--x-color-success,rgba(60,210,120,1));"
   "--x-timeline-item-connector-color:var(--x-color-success,rgba(60,210,120,0.5));}"
   ":host([data-status='error']){"
   "--x-timeline-item-marker-bg:var(--x-color-danger,rgba(255,90,110,1));"
   "--x-timeline-item-connector-color:var(--x-color-danger,rgba(255,90,110,0.4));}"
   ":host([data-status='warning']){"
   "--x-timeline-item-marker-bg:var(--x-color-warning,rgba(255,190,90,1));"
   "--x-timeline-item-connector-color:var(--x-color-warning,rgba(255,190,90,0.4));}}"

   ":host([data-striped]){"
   "background:var(--x-timeline-item-stripe-bg);"
   "border-radius:var(--x-radius-sm,4px);}"

   ":host([disabled]){"
   "opacity:var(--x-timeline-item-disabled-opacity);"
   "pointer-events:none;}"

   ":host(:not([disabled])){cursor:pointer;}"

   ":host(:not([disabled])):focus{outline:none;}"
   ":host(:not([disabled])):focus-visible{"
   "outline:2px solid var(--x-color-focus-ring,rgba(0,102,204,0.55));"
   "outline-offset:2px;}"

   "@media (prefers-color-scheme:dark){"
   ":host(:not([disabled])):focus-visible{"
   "outline-color:var(--x-color-focus-ring,rgba(80,160,255,0.7));}}"

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

    (du/set-attr! item-div "class" "timeline-item")

    (du/set-attr! label-col "class" "label-col")
    (du/set-attr! label-slot "name" "label")
    (du/set-attr! label-text "class" "label-text")
    (.appendChild label-slot label-text)
    (.appendChild label-col label-slot)

    (du/set-attr! track-col "class" "track-col")
    (du/set-attr! marker "class" "marker")
    (du/set-attr! icon-slot "name" "icon")
    (du/set-attr! default-icon "class" "default-icon")
    (du/set-attr! default-icon "aria-hidden" "true")
    (.appendChild icon-slot default-icon)
    (.appendChild marker icon-slot)
    (du/set-attr! connector "class" "connector")
    (.appendChild track-col marker)
    (.appendChild track-col connector)

    (du/set-attr! content-col "class" "content-col")
    (du/set-attr! title-el "class" "title")
    (du/set-attr! body-el "class" "body")
    (.appendChild body-el default-slot)
    (du/set-attr! actions-el "class" "actions")
    (du/set-attr! actions-slot "name" "actions")
    (.appendChild actions-el actions-slot)
    (.appendChild content-col title-el)
    (.appendChild content-col body-el)
    (.appendChild content-col actions-el)

    (.appendChild item-div label-col)
    (.appendChild item-div track-col)
    (.appendChild item-div content-col)

    (.appendChild root style)
    (.appendChild root item-div)

    (du/setv! el k-refs
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
               :actions-slot actions-slot})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:label-raw         (du/get-attr el model/attr-label)
    :title-raw         (du/get-attr el model/attr-title)
    :status-raw        (du/get-attr el model/attr-status)
    :icon-present?     (du/has-attr? el model/attr-icon)
    :icon-raw          (du/get-attr el model/attr-icon)
    :connector-raw     (du/get-attr el model/attr-connector)
    :position-raw      (du/get-attr el model/attr-position)
    :disabled?         (du/has-attr? el model/attr-disabled)
    :data-last?        (du/has-attr? el model/data-attr-last)
    :data-index-raw    (du/get-attr el model/data-attr-index)
    :data-position-raw (du/getv el k-parent-pos)
    :data-striped?     (du/has-attr? el model/data-attr-striped)}))

;; ── DOM patching ──────────────────────────────────────────────────────────────
(defn- apply-host-data-attrs! [^js el {:keys [status effective-position connector]}]
  (du/set-attr! el "data-status" (model/status->attr status))
  ;; Setting data-position on the host is a *self-write*: it fires
  ;; attributeChangedCallback, which is observed. The k-self-pos flag
  ;; suppresses the resulting feedback loop — see attribute-changed!.
  (du/setv! el k-self-pos true)
  (du/set-attr! el "data-position" (name effective-position))
  (du/setv! el k-self-pos nil)
  (case connector
    :dashed (du/set-attr! el "data-connector" "dashed")
    :none   (du/set-attr! el "data-connector" "none")
    (du/remove-attr! el "data-connector")))

(defn- apply-host-a11y! [^js el {:keys [label title disabled?]}]
  (du/set-attr! el "role" "listitem")
  (if disabled?
    (do (du/set-attr! el "aria-disabled" "true")
        (set! (.-tabIndex el) -1))
    (do (du/remove-attr! el "aria-disabled")
        (set! (.-tabIndex el) 0)))
  (let [host-label (cond
                     (not= title "") title
                     (not= label "") label
                     :else           nil)]
    (if host-label
      (du/set-attr! el "aria-label" host-label)
      (du/remove-attr! el "aria-label"))))

(defn- apply-marker! [^js marker ^js default-icon {:keys [marker-icon marker-aria]}]
  (du/set-attr! marker "aria-label" marker-aria)
  (set! (.-textContent default-icon)
        (if (some? marker-icon) marker-icon "")))

(defn- apply-text! [^js label-text ^js title-el {:keys [label title]}]
  (set! (.-textContent label-text) label)
  (if (not= title "")
    (do (du/remove-attr! title-el "hidden")
        (set! (.-textContent title-el) title))
    (do (du/set-attr! title-el "hidden" "")
        (set! (.-textContent title-el) ""))))

(defn- apply-model! [^js el m]
  (let [{:keys [label-text marker title-el default-icon]} (ensure-refs! el)]
    (apply-host-data-attrs! el m)
    (apply-host-a11y!       el m)
    (apply-marker!          marker default-icon m)
    (apply-text!            label-text title-el m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Enter animation ───────────────────────────────────────────────────────────
(defn- start-enter! [^js el]
  (when-not (du/getv el k-entered)
    (du/setv! el k-entered true)
    (du/set-attr! el "data-entering" "")
    (letfn [(on-end [^js e]
              (when (= (.-target e) el)
                (.removeEventListener el "animationend" on-end)
                (du/remove-attr! el "data-entering")))]
      (.addEventListener el "animationend" on-end))))

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch-click! [^js el]
  (let [m (or (du/getv el k-model) (read-model el))]
    (du/dispatch-cancelable! el model/event-click (clj->js (model/click-detail m)))))

(defn- dispatch-connected! [^js el m]
  (du/dispatch! el model/event-connected (clj->js (model/connected-detail m))))

(defn- dispatch-disconnected! [^js _el m]
  (du/dispatch-document! model/event-disconnected
                         (clj->js (model/disconnected-detail m))))

;; ── Event handlers ────────────────────────────────────────────────────────────
(defn- on-click [^js el ^js _e]
  (let [m (or (du/getv el k-model) (read-model el))]
    (when (model/interactive-eligible? m)
      (dispatch-click! el))))

(defn- on-keydown [^js el ^js e]
  (let [key (.-key e)]
    (when (or (= key "Enter") (= key " "))
      (let [m (or (du/getv el k-model) (read-model el))]
        (when (model/interactive-eligible? m)
          (.preventDefault e)
          (dispatch-click! el))))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [click-h   (fn [e] (on-click el e))
        keydown-h (fn [e] (on-keydown el e))]
    (.addEventListener el "click" click-h)
    (.addEventListener el "keydown" keydown-h)
    (du/setv! el k-handlers #js {:click click-h :keydown keydown-h})))

(defn- remove-listeners! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when hs
      (let [click-h   (gobj/get hs "click")
            keydown-h (gobj/get hs "keydown")]
        (when click-h   (.removeEventListener el "click" click-h))
        (when keydown-h (.removeEventListener el "keydown" keydown-h)))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-label
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (du/set-attr! this model/attr-label (str v))
                                          (du/remove-attr! this model/attr-label))))
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
                                          (du/set-attr! this model/attr-title (str v))
                                          (du/remove-attr! this model/attr-title))))
                        :enumerable true :configurable true})

  (du/define-string-prop! proto model/attr-status    model/attr-status    "pending")
  (du/define-string-prop! proto model/attr-icon      model/attr-icon)
  (du/define-string-prop! proto model/attr-connector model/attr-connector "solid")
  (du/define-string-prop! proto model/attr-position  model/attr-position  "start")
  (du/define-bool-prop!   proto model/attr-disabled  model/attr-disabled))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  ;; Initialise k-parent-pos from any data-position already
  ;; present on the element (e.g., set in HTML before connect)
  (let [dp (du/get-attr el model/data-attr-position)]
    (when dp (du/setv! el k-parent-pos dp)))
  (update-from-attrs! el)
  (start-enter! el)
  (let [m (or (du/getv el k-model) (read-model el))]
    (dispatch-connected! el m)))

(defn- disconnected! [^js el]
  (let [m (or (du/getv el k-model) (read-model el))]
    (remove-listeners! el)
    (du/setv! el k-entered nil)
    (dispatch-disconnected! el m)))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (if (and (= attr-name model/data-attr-position)
             (du/getv el k-self-pos))
      nil ;; self-write: ignore to break the feedback loop
      (do
        (when (= attr-name model/data-attr-position)
          (du/setv! el k-parent-pos new-val))
        (update-from-attrs! el)))))

;; ── Public API ────────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
