(ns baredom.components.x-search-field.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-search-field.model :as model]))

;; ---------------------------------------------------------------------------
;; normalize-autocomplete
;; ---------------------------------------------------------------------------

(deftest autocomplete-valid-on-test
  (is (= "on" (model/normalize-autocomplete "on"))))

(deftest autocomplete-valid-off-test
  (is (= "off" (model/normalize-autocomplete "off"))))

(deftest autocomplete-invalid-defaults-off-test
  (is (= "off" (model/normalize-autocomplete "yes"))))

(deftest autocomplete-nil-defaults-off-test
  (is (= "off" (model/normalize-autocomplete nil))))

(deftest autocomplete-empty-defaults-off-test
  (is (= "off" (model/normalize-autocomplete ""))))

;; ---------------------------------------------------------------------------
;; normalize — boolean flags
;; ---------------------------------------------------------------------------

(deftest normalize-disabled-present-test
  (let [m (model/normalize {:disabled-present? true})]
    (is (= true (:disabled? m)))))

(deftest normalize-disabled-absent-test
  (let [m (model/normalize {:disabled-present? false})]
    (is (= false (:disabled? m)))))

(deftest normalize-required-present-test
  (let [m (model/normalize {:required-present? true})]
    (is (= true (:required? m)))))

(deftest normalize-required-absent-test
  (let [m (model/normalize {:required-present? nil})]
    (is (= false (:required? m)))))

;; ---------------------------------------------------------------------------
;; normalize — empty defaults
;; ---------------------------------------------------------------------------

(deftest normalize-empty-defaults-test
  (let [m (model/normalize {})]
    (is (= "" (:name m)))
    (is (= "" (:value m)))
    (is (= "" (:placeholder m)))
    (is (= "" (:label m)))
    (is (= false (:disabled? m)))
    (is (= false (:required? m)))
    (is (= "off" (:autocomplete m)))))

;; ---------------------------------------------------------------------------
;; normalize — string fields pass through
;; ---------------------------------------------------------------------------

(deftest normalize-string-fields-test
  (let [m (model/normalize {:name-raw        "q"
                             :value-raw       "hello"
                             :placeholder-raw "Search…"
                             :label-raw       "Site search"})]
    (is (= "q"           (:name m)))
    (is (= "hello"       (:value m)))
    (is (= "Search…"     (:placeholder m)))
    (is (= "Site search" (:label m)))))
