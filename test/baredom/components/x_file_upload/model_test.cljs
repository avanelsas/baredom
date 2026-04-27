(ns baredom.components.x-file-upload.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-file-upload.model :as model]))

;; ── parse-positive-int ──────────────────────────────────────────────────────
(deftest parse-positive-int-valid-test
  (is (= 5 (model/parse-positive-int "5")))
  (is (= 1024 (model/parse-positive-int "1024"))))

(deftest parse-positive-int-invalid-test
  (is (nil? (model/parse-positive-int nil)))
  (is (nil? (model/parse-positive-int "")))
  (is (nil? (model/parse-positive-int "0")))
  (is (nil? (model/parse-positive-int "-1")))
  (is (nil? (model/parse-positive-int "abc"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= ""    (:accept m)))
    (is (false?  (:multiple? m)))
    (is (nil?    (:max-size m)))
    (is (nil?    (:max-files m)))
    (is (false?  (:disabled? m)))
    (is (false?  (:required? m)))
    (is (= ""    (:name m)))))

(deftest normalize-full-test
  (let [m (model/normalize {:accept-raw "image/*"
                            :multiple-present? true
                            :max-size-raw "5242880"
                            :max-files-raw "3"
                            :disabled-present? false
                            :required-present? true
                            :name-raw "photos"})]
    (is (= "image/*" (:accept m)))
    (is (true?  (:multiple? m)))
    (is (= 5242880 (:max-size m)))
    (is (= 3 (:max-files m)))
    (is (true? (:required? m)))
    (is (= "photos" (:name m)))))

;; ── matches-accept? ────────────────────────────────────────────────────────
(defn- mock-file [name type size]
  #js {:name name :type type :size size})

(deftest matches-accept-nil-accepts-all-test
  (is (true? (model/matches-accept? (mock-file "f.txt" "text/plain" 100) nil)))
  (is (true? (model/matches-accept? (mock-file "f.txt" "text/plain" 100) ""))))

(deftest matches-accept-extension-test
  (is (true?  (model/matches-accept? (mock-file "doc.pdf" "application/pdf" 100) ".pdf")))
  (is (false? (model/matches-accept? (mock-file "doc.pdf" "application/pdf" 100) ".doc")))
  (is (true?  (model/matches-accept? (mock-file "Photo.JPG" "image/jpeg" 100) ".jpg"))))

(deftest matches-accept-wildcard-test
  (is (true?  (model/matches-accept? (mock-file "a.png" "image/png" 100) "image/*")))
  (is (true?  (model/matches-accept? (mock-file "a.jpg" "image/jpeg" 100) "image/*")))
  (is (false? (model/matches-accept? (mock-file "a.pdf" "application/pdf" 100) "image/*"))))

(deftest matches-accept-exact-mime-test
  (is (true?  (model/matches-accept? (mock-file "a.png" "image/png" 100) "image/png")))
  (is (false? (model/matches-accept? (mock-file "a.jpg" "image/jpeg" 100) "image/png"))))

(deftest matches-accept-multiple-tokens-test
  (is (true? (model/matches-accept? (mock-file "a.pdf" "application/pdf" 100) ".pdf,.doc")))
  (is (true? (model/matches-accept? (mock-file "a.doc" "application/msword" 100) ".pdf,.doc")))
  (is (false? (model/matches-accept? (mock-file "a.txt" "text/plain" 100) ".pdf,.doc"))))

;; ── validate-file ───────────────────────────────────────────────────────────
(deftest validate-file-valid-test
  (let [r (model/validate-file (mock-file "a.png" "image/png" 1000) "image/*" 5000)]
    (is (true? (:valid? r)))))

(deftest validate-file-wrong-type-test
  (let [r (model/validate-file (mock-file "a.txt" "text/plain" 100) "image/*" nil)]
    (is (false? (:valid? r)))
    (is (= "File type not accepted" (:reason r)))))

(deftest validate-file-too-large-test
  (let [r (model/validate-file (mock-file "a.png" "image/png" 10000) nil 5000)]
    (is (false? (:valid? r)))
    (is (= "File too large" (:reason r)))))

;; ── validate-files ──────────────────────────────────────────────────────────
(deftest validate-files-all-valid-test
  (let [files [(mock-file "a.png" "image/png" 100)
               (mock-file "b.jpg" "image/jpeg" 200)]
        r     (model/validate-files files "image/*" nil nil 0)]
    (is (= 2 (count (:accepted r))))
    (is (= 0 (count (:rejected r))))))

(deftest validate-files-max-files-enforced-test
  (let [files [(mock-file "a.png" "image/png" 100)
               (mock-file "b.jpg" "image/jpeg" 200)
               (mock-file "c.gif" "image/gif" 300)]
        r     (model/validate-files files "image/*" nil 2 0)]
    (is (= 2 (count (:accepted r))))
    (is (= 1 (count (:rejected r))))
    (is (= "Maximum files reached" (:reason (first (:rejected r)))))))

(deftest validate-files-mixed-test
  (let [files [(mock-file "a.png" "image/png" 100)
               (mock-file "b.txt" "text/plain" 200)]
        r     (model/validate-files files "image/*" nil nil 0)]
    (is (= 1 (count (:accepted r))))
    (is (= 1 (count (:rejected r))))))

;; ── format-file-size ────────────────────────────────────────────────────────
(deftest format-file-size-bytes-test
  (is (= "500 B" (model/format-file-size 500))))

(deftest format-file-size-kb-test
  (is (= "2.5 KB" (model/format-file-size 2560))))

(deftest format-file-size-mb-test
  (is (= "5.0 MB" (model/format-file-size 5242880))))

(deftest format-file-size-gb-test
  (is (= "1.5 GB" (model/format-file-size 1610612736))))

;; ── file-is-image? ─────────────────────────────────────────────────────────
(deftest file-is-image-true-test
  (is (true? (model/file-is-image? (mock-file "a.png" "image/png" 100))))
  (is (true? (model/file-is-image? (mock-file "a.jpg" "image/jpeg" 100)))))

(deftest file-is-image-false-test
  (is (false? (model/file-is-image? (mock-file "a.pdf" "application/pdf" 100))))
  (is (false? (model/file-is-image? (mock-file "a.txt" "text/plain" 100)))))
