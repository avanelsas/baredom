(ns baredom.components.x-calendar.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.dates :as dates]
            [baredom.components.x-calendar.model :as model]))

(deftest parse-mode-test
  (is (= :single (model/parse-mode nil)))
  (is (= :single (model/parse-mode "single")))
  (is (= :range  (model/parse-mode "range")))
  (is (= :single (model/parse-mode "bogus"))))

(deftest parse-first-dow-test
  (testing "digit forms"
    (is (= 0 (model/parse-first-dow "0")))
    (is (= 1 (model/parse-first-dow "1")))
    (is (= 6 (model/parse-first-dow "6"))))
  (testing "weekday words (case-insensitive)"
    (is (= 0 (model/parse-first-dow "sunday")))
    (is (= 1 (model/parse-first-dow "Monday")))
    (is (= 6 (model/parse-first-dow "SATURDAY"))))
  (testing "invalid / out-of-range / nil default to 0"
    (is (= 0 (model/parse-first-dow nil)))
    (is (= 0 (model/parse-first-dow "")))
    (is (= 0 (model/parse-first-dow "7")))
    (is (= 0 (model/parse-first-dow "funday")))))

(deftest parse-disabled-dates-test
  (testing "space and comma separated"
    (is (= #{"2026-05-01" "2026-05-04"}
           (model/parse-disabled-dates "2026-05-01 2026-05-04")))
    (is (= #{"2026-05-01" "2026-05-04"}
           (model/parse-disabled-dates "2026-05-01, 2026-05-04"))))
  (testing "drops invalid entries and handles empty / nil"
    (is (= #{"2026-05-01"}
           (model/parse-disabled-dates "2026-05-01 not-a-date")))
    (is (= #{} (model/parse-disabled-dates "")))
    (is (= #{} (model/parse-disabled-dates "   ")))
    (is (= #{} (model/parse-disabled-dates nil)))))

(deftest safe-locale-test
  (testing "valid BCP-47 tags pass through, trimmed"
    (is (= "en-US" (model/safe-locale "en-US")))
    (is (= "de-DE" (model/safe-locale "  de-DE  "))))
  (testing "malformed tags fall back to nil so callers use the default"
    (is (nil? (model/safe-locale "en_US")))
    (is (nil? (model/safe-locale "!!bogus")))
    (is (nil? (model/safe-locale "")))
    (is (nil? (model/safe-locale nil)))))

(deftest parse-year-month-test
  (is (= "2026-03-01" (dates/date->iso (model/parse-year-month "2026-03"))))
  (testing "full ISO date snaps to start-of-month"
    (is (= "2026-03-01" (dates/date->iso (model/parse-year-month "2026-03-18")))))
  (testing "invalid returns nil"
    (is (nil? (model/parse-year-month "2026")))
    (is (nil? (model/parse-year-month "bogus")))
    (is (nil? (model/parse-year-month nil)))))

(deftest date->year-month-test
  (is (= "2026-05" (model/date->year-month (dates/iso->date "2026-05-21"))))
  (is (nil? (model/date->year-month nil))))

(deftest displayed-month-test
  (let [today (dates/iso->date "2026-01-10")]
    (testing "explicit month attr wins"
      (is (= "2026-03-01"
             (dates/date->iso
              (model/displayed-month {:month-raw "2026-03"
                                      :value-d   (dates/iso->date "2026-07-15")
                                      :today     today})))))
    (testing "falls back to value month"
      (is (= "2026-07-01"
             (dates/date->iso
              (model/displayed-month {:value-d (dates/iso->date "2026-07-15")
                                      :today   today})))))
    (testing "falls back to start month"
      (is (= "2026-09-01"
             (dates/date->iso
              (model/displayed-month {:start-d (dates/iso->date "2026-09-20")
                                      :today   today})))))
    (testing "falls back to today"
      (is (= "2026-01-01"
             (dates/date->iso (model/displayed-month {:today today})))))))

(deftest canonicalize-test
  (let [today (dates/iso->date "2026-05-21")]
    (testing "single mode defaults"
      (let [c (model/canonicalize {:today today})]
        (is (= :single (:mode c)))
        (is (= 0 (:fdow c)))
        (is (nil? (:value c)))
        (is (= "2026-05-01" (:view-iso c)))
        (is (false? (:disabled? c)))))
    (testing "single with value"
      (let [c (model/canonicalize {:value "2026-06-15" :today today})]
        (is (= "2026-06-15" (:value c)))
        (is (= "2026-06-01" (:view-iso c)))))
    (testing "range with bounds and flags"
      (let [c (model/canonicalize {:mode "range" :start "2026-05-03" :end "2026-05-09"
                                   :min "2026-05-01" :max "2026-05-31"
                                   :first-day-of-week "monday"
                                   :disabled-dates "2026-05-06"
                                   :disabled? true :show-week-numbers? true
                                   :today today})]
        (is (= :range (:mode c)))
        (is (= 1 (:fdow c)))
        (is (= "2026-05-03" (:start c)))
        (is (= "2026-05-09" (:end c)))
        (is (= "2026-05-01" (:min c)))
        (is (= "2026-05-31" (:max c)))
        (is (= #{"2026-05-06"} (:disabled-set c)))
        (is (true? (:disabled? c)))
        (is (true? (:show-week-numbers? c)))))
    (testing "model is value-comparable (no Date objects leak in)"
      (let [raw {:value "2026-06-15" :mode "range" :start "2026-05-03"
                 :today today}]
        (is (= (model/canonicalize raw) (model/canonicalize raw)))))))

(deftest weekday-names-test
  (testing "seven labels, Sunday-start"
    (let [names (model/weekday-names "en-US" 0)]
      (is (= 7 (count names)))
      (is (= "Sun" (first names)))))
  (testing "Monday-start rotates the labels"
    (is (= "Mon" (first (model/weekday-names "en-US" 1))))))

(deftest month-label-test
  (is (= "May 2026"
         (model/month-label (dates/iso->date "2026-05-01") "en-US")))
  (is (nil? (model/month-label nil "en-US"))))

(deftest month-options-test
  (let [opts (model/month-options "en-US")]
    (is (= 12 (count opts)))
    (is (= {:index 0 :label "January"} (first opts)))
    (is (= 11 (:index (last opts))))))

(deftest compute-cell-flags-test
  (testing "selected single value"
    (let [f (model/compute-cell-flags {:mode :single :value "2026-05-10"}
                                      "2026-05-10" true "2026-05-21")]
      (is (true? (:selected? f)))
      (is (false? (:disabled? f)))))
  (testing "today detection"
    (let [f (model/compute-cell-flags {} "2026-05-21" true "2026-05-21")]
      (is (true? (:today? f)))))
  (testing "out-of-range below min is disabled"
    (let [f (model/compute-cell-flags {:min "2026-05-05"}
                                      "2026-05-01" true "2026-05-21")]
      (is (true? (:disabled? f)))))
  (testing "explicitly disabled date"
    (let [f (model/compute-cell-flags {:disabled-set #{"2026-05-15"}}
                                      "2026-05-15" true "2026-05-21")]
      (is (true? (:disabled? f)))))
  (testing "range interior and edges"
    (let [m {:mode :range :start "2026-05-05" :end "2026-05-10"}
          interior (model/compute-cell-flags m "2026-05-07" true "2026-05-21")
          edge     (model/compute-cell-flags m "2026-05-05" true "2026-05-21")]
      (is (true? (:in-range? interior)))
      (is (false? (:range-edge? interior)))
      (is (true? (:range-edge? edge)))
      (is (true? (:selected? edge)))))
  (testing "sparse map does not throw and yields all-false flags"
    (let [f (model/compute-cell-flags {} "2026-05-10" true "2026-05-21")]
      (is (false? (:selected? f)))
      (is (false? (:disabled? f)))
      (is (false? (:in-range? f)))
      (is (false? (:range-edge? f))))))
