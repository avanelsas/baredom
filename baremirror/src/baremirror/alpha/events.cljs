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

(defn- dispatch-request!
  "Dispatches `message`, and cancels `e` when `node` shows the same of `attrs` after as before."
  [dispatch! ^js node attrs ^js e message]
  (let [before (shown node attrs)]
    (dispatch! message)
    (when (= before (shown node attrs))
      (.preventDefault e))))

(defn- handle!
  "Dispatches the message of the DOM event `e`, when its part has an entry in `events`."
  [{:keys [dispatch! events requests]} ^js e]
  (let [{:keys [part node] :as origin} (read-origin e)]
    (when-some [entry (get events [(.-type e) part])]
      (let [message (message-of entry origin e)]
        (if-some [attrs (get requests (.-type e))]
          (dispatch-request! dispatch! node attrs e message)
          (dispatch! message))))))

(defn- add-listener! [^js root options event-type]
  (.addEventListener root event-type (partial handle! options)))

(defn listen!
  "Adds one listener to `root` for each event type in `:events`, which maps an event type and a
   part name to the meaning of a message for `:dispatch!`. `:requests` maps an event type to the
   attributes it asks to change: such an event is cancelled when its part shows the same of
   them after the dispatch as before."
  [^js root {:keys [events] :as options}]
  (run! (partial add-listener! root options) (into #{} (map first) (keys events))))
