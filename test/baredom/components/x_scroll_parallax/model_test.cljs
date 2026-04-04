(ns baredom.components.x-scroll-parallax.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-scroll-parallax.model :as model]))

;; ── parse-direction ─────────────────────────────────────────────────────────
(deftest parse-direction-known-values-test
  (is (= "vertical"   (model/parse-direction "vertical")))
  (is (= "horizontal" (model/parse-direction "horizontal"))))

(deftest parse-direction-case-insensitive-test
  (is (= "vertical"   (model/parse-direction "VERTICAL")))
  (is (= "horizontal" (model/parse-direction "Horizontal"))))

(deftest parse-direction-unknown-falls-back-test
  (is (= "vertical" (model/parse-direction nil)))
  (is (= "vertical" (model/parse-direction "")))
  (is (= "vertical" (model/parse-direction "diagonal"))))

;; ── parse-source ────────────────────────────────────────────────────────────
(deftest parse-source-known-values-test
  (is (= "document" (model/parse-source "document"))))

(deftest parse-source-case-insensitive-test
  (is (= "document" (model/parse-source "DOCUMENT"))))

(deftest parse-source-unknown-falls-back-test
  (is (= "document" (model/parse-source nil)))
  (is (= "document" (model/parse-source "")))
  (is (= "document" (model/parse-source "self"))))

;; ── parse-easing ────────────────────────────────────────────────────────────
(deftest parse-easing-known-values-test
  (is (= "none"   (model/parse-easing "none")))
  (is (= "smooth" (model/parse-easing "smooth"))))

(deftest parse-easing-case-insensitive-test
  (is (= "smooth" (model/parse-easing "SMOOTH")))
  (is (= "none"   (model/parse-easing "None"))))

(deftest parse-easing-unknown-falls-back-test
  (is (= "none" (model/parse-easing nil)))
  (is (= "none" (model/parse-easing "")))
  (is (= "none" (model/parse-easing "ease-in"))))

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

;; ── parse-speed ─────────────────────────────────────────────────────────────
(deftest parse-speed-valid-test
  (is (= 0.5 (model/parse-speed "0.5")))
  (is (= 2.0 (model/parse-speed "2")))
  (is (= -1.0 (model/parse-speed "-1")))
  (is (= 0.0 (model/parse-speed "0"))))

(deftest parse-speed-default-test
  (is (= 1.0 (model/parse-speed nil)))
  (is (= 1.0 (model/parse-speed "")))
  (is (= 1.0 (model/parse-speed "abc"))))

;; ── parse-offset ────────────────────────────────────────────────────────────
(deftest parse-offset-valid-test
  (is (= 50.0 (model/parse-offset "50")))
  (is (= -20.0 (model/parse-offset "-20")))
  (is (= 0.0 (model/parse-offset "0"))))

(deftest parse-offset-default-test
  (is (= 0.0 (model/parse-offset nil)))
  (is (= 0.0 (model/parse-offset "")))
  (is (= 0.0 (model/parse-offset "abc"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "vertical" (:direction m)))
    (is (= "document" (:source m)))
    (is (= "none"     (:easing m)))
    (is (false?       (:disabled? m)))
    (is (= ""         (:label m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize {:direction-raw "horizontal"
                            :source-raw    "document"
                            :easing-raw    "smooth"
                            :disabled-attr ""
                            :label-raw     "Hero section"})]
    (is (= "horizontal" (:direction m)))
    (is (= "document"   (:source m)))
    (is (= "smooth"     (:easing m)))
    (is (true?          (:disabled? m)))
    (is (= "Hero section" (:label m)))))

;; ── compute-progress ────────────────────────────────────────────────────────
(deftest compute-progress-test
  (testing "element at bottom of viewport → progress ≈ 0"
    (let [p (model/compute-progress 1000 200 1000)]
      ;; element-top = viewport-height, so scrolled = 0, total = 1200
      (is (= 0.0 p))))

  (testing "element centered in viewport → progress ≈ 0.5"
    (let [p (model/compute-progress 400 200 1000)]
      ;; scrolled = 600, total = 1200, progress = 0.5
      (is (= 0.5 p))))

  (testing "element at top of viewport → progress ≈ 1"
    (let [p (model/compute-progress -200 200 1000)]
      ;; scrolled = 1200, total = 1200, progress = 1.0
      (is (= 1.0 p))))

  (testing "clamps below 0"
    (is (= 0.0 (model/compute-progress 2000 200 1000))))

  (testing "clamps above 1"
    (is (= 1.0 (model/compute-progress -500 200 1000)))))

;; ── compute-parallax-offset ─────────────────────────────────────────────────
(deftest compute-parallax-offset-test
  (testing "speed=1 produces no parallax"
    (is (= 0.0 (model/compute-parallax-offset 0.5 1.0 1000 0))))

  (testing "speed=0 at center produces no offset"
    (is (= 0.0 (model/compute-parallax-offset 0.5 0.0 1000 0))))

  (testing "speed=0.5 at progress=0 → moves half-viewport backward"
    (let [px (model/compute-parallax-offset 0.0 0.5 1000 0)]
      (is (= 250.0 px))))

  (testing "initial offset is added"
    (let [px (model/compute-parallax-offset 0.5 1.0 1000 50)]
      (is (= 50.0 px)))))

;; ── compute-fade-opacity ────────────────────────────────────────────────────
(deftest compute-fade-opacity-test
  (testing "at center → fully visible"
    (is (= 1.0 (model/compute-fade-opacity 0.5 0.2))))

  (testing "at very start → nearly invisible"
    (is (< (model/compute-fade-opacity 0.05 0.2) 0.5)))

  (testing "at very end → nearly invisible"
    (is (< (model/compute-fade-opacity 0.95 0.2) 0.5)))

  (testing "at fade boundary → fully visible"
    (is (= 1.0 (model/compute-fade-opacity 0.2 0.2)))
    (is (= 1.0 (model/compute-fade-opacity 0.8 0.2)))))

;; ── compute-scale ───────────────────────────────────────────────────────────
(deftest compute-scale-test
  (testing "at center → scale = 1.0"
    (is (= 1.0 (model/compute-scale 0.5 0.85))))

  (testing "at edge (progress=0) → scale = scale-min"
    (is (= 0.85 (model/compute-scale 0.0 0.85))))

  (testing "at edge (progress=1) → scale = scale-min"
    (is (= 0.85 (model/compute-scale 1.0 0.85)))))

;; ── progress-detail ─────────────────────────────────────────────────────────
(deftest progress-detail-test
  (is (= {:progress 0.42} (model/progress-detail 0.42))))
