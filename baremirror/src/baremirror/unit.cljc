(ns baremirror.unit
  "The pure functions on shells. A shell is a vector of a tag, an optional map of attributes,
   and children that are shells or text.")

(def attr-part "data-x-part")

(defn pieces
  "The tag, the attributes and the children of `shell`."
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
  "True when the element of `shell` can be made as it is: no `:on` entry, each attribute value
   text, a number, a boolean or nil, and each child a shell, text or a number.
   The shells among its children are not looked at."
  [shell]
  (let [[_ attrs children] (pieces shell)]
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
   Throws when one of them is not a shell, text or a number."
  [tag children]
  (if (every? fixed-child? children)
    children
    (throw (ex-info "A child of a shell is a shell, text, a number or one hole." {:tag tag}))))

(defn- element
  "What the element of `shell` holds: its fixed attributes, its attribute holes, its text hole,
   its event meanings and its children."
  [shell]
  (let [[tag attrs children] (pieces shell)
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
   A part name the developer gave is kept, and any other is made from `unit-name` and a count."
  [unit-name {:keys [n] :as found} {:keys [fixed] :as el}]
  (let [given (given-part fixed)]
    (cond
      (not (needs-part? el)) [el found]
      given                  [el (with-part found given el)]
      :else                  (let [part (str unit-name "." n)]
                               [(assoc-in el [:fixed (keyword attr-part)] part)
                                (with-part (update found :n inc) part el)]))))

(declare split-shell)

(defn- split-child
  "Adds one child to `children`: the fixed shell of a shell, or the child itself."
  [unit-name [children found] child]
  (if (vector? child)
    (let [[shell found] (split-shell unit-name found child)]
      [(conj children shell) found])
    [(conj children child) found]))

(defn- split-shell
  "The fixed shell of `shell`, and `found` with the holes and events below it."
  [unit-name found shell]
  (let [[{:keys [tag fixed children]} found] (named unit-name found (element shell))
        [children found]                     (reduce (partial split-child unit-name)
                                                     [[] found]
                                                     children)]
    [(into (cond-> [tag] (seq fixed) (conj fixed)) children) found]))

(defn split
  "Makes a unit of a shell with holes: the fixed shell, the holes and the event meanings.
   A hole is an attribute value or the only child of an element, and `:on` maps an event type
   to a meaning."
  [unit-name shell]
  (let [[fixed found] (split-shell (name unit-name) {:n 0 :holes [] :events {}} shell)]
    {:shell  fixed
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
  "The writes of `item` for the parts of `unit`, by part name."
  [unit item]
  (into {} (map (partial hole-writes item)) (:holes unit)))
