(ns baredom.components.x-date-picker.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-date-picker.model :as model]))

(deftest pad2-test
  (is (= "01" (model/pad2 1)))
  (is (= "09" (model/pad2 9)))
  (is (= "10" (model/pad2 10)))
  (is (= "31" (model/pad2 31))))

(deftest date->iso-test
  (testing "nil input returns nil"
    (is (nil? (model/date->iso nil))))
  (testing "known date round-trips"
    (let [d (js/Date. "2024-03-15T00:00:00.000Z")]
      (is (= "2024-03-15" (model/date->iso d))))))

(deftest iso->date-test
  (testing "valid ISO string produces a Date"
    (let [d (model/iso->date "2024-03-15")]
      (is (some? d))
      (is (= 2024 (.getUTCFullYear d)))
      (is (= 2 (.getUTCMonth d)))
      (is (= 15 (.getUTCDate d)))))
  (testing "invalid string returns nil"
    (is (nil? (model/iso->date "not-a-date")))
    (is (nil? (model/iso->date nil)))
    (is (nil? (model/iso->date ""))))
  (testing "round-trip"
    (let [original "2026-01-31"
          d        (model/iso->date original)]
      (is (= original (model/date->iso d))))))

(deftest compare-date-test
  (let [d1 (model/iso->date "2024-01-01")
        d2 (model/iso->date "2024-06-15")
        d3 (model/iso->date "2024-01-01")]
    (is (= -1 (model/compare-date d1 d2)))
    (is (= 1  (model/compare-date d2 d1)))
    (is (= 0  (model/compare-date d1 d3)))))

(deftest add-days-test
  (let [d  (model/iso->date "2024-01-30")
        d2 (model/add-days d 3)]
    (is (= "2024-02-02" (model/date->iso d2))))
  (let [d  (model/iso->date "2024-02-28")
        d2 (model/add-days d 1)]
    ;; 2024 is leap year
    (is (= "2024-02-29" (model/date->iso d2)))))

(deftest start-of-month-test
  (let [d   (model/iso->date "2024-03-15")
        som (model/start-of-month d)]
    (is (= "2024-03-01" (model/date->iso som)))))

(deftest days-in-month-test
  (let [jan (model/iso->date "2024-01-15")
        feb (model/iso->date "2024-02-10")
        feb-non-leap (model/iso->date "2023-02-10")]
    (is (= 31 (model/days-in-month jan)))
    (is (= 29 (model/days-in-month feb)))
    (is (= 28 (model/days-in-month feb-non-leap)))))

(deftest in-range-test
  (let [a (model/iso->date "2024-01-01")
        b (model/iso->date "2024-01-31")
        mid (model/iso->date "2024-01-15")
        out (model/iso->date "2024-02-01")]
    (is (= true  (model/in-range? mid a b)))
    (is (= true  (model/in-range? a a b)))   ; inclusive start
    (is (= true  (model/in-range? b a b)))   ; inclusive end
    (is (= false (model/in-range? out a b)))))

(deftest month-grid-test
  (testing "always returns 42 items"
    (let [d    (model/iso->date "2024-03-01")
          grid (model/month-grid d)]
      (is (= 42 (count grid)))))
  (testing "grid items have :date and :in-month?"
    (let [d    (model/iso->date "2024-03-01")
          grid (model/month-grid d)
          first-in-month (first (filter :in-month? grid))]
      (is (some? first-in-month))
      (is (some? (:date first-in-month)))))
  (testing "all days in month are marked in-month?"
    (let [d    (model/iso->date "2024-03-01")
          grid (model/month-grid d)
          in-month (filter :in-month? grid)]
      (is (= 31 (count in-month)))))
  (testing "first cell is a Sunday or before the 1st"
    (let [d    (model/iso->date "2024-03-01")
          grid (model/month-grid d)
          ^js first-date (:date (first grid))]
      ;; March 2024 starts on Friday (5), so grid starts Sun Feb 25
      (is (= 0 (.getUTCDay first-date))))))

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
      (is (= "2024-06-15" (model/date->iso (:value-d c))))))
  (testing "min/max parsing"
    (let [c (model/canonicalize {:min "2024-01-01" :max "2024-12-31"})]
      (is (= "2024-01-01" (model/date->iso (:min-d c))))
      (is (= "2024-12-31" (model/date->iso (:max-d c)))))))

(deftest canonicalize-range-test
  (testing "range mode with start and end"
    (let [c (model/canonicalize {:mode "range" :start "2024-03-01" :end "2024-03-15"})]
      (is (= :range (:mode c)))
      (is (= true (:complete? c)))
      (is (= "2024-03-01" (model/date->iso (:start-d c))))
      (is (= "2024-03-15" (model/date->iso (:end-d c))))))
  (testing "range incomplete when no end"
    (let [c (model/canonicalize {:mode "range" :start "2024-03-01"})]
      (is (= false (:complete? c))))))

(deftest parse-display->single-test
  (testing "valid ISO string"
    (let [{:keys [ok? date]} (model/parse-display->single "2024-06-15")]
      (is (= true ok?))
      (is (= "2024-06-15" (model/date->iso date)))))
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
      (is (= "2024-01-01" (model/date->iso start)))
      (is (= "2024-01-31" (model/date->iso end)))))
  (testing "missing separator"
    (let [{:keys [ok?]}
          (model/parse-display->range "2024-01-01" {:separator " - "})]
      ;; treated as single start
      (is (= true ok?))))
  (testing "empty string"
    (let [{:keys [ok?]}
          (model/parse-display->range "" {:separator " - "})]
      (is (= false ok?)))))

(deftest add-months-test
  (testing "forward one month"
    (let [d  (model/iso->date "2024-01-15")
          d2 (model/add-months d 1)]
      (is (= "2024-02-15" (model/date->iso d2)))))
  (testing "day clamping on overflow (Jan 31 + 1 = Feb 29 leap year)"
    (let [d  (model/iso->date "2024-01-31")
          d2 (model/add-months d 1)]
      (is (= "2024-02-29" (model/date->iso d2)))))
  (testing "day clamping non-leap year (Jan 31 + 1 = Feb 28)"
    (let [d  (model/iso->date "2023-01-31")
          d2 (model/add-months d 1)]
      (is (= "2023-02-28" (model/date->iso d2)))))
  (testing "backward one month"
    (let [d  (model/iso->date "2024-03-15")
          d2 (model/add-months d -1)]
      (is (= "2024-02-15" (model/date->iso d2)))))
  (testing "cross year boundary"
    (let [d  (model/iso->date "2024-01-15")
          d2 (model/add-months d -1)]
      (is (= "2023-12-15" (model/date->iso d2))))))

(deftest start-of-week-test
  (testing "Sunday stays on Sunday"
    ;; 2024-03-03 is a Sunday
    (let [d (model/iso->date "2024-03-03")]
      (is (= "2024-03-03" (model/date->iso (model/start-of-week d))))))
  (testing "Wednesday goes back to Sunday"
    ;; 2024-03-06 is a Wednesday
    (let [d (model/iso->date "2024-03-06")]
      (is (= "2024-03-03" (model/date->iso (model/start-of-week d)))))))

(deftest end-of-week-test
  (testing "Saturday stays on Saturday"
    ;; 2024-03-09 is a Saturday
    (let [d (model/iso->date "2024-03-09")]
      (is (= "2024-03-09" (model/date->iso (model/end-of-week d))))))
  (testing "Wednesday goes forward to Saturday"
    (let [d (model/iso->date "2024-03-06")]
      (is (= "2024-03-09" (model/date->iso (model/end-of-week d)))))))

(deftest clamp-to-range-test
  (let [min-d (model/iso->date "2024-01-01")
        max-d (model/iso->date "2024-12-31")
        below (model/iso->date "2023-06-01")
        above (model/iso->date "2025-03-01")
        mid   (model/iso->date "2024-06-15")]
    (is (= "2024-01-01" (model/date->iso (model/clamp-to-range below min-d max-d))))
    (is (= "2024-12-31" (model/date->iso (model/clamp-to-range above min-d max-d))))
    (is (= "2024-06-15" (model/date->iso (model/clamp-to-range mid   min-d max-d))))))
