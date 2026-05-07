(ns baredom.components.x-otp-input.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-otp-input.model :as model]))

;; ---------------------------------------------------------------------------
;; parse-length
;; ---------------------------------------------------------------------------

(deftest parse-length-default-test
  (is (= 6 (model/parse-length nil))
      "nil falls back to default-length")
  (is (= 6 (model/parse-length ""))
      "empty string falls back to default-length")
  (is (= 6 (model/parse-length "abc"))
      "non-numeric falls back to default-length"))

(deftest parse-length-numeric-test
  (is (= 4  (model/parse-length "4")))
  (is (= 8  (model/parse-length "8")))
  (is (= 12 (model/parse-length "12"))))

(deftest parse-length-clamp-test
  (is (= model/min-length     (model/parse-length "0"))
      "zero clamps up to min-length")
  (is (= model/min-length     (model/parse-length "-3"))
      "negative clamps up to min-length")
  (is (= model/max-length-cap (model/parse-length "99"))
      "values above cap clamp to max-length-cap"))

;; ---------------------------------------------------------------------------
;; parse-type
;; ---------------------------------------------------------------------------

(deftest parse-type-default-test
  (is (= "numeric" (model/parse-type nil)))
  (is (= "numeric" (model/parse-type "")))
  (is (= "numeric" (model/parse-type "wat"))))

(deftest parse-type-valid-test
  (is (= "numeric"      (model/parse-type "numeric")))
  (is (= "alpha"        (model/parse-type "alpha")))
  (is (= "alphanumeric" (model/parse-type "alphanumeric"))))

(deftest parse-type-case-insensitive-test
  (is (= "numeric" (model/parse-type "Numeric")))
  (is (= "alpha"   (model/parse-type "  ALPHA  "))))

;; ---------------------------------------------------------------------------
;; filter-chars
;; ---------------------------------------------------------------------------

(deftest filter-chars-numeric-test
  (is (= "1234" (model/filter-chars "numeric" "1a2b3c4")))
  (is (= ""     (model/filter-chars "numeric" "abcdef")))
  (is (= "0987" (model/filter-chars "numeric" " 0 9 8 7 "))))

(deftest filter-chars-alpha-test
  (is (= "abXY"  (model/filter-chars "alpha" "ab12XY!@")))
  (is (= ""      (model/filter-chars "alpha" "1234"))))

(deftest filter-chars-alphanumeric-test
  (is (= "ab12XY"  (model/filter-chars "alphanumeric" "ab12XY!@")))
  (is (= "abc"     (model/filter-chars "alphanumeric" "a-b-c"))))

(deftest filter-chars-nil-test
  (is (= "" (model/filter-chars "numeric" nil))))

;; ---------------------------------------------------------------------------
;; truncate
;; ---------------------------------------------------------------------------

(deftest truncate-test
  (is (= "12345"   (model/truncate "1234567" 5)))
  (is (= "abc"     (model/truncate "abc" 5)))
  (is (= ""        (model/truncate "" 6)))
  (is (= ""        (model/truncate nil 6))))

;; ---------------------------------------------------------------------------
;; normalize-value
;; ---------------------------------------------------------------------------

(deftest normalize-value-test
  (is (= "1234"   (model/normalize-value "numeric" 6 "1234"))
      "passes valid numeric through")
  (is (= "1234"   (model/normalize-value "numeric" 6 "1a2b3c4d"))
      "filters non-numeric")
  (is (= "123456" (model/normalize-value "numeric" 6 "123456789"))
      "truncates to length"))

;; ---------------------------------------------------------------------------
;; complete?
;; ---------------------------------------------------------------------------

(deftest complete-test
  (is (= true  (model/complete? "123456" 6)))
  (is (= false (model/complete? "12345"  6)))
  (is (= false (model/complete? ""       6)))
  (is (= true  (model/complete? "" 0))))

;; ---------------------------------------------------------------------------
;; placeholder-char
;; ---------------------------------------------------------------------------

(deftest placeholder-char-test
  (is (= "" (model/placeholder-char nil)))
  (is (= "" (model/placeholder-char "")))
  (is (= "0" (model/placeholder-char "0")))
  (is (= "·" (model/placeholder-char "··"))
      "uses only the first character"))

;; ---------------------------------------------------------------------------
;; slot-aria-label
;; ---------------------------------------------------------------------------

(deftest slot-aria-label-numeric-test
  (is (= "Digit 1 of 6" (model/slot-aria-label "numeric" 0 6)))
  (is (= "Digit 6 of 6" (model/slot-aria-label "numeric" 5 6))))

(deftest slot-aria-label-alpha-test
  (is (= "Character 1 of 4" (model/slot-aria-label "alpha" 0 4))))

;; ---------------------------------------------------------------------------
;; slot-inputmode
;; ---------------------------------------------------------------------------

(deftest slot-inputmode-test
  (is (= "numeric" (model/slot-inputmode "numeric")))
  (is (= "text"    (model/slot-inputmode "alpha")))
  (is (= "text"    (model/slot-inputmode "alphanumeric"))))

;; ---------------------------------------------------------------------------
;; normalize — full view-model
;; ---------------------------------------------------------------------------

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= ""        (:name m)))
    (is (= ""        (:value m)))
    (is (= 6         (:length m)))
    (is (= "numeric" (:type m)))
    (is (= false     (:mask? m)))
    (is (= false     (:disabled? m)))
    (is (= false     (:readonly? m)))
    (is (= false     (:required? m)))
    (is (= false     (:autofocus? m)))
    (is (= ""        (:label m)))
    (is (= ""        (:placeholder m)))
    (is (= ""        (:error m)))
    (is (= false     (:has-error? m)))
    (is (= false     (:complete? m)))))

(deftest normalize-value-filtering-test
  (let [m (model/normalize {:value-raw "ab12cd34" :type-raw "numeric" :length-raw "4"})]
    (is (= "1234" (:value m)))
    (is (= 4      (:length m)))
    (is (= true   (:complete? m)))))

(deftest normalize-truncation-test
  (let [m (model/normalize {:value-raw "1234567890" :length-raw "6"})]
    (is (= "123456" (:value m)))
    (is (= true     (:complete? m)))))

(deftest normalize-error-test
  (let [with-msg (model/normalize {:error-raw "Invalid code"})
        empty    (model/normalize {:error-raw ""})
        absent   (model/normalize {:error-raw nil})]
    (is (= true  (:has-error? with-msg)))
    (is (= "Invalid code" (:error with-msg)))
    (is (= false (:has-error? empty)))
    (is (= false (:has-error? absent)))))

(deftest normalize-flags-test
  (let [m (model/normalize {:disabled-present?  true
                             :required-present?  true
                             :readonly-present?  true
                             :mask-present?      true
                             :autofocus-present? true})]
    (is (= true (:disabled? m)))
    (is (= true (:required? m)))
    (is (= true (:readonly? m)))
    (is (= true (:mask? m)))
    (is (= true (:autofocus? m)))))

(deftest normalize-incomplete-with-required-test
  (let [m (model/normalize {:value-raw "12" :length-raw "6" :required-present? true})]
    (is (= "12"  (:value m)))
    (is (= 6     (:length m)))
    (is (= false (:complete? m)))
    (is (= true  (:required? m)))))
