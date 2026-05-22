(ns baredom.components.x-rating.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-rating.model :as model]))

;; ---------------------------------------------------------------------------
;; normalize-number / normalize-max
;; ---------------------------------------------------------------------------

(deftest normalize-number-test
  (is (= 4.5 (model/normalize-number "4.5" 0)))
  (is (= 3 (model/normalize-number "3" 0)))
  (testing "NaN string returns default"
    (is (= 7 (model/normalize-number "abc" 7))))
  (testing "nil returns default"
    (is (= 0 (model/normalize-number nil 0)))))

(deftest normalize-max-test
  (is (= 5 (model/normalize-max "5")))
  (is (= model/default-max (model/normalize-max nil)))
  (testing "fractional values round to a whole number"
    (is (= 4 (model/normalize-max "3.7"))))
  (testing "zero and negative values floor to 1"
    (is (= 1 (model/normalize-max "0")))
    (is (= 1 (model/normalize-max "-2"))))
  (testing "an extreme or non-finite value is capped"
    (is (= model/max-stars-cap (model/normalize-max "100000")))
    (is (= model/max-stars-cap (model/normalize-max "1e999")))
    (is (= model/max-stars-cap (model/normalize-max "Infinity")))))

;; ---------------------------------------------------------------------------
;; normalize-precision / normalize-shape / normalize-size
;; ---------------------------------------------------------------------------

(deftest normalize-precision-test
  (is (= "full" (model/normalize-precision "full")))
  (is (= "half" (model/normalize-precision "half")))
  (is (= model/default-precision (model/normalize-precision nil)))
  (is (= model/default-precision (model/normalize-precision "quarter"))))

(deftest normalize-shape-test
  (is (= "star" (model/normalize-shape "star")))
  (is (= "heart" (model/normalize-shape "heart")))
  (is (= model/default-shape (model/normalize-shape nil)))
  (is (= model/default-shape (model/normalize-shape "diamond"))))

(deftest normalize-size-test
  (is (= "sm" (model/normalize-size "sm")))
  (is (= "md" (model/normalize-size "md")))
  (is (= "lg" (model/normalize-size "lg")))
  (is (= model/default-size (model/normalize-size nil)))
  (is (= model/default-size (model/normalize-size "xl"))))

;; ---------------------------------------------------------------------------
;; precision-step
;; ---------------------------------------------------------------------------

(deftest precision-step-test
  (is (= 1 (model/precision-step "full")))
  (is (= 0.5 (model/precision-step "half"))))

;; ---------------------------------------------------------------------------
;; clamp / snap-to-precision / clamp-value
;; ---------------------------------------------------------------------------

(deftest clamp-test
  (is (= 3 (model/clamp 3 0 5)))
  (is (= 0 (model/clamp -1 0 5)))
  (is (= 5 (model/clamp 9 0 5)))
  (testing "degenerate hi < lo returns lo"
    (is (= 5 (model/clamp 3 5 0)))))

(deftest snap-to-precision-test
  (testing "step 1 rounds to the nearest integer"
    (is (= 3 (model/snap-to-precision 3.4 1)))
    (is (= 4 (model/snap-to-precision 3.6 1))))
  (testing "step 0.5 rounds to the nearest half"
    (is (= 3.0 (model/snap-to-precision 3.2 0.5)))
    (is (= 3.5 (model/snap-to-precision 3.4 0.5)))
    (is (= 3.5 (model/snap-to-precision 3.7 0.5))))
  (testing "nil step leaves the value unchanged"
    (is (= 3.7 (model/snap-to-precision 3.7 nil))))
  (testing "suppresses floating-point noise"
    (is (= 0.3 (model/snap-to-precision 0.3 0.1)))))

(deftest clamp-value-test
  (is (= 3 (model/clamp-value 3 5)))
  (is (= 5 (model/clamp-value 9 5)))
  (is (= 0 (model/clamp-value -2 5))))

;; ---------------------------------------------------------------------------
;; pointer-star-index / pointer->value
;; ---------------------------------------------------------------------------

(deftest pointer-star-index-test
  (is (= 0 (model/pointer-star-index 5 0 100 5)))
  (is (= 2 (model/pointer-star-index 50 0 100 5)))
  (is (= 4 (model/pointer-star-index 95 0 100 5)))
  (testing "below the row clamps to the first star"
    (is (= 0 (model/pointer-star-index -10 0 100 5))))
  (testing "beyond the row clamps to the last star"
    (is (= 4 (model/pointer-star-index 250 0 100 5))))
  (testing "zero width returns 0"
    (is (= 0 (model/pointer-star-index 50 0 0 5)))))

(deftest pointer->value-test
  (testing "full precision — anywhere in a star picks the whole value"
    (is (= 3 (model/pointer->value 50 0 100 5 1)))
    (is (= 3 (model/pointer->value 45 0 100 5 1))))
  (testing "half precision — left half picks the half value"
    (is (= 2.5 (model/pointer->value 45 0 100 5 0.5)))
    (is (= 3 (model/pointer->value 55 0 100 5 0.5))))
  (testing "half precision — left half of the first star is 0.5"
    (is (= 0.5 (model/pointer->value 5 0 100 5 0.5))))
  (testing "the last star resolves to the star count"
    (is (= 5 (model/pointer->value 95 0 100 5 1)))))

;; ---------------------------------------------------------------------------
;; star-fill / fill-states
;; ---------------------------------------------------------------------------

(deftest star-fill-test
  (testing "value 3 fills the first three stars"
    (is (= :full (model/star-fill 0 3)))
    (is (= :full (model/star-fill 2 3)))
    (is (= :empty (model/star-fill 3 3))))
  (testing "value 3.5 half-fills the fourth star"
    (is (= :half (model/star-fill 3 3.5))))
  (testing "value 0 leaves every star empty"
    (is (= :empty (model/star-fill 0 0)))))

(deftest fill-states-test
  (is (= [:full :full :full :empty :empty] (model/fill-states 3 5)))
  (is (= [:full :full :full :half :empty] (model/fill-states 3.5 5)))
  (is (= [:empty :empty :empty :empty :empty] (model/fill-states 0 5))))

;; ---------------------------------------------------------------------------
;; key-target
;; ---------------------------------------------------------------------------

(deftest key-target-test
  (testing "arrow keys move by one precision step"
    (is (= 4 (model/key-target "ArrowRight" 3 5 1 false)))
    (is (= 2 (model/key-target "ArrowLeft" 3 5 1 false)))
    (is (= 3.5 (model/key-target "ArrowUp" 3 5 0.5 false)))
    (is (= 1.5 (model/key-target "ArrowDown" 2 5 0.5 false))))
  (testing "Home / End jump to the bounds"
    (is (= 0 (model/key-target "Home" 3 5 1 false)))
    (is (= 5 (model/key-target "End" 3 5 1 false))))
  (testing "Delete / Backspace clear only when clearing is allowed"
    (is (= 0 (model/key-target "Delete" 3 5 1 true)))
    (is (= 0 (model/key-target "Backspace" 3 5 1 true)))
    (is (= 3 (model/key-target "Delete" 3 5 1 false))))
  (testing "unknown key leaves the value unchanged"
    (is (= 3 (model/key-target "Tab" 3 5 1 false)))))

;; ---------------------------------------------------------------------------
;; aria-valuetext
;; ---------------------------------------------------------------------------

(deftest aria-valuetext-test
  (is (= "No rating" (model/aria-valuetext 0 5 "star")))
  (is (= "1 out of 5 stars" (model/aria-valuetext 1 5 "star")))
  (is (= "3 out of 5 stars" (model/aria-valuetext 3 5 "star")))
  (is (= "3.5 out of 5 stars" (model/aria-valuetext 3.5 5 "star")))
  (testing "the icon noun follows the shape"
    (is (= "4 out of 5 hearts" (model/aria-valuetext 4 5 "heart")))))

;; ---------------------------------------------------------------------------
;; interactable?
;; ---------------------------------------------------------------------------

(deftest interactable?-test
  (is (true? (model/interactable? {})))
  (is (false? (model/interactable? {:disabled? true})))
  (is (false? (model/interactable? {:readonly? true}))))

;; ---------------------------------------------------------------------------
;; make-detail / make-change-request-detail
;; ---------------------------------------------------------------------------

(deftest make-detail-test
  (is (= {:value 3 :max 5} (model/make-detail 3 5)))
  (is (= {:value nil :max 5} (model/make-detail nil 5))))

(deftest make-change-request-detail-test
  (is (= {:value 4 :previousValue 3 :max 5}
         (model/make-change-request-detail 4 3 5))))

;; ---------------------------------------------------------------------------
;; normalize
;; ---------------------------------------------------------------------------

(deftest normalize-defaults-test
  (testing "empty input produces an unrated, five-star widget"
    (let [m (model/normalize {})]
      (is (= 0 (:value m)))
      (is (= model/default-max (:max m)))
      (is (= model/default-precision (:precision m)))
      (is (= 1 (:precision-step m)))
      (is (= model/default-shape (:shape m)))
      (is (= model/default-size (:size m)))
      (is (= false (:allow-clear? m)))
      (is (= false (:disabled? m)))
      (is (= false (:readonly? m)))
      (is (nil? (:name m)))
      (is (nil? (:label m)))
      (is (= [:empty :empty :empty :empty :empty] (:fill-states m)))
      (is (= "No rating" (:value-text m))))))

(deftest normalize-clamp-test
  (testing "value above max is clamped to max"
    (is (= 5 (:value (model/normalize {:value "9" :max "5"})))))
  (testing "negative value is clamped to 0"
    (is (= 0 (:value (model/normalize {:value "-2"}))))))

(deftest normalize-snap-test
  (testing "half precision keeps a 0.5 value"
    (is (= 2.5 (:value (model/normalize {:value "2.5" :precision "half"})))))
  (testing "half precision snaps to the nearest half"
    (is (= 3.5 (:value (model/normalize {:value "3.4" :precision "half"})))))
  (testing "full precision snaps a fractional value to a whole star"
    (is (= 3 (:value (model/normalize {:value "3.4"}))))))

(deftest normalize-booleans-test
  (let [m (model/normalize {:allow-clear true :disabled true :readonly true})]
    (is (= true (:allow-clear? m)))
    (is (= true (:disabled? m)))
    (is (= true (:readonly? m)))))

(deftest normalize-label-test
  (testing "empty label becomes nil"
    (is (nil? (:label (model/normalize {:label ""})))))
  (testing "non-empty label is preserved"
    (is (= "Rate us" (:label (model/normalize {:label "Rate us"}))))))

(deftest normalize-shape-precision-test
  (is (= "heart" (:shape (model/normalize {:shape "heart"}))))
  (is (= "star" (:shape (model/normalize {:shape "bogus"}))))
  (is (= 0.5 (:precision-step (model/normalize {:precision "half"}))))
  (testing "value-text noun follows the shape"
    (is (= "2 out of 5 hearts"
           (:value-text (model/normalize {:value "2" :shape "heart"}))))))

(deftest normalize-fill-states-test
  (let [m (model/normalize {:value "3" :max "5"})]
    (is (= [:full :full :full :empty :empty] (:fill-states m)))
    (is (= "3 out of 5 stars" (:value-text m)))))

(deftest normalize-aria-test
  (let [m (model/normalize {:aria-label "Rating"
                            :aria-labelledby "lbl-1"
                            :aria-describedby "hint-1"})]
    (is (= "Rating" (:aria-label m)))
    (is (= "lbl-1" (:aria-labelledby m)))
    (is (= "hint-1" (:aria-describedby m))))
  (testing "empty aria strings become nil"
    (is (nil? (:aria-label (model/normalize {:aria-label ""}))))))
