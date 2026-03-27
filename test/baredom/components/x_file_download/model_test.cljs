(ns baredom.components.x-file-download.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-file-download.model :as model]))

(deftest normalize-defaults-test
  (testing "defaults when no attrs present"
    (let [m (model/normalize {})]
      (is (= ""    (:href m)))
      (is (= ""    (:filename m)))
      (is (= false (:disabled? m)))
      (is (= nil   (:aria-label m))))))

(deftest normalize-href-test
  (testing "href-raw is forwarded"
    (let [m (model/normalize {:href-raw "https://example.com/file.pdf"})]
      (is (= "https://example.com/file.pdf" (:href m)))))

  (testing "nil href-raw yields empty string"
    (let [m (model/normalize {:href-raw nil})]
      (is (= "" (:href m)))))

  (testing "empty href-raw yields empty string"
    (let [m (model/normalize {:href-raw ""})]
      (is (= "" (:href m))))))

(deftest normalize-filename-test
  (testing "filename-raw is forwarded"
    (let [m (model/normalize {:filename-raw "report.pdf"})]
      (is (= "report.pdf" (:filename m)))))

  (testing "nil filename-raw yields empty string"
    (let [m (model/normalize {:filename-raw nil})]
      (is (= "" (:filename m)))))

  (testing "empty filename-raw yields empty string"
    (let [m (model/normalize {:filename-raw ""})]
      (is (= "" (:filename m))))))

(deftest normalize-disabled-test
  (testing "disabled-present? true sets disabled?"
    (let [m (model/normalize {:disabled-present? true})]
      (is (= true (:disabled? m)))))

  (testing "disabled-present? nil yields disabled? false"
    (let [m (model/normalize {:disabled-present? nil})]
      (is (= false (:disabled? m)))))

  (testing "disabled-present? false yields disabled? false"
    (let [m (model/normalize {:disabled-present? false})]
      (is (= false (:disabled? m))))))

(deftest normalize-aria-label-test
  (testing "aria-label-raw is forwarded"
    (let [m (model/normalize {:aria-label-raw "Download report"})]
      (is (= "Download report" (:aria-label m)))))

  (testing "nil aria-label-raw yields nil"
    (let [m (model/normalize {:aria-label-raw nil})]
      (is (= nil (:aria-label m))))))
