(ns baremirror.alpha.element-test
  (:require [baremirror.alpha.element :as mirror-element]
            [baremirror.alpha.parts :as mirror-parts]
            [baremirror.alpha.template :as template]
            [baremirror.alpha.test-stage :refer [element! element-with! record-type records-of! remove-stages! stage!]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(use-fixtures :each {:after remove-stages!})

(def ^:private task-count-template
  [:span [:b :count] " " [:i :label]])

(mirror-element/define-element! "bm-task-count"
                        {:attrs    ["count" "label"]
                         :template task-count-template
                         :css      ":host{display:inline-flex}"})

(mirror-element/define-element! "bm-frame"
                        {:attrs    ["title"]
                         :template [:section [:h2 :title] [:slot]]})

(defn- task-count!
  "A bm-task-count element with the attributes `attrs`, in the document."
  [attrs]
  (let [el (element-with! "bm-task-count" attrs)]
    (.append (stage!) el)
    el))

(defn- in-shadow [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(deftest define-element-shows-the-attributes-of-an-element
  (let [el (task-count! {"count" "3" "label" "left"})]
    (testing "each hole has the value of its attribute"
      (is (= "3" (.-textContent (in-shadow el "b"))))
      (is (= "left" (.-textContent (in-shadow el "i")))))
    (testing "the style element holds the css"
      (is (= ":host{display:inline-flex}" (.-textContent (in-shadow el "style")))))))

(deftest define-element-follows-a-change-of-an-attribute
  (let [el      (task-count! {"count" "3" "label" "left"})
        counter (in-shadow el "b")
        records (records-of! (.-shadowRoot el) (fn [] (.setAttribute el "count" "2")))]
    (testing "the node of the hole stays and shows the new value"
      (is (identical? counter (in-shadow el "b")))
      (is (= "2" (.-textContent counter))))
    (testing "only the text that differs is written"
      (is (= ["characterData"] (mapv record-type records))))))

(deftest define-element-shows-an-element-with-no-attributes
  (let [el (task-count! {})]
    (is (= "" (.-textContent (in-shadow el "b"))))))

(deftest define-element-shows-an-element-before-it-is-in-the-document
  (let [el (element-with! "bm-task-count" {"count" "3"})]
    (is (= "3" (.-textContent (in-shadow el "b"))))))

(deftest define-element-refuses-a-template-with-an-on-entry
  (is (thrown? ExceptionInfo
               (mirror-element/define-element! "bm-refused"
                                       {:attrs    []
                                        :template [:button {:on {"click" :press}} "Go"]}))))

(deftest define-element-leaves-a-registered-tag-as-it-is
  (mirror-element/define-element! "bm-task-count" {:attrs [] :template [:p "other"]})
  (is (some? (in-shadow (task-count! {"count" "3"}) "b"))))

(defn- prerendered-task-count!
  "A bm-task-count element that already has a shadow root with the tree of its template."
  []
  (let [el    (element! "bm-task-count")
        fixed (:fixed (template/split "bm-task-count" task-count-template))]
    (.append (.attachShadow el #js {:mode "open"}) (element! "style") (mirror-parts/make-node! fixed))
    el))

(deftest define-element-writes-into-a-shadow-root-that-is-already-there
  (let [el      (prerendered-task-count!)
        counter (in-shadow el "b")]
    (.setAttribute el "count" "3")
    (testing "the node that was there has the value"
      (is (identical? counter (in-shadow el "b")))
      (is (= "3" (.-textContent counter))))
    (testing "no second tree is made"
      (is (= 2 (.. el -shadowRoot -childElementCount))))))

(deftest define-element-shows-the-children-of-an-element-in-a-slot
  (let [child (element! "p")
        el    (doto (element-with! "bm-frame" {"title" "Tasks"}) (.append child))]
    (.append (stage!) el)
    (is (= "Tasks" (.-textContent (in-shadow el "h2"))))
    (is (= [child] (vec (array-seq (.assignedNodes (in-shadow el "slot"))))))))
