(ns baredom.components.x-morph-stack.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-morph-stack.model :as model]))

;; ── parsers ──────────────────────────────────────────────────────────────────
(deftest parse-stiffness-test
  (testing "default for nil/empty/junk"
    (is (= 170.0 (model/parse-stiffness nil)))
    (is (= 170.0 (model/parse-stiffness "")))
    (is (= 170.0 (model/parse-stiffness "abc"))))
  (testing "clamps to range [10, 500]"
    (is (= 10.0  (model/parse-stiffness "1")))
    (is (= 500.0 (model/parse-stiffness "9999")))
    (is (= 250.0 (model/parse-stiffness "250")))))

(deftest parse-damping-test
  (is (= 26.0  (model/parse-damping nil)))
  (is (= 1.0   (model/parse-damping "0")))
  (is (= 100.0 (model/parse-damping "500")))
  (is (= 12.5  (model/parse-damping "12.5"))))

(deftest parse-mass-test
  (is (= 1.0  (model/parse-mass nil)))
  (is (= 0.1  (model/parse-mass "0")))
  (is (= 10.0 (model/parse-mass "100")))
  (is (= 2.0  (model/parse-mass "2"))))

(deftest parse-active-index-test
  (is (nil? (model/parse-active-index nil)))
  (is (nil? (model/parse-active-index "")))
  (is (nil? (model/parse-active-index "abc")))
  (is (nil? (model/parse-active-index "-3")))
  (is (= 0 (model/parse-active-index "0")))
  (is (= 5 (model/parse-active-index "5"))))

(deftest parse-bool-test
  (is (false? (model/parse-bool nil)))
  (is (true?  (model/parse-bool "")))
  (is (true?  (model/parse-bool "true")))
  (is (true?  (model/parse-bool "false"))))  ; presence-based

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil? (:active-state m)))
    (is (nil? (:active-index m)))
    (is (= 170.0 (:stiffness m)))
    (is (= 26.0  (:damping m)))
    (is (= 1.0   (:mass m)))
    (is (false? (:goo? m)))
    (is (false? (:disabled? m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize {:active-state-raw " welcome "
                            :active-index-raw "2"
                            :stiffness-raw "200"
                            :damping-raw "30"
                            :mass-raw "1.5"
                            :goo-present? true
                            :disabled-present? true})]
    (is (= "welcome" (:active-state m)))
    (is (= 2        (:active-index m)))
    (is (= 200.0    (:stiffness m)))
    (is (= 30.0     (:damping m)))
    (is (= 1.5      (:mass m)))
    (is (true? (:goo? m)))
    (is (true? (:disabled? m)))))

;; ── resolve-active ───────────────────────────────────────────────────────────
(deftest resolve-active-test
  (testing "name match wins"
    (is (= "b" (model/resolve-active ["a" "b" "c"]
                                     {:active-state "b" :active-index 0}))))
  (testing "unknown name → falls back to clamped index"
    (is (= "a" (model/resolve-active ["a" "b" "c"]
                                     {:active-state "zzz" :active-index 0})))
    (is (= "c" (model/resolve-active ["a" "b" "c"]
                                     {:active-state "zzz" :active-index 99}))))
  (testing "no name, default index 0"
    (is (= "a" (model/resolve-active ["a" "b" "c"] {}))))
  (testing "empty list returns nil"
    (is (nil? (model/resolve-active [] {:active-state "x"})))))

;; ── spring physics ───────────────────────────────────────────────────────────
(deftest spring-step-monotonic-test
  (let [step1 (model/spring-step 0.0 1.0 0.0 (/ 1.0 60.0) 1.0 170.0 26.0)
        new-pos (aget step1 0)]
    (is (pos? new-pos) "first step moves toward target")
    (is (< new-pos 1.0) "but not past target on first step")))

(deftest spring-settled-test
  (is (true?  (model/spring-settled? 0.0     0.0)))
  (is (true?  (model/spring-settled? 0.0005  0.005)))
  (is (false? (model/spring-settled? 0.5     0.0)))
  (is (false? (model/spring-settled? 0.0     1.0))))

;; ── lerp helpers ─────────────────────────────────────────────────────────────
(deftest lerp-test
  (is (= 0.0  (model/lerp 0.0 10.0 0.0)))
  (is (= 5.0  (model/lerp 0.0 10.0 0.5)))
  (is (= 10.0 (model/lerp 0.0 10.0 1.0))))

(deftest parse-rgb-string-test
  (let [r (model/parse-rgb-string "rgb(10, 20, 30)")]
    (is (= 10.0 (aget r 0)))
    (is (= 20.0 (aget r 1)))
    (is (= 30.0 (aget r 2)))
    (is (= 1.0  (aget r 3))))
  (let [r (model/parse-rgb-string "rgba(255,0,0,0.5)")]
    (is (= 255.0 (aget r 0)))
    (is (= 0.5   (aget r 3))))
  (is (nil? (model/parse-rgb-string "transparent")))
  (is (nil? (model/parse-rgb-string nil))))

(deftest lerp-color-test
  (testing "endpoints"
    (is (= "rgba(0,0,0,1)"
           (model/lerp-color "rgb(0,0,0)" "rgb(100,100,100)" 0.0)))
    (is (= "rgba(100,100,100,1)"
           (model/lerp-color "rgb(0,0,0)" "rgb(100,100,100)" 1.0))))
  (testing "midpoint"
    (is (= "rgba(50,50,50,1)"
           (model/lerp-color "rgb(0,0,0)" "rgb(100,100,100)" 0.5))))
  (testing "snap on parse failure"
    (is (= "transparent" (model/lerp-color "transparent" "rgb(0,0,0)" 0.4)))
    (is (= "rgb(0,0,0)"  (model/lerp-color "transparent" "rgb(0,0,0)" 0.6)))))

(deftest parse-px-list-test
  (let [r (model/parse-px-list "8px 12px 4px 0px")]
    (is (= 4 (.-length r)))
    (is (= 8.0 (aget r 0)))
    (is (= 0.0 (aget r 3))))
  (is (nil? (model/parse-px-list "8px 50%"))) ; mixed units → nil
  (is (nil? (model/parse-px-list nil)))
  (is (nil? (model/parse-px-list ""))))

(deftest lerp-radius-list-test
  (testing "matched-shape lerp"
    (is (= "5px" (model/lerp-radius-list "0px" "10px" 0.5)))
    (is (= "0px 5px" (model/lerp-radius-list "0px 0px" "0px 10px" 0.5))))
  (testing "mismatched shapes snap at midpoint"
    (is (= "0px"     (model/lerp-radius-list "0px" "5px 10px" 0.4)))
    (is (= "5px 10px" (model/lerp-radius-list "0px" "5px 10px" 0.6)))))

;; ── diff-morph-ids ───────────────────────────────────────────────────────────
(deftest diff-morph-ids-test
  (let [d (model/diff-morph-ids ["a" "b" "c"] ["b" "c" "d"])]
    (is (= #{"b" "c"} (:matched d)))
    (is (= #{"a"}     (:leaving d)))
    (is (= #{"d"}     (:entering d))))
  (let [d (model/diff-morph-ids [] ["x"])]
    (is (= #{}    (:matched d)))
    (is (= #{}    (:leaving d)))
    (is (= #{"x"} (:entering d)))))

;; ── event detail ─────────────────────────────────────────────────────────────
(deftest change-detail-test
  (is (= {:from "a" :to "b" :reason "method"}
         (model/change-detail "a" "b" "method")))
  (is (= {:from "" :to "b" :reason "attribute"}
         (model/change-detail nil "b" "attribute"))))

(deftest changed-detail-test
  (is (= {:from "a" :to "b"} (model/changed-detail "a" "b")))
  (is (= {:from "" :to ""}   (model/changed-detail nil nil))))
