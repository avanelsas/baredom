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
  "The entries of the JavaScript object `o` as a map with string keys.
   The values stay as they are."
  [^js o]
  (if (some? o)
    (into {} (map (partial own-entry o)) (js-keys o))
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

(defn- ->attr
  "One attribute of a template. `on` holds event meanings, and `hole` is applied to a function."
  [hole [k v]]
  (cond
    (= "on" k) [:on (update-vals (object-map v) ->meaning)]
    (fn? v)    [k (hole v)]
    :else      [k v]))

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

(def ^:private split-of
  "The symbol under which a JavaScript split template holds the split template it stands for."
  (js/Symbol "baremirror.split"))

(defn split-template
  "Splits the template `array`. Returns a frozen object with `fixed`, `holes`, `events` as pairs
   of an event type with a part name and a meaning, and `writes`, a function of an item."
  [template-name array]
  (let [taken (template/split template-name (->template identity array))]
    (frozen (doto #js {:fixed  (clj->js (:fixed taken))
                       :holes  (clj->js (:holes taken))
                       :events (clj->js (vec (:events taken)))
                       :writes (fn [item] (clj->js (template/writes taken item)))}
              (unchecked-set split-of taken)))))

(defn make-node
  "Makes one detached HTML element from the fixed template `array`."
  [array]
  (mirror-parts/make-node! (->template identity array)))

(defn read-parts
  "The parts of `node` as an object of part name to node."
  [node]
  (clj->js (mirror-parts/read-parts node)))

(defn- ->write
  "One write: `text` when the object has it, and `attrs` as a map."
  [^js write]
  (cond-> {}
    (js-in "text" write)        (assoc :text (.-text write))
    (some? (.-attrs write))     (assoc :attrs (object-map (.-attrs write)))))

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

(defn- same-fields?
  "True when `a` and `b` are the same value, or plain objects with the same fields of their own
   and the same value in each. A field is not looked into."
  [^js a ^js b]
  (or (identical? a b)
      (and (plain-object? a)
           (plain-object? b)
           (== (alength (js/Object.keys a)) (alength (js/Object.keys b)))
           (every? (partial same-field? a b) (js/Object.keys a)))))

(defn write-item
  "Brings `node` to `item` through the split template `split`. Writes nothing when `item` has the
   same fields as the item it last wrote to `node`."
  [node ^js split item]
  (let [taken (some-> split (unchecked-get split-of))]
    (when (nil? taken)
      (throw (ex-info "writeItem: not a split template." {:given split})))
    (mirror-parts/write-item! same-fields? node taken item)))

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
  {:parent (.-parent container) :before (.-before container)})

(defn- ->containers [containers]
  (update-vals (object-map containers) ->container))

(defn- ->places [places]
  (update-vals (object-map places) vec))

(defn- ->placement [^js placement]
  {:key (.-key placement) :in (.-in placement) :before (.-before placement)})

(defn- ->plan [^js steps]
  {:remove (vec (.-remove steps))
   :place  (mapv ->placement (.-place steps))})

(defn- ->reading [^js reading]
  {:containers (->containers (.-containers reading))
   :nodes      (object-map (.-nodes reading))})

(defn plan
  "What must happen to turn the `current` places into the `wanted` places: an object with
   `remove`, the keys, and `place`, objects of `key`, `in` and `before`."
  [current wanted]
  (clj->js (places-plan/plan (->places current) (->places wanted))))

(defn read-places
  "The current places of `containers`, with the node of each key: an object with `containers`,
   `places` and `nodes`."
  [containers]
  (clj->js (mirror-places/read-places (->containers containers))))

(defn perform
  "Performs the plan `steps` on what `read-places` returned, with `make` making the node of a
   new key. Returns an object of key to node."
  [reading steps make]
  (clj->js (mirror-places/perform! (->reading reading) (->plan steps) make)))

(defn sync-places
  "Brings `containers` to the `wanted` places, with `make` making the node of a new key.
   Returns an object of key to node."
  [containers wanted make]
  (clj->js (mirror-places/sync! (->containers containers) (->places wanted) make)))

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
   pairs of an event type with a part name and a meaning, and `requests`, an object from an event
   type to the attributes it asks to change."
  [root ^js options]
  (mirror-events/listen! root {:dispatch! (->message-handler (.-dispatch options))
                        :events    (->events (.-events options))
                        :requests  (update-vals (object-map (.-requests options)) vec)}))

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
