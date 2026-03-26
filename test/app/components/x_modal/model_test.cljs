(ns app.components.x-modal.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-modal.model :as model]))

;; ── normalize-size ────────────────────────────────────────────────────────────
(deftest normalize-size-test
  (testing "valid sizes are accepted"
    (doseq [s ["sm" "md" "lg" "xl" "full"]]
      (is (= s (model/normalize-size s)))))
  (testing "nil falls back to default"
    (is (= model/default-size (model/normalize-size nil))))
  (testing "empty string falls back to default"
    (is (= model/default-size (model/normalize-size ""))))
  (testing "invalid string falls back to default"
    (is (= model/default-size (model/normalize-size "huge"))))
  (testing "default-size is md"
    (is (= "md" model/default-size))))

;; ── normalize-label ───────────────────────────────────────────────────────────
(deftest normalize-label-test
  (testing "non-empty string is returned as-is"
    (is (= "Confirm Delete" (model/normalize-label "Confirm Delete"))))
  (testing "empty string falls back to default"
    (is (= model/default-label (model/normalize-label ""))))
  (testing "nil falls back to default"
    (is (= model/default-label (model/normalize-label nil))))
  (testing "default-label is Modal"
    (is (= "Modal" model/default-label))))

;; ── normalize defaults ────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (testing "open? defaults to false"
      (is (= false (:open? m))))
    (testing "size defaults to md"
      (is (= "md" (:size m))))
    (testing "label defaults to Modal"
      (is (= "Modal" (:label m))))))

;; ── normalize with values ─────────────────────────────────────────────────────
(deftest normalize-open-test
  (is (= true (:open? (model/normalize {:open-present? true}))))
  (is (= false (:open? (model/normalize {:open-present? false})))))

(deftest normalize-all-sizes-test
  (doseq [s ["sm" "md" "lg" "xl" "full"]]
    (let [m (model/normalize {:size-raw s})]
      (is (= s (:size m))))))

(deftest normalize-invalid-size-falls-back-test
  (let [m (model/normalize {:size-raw "huge"})]
    (is (= model/default-size (:size m)))))

(deftest normalize-label-custom-test
  (let [m (model/normalize {:label-raw "Confirm Delete"})]
    (is (= "Confirm Delete" (:label m)))))

;; ── event details ─────────────────────────────────────────────────────────────
(deftest toggle-event-detail-open-test
  (let [^js d (model/toggle-event-detail true)]
    (is (= true (.-open d)))))

(deftest toggle-event-detail-closed-test
  (let [^js d (model/toggle-event-detail false)]
    (is (= false (.-open d)))))

(deftest dismiss-event-detail-escape-test
  (let [^js d (model/dismiss-event-detail model/reason-escape)]
    (is (= "escape" (.-reason d)))))

(deftest dismiss-event-detail-backdrop-test
  (let [^js d (model/dismiss-event-detail model/reason-backdrop)]
    (is (= "backdrop" (.-reason d)))))

;; ── constants ─────────────────────────────────────────────────────────────────
(deftest tag-name-test
  (is (= "x-modal" model/tag-name)))

(deftest event-names-test
  (is (= "x-modal-toggle" model/event-toggle))
  (is (= "x-modal-dismiss" model/event-dismiss)))

(deftest observed-attributes-test
  (is (= 3 (.-length model/observed-attributes)))
  (is (some #{"open"} (array-seq model/observed-attributes)))
  (is (some #{"size"} (array-seq model/observed-attributes)))
  (is (some #{"label"} (array-seq model/observed-attributes))))
