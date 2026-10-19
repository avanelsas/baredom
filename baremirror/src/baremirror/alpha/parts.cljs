(ns baremirror.alpha.parts
  "The nodes of templates, and the attributes and text of their parts."
  (:require [baremirror.alpha.places :as places]
            [baremirror.alpha.template :as template]))

(def render-hold
  "The symbol under which an element offers the hold of its render."
  (js/Symbol.for "x-render-hold"))

(defn with-one-render!
  "Calls `f` with `el` while `el` holds its attribute changes, when it offers that."
  [^js el f]
  (if-some [hold (unchecked-get el render-hold)]
    (.call hold el (partial f el))
    (f el))
  nil)

(defn attr-text
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

(declare make-node-in!)

(defn- append-child!
  "Appends one child of a template to `el`: a node for a template, text for anything else."
  [^js doc ^js el child]
  (.append el (if (vector? child) (make-node-in! doc child) (str child))))

(defn- fixed-pieces
  "The pieces of the template `fixed`. Throws when its element is not fixed."
  [fixed]
  (if (template/fixed? fixed)
    (template/pieces fixed)
    (throw (ex-info "A node is made from a fixed template. split takes holes and :on out of one."
                    {:tag (first fixed)}))))

(defn- make-node-in!
  "Makes one detached HTML element from the template `fixed`, in the document `doc`."
  [^js doc fixed]
  (let [[tag attrs children] (fixed-pieces fixed)
        el                   (.createElement doc (name tag))]
    (set-attrs! el attrs)
    (run! (partial append-child! doc el) children)
    el))

(defn make-node!
  "Makes one detached HTML element from the template `fixed`.
   Throws when the template is not fixed, as `template/fixed?` defines it."
  [fixed]
  (make-node-in! js/document fixed))

(defn- copy-of [^js node & _]
  (.cloneNode node true))

;; The first node is made in the document of a template element, where no element is defined.
;; A copy of it is a plain element until it enters the page, so a write before that costs what
;; an attribute costs.
(defn node-maker
  "A function that makes one detached HTML element from the template `fixed` each time,
   whatever it is called with. Throws when the template is not fixed, as `template/fixed?`
   defines it."
  [fixed]
  (let [^js holder (.createElement js/document "template")
        ^js inert  (.-ownerDocument (.-content holder))]
    (partial copy-of (make-node-in! inert fixed))))

(defn part
  "The part name of `node` and the node, when it is a part."
  [^js node]
  (when-some [part-name (.getAttribute node template/attr-part)]
    [part-name node]))

(defn- with-part
  "The transient map `found` with `el` under its part name, when it is a part."
  [found ^js el]
  (if-some [[part-name node] (part el)]
    (assoc! found part-name node)
    found))

(declare with-parts-below)

(defn- with-parts-from
  "The transient map `found` with `el` and the parts below it. A keyed `el` adds nothing."
  [found ^js el]
  (if (.hasAttribute el places/attr-key)
    found
    (with-parts-below (with-part found el) el)))

(defn- with-parts-below
  "The transient map `found` with the parts below `node`, in document order."
  [found ^js node]
  (loop [found found ^js child (.-firstElementChild node)]
    (if (some? child)
      (recur (with-parts-from found child) (.-nextElementSibling child))
      found)))

(defn read-parts
  "The parts of `node` by name: `node` itself and the parts below it.
   A keyed node below `node` is left out with everything in it."
  [^js node]
  (persistent! (with-parts-below (with-part (transient {}) node) node)))

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

(defn write-parts!
  "Applies `writes` to `parts`, which are maps by part name: the nodes as `read-parts` gives
   them, and `:text` and `:attrs` as `template/writes` gives them.
   Throws before any write when a part name has no node."
  [parts writes]
  (when-some [missing (seq (missing-parts parts writes))]
    (throw (ex-info "write-parts!: no node has these part names." {:parts (vec missing)})))
  (run! (partial write-part! parts) writes))

(def ^:private last-written
  "The symbol under which a node holds the split template and the item last written to it."
  (js/Symbol "baremirror.last-written"))

(defn- written?
  "True when `item` is what was last written to `node` through `split-template`, as `same?` sees it."
  [same? ^js node split-template item]
  (let [[template-then item-then] (unchecked-get node last-written)]
    (and (identical? split-template template-then)
         (same? item item-then))))

(defn write-item!
  "Brings `node` to `item` through `split-template`. Writes nothing when `same?`, by default
   `=`, holds for `item` and the item it last wrote to `node`, in that order."
  ([^js node split-template item]
   (write-item! = node split-template item))
  ([same? ^js node split-template item]
   (when-not (template/split? split-template)
     (throw (ex-info "write-item!: not a split template." {:given split-template})))
   (when-not (written? same? node split-template item)
     (write-parts! (read-parts node) (template/writes split-template item))
     (unchecked-set node last-written [split-template item]))
   nil))
