(ns baredom.components.x-avatar-group.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-avatar-group.model :as model]))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (doseq [s ["xs" "sm" "md" "lg" "xl"]]
    (is (= s (model/parse-size s)))))

(deftest parse-size-default-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "huge"))))

;; ── parse-overlap ─────────────────────────────────────────────────────────
(deftest parse-overlap-valid-test
  (doseq [o ["none" "sm" "md" "lg"]]
    (is (= o (model/parse-overlap o)))))

(deftest parse-overlap-default-test
  (is (= "md" (model/parse-overlap nil)))
  (is (= "md" (model/parse-overlap "xl"))))

;; ── parse-direction ───────────────────────────────────────────────────────
(deftest parse-direction-valid-test
  (is (= "ltr" (model/parse-direction "ltr")))
  (is (= "rtl" (model/parse-direction "rtl"))))

(deftest parse-direction-default-test
  (is (= "ltr" (model/parse-direction nil)))
  (is (= "ltr" (model/parse-direction "auto"))))

;; ── parse-max ─────────────────────────────────────────────────────────────
(deftest parse-max-valid-test
  (is (= 3  (model/parse-max "3")))
  (is (= 10 (model/parse-max "10"))))

(deftest parse-max-invalid-test
  (is (nil? (model/parse-max nil)))
  (is (nil? (model/parse-max "")))
  (is (nil? (model/parse-max "0")))
  (is (nil? (model/parse-max "-1"))))

;; ── normalize-label ───────────────────────────────────────────────────────
(deftest normalize-label-test
  (is (= "Team"  (model/normalize-label "Team")))
  (is (= "Team"  (model/normalize-label "  Team  ")))
  (is (nil?      (model/normalize-label "")))
  (is (nil?      (model/normalize-label "   ")))
  (is (nil?      (model/normalize-label nil))))

;; ── compute-visible-hidden ────────────────────────────────────────────────
(deftest compute-visible-hidden-no-max-test
  (is (= [5 0] (model/compute-visible-hidden 5 nil))))

(deftest compute-visible-hidden-under-max-test
  (is (= [3 0] (model/compute-visible-hidden 3 5))))

(deftest compute-visible-hidden-at-max-test
  (is (= [5 0] (model/compute-visible-hidden 5 5))))

(deftest compute-visible-hidden-over-max-test
  (is (= [3 2] (model/compute-visible-hidden 5 3)))
  (is (= [1 4] (model/compute-visible-hidden 5 1))))

;; ── overlap-margin ────────────────────────────────────────────────────────
(deftest overlap-margin-values-test
  (is (= "0px"   (get model/overlap-margin "none")))
  (is (= "-4px"  (get model/overlap-margin "sm")))
  (is (= "-8px"  (get model/overlap-margin "md")))
  (is (= "-12px" (get model/overlap-margin "lg"))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "md"  (:size m)))
    (is (= "md"  (:overlap m)))
    (is (= "ltr" (:direction m)))
    (is (nil?    (:max m)))
    (is (false?  (:disabled m)))
    (is (nil?    (:label m)))))

(deftest normalize-all-attrs-test
  (let [m (model/normalize {:size-raw          "lg"
                             :overlap-raw       "sm"
                             :max-raw           "3"
                             :direction-raw     "rtl"
                             :disabled-present? true
                             :label-raw         "Avatar group"})]
    (is (= "lg"           (:size m)))
    (is (= "sm"           (:overlap m)))
    (is (= 3              (:max m)))
    (is (= "rtl"          (:direction m)))
    (is (true?            (:disabled m)))
    (is (= "Avatar group" (:label m)))))
