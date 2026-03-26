(ns app.components.x-badge.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-badge.model :as model]))

;; ── parse-variant ─────────────────────────────────────────────────────────
(deftest parse-variant-valid-test
  (doseq [v ["neutral" "info" "success" "warning" "error"]]
    (is (= v (model/parse-variant v)))))

(deftest parse-variant-default-test
  (is (= "neutral" (model/parse-variant nil)))
  (is (= "neutral" (model/parse-variant "")))
  (is (= "neutral" (model/parse-variant "invalid"))))

(deftest parse-variant-case-insensitive-test
  (is (= "info" (model/parse-variant "INFO")))
  (is (= "error" (model/parse-variant "ERROR"))))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (is (= "sm" (model/parse-size "sm")))
  (is (= "md" (model/parse-size "md"))))

(deftest parse-size-default-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "xl"))))

;; ── parse-int-attr ────────────────────────────────────────────────────────
(deftest parse-int-attr-valid-test
  (is (= 5   (model/parse-int-attr "5"  0)))
  (is (= 0   (model/parse-int-attr "0"  0)))
  (is (= 99  (model/parse-int-attr "99" 0))))

(deftest parse-int-attr-invalid-test
  (is (= 0  (model/parse-int-attr "abc" 0)))
  (is (= 42 (model/parse-int-attr nil   42)))
  (is (= 42 (model/parse-int-attr ""    42))))

;; ── parse-bool-attr ───────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (is (true?  (model/parse-bool-attr "")))
  (is (true?  (model/parse-bool-attr "true")))
  (is (false? (model/parse-bool-attr "false")))
  (is (false? (model/parse-bool-attr nil))))

;; ── compute-mode ─────────────────────────────────────────────────────────
(deftest compute-mode-slot-priority-test
  (is (= :slot (model/compute-mode {:has-slot? true :count 5 :text "hi" :dot true}))))

(deftest compute-mode-count-test
  (is (= :count (model/compute-mode {:has-slot? false :count 5}))))

(deftest compute-mode-text-test
  (is (= :text (model/compute-mode {:has-slot? false :count nil :text "NEW"}))))

(deftest compute-mode-dot-test
  (is (= :dot (model/compute-mode {:has-slot? false :count nil :text nil :dot true}))))

(deftest compute-mode-empty-test
  (is (= :empty (model/compute-mode {:has-slot? false :count nil :text nil :dot false}))))

;; ── display-text ─────────────────────────────────────────────────────────
(deftest display-text-count-test
  (is (= "5"   (model/display-text {:has-slot? false :count 5  :max 99 :text nil :dot false})))
  (is (= "42"  (model/display-text {:has-slot? false :count 42 :max 99 :text nil :dot false}))))

(deftest display-text-count-capped-test
  (is (= "99+" (model/display-text {:has-slot? false :count 100 :max 99 :text nil :dot false})))
  (is (= "9+"  (model/display-text {:has-slot? false :count 10  :max 9  :text nil :dot false}))))

(deftest display-text-count-at-max-test
  (is (= "99" (model/display-text {:has-slot? false :count 99 :max 99 :text nil :dot false}))))

(deftest display-text-text-mode-test
  (is (= "NEW" (model/display-text {:has-slot? false :count nil :max 99 :text "NEW" :dot false}))))

(deftest display-text-nil-for-dot-and-slot-test
  (is (nil? (model/display-text {:has-slot? false :count nil :max 99 :text nil :dot true})))
  (is (nil? (model/display-text {:has-slot? true  :count 5  :max 99 :text nil :dot false}))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "neutral" (:variant m)))
    (is (= "md"      (:size m)))
    (is (= 99        (:max m)))
    (is (false?      (:pill m)))
    (is (false?      (:dot m)))
    (is (nil?        (:count m)))
    (is (nil?        (:text m)))))

(deftest normalize-count-zero-test
  (let [m (model/normalize {:count-raw "0"})]
    (is (= 0 (:count m)))))

(deftest normalize-text-trimmed-test
  (is (= "hi" (:text (model/normalize {:text-raw "  hi  "}))))
  (is (nil?   (:text (model/normalize {:text-raw "  "})))))
