(ns baredom.components.x-file-download.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-file-download.model :as model]))

(deftest normalize-defaults-test
  (testing "defaults when no attrs present"
    (let [m (model/normalize {})]
      (is (= ""    (:href m)))
      (is (= ""    (:filename m)))
      (is (= false (:disabled? m)))
      (is (= nil   (:aria-label m)))
      (is (= false (:picker? m))))))

(deftest normalize-picker-test
  (testing "picker-present? true sets picker?"
    (is (= true (:picker? (model/normalize {:picker-present? true}))))))

(deftest download-value-test
  (testing "returns filename when present"
    (is (= "a.txt" (model/download-value {:href "data:text/plain,x" :filename "a.txt"}))))
  (testing "returns the empty string for a data: URL without filename"
    (is (= "" (model/download-value {:href "data:text/plain,x" :filename ""}))))
  (testing "returns nil for any other href without filename"
    (is (nil? (model/download-value {:href "/file.pdf" :filename ""})))
    (is (nil? (model/download-value {})))))

(deftest suggested-name-test
  (testing "returns filename when present"
    (is (= "report.pdf" (model/suggested-name {:href "/x/other.pdf" :filename "report.pdf"}))))
  (testing "returns the last path segment of href without filename"
    (is (= "q1.pdf" (model/suggested-name {:href "/reports/q1.pdf" :filename ""})))
    (is (= "q1.pdf" (model/suggested-name {:href "https://example.com/reports/q1.pdf?v=2#top"})))
    (is (= "q1.pdf" (model/suggested-name {:href "//cdn.example.com/q1.pdf"})))
    (is (= "my file.zip" (model/suggested-name {:href "/files/my%20file.zip"}))))
  (testing "keeps a segment that does not percent-decode"
    (is (= "bad%E0.zip" (model/suggested-name {:href "/files/bad%E0.zip"}))))
  (testing "returns nil when href names no file"
    (is (nil? (model/suggested-name {:href "https://example.com"})))
    (is (nil? (model/suggested-name {:href "https://example.com/"})))
    (is (nil? (model/suggested-name {:href "https://example.com?x=1"})))
    (is (nil? (model/suggested-name {:href ""})))
    (is (nil? (model/suggested-name {}))))
  (testing "returns nil for blob: and data: URLs"
    (is (nil? (model/suggested-name {:href "blob:https://example.com/uuid"})))
    (is (nil? (model/suggested-name {:href "data:text/plain,hello"})))))

(deftest picker-options-test
  (testing "returns suggestedName when there is a suggested name"
    (is (= {:suggestedName "p.zip"}
           (model/picker-options {:href "blob:x" :filename "p.zip"}))))
  (testing "returns an empty map without a suggested name"
    (is (= {} (model/picker-options {:href "blob:x" :filename ""})))
    (is (= {} (model/picker-options {})))))

(deftest failure-outcome-test
  (testing "returns :cancel for AbortError from the picker"
    (is (= :cancel (model/failure-outcome {:phase model/phase-pick :error model/error-abort}))))
  (testing "returns :fallback for SecurityError from the picker"
    (is (= :fallback (model/failure-outcome {:phase model/phase-pick :error model/error-security}))))
  (testing "returns :error for every other failure"
    (is (= :error (model/failure-outcome {:phase model/phase-pick :error "TypeError"})))
    (is (= :error (model/failure-outcome {:phase model/phase-fetch :error model/error-abort})))
    (is (= :error (model/failure-outcome {:phase model/phase-write :error model/error-security})))
    (is (= :error (model/failure-outcome {})))))

(deftest error-name-test
  (testing "returns the rejection name"
    (is (= "AbortError" (model/error-name (js/DOMException. "x" "AbortError"))))
    (is (= "TypeError" (model/error-name (js/TypeError. "x")))))
  (testing "returns Error when there is no name"
    (is (= "Error" (model/error-name nil)))
    (is (= "Error" (model/error-name #js {})))
    (is (= "Error" (model/error-name "boom")))))

(deftest http-error-test
  (testing "formats the status"
    (is (= "HTTP 404" (model/http-error 404)))))

(deftest event-details-test
  (testing "click detail carries href and filename"
    (let [d (model/click-detail {:href "/p.zip" :filename "p.zip"})]
      (is (= "/p.zip" (.-href d)))
      (is (= "p.zip" (.-filename d)))))
  (testing "success detail carries filename"
    (is (= "p.zip" (.-filename (model/success-detail "p.zip")))))
  (testing "cancel detail is empty"
    (is (= 0 (.-length (js/Object.keys (model/cancel-detail))))))
  (testing "error detail carries error and phase"
    (let [d (model/error-detail "HTTP 404" model/phase-fetch)]
      (is (= "HTTP 404" (.-error d)))
      (is (= "fetch" (.-phase d))))))

(deftest normalize-href-test
  (testing "href-raw is forwarded"
    (let [m (model/normalize {:href-raw "https://example.com/file.pdf"})]
      (is (= "https://example.com/file.pdf" (:href m)))))

  (testing "nil href-raw yields empty string"
    (let [m (model/normalize {:href-raw nil})]
      (is (= "" (:href m)))))

  (testing "empty href-raw yields empty string"
    (let [m (model/normalize {:href-raw ""})]
      (is (= "" (:href m))))))

(deftest normalize-filename-test
  (testing "filename-raw is forwarded"
    (let [m (model/normalize {:filename-raw "report.pdf"})]
      (is (= "report.pdf" (:filename m)))))

  (testing "nil filename-raw yields empty string"
    (let [m (model/normalize {:filename-raw nil})]
      (is (= "" (:filename m)))))

  (testing "empty filename-raw yields empty string"
    (let [m (model/normalize {:filename-raw ""})]
      (is (= "" (:filename m))))))

(deftest normalize-disabled-test
  (testing "disabled-present? true sets disabled?"
    (let [m (model/normalize {:disabled-present? true})]
      (is (= true (:disabled? m)))))

  (testing "disabled-present? nil yields disabled? false"
    (let [m (model/normalize {:disabled-present? nil})]
      (is (= false (:disabled? m)))))

  (testing "disabled-present? false yields disabled? false"
    (let [m (model/normalize {:disabled-present? false})]
      (is (= false (:disabled? m))))))

(deftest normalize-aria-label-test
  (testing "aria-label-raw is forwarded"
    (let [m (model/normalize {:aria-label-raw "Download report"})]
      (is (= "Download report" (:aria-label m)))))

  (testing "nil aria-label-raw yields nil"
    (let [m (model/normalize {:aria-label-raw nil})]
      (is (= nil (:aria-label m))))))

(deftest normalize-href-blocks-javascript-protocol-test
  (testing "javascript: URLs are sanitized to empty string"
    (is (= "" (:href (model/normalize {:href-raw "javascript:alert(1)"}))))
    (is (= "" (:href (model/normalize {:href-raw "JAVASCRIPT:alert(document.cookie)"}))))
    (is (= "" (:href (model/normalize {:href-raw "vbscript:MsgBox"})))))
  (testing "safe URLs pass through"
    (is (= "https://example.com/file.pdf"
           (:href (model/normalize {:href-raw "https://example.com/file.pdf"}))))
    (is (= "data:text/plain,hello"
           (:href (model/normalize {:href-raw "data:text/plain,hello"}))))
    (is (= "/relative/path"
           (:href (model/normalize {:href-raw "/relative/path"}))))))

(deftest data-url?-test
  (testing "detects data: URIs"
    (is (true?  (model/data-url? "data:text/plain,hello")))
    (is (true?  (model/data-url? "data:image/png;base64,abc"))))

  (testing "rejects non-data URIs"
    (is (false? (model/data-url? "https://example.com/file.pdf")))
    (is (false? (model/data-url? ""))))

  (testing "handles nil and non-string"
    (is (false? (model/data-url? nil)))
    (is (false? (model/data-url? 42)))))
