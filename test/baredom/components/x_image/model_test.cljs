(ns baredom.components.x-image.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-image.model :as model]))

;; ── parse-ratio ──────────────────────────────────────────────────────────────
(deftest parse-ratio-valid-test
  (testing "colon form"
    (let [r (model/parse-ratio "16:9")]
      (is (true? (:valid? r)))
      (is (= "16 / 9" (:css r)))))
  (testing "square"
    (is (= "1 / 1" (:css (model/parse-ratio "1:1")))))
  (testing "slash form"
    (is (= "16 / 9" (:css (model/parse-ratio "16/9")))))
  (testing "whitespace tolerated"
    (is (= "4 / 3" (:css (model/parse-ratio " 4 : 3 ")))))
  (testing "decimal values"
    (is (= "1.5 / 1" (:css (model/parse-ratio "1.5:1"))))))

(deftest parse-ratio-auto-test
  (testing "nil → valid, no css"
    (let [r (model/parse-ratio nil)]
      (is (true? (:valid? r)))
      (is (nil?  (:css r)))))
  (testing "empty string → valid, no css"
    (let [r (model/parse-ratio "")]
      (is (true? (:valid? r)))
      (is (nil?  (:css r)))))
  (testing "\"auto\" → valid, no css"
    (let [r (model/parse-ratio "auto")]
      (is (true? (:valid? r)))
      (is (nil?  (:css r)))))
  (testing "\"AUTO\" case-insensitive"
    (is (true? (:valid? (model/parse-ratio "AUTO"))))))

(deftest parse-ratio-invalid-test
  (testing "garbage"
    (let [r (model/parse-ratio "abc")]
      (is (false? (:valid? r)))
      (is (nil?   (:css r)))))
  (testing "zero denominator"
    (is (false? (:valid? (model/parse-ratio "16:0")))))
  (testing "zero numerator"
    (is (false? (:valid? (model/parse-ratio "0:9")))))
  (testing "negative not matched"
    (is (false? (:valid? (model/parse-ratio "-1:2")))))
  (testing "three parts"
    (is (false? (:valid? (model/parse-ratio "16:9:1"))))))

;; ── parse-fit ────────────────────────────────────────────────────────────────
(deftest parse-fit-test
  (testing "all enum values"
    (is (= "cover"      (model/parse-fit "cover")))
    (is (= "contain"    (model/parse-fit "contain")))
    (is (= "fill"       (model/parse-fit "fill")))
    (is (= "none"       (model/parse-fit "none")))
    (is (= "scale-down" (model/parse-fit "scale-down"))))
  (testing "case insensitive"
    (is (= "cover" (model/parse-fit "COVER"))))
  (testing "unknown falls back"
    (is (= "cover" (model/parse-fit "bogus"))))
  (testing "nil falls back"
    (is (= "cover" (model/parse-fit nil)))))

;; ── parse-loading ────────────────────────────────────────────────────────────
(deftest parse-loading-test
  (is (= "lazy"  (model/parse-loading "lazy")))
  (is (= "eager" (model/parse-loading "eager")))
  (is (= "eager" (model/parse-loading "EAGER")))
  (is (= "lazy"  (model/parse-loading nil)))
  (is (= "lazy"  (model/parse-loading "bogus"))))

;; ── parse-position ───────────────────────────────────────────────────────────
(deftest parse-position-test
  (is (= "center"    (model/parse-position nil)))
  (is (= "center"    (model/parse-position "")))
  (is (= "center"    (model/parse-position "  ")))
  (is (= "top"       (model/parse-position "top")))
  (is (= "50% 50%"   (model/parse-position "50% 50%"))))

;; ── safe-src ─────────────────────────────────────────────────────────────────
(deftest safe-src-test
  (testing "https passes"
    (is (= "https://example.com/a.jpg" (model/safe-src "https://example.com/a.jpg"))))
  (testing "relative passes"
    (is (= "/local.png" (model/safe-src "/local.png"))))
  (testing "data: image passes"
    (let [u "data:image/png;base64,iVBORw0KGgo="]
      (is (= u (model/safe-src u)))))
  (testing "javascript: blocked"
    (is (nil? (model/safe-src "javascript:alert(1)"))))
  (testing "nil"
    (is (nil? (model/safe-src nil))))
  (testing "empty"
    (is (nil? (model/safe-src "")))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil?    (:src m)))
    (is (= ""    (:alt m)))
    (is (false?  (:decorative? m)))
    (is (nil?    (:ratio-css m)))
    (is (true?   (:ratio-valid? m)))
    (is (= "cover"  (:fit m)))
    (is (= "center" (:position m)))
    (is (= "lazy"   (:loading m)))
    (is (true? (:warn-alt? m)))))

(deftest normalize-decorative-precedence-test
  (testing "decorative + missing alt → no warning, empty alt"
    (let [m (model/normalize {:decorative-present? true})]
      (is (true?  (:decorative? m)))
      (is (= ""   (:alt m)))
      (is (false? (:warn-alt? m)))))
  (testing "decorative wins over alt attribute"
    (let [m (model/normalize {:decorative-present? true
                              :alt-present? true
                              :alt-raw "ignored"})]
      (is (= "" (:alt m)))
      (is (false? (:warn-alt? m))))))

(deftest normalize-alt-empty-present-test
  (testing "non-decorative with explicit empty alt → no warning"
    (let [m (model/normalize {:alt-present? true :alt-raw ""})]
      (is (false? (:warn-alt? m)))
      (is (= "" (:alt m))))))

(deftest normalize-alt-missing-test
  (testing "non-decorative without alt → warning flag"
    (let [m (model/normalize {})]
      (is (true? (:warn-alt? m))))))

(deftest normalize-alt-present-test
  (let [m (model/normalize {:alt-present? true :alt-raw "A cat"})]
    (is (= "A cat" (:alt m)))
    (is (false? (:warn-alt? m)))))

(deftest normalize-src-sanitization-test
  (testing "javascript: rejected"
    (is (nil? (:src (model/normalize {:src-raw "javascript:alert(1)"})))))
  (testing "https: accepted"
    (is (= "https://a/b.jpg"
           (:src (model/normalize {:src-raw "https://a/b.jpg"})))))
  (testing "relative accepted"
    (is (= "/x.png" (:src (model/normalize {:src-raw "/x.png"})))))
  (testing "data:image accepted"
    (is (= "data:image/png;base64,abc"
           (:src (model/normalize {:src-raw "data:image/png;base64,abc"}))))))

(deftest normalize-ratio-test
  (testing "valid ratio-css populated"
    (let [m (model/normalize {:ratio-raw "16:9"})]
      (is (= "16 / 9" (:ratio-css m)))
      (is (true? (:ratio-valid? m)))))
  (testing "invalid ratio → nil css + false valid"
    (let [m (model/normalize {:ratio-raw "oops"})]
      (is (nil?   (:ratio-css m)))
      (is (false? (:ratio-valid? m))))))

(deftest normalize-fit-loading-position-test
  (let [m (model/normalize {:fit-raw "contain"
                            :loading-raw "eager"
                            :position-raw "top right"})]
    (is (= "contain"   (:fit m)))
    (is (= "eager"     (:loading m)))
    (is (= "top right" (:position m)))))

;; ── load-detail / error-detail ───────────────────────────────────────────────
(deftest load-detail-test
  (let [d (model/load-detail "/foo.png" 100 50)]
    (is (= "/foo.png" (:src d)))
    (is (= 100       (:naturalWidth d)))
    (is (= 50        (:naturalHeight d)))))

(deftest error-detail-test
  (let [d (model/error-detail "/missing.png")]
    (is (= "/missing.png" (:src d)))))

(deftest load-detail-nil-test
  (let [d (model/load-detail nil nil nil)]
    (is (= "" (:src d)))
    (is (= 0 (:naturalWidth d)))
    (is (= 0 (:naturalHeight d)))))
