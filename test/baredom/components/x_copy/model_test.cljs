(ns baredom.components.x-copy.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-copy.model :as model]))

;; ── parse-mode ────────────────────────────────────────────────────────────────
(deftest parse-mode-test
  (testing "html is preserved"
    (is (= "html" (model/parse-mode "html"))))
  (testing "text is preserved"
    (is (= "text" (model/parse-mode "text"))))
  (testing "nil defaults to text"
    (is (= "text" (model/parse-mode nil))))
  (testing "unknown defaults to text"
    (is (= "text" (model/parse-mode "richtext"))))
  (testing "empty defaults to text"
    (is (= "text" (model/parse-mode "")))))

;; ── parse-bool-default-true ───────────────────────────────────────────────────
(deftest parse-bool-default-true-test
  (testing "nil → true"
    (is (= true (model/parse-bool-default-true nil))))
  (testing "empty string → true"
    (is (= true (model/parse-bool-default-true ""))))
  (testing "\"true\" → true"
    (is (= true (model/parse-bool-default-true "true"))))
  (testing "\"false\" → false"
    (is (= false (model/parse-bool-default-true "false"))))
  (testing "\"0\" → false"
    (is (= false (model/parse-bool-default-true "0"))))
  (testing "any other string → true"
    (is (= true (model/parse-bool-default-true "yes")))))

;; ── parse-int-pos ─────────────────────────────────────────────────────────────
(deftest parse-int-pos-test
  (testing "valid positive integer"
    (is (= 500 (model/parse-int-pos "500" 1200))))
  (testing "zero → default"
    (is (= 1200 (model/parse-int-pos "0" 1200))))
  (testing "negative → default"
    (is (= 1200 (model/parse-int-pos "-100" 1200))))
  (testing "nil → default"
    (is (= 1200 (model/parse-int-pos nil 1200))))
  (testing "empty string → default"
    (is (= 1200 (model/parse-int-pos "" 1200))))
  (testing "non-numeric → default"
    (is (= 1200 (model/parse-int-pos "abc" 1200)))))

;; ── clamp ─────────────────────────────────────────────────────────────────────
(deftest clamp-test
  (testing "value within range"
    (is (= 500 (model/clamp 500 100 10000))))
  (testing "value below lo → lo"
    (is (= 100 (model/clamp 50 100 10000))))
  (testing "value above hi → hi"
    (is (= 10000 (model/clamp 99999 100 10000))))
  (testing "value at lo"
    (is (= 100 (model/clamp 100 100 10000))))
  (testing "value at hi"
    (is (= 10000 (model/clamp 10000 100 10000)))))

;; ── normalize ─────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (testing "text is nil by default"
      (is (nil? (:text m))))
    (testing "from is nil by default"
      (is (nil? (:from m))))
    (testing "mode defaults to text"
      (is (= "text" (:mode m))))
    (testing "disabled? false by default"
      (is (= false (:disabled? m))))
    (testing "show-tooltip? true by default"
      (is (= true (:show-tooltip? m))))
    (testing "tooltip-ms defaults to 1200"
      (is (= 1200 (:tooltip-ms m))))
    (testing "success-message defaults to Copied"
      (is (= "Copied" (:success-message m))))
    (testing "error-message defaults to Copy failed"
      (is (= "Copy failed" (:error-message m))))
    (testing "hotkey is nil by default"
      (is (nil? (:hotkey m))))))

(deftest normalize-text-trimming-test
  (let [m (model/normalize {:text-raw "  hello  "})]
    (is (= "hello" (:text m))))
  (let [m (model/normalize {:text-raw "   "})]
    (is (nil? (:text m)))))

(deftest normalize-tooltip-ms-clamping-test
  (testing "below minimum is clamped"
    (let [m (model/normalize {:tooltip-ms-raw "50"})]
      (is (= 100 (:tooltip-ms m)))))
  (testing "above maximum is clamped"
    (let [m (model/normalize {:tooltip-ms-raw "99999"})]
      (is (= 10000 (:tooltip-ms m)))))
  (testing "valid value within range"
    (let [m (model/normalize {:tooltip-ms-raw "2000"})]
      (is (= 2000 (:tooltip-ms m))))))

(deftest normalize-disabled-test
  (let [m (model/normalize {:disabled-present? true})]
    (is (= true (:disabled? m))))
  (let [m (model/normalize {:disabled-present? false})]
    (is (= false (:disabled? m)))))

(deftest normalize-messages-test
  (let [m (model/normalize {:success-message-raw "Done!" :error-message-raw "Oops!"})]
    (is (= "Done!" (:success-message m)))
    (is (= "Oops!" (:error-message m)))))

;; ── request-detail ────────────────────────────────────────────────────────────
(deftest request-detail-test
  (let [m   {:text "hello" :mode "text" :from nil :from-attr nil}
        ^js d (model/request-detail m)]
    (is (= "hello" (.-text d)))
    (is (= "text"  (.-mode d)))))

;; ── success-detail ────────────────────────────────────────────────────────────
(deftest success-detail-test
  (let [^js d (model/success-detail "copied text")]
    (is (= "copied text" (.-text d)))))

;; ── error-detail ─────────────────────────────────────────────────────────────
(deftest error-detail-test
  (let [^js d (model/error-detail (js/Error. "clipboard denied"))]
    (is (string? (.-error d)))
    (is (not= "" (.-error d)))))
