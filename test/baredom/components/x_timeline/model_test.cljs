(ns baredom.components.x-timeline.model-test
  (:require [cljs.test :refer [deftest is testing]]
            [baredom.components.x-timeline.model :as model]))

;; ── parse-position ────────────────────────────────────────────────────────────

(deftest parse-position-defaults
  (testing "nil and empty fall back to start"
    (is (= "start" (model/parse-position nil)))
    (is (= "start" (model/parse-position "")))
    (is (= "start" (model/parse-position "   ")))))

(deftest parse-position-valid-values
  (testing "valid position values are preserved"
    (is (= "start"       (model/parse-position "start")))
    (is (= "end"         (model/parse-position "end")))
    (is (= "alternating" (model/parse-position "alternating")))))

(deftest parse-position-case-insensitive
  (testing "values are normalised to lowercase"
    (is (= "start"       (model/parse-position "START")))
    (is (= "end"         (model/parse-position "End")))
    (is (= "alternating" (model/parse-position "Alternating")))))

(deftest parse-position-unknown-falls-back
  (testing "unknown values fall back to start"
    (is (= "start" (model/parse-position "center")))
    (is (= "start" (model/parse-position "both")))
    (is (= "start" (model/parse-position "left")))))

;; ── item-position ─────────────────────────────────────────────────────────────

(deftest item-position-start-mode
  (testing "start mode always returns start"
    (is (= "start" (model/item-position "start" 0)))
    (is (= "start" (model/item-position "start" 1)))
    (is (= "start" (model/item-position "start" 5)))))

(deftest item-position-end-mode
  (testing "end mode always returns end"
    (is (= "end" (model/item-position "end" 0)))
    (is (= "end" (model/item-position "end" 1)))
    (is (= "end" (model/item-position "end" 5)))))

(deftest item-position-alternating-mode
  (testing "alternating: even index → start, odd → end"
    (is (= "start" (model/item-position "alternating" 0)))
    (is (= "end"   (model/item-position "alternating" 1)))
    (is (= "start" (model/item-position "alternating" 2)))
    (is (= "end"   (model/item-position "alternating" 3)))
    (is (= "start" (model/item-position "alternating" 4)))))

(deftest item-position-unknown-falls-back
  (testing "unknown position string falls back to start"
    (is (= "start" (model/item-position "unknown" 0)))
    (is (= "start" (model/item-position "unknown" 1)))))

;; ── normalize ─────────────────────────────────────────────────────────────────

(deftest normalize-defaults
  (testing "all nil/missing inputs produce default output"
    (let [m (model/normalize {})]
      (is (= "" (:label m)))
      (is (= "start" (:position m)))
      (is (= false (:striped? m))))))

(deftest normalize-label
  (testing "label-raw is preserved"
    (is (= "My Timeline" (:label (model/normalize {:label-raw "My Timeline"}))))
    (is (= "" (:label (model/normalize {:label-raw nil}))))
    (is (= "" (:label (model/normalize {:label-raw ""}))))))

(deftest normalize-position
  (testing "position-raw is normalised"
    (is (= "alternating" (:position (model/normalize {:position-raw "alternating"}))))
    (is (= "end"         (:position (model/normalize {:position-raw "end"}))))
    (is (= "start"       (:position (model/normalize {:position-raw "bad"}))))))

(deftest normalize-striped
  (testing "striped? is coerced to boolean"
    (is (= true  (:striped? (model/normalize {:striped? true}))))
    (is (= false (:striped? (model/normalize {:striped? false}))))
    (is (= false (:striped? (model/normalize {:striped? nil}))))))

;; ── select-detail ─────────────────────────────────────────────────────────────

(deftest select-detail-shape
  (testing "returns map with correct keys"
    (let [d (model/select-detail 2 "active" "Launch")]
      (is (= 2 (:index d)))
      (is (= "active" (:status d)))
      (is (= "Launch" (:label d))))))

(deftest select-detail-nil-defaults
  (testing "nil index falls back to 0, nil strings to empty"
    (let [d (model/select-detail nil nil nil)]
      (is (= 0  (:index d)))
      (is (= "" (:status d)))
      (is (= "" (:label d))))))
