(ns app.components.x-spacer.x-spacer-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-spacer.x-spacer :as x]
            [app.components.x-spacer.model    :as model]))

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

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest idempotent-registration-test
  (x/init!)
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-root-created-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest shadow-has-style-only-test
  (let [^js el     (append! (make-el))
        ^js root   (.-shadowRoot el)
        ^js style  (.querySelector root "style")]
    (is (some? style))
    ;; No other children besides the style element
    (is (= 1 (.-length (.-childNodes root))))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "vertical" (.getAttribute el "data-axis")))
    (is (= "false"    (.getAttribute el "data-grow")))
    (is (= "none"     (.getAttribute el "role")))
    (is (= "true"     (.getAttribute el "aria-hidden")))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-attribute-applied-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "2rem")
    (is (= "2rem" (.. el -style (getPropertyValue "--x-spacer-size"))))))

(deftest size-default-applied-test
  (let [^js el (append! (make-el))]
    (is (= "1rem" (.. el -style (getPropertyValue "--x-spacer-size"))))))

;; ── Axis attribute ────────────────────────────────────────────────────────
(deftest axis-vertical-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-axis "vertical")
    (is (= "vertical" (.getAttribute el "data-axis")))))

(deftest axis-horizontal-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-axis "horizontal")
    (is (= "horizontal" (.getAttribute el "data-axis")))))

(deftest axis-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-axis "diagonal")
    (is (= "vertical" (.getAttribute el "data-axis")))))

;; ── Grow attribute ────────────────────────────────────────────────────────
(deftest grow-present-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-grow "")
    (append! el)
    (is (= "true" (.getAttribute el "data-grow")))))

(deftest grow-absent-test
  (let [^js el (append! (make-el))]
    (is (= "false" (.getAttribute el "data-grow")))))

(deftest grow-removed-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-grow "")
    (append! el)
    (is (= "true" (.getAttribute el "data-grow")))
    (.removeAttribute el model/attr-grow)
    (is (= "false" (.getAttribute el "data-grow")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest size-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "1rem" (.-size el)))))

(deftest size-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-size el) "24px")
    (is (= "24px" (.getAttribute el model/attr-size)))))

(deftest axis-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "vertical" (.-axis el)))))

(deftest axis-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-axis el) "horizontal")
    (is (= "horizontal" (.getAttribute el model/attr-axis)))))

(deftest grow-property-false-by-default-test
  (let [^js el (append! (make-el))]
    (is (false? (.-grow el)))))

(deftest grow-property-true-when-set-test
  (let [^js el (append! (make-el))]
    (set! (.-grow el) true)
    (is (true? (.-grow el)))
    (is (some? (.getAttribute el model/attr-grow)))))

(deftest grow-property-false-removes-attribute-test
  (let [^js el (append! (make-el))]
    (set! (.-grow el) true)
    (set! (.-grow el) false)
    (is (false? (.-grow el)))
    (is (nil? (.getAttribute el model/attr-grow)))))

;; ── Reconnect stability ───────────────────────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-size "3rem")
    (.setAttribute el model/attr-axis "horizontal")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "horizontal" (.getAttribute el "data-axis")))
    (is (= "3rem" (.. el -style (getPropertyValue "--x-spacer-size"))))))

(deftest reconnect-no-duplicate-shadow-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (let [root1 (.-shadowRoot el)]
      (.remove el)
      (.appendChild (.-body js/document) el)
      (is (= root1 (.-shadowRoot el))))))
