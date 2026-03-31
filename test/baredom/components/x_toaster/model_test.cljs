(ns baredom.components.x-toaster.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-toaster.model :as model]))

;; ── parse-position ────────────────────────────────────────────────────────────
(deftest parse-position-test
  (testing "valid positions pass through"
    (is (= "top-start"    (model/parse-position "top-start")))
    (is (= "top-center"   (model/parse-position "top-center")))
    (is (= "top-end"      (model/parse-position "top-end")))
    (is (= "bottom-start" (model/parse-position "bottom-start")))
    (is (= "bottom-center" (model/parse-position "bottom-center")))
    (is (= "bottom-end"   (model/parse-position "bottom-end"))))

  (testing "normalises case"
    (is (= "top-end"    (model/parse-position "TOP-END")))
    (is (= "top-start"  (model/parse-position "Top-Start"))))

  (testing "trims whitespace"
    (is (= "bottom-end" (model/parse-position "  bottom-end  "))))

  (testing "unknown values fall back to top-end"
    (is (= "top-end" (model/parse-position nil)))
    (is (= "top-end" (model/parse-position "")))
    (is (= "top-end" (model/parse-position "middle")))
    (is (= "top-end" (model/parse-position "right")))))

;; ── parse-max-toasts ─────────────────────────────────────────────────────────
(deftest parse-max-toasts-test
  (testing "valid positive integers"
    (is (= 1   (model/parse-max-toasts "1")))
    (is (= 3   (model/parse-max-toasts "3")))
    (is (= 10  (model/parse-max-toasts "10"))))

  (testing "truncates decimals"
    (is (= 4 (model/parse-max-toasts "4.9"))))

  (testing "invalid values return nil"
    (is (nil? (model/parse-max-toasts nil)))
    (is (nil? (model/parse-max-toasts "")))
    (is (nil? (model/parse-max-toasts "abc")))
    (is (nil? (model/parse-max-toasts "0")))
    (is (nil? (model/parse-max-toasts "-2")))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-test
  (testing "defaults when all nil"
    (let [m (model/normalize {:position-raw nil :max-toasts-raw nil :label-raw nil})]
      (is (= "top-end"       (:position m)))
      (is (= 5               (:max-toasts m)))
      (is (= "Notifications" (:label m)))))

  (testing "valid inputs flow through"
    (let [m (model/normalize {:position-raw   "bottom-start"
                              :max-toasts-raw "3"
                              :label-raw      "Alerts"})]
      (is (= "bottom-start" (:position m)))
      (is (= 3              (:max-toasts m)))
      (is (= "Alerts"       (:label m)))))

  (testing "blank label falls back to Notifications"
    (let [m (model/normalize {:position-raw nil :max-toasts-raw nil :label-raw "  "})]
      (is (= "Notifications" (:label m))))))

;; ── dismiss-detail ────────────────────────────────────────────────────────────
(deftest dismiss-detail-test
  (testing "maps all fields"
    (let [d (model/dismiss-detail "success" "button" "Saved" "File saved.")]
      (is (= "success"    (:type d)))
      (is (= "button"     (:reason d)))
      (is (= "Saved"      (:heading d)))
      (is (= "File saved." (:message d)))))

  (testing "nil fields fall back to defaults"
    (let [d (model/dismiss-detail nil nil nil nil)]
      (is (= "info" (:type d)))
      (is (= ""     (:reason d)))
      (is (= ""     (:heading d)))
      (is (= ""     (:message d))))))

;; ── derived helpers ───────────────────────────────────────────────────────────
(deftest bottom-position-test
  (is (true?  (model/bottom-position? "bottom-start")))
  (is (true?  (model/bottom-position? "bottom-center")))
  (is (true?  (model/bottom-position? "bottom-end")))
  (is (false? (model/bottom-position? "top-start")))
  (is (false? (model/bottom-position? "top-end"))))

(deftest center-position-test
  (is (true?  (model/center-position? "top-center")))
  (is (true?  (model/center-position? "bottom-center")))
  (is (false? (model/center-position? "top-end")))
  (is (false? (model/center-position? "bottom-start"))))
