(ns baredom.components.x-kinetic-font.x-kinetic-font-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [baredom.components.x-kinetic-font.x-kinetic-font :as x-kinetic-font]
   [baredom.components.x-kinetic-font.model :as model]))

;; Register element once
(x-kinetic-font/init!)

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- make-el [] (.createElement js/document model/tag-name))
(defn- append! [^js el] (.appendChild (.-body js/document) el) el)
(defn- shadow-part [^js el selector]
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
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=sr-only]")))))

;; ── Property reflection: text ───────────────────────────────────────────────

(deftest text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-text el) "Hello")
    (is (= "Hello" (.getAttribute el model/attr-text)))
    (is (= "Hello" (.-text el)))))

;; ── Property reflection: trigger ────────────────────────────────────────────

(deftest trigger-property-test
  (let [^js el (append! (make-el))]
    (set! (.-trigger el) "scroll")
    (is (= "scroll" (.getAttribute el model/attr-trigger)))
    (is (= "scroll" (.-trigger el)))))

;; ── Property reflection: mode ───────────────────────────────────────────────

(deftest mode-property-test
  (let [^js el (append! (make-el))]
    (set! (.-mode el) "lean stretch")
    (is (= "lean stretch" (.getAttribute el model/attr-mode)))
    (is (= "lean stretch" (.-mode el)))))

;; ── Property reflection: perChar ────────────────────────────────────────────

(deftest per-char-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-perChar el)))
    (set! (.-perChar el) true)
    (is (= "" (.getAttribute el model/attr-per-char)))
    (is (true? (.-perChar el)))
    (set! (.-perChar el) false)
    (is (nil? (.getAttribute el model/attr-per-char)))
    (is (false? (.-perChar el)))))

;; ── Property reflection: mass ───────────────────────────────────────────────

(deftest mass-property-test
  (let [^js el (append! (make-el))]
    (set! (.-mass el) 3)
    (is (= "3" (.getAttribute el model/attr-mass)))
    (is (= 3.0 (.-mass el)))))

;; ── Property reflection: tension ────────────────────────────────────────────

(deftest tension-property-test
  (let [^js el (append! (make-el))]
    (set! (.-tension el) 200)
    (is (= "200" (.getAttribute el model/attr-tension)))
    (is (= 200.0 (.-tension el)))))

;; ── Property reflection: friction ───────────────────────────────────────────

(deftest friction-property-test
  (let [^js el (append! (make-el))]
    (set! (.-friction el) 30)
    (is (= "30" (.getAttribute el model/attr-friction)))
    (is (= 30.0 (.-friction el)))))

;; ── Property reflection: intensity ──────────────────────────────────────────

(deftest intensity-property-test
  (let [^js el (append! (make-el))]
    (set! (.-intensity el) 0.8)
    (is (= "0.8" (.getAttribute el model/attr-intensity)))
    (is (= 0.8 (.-intensity el)))))

;; ── Property reflection: radius ─────────────────────────────────────────────

(deftest radius-property-test
  (let [^js el (append! (make-el))]
    (set! (.-radius el) 300)
    (is (= "300" (.getAttribute el model/attr-radius)))
    (is (= 300.0 (.-radius el)))))

;; ── Property reflection: fontFamily ─────────────────────────────────────────

(deftest font-family-property-test
  (let [^js el (append! (make-el))]
    (set! (.-fontFamily el) "Inter Variable")
    (is (= "Inter Variable" (.getAttribute el model/attr-font-family)))
    (is (= "Inter Variable" (.-fontFamily el)))))

;; ── Default property values ─────────────────────────────────────────────────

(deftest default-property-values-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-text el)))
    (is (= "cursor" (.-trigger el)))
    (is (= "bulge" (.-mode el)))
    (is (false? (.-perChar el)))
    (is (= 1.0 (.-mass el)))
    (is (= 170.0 (.-tension el)))
    (is (= 26.0 (.-friction el)))
    (is (= 0.5 (.-intensity el)))
    (is (= 200.0 (.-radius el)))
    (is (nil? (.-fontFamily el)))))

;; ── Null clears attribute ───────────────────────────────────────────────────

(deftest null-clears-text-test
  (let [^js el (append! (make-el))]
    (set! (.-text el) "Hello")
    (set! (.-text el) nil)
    (is (nil? (.getAttribute el model/attr-text)))
    (is (= "" (.-text el)))))

(deftest null-clears-mass-test
  (let [^js el (append! (make-el))]
    (set! (.-mass el) 5)
    (set! (.-mass el) nil)
    (is (nil? (.getAttribute el model/attr-mass)))
    (is (= 1.0 (.-mass el)))))

(deftest null-clears-font-family-test
  (let [^js el (append! (make-el))]
    (set! (.-fontFamily el) "Inter")
    (set! (.-fontFamily el) nil)
    (is (nil? (.getAttribute el model/attr-font-family)))
    (is (nil? (.-fontFamily el)))))

;; ── Per-char mode renders spans ─────────────────────────────────────────────

(deftest per-char-renders-spans-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "Hi")
    (.setAttribute el model/attr-per-char "")
    (append! el)
    (let [^js container (shadow-part el "[part=container]")
          spans (.querySelectorAll container "[part=char]")]
      (is (= 2 (.-length spans)))
      (is (= "H" (.-textContent (aget spans 0))))
      (is (= "i" (.-textContent (aget spans 1)))))))

;; ── Whole-text mode renders text directly ───────────────────────────────────

(deftest whole-text-renders-text-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "Hello")
    (append! el)
    (let [^js container (shadow-part el "[part=container]")]
      (is (= "Hello" (.-textContent container)))
      (is (= 0 (.-length (.querySelectorAll container "[part=char]")))))))

;; ── Accessibility: aria-label ───────────────────────────────────────────────

(deftest aria-label-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "Hello World")
    (append! el)
    (is (= "Hello World" (.getAttribute el "aria-label")))
    (is (= "text" (.getAttribute el "role")))))

(deftest empty-text-aria-hidden-test
  (let [^js el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── SR-only text ────────────────────────────────────────────────────────────

(deftest sr-only-text-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "Accessible")
    (append! el)
    (let [^js sr (shadow-part el "[part=sr-only]")]
      (is (= "Accessible" (.-textContent sr))))))

;; ── Text update rebuilds DOM ────────────────────────────────────────────────

(deftest text-update-rebuilds-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "AB")
    (.setAttribute el model/attr-per-char "")
    (append! el)
    (let [^js container (shadow-part el "[part=container]")]
      (is (= 2 (.-length (.querySelectorAll container "[part=char]"))))
      ;; Update text
      (.setAttribute el model/attr-text "XYZ")
      (is (= 3 (.-length (.querySelectorAll container "[part=char]")))))))

;; ── Whitespace chars get data-ws attribute ──────────────────────────────────

(deftest whitespace-data-ws-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-text "A B")
    (.setAttribute el model/attr-per-char "")
    (append! el)
    (let [^js container (shadow-part el "[part=container]")
          spans (.querySelectorAll container "[part=char]")]
      (is (= 3 (.-length spans)))
      (is (nil? (.getAttribute (aget spans 0) "data-ws")))
      (is (= "" (.getAttribute (aget spans 1) "data-ws")))
      (is (nil? (.getAttribute (aget spans 2) "data-ws"))))))
