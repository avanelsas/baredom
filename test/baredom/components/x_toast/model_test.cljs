(ns baredom.components.x-toast.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-toast.model :as model]))

;; ── parse-type ───────────────────────────────────────────────────────────────
(deftest parse-type-known-values-test
  (is (= :info    (model/parse-type "info")))
  (is (= :success (model/parse-type "success")))
  (is (= :warning (model/parse-type "warning")))
  (is (= :error   (model/parse-type "error"))))

(deftest parse-type-case-insensitive-test
  (is (= :info    (model/parse-type "INFO")))
  (is (= :success (model/parse-type "Success")))
  (is (= :warning (model/parse-type "WARNING"))))

(deftest parse-type-unknown-falls-back-to-info-test
  (is (= :info (model/parse-type nil)))
  (is (= :info (model/parse-type "")))
  (is (= :info (model/parse-type "bad"))))

;; ── type->attr ───────────────────────────────────────────────────────────────
(deftest type->attr-roundtrip-test
  (is (= "info"    (model/type->attr :info)))
  (is (= "success" (model/type->attr :success)))
  (is (= "warning" (model/type->attr :warning)))
  (is (= "error"   (model/type->attr :error)))
  (is (= "info"    (model/type->attr :unknown))))

;; ── parse-bool-default-true ──────────────────────────────────────────────────
(deftest parse-bool-default-true-test
  (testing "absent (nil) → true"
    (is (true? (model/parse-bool-default-true nil))))
  (testing "empty string → true"
    (is (true? (model/parse-bool-default-true ""))))
  (testing "\"true\" → true"
    (is (true? (model/parse-bool-default-true "true"))))
  (testing "\"false\" → false"
    (is (false? (model/parse-bool-default-true "false"))))
  (testing "\"FALSE\" → false (case-insensitive)"
    (is (false? (model/parse-bool-default-true "FALSE"))))
  (testing "other values → true"
    (is (true? (model/parse-bool-default-true "yes")))))

;; ── parse-bool-default-false ─────────────────────────────────────────────────
(deftest parse-bool-default-false-test
  (testing "absent (nil) → false"
    (is (false? (model/parse-bool-default-false nil))))
  (testing "empty string → true (attribute present)"
    (is (true? (model/parse-bool-default-false ""))))
  (testing "\"false\" → false"
    (is (false? (model/parse-bool-default-false "false"))))
  (testing "\"FALSE\" → false (case-insensitive)"
    (is (false? (model/parse-bool-default-false "FALSE"))))
  (testing "\"true\" → true"
    (is (true? (model/parse-bool-default-false "true"))))
  (testing "other values → true"
    (is (true? (model/parse-bool-default-false "yes")))))

;; ── parse-timeout-ms ─────────────────────────────────────────────────────────
(deftest parse-timeout-ms-valid-test
  (is (= 3000 (model/parse-timeout-ms "3000")))
  (is (= 1    (model/parse-timeout-ms "1"))))

(deftest parse-timeout-ms-invalid-test
  (is (nil? (model/parse-timeout-ms nil)))
  (is (nil? (model/parse-timeout-ms "")))
  (is (nil? (model/parse-timeout-ms "0")))
  (is (nil? (model/parse-timeout-ms "-1")))
  (is (nil? (model/parse-timeout-ms "abc"))))

;; ── normalize-icon ───────────────────────────────────────────────────────────
(deftest normalize-icon-test
  (is (nil? (model/normalize-icon nil)))
  (is (nil? (model/normalize-icon "")))
  (is (nil? (model/normalize-icon "   ")))
  (is (nil? (model/normalize-icon "none")))
  (is (nil? (model/normalize-icon "NONE")))
  (is (= "★"  (model/normalize-icon "★")))
  (is (= "!" (model/normalize-icon "  !  "))))

;; ── role-for-type ────────────────────────────────────────────────────────────
(deftest role-for-type-test
  (is (= "status" (model/role-for-type :info)))
  (is (= "status" (model/role-for-type :success)))
  (is (= "alert"  (model/role-for-type :warning)))
  (is (= "alert"  (model/role-for-type :error))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= :info    (:type m)))
    (is (= ""       (:heading m)))
    (is (= ""       (:message m)))
    (is (= :default (:icon-mode m)))
    (is (nil?       (:icon m)))
    (is (true?      (:dismissible? m)))
    (is (false?     (:disabled? m)))
    (is (nil?       (:timeout-ms m)))
    (is (false?     (:show-progress? m)))))

(deftest normalize-type-propagates-test
  (is (= :success (:type (model/normalize {:type-raw "success"})))))

(deftest normalize-heading-trimmed-test
  (is (= "Hello" (:heading (model/normalize {:heading "  Hello  "}))))
  (is (= ""      (:heading (model/normalize {:heading nil})))))

(deftest normalize-message-trimmed-test
  (is (= "Body text" (:message (model/normalize {:message "  Body text  "}))))
  (is (= ""          (:message (model/normalize {:message nil})))))

(deftest normalize-icon-mode-custom-test
  (let [m (model/normalize {:icon-present? true :icon-raw "★"})]
    (is (= :custom (:icon-mode m)))
    (is (= "★"     (:icon m)))))

(deftest normalize-icon-mode-hidden-test
  (let [m (model/normalize {:icon-present? true :icon-raw "none"})]
    (is (= :hidden (:icon-mode m)))
    (is (nil?      (:icon m)))))

(deftest normalize-icon-mode-default-when-absent-test
  (let [m (model/normalize {:icon-present? false :icon-raw nil})]
    (is (= :default (:icon-mode m)))))

(deftest normalize-dismissible-false-test
  (is (false? (:dismissible? (model/normalize {:dismissible-attr "false"})))))

(deftest normalize-disabled-test
  (is (true? (:disabled? (model/normalize {:disabled-present? true})))))

(deftest normalize-timeout-ms-test
  (is (= 5000 (:timeout-ms (model/normalize {:timeout-ms-raw "5000"})))))

(deftest normalize-show-progress-absent-test
  (is (false? (:show-progress? (model/normalize {})))))

(deftest normalize-show-progress-present-test
  (is (true? (:show-progress? (model/normalize {:show-progress-attr ""})))))

(deftest normalize-show-progress-false-string-test
  (is (false? (:show-progress? (model/normalize {:show-progress-attr "false"})))))

;; ── dismiss-eligible? ────────────────────────────────────────────────────────
(deftest dismiss-eligible-test
  (is (true?  (model/dismiss-eligible? {:dismissible? true  :disabled? false})))
  (is (false? (model/dismiss-eligible? {:dismissible? false :disabled? false})))
  (is (false? (model/dismiss-eligible? {:dismissible? true  :disabled? true}))))

;; ── dismiss-detail ───────────────────────────────────────────────────────────
(deftest dismiss-detail-test
  (let [d (model/dismiss-detail {:type :warning :heading "Watch out" :message "Something bad"} "button")]
    (is (= "warning"        (:type d)))
    (is (= "Watch out"      (:heading d)))
    (is (= "Something bad"  (:message d)))
    (is (= "button"         (:reason d)))))

(deftest dismiss-detail-has-no-text-key-test
  (let [d (model/dismiss-detail {:type :info :heading "" :message "Hello"} "api")]
    (is (not (contains? d :text)))))

;; ── progress-eligible? ───────────────────────────────────────────────────────
(deftest progress-eligible-both-required-test
  (is (true?  (model/progress-eligible? {:show-progress? true  :timeout-ms 3000})))
  (is (false? (model/progress-eligible? {:show-progress? false :timeout-ms 3000})))
  (is (false? (model/progress-eligible? {:show-progress? true  :timeout-ms nil})))
  (is (false? (model/progress-eligible? {:show-progress? true  :timeout-ms 0})))
  (is (false? (model/progress-eligible? {}))))
