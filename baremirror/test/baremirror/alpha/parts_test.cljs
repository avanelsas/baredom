(ns baremirror.alpha.parts-test
  (:require [baremirror.alpha.parts :as mirror-parts]
            [baremirror.alpha.template :as template]
            [baremirror.alpha.test-stage :refer [element! element-with! record-type records-of! remove-stages!]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(use-fixtures :each {:after remove-stages!})

(defn- holding-element!
  "An element that offers a hold. The hold adds its start, its `this` and its end to `log`."
  [log]
  (doto (element! "div")
    (unchecked-set mirror-parts/render-hold (fn [f]
                                     (this-as this
                                       (swap! log conj :hold this)
                                       (f)
                                       (swap! log conj :release))))))

(deftest with-one-render-calls-the-work-inside-the-hold-of-the-element
  (let [log (atom [])
        el  (holding-element! log)]
    (mirror-parts/with-one-render! el (partial swap! log conj))
    (testing "the hold runs on the element, and the work gets the element"
      (is (= [:hold el el :release] @log)))))

(deftest with-one-render-calls-the-work-on-an-element-with-no-hold
  (let [log (atom [])
        el  (element! "div")]
    (mirror-parts/with-one-render! el (partial swap! log conj))
    (is (= [el] @log))))

(deftest with-one-render-returns-nothing
  (testing "on an element with no hold"
    (is (nil? (mirror-parts/with-one-render! (element! "div") identity))))
  (testing "on an element whose hold returns a value"
    (is (nil? (mirror-parts/with-one-render! (holding-element! (atom [])) identity)))))

(defn- attribute-name [^js record]
  (.-attributeName record))

(defn- attr-logging-element!
  "An element that offers a hold.
   The hold adds the value of `attr` before and after the work to `log`."
  [log attr]
  (doto (element! "div")
    (unchecked-set mirror-parts/render-hold (fn [f]
                                     (this-as ^js this
                                       (swap! log conj (.getAttribute this attr))
                                       (f)
                                       (swap! log conj (.getAttribute this attr)))))))

(deftest set-attrs-writes-only-the-attributes-that-differ
  (let [el      (element-with! "div" {"a" "1" "b" "2" "kept" "x"})
        records (records-of! el (fn [] (mirror-parts/set-attrs! el (array-map :a "1" :b "3" :c "4"))))]
    (testing "an equal value is not written"
      (is (= ["b" "c"] (mapv attribute-name records))))
    (testing "the named attributes have their values"
      (is (= ["1" "3" "4"] (mapv (fn [k] (.getAttribute el k)) ["a" "b" "c"]))))
    (testing "an attribute that is not named stays"
      (is (= "x" (.getAttribute el "kept"))))))

(deftest set-attrs-gives-true-false-and-nil-their-meaning
  (let [el (element-with! "div" {"off" "" "gone" "1"})]
    (mirror-parts/set-attrs! el {:on true :off false :gone nil :count 5 :variant :primary})
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
    (is (= [] (records-of! el (fn [] (mirror-parts/set-attrs! el {:on true :off false :count 5})))))))

(deftest set-attrs-writes-a-sequence-of-pairs-in-its-order
  (let [el (element! "div")]
    (is (= ["b" "a"]
           (mapv attribute-name
                 (records-of! el (fn [] (mirror-parts/set-attrs! el [[:b "1"] [:a "2"]]))))))))

(deftest set-attrs-takes-a-name-as-a-string
  (let [el (element! "div")]
    (mirror-parts/set-attrs! el {"data-state" "open"})
    (is (= "open" (.getAttribute el "data-state")))))

(deftest set-attrs-writes-inside-the-hold-of-the-element
  (let [log (atom [])
        el  (attr-logging-element! log "a")]
    (mirror-parts/set-attrs! el {:a "1"})
    (is (= [nil "1"] @log))))

(deftest set-attrs-returns-nothing
  (is (nil? (mirror-parts/set-attrs! (element! "div") {:a "1"}))))

(deftest set-text-writes-into-the-text-node-of-the-element
  (let [el      (doto (element! "span") (.append "old"))
        node    (.-firstChild el)
        records (records-of! el (fn [] (mirror-parts/set-text! el "new")))]
    (testing "the text node stays and holds the text"
      (is (identical? node (.-firstChild el)))
      (is (= "new" (.-data node))))
    (testing "no child is added or removed"
      (is (= ["characterData"] (mapv record-type records))))))

(deftest set-text-writes-nothing-when-the-text-is-equal
  (let [el (doto (element! "span") (.append "5"))]
    (testing "equal text"
      (is (= [] (records-of! el (fn [] (mirror-parts/set-text! el "5"))))))
    (testing "a number equal to the text"
      (is (= [] (records-of! el (fn [] (mirror-parts/set-text! el 5))))))))

(deftest set-text-of-nil-leaves-the-element-with-no-text
  (let [el (doto (element! "span") (.append "old"))]
    (mirror-parts/set-text! el nil)
    (is (= "" (.-textContent el)))))

(deftest set-text-gives-an-empty-element-its-text
  (let [el (element! "span")]
    (mirror-parts/set-text! el "new")
    (is (= "new" (.-textContent el)))))

(deftest set-text-replaces-the-children-of-an-element-with-more-than-text
  (let [el (doto (element! "span") (.append (element! "b") "old"))]
    (mirror-parts/set-text! el "new")
    (is (= 1 (.. el -childNodes -length)))
    (is (= "new" (.-textContent el)))))

(deftest set-text-returns-nothing
  (is (nil? (mirror-parts/set-text! (element! "span") "new"))))

(defn- child-tags [^js node]
  (mapv (fn [^js child] (.-localName child)) (array-seq (.-children node))))

(deftest make-node-makes-one-detached-element-from-a-template
  (let [node (mirror-parts/make-node! [:li {:class "row"} "Buy " [:b "milk"] [:x-button {:size "sm"}]])]
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
  (let [node (mirror-parts/make-node! [:x-checkbox {:checked true :disabled false :tabindex 0}])]
    (is (= "" (.getAttribute node "checked")))
    (is (not (.hasAttribute node "disabled")))
    (is (= "0" (.getAttribute node "tabindex")))))

(deftest make-node-writes-a-child-that-is-a-number-as-its-text
  (is (= "3 left" (.-textContent (mirror-parts/make-node! [:span 3 " left"])))))

(deftest make-node-makes-a-new-element-on-each-call
  (let [fixed [:li "text"]]
    (is (not (identical? (mirror-parts/make-node! fixed) (mirror-parts/make-node! fixed))))))

(deftest make-node-refuses-a-template-that-split-has-not-seen
  (testing "a keyword as an attribute value"
    (is (thrown? ExceptionInfo (mirror-parts/make-node! [:x-button {:variant :primary}]))))
  (testing "a hole as a child"
    (is (thrown? ExceptionInfo (mirror-parts/make-node! [:span :text]))))
  (testing "an :on entry"
    (is (thrown? ExceptionInfo (mirror-parts/make-node! [:x-button {:on {"press" :remove}}]))))
  (testing "a sequence as a child"
    (is (thrown? ExceptionInfo (mirror-parts/make-node! [:ul (map (partial vector :li) ["a" "b"])]))))
  (testing "a hole in a template below"
    (is (thrown? ExceptionInfo (mirror-parts/make-node! [:li [:span :text]])))))

(deftest make-node-makes-the-fixed-template-that-split-gives
  (let [node (mirror-parts/make-node! (:fixed (template/split :row [:li [:span :text]])))]
    (is (= "row.0" (.getAttribute (.-firstElementChild node) "data-x-part")))))

(deftest make-node-takes-a-template-of-a-tag-alone
  (is (= "li" (.-localName (mirror-parts/make-node! [:li])))))

(defn- part-names [^js node]
  (set (keys (mirror-parts/read-parts node))))

(deftest read-parts-finds-the-parts-below-a-node
  (let [node  (mirror-parts/make-node! [:div
                                  [:header {:data-x-part "top"}
                                   [:span {:data-x-part "title"}]]
                                  [:ul [:li [:b {:data-x-part "deep"}]]]])
        parts (mirror-parts/read-parts node)]
    (testing "a part is found at any depth"
      (is (= #{"top" "title" "deep"} (set (keys parts)))))
    (testing "a name maps to its node"
      (is (identical? (.-firstElementChild node) (parts "top"))))))

(deftest read-parts-includes-the-node-it-is-given
  (is (= #{"row" "label"}
         (part-names (mirror-parts/make-node! [:li {:data-x-part "row"}
                                         [:span {:data-x-part "label"}]])))))

(deftest read-parts-leaves-out-a-keyed-node-below
  (let [node (mirror-parts/make-node! [:ul {:data-x-part "list"}
                                 [:li {:data-x-key "1" :data-x-part "row"}
                                  [:span {:data-x-part "label"}]]])]
    (testing "the keyed node and its parts are not in the map of the node around it"
      (is (= #{"list"} (part-names node))))
    (testing "the keyed node gives its own parts when it is the node given"
      (is (= #{"row" "label"} (part-names (.-firstElementChild node)))))))

(deftest read-parts-leaves-out-a-keyed-node-at-any-depth
  (let [node (mirror-parts/make-node! [:div {:data-x-part "page"}
                                 [:section
                                  [:ul
                                   [:li {:data-x-key "1"}
                                    [:span {:data-x-part "label"}
                                     [:b {:data-x-part "deep"}]]]]]
                                 [:footer {:data-x-part "foot"}]])]
    (is (= #{"page" "foot"} (part-names node)))))

(deftest read-parts-of-a-node-inside-a-keyed-node-finds-its-parts
  (let [row (mirror-parts/make-node! [:li {:data-x-key "1" :data-x-part "row"}
                                [:div
                                 [:span {:data-x-part "label"}]]])]
    (is (= #{"label"} (part-names (.-firstElementChild row))))))

(deftest read-parts-of-a-node-inside-a-keyed-node-leaves-out-a-keyed-node-below
  (let [row (mirror-parts/make-node! [:li {:data-x-key "1"}
                                [:div {:data-x-part "cell"}
                                 [:ul
                                  [:li {:data-x-key "a" :data-x-part "inner"}
                                   [:span {:data-x-part "deep"}]]]
                                 [:b {:data-x-part "label"}]]])]
    (is (= #{"cell" "label"} (part-names (.-firstElementChild row))))))

(deftest read-parts-gives-the-last-node-of-a-name-that-occurs-twice
  (let [node (mirror-parts/make-node! [:div
                                 [:span {:data-x-part "label"}]
                                 [:b {:data-x-part "label"}]])]
    (is (identical? (.-lastElementChild node) ((mirror-parts/read-parts node) "label")))))

(deftest read-parts-of-a-node-with-no-parts-is-empty
  (is (= {} (mirror-parts/read-parts (mirror-parts/make-node! [:div [:span]])))))

(def ^:private task-row
  (template/split :row [:li
                    [:span {:role "checkbox" :aria-checked (comp str :done?) :aria-label :text}]
                    [:span :text]]))

(defn- write-task! [^js node task]
  (mirror-parts/write-parts! (mirror-parts/read-parts node) (template/writes task-row task)))

(deftest write-parts-brings-the-parts-of-a-node-to-the-writes-of-an-item
  (let [node  (mirror-parts/make-node! (:fixed task-row))
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

(def ^:private other-row
  (template/split :row [:li
                      [:span {:role "checkbox" :aria-checked (comp str :done?) :aria-label :text}]
                      [:span :text]]))

(defn- checked-of [^js node]
  (.getAttribute (.-firstElementChild node) "aria-checked"))

(deftest write-item-brings-a-node-to-an-item
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (testing "a node that was never written is written"
      (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
      (is (= "true" (checked-of node)))
      (is (= "Buy milk" (.-textContent (.-lastElementChild node)))))
    (testing "another item writes only what differs"
      (is (= ["aria-checked"]
             (mapv attribute-name
                   (records-of! node (fn [] (mirror-parts/write-item! node task-row {:done? false :text "Buy milk"})))))))
    (testing "it returns nothing"
      (is (nil? (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"}))))))

(deftest write-item-writes-nothing-for-an-equal-item
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (.setAttribute (.-firstElementChild node) "aria-checked" "changed from outside")
    (testing "an equal item leaves a change from outside as it is"
      (is (= [] (records-of! node (fn [] (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})))))
      (is (= "changed from outside" (checked-of node))))
    (testing "write-parts! brings the node back"
      (write-task! node {:done? true :text "Buy milk"})
      (is (= "true" (checked-of node))))))

(deftest write-item-writes-an-equal-item-through-another-template
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (.setAttribute (.-firstElementChild node) "aria-checked" "changed from outside")
    (mirror-parts/write-item! node other-row {:done? true :text "Buy milk"})
    (is (= "true" (checked-of node)))))

(deftest write-item-refuses-what-is-not-a-split-template
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (is (thrown-with-msg? ExceptionInfo #"not a split template"
                          (mirror-parts/write-item! node (:fixed task-row) {:done? true :text "Buy milk"})))
    (is (thrown-with-msg? ExceptionInfo #"not a split template"
                          (mirror-parts/write-item! node nil {:done? true :text "Buy milk"})))))

(deftest write-item-that-throws-writes-the-same-item-the-next-time
  (let [node (mirror-parts/make-node! (:fixed task-row))
        text (.-lastElementChild node)
        part (.getAttribute text "data-x-part")]
    (.removeAttribute text "data-x-part")
    (is (thrown? ExceptionInfo (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})))
    (.setAttribute text "data-x-part" part)
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (is (= "Buy milk" (.-textContent text)))))

(deftest write-item-after-write-parts-skips-the-item-it-last-wrote
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (write-task! node {:done? false :text "Buy milk"})
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (is (= "false" (checked-of node)))))

(defn- milk-after-bread? [item item-then]
  (and (= "Buy milk" (:text item)) (= "Buy bread" (:text item-then))))

(deftest write-item-skips-an-item-that-same?-accepts
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (mirror-parts/write-item! (constantly true) node task-row {:done? true :text "Buy bread"})
    (testing "a node that was never written is written, whatever same? answers"
      (is (= "true" (checked-of node))))
    (testing "an item that same? accepts is not written"
      (mirror-parts/write-item! (constantly true) node task-row {:done? false :text "Buy bread"})
      (is (= "true" (checked-of node))))
    (testing "an item that same? refuses is written"
      (mirror-parts/write-item! (constantly false) node task-row {:done? false :text "Buy bread"})
      (is (= "false" (checked-of node))))
    (testing "same? gets the item first and the item last written second"
      (mirror-parts/write-item! milk-after-bread? node task-row {:done? true :text "Buy milk"})
      (is (= "false" (checked-of node))))))

(deftest write-item-writes-a-nil-item-once
  (let [node (mirror-parts/make-node! (:fixed task-row))]
    (mirror-parts/write-item! node task-row {:done? true :text "Buy milk"})
    (mirror-parts/write-item! node task-row nil)
    (is (= "" (.-textContent (.-lastElementChild node))))
    (is (= [] (records-of! node (fn [] (mirror-parts/write-item! node task-row nil)))))))

(deftest write-parts-applies-a-write-of-text-alone-and-of-attributes-alone
  (let [node (mirror-parts/make-node! [:div [:b {:data-x-part "count"}] [:i {:data-x-part "state"}]])]
    (mirror-parts/write-parts! (mirror-parts/read-parts node) {"count" {:text 3} "state" {:attrs {:hidden true}}})
    (is (= "3" (.-textContent (.-firstElementChild node))))
    (is (= "" (.getAttribute (.-lastElementChild node) "hidden")))))

(deftest write-parts-of-nil-text-clears-the-text-of-a-part
  (let [node (mirror-parts/make-node! [:b {:data-x-part "count"} "3"])]
    (mirror-parts/write-parts! (mirror-parts/read-parts node) {"count" {:text nil}})
    (is (= "" (.-textContent node)))))

(deftest write-parts-refuses-a-part-name-that-no-node-has
  (let [node  (mirror-parts/make-node! [:b {:data-x-part "count"} "3"])
        parts (mirror-parts/read-parts node)
        wrong (array-map "count" {:text 4} "gone" {:text "x"} "lost" {:text "y"})]
    (testing "the error names every part that has no node"
      (is (= ["gone" "lost"]
             (try (mirror-parts/write-parts! parts wrong) (catch ExceptionInfo e (:parts (ex-data e)))))))
    (testing "nothing is written"
      (is (= "3" (.-textContent node))))))

(deftest write-parts-returns-nothing
  (is (nil? (mirror-parts/write-parts! {} {}))))
