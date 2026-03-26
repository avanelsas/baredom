(ns app.components.x-cancel-dialogue.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-cancel-dialogue.model :as model]))

;; ── parse-bool-attr ───────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (testing "nil → false"
    (is (= false (model/parse-bool-attr nil))))
  (testing "empty string → true (attr present)"
    (is (= true (model/parse-bool-attr ""))))
  (testing "any string → true"
    (is (= true (model/parse-bool-attr "true"))))
  (testing "false string still true (presence semantics)"
    (is (= true (model/parse-bool-attr "false")))))

;; ── normalize defaults ────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (testing "open? defaults to false"
      (is (= false (:open? m))))
    (testing "disabled? defaults to false"
      (is (= false (:disabled? m))))
    (testing "headline defaults to default-headline"
      (is (= model/default-headline (:headline m))))
    (testing "message defaults to nil"
      (is (nil? (:message m))))
    (testing "confirm-text defaults to default-confirm-text"
      (is (= model/default-confirm-text (:confirm-text m))))
    (testing "cancel-text defaults to default-cancel-text"
      (is (= model/default-cancel-text (:cancel-text m))))
    (testing "danger? defaults to false"
      (is (= false (:danger? m))))
    (testing "portal defaults to nil"
      (is (nil? (:portal m))))))

;; ── normalize with values ─────────────────────────────────────────────────────
(deftest normalize-open-test
  (let [m (model/normalize {:open-present? true})]
    (is (= true (:open? m))))
  (let [m (model/normalize {:open-present? false})]
    (is (= false (:open? m)))))

(deftest normalize-headline-test
  (let [m (model/normalize {:headline-raw "Are you sure?"})]
    (is (= "Are you sure?" (:headline m))))
  (testing "empty string falls back to default"
    (let [m (model/normalize {:headline-raw ""})]
      (is (= model/default-headline (:headline m))))))

(deftest normalize-message-test
  (let [m (model/normalize {:message-raw "You will lose your changes."})]
    (is (= "You will lose your changes." (:message m))))
  (testing "empty string message is nil"
    (let [m (model/normalize {:message-raw ""})]
      (is (nil? (:message m))))))

(deftest normalize-confirm-cancel-text-test
  (let [m (model/normalize {:confirm-text-raw "Delete" :cancel-text-raw "Never mind"})]
    (is (= "Delete" (:confirm-text m)))
    (is (= "Never mind" (:cancel-text m)))))

(deftest normalize-danger-test
  (let [m (model/normalize {:danger-present? true})]
    (is (= true (:danger? m))))
  (let [m (model/normalize {:danger-present? false})]
    (is (= false (:danger? m)))))

(deftest normalize-portal-test
  (let [m (model/normalize {:portal-raw "#my-portal"})]
    (is (= "#my-portal" (:portal m))))
  (testing "whitespace-only portal is nil"
    (let [m (model/normalize {:portal-raw "   "})]
      (is (nil? (:portal m))))))

;; ── cancel-request-detail ─────────────────────────────────────────────────────
(deftest cancel-request-detail-test
  (doseq [reason ["cancel-button" "backdrop" "escape"]]
    (let [^js d (model/cancel-request-detail reason)]
      (is (= reason (.-reason d))))))

;; ── confirm-request-detail ────────────────────────────────────────────────────
(deftest confirm-request-detail-test
  (let [^js d (model/confirm-request-detail)]
    (is (some? d))))

;; ── default constants ─────────────────────────────────────────────────────────
(deftest default-constants-test
  (is (string? model/default-headline))
  (is (string? model/default-confirm-text))
  (is (string? model/default-cancel-text))
  (is (not= "" model/default-headline))
  (is (not= "" model/default-confirm-text))
  (is (not= "" model/default-cancel-text)))
