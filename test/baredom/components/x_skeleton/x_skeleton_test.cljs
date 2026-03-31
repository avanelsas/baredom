(ns baredom.components.x-skeleton.x-skeleton-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-skeleton.x-skeleton :as x-skeleton]
            [baredom.components.x-skeleton.model :as model]))

(x-skeleton/init!)

(defn cleanup-fixture [f]
  (f)
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each cleanup-fixture)

(defn make-el [] (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-base [el]
  (.querySelector (.-shadowRoot el) "[part=base]"))

(defn shadow-shimmer [el]
  (.querySelector (.-shadowRoot el) "[part=shimmer]"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-data-attributes-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (is (= "rect"  (.getAttribute base "data-variant")))
    (is (= "pulse" (.getAttribute base "data-animation")))))

(deftest aria-hidden-set-on-host-test
  (let [el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── Variant attribute ─────────────────────────────────────────────────────
(deftest variant-attr-sets-data-attribute-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-variant "circle")
    (is (= "circle" (.getAttribute base "data-variant")))
    (.setAttribute el model/attr-variant "text")
    (is (= "text" (.getAttribute base "data-variant")))))

(deftest invalid-variant-falls-back-to-rect-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-variant "oval")
    (is (= "rect" (.getAttribute base "data-variant")))))

;; ── Animation attribute ───────────────────────────────────────────────────
(deftest animation-attr-sets-data-attribute-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-animation "wave")
    (is (= "wave" (.getAttribute base "data-animation")))
    (.setAttribute el model/attr-animation "none")
    (is (= "none" (.getAttribute base "data-animation")))))

(deftest invalid-animation-falls-back-to-pulse-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-animation "shimmer")
    (is (= "pulse" (.getAttribute base "data-animation")))))

;; ── Width / height attributes ─────────────────────────────────────────────
(deftest width-attr-applies-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-width "120px")
    (is (= "120px" (.getPropertyValue (.-style base) "width")))))

(deftest height-attr-applies-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-height "3rem")
    (is (= "3rem" (.getPropertyValue (.-style base) "height")))))

(deftest removing-width-attr-clears-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-width "80px")
    (.removeAttribute el model/attr-width)
    (is (= "" (.getPropertyValue (.-style base) "width")))))

(deftest removing-height-attr-clears-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-height "80px")
    (.removeAttribute el model/attr-height)
    (is (= "" (.getPropertyValue (.-style base) "height")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest variant-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-variant el) "text")
    (is (.hasAttribute el model/attr-variant))
    (is (= "text" (.getAttribute el model/attr-variant)))))

(deftest animation-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-animation el) "wave")
    (is (= "wave" (.getAttribute el model/attr-animation)))))

(deftest width-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-width el) "200px")
    (is (= "200px" (.getAttribute el model/attr-width)))))

(deftest height-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-height el) "50px")
    (is (= "50px" (.getAttribute el model/attr-height)))))

(deftest setting-empty-string-property-removes-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-width el) "100px")
    (set! (.-width el) "")
    (is (not (.hasAttribute el model/attr-width)))))

;; ── Property getters ─────────────────────────────────────────────────────
(deftest variant-property-getter-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-variant "circle")
    (is (= "circle" (.-variant el)))))

(deftest animation-property-getter-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-animation "wave")
    (is (= "wave" (.-animation el)))))

(deftest width-property-getter-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-width "100px")
    (is (= "100px" (.-width el)))))

(deftest height-property-getter-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-height "2rem")
    (is (= "2rem" (.-height el)))))

(deftest property-getter-returns-empty-when-no-attribute-test
  (let [el ^js (append! (make-el))]
    (is (= "" (.-width el)))
    (is (= "" (.-height el)))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-root-exists-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest shimmer-part-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-shimmer el)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(deftest reconnect-preserves-rendering-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-variant "circle")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "circle" (.getAttribute (shadow-base el) "data-variant")))))
