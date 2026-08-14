(ns baredom.dev.pick
  "Click-to-select for a REPL. `pick!` puts the page into select mode and answers with a stable,
   readable id for whatever is clicked. `element-for` turns that id back into an element.

   The id is minted here rather than reused from x-trace-history's component id, which is an
   integer assigned inside the trace hook and only exists while the recorder is installed. Picking
   has to work with tracing off.

   Dev-only. Nothing here is reachable from a component."
  (:require [baredom.utils.dom :as du]
            [clojure.string :as str]))

;; The id lives on the element so it survives re-projection, which replaces nodes but does not
;; touch instance fields. setv-untraced! rather than setv!, because stamping a name onto an
;; element is bookkeeping and should not appear in a trace as a state change.
(def ^:private k-pick-id "__xPickId")

;; One atom: the per-tag counters that make an id, the last id picked so a REPL can read it after
;; the click, and the listener currently installed so `cancel!` can remove it. A second would need
;; justifying.
(defonce ^:private state (atom {:counts {} :last nil :listener nil}))

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
   button does not also press it."
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
