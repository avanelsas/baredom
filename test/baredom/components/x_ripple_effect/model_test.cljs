(ns baredom.components.x-ripple-effect.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-ripple-effect.model :as model]))

;; ── parse-intensity ─────────────────────────────────────────────────────────
(deftest parse-intensity-valid-test
  (is (= 25 (model/parse-intensity "25")))
  (is (= 50 (model/parse-intensity "50")))
  (is (= 1  (model/parse-intensity "1"))))

(deftest parse-intensity-default-test
  (is (= 25 (model/parse-intensity nil)))
  (is (= 25 (model/parse-intensity "")))
  (is (= 25 (model/parse-intensity "abc")))
  (is (= 25 (model/parse-intensity "-5"))))

(deftest parse-intensity-clamping-test
  (is (= 1   (model/parse-intensity "0.5")))
  (is (= 100 (model/parse-intensity "200"))))

;; ── parse-duration ──────────────────────────────────────────────────────────
(deftest parse-duration-valid-test
  (is (= 800  (model/parse-duration "800")))
  (is (= 100  (model/parse-duration "100")))
  (is (= 5000 (model/parse-duration "5000"))))

(deftest parse-duration-default-test
  (is (= 800 (model/parse-duration nil)))
  (is (= 800 (model/parse-duration "")))
  (is (= 800 (model/parse-duration "abc")))
  (is (= 800 (model/parse-duration "-1"))))

(deftest parse-duration-clamping-test
  (is (= 100  (model/parse-duration "50")))
  (is (= 5000 (model/parse-duration "10000"))))

;; ── parse-frequency ─────────────────────────────────────────────────────────
(deftest parse-frequency-valid-test
  (is (= 0.04 (model/parse-frequency "0.04")))
  (is (= 0.1  (model/parse-frequency "0.1"))))

(deftest parse-frequency-default-test
  (is (= 0.04 (model/parse-frequency nil)))
  (is (= 0.04 (model/parse-frequency "")))
  (is (= 0.04 (model/parse-frequency "abc")))
  (is (= 0.04 (model/parse-frequency "-0.01"))))

(deftest parse-frequency-clamping-test
  (is (= 0.005 (model/parse-frequency "0.001")))
  (is (= 0.2   (model/parse-frequency "0.5"))))

;; ── whitespace handling ──────────────────────────────────────────────────────
(deftest whitespace-trimmed-test
  (is (= 50   (model/parse-intensity " 50 ")))
  (is (= 400  (model/parse-duration  " 400 ")))
  (is (= 0.1  (model/parse-frequency " 0.1 "))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= 25   (:intensity m)))
    (is (= 800  (:duration m)))
    (is (= 0.04 (:frequency m)))
    (is (false? (:disabled? m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize {:intensity-raw "50"
                            :duration-raw  "400"
                            :frequency-raw "0.08"
                            :disabled-present? true})]
    (is (= 50   (:intensity m)))
    (is (= 400  (:duration m)))
    (is (= 0.08 (:frequency m)))
    (is (true?  (:disabled? m)))))
