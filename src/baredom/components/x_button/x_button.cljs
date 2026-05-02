(ns baredom.components.x-button.x-button
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-button.model :as model]))

(def state-key "__xButtonState")
(def hover-key "__xButtonHover")
(def focus-visible-key "__xButtonFocusVisible")
(def active-source-key "__xButtonActiveSource")
(def last-activation-source-key "__xButtonLastActivationSource")

(defn get-prop
  [obj k]
  (aget obj k))

(defn set-prop!
  [obj k v]
  (aset obj k v))


(defn get-el-state
  [^js el]
  (get-prop el state-key))

(defn set-el-state!
  [^js el state]
  (set-prop! el state-key state))

(defn get-hover
  [^js el]
  (= true (get-prop el hover-key)))

(defn set-hover!
  [^js el value]
  (set-prop! el hover-key (boolean value)))

(defn get-focus-visible
  [^js el]
  (= true (get-prop el focus-visible-key)))

(defn set-focus-visible!
  [^js el value]
  (set-prop! el focus-visible-key (boolean value)))

(defn get-active-source
  [^js el]
  (get-prop el active-source-key))

(defn set-active-source!
  [^js el value]
  (set-prop! el active-source-key value))

(defn get-last-activation-source
  [^js el]
  (get-prop el last-activation-source-key))

(defn set-last-activation-source!
  [^js el value]
  (set-prop! el last-activation-source-key value))

(defn find-owner-form
  [^js el]
  (or (when-let [form-id (.getAttribute el "form")]
        (.getElementById js/document form-id))
      (.closest el "form")))

(defn read-public-state
  [^js el]
  (model/public-state
   {:disabled (du/has-attr? el model/attr-disabled)
    :loading (du/has-attr? el model/attr-loading)
    :pressed (du/has-attr? el model/attr-pressed)
    :type (du/get-attr el model/attr-type)
    :variant (du/get-attr el model/attr-variant)
    :size (du/get-attr el model/attr-size)
    :label (du/get-attr el model/attr-label)}))

(defn interactive-el?
  [^js el]
  (model/interactive? (read-public-state el)))

(defn assigned-nodes
  [^js slot-el]
  (.assignedNodes slot-el #js {:flatten true}))

(defn slot-has-content?
  [^js slot-el]
  (> (alength (assigned-nodes slot-el)) 0))

(defn meaningful-text-node?
  [^js node]
  (and (= (.-nodeType node) js/Node.TEXT_NODE)
       (not= "" (.trim (or (.-textContent node) "")))))

(defn meaningful-element-node?
  [^js node]
  (and (= (.-nodeType node) js/Node.ELEMENT_NODE)
       (not= "" (.trim (or (.-textContent node) "")))))

(defn slot-has-meaningful-text?
  [^js slot-el]
  (let [nodes (assigned-nodes slot-el)]
    (loop [idx 0]
      (if (< idx (alength nodes))
        (let [node (aget nodes idx)]
          (if (or (meaningful-text-node? node)
                  (meaningful-element-node? node))
            true
            (recur (inc idx))))
        false))))

(defn style-text
  []
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-button-radius:var(--x-radius-md,0.75rem);"
   "--x-button-gap:0.5rem;"
   "--x-button-padding-inline:0.95rem;"
   "--x-button-height-sm:2rem;"
   "--x-button-height-md:2.5rem;"
   "--x-button-height-lg:3rem;"
   "--x-button-font-size-sm:0.875rem;"
   "--x-button-font-size-md:0.9375rem;"
   "--x-button-font-size-lg:1rem;"
   "--x-button-font-weight:600;"
   "--x-button-icon-size-sm:0.875rem;"
   "--x-button-icon-size-md:1rem;"
   "--x-button-icon-size-lg:1.125rem;"
   "--x-button-spinner-size:1em;"
   "--x-button-spinner-stroke:2px;"
   "--x-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08),0 1px 1px rgba(15,23,42,0.04));"
   "--x-button-shadow-hover:var(--x-shadow-md,0 4px 10px rgba(15,23,42,0.10),0 2px 4px rgba(15,23,42,0.06));"
   "--x-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(15,23,42,0.08));"
   "--x-button-bg:var(--x-color-primary,#2563eb);"
   "--x-button-bg-hover:var(--x-color-primary-hover,#1d4ed8);"
   "--x-button-bg-active:var(--x-color-primary-active,#1e40af);"
   "--x-button-bg-disabled:#cbd5e1;"
   "--x-button-fg:#ffffff;"
   "--x-button-fg-disabled:#ffffff;"
   "--x-button-border:transparent;"
   "--x-button-border-hover:transparent;"
   "--x-button-border-active:transparent;"
   "--x-button-secondary-bg:var(--x-color-surface,#ffffff);"
   "--x-button-secondary-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-button-secondary-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-secondary-fg:var(--x-color-text,#0f172a);"
   "--x-button-secondary-border:var(--x-color-border,#cbd5e1);"
   "--x-button-tertiary-bg:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-tertiary-bg-hover:var(--x-color-surface-active,#e2e8f0);"
   "--x-button-tertiary-bg-active:var(--x-color-border,#cbd5e1);"
   "--x-button-tertiary-fg:var(--x-color-text,#0f172a);"
   "--x-button-ghost-bg:transparent;"
   "--x-button-ghost-bg-hover:var(--x-color-surface-hover,#f8fafc);"
   "--x-button-ghost-bg-active:var(--x-color-surface-active,#f1f5f9);"
   "--x-button-ghost-fg:var(--x-color-text-muted,#334155);"
   "--x-button-danger-bg:var(--x-color-danger,#dc2626);"
   "--x-button-danger-bg-hover:#b91c1c;"
   "--x-button-danger-bg-active:#991b1b;"
   "--x-button-danger-fg:#ffffff;"
   "--x-button-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-button-transition-duration:var(--x-transition-duration,140ms);"
   "--x-button-transition-easing:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "}"
   "@media (prefers-color-scheme: dark){"
   ":host{"
   "--x-button-shadow:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.35),0 1px 1px rgba(0,0,0,0.2));"
   "--x-button-shadow-hover:var(--x-shadow-md,0 6px 14px rgba(0,0,0,0.35),0 2px 6px rgba(0,0,0,0.24));"
   "--x-button-shadow-active:var(--x-shadow-sm,0 1px 2px rgba(0,0,0,0.3));"
   "--x-button-bg:var(--x-color-primary,#3b82f6);"
   "--x-button-bg-hover:var(--x-color-primary-hover,#2563eb);"
   "--x-button-bg-active:var(--x-color-primary-active,#1d4ed8);"
   "--x-button-bg-disabled:#334155;"
   "--x-button-fg:#eff6ff;"
   "--x-button-fg-disabled:#94a3b8;"
   "--x-button-secondary-bg:var(--x-color-surface,#111827);"
   "--x-button-secondary-bg-hover:var(--x-color-surface-hover,#1f2937);"
   "--x-button-secondary-bg-active:var(--x-color-surface-active,#273449);"
   "--x-button-secondary-fg:var(--x-color-text,#e5e7eb);"
   "--x-button-secondary-border:var(--x-color-border,#374151);"
   "--x-button-tertiary-bg:var(--x-color-surface-active,#1e293b);"
   "--x-button-tertiary-bg-hover:var(--x-color-surface-active,#273449);"
   "--x-button-tertiary-bg-active:var(--x-color-border,#334155);"
   "--x-button-tertiary-fg:var(--x-color-text,#e5e7eb);"
   "--x-button-ghost-bg:transparent;"
   "--x-button-ghost-bg-hover:var(--x-color-surface-hover,#0f172a);"
   "--x-button-ghost-bg-active:var(--x-color-surface-active,#172033);"
   "--x-button-ghost-fg:var(--x-color-text-muted,#cbd5e1);"
   "--x-button-danger-bg:var(--x-color-danger,#ef4444);"
   "--x-button-danger-bg-hover:#dc2626;"
   "--x-button-danger-bg-active:#b91c1c;"
   "--x-button-danger-fg:#ffffff;"
   "--x-button-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "@keyframes x-button-spin{"
   "from{transform:rotate(0deg);}"
   "to{transform:rotate(360deg);}"
   "}"
   "button{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "inline-size:100%;"
   "min-inline-size:0;"
   "min-block-size:var(--x-button-height-md);"
   "padding-inline:var(--x-button-padding-inline);"
   "border-radius:var(--x-button-radius);"
   "border:1px solid var(--x-button-border);"
   "background:var(--x-button-bg);"
   "color:var(--x-button-fg);"
   "font-size:var(--x-button-font-size-md);"
   "font-weight:var(--x-button-font-weight);"
   "line-height:1;"
   "cursor:pointer;"
   "user-select:none;"
   "touch-action:manipulation;"
   "-webkit-tap-highlight-color:transparent;"
   "box-shadow:var(--x-button-shadow);"
   "transition:"
   "background var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "border-color var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "color var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "transform var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "box-shadow var(--x-button-transition-duration) var(--x-button-transition-easing),"
   "opacity var(--x-button-transition-duration) var(--x-button-transition-easing);"
   "}"
   "button[disabled]{cursor:default;box-shadow:none;}"
   "button:focus-visible{outline:none;box-shadow:0 0 0 3px var(--x-button-focus-ring),var(--x-button-shadow);}"
   "button[data-size='sm']{min-block-size:var(--x-button-height-sm);font-size:var(--x-button-font-size-sm);}"
   "button[data-size='md']{min-block-size:var(--x-button-height-md);font-size:var(--x-button-font-size-md);}"
   "button[data-size='lg']{min-block-size:var(--x-button-height-lg);font-size:var(--x-button-font-size-lg);}"

   "button[data-variant='primary']{"
   "background:var(--x-button-bg);"
   "color:var(--x-button-fg);"
   "border-color:var(--x-button-border);"
   "}"
   "button[data-variant='primary'][data-hover='true']:not([disabled]){"
   "background:var(--x-button-bg-hover);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='primary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-variant='secondary']{"
   "background:var(--x-button-secondary-bg);"
   "color:var(--x-button-secondary-fg);"
   "border-color:var(--x-button-secondary-border);"
   "}"
   "button[data-variant='secondary'][data-hover='true']:not([disabled]){"
   "background:var(--x-button-secondary-bg-hover);"
   "border-color:var(--x-button-secondary-border);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='secondary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-secondary-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-variant='tertiary']{"
   "background:var(--x-button-tertiary-bg);"
   "color:var(--x-button-tertiary-fg);"
   "border-color:transparent;"
   "box-shadow:none;"
   "}"
   "button[data-variant='tertiary'][data-hover='true']:not([disabled]){"
   "background:var(--x-button-tertiary-bg-hover);"
   "}"
   "button[data-variant='tertiary'][data-active='true']:not([disabled]){"
   "background:var(--x-button-tertiary-bg-active);"
   "transform:translateY(1px);"
   "}"

   "button[data-variant='ghost']{"
   "background:var(--x-button-ghost-bg);"
   "color:var(--x-button-ghost-fg);"
   "border-color:transparent;"
   "box-shadow:none;"
   "}"
   "button[data-variant='ghost'][data-hover='true']:not([disabled]){"
   "background:var(--x-button-ghost-bg-hover);"
   "}"
   "button[data-variant='ghost'][data-active='true']:not([disabled]){"
   "background:var(--x-button-ghost-bg-active);"
   "transform:translateY(1px);"
   "}"

   "button[data-variant='danger']{"
   "background:var(--x-button-danger-bg);"
   "color:var(--x-button-danger-fg);"
   "border-color:transparent;"
   "}"
   "button[data-variant='danger'][data-hover='true']:not([disabled]){"
   "background:var(--x-button-danger-bg-hover);"
   "box-shadow:var(--x-button-shadow-hover);"
   "}"
   "button[data-variant='danger'][data-active='true']:not([disabled]){"
   "background:var(--x-button-danger-bg-active);"
   "transform:translateY(1px);"
   "box-shadow:var(--x-button-shadow-active);"
   "}"

   "button[data-loading='true']{cursor:progress;}"
   "button[data-disabled='true']{"
   "background:var(--x-button-bg-disabled);"
   "color:var(--x-button-fg-disabled);"
   "border-color:transparent;"
   "opacity:0.72;"
   "}"
   "[part='inner']{display:inline-flex;align-items:center;justify-content:center;gap:var(--x-button-gap);min-inline-size:0;}"
   "[part='label']{display:inline-flex;align-items:center;justify-content:center;min-inline-size:0;white-space:nowrap;}"
   "[part='icon-start'],[part='icon-end'],[part='spinner']{display:inline-flex;align-items:center;justify-content:center;flex:none;}"
   "button[data-size='sm'] [part='icon-start'],button[data-size='sm'] [part='icon-end']{inline-size:var(--x-button-icon-size-sm);block-size:var(--x-button-icon-size-sm);}"
   "button[data-size='md'] [part='icon-start'],button[data-size='md'] [part='icon-end']{inline-size:var(--x-button-icon-size-md);block-size:var(--x-button-icon-size-md);}"
   "button[data-size='lg'] [part='icon-start'],button[data-size='lg'] [part='icon-end']{inline-size:var(--x-button-icon-size-lg);block-size:var(--x-button-icon-size-lg);}"
   "[part='spinner']{inline-size:var(--x-button-spinner-size);block-size:var(--x-button-spinner-size);position:relative;}"
   "[part='spinner-slot']{display:inline-flex;align-items:center;justify-content:center;}"
   "[part='spinner-fallback']{"
   "display:none;"
   "inline-size:100%;"
   "block-size:100%;"
   "box-sizing:border-box;"
   "border-radius:999px;"
   "border:var(--x-button-spinner-stroke) solid currentColor;"
   "border-inline-end-color:transparent;"
   "animation:x-button-spin 0.7s linear infinite;"
   "opacity:0.9;"
   "}"
   "button[data-has-icon-start='false'] [part='icon-start']{display:none;}"
   "button[data-has-icon-end='false'] [part='icon-end']{display:none;}"
   "button[data-loading='false'] [part='spinner']{display:none;}"
   "button[data-loading='true'][data-has-spinner='false'] [part='spinner-fallback']{display:inline-block;}"
   "button[data-loading='true'][data-has-spinner='true'] [part='spinner-fallback']{display:none;}"
   "@media (prefers-reduced-motion: reduce){"
   "button{transition:none;}"
   "[part='spinner-fallback']{animation:none;}"
   "}"
   ))

(defn create-el
  [tag]
  (.createElement js/document tag))

(defn make-shadow-state
  [root style-el button-el inner-el label-slot-el icon-start-slot-el icon-end-slot-el spinner-slot-el]
  #js {:root root
       :style style-el
       :button button-el
       :inner inner-el
       :label-slot label-slot-el
       :icon-start-slot icon-start-slot-el
       :icon-end-slot icon-end-slot-el
       :spinner-slot spinner-slot-el})

(defn create-shadow!
  [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-el (create-el "style")
        button-el (create-el "button")
        inner-el (create-el "span")
        icon-start-el (create-el "span")
        label-el (create-el "span")
        spinner-el (create-el "span")
        spinner-fallback-el (create-el "span")
        icon-end-el (create-el "span")
        default-slot-el (create-el "slot")
        icon-start-slot-el (create-el "slot")
        icon-end-slot-el (create-el "slot")
        spinner-slot-el (create-el "slot")]

    (set! (.-textContent style-el) (style-text))

    (.setAttribute button-el "part" "button")
    (.setAttribute inner-el "part" "inner")
    (.setAttribute icon-start-el "part" "icon-start")
    (.setAttribute label-el "part" "label")
    (.setAttribute spinner-el "part" "spinner")
    (.setAttribute spinner-slot-el "part" "spinner-slot")
    (.setAttribute spinner-fallback-el "part" "spinner-fallback")
    (.setAttribute icon-end-el "part" "icon-end")

    (.setAttribute icon-start-slot-el "name" model/slot-icon-start)
    (.setAttribute icon-end-slot-el "name" model/slot-icon-end)
    (.setAttribute spinner-slot-el "name" model/slot-spinner)

    (.setAttribute spinner-el "aria-hidden" "true")
    (.setAttribute spinner-fallback-el "aria-hidden" "true")

    (.appendChild icon-start-el icon-start-slot-el)
    (.appendChild label-el default-slot-el)
    (.appendChild spinner-el spinner-slot-el)
    (.appendChild spinner-el spinner-fallback-el)
    (.appendChild icon-end-el icon-end-slot-el)

    (.appendChild inner-el icon-start-el)
    (.appendChild inner-el label-el)
    (.appendChild inner-el spinner-el)
    (.appendChild inner-el icon-end-el)

    (.appendChild button-el inner-el)

    (.appendChild root style-el)
    (.appendChild root button-el)

    (make-shadow-state root
                       style-el
                       button-el
                       inner-el
                       default-slot-el
                       icon-start-slot-el
                       icon-end-slot-el
                       spinner-slot-el)))

(defn render!
  [^js el state]
  (let [button-el (aget state "button")
        label-slot-el (aget state "label-slot")
        icon-start-slot-el (aget state "icon-start-slot")
        icon-end-slot-el (aget state "icon-end-slot")
        spinner-slot-el (aget state "spinner-slot")
        public-state (read-public-state el)
        has-default-text? (slot-has-meaningful-text? label-slot-el)
        has-icon-start? (slot-has-content? icon-start-slot-el)
        has-icon-end? (slot-has-content? icon-end-slot-el)
        has-spinner? (slot-has-content? spinner-slot-el)
        hover? (get-hover el)
        active? (some? (get-active-source el))
        focus-visible? (get-focus-visible el)
        aria-label-value (model/aria-label
                          (assoc public-state :has-default-text? has-default-text?))]

    (.setAttribute button-el "type" (:type public-state))
    (set! (.-disabled button-el) (or (:disabled public-state)
                                     (:loading public-state)))

    (if-let [v (model/aria-busy public-state)]
      (.setAttribute button-el "aria-busy" v)
      (.removeAttribute button-el "aria-busy"))

    (if (:pressed public-state)
      (.setAttribute button-el "aria-pressed" "true")
      (.removeAttribute button-el "aria-pressed"))

    (if aria-label-value
      (.setAttribute button-el "aria-label" aria-label-value)
      (.removeAttribute button-el "aria-label"))

    (.setAttribute button-el "data-variant" (:variant public-state))
    (.setAttribute button-el "data-size" (:size public-state))
    (.setAttribute button-el "data-loading" (if (:loading public-state) "true" "false"))
    (.setAttribute button-el "data-disabled" (if (:disabled public-state) "true" "false"))
    (.setAttribute button-el "data-hover" (if hover? "true" "false"))
    (.setAttribute button-el "data-active" (if active? "true" "false"))
    (.setAttribute button-el "data-focus-visible" (if focus-visible? "true" "false"))
    (.setAttribute button-el "data-has-icon-start" (if has-icon-start? "true" "false"))
    (.setAttribute button-el "data-has-icon-end" (if has-icon-end? "true" "false"))
    (.setAttribute button-el "data-has-spinner" (if has-spinner? "true" "false"))

    (.setAttribute el "data-variant" (:variant public-state))
    (.setAttribute el "data-size" (:size public-state))))

(defn end-active-press!
  [^js el]
  (let [source (get-active-source el)]
    (when source
      (set-active-source! el nil)
      (du/dispatch! el model/event-press-end #js {:source source})
      (when-let [state (get-el-state el)]
        (render! el state)))))

(defn sync-noninteractive-state!
  [^js el]
  (when-not (interactive-el? el)
    (set-hover! el false)
    (set-active-source! el nil)))

(defn setup-hover!
  [^js el ^js button-el]
  (.addEventListener
   button-el
   "pointerenter"
   (fn [_]
     (when (interactive-el? el)
       (when-not (get-hover el)
         (set-hover! el true)
         (render! el (get-el-state el))
         (du/dispatch! el model/event-hover-start #js {})))))

  (.addEventListener
   button-el
   "pointerleave"
   (fn [_]
     (when (get-hover el)
       (set-hover! el false)
       (render! el (get-el-state el))
       (when (interactive-el? el)
         (du/dispatch! el model/event-hover-end #js {})))
     (when (= "pointer" (get-active-source el))
       (end-active-press! el)))))

(defn setup-press!
  [^js el ^js button-el]
  (.addEventListener
   button-el
   "pointerdown"
   (fn [_]
     (when (interactive-el? el)
       (when-not (get-active-source el)
         (set-last-activation-source! el "pointer")
         (set-active-source! el "pointer")
         (render! el (get-el-state el))
         (du/dispatch! el model/event-press-start #js {:source "pointer"})))))

  (.addEventListener
   button-el
   "pointerup"
   (fn [_]
     (when (= "pointer" (get-active-source el))
       (end-active-press! el))))

  (.addEventListener
   button-el
   "pointercancel"
   (fn [_]
     (when (= "pointer" (get-active-source el))
       (end-active-press! el))))

  (.addEventListener
   button-el
   "keydown"
   (fn [event]
     (let [key (.-key event)]
       (when (and (interactive-el? el)
                  (or (= key "Enter") (= key " ")))
         (when-not (= "keyboard" (get-active-source el))
           (set-last-activation-source! el "keyboard")
           (set-active-source! el "keyboard")
           (render! el (get-el-state el))
           (du/dispatch! el model/event-press-start #js {:source "keyboard"}))))))

  (.addEventListener
   button-el
   "keyup"
   (fn [event]
     (let [key (.-key event)]
       (when (and (= "keyboard" (get-active-source el))
                  (or (= key "Enter") (= key " ")))
         (end-active-press! el)))))

  (.addEventListener
   button-el
   "blur"
   (fn [_]
     (when (get-active-source el)
       (end-active-press! el))))

  (.addEventListener
   button-el
   "click"
   (fn [_]
     (when (interactive-el? el)
       (let [source (or (get-last-activation-source el) "programmatic")]
         (du/dispatch! el model/event-press #js {:source source})
         (set-last-activation-source! el nil)
         (let [btn-type (:type (read-public-state el))
               form (find-owner-form el)]
           (when form
             (cond
               (= btn-type "submit") (.requestSubmit form)
               (= btn-type "reset")  (.reset form)))))))))

(defn setup-focus!
  [^js el ^js button-el]
  (.addEventListener
   button-el
   "focus"
   (fn [_]
     (let [visible (.matches button-el ":focus-visible")]
       (set-focus-visible! el visible)
       (render! el (get-el-state el))
       (when visible
         (du/dispatch! el model/event-focus-visible #js {})))))

  (.addEventListener
   button-el
   "blur"
   (fn [_]
     (set-focus-visible! el false)
     (render! el (get-el-state el)))))

(defn setup-slots!
  [^js el state]
  (let [rerender (fn [_]
                   (render! el state))]
    (.addEventListener (aget state "label-slot") "slotchange" rerender)
    (.addEventListener (aget state "icon-start-slot") "slotchange" rerender)
    (.addEventListener (aget state "icon-end-slot") "slotchange" rerender)
    (.addEventListener (aget state "spinner-slot") "slotchange" rerender)))

(defn connected!
  [^js el]
  (when-not (get-el-state el)
    (let [state (create-shadow! el)
          button-el (aget state "button")]
      (set-hover! el false)
      (set-focus-visible! el false)
      (set-active-source! el nil)
      (set-last-activation-source! el nil)
      (setup-hover! el button-el)
      (setup-press! el button-el)
      (setup-focus! el button-el)
      (setup-slots! el state)
      (set-el-state! el state)))
  (sync-noninteractive-state! el)
  (render! el (get-el-state el)))

(defn disconnected!
  [^js el]
  (set-hover! el false)
  (set-focus-visible! el false)
  (set-active-source! el nil)
  (set-last-activation-source! el nil))

(defn attribute-changed!
  [^js el _name _old-value _new-value]
  (when-let [state (get-el-state el)]
    (sync-noninteractive-state! el)
    (render! el state)))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :setup-prototype-fn     (fn [proto] (du/install-properties! proto model/property-api))}))
