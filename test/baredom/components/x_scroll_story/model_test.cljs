(ns baredom.components.x-scroll-story.model-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [baredom.components.x-scroll-story.model :as model]
   [baredom.utils.model :as mu]))

;; ── parse-layout ────────────────────────────────────────────────────────────

(deftest parse-layout-test
  (testing "known values"
    (is (= "left"  (model/parse-layout "left")))
    (is (= "right" (model/parse-layout "right")))
    (is (= "top"   (model/parse-layout "top"))))

  (testing "case insensitivity"
    (is (= "left"  (model/parse-layout "LEFT")))
    (is (= "right" (model/parse-layout "Right")))
    (is (= "top"   (model/parse-layout "TOP"))))

  (testing "unknown / nil fallback"
    (is (= "left" (model/parse-layout nil)))
    (is (= "left" (model/parse-layout "")))
    (is (= "left" (model/parse-layout "bottom")))
    (is (= "left" (model/parse-layout "center")))))

;; ── parse-threshold ─────────────────────────────────────────────────────────

(deftest parse-threshold-test
  (testing "valid floats"
    (is (= 0.5 (model/parse-threshold "0.5")))
    (is (= 0.0 (model/parse-threshold "0")))
    (is (= 1.0 (model/parse-threshold "1")))
    (is (= 0.3 (model/parse-threshold "0.3"))))

  (testing "clamping"
    (is (= 0.0 (model/parse-threshold "-0.5")))
    (is (= 1.0 (model/parse-threshold "1.5")))
    (is (= 1.0 (model/parse-threshold "99"))))

  (testing "defaults"
    (is (= 0.5 (model/parse-threshold nil)))
    (is (= 0.5 (model/parse-threshold "abc")))
    (is (= 0.5 (model/parse-threshold "")))))

;; ── parse-split ─────────────────────────────────────────────────────────────

(deftest parse-split-test
  (testing "valid floats"
    (is (= 0.5 (model/parse-split "0.5")))
    (is (= 0.3 (model/parse-split "0.3")))
    (is (= 0.7 (model/parse-split "0.7"))))

  (testing "clamping"
    (is (= 0.1 (model/parse-split "0.0")))
    (is (= 0.1 (model/parse-split "-1")))
    (is (= 0.9 (model/parse-split "0.95")))
    (is (= 0.9 (model/parse-split "1"))))

  (testing "defaults"
    (is (= 0.5 (model/parse-split nil)))
    (is (= 0.5 (model/parse-split "abc")))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────

(deftest parse-bool-attr-test
  (is (false? (mu/parse-bool-present nil)))
  (is (true?  (mu/parse-bool-present "")))
  (is (true?  (mu/parse-bool-present "true")))
  (is (true?  (mu/parse-bool-present "anything"))))

;; ── parse-autoplay-speed ─────────────────────────────────────────────────────

(deftest parse-autoplay-speed-test
  (testing "valid numbers"
    (is (= 50  (model/parse-autoplay-speed "50")))
    (is (= 100 (model/parse-autoplay-speed "100")))
    (is (= 1   (model/parse-autoplay-speed "1"))))

  (testing "clamping"
    (is (= 1    (model/parse-autoplay-speed "0")))
    (is (= 1    (model/parse-autoplay-speed "-10")))
    (is (= 1000 (model/parse-autoplay-speed "2000")))
    (is (= 1000 (model/parse-autoplay-speed "9999"))))

  (testing "defaults"
    (is (= 50 (model/parse-autoplay-speed nil)))
    (is (= 50 (model/parse-autoplay-speed "abc")))
    (is (= 50 (model/parse-autoplay-speed "")))))

;; ── normalize ───────────────────────────────────────────────────────────────

(deftest normalize-test
  (testing "defaults"
    (let [m (model/normalize {})]
      (is (= "left" (:layout m)))
      (is (= 0.5    (:threshold m)))
      (is (= 0.5    (:split m)))
      (is (false?   (:disabled? m)))
      (is (= ""     (:label m)))
      (is (false?   (:autoplay? m)))
      (is (= 50     (:autoplay-speed m)))
      (is (false?   (:autoplay-loop? m)))
      (is (false?   (:autoplay-indicator? m)))))

  (testing "explicit values"
    (let [m (model/normalize
             {:layout-raw    "right"
              :threshold-raw "0.7"
              :split-raw     "0.4"
              :disabled-attr ""
              :label-raw     "My story"})]
      (is (= "right" (:layout m)))
      (is (= 0.7     (:threshold m)))
      (is (= 0.4     (:split m)))
      (is (true?     (:disabled? m)))
      (is (= "My story" (:label m)))))

  (testing "autoplay explicit values"
    (let [m (model/normalize
             {:autoplay-attr           ""
              :autoplay-speed-raw      "75"
              :autoplay-loop-attr      ""
              :autoplay-indicator-attr ""})]
      (is (true?  (:autoplay? m)))
      (is (= 75   (:autoplay-speed m)))
      (is (true?  (:autoplay-loop? m)))
      (is (true?  (:autoplay-indicator? m))))))

;; ── compute-overall-progress ────────────────────────────────────────────────

(deftest compute-overall-progress-test
  (testing "element just entering at bottom of viewport"
    ;; container-top = viewport-height, so scrolled = 0
    (is (= 0.0 (model/compute-overall-progress 1000 500 1000))))

  (testing "element centered"
    ;; mid-way through
    (let [p (model/compute-overall-progress 250 500 1000)]
      (is (> p 0.4))
      (is (< p 0.6))))

  (testing "element fully past top"
    ;; container-top = -500 (past top), height = 500, viewport = 1000
    (is (= 1.0 (model/compute-overall-progress -500 500 1000))))

  (testing "clamping"
    (is (= 0.0 (model/compute-overall-progress 2000 500 1000)))
    (is (= 1.0 (model/compute-overall-progress -2000 500 1000)))))

;; ── find-active-step ────────────────────────────────────────────────────────

(deftest find-active-step-test
  (testing "no steps"
    (is (= -1 (model/find-active-step [] 500))))

  (testing "single step spanning trigger"
    (is (= 0 (model/find-active-step [{:top 300 :bottom 700}] 500))))

  (testing "single step below trigger"
    (is (= -1 (model/find-active-step [{:top 600 :bottom 900}] 500))))

  (testing "single step above trigger"
    (is (= 0 (model/find-active-step [{:top 100 :bottom 400}] 500))))

  (testing "multiple steps, second spans trigger"
    (is (= 1 (model/find-active-step
              [{:top 0 :bottom 300}
               {:top 300 :bottom 600}
               {:top 600 :bottom 900}]
              400))))

  (testing "no step spans trigger, last above wins"
    (is (= 1 (model/find-active-step
              [{:top 0 :bottom 200}
               {:top 200 :bottom 400}
               {:top 600 :bottom 900}]
              500)))))

;; ── compute-step-progress ───────────────────────────────────────────────────

(deftest compute-step-progress-test
  (testing "trigger at step top"
    (is (= 0.0 (model/compute-step-progress 500 300 500))))

  (testing "trigger at step bottom"
    (is (= 1.0 (model/compute-step-progress 200 300 500))))

  (testing "trigger at mid-point"
    (is (= 0.5 (model/compute-step-progress 350 300 500))))

  (testing "clamping"
    (is (= 0.0 (model/compute-step-progress 600 300 500)))
    (is (= 1.0 (model/compute-step-progress 0 300 500))))

  (testing "zero height"
    (is (= 0.0 (model/compute-step-progress 500 0 500)))))

;; ── Event detail builders ───────────────────────────────────────────────────

(deftest step-change-detail-test
  (let [d (model/step-change-detail 2 "intro" 1 "hero")]
    (is (= 2       (:index d)))
    (is (= "intro" (:id d)))
    (is (= 1       (:previousIndex d)))
    (is (= "hero"  (:previousId d)))))

(deftest progress-detail-test
  (let [d (model/progress-detail 0.6 1 "step-2")]
    (is (= 0.6      (:progress d)))
    (is (= 1        (:activeIndex d)))
    (is (= "step-2" (:activeId d)))))

(deftest step-enter-detail-test
  (let [d (model/step-enter-detail 1 "hero" 0.4)]
    (is (= 1      (:index d)))
    (is (= "hero" (:id d)))
    (is (= 0.4    (:progress d)))))

(deftest step-leave-detail-test
  (let [d (model/step-leave-detail 0 "intro" 0.9)]
    (is (= 0       (:index d)))
    (is (= "intro" (:id d)))
    (is (= 0.9     (:progress d)))))

(deftest enter-detail-test
  (let [d (model/enter-detail 0.1)]
    (is (= 0.1 (:progress d)))))

(deftest leave-detail-test
  (let [d (model/leave-detail 0.8)]
    (is (= 0.8 (:progress d)))))

(deftest autoplay-pause-detail-test
  (let [d (model/autoplay-pause-detail 0.5 2 "step-3")]
    (is (= 0.5      (:progress d)))
    (is (= 2        (:activeIndex d)))
    (is (= "step-3" (:activeId d))))
  (testing "nil active-id"
    (let [d (model/autoplay-pause-detail 0.5 -1 nil)]
      (is (nil? (:activeId d))))))

(deftest autoplay-resume-detail-test
  (let [d (model/autoplay-resume-detail 0.6 3 "step-4")]
    (is (= 0.6      (:progress d)))
    (is (= 3        (:activeIndex d)))
    (is (= "step-4" (:activeId d))))
  (testing "nil active-id"
    (let [d (model/autoplay-resume-detail 0.6 -1 nil)]
      (is (nil? (:activeId d))))))
