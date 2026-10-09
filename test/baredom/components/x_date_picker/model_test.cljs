(ns baredom.components.x-date-picker.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.dates :as dates]
            [baredom.components.x-date-picker.model :as model]))

;; UTC date primitives moved to baredom.utils.dates — covered by
;; baredom.utils.dates-test. This file keeps only x-date-picker-specific
;; model logic (mode/format parsing, canonicalization, display parsing).

(deftest error-metadata-test
  (testing "error is an observed attribute so setFieldError re-renders the message"
    (is (some #(= % "error") (array-seq model/observed-attributes))))
  (testing "error is a reflecting string property"
    (is (= model/attr-error (get-in model/property-api [:error :reflects-attribute])))
    (is (= 'string (get-in model/property-api [:error :type])))))

(deftest parse-mode-test
  (is (= :single (model/parse-mode nil)))
  (is (= :single (model/parse-mode "single")))
  (is (= :range  (model/parse-mode "range")))
  (is (= :single (model/parse-mode "invalid"))))

(deftest parse-format-test
  (is (= :iso        (model/parse-format nil)))
  (is (= :iso        (model/parse-format "iso")))
  (is (= :localized  (model/parse-format "localized"))))

(deftest canonicalize-keeps-a-locale-only-when-it-is-a-language-tag
  (is (= "nl-NL" (:locale (model/canonicalize {:locale "nl-NL"}))))
  (is (nil? (:locale (model/canonicalize {:locale "a"}))))
  (is (nil? (:locale (model/canonicalize {})))))

(deftest canonicalize-single-test
  (testing "single mode defaults"
    (let [c (model/canonicalize {})]
      (is (= :single (:mode c)))
      (is (= :iso    (:format c)))
      (is (= false   (:complete? c)))))
  (testing "single with value"
    (let [c (model/canonicalize {:value "2024-06-15"})]
      (is (= true (:complete? c)))
      (is (= "2024-06-15" (dates/date->iso (:value-d c))))))
  (testing "min/max parsing"
    (let [c (model/canonicalize {:min "2024-01-01" :max "2024-12-31"})]
      (is (= "2024-01-01" (dates/date->iso (:min-d c))))
      (is (= "2024-12-31" (dates/date->iso (:max-d c)))))))

(deftest canonicalize-range-test
  (testing "range mode with start and end"
    (let [c (model/canonicalize {:mode "range" :start "2024-03-01" :end "2024-03-15"})]
      (is (= :range (:mode c)))
      (is (= true (:complete? c)))
      (is (= "2024-03-01" (dates/date->iso (:start-d c))))
      (is (= "2024-03-15" (dates/date->iso (:end-d c))))))
  (testing "range incomplete when no end"
    (let [c (model/canonicalize {:mode "range" :start "2024-03-01"})]
      (is (= false (:complete? c))))))

(deftest committed-value?-test
  (testing "single mode"
    (is (= false (model/committed-value? (model/canonicalize {}))))
    (is (= false (model/committed-value? (model/canonicalize {:value ""}))))
    (is (= false (model/committed-value? (model/canonicalize {:value "not-a-date"}))))
    (is (= true  (model/committed-value? (model/canonicalize {:value "2024-06-15"})))))
  (testing "range mode — either endpoint counts"
    (is (= false (model/committed-value? (model/canonicalize {:mode "range"}))))
    (is (= true  (model/committed-value?
                  (model/canonicalize {:mode "range" :start "2024-03-01"}))))
    (is (= true  (model/committed-value?
                  (model/canonicalize {:mode "range" :end "2024-03-15"}))))
    (is (= true  (model/committed-value?
                  (model/canonicalize {:mode "range" :start "2024-03-01"
                                       :end "2024-03-15"})))))
  (testing "sparse map — no canonicalize"
    (is (= false (model/committed-value? {})))
    (is (= false (model/committed-value? {:mode :range})))))

(deftest parse-display->single-test
  (testing "valid ISO string"
    (let [{:keys [ok? date]} (model/parse-display->single "2024-06-15")]
      (is (= true ok?))
      (is (= "2024-06-15" (dates/date->iso date)))))
  (testing "invalid string"
    (let [{:keys [ok?]} (model/parse-display->single "not-a-date")]
      (is (= false ok?))))
  (testing "nil / empty"
    (is (= false (:ok? (model/parse-display->single nil))))
    (is (= false (:ok? (model/parse-display->single ""))))))

(deftest parse-display->range-test
  (testing "valid range string"
    (let [{:keys [ok? start end]}
          (model/parse-display->range "2024-01-01 - 2024-01-31" {:separator " - "})]
      (is (= true ok?))
      (is (= "2024-01-01" (dates/date->iso start)))
      (is (= "2024-01-31" (dates/date->iso end)))))
  (testing "missing separator"
    (let [{:keys [ok?]}
          (model/parse-display->range "2024-01-01" {:separator " - "})]
      ;; treated as single start
      (is (= true ok?))))
  (testing "empty string"
    (let [{:keys [ok?]}
          (model/parse-display->range "" {:separator " - "})]
      (is (= false ok?)))))

(defn- days-of
  "The shown days of June 2024 for a picker made from `raw`, with `focus-iso` as the reachable cell."
  [raw focus-iso]
  (model/shown-days (dates/iso->date "2024-06-01") (model/canonicalize raw) focus-iso))

(defn- day-of [days iso]
  (first (filter (comp #{iso} :iso) days)))

(deftest shown-days-gives-each-cell-its-key-test
  (let [days (days-of {} nil)]
    (is (= 42 (count days)))
    (is (= ["2024-05-26" "2024-05-27"] (mapv :key (take 2 days))))
    (is (= (mapv :iso days) (mapv :key days)) "the key of a cell is its date"))
  (is (= [] (model/shown-days nil (model/canonicalize {}) nil)) "no cells with no month"))

(deftest shown-days-gives-a-day-what-it-shows-test
  (testing "a selected day in single mode"
    (is (= {:key "2024-06-15" :iso "2024-06-15" :text "15" :outside "false" :disabled "false"
            :selected "true" :in-range "false" :range-edge "false" :tabindex "0"}
           (day-of (days-of {:value "2024-06-15"} "2024-06-15") "2024-06-15"))))
  (testing "a day of another month, and a day out of range"
    (let [days (days-of {:min "2024-06-10"} nil)]
      (is (= "true" (:outside (first days))))
      (is (= "true" (:disabled (day-of days "2024-06-05"))))
      (is (= "false" (:disabled (day-of days "2024-06-10"))))))
  (testing "the edges and the inside of a range"
    (let [days (days-of {:mode "range" :start "2024-06-10" :end "2024-06-12"} nil)]
      (is (= {:selected "true" :in-range "true" :range-edge "true"}
             (select-keys (day-of days "2024-06-10") [:selected :in-range :range-edge])))
      (is (= {:selected "false" :in-range "true" :range-edge "false"}
             (select-keys (day-of days "2024-06-11") [:selected :in-range :range-edge]))))))

(deftest shown-days-lets-tab-reach-only-the-focus-cell-test
  (is (= ["2024-06-15"]
         (into [] (comp (filter (comp #{"0"} :tabindex)) (map :iso)) (days-of {} "2024-06-15"))))
  (is (not-any? (comp #{"0"} :tabindex) (days-of {} "2024-09-01"))
      "no cell when the focus is in another month"))

