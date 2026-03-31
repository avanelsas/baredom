(ns baredom.components.x-grid.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-grid.model :as model]))

(deftest normalize-gap-test
  (testing "valid tokens pass through"
    (doseq [v ["none" "xs" "sm" "md" "lg" "xl"]]
      (is (= v (model/normalize-gap v)))))
  (testing "invalid/nil falls back to md"
    (is (= "md" (model/normalize-gap "bad")))
    (is (= "md" (model/normalize-gap nil)))
    (is (= "md" (model/normalize-gap "")))))

(deftest normalize-row-gap-test
  (testing "valid token returns token"
    (is (= "lg" (model/normalize-row-gap "lg"))))
  (testing "invalid/nil returns nil"
    (is (nil? (model/normalize-row-gap "bad")))
    (is (nil? (model/normalize-row-gap nil)))))

(deftest normalize-column-gap-test
  (testing "valid token returns token"
    (is (= "sm" (model/normalize-column-gap "sm"))))
  (testing "invalid/nil returns nil"
    (is (nil? (model/normalize-column-gap "bad")))
    (is (nil? (model/normalize-column-gap nil)))))

(deftest normalize-align-test
  (testing "valid values pass through"
    (doseq [v ["start" "center" "end" "stretch"]]
      (is (= v (model/normalize-align v)))))
  (testing "invalid falls back to stretch"
    (is (= "stretch" (model/normalize-align "bad")))
    (is (= "stretch" (model/normalize-align nil)))))

(deftest normalize-flow-test
  (testing "valid values pass through"
    (doseq [v ["row" "column" "dense" "row-dense" "column-dense"]]
      (is (= v (model/normalize-flow v)))))
  (testing "invalid falls back to row"
    (is (= "row" (model/normalize-flow "bad")))
    (is (= "row" (model/normalize-flow nil)))))

(deftest normalize-columns-test
  (testing "non-empty passes through"
    (is (= "3" (model/normalize-columns "3")))
    (is (= "1fr 2fr" (model/normalize-columns "1fr 2fr"))))
  (testing "nil and empty return nil"
    (is (nil? (model/normalize-columns nil)))
    (is (nil? (model/normalize-columns "")))))

(deftest normalize-min-size-test
  (testing "non-empty passes through"
    (is (= "20rem" (model/normalize-min-size "20rem"))))
  (testing "nil and empty return default"
    (is (= "16rem" (model/normalize-min-size nil)))
    (is (= "16rem" (model/normalize-min-size "")))))

(deftest gap->css-test
  (is (= "0" (model/gap->css "none")))
  (is (= "4px" (model/gap->css "xs")))
  (is (= "8px" (model/gap->css "sm")))
  (is (= "16px" (model/gap->css "md")))
  (is (= "24px" (model/gap->css "lg")))
  (is (= "32px" (model/gap->css "xl")))
  (is (= "16px" (model/gap->css "unknown"))))

(deftest flow->css-test
  (is (= "row dense" (model/flow->css "row-dense")))
  (is (= "column dense" (model/flow->css "column-dense")))
  (is (= "row" (model/flow->css "row")))
  (is (= "column" (model/flow->css "column")))
  (is (= "dense" (model/flow->css "dense"))))

(deftest derive-state-defaults-test
  (let [state (model/derive-state {})]
    (is (= "repeat(auto-fit,minmax(16rem,1fr))" (:columns state)))
    (is (= "md" (:gap state)))
    (is (= "16px" (:row-gap state)))
    (is (= "16px" (:column-gap state)))
    (is (= "stretch" (:align-items state)))
    (is (= "stretch" (:justify-items state)))
    (is (= "row" (:auto-flow state)))
    (is (= false (:inline state)))))

(deftest derive-state-explicit-columns-test
  (let [state (model/derive-state {:columns "1fr 2fr 1fr"})]
    (is (= "1fr 2fr 1fr" (:columns state)))))

(deftest derive-state-gap-overrides-test
  (let [state (model/derive-state {:gap "lg" :row-gap "xl" :column-gap "sm"})]
    (is (= "lg" (:gap state)))
    (is (= "32px" (:row-gap state)))
    (is (= "8px" (:column-gap state)))))

(deftest derive-state-inline-test
  (let [state (model/derive-state {:inline true})]
    (is (= true (:inline state)))))
