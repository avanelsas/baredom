(ns baremirror.alpha.plan
  "The plan that turns the current places into the wanted places.")

(defn- first-at-least
  "The first position below `length` whose run ends at a rank of `r` or more, or `length`.
   `ends` holds, for each length of run, the position that the best run of that length ends at."
  [ranks ends length r]
  (loop [low 0 high length]
    (if (< low high)
      (let [middle (quot (+ low high) 2)]
        (if (< (aget ranks (aget ends middle)) r)
          (recur (inc middle) high)
          (recur low middle)))
      low)))

(defn- take-in!
  "Takes position `i` into the runs, of which the longest has `length` positions. Returns the
   length of the longest run after it."
  [ranks ends before length i]
  (let [at (first-at-least ranks ends length (aget ranks i))]
    (aset before i (if (pos? at) (aget ends (dec at)) -1))
    (aset ends at i)
    (max length (inc at))))

(defn- mark-run!
  "Marks in `stays` each position of the run that ends at position `end`. `before` holds the
   position that comes before each position in its run, or -1."
  [stays before end]
  (loop [i end]
    (when-not (neg? i)
      (aset stays i true)
      (recur (aget before i))))
  stays)

;; Patience sorting: one pass over the ranks, and a binary search for each. The arrays `ends`
;; and `before` never leave this function.
(defn- longest-rising
  "The positions of the array `ranks` that are in one longest run that rises, as an array with
   true at each of them. A position with no rank is in no run."
  [ranks]
  (let [n      (alength ranks)
        ends   (object-array n)
        before (object-array n)
        length (loop [i 0 length 0]
                 (cond
                   (== i n)              length
                   (nil? (aget ranks i)) (recur (inc i) length)
                   :else                 (recur (inc i) (take-in! ranks ends before length i))))]
    (cond-> (object-array n)
      (pos? length) (mark-run! before (aget ends (dec length))))))

#?(:cljs
   (defn- string-so-far [_ x]
     (or (string? x) (reduced false))))

#?(:cljs
   (defn- strings?
     "True when every item of `xs` is a string."
     [xs]
     (reduce string-so-far true xs)))

#?(:cljs
   (defn- note-position! [^js found i k]
     (.set found k i)
     (inc i)))

#?(:cljs
   (defn- position-in [^js found k]
     (.get found k)))

;; The map of the host finds a string faster than a persistent map does, and it never leaves
;; the function that is returned. It finds an object by identity, so it is for strings only.
#?(:cljs
   (defn- string-positions
     "A function of a key that gives its position in the strings `ks`, or nil."
     [ks]
     (let [found (js/Map.)]
       (reduce (partial note-position! found) 0 ks)
       (partial position-in found))))

(defn- positions
  "A function of a key that gives its position in `ks`, or nil."
  [ks]
  (or #?(:cljs (when (strings? ks) (string-positions ks)))
      (zipmap ks (range))))

(defn- ranks
  "The position in `current` of each key of `wanted`, as an array, or nil for a key it lacks."
  [current wanted]
  (object-array (mapv (positions current) wanted)))

#?(:cljs
   (defn- note-key! [^js found k]
     (doto found (.add k))))

#?(:cljs
   (defn- note-keys! [found ks]
     (reduce note-key! found ks)))

#?(:cljs
   (defn- noted? [^js found k]
     (.has found k)))

(defn- among
  "A function of a key that is true when the key is in one of the sequences `kss`."
  [kss]
  (or #?(:cljs (when (every? strings? kss)
                 (partial noted? (reduce note-keys! (js/Set.) kss))))
      (into #{} cat kss)))

(defn- same-from-start?
  "True when `xs` and `ys` hold the same item at position `i`."
  [xs ys i]
  (= (nth xs i) (nth ys i)))

(defn- same-from-end?
  "True when `xs` and `ys` hold the same item at position `i`, counted from the end."
  [xs ys i]
  (= (nth xs (- (count xs) i 1)) (nth ys (- (count ys) i 1))))

(defn- shared-length
  "How many of the first `limit` positions pass `same?`, counted until one fails."
  [same? limit]
  (transduce (comp (take-while same?) (map (constantly 1))) + (range limit)))

(defn- shared-ends
  "How many keys `xs` and `ys` share at the start and, after those, at the end."
  [xs ys]
  (let [limit (min (count xs) (count ys))
        head  (shared-length (partial same-from-start? xs ys) limit)]
    [head (shared-length (partial same-from-end? xs ys) (- limit head))]))

(defn- middle
  "The part of `xs` between the shared ends."
  [xs [head tail]]
  (subvec xs head (- (count xs) tail)))

(defn- placement
  "The placement of the key at position `i` of `wanted`, before the key that follows it."
  [container wanted i]
  {:key (nth wanted i) :in container :before (get wanted (inc i))})

(defn- stays-at?
  "True when the key at position `i` of the wanted keys stays where it is. `stays` starts at
   the position `head`."
  [stays head i]
  (aget stays (- i head)))

(defn- placements
  "The placements that turn `current` into `wanted` in one container, in the order to perform them.
   The keys that both share at the start and at the end are left alone."
  [container current wanted]
  (let [[head tail :as ends] (shared-ends current wanted)
        stays                (longest-rising (ranks (middle current ends) (middle wanted ends)))]
    (into []
          (comp (remove (partial stays-at? stays head))
                (map (partial placement container wanted)))
          (range (- (count wanted) tail 1) (dec head) -1))))

(defn- container-placements
  "The placements for `container`, which either side may leave out."
  [current wanted container]
  (placements container (vec (get current container)) (vec (get wanted container))))

(defn plan
  "What must happen to turn the `current` places into the `wanted` places.
   Both map a container to its keys, in order. A key is in at most one container."
  [current wanted]
  (if (= current wanted)
    {:remove [] :place []}
    (let [wanted-anywhere (among (vals wanted))]
      {:remove (into [] (comp cat (remove wanted-anywhere)) (vals current))
       :place  (into []
                     (mapcat (partial container-placements current wanted))
                     (distinct (concat (keys current) (keys wanted))))})))

(defn- without-keys
  "The `places` with none of `ks` in any container."
  [places ks]
  (update-vals places (partial into [] (remove (set ks)))))

(defn- insert-before
  "The keys `ks` with `k` before the key `before`, or at the end."
  [ks before k]
  (let [[head tail] (split-with (partial not= before) ks)]
    (-> (vec head) (conj k) (into tail))))

(defn- with-placement
  "The `places` after one placement."
  [places {:keys [key in before]}]
  (update (without-keys places [key]) in (fnil insert-before []) before key))

(defn perform
  "The places after `plan` is performed on `places`."
  [places {removed :remove placements :place}]
  (reduce with-placement
          (without-keys places removed)
          placements))
