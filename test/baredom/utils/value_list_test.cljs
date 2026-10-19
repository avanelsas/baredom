(ns baredom.utils.value-list-test
  (:require [cljs.test :refer [deftest is testing]]
            [baredom.utils.value-list :as vl]))

(def ^:private charity "Liefdadigheid, religieus en non-profit")

(deftest parse-value-test
  (testing "nil and empty string yield empty set"
    (is (= #{} (vl/parse-value nil)))
    (is (= #{} (vl/parse-value ""))))
  (testing "single value"
    (is (= #{"apple"} (vl/parse-value "apple"))))
  (testing "multiple values"
    (is (= #{"apple" "banana" "cherry"} (vl/parse-value "apple,banana,cherry"))))
  (testing "trims whitespace"
    (is (= #{"apple" "banana"} (vl/parse-value " apple , banana "))))
  (testing "ignores empty segments"
    (is (= #{"a" "b"} (vl/parse-value "a,,b,")))))

(deftest serialize-value-test
  (testing "empty set yields empty string"
    (is (= "" (vl/serialize-value #{}))))
  (testing "single value"
    (is (= "apple" (vl/serialize-value #{"apple"}))))
  (testing "sorted output"
    (is (= "apple,banana,cherry" (vl/serialize-value #{"cherry" "banana" "apple"})))))

(deftest normalize-value-format-test
  (testing "csv and json pass through"
    (is (= "csv" (vl/normalize-value-format "csv")))
    (is (= "json" (vl/normalize-value-format "json"))))
  (testing "nil and unknown values yield csv"
    (is (= "csv" (vl/normalize-value-format nil)))
    (is (= "csv" (vl/normalize-value-format "JSON")))
    (is (= "csv" (vl/normalize-value-format "xml")))))

(deftest parse-json-value-test
  (testing "nil and empty string yield empty set"
    (is (= #{} (vl/parse-json-value nil)))
    (is (= #{} (vl/parse-json-value ""))))
  (testing "an empty array yields empty set"
    (is (= #{} (vl/parse-json-value "[]"))))
  (testing "a value with a comma stays one value"
    (is (= #{charity "news"} (vl/parse-json-value (str "[\"" charity "\",\"news\"]")))))
  (testing "spaces are kept"
    (is (= #{" padded "} (vl/parse-json-value "[\" padded \"]"))))
  (testing "an empty string is dropped, as in csv"
    (is (= #{"a"} (vl/parse-json-value "[\"a\",\"\"]")))
    (is (= #{} (vl/parse-json-value "[\"\"]"))))
  (testing "text that is not a JSON array of strings yields nil"
    (is (nil? (vl/parse-json-value "apple,banana")))
    (is (nil? (vl/parse-json-value "[\"a\",")))
    (is (nil? (vl/parse-json-value "\"apple\"")))
    (is (nil? (vl/parse-json-value "{\"a\":1}")))
    (is (nil? (vl/parse-json-value "[1,2]")))
    (is (nil? (vl/parse-json-value "null")))))

(deftest serialize-json-value-test
  (testing "empty set yields empty string"
    (is (= "" (vl/serialize-json-value #{}))))
  (testing "sorted JSON array with exact strings"
    (is (= (str "[\" padded \",\"" charity "\",\"news\"]")
           (vl/serialize-json-value #{"news" charity " padded "})))))

(deftest json-value-round-trips-test
  (let [values #{charity " padded " "news"}]
    (is (= values (vl/parse-json-value (vl/serialize-json-value values))))))

(deftest parse-value-as-test
  (testing "csv splits and trims as parse-value does"
    (is (= {:value #{"a" "b"} :invalid? false} (vl/parse-value-as "csv" " a , b "))))
  (testing "json keeps the exact strings"
    (is (= {:value #{"a, b"} :invalid? false} (vl/parse-value-as "json" "[\"a, b\"]"))))
  (testing "invalid json yields the empty set and invalid?"
    (is (= {:value #{} :invalid? true} (vl/parse-value-as "json" "a,b")))))

(deftest serialize-value-as-test
  (is (= "a,b" (vl/serialize-value-as "csv" #{"b" "a"})))
  (is (= "[\"a\",\"b\"]" (vl/serialize-value-as "json" #{"b" "a"}))))
