(ns baredom.components.x-drawer.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-drawer.model :as model]))

;; ── normalize-placement ───────────────────────────────────────────────────────
(deftest normalize-placement-test
  (testing "valid placements are accepted"
    (doseq [p ["left" "right" "top" "bottom"]]
      (is (= p (model/normalize-placement p)))))
  (testing "nil falls back to default"
    (is (= model/default-placement (model/normalize-placement nil))))
  (testing "empty string falls back to default"
    (is (= model/default-placement (model/normalize-placement ""))))
  (testing "invalid string falls back to default"
    (is (= model/default-placement (model/normalize-placement "center"))))
  (testing "default-placement is right"
    (is (= "right" model/default-placement))))

;; ── normalize-label ───────────────────────────────────────────────────────────
(deftest normalize-label-test
  (testing "non-empty string is returned as-is"
    (is (= "Navigation" (model/normalize-label "Navigation"))))
  (testing "empty string falls back to default"
    (is (= model/default-label (model/normalize-label ""))))
  (testing "nil falls back to default"
    (is (= model/default-label (model/normalize-label nil))))
  (testing "default-label is Drawer"
    (is (= "Drawer" model/default-label))))

;; ── normalize defaults ────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (testing "open? defaults to false"
      (is (= false (:open? m))))
    (testing "placement defaults to right"
      (is (= "right" (:placement m))))
    (testing "label defaults to Drawer"
      (is (= "Drawer" (:label m))))))

;; ── normalize with values ─────────────────────────────────────────────────────
(deftest normalize-open-test
  (is (= true (:open? (model/normalize {:open-present? true}))))
  (is (= false (:open? (model/normalize {:open-present? false})))))

(deftest normalize-all-placements-test
  (doseq [p ["left" "right" "top" "bottom"]]
    (let [m (model/normalize {:placement-raw p})]
      (is (= p (:placement m))))))

(deftest normalize-invalid-placement-falls-back-test
  (let [m (model/normalize {:placement-raw "diagonal"})]
    (is (= model/default-placement (:placement m)))))

(deftest normalize-label-custom-test
  (let [m (model/normalize {:label-raw "Task panel"})]
    (is (= "Task panel" (:label m)))))

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
  (is (= "x-drawer" model/tag-name)))

(deftest event-names-test
  (is (= "x-drawer-toggle" model/event-toggle))
  (is (= "x-drawer-dismiss" model/event-dismiss)))

(deftest observed-attributes-test
  (is (= 3 (.-length model/observed-attributes)))
  (is (some #{"open"} (array-seq model/observed-attributes)))
  (is (some #{"placement"} (array-seq model/observed-attributes)))
  (is (some #{"label"} (array-seq model/observed-attributes))))
