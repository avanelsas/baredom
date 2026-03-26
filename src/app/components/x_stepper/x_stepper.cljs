(ns app.components.x-stepper.x-stepper
  (:require
   [goog.object :as gobj]
   [app.components.x-stepper.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ─────────────────────────────────
(def ^:private k-refs     "__xStepperRefs")
(def ^:private k-model    "__xStepperModel")
(def ^:private k-handlers "__xStepperHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-stepper-indicator-size:2rem;"
   "--x-stepper-connector-thickness:2px;"
   "--x-stepper-step-gap:0.75rem;"
   "--x-stepper-font-size:0.875rem;"
   "--x-stepper-label-font-weight:500;"
   "--x-stepper-desc-font-size:0.75rem;"
   "--x-stepper-radius:999px;"
   "--x-stepper-motion:120ms;"
   "--x-stepper-press-scale:0.93;"
   "--x-stepper-focus-ring:rgba(0,0,0,0.55);"
   "--x-stepper-disabled-opacity:0.5;"
   ;; Light-mode colours
   "--x-stepper-complete-bg:rgba(16,140,72,1);"
   "--x-stepper-complete-color:#fff;"
   "--x-stepper-complete-connector:rgba(16,140,72,1);"
   "--x-stepper-current-bg:rgba(0,102,204,1);"
   "--x-stepper-current-color:#fff;"
   "--x-stepper-upcoming-bg:rgba(0,0,0,0.08);"
   "--x-stepper-upcoming-color:rgba(0,0,0,0.45);"
   "--x-stepper-idle-connector:rgba(0,0,0,0.12);"
   "--x-stepper-label-done-color:rgba(0,0,0,0.85);"
   "--x-stepper-label-current-color:rgba(0,0,0,0.85);"
   "--x-stepper-label-upcoming-color:rgba(0,0,0,0.4);"
   "--x-stepper-desc-color:rgba(0,0,0,0.4);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-stepper-focus-ring:rgba(255,255,255,0.7);"
   "--x-stepper-complete-bg:rgba(60,210,120,1);"
   "--x-stepper-complete-color:#000;"
   "--x-stepper-complete-connector:rgba(60,210,120,1);"
   "--x-stepper-current-bg:rgba(80,160,255,1);"
   "--x-stepper-current-color:#000;"
   "--x-stepper-upcoming-bg:rgba(255,255,255,0.1);"
   "--x-stepper-upcoming-color:rgba(255,255,255,0.35);"
   "--x-stepper-idle-connector:rgba(255,255,255,0.12);"
   "--x-stepper-label-done-color:rgba(255,255,255,0.9);"
   "--x-stepper-label-current-color:rgba(255,255,255,0.9);"
   "--x-stepper-label-upcoming-color:rgba(255,255,255,0.3);"
   "--x-stepper-desc-color:rgba(255,255,255,0.35);}}"

   ;; Container — shared
   "[part=container]{"
   "display:flex;"
   "align-items:flex-start;"
   "margin:0;padding:0;list-style:none;}"

   ;; ─── Horizontal layout ────────────────────────────────────────────────────
   ":host([data-orientation=horizontal]) [part=container]{"
   "flex-direction:row;}"

   ":host([data-orientation=horizontal]) [part=step]{"
   "display:flex;"
   "flex-direction:column;"
   "align-items:center;"
   "flex:1;"
   "min-width:0;}"

   ":host([data-orientation=horizontal]) [part=step-track]{"
   "display:flex;"
   "align-items:center;"
   "width:100%;}"

   ":host([data-orientation=horizontal]) [part=step-connector]{"
   "flex:1;"
   "height:var(--x-stepper-connector-thickness);"
   "min-width:8px;}"

   ":host([data-orientation=horizontal]) [part=step-content]{"
   "display:flex;"
   "flex-direction:column;"
   "align-items:center;"
   "text-align:center;"
   "padding:var(--x-stepper-step-gap) 4px 0;"
   "width:100%;}"

   ;; ─── Vertical layout ──────────────────────────────────────────────────────
   ":host([data-orientation=vertical]) [part=container]{"
   "flex-direction:column;}"

   ":host([data-orientation=vertical]) [part=step]{"
   "display:flex;"
   "flex-direction:row;"
   "gap:var(--x-stepper-step-gap);}"

   ":host([data-orientation=vertical]) [part=step-track]{"
   "display:flex;"
   "flex-direction:column;"
   "align-items:center;"
   "flex-shrink:0;"
   "width:var(--x-stepper-indicator-size);}"

   ":host([data-orientation=vertical]) [part=step-connector]{"
   "flex:1;"
   "width:var(--x-stepper-connector-thickness);"
   "min-height:16px;"
   "margin:4px 0;}"

   ":host([data-orientation=vertical]) [part=step-content]{"
   "display:flex;"
   "flex-direction:column;"
   "flex:1;"
   "min-width:0;"
   "padding-bottom:var(--x-stepper-step-gap);}"

   ;; ─── Step indicator (button) ──────────────────────────────────────────────
   "[part=step-indicator]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-stepper-indicator-size);"
   "height:var(--x-stepper-indicator-size);"
   "border-radius:var(--x-stepper-radius);"
   "border:none;"
   "padding:0;margin:0;"
   "cursor:pointer;"
   "font-size:0.875em;"
   "font-weight:600;"
   "font-family:inherit;"
   "flex-shrink:0;"
   "position:relative;z-index:1;"
   "transition:"
   "background var(--x-stepper-motion) ease,"
   "color var(--x-stepper-motion) ease,"
   "transform 80ms ease;}"

   "[part=step-indicator]:focus{outline:none;}"
   "[part=step-indicator]:focus-visible{"
   "outline:2px solid var(--x-stepper-focus-ring);"
   "outline-offset:3px;}"

   ;; State colours — indicator
   "[data-state=complete] [part=step-indicator]{"
   "background:var(--x-stepper-complete-bg);"
   "color:var(--x-stepper-complete-color);}"

   "[data-state=current] [part=step-indicator]{"
   "background:var(--x-stepper-current-bg);"
   "color:var(--x-stepper-current-color);"
   "cursor:default;}"

   "[data-state=upcoming] [part=step-indicator]{"
   "background:var(--x-stepper-upcoming-bg);"
   "color:var(--x-stepper-upcoming-color);}"

   ;; Connector colours
   "[data-state=complete] [part=step-connector]{"
   "background:var(--x-stepper-complete-connector);}"

   "[data-state=current] [part=step-connector],"
   "[data-state=upcoming] [part=step-connector]{"
   "background:var(--x-stepper-idle-connector);}"

   ;; Last step has no connector
   "[part=step]:last-child [part=step-connector]{"
   "display:none;}"

   ;; Label colours
   "[part=step-label]{"
   "font-size:var(--x-stepper-font-size);"
   "line-height:1.3;"
   "font-family:inherit;}"

   "[data-state=complete] [part=step-label]{"
   "color:var(--x-stepper-label-done-color);}"

   "[data-state=current] [part=step-label]{"
   "color:var(--x-stepper-label-current-color);"
   "font-weight:var(--x-stepper-label-font-weight);}"

   "[data-state=upcoming] [part=step-label]{"
   "color:var(--x-stepper-label-upcoming-color);}"

   ;; Description
   "[part=step-description]{"
   "font-size:var(--x-stepper-desc-font-size);"
   "color:var(--x-stepper-desc-color);"
   "line-height:1.3;"
   "margin-top:0.2em;}"

   ;; Hover / active — only when not disabled and not current
   ":host(:not([disabled])) [data-state=complete] [part=step-indicator]:hover,"
   ":host(:not([disabled])) [data-state=upcoming] [part=step-indicator]:hover{"
   "filter:brightness(0.88);}"

   ":host(:not([disabled])) [data-state=complete] [part=step-indicator]:active,"
   ":host(:not([disabled])) [data-state=upcoming] [part=step-indicator]:active{"
   "transform:scale(var(--x-stepper-press-scale));}"

   ;; Disabled host
   ":host([disabled]){"
   "opacity:var(--x-stepper-disabled-opacity);}"

   ":host([disabled]) [part=step-indicator]{"
   "cursor:default;pointer-events:none;}"

   ;; Size variants
   ":host([data-size=sm]){"
   "--x-stepper-indicator-size:1.5rem;"
   "--x-stepper-font-size:0.8125rem;"
   "--x-stepper-desc-font-size:0.6875rem;}"

   ":host([data-size=lg]){"
   "--x-stepper-indicator-size:2.5rem;"
   "--x-stepper-font-size:1rem;"
   "--x-stepper-desc-font-size:0.875rem;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=step-indicator]{transition:none;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")]
    (set! (.-textContent style) style-text)
    (.setAttribute container "part" "container")
    (.setAttribute container "role" "list")
    (.appendChild root style)
    (.appendChild root container)
    (gobj/set el k-refs {:root root :container container}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:steps-raw       (.getAttribute el model/attr-steps)
    :current-raw     (.getAttribute el model/attr-current)
    :orientation-raw (.getAttribute el model/attr-orientation)
    :size-raw        (.getAttribute el model/attr-size)
    :disabled?       (.hasAttribute el model/attr-disabled)}))

;; ── Step node construction ───────────────────────────────────────────────────
(defn- step-aria-label [idx label state]
  (str "Step " (inc idx) ": " label
       " ("
       (case state
         :complete "completed"
         :current  "current"
         :upcoming "upcoming")
       ")"))

(defn- make-step-node! [idx {:keys [label description]} state disabled?]
  (let [step-el    (.createElement js/document "div")
        track-el   (.createElement js/document "div")
        btn-el     (.createElement js/document "button")
        num-el     (.createElement js/document "span")
        conn-el    (.createElement js/document "div")
        content-el (.createElement js/document "div")
        label-el   (.createElement js/document "span")
        desc-el    (.createElement js/document "span")
        state-str  (model/state->attr state)
        label-str  (or label "")]

    (.setAttribute step-el "part" "step")
    (.setAttribute step-el "role" "listitem")
    (.setAttribute step-el "data-index" (str idx))
    (.setAttribute step-el "data-state" state-str)

    (.setAttribute track-el "part" "step-track")

    (.setAttribute btn-el "part" "step-indicator")
    (.setAttribute btn-el "type" "button")
    (.setAttribute btn-el "aria-label" (step-aria-label idx label-str state))
    (if (= state :current)
      (.setAttribute btn-el "aria-current" "step")
      (.removeAttribute btn-el "aria-current"))
    (.setAttribute btn-el "tabindex" (if disabled? "-1" "0"))

    (.setAttribute num-el "part" "step-number")
    (.setAttribute num-el "aria-hidden" "true")
    (set! (.-textContent num-el) (if (= state :complete) "✓" (str (inc idx))))

    (.setAttribute conn-el "part" "step-connector")
    (.setAttribute conn-el "aria-hidden" "true")

    (.setAttribute content-el "part" "step-content")

    (.setAttribute label-el "part" "step-label")
    (set! (.-textContent label-el) label-str)

    (.setAttribute desc-el "part" "step-description")
    (if (and description (not= description ""))
      (do (set! (.-textContent desc-el) description)
          (set! (.. desc-el -style -display) "block"))
      (set! (.. desc-el -style -display) "none"))

    (.appendChild btn-el num-el)
    (.appendChild track-el btn-el)
    (.appendChild track-el conn-el)
    (.appendChild content-el label-el)
    (.appendChild content-el desc-el)
    (.appendChild step-el track-el)
    (.appendChild step-el content-el)

    step-el))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [steps current orientation size disabled?] :as m}]
  (let [{:keys [container]} (ensure-refs! el)
        ^js container container]

    ;; data-* attributes drive CSS — not in observed-attributes, safe to set here
    (.setAttribute el "data-orientation" (model/orientation->attr orientation))
    (.setAttribute el "data-size" (model/size->attr size))

    ;; Rebuild step nodes (clear + append)
    (set! (.-innerHTML container) "")
    (doseq [[idx step] (map-indexed vector steps)]
      (let [state (model/step-state idx current)]
        (.appendChild container (make-step-node! idx step state disabled?))))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-container-click [^js el ^js e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [^js target (.-target e)
            ^js btn    (.closest target "[part=step-indicator]")]
        (when btn
          (let [^js step (.closest btn "[part=step]")
                idx      (js/parseInt (.getAttribute step "data-index") 10)
                cur      (:current m)]
            (when (and (number? idx) (not (js/isNaN idx)) (not= idx cur))
              (let [detail (clj->js (model/change-detail cur idx))
                    ^js ev (js/CustomEvent.
                            model/event-change
                            #js {:detail    detail
                                 :bubbles   true
                                 :composed  true
                                 :cancelable true})
                    ok?    (.dispatchEvent el ev)]
                (when ok?
                  (.setAttribute el model/attr-current (str idx)))))))))
  nil))

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [container]} (ensure-refs! el)
        ^js container container
        click-h (fn [e] (on-container-click el e))]
    (.addEventListener container "click" click-h)
    (gobj/set el k-handlers #js {:click click-h}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (let [refs (gobj/get el k-refs)]
      (when refs
        (let [^js container (:container refs)
              click-h       (gobj/get hs "click")]
          (when (and container click-h)
            (.removeEventListener container "click" click-h))))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-steps
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-steps) "[]")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-steps (str v))
                                          (.removeAttribute this model/attr-steps))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-current
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-current
                                         (.getAttribute this model/attr-current))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-current)
                                          (.setAttribute this model/attr-current
                                                         (str (int v))))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-orientation
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/orientation->attr
                                         (model/parse-orientation
                                          (.getAttribute this model/attr-orientation)))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-orientation (str v))
                                          (.removeAttribute this model/attr-orientation))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-size
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/size->attr
                                         (model/parse-size
                                          (.getAttribute this model/attr-size)))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-size (str v))
                                          (.removeAttribute this model/attr-size))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-disabled)))
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

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
