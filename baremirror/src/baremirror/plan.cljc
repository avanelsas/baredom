(ns baremirror.plan
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
  "A longest run of `xs` whose ranks rise, as a set."
  [rank xs]
  (set (some-> (reduce (partial extend-runs rank) (sorted-map) xs) rseq first val)))

(defn- stays
  "The keys of `wanted` that keep their place in `current`."
  [current wanted]
  (let [rank (zipmap current (range))]
    (longest-rising rank (filter rank wanted))))

(defn- placements
  "The placements that turn `current` into `wanted` in one container, in the order to perform them."
  [container current wanted]
  (let [stay (stays current wanted)]
    (for [[k before] (reverse (partition 2 1 [nil] wanted))
          :when      (not (stay k))]
      {:key k :in container :before before})))

(defn plan
  "What must happen to turn the `current` places into the `wanted` places.
   Both map a container to its keys, in order. A key is in at most one container."
  [current wanted]
  (let [wanted-anywhere (into #{} cat (vals wanted))]
    {:remove (into [] (comp cat (remove wanted-anywhere)) (vals current))
     :place  (vec (for [container (distinct (concat (keys current) (keys wanted)))
                        placement (placements container (get current container) (get wanted container))]
                    placement))}))
