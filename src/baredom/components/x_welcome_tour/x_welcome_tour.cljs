(ns baredom.components.x-welcome-tour.x-welcome-tour
  "Orchestrator for the welcome tour overlay.
   Manages the SVG spotlight backdrop, popover positioning, step
   navigation, connector rendering, focus trap, and keyboard/scroll/resize
   handling. The host element is invisible — all visible UI lives in a
   portal layer created via the shared overlay utility."
  (:require
   [baredom.components.x-welcome-tour.model :as model]
   [baredom.components.x-welcome-tour-step.model :as step-model]
   [baredom.utils.dom :as du]
   [baredom.utils.overlay :as overlay]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs       "__xWelcomeTourRefs")
(def ^:private k-model      "__xWelcomeTourModel")
(def ^:private k-handlers   "__xWelcomeTourHandlers")
(def ^:private k-layer      "__xWelcomeTourLayer")
(def ^:private k-observer   "__xWelcomeTourObserver")
(def ^:private k-raf        "__xWelcomeTourRaf")
(def ^:private k-resize-obs "__xWelcomeTourResizeObs")
(def ^:private k-restore    "__xWelcomeTourRestore")
(def ^:private k-prev-open  "__xWelcomeTourPrevOpen")

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Arrow size ──────────────────────────────────────────────────────────────
(def ^:private arrow-size 8)

;; ── Host styles ─────────────────────────────────────────────────────────────
(def ^:private host-style-text
  ":host{display:contents;}")

;; ── Overlay layer styles ────────────────────────────────────────────────────
(def ^:private layer-style-text
  (str
   ":host{pointer-events:auto;}"

   ;; CSS custom properties (consumed from x-theme or host overrides)
   "[part=backdrop-svg]{"
   "position:fixed;inset:0;width:100%;height:100%;z-index:0;}"

   ".cutout-rect{"
   "transition:"
   "x var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "y var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "width var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "height var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "rx var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "ry var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease));}"

   "[part=popover]{"
   "position:fixed;"
   "z-index:1;"
   "width:var(--x-welcome-tour-popover-width,min(360px,calc(100vw - 2rem)));"
   "background:var(--x-welcome-tour-popover-bg,var(--x-color-bg,#ffffff));"
   "color:var(--x-welcome-tour-popover-fg,var(--x-color-text,#0f172a));"
   "border:var(--x-welcome-tour-popover-border,1px solid var(--x-color-border,#e2e8f0));"
   "border-radius:var(--x-welcome-tour-popover-radius,var(--x-radius-lg,12px));"
   "box-shadow:var(--x-welcome-tour-popover-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.18)));"
   "font-family:var(--x-font-family,system-ui,-apple-system,sans-serif);"
   "font-size:var(--x-font-size-base,0.875rem);"
   "line-height:var(--x-line-height-normal,1.5);"
   "transition:"
   "left var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "top var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease)),"
   "opacity var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease));"
   "}"

   "@media (prefers-color-scheme:dark){"
   "[part=popover]{"
   "background:var(--x-welcome-tour-popover-bg,var(--x-color-bg,#1e293b));"
   "color:var(--x-welcome-tour-popover-fg,var(--x-color-text,#f1f5f9));"
   "border-color:var(--x-color-border,#334155);"
   "box-shadow:var(--x-welcome-tour-popover-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.4)));}}"

   "[part=header]{"
   "display:flex;align-items:center;gap:var(--x-space-sm,8px);"
   "padding:var(--x-space-md,12px) var(--x-space-md,12px) 0;}"

   "[part=title]{"
   "flex:1;font-weight:var(--x-font-weight-semibold,600);"
   "font-size:var(--x-font-size-base,0.875rem);"
   "overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}"

   "[part=counter]{"
   "font-size:var(--x-font-size-xs,0.75rem);"
   "color:var(--x-color-text-muted,#64748b);white-space:nowrap;}"

   "@media (prefers-color-scheme:dark){"
   "[part=counter]{color:var(--x-color-text-muted,#94a3b8);}}"

   "[part=close-button]{"
   "appearance:none;border:none;background:none;cursor:pointer;"
   "padding:var(--x-space-xs,4px);border-radius:var(--x-radius-sm,4px);"
   "color:var(--x-color-text-muted,#64748b);font-size:1.125rem;line-height:1;"
   "display:flex;align-items:center;justify-content:center;"
   "min-width:28px;min-height:28px;}"
   "[part=close-button]:hover{background:var(--x-color-surface-hover,rgba(0,0,0,0.06));}"
   "[part=close-button]:focus-visible{"
   "outline:2px solid var(--x-color-focus-ring,rgba(0,102,204,0.55));"
   "outline-offset:2px;}"

   "@media (prefers-color-scheme:dark){"
   "[part=close-button]{color:var(--x-color-text-muted,#94a3b8);}"
   "[part=close-button]:hover{background:var(--x-color-surface-hover,rgba(255,255,255,0.08));}}"

   "@media (pointer:coarse){"
   "[part=close-button]{min-width:44px;min-height:44px;}}"

   "[part=body]{"
   "padding:var(--x-space-sm,8px) var(--x-space-md,12px);}"

   "[part=footer]{"
   "display:flex;align-items:center;justify-content:space-between;"
   "padding:0 var(--x-space-md,12px) var(--x-space-md,12px);gap:var(--x-space-sm,8px);}"

   "[part=prev-button],[part=next-button]{"
   "appearance:none;border:none;cursor:pointer;"
   "padding:var(--x-space-xs,4px) var(--x-space-md,12px);"
   "border-radius:var(--x-radius-md,6px);"
   "font-family:inherit;font-size:var(--x-font-size-sm,0.8125rem);"
   "font-weight:var(--x-font-weight-medium,500);"
   "min-height:32px;transition:background 120ms ease,color 120ms ease;}"

   "@media (pointer:coarse){"
   "[part=prev-button],[part=next-button]{min-height:44px;padding:var(--x-space-sm,8px) var(--x-space-md,12px);}}"

   "[part=prev-button]{"
   "background:var(--x-color-surface,rgba(0,0,0,0.04));"
   "color:var(--x-color-text,#0f172a);}"
   "[part=prev-button]:hover{background:var(--x-color-surface-hover,rgba(0,0,0,0.08));}"

   "@media (prefers-color-scheme:dark){"
   "[part=prev-button]{"
   "background:var(--x-color-surface,rgba(255,255,255,0.06));"
   "color:var(--x-color-text,#f1f5f9);}"
   "[part=prev-button]:hover{background:var(--x-color-surface-hover,rgba(255,255,255,0.1));}}"

   "[part=next-button]{"
   "background:var(--x-welcome-tour-button-bg,var(--x-color-primary,#3b82f6));"
   "color:var(--x-welcome-tour-button-fg,#ffffff);}"
   "[part=next-button]:hover{filter:brightness(1.1);}"

   "[part=prev-button]:focus-visible,[part=next-button]:focus-visible{"
   "outline:2px solid var(--x-color-focus-ring,rgba(0,102,204,0.55));"
   "outline-offset:2px;}"

   "[part=dots]{"
   "display:flex;align-items:center;gap:6px;flex:1;justify-content:center;}"

   "[part=dot]{"
   "width:8px;height:8px;border-radius:var(--x-radius-full,9999px);"
   "background:var(--x-welcome-tour-dot-color,var(--x-color-border,#e2e8f0));"
   "transition:background 150ms ease;}"
   "[part=dot][data-active]{"
   "background:var(--x-welcome-tour-dot-active,var(--x-color-primary,#3b82f6));}"

   "@media (prefers-color-scheme:dark){"
   "[part=dot]{background:var(--x-welcome-tour-dot-color,var(--x-color-border,#475569));}"
   "[part=dot][data-active]{background:var(--x-welcome-tour-dot-active,var(--x-color-primary,#60a5fa));}}"

   "[part=arrow]{"
   "position:absolute;width:0;height:0;"
   "border:8px solid transparent;pointer-events:none;}"

   "[part=connector-svg]{"
   "position:fixed;inset:0;width:100%;height:100%;"
   "pointer-events:none;z-index:0;overflow:visible;}"

   "[part=connector-svg] path{"
   "fill:none;"
   "stroke:var(--x-welcome-tour-connector-color,var(--x-color-primary,#3b82f6));"
   "stroke-width:var(--x-welcome-tour-connector-width,2);"
   "stroke-linecap:round;"
   "transition:d var(--x-welcome-tour-transition-duration,var(--x-transition-duration,300ms)) var(--x-welcome-tour-transition-easing,var(--x-transition-easing,ease));}"

   "@media (prefers-reduced-motion:reduce){"
   ".cutout-rect,"
   "[part=popover],"
   "[part=connector-svg] path{transition:none !important;}}"))

;; ── Helper: create SVG element ──────────────────────────────────────────────
(defn- svg-el [tag]
  (.createElementNS js/document svg-ns tag))

;; ── Collect step definitions from child elements ────────────────────────────
(defn- get-step-els
  "Return a js array of x-welcome-tour-step child elements."
  [^js el]
  (let [children (.-children el)
        len      (.-length children)
        result   #js []]
    (loop [i 0]
      (when (< i len)
        (let [^js child (aget children i)]
          (when (= (.toLowerCase (.-tagName child)) step-model/tag-name)
            (.push result child)))
        (recur (inc i))))
    result))

(defn- read-step-model
  "Read a step element's model (normalised attributes)."
  [^js step-el]
  (or (du/getv step-el "__xWelcomeTourStepModel")
      (step-model/normalize
       {:target-raw         (du/get-attr step-el step-model/attr-target)
        :title-raw          (du/get-attr step-el step-model/attr-title)
        :placement-raw      (du/get-attr step-el step-model/attr-placement)
        :connector-raw      (du/get-attr step-el step-model/attr-connector)
        :cutout-padding-raw (du/get-attr step-el step-model/attr-cutout-padding)
        :cutout-radius-raw  (du/get-attr step-el step-model/attr-cutout-radius)
        :scroll-to-raw      (if (du/has-attr? step-el step-model/attr-scroll-to)
                               (du/get-attr step-el step-model/attr-scroll-to)
                               nil)})))

;; ── Resolve target element from selector ────────────────────────────────────
(defn- resolve-target
  "Query document for the target element. Returns nil if not found or hidden."
  [^js selector]
  (when (and (string? selector) (not= selector ""))
    (let [^js el (.querySelector js/document selector)]
      (when (and el (pos? (.-offsetWidth el)))
        el))))

;; ── Create overlay layer DOM ────────────────────────────────────────────────
(defn- create-layer-dom!
  "Build the overlay layer with SVG backdrop, popover, and connector.
   Returns a map of DOM refs."
  [^js trigger-el]
  (let [^js layer  (overlay/make-layer! trigger-el layer-style-text
                                         "var(--x-welcome-tour-backdrop-z,var(--x-z-modal,1100))")
        ^js shadow (.-shadowRoot layer)

        ;; SVG backdrop with mask cutout
        ^js svg    (svg-el "svg")
        ^js defs   (svg-el "defs")
        ^js mask   (svg-el "mask")
        ^js mask-white (svg-el "rect")
        ^js mask-cut   (svg-el "rect")
        ^js filter-el  (svg-el "filter")
        ^js blur       (svg-el "feGaussianBlur")
        ^js backdrop   (svg-el "rect")
        ^js glow       (svg-el "rect")

        ;; Popover
        ^js popover      (.createElement js/document "div")
        ^js header       (.createElement js/document "div")
        ^js title-el     (.createElement js/document "span")
        ^js counter-el   (.createElement js/document "span")
        ^js close-btn    (.createElement js/document "button")
        ^js body-el      (.createElement js/document "div")
        ^js footer       (.createElement js/document "div")
        ^js prev-btn     (.createElement js/document "button")
        ^js dots-el      (.createElement js/document "div")
        ^js next-btn     (.createElement js/document "button")
        ^js arrow-el     (.createElement js/document "div")

        ;; Connector SVG
        ^js conn-svg     (svg-el "svg")
        ^js conn-path    (svg-el "path")]

    ;; --- SVG backdrop ---
    (.setAttribute svg "part" "backdrop-svg")
    (.setAttribute svg "aria-hidden" "true")

    (.setAttribute mask "id" "tour-cutout-mask")
    (.setAttribute mask-white "width" "100%")
    (.setAttribute mask-white "height" "100%")
    (.setAttribute mask-white "fill" "white")
    (.setAttribute mask-cut "class" "cutout-rect")
    (.setAttribute mask-cut "fill" "black")
    (.setAttribute mask-cut "x" "0")
    (.setAttribute mask-cut "y" "0")
    (.setAttribute mask-cut "width" "0")
    (.setAttribute mask-cut "height" "0")
    (.setAttribute mask-cut "rx" "0")
    (.setAttribute mask-cut "ry" "0")
    (.appendChild mask mask-white)
    (.appendChild mask mask-cut)

    (.setAttribute filter-el "id" "tour-glow")
    (.setAttribute blur "stdDeviation" "8")
    (.setAttribute blur "in" "SourceGraphic")
    (.appendChild filter-el blur)

    (.appendChild defs mask)
    (.appendChild defs filter-el)
    (.appendChild svg defs)

    (.setAttribute backdrop "width" "100%")
    (.setAttribute backdrop "height" "100%")
    (.setAttribute backdrop "fill"
                   "var(--x-welcome-tour-backdrop,rgba(0,0,0,0.5))")
    (.setAttribute backdrop "mask" "url(#tour-cutout-mask)")
    (.appendChild svg backdrop)

    (.setAttribute glow "class" "cutout-rect")
    (.setAttribute glow "fill"
                   "var(--x-welcome-tour-glow-color,rgba(255,255,255,0.15))")
    (.setAttribute glow "filter" "url(#tour-glow)")
    (.setAttribute glow "x" "0")
    (.setAttribute glow "y" "0")
    (.setAttribute glow "width" "0")
    (.setAttribute glow "height" "0")
    (.setAttribute glow "rx" "0")
    (.setAttribute glow "ry" "0")
    (.appendChild svg glow)

    ;; --- Popover ---
    (.setAttribute popover "part" "popover")
    (.setAttribute popover "role" "dialog")
    (.setAttribute popover "aria-modal" "false")

    (.setAttribute header "part" "header")
    (.setAttribute title-el "part" "title")
    (.setAttribute counter-el "part" "counter")
    (.setAttribute close-btn "part" "close-button")
    (.setAttribute close-btn "type" "button")
    (.setAttribute close-btn "aria-label" "Close tour")
    (set! (.-textContent close-btn) "\u00d7")
    (.appendChild header title-el)
    (.appendChild header counter-el)
    (.appendChild header close-btn)

    (.setAttribute body-el "part" "body")

    (.setAttribute footer "part" "footer")
    (.setAttribute prev-btn "part" "prev-button")
    (.setAttribute prev-btn "type" "button")
    (.setAttribute dots-el "part" "dots")
    (.setAttribute next-btn "part" "next-button")
    (.setAttribute next-btn "type" "button")
    (.appendChild footer prev-btn)
    (.appendChild footer dots-el)
    (.appendChild footer next-btn)

    (.setAttribute arrow-el "part" "arrow")
    (.appendChild popover arrow-el)
    (.appendChild popover header)
    (.appendChild popover body-el)
    (.appendChild popover footer)

    ;; --- Connector SVG ---
    (.setAttribute conn-svg "part" "connector-svg")
    (.setAttribute conn-svg "aria-hidden" "true")
    (.appendChild conn-svg conn-path)

    ;; --- Assemble in shadow ---
    (.appendChild shadow svg)
    (.appendChild shadow popover)
    (.appendChild shadow conn-svg)

    ;; Enable pointer events on the layer itself
    (set! (.. layer -style -pointerEvents) "auto")

    {:layer      layer
     :svg        svg
     :mask-cut   mask-cut
     :glow       glow
     :popover    popover
     :title-el   title-el
     :counter-el counter-el
     :close-btn  close-btn
     :body-el    body-el
     :prev-btn   prev-btn
     :dots-el    dots-el
     :next-btn   next-btn
     :arrow-el   arrow-el
     :conn-svg   conn-svg
     :conn-path  conn-path}))

;; ── Update cutout rect ──────────────────────────────────────────────────────
(defn- update-cutout!
  "Update the SVG cutout mask and glow rect to match the target."
  [{:keys [^js mask-cut ^js glow]} cutout]
  (let [{:keys [x y width height rx ry]} cutout]
    (doseq [^js r [mask-cut glow]]
      (.setAttribute r "x" (str x))
      (.setAttribute r "y" (str y))
      (.setAttribute r "width" (str width))
      (.setAttribute r "height" (str height))
      (.setAttribute r "rx" (str rx))
      (.setAttribute r "ry" (str ry))))
  nil)

;; ── Update popover position ─────────────────────────────────────────────────
(defn- update-popover-position!
  "Position the popover at the given coordinates."
  [^js popover x y]
  (set! (.. popover -style -left) (str x "px"))
  (set! (.. popover -style -top) (str y "px"))
  nil)

;; ── Update arrow ────────────────────────────────────────────────────────────
(defn- update-arrow!
  "Update the CSS arrow position and direction.
   The arrow points at the centre of the target, clamped to stay within
   the popover bounds."
  [^js arrow-el connector-type final-placement target-rect popover-rect]
  (if (= connector-type "arrow")
    (let [styles (model/arrow-style final-placement arrow-size target-rect popover-rect)]
      (set! (.. arrow-el -style -display) "block")
      ;; Reset all directional properties
      (set! (.. arrow-el -style -top) "auto")
      (set! (.. arrow-el -style -bottom) "auto")
      (set! (.. arrow-el -style -left) "auto")
      (set! (.. arrow-el -style -right) "auto")
      (set! (.. arrow-el -style -marginLeft) "0")
      (set! (.. arrow-el -style -marginTop) "0")
      (set! (.. arrow-el -style -borderTopColor) "transparent")
      (set! (.. arrow-el -style -borderBottomColor) "transparent")
      (set! (.. arrow-el -style -borderLeftColor) "transparent")
      (set! (.. arrow-el -style -borderRightColor) "transparent")
      ;; Apply placement-specific styles
      (when-let [v (:top styles)]    (set! (.. arrow-el -style -top) v))
      (when-let [v (:bottom styles)] (set! (.. arrow-el -style -bottom) v))
      (when-let [v (:left styles)]   (set! (.. arrow-el -style -left) v))
      (when-let [v (:right styles)]  (set! (.. arrow-el -style -right) v))
      (when-let [v (:margin-left styles)]  (set! (.. arrow-el -style -marginLeft) v))
      (when-let [v (:margin-top styles)]   (set! (.. arrow-el -style -marginTop) v))
      (when-let [v (:border-top-color styles)]    (set! (.. arrow-el -style -borderTopColor) v))
      (when-let [v (:border-bottom-color styles)] (set! (.. arrow-el -style -borderBottomColor) v))
      (when-let [v (:border-left-color styles)]   (set! (.. arrow-el -style -borderLeftColor) v))
      (when-let [v (:border-right-color styles)]  (set! (.. arrow-el -style -borderRightColor) v)))
    (set! (.. arrow-el -style -display) "none"))
  nil)

;; ── Update connector SVG ────────────────────────────────────────────────────
(defn- update-connector!
  "Update the SVG connector path between target and popover."
  [{:keys [^js conn-svg ^js conn-path]}
   connector-type target-rect popover-rect final-placement]
  (if (or (= connector-type "line") (= connector-type "curve"))
    (let [{:keys [target-point popover-point]}
          (model/connector-anchor-points target-rect popover-rect final-placement)
          d (model/connector-path-d connector-type target-point popover-point final-placement)]
      (set! (.. conn-svg -style -display) "block")
      (.setAttribute conn-path "d" (or d "")))
    (set! (.. conn-svg -style -display) "none"))
  nil)

;; ── Update dots ─────────────────────────────────────────────────────────────
(defn- update-dots!
  "Render dot indicators for step progress."
  [^js dots-el step total show-dots?]
  (if show-dots?
    (do
      (set! (.. dots-el -style -display) "flex")
      ;; Clear existing dots
      (set! (.-innerHTML dots-el) "")
      (dotimes [i total]
        (let [^js dot (.createElement js/document "span")]
          (.setAttribute dot "part" "dot")
          (when (= i step)
            (.setAttribute dot "data-active" ""))
          (.appendChild dots-el dot))))
    (set! (.. dots-el -style -display) "none"))
  nil)

;; ── Inject step content into popover body ───────────────────────────────────
(defn- inject-step-content!
  "Clone the step element's slotted content into the popover body."
  [^js body-el ^js step-el]
  (set! (.-innerHTML body-el) "")
  (let [children (.-childNodes step-el)
        len      (.-length children)]
    (loop [i 0]
      (when (< i len)
        (let [^js child (aget children i)]
          (.appendChild body-el (.cloneNode child true)))
        (recur (inc i)))))
  nil)

;; ── Focus trap ──────────────────────────────────────────────────────────────
(defn- collect-tabbables
  "Collect tabbable elements within the popover."
  [^js popover]
  (let [sel (str "button:not([disabled]),a[href],"
                 "[tabindex]:not([tabindex='-1'])")
        ^js nodes (.querySelectorAll popover sel)]
    (vec (array-seq nodes))))

(defn- activate-focus-trap!
  "Save current focus and move focus into the popover."
  [^js el layer-refs]
  (du/setv! el k-restore (.-activeElement js/document))
  (let [{:keys [^js next-btn ^js popover]} layer-refs
        tabbables (collect-tabbables popover)]
    (if (seq tabbables)
      (.focus (first tabbables))
      (.focus next-btn)))
  nil)

(defn- deactivate-focus-trap!
  "Restore focus to the element that was focused before the tour opened."
  [^js el]
  (let [^js restore (du/getv el k-restore)]
    (when (and restore (.-isConnected restore))
      (.focus restore)))
  (du/setv! el k-restore nil)
  nil)

(defn- cycle-focus!
  "Handle Tab key cycling within the popover."
  [^js el ^js e]
  (let [layer-refs (du/getv el k-layer)
        {:keys [^js popover]} layer-refs
        tabbables (collect-tabbables popover)]
    (when (seq tabbables)
      (let [^js layer-shadow (when layer-refs
                               (.-shadowRoot (:layer layer-refs)))
            ^js active-in-shadow (when layer-shadow
                                   (.-activeElement layer-shadow))
            act (or active-in-shadow (.-activeElement js/document))
            first-el (first tabbables)
            last-el  (last tabbables)
            shift?   (.-shiftKey e)]
        (cond
          (and shift? (= act first-el))
          (do (.preventDefault e) (.focus last-el))

          (and (not shift?) (= act last-el))
          (do (.preventDefault e) (.focus first-el))))))
  nil)

;; ── Event dispatching ───────────────────────────────────────────────────────
(defn- dispatch!
  "Dispatch a CustomEvent on the host element. Returns true if not cancelled."
  [^js el event-name detail cancelable?]
  (.dispatchEvent el
                  (js/CustomEvent.
                   event-name
                   #js {:detail     (clj->js detail)
                        :bubbles    true
                        :composed   true
                        :cancelable (boolean cancelable?)})))

;; ── Scroll target into view ─────────────────────────────────────────────────
(defn- scroll-target-into-view!
  "Smooth-scroll the target element into view if needed."
  [^js target-el]
  (when target-el
    (let [rect (.getBoundingClientRect target-el)
          vh   (.-innerHeight js/window)]
      (when (or (< (.-top rect) 0) (> (.-bottom rect) vh))
        (.scrollIntoView target-el #js {:behavior "smooth"
                                        :block    "center"
                                        :inline   "center"}))))
  nil)

;; ── Read host model ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open?          (du/has-attr? el model/attr-open)
    :step-raw       (du/get-attr el model/attr-step)
    :connector-raw  (du/get-attr el model/attr-connector)
    :prev-label-raw (du/get-attr el model/attr-prev-label)
    :next-label-raw (du/get-attr el model/attr-next-label)
    :done-label-raw (du/get-attr el model/attr-done-label)
    :skip-label-raw (du/get-attr el model/attr-skip-label)
    :counter?       (du/has-attr? el model/attr-counter)
    :dots?          (du/has-attr? el model/attr-dots)}))

;; ── Core render ─────────────────────────────────────────────────────────────
(defn- render!
  "Main render function. Reads current state and updates the overlay layer."
  [^js el]
  (let [m          (read-model el)
        layer-refs (du/getv el k-layer)]
    (du/setv! el k-model m)
    (if-not (:open? m)
      ;; --- Tour closed ---
      (when layer-refs
        (overlay/remove-layer! (:layer layer-refs))
        (du/setv! el k-layer nil)
        (deactivate-focus-trap! el))

      ;; --- Tour open ---
      (let [refs       (or layer-refs (create-layer-dom! el))
            _          (when-not layer-refs (du/setv! el k-layer refs))
            step-els   (get-step-els el)
            total      (.-length step-els)
            step-idx   (model/clamp-step (:step m) total)]
        (when (pos? total)
          (let [^js step-el  (aget step-els step-idx)
                step-m       (read-step-model step-el)
                connector    (or (:connector step-m) (:connector m))
                ^js target   (resolve-target (:target step-m))
                target-rect  (when target
                               (let [r (.getBoundingClientRect target)]
                                 {:x (.-x r) :y (.-y r)
                                  :width (.-width r) :height (.-height r)}))
                cutout       (when target-rect
                               (model/compute-cutout target-rect
                                                     (:cutout-padding step-m)
                                                     (:cutout-radius step-m)))
                vp           {:width (.-innerWidth js/window)
                              :height (.-innerHeight js/window)}
                ^js popover  (:popover refs)
                ;; Measure popover to compute position
                pop-w        (.-offsetWidth popover)
                pop-h        (.-offsetHeight popover)
                pop-size     {:width (if (pos? pop-w) pop-w 360)
                              :height (if (pos? pop-h) pop-h 200)}
                pos          (when target-rect
                               (model/compute-position
                                (:placement step-m) target-rect pop-size vp connector))
                final-pl     (or (:final-placement pos) (:placement step-m))
                popover-rect (when pos {:x (:x pos) :y (:y pos)
                                        :width pop-w :height pop-h})
                last?        (model/last-step? step-idx total)
                first?       (model/first-step? step-idx)]

            ;; Update cutout
            (if cutout
              (update-cutout! refs cutout)
              ;; No target — hide cutout off-screen
              (update-cutout! refs {:x -9999 :y -9999 :width 0 :height 0 :rx 0 :ry 0}))

            ;; Update popover position
            (when pos
              (update-popover-position! popover (:x pos) (:y pos)))

            ;; Update arrow (points at target centre, clamped to popover edge)
            (when (and target-rect popover-rect)
              (update-arrow! (:arrow-el refs) connector final-pl
                             target-rect popover-rect))

            ;; Update connector line/curve
            (when (and target-rect popover-rect)
              (update-connector! refs connector target-rect popover-rect final-pl))

            ;; Update popover content
            (set! (.-textContent (:title-el refs)) (:title step-m))
            (inject-step-content! (:body-el refs) step-el)

            ;; Counter / dots
            (if (:counter? m)
              (do (set! (.. (:counter-el refs) -style -display) "inline")
                  (set! (.-textContent (:counter-el refs))
                        (model/counter-text step-idx total)))
              (set! (.. (:counter-el refs) -style -display) "none"))

            (update-dots! (:dots-el refs) step-idx total (:dots? m))

            ;; Button labels and visibility
            (set! (.-textContent (:prev-btn refs)) (:prev-label m))
            (set! (.-textContent (:next-btn refs))
                  (if last? (:done-label m) (:next-label m)))
            (set! (.. (:prev-btn refs) -style -visibility)
                  (if first? "hidden" "visible"))

            ;; Close button label
            (.setAttribute (:close-btn refs) "aria-label"
                          (str (:skip-label m) " tour")))))))
  nil)

;; ── Forward declarations ────────────────────────────────────────────────────
(declare add-listeners!)
(declare remove-listeners!)

;; ── Throttled reposition ────────────────────────────────────────────────────
(defn- request-reposition!
  "Request a throttled reposition via requestAnimationFrame."
  [^js el]
  (when-not (du/getv el k-raf)
    (du/setv! el k-raf
              (js/requestAnimationFrame
               (fn []
                 (du/setv! el k-raf nil)
                 (when (and (.-isConnected el) (du/getv el k-layer))
                   (render! el))))))
  nil)

;; ── Open / close orchestration ──────────────────────────────────────────────
(defn- open-tour! [^js el]
  (render! el)
  (let [layer-refs (du/getv el k-layer)]
    (when layer-refs
      (add-listeners! el)
      ;; Scroll first step target into view
      (let [step-els (get-step-els el)
            m        (du/getv el k-model)
            idx      (model/clamp-step (:step m) (.-length step-els))]
        (when (pos? (.-length step-els))
          (let [step-m (read-step-model (aget step-els idx))]
            (when (:scroll-to? step-m)
              (scroll-target-into-view! (resolve-target (:target step-m)))))))
      ;; Focus trap after a tick to allow scroll
      (js/setTimeout (fn [] (activate-focus-trap! el layer-refs)) 50)))
  nil)

(defn- close-tour! [^js el]
  (remove-listeners! el)
  (render! el)
  nil)

;; ── Public methods ──────────────────────────────────────────────────────────
(defn- do-complete! [^js el]
  (let [total (.-length (get-step-els el))
        m     (or (du/getv el k-model) (read-model el))]
    (.removeAttribute el model/attr-open)
    (dispatch! el model/event-complete
               (model/complete-detail (inc (model/clamp-step (:step m) total)))
               false))
  nil)

(defn- do-skip! [^js el]
  (let [m     (or (du/getv el k-model) (read-model el))
        total (.-length (get-step-els el))]
    (.removeAttribute el model/attr-open)
    (dispatch! el model/event-skip
               (model/skip-detail (model/clamp-step (:step m) total))
               false))
  nil)

(defn- do-start! [^js el]
  (.setAttribute el model/attr-step "0")
  (.setAttribute el model/attr-open "")
  (dispatch! el model/event-start {} false)
  nil)

(defn- do-next! [^js el]
  (let [m     (or (du/getv el k-model) (read-model el))
        total (.-length (get-step-els el))
        step  (model/clamp-step (:step m) total)]
    (if (model/last-step? step total)
      (do-complete! el)
      (let [new-step (inc step)]
        (when (dispatch! el model/event-step-change
                         (model/step-change-detail new-step step) true)
          (.setAttribute el model/attr-step (str new-step))
          ;; Scroll new target
          (let [step-els (get-step-els el)
                step-m   (read-step-model (aget step-els new-step))]
            (when (:scroll-to? step-m)
              (scroll-target-into-view! (resolve-target (:target step-m)))))))))
  nil)

(defn- do-prev! [^js el]
  (let [m     (or (du/getv el k-model) (read-model el))
        total (.-length (get-step-els el))
        step  (model/clamp-step (:step m) total)]
    (when-not (model/first-step? step)
      (let [new-step (dec step)]
        (when (dispatch! el model/event-step-change
                         (model/step-change-detail new-step step) true)
          (.setAttribute el model/attr-step (str new-step))
          (let [step-els (get-step-els el)
                step-m   (read-step-model (aget step-els new-step))]
            (when (:scroll-to? step-m)
              (scroll-target-into-view! (resolve-target (:target step-m)))))))))
  nil)

(defn- do-go-to! [^js el n]
  (let [m     (or (du/getv el k-model) (read-model el))
        total (.-length (get-step-els el))
        old   (model/clamp-step (:step m) total)
        new-s (model/clamp-step n total)]
    (when (and (not= old new-s)
               (dispatch! el model/event-step-change
                          (model/step-change-detail new-s old) true))
      (.setAttribute el model/attr-step (str new-s))))
  nil)

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-keydown [^js el ^js e]
  (let [key (.-key e)]
    (cond
      (= key "Escape")
      (do (.preventDefault e) (.stopPropagation e) (do-skip! el))

      (or (= key "ArrowRight") (= key "ArrowDown"))
      (do (.preventDefault e) (do-next! el))

      (or (= key "ArrowLeft") (= key "ArrowUp"))
      (do (.preventDefault e) (do-prev! el))

      (= key "Tab")
      (cycle-focus! el e)))
  nil)

(defn- on-backdrop-click [^js el ^js e]
  ;; Only trigger skip when clicking the backdrop SVG itself
  (let [layer-refs (du/getv el k-layer)]
    (when (and layer-refs (= (.-target e) (:svg layer-refs)))
      (do-skip! el)))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [layer-refs (du/getv el k-layer)]
    (when layer-refs
      (let [^js layer  (:layer layer-refs)
            ^js shadow (.-shadowRoot layer)
            key-h      (fn [^js e] (on-keydown el e))
            click-h    (fn [^js e] (on-backdrop-click el e))
            scroll-h   (fn [] (request-reposition! el))
            prev-h     (fn [] (do-prev! el))
            next-h     (fn []
                         (let [m     (or (du/getv el k-model) (read-model el))
                               total (.-length (get-step-els el))
                               step  (model/clamp-step (:step m) total)]
                           (if (model/last-step? step total)
                             (do-complete! el)
                             (do-next! el))))
            close-h    (fn [] (do-skip! el))
            resize-obs (js/ResizeObserver.
                        (fn [] (request-reposition! el)))]
        (.addEventListener shadow "keydown" key-h true)
        (.addEventListener shadow "click" click-h)
        (js/window.addEventListener "scroll" scroll-h #js {:passive true})
        (js/window.addEventListener "resize" scroll-h #js {:passive true})
        (.addEventListener (:prev-btn layer-refs) "click" prev-h)
        (.addEventListener (:next-btn layer-refs) "click" next-h)
        (.addEventListener (:close-btn layer-refs) "click" close-h)
        (.observe resize-obs (.-body js/document))
        (du/setv! el k-handlers
                  {:key key-h :click click-h :scroll scroll-h
                   :prev prev-h :next next-h :close close-h
                   :shadow shadow})
        (du/setv! el k-resize-obs resize-obs))))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when hs
      (let [{:keys [key click scroll prev next close shadow]} hs
            layer-refs (du/getv el k-layer)]
        (when shadow
          (.removeEventListener shadow "keydown" key true)
          (.removeEventListener shadow "click" click))
        (js/window.removeEventListener "scroll" scroll)
        (js/window.removeEventListener "resize" scroll)
        (when layer-refs
          (.removeEventListener (:prev-btn layer-refs) "click" prev)
          (.removeEventListener (:next-btn layer-refs) "click" next)
          (.removeEventListener (:close-btn layer-refs) "click" close)))))
  (let [^js ro (du/getv el k-resize-obs)]
    (when ro (.disconnect ro)))
  (let [raf (du/getv el k-raf)]
    (when raf (js/cancelAnimationFrame raf)))
  (du/setv! el k-handlers nil)
  (du/setv! el k-resize-obs nil)
  (du/setv! el k-raf nil)
  nil)

;; ── MutationObserver for child steps ────────────────────────────────────────
(defn- install-mutation-observer! [^js el]
  (let [observer (js/MutationObserver.
                  (fn [_records _observer]
                    (when (du/getv el k-layer)
                      (render! el))))]
    (.observe observer el #js {:childList true})
    (du/setv! el k-observer observer))
  nil)

(defn- remove-mutation-observer! [^js el]
  (let [^js obs (du/getv el k-observer)]
    (when obs (.disconnect obs)))
  (du/setv! el k-observer nil)
  nil)

;; ── Host shadow DOM (minimal) ───────────────────────────────────────────────
(defn- init-host-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) host-style-text)
    (.appendChild root style)
    (.appendChild root slot)
    (du/setv! el k-refs {:root root}))
  nil)

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-host-dom! el)
          (du/getv el k-refs))))

;; ── Lifecycle callbacks ─────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (install-mutation-observer! el)
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-prev-open (:open? m))
    (when (:open? m)
      (open-tour! el)))
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (remove-mutation-observer! el)
  (let [layer-refs (du/getv el k-layer)]
    (when layer-refs
      (overlay/remove-layer! (:layer layer-refs))
      (du/setv! el k-layer nil)))
  (deactivate-focus-trap! el)
  nil)

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (and (not= old-val new-val) (.-isConnected el))
    (let [old-m    (du/getv el k-model)
          new-m    (read-model el)
          was-open (or (:open? old-m) false)
          now-open (:open? new-m)]
      (du/setv! el k-model new-m)
      (cond
        (and (not was-open) now-open) (open-tour! el)
        (and was-open (not now-open)) (close-tour! el)
        now-open                      (render! el))))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto "open"
                   #js {:get (fn []
                               (this-as ^js this
                                        (du/has-attr? this model/attr-open)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-open "")
                                          (.removeAttribute this model/attr-open))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "step"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-step (du/get-attr this model/attr-step))))
                        :set (fn [v]
                               (this-as ^js this
                                        (.setAttribute this model/attr-step (str (or v 0)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "connector"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-connector) "arrow")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-connector (str v))
                                          (.removeAttribute this model/attr-connector))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "totalSteps"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.-length (get-step-els this))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "prevLabel"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-prev-label) "Back")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-prev-label (str v))
                                          (.removeAttribute this model/attr-prev-label))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "nextLabel"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-next-label) "Next")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-next-label (str v))
                                          (.removeAttribute this model/attr-next-label))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "doneLabel"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-done-label) "Done")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-done-label (str v))
                                          (.removeAttribute this model/attr-done-label))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "skipLabel"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-skip-label) "Skip")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-skip-label (str v))
                                          (.removeAttribute this model/attr-skip-label))))
                        :enumerable true :configurable true}))

;; ── Install public methods ──────────────────────────────────────────────────
(defn- install-methods! [^js proto]
  (.defineProperty js/Object proto "start"
                   #js {:value (fn []
                                 (this-as ^js this (do-start! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "next"
                   #js {:value (fn []
                                 (this-as ^js this (do-next! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "prev"
                   #js {:value (fn []
                                 (this-as ^js this (do-prev! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "goTo"
                   #js {:value (fn [n]
                                 (this-as ^js this (do-go-to! this n)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "complete"
                   #js {:value (fn []
                                 (this-as ^js this (do-complete! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "skip"
                   #js {:value (fn []
                                 (this-as ^js this (do-skip! this)))
                        :writable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (connected! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (disconnected! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [attr-name old-val new-val]
            (this-as ^js this
                     (attribute-changed! this attr-name old-val new-val)
                     nil)))

    (install-property-accessors! (.-prototype klass))
    (install-methods! (.-prototype klass))
    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
