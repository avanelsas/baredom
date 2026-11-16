(ns baredom.components.x-context-menu.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-context-menu.model :as model]
            [baredom.utils.position :as position]))

;; ---- parse-placement ----

(deftest parse-placement-test
  (testing "known placements pass through"
    (is (= "bottom-start" (model/parse-placement "bottom-start")))
    (is (= "bottom-end"   (model/parse-placement "bottom-end")))
    (is (= "top-start"    (model/parse-placement "top-start")))
    (is (= "top-end"      (model/parse-placement "top-end")))
    (is (= "right-start"  (model/parse-placement "right-start")))
    (is (= "left-start"   (model/parse-placement "left-start"))))
  (testing "unknown or nil falls back to default"
    (is (= model/default-placement (model/parse-placement nil)))
    (is (= model/default-placement (model/parse-placement "")))
    (is (= model/default-placement (model/parse-placement "center")))))

;; ---- parse-int-pos ----

(deftest parse-int-pos-test
  (is (= 16   (model/parse-int-pos "16"  model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "0"   model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "-1"  model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos "abc" model/default-offset)))
  (is (= model/default-offset (model/parse-int-pos nil   model/default-offset))))

;; ---- parse-bool-attr ----

;; ---- normalize ----

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false? (:open? m)))
    (is (false? (:disabled? m)))
    (is (= model/default-placement (:placement m)))
    (is (= model/default-offset    (:offset m)))
    (is (= model/default-z-index   (:z-index m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize
           {:open-present?     true
            :disabled-present? true
            :placement-raw     "top-end"
            :offset-raw        "16"
            :z-index-raw       "2000"})]
    (is (true?  (:open? m)))
    (is (true?  (:disabled? m)))
    (is (= "top-end" (:placement m)))
    (is (= 16        (:offset m)))
    (is (= 2000      (:z-index m)))))

(deftest normalize-reads-absent-flags-as-off-test
  (let [m (model/normalize {})]
    (is (false? (:open? m)))
    (is (false? (:disabled? m)))))

(deftest placements-are-positioned-test
  (testing "every placement the component allows has an entry in position/placements"
    (is (every? position/placements model/valid-placements))))
