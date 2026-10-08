(ns baremirror.alpha.plan
  "The plan that turns the current places into the wanted places.")

(defn- extend-runs
  "The runs after `x` is taken in. `runs` maps a rank to the best run that ends at it."
  [rank runs x]
  (let [r     (rank x)
        above (first (subseq runs >= r))
        below (first (rsubseq runs < r))]
    (assoc (cond-> runs above (dissoc (key above)))
           r (conj (some-> below val) x))))

(defn- longest-rising
  "A longest run of `xs` whose ranks rise, as a set. An item without a rank is left out."
  [rank xs]
  (set (some-> (transduce (filter rank) (completing (partial extend-runs rank)) (sorted-map) xs)
               rseq
               first
               val)))

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

(defn- placements
  "The placements that turn `current` into `wanted` in one container, in the order to perform them.
   The keys that both share at the start and at the end are left alone."
  [container current wanted]
  (let [[head tail :as ends] (shared-ends current wanted)
        stay                 (longest-rising (zipmap (middle current ends) (range))
                                             (middle wanted ends))]
    (into []
          (comp (remove (comp stay wanted))
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
    (let [wanted-anywhere (into #{} cat (vals wanted))]
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
