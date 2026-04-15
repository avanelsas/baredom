(ns baredom.components.x-icon.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-icon.model :as model]))

;; ── parse-size ───────────────────────────────────────────────────────────────
(deftest parse-size-tokens-test
  (testing "sm"
    (let [r (model/parse-size "sm")]
      (is (= "16px" (:css r)))
      (is (= "sm"   (:token r)))
      (is (= 16     (:px r)))))
  (testing "md"
    (let [r (model/parse-size "md")]
      (is (= "20px" (:css r)))
      (is (= "md"   (:token r)))
      (is (= 20     (:px r)))))
  (testing "lg"
    (is (= "24px" (:css (model/parse-size "lg")))))
  (testing "xl"
    (is (= "32px" (:css (model/parse-size "xl")))))
  (testing "case insensitive"
    (is (= "24px" (:css (model/parse-size "LG"))))))

(deftest parse-size-numeric-test
  (testing "plain integer"
    (let [r (model/parse-size "48")]
      (is (= "48px" (:css r)))
      (is (= 48     (:px r)))
      (is (nil?     (:token r)))))
  (testing "whitespace tolerated"
    (is (= "48px" (:css (model/parse-size "  48  "))))))

(deftest parse-size-invalid-test
  (testing "empty → default md"
    (is (= "20px" (:css (model/parse-size "")))))
  (testing "nil → default md"
    (is (= "20px" (:css (model/parse-size nil)))))
  (testing "unknown token → default md"
    (is (= "20px" (:css (model/parse-size "huge")))))
  (testing "zero → default"
    (is (= "20px" (:css (model/parse-size "0")))))
  (testing "negative → default"
    (is (= "20px" (:css (model/parse-size "-5")))))
  (testing "mixed alphanumeric → default"
    (is (= "20px" (:css (model/parse-size "48abc")))))
  (testing "decimals rejected → default (integers only)"
    (is (= "20px" (:css (model/parse-size "16.5"))))))

;; ── parse-color ──────────────────────────────────────────────────────────────
(deftest parse-color-valid-test
  (doseq [c ["inherit" "primary" "secondary" "tertiary"
             "success" "warning" "danger" "muted"]]
    (is (= c (model/parse-color c)))))

(deftest parse-color-invalid-test
  (is (= "inherit" (model/parse-color nil)))
  (is (= "inherit" (model/parse-color "")))
  (is (= "inherit" (model/parse-color "purple")))
  (is (= "inherit" (model/parse-color "bogus"))))

(deftest parse-color-case-insensitive-test
  (is (= "primary" (model/parse-color "PRIMARY")))
  (is (= "success" (model/parse-color "Success"))))

;; ── color-css-value ──────────────────────────────────────────────────────────
(deftest color-css-value-inherit-test
  (is (= "inherit" (model/color-css-value "inherit"))))

(deftest color-css-value-theme-test
  (testing "primary references theme token"
    (let [v (model/color-css-value "primary")]
      (is (.startsWith v "var(--x-color-primary,"))))
  (testing "danger references theme token"
    (is (.startsWith (model/color-css-value "danger") "var(--x-color-danger,")))
  (testing "muted uses --x-color-text-muted specifically"
    (is (.startsWith (model/color-css-value "muted") "var(--x-color-text-muted,"))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "20px"    (:size-css m)))
    (is (= "inherit" (:color-css m)))
    (is (= ""        (:label m)))
    (is (false?      (:labelled? m)))))

(deftest normalize-label-present-test
  (let [m (model/normalize {:label-present? true :label-raw "Save"})]
    (is (= "Save" (:label m)))
    (is (true?    (:labelled? m)))))

(deftest normalize-label-empty-is-decorative-test
  (let [m (model/normalize {:label-present? true :label-raw ""})]
    (is (= ""     (:label m)))
    (is (false?   (:labelled? m)))))

(deftest normalize-label-absent-is-decorative-test
  (let [m (model/normalize {:label-present? false})]
    (is (false? (:labelled? m)))))

(deftest normalize-combined-test
  (let [m (model/normalize {:size-raw       "lg"
                            :color-raw      "danger"
                            :label-raw      "Delete"
                            :label-present? true})]
    (is (= "24px"  (:size-css m)))
    (is (.startsWith (:color-css m) "var(--x-color-danger,"))
    (is (= "Delete" (:label m)))
    (is (true?      (:labelled? m)))))

(deftest normalize-numeric-size-test
  (let [m (model/normalize {:size-raw "64"})]
    (is (= "64px" (:size-css m)))))
