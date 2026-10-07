(ns baremirror.js-test
  (:require [baremirror.js :as mjs]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(def ^:private stage-attr "data-baremirror-js-test")

(defn- remove-stages! []
  (run! (fn [^js node] (.remove node))
        (array-seq (.querySelectorAll js/document (str "[" stage-attr "]")))))

(use-fixtures :each {:after remove-stages!})

(defn- staged!
  "The node of the fixed template `array`, in the document."
  [array]
  (let [node (mjs/make-node array)]
    (.setAttribute node stage-attr "")
    (.append (.-body js/document) node)
    node))

(defn- data
  "The JavaScript value `x` as data, with string keys."
  [x]
  (js->clj x))

(defn- text-of [^js item]
  (.-text item))

(defn- done-of [^js item]
  (.-done item))

(defn- row-template []
  #js ["li"
       #js ["span" #js {:role "checkbox" :aria-checked done-of :on #js {"toggle" "toggle"}}]
       #js ["b" text-of]
       #js ["button" #js {:on #js {"press" "remove"}} "Remove"]])

(deftest split-returns-javascript-values
  (let [^js row (mjs/split "row" (row-template))]
    (testing "the fixed template is an array with the part names"
      (is (= ["li"
              ["span" {"role" "checkbox" "data-x-part" "row.0"}]
              ["b" {"data-x-part" "row.1"}]
              ["button" {"data-x-part" "row.2"} "Remove"]]
             (data (.-fixed row)))))
    (testing "the events are pairs of an event type with a part name and a meaning"
      (is (= [[["toggle" "row.0"] "toggle"] [["press" "row.2"] "remove"]]
             (data (.-events row)))))
    (testing "the writes of an item are an object by part name"
      (is (= {"row.0" {"attrs" {"aria-checked" true}} "row.1" {"text" "Buy milk"}}
             (data (.writes row #js {:done true :text "Buy milk"})))))
    (testing "a hole keeps its function"
      (is (identical? text-of (.-text (aget (.-holes row) 1)))))
    (testing "the object is frozen, with every array and object in it"
      (is (js/Object.isFrozen row))
      (is (js/Object.isFrozen (.-fixed row)))
      (is (js/Object.isFrozen (aget (.-fixed row) 1)))
      (is (js/Object.isFrozen (aget (.-events row) 0))))))

(deftest make-node-takes-an-array-and-refuses-a-hole
  (let [node (mjs/make-node #js ["li" #js {:class "row"} "Buy " #js ["b" "milk"]])]
    (is (= "row" (.getAttribute node "class")))
    (is (= "Buy milk" (.-textContent node))))
  (is (thrown? ExceptionInfo (mjs/make-node #js ["b" text-of]))))

(deftest plan-takes-and-returns-javascript-values
  (is (= {"remove" ["c"] "place" [{"key" "d" "in" "rows" "before" nil}]}
         (data (mjs/plan #js {:rows #js ["a" "b" "c"]} #js {:rows #js ["a" "b" "d"]})))))

(defn- make-item [_key]
  (mjs/make-node #js ["li"]))

(defn- keys-in [^js parent]
  (mapv (fn [^js child] (.getAttribute child "data-x-key")) (array-seq (.-children parent))))

(defn- items [^js parent ks]
  [#js {:items #js {:parent parent}} #js {:items (into-array ks)}])

(deftest sync-brings-a-parent-to-the-wanted-keys
  (let [parent         (staged! #js ["ul"])
        [containers a] (items parent ["a" "b"])
        [_ b]          (items parent ["b" "a" "c"])
        first-nodes    (mjs/sync containers a make-item)
        later-nodes    (mjs/sync containers b make-item)]
    (testing "the parent holds the wanted keys in order"
      (is (= ["b" "a" "c"] (keys-in parent))))
    (testing "the result is an object of key to node, and a node that stays is the same node"
      (is (identical? (.-a first-nodes) (.-a later-nodes))))))

(deftest the-pieces-of-sync-take-the-values-of-each-other
  (let [parent         (staged! #js ["ul"])
        [containers a] (items parent ["a" "b"])
        ^js reading    (mjs/read-places containers)
        nodes          (mjs/perform reading (mjs/plan (.-places reading) a) make-item)]
    (is (= {"items" []} (data (.-places reading))))
    (is (= ["a" "b"] (keys-in parent)))
    (is (= #{"a" "b"} (set (js-keys nodes))))))

(deftest write-applies-an-object-of-writes-to-an-object-of-parts
  (let [node  (staged! #js ["div" #js ["b" #js {:data-x-part "count"} "0"]
                            #js ["i" #js {:data-x-part "state"}]])
        parts (mjs/read-parts node)]
    (mjs/write parts #js {:count #js {:text 3} :state #js {:attrs #js {:hidden true}}})
    (testing "text and attributes are written"
      (is (= "3" (.-textContent (.-count parts))))
      (is (= "" (.getAttribute (.-state parts) "hidden"))))
    (testing "a text of null clears the part"
      (mjs/write parts #js {:count #js {:text nil}})
      (is (= "" (.-textContent (.-count parts)))))
    (testing "a part name that no node has is refused"
      (is (thrown? ExceptionInfo (mjs/write parts #js {:gone #js {:text "x"}}))))))

(deftest set-attrs-takes-an-object
  (let [node (mjs/make-node #js ["div" #js {:hidden true}])]
    (mjs/set-attrs node #js {:hidden false :data-state "open"})
    (is (not (.hasAttribute node "hidden")))
    (is (= "open" (.getAttribute node "data-state")))))

(defn- send!
  "Sends a cancelable event of `event-type` from `target`, and returns it."
  [^js target event-type]
  (let [event (js/CustomEvent. event-type #js {:bubbles true :cancelable true})]
    (.dispatchEvent target event)
    event))

(defn- page! []
  (staged! #js ["ul" #js ["li" #js {:data-x-key "t1"}
                          #js ["span" #js {:data-x-part "check" :aria-checked "false"}]
                          #js ["button" #js {:data-x-part "remove"}]]]))

(deftest read-origin-returns-an-object
  (let [page   (page!)
        button (.querySelector page "button")
        origin (atom nil)]
    (.addEventListener page "press" (comp (partial reset! origin) mjs/read-origin))
    (send! button "press")
    (is (= ["t1"] (data (.-keyPath @origin))))
    (is (= "remove" (.-part @origin)))
    (is (identical? button (.-node @origin)))))

(defn- key-count [^js origin _event]
  (.. origin -keyPath -length))

(deftest listen-gives-the-dispatch-function-a-message-as-an-array
  (let [page     (page!)
        messages (atom [])]
    (mjs/listen page #js {:dispatch (partial swap! messages conj)
                          :events   #js [#js [#js ["press" "remove"] "remove"]
                                         #js [#js ["toggle" "check"] #js ["toggle" key-count]]]})
    (send! (.querySelector page "button") "press")
    (send! (.querySelector page "span") "toggle")
    (testing "a message is an array of a meaning and an argument"
      (is (every? array? @messages)))
    (testing "the argument is the nearest key, or what the function of the entry gives"
      (is (= [["remove" "t1"] ["toggle" 1]] (mapv data @messages))))))

(deftest a-meaning-with-a-function-in-a-template-reaches-the-dispatch-function
  (let [^js tag  (mjs/split "tag" #js ["button" #js {:on #js {"click" #js ["untag" key-count]}} "x"])
        button   (staged! (.-fixed tag))
        messages (atom [])]
    (mjs/listen button #js {:dispatch (partial swap! messages conj) :events (.-events tag)})
    (send! button "click")
    (is (= [["untag" 0]] (mapv data @messages)))))

(defn- cancelled?
  "True when a `toggle` from the check of a page is cancelled, with `answer` as the dispatch."
  [answer]
  (let [page  (page!)
        check (.querySelector page "span")]
    (mjs/listen page #js {:dispatch (partial answer check)
                          :events   #js [#js [#js ["toggle" "check"] "toggle"]]
                          :requests #js {:toggle #js ["aria-checked"]}})
    (.-defaultPrevented (send! check "toggle"))))

(defn- refuse-with-a-mark [^js check _message]
  (mjs/set-attrs check #js {:data-error "refused"}))

(defn- accept [^js check _message]
  (mjs/set-attrs check #js {:aria-checked "true"}))

(deftest listen-cancels-a-request-that-did-not-change-the-attributes-it-asks-for
  (is (true? (cancelled? refuse-with-a-mark)))
  (is (false? (cancelled? accept))))

(defn- count-step [^js state message]
  #js {:count (+ (.-count state) (aget message 1))})

(defn- count-of [^js state]
  (.-count state))

(deftest dispatcher-keeps-the-state-in-the-value-of-a-store
  (let [store    #js {:value #js {:count 1}}
        rendered (atom [])
        dispatch (mjs/dispatcher store count-step count-of (partial swap! rendered conj))
        returned (dispatch #js ["add" 2])]
    (testing "the store holds the new state, and the dispatch returns it"
      (is (= 3 (.-count (.-value store))))
      (is (identical? returned (.-value store))))
    (testing "the view of the new state is rendered"
      (is (= [3] @rendered)))))

(defn- count-attr [^js item]
  (.-count item))

(mjs/define-element "bm-js-count" #js {:attrs    #js ["count"]
                                      :template #js ["span" #js ["b" count-attr]]
                                      :css      "b{font-weight:600}"})

(deftest define-element-gives-a-hole-its-attributes-as-an-object
  (let [el (doto (.createElement js/document "bm-js-count") (.setAttribute "count" "3"))]
    (is (= "3" (.-textContent (.querySelector (.-shadowRoot el) "b"))))))
