(ns baredom.dev.x-trace-history.highlight
  "Live-element outline for the x-trace-history dock.

   When the user selects a record in the dock, this namespace draws a
   high-contrast magenta outline around the live DOM element
   identified by the record's componentId — a solid 3px border with a
   triple box-shadow ring (dark + white + magenta glow) that reads on
   both light and dark backgrounds, a faint magenta inner fill, and a
   one-shot attention pulse on each new selection. The overlay is a
   fixed-position layer mounted into the shared overlay root (which
   lives under the nearest <x-theme> ancestor, outside the dock's
   marked-internal subtree). It does NOT mutate the target element —
   no CSS injection into its shadow root, no inline style writes on
   the host.

   The layer is created once per dock lifetime and shown/hidden as
   selection changes. Scroll and resize listeners (installed by the
   dock) call reposition! through a single coalesced rAF so the box
   stays aligned as the page moves.

   Recorder-pollution contract: every write that maintains the overlay
   is either a native DOM call (.style.*, .appendChild, scrollIntoView)
   or routes through du/setv-untraced!, so the trace timeline is not
   polluted by the highlight machinery."
  (:require
   [baredom.utils.dom :as du]
   [baredom.utils.overlay :as overlay]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private outline-class "outline")
(def ^:private box-tag       "div")

;; Layered just below overlay.cljs's default overlay-root z-index (9999)
;; so panels (menus, tooltips) from real components still paint over the
;; highlight if they happen to share the same screen region.
(def ^:private layer-z-index 9998)

(def ^:private reduced-motion-query "(prefers-reduced-motion: reduce)")

(def ^:private layer-style-text
  "Shadow-root stylesheet for the outline layer.

   Design notes — visibility on both light and dark backgrounds:
   - `border` uses a saturated magenta (#ff2d8a) hard-coded rather
     than --x-color-accent so the outline visually stands apart from
     the dock's own accent and the page's regular UI accents. Magenta
     pops against the neutrals (white, black, dark navy, light grey)
     that real pages and components actually use as backgrounds.
   - A triple box-shadow ring sandwiches the border with a bright
     white halo and a dark outline. On a white background the dark
     outline is what your eye picks up; on a dark background the
     white halo is. Together they keep the highlight readable across
     every reasonable surface — including theme-switching surfaces
     where neither alone would suffice.
   - A faint magenta inner fill (background-color rgba) reinforces
     the selection — the eye reads a tinted *region*, not just a
     border. The fill is low-alpha so the underlying component stays
     visible (the point of debugging is to see the component, not to
     cover it).
   - A brief attention pulse on appearance (CSS animation, gated on
     prefers-reduced-motion) makes the highlight unmistakable when
     it first lands. The animation shorthand uses fill-mode `both`
     so the keyframe end-state sticks after the single play, and
     `restart-pulse!` re-arms it on every fresh selection (scroll /
     resize repositioning intentionally does not pulse).

   Layer mechanics (unchanged):
   - `:host { all: initial }` insulates the overlay from any
     inherited styles.
   - The box is `position: absolute` inside a `position: fixed`
     layer, so writes to top/left/width/height in client coordinates
     land at viewport positions matching the target's bounding-
     client-rect.
   - `pointer-events: none` ensures the outline never blocks clicks
     on the underlying component."
  (str
   ":host { all: initial; }\n"
   ".outline {\n"
   "  position: absolute;\n"
   "  pointer-events: none;\n"
   "  box-sizing: border-box;\n"
   "  border: 3px solid #ff2d8a;\n"
   "  border-radius: 3px;\n"
   "  background: rgba(255, 45, 138, 0.12);\n"
   "  box-shadow:\n"
   "    0 0 0 1px rgba(0, 0, 0, 0.85),\n"
   "    0 0 0 4px rgba(255, 255, 255, 0.85),\n"
   "    0 0 14px 2px rgba(255, 45, 138, 0.55);\n"
   "  display: none;\n"
   "}\n"
   "@media (prefers-reduced-motion: no-preference) {\n"
   "  .outline {\n"
   "    transition: top .08s linear, left .08s linear,\n"
   "                width .08s linear, height .08s linear;\n"
   "    animation: x-th-highlight-pulse .55s ease-out 1 both;\n"
   "  }\n"
   "  @keyframes x-th-highlight-pulse {\n"
   "    0%   { box-shadow:\n"
   "             0 0 0 1px rgba(0, 0, 0, 0.85),\n"
   "             0 0 0 4px rgba(255, 255, 255, 0.85),\n"
   "             0 0 0 12px rgba(255, 45, 138, 0.55); }\n"
   "    100% { box-shadow:\n"
   "             0 0 0 1px rgba(0, 0, 0, 0.85),\n"
   "             0 0 0 4px rgba(255, 255, 255, 0.85),\n"
   "             0 0 14px 2px rgba(255, 45, 138, 0.55); }\n"
   "  }\n"
   "}\n"))

;; ---------------------------------------------------------------------------
;; Reduced-motion probe
;; ---------------------------------------------------------------------------
;; matchMedia returns a *live* MediaQueryList whose `.matches` tracks
;; the current OS / browser preference and updates if the user flips
;; the setting at runtime. Resolve the query once at namespace load and
;; cache the live object; subsequent calls read `.matches` on it.

(defonce ^:private reduced-motion-mql
  (let [^js mm js/window.matchMedia]
    (when mm
      (.call mm js/window reduced-motion-query))))

(defn- prefers-reduced-motion?
  "True when the user has opted out of animation via OS / browser
   settings. Falls back to false if matchMedia is unavailable (non-
   browser test environments)."
  []
  (boolean (when reduced-motion-mql (.-matches reduced-motion-mql))))

;; ---------------------------------------------------------------------------
;; Layer construction (lazy)
;; ---------------------------------------------------------------------------

(defn- ensure-layer!
  "Create the outline layer on first call, cache layer + box on the dock
   host. Subsequent calls return the cached pair. The layer is mounted
   into the shared overlay root via overlay/make-layer! and stashed via
   du/setv-untraced! so this slot does not generate a trace record on
   the dock host's instance fields."
  [^js host]
  (or (du/getv host model/k-highlight-box)
      (let [^js layer  (overlay/make-layer! host layer-style-text layer-z-index)
            ^js shadow (.-shadowRoot layer)
            ^js box    (.createElement js/document box-tag)]
        (set! (.-className box) outline-class)
        (.appendChild shadow box)
        (du/setv-untraced! host model/k-highlight-layer layer)
        (du/setv-untraced! host model/k-highlight-box   box)
        box)))

;; ---------------------------------------------------------------------------
;; Geometry
;; ---------------------------------------------------------------------------

(defn- position-box!
  "Write the outline box's position to match the target's bounding-client-
   rect. Pure native DOM writes — no du/* calls, no recorder pollution."
  [^js box ^js target]
  (let [^js rect (.getBoundingClientRect target)]
    (set! (.. box -style -top)    (str (.-top    rect) "px"))
    (set! (.. box -style -left)   (str (.-left   rect) "px"))
    (set! (.. box -style -width)  (str (.-width  rect) "px"))
    (set! (.. box -style -height) (str (.-height rect) "px"))))

(defn- fully-visible?
  "True when every edge of the target's rect is inside the viewport.
   Used as the gate for maybe-scroll-into-view! — we never scroll
   unless at least one edge is offscreen."
  [^js target]
  (let [^js rect (.getBoundingClientRect target)
        vw       (.-innerWidth  js/window)
        vh       (.-innerHeight js/window)]
    (and (>= (.-top    rect) 0)
         (>= (.-left   rect) 0)
         (<= (.-bottom rect) vh)
         (<= (.-right  rect) vw))))

(defn- maybe-scroll-into-view!
  "Scroll the page when the target is not fully visible. Smooth scroll
   unless the user has opted out via prefers-reduced-motion, in which
   case we jump instantly. block/inline 'nearest' keeps the page from
   jumping further than necessary."
  [^js target]
  (when-not (fully-visible? target)
    (let [opts #js {:block    "nearest"
                    :inline   "nearest"
                    :behavior (if (prefers-reduced-motion?) "auto" "smooth")}]
      (.scrollIntoView target opts))))

(defn- restart-pulse!
  "Re-trigger the .outline attention pulse so each new selection lands
   with a visible flash. CSS animations only run when the rule first
   applies; setting style.animation to 'none' overrides the rule and
   reading offsetWidth forces a synchronous reflow so the browser
   observes the off state. Clearing the inline animation then lets the
   CSS rule reapply, restarting the animation. Called only from show!
   so reposition (scroll/resize) doesn't pulse on every tick. Bound
   `_` keeps the offsetWidth read live under Closure Advanced — it's
   the read itself, not the value, that triggers reflow."
  [^js box]
  (set! (.. box -style -animation) "none")
  (let [_ (.-offsetWidth box)]
    (set! (.. box -style -animation) "")))

;; ---------------------------------------------------------------------------
;; Public show / hide / reposition / remove
;; ---------------------------------------------------------------------------

(defn show!
  "Make the outline visible over `target`. Scrolls the target into view
   if any edge is offscreen, writes the box geometry, flips display to
   block, and re-triggers the attention pulse. Called only on a fresh
   selection (apply-highlight! routes same-cid re-selection through
   reposition!, which intentionally does not pulse)."
  [^js host ^js target]
  (let [^js box (ensure-layer! host)]
    (maybe-scroll-into-view! target)
    (position-box! box target)
    (set! (.. box -style -display) "block")
    (restart-pulse! box)))

(defn hide!
  "Hide the outline if a layer exists. Cheaper than removing — the
   layer is reused for the dock's lifetime."
  [^js host]
  (when-let [^js box (du/getv host model/k-highlight-box)]
    (set! (.. box -style -display) "none")))

(defn reposition!
  "Realign the outline to its current target without rescrolling. Called
   from the scroll/resize handlers (the page moved, the target may
   have moved with it). No-op when no record is currently highlighted.

   When the cid is stale (target was disconnected since the last
   selection), hide the box AND clear k-highlight-cid so subsequent
   scroll/resize ticks short-circuit on the initial `when-let` rather
   than re-walking the DOM only to discover the same miss again."
  [^js host]
  (when-let [cid (du/getv host model/k-highlight-cid)]
    (if-let [^js target (recorder/find-element-by-component-id (.-body js/document) cid)]
      (when-let [^js box (du/getv host model/k-highlight-box)]
        (position-box! box target)
        (set! (.. box -style -display) "block"))
      (do
        (hide! host)
        (du/setv-untraced! host model/k-highlight-cid nil)))))

(defn remove!
  "Tear down the overlay layer and clear every highlight slot on the
   host. Called from the dock's unmount!. Idempotent — a second call
   with no layer is a no-op."
  [^js host]
  (when-let [^js layer (du/getv host model/k-highlight-layer)]
    (overlay/remove-layer! layer))
  (du/setv-untraced! host model/k-highlight-layer nil)
  (du/setv-untraced! host model/k-highlight-box   nil)
  (du/setv-untraced! host model/k-highlight-cid   nil))
