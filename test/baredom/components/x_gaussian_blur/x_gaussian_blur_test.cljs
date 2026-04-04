(ns baredom.components.x-gaussian-blur.x-gaussian-blur-test
  (:require [cljs.test :refer [deftest testing is use-fixtures]]
            [baredom.components.x-gaussian-blur.model :as model]
            [baredom.components.x-gaussian-blur.x-gaussian-blur :as x]))

(x/init!)

(defn- cleanup-dom! []
  (doseq [el (array-seq (.querySelectorAll (.-body js/document) model/tag-name))]
    (.removeChild (.-body js/document) el)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn- make-el [] (.createElement js/document model/tag-name))
(defn- append! [el] (.appendChild (.-body js/document) el) el)
(defn- shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Registration ─────────────────────────────────────────────────────────

(deftest registration-test
  (testing "element is registered"
    (is (some? (.get js/customElements model/tag-name)))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────

(deftest shadow-dom-structure-test
  (let [el (append! (make-el))]
    (testing "has shadow root"
      (is (some? (.-shadowRoot el))))

    (testing "has backdrop part"
      (is (some? (shadow-part el "[part=backdrop]"))))

    (testing "has content part"
      (is (some? (shadow-part el "[part=content]"))))

    (testing "has slot inside content"
      (is (some? (.querySelector (shadow-part el "[part=content]") "slot"))))

    (testing "backdrop is aria-hidden"
      (is (= "true" (.getAttribute (shadow-part el "[part=backdrop]") "aria-hidden"))))))

;; ── Default blob count ───────────────────────────────────────────────────

(deftest default-blob-count-test
  (let [el (append! (make-el))
        backdrop (shadow-part el "[part=backdrop]")]
    (testing "creates default number of blobs"
      (is (= model/default-count (.-length (.querySelectorAll backdrop ".blob")))))))

;; ── Custom blob count ────────────────────────────────────────────────────

(deftest custom-blob-count-test
  (let [el (make-el)]
    (.setAttribute el "count" "3")
    (append! el)
    (let [backdrop (shadow-part el "[part=backdrop]")]
      (testing "creates specified number of blobs"
        (is (= 3 (.-length (.querySelectorAll backdrop ".blob"))))))))

;; ── Blur attribute ───────────────────────────────────────────────────────

(deftest blur-attribute-test
  (let [el (make-el)]
    (.setAttribute el "blur" "80")
    (append! el)
    (let [backdrop (shadow-part el "[part=backdrop]")]
      (testing "applies blur filter"
        (is (= "blur(80px)" (.getPropertyValue (.-style backdrop) "filter")))))))

;; ── Opacity attribute ────────────────────────────────────────────────────

(deftest opacity-attribute-test
  (let [el (make-el)]
    (.setAttribute el "opacity" "0.5")
    (append! el)
    (let [backdrop (shadow-part el "[part=backdrop]")]
      (testing "applies opacity"
        (is (= "0.5" (.getPropertyValue (.-style backdrop) "opacity")))))))

;; ── Blend attribute ──────────────────────────────────────────────────────

(deftest blend-attribute-test
  (let [el (make-el)]
    (.setAttribute el "blend" "multiply")
    (append! el)
    (let [backdrop (shadow-part el "[part=backdrop]")]
      (testing "applies blend mode"
        (is (= "multiply" (.getPropertyValue (.-style backdrop) "mix-blend-mode")))))))

;; ── Animation attribute ──────────────────────────────────────────────────

(deftest animation-attribute-test
  (let [el (append! (make-el))]
    (testing "default animation is float"
      (is (= "float" (.getAttribute el "data-animation"))))

    (.setAttribute el "animation" "none")
    (testing "can set to none"
      (is (= "none" (.getAttribute el "data-animation"))))))

;; ── Paused attribute ─────────────────────────────────────────────────────

(deftest paused-attribute-test
  (let [el (append! (make-el))]
    (testing "not paused by default"
      (is (not (.hasAttribute el "data-paused"))))

    (.setAttribute el "paused" "")
    (testing "paused when attribute set"
      (is (.hasAttribute el "data-paused")))))

;; ── Property accessors ───────────────────────────────────────────────────

(deftest property-accessors-test
  (let [el (append! (make-el))]
    (testing "colors property reflects attribute"
      (aset el "colors" "red, blue")
      (is (= "red, blue" (.getAttribute el "colors")))
      (is (= "red, blue" (aget el "colors"))))

    (testing "blur property reflects attribute"
      (aset el "blur" "80")
      (is (= "80" (.getAttribute el "blur"))))

    (testing "count property reflects attribute"
      (aset el "count" "7")
      (is (= "7" (.getAttribute el "count"))))

    (testing "paused boolean property"
      (aset el "paused" true)
      (is (.hasAttribute el "paused"))
      (aset el "paused" false)
      (is (not (.hasAttribute el "paused"))))))

;; ── Accessibility ────────────────────────────────────────────────────────

(deftest a11y-test
  (let [el (append! (make-el))]
    (testing "decorative by default — role=presentation, aria-hidden"
      (is (= "presentation" (.getAttribute el "role")))
      (is (= "true" (.getAttribute el "aria-hidden"))))))

;; ── Attribute change triggers re-render ──────────────────────────────────

(deftest attribute-change-rerender-test
  (let [el (append! (make-el))
        backdrop (shadow-part el "[part=backdrop]")]
    (.setAttribute el "count" "2")
    (testing "changing count updates blob divs"
      (is (= 2 (.-length (.querySelectorAll backdrop ".blob")))))

    (.setAttribute el "count" "8")
    (testing "increasing count adds blobs"
      (is (= 8 (.-length (.querySelectorAll backdrop ".blob")))))))
