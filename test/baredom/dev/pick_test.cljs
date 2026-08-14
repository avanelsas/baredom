(ns baredom.dev.pick-test
  (:require [baredom.dev.pick :as pick]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [clojure.string :as str]))

(def ^:private marker "data-pick-test")

(defn- cleanup-dom! []
  (doseq [^js node (array-seq (.querySelectorAll js/document (str "[" marker "]")))]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn- append-div! []
  (let [^js el (.createElement js/document "div")]
    (.setAttribute el marker "")
    (.appendChild js/document.body el)
    el))

(defn- id-number
  "The number in a `tag#n` id."
  [id]
  (js/parseInt (second (str/split id #"#")) 10))

;; The counters live in a defonce atom no test can reset, so an element's number depends on what
;; ran before it.
(deftest id-of-numbers-in-document-order-test
  (let [a (append-div!)
        b (append-div!)]
    (testing "an element earlier in the document gets the lower number"
      (is (< (id-number (pick/id-of a)) (id-number (pick/id-of b)))))
    (testing "the id names the tag"
      (is (str/starts-with? (pick/id-of a) "div#")))
    (testing "asking twice answers the same"
      (is (= (pick/id-of a) (pick/id-of a))))))

(deftest element-for-resolves-a-stamped-id-test
  (let [el (append-div!)]
    (is (identical? el (pick/element-for (pick/id-of el))))))

(deftest element-for-refuses-nil-test
  (testing "a nil id resolves to nothing, with unstamped elements on the page"
    (append-div!)
    (append-div!)
    (is (nil? (pick/element-for nil))))
  (testing "refuses after something has been stamped"
    (pick/id-of (append-div!))
    (append-div!)
    (is (nil? (pick/element-for nil)))))

(deftest element-for-answers-nil-for-an-unknown-id-test
  (append-div!)
  (is (nil? (pick/element-for "div#999999"))))
