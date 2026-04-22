(ns baredom.components.x-skeleton-group.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-skeleton-group.model :as model]))

;; ── normalize-preset ────────────────────────────────────────────────────────
(deftest normalize-preset-valid-test
  (is (= "card"      (model/normalize-preset "card")))
  (is (= "list-item" (model/normalize-preset "list-item")))
  (is (= "paragraph" (model/normalize-preset "paragraph")))
  (is (= "table-row" (model/normalize-preset "table-row")))
  (is (= "profile"   (model/normalize-preset "profile"))))

(deftest normalize-preset-invalid-test
  (is (nil? (model/normalize-preset nil)))
  (is (nil? (model/normalize-preset "")))
  (is (nil? (model/normalize-preset "unknown"))))

;; ── normalize-animation ─────────────────────────────────────────────────────
(deftest normalize-animation-valid-test
  (is (= "pulse" (model/normalize-animation "pulse")))
  (is (= "wave"  (model/normalize-animation "wave")))
  (is (= "none"  (model/normalize-animation "none"))))

(deftest normalize-animation-invalid-test
  (is (= "pulse" (model/normalize-animation nil)))
  (is (= "pulse" (model/normalize-animation "")))
  (is (= "pulse" (model/normalize-animation "bounce"))))

;; ── parse-count ─────────────────────────────────────────────────────────────
(deftest parse-count-valid-test
  (is (= 1  (model/parse-count "1")))
  (is (= 5  (model/parse-count "5")))
  (is (= 20 (model/parse-count "20"))))

(deftest parse-count-clamps-test
  (is (= 1  (model/parse-count "0")))
  (is (= 1  (model/parse-count "-3")))
  (is (= 20 (model/parse-count "99"))))

(deftest parse-count-fallback-test
  (is (= 1 (model/parse-count nil)))
  (is (= 1 (model/parse-count "abc"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil?      (:preset m)))
    (is (= "pulse" (:animation m)))
    (is (= 1       (:count m)))))

(deftest normalize-full-test
  (let [m (model/normalize {:preset-raw "card"
                            :animation-raw "wave"
                            :count-raw "3"})]
    (is (= "card" (:preset m)))
    (is (= "wave" (:animation m)))
    (is (= 3      (:count m)))))

;; ── presets data ────────────────────────────────────────────────────────────
(deftest presets-all-exist-test
  (is (some? (get model/presets "card")))
  (is (some? (get model/presets "list-item")))
  (is (some? (get model/presets "paragraph")))
  (is (some? (get model/presets "table-row")))
  (is (some? (get model/presets "profile"))))

(deftest presets-have-required-keys-test
  (doseq [[_ preset] model/presets]
    (is (some? (:layout preset)))
    (is (some? (:gap preset)))
    (is (seq (:items preset)))))
