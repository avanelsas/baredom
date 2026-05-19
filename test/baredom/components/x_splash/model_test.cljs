(ns baredom.components.x-splash.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-splash.model :as model]))

;; ── normalize-variant ────────────────────────────────────────────────────────
(deftest normalize-variant-known-values-test
  (is (= "default" (model/normalize-variant "default")))
  (is (= "branded" (model/normalize-variant "branded")))
  (is (= "minimal" (model/normalize-variant "minimal"))))

(deftest normalize-variant-case-insensitive-test
  (is (= "default" (model/normalize-variant "DEFAULT")))
  (is (= "branded" (model/normalize-variant "Branded"))))

(deftest normalize-variant-fallback-test
  (is (= "default" (model/normalize-variant nil)))
  (is (= "default" (model/normalize-variant "")))
  (is (= "default" (model/normalize-variant "bad"))))

;; ── normalize-overlay ────────────────────────────────────────────────────────
(deftest normalize-overlay-known-values-test
  (is (= "solid"       (model/normalize-overlay "solid")))
  (is (= "blur"        (model/normalize-overlay "blur")))
  (is (= "transparent" (model/normalize-overlay "transparent"))))

(deftest normalize-overlay-case-insensitive-test
  (is (= "solid" (model/normalize-overlay "SOLID")))
  (is (= "blur"  (model/normalize-overlay "Blur"))))

(deftest normalize-overlay-fallback-test
  (is (= "solid" (model/normalize-overlay nil)))
  (is (= "solid" (model/normalize-overlay "")))
  (is (= "solid" (model/normalize-overlay "bad"))))

;; ── parse-progress ───────────────────────────────────────────────────────────
(deftest parse-progress-valid-test
  (is (= 0   (model/parse-progress "0")))
  (is (= 50  (model/parse-progress "50")))
  (is (= 100 (model/parse-progress "100")))
  (is (= 33.5 (model/parse-progress "33.5"))))

(deftest parse-progress-clamps-test
  (is (= 0   (model/parse-progress "-10")))
  (is (= 100 (model/parse-progress "200"))))

(deftest parse-progress-invalid-test
  (is (nil? (model/parse-progress nil)))
  (is (nil? (model/parse-progress "abc")))
  (is (nil? (model/parse-progress ""))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false?  (:active? m)))
    (is (= "default" (:variant m)))
    (is (nil?    (:progress m)))
    (is (true?   (:spinner? m)))
    (is (= "solid" (:overlay m)))))

(deftest normalize-active-test
  (is (true? (:active? (model/normalize {:active-present? true})))))

(deftest normalize-variant-test
  (is (= "minimal" (:variant (model/normalize {:variant-raw "minimal"})))))

(deftest normalize-progress-test
  (is (= 75 (:progress (model/normalize {:progress-raw "75"})))))

(deftest normalize-spinner-false-test
  (is (false? (:spinner? (model/normalize {:spinner-attr "false"})))))

(deftest normalize-overlay-test
  (is (= "blur" (:overlay (model/normalize {:overlay-raw "blur"})))))
