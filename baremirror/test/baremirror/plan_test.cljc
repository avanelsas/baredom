(ns baremirror.plan-test
  (:require [baremirror.plan :as plan]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def ^:private containers [:todo :doing :done])

(def ^:private pool (mapv str (range 14)))

(defn- put [places [k container]]
  (update places container conj k))

(defn- gen-places
  "A generator of places: up to `limit` keys of `pool`, in any order, spread over `containers`."
  [containers pool limit]
  (gen/let [ks (gen/vector-distinct (gen/elements pool) {:max-elements limit})
            cs (gen/vector (gen/elements containers) (count ks))]
    (reduce put (zipmap containers (repeat [])) (map vector ks cs))))

(def ^:private gen-board (gen-places containers pool 10))

(def ^:private gen-small (gen-places [:todo :done] (subvec pool 0 6) 5))

(defn- without [k ks]
  (filterv (partial not= k) ks))

(defn- insert-before [ks before k]
  (let [[head tail] (split-with (partial not= before) ks)]
    (-> (vec head) (conj k) (into tail))))

(defn- take-out [places k]
  (update-vals places (partial without k)))

(defn- place [places {:keys [key in before]}]
  (update (take-out places key) in (fnil insert-before []) before key))

(defn- perform
  "The places after `plan` is performed on `current`, with the containers taken in `order`."
  [current {removed :remove placements :place} order]
  (reduce place
          (reduce take-out current removed)
          (mapcat (group-by :in placements) order)))

(defn- arrivals
  "Every places value that one placement of one of `ks` can make of `places`."
  [ks places]
  (for [k         ks
        :let      [others (take-out places k)]
        container (keys others)
        i         (range (inc (count (get others container))))]
    (place places {:key k :in container :before (get-in others [container i])})))

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
    (= wanted (perform current (plan/plan current wanted) order))))

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
      (= (fewest-placements (reduce take-out current removed) wanted)
         (count placements)))))
