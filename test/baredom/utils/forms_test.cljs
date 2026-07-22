(ns baredom.utils.forms-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.forms :as forms]))

;; ── validity: valid (clear) ─────────────────────────────────────────────────
(deftest validity-clear-test
  (testing "no error, not required → valid: empty flags + empty message"
    (let [{:keys [flags message]} (forms/validity {})]
      (is (= "" message))
      (is (nil? (.-customError flags)))
      (is (nil? (.-valueMissing flags)))))
  (testing "required but not empty → valid"
    (let [{:keys [flags message]} (forms/validity {:required? true :empty? false})]
      (is (= "" message))
      (is (nil? (.-valueMissing flags)))))
  (testing "empty but not required → valid"
    (let [{:keys [message]} (forms/validity {:required? false :empty? true})]
      (is (= "" message)))))

;; ── validity: customError from `error` ──────────────────────────────────────
(deftest validity-custom-error-test
  (testing "a present error reports customError with the message"
    (let [{:keys [flags message]} (forms/validity {:has-error? true :error "Bad value"})]
      (is (true? (.-customError flags)))
      (is (= "Bad value" message))))
  (testing "customError with a nil error message coerces to empty string"
    (let [{:keys [flags message]} (forms/validity {:has-error? true :error nil})]
      (is (true? (.-customError flags)))
      (is (= "" message)))))

;; ── validity: valueMissing from required + empty ────────────────────────────
(deftest validity-value-missing-test
  (testing "required + empty → valueMissing with the default message"
    (let [{:keys [flags message]} (forms/validity {:required? true :empty? true})]
      (is (true? (.-valueMissing flags)))
      (is (= forms/default-value-missing message))))
  (testing "a custom missing-message overrides the default"
    (let [{:keys [message]} (forms/validity {:required? true :empty? true
                                             :missing-message "Pick a date."})]
      (is (= "Pick a date." message)))))

;; ── validity: precedence (error wins over valueMissing) ─────────────────────
(deftest validity-precedence-test
  (testing "a present error wins even when required + empty would be valueMissing"
    (let [{:keys [flags message]} (forms/validity {:has-error? true :error "Boom"
                                                   :required? true :empty? true})]
      (is (true? (.-customError flags)))
      (is (nil? (.-valueMissing flags)))
      (is (= "Boom" message)))))

;; ── set-validity! / sync!: no-op when internals absent (older browsers) ──────
(deftest effects-noop-without-internals-test
  (testing "set-validity! and sync! are safe no-ops when internals is nil"
    (is (nil? (forms/set-validity! nil nil {:required? true :empty? true})))
    (is (nil? (forms/sync! nil nil "x" {:has-error? true :error "e"})))))

;; ── error-describedby: the aria-describedby token projection ─────────────────
(deftest error-describedby-test
  (testing "no error, no author → nil (attribute is removed)"
    (is (nil? (forms/error-describedby false nil)))
    (is (nil? (forms/error-describedby false ""))))
  (testing "error only → the error id"
    (is (= "error" (forms/error-describedby true nil)))
    (is (= "error" (forms/error-describedby true ""))))
  (testing "author only → the author value unchanged"
    (is (= "hint-1" (forms/error-describedby false "hint-1"))))
  (testing "author + error → author list with the error id appended"
    (is (= "hint-1 error" (forms/error-describedby true "hint-1")))))
