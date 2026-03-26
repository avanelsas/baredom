(ns app.components.x-context-menu.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-context-menu.model :as model]))

;; ---- parse-placement ----

(deftest parse-placement-test
  (testing "known placements pass through"
    (is (= "bottom-start" (model/parse-placement "bottom-start")))
    (is (= "bottom-end"   (model/parse-placement "bottom-end")))
    (is (= "top-start"    (model/parse-placement "top-start")))
    (is (= "top-end"      (model/parse-placement "top-end")))
    (is (= "right-start"  (model/parse-placement "right-start")))
    (is (= "left-start"   (model/parse-placement "left-start"))))
  (testing "unknown or nil falls back to default"
    (is (= model/default-placement (model/parse-placement nil)))
    (is (= model/default-placement (model/parse-placement "")))
    (is (= model/default-placement (model/parse-placement "center")))))

;; ---- parse-int-pos ----

(deftest parse-int-pos-test
  (is (= 16   (model/parse-int-pos "16"  model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "0"   model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "-1"  model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "abc" model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos nil   model/default-offset))))

;; ---- parse-bool-attr ----

(deftest parse-bool-attr-test
  (is (true?  (model/parse-bool-attr "")))
  (is (true?  (model/parse-bool-attr "true")))
  (is (false? (model/parse-bool-attr "false")))
  (is (false? (model/parse-bool-attr nil))))

;; ---- normalize ----

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false? (:open? m)))
    (is (false? (:disabled? m)))
    (is (= model/default-placement (:placement m)))
    (is (= model/default-offset    (:offset m)))
    (is (= model/default-z-index   (:z-index m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize
           {:open-present?     ""
            :disabled-present? ""
            :placement-raw     "top-end"
            :offset-raw        "16"
            :z-index-raw       "2000"})]
    (is (true?  (:open? m)))
    (is (true?  (:disabled? m)))
    (is (= "top-end" (:placement m)))
    (is (= 16        (:offset m)))
    (is (= 2000      (:z-index m)))))

(deftest normalize-false-attrs-test
  (let [m (model/normalize
           {:open-present?     "false"
            :disabled-present? "false"})]
    (is (false? (:open? m)))
    (is (false? (:disabled? m)))))

;; ---- compute-position ----

(deftest compute-position-bottom-start-test
  (let [anchor {:x 100 :y 200 :width 120 :height 40}
        panel  {:width 160 :height 200}
        vp     {:width 1440 :height 900}
        pos    (model/compute-position "bottom-start" 8 anchor panel vp 8)]
    ;; Should place below anchor
    (is (>= (:y pos) (+ 200 40))
        "y should be at or below bottom of anchor")
    (is (= "bottom-start" (:final-placement pos)))
    (is (number? (:x pos)))
    (is (number? (:max-height pos)))))

(deftest compute-position-flip-top-test
  ;; Anchor near bottom of viewport: bottom-start should flip to top-start
  (let [anchor {:x 100 :y 800 :width 120 :height 40}
        panel  {:width 160 :height 200}
        vp     {:width 1440 :height 900}
        pos    (model/compute-position "bottom-start" 8 anchor panel vp 8)]
    ;; Panel (200px tall) below anchor at y=840 doesn't fit in 900px viewport with 8px margin
    (is (= "top-start" (:final-placement pos))
        "should flip to top-start when bottom doesn't fit")))

(deftest compute-position-clamped-to-viewport-test
  ;; Anchor far to the right — x should be clamped
  (let [anchor {:x 1400 :y 100 :width 10 :height 10}
        panel  {:width 200 :height 100}
        vp     {:width 1440 :height 900}
        pos    (model/compute-position "bottom-start" 8 anchor panel vp 8)]
    (is (<= (:x pos) (- 1440 200 8))
        "x should be clamped so panel stays within viewport")))

(deftest compute-position-max-height-test
  (let [anchor {:x 100 :y 100 :width 120 :height 40}
        panel  {:width 160 :height 200}
        vp     {:width 1440 :height 900}
        pos    (model/compute-position "bottom-start" 8 anchor panel vp 8)]
    (is (>= (:max-height pos) 40)
        "max-height should be at least 40")))
