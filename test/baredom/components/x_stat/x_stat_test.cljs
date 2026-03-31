(ns baredom.components.x-stat.x-stat-test
  (:require
   [cljs.test :refer-macros [deftest is testing use-fixtures]]
   [baredom.components.x-stat.x-stat :as x]
   [baredom.components.x-stat.model :as model]))

(x/init!)

(defn cleanup! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures
 :each
 {:before cleanup!
  :after cleanup!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow [el sel]
  (.querySelector (.-shadowRoot el) sel))

;; ── Registration ────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────

(deftest shadow-dom-parts-test
  (let [el (append! (make-el))]
    (testing "all named parts exist"
      (is (some? (shadow el "[part='base']")))
      (is (some? (shadow el "[part='icon']")))
      (is (some? (shadow el "[part='body']")))
      (is (some? (shadow el "[part='label']")))
      (is (some? (shadow el "[part='value']")))
      (is (some? (shadow el "[part='hint']"))))))

(deftest shadow-dom-slots-test
  (let [el (append! (make-el))]
    (testing "named slots exist"
      (is (some? (shadow el "slot[name='icon']")))
      (is (some? (shadow el "slot[name='label']")))
      (is (some? (shadow el "slot[name='value']")))
      (is (some? (shadow el "slot[name='hint']"))))
    (testing "default slot exists"
      (is (some? (shadow el "slot:not([name])"))))))

;; ── Default state ───────────────────────────────────────────────────────────

(deftest default-state-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (testing "default data attributes on base"
      (is (= "default" (.getAttribute base "data-variant")))
      (is (= "start" (.getAttribute base "data-align")))
      (is (= "md" (.getAttribute base "data-size")))
      (is (= "normal" (.getAttribute base "data-emphasis")))
      (is (= "neutral" (.getAttribute base "data-trend")))
      (is (nil? (.getAttribute base "data-loading"))))
    (testing "default host a11y"
      (is (= "figure" (.getAttribute el "role")))
      (is (nil? (.getAttribute el "aria-busy"))))))

;; ── Attribute rendering ─────────────────────────────────────────────────────

(deftest variant-attribute-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "variant" "positive")
    (is (= "positive" (.getAttribute base "data-variant")))))

(deftest align-attribute-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "align" "center")
    (is (= "center" (.getAttribute base "data-align")))))

(deftest size-attribute-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "size" "lg")
    (is (= "lg" (.getAttribute base "data-size")))))

(deftest emphasis-attribute-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "emphasis" "high")
    (is (= "high" (.getAttribute base "data-emphasis")))))

(deftest trend-attribute-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "trend" "up")
    (is (= "up" (.getAttribute base "data-trend")))))

(deftest invalid-enum-fallback-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (.setAttribute el "variant" "bogus")
    (is (= "default" (.getAttribute base "data-variant")))))

;; ── Text content ────────────────────────────────────────────────────────────

(deftest label-text-test
  (let [el (make-el)]
    (.setAttribute el "label" "Revenue")
    (let [el (append! el)]
      (is (= "Revenue" (.-textContent (shadow el "[part='label'] span")))))))

(deftest value-text-test
  (let [el (make-el)]
    (.setAttribute el "value" "$128K")
    (let [el (append! el)]
      (is (= "$128K" (.-textContent (shadow el "[part='value'] span")))))))

(deftest hint-text-test
  (let [el (make-el)]
    (.setAttribute el "hint" "Quarter to date")
    (let [el (append! el)]
      (is (= "Quarter to date" (.-textContent (shadow el "[part='hint'] span")))))))

(deftest empty-text-renders-empty-string-test
  (let [el (append! (make-el))]
    (is (= "" (.-textContent (shadow el "[part='label'] span"))))
    (is (= "" (.-textContent (shadow el "[part='value'] span"))))
    (is (= "" (.-textContent (shadow el "[part='hint'] span"))))))

;; ── Loading state ───────────────────────────────────────────────────────────

(deftest loading-state-test
  (let [el (make-el)]
    (.setAttribute el "loading" "")
    (let [el (append! el)
          base (shadow el "[part='base']")]
      (is (= "true" (.getAttribute base "data-loading")))
      (is (= "true" (.getAttribute el "aria-busy"))))))

(deftest loading-removed-test
  (let [el (make-el)]
    (.setAttribute el "loading" "")
    (let [el (append! el)]
      (.removeAttribute el "loading")
      (let [base (shadow el "[part='base']")]
        (is (nil? (.getAttribute base "data-loading")))
        (is (nil? (.getAttribute el "aria-busy")))))))

;; ── Aria attributes ─────────────────────────────────────────────────────────

(deftest aria-label-from-label-test
  (let [el (make-el)]
    (.setAttribute el "label" "Revenue")
    (let [el (append! el)]
      (is (= "Revenue" (.getAttribute el "aria-label"))))))

(deftest aria-label-absent-when-no-label-test
  (let [el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-label")))))

;; ── Property accessors ──────────────────────────────────────────────────────

(deftest loading-property-getter-test
  (let [el (append! (make-el))]
    (is (false? (.-loading el)))
    (.setAttribute el "loading" "")
    (is (true? (.-loading el)))))

(deftest loading-property-setter-test
  (let [el (append! (make-el))]
    (set! (.-loading el) true)
    (is (.hasAttribute el "loading"))
    (set! (.-loading el) false)
    (is (not (.hasAttribute el "loading")))))

(deftest string-property-getter-test
  (let [el (append! (make-el))]
    (.setAttribute el "variant" "positive")
    (is (= "positive" (.-variant el)))
    (.setAttribute el "label" "Test")
    (is (= "Test" (.-label el)))))

(deftest string-property-setter-test
  (let [el (append! (make-el))]
    (set! (.-variant el) "warning")
    (is (= "warning" (.getAttribute el "variant")))
    (set! (.-label el) "Revenue")
    (is (= "Revenue" (.getAttribute el "label")))))

(deftest string-property-default-test
  (let [el (append! (make-el))]
    (is (= "" (.-variant el)))
    (is (= "" (.-label el)))
    (is (= "" (.-value el)))
    (is (= "" (.-hint el)))))
