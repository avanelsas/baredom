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
