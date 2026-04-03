(ns baredom.components.x-organic-divider.x-organic-divider-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-organic-divider.x-organic-divider :as x-organic-divider]
            [baredom.components.x-organic-divider.model :as model]))

(x-organic-divider/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-el [] (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-base [^js el]
  (.querySelector (.-shadowRoot el) "[part=base]"))

(defn shadow-svg [^js el]
  (.querySelector (.-shadowRoot el) "svg"))

(defn shadow-paths [^js el]
  (.querySelectorAll (.-shadowRoot el) "svg path"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-root-exists-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest base-part-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-base el)))))

(deftest svg-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-svg el)))))

(deftest svg-has-preserve-aspect-ratio-test
  (let [el  (append! (make-el))
        ^js svg (shadow-svg el)]
    (is (= "none" (.getAttribute svg "preserveAspectRatio")))))

(deftest svg-has-aria-hidden-test
  (let [el  (append! (make-el))
        ^js svg (shadow-svg el)]
    (is (= "true" (.getAttribute svg "aria-hidden")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-renders-one-path-test
  (let [el (append! (make-el))]
    (is (= 1 (.-length (shadow-paths el))))))

(deftest default-path-uses-wave-preset-test
  (let [el    (append! (make-el))
        ^js p (aget (shadow-paths el) 0)]
    (is (= (get-in model/shape-presets ["wave" :path])
           (.getAttribute p "d")))))

(deftest default-a11y-decorative-test
  (let [el (append! (make-el))]
    (is (= "presentation" (.getAttribute el "role")))
    (is (= "true" (.getAttribute el "aria-hidden")))))

(deftest default-height-applied-test
  (let [el   (append! (make-el))
        ^js base (shadow-base el)]
    (is (= "120px" (.getPropertyValue (.-style base) "height")))))

;; ── Shape attribute ───────────────────────────────────────────────────────
(deftest shape-attr-updates-path-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-shape "mountain")
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= (get-in model/shape-presets ["mountain" :path])
             (.getAttribute p "d"))))))

(deftest invalid-shape-falls-back-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-shape "zigzag")
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= (get-in model/shape-presets ["wave" :path])
             (.getAttribute p "d"))))))

;; ── Layers attribute ──────────────────────────────────────────────────────
(deftest layers-attr-creates-correct-paths-test
  (doseq [n [1 2 3 4 5]]
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-layers (str n))
      (is (= n (.-length (shadow-paths el)))))))

(deftest layers-second-path-has-transform-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-layers "3")
    (let [^js p1 (aget (shadow-paths el) 0)
          ^js p2 (aget (shadow-paths el) 1)
          ^js p3 (aget (shadow-paths el) 2)]
      (is (not (.hasAttribute p1 "transform")))
      (is (= "translate(20,8)" (.getAttribute p2 "transform")))
      (is (= "translate(40,16)" (.getAttribute p3 "transform"))))))

(deftest reducing-layers-removes-paths-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-layers "4")
    (is (= 4 (.-length (shadow-paths el))))
    (.setAttribute el model/attr-layers "2")
    (is (= 2 (.-length (shadow-paths el))))))

;; ── Height attribute ──────────────────────────────────────────────────────
(deftest height-attr-updates-style-test
  (let [el   (append! (make-el))
        ^js base (shadow-base el)]
    (.setAttribute el model/attr-height "80px")
    (is (= "80px" (.getPropertyValue (.-style base) "height")))))

;; ── Flip / Mirror ─────────────────────────────────────────────────────────
(deftest flip-attr-sets-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-flip "")
    (is (.hasAttribute el "data-flip"))))

(deftest mirror-attr-sets-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mirror "")
    (is (.hasAttribute el "data-mirror"))))

(deftest removing-flip-clears-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-flip "")
    (.removeAttribute el model/attr-flip)
    (is (not (.hasAttribute el "data-flip")))))

;; ── Animation attribute ───────────────────────────────────────────────────
(deftest animation-attr-sets-data-attribute-test
  (doseq [anim ["drift" "morph"]]
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-animation anim)
      (is (= anim (.getAttribute el "data-animation"))))))

(deftest animation-none-removes-data-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "drift")
    (.setAttribute el model/attr-animation "none")
    (is (not (.hasAttribute el "data-animation")))))

(deftest morph-disabled-when-custom-path-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L100,100 L0,100 Z")
    (.setAttribute el model/attr-animation "morph")
    (is (not (.hasAttribute el "data-animation")))))

(deftest drift-works-with-custom-path-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L100,100 L0,100 Z")
    (.setAttribute el model/attr-animation "drift")
    (is (= "drift" (.getAttribute el "data-animation")))))

(deftest drift-changes-viewbox-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "drift")
    (let [^js svg (shadow-svg el)]
      (is (= "0 0 1800 120" (.getAttribute svg "viewBox"))))))

(deftest drift-viewbox-restores-on-removal-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "drift")
    (.removeAttribute el model/attr-animation)
    (let [^js svg (shadow-svg el)]
      (is (= "0 0 1200 120" (.getAttribute svg "viewBox"))))))

(deftest morph-sets-css-custom-properties-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "morph")
    (let [^js p (aget (shadow-paths el) 0)]
      (is (not= "" (.getPropertyValue (.-style p) "--x-organic-divider-d")))
      (is (not= "" (.getPropertyValue (.-style p) "--x-organic-divider-d-alt"))))))

(deftest morph-clears-css-custom-properties-on-removal-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "morph")
    (.removeAttribute el model/attr-animation)
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= "" (.getPropertyValue (.-style p) "--x-organic-divider-d")))
      (is (= "" (.getPropertyValue (.-style p) "--x-organic-divider-d-alt"))))))

(deftest drift-uses-extended-path-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "drift")
    (let [^js p (aget (shadow-paths el) 0)
          d     (.getAttribute p "d")]
      (is (= d (get model/drift-presets "wave"))))))

;; ── Layer opacity ─────────────────────────────────────────────────────────
(deftest single-layer-has-full-opacity-test
  (let [el (append! (make-el))]
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= "1" (.getAttribute p "opacity"))))))

(deftest multi-layer-opacity-range-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-layers "3")
    (let [^js p0 (aget (shadow-paths el) 0)
          ^js p2 (aget (shadow-paths el) 2)]
      ;; First layer should be lowest opacity (0.3)
      (is (= "0.3" (.getAttribute p0 "opacity")))
      ;; Last layer should be full opacity (1)
      (is (= "1" (.getAttribute p2 "opacity"))))))

;; ── Custom path ───────────────────────────────────────────────────────────
(deftest custom-path-overrides-preset-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L1200,0 L1200,120 L0,120 Z")
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= "M0,0 L1200,0 L1200,120 L0,120 Z" (.getAttribute p "d"))))))

(deftest removing-path-restores-preset-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L100,100")
    (.removeAttribute el model/attr-path)
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= (get-in model/shape-presets ["wave" :path])
             (.getAttribute p "d"))))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest shape-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-shape el) "cloud")
    (is (= "cloud" (.getAttribute el model/attr-shape)))))

(deftest layers-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-layers el) "3")
    (is (= "3" (.getAttribute el model/attr-layers)))))

(deftest flip-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-flip el) true)
    (is (.hasAttribute el model/attr-flip))
    (set! (.-flip el) false)
    (is (not (.hasAttribute el model/attr-flip)))))

(deftest mirror-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-mirror el) true)
    (is (.hasAttribute el model/attr-mirror))
    (set! (.-mirror el) false)
    (is (not (.hasAttribute el model/attr-mirror)))))

(deftest animation-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-animation el) "drift")
    (is (= "drift" (.getAttribute el model/attr-animation)))
    (set! (.-animation el) "")
    (is (not (.hasAttribute el model/attr-animation)))))

(deftest height-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-height el) "80px")
    (is (= "80px" (.getAttribute el model/attr-height)))))

(deftest path-property-reflects-attribute-test
  (let [el ^js (append! (make-el))]
    (set! (.-path el) "M0,0 L100,100")
    (is (= "M0,0 L100,100" (.getAttribute el model/attr-path)))))

;; ── Property getters ─────────────────────────────────────────────────────
(deftest shape-property-getter-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-shape "scallop")
    (is (= "scallop" (.-shape el)))))

(deftest layers-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "1" (.-layers el)))
    (.setAttribute el model/attr-layers "4")
    (is (= "4" (.-layers el)))))

(deftest flip-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (false? (.-flip el)))
    (.setAttribute el model/attr-flip "")
    (is (true? (.-flip el)))))

(deftest animation-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "none" (.-animation el)))
    (.setAttribute el model/attr-animation "morph")
    (is (= "morph" (.-animation el)))))

(deftest height-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (= "120px" (.-height el)))
    (.setAttribute el model/attr-height "5rem")
    (is (= "5rem" (.-height el)))))

(deftest path-property-getter-test
  (let [el ^js (append! (make-el))]
    (is (nil? (.-path el)))
    (.setAttribute el model/attr-path "M0,0 L100,100")
    (is (= "M0,0 L100,100" (.-path el)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(deftest reconnect-preserves-rendering-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-shape "cloud")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [^js p (aget (shadow-paths el) 0)]
      (is (= (get-in model/shape-presets ["cloud" :path])
             (.getAttribute p "d"))))))
