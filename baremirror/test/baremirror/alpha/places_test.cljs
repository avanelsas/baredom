(ns baremirror.alpha.places-test
  (:require [baremirror.alpha.generators :as generators]
            [baremirror.alpha.places :as mirror-places]
            [baremirror.alpha.plan :as plan]
            [baremirror.alpha.template :as template]
            [baremirror.alpha.test-stage :refer [element! remove-stages! stage!]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(use-fixtures :each {:after remove-stages!})

(defn- keyed-element! [tag k]
  (doto (element! tag) (.setAttribute mirror-places/attr-key k)))

(defn- make-item! [_k]
  (element! "li"))

(defn- parent-of
  "A parent of `tag` that holds a keyed `child-tag` for each of `ks`."
  [tag child-tag ks]
  (let [parent (element! tag)]
    (run! (fn [k] (.append parent (keyed-element! child-tag k))) ks)
    parent))

(defn- parent-with!
  "A parent of `tag` in `stage` that holds a keyed `child-tag` for each of `ks`."
  [^js stage tag child-tag ks]
  (let [parent (parent-of tag child-tag ks)]
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

(defn- mounted!
  "The `node`, put in a new stage."
  [^js node]
  (.append (stage!) node)
  node)

(defn- branch!
  "A keyed `li` for `k` that holds a `ul` with a keyed `button` for each of `ks`."
  [k ks]
  (doto (keyed-element! "li" k)
    (.append (parent-of "ul" "button" ks))))

(deftest perform-keeps-the-focus-of-a-node-it-moves-out-of-a-removed-node
  (let [a          (branch! "A" ["x"])
        b          (branch! "B" ["z"])
        root       (mounted! (doto (element! "ul") (.append a b)))
        containers {:root {:parent root}
                    :A    {:parent (.-lastElementChild a)}
                    :B    {:parent (.-lastElementChild b)}}
        reading    (mirror-places/read-places containers)
        moved      (get-in reading [:nodes "x"])]
    (.focus moved)
    (mirror-places/perform! reading
                            (plan/plan (:places reading) {:root ["B"] :B ["x" "z"]})
                            make-item!)
    (testing "the node is the same node, first in the other container"
      (is (identical? moved (.. b -lastElementChild -firstElementChild))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(defn- holding!
  "A keyed `li` for `k` that holds a `ul` with `child` in it."
  [k ^js child]
  (doto (keyed-element! "li" k)
    (.append (doto (element! "ul") (.append child)))))

(deftest perform-keeps-the-focus-of-a-node-it-moves-into-a-new-node
  (let [a          (branch! "A" ["x"])
        n          (branch! "N" [])
        root       (mounted! (doto (element! "ul") (.append a)))
        containers {:N    {:parent (.-lastElementChild n)}
                    :A    {:parent (.-lastElementChild a)}
                    :root {:parent root}}
        reading    (mirror-places/read-places containers)
        moved      (get-in reading [:nodes "x"])]
    (.focus moved)
    (mirror-places/perform! reading
                            {:remove [] :place [{:key "x" :in :N :before nil}
                                                {:key "N" :in :root :before nil}]}
                            {"N" n})
    (testing "the node is the same node, in the new node"
      (is (identical? moved (.. n -lastElementChild -firstElementChild))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest perform-keeps-the-focus-of-a-node-it-moves-into-a-node-that-stays-inside-a-new-node
  (let [a          (branch! "A" ["x"])
        k          (branch! "K" [])
        n          (holding! "N" k)
        root       (mounted! (doto (element! "ul") (.append a)))
        containers {:K    {:parent (.-lastElementChild k)}
                    :A    {:parent (.-lastElementChild a)}
                    :root {:parent root}}
        reading    (mirror-places/read-places containers)
        moved      (get-in reading [:nodes "x"])]
    (.focus moved)
    (mirror-places/perform! reading
                            {:remove [] :place [{:key "x" :in :K :before nil}
                                                {:key "N" :in :root :before nil}]}
                            {"N" n})
    (testing "the node is the same node, in the node inside the new node"
      (is (identical? moved (.. k -lastElementChild -firstElementChild))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest perform-puts-a-node-into-the-node-it-was-in
  (let [b          (branch! "B" [])
        a          (holding! "A" b)
        root       (mounted! (doto (element! "ul") (.append a)))
        containers {:B    {:parent (.-lastElementChild b)}
                    :A    {:parent (.-lastElementChild a)}
                    :root {:parent root}}]
    (mirror-places/perform! (mirror-places/read-places containers)
                            {:remove [] :place [{:key "A" :in :B :before nil}
                                                {:key "B" :in :root :before nil}]}
                            make-item!)
    (testing "the node that was inside holds the other"
      (is (= {:B ["A"] :A [] :root ["B"]} (:places (mirror-places/read-places containers)))))))

(deftest perform-refuses-a-key-inside-its-own-node-and-writes-nothing
  (let [a          (branch! "A" [])
        root       (mounted! (doto (element! "ul") (.append a)))
        containers {:A {:parent (.-lastElementChild a)} :root {:parent root}}
        reading    (mirror-places/read-places containers)]
    (is (thrown-with-msg? js/Error #"inside its own node"
                          (mirror-places/perform! reading
                                                  {:remove [] :place [{:key "A" :in :A :before nil}]}
                                                  make-item!)))
    (testing "the page is as it was"
      (is (= {:A [] :root ["A"]} (:places (mirror-places/read-places containers)))))))

(def ^:private part-children "children")

(defn- tree-node!
  "A keyed `li` for `k` that holds a `ul`, the part `children`."
  [k]
  (doto (keyed-element! "li" k)
    (.append (doto (element! "ul") (.setAttribute template/attr-part part-children)))))

(defn- children-vec
  "The element children of `parent`, as a vector."
  [^js parent]
  (vec (array-seq (.-children parent))))

(defn- focusable-tree-node!
  "A tree node for `k` that can take the focus."
  [k]
  (doto (tree-node! k) (.setAttribute "tabindex" "-1")))

(defn- children-of ^js [^js node]
  (.-lastElementChild node))

(defn- with-child!
  "The tree `node`, with `child` in its part `children`."
  [^js node ^js child]
  (.append (children-of node) child)
  node)

(defn- key-container
  "The container named by `k` and its part `children`."
  [k]
  {:key k :part part-children})

(defn- tree-containers
  "The containers of a tree under `root`: the root, and one named by each of `ks`."
  [^js root ks]
  (into {:root {:parent root}} (map (juxt identity key-container)) ks))

(defn- part-containers
  "The containers of a tree under `root`: the root, and the part `children` of every key."
  [^js root]
  {:root {:parent root} :rows {:part part-children}})

(defn- grow!
  "Appends to `parent` a tree node for each key of `container` in `places`, with its own keys
   below it."
  [^js parent places container]
  (doseq [k (get places container)]
    (let [node (tree-node! k)]
      (.append parent node)
      (grow! (children-of node) places k))))

(defn- filled
  "The `places` without the containers that hold no key."
  [places]
  (into {} (remove (comp empty? val)) places))

(defn- refuse-to-make! [_k]
  (throw (js/Error. "make-node was called")))

(deftest read-places-gives-a-container-named-by-a-key-its-parent
  (let [a       (with-child! (tree-node! "a") (tree-node! "b"))
        root    (mounted! (doto (element! "ul") (.append a)))
        reading (mirror-places/read-places (assoc (tree-containers root ["a" "b" "c"])
                                                  :self {:key "a"}))]
    (testing "a key and a part name that part of the node of the key"
      (is (identical? (children-of a) (get-in reading [:containers "a" :parent]))))
    (testing "a key alone names the node of the key"
      (is (identical? a (get-in reading [:containers :self :parent]))))
    (testing "a container inside a container named by a key is read too"
      (is (= ["b"] (get-in reading [:places "a"])))
      (is (= #{"a" "b"} (set (keys (:nodes reading))))))
    (testing "a key with no node gives no parent and no keys"
      (is (nil? (get-in reading [:containers "c" :parent])))
      (is (= [] (get-in reading [:places "c"]))))))

(deftest read-places-refuses-a-part-that-is-only-in-a-keyed-node-below
  (let [a    (doto (keyed-element! "li" "a") (.append (tree-node! "b")))
        root (mounted! (doto (element! "ul") (.append a)))]
    (is (thrown-with-msg? js/Error #"no part of that name"
                          (mirror-places/read-places (tree-containers root ["a"]))))))

(deftest read-places-refuses-a-container-that-names-its-parent-both-ways-or-neither
  (is (thrown-with-msg? js/Error #"a :parent, a :key, or a :part alone"
                        (mirror-places/read-places {:c {:parent (element! "ul") :key "a"}})))
  (is (thrown-with-msg? js/Error #"a :parent, a :key, or a :part alone"
                        (mirror-places/read-places {:c {}}))))

(deftest read-places-refuses-a-before-beside-a-key
  (is (thrown-with-msg? js/Error #"takes no :before"
                        (mirror-places/read-places {:c {:key "a" :before (element! "li")}}))))

(deftest sync-makes-a-tree-of-new-nodes-in-one-call
  (let [root   (mounted! (element! "ul"))
        wanted {:root ["a"] "a" ["b"] "b" ["c"]}]
    (mirror-places/sync! (tree-containers root ["a" "b" "c"]) wanted tree-node!)
    (is (= wanted (filled (:places (mirror-places/read-places
                                    (tree-containers root ["a" "b" "c"]))))))))

(deftest sync-keeps-the-focus-of-a-node-that-moves-to-another-key
  (let [moved      (focusable-tree-node! "x")
        a          (with-child! (tree-node! "a") moved)
        b          (with-child! (tree-node! "b") (tree-node! "z"))
        root       (mounted! (doto (element! "ul") (.append a b)))]
    (.focus moved)
    (mirror-places/sync! (tree-containers root ["a" "b"])
                         {:root ["a" "b"] "a" [] "b" ["x" "z"]}
                         refuse-to-make!)
    (testing "the node is the same node, first under the other key"
      (is (identical? moved (.-firstElementChild (children-of b)))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest sync-keeps-the-focus-of-a-node-that-moves-under-a-new-key
  (let [moved (focusable-tree-node! "x")
        a     (with-child! (tree-node! "a") moved)
        root  (mounted! (doto (element! "ul") (.append a)))]
    (.focus moved)
    (let [nodes (mirror-places/sync! (tree-containers root ["a" "n"])
                                     {:root ["a" "n"] "a" [] "n" ["x"]}
                                     tree-node!)]
      (testing "the node is the same node, under the new key"
        (is (identical? moved (.-firstElementChild (children-of (nodes "n")))))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest sync-refuses-keys-under-a-key-that-is-not-wanted-and-writes-nothing
  (let [root (mounted! (element! "ul"))]
    (is (thrown-with-msg? js/Error #"named by a key that is not wanted"
                          (mirror-places/sync! (tree-containers root ["a"]) {"a" ["x"]}
                                               refuse-to-make!)))
    (is (= [] (children-vec root)))))

(deftest sync-refuses-keys-in-a-node-that-is-removed-and-writes-nothing
  (let [a    (tree-node! "a")
        root (mounted! (doto (element! "ul") (.append a)))]
    (is (thrown-with-msg? js/Error #"placed in a node that is removed"
                          (mirror-places/sync! (tree-containers root ["a"]) {:root [] "a" ["x"]}
                                               refuse-to-make!)))
    (is (= [a] (children-vec root)))
    (is (= [] (children-vec (children-of a))))))

(deftest sync-places-keys-under-a-node-that-leaves-a-removed-node
  (let [m    (with-child! (tree-node! "m") (tree-node! "y"))
        root (mounted! (doto (element! "ul") (.append (with-child! (tree-node! "r") m))))]
    (mirror-places/sync! (tree-containers root ["r" "m"]) {:root ["m"] "m" ["y" "z"]} tree-node!)
    (is (= {:root ["m"] "m" ["y" "z"]}
           (filled (:places (mirror-places/read-places (tree-containers root ["r" "m"]))))))))

(deftest sync-refuses-a-new-node-without-the-part-and-writes-nothing
  (let [root (mounted! (element! "ul"))]
    (is (thrown-with-msg? js/Error #"no part of that name"
                          (mirror-places/sync! (tree-containers root ["n"]) {:root ["n"] "n" ["x"]}
                                               make-item!)))
    (is (= [] (children-vec root)))))

(def ^:private tree-pool (mapv str (range 10)))

(def ^:private gen-tree (generators/tree :root tree-pool 8))

(defn- listed-containers
  "The containers of a tree under `root`, with one named by each key of the pool."
  [^js root]
  (tree-containers root tree-pool))

(defn- tree-case!
  "The nodes before and after `current` is grown and synced to `wanted` in the containers that
   `containers-of` gives for the root, and the places after. The page is cleaned."
  [containers-of current wanted]
  (let [root       (doto (mounted! (element! "ul")) (grow! current :root))
        containers (containers-of root)
        before     (:nodes (mirror-places/read-places containers))
        nodes      (mirror-places/sync! containers wanted tree-node!)
        after      (mirror-places/read-places containers)]
    (remove-stages!)
    {:before before :nodes nodes :after after}))

(defspec sync-brings-a-tree-to-the-wanted-places 100
  (prop/for-all [current gen-tree
                 wanted  gen-tree]
    (= (filled wanted) (filled (get-in (tree-case! listed-containers current wanted) [:after :places])))))

(defspec sync-keeps-the-node-of-each-key-that-was-in-the-tree 100
  (prop/for-all [current gen-tree
                 wanted  gen-tree]
    (let [{:keys [before nodes]} (tree-case! listed-containers current wanted)]
      (every? (partial same-node? nodes before) (filter before (keys nodes))))))

(defspec sync-brings-a-tree-to-the-wanted-places-with-a-part-alone 100
  (prop/for-all [current gen-tree
                 wanted  gen-tree]
    (= (filled wanted) (filled (get-in (tree-case! part-containers current wanted) [:after :places])))))

(defspec sync-keeps-the-node-of-each-key-that-was-in-the-tree-with-a-part-alone 100
  (prop/for-all [current gen-tree
                 wanted  gen-tree]
    (let [{:keys [before nodes]} (tree-case! part-containers current wanted)]
      (every? (partial same-node? nodes before) (filter before (keys nodes))))))

(deftest read-places-gives-every-key-a-container-through-a-part-alone
  (let [b       (tree-node! "b")
        a       (with-child! (tree-node! "a") b)
        root    (mounted! (doto (element! "ul") (.append a)))
        reading (mirror-places/read-places (part-containers root))]
    (testing "each key has a container named by it, an empty one too, and the entry itself is none"
      (is (= {:root ["a"] "a" ["b"] "b" []} (:places reading))))
    (testing "the container of a key is its part"
      (is (identical? (children-of b) (get-in reading [:containers "b" :parent]))))))

(deftest read-places-keeps-a-container-named-by-a-key-over-the-one-of-a-part-alone
  (let [a       (tree-node! "a")
        root    (mounted! (doto (element! "ul") (.append a)))
        reading (mirror-places/read-places (assoc (part-containers root) "a" {:key "a"}))]
    (is (identical? a (get-in reading [:containers "a" :parent])))))

(deftest sync-keeps-the-nodes-and-the-focus-of-the-keys-in-a-removed-node
  (let [moved (focusable-tree-node! "c")
        root  (mounted! (doto (element! "ul") (.append (with-child! (tree-node! "p") moved))))]
    (.focus moved)
    (mirror-places/sync! (part-containers root) {:root ["c"]} refuse-to-make!)
    (testing "the node is the same node, where the removed node was"
      (is (= [moved] (children-vec root))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest sync-makes-a-tree-of-new-nodes-with-a-part-alone
  (let [root   (mounted! (element! "ul"))
        wanted {:root ["a"] "a" ["b"] "b" ["c"]}]
    (mirror-places/sync! (part-containers root) wanted tree-node!)
    (is (= wanted (filled (:places (mirror-places/read-places (part-containers root))))))))

(deftest sync-keeps-the-focus-of-a-node-that-moves-under-a-new-key-with-a-part-alone
  (let [moved (focusable-tree-node! "x")
        root  (mounted! (doto (element! "ul") (.append (with-child! (tree-node! "a") moved))))]
    (.focus moved)
    (let [nodes (mirror-places/sync! (part-containers root) {:root ["a" "n"] "n" ["x"]} tree-node!)]
      (testing "the node is the same node, under the new key"
        (is (identical? moved (.-firstElementChild (children-of (nodes "n")))))))
    (when (.-moveBefore root)
      (testing "it still has the focus"
        (is (identical? moved (.-activeElement js/document)))))))

(deftest read-places-refuses-two-containers-of-a-part-alone
  (is (thrown-with-msg? js/Error #"one container names a part alone"
                        (mirror-places/read-places {:a {:part "children"} :b {:part "more"}}))))

(deftest read-places-refuses-a-before-beside-a-part-alone
  (is (thrown-with-msg? js/Error #"takes no :before"
                        (mirror-places/read-places {:c {:part "children" :before (element! "li")}}))))

(deftest read-places-refuses-a-key-that-names-another-container-beside-a-part-alone
  (let [root (mounted! (doto (element! "ul") (.append (tree-node! "root"))))]
    (is (thrown-with-msg? js/Error #"also the name of a container"
                          (mirror-places/read-places {"root" {:parent root}
                                                      :rows  {:part part-children}})))))

(deftest sync-refuses-a-new-key-that-names-another-container-beside-a-part-alone-and-writes-nothing
  (let [root (mounted! (element! "ul"))]
    (is (thrown-with-msg? js/Error #"also the name of a container"
                          (mirror-places/sync! {"root" {:parent root} :rows {:part part-children}}
                                               {"root" ["root"]}
                                               refuse-to-make!)))
    (is (= [] (children-vec root)))))

(deftest read-places-refuses-a-keyed-node-without-the-part-of-a-part-alone
  (let [root (mounted! (doto (element! "ul") (.append (keyed-element! "li" "a"))))]
    (is (thrown-with-msg? js/Error #"no part of that name"
                          (mirror-places/read-places (part-containers root))))))
