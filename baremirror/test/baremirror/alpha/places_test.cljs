(ns baremirror.alpha.places-test
  (:require [baremirror.alpha.generators :as generators]
            [baremirror.alpha.places :as mirror-places]
            [baremirror.alpha.plan :as plan]
            [baremirror.alpha.test-stage :refer [element! remove-stages! stage!]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(use-fixtures :each {:after remove-stages!})

(defn- keyed-element! [tag k]
  (doto (element! tag) (.setAttribute mirror-places/attr-key k)))

(defn- make-item! [_k]
  (element! "li"))

(defn- parent-with!
  "A parent of `tag` in `stage` that holds a keyed `child-tag` for each of `ks`."
  [^js stage tag child-tag ks]
  (let [parent (element! tag)]
    (run! (fn [k] (.append parent (keyed-element! child-tag k))) ks)
    (.append stage parent)
    parent))

(defn- container! [^js stage ks]
  {:parent (parent-with! stage "ul" "li" ks)})

(defn- mount!
  "Containers in a new stage whose parents hold `places`."
  [places]
  (update-vals places (partial container! (stage!))))

(defn- watch! [{:keys [^js parent]}]
  (doto (js/MutationObserver. (fn [_ _]))
    (.observe parent #js {:childList true})))

(defn- added-nodes!
  "The number of nodes `observer` saw added. Stops the observer."
  [^js observer]
  (let [records (array-seq (.takeRecords observer))]
    (.disconnect observer)
    (transduce (map (fn [^js record] (.. record -addedNodes -length))) + records)))

(defn- perform-case!
  "The facts of one case: `current` is mounted, the plan to `wanted` is performed, the page is cleaned."
  [current wanted]
  (let [containers (mount! current)
        reading    (mirror-places/read-places containers)
        p          (plan/plan (:places reading) wanted)
        observers  (mapv watch! (vals containers))
        nodes      (mirror-places/perform! reading p make-item!)
        facts      {:placements (:place p)
                    :before     (:nodes reading)
                    :nodes      nodes
                    :after      (mirror-places/read-places containers)
                    :added      (transduce (map added-nodes!) + observers)
                    :expected   (plan/perform current p)}]
    (remove-stages!)
    facts))

(defn- all-keys [places]
  (into #{} cat (vals places)))

(defn- same-node? [nodes others k]
  (identical? (nodes k) (others k)))

(def ^:private gen-board
  (generators/places [:todo :doing :done] (mapv str (range 12)) 9))

(deftest read-places-reads-the-keyed-children
  (let [stage  (stage!)
        parent (parent-with! stage "ul" "li" ["a" "b"])]
    (.prepend parent (element! "li"))
    (.append (.-lastElementChild parent) (keyed-element! "span" "inner"))
    (let [reading (mirror-places/read-places {:list {:parent parent}})]
      (testing "the places are the keys of the keyed children, in document order"
        (is (= {:list ["a" "b"]} (:places reading))))
      (testing "the nodes are the children themselves"
        (is (identical? (.-lastElementChild parent) (get-in reading [:nodes "b"]))))
      (testing "a child without a key and a keyed node deeper down are left out"
        (is (= #{"a" "b"} (set (keys (:nodes reading)))))))))

(defspec perform-brings-the-document-to-the-wanted-places 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (= wanted (get-in (perform-case! current wanted) [:after :places]))))

(defspec perform-agrees-with-the-pure-perform 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [expected after]} (perform-case! current wanted)]
      (= expected (:places after)))))

(defspec perform-returns-the-node-of-each-wanted-key 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [nodes after]} (perform-case! current wanted)]
      (and (= (all-keys wanted) (set (keys nodes)))
           (every? (partial same-node? nodes (:nodes after)) (keys nodes))))))

(defspec perform-keeps-the-node-of-a-key-that-was-there 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [nodes before]} (perform-case! current wanted)]
      (every? (partial same-node? nodes before) (filter before (keys nodes))))))

(defspec perform-makes-one-new-node-for-each-new-key 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [nodes before]} (perform-case! current wanted)
          made                   (map nodes (remove before (keys nodes)))]
      (and (= (count made) (count (set made)))
           (not-any? (set (vals before)) made)))))

(defspec perform-adds-one-node-per-placement 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [placements added]} (perform-case! current wanted)]
      (= (count placements) added))))

(defn- sync-case!
  "The places and the nodes after `current` is mounted and brought to `wanted`. The page is cleaned."
  [current wanted]
  (let [containers (mount! current)
        nodes      (mirror-places/sync! containers wanted make-item!)
        after      (mirror-places/read-places containers)]
    (remove-stages!)
    {:nodes nodes :after after}))

(defspec sync-brings-the-document-to-the-wanted-places 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (= wanted (get-in (sync-case! current wanted) [:after :places]))))

(defspec sync-returns-the-nodes-that-are-in-the-document 100
  (prop/for-all [current gen-board
                 wanted  gen-board]
    (let [{:keys [nodes after]} (sync-case! current wanted)]
      (= nodes (:nodes after)))))

(deftest perform-removes-the-nodes-of-removed-keys
  (let [containers (mount! {:list ["a" "b"]})
        reading    (mirror-places/read-places containers)
        a          (get-in reading [:nodes "a"])
        nodes      (mirror-places/perform! reading {:remove ["a"] :place []} make-item!)]
    (testing "a removed node leaves the document and loses its key"
      (is (false? (.-isConnected a)))
      (is (false? (.hasAttribute a mirror-places/attr-key))))
    (testing "the returned nodes are those of the keys that remain"
      (is (= #{"b"} (set (keys nodes)))))
    (testing "the places no longer hold the removed key"
      (is (= {:list ["b"]} (:places (mirror-places/read-places containers)))))))

(deftest release-gives-a-node-up
  (let [containers (mount! {:list ["a" "b"]})
        a          (get-in (mirror-places/read-places containers) [:nodes "a"])]
    (mirror-places/release! a)
    (testing "the node stays in the document and loses its key"
      (is (true? (.-isConnected a)))
      (is (false? (.hasAttribute a mirror-places/attr-key))))
    (testing "the places no longer hold its key"
      (is (= {:list ["b"]} (:places (mirror-places/read-places containers)))))
    (testing "a later sync for the same key makes a new node"
      (is (not (identical? a (get (mirror-places/sync! containers {:list ["a" "b"]} make-item!) "a")))))))

(defn- key-or-tag [^js node]
  (or (.getAttribute node mirror-places/attr-key) (.-tagName node)))

(deftest perform-places-before-the-anchor
  (let [parent     (parent-with! (stage!) "div" "span" ["a"])
        anchor     (element! "input")
        containers {:chips {:parent parent :before anchor}}]
    (.append parent anchor)
    (.prepend parent (element! "label"))
    (let [reading (mirror-places/read-places containers)]
      (mirror-places/perform! reading (plan/plan (:places reading) {:chips ["b" "a" "c"]}) make-item!)
      (testing "new and moved nodes go before the anchor, and nodes without a key stay where they are"
        (is (= ["LABEL" "b" "a" "c" "INPUT"]
               (mapv key-or-tag (array-seq (.-children parent)))))))))

(deftest perform-works-on-a-parent-outside-the-document
  (let [containers {:list {:parent (element! "ul")}}
        reading    (mirror-places/read-places containers)]
    (mirror-places/perform! reading (plan/plan (:places reading) {:list ["a" "b"]}) make-item!)
    (is (= {:list ["a" "b"]} (:places (mirror-places/read-places containers))))))

(deftest perform-keeps-the-focus-of-a-moved-node-where-the-browser-can-move
  (let [parent     (parent-with! (stage!) "div" "button" ["a" "b" "c"])
        containers {:list {:parent parent}}
        reading    (mirror-places/read-places containers)
        moved      (get-in reading [:nodes "a"])]
    (.focus moved)
    (mirror-places/perform! reading (plan/plan (:places reading) {:list ["b" "c" "a"]}) make-item!)
    (testing "the moved node is the same node, at the end"
      (is (identical? moved (.-lastElementChild parent))))
    (when (.-moveBefore parent)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))
