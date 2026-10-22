(ns baremirror.alpha.js
  "The functions for JavaScript. JavaScript values go in and come out: a template is an array,
   attributes and containers are objects, and a hole is a function."
  (:require [baremirror.alpha.element :as mirror-element]
            [baremirror.alpha.events :as mirror-events]
            [baremirror.alpha.parts :as mirror-parts]
            [baremirror.alpha.places :as mirror-places]
            [baremirror.alpha.plan :as places-plan]
            [baremirror.alpha.template :as template]))

(defn- own-entry [^js o k]
  [k (unchecked-get o k)])

(defn- object-map
  "The own entries of the JavaScript object `o` as a map with string keys.
   The values stay as they are."
  [^js o]
  (if (some? o)
    (into {} (map (partial own-entry o)) (js/Object.keys o))
    {}))

(defn- frozen
  "The JavaScript value `x`, frozen with every array and object in it."
  [x]
  (when (or (array? x) (object? x))
    (run! frozen (js/Object.values x))
    (js/Object.freeze x))
  x)

(declare ->template)

(defn- ->meaning
  "The meaning of an event: a JavaScript array of a meaning and a function becomes a vector."
  [meaning]
  (if (array? meaning) (vec meaning) meaning))

(defn- ->value
  "One value of a template: `hole` applied to a function, or the value itself."
  [hole v]
  (if (fn? v) (hole v) v))

(defn- ->attr
  "One attribute of a template. `on` holds event meanings, `props` holds property holes, and
   `hole` is applied to each function."
  [hole [k v]]
  (case k
    "on"    [:on (update-vals (object-map v) ->meaning)]
    "props" [:props (update-vals (object-map v) (partial ->value hole))]
    [k (->value hole v)]))

(defn- ->child [hole child]
  (cond
    (array? child) (->template hole child)
    (fn? child)    (hole child)
    :else          child))

(defn- ->template
  "The template that the JavaScript array `array` describes. `hole` is applied to each function."
  [hole array]
  (let [[tag & more] (array-seq array)
        attrs        (when (object? (first more)) (first more))]
    (into (cond-> [tag]
            attrs (conj (into {} (map (partial ->attr hole)) (object-map attrs))))
          (map (partial ->child hole))
          (if attrs (rest more) more))))

(defn- holding
  "The JavaScript object `o` with the value `v` under the symbol `k`. A copy of the object does
   not get the value."
  [^js o k v]
  (js/Object.defineProperty o k #js {:value v}))

(def ^:private split-of
  "The symbol under which a JavaScript split template holds the split template it stands for."
  (js/Symbol "baremirror.split"))

(defn split-template
  "Splits the template `array`. Returns a frozen object with `fixed`, `holes`, `events` as pairs
   of an event type with a part name and a meaning, and `writes`, a function of an item."
  [template-name array]
  (let [taken (template/split template-name (->template identity array))]
    (frozen (holding #js {:fixed  (clj->js (:fixed taken))
                          :holes  (clj->js (:holes taken))
                          :events (clj->js (vec (:events taken)))
                          :writes (fn [item] (clj->js (template/writes taken item)))}
                     split-of
                     taken))))

(defn make-node
  "Makes one detached element from the fixed template `array`, as `parts/make-node!` makes it."
  [array]
  (mirror-parts/make-node! (->template identity array)))

(defn node-maker
  "A function that makes one detached element from the fixed template `array` each time,
   whatever it is called with."
  [array]
  (mirror-parts/node-maker (->template identity array)))

(defn read-parts
  "The parts of `node` as an object of part name to node."
  [node]
  (clj->js (mirror-parts/read-parts node)))

(defn- ->write
  "One write, from the own fields of `write`: `text` when it is there, and `attrs` and `props` as
   maps."
  [write]
  (let [fields (object-map write)]
    (cond-> {}
      (contains? fields "text")    (assoc :text (fields "text"))
      (some? (fields "attrs"))     (assoc :attrs (object-map (fields "attrs")))
      (some? (fields "props"))     (assoc :props (object-map (fields "props"))))))

(defn write-parts
  "Applies the object `writes` to the object `parts`, both by part name."
  [parts writes]
  (mirror-parts/write-parts! (object-map parts) (update-vals (object-map writes) ->write)))

(defn- plain-object?
  "True when `x` is an object with the prototype of an object literal."
  [x]
  (and (some? x)
       (identical? (.-prototype js/Object) (js/Object.getPrototypeOf x))))

(defn- own-field? [^js o k]
  (.call (.. js/Object -prototype -hasOwnProperty) o k))

(defn- same-field?
  "True when the objects `a` and `b` both have the field `k` of their own, with the same value."
  [^js a ^js b k]
  (and (own-field? b k)
       (js/Object.is (unchecked-get a k) (unchecked-get b k))))

(defn- same-in-each?
  "True when `b` has each of the fields `ks` of `a`, with the same value."
  [^js a ^js b ^js ks]
  (loop [i 0]
    (or (== i (alength ks))
        (and (same-field? a b (aget ks i))
             (recur (inc i))))))

(defn- same-fields?
  "True when `a` and `b` are the same value, or plain objects with the same fields of their own
   and the same value in each. A field is not looked into."
  [^js a ^js b]
  (or (identical? a b)
      (and (plain-object? a)
           (plain-object? b)
           (let [ks (js/Object.keys a)]
             (and (== (alength ks) (alength (js/Object.keys b)))
                  (same-in-each? a b ks))))))

(defn- taken-split
  "The split template that the JavaScript split template `split` stands for.
   Throws an error that names `fn-name` when `split` is not a split template."
  [fn-name ^js split]
  (or (some-> split (unchecked-get split-of))
      (throw (ex-info (str fn-name ": not a split template.") {:given split}))))

(defn write-item
  "Brings `node` to `item` through the split template `split`. Writes nothing when `item` has the
   same fields as the item it last wrote to `node`."
  [node split item]
  (mirror-parts/write-item! same-fields? node (taken-split "writeItem" split) item))

(defn- foreign-write? [^js w]
  (and (some? w)
       (string? (.-target w))
       (or (= "any" (.-attributes w)) (array? (.-attributes w)))))

(defn- ->foreign-write
  "The foreign write `w` as a map. Throws when it has no target text, or attributes that are
   neither \"any\" nor an array."
  [^js w]
  (when-not (foreign-write? w)
    (throw (ex-info "sharedHoles: a foreign write has a target and attributes." {:given w})))
  {:target     (.-target w)
   :attributes (if (array? (.-attributes w)) (vec (.-attributes w)) (.-attributes w))})

(defn- ->foreign-writes [foreign-writes]
  (update-vals (object-map foreign-writes) (partial mapv ->foreign-write)))

(defn shared-holes
  "The attribute holes of the split template `split` that a component in `foreignWrites` may
   also write: an array of objects with `part`, `tag`, `attr`, `writer` and `target`."
  [split foreign-writes]
  (clj->js (template/shared-holes (taken-split "sharedHoles" split)
                                  (->foreign-writes foreign-writes))))

(defn set-text
  "Makes `text` the whole content of `el`, and writes only where it differs."
  [el text]
  (mirror-parts/set-text! el text))

(defn with-one-render
  "Calls `f` with `el` while `el` holds its attribute changes, when it offers that."
  [el f]
  (mirror-parts/with-one-render! el f))

(defn release
  "Gives `node` up: it keeps its place in the document and loses its key."
  [node]
  (mirror-places/release! node))

(defn set-attrs
  "Brings the attributes of `el` that the object `attrs` names to their values."
  [el attrs]
  (mirror-parts/set-attrs! el (object-map attrs)))

(defn- ->container [^js container]
  {:parent (.-parent container)
   :before (.-before container)
   :key    (.-key container)
   :part   (.-part container)})

(defn- ->containers [containers]
  (update-vals (object-map containers) ->container))

(defn- ->places [places]
  (update-vals (object-map places) vec))

(defn- ->placement [^js placement]
  {:key (.-key placement) :in (.-in placement) :before (.-before placement)})

(defn- ->plan [^js steps]
  {:remove (vec (.-remove steps))
   :place  (mapv ->placement (.-place steps))})

(def ^:private value-of
  "The symbol under which a reading and a plan hold the value they were made from."
  (js/Symbol "baremirror.value"))

(defn- stood-for
  "The value the JavaScript object `o` was made from, or nil when it holds none."
  [^js o]
  (some-> o (unchecked-get value-of)))

(defn- put! [^js o k v]
  (doto o (unchecked-set k v)))

(defn- object-of
  "A JavaScript object with the entries of the map `m`. The values stay as they are."
  [m]
  (reduce-kv put! #js {} m))

(defn- push! [^js a x]
  (doto a (.push x)))

(defn- array-of
  "A JavaScript array with the items of `xs`. The items stay as they are."
  [xs]
  (reduce push! #js [] xs))

(defn- container->js [{:keys [parent before part] k :key}]
  #js {:parent parent :before before :key k :part part})

(defn- with-nodes-when-read
  "The object `o` with a field `nodes` that makes the frozen object of the map `nodes` when it
   is first read."
  [^js o nodes]
  (let [made (delay (js/Object.freeze (object-of nodes)))]
    (js/Object.defineProperty o "nodes" #js {:get (fn [] @made) :enumerable true})))

;; `perform` takes the reading that the object holds, so a render that only performs never
;; reads `nodes`, and a list of many keys does not pay for an object of them.
(defn- reading->js
  "The reading as a frozen object with `containers`, `places` and `nodes`, holding the reading."
  [{:keys [containers places nodes] :as reading}]
  (-> #js {:containers (frozen (object-of (update-vals containers container->js)))
           :places     (frozen (object-of (update-vals places array-of)))}
      (with-nodes-when-read nodes)
      (holding value-of reading)
      (js/Object.freeze)))

(defn- ->reading [^js reading]
  (or (stood-for reading)
      {:containers (->containers (.-containers reading))
       :nodes      (object-map (.-nodes reading))}))

(defn- placement->js [{:keys [key in before]}]
  #js {:key key :in in :before before})

(defn- plan->js
  "The plan as a frozen object with `remove` and `place`, holding the plan."
  [{removed :remove placements :place :as steps}]
  (frozen (holding #js {:remove (array-of removed)
                        :place  (array-of (map placement->js placements))}
                   value-of
                   steps)))

(defn plan
  "What must happen to turn the `current` places into the `wanted` places: an object with
   `remove`, the keys, and `place`, objects of `key`, `in` and `before`."
  [current wanted]
  (plan->js (places-plan/plan (->places current) (->places wanted))))

(defn read-places
  "The current places of `containers`, with the node of each key: an object with `containers`,
   `places` and `nodes`."
  [containers]
  (reading->js (mirror-places/read-places (->containers containers))))

(defn perform
  "Performs the plan `steps` on what `read-places` returned, with `make` making the node of a
   new key. Returns an object of key to node."
  [reading steps make]
  (object-of (mirror-places/perform! (->reading reading) (or (stood-for steps) (->plan steps)) make)))

(defn sync-places
  "Brings `containers` to the `wanted` places, with `make` making the node of a new key.
   Returns an object of key to node."
  [containers wanted make]
  (object-of (mirror-places/sync! (->containers containers) (->places wanted) make)))

(defn- origin->js [{:keys [key-path part node]}]
  #js {:keyPath (clj->js key-path) :part part :node node})

(defn read-origin
  "The origin of the event `e`: an object with `keyPath`, `part` and `node`."
  [e]
  (origin->js (mirror-events/read-origin e)))

(defn- ->arg-of
  "A function of an origin and an event that gives `f` the origin as an object."
  [f]
  (fn [origin e] (f (origin->js origin) e)))

(defn- ->entry [entry]
  (if (vector? entry)
    (update entry 1 ->arg-of)
    entry))

(defn- ->event [pair]
  [(vec (aget pair 0)) (->entry (->meaning (aget pair 1)))])

(defn- ->events [events]
  (into {} (map ->event) (array-seq events)))

(defn- ->message-handler
  "A function of a message that gives `dispatch` the message as an array."
  [dispatch]
  (fn [message] (dispatch (clj->js message))))

(defn listen
  "Adds the listeners of `options` to `root`: `dispatch`, a function of a message, `events`,
   pairs of an event type with a part name and a meaning, and `requestedValues`, an object from
   a request event to what it asks of each attribute. Returns a function that removes the
   listeners."
  [root ^js options]
  (mirror-events/listen! root {:dispatch!        (->message-handler (.-dispatch options))
                               :events           (->events (.-events options))
                               :requested-values (update-vals (object-map (.-requestedValues options))
                                                              object-map)}))

(defn- holder
  "A state holder over the `value` property of the object `store`."
  [^js store]
  (reify
    IDeref
    (-deref [_] (.-value store))
    ISwap
    (-swap! [_ f a] (set! (.-value store) (f (.-value store) a)))))

(defn dispatcher
  "A function of a message. It puts `step(store.value, message)` into `store.value`, renders the
   view of what `store` then holds, and returns the new state."
  [store step view render]
  (mirror-events/dispatcher (holder store) step view render))

(defn- with-object-item
  "A hole that gives `f` its item as an object."
  [f]
  (fn [item] (f (clj->js item))))

(defn define-element
  "Registers `tag` as an element with no state, from an object with `attrs`, `template` and `css`."
  [tag ^js options]
  (mirror-element/define-element! tag {:attrs    (vec (.-attrs options))
                               :template (->template with-object-item (.-template options))
                               :css      (.-css options)}))
