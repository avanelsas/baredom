(ns baremirror.alpha.generators
  "Generators shared by the tests."
  (:require [baremirror.alpha.plan :as plan]
            [clojure.test.check.generators :as gen]))

(defn- put [places [k container]]
  (update places container conj k))

(defn places
  "A generator of places: up to `limit` keys of `pool`, in any order, spread over `containers`."
  [containers pool limit]
  (gen/let [ks (gen/vector-distinct (gen/elements pool) {:max-elements limit})
            cs (gen/vector (gen/elements containers) (count ks))]
    (reduce put (zipmap containers (repeat [])) (map vector ks cs))))

(defn- edit-of
  "A generator of one edit: what to do, which key, and where it goes."
  [containers]
  (gen/hash-map :kind      (gen/elements [:move :move :remove :add])
                :pick      gen/nat
                :container (gen/elements containers)
                :position  gen/nat))

(defn- placement-at
  "The placement of `k` in the container of `edit`, at the position the edit picks."
  [places {:keys [container position]} k]
  (let [others (get (plan/perform places {:remove [k]}) container)]
    {:key k :in container :before (get others (mod position (inc (count others))))}))

(defn- edit
  "The `places` after the numbered `edit`. An edit that needs a key does nothing when there is none."
  [places [n {:keys [kind pick] :as edit}]]
  (let [ks (into [] cat (vals places))
        k  (when (seq ks) (nth ks (mod pick (count ks))))]
    (cond
      (= :add kind)    (plan/perform places {:place [(placement-at places edit (str "new" n))]})
      (nil? k)         places
      (= :remove kind) (plan/perform places {:remove [k]})
      :else            (plan/perform places {:place [(placement-at places edit k)]}))))

(defn edited
  "A generator of places and a copy with up to `limit` edits: keys moved, removed and added."
  [containers gen-places limit]
  (gen/let [places gen-places
            edits  (gen/vector (edit-of containers) 0 limit)]
    [places (reduce edit places (map-indexed vector edits))]))

(defn- put-in-tree
  "The `places` with the numbered key `k` in `root` or in the container of a key before it.
   The container of a key is the key itself."
  [root ks places [i [k pick]]]
  (let [holders (into [root] (take i ks))]
    (update places (nth holders (mod pick (count holders))) (fnil conj []) k)))

(defn tree
  "A generator of places that form a tree: up to `limit` keys of `pool`, each in `root` or in
   the container of a key before it."
  [root pool limit]
  (gen/let [ks    (gen/vector-distinct (gen/elements pool) {:max-elements limit})
            picks (gen/vector gen/nat (count ks))]
    (reduce (partial put-in-tree root ks) {root []} (map-indexed vector (map vector ks picks)))))
