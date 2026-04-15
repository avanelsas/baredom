(ns baredom.components.x-icon.x-icon-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-icon.x-icon :as x]
            [baredom.components.x-icon.model  :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn- size-var [^js el]
  (.. el -style (getPropertyValue "--x-icon-size")))

(defn- color-var [^js el]
  (.. el -style (getPropertyValue "--x-icon-color")))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ─────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=box]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default (decorative) state ───────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))
    (is (nil?     (.getAttribute el "role")))
    (is (nil?     (.getAttribute el "aria-label")))
    (is (= "20px" (size-var el)))
    (is (= "inherit" (color-var el)))))

;; ── size attribute: tokens ───────────────────────────────────────────────────
(deftest size-token-test
  (testing "sm"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size "sm")
      (is (= "16px" (size-var el)))))
  (testing "lg"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size "lg")
      (is (= "24px" (size-var el)))))
  (testing "xl"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size "xl")
      (is (= "32px" (size-var el))))))

;; ── size attribute: numeric ──────────────────────────────────────────────────
(deftest size-numeric-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "48")
    (is (= "48px" (size-var el)))))

;; ── size attribute: invalid falls back ───────────────────────────────────────
(deftest size-invalid-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "nonsense")
    (is (= "20px" (size-var el)))))

;; ── color attribute ──────────────────────────────────────────────────────────
(deftest color-theme-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-color "primary")
    (is (.startsWith (color-var el) "var(--x-color-primary,"))))

(deftest color-inherit-default-test
  (let [^js el (append! (make-el))]
    (is (= "inherit" (color-var el)))))

(deftest color-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-color "purple")
    (is (= "inherit" (color-var el)))))

(deftest color-muted-uses-text-muted-token-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-color "muted")
    (is (.startsWith (color-var el) "var(--x-color-text-muted,"))))

;; ── label ────────────────────────────────────────────────────────────────────
(deftest label-adds-role-and-aria-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Save")
    (is (= "img"  (.getAttribute el "role")))
    (is (= "Save" (.getAttribute el "aria-label")))
    (is (nil?     (.getAttribute el "aria-hidden")))))

(deftest label-removal-reverts-to-decorative-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Save")
    (.removeAttribute el model/attr-label)
    (is (= "true" (.getAttribute el "aria-hidden")))
    (is (nil?     (.getAttribute el "role")))
    (is (nil?     (.getAttribute el "aria-label")))))

(deftest label-empty-is-decorative-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "")
    (is (= "true" (.getAttribute el "aria-hidden")))
    (is (nil?     (.getAttribute el "role")))))

;; ── Slotted SVG distribution ────────────────────────────────────────────────
(deftest slotted-svg-is-distributed-test
  (let [^js el  (append! (make-el))
        ^js svg (.createElementNS js/document "http://www.w3.org/2000/svg" "svg")
        ^js slot (shadow-part el "slot")]
    (.appendChild el svg)
    (let [assigned (.assignedNodes slot)]
      (is (pos? (.-length assigned)))
      (is (= svg (aget assigned 0))))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest size-property-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-size el)))
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))
    (is (= "lg" (.-size el)))
    (set! (.-size el) "")
    (is (not (.hasAttribute el model/attr-size)))))

(deftest color-property-test
  (let [^js el (append! (make-el))]
    (set! (.-color el) "danger")
    (is (= "danger" (.getAttribute el model/attr-color)))
    (is (= "danger" (.-color el)))))

(deftest label-property-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "Delete")
    (is (= "Delete" (.getAttribute el model/attr-label)))
    (is (= "Delete" (.-label el)))
    (is (= "img"    (.getAttribute el "role")))))
