(ns baredom.components.x-popover.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-popover.model :as model]))

;; ---------------------------------------------------------------------------
;; normalize-placement
;; ---------------------------------------------------------------------------

(deftest normalize-placement-default-test
  (testing "nil falls back to default"
    (is (= model/default-placement (model/normalize-placement nil))))
  (testing "empty string falls back to default"
    (is (= model/default-placement (model/normalize-placement ""))))
  (testing "unknown value falls back to default"
    (is (= model/default-placement (model/normalize-placement "left"))))
  (testing "valid values are preserved"
    (is (= "bottom-start" (model/normalize-placement "bottom-start")))
    (is (= "bottom-end"   (model/normalize-placement "bottom-end")))
    (is (= "top-start"    (model/normalize-placement "top-start")))
    (is (= "top-end"      (model/normalize-placement "top-end")))))

;; ---------------------------------------------------------------------------
;; normalize
;; ---------------------------------------------------------------------------

(deftest normalize-defaults-test
  (testing "all absent gives defaults"
    (let [m (model/normalize {})]
      (is (= false (:open? m)))
      (is (= false (:disabled? m)))
      (is (= false (:no-close? m)))
      (is (= ""    (:heading m)))
      (is (= model/default-close-label (:close-label m)))
      (is (= model/default-placement   (:placement m))))))

(deftest normalize-open-test
  (let [m (model/normalize {:open-present? true})]
    (is (= true (:open? m)))))

(deftest normalize-disabled-test
  (let [m (model/normalize {:disabled-present? true})]
    (is (= true (:disabled? m)))))

(deftest normalize-no-close-test
  (let [m (model/normalize {:no-close-present? true})]
    (is (= true (:no-close? m)))))

(deftest normalize-heading-test
  (let [m (model/normalize {:heading-raw "My Heading"})]
    (is (= "My Heading" (:heading m)))))

(deftest normalize-close-label-test
  (let [m (model/normalize {:close-label-raw "Dismiss"})]
    (is (= "Dismiss" (:close-label m)))))

(deftest normalize-placement-test
  (testing "valid placement passes through"
    (let [m (model/normalize {:placement-raw "top-end"})]
      (is (= "top-end" (:placement m)))))
  (testing "invalid placement falls back to default"
    (let [m (model/normalize {:placement-raw "invalid"})]
      (is (= model/default-placement (:placement m))))))

;; ---------------------------------------------------------------------------
;; toggle-detail
;; ---------------------------------------------------------------------------

(deftest toggle-detail-test
  (let [d (model/toggle-detail true "pointer")]
    (is (= true      (.-open d)))
    (is (= "pointer" (.-source d))))
  (let [d (model/toggle-detail false "escape")]
    (is (= false    (.-open d)))
    (is (= "escape" (.-source d)))))

;; ---------------------------------------------------------------------------
;; normalize portal
;; ---------------------------------------------------------------------------

(deftest normalize-portal-test
  (is (false? (:portal? (model/normalize {}))))
  (is (true?  (:portal? (model/normalize {:portal-present? true})))))

;; ---------------------------------------------------------------------------
;; compute-position
;; ---------------------------------------------------------------------------

(deftest compute-position-bottom-start-test
  (let [pos (model/compute-position
             "bottom-start" 4
             {:x 100 :y 50 :width 100 :height 40}
             {:width 200 :height 150}
             {:width 1024 :height 768} 8)]
    (is (= "bottom-start" (:final-placement pos)))
    (is (>= (:x pos) 8))
    (is (= 94 (:y pos)))))

(deftest compute-position-flips-when-no-space-test
  (testing "flips from bottom to top when panel would overflow viewport"
    (let [pos (model/compute-position
               "bottom-start" 4
               {:x 100 :y 700 :width 100 :height 40}
               {:width 200 :height 150}
               {:width 1024 :height 768} 8)]
      (is (= "top-start" (:final-placement pos))))))
