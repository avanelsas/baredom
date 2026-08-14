(ns baredom.dev.pick
  "Pointing at elements from a REPL, in both directions. `pick!` puts the page into select mode and
   answers with a stable, readable id for whatever is clicked. `show!` takes an id and outlines
   where it is. `element-for` turns an id back into an element.

   The id is minted here rather than reused from x-trace-history's component id, which is an
   integer assigned inside the trace hook and only exists while the recorder is installed. Picking
   has to work with tracing off.

   Dev-only. Nothing here is reachable from a component."
  (:require [baredom.utils.dom :as du]
            [baredom.utils.overlay :as overlay]
            [clojure.string :as str]))

;; The id lives on the element so it survives re-projection, which replaces nodes but does not
;; touch instance fields. setv-untraced! rather than setv!, because stamping a name onto an
;; element is bookkeeping and should not appear in a trace as a state change.
(def ^:private k-pick-id "__xPickId")

;; One atom: the per-tag counters that make an id, the last id picked so a REPL can read it after
;; the click, the click listener so `cancel!` can remove it, the outline box once something has
;; been shown, and the scroll listener that hides it again. A second would need justifying.
(defonce ^:private state
  (atom {:counts {} :last nil :listener nil :box nil :scroll-listener nil}))

(defn- tag-of
  "The lowercase tag name of `el`."
  [^js el]
  (str/lower-case (.-tagName el)))

(defn- elements-under
  "Every element under `root`, descending into open shadow roots."
  [^js root]
  (mapcat (fn [^js el]
            (cons el (concat (elements-under el)
                             (when-let [^js shadow (.-shadowRoot el)]
                               (elements-under shadow)))))
          (array-seq (.-children root))))

(defn- number-tag!
  "Stamp an id on every unstamped element whose tag is `tag`, in document order, continuing from
   the count already given out. Numbering the whole tag at once is what makes the number mean
   something: x-table-cell#7 is the seventh cell on the page, not the seventh one clicked."
  [tag]
  (let [taken (get-in @state [:counts tag] 0)
        fresh (into []
                    (comp (filter #(= tag (tag-of %)))
                          (remove #(du/getv % k-pick-id)))
                    (elements-under js/document.body))]
    (doseq [[i ^js el] (map-indexed vector fresh)]
      (du/setv-untraced! el k-pick-id (str tag "#" (+ taken i 1))))
    (swap! state assoc-in [:counts tag] (+ taken (count fresh)))))

(defn id-of
  "The id of `el`. The first time any element of its tag is asked for, every one then on the page
   is numbered in document order."
  [^js el]
  (or (du/getv el k-pick-id)
      (do (number-tag! (tag-of el))
          (du/getv el k-pick-id))))

(defn element-for
  "The element carrying `id`, or nil. Walks the document on every call, so an id always resolves
   to whatever is there now rather than to a node that has since been replaced."
  [id]
  (some (fn [^js el] (when (= id (du/getv el k-pick-id)) el))
        (elements-under js/document.body)))

(defn- custom-element?
  "True when `el` is a custom element. A custom element's tag name must contain a hyphen, which is
   what separates one from a div, a span, or the slot a click actually lands on."
  [^js el]
  (str/includes? (tag-of el) "-"))

(defn- clicked-element
  "The element `e` picks out: the innermost custom element on its composed path, or the deepest
   element when the click landed outside any component. The composed path runs deepest first, so
   the first custom element on it is the innermost one containing the click."
  [^js e]
  (let [path (filter #(instance? js/Element %) (array-seq (.composedPath e)))]
    (or (first (filter custom-element? path))
        (first path))))

(defn cancel!
  "Leave select mode without picking anything."
  []
  (when-let [listener (:listener @state)]
    (.removeEventListener js/document "click" listener true))
  (swap! state assoc :listener nil)
  nil)

(defn pick!
  "Enter select mode and answer with the id of the next element clicked. Returns a promise, and
   records the id so `picked` can read it afterwards. The click is swallowed, so selecting a
   button does not also press it. A second call abandons the first, whose promise never settles."
  []
  (cancel!)
  ;; Clear the last pick on entering select mode, so `picked` answers nil until a click lands
  ;; rather than handing back the previous answer as though it were this one.
  (swap! state assoc :last nil)
  (js/Promise.
   (fn [resolve _reject]
     (let [listener (fn handle [^js e]
                      (.preventDefault e)
                      (.stopPropagation e)
                      (.removeEventListener js/document "click" handle true)
                      (swap! state assoc :listener nil)
                      (let [id (some-> (clicked-element e) id-of)]
                        (swap! state assoc :last id)
                        (resolve id)))]
       (swap! state assoc :listener listener)
       (.addEventListener js/document "click" listener true)))))

(defn picked
  "The id picked most recently, for reading after the click at a REPL. Nil while a pick is waiting
   for its click, so an early call says nothing rather than repeating the last answer."
  []
  (:last @state))

;; Showing, the other direction ----------------------------------------------------------
;; An id tells you the order of a thing, never where it is. The outline is drawn on a shared
;; overlay layer, which escapes every stacking context and takes no pointer events, so showing
;; something never changes what a click would hit.

(def ^:private outline-css
  "Deliberately not themed. This is a dev overlay, not a component, and it has to stay legible
   over whatever it is drawn on top of."
  "[part=box] { position: fixed; box-sizing: border-box; display: none;
                border: 2px solid #e11d48; border-radius: 2px;
                background: rgba(225, 29, 72, 0.08); }")

;; Just under the maximum, so the outline sits above every stacking context on the page.
(def ^:private outline-z-index 2147483000)

(defn- ensure-box!
  "The one box this namespace draws with. Created on first use, and `near` decides only which
   overlay root it is created in, since the box is shared from then on."
  [^js near]
  (or (:box @state)
      (let [^js layer (overlay/make-layer! near outline-css outline-z-index)
            ^js box   (.createElement js/document "div")]
        (.setAttribute box "part" "box")
        (.appendChild (.-shadowRoot layer) box)
        (swap! state assoc :box box)
        box)))

(defn- offscreen?
  "True when `rect` lies outside the viewport, where an outline would be drawn and never seen."
  [^js rect]
  (or (neg? (.-bottom rect))
      (neg? (.-right rect))
      (> (.-top rect) (.-innerHeight js/window))
      (> (.-left rect) (.-innerWidth js/window))))

(defn- scroll-position
  "Where the page is scrolled to, as a pair."
  []
  [(.-scrollX js/window) (.-scrollY js/window)])

(defn hide!
  "Take the outline off, and stop watching for the scroll that would strand it."
  []
  (when-let [^js box (:box @state)]
    (set! (.. box -style -display) "none"))
  (when-let [listener (:scroll-listener @state)]
    (.removeEventListener js/window "scroll" listener true)
    (.removeEventListener js/window "resize" listener))
  (swap! state assoc :scroll-listener nil)
  nil)

(defn- hide-when-page-moves!
  "Hide the outline once the page has actually moved from `from`. The box is positioned once, so
   leaving it up would point it confidently at whatever has moved into that spot.

   Comparing positions rather than reacting to the event matters: scrolling an offscreen element
   into view queues a scroll event that arrives after this listener is installed, and reacting to
   it would hide the outline a frame after drawing it."
  [from]
  (let [listener (fn [_] (when (not= from (scroll-position)) (hide!)))]
    (swap! state assoc :scroll-listener listener)
    (.addEventListener js/window "scroll" listener true)
    (.addEventListener js/window "resize" listener)))

(defn show!
  "Outline the element `id` names, scrolling it into view when it is off screen. The outline goes
   away on the next scroll or resize rather than following the element. Returns the id, or nil
   when the id resolves to nothing."
  [id]
  (when-let [^js el (element-for id)]
    (hide!)
    (when (offscreen? (.getBoundingClientRect el))
      (.scrollIntoView el #js {:block "center" :inline "center"}))
    (let [^js box  (ensure-box! el)
          ^js rect (.getBoundingClientRect el)]
      (set! (.. box -style -left)    (str (.-left rect) "px"))
      (set! (.. box -style -top)     (str (.-top rect) "px"))
      (set! (.. box -style -width)   (str (.-width rect) "px"))
      (set! (.. box -style -height)  (str (.-height rect) "px"))
      (set! (.. box -style -display) "block")
      (hide-when-page-moves! (scroll-position))
      id)))
