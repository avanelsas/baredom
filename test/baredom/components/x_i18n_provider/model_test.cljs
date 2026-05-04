(ns baredom.components.x-i18n-provider.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-i18n-provider.model :as model]))

;; ── resolve-url ─────────────────────────────────────────────────────────────
(deftest resolve-url-replaces-locale-placeholder-test
  (is (= "/locales/en.json"
         (model/resolve-url "/locales/{locale}.json" "en")))
  (is (= "/i18n/nl.json"
         (model/resolve-url "/i18n/{locale}.json" "nl"))))

(deftest resolve-url-returns-nil-when-src-blank-test
  (is (nil? (model/resolve-url "" "en")))
  (is (nil? (model/resolve-url nil "en"))))

(deftest resolve-url-returns-nil-when-locale-blank-test
  (is (nil? (model/resolve-url "/locales/{locale}.json" "")))
  (is (nil? (model/resolve-url "/locales/{locale}.json" nil))))

(deftest resolve-url-no-placeholder-test
  (is (= "/static/messages.json"
         (model/resolve-url "/static/messages.json" "en"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-locale-to-en-test
  (let [m (model/normalize "/locales/{locale}.json" nil nil)]
    (is (= "en" (:locale m))))
  (let [m (model/normalize "/locales/{locale}.json" "" nil)]
    (is (= "en" (:locale m)))))

(deftest normalize-preserves-explicit-locale-test
  (let [m (model/normalize "/locales/{locale}.json" "nl" "en")]
    (is (= "nl" (:locale m)))
    (is (= "en" (:fallback-locale m)))
    (is (= "/locales/{locale}.json" (:src m)))))

(deftest normalize-empty-fallback-test
  (let [m (model/normalize "/locales/{locale}.json" "en" nil)]
    (is (= "" (:fallback-locale m)))))

;; ── needs-fetch? ────────────────────────────────────────────────────────────
(deftest needs-fetch-detects-locale-change-test
  (is (model/needs-fetch?
       {:src "/l/{locale}.json" :locale "en" :fallback-locale ""}
       {:src "/l/{locale}.json" :locale "nl" :fallback-locale ""})))

(deftest needs-fetch-detects-src-change-test
  (is (model/needs-fetch?
       {:src "/a/{locale}.json" :locale "en" :fallback-locale ""}
       {:src "/b/{locale}.json" :locale "en" :fallback-locale ""})))

(deftest needs-fetch-false-when-identical-test
  (is (not (model/needs-fetch?
            {:src "/l/{locale}.json" :locale "en" :fallback-locale ""}
            {:src "/l/{locale}.json" :locale "en" :fallback-locale ""}))))

(deftest needs-fetch-detects-fallback-change-test
  (is (model/needs-fetch?
       {:src "/l/{locale}.json" :locale "en" :fallback-locale ""}
       {:src "/l/{locale}.json" :locale "en" :fallback-locale "de"})))
