(ns baredom.components.x-stepper.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-stepper.model :as model]))

;; ── parse-orientation ────────────────────────────────────────────────────────
(deftest parse-orientation-known-test
  (is (= :horizontal (model/parse-orientation "horizontal")))
  (is (= :vertical   (model/parse-orientation "vertical"))))

(deftest parse-orientation-case-insensitive-test
  (is (= :horizontal (model/parse-orientation "HORIZONTAL")))
  (is (= :vertical   (model/parse-orientation "Vertical"))))

(deftest parse-orientation-unknown-falls-back-to-horizontal-test
  (is (= :horizontal (model/parse-orientation nil)))
  (is (= :horizontal (model/parse-orientation "")))
  (is (= :horizontal (model/parse-orientation "diagonal"))))

;; ── orientation->attr ────────────────────────────────────────────────────────
(deftest orientation-attr-roundtrip-test
  (is (= "horizontal" (model/orientation->attr :horizontal)))
  (is (= "vertical"   (model/orientation->attr :vertical)))
  (is (= "horizontal" (model/orientation->attr :unknown))))

;; ── parse-size ───────────────────────────────────────────────────────────────
(deftest parse-size-known-test
  (is (= :sm (model/parse-size "sm")))
  (is (= :md (model/parse-size "md")))
  (is (= :lg (model/parse-size "lg"))))

(deftest parse-size-case-insensitive-test
  (is (= :sm (model/parse-size "SM")))
  (is (= :lg (model/parse-size "LG"))))

(deftest parse-size-unknown-falls-back-to-md-test
  (is (= :md (model/parse-size nil)))
  (is (= :md (model/parse-size "")))
  (is (= :md (model/parse-size "xl"))))

;; ── size->attr ───────────────────────────────────────────────────────────────
(deftest size-attr-roundtrip-test
  (is (= "sm" (model/size->attr :sm)))
  (is (= "md" (model/size->attr :md)))
  (is (= "lg" (model/size->attr :lg)))
  (is (= "md" (model/size->attr :unknown))))

;; ── parse-current ────────────────────────────────────────────────────────────
(deftest parse-current-valid-test
  (is (= 0 (model/parse-current "0")))
  (is (= 2 (model/parse-current "2")))
  (is (= 9 (model/parse-current "9"))))

(deftest parse-current-invalid-falls-back-to-zero-test
  (is (= 0 (model/parse-current nil)))
  (is (= 0 (model/parse-current "")))
  (is (= 0 (model/parse-current "abc")))
  (is (= 0 (model/parse-current "-1"))))

;; ── clamp-current ────────────────────────────────────────────────────────────
(deftest clamp-current-test
  (is (= 0 (model/clamp-current 0 3)))
  (is (= 2 (model/clamp-current 2 3)))
  (is (= 2 (model/clamp-current 5 3)))
  (is (= 0 (model/clamp-current 0 0))))

;; ── parse-steps ──────────────────────────────────────────────────────────────
(deftest parse-steps-integer-string-test
  (let [steps (model/parse-steps "3")]
    (is (= 3 (count steps)))
    (is (= "Step 1" (:label (first steps))))
    (is (= "Step 3" (:label (last steps))))
    (is (nil? (:description (first steps))))))

(deftest parse-steps-json-array-test
  (let [steps (model/parse-steps "[{\"label\":\"One\"},{\"label\":\"Two\",\"description\":\"desc\"}]")]
    (is (= 2 (count steps)))
    (is (= "One" (:label (first steps))))
    (is (nil? (:description (first steps))))
    (is (= "Two" (:label (second steps))))
    (is (= "desc" (:description (second steps))))))

(deftest parse-steps-empty-test
  (is (= [] (model/parse-steps nil)))
  (is (= [] (model/parse-steps "")))
  (is (= [] (model/parse-steps "bad")))
  (is (= [] (model/parse-steps "0"))))

(deftest parse-steps-json-invalid-falls-back-test
  (is (= [] (model/parse-steps "[{bad json}"))))

(deftest parse-steps-non-array-json-falls-back-test
  (is (= [] (model/parse-steps "{\"a\":1}"))))

(deftest parse-steps-json-array-capped-at-200-test
  (let [big-json (js/JSON.stringify
                  (clj->js (mapv (fn [i] {:label (str "S" i)}) (range 250))))
        steps    (model/parse-steps big-json)]
    (is (= 200 (count steps)))
    (is (= "S0" (:label (first steps))))
    (is (= "S199" (:label (last steps))))))

;; ── step-state ───────────────────────────────────────────────────────────────
(deftest step-state-test
  (is (= :complete (model/step-state 0 2)))
  (is (= :complete (model/step-state 1 2)))
  (is (= :current  (model/step-state 2 2)))
  (is (= :upcoming (model/step-state 3 2)))
  (is (= :current  (model/step-state 0 0))))

;; ── state->attr ──────────────────────────────────────────────────────────────
(deftest state-attr-test
  (is (= "complete" (model/state->attr :complete)))
  (is (= "current"  (model/state->attr :current)))
  (is (= "upcoming" (model/state->attr :upcoming))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= [] (:steps m)))
    (is (= 0  (:current m)))
    (is (= :horizontal (:orientation m)))
    (is (= :md (:size m)))
    (is (false? (:disabled? m)))))

(deftest normalize-with-integer-steps-test
  (let [m (model/normalize {:steps-raw "3" :current-raw "1"})]
    (is (= 3 (count (:steps m))))
    (is (= 1 (:current m)))))

(deftest normalize-clamps-current-test
  (let [m (model/normalize {:steps-raw "3" :current-raw "99"})]
    (is (= 2 (:current m)))))

(deftest normalize-propagates-orientation-test
  (is (= :vertical (:orientation (model/normalize {:orientation-raw "vertical"})))))

(deftest normalize-propagates-size-test
  (is (= :lg (:size (model/normalize {:size-raw "lg"})))))

(deftest normalize-disabled-test
  (is (true? (:disabled? (model/normalize {:disabled? true}))))
  (is (false? (:disabled? (model/normalize {:disabled? false})))))

;; ── change-detail ────────────────────────────────────────────────────────────
(deftest change-detail-test
  (let [d (model/change-detail 1 3)]
    (is (= 1 (:from d)))
    (is (= 3 (:to d)))))
