(ns baredom.components.x-form-field.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-form-field.model :as model]))

(deftest normalize-defaults-test
  (testing "all nil inputs produce safe defaults"
    (let [m (model/normalize {})]
      (is (= ""    (:label m)))
      (is (= "text" (:type m)))
      (is (= ""    (:name m)))
      (is (= ""    (:value m)))
      (is (= ""    (:placeholder m)))
      (is (= ""    (:hint m)))
      (is (= ""    (:error m)))
      (is (= false (:disabled? m)))
      (is (= false (:readonly? m)))
      (is (= false (:required? m)))
      (is (= ""    (:autocomplete m)))
      (is (= false (:has-error? m)))
      (is (= false (:has-hint? m)))
      (is (= false (:has-label? m))))))

(deftest normalize-type-test
  (testing "valid types pass through"
    (doseq [t ["text" "email" "password" "url" "number" "tel"]]
      (is (= t (:type (model/normalize {:type-raw t})))
          (str "type " t " should be preserved"))))

  (testing "unknown type falls back to text"
    (is (= "text" (:type (model/normalize {:type-raw "search"}))))
    (is (= "text" (:type (model/normalize {:type-raw "color"}))))
    (is (= "text" (:type (model/normalize {:type-raw nil})))))

  (testing "empty string type falls back to text"
    (is (= "text" (:type (model/normalize {:type-raw ""}))))))

(deftest normalize-error-test
  (testing "non-empty error sets has-error? true"
    (let [m (model/normalize {:error-raw "Required field"})]
      (is (= "Required field" (:error m)))
      (is (= true (:has-error? m)))))

  (testing "empty error sets has-error? false"
    (let [m (model/normalize {:error-raw ""})]
      (is (= false (:has-error? m)))
      (is (= "" (:error m)))))

  (testing "nil error sets has-error? false"
    (is (= false (:has-error? (model/normalize {:error-raw nil}))))))

(deftest normalize-hint-test
  (testing "non-empty hint sets has-hint? true"
    (let [m (model/normalize {:hint-raw "Enter your email address"})]
      (is (= true (:has-hint? m)))
      (is (= "Enter your email address" (:hint m)))))

  (testing "empty hint sets has-hint? false"
    (is (= false (:has-hint? (model/normalize {:hint-raw ""}))))))

(deftest normalize-label-test
  (testing "non-empty label sets has-label? true"
    (let [m (model/normalize {:label-raw "Email"})]
      (is (= true (:has-label? m)))
      (is (= "Email" (:label m)))))

  (testing "empty label sets has-label? false"
    (is (= false (:has-label? (model/normalize {:label-raw ""}))))))

(deftest normalize-boolean-attrs-test
  (testing "disabled-present? true → disabled? true"
    (is (= true (:disabled? (model/normalize {:disabled-present? true})))))

  (testing "disabled-present? false → disabled? false"
    (is (= false (:disabled? (model/normalize {:disabled-present? false})))))

  (testing "readonly-present? true → readonly? true"
    (is (= true (:readonly? (model/normalize {:readonly-present? true})))))

  (testing "required-present? true → required? true"
    (is (= true (:required? (model/normalize {:required-present? true}))))))

(deftest normalize-passthrough-test
  (testing "name passes through"
    (is (= "email" (:name (model/normalize {:name-raw "email"})))))

  (testing "value passes through"
    (is (= "hello" (:value (model/normalize {:value-raw "hello"})))))

  (testing "placeholder passes through"
    (is (= "Enter email" (:placeholder (model/normalize {:placeholder-raw "Enter email"})))))

  (testing "autocomplete passes through"
    (is (= "email" (:autocomplete (model/normalize {:autocomplete-raw "email"}))))))
