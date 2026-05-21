(ns baredom.components.x-split-pane.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-split-pane.model :as model]))

(deftest parse-orientation-test
  (testing "defaults to horizontal"
    (is (= "horizontal" (model/parse-orientation nil)))
    (is (= "horizontal" (model/parse-orientation "bogus")))
    (is (= "horizontal" (model/parse-orientation 42))))
  (testing "valid values, case-insensitive and trimmed"
    (is (= "horizontal" (model/parse-orientation "horizontal")))
    (is (= "vertical"   (model/parse-orientation "vertical")))
    (is (= "vertical"   (model/parse-orientation " VERTICAL ")))))

(deftest clamp-position-test
  (is (= 0   (model/clamp-position -20)))
  (is (= 50  (model/clamp-position 50)))
  (is (= 100 (model/clamp-position 140))))

(deftest parse-position-test
  (testing "defaults to 50 for nil / non-numeric input"
    (is (= 50 (model/parse-position nil)))
    (is (= 50 (model/parse-position "abc"))))
  (testing "parses and clamps to [0,100]"
    (is (= 30  (model/parse-position "30")))
    (is (= 100 (model/parse-position "150")))
    (is (= 0   (model/parse-position "-10")))))

(deftest parse-min-test
  (is (= 0   (model/parse-min nil)))
  (is (= 0   (model/parse-min "abc")))
  (is (= 240 (model/parse-min "240")))
  (testing "negative pixel values floor at zero"
    (is (= 0 (model/parse-min "-5")))))

(deftest pointer->percent-test
  (testing "maps a client coordinate to a percentage of the container"
    (is (= 0   (model/pointer->percent 100 100 400)))
    (is (= 50  (model/pointer->percent 300 100 400)))
    (is (= 100 (model/pointer->percent 500 100 400))))
  (testing "clamps out-of-range coordinates"
    (is (= 0   (model/pointer->percent 0   100 400)))
    (is (= 100 (model/pointer->percent 999 100 400))))
  (testing "a zero-size container falls back to the default position"
    (is (= 50 (model/pointer->percent 300 100 0)))))

(deftest clamp-by-mins-test
  (testing "no minimums — the position passes through"
    (is (= 50 (model/clamp-by-mins 50 1000 0 0))))
  (testing "min-start raises the floor"
    (is (= 20 (model/clamp-by-mins 10 1000 200 0))))
  (testing "min-end lowers the ceiling"
    (is (= 70 (model/clamp-by-mins 90 1000 0 300))))
  (testing "in-range positions are untouched"
    (is (= 50 (model/clamp-by-mins 50 1000 200 300))))
  (testing "overlapping minimums resolve to the midpoint"
    (is (= 50 (model/clamp-by-mins 80 1000 600 600))))
  (testing "a minimum larger than the container still yields a 0-100 result"
    (is (= 100 (model/clamp-by-mins 80 320 350 0)))
    (is (= 0   (model/clamp-by-mins 20 320 0 350))))
  (testing "a zero-size container clamps to [0,100] only"
    (is (= 100 (model/clamp-by-mins 150 0 200 0)))))

(deftest keyboard-delta-test
  (testing "horizontal split uses left/right arrows"
    (is (= 1  (model/keyboard-delta "ArrowRight" "horizontal")))
    (is (= -1 (model/keyboard-delta "ArrowLeft"  "horizontal")))
    (is (nil? (model/keyboard-delta "ArrowUp"    "horizontal"))))
  (testing "vertical split uses up/down arrows"
    (is (= 1  (model/keyboard-delta "ArrowDown" "vertical")))
    (is (= -1 (model/keyboard-delta "ArrowUp"   "vertical")))
    (is (nil? (model/keyboard-delta "ArrowLeft" "vertical"))))
  (testing "page keys and Home/End"
    (is (= 10  (model/keyboard-delta "PageUp"   "horizontal")))
    (is (= -10 (model/keyboard-delta "PageDown" "horizontal")))
    (is (= :to-min (model/keyboard-delta "Home" "horizontal")))
    (is (= :to-max (model/keyboard-delta "End"  "horizontal"))))
  (testing "irrelevant keys yield nil"
    (is (nil? (model/keyboard-delta "Enter" "horizontal")))))

(deftest next-position-test
  (is (= 51  (model/next-position 50 1)))
  (is (= 49  (model/next-position 50 -1)))
  (is (= 0   (model/next-position 50 :to-min)))
  (is (= 100 (model/next-position 50 :to-max)))
  (testing "the result is clamped to [0,100]"
    (is (= 100 (model/next-position 95 10)))
    (is (= 0   (model/next-position 5 -10)))))

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "horizontal"    (:orientation m)))
    (is (= 50              (:position m)))
    (is (= 0               (:min-start m)))
    (is (= 0               (:min-end m)))
    (is (= false           (:disabled? m)))
    (is (= "Resize panels" (:divider-label m)))))

(deftest normalize-full-test
  (let [m (model/normalize {:orientation-raw   "vertical"
                            :position-raw      "30"
                            :min-start-raw     "100"
                            :min-end-raw       "200"
                            :disabled?         true
                            :divider-label-raw "Drag me"})]
    (is (= "vertical" (:orientation m)))
    (is (= 30         (:position m)))
    (is (= 100        (:min-start m)))
    (is (= 200        (:min-end m)))
    (is (= true       (:disabled? m)))
    (is (= "Drag me"  (:divider-label m)))))

(deftest normalize-disabled-coercion-test
  (testing ":disabled? is always a strict boolean (tests pass sparse maps)"
    (is (false? (:disabled? (model/normalize {}))))
    (is (false? (:disabled? (model/normalize {:disabled? nil}))))
    (is (true?  (:disabled? (model/normalize {:disabled? true}))))))

(deftest normalize-divider-label-test
  (testing "blank labels fall back to the default"
    (is (= "Resize panels" (:divider-label (model/normalize {:divider-label-raw ""}))))
    (is (= "Resize panels" (:divider-label (model/normalize {:divider-label-raw "   "})))))
  (testing "a non-blank label is kept verbatim"
    (is (= "Resize columns"
           (:divider-label (model/normalize {:divider-label-raw "Resize columns"}))))))
