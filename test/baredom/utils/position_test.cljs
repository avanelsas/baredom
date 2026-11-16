(ns baredom.utils.position-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.position :as position]))

(deftest placements-flip-in-pairs-test
  (testing "each placement flips to a placement that flips back on the same axis"
    (doseq [[p {:keys [opposite axis]}] position/placements]
      (is (= p (get-in position/placements [opposite :opposite])) p)
      (is (= axis (get-in position/placements [opposite :axis])) p))))

(deftest compute-bottom-start-test
  (testing "bottom-start places the panel below the anchor"
    (let [pos (position/compute
               "bottom-start" 4
               {:x 100 :y 50 :width 100 :height 40}
               {:width 200 :height 150}
               {:width 1024 :height 768} 8)]
      (is (= "bottom-start" (:final-placement pos)))
      (is (>= (:x pos) 8))
      (is (= 94 (:y pos)))
      (is (number? (:max-height pos)))
      (is (not (contains? pos :fits))))))

(deftest compute-flips-above-test
  (testing "bottom-start flips to top-start when the panel overflows the bottom"
    (let [pos (position/compute
               "bottom-start" 4
               {:x 100 :y 700 :width 100 :height 40}
               {:width 200 :height 150}
               {:width 1024 :height 768} 8)]
      (is (= "top-start" (:final-placement pos))))))

(deftest compute-clamps-to-viewport-test
  (testing "x is clamped so the panel ends inside the viewport"
    (let [pos (position/compute
               "bottom-start" 8
               {:x 1400 :y 100 :width 10 :height 10}
               {:width 200 :height 100}
               {:width 1440 :height 900} 8)]
      (is (= (- 1440 200 8) (:x pos))))))

(deftest compute-max-height-test
  (testing "max-height is never below min-max-height"
    (let [pos (position/compute
               "bottom-start" 8
               {:x 100 :y 100 :width 120 :height 40}
               {:width 160 :height 200}
               {:width 1440 :height 900} 8)]
      (is (>= (:max-height pos) position/min-max-height)))))

(deftest compute-flips-vertically-on-vertical-fit-only-test
  (testing "bottom-start near the bottom-right flips above when the panel overflows horizontally"
    (let [pos (position/compute
               "bottom-start" 4
               {:x 1287 :y 780 :width 24 :height 20}
               {:width 298 :height 127}
               {:width 1440 :height 900} 8)]
      (is (= "top-start" (:final-placement pos)))
      (is (= 649 (:y pos)))
      (is (= 1134 (:x pos)))))
  (testing "top-end near the top-left flips below when the panel overflows horizontally"
    (let [pos (position/compute
               "top-end" 4
               {:x 4 :y 20 :width 24 :height 20}
               {:width 298 :height 127}
               {:width 1440 :height 900} 8)]
      (is (= "bottom-end" (:final-placement pos)))
      (is (= 44 (:y pos)))
      (is (= 8 (:x pos)))))
  (testing "a menu at the pointer near the bottom-right flips above and leaves the pointer clear"
    (let [pos (position/compute
               "bottom-start" 8
               {:x 1400 :y 850 :width 1 :height 1}
               {:width 200 :height 240}
               {:width 1440 :height 900} 8)]
      (is (= "top-start" (:final-placement pos)))
      (is (< (+ (:y pos) 240) 850)))))

(deftest compute-flips-horizontally-on-horizontal-fit-only-test
  (testing "right-start near the bottom-right flips left and leaves the anchor clear"
    (let [pos (position/compute
               "right-start" 8
               {:x 1380 :y 800 :width 40 :height 20}
               {:width 200 :height 240}
               {:width 1440 :height 900} 8)]
      (is (= "left-start" (:final-placement pos)))
      (is (< (+ (:x pos) 200) 1380)))))
