(ns baredom.components.x-multi-combobox.model-test
  (:require [cljs.test :refer [deftest is testing]]
            [baredom.components.x-multi-combobox.model :as model]))

;; ── parse-value ──────────────────────────────────────────────────────────
(deftest parse-value-test
  (testing "nil and empty string yield empty set"
    (is (= #{} (model/parse-value nil)))
    (is (= #{} (model/parse-value ""))))
  (testing "single value"
    (is (= #{"apple"} (model/parse-value "apple"))))
  (testing "multiple values"
    (is (= #{"apple" "banana" "cherry"} (model/parse-value "apple,banana,cherry"))))
  (testing "trims whitespace"
    (is (= #{"apple" "banana"} (model/parse-value " apple , banana "))))
  (testing "ignores empty segments"
    (is (= #{"a" "b"} (model/parse-value "a,,b,")))))

;; ── serialize-value ──────────────────────────────────────────────────────
(deftest serialize-value-test
  (testing "empty set yields empty string"
    (is (= "" (model/serialize-value #{}))))
  (testing "single value"
    (is (= "apple" (model/serialize-value #{"apple"}))))
  (testing "sorted output"
    (is (= "apple,banana,cherry" (model/serialize-value #{"cherry" "banana" "apple"})))))

;; ── parse-max ────────────────────────────────────────────────────────────
(deftest parse-max-test
  (testing "nil yields nil"
    (is (nil? (model/parse-max nil))))
  (testing "valid positive integer"
    (is (= 3 (model/parse-max "3"))))
  (testing "zero yields nil"
    (is (nil? (model/parse-max "0"))))
  (testing "negative yields nil"
    (is (nil? (model/parse-max "-1"))))
  (testing "non-numeric yields nil"
    (is (nil? (model/parse-max "abc")))))

;; ── normalize ────────────────────────────────────────────────────────────
(deftest normalize-test
  (testing "defaults"
    (let [m (model/normalize {})]
      (is (= #{} (:value m)))
      (is (= "" (:placeholder m)))
      (is (= "" (:name m)))
      (is (false? (:disabled? m)))
      (is (false? (:required? m)))
      (is (false? (:open? m)))
      (is (= "bottom-start" (:placement m)))
      (is (nil? (:max m)))))
  (testing "with values"
    (let [m (model/normalize {:value-raw "a,b" :max-raw "5"
                              :disabled-present? true
                              :placement-raw "top-end"})]
      (is (= #{"a" "b"} (:value m)))
      (is (= 5 (:max m)))
      (is (true? (:disabled? m)))
      (is (= "top-end" (:placement m))))))

;; ── max-reached? ─────────────────────────────────────────────────────────
(deftest max-reached?-test
  (testing "nil max is never reached"
    (is (false? (model/max-reached? #{"a" "b" "c"} nil))))
  (testing "below limit"
    (is (false? (model/max-reached? #{"a"} 3))))
  (testing "at limit"
    (is (true? (model/max-reached? #{"a" "b" "c"} 3))))
  (testing "above limit"
    (is (true? (model/max-reached? #{"a" "b" "c" "d"} 3)))))

;; ── filter-options ───────────────────────────────────────────────────────
(deftest filter-options-test
  (let [opts [{:value "a" :label "Apple"}
              {:value "b" :label "Banana"}
              {:value "c" :label "Cherry"}
              {:value "d" :label "Apricot"}]]
    (testing "no query, no selected — returns all"
      (is (= 4 (count (model/filter-options opts nil #{})))))
    (testing "excludes selected values"
      (is (= 3 (count (model/filter-options opts nil #{"a"})))))
    (testing "filters by query"
      (is (= 2 (count (model/filter-options opts "ap" #{})))))
    (testing "filter + exclude combined"
      (is (= 1 (count (model/filter-options opts "ap" #{"a"})))))
    (testing "case insensitive"
      (is (= 2 (count (model/filter-options opts "AP" #{})))))))

;; ── navigation ───────────────────────────────────────────────────────────
(deftest navigation-test
  (testing "next wraps around"
    (is (= 0 (model/next-active-idx 3 nil)))
    (is (= 1 (model/next-active-idx 3 0)))
    (is (= 0 (model/next-active-idx 3 2))))
  (testing "prev wraps around"
    (is (= 2 (model/prev-active-idx 3 nil)))
    (is (= 0 (model/prev-active-idx 3 1)))
    (is (= 2 (model/prev-active-idx 3 0))))
  (testing "empty list"
    (is (= -1 (model/next-active-idx 0 nil)))
    (is (= -1 (model/prev-active-idx 0 nil)))))

;; ── clamp-active-idx ─────────────────────────────────────────────────────
(deftest clamp-active-idx-test
  (testing "empty visible list yields -1"
    (is (= -1 (model/clamp-active-idx 0 0)))
    (is (= -1 (model/clamp-active-idx 5 0)))
    (is (= -1 (model/clamp-active-idx nil 0))))
  (testing "in-range index passes through"
    (is (= 0 (model/clamp-active-idx 0 3)))
    (is (= 2 (model/clamp-active-idx 2 3))))
  (testing "out-of-range index clamps to last visible"
    (is (= 2 (model/clamp-active-idx 5 3))))
  (testing "nil raw-idx is treated as 0"
    (is (= 0 (model/clamp-active-idx nil 3)))))

;; ── last-value ───────────────────────────────────────────────────────────
(deftest last-value-test
  (testing "empty set yields nil"
    (is (nil? (model/last-value #{}))))
  (testing "single value"
    (is (= "apple" (model/last-value #{"apple"}))))
  (testing "returns the largest value in sort order"
    (is (= "cherry" (model/last-value #{"apple" "banana" "cherry"})))
    (is (= "cherry" (model/last-value #{"cherry" "apple" "banana"})))))

;; ── highlight-match ──────────────────────────────────────────────────────
(deftest highlight-match-test
  (testing "nil/empty query yields nil"
    (is (nil? (model/highlight-match "Apple" nil)))
    (is (nil? (model/highlight-match "Apple" ""))))
  (testing "match found"
    (is (= {:before "A" :match "pp" :after "le"}
           (model/highlight-match "Apple" "pp"))))
  (testing "case insensitive"
    (is (= {:before "" :match "App" :after "le"}
           (model/highlight-match "Apple" "app"))))
  (testing "no match yields nil"
    (is (nil? (model/highlight-match "Apple" "xyz")))))
