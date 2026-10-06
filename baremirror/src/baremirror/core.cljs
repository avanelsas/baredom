(ns baremirror.core
  "The functions that bring the document to what an application wants: the places of keyed nodes,
   attributes and text. A key is a string, and a container is a parent with an optional `:before`
   anchor."
  (:require [baremirror.plan :as plan]
            [baremirror.unit :as unit]))

(def attr-key "data-x-key")

(def attr-part "data-x-part")

(def hold-key
  "The key under which an element offers the hold of its render."
  (js/Symbol.for "x-render-hold"))

(defn- keyed
  "The key of `node` and the node, when it has a key."
  [^js node]
  (when-some [k (.getAttribute node attr-key)]
    [k node]))

(defn- keyed-children
  "The keys and nodes of the keyed children of a container's parent, in document order."
  [{:keys [^js parent]}]
  (into [] (keep keyed) (array-seq (.-children parent))))

(defn read-places
  "The current places of `containers`, with the node of each key.
   A parent belongs to one container."
  [containers]
  (let [children (update-vals containers keyed-children)]
    {:containers containers
     :places     (update-vals children (partial mapv first))
     :nodes      (into {} cat (vals children))}))

(defn release!
  "Gives `node` up: it keeps its place in the document and loses its key."
  [^js node]
  (.removeAttribute node attr-key))

(defn- remove-node! [^js node]
  (release! node)
  (.remove node))

(defn- keyed-node!
  "A new node for `k`, made by `make-node` and marked with its key."
  [make-node k]
  (doto ^js (make-node k)
    (.setAttribute attr-key k)))

(defn- with-new-nodes!
  "The `nodes` with a new node for each key of `placements` that has none."
  [make-node nodes placements]
  (into nodes
        (comp (map :key)
              (remove nodes)
              (map (juxt identity (partial keyed-node! make-node))))
        placements))

(defn- can-move? [^js parent ^js node]
  (and (.-moveBefore parent) (.-isConnected parent) (.-isConnected node)))

(defn- put-before!
  "Puts `node` into `parent` before `before`, or at the end."
  [^js parent ^js node ^js before]
  (if (can-move? parent node)
    (.moveBefore parent node before)
    (.insertBefore parent node before)))

(defn- place!
  "Performs one placement with the nodes in `nodes`."
  [containers nodes {:keys [key in before]}]
  (let [{:keys [parent] anchor :before} (containers in)]
    (put-before! parent (nodes key) (if (some? before) (nodes before) anchor))))

(defn perform!
  "Performs `plan` on what `read-places` returned, with `make-node` making the node of a new key.
   Returns the node of each key that remains."
  [{:keys [containers nodes]} {removed :remove placements :place} make-node]
  (let [remaining (with-new-nodes! make-node (apply dissoc nodes removed) placements)]
    (run! (comp remove-node! nodes) removed)
    (run! (partial place! containers remaining) placements)
    remaining))

(defn sync!
  "Brings `containers` to the `wanted` places, with `make-node` making the node of a new key.
   Returns the node of each key that remains."
  [containers wanted make-node]
  (let [reading (read-places containers)]
    (perform! reading (plan/plan (:places reading) wanted) make-node)))

(defn with-one-render!
  "Calls `f` with `el` while `el` holds its attribute changes, when it offers that."
  [^js el f]
  (if-some [hold (unchecked-get el hold-key)]
    (.call hold el (partial f el))
    (f el))
  nil)

(defn- attr-text
  "The text of an attribute for `v`, or nil when `v` asks for no attribute."
  [v]
  (cond
    (true? v)                ""
    (or (false? v) (nil? v)) nil
    (keyword? v)             (name v)
    :else                    (str v)))

(defn- set-attr!
  "Brings one attribute of `el` to `v`, where it differs."
  [^js el [k v]]
  (let [attr (name k)
        text (attr-text v)]
    (when (not= text (.getAttribute el attr))
      (if (some? text)
        (.setAttribute el attr text)
        (.removeAttribute el attr)))))

(defn- set-each-attr! [attrs ^js el]
  (run! (partial set-attr! el) attrs))

(defn set-attrs!
  "Brings the attributes that `attrs` names to their values, where they differ: true sets an empty
   attribute, false and nil remove it, a keyword gives its name. `attrs` is a map or a sequence
   of pairs, written in its order."
  [^js el attrs]
  (with-one-render! el (partial set-each-attr! attrs)))

(defn- only-text-node
  "The text node of `el`, when it is the only child."
  [^js el]
  (when-some [^js node (.-firstChild el)]
    (when (and (= (.-TEXT_NODE js/Node) (.-nodeType node)) (nil? (.-nextSibling node)))
      node)))

(defn set-text!
  "Makes `text` the whole content of `el`, and writes only where it differs."
  [^js el text]
  (let [wanted (str text)]
    (if-some [^js node (only-text-node el)]
      (when (not= wanted (.-data node))
        (set! (.-data node) wanted))
      (set! (.-textContent el) wanted)))
  nil)

(declare make-node!)

(defn- append-child!
  "Appends one child of a shell to `el`: a node for a shell, text for anything else."
  [^js el child]
  (.append el (if (vector? child) (make-node! child) (str child))))

(defn make-node!
  "Makes one detached HTML element from `shell`. The shell holds no holes."
  [shell]
  (let [[tag attrs children] (unit/pieces shell)
        el                   (.createElement js/document (name tag))]
    (set-attrs! el attrs)
    (run! (partial append-child! el) children)
    el))

(defn- part
  "The part name of `node` and the node, when it is a part."
  [^js node]
  (when-some [part-name (.getAttribute node attr-part)]
    [part-name node]))

(defn- unkeyed-children [^js node]
  (remove keyed (array-seq (.-children node))))

(defn read-parts
  "The parts of `node` by name: `node` itself and the parts below it.
   A keyed node below `node` is left out with everything in it."
  [^js node]
  (into {} (keep part) (tree-seq (constantly true) unkeyed-children node)))
