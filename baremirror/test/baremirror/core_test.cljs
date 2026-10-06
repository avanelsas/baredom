(ns baremirror.core-test
  (:require [baremirror.core :as mirror]
            [baremirror.generators :as generators]
            [baremirror.plan :as plan]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(def ^:private stage-attr "data-baremirror-test")

(defn- detach! [^js node]
  (.remove node))

(defn- remove-stages! []
  (run! detach! (array-seq (.querySelectorAll js/document (str "[" stage-attr "]")))))

(use-fixtures :each {:after remove-stages!})

(defn- element! [tag]
  (.createElement js/document tag))

(defn- keyed-element! [tag k]
  (doto (element! tag) (.setAttribute mirror/attr-key k)))

(defn- make-item! [_k]
  (element! "li"))

(defn- stage! []
  (let [stage (doto (element! "div") (.setAttribute stage-attr ""))]
    (.append (.-body js/document) stage)
    stage))

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
        reading    (mirror/read-places containers)
        p          (plan/plan (:places reading) wanted)
        observers  (mapv watch! (vals containers))
        nodes      (mirror/perform! reading p make-item!)
        facts      {:placements (:place p)
                    :before     (:nodes reading)
                    :nodes      nodes
                    :after      (mirror/read-places containers)
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
    (let [reading (mirror/read-places {:list {:parent parent}})]
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

(deftest perform-removes-and-releases
  (let [containers (mount! {:list ["a" "b" "c"]})
        reading    (mirror/read-places containers)
        a          (get-in reading [:nodes "a"])
        b          (get-in reading [:nodes "b"])
        nodes      (mirror/perform! reading {:remove ["a"] :release ["b"] :place []} make-item!)]
    (testing "a removed node leaves the document and loses its key"
      (is (false? (.-isConnected a)))
      (is (false? (.hasAttribute a mirror/attr-key))))
    (testing "a released node stays in the document and loses its key"
      (is (true? (.-isConnected b)))
      (is (false? (.hasAttribute b mirror/attr-key))))
    (testing "the returned nodes are those of the keys that remain"
      (is (= #{"c"} (set (keys nodes)))))
    (testing "the places no longer hold the removed and the released key"
      (is (= {:list ["c"]} (:places (mirror/read-places containers)))))))

(defn- key-or-tag [^js node]
  (or (.getAttribute node mirror/attr-key) (.-tagName node)))

(deftest perform-places-before-the-anchor
  (let [parent     (parent-with! (stage!) "div" "span" ["a"])
        anchor     (element! "input")
        containers {:chips {:parent parent :before anchor}}]
    (.append parent anchor)
    (.prepend parent (element! "label"))
    (let [reading (mirror/read-places containers)]
      (mirror/perform! reading (plan/plan (:places reading) {:chips ["b" "a" "c"]}) make-item!)
      (testing "new and moved nodes go before the anchor, and nodes without a key stay where they are"
        (is (= ["LABEL" "b" "a" "c" "INPUT"]
               (mapv key-or-tag (array-seq (.-children parent)))))))))

(deftest perform-works-on-a-parent-outside-the-document
  (let [containers {:list {:parent (element! "ul")}}
        reading    (mirror/read-places containers)]
    (mirror/perform! reading (plan/plan (:places reading) {:list ["a" "b"]}) make-item!)
    (is (= {:list ["a" "b"]} (:places (mirror/read-places containers))))))

(deftest perform-keeps-the-focus-of-a-moved-node-where-the-browser-can-move
  (let [parent     (parent-with! (stage!) "div" "button" ["a" "b" "c"])
        containers {:list {:parent parent}}
        reading    (mirror/read-places containers)
        moved      (get-in reading [:nodes "a"])]
    (.focus moved)
    (mirror/perform! reading (plan/plan (:places reading) {:list ["b" "c" "a"]}) make-item!)
    (testing "the moved node is the same node, at the end"
      (is (identical? moved (.-lastElementChild parent))))
    (when (.-moveBefore parent)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))
