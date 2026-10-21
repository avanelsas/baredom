(ns baremirror.alpha.template
  "The pure functions on templates. A template is a vector of a tag, an optional map of
   attributes, and children that are templates or text."
  (:require [clojure.string :as string]))

(def attr-part "data-x-part")

(defn pieces
  "The tag, the attributes and the children of `template`."
  [[tag & more]]
  (if (map? (first more))
    [tag (first more) (next more)]
    [tag nil (seq more)]))

(defn attr-value
  "The value of the attribute `attr-name` in `attrs`, by keyword or by string."
  [attrs attr-name]
  (some (fn [[k v]] (when (= attr-name (name k)) v)) attrs))

(defn hole?
  "True when `x` is a hole: a keyword or a function of the item."
  [x]
  (or (keyword? x) (fn? x)))

(defn- fixed-value? [x]
  (or (string? x) (number? x) (boolean? x) (nil? x)))

(defn- fixed-child? [x]
  (or (vector? x) (string? x) (number? x)))

(defn fixed?
  "True when the element of `template` can be made as it is: no `:on` or `:props` entry, each
   attribute value text, a number, a boolean or nil, and each child a template, text or a number.
   The templates among its children are not looked at."
  [template]
  (let [[_ attrs children] (pieces template)]
    (and (not-any? (partial contains? attrs) [:on :props])
         (every? fixed-value? (vals attrs))
         (every? fixed-child? children))))

(defn- text-hole
  "The hole that is the only child of an element, or nil.
   Throws when a hole stands beside other children."
  [tag children]
  (cond
    (not-any? hole? children) nil
    (next children)           (throw (ex-info "A text hole must be the only child of its element."
                                              {:tag tag}))
    :else                     (first children)))

(defn- fixed-children
  "The `children` of an element with no text hole.
   Throws when one of them is not a template, text or a number."
  [tag children]
  (if (every? fixed-child? children)
    children
    (throw (ex-info "A child of a template is a template, text, a number or one hole."
                    {:tag tag}))))

(defn- prop-holes
  "The `props` of an element with the tag `tag`. Throws when one of them is not a hole."
  [tag props]
  (if (every? hole? (vals props))
    props
    (throw (ex-info "A value in :props is a hole." {:tag tag}))))

(defn- checked-attr-holes
  "The holes among the attributes `values` of an element with the tag `tag`.
   Throws when `xmlns` is one of them."
  [tag values]
  (let [holes (into {} (filter (comp hole? val)) values)]
    (if (attr-value holes "xmlns")
      (throw (ex-info "xmlns is a fixed value, never a hole." {:tag tag}))
      holes)))

(defn- element
  "What the element of `template` holds: its fixed attributes, its attribute holes, its text hole,
   its property holes, its event meanings and its children."
  [template]
  (let [[tag attrs children] (pieces template)
        values               (dissoc attrs :on :props)
        text                 (text-hole tag children)]
    {:tag        tag
     :on         (:on attrs)
     :props      (prop-holes tag (:props attrs))
     :fixed      (into {} (remove (comp hole? val)) values)
     :attr-holes (checked-attr-holes tag values)
     :text       text
     :children   (when-not text (fixed-children tag children))}))

(defn- given-part
  "The part name that the fixed attributes give, or nil."
  [fixed]
  (attr-value fixed attr-part))

(defn- has-hole?
  "True when the element has a hole."
  [{:keys [attr-holes text props]}]
  (boolean (or (seq attr-holes) text (seq props))))

(defn- needs-part?
  "True when the element has an event meaning or a hole, so a write or an event must find it."
  [{:keys [on] :as el}]
  (boolean (or (seq on) (has-hole? el))))

(defn- hole-of
  "The hole entry of an element with the part name `part`, or nil when it has no hole."
  [part {:keys [attr-holes text props] :as el}]
  (when (has-hole? el)
    (cond-> {:part part}
      (seq attr-holes) (assoc :attrs attr-holes)
      text             (assoc :text text)
      (seq props)      (assoc :props props))))

(defn- event-entry [part [event-type meaning]]
  [[event-type part] meaning])

(defn- with-part
  "The `found` after its element got the part name `part`."
  [found part {:keys [on] :as el}]
  (let [hole (hole-of part el)]
    (cond-> (update found :events into (map (partial event-entry part)) on)
      hole (update :holes conj hole))))

(defn- named
  "The element `el` with its part name, and `found` with what the element adds to it.
   A part name the developer gave is kept, and any other is made from `template-name` and a
   count."
  [template-name {:keys [n] :as found} {:keys [fixed] :as el}]
  (let [given (given-part fixed)]
    (cond
      (not (needs-part? el)) [el found]
      given                  [el (with-part found given el)]
      :else                  (let [part (str template-name "." n)]
                               [(assoc-in el [:fixed (keyword attr-part)] part)
                                (with-part (update found :n inc) part el)]))))

(declare split-element)

(defn- split-child
  "Adds one child to `children`: the fixed template of a template, or the child itself."
  [template-name [children found] child]
  (if (vector? child)
    (let [[fixed found] (split-element template-name found child)]
      [(conj children fixed) found])
    [(conj children child) found]))

(defn- split-element
  "The fixed template of `template`, and `found` with the holes and events below it."
  [template-name found template]
  (let [[{:keys [tag fixed children]} found] (named template-name found (element template))
        [children found]                     (reduce (partial split-child template-name)
                                                     [[] found]
                                                     children)]
    [(into (cond-> [tag] (seq fixed) (conj fixed)) children) found]))

(defn split
  "Splits `template`: its fixed template under `:fixed`, its holes and its event meanings.
   A hole is an attribute value, the only child of an element or a value in `:props`, which
   maps a property name to a hole. `:on` maps an event type to a meaning."
  [template-name template]
  (let [[fixed found] (split-element (name template-name) {:n 0 :holes [] :events {}} template)]
    {:fixed  fixed
     :holes  (:holes found)
     :events (:events found)}))

(defn- fill [item hole]
  (hole item))

(defn- hole-writes
  "The part name of `hole` and its writes for `item`."
  [item {:keys [part attrs text props]}]
  [part (cond-> {}
          attrs (assoc :attrs (update-vals attrs (partial fill item)))
          text  (assoc :text (fill item text))
          props (assoc :props (update-vals props (partial fill item))))])

(defn split?
  "True when `x` is a split template."
  [x]
  (and (map? x) (contains? x :fixed) (contains? x :holes)))

(defn writes
  "The writes of `item` for the holes of `split-template`, by part name."
  [split-template item]
  (into {} (map (partial hole-writes item)) (:holes split-template)))

(defn- child-templates [template]
  (filter vector? (nth (pieces template) 2)))

(defn unfixed
  "The first element of `template`, or of a template below it, that is not fixed, or nil."
  [template]
  (first (remove fixed? (tree-seq vector? child-templates template))))

(defn- part-and-tag
  "The part name and the tag of the element of `template`, or nil when it is not a part."
  [template]
  (let [[tag attrs] (pieces template)]
    (when-some [part (given-part attrs)]
      [part (string/lower-case (name tag))])))

(defn- part-tags
  "The tag of each part of the fixed template `fixed`, by part name."
  [fixed]
  (into {} (keep part-and-tag) (tree-seq vector? child-templates fixed)))

(defn- any-name?
  "True when `x` is \"any\" or `:any`."
  [x]
  (and (or (string? x) (keyword? x)) (= "any" (name x))))

(defn- writes-attr?
  "True when the foreign write `w` may write the attribute `attr` of an element with the tag
   `tag`. A target or attributes of \"any\" or `:any` match every tag or attribute."
  [tag attr {:keys [target attributes]}]
  (and (or (any-name? target) (= (name target) tag))
       (or (any-name? attributes) (contains? (into #{} (map name) attributes) attr))))

(defn- specificity
  "0 for a foreign write with a named target, 1 for one with any target."
  [{:keys [target]}]
  (if (any-name? target) 1 0))

(defn- writer-entry
  "The entry for `writer`, when one of its `declared` writes may write the attribute `attr` of the
   part `part` with the tag `tag`. A named target comes before any target."
  [part tag attr [writer declared]]
  (when-some [w (first (sort-by specificity (filter (partial writes-attr? tag attr) declared)))]
    {:part part :tag tag :attr attr :writer writer :target (name (:target w))}))

(defn- hole-attrs
  "The names of the attribute holes of `attrs`, in lower case and in order."
  [attrs]
  (sort (map (comp string/lower-case name) (keys attrs))))

(defn shared-holes
  "The attribute holes of `split-template` that a component in `foreign-writes` may also write:
   an entry for each hole and writer. It does not look at where the writer is on the page."
  [split-template foreign-writes]
  (let [tags    (part-tags (:fixed split-template))
        writers (sort-by key foreign-writes)]
    (into []
          (for [{:keys [part attrs]} (:holes split-template)
                attr                 (hole-attrs attrs)
                writer               writers
                :let                 [entry (writer-entry part (tags part) attr writer)]
                :when                entry]
            entry))))
