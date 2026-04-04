(ns baredom.components.x-scroll.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-scroll.model :as model]))

;; ── parse-mode ──────────────────────────────────────────────────────────────
(deftest parse-mode-known-values-test
  (is (= "horizontal" (model/parse-mode "horizontal")))
  (is (= "vertical"   (model/parse-mode "vertical"))))

(deftest parse-mode-case-insensitive-test
  (is (= "horizontal" (model/parse-mode "HORIZONTAL")))
  (is (= "vertical"   (model/parse-mode "Vertical"))))

(deftest parse-mode-unknown-falls-back-test
  (is (= "horizontal" (model/parse-mode nil)))
  (is (= "horizontal" (model/parse-mode "")))
  (is (= "horizontal" (model/parse-mode "diagonal"))))

;; ── parse-snap ──────────────────────────────────────────────────────────────
(deftest parse-snap-known-values-test
  (is (= "none"   (model/parse-snap "none")))
  (is (= "start"  (model/parse-snap "start")))
  (is (= "center" (model/parse-snap "center")))
  (is (= "end"    (model/parse-snap "end"))))

(deftest parse-snap-case-insensitive-test
  (is (= "center" (model/parse-snap "CENTER")))
  (is (= "start"  (model/parse-snap "Start"))))

(deftest parse-snap-unknown-falls-back-test
  (is (= "none" (model/parse-snap nil)))
  (is (= "none" (model/parse-snap "")))
  (is (= "none" (model/parse-snap "middle"))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (testing "absent (nil) -> false"
    (is (false? (model/parse-bool-attr nil))))
  (testing "present (empty string) -> true"
    (is (true? (model/parse-bool-attr ""))))
  (testing "present (any value) -> true"
    (is (true? (model/parse-bool-attr "true")))
    (is (true? (model/parse-bool-attr "false")))
    (is (true? (model/parse-bool-attr "anything")))))

;; ── parse-bool-default-true ─────────────────────────────────────────────────
(deftest parse-bool-default-true-test
  (testing "absent (nil) -> true"
    (is (true? (model/parse-bool-default-true nil))))
  (testing "empty string -> true"
    (is (true? (model/parse-bool-default-true ""))))
  (testing "\"true\" -> true"
    (is (true? (model/parse-bool-default-true "true"))))
  (testing "\"false\" -> false"
    (is (false? (model/parse-bool-default-true "false"))))
  (testing "\"FALSE\" -> false (case-insensitive)"
    (is (false? (model/parse-bool-default-true "FALSE"))))
  (testing "other values -> true"
    (is (true? (model/parse-bool-default-true "yes")))))

;; ── parse-non-neg-int ───────────────────────────────────────────────────────
(deftest parse-non-neg-int-valid-test
  (is (= 42  (model/parse-non-neg-int "42" 0)))
  (is (= 0   (model/parse-non-neg-int "0" 99))))

(deftest parse-non-neg-int-invalid-test
  (is (= 99 (model/parse-non-neg-int nil 99)))
  (is (= 99 (model/parse-non-neg-int "" 99)))
  (is (= 99 (model/parse-non-neg-int "abc" 99)))
  (is (= 99 (model/parse-non-neg-int "-5" 99))))

;; ── parse-interval ──────────────────────────────────────────────────────────
(deftest parse-interval-valid-test
  (is (= 3000 (model/parse-interval "3000")))
  (is (= 5000 (model/parse-interval nil))))

(deftest parse-interval-clamps-minimum-test
  (is (= 500 (model/parse-interval "100")))
  (is (= 500 (model/parse-interval "0"))))

;; ── parse-active-index ──────────────────────────────────────────────────────
(deftest parse-active-index-test
  (is (= 3 (model/parse-active-index "3")))
  (is (= 0 (model/parse-active-index nil)))
  (is (= 0 (model/parse-active-index "abc"))))

;; ── parse-gap ───────────────────────────────────────────────────────────────
(deftest parse-gap-test
  (is (= 16 (model/parse-gap "16")))
  (is (= 0  (model/parse-gap nil)))
  (is (= 0  (model/parse-gap "-5"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "horizontal" (:mode m)))
    (is (= "none"       (:snap m)))
    (is (false?         (:loop? m)))
    (is (false?         (:auto-play? m)))
    (is (= 5000         (:interval m)))
    (is (true?          (:show-controls? m)))
    (is (false?         (:show-indicators? m)))
    (is (= 0            (:active-index m)))
    (is (= 0            (:gap m)))
    (is (false?         (:disabled? m)))
    (is (= ""           (:label m)))))

(deftest normalize-propagates-values-test
  (let [m (model/normalize {:mode-raw "vertical"
                            :snap-raw "center"
                            :loop-attr ""
                            :auto-play-attr ""
                            :interval-raw "3000"
                            :show-controls-attr "false"
                            :show-indicators-attr ""
                            :active-index-raw "2"
                            :gap-raw "16"
                            :disabled-attr ""
                            :label-raw "My carousel"})]
    (is (= "vertical" (:mode m)))
    (is (= "center"   (:snap m)))
    (is (true?        (:loop? m)))
    (is (true?        (:auto-play? m)))
    (is (= 3000       (:interval m)))
    (is (false?       (:show-controls? m)))
    (is (true?        (:show-indicators? m)))
    (is (= 2          (:active-index m)))
    (is (= 16         (:gap m)))
    (is (true?        (:disabled? m)))
    (is (= "My carousel" (:label m)))))

;; ── loop-eligible? ──────────────────────────────────────────────────────────
(deftest loop-eligible-test
  (is (false? (model/loop-eligible? 0)))
  (is (false? (model/loop-eligible? 1)))
  (is (false? (model/loop-eligible? 2)))
  (is (true?  (model/loop-eligible? 3)))
  (is (true?  (model/loop-eligible? 10))))

;; ── effective-loop? ─────────────────────────────────────────────────────────
(deftest effective-loop-test
  (is (false? (model/effective-loop? false 5)))
  (is (false? (model/effective-loop? true 2)))
  (is (true?  (model/effective-loop? true 3)))
  (is (true?  (model/effective-loop? true 10))))

;; ── wrap-index ──────────────────────────────────────────────────────────────
(deftest wrap-index-normal-test
  (is (= 0 (model/wrap-index 0 5)))
  (is (= 3 (model/wrap-index 3 5)))
  (is (= 4 (model/wrap-index 4 5))))

(deftest wrap-index-overflow-test
  (is (= 0 (model/wrap-index 5 5)))
  (is (= 1 (model/wrap-index 6 5))))

(deftest wrap-index-negative-test
  (is (= 4 (model/wrap-index -1 5)))
  (is (= 3 (model/wrap-index -2 5))))

(deftest wrap-index-zero-count-test
  (is (= 0 (model/wrap-index 0 0)))
  (is (= 0 (model/wrap-index 3 0))))

;; ── effective-offset ────────────────────────────────────────────────────────
(deftest effective-offset-active-zero-test
  ;; 5 children, active=0: offsets should be [0, 1, 2, -2, -1]
  (is (= 0  (model/effective-offset 0 0 5)))
  (is (= 1  (model/effective-offset 1 0 5)))
  (is (= 2  (model/effective-offset 2 0 5)))
  (is (= -2 (model/effective-offset 3 0 5)))
  (is (= -1 (model/effective-offset 4 0 5))))

(deftest effective-offset-active-three-test
  ;; 5 children, active=3: offsets should be [-3+5=2, -2+0=-2, -1, 0, 1]
  ;; slide 0 -> (0-3)=-3, wrap: (-3+2+5)%5 - 2 = 4%5 - 2 = 2
  ;; slide 1 -> (1-3)=-2, wrap: (-2+2+5)%5 - 2 = 5%5 - 2 = -2
  ;; slide 2 -> (2-3)=-1, wrap: (-1+2+5)%5 - 2 = 6%5 - 2 = -1
  ;; slide 3 -> (3-3)=0, wrap: (0+2+5)%5 - 2 = 2%5 - 2 = 0
  ;; slide 4 -> (4-3)=1, wrap: (1+2+5)%5 - 2 = 3%5 - 2 = 1
  (is (= 2  (model/effective-offset 0 3 5)))
  (is (= -2 (model/effective-offset 1 3 5)))
  (is (= -1 (model/effective-offset 2 3 5)))
  (is (= 0  (model/effective-offset 3 3 5)))
  (is (= 1  (model/effective-offset 4 3 5))))

(deftest effective-offset-even-count-test
  ;; 4 children, active=0: offsets [0, 1, -2, -1]
  (is (= 0  (model/effective-offset 0 0 4)))
  (is (= 1  (model/effective-offset 1 0 4)))
  (is (= -2 (model/effective-offset 2 0 4)))
  (is (= -1 (model/effective-offset 3 0 4))))

;; ── slide-translate ─────────────────────────────────────────────────────────
(deftest slide-translate-test
  (is (= 0   (model/slide-translate 0 300 16)))
  (is (= 316 (model/slide-translate 1 300 16)))
  (is (= -316 (model/slide-translate -1 300 16))))

;; ── clamp-index ─────────────────────────────────────────────────────────────
(deftest clamp-index-test
  (is (= 0 (model/clamp-index -1 5)))
  (is (= 0 (model/clamp-index 0 5)))
  (is (= 3 (model/clamp-index 3 5)))
  (is (= 4 (model/clamp-index 4 5)))
  (is (= 4 (model/clamp-index 10 5)))
  (is (= 0 (model/clamp-index 5 0))))

;; ── resolve-target-index ────────────────────────────────────────────────────
(deftest resolve-target-index-non-loop-test
  (is (= 1 (model/resolve-target-index 0 1 5 false)))
  (is (= 4 (model/resolve-target-index 4 1 5 false)))
  (is (= 0 (model/resolve-target-index 0 -1 5 false))))

(deftest resolve-target-index-loop-test
  (is (= 0 (model/resolve-target-index 4 1 5 true)))
  (is (= 4 (model/resolve-target-index 0 -1 5 true))))

(deftest resolve-target-index-loop-too-few-children-test
  ;; With 2 children, loop degrades to clamp
  (is (= 1 (model/resolve-target-index 1 1 2 true)))
  (is (= 0 (model/resolve-target-index 0 -1 2 true))))

;; ── can-go-prev? / can-go-next? ─────────────────────────────────────────────
(deftest can-go-prev-test
  (is (false? (model/can-go-prev? {:active-index 0 :loop? false} 5)))
  (is (true?  (model/can-go-prev? {:active-index 2 :loop? false} 5)))
  (is (true?  (model/can-go-prev? {:active-index 0 :loop? true} 5))))

(deftest can-go-next-test
  (is (true?  (model/can-go-next? {:active-index 0 :loop? false} 5)))
  (is (false? (model/can-go-next? {:active-index 4 :loop? false} 5)))
  (is (true?  (model/can-go-next? {:active-index 4 :loop? true} 5))))

;; ── snap-to-index ───────────────────────────────────────────────────────────
(deftest snap-to-index-test
  (is (= 0 (model/snap-to-index 0 300 16 5)))
  (is (= 1 (model/snap-to-index -316 300 16 5)))
  (is (= 2 (model/snap-to-index -632 300 16 5)))
  ;; Clamps to valid range
  (is (= 0 (model/snap-to-index 500 300 16 5)))
  (is (= 4 (model/snap-to-index -2000 300 16 5))))

(deftest snap-to-index-edge-cases-test
  (is (= 0 (model/snap-to-index 0 300 16 0)))
  (is (= 0 (model/snap-to-index 0 0 16 5))))

;; ── crosses-boundary? ───────────────────────────────────────────────────────
(deftest crosses-boundary-test
  ;; Forward wrap: 4 -> 0
  (is (true?  (model/crosses-boundary? 4 0 1 5)))
  ;; Backward wrap: 0 -> 4
  (is (true?  (model/crosses-boundary? 0 4 -1 5)))
  ;; Normal forward: 1 -> 2
  (is (false? (model/crosses-boundary? 1 2 1 5)))
  ;; Normal backward: 2 -> 1
  (is (false? (model/crosses-boundary? 2 1 -1 5))))

;; ── direction-for-delta ─────────────────────────────────────────────────────
(deftest direction-for-delta-test
  (is (= "forward"  (model/direction-for-delta 1)))
  (is (= "backward" (model/direction-for-delta -1))))

;; ── Event detail builders ───────────────────────────────────────────────────
(deftest change-detail-test
  (let [d (model/change-detail 3 2)]
    (is (= 3 (:activeIndex d)))
    (is (= 2 (:previousIndex d)))))

(deftest start-detail-test
  (let [d (model/start-detail "forward" 2)]
    (is (= "forward" (:direction d)))
    (is (= 2 (:activeIndex d)))))

(deftest end-detail-test
  (let [d (model/end-detail 3)]
    (is (= 3 (:activeIndex d)))))

(deftest loop-detail-test
  (let [d (model/loop-detail "backward")]
    (is (= "backward" (:direction d)))))
