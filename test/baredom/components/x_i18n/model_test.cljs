(ns baredom.components.x-i18n.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-i18n.model :as model]))

;; ── parse-params ────────────────────────────────────────────────────────────
(deftest parse-params-valid-json-test
  (is (= {"name" "World"} (model/parse-params "{\"name\":\"World\"}")))
  (is (= {"a" 1 "b" 2} (model/parse-params "{\"a\":1,\"b\":2}"))))

(deftest parse-params-nil-and-empty-test
  (is (= {} (model/parse-params nil)))
  (is (= {} (model/parse-params ""))))

(deftest parse-params-invalid-json-test
  (is (= {} (model/parse-params "not json")))
  (is (= {} (model/parse-params "42"))))

;; ── resolve-key ─────────────────────────────────────────────────────────────
(deftest resolve-key-flat-test
  (let [t #js {"greeting" "Hello"}]
    (is (= "Hello" (model/resolve-key t "greeting")))))

(deftest resolve-key-nested-test
  (let [t #js {"nav" #js {"home" "Home" "about" "About"}}]
    (is (= "Home" (model/resolve-key t "nav.home")))
    (is (= "About" (model/resolve-key t "nav.about")))))

(deftest resolve-key-deep-nesting-test
  (let [t #js {"a" #js {"b" #js {"c" "deep"}}}]
    (is (= "deep" (model/resolve-key t "a.b.c")))))

(deftest resolve-key-missing-returns-nil-test
  (let [t #js {"a" "b"}]
    (is (nil? (model/resolve-key t "missing")))
    (is (nil? (model/resolve-key t "a.b.c")))))

(deftest resolve-key-nil-translations-test
  (is (nil? (model/resolve-key nil "key"))))

(deftest resolve-key-empty-key-test
  (is (nil? (model/resolve-key #js {"a" "b"} "")))
  (is (nil? (model/resolve-key #js {"a" "b"} nil))))

;; ── interpolate ─────────────────────────────────────────────────────────────
(deftest interpolate-single-placeholder-test
  (is (= "Hello World" (model/interpolate "Hello {name}" {"name" "World"}))))

(deftest interpolate-multiple-placeholders-test
  (is (= "Hello World, you have 5 messages"
         (model/interpolate "Hello {name}, you have {count} messages"
                            {"name" "World" "count" "5"}))))

(deftest interpolate-missing-key-leaves-placeholder-test
  (is (= "Hello {name}" (model/interpolate "Hello {name}" {}))))

(deftest interpolate-no-params-test
  (is (= "Hello World" (model/interpolate "Hello World" {})))
  (is (= "Hello World" (model/interpolate "Hello World" nil))))

(deftest interpolate-nil-template-test
  (is (= "" (model/interpolate nil {"a" "b"}))))

;; ── resolve-translation ─────────────────────────────────────────────────────
(deftest resolve-translation-from-current-test
  (let [t #js {"greeting" "Hello {name}"}]
    (is (= "Hello World"
           (model/resolve-translation t nil "greeting" {"name" "World"})))))

(deftest resolve-translation-falls-back-test
  (let [fb #js {"greeting" "Fallback {name}"}]
    (is (= "Fallback Bob"
           (model/resolve-translation nil fb "greeting" {"name" "Bob"})))))

(deftest resolve-translation-falls-back-to-key-test
  (is (= "missing.key"
         (model/resolve-translation nil nil "missing.key" {}))))

(deftest resolve-translation-current-preferred-over-fallback-test
  (let [t  #js {"msg" "Current"}
        fb #js {"msg" "Fallback"}]
    (is (= "Current" (model/resolve-translation t fb "msg" {})))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize nil nil)]
    (is (= "" (:key m)))
    (is (= {} (:params m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize "greeting" "{\"name\":\"World\"}")]
    (is (= "greeting" (:key m)))
    (is (= {"name" "World"} (:params m)))))
