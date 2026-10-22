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

(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; A namespace of nil is HTML.
(defn- own-ns
  "The namespace of an element with the tag `tag` and the attributes `attrs`, below a parent
   whose children are in `ns`. An `xmlns` attribute gives it, and an `svg` is in SVG."
  [ns tag attrs]
  (or (template/attr-value attrs "xmlns")
      (case tag "svg" svg-ns ns)))

(defn- children-ns
  "The namespace of the children of an element with the tag `tag` in the namespace `ns`."
  [ns tag]
  (case tag "foreignObject" nil ns))

(defn- create-element!
  "A new element with the tag `tag` in the namespace `ns` of `doc`."
  [^js doc ns tag]
  (if (some? ns)
    (.createElementNS doc ns tag)
    (.createElement doc tag)))

(declare make-node-in!)

(defn- append-child!
  "Appends one child of a template to `el`: a node in the namespace `ns` for a template, text
   for anything else."
  [^js doc ns ^js el child]
  (.append el (if (vector? child) (make-node-in! doc ns child) (str child))))

(defn- checked-fixed
  "The template `fixed`. Throws when one of its elements is not fixed."
  [fixed]
  (if-some [el (template/unfixed fixed)]
    (throw (ex-info "A node is made from a fixed template. split takes holes and :on out of one."
                    {:tag (first el)}))
    fixed))

(defn- make-node-in!
  "Makes one detached element from the checked template `fixed`, in the document `doc`, below a
   parent whose children are in the namespace `ns`."
  [^js doc ns fixed]
  (let [[tag attrs children] (template/pieces fixed)
        tag-name             (name tag)
        el-ns                (own-ns ns tag-name attrs)
        el                   (create-element! doc el-ns tag-name)]
    (set-attrs! el attrs)
    (run! (partial append-child! doc (children-ns el-ns tag-name) el) children)
    el))

(defn make-node!
  "Makes one detached element from the template `fixed`. An element with an `xmlns` attribute is
   in that namespace, an `svg` is in SVG, and the elements below take the namespace of their
   parent. The children of a `foreignObject` are HTML again.
   Throws when the template is not fixed, as `template/fixed?` defines it."
  [fixed]
  (make-node-in! js/document nil (checked-fixed fixed)))

(defn- inert-document
  "The document of a template element, where no element is defined."
  []
  (.-ownerDocument (.-content (.createElement js/document "template"))))

(defn- copy-of [prototype & _]
  (.cloneNode ^js @prototype true))

;; The first node is made on the first call, in a document where no element is defined. A copy
;; of it is a plain element until it enters the page, so a write before that costs what an
;; attribute costs.
(defn node-maker
  "A function that makes one detached element from the template `fixed` each time, as
   `make-node!` makes it, whatever it is called with. Throws when the template is not fixed, as
   `template/fixed?` defines it. It needs no document until it is first called."
  [fixed]
  (let [fixed (checked-fixed fixed)]
    (partial copy-of (delay (make-node-in! (inert-document) nil fixed)))))

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

(declare same-value?)

(defn- same-at?
  "True when `a` and `b` have the same value under `k`."
  [^js a ^js b k]
  (and (js/Object.hasOwn b k)
       (same-value? (unchecked-get a k) (unchecked-get b k))))

(defn- same-entries?
  "True when `a` and `b` have the same own keys, with the same value under each."
  [^js a ^js b]
  (let [ks (js/Object.keys a)]
    (and (== (alength ks) (alength (js/Object.keys b)))
         (every? (partial same-at? a b) ks))))

(defn same-value?
  "True when `a` and `b` are equal: by `=`, and by content for JavaScript arrays and plain
   objects."
  [a b]
  (cond
    (identical? a b)              true
    (and (array? a) (array? b))   (same-entries? a b)
    (and (object? a) (object? b)) (same-entries? a b)
    :else                         (= a b)))

(def ^:private written-props
  "The symbol under which a node holds the value last written to each of its properties."
  (js/Symbol "baremirror.written-props"))

(defn- set-prop!
  "Sets one property of `el` to `v`, unless `v` is the value last written to it."
  [^js el [k v]]
  (let [prop    (name k)
        written (unchecked-get el written-props)]
    (when-not (and (contains? written prop) (same-value? v (get written prop)))
      (unchecked-set el prop v)
      (unchecked-set el written-props (assoc written prop v)))))

(defn- write-part!
  "Applies the write of one part to its node: the text, then the attributes, then the
   properties."
  [parts [part-name {:keys [attrs props] :as write}]]
  (let [node (parts part-name)]
    (when (contains? write :text)
      (set-text! node (:text write)))
    (when attrs
      (set-attrs! node attrs))
    (run! (partial set-prop! node) props)))

(defn write-parts!
  "Applies `writes` to `parts`, which are maps by part name: the nodes as `read-parts` gives
   them, and `:text`, `:attrs` and `:props` as `template/writes` gives them. A property is
   written when its value differs from the value last written to it, as `same-value?` sees it.
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
   ;; Only a split template was ever written, so the check waits until something is.
   (when-not (written? same? node split-template item)
     (when-not (template/split? split-template)
       (throw (ex-info "write-item!: not a split template." {:given split-template})))
     (write-parts! (read-parts node) (template/writes split-template item))
     (unchecked-set node last-written [split-template item]))
   nil))
