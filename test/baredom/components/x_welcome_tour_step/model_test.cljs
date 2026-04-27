(ns baredom.components.x-welcome-tour-step.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-welcome-tour-step.model :as model]))

;; ── parse-placement ─────────────────────────────────────────────────────────
(deftest parse-placement-valid-values-test
  (is (= "bottom"       (model/parse-placement "bottom")))
  (is (= "top"          (model/parse-placement "top")))
  (is (= "left"         (model/parse-placement "left")))
  (is (= "right"        (model/parse-placement "right")))
  (is (= "top-start"    (model/parse-placement "top-start")))
  (is (= "top-end"      (model/parse-placement "top-end")))
  (is (= "bottom-start" (model/parse-placement "bottom-start")))
  (is (= "bottom-end"   (model/parse-placement "bottom-end"))))

(deftest parse-placement-case-insensitive-test
  (is (= "bottom" (model/parse-placement "BOTTOM")))
  (is (= "top"    (model/parse-placement "Top"))))

(deftest parse-placement-falls-back-to-bottom-test
  (is (= "bottom" (model/parse-placement nil)))
  (is (= "bottom" (model/parse-placement "")))
  (is (= "bottom" (model/parse-placement "invalid"))))

;; ── parse-connector ─────────────────────────────────────────────────────────
(deftest parse-connector-valid-values-test
  (is (= "arrow" (model/parse-connector "arrow")))
  (is (= "line"  (model/parse-connector "line")))
  (is (= "curve" (model/parse-connector "curve")))
  (is (= "none"  (model/parse-connector "none"))))

(deftest parse-connector-nil-returns-nil-test
  (is (nil? (model/parse-connector nil)))
  (is (nil? (model/parse-connector "")))
  (is (nil? (model/parse-connector "invalid"))))

;; ── parse-cutout-padding ────────────────────────────────────────────────────
(deftest parse-cutout-padding-test
  (is (= 8   (model/parse-cutout-padding nil)))
  (is (= 8   (model/parse-cutout-padding "")))
  (is (= 16  (model/parse-cutout-padding "16")))
  (is (= 0   (model/parse-cutout-padding "0")))
  (is (= 8   (model/parse-cutout-padding "abc"))))

;; ── parse-cutout-radius ─────────────────────────────────────────────────────
(deftest parse-cutout-radius-test
  (is (= 4   (model/parse-cutout-radius nil)))
  (is (= 4   (model/parse-cutout-radius "")))
  (is (= 12  (model/parse-cutout-radius "12")))
  (is (= 0   (model/parse-cutout-radius "0")))
  (is (= 4   (model/parse-cutout-radius "abc"))))

;; ── parse-scroll-to ─────────────────────────────────────────────────────────
(deftest parse-scroll-to-test
  (testing "absent (nil) defaults to true"
    (is (true? (model/parse-scroll-to nil))))
  (testing "empty string = true (attribute present)"
    (is (true? (model/parse-scroll-to ""))))
  (testing "\"false\" = false"
    (is (false? (model/parse-scroll-to "false"))))
  (testing "\"FALSE\" = false"
    (is (false? (model/parse-scroll-to "FALSE"))))
  (testing "\"true\" = true"
    (is (true? (model/parse-scroll-to "true")))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil? (:target m)))
    (is (= "" (:title m)))
    (is (= "bottom" (:placement m)))
    (is (nil? (:connector m)))
    (is (= 8 (:cutout-padding m)))
    (is (= 4 (:cutout-radius m)))
    (is (true? (:scroll-to? m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize
           {:target-raw "#my-el"
            :title-raw "Step One"
            :placement-raw "right"
            :connector-raw "curve"
            :cutout-padding-raw "16"
            :cutout-radius-raw "8"
            :scroll-to-raw "false"})]
    (is (= "#my-el" (:target m)))
    (is (= "Step One" (:title m)))
    (is (= "right" (:placement m)))
    (is (= "curve" (:connector m)))
    (is (= 16 (:cutout-padding m)))
    (is (= 8 (:cutout-radius m)))
    (is (false? (:scroll-to? m)))))

(deftest normalize-trims-target-test
  (is (nil? (:target (model/normalize {:target-raw "  "}))))
  (is (= "#foo" (:target (model/normalize {:target-raw "  #foo  "})))))
