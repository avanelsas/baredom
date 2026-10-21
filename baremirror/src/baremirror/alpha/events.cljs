(ns baremirror.alpha.events
  "The DOM events of a page as messages for an application."
  (:require [baremirror.alpha.parts :as parts]
            [baremirror.alpha.places :as places]))

(defn- up-to-first
  "The items of `xs` up to and with the first that passes `pred`."
  [pred xs]
  (let [[before after] (split-with (complement pred) xs)]
    (concat before (take 1 after))))

(defn- element? [node]
  (instance? js/Element node))

(defn- path-to-listener
  "The elements on the path of `e`, from its target up to and with the node that listens."
  [^js e]
  (->> (array-seq (.composedPath e))
       (up-to-first (partial identical? (.-currentTarget e)))
       (filter element?)))

(defn read-origin
  "The origin of the event `e`: the keys on its path, outermost first, and the nearest part with
   its node. A part is looked for no further than the nearest keyed node, and the path exists
   only while the event is handled."
  [^js e]
  (let [nodes            (path-to-listener e)
        [part-name node] (some parts/part (up-to-first places/keyed nodes))]
    {:key-path (into [] (keep (comp first places/keyed)) (reverse nodes))
     :part     part-name
     :node     node}))

(defn- render-current!
  "Renders the view of the state that `state` holds.
   It renders again while a dispatch during the render has put another state there."
  [state view render!]
  (let [rendered @state]
    (render! (view rendered))
    (when-not (identical? rendered @state)
      (recur state view render!))))

(defn dispatcher
  "A function of a message. It puts `(step state message)` into the state holder `state`,
   renders the view of what `state` then holds, and returns the new state."
  [state step view render!]
  (fn [message]
    (let [new-state (swap! state step message)]
      (render-current! state view render!)
      new-state)))

(defn- attribute-value [^js node attr]
  (.getAttribute node attr))

(defn- shown
  "What `node` shows of `attrs`: the value of each attribute, in order."
  [^js node attrs]
  (mapv (partial attribute-value node) attrs))

(defn- nearest-key [origin _e]
  (peek (:key-path origin)))

(defn- message-of
  "The message that `entry` gives for `origin` and the DOM event `e`: its meaning, and its
   argument when there is one. With no function in the entry the argument is the nearest key."
  [entry origin e]
  (let [[meaning arg-of] (if (vector? entry) entry [entry nearest-key])
        arg              (arg-of origin e)]
    (cond-> [meaning] (some? arg) (conj arg))))

(defn- asked-value
  "The value that `source` asks of an attribute for the DOM event `e`: the detail field that a
   text names, or `source` itself. A missing detail or field asks for no attribute."
  [^js e source]
  (if (string? source) (some-> (.-detail e) (unchecked-get source)) source))

(defn- asked-text
  "The text that `asked` asks of each of its attributes for the DOM event `e`, in order."
  [asked e]
  (mapv (comp parts/attr-text (partial asked-value e)) (vals asked)))

(defn- dispatch-request!
  "Dispatches `message`, and cancels `e` when `node` then does not show what `asked` asks for."
  [dispatch! ^js node asked ^js e message]
  (let [wanted (asked-text asked e)]
    (dispatch! message)
    (when (not= wanted (shown node (keys asked)))
      (.preventDefault e))))

(defn- handle!
  "Dispatches the message of the DOM event `e`, when its part has an entry in `events`."
  [{:keys [dispatch! events requested-values]} ^js e]
  (let [{:keys [part node] :as origin} (read-origin e)]
    (when-some [entry (get events [(.-type e) part])]
      (let [message (message-of entry origin e)]
        (if-some [asked (get requested-values (.-type e))]
          (dispatch-request! dispatch! node asked e message)
          (dispatch! message))))))

(defn- on-capture! [options ^js e]
  (when-not (.-bubbles e)
    (handle! options e)))

(defn- on-bubble! [options ^js e]
  (when (.-bubbles e)
    (handle! options e)))

(defn- event-listeners
  "The capture and the bubble listener of `event-type`."
  [options event-type]
  [[event-type (partial on-capture! options) true]
   [event-type (partial on-bubble! options) false]])

(defn- listeners
  "An event type, a handler and whether it captures, for each listener of `options`."
  [{:keys [events] :as options}]
  (into []
        (comp (map first) (distinct) (mapcat (partial event-listeners options)))
        (keys events)))

(defn- add-listener! [^js root [event-type handler capture?]]
  (.addEventListener root event-type handler capture?))

(defn- remove-listener! [^js root [event-type handler capture?]]
  (.removeEventListener root event-type handler capture?))

(defn listen!
  "Listens on `root` for each event type in `:events`, which maps an event type and a part name
   to the meaning of a message for `:dispatch!`, and returns a function that removes the
   listeners. `:requested-values` maps a request event to what it asks of each attribute, and
   `listen!` cancels it when its part does not show that after the dispatch.
   An event that does not bubble reaches `root` in the capture phase, before the listeners below."
  [^js root options]
  (let [spec (listeners options)]
    (run! (partial add-listener! root) spec)
    (partial run! (partial remove-listener! root) spec)))
