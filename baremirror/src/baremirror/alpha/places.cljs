(ns baremirror.alpha.places
  "The places of keyed nodes in the document. A key is a string, and a container is a parent
   with an optional `:before` anchor."
  (:require [baremirror.alpha.plan :as plan]))

(def attr-key "data-x-key")

(def ^:private selector-keyed (str "[" attr-key "]"))

(defn keyed
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

(defn- holding-keys
  "The keys of the keyed nodes that `parent` is or is in, nearest first."
  [^js parent]
  (loop [^js node (.closest parent selector-keyed) ks []]
    (if (some? node)
      (recur (some-> (.-parentElement node) (.closest selector-keyed))
             (conj ks (.getAttribute node attr-key)))
      ks)))

(defn- holders-of
  "The keys that hold each container of `placements`, nearest first, by container."
  [containers placements]
  (into {}
        (map (juxt identity (comp holding-keys :parent containers)))
        (distinct (map :in placements))))

(defn- depth
  "How many containers hold `container` through the keys placed in them. `holders` maps a
   container to the keys that hold it, nearest first, and `homes` maps a placed key to its
   container. Throws when a key is placed inside its own node."
  [holders homes container]
  (loop [c container n 0 seen #{container}]
    (if-some [home (some homes (holders c))]
      (if (contains? seen home)
        (throw (ex-info "perform!: a key is placed inside its own node." {:container container}))
        (recur home (inc n) (conj seen home)))
      n)))

(defn- parents-first
  "The `placements`, with those of a container after those of the containers that hold it.
   The order within a container stays. `holders` is what `holders-of` gives."
  [holders placements]
  (let [homes  (into {} (map (juxt :key :in)) placements)
        depths (into {} (map (juxt identity (partial depth holders homes))) (keys holders))]
    (sort-by (comp depths :in) placements)))

(defn perform!
  "Performs `plan` on what `read-places` returned, with `make-node` making the node of a new key.
   Returns the node of each key that remains. Throws before any write when a key is placed
   inside its own node."
  [{:keys [containers nodes]} {removed :remove placements :place} make-node]
  (let [ordered   (parents-first (holders-of containers placements) placements)
        remaining (with-new-nodes! make-node (apply dissoc nodes removed) placements)]
    ;; A node that leaves a removed node moves while both are in the document, so moveBefore applies.
    (run! (partial place! containers remaining) ordered)
    (run! (comp remove-node! nodes) removed)
    remaining))

(defn sync!
  "Brings `containers` to the `wanted` places, with `make-node` making the node of a new key.
   Returns the node of each key that remains. Throws before any write when a key is placed
   inside its own node."
  [containers wanted make-node]
  (let [reading (read-places containers)]
    (perform! reading (plan/plan (:places reading) wanted) make-node)))
