(ns baredom.components.x-organic-shape.x-organic-shape-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-organic-shape.x-organic-shape :as x-organic-shape]
            [baredom.components.x-organic-shape.model :as model]))

(x-organic-shape/init!)

(defn cleanup-fixture [f]
  (f)
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each cleanup-fixture)

(defn make-el [] (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-shape [el]
  (.querySelector (.-shadowRoot el) "[part=shape]"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-root-exists-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest shape-part-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-shape el)))))

(deftest slot-exists-test
  (let [el (append! (make-el))]
    (is (some? (.querySelector (.-shadowRoot el) "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-clip-path-applied-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (is (= (get-in model/shape-presets ["blob-1" :clip])
           (.getPropertyValue (.-style base) "clip-path")))))

(deftest default-aspect-ratio-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (is (= "1 / 1" (.getPropertyValue (.-style base) "aspect-ratio")))))

(deftest default-a11y-decorative-test
  (let [el (append! (make-el))]
    (is (= "presentation" (.getAttribute el "role")))
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── Shape attribute ───────────────────────────────────────────────────────
(deftest shape-attr-updates-clip-path-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-shape "leaf")
    (is (= (get-in model/shape-presets ["leaf" :clip])
           (.getPropertyValue (.-style base) "clip-path")))))

(deftest invalid-shape-falls-back-to-default-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-shape "hexagon")
    (is (= (get-in model/shape-presets ["blob-1" :clip])
           (.getPropertyValue (.-style base) "clip-path")))))

;; ── Path attribute ────────────────────────────────────────────────────────
(deftest custom-path-overrides-shape-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-path "circle(50%)")
    (is (= "circle(50%)" (.getPropertyValue (.-style base) "clip-path")))))

(deftest removing-path-restores-shape-preset-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-path "circle(50%)")
    (.removeAttribute el model/attr-path)
    (is (= (get-in model/shape-presets ["blob-1" :clip])
           (.getPropertyValue (.-style base) "clip-path")))))

;; ── Animation attribute ───────────────────────────────────────────────────
(deftest animation-attr-sets-data-attribute-test
  (doseq [anim ["morph" "pulse" "float" "spin"]]
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-animation anim)
      (is (= anim (.getAttribute el "data-animation"))))))

(deftest animation-none-removes-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "morph")
    (.setAttribute el model/attr-animation "none")
    (is (not (.hasAttribute el "data-animation")))))

(deftest animation-removed-clears-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "pulse")
    (.removeAttribute el model/attr-animation)
    (is (not (.hasAttribute el "data-animation")))))

(deftest morph-disabled-when-custom-path-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "circle(50%)")
    (.setAttribute el model/attr-animation "morph")
    (is (not (.hasAttribute el "data-animation")))))

(deftest non-morph-animations-work-with-custom-path-test
  (doseq [anim ["pulse" "float" "spin"]]
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-path "circle(50%)")
      (.setAttribute el model/attr-animation anim)
      (is (= anim (.getAttribute el "data-animation"))))))

;; ── Ratio attribute ───────────────────────────────────────────────────────
(deftest ratio-attr-updates-aspect-ratio-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-ratio "16/9")
    (is (= "16 / 9" (.getPropertyValue (.-style base) "aspect-ratio")))))

(deftest removing-ratio-restores-default-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-ratio "4/3")
    (.removeAttribute el model/attr-ratio)
    (is (= "1 / 1" (.getPropertyValue (.-style base) "aspect-ratio")))))

;; ── Width / height attributes ─────────────────────────────────────────────
(deftest width-attr-applies-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-width "200px")
    (is (= "200px" (.getPropertyValue (.-style base) "width")))))

(deftest height-attr-applies-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-height "150px")
    (is (= "150px" (.getPropertyValue (.-style base) "height")))))

(deftest removing-width-attr-clears-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-width "80px")
    (.removeAttribute el model/attr-width)
    (is (= "" (.getPropertyValue (.-style base) "width")))))

(deftest removing-height-attr-clears-inline-style-test
  (let [el   (append! (make-el))
        base (shadow-shape el)]
    (.setAttribute el model/attr-height "80px")
    (.removeAttribute el model/attr-height)
    (is (= "" (.getPropertyValue (.-style base) "height")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest shape-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-shape el) "droplet")
    (is (= "droplet" (.getAttribute el model/attr-shape)))))

(deftest path-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-path el) "circle(40%)")
    (is (= "circle(40%)" (.getAttribute el model/attr-path)))))

(deftest animation-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-animation el) "pulse")
    (is (= "pulse" (.getAttribute el model/attr-animation)))
    (set! (.-animation el) "")
    (is (not (.hasAttribute el model/attr-animation)))))

(deftest ratio-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-ratio el) "4/3")
    (is (= "4/3" (.getAttribute el model/attr-ratio)))))

(deftest width-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-width el) "200px")
    (is (= "200px" (.getAttribute el model/attr-width)))))

(deftest height-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-height el) "150px")
    (is (= "150px" (.getAttribute el model/attr-height)))))

(deftest setting-empty-string-property-removes-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-width el) "100px")
    (set! (.-width el) "")
    (is (not (.hasAttribute el model/attr-width)))))

;; ── Property getters ─────────────────────────────────────────────────────
(deftest shape-property-getter-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-shape "cloud")
    (is (= "cloud" (.-shape el)))))

(deftest path-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (nil? (.-path el)))
    (.setAttribute el model/attr-path "circle(50%)")
    (is (= "circle(50%)" (.-path el)))))

(deftest animation-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "none" (.-animation el)))
    (.setAttribute el model/attr-animation "float")
    (is (= "float" (.-animation el)))))

(deftest ratio-property-getter-default-test
  (let [el ^js (append! (make-el))]
    (is (= "1/1" (.-ratio el)))))

(deftest width-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "" (.-width el)))
    (.setAttribute el model/attr-width "200px")
    (is (= "200px" (.-width el)))))

(deftest height-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "" (.-height el)))
    (.setAttribute el model/attr-height "3rem")
    (is (= "3rem" (.-height el)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(deftest reconnect-preserves-rendering-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-shape "wave")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= (get-in model/shape-presets ["wave" :clip])
           (.getPropertyValue (.-style (shadow-shape el)) "clip-path")))))

;; ── Slotted content a11y ─────────────────────────────────────────────────
(deftest slotted-content-removes-decorative-role-test
  (cljs.test/async done
    (let [el   (append! (make-el))
          span (.createElement js/document "span")]
      (set! (.-textContent span) "Hello")
      (.appendChild el span)
      ;; slotchange fires asynchronously; give it a tick
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el "role")))
         (is (not (.hasAttribute el "aria-hidden")))
         (done))
       0))))
