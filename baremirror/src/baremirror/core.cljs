(ns baremirror.core
  "The functions that bring the document to what an application wants: the places of keyed nodes,
   the nodes of templates, and the attributes and text of their parts. A key is a string, and a
   container is a parent with an optional `:before` anchor."
  (:require [baremirror.plan :as plan]
            [baremirror.template :as template]))

(def attr-key "data-x-key")

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
  "Appends one child of a template to `el`: a node for a template, text for anything else."
  [^js el child]
  (.append el (if (vector? child) (make-node! child) (str child))))

(defn- fixed-pieces
  "The pieces of the template `fixed`. Throws when its element is not fixed."
  [fixed]
  (if (template/fixed? fixed)
    (template/pieces fixed)
    (throw (ex-info "make-node!: the template is not fixed. split takes holes and :on out of it."
                    {:tag (first fixed)}))))

(defn make-node!
  "Makes one detached HTML element from the template `fixed`.
   Throws when the template is not fixed, as `template/fixed?` defines it."
  [fixed]
  (let [[tag attrs children] (fixed-pieces fixed)
        el                   (.createElement js/document (name tag))]
    (set-attrs! el attrs)
    (run! (partial append-child! el) children)
    el))

(defn- part
  "The part name of `node` and the node, when it is a part."
  [^js node]
  (when-some [part-name (.getAttribute node template/attr-part)]
    [part-name node]))

(defn- unkeyed-children [^js node]
  (remove keyed (array-seq (.-children node))))

(defn read-parts
  "The parts of `node` by name: `node` itself and the parts below it.
   A keyed node below `node` is left out with everything in it."
  [^js node]
  (into {} (keep part) (tree-seq (constantly true) unkeyed-children node)))

(defn- missing-parts
  "The part names of `writes` that have no node in `parts`."
  [parts writes]
  (into [] (remove parts) (keys writes)))

(defn- write-part!
  "Applies the write of one part to its node: the text, then the attributes."
  [parts [part-name {:keys [attrs] :as write}]]
  (let [node (parts part-name)]
    (when (contains? write :text)
      (set-text! node (:text write)))
    (when attrs
      (set-attrs! node attrs))))

(defn write!
  "Applies `writes` to `parts`, which are maps by part name: the nodes as `read-parts` gives
   them, and `:text` and `:attrs` as `template/writes` gives them.
   Throws before any write when a part name has no node."
  [parts writes]
  (when-some [missing (seq (missing-parts parts writes))]
    (throw (ex-info "write!: no node has these part names." {:parts (vec missing)})))
  (run! (partial write-part! parts) writes))

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
        [part-name node] (some part (up-to-first keyed nodes))]
    {:key-path (into [] (keep (comp first keyed)) (reverse nodes))
     :part     part-name
     :node     node}))
