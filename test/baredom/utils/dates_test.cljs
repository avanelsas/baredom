(ns baredom.utils.dates-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.dates :as dates]))

(deftest pad2-test
  (is (= "01" (dates/pad2 1)))
  (is (= "09" (dates/pad2 9)))
  (is (= "10" (dates/pad2 10)))
  (is (= "31" (dates/pad2 31))))

(deftest date->iso-test
  (testing "nil input returns nil"
    (is (nil? (dates/date->iso nil))))
  (testing "known date round-trips"
    (let [d (js/Date. "2024-03-15T00:00:00.000Z")]
      (is (= "2024-03-15" (dates/date->iso d))))))

(deftest iso->date-test
  (testing "valid ISO string produces a Date"
    (let [d (dates/iso->date "2024-03-15")]
      (is (some? d))
      (is (= 2024 (.getUTCFullYear d)))
      (is (= 2 (.getUTCMonth d)))
      (is (= 15 (.getUTCDate d)))))
  (testing "invalid string returns nil"
    (is (nil? (dates/iso->date "not-a-date")))
    (is (nil? (dates/iso->date nil)))
    (is (nil? (dates/iso->date ""))))
  (testing "round-trip"
    (let [original "2026-01-31"
          d        (dates/iso->date original)]
      (is (= original (dates/date->iso d))))))

(deftest compare-date-test
  (let [d1 (dates/iso->date "2024-01-01")
        d2 (dates/iso->date "2024-06-15")
        d3 (dates/iso->date "2024-01-01")]
    (is (= -1 (dates/compare-date d1 d2)))
    (is (= 1  (dates/compare-date d2 d1)))
    (is (= 0  (dates/compare-date d1 d3)))))

(deftest min-max-date-test
  (let [d1 (dates/iso->date "2024-01-01")
        d2 (dates/iso->date "2024-06-15")]
    (is (= "2024-01-01" (dates/date->iso (dates/min-date d1 d2))))
    (is (= "2024-06-15" (dates/date->iso (dates/max-date d1 d2))))))

(deftest add-days-test
  (let [d  (dates/iso->date "2024-01-30")
        d2 (dates/add-days d 3)]
    (is (= "2024-02-02" (dates/date->iso d2))))
  (let [d  (dates/iso->date "2024-02-28")
        d2 (dates/add-days d 1)]
    ;; 2024 is leap year
    (is (= "2024-02-29" (dates/date->iso d2)))))

(deftest start-of-month-test
  (let [d   (dates/iso->date "2024-03-15")
        som (dates/start-of-month d)]
    (is (= "2024-03-01" (dates/date->iso som)))))

(deftest days-in-month-test
  (let [jan (dates/iso->date "2024-01-15")
        feb (dates/iso->date "2024-02-10")
        feb-non-leap (dates/iso->date "2023-02-10")]
    (is (= 31 (dates/days-in-month jan)))
    (is (= 29 (dates/days-in-month feb)))
    (is (= 28 (dates/days-in-month feb-non-leap)))))

(deftest weekday-0-sun-test
  ;; 2024-03-03 is a Sunday, 2024-03-06 a Wednesday
  (is (= 0 (dates/weekday-0-sun (dates/iso->date "2024-03-03"))))
  (is (= 3 (dates/weekday-0-sun (dates/iso->date "2024-03-06")))))

(deftest in-range-test
  (let [a (dates/iso->date "2024-01-01")
        b (dates/iso->date "2024-01-31")
        mid (dates/iso->date "2024-01-15")
        out (dates/iso->date "2024-02-01")]
    (is (= true  (dates/in-range? mid a b)))
    (is (= true  (dates/in-range? a a b)))   ; inclusive start
    (is (= true  (dates/in-range? b a b)))   ; inclusive end
    (is (= false (dates/in-range? out a b)))))

(deftest add-months-test
  (testing "forward one month"
    (let [d  (dates/iso->date "2024-01-15")
          d2 (dates/add-months d 1)]
      (is (= "2024-02-15" (dates/date->iso d2)))))
  (testing "day clamping on overflow (Jan 31 + 1 = Feb 29 leap year)"
    (let [d  (dates/iso->date "2024-01-31")
          d2 (dates/add-months d 1)]
      (is (= "2024-02-29" (dates/date->iso d2)))))
  (testing "day clamping non-leap year (Jan 31 + 1 = Feb 28)"
    (let [d  (dates/iso->date "2023-01-31")
          d2 (dates/add-months d 1)]
      (is (= "2023-02-28" (dates/date->iso d2)))))
  (testing "backward one month"
    (let [d  (dates/iso->date "2024-03-15")
          d2 (dates/add-months d -1)]
      (is (= "2024-02-15" (dates/date->iso d2)))))
  (testing "cross year boundary"
    (let [d  (dates/iso->date "2024-01-15")
          d2 (dates/add-months d -1)]
      (is (= "2023-12-15" (dates/date->iso d2))))))

(deftest start-of-week-test
  (testing "Sunday-start (default arity)"
    ;; 2024-03-03 Sunday, 2024-03-06 Wednesday
    (is (= "2024-03-03" (dates/date->iso (dates/start-of-week (dates/iso->date "2024-03-03")))))
    (is (= "2024-03-03" (dates/date->iso (dates/start-of-week (dates/iso->date "2024-03-06"))))))
  (testing "Monday-start (fdow=1)"
    ;; Sunday belongs to the week starting the previous Monday
    (is (= "2024-02-26" (dates/date->iso (dates/start-of-week (dates/iso->date "2024-03-03") 1))))
    ;; Wednesday goes back to its Monday
    (is (= "2024-03-04" (dates/date->iso (dates/start-of-week (dates/iso->date "2024-03-06") 1))))
    ;; Monday stays put
    (is (= "2024-03-04" (dates/date->iso (dates/start-of-week (dates/iso->date "2024-03-04") 1))))))

(deftest end-of-week-test
  (testing "Sunday-start (default arity) → Saturday"
    (is (= "2024-03-09" (dates/date->iso (dates/end-of-week (dates/iso->date "2024-03-09")))))
    (is (= "2024-03-09" (dates/date->iso (dates/end-of-week (dates/iso->date "2024-03-06"))))))
  (testing "Monday-start (fdow=1) → Sunday"
    (is (= "2024-03-10" (dates/date->iso (dates/end-of-week (dates/iso->date "2024-03-06") 1))))))

(deftest clamp-to-range-test
  (let [min-d (dates/iso->date "2024-01-01")
        max-d (dates/iso->date "2024-12-31")
        below (dates/iso->date "2023-06-01")
        above (dates/iso->date "2025-03-01")
        mid   (dates/iso->date "2024-06-15")]
    (is (= "2024-01-01" (dates/date->iso (dates/clamp-to-range below min-d max-d))))
    (is (= "2024-12-31" (dates/date->iso (dates/clamp-to-range above min-d max-d))))
    (is (= "2024-06-15" (dates/date->iso (dates/clamp-to-range mid   min-d max-d))))))

(deftest month-grid-test
  (testing "always returns 42 items"
    (let [grid (dates/month-grid (dates/iso->date "2024-03-01"))]
      (is (= 42 (count grid)))))
  (testing "grid items have :date and :in-month?"
    (let [grid (dates/month-grid (dates/iso->date "2024-03-01"))
          first-in-month (first (filter :in-month? grid))]
      (is (some? first-in-month))
      (is (some? (:date first-in-month)))))
  (testing "all days in month are marked in-month?"
    (let [grid (dates/month-grid (dates/iso->date "2024-03-01"))
          in-month (filter :in-month? grid)]
      (is (= 31 (count in-month)))))
  (testing "Sunday-start (default arity): first cell is a Sunday"
    ;; March 2024 starts on Friday, so the grid starts Sun Feb 25
    (let [grid (dates/month-grid (dates/iso->date "2024-03-01"))
          ^js first-date (:date (first grid))]
      (is (= 0 (.getUTCDay first-date)))
      (is (= "2024-02-25" (dates/date->iso first-date)))))
  (testing "Monday-start (fdow=1): first cell is a Monday"
    (let [grid (dates/month-grid (dates/iso->date "2024-03-01") 1)
          ^js first-date (:date (first grid))]
      (is (= 1 (.getUTCDay first-date)))
      (is (= "2024-02-26" (dates/date->iso first-date)))
      (is (= 42 (count grid)))
      (is (= 31 (count (filter :in-month? grid)))))))

(deftest iso-week-number-test
  (testing "nil input returns nil"
    (is (nil? (dates/iso-week-number nil))))
  (testing "known ISO week numbers"
    ;; 2024-01-01 is a Monday → ISO week 1
    (is (= 1 (dates/iso-week-number (dates/iso->date "2024-01-01"))))
    ;; 2024-01-08 is a Monday → ISO week 2
    (is (= 2 (dates/iso-week-number (dates/iso->date "2024-01-08"))))
    ;; 2024-06-15 falls in ISO week 24
    (is (= 24 (dates/iso-week-number (dates/iso->date "2024-06-15"))))
    ;; 2024-12-31 belongs to ISO week 1 of the next year
    (is (= 1 (dates/iso-week-number (dates/iso->date "2024-12-31"))))))
