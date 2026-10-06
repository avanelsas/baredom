(ns baremirror.core
  "The functions that read and change the places of keyed nodes.
   A key is a string, and a container is a parent with an optional `:before` anchor."
  (:require [baremirror.plan :as plan]))

(def attr-key "data-x-key")

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
