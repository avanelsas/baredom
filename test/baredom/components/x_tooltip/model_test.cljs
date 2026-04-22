(ns baredom.components.x-tooltip.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-tooltip.model :as model]))

;; ── normalize-placement ─────────────────────────────────────────────────────
(deftest normalize-placement-valid-test
  (is (= "top"    (model/normalize-placement "top")))
  (is (= "bottom" (model/normalize-placement "bottom")))
  (is (= "left"   (model/normalize-placement "left")))
  (is (= "right"  (model/normalize-placement "right"))))

(deftest normalize-placement-invalid-test
  (is (= "top" (model/normalize-placement nil)))
  (is (= "top" (model/normalize-placement "")))
  (is (= "top" (model/normalize-placement "center")))
  (is (= "top" (model/normalize-placement "top-start"))))

;; ── parse-delay ─────────────────────────────────────────────────────────────
(deftest parse-delay-valid-test
  (is (= 0    (model/parse-delay "0")))
  (is (= 200  (model/parse-delay "200")))
  (is (= 400  (model/parse-delay "400")))
  (is (= 5000 (model/parse-delay "5000"))))

(deftest parse-delay-clamps-negative-test
  (is (= 0 (model/parse-delay "-100"))))

(deftest parse-delay-clamps-over-max-test
  (is (= 5000 (model/parse-delay "9999"))))

(deftest parse-delay-fallback-test
  (is (= 400 (model/parse-delay nil)))
  (is (= 400 (model/parse-delay "abc")))
  (is (= 400 (model/parse-delay ""))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= ""    (:text m)))
    (is (= "top" (:placement m)))
    (is (= 400   (:delay m)))
    (is (false?  (:disabled? m)))
    (is (false?  (:open? m)))))

(deftest normalize-text-test
  (is (= "Save" (:text (model/normalize {:text-raw "Save"})))))

(deftest normalize-placement-test
  (is (= "bottom" (:placement (model/normalize {:placement-raw "bottom"}))))
  (is (= "top"    (:placement (model/normalize {:placement-raw "invalid"})))))

(deftest normalize-delay-test
  (is (= 200 (:delay (model/normalize {:delay-raw "200"})))))

(deftest normalize-disabled-test
  (is (true? (:disabled? (model/normalize {:disabled-present? true})))))

(deftest normalize-open-test
  (is (true? (:open? (model/normalize {:open-present? true})))))
