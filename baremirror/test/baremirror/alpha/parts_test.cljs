(ns baremirror.alpha.parts-test
  (:require [baremirror.alpha.element :as mirror-element]
            [baremirror.alpha.parts :as mirror-parts]
            [baremirror.alpha.places :as mirror-places]
            [baremirror.alpha.template :as template]
            [baremirror.alpha.test-stage :refer [element! element-with! record-type records-of! remove-stages!
                                                 stage!]]
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

(def ^:private svg-ns "http://www.w3.org/2000/svg")

(def ^:private html-ns "http://www.w3.org/1999/xhtml")

(defn- namespace-of [^js node]
  (.-namespaceURI node))

(def ^:private icon-template
  [:span
   [:svg {:viewBox "0 0 24 24"}
    [:linearGradient {:id "fade"}]
    [:path {:d "M0 0h24"}]
    [:foreignObject [:p "note"]]]])

(deftest make-node-makes-an-svg-and-the-elements-below-it-in-the-svg-namespace
  (let [node (mirror-parts/make-node! icon-template)
        svg  (.querySelector node "svg")]
    (testing "an element above the svg is HTML"
      (is (= html-ns (namespace-of node))))
    (testing "the svg and its children are SVG elements"
      (is (= [svg-ns svg-ns svg-ns svg-ns]
             (mapv namespace-of (cons svg (array-seq (.-children svg)))))))
    (testing "a tag and an attribute keep their case"
      (is (= ["linearGradient" "path" "foreignObject"] (child-tags svg)))
      (is (= "0 0 24 24" (.getAttribute svg "viewBox"))))
    (testing "a child of a foreignObject is HTML"
      (is (= html-ns (namespace-of (.querySelector node "p")))))))

(deftest make-node-makes-an-element-in-the-namespace-of-its-xmlns
  (let [node (mirror-parts/make-node! [:g {:xmlns svg-ns} [:circle {:r 4}]])]
    (testing "the element and the elements below it are SVG elements"
      (is (= [svg-ns svg-ns] (mapv namespace-of [node (.-firstElementChild node)]))))
    (testing "the element keeps its xmlns attribute"
      (is (= svg-ns (.getAttribute node "xmlns"))))))

(deftest node-maker-makes-svg-rows-that-draw-in-an-svg-of-the-page
  (let [svg      (mirror-parts/make-node! [:svg {:width 20 :height 20}])
        dot      (template/split :dot [:circle {:xmlns svg-ns :r :r :cx 10 :cy 10}])
        make-dot (mirror-parts/node-maker (:fixed dot))
        radii    {"a" 2 "b" 5}]
    (.append (stage!) svg)
    (let [nodes (mirror-places/sync! {:dots {:parent svg}} {:dots ["a" "b"]} make-dot)]
      (doseq [[k r] radii]
        (mirror-parts/write-item! (nodes k) dot {:r r}))
      (is (every? (partial instance? js/SVGCircleElement) (vals nodes)))
      (is (= 10 (.-width (.getBBox (nodes "b"))))))))

(deftest make-node-makes-a-foreign-object-outside-an-svg-in-html
  (let [node (mirror-parts/make-node! [:div [:foreignObject [:p "note"]]])]
    (is (= html-ns (namespace-of (.-firstElementChild node))))
    (is (= html-ns (namespace-of (.querySelector node "p"))))))

(deftest node-maker-makes-an-svg-in-the-svg-namespace
  (let [node ((mirror-parts/node-maker icon-template))]
    (is (= svg-ns (namespace-of (.querySelector node "path"))))
    (is (= html-ns (namespace-of (.querySelector node "p"))))))

(def ^:private row-template
  [:li {:class "row"} "Buy " [:b "milk"] [:x-button {:size "sm" :disabled true}]])

(deftest node-maker-makes-the-element-of-a-template-on-each-call
  (let [make-row (mirror-parts/node-maker row-template)
        node     (make-row)]
    (testing "the element is the one that make-node! makes of the template"
      (is (= (.-outerHTML (mirror-parts/make-node! row-template)) (.-outerHTML node))))
    (testing "each call gives another element"
      (is (not (identical? node (make-row)))))
    (testing "the element is in no document"
      (is (not (.-isConnected node))))))

(deftest node-maker-refuses-a-template-that-is-not-fixed
  (testing "a hole as a child"
    (is (thrown? ExceptionInfo (mirror-parts/node-maker [:span :text]))))
  (testing "a hole in a template below"
    (is (thrown? ExceptionInfo (mirror-parts/node-maker [:li [:span :text]]))))
  (testing "a hole two levels below"
    (is (thrown? ExceptionInfo (mirror-parts/node-maker [:ul [:li [:span :text]]]))))
  (testing "an :on entry below"
    (is (thrown? ExceptionInfo (mirror-parts/node-maker [:li [:button {:on {"press" :go}}]])))))

(deftest node-maker-makes-no-element-before-its-first-call
  (let [made     (atom 0)
        original (.-createElement js/document)]
    (set! (.-createElement js/document)
          (fn [tag] (swap! made inc) (.call original js/document tag)))
    (try
      (let [make-row (mirror-parts/node-maker [:li [:b "text"]])]
        (testing "the maker touches no document"
          (is (= 0 @made)))
        (make-row)
        (testing "the first call does"
          (is (pos? @made))))
      (finally
        (set! (.-createElement js/document) original)))))

(deftest node-maker-makes-a-node-whatever-it-is-called-with
  (let [make-row (mirror-parts/node-maker [:li "text"])
        parent   (element! "ul")]
    (testing "an argument is left alone"
      (is (= "li" (.-localName (make-row "a")))))
    (testing "sync! takes it as the function that makes the node of a key"
      (mirror-places/sync! {:rows {:parent parent}} {:rows ["a" "b"]} make-row)
      (is (= ["a" "b"] (mapv (fn [^js child] (.getAttribute child "data-x-key"))
                             (array-seq (.-children parent))))))))

(mirror-element/define-element! "bm-made-probe" {:attrs [] :template [:span "shown"] :css ""})

(deftest node-maker-makes-an-element-that-is-a-component-once-it-is-in-the-page
  (let [node  ((mirror-parts/node-maker [:li [:bm-made-probe]]))
        probe (.-firstElementChild node)]
    (testing "before it is placed, a custom element in it has shown nothing"
      (is (nil? (.-shadowRoot probe))))
    (.append (stage!) node)
    (testing "in the page, the same element shows what it is defined to show"
      (is (= "shown" (.-textContent (.-lastElementChild (.-shadowRoot probe))))))))

(deftest write-item-skips-the-item-it-wrote-before-the-node-was-placed
  (let [split (template/split :row [:li [:span :text]])
        node  ((mirror-parts/node-maker (:fixed split)))]
    (mirror-parts/write-item! node split {:text "Buy milk"})
    (.append (stage!) node)
    (testing "the node shows the item"
      (is (= "Buy milk" (.-textContent node))))
    (testing "the same item is not written again"
      (is (empty? (records-of! node (partial mirror-parts/write-item! node split {:text "Buy milk"})))))))

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

(deftest read-parts-leaves-out-a-part-in-a-shadow-root
  (let [node (mirror-parts/make-node! [:div [:span {:data-x-part "label"}]])
        root (.attachShadow (.-firstElementChild node) #js {:mode "open"})]
    (.append root (mirror-parts/make-node! [:b {:data-x-part "inside"}]))
    (is (= #{"label"} (part-names node)))))

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

(defn- prop-recorder!
  "An element with a property `items` that adds each value set to it to `log`."
  [log]
  (doto (element! "div")
    (js/Object.defineProperty "items" #js {:set (partial swap! log conj)})))

(defn- write-items! [^js el items]
  (mirror-parts/write-parts! {"list" el} {"list" {:props {:items items}}}))

(deftest write-parts-sets-a-property-that-differs-from-the-value-last-written
  (let [log (atom [])
        el  (prop-recorder! log)]
    (write-items! el [1 2])
    (write-items! el [1 2])
    (write-items! el [3])
    (is (= [[1 2] [3]] @log))))

(deftest write-parts-compares-a-javascript-array-and-object-by-content
  (let [log (atom [])
        el  (prop-recorder! log)]
    (write-items! el #js [#js {:id "a"}])
    (write-items! el #js [#js {:id "a"}])
    (write-items! el #js [#js {:id "b"}])
    (write-items! el #js [#js {:id "b" :label nil}])
    (is (= 3 (count @log)))))

(deftest write-parts-compares-an-instance-of-a-class-by-identity
  (let [log   (atom [])
        el    (prop-recorder! log)
        items (js/Map.)]
    (write-items! el items)
    (write-items! el items)
    (write-items! el (js/Map.))
    (is (= 2 (count @log)))))

(defn- record-what-is-there!
  "Puts the text and the `open` attribute of `el` into `seen`."
  [seen ^js el]
  (reset! seen [(.-textContent el) (.getAttribute el "open")]))

(deftest write-parts-sets-the-properties-after-the-text-and-the-attributes
  (let [seen (atom nil)
        el   (element! "div")]
    (js/Object.defineProperty el "items" #js {:set (fn [_] (record-what-is-there! seen el))})
    (mirror-parts/write-parts! {"list" el} {"list" {:text "x" :attrs {:open true} :props {:items []}}})
    (is (= ["x" ""] @seen))))

(deftest same-value-compares-by-equality-and-by-content
  (testing "ClojureScript values compare by ="
    (is (true? (mirror-parts/same-value? [1 {:a 2}] [1 {:a 2}]))))
  (testing "JavaScript arrays and plain objects compare by content, at every depth"
    (is (true? (mirror-parts/same-value? #js [#js {:a #js [1]}] #js [#js {:a #js [1]}])))
    (is (false? (mirror-parts/same-value? #js [#js {:a #js [1]}] #js [#js {:a #js [2]}]))))
  (testing "an array and an object with the same entries differ"
    (is (false? (mirror-parts/same-value? #js ["a"] #js {"0" "a"}))))
  (testing "objects with other keys differ, even when each value is undefined"
    (is (false? (mirror-parts/same-value? #js {:a js/undefined} #js {:b js/undefined})))))
