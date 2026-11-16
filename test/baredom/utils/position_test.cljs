(ns baredom.utils.position-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.position :as position]))

(deftest placements-flip-in-pairs-test
  (testing "each placement flips to a placement that flips back on the same axis"
    (doseq [[p {:keys [opposite axis]}] position/placements]
      (is (= p (get-in position/placements [opposite :opposite])) p)
      (is (= axis (get-in position/placements [opposite :axis])) p))))

(deftest placements-flip-past-the-anchor-test
  (testing "each placement puts the panel past the anchor on the axis its flip moves"
    (doseq [[p {:keys [axis] :as rules}] position/placements]
      (is (contains? #{:after :before} (rules axis)) p))))

(deftest compute-bottom-start-test
  (testing "bottom-start places the panel below the anchor and returns only the position"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 50 :width 100 :height 40}
                                 :panel     {:width 200 :height 150}
                                 :viewport  {:width 1024 :height 768} :margin 8})]
      (is (= "bottom-start" (:final-placement pos)))
      (is (= 100 (:x pos)))
      (is (= 94 (:y pos)))
      (is (= #{:x :y :final-placement :max-height} (set (keys pos)))))))

(deftest compute-unknown-placement-test
  (testing "an unknown placement falls back to bottom-start"
    (let [pos (position/compute {:placement "middle" :offset 4
                                 :anchor    {:x 100 :y 50 :width 100 :height 40}
                                 :panel     {:width 200 :height 150}
                                 :viewport  {:width 1024 :height 768} :margin 8})]
      (is (= "bottom-start" (:final-placement pos))))))

(deftest compute-flips-above-test
  (testing "bottom-start flips to top-start when the panel overflows the bottom"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 700 :width 100 :height 40}
                                 :panel     {:width 200 :height 150}
                                 :viewport  {:width 1024 :height 768} :margin 8})]
      (is (= "top-start" (:final-placement pos))))))

(deftest compute-clamps-to-viewport-test
  (testing "x is clamped so the panel ends inside the viewport"
    (let [pos (position/compute {:placement "bottom-start" :offset 8
                                 :anchor    {:x 1400 :y 100 :width 10 :height 10}
                                 :panel     {:width 200 :height 100}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= (- 1440 200 8) (:x pos))))))

(deftest compute-max-height-test
  (testing "max-height is never below max-height-floor"
    (let [pos (position/compute {:placement "bottom-start" :offset 8
                                 :anchor    {:x 100 :y 100 :width 120 :height 40}
                                 :panel     {:width 160 :height 200}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (>= (:max-height pos) position/max-height-floor)))))

(deftest compute-flips-vertically-on-vertical-fit-only-test
  (testing "bottom-start near the bottom-right flips above when the panel overflows horizontally"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 1287 :y 780 :width 24 :height 20}
                                 :panel     {:width 298 :height 127}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "top-start" (:final-placement pos)))
      (is (= 649 (:y pos)))
      (is (= 1134 (:x pos)))))
  (testing "top-end near the top-left flips below when the panel overflows horizontally"
    (let [pos (position/compute {:placement "top-end" :offset 4
                                 :anchor    {:x 4 :y 20 :width 24 :height 20}
                                 :panel     {:width 298 :height 127}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "bottom-end" (:final-placement pos)))
      (is (= 44 (:y pos)))
      (is (= 8 (:x pos)))))
  (testing "a menu at the pointer near the bottom-right flips above and leaves the pointer clear"
    (let [pos (position/compute {:placement "bottom-start" :offset 8
                                 :anchor    {:x 1400 :y 850 :width 1 :height 1}
                                 :panel     {:width 200 :height 240}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "top-start" (:final-placement pos)))
      (is (< (+ (:y pos) 240) 850))
      (is (= 842 (+ (:y pos) (:max-height pos))) "a taller panel ends at the offset above the pointer"))))

(deftest compute-flips-horizontally-on-horizontal-fit-only-test
  (testing "right-start near the bottom-right flips left and leaves the anchor clear"
    (let [pos (position/compute {:placement "right-start" :offset 8
                                 :anchor    {:x 1380 :y 800 :width 40 :height 20}
                                 :panel     {:width 200 :height 240}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "left-start" (:final-placement pos)))
      (is (< (+ (:x pos) 200) 1380)))))

(deftest compute-keeps-the-anchor-clear-when-neither-side-fits-test
  (testing "bottom-start stays below the anchor and gets the room below as max-height"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 400 :width 100 :height 20}
                                 :panel     {:width 200 :height 600}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "bottom-start" (:final-placement pos)))
      (is (= 424 (:y pos)))
      (is (= 468 (:max-height pos)))))
  (testing "top-start stays above the anchor and gets the room above as max-height"
    (let [pos (position/compute {:placement "top-start" :offset 4
                                 :anchor    {:x 100 :y 500 :width 100 :height 20}
                                 :panel     {:width 200 :height 600}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "top-start" (:final-placement pos)))
      (is (= 8 (:y pos)))
      (is (= 488 (:max-height pos)))))
  (testing "bottom-start flips above when the room above is larger"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 800 :width 100 :height 20}
                                 :panel     {:width 200 :height 1000}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "top-start" (:final-placement pos)))
      (is (= 8 (:y pos)))
      (is (= 788 (:max-height pos)))))
  (testing "right-start keeps its side when neither side fits and the rooms are equal"
    (let [pos (position/compute {:placement "right-start" :offset 8
                                 :anchor    {:x 700 :y 100 :width 40 :height 20}
                                 :panel     {:width 900 :height 100}
                                 :viewport  {:width 1440 :height 900} :margin 8})]
      (is (= "right-start" (:final-placement pos))))))

(deftest compute-caps-max-height-at-the-panel-max-height-test
  (testing "max-height is capped at the panel's own max-height"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 50 :width 100 :height 40}
                                 :panel     {:width 200 :height 150 :max-height 384}
                                 :viewport  {:width 1024 :height 768} :margin 8})]
      (is (= 384 (:max-height pos)))))
  (testing "max-height is the room on the side when the panel sets no max-height"
    (let [pos (position/compute {:placement "bottom-start" :offset 4
                                 :anchor    {:x 100 :y 50 :width 100 :height 40}
                                 :panel     {:width 200 :height 150}
                                 :viewport  {:width 1024 :height 768} :margin 8})]
      (is (= 666 (:max-height pos))))))
