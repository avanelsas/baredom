(ns app.components.x-collapse.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-collapse.model :as model]))

(deftest parse-int-pos-test
  (testing "nil returns nil"
    (is (nil? (model/parse-int-pos nil))))
  (testing "zero returns nil"
    (is (nil? (model/parse-int-pos "0"))))
  (testing "negative returns nil"
    (is (nil? (model/parse-int-pos "-5"))))
  (testing "non-numeric returns nil"
    (is (nil? (model/parse-int-pos "abc"))))
  (testing "valid positive integer"
    (is (= 300 (model/parse-int-pos "300")))
    (is (= 1   (model/parse-int-pos "1")))
    (is (= 2000 (model/parse-int-pos "2000")))))

(deftest clamp-test
  (is (= 0    (model/clamp -10 0 100)))
  (is (= 100  (model/clamp 200 0 100)))
  (is (= 50   (model/clamp 50 0 100)))
  (is (= 0    (model/clamp 0 0 100)))
  (is (= 100  (model/clamp 100 0 100))))

(deftest normalize-defaults-test
  (testing "all-absent gives defaults"
    (let [m (model/normalize {})]
      (is (= false (:open? m)))
      (is (= false (:disabled? m)))
      (is (= ""    (:header m)))
      (is (= model/default-duration-ms (:duration-ms m))))))

(deftest normalize-open-test
  (let [m (model/normalize {:open-present? true})]
    (is (= true (:open? m)))))

(deftest normalize-disabled-test
  (let [m (model/normalize {:disabled-present? true})]
    (is (= true (:disabled? m)))))

(deftest normalize-header-test
  (let [m (model/normalize {:header-raw "Section 1"})]
    (is (= "Section 1" (:header m)))))

(deftest normalize-duration-ms-test
  (testing "valid duration passes through"
    (let [m (model/normalize {:duration-ms-raw "500"})]
      (is (= 500 (:duration-ms m)))))
  (testing "duration below 0 is clamped (but parse-int-pos returns nil so default is used)"
    (let [m (model/normalize {:duration-ms-raw "0"})]
      (is (= model/default-duration-ms (:duration-ms m)))))
  (testing "duration above 2000 is clamped to 2000"
    (let [m (model/normalize {:duration-ms-raw "9999"})]
      (is (= 2000 (:duration-ms m)))))
  (testing "non-numeric falls back to default"
    (let [m (model/normalize {:duration-ms-raw "fast"})]
      (is (= model/default-duration-ms (:duration-ms m)))))
  (testing "nil falls back to default"
    (let [m (model/normalize {:duration-ms-raw nil})]
      (is (= model/default-duration-ms (:duration-ms m))))))

(deftest toggle-detail-test
  (let [d (model/toggle-detail true "pointer")]
    (is (= true      (.-open d)))
    (is (= "pointer" (.-source d))))
  (let [d (model/toggle-detail false "keyboard")]
    (is (= false      (.-open d)))
    (is (= "keyboard" (.-source d)))))
