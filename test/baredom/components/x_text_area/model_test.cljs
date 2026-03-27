(ns baredom.components.x-text-area.model-test
  (:require [cljs.test :refer [deftest is testing]]
            [baredom.components.x-text-area.model :as model]))

(deftest normalize-resize-test
  (testing "valid values are passed through"
    (is (= "none"       (model/normalize-resize "none")))
    (is (= "vertical"   (model/normalize-resize "vertical")))
    (is (= "horizontal" (model/normalize-resize "horizontal")))
    (is (= "both"       (model/normalize-resize "both"))))
  (testing "nil and absent default to vertical"
    (is (= "vertical" (model/normalize-resize nil)))
    (is (= "vertical" (model/normalize-resize ""))))
  (testing "unknown values default to vertical"
    (is (= "vertical" (model/normalize-resize "auto")))
    (is (= "vertical" (model/normalize-resize "VERTICAL")))))

(deftest parse-positive-int-test
  (testing "valid positive integers"
    (is (= 3   (model/parse-positive-int "3")))
    (is (= 10  (model/parse-positive-int "10")))
    (is (= 500 (model/parse-positive-int "500"))))
  (testing "zero and negative return nil"
    (is (nil? (model/parse-positive-int "0")))
    (is (nil? (model/parse-positive-int "-1"))))
  (testing "non-numeric strings return nil"
    (is (nil? (model/parse-positive-int "abc")))
    (is (nil? (model/parse-positive-int "")))
    (is (nil? (model/parse-positive-int nil)))))

(deftest normalize-defaults-test
  (testing "all defaults when no attributes"
    (let [m (model/normalize {})]
      (is (= ""         (:label m)))
      (is (= ""         (:name m)))
      (is (= ""         (:value m)))
      (is (= ""         (:placeholder m)))
      (is (= ""         (:hint m)))
      (is (= ""         (:error m)))
      (is (false?       (:disabled? m)))
      (is (false?       (:readonly? m)))
      (is (false?       (:required? m)))
      (is (= 3          (:rows m)))
      (is (nil?         (:maxlength m)))
      (is (nil?         (:minlength m)))
      (is (= ""         (:autocomplete m)))
      (is (= "vertical" (:resize m)))
      (is (false?       (:has-error? m)))
      (is (false?       (:has-hint? m)))
      (is (false?       (:has-label? m))))))

(deftest normalize-flags-test
  (testing "has-error? is true when error is non-empty string"
    (let [m (model/normalize {:error-raw "Required field"})]
      (is (true? (:has-error? m)))
      (is (= "Required field" (:error m)))))
  (testing "has-error? is false when error is empty string"
    (let [m (model/normalize {:error-raw ""})]
      (is (false? (:has-error? m)))))
  (testing "has-hint? is true when hint is non-empty string"
    (let [m (model/normalize {:hint-raw "Enter your message"})]
      (is (true? (:has-hint? m)))
      (is (= "Enter your message" (:hint m)))))
  (testing "has-label? is true when label is non-empty string"
    (let [m (model/normalize {:label-raw "Comments"})]
      (is (true? (:has-label? m)))
      (is (= "Comments" (:label m))))))

(deftest normalize-full-test
  (testing "all attributes set"
    (let [m (model/normalize {:label-raw         "Bio"
                               :name-raw          "bio"
                               :value-raw         "Hello world"
                               :placeholder-raw   "Tell us about yourself"
                               :hint-raw          "Keep it brief"
                               :error-raw         ""
                               :disabled-present? false
                               :readonly-present? false
                               :required-present? true
                               :rows-raw          "5"
                               :maxlength-raw     "200"
                               :minlength-raw     "10"
                               :autocomplete-raw  "off"
                               :resize-raw        "both"})]
      (is (= "Bio"                    (:label m)))
      (is (= "bio"                    (:name m)))
      (is (= "Hello world"            (:value m)))
      (is (= "Tell us about yourself" (:placeholder m)))
      (is (= "Keep it brief"          (:hint m)))
      (is (false?                     (:disabled? m)))
      (is (false?                     (:readonly? m)))
      (is (true?                      (:required? m)))
      (is (= 5                        (:rows m)))
      (is (= 200                      (:maxlength m)))
      (is (= 10                       (:minlength m)))
      (is (= "off"                    (:autocomplete m)))
      (is (= "both"                   (:resize m)))
      (is (false?                     (:has-error? m)))
      (is (true?                      (:has-hint? m)))
      (is (true?                      (:has-label? m))))))

(deftest normalize-boolean-attrs-test
  (testing "disabled, readonly, required toggle on presence"
    (let [m (model/normalize {:disabled-present? true
                               :readonly-present? true
                               :required-present? true})]
      (is (true? (:disabled? m)))
      (is (true? (:readonly? m)))
      (is (true? (:required? m))))))
