(ns baredom.components.x-avatar.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-avatar.model :as model]))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (is (= "xs" (model/parse-size "xs")))
  (is (= "sm" (model/parse-size "sm")))
  (is (= "md" (model/parse-size "md")))
  (is (= "lg" (model/parse-size "lg")))
  (is (= "xl" (model/parse-size "xl"))))

(deftest parse-size-defaults-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "")))
  (is (= "md" (model/parse-size "huge"))))

(deftest parse-size-case-insensitive-test
  (is (= "lg" (model/parse-size "LG")))
  (is (= "xl" (model/parse-size "  XL  "))))

;; ── parse-shape ───────────────────────────────────────────────────────────
(deftest parse-shape-valid-test
  (is (= "circle"  (model/parse-shape "circle")))
  (is (= "square"  (model/parse-shape "square")))
  (is (= "rounded" (model/parse-shape "rounded"))))

(deftest parse-shape-defaults-test
  (is (= "circle" (model/parse-shape nil)))
  (is (= "circle" (model/parse-shape "oval"))))

;; ── parse-variant ─────────────────────────────────────────────────────────
(deftest parse-variant-valid-test
  (is (= "neutral" (model/parse-variant "neutral")))
  (is (= "brand"   (model/parse-variant "brand")))
  (is (= "subtle"  (model/parse-variant "subtle"))))

(deftest parse-variant-defaults-test
  (is (= "neutral" (model/parse-variant nil)))
  (is (= "neutral" (model/parse-variant "primary"))))

;; ── parse-status ──────────────────────────────────────────────────────────
(deftest parse-status-valid-test
  (is (= "online"  (model/parse-status "online")))
  (is (= "offline" (model/parse-status "offline")))
  (is (= "busy"    (model/parse-status "busy")))
  (is (= "away"    (model/parse-status "away"))))

(deftest parse-status-nil-for-absent-test
  (is (nil? (model/parse-status nil)))
  (is (nil? (model/parse-status "")))
  (is (nil? (model/parse-status "active"))))

(deftest parse-status-case-insensitive-test
  (is (= "online" (model/parse-status "ONLINE")))
  (is (= "busy"   (model/parse-status "  Busy  "))))

;; ── normalize-text ────────────────────────────────────────────────────────
(deftest normalize-text-test
  (is (nil? (model/normalize-text nil)))
  (is (nil? (model/normalize-text "")))
  (is (nil? (model/normalize-text "   ")))
  (is (= "hello"       (model/normalize-text "hello")))
  (is (= "hello world" (model/normalize-text "  hello world  "))))

;; ── normalize-initials ────────────────────────────────────────────────────
(deftest normalize-initials-test
  (is (nil? (model/normalize-initials nil)))
  (is (nil? (model/normalize-initials "")))
  (is (= "AB"  (model/normalize-initials "AB")))
  (is (= "ABC" (model/normalize-initials "ABC")))
  (is (= "ABC" (model/normalize-initials "ABCD")))  ; truncated to 3
  (is (= "A"   (model/normalize-initials "A"))))

;; ── derive-initials ───────────────────────────────────────────────────────
(deftest derive-initials-nil-test
  (is (nil? (model/derive-initials nil)))
  (is (nil? (model/derive-initials "")))
  (is (nil? (model/derive-initials "   "))))

(deftest derive-initials-single-word-test
  (is (= "A" (model/derive-initials "Alice")))
  (is (= "B" (model/derive-initials "bob"))))

(deftest derive-initials-two-words-test
  (is (= "AB" (model/derive-initials "Alice Bob")))
  (is (= "JD" (model/derive-initials "john doe"))))

(deftest derive-initials-many-words-uses-first-two-test
  (is (= "AB" (model/derive-initials "Alice Bob Carol"))))

(deftest derive-initials-extra-whitespace-test
  (is (= "AB" (model/derive-initials "Alice   Bob"))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil?        (:src m)))
    (is (nil?        (:alt m)))
    (is (nil?        (:name m)))
    (is (nil?        (:initials m)))
    (is (nil?        (:derived m)))
    (is (= "md"      (:size m)))
    (is (= "circle"  (:shape m)))
    (is (= "neutral" (:variant m)))
    (is (nil?        (:status m)))
    (is (false?      (:disabled m)))))

(deftest normalize-explicit-initials-skip-derived-test
  (let [m (model/normalize {:initials-raw "ZZ" :name-raw "Alice Bob"})]
    (is (= "ZZ" (:initials m)))
    (is (nil?   (:derived m)))))  ; explicit initials → no derivation

(deftest normalize-derives-when-no-initials-test
  (let [m (model/normalize {:name-raw "Alice Bob"})]
    (is (nil?   (:initials m)))
    (is (= "AB" (:derived m)))))

(deftest normalize-disabled-test
  (is (true?  (:disabled (model/normalize {:disabled-present? true}))))
  (is (false? (:disabled (model/normalize {:disabled-present? false})))))

;; ── display-text ──────────────────────────────────────────────────────────
(deftest display-text-test
  (is (= "JD" (model/display-text {:initials "JD" :derived "AB"})))  ; explicit wins
  (is (= "AB" (model/display-text {:initials nil  :derived "AB"})))  ; fallback to derived
  (is (nil?   (model/display-text {:initials nil  :derived nil}))))   ; no text → fallback glyph

;; ── label ─────────────────────────────────────────────────────────────────
(deftest label-test
  (is (= "photo" (model/label {:alt "photo" :name "Alice"})))  ; alt wins
  (is (= "Alice" (model/label {:alt nil     :name "Alice"})))  ; fallback to name
  (is (nil?      (model/label {:alt nil     :name nil}))))     ; decorative
