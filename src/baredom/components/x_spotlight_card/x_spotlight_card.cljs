(ns baredom.components.x-spotlight-card.x-spotlight-card
  (:require
   [goog.object :as gobj]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-spotlight-card.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────

(def ^:private k-root        "__xSpotlightCardRoot")
(def ^:private k-card        "__xSpotlightCardCard")
(def ^:private k-style       "__xSpotlightCardStyle")
(def ^:private k-model       "__xSpotlightCardModel")
(def ^:private k-handlers    "__xSpotlightCardHandlers")
(def ^:private k-mq          "__xSpotlightCardMq")
(def ^:private k-mq-listener "__xSpotlightCardMqL")
(def ^:private k-initialized "__xSpotlightCardInit")

;; ── CSS custom property names ──────────────────────────────────────────────

(def ^:private css-var-color     "--x-spotlight-card-color")
(def ^:private css-var-intensity "--x-spotlight-card-intensity")
(def ^:private css-var-size      "--x-spotlight-card-size")
(def ^:private css-var-x         "--x-spotlight-card-x")
(def ^:private css-var-y         "--x-spotlight-card-y")

;; ── Reduced-motion detection ────────────────────────────────────────────────

(defn- motion-ok? []
  (not (.-matches (.matchMedia js/window "(prefers-reduced-motion: reduce)"))))

;; ── Style text ──────────────────────────────────────────────────────────────

(defn- style-text []
  "
  :host {
    display: block;
    color-scheme: light dark;

    --x-spotlight-card-background: var(--x-color-surface, rgba(255, 255, 255, 0.92));
    --x-spotlight-card-color-fg: var(--x-color-text, #111827);
    --x-spotlight-card-border-color: var(--x-color-border, rgba(17, 24, 39, 0.12));
    --x-spotlight-card-shadow: var(--x-shadow-md, 0 10px 24px rgba(15, 23, 42, 0.10));

    --x-spotlight-card-padding-none: 0;
    --x-spotlight-card-padding-sm: 0.5rem;
    --x-spotlight-card-padding-md: 1rem;
    --x-spotlight-card-padding-lg: 1.5rem;

    --x-spotlight-card-radius-none: 0;
    --x-spotlight-card-radius-sm: var(--x-radius-sm, 0.375rem);
    --x-spotlight-card-radius-md: var(--x-radius-md, 0.75rem);
    --x-spotlight-card-radius-lg: var(--x-radius-lg, 1rem);
    --x-spotlight-card-radius-xl: 1.5rem;

    --x-spotlight-card-transition-duration: var(--x-transition-duration, 200ms);
    --x-spotlight-card-transition-timing: ease;

    --x-spotlight-card-x: 50%;
    --x-spotlight-card-y: 50%;
  }

  @media (prefers-color-scheme: dark) {
    :host {
      --x-spotlight-card-background: var(--x-color-surface, rgba(15, 23, 42, 0.88));
      --x-spotlight-card-color-fg: var(--x-color-text, #e5e7eb);
      --x-spotlight-card-border-color: var(--x-color-border, rgba(148, 163, 184, 0.24));
      --x-spotlight-card-shadow: var(--x-shadow-md, 0 16px 32px rgba(0, 0, 0, 0.32));
    }
  }

  .card {
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    display: block;
    min-width: 0;
    width: 100%;
    max-width: 100%;
    background: var(--x-spotlight-card-background);
    color: var(--x-spotlight-card-color-fg);
    border: 1px solid transparent;
    border-radius: var(--x-spotlight-card-radius-lg);
    transition:
      background var(--x-spotlight-card-transition-duration) var(--x-spotlight-card-transition-timing),
      box-shadow var(--x-spotlight-card-transition-duration) var(--x-spotlight-card-transition-timing),
      border-color var(--x-spotlight-card-transition-duration) var(--x-spotlight-card-transition-timing);
  }

  .card[data-variant='elevated'] {
    box-shadow: var(--x-spotlight-card-shadow);
  }

  .card[data-variant='bordered'] {
    border-color: var(--x-spotlight-card-border-color);
    box-shadow: none;
  }

  .card[data-padding='none'] { padding: var(--x-spotlight-card-padding-none); }
  .card[data-padding='sm']   { padding: var(--x-spotlight-card-padding-sm); }
  .card[data-padding='md']   { padding: var(--x-spotlight-card-padding-md); }
  .card[data-padding='lg']   { padding: var(--x-spotlight-card-padding-lg); }

  .card[data-radius='none'] { border-radius: var(--x-spotlight-card-radius-none); }
  .card[data-radius='sm']   { border-radius: var(--x-spotlight-card-radius-sm); }
  .card[data-radius='md']   { border-radius: var(--x-spotlight-card-radius-md); }
  .card[data-radius='lg']   { border-radius: var(--x-spotlight-card-radius-lg); }
  .card[data-radius='xl']   { border-radius: var(--x-spotlight-card-radius-xl); }

  .spotlight {
    position: absolute;
    inset: 0;
    pointer-events: none;
    border-radius: inherit;
    opacity: 0;
    transition: opacity var(--x-spotlight-card-transition-duration) var(--x-spotlight-card-transition-timing);
    background: radial-gradient(
      var(--x-spotlight-card-size, 200px) circle at
      var(--x-spotlight-card-x) var(--x-spotlight-card-y),
      color-mix(in srgb,
        var(--x-spotlight-card-color, transparent)
        calc(var(--x-spotlight-card-intensity, 0.18) * 100%),
        transparent),
      transparent 70%);
  }

  .card[data-active='true'] > .spotlight,
  .card[data-static='true'] > .spotlight {
    opacity: 1;
  }

  .content {
    position: relative;
  }

  @media (prefers-reduced-motion: reduce) {
    .card { transition: none; }
    .spotlight { transition: none; opacity: 1; }
  }
  ")

;; ── Shadow DOM construction ─────────────────────────────────────────────────

(defn- create-style-node []
  (let [node (.createElement js/document "style")]
    (set! (.-textContent node) (style-text))
    node))

(defn- create-card-node []
  (let [node (.createElement js/document "div")]
    (.setAttribute node "part" "card")
    (set! (.-className node) "card")
    node))

(defn- create-spotlight-node []
  (let [node (.createElement js/document "div")]
    (.setAttribute node "part" "spotlight")
    (.setAttribute node "aria-hidden" "true")
    (set! (.-className node) "spotlight")
    node))

(defn- create-content-node []
  (let [node (.createElement js/document "div")]
    (.setAttribute node "part" "content")
    (set! (.-className node) "content")
    node))

(defn- init-shadow-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (create-style-node)
        card      (create-card-node)
        spotlight (create-spotlight-node)
        content   (create-content-node)
        slot      (.createElement js/document "slot")]
    (.appendChild content slot)
    (.appendChild card spotlight)
    (.appendChild card content)
    (.appendChild root style)
    (.appendChild root card)
    (du/setv! el k-root  root)
    (du/setv! el k-style style)
    (du/setv! el k-card  card)
    root))

;; ── Read inputs ─────────────────────────────────────────────────────────────

(defn- read-inputs [^js el]
  {:variant    (du/get-attr el model/attr-variant)
   :radius     (du/get-attr el model/attr-radius)
   :padding    (du/get-attr el model/attr-padding)
   :color      (du/get-attr el model/attr-color)
   :intensity  (du/get-attr el model/attr-intensity)
   :size       (du/get-attr el model/attr-size)
   :static?    (du/has-attr? el model/attr-static)
   :motion-ok? (motion-ok?)})

;; ── Pointer handlers ────────────────────────────────────────────────────────

(defn- on-pointerenter [^js el ^js _e]
  (when-let [^js card (du/getv el k-card)]
    (.setAttribute card "data-active" "true"))
  nil)

(defn- on-pointerleave [^js el ^js _e]
  (when-let [^js card (du/getv el k-card)]
    (.removeAttribute card "data-active"))
  nil)

(defn- on-pointermove [^js el ^js e]
  (let [^js rect (.getBoundingClientRect el)
        w        (.-width rect)
        h        (.-height rect)]
    (when (and (pos? w) (pos? h))
      (let [x  (* (/ (- (.-clientX e) (.-left rect)) w) 100)
            y  (* (/ (- (.-clientY e) (.-top rect))  h) 100)
            ^js style (.-style el)]
        (.setProperty style css-var-x (str x "%"))
        (.setProperty style css-var-y (str y "%")))))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────

(defn- listening? [^js el]
  (some? (du/getv el k-handlers)))

(defn- add-listeners! [^js el]
  (when-not (listening? el)
    (let [enter-fn (fn handle-pointerenter [^js e] (on-pointerenter el e))
          leave-fn (fn handle-pointerleave [^js e] (on-pointerleave el e))
          move-fn  (fn handle-pointermove  [^js e] (on-pointermove  el e))
          handlers #js {:enter enter-fn :leave leave-fn :move move-fn}]
      (.addEventListener el "pointerenter" enter-fn #js {:passive true})
      (.addEventListener el "pointerleave" leave-fn #js {:passive true})
      (.addEventListener el "pointermove"  move-fn  #js {:passive true})
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [^js handlers (du/getv el k-handlers)]
    (when-let [f (gobj/get handlers "enter")]
      (.removeEventListener el "pointerenter" f))
    (when-let [f (gobj/get handlers "leave")]
      (.removeEventListener el "pointerleave" f))
    (when-let [f (gobj/get handlers "move")]
      (.removeEventListener el "pointermove"  f))
    (du/setv! el k-handlers nil))
  nil)

;; ── Apply model ─────────────────────────────────────────────────────────────

(defn- reflect-card-state! [^js card m]
  (.setAttribute card "data-variant" (:variant m))
  (.setAttribute card "data-radius"  (:radius m))
  (.setAttribute card "data-padding" (:padding m))
  (if (:static? m)
    (.setAttribute    card "data-static" "true")
    (.removeAttribute card "data-static"))
  ;; data-active is owned by pointer handlers — never set/cleared here.
  nil)

(defn- reflect-host-style! [^js el m]
  (let [^js style (.-style el)]
    (.setProperty style css-var-color     (:color-css m))
    (.setProperty style css-var-intensity (:intensity-css m))
    (.setProperty style css-var-size      (:size-css m)))
  nil)

(defn- apply-model! [^js el m]
  (when-let [^js card (du/getv el k-card)]
    (reflect-card-state! card m))
  (reflect-host-style! el m)
  ;; Pointer tracking is disabled when the spotlight is forced static.
  (if (:static? m)
    (do
      (remove-listeners! el)
      ;; Clear any leftover active state so the static spotlight is what shows.
      (when-let [^js card (du/getv el k-card)]
        (.removeAttribute card "data-active")))
    (add-listeners! el))
  (du/setv! el k-model m)
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (model/derive-state (read-inputs el))
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Reduced-motion observer ─────────────────────────────────────────────────

(defn- add-mq-listener! [^js el]
  (when-not (du/getv el k-mq)
    (let [^js mq  (.matchMedia js/window "(prefers-reduced-motion: reduce)")
          handler (fn handle-motion-change [^js _e] (update-from-attrs! el))]
      (.addEventListener mq "change" handler)
      (du/setv! el k-mq mq)
      (du/setv! el k-mq-listener handler))))

(defn- remove-mq-listener! [^js el]
  (when-let [^js mq (du/getv el k-mq)]
    (when-let [handler (du/getv el k-mq-listener)]
      (.removeEventListener mq "change" handler))
    (du/setv! el k-mq nil)
    (du/setv! el k-mq-listener nil))
  nil)

;; ── Lifecycle ───────────────────────────────────────────────────────────────

(defn- init-element! [^js el]
  (when-not (du/initialized? el k-initialized)
    (init-shadow-dom! el)
    (du/mark-initialized! el k-initialized))
  (update-from-attrs! el)
  el)

(defn- connected! [^js el]
  (init-element! el)
  (add-mq-listener! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (remove-mq-listener! el)
  (when-let [^js card (du/getv el k-card)]
    (.removeAttribute card "data-active")))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (du/initialized? el k-initialized) (not= old-val new-val))
    (update-from-attrs! el)))

;; ── Property accessors ──────────────────────────────────────────────────────

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Registration ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
