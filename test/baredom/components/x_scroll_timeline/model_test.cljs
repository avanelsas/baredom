(ns baredom.components.x-scroll-timeline.model-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [baredom.components.x-scroll-timeline.model :as model]))

;; ── parse-layout ────────────────────────────────────────────────────────────
(deftest parse-layout-test
  (testing "valid values"
    (is (= "alternating" (model/parse-layout "alternating")))
    (is (= "left"        (model/parse-layout "left")))
    (is (= "right"       (model/parse-layout "right"))))
  (testing "case insensitive"
    (is (= "alternating" (model/parse-layout "ALTERNATING")))
    (is (= "left"        (model/parse-layout "Left"))))
  (testing "trims whitespace"
    (is (= "right" (model/parse-layout " right "))))
  (testing "unknown values default to alternating"
    (is (= "alternating" (model/parse-layout "top")))
    (is (= "alternating" (model/parse-layout "center")))
    (is (= "alternating" (model/parse-layout ""))))
  (testing "nil defaults to alternating"
    (is (= "alternating" (model/parse-layout nil)))))

;; ── parse-track ─────────────────────────────────────────────────────────────
(deftest parse-track-test
  (testing "valid values"
    (is (= "straight" (model/parse-track "straight")))
    (is (= "curved"   (model/parse-track "curved"))))
  (testing "case insensitive"
    (is (= "curved" (model/parse-track "CURVED"))))
  (testing "unknown defaults to straight"
    (is (= "straight" (model/parse-track "wavy")))
    (is (= "straight" (model/parse-track nil)))))

;; ── parse-threshold ─────────────────────────────────────────────────────────
(deftest parse-threshold-test
  (testing "valid values"
    (is (= 0.5 (model/parse-threshold "0.5")))
    (is (= 0.0 (model/parse-threshold "0")))
    (is (= 1.0 (model/parse-threshold "1"))))
  (testing "clamped to [0,1]"
    (is (= 0.0 (model/parse-threshold "-0.5")))
    (is (= 1.0 (model/parse-threshold "2.5"))))
  (testing "invalid string defaults to 0.5"
    (is (= 0.5 (model/parse-threshold "abc"))))
  (testing "nil defaults to 0.5"
    (is (= 0.5 (model/parse-threshold nil)))))

;; ── parse-marker ────────────────────────────────────────────────────────────
(deftest parse-marker-test
  (testing "valid values"
    (is (= "dot"  (model/parse-marker "dot")))
    (is (= "ring" (model/parse-marker "ring")))
    (is (= "none" (model/parse-marker "none"))))
  (testing "unknown defaults to dot"
    (is (= "dot" (model/parse-marker "circle")))
    (is (= "dot" (model/parse-marker nil)))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (is (true?  (model/parse-bool-attr "")))
  (is (true?  (model/parse-bool-attr "anything")))
  (is (false? (model/parse-bool-attr nil))))

;; ── parse-autoplay-speed ────────────────────────────────────────────────────
(deftest parse-autoplay-speed-test
  (testing "valid values"
    (is (= 50  (model/parse-autoplay-speed "50")))
    (is (= 100 (model/parse-autoplay-speed "100")))
    (is (= 1   (model/parse-autoplay-speed "1"))))
  (testing "clamped to [1,1000]"
    (is (= 1    (model/parse-autoplay-speed "0")))
    (is (= 1    (model/parse-autoplay-speed "0.5")))
    (is (= 1    (model/parse-autoplay-speed "-10")))
    (is (= 1000 (model/parse-autoplay-speed "2000"))))
  (testing "invalid string defaults to 50"
    (is (= 50 (model/parse-autoplay-speed "abc"))))
  (testing "nil defaults to 50"
    (is (= 50 (model/parse-autoplay-speed nil)))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-test
  (testing "defaults"
    (let [m (model/normalize {})]
      (is (= "alternating" (:layout m)))
      (is (= "straight"    (:track m)))
      (is (= 0.5           (:threshold m)))
      (is (false?           (:no-progress? m)))
      (is (false?           (:disabled? m)))
      (is (= ""            (:label m)))
      (is (= "dot"         (:marker m)))
      (is (false?           (:autoplay? m)))
      (is (= 50            (:autoplay-speed m)))
      (is (false?           (:autoplay-loop? m)))
      (is (false?           (:autoplay-indicator? m)))))

  (testing "all values provided"
    (let [m (model/normalize {:layout-raw              "right"
                              :track-raw               "curved"
                              :threshold-raw           "0.3"
                              :no-progress-attr        ""
                              :disabled-attr           ""
                              :label-raw               "Project timeline"
                              :marker-raw              "ring"
                              :autoplay-attr           ""
                              :autoplay-speed-raw      "80"
                              :autoplay-loop-attr      ""
                              :autoplay-indicator-attr ""})]
      (is (= "right"            (:layout m)))
      (is (= "curved"           (:track m)))
      (is (= 0.3                (:threshold m)))
      (is (true?                 (:no-progress? m)))
      (is (true?                 (:disabled? m)))
      (is (= "Project timeline" (:label m)))
      (is (= "ring"             (:marker m)))
      (is (true?                 (:autoplay? m)))
      (is (= 80                 (:autoplay-speed m)))
      (is (true?                 (:autoplay-loop? m)))
      (is (true?                 (:autoplay-indicator? m))))))

;; ── entry-side ──────────────────────────────────────────────────────────────
(deftest entry-side-test
  (testing "alternating layout"
    (is (= "left"  (model/entry-side "alternating" 0)))
    (is (= "right" (model/entry-side "alternating" 1)))
    (is (= "left"  (model/entry-side "alternating" 2)))
    (is (= "right" (model/entry-side "alternating" 3))))
  (testing "left layout — entries go right"
    (is (= "right" (model/entry-side "left" 0)))
    (is (= "right" (model/entry-side "left" 1))))
  (testing "right layout — entries go left"
    (is (= "left" (model/entry-side "right" 0)))
    (is (= "left" (model/entry-side "right" 1)))))

;; ── compute-overall-progress ────────────────────────────────────────────────
(deftest compute-overall-progress-test
  (testing "at bottom of viewport"
    (is (= 0.0 (model/compute-overall-progress 1000 500 1000))))
  (testing "halfway"
    (is (< 0.3 (model/compute-overall-progress 0 1000 1000) 0.7)))
  (testing "clamped to [0,1]"
    (is (= 0.0 (model/compute-overall-progress 2000 500 1000)))
    (is (= 1.0 (model/compute-overall-progress -2000 500 1000)))))

;; ── find-active-entry ───────────────────────────────────────────────────────
(deftest find-active-entry-test
  (testing "empty returns -1"
    (is (= -1 (model/find-active-entry [] 500))))
  (testing "spanning entry wins"
    (let [rects [{:top 100 :bottom 300}
                 {:top 400 :bottom 600}
                 {:top 700 :bottom 900}]]
      (is (= 0 (model/find-active-entry rects 200)))
      (is (= 1 (model/find-active-entry rects 500)))
      (is (= 2 (model/find-active-entry rects 800)))))
  (testing "last-above fallback"
    (let [rects [{:top 100 :bottom 200}
                 {:top 300 :bottom 400}]]
      (is (= 0 (model/find-active-entry rects 250))))))

;; ── compute-entry-progress ──────────────────────────────────────────────────
(deftest compute-entry-progress-test
  (testing "at top of entry"
    (is (= 0.0 (model/compute-entry-progress 500 200 500))))
  (testing "halfway through"
    (is (= 0.5 (model/compute-entry-progress 400 200 500))))
  (testing "past entry"
    (is (= 1.0 (model/compute-entry-progress 200 200 500))))
  (testing "zero-height entry"
    (is (= 0.0 (model/compute-entry-progress 500 0 500)))))

;; ── build-serpentine-path ───────────────────────────────────────────────────
(deftest build-serpentine-path-test
  (testing "empty points returns empty string"
    (is (= "" (model/build-serpentine-path [] 60 200))))
  (testing "single point returns M command"
    (is (= "M 200 100" (model/build-serpentine-path [{:y 100 :side "left"}] 60 200))))
  (testing "two points returns M + C"
    (let [path (model/build-serpentine-path [{:y 100 :side "left"}
                                             {:y 300 :side "right"}]
                                            60 200)]
      (is (string? path))
      (is (.startsWith path "M "))
      (is (.includes path "C "))))
  (testing "multiple points"
    (let [path (model/build-serpentine-path [{:y 100 :side "left"}
                                             {:y 300 :side "right"}
                                             {:y 500 :side "left"}]
                                            60 200)]
      (is (string? path))
      ;; Should have M + 2 C commands
      (is (= 2 (count (re-seq #"C " path)))))))

