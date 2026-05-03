(ns baredom.components.x-kinetic-canvas.x-kinetic-canvas-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [baredom.components.x-kinetic-canvas.x-kinetic-canvas :as x-kinetic-canvas]
   [baredom.components.x-kinetic-canvas.model :as model]))

;; Register element once
(x-kinetic-canvas/init!)

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- make-el [] (.createElement js/document model/tag-name))

(defn- append! [^js el]
  ;; Give it dimensions so ResizeObserver fires
  (set! (.. el -style -width) "400px")
  (set! (.. el -style -height) "300px")
  (.appendChild (.-body js/document) el)
  el)

(defn- shadow-q [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Fixtures ────────────────────────────────────────────────────────────────

(defn- cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each
  {:before cleanup-dom!
   :after  cleanup-dom!})

;; ── Registration ────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────

(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-q el "canvas")))
    (is (some? (shadow-q el "slot")))
    (is (some? (shadow-q el ".kc-canvas-wrap")))
    (is (some? (shadow-q el ".kc-content")))))

;; ── Canvas has aria-hidden ──────────────────────────────────────────────────

(deftest canvas-aria-hidden-test
  (let [^js el     (append! (make-el))
        ^js canvas (shadow-q el "canvas")]
    (is (= "true" (.getAttribute canvas "aria-hidden")))))

;; ── Default property values ─────────────────────────────────────────────────

(deftest default-type-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-type el)))))

(deftest default-fullscreen-test
  (let [^js el (append! (make-el))]
    (is (false? (.-fullscreen el)))))

(deftest default-paused-test
  (let [^js el (append! (make-el))]
    (is (false? (.-paused el)))))

;; ── Property reflection: type ───────────────────────────────────────────────

(deftest type-property-test
  (let [^js el (append! (make-el))]
    (set! (.-type el) "bubbles")
    (is (= "bubbles" (.getAttribute el model/attr-type)))
    (is (= "bubbles" (.-type el)))))

;; ── Property reflection: variant ────────────────────────────────────────────

(deftest variant-property-test
  (let [^js el (append! (make-el))]
    (set! (.-variant el) "twinkle")
    (is (= "twinkle" (.getAttribute el model/attr-variant)))
    (is (= "twinkle" (.-variant el)))))

;; ── Property reflection: speed ──────────────────────────────────────────────

(deftest speed-property-test
  (let [^js el (append! (make-el))]
    (set! (.-speed el) "fast")
    (is (= "fast" (.getAttribute el model/attr-speed)))
    (is (= "fast" (.-speed el)))))

;; ── Property reflection: density ────────────────────────────────────────────

(deftest density-property-test
  (let [^js el (append! (make-el))]
    (set! (.-density el) "high")
    (is (= "high" (.getAttribute el model/attr-density)))
    (is (= "high" (.-density el)))))

;; ── Property reflection: fullscreen ─────────────────────────────────────────

(deftest fullscreen-property-test
  (let [^js el (append! (make-el))]
    (set! (.-fullscreen el) true)
    (is (true? (.hasAttribute el model/attr-fullscreen)))
    (is (true? (.-fullscreen el)))
    (set! (.-fullscreen el) false)
    (is (false? (.hasAttribute el model/attr-fullscreen)))
    (is (false? (.-fullscreen el)))))

;; ── Property reflection: paused ─────────────────────────────────────────────

(deftest paused-property-test
  (let [^js el (append! (make-el))]
    (set! (.-paused el) true)
    (is (true? (.hasAttribute el model/attr-paused)))
    (is (true? (.-paused el)))
    (set! (.-paused el) false)
    (is (false? (.hasAttribute el model/attr-paused)))
    (is (false? (.-paused el)))))

;; ── Type change via attribute ───────────────────────────────────────────────

(deftest type-attribute-change-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "matrix")
    (is (= "matrix" (.-type el)))))
