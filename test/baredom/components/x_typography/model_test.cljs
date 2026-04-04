(ns baredom.components.x-typography.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-typography.model :as model]))

;; ── parse-variant ───────────────────────────────────────────────────────────
(deftest parse-variant-known-values-test
  (is (= "h1"        (model/parse-variant "h1")))
  (is (= "h2"        (model/parse-variant "h2")))
  (is (= "h3"        (model/parse-variant "h3")))
  (is (= "h4"        (model/parse-variant "h4")))
  (is (= "h5"        (model/parse-variant "h5")))
  (is (= "h6"        (model/parse-variant "h6")))
  (is (= "subtitle1" (model/parse-variant "subtitle1")))
  (is (= "subtitle2" (model/parse-variant "subtitle2")))
  (is (= "body1"     (model/parse-variant "body1")))
  (is (= "body2"     (model/parse-variant "body2")))
  (is (= "caption"   (model/parse-variant "caption")))
  (is (= "overline"  (model/parse-variant "overline")))
  (is (= "blockquote" (model/parse-variant "blockquote")))
  (is (= "code"      (model/parse-variant "code")))
  (is (= "kbd"       (model/parse-variant "kbd")))
  (is (= "small"     (model/parse-variant "small"))))

(deftest parse-variant-case-insensitive-test
  (is (= "h1"        (model/parse-variant "H1")))
  (is (= "body1"     (model/parse-variant "Body1")))
  (is (= "blockquote" (model/parse-variant "BLOCKQUOTE"))))

(deftest parse-variant-unknown-falls-back-test
  (is (= "body1" (model/parse-variant nil)))
  (is (= "body1" (model/parse-variant "")))
  (is (= "body1" (model/parse-variant "bad"))))

;; ── parse-align ─────────────────────────────────────────────────────────────
(deftest parse-align-known-values-test
  (is (= "left"    (model/parse-align "left")))
  (is (= "center"  (model/parse-align "center")))
  (is (= "right"   (model/parse-align "right")))
  (is (= "justify" (model/parse-align "justify"))))

(deftest parse-align-case-insensitive-test
  (is (= "center" (model/parse-align "CENTER")))
  (is (= "right"  (model/parse-align "Right"))))

(deftest parse-align-unknown-falls-back-test
  (is (= "left" (model/parse-align nil)))
  (is (= "left" (model/parse-align "")))
  (is (= "left" (model/parse-align "bad"))))

;; ── parse-truncate ──────────────────────────────────────────────────────────
(deftest parse-truncate-test
  (testing "absent (nil) -> false"
    (is (false? (model/parse-truncate nil))))
  (testing "present (empty string) -> true"
    (is (true? (model/parse-truncate ""))))
  (testing "present (any value) -> true"
    (is (true? (model/parse-truncate "truncate")))))

;; ── parse-line-clamp ────────────────────────────────────────────────────────
(deftest parse-line-clamp-valid-test
  (is (= 3 (model/parse-line-clamp "3")))
  (is (= 1 (model/parse-line-clamp "1"))))

(deftest parse-line-clamp-whitespace-test
  (is (= 3 (model/parse-line-clamp " 3 ")))
  (is (= 5 (model/parse-line-clamp "  5"))))

(deftest parse-line-clamp-float-test
  (is (= 3 (model/parse-line-clamp "3.7")))
  (is (= 2 (model/parse-line-clamp "2.1"))))

(deftest parse-line-clamp-invalid-test
  (is (nil? (model/parse-line-clamp nil)))
  (is (nil? (model/parse-line-clamp "")))
  (is (nil? (model/parse-line-clamp "0")))
  (is (nil? (model/parse-line-clamp "-1")))
  (is (nil? (model/parse-line-clamp "-5")))
  (is (nil? (model/parse-line-clamp "abc"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "body1" (:variant m)))
    (is (= "left"  (:align m)))
    (is (false?    (:truncate? m)))
    (is (nil?      (:line-clamp m)))))

(deftest normalize-variant-propagates-test
  (is (= "h1" (:variant (model/normalize {:variant-raw "h1"})))))

(deftest normalize-align-propagates-test
  (is (= "center" (:align (model/normalize {:align-raw "center"})))))

(deftest normalize-truncate-propagates-test
  (is (true? (:truncate? (model/normalize {:truncate-raw ""})))))

(deftest normalize-line-clamp-propagates-test
  (is (= 3 (:line-clamp (model/normalize {:line-clamp-raw "3"})))))
