(ns baremirror.core-test
  (:require [baremirror.core :as mirror]
            [baremirror.generators :as generators]
            [baremirror.plan :as plan]
            [baremirror.template :as template]
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

(defn- sync-case!
  "The places and the nodes after `current` is mounted and brought to `wanted`. The page is cleaned."
  [current wanted]
  (let [containers (mount! current)
        nodes      (mirror/sync! containers wanted make-item!)
        after      (mirror/read-places containers)]
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
        reading    (mirror/read-places containers)
        a          (get-in reading [:nodes "a"])
        nodes      (mirror/perform! reading {:remove ["a"] :place []} make-item!)]
    (testing "a removed node leaves the document and loses its key"
      (is (false? (.-isConnected a)))
      (is (false? (.hasAttribute a mirror/attr-key))))
    (testing "the returned nodes are those of the keys that remain"
      (is (= #{"b"} (set (keys nodes)))))
    (testing "the places no longer hold the removed key"
      (is (= {:list ["b"]} (:places (mirror/read-places containers)))))))

(deftest release-gives-a-node-up
  (let [containers (mount! {:list ["a" "b"]})
        a          (get-in (mirror/read-places containers) [:nodes "a"])]
    (mirror/release! a)
    (testing "the node stays in the document and loses its key"
      (is (true? (.-isConnected a)))
      (is (false? (.hasAttribute a mirror/attr-key))))
    (testing "the places no longer hold its key"
      (is (= {:list ["b"]} (:places (mirror/read-places containers)))))
    (testing "a later sync for the same key makes a new node"
      (is (not (identical? a (get (mirror/sync! containers {:list ["a" "b"]} make-item!) "a")))))))

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

(defn- holding-element!
  "An element that offers a hold. The hold adds its start, its `this` and its end to `log`."
  [log]
  (doto (element! "div")
    (unchecked-set mirror/hold-key (fn [f]
                                     (this-as this
                                       (swap! log conj :hold this)
                                       (f)
                                       (swap! log conj :release))))))

(deftest with-one-render-calls-the-work-inside-the-hold-of-the-element
  (let [log (atom [])
        el  (holding-element! log)]
    (mirror/with-one-render! el (partial swap! log conj))
    (testing "the hold runs on the element, and the work gets the element"
      (is (= [:hold el el :release] @log)))))

(deftest with-one-render-calls-the-work-on-an-element-with-no-hold
  (let [log (atom [])
        el  (element! "div")]
    (mirror/with-one-render! el (partial swap! log conj))
    (is (= [el] @log))))

(deftest with-one-render-returns-nothing
  (testing "on an element with no hold"
    (is (nil? (mirror/with-one-render! (element! "div") identity))))
  (testing "on an element whose hold returns a value"
    (is (nil? (mirror/with-one-render! (holding-element! (atom [])) identity)))))

(defn- element-with!
  "An element of `tag` with the attributes `attrs`."
  [tag attrs]
  (let [el (element! tag)]
    (run! (fn [[k v]] (.setAttribute el k v)) attrs)
    el))

(defn- records-of!
  "The mutation records of `el` and its descendants that `f` causes."
  [^js el f]
  (let [observer (js/MutationObserver. identity)]
    (.observe observer el #js {:attributes true :characterData true :childList true :subtree true})
    (f)
    (let [records (vec (array-seq (.takeRecords observer)))]
      (.disconnect observer)
      records)))

(defn- record-type [^js record]
  (.-type record))

(defn- attribute-name [^js record]
  (.-attributeName record))

(defn- attr-logging-element!
  "An element that offers a hold.
   The hold adds the value of `attr` before and after the work to `log`."
  [log attr]
  (doto (element! "div")
    (unchecked-set mirror/hold-key (fn [f]
                                     (this-as ^js this
                                       (swap! log conj (.getAttribute this attr))
                                       (f)
                                       (swap! log conj (.getAttribute this attr)))))))

(deftest set-attrs-writes-only-the-attributes-that-differ
  (let [el      (element-with! "div" {"a" "1" "b" "2" "kept" "x"})
        records (records-of! el (fn [] (mirror/set-attrs! el (array-map :a "1" :b "3" :c "4"))))]
    (testing "an equal value is not written"
      (is (= ["b" "c"] (mapv attribute-name records))))
    (testing "the named attributes have their values"
      (is (= ["1" "3" "4"] (mapv (fn [k] (.getAttribute el k)) ["a" "b" "c"]))))
    (testing "an attribute that is not named stays"
      (is (= "x" (.getAttribute el "kept"))))))

(deftest set-attrs-gives-true-false-and-nil-their-meaning
  (let [el (element-with! "div" {"off" "" "gone" "1"})]
    (mirror/set-attrs! el {:on true :off false :gone nil :count 5 :variant :primary})
    (testing "true sets an empty attribute"
      (is (= "" (.getAttribute el "on"))))
    (testing "false and nil remove the attribute"
      (is (not (.hasAttribute el "off")))
      (is (not (.hasAttribute el "gone"))))
    (testing "a keyword is written as its name"
      (is (= "primary" (.getAttribute el "variant"))))
    (testing "another value is written as its text"
      (is (= "5" (.getAttribute el "count"))))))

(deftest set-attrs-writes-nothing-when-nothing-differs
  (let [el (element-with! "div" {"on" "" "count" "5"})]
    (is (= [] (records-of! el (fn [] (mirror/set-attrs! el {:on true :off false :count 5})))))))

(deftest set-attrs-writes-a-sequence-of-pairs-in-its-order
  (let [el (element! "div")]
    (is (= ["b" "a"]
           (mapv attribute-name
                 (records-of! el (fn [] (mirror/set-attrs! el [[:b "1"] [:a "2"]]))))))))

(deftest set-attrs-takes-a-name-as-a-string
  (let [el (element! "div")]
    (mirror/set-attrs! el {"data-state" "open"})
    (is (= "open" (.getAttribute el "data-state")))))

(deftest set-attrs-writes-inside-the-hold-of-the-element
  (let [log (atom [])
        el  (attr-logging-element! log "a")]
    (mirror/set-attrs! el {:a "1"})
    (is (= [nil "1"] @log))))

(deftest set-attrs-returns-nothing
  (is (nil? (mirror/set-attrs! (element! "div") {:a "1"}))))

(deftest set-text-writes-into-the-text-node-of-the-element
  (let [el      (doto (element! "span") (.append "old"))
        node    (.-firstChild el)
        records (records-of! el (fn [] (mirror/set-text! el "new")))]
    (testing "the text node stays and holds the text"
      (is (identical? node (.-firstChild el)))
      (is (= "new" (.-data node))))
    (testing "no child is added or removed"
      (is (= ["characterData"] (mapv record-type records))))))

(deftest set-text-writes-nothing-when-the-text-is-equal
  (let [el (doto (element! "span") (.append "5"))]
    (testing "equal text"
      (is (= [] (records-of! el (fn [] (mirror/set-text! el "5"))))))
    (testing "a number equal to the text"
      (is (= [] (records-of! el (fn [] (mirror/set-text! el 5))))))))

(deftest set-text-of-nil-leaves-the-element-with-no-text
  (let [el (doto (element! "span") (.append "old"))]
    (mirror/set-text! el nil)
    (is (= "" (.-textContent el)))))

(deftest set-text-gives-an-empty-element-its-text
  (let [el (element! "span")]
    (mirror/set-text! el "new")
    (is (= "new" (.-textContent el)))))

(deftest set-text-replaces-the-children-of-an-element-with-more-than-text
  (let [el (doto (element! "span") (.append (element! "b") "old"))]
    (mirror/set-text! el "new")
    (is (= 1 (.. el -childNodes -length)))
    (is (= "new" (.-textContent el)))))

(deftest set-text-returns-nothing
  (is (nil? (mirror/set-text! (element! "span") "new"))))

(defn- child-tags [^js node]
  (mapv (fn [^js child] (.-localName child)) (array-seq (.-children node))))

(deftest make-node-makes-one-detached-element-from-a-template
  (let [node (mirror/make-node! [:li {:class "row"} "Buy " [:b "milk"] [:x-button {:size "sm"}]])]
    (testing "the element has the tag and the attributes of the template"
      (is (= "li" (.-localName node)))
      (is (= "row" (.getAttribute node "class"))))
    (testing "the children are in the order of the template"
      (is (= ["b" "x-button"] (child-tags node)))
      (is (= "Buy milk" (.-textContent node)))
      (is (= "sm" (.getAttribute (.-lastElementChild node) "size"))))
    (testing "the element is in no document"
      (is (not (.-isConnected node))))))

(deftest make-node-writes-a-fixed-value-as-set-attrs-does
  (let [node (mirror/make-node! [:x-checkbox {:checked true :disabled false :tabindex 0}])]
    (is (= "" (.getAttribute node "checked")))
    (is (not (.hasAttribute node "disabled")))
    (is (= "0" (.getAttribute node "tabindex")))))

(deftest make-node-writes-a-child-that-is-a-number-as-its-text
  (is (= "3 left" (.-textContent (mirror/make-node! [:span 3 " left"])))))

(deftest make-node-makes-a-new-element-on-each-call
  (let [fixed [:li "text"]]
    (is (not (identical? (mirror/make-node! fixed) (mirror/make-node! fixed))))))

(deftest make-node-refuses-a-template-that-split-has-not-seen
  (testing "a keyword as an attribute value"
    (is (thrown? ExceptionInfo (mirror/make-node! [:x-button {:variant :primary}]))))
  (testing "a hole as a child"
    (is (thrown? ExceptionInfo (mirror/make-node! [:span :text]))))
  (testing "an :on entry"
    (is (thrown? ExceptionInfo (mirror/make-node! [:x-button {:on {"press" :remove}}]))))
  (testing "a sequence as a child"
    (is (thrown? ExceptionInfo (mirror/make-node! [:ul (map (partial vector :li) ["a" "b"])]))))
  (testing "a hole in a template below"
    (is (thrown? ExceptionInfo (mirror/make-node! [:li [:span :text]])))))

(deftest make-node-makes-the-fixed-template-that-split-gives
  (let [node (mirror/make-node! (:fixed (template/split :row [:li [:span :text]])))]
    (is (= "row.0" (.getAttribute (.-firstElementChild node) "data-x-part")))))

(deftest make-node-takes-a-template-of-a-tag-alone
  (is (= "li" (.-localName (mirror/make-node! [:li])))))

(defn- part-names [^js node]
  (set (keys (mirror/read-parts node))))

(deftest read-parts-finds-the-parts-below-a-node
  (let [node  (mirror/make-node! [:div
                                  [:header {:data-x-part "top"}
                                   [:span {:data-x-part "title"}]]
                                  [:ul [:li [:b {:data-x-part "deep"}]]]])
        parts (mirror/read-parts node)]
    (testing "a part is found at any depth"
      (is (= #{"top" "title" "deep"} (set (keys parts)))))
    (testing "a name maps to its node"
      (is (identical? (.-firstElementChild node) (parts "top"))))))

(deftest read-parts-includes-the-node-it-is-given
  (is (= #{"row" "label"}
         (part-names (mirror/make-node! [:li {:data-x-part "row"}
                                         [:span {:data-x-part "label"}]])))))

(deftest read-parts-leaves-out-a-keyed-node-below
  (let [node (mirror/make-node! [:ul {:data-x-part "list"}
                                 [:li {:data-x-key "1" :data-x-part "row"}
                                  [:span {:data-x-part "label"}]]])]
    (testing "the keyed node and its parts are not in the map of the node around it"
      (is (= #{"list"} (part-names node))))
    (testing "the keyed node gives its own parts when it is the node given"
      (is (= #{"row" "label"} (part-names (.-firstElementChild node)))))))

(deftest read-parts-gives-the-last-node-of-a-name-that-occurs-twice
  (let [node (mirror/make-node! [:div
                                 [:span {:data-x-part "label"}]
                                 [:b {:data-x-part "label"}]])]
    (is (identical? (.-lastElementChild node) ((mirror/read-parts node) "label")))))

(deftest read-parts-of-a-node-with-no-parts-is-empty
  (is (= {} (mirror/read-parts (mirror/make-node! [:div [:span]])))))

(def ^:private task-row
  (template/split :row [:li
                    [:span {:role "checkbox" :aria-checked (comp str :done?) :aria-label :text}]
                    [:span :text]]))

(defn- write-task! [^js node task]
  (mirror/write! (mirror/read-parts node) (template/writes task-row task)))

(deftest write-brings-the-parts-of-a-node-to-the-writes-of-an-item
  (let [node  (mirror/make-node! (:fixed task-row))
        check (.-firstElementChild node)
        label (.-lastElementChild node)]
    (write-task! node {:done? true :text "Buy milk"})
    (testing "each hole has the value of the item"
      (is (= "true" (.getAttribute check "aria-checked")))
      (is (= "Buy milk" (.getAttribute check "aria-label")))
      (is (= "Buy milk" (.-textContent label))))
    (testing "the fixed attribute stays"
      (is (= "checkbox" (.getAttribute check "role"))))
    (testing "the same item again writes nothing"
      (is (= [] (records-of! node (fn [] (write-task! node {:done? true :text "Buy milk"}))))))
    (testing "another item writes only what differs"
      (is (= ["aria-checked"]
             (mapv attribute-name
                   (records-of! node (fn [] (write-task! node {:done? false :text "Buy milk"})))))))))

(deftest write-applies-a-write-of-text-alone-and-of-attributes-alone
  (let [node (mirror/make-node! [:div [:b {:data-x-part "count"}] [:i {:data-x-part "state"}]])]
    (mirror/write! (mirror/read-parts node) {"count" {:text 3} "state" {:attrs {:hidden true}}})
    (is (= "3" (.-textContent (.-firstElementChild node))))
    (is (= "" (.getAttribute (.-lastElementChild node) "hidden")))))

(deftest write-of-nil-text-clears-the-text-of-a-part
  (let [node (mirror/make-node! [:b {:data-x-part "count"} "3"])]
    (mirror/write! (mirror/read-parts node) {"count" {:text nil}})
    (is (= "" (.-textContent node)))))

(deftest write-refuses-a-part-name-that-no-node-has
  (let [node  (mirror/make-node! [:b {:data-x-part "count"} "3"])
        parts (mirror/read-parts node)
        wrong (array-map "count" {:text 4} "gone" {:text "x"} "lost" {:text "y"})]
    (testing "the error names every part that has no node"
      (is (= ["gone" "lost"]
             (try (mirror/write! parts wrong) (catch ExceptionInfo e (:parts (ex-data e)))))))
    (testing "nothing is written"
      (is (= "3" (.-textContent node))))))

(deftest write-returns-nothing
  (is (nil? (mirror/write! {} {}))))

(defn- staged!
  "The node of the fixed template `fixed`, in the document."
  [fixed]
  (let [node (mirror/make-node! fixed)]
    (.append (stage!) node)
    node))

(defn- origin-of!
  "The origin of an event of `event-type` sent from `target`, as a listener on `root` reads it."
  [^js root ^js target event-type]
  (let [origin (atom nil)]
    (.addEventListener root event-type (comp (partial reset! origin) mirror/read-origin)
                       #js {:once true})
    (.dispatchEvent target (js/Event. event-type #js {:bubbles true :composed true}))
    @origin))

(defn- find-one [^js root selector]
  (.querySelector root selector))

(def ^:private page
  [:div {:data-x-part "app"}
   [:ul {:data-x-part "list"}
    [:li {:data-x-key "t1"}
     [:span "Buy milk"]
     [:button {:data-x-part "remove"} [:i "x"]]
     [:ul
      [:li {:data-x-key "s1" :data-x-part "step"} [:b {:data-x-part "name"}]]]]]
   [:button {:data-x-part "add"}]
   [:p "No part here"]])

(deftest read-origin-gives-the-keys-and-the-nearest-part
  (let [root (staged! page)]
    (testing "an event from inside a part of a keyed node"
      (is (= {:key-path ["t1"] :part "remove" :node (find-one root "[data-x-part=remove]")}
             (origin-of! root (find-one root "i") "press"))))
    (testing "the keys of nested keyed nodes come outermost first"
      (is (= {:key-path ["t1" "s1"] :part "name" :node (find-one root "b")}
             (origin-of! root (find-one root "b") "press"))))
    (testing "a keyed node that is a part is the nearest part of its own event"
      (is (= {:key-path ["t1" "s1"] :part "step" :node (find-one root "[data-x-key=s1]")}
             (origin-of! root (find-one root "[data-x-key=s1]") "press"))))
    (testing "an event from a part outside every keyed node has no keys"
      (is (= {:key-path [] :part "add" :node (find-one root "[data-x-part=add]")}
             (origin-of! root (find-one root "[data-x-part=add]") "press"))))))

(deftest read-origin-does-not-look-for-a-part-beyond-the-nearest-keyed-node
  (let [root (staged! page)]
    (is (= {:key-path ["t1"] :part nil :node nil}
           (origin-of! root (find-one root "span") "press")))))

(deftest read-origin-takes-the-node-that-listens-as-a-part
  (let [root (staged! page)]
    (is (= {:key-path [] :part "app" :node root}
           (origin-of! root (find-one root "p") "press")))))

(deftest read-origin-takes-the-key-of-the-node-that-listens
  (let [root (staged! [:li {:data-x-key "t1"} [:ul [:li {:data-x-key "s1"} [:b]]]])]
    (is (= ["t1" "s1"] (:key-path (origin-of! root (find-one root "b") "press"))))))

(deftest read-origin-reads-the-path-through-a-shadow-root
  (let [root   (staged! [:ul [:li {:data-x-key "t1"} [:div {:data-x-part "host"}]]])
        host   (find-one root "div")
        inside (element! "button")]
    (.append (.attachShadow host #js {:mode "open"}) inside)
    (is (= {:key-path ["t1"] :part "host" :node host}
           (origin-of! root inside "press")))))

(deftest read-origin-of-an-event-that-is-no-longer-handled-is-empty
  (let [root  (staged! page)
        event (atom nil)]
    (.addEventListener root "press" (partial reset! event))
    (.dispatchEvent (find-one root "i") (js/Event. "press" #js {:bubbles true}))
    (is (= {:key-path [] :part nil :node nil} (mirror/read-origin @event)))))
