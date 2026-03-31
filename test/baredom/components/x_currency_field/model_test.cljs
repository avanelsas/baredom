(ns baredom.components.x-currency-field.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-currency-field.model :as model]))

(deftest currency-normalization-test
  (testing "uppercase passthrough"
    (is (= "USD" (model/normalize-currency "USD"))))
  (testing "lowercase is uppercased"
    (is (= "USD" (model/normalize-currency "usd"))))
  (testing "mixed case"
    (is (= "EUR" (model/normalize-currency "eur"))))
  (testing "empty string falls back to USD"
    (is (= "USD" (model/normalize-currency ""))))
  (testing "nil falls back to USD"
    (is (= "USD" (model/normalize-currency nil))))
  (testing "GBP preserved"
    (is (= "GBP" (model/normalize-currency "gbp")))))

(deftest normalize-defaults-test
  (testing "all nil inputs produce safe defaults"
    (let [m (model/normalize {})]
      (is (= ""    (:name m)))
      (is (= ""    (:value m)))
      (is (= "USD" (:currency m)))
      (is (= ""    (:locale m)))
      (is (= ""    (:min m)))
      (is (= ""    (:max m)))
      (is (= ""    (:placeholder m)))
      (is (= ""    (:label m)))
      (is (= ""    (:hint m)))
      (is (= ""    (:error m)))
      (is (= false (:disabled? m)))
      (is (= false (:required? m)))
      (is (= false (:readonly? m)))
      (is (= false (:has-error? m)))
      (is (= false (:has-hint? m)))
      (is (= false (:has-label? m))))))

(deftest boolean-flags-test
  (testing "disabled-present? true → disabled? true"
    (is (= true (:disabled? (model/normalize {:disabled-present? true})))))
  (testing "disabled-present? false → disabled? false"
    (is (= false (:disabled? (model/normalize {:disabled-present? false})))))
  (testing "required-present? true → required? true"
    (is (= true (:required? (model/normalize {:required-present? true})))))
  (testing "readonly-present? true → readonly? true"
    (is (= true (:readonly? (model/normalize {:readonly-present? true}))))))

(deftest has-error-test
  (testing "non-empty error sets has-error? true"
    (is (= true (:has-error? (model/normalize {:error-raw "Invalid"})))))
  (testing "empty error sets has-error? false"
    (is (= false (:has-error? (model/normalize {:error-raw ""})))))
  (testing "nil error sets has-error? false"
    (is (= false (:has-error? (model/normalize {:error-raw nil}))))))

(deftest has-hint-test
  (testing "non-empty hint sets has-hint? true"
    (is (= true (:has-hint? (model/normalize {:hint-raw "Enter amount"})))))
  (testing "empty hint sets has-hint? false"
    (is (= false (:has-hint? (model/normalize {:hint-raw ""})))))
  (testing "nil hint sets has-hint? false"
    (is (= false (:has-hint? (model/normalize {:hint-raw nil}))))))

(deftest has-label-test
  (testing "non-empty label sets has-label? true"
    (is (= true (:has-label? (model/normalize {:label-raw "Price"})))))
  (testing "empty label sets has-label? false"
    (is (= false (:has-label? (model/normalize {:label-raw ""})))))
  (testing "nil label sets has-label? false"
    (is (= false (:has-label? (model/normalize {:label-raw nil}))))))

(deftest min-max-defaults-test
  (testing "nil min → empty string"
    (is (= "" (:min (model/normalize {:min-raw nil})))))
  (testing "nil max → empty string"
    (is (= "" (:max (model/normalize {:max-raw nil})))))
  (testing "min passes through"
    (is (= "0" (:min (model/normalize {:min-raw "0"})))))
  (testing "max passes through"
    (is (= "9999" (:max (model/normalize {:max-raw "9999"}))))))

(deftest normalize-passthrough-test
  (testing "name passes through"
    (is (= "price" (:name (model/normalize {:name-raw "price"})))))
  (testing "value passes through"
    (is (= "1234.56" (:value (model/normalize {:value-raw "1234.56"})))))
  (testing "placeholder passes through"
    (is (= "0.00" (:placeholder (model/normalize {:placeholder-raw "0.00"})))))
  (testing "locale passes through"
    (is (= "de-DE" (:locale (model/normalize {:locale-raw "de-DE"}))))))

;; ---------------------------------------------------------------------------
;; validation-message
;; ---------------------------------------------------------------------------

(deftest validation-message-empty-value-test
  (testing "empty value returns no error"
    (is (= "" (model/validation-message {:value "" :min "" :max ""})))))

(deftest validation-message-valid-number-test
  (testing "valid number within range returns no error"
    (is (= "" (model/validation-message {:value "50" :min "0" :max "100"})))))

(deftest validation-message-bad-input-test
  (testing "non-numeric non-empty value returns badInput message"
    (is (= "Please enter a valid number."
           (model/validation-message {:value "abc" :min "" :max ""})))))

(deftest validation-message-range-underflow-test
  (testing "value below min returns underflow message"
    (is (= "Value must be at least 10."
           (model/validation-message {:value "5" :min "10" :max ""})))))

(deftest validation-message-range-overflow-test
  (testing "value above max returns overflow message"
    (is (= "Value must be at most 100."
           (model/validation-message {:value "200" :min "" :max "100"})))))

(deftest validation-message-no-min-max-test
  (testing "valid number with no min/max returns no error"
    (is (= "" (model/validation-message {:value "42" :min "" :max ""})))))
