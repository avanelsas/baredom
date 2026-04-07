(ns baredom.components.x-bento-grid.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-bento-grid.model :as model]))

(deftest normalize-columns-test
  (testing "valid integers"
    (is (= 4 (model/normalize-columns "4")))
    (is (= 1 (model/normalize-columns "1")))
    (is (= 12 (model/normalize-columns "12"))))
  (testing "clamping"
    (is (= 1 (model/normalize-columns "0")))
    (is (= 1 (model/normalize-columns "-3")))
    (is (= 12 (model/normalize-columns "13")))
    (is (= 12 (model/normalize-columns "99"))))
  (testing "invalid/nil/empty fall back to 4"
    (is (= 4 (model/normalize-columns nil)))
    (is (= 4 (model/normalize-columns "")))
    (is (= 4 (model/normalize-columns "abc")))))

(deftest normalize-row-height-test
  (testing "non-empty passes through"
    (is (= "120px" (model/normalize-row-height "120px")))
    (is (= "1fr" (model/normalize-row-height "1fr")))
    (is (= "minmax(100px,auto)" (model/normalize-row-height "minmax(100px,auto)"))))
  (testing "nil/empty falls back to auto"
    (is (= "auto" (model/normalize-row-height nil)))
    (is (= "auto" (model/normalize-row-height "")))))

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

(deftest gap->css-test
  (is (= "0" (model/gap->css "none")))
  (is (= "4px" (model/gap->css "xs")))
  (is (= "8px" (model/gap->css "sm")))
  (is (= "16px" (model/gap->css "md")))
  (is (= "24px" (model/gap->css "lg")))
  (is (= "32px" (model/gap->css "xl")))
  (is (= "16px" (model/gap->css "unknown"))))

(deftest derive-state-defaults-test
  (let [state (model/derive-state {})]
    (is (= 4 (:columns state)))
    (is (= "repeat(4,minmax(0,1fr))" (:template state)))
    (is (= "md" (:gap state)))
    (is (= "16px" (:row-gap state)))
    (is (= "16px" (:column-gap state)))
    (is (= "auto" (:row-height state)))))

(deftest derive-state-custom-test
  (let [state (model/derive-state {:columns "3" :gap "lg" :row-height "100px"})]
    (is (= 3 (:columns state)))
    (is (= "repeat(3,minmax(0,1fr))" (:template state)))
    (is (= "lg" (:gap state)))
    (is (= "24px" (:row-gap state)))
    (is (= "24px" (:column-gap state)))
    (is (= "100px" (:row-height state)))))

(deftest derive-state-gap-overrides-test
  (let [state (model/derive-state {:gap "lg" :row-gap "xl" :column-gap "sm"})]
    (is (= "lg" (:gap state)))
    (is (= "32px" (:row-gap state)))
    (is (= "8px" (:column-gap state)))))
