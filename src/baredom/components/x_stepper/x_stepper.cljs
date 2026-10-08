(ns baredom.components.x-stepper.x-stepper
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baremirror.core :as mirror]
            [goog.object :as gobj]
            [baredom.components.x-stepper.model :as model]))

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
   "--x-stepper-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-stepper-label-font-weight:var(--x-font-weight-medium,500);"
   "--x-stepper-desc-font-size:var(--x-font-size-xs,0.75rem);"
   "--x-stepper-radius:var(--x-radius-full,999px);"
   "--x-stepper-motion:var(--x-transition-duration,120ms);"
   "--x-stepper-press-scale:0.93;"
   "--x-stepper-focus-ring:var(--x-color-focus-ring,rgba(0,0,0,0.55));"
   "--x-stepper-disabled-opacity:0.5;"
   ;; Light-mode colours
   "--x-stepper-complete-bg:var(--x-color-success,rgba(16,140,72,1));"
   "--x-stepper-complete-color:#fff;"
   "--x-stepper-complete-connector:var(--x-color-success,rgba(16,140,72,1));"
   "--x-stepper-current-bg:var(--x-color-primary,rgba(0,102,204,1));"
   "--x-stepper-current-color:#fff;"
   "--x-stepper-upcoming-bg:color-mix(in srgb,var(--x-color-text,#000) 8%,transparent);"
   "--x-stepper-upcoming-color:var(--x-color-text-muted,rgba(0,0,0,0.45));"
   "--x-stepper-idle-connector:var(--x-color-border,rgba(0,0,0,0.12));"
   "--x-stepper-label-done-color:var(--x-color-text,rgba(0,0,0,0.85));"
   "--x-stepper-label-current-color:var(--x-color-text,rgba(0,0,0,0.85));"
   "--x-stepper-label-upcoming-color:var(--x-color-text-muted,rgba(0,0,0,0.4));"
   "--x-stepper-desc-color:var(--x-color-text-muted,rgba(0,0,0,0.4));}"
   du/control-font-rule
   du/hidden-rule

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-stepper-focus-ring:var(--x-color-focus-ring,rgba(255,255,255,0.7));"
   "--x-stepper-complete-bg:var(--x-color-success,rgba(60,210,120,1));"
   "--x-stepper-complete-color:#000;"
   "--x-stepper-complete-connector:var(--x-color-success,rgba(60,210,120,1));"
   "--x-stepper-current-bg:var(--x-color-primary,rgba(80,160,255,1));"
   "--x-stepper-current-color:#000;"
   "--x-stepper-upcoming-bg:color-mix(in srgb,var(--x-color-text,#fff) 10%,transparent);"
   "--x-stepper-upcoming-color:var(--x-color-text-muted,rgba(255,255,255,0.35));"
   "--x-stepper-idle-connector:var(--x-color-border,rgba(255,255,255,0.12));"
   "--x-stepper-label-done-color:var(--x-color-text,rgba(255,255,255,0.9));"
   "--x-stepper-label-current-color:var(--x-color-text,rgba(255,255,255,0.9));"
   "--x-stepper-label-upcoming-color:var(--x-color-text-muted,rgba(255,255,255,0.3));"
   "--x-stepper-desc-color:var(--x-color-text-muted,rgba(255,255,255,0.35));}}"

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
   "font-weight:var(--x-font-weight-semibold,600);"
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
   "--x-stepper-font-size:var(--x-font-size-xs,0.8125rem);"
   "--x-stepper-desc-font-size:0.6875rem;}"

   ":host([data-size=lg]){"
   "--x-stepper-indicator-size:2.5rem;"
   "--x-stepper-font-size:var(--x-font-size-base,1rem);"
   "--x-stepper-desc-font-size:var(--x-font-size-sm,0.875rem);}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=step-indicator]{transition:none;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")]
    (set! (.-textContent style) style-text)
    (du/set-attr! container "part" "container")
    (du/set-attr! container "role" "list")
    (.appendChild root style)
    (.appendChild root container)
    (du/setv! el k-refs {:root root :container container})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:steps-raw       (du/get-attr el model/attr-steps)
    :current-raw     (du/get-attr el model/attr-current)
    :orientation-raw (du/get-attr el model/attr-orientation)
    :size-raw        (du/get-attr el model/attr-size)
    :disabled?       (du/has-attr? el model/attr-disabled)}))

;; ── Step node construction ───────────────────────────────────────────────────
(defn- make-step-indicator!
  "The button of a step with its number badge, with no values."
  []
  (let [btn-el (.createElement js/document "button")
        num-el (.createElement js/document "span")]
    (du/set-attr! btn-el "part" "step-indicator")
    (du/set-attr! btn-el "type" "button")
    (du/set-attr! num-el "part" "step-number")
    (du/set-attr! num-el "aria-hidden" "true")
    (.appendChild btn-el num-el)
    btn-el))

(defn- make-step-track!
  "The track of a step: its indicator and its connector line."
  []
  (let [track-el (.createElement js/document "div")
        conn-el  (.createElement js/document "div")]
    (du/set-attr! track-el "part" "step-track")
    (du/set-attr! conn-el  "part" "step-connector")
    (du/set-attr! conn-el  "aria-hidden" "true")
    (.appendChild track-el (make-step-indicator!))
    (.appendChild track-el conn-el)
    track-el))

(defn- make-step-content!
  "The label and description column of a step, with no values."
  []
  (let [content-el (.createElement js/document "div")
        label-el   (.createElement js/document "span")
        desc-el    (.createElement js/document "span")]
    (du/set-attr! content-el "part" "step-content")
    (du/set-attr! label-el   "part" "step-label")
    (du/set-attr! desc-el    "part" "step-description")
    (.appendChild content-el label-el)
    (.appendChild content-el desc-el)
    content-el))

(defn- make-step-node!
  "The node of a step: its fixed structure. `apply-step!` gives it its values."
  [_key]
  (let [step-el (.createElement js/document "div")]
    (du/set-attr! step-el "part" "step")
    (du/set-attr! step-el "role" "listitem")
    (.appendChild step-el (make-step-track!))
    (.appendChild step-el (make-step-content!))
    step-el))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- step-part [^js step-el part]
  (.querySelector step-el (str "[part=" part "]")))

(defn- apply-step-indicator!
  [^js step-el {:keys [aria-label aria-current tabindex aria-disabled number]}]
  (let [btn-el (step-part step-el "step-indicator")]
    (du/set-attr-to! btn-el "aria-label" aria-label)
    (du/set-attr-to! btn-el "aria-current" aria-current)
    (du/set-attr-to! btn-el "tabindex" tabindex)
    (du/set-attr-to! btn-el "aria-disabled" aria-disabled)
    (du/set-text-to! (step-part step-el "step-number") number)))

(defn- apply-step-content! [^js step-el {:keys [label description described?]}]
  (let [^js desc-el (step-part step-el "step-description")]
    (du/set-text-to! (step-part step-el "step-label") label)
    (du/set-text-to! desc-el description)
    (set! (.. desc-el -style -display) (if described? "block" "none"))))

(defn- apply-step!
  "Writes what `step` shows onto its node in `nodes`."
  [nodes {step-key :key :keys [index state] :as step}]
  (let [^js step-el (nodes step-key)]
    (du/set-attr-to! step-el "data-index" index)
    (du/set-attr-to! step-el "data-state" state)
    (apply-step-indicator! step-el step)
    (apply-step-content! step-el step)))

(defn- render-steps!
  "Brings the steps in `container` to the model. A step that stays keeps its node."
  [^js container m]
  (let [steps (model/shown-steps m)
        nodes (mirror/sync! {:steps {:parent container}} {:steps (mapv :key steps)}
                            make-step-node!)]
    (run! (partial apply-step! nodes) steps)))

(defn- apply-model! [^js el {:keys [orientation size] :as m}]
  (let [{:keys [container]} (ensure-refs! el)]
    ;; data-* attributes drive CSS — not in observed-attributes, safe to set here
    (du/set-attr! el "data-orientation" (model/orientation->attr orientation))
    (du/set-attr! el "data-size" (model/size->attr size))
    (render-steps! container m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-container-click [^js el ^js e]
  (let [m (or (du/getv el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [^js target (.-target e)
            ^js btn    (.closest target "[part=step-indicator]")]
        (when btn
          (let [^js step (.closest btn "[part=step]")
                idx      (js/parseInt (.getAttribute step "data-index") 10)
                cur      (:current m)]
            (when (and (number? idx) (not (js/isNaN idx)) (not= idx cur))
              (let [detail (clj->js (model/change-detail cur idx))]
                (when (du/dispatch-cancelable! el model/event-change detail)
                  (du/set-attr! el model/attr-current (str idx)))))))))))

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [container]} (ensure-refs! el)
        ^js container container
        click-h (fn handle-container-click [e] (on-container-click el e))]
    (.addEventListener container "click" click-h)
    (du/setv! el k-handlers #js {:click click-h})))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (let [refs (du/getv el k-refs)]
      (when refs
        (let [^js container (:container refs)
              click-h       (gobj/get hs "click")]
          (when (and container click-h)
            (.removeEventListener container "click" click-h))))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ───────────────────────────────────────────────────────
;; orientation/size getters compose parse + canonical-form normalisation, so
;; the property always returns the canonical string regardless of how the
;; attribute was written. Each one needs its own 1-arg parse fn for
;; du/define-parsed-prop!.
(defn- parse-orientation-prop [s]
  (model/orientation->attr (model/parse-orientation s)))

(defn- parse-size-prop [s]
  (model/size->attr (model/parse-size s)))

(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-steps       model/attr-steps       "[]")
  (du/define-parsed-prop! proto model/attr-current     model/attr-current     model/parse-current)
  (du/define-parsed-prop! proto model/attr-orientation model/attr-orientation parse-orientation-prop)
  (du/define-parsed-prop! proto model/attr-size        model/attr-size        parse-size-prop)
  (du/define-bool-prop!   proto model/attr-disabled    model/attr-disabled))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
