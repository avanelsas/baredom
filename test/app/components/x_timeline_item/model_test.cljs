(ns app.components.x-timeline-item.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-timeline-item.model :as model]))

;; ── parse-status ─────────────────────────────────────────────────────────────
(deftest parse-status-nil-defaults-to-pending-test
  (is (= :pending (model/parse-status nil))))

(deftest parse-status-empty-string-defaults-to-pending-test
  (is (= :pending (model/parse-status ""))))

(deftest parse-status-unknown-defaults-to-pending-test
  (is (= :pending (model/parse-status "foo"))))

(deftest parse-status-pending-test
  (is (= :pending (model/parse-status "pending"))))

(deftest parse-status-active-test
  (is (= :active (model/parse-status "active"))))

(deftest parse-status-complete-test
  (is (= :complete (model/parse-status "complete"))))

(deftest parse-status-error-test
  (is (= :error (model/parse-status "error"))))

(deftest parse-status-warning-test
  (is (= :warning (model/parse-status "warning"))))

(deftest parse-status-uppercase-normalised-test
  (is (= :active (model/parse-status "ACTIVE")))
  (is (= :complete (model/parse-status "Complete"))))

;; ── parse-position ───────────────────────────────────────────────────────────
(deftest parse-position-nil-defaults-to-start-test
  (is (= :start (model/parse-position nil))))

(deftest parse-position-start-test
  (is (= :start (model/parse-position "start"))))

(deftest parse-position-end-test
  (is (= :end (model/parse-position "end"))))

(deftest parse-position-unknown-defaults-to-start-test
  (is (= :start (model/parse-position "alternate")))
  (is (= :start (model/parse-position "center"))))

;; ── parse-connector ──────────────────────────────────────────────────────────
(deftest parse-connector-nil-defaults-to-solid-test
  (is (= :solid (model/parse-connector nil))))

(deftest parse-connector-solid-test
  (is (= :solid (model/parse-connector "solid"))))

(deftest parse-connector-dashed-test
  (is (= :dashed (model/parse-connector "dashed"))))

(deftest parse-connector-none-test
  (is (= :none (model/parse-connector "none"))))

(deftest parse-connector-unknown-defaults-to-solid-test
  (is (= :solid (model/parse-connector "dotted"))))

;; ── normalize-icon ───────────────────────────────────────────────────────────
(deftest normalize-icon-nil-returns-nil-test
  (is (nil? (model/normalize-icon nil))))

(deftest normalize-icon-empty-returns-nil-test
  (is (nil? (model/normalize-icon ""))))

(deftest normalize-icon-none-returns-nil-test
  (is (nil? (model/normalize-icon "none")))
  (is (nil? (model/normalize-icon "NONE"))))

(deftest normalize-icon-value-returns-value-test
  (is (= "★" (model/normalize-icon "★")))
  (is (= "✓" (model/normalize-icon "✓"))))

;; ── parse-data-index ─────────────────────────────────────────────────────────
(deftest parse-data-index-nil-returns-nil-test
  (is (nil? (model/parse-data-index nil))))

(deftest parse-data-index-zero-returns-zero-test
  (is (= 0 (model/parse-data-index "0"))))

(deftest parse-data-index-positive-returns-int-test
  (is (= 3 (model/parse-data-index "3"))))

(deftest parse-data-index-negative-returns-nil-test
  (is (nil? (model/parse-data-index "-1"))))

(deftest parse-data-index-invalid-returns-nil-test
  (is (nil? (model/parse-data-index "abc"))))

;; ── effective-position ───────────────────────────────────────────────────────
(deftest effective-position-data-pos-wins-test
  (is (= :end (model/effective-position "end" "start"))))

(deftest effective-position-data-pos-start-wins-test
  (is (= :start (model/effective-position "start" "end"))))

(deftest effective-position-invalid-data-pos-falls-back-to-position-test
  (is (= :end (model/effective-position "alternate" "end")))
  (is (= :start (model/effective-position "center" "start"))))

(deftest effective-position-nil-data-pos-uses-position-test
  (is (= :end (model/effective-position nil "end"))))

(deftest effective-position-both-nil-defaults-to-start-test
  (is (= :start (model/effective-position nil nil))))

;; ── icon-for-status ──────────────────────────────────────────────────────────
(deftest icon-for-status-pending-test
  (is (= "○" (model/icon-for-status :pending))))

(deftest icon-for-status-active-test
  (is (= "●" (model/icon-for-status :active))))

(deftest icon-for-status-complete-test
  (is (= "✓" (model/icon-for-status :complete))))

(deftest icon-for-status-error-test
  (is (= "✕" (model/icon-for-status :error))))

(deftest icon-for-status-warning-test
  (is (= "!" (model/icon-for-status :warning))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "" (:label m)))
    (is (= "" (:title m)))
    (is (= :pending (:status m)))
    (is (= :default (:icon-mode m)))
    (is (= :solid (:connector m)))
    (is (= :start (:effective-position m)))
    (is (false? (:disabled? m)))
    (is (false? (:data-last? m)))
    (is (nil? (:data-index m)))
    (is (false? (:data-striped? m)))
    (is (= "○" (:marker-icon m)))))

(deftest normalize-custom-icon-test
  (let [m (model/normalize {:icon-present? true :icon-raw "★"})]
    (is (= :custom (:icon-mode m)))
    (is (= "★" (:icon m)))
    (is (= "★" (:marker-icon m)))))

(deftest normalize-hidden-icon-test
  (let [m (model/normalize {:icon-present? true :icon-raw "none"})]
    (is (= :hidden (:icon-mode m)))
    (is (nil? (:marker-icon m)))))

(deftest normalize-data-position-overrides-position-test
  (let [m (model/normalize {:data-position-raw "end" :position-raw "start"})]
    (is (= :end (:effective-position m)))))

(deftest normalize-status-active-test
  (let [m (model/normalize {:status-raw "active"})]
    (is (= :active (:status m)))
    (is (= "●" (:marker-icon m)))))

(deftest normalize-data-last-test
  (let [m (model/normalize {:data-last? true})]
    (is (true? (:data-last? m)))))

(deftest normalize-marker-aria-with-index-test
  (let [m (model/normalize {:status-raw "complete" :label-raw "Jan 2024" :data-index-raw "0"})]
    (is (= "Step 1: Jan 2024 (complete)" (:marker-aria m)))))

(deftest normalize-marker-aria-without-index-test
  (let [m (model/normalize {:status-raw "active"})]
    (is (= "(active)" (:marker-aria m)))))

;; ── interactive-eligible? ────────────────────────────────────────────────────
(deftest interactive-eligible-not-disabled-test
  (is (true? (model/interactive-eligible? {:disabled? false}))))

(deftest interactive-eligible-disabled-test
  (is (false? (model/interactive-eligible? {:disabled? true}))))

;; ── event detail builders ────────────────────────────────────────────────────
(deftest connected-detail-shape-test
  (let [m {:status :complete :label "Jan" :effective-position :start :disabled? false}
        d (model/connected-detail m)]
    (is (= "complete" (:status d)))
    (is (= "Jan" (:label d)))
    (is (= "start" (:position d)))
    (is (= false (:disabled d)))))

(deftest disconnected-detail-shape-test
  (let [m {:status :error :label "Feb"}
        d (model/disconnected-detail m)]
    (is (= "error" (:status d)))
    (is (= "Feb" (:label d)))))

(deftest click-detail-shape-test
  (let [m {:status :active :label "Now"}
        d (model/click-detail m)]
    (is (= "active" (:status d)))
    (is (= "Now" (:label d)))))
