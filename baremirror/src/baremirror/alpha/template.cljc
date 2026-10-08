(ns baremirror.alpha.template
  "The pure functions on templates. A template is a vector of a tag, an optional map of
   attributes, and children that are templates or text.")

(def attr-part "data-x-part")

(defn pieces
  "The tag, the attributes and the children of `template`."
  [[tag & more]]
  (if (map? (first more))
    [tag (first more) (next more)]
    [tag nil (seq more)]))

(defn hole?
  "True when `x` is a hole: a keyword or a function of the item."
  [x]
  (or (keyword? x) (fn? x)))

(defn- fixed-value? [x]
  (or (string? x) (number? x) (boolean? x) (nil? x)))

(defn- fixed-child? [x]
  (or (vector? x) (string? x) (number? x)))

(defn fixed?
  "True when the element of `template` can be made as it is: no `:on` entry, each attribute value
   text, a number, a boolean or nil, and each child a template, text or a number.
   The templates among its children are not looked at."
  [template]
  (let [[_ attrs children] (pieces template)]
    (and (not (contains? attrs :on))
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

(defn- element
  "What the element of `template` holds: its fixed attributes, its attribute holes, its text hole,
   its event meanings and its children."
  [template]
  (let [[tag attrs children] (pieces template)
        values               (dissoc attrs :on)
        text                 (text-hole tag children)]
    {:tag        tag
     :on         (:on attrs)
     :fixed      (into {} (remove (comp hole? val)) values)
     :attr-holes (into {} (filter (comp hole? val)) values)
     :text       text
     :children   (when-not text (fixed-children tag children))}))

(defn- given-part
  "The part name that the fixed attributes give, or nil."
  [fixed]
  (some (fn [[k v]] (when (= attr-part (name k)) v)) fixed))

(defn- needs-part?
  "True when the element has an event meaning or a hole, so a write or an event must find it."
  [{:keys [on attr-holes text]}]
  (boolean (or (seq on) (seq attr-holes) text)))

(defn- hole-of
  "The hole entry of an element with the part name `part`, or nil when it has no hole."
  [part {:keys [attr-holes text]}]
  (when (or (seq attr-holes) text)
    (cond-> {:part part}
      (seq attr-holes) (assoc :attrs attr-holes)
      text             (assoc :text text))))

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
   A hole is an attribute value or the only child of an element, and `:on` maps an event type
   to a meaning."
  [template-name template]
  (let [[fixed found] (split-element (name template-name) {:n 0 :holes [] :events {}} template)]
    {:fixed  fixed
     :holes  (:holes found)
     :events (:events found)}))

(defn- fill [item hole]
  (hole item))

(defn- hole-writes
  "The part name of `hole` and its writes for `item`."
  [item {:keys [part attrs text]}]
  [part (cond-> {}
          attrs (assoc :attrs (update-vals attrs (partial fill item)))
          text  (assoc :text (fill item text)))])

(defn writes
  "The writes of `item` for the holes of `split-template`, by part name."
  [split-template item]
  (into {} (map (partial hole-writes item)) (:holes split-template)))
