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
      (is (= "top-end" (:placement m)))))
  (testing "error defaults to empty with strict has-error?"
    (let [m (model/normalize {})]
      (is (= "" (:error m)))
      (is (false? (:has-error? m)))))
  (testing "error present"
    (let [m (model/normalize {:error-raw "Pick at least one"})]
      (is (= "Pick at least one" (:error m)))
      (is (true? (:has-error? m)))))
  (testing "empty-string error is treated as no error"
    (let [m (model/normalize {:error-raw ""})]
      (is (= "" (:error m)))
      (is (false? (:has-error? m))))))

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

;; ── chip-label ───────────────────────────────────────────────────────────
(deftest chip-label-test
  (let [options [{:value "apple" :label "Apple"}]]
    (testing "a value with an option shows the label of the option"
      (is (= "Apple" (model/chip-label options "apple"))))
    (testing "a value with no option shows itself"
      (is (= "kiwi" (model/chip-label options "kiwi"))))))

;; ── shown-panel ──────────────────────────────────────────────────────────
(def ^:private fruits
  [{:value "apple" :label "Apple"} {:value "banana" :label "Banana"}
   {:value "cherry" :label "Cherry"}])

(deftest shown-panel-gives-each-option-what-it-shows-test
  (let [{:keys [items active-id]} (model/shown-panel fruits "" 1 {:value #{} :max nil})]
    (is (= ["apple|0" "banana|0" "cherry|0"] (mapv :key items)))
    (is (= {:key "banana|0" :id "x-mcb-opt-1" :value "banana" :active "" :disabled nil
            :aria-disabled nil :before "Banana" :match "" :after ""}
           (nth items 1)))
    (is (= "x-mcb-opt-1" active-id))))

(deftest shown-panel-leaves-out-a-selected-option-and-keeps-the-keys-test
  (let [{:keys [items]} (model/shown-panel fruits "" 0 {:value #{"apple"} :max nil})]
    (is (= ["banana|0" "cherry|0"] (mapv :key items)) "the key is made of the value")
    (is (= ["x-mcb-opt-0" "x-mcb-opt-1"] (mapv :id items)))))

(deftest shown-panel-disables-every-option-at-the-maximum-test
  (let [{:keys [items]} (model/shown-panel fruits "" 0 {:value #{"apple"} :max 1})]
    (is (= [{:disabled "" :aria-disabled "true"} {:disabled "" :aria-disabled "true"}]
           (mapv #(select-keys % [:disabled :aria-disabled]) items)))))

(deftest shown-panel-shows-the-empty-message-when-nothing-is-left-test
  (is (= {:items [{:key model/empty-key :text model/empty-message}] :active-id nil}
         (model/shown-panel fruits "zzz" 0 {:value #{} :max nil}))))


;; ── value formats ────────────────────────────────────────────────────────
(def ^:private charity "Liefdadigheid, religieus en non-profit")

(deftest normalize-value-format-test
  (testing "csv and json pass through"
    (is (= "csv" (model/normalize-value-format "csv")))
    (is (= "json" (model/normalize-value-format "json"))))
  (testing "nil and unknown values yield csv"
    (is (= "csv" (model/normalize-value-format nil)))
    (is (= "csv" (model/normalize-value-format "JSON")))
    (is (= "csv" (model/normalize-value-format "xml")))))

(deftest parse-json-value-test
  (testing "nil and empty string yield empty set"
    (is (= #{} (model/parse-json-value nil)))
    (is (= #{} (model/parse-json-value ""))))
  (testing "an empty array yields empty set"
    (is (= #{} (model/parse-json-value "[]"))))
  (testing "a value with a comma stays one value"
    (is (= #{charity "news"} (model/parse-json-value (str "[\"" charity "\",\"news\"]")))))
  (testing "spaces are kept"
    (is (= #{" padded "} (model/parse-json-value "[\" padded \"]"))))
  (testing "text that is not a JSON array of strings yields nil"
    (is (nil? (model/parse-json-value "apple,banana")))
    (is (nil? (model/parse-json-value "[\"a\",")))
    (is (nil? (model/parse-json-value "\"apple\"")))
    (is (nil? (model/parse-json-value "{\"a\":1}")))
    (is (nil? (model/parse-json-value "[1,2]")))
    (is (nil? (model/parse-json-value "null")))))

(deftest serialize-json-value-test
  (testing "empty set yields empty string"
    (is (= "" (model/serialize-json-value #{}))))
  (testing "sorted JSON array with exact strings"
    (is (= (str "[\" padded \",\"" charity "\",\"news\"]")
           (model/serialize-json-value #{"news" charity " padded "})))))

(deftest json-value-round-trips-test
  (let [values #{charity " padded " "news"}]
    (is (= values (model/parse-json-value (model/serialize-json-value values))))))

(deftest parse-value-as-test
  (testing "csv splits and trims as parse-value does"
    (is (= {:value #{"a" "b"} :invalid? false} (model/parse-value-as "csv" " a , b "))))
  (testing "json keeps the exact strings"
    (is (= {:value #{"a, b"} :invalid? false} (model/parse-value-as "json" "[\"a, b\"]"))))
  (testing "invalid json yields the empty set and invalid?"
    (is (= {:value #{} :invalid? true} (model/parse-value-as "json" "a,b")))))

(deftest serialize-value-as-test
  (is (= "a,b" (model/serialize-value-as "csv" #{"b" "a"})))
  (is (= "[\"a\",\"b\"]" (model/serialize-value-as "json" #{"b" "a"}))))

(deftest csv-unrepresentable-test
  (testing "plain values can be held"
    (is (= [] (model/csv-unrepresentable ["apple" "banana split"]))))
  (testing "values with the separator or outer whitespace cannot"
    (is (= [charity " padded" "tail "]
           (model/csv-unrepresentable ["apple" charity " padded" "tail "]))))
  (testing "the message names the values and the json format"
    (let [msg (model/unrepresentable-message [charity])]
      (is (not= -1 (.indexOf msg charity)))
      (is (not= -1 (.indexOf msg "value-format=\"json\""))))))

(deftest normalize-value-format-model-test
  (testing "defaults to csv with a valid value"
    (let [m (model/normalize {})]
      (is (= "csv" (:value-format m)))
      (is (false? (:value-invalid? m)))))
  (testing "json keeps a value with a comma"
    (let [m (model/normalize {:value-format-raw "json" :value-raw (str "[\"" charity "\"]")})]
      (is (= #{charity} (:value m)))
      (is (false? (:value-invalid? m)))))
  (testing "invalid json yields the empty set and value-invalid?"
    (let [m (model/normalize {:value-format-raw "json" :value-raw "apple,banana"})]
      (is (= #{} (:value m)))
      (is (true? (:value-invalid? m))))))
