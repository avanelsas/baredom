(ns baremirror.alpha.plan-test
  (:require [baremirror.alpha.generators :as generators]
            [baremirror.alpha.plan :as plan]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def ^:private containers [:todo :doing :done])

(def ^:private pool (mapv str (range 14)))

(def ^:private gen-board (generators/places containers pool 10))

(def ^:private gen-small (generators/places [:todo :done] (subvec pool 0 6) 5))

(def ^:private gen-edited
  (generators/edited containers
                     (generators/places containers (mapv str (range 60)) 40)
                     4))

(defn- in-order
  "The `plan` with its placements grouped by container, the containers taken in `order`."
  [order plan]
  (assoc plan :place (vec (mapcat (group-by :in (:place plan)) order))))

(defn- arrivals
  "Every places value that one placement of one of `ks` can make of `places`."
  [ks places]
  (for [k         ks
        :let      [others (plan/perform places {:remove [k]})]
        container (keys others)
        i         (range (inc (count (get others container))))]
    (plan/perform places {:place [{:key k :in container :before (get-in others [container i])}]})))

(defn- next-wave
  "The places one more placement away, and everything seen so far."
  [ks [frontier seen]]
  (let [reached (into #{} (comp (mapcat (partial arrivals ks)) (remove seen)) frontier)]
    [reached (into seen reached)]))

(defn- fewest-placements
  "The fewest placements that turn `start` into `wanted`, found by trying every placement."
  [start wanted]
  (let [ks (into [] cat (vals wanted))]
    (->> (iterate (partial next-wave ks) [#{start} #{start}])
         (map first)
         (take (inc (count ks)))
         (take-while (complement (partial some #{wanted})))
         count)))

(defn- extend-lengths
  "The `lengths` with the rank `r` and the length of the longest rising run that ends at it."
  [lengths r]
  (conj lengths [r (inc (transduce (comp (filter (comp (partial > r) first)) (map second))
                                   max 0 lengths))]))

(defn- longest-rising-length
  "The length of a longest rising run in `ranks`, found by comparing every pair."
  [ranks]
  (transduce (map second) max 0 (reduce extend-lengths [] ranks)))

(defn- fewest-in-container
  "The fewest placements one container needs, by the plain method with no shortcut."
  [current wanted container]
  (let [rank (zipmap (get current container) (range))
        ks   (get wanted container)]
    (- (count ks) (longest-rising-length (keep rank ks)))))

(defn- fewest-by-counting [current wanted]
  (transduce (map (partial fewest-in-container current wanted)) + (keys wanted)))

(defn- all-keys [places]
  (into #{} cat (vals places)))

(deftest plan-of-small-cases
  (testing "a key that changes container is placed and not removed"
    (is (= {:remove []
            :place  [{:key "a" :in :doing :before "c"}
                     {:key "d" :in :done :before nil}]}
           (plan/plan (array-map :todo ["a" "b"] :doing ["c"] :done [])
                      (array-map :todo ["b"] :doing ["a" "c"] :done ["d"])))))
  (testing "a key that is wanted nowhere is removed"
    (is (= {:remove ["b"] :place []}
           (plan/plan {:todo ["a" "b" "c"]} {:todo ["a" "c"]}))))
  (testing "a key moved from first to last is the one placement"
    (is (= {:remove [] :place [{:key "a" :in :todo :before nil}]}
           (plan/plan {:todo ["a" "b" "c" "d"]} {:todo ["b" "c" "d" "a"]}))))
  (testing "a container that only one side names is planned"
    (is (= {:remove ["a"] :place [{:key "b" :in :done :before nil}]}
           (plan/plan {:todo ["a"]} {:done ["b"]}))))
  (testing "two keys swapped in the middle of a list are one placement"
    (let [current {:todo ["a" "b" "c" "d" "e"]}
          wanted  {:todo ["a" "c" "b" "d" "e"]}
          p       (plan/plan current wanted)]
      (is (= 1 (count (:place p))))
      (is (= wanted (plan/perform current p)))))
  (testing "a key added between keys that stay is the one placement"
    (is (= {:remove [] :place [{:key "x" :in :todo :before "b"}]}
           (plan/plan {:todo ["a" "b"]} {:todo ["a" "x" "b"]}))))
  (testing "the plan of no places is empty"
    (is (= {:remove [] :place []} (plan/plan {} {})))))

(deftest a-move-in-two-thousand-keys-is-one-placement
  (let [current (mapv str (range 2000))
        wanted  (conj (subvec current 1) (first current))]
    (is (= {:remove [] :place [{:key "0" :in :rows :before nil}]}
           (plan/plan {:rows current} {:rows wanted})))))

(defspec performing-the-plan-gives-the-wanted-places 1000
  (prop/for-all [current gen-board
                 wanted  gen-board
                 order   (gen/shuffle containers)]
    (= wanted (plan/perform current (in-order order (plan/plan current wanted))))))

(defspec plan-removes-exactly-the-keys-wanted-nowhere 1000
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [removed (:remove (plan/plan current wanted))]
      (and (= (count removed) (count (set removed)))
           (= (set removed) (into #{} (remove (all-keys wanted)) (all-keys current)))))))

(defspec plan-places-only-wanted-keys-and-each-at-most-once 1000
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [placed (map :key (:place (plan/plan current wanted)))]
      (and (= (count placed) (count (set placed)))
           (every? (all-keys wanted) placed)))))

(defspec plan-of-equal-places-is-empty 500
  (prop/for-all [places gen-board]
    (= {:remove [] :place []} (plan/plan places places))))

(defspec plan-uses-the-fewest-placements 300
  (prop/for-all [current gen-small
                 wanted  gen-small]
    (let [{removed :remove placements :place} (plan/plan current wanted)]
      (= (fewest-placements (plan/perform current {:remove removed}) wanted)
         (count placements)))))

(deftest perform-of-small-cases
  (testing "a removed key leaves the places"
    (is (= {:todo ["a"]}
           (plan/perform {:todo ["a" "b"]} {:remove ["b"]}))))
  (testing "a placement into a container the places do not name adds it"
    (is (= {:todo [] :done ["a"]}
           (plan/perform {:todo ["a"]} {:place [{:key "a" :in :done :before nil}]})))))

(defspec performing-the-plan-of-an-edited-copy-gives-the-copy 500
  (prop/for-all [[current wanted] gen-edited]
    (= wanted (plan/perform current (plan/plan current wanted)))))

(defspec plan-of-an-edited-copy-uses-the-fewest-placements 500
  (prop/for-all [[current wanted] gen-edited]
    (= (fewest-by-counting current wanted)
       (count (:place (plan/plan current wanted))))))
