(ns baremirror.alpha.places
  "The places of keyed nodes in the document. A key is a string, and a container is a parent
   with an optional `:before` anchor, the key of a node and an optional part in it, or a part
   alone, which gives the node of every key a container."
  (:require [baremirror.alpha.plan :as plan]
            [baremirror.alpha.template :as template]))

(def attr-key "data-x-key")

(def ^:private selector-keyed (str "[" attr-key "]"))

(defn keyed
  "The key of `node` and the node, when it has a key."
  [^js node]
  (when-some [k (.getAttribute node attr-key)]
    [k node]))

(defn- part-alone?
  "True when `container` names a part alone, the part of the node of every key."
  [{:keys [parent part] k :key}]
  (and (nil? parent) (nil? k) (some? part)))

(defn- checked-container
  "The `container`. Throws unless it names exactly one of `:parent`, `:key` and a part alone,
   or when it has `:before` and no `:parent`."
  [{:keys [parent before part] k :key :as container}]
  (cond
    (or (and (some? parent) (some? k)) (and (nil? parent) (nil? k) (nil? part)))
    (throw (ex-info "read-places: a container names a :parent, a :key, or a :part alone."
                    {:container container}))
    (and (nil? parent) (some? before))
    (throw (ex-info "read-places: a container named by a key or a part alone takes no :before."
                    {:container container}))
    :else container))

(defn- part-alone
  "The part that one of `containers` names alone, or nil. Throws when more than one does."
  [containers]
  (let [parts (into [] (comp (filter part-alone?) (map :part)) (vals containers))]
    (when (next parts)
      (throw (ex-info "read-places: only one container names a part alone." {:parts parts})))
    (first parts)))

(defn- key-container [part k]
  {:key k :part part})

(defn- with-part-containers
  "The `containers` with a container in the part `part` for each of `ks` that has none.
   The `containers` as they are when `part` is nil."
  [part containers ks]
  (if (nil? part)
    containers
    (into containers
          (comp (remove (partial contains? containers))
                (map (juxt identity (partial key-container part))))
          ks)))

(defn- clash
  "The key `k` when it is the name of a container of `containers` not named by `k`, or nil."
  [containers k]
  (when-some [container (get containers k)]
    (when (not= k (:key container)) k)))

(defn- part-selector [part]
  (let [named (str "[" template/attr-part "=\"" (js/CSS.escape part) "\"]")]
    (str named ":not(:scope " selector-keyed ", :scope " selector-keyed " *)")))

(defn- part-in
  "The part `part` of `node`, or `node` itself when `part` is nil. A part in a keyed node below
   `node` is left out. Throws when there is no such part."
  [^js node k part]
  (if (nil? part)
    node
    (or (.querySelector node (part-selector part))
        (throw (ex-info "The node of a key has no part of that name." {:key k :part part})))))

(defn- resolved
  "The `container` with its `:parent`, when it is named by a key that has a node in `nodes`."
  [nodes {:keys [parent part] k :key :as container}]
  (if-some [node (when (nil? parent) (nodes k))]
    (assoc container :parent (part-in node k part))
    container))

(defn- with-read
  "The `reading` with the keys of the keyed children of `container`, in document order, and
   their nodes."
  [{:keys [places nodes]} [container {:keys [^js parent]}]]
  (loop [^js child (.-firstElementChild parent) ks (transient []) nodes (transient nodes)]
    (if (some? child)
      (if-some [k (.getAttribute child attr-key)]
        (recur (.-nextElementSibling child) (conj! ks k) (assoc! nodes k child))
        (recur (.-nextElementSibling child) ks nodes))
      {:places (assoc places container (persistent! ks)) :nodes (persistent! nodes)})))

(defn- with-children
  "The `reading` with the keyed children of each of `containers` that has a parent and is not
   read yet."
  [containers {:keys [places] :as reading}]
  (transduce (comp (filter (comp some? :parent val))
                   (remove (comp (partial contains? places) key)))
             (completing with-read)
             reading
             containers))

(defn- keys-in [places container]
  (get places container []))

(defn- places-of
  "The keys that `reading` holds for each of `containers`, a part alone left out."
  [containers {:keys [places]}]
  (into {}
        (comp (remove (comp part-alone? containers))
              (map (juxt identity (partial keys-in places))))
        (keys containers)))

(defn- checked-names
  "The `containers`. Throws, with `where` in the message, when one of them names a part alone and
   a key of `ks` is the name of another."
  [where part containers ks]
  (when-some [k (when (some? part) (some (partial clash containers) ks))]
    (throw (ex-info (str where ": a key is also the name of a container.") {:key k})))
  containers)

(defn read-places
  "The current places of `containers`, with the node of each key, a parent in one container.
   A container named by a key with no node has no keys, and a part alone gives every key found
   its container but is not in the places. With a part alone, every keyed node has that part."
  [containers]
  (let [checked (update-vals containers checked-container)
        part    (part-alone checked)]
    (loop [containers checked
           reading    (with-children containers {:places {} :nodes {}})]
      (let [nodes (:nodes reading)
            found (update-vals (with-part-containers part containers (keys nodes))
                               (partial resolved nodes))]
        (if (= found containers)
          {:containers (checked-names "read-places" part containers (keys nodes))
           :places     (places-of containers reading)
           :nodes      nodes}
          (recur found (with-children found reading)))))))

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

(defn- container-holders
  "The keys that hold `container`, nearest first. A container with no parent yet is held by its key."
  [{:keys [parent] k :key}]
  (if (some? parent) (holding-keys parent) [k]))

(defn- holders-of
  "The keys that hold each container of `placements`, nearest first, by container."
  [containers placements]
  (into {}
        (map (juxt identity (comp container-holders containers)))
        (distinct (map :in placements))))

(defn- refusal
  "A container of `holders` and why its placements cannot be performed, or nil.
   The keys that hold a container count up to the first key that is placed."
  [containers homes removed [container holding]]
  (let [{:keys [parent] k :key} (containers container)]
    (cond
      (and (nil? parent) (not (contains? homes k)))
      [container "a container is named by a key that is not wanted."]
      (some removed (take-while (complement homes) holding))
      [container "a key is placed in a node that is removed."])))

(defn- checked-holders
  "The `holders`. Throws when the placements into one of their containers cannot be performed."
  [containers homes removed holders]
  (when-some [[container reason] (some (partial refusal containers homes removed) holders)]
    (throw (ex-info (str "perform!: " reason) {:container container})))
  holders)

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
   The order within a container stays. `holders` is what `holders-of` gives, and `homes` maps a
   placed key to its container."
  [holders homes placements]
  (let [depths (into {} (map (juxt identity (partial depth holders homes))) (keys holders))]
    (sort-by (comp depths :in) placements)))

(defn perform!
  "Performs `plan` on what `read-places` returned, with `make-node` making the node of a new key.
   Returns the node of each key that remains. Throws before any write to the page when the plan
   cannot be performed as given."
  [{:keys [containers nodes]} {removed :remove placements :place} make-node]
  (let [part      (part-alone containers)
        checked   (checked-names "perform!" part containers (map :key placements))
        named     (with-part-containers part checked (map :in placements))
        known     (update-vals named (partial resolved nodes))
        homes     (into {} (map (juxt :key :in)) placements)
        holders   (checked-holders known homes (set removed) (holders-of known placements))
        ordered   (parents-first holders homes placements)
        remaining (with-new-nodes! make-node (apply dissoc nodes removed) placements)
        targets   (update-vals known (partial resolved remaining))]
    ;; A node that leaves a removed node moves while both are in the document, so moveBefore applies.
    (run! (partial place! targets remaining) ordered)
    (run! (comp remove-node! nodes) removed)
    remaining))

(defn sync!
  "Brings `containers` to the `wanted` places, with `make-node` making the node of a new key.
   Returns the node of each key that remains. Throws before any write to the page when the
   containers or the wanted places cannot be performed."
  [containers wanted make-node]
  (let [reading (read-places containers)]
    (perform! reading (plan/plan (:places reading) wanted) make-node)))
