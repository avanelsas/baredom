(ns baredom.utils.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.utils.model :as model]))

;; ── sanitize-svg-path-d ─────────────────────────────────────────────────────

(deftest sanitize-svg-path-d-valid-paths-test
  (testing "preserves valid SVG path data"
    (is (= "M0,60 C200,20 400,100 600,60"
           (model/sanitize-svg-path-d "M0,60 C200,20 400,100 600,60")))
    (is (= "M0,0 L100,100 Z"
           (model/sanitize-svg-path-d "M0,0 L100,100 Z")))
    (is (= "m10,20 l30,40 h50 v60 z"
           (model/sanitize-svg-path-d "m10,20 l30,40 h50 v60 z")))
    (is (= "A25,26 -30 0,1 50,-25"
           (model/sanitize-svg-path-d "A25,26 -30 0,1 50,-25")))))

(deftest sanitize-svg-path-d-strips-dangerous-chars-test
  (testing "strips double-quotes"
    (is (= "M0,0 C200,20"
           (model/sanitize-svg-path-d "M0,0\" C200,20"))))
  (testing "strips parentheses"
    (is (= "M0,0 C200,20"
           (model/sanitize-svg-path-d "M0,0) C200,20"))))
  (testing "strips semicolons and colons"
    (is (not (re-find #"[;:]" (model/sanitize-svg-path-d "M0,0; background: red")))))
  (testing "strips quotes and parens from CSS injection payload"
    (let [result (model/sanitize-svg-path-d "M0,0\"); background: url(evil)")]
      (is (not (re-find #"[\"();:]" result)))))
  (testing "strips protocol slashes from url() attempts"
    (let [result (model/sanitize-svg-path-d "M0,0\"); url(https://evil.com)")]
      (is (not (re-find #"[\"();:/]" result))))))

(deftest sanitize-svg-path-d-edge-cases-test
  (is (nil? (model/sanitize-svg-path-d nil)))
  (is (nil? (model/sanitize-svg-path-d "")))
  (is (nil? (model/sanitize-svg-path-d "   ")))
  (testing "all-dangerous string has dangerous chars stripped"
    (let [result (model/sanitize-svg-path-d "\")(;:{}[]")]
      (is (or (nil? result)
              (not (re-find #"[\"();:{}\[\]/]" (or result ""))))))))

;; ── safe-url? ───────────────────────────────────────────────────────────────

(deftest safe-url-allowed-protocols-test
  (is (true?  (model/safe-url? "https://example.com")))
  (is (true?  (model/safe-url? "http://example.com")))
  (is (true?  (model/safe-url? "data:text/plain,hello")))
  (is (true?  (model/safe-url? "blob:https://example.com/uuid")))
  (is (true?  (model/safe-url? "mailto:user@example.com")))
  (is (true?  (model/safe-url? "tel:+1234567890"))))

(deftest safe-url-relative-urls-test
  (is (true? (model/safe-url? "/path/to/file")))
  (is (true? (model/safe-url? "./relative")))
  (is (true? (model/safe-url? "../parent")))
  (is (true? (model/safe-url? "file.pdf")))
  (is (true? (model/safe-url? "/path?q=a:b"))))

(deftest safe-url-blocks-dangerous-protocols-test
  (is (not (model/safe-url? "javascript:alert(1)")))
  (is (not (model/safe-url? "JAVASCRIPT:alert(1)")))
  (is (not (model/safe-url? "jAvAsCrIpT:alert(document.cookie)")))
  (is (not (model/safe-url? "vbscript:MsgBox")))
  (is (not (model/safe-url? "VBSCRIPT:MsgBox"))))

(deftest safe-url-control-char-bypass-test
  (testing "strips control characters before checking"
    (is (not (model/safe-url? "java\tscript:alert(1)")))
    (is (not (model/safe-url? "java\nscript:alert(1)")))
    (is (not (model/safe-url? "java\rscript:alert(1)")))))

(deftest safe-url-edge-cases-test
  (is (nil? (model/safe-url? nil)))
  (is (true? (model/safe-url? "")))
  (is (true? (model/safe-url? "   "))))

;; ── sanitize-url ────────────────────────────────────────────────────────────

(deftest sanitize-url-returns-safe-urls-test
  (is (= "https://example.com" (model/sanitize-url "https://example.com")))
  (is (= "/relative/path"      (model/sanitize-url "/relative/path")))
  (is (= ""                    (model/sanitize-url nil))))

(deftest sanitize-url-blocks-dangerous-urls-test
  (is (= "" (model/sanitize-url "javascript:alert(1)")))
  (is (= "" (model/sanitize-url "JAVASCRIPT:void(0)")))
  (is (= "" (model/sanitize-url "vbscript:MsgBox"))))
