(ns baredom.components.x-neural-glow.x-neural-glow-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-neural-glow.x-neural-glow :as x]
            [baredom.components.x-neural-glow.model          :as model]))

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

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=canvas]")))
    (is (some? (shadow-part el "style")))))

(deftest canvas-exists-test
  (let [^js el     (append! (make-el))
        ^js canvas (shadow-part el "canvas")]
    (is (some? canvas))
    (is (= "canvas" (.getAttribute canvas "part")))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest orb-count-property-test
  (let [^js el (append! (make-el))]
    (is (= 15 (.-orbCount el)))
    (set! (.-orbCount el) 20)
    (is (= "20" (.getAttribute el model/attr-orb-count)))
    (is (= 20 (.-orbCount el)))))

(deftest color-primary-property-test
  (let [^js el (append! (make-el))]
    (is (= "#4f8bff" (.-colorPrimary el)))
    (set! (.-colorPrimary el) "hotpink")
    (is (= "hotpink" (.getAttribute el model/attr-color-primary)))
    (is (= "hotpink" (.-colorPrimary el)))))

(deftest color-secondary-property-test
  (let [^js el (append! (make-el))]
    (is (= "#00e5cc" (.-colorSecondary el)))
    (set! (.-colorSecondary el) "lime")
    (is (= "lime" (.getAttribute el model/attr-color-secondary)))))

(deftest color-background-property-test
  (let [^js el (append! (make-el))]
    (is (= "#050a18" (.-colorBackground el)))
    (set! (.-colorBackground el) "#111")
    (is (= "#111" (.getAttribute el model/attr-color-background)))))

(deftest pulse-speed-property-test
  (let [^js el (append! (make-el))]
    (is (= 1.0 (.-pulseSpeed el)))
    (set! (.-pulseSpeed el) 2.5)
    (is (= "2.5" (.getAttribute el model/attr-pulse-speed)))
    (is (= 2.5 (.-pulseSpeed el)))))

(deftest rest-rate-property-test
  (let [^js el (append! (make-el))]
    (is (= 4.0 (.-restRate el)))
    (set! (.-restRate el) 6.0)
    (is (= "6" (.getAttribute el model/attr-rest-rate)))))

(deftest connection-distance-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.15 (.-connectionDistance el)))
    (set! (.-connectionDistance el) 0.3)
    (is (= "0.3" (.getAttribute el model/attr-connection-distance)))))

(deftest orb-size-property-test
  (let [^js el (append! (make-el))]
    (is (= 40 (.-orbSize el)))
    (set! (.-orbSize el) 80)
    (is (= "80" (.getAttribute el model/attr-orb-size)))))

(deftest opacity-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.8 (.-opacity el)))
    (set! (.-opacity el) 0.5)
    (is (= "0.5" (.getAttribute el model/attr-opacity)))))

(deftest interactive-property-test
  (let [^js el (append! (make-el))]
    (is (true? (.-interactive el)) "Default interactive is true")
    (set! (.-interactive el) false)
    (is (= "false" (.getAttribute el model/attr-interactive)))
    (is (false? (.-interactive el)))))

;; ── Accessibility ───────────────────────────────────────────────────────────
(deftest a11y-test
  (let [^js el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))
    (is (= "presentation" (.getAttribute el "role")))))

;; ── Reconnect: no duplicate canvases ────────────────────────────────────────
(deftest reconnect-no-duplicates-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (is (= 1 (.-length (.querySelectorAll (.-shadowRoot el) "canvas"))))
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= 1 (.-length (.querySelectorAll (.-shadowRoot el) "canvas"))))))
