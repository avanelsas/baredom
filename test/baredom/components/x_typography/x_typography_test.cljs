(ns baredom.components.x-typography.x-typography-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-typography.x-typography :as x]
            [baredom.components.x-typography.model        :as model]))

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

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (is (= "body1" (.getAttribute el "data-variant")))
    (is (= "left"  (.getAttribute el "data-align")))
    (is (not (.hasAttribute container "data-truncate")))))

;; ── Variant attribute ────────────────────────────────────────────────────────
(deftest variant-attribute-test
  (testing "h1"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant "h1")
      (is (= "h1" (.getAttribute el "data-variant")))))

  (testing "code"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant "code")
      (is (= "code" (.getAttribute el "data-variant")))))

  (testing "invalid falls back to body1"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant "invalid")
      (is (= "body1" (.getAttribute el "data-variant"))))))

;; ── Align attribute ──────────────────────────────────────────────────────────
(deftest align-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-align "center")
    (is (= "center" (.getAttribute el "data-align")))))

;; ── Truncate attribute ───────────────────────────────────────────────────────
(deftest truncate-attribute-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (.setAttribute el model/attr-truncate "")
    (is (.hasAttribute container "data-truncate"))
    (.removeAttribute el model/attr-truncate)
    (is (not (.hasAttribute container "data-truncate")))))

;; ── Line-clamp attribute ─────────────────────────────────────────────────────
(deftest line-clamp-attribute-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (.setAttribute el model/attr-line-clamp "3")
    (is (= "3" (.getPropertyValue (.-style container) "-webkit-line-clamp")))
    (.removeAttribute el model/attr-line-clamp)
    (is (= "" (.getPropertyValue (.-style container) "-webkit-line-clamp")))))

;; ── Truncate overrides line-clamp ────────────────────────────────────────────
(deftest truncate-overrides-line-clamp-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (.setAttribute el model/attr-line-clamp "3")
    (.setAttribute el model/attr-truncate "")
    ;; truncate wins: data-truncate is set, line-clamp inline styles are cleared
    (is (.hasAttribute container "data-truncate"))
    (is (= "" (.getPropertyValue (.-style container) "-webkit-line-clamp")))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest variant-property-test
  (let [^js el (append! (make-el))]
    (testing "default before any attribute is set"
      (is (= "body1" (.-variant el))))
    (testing "set and get"
      (set! (.-variant el) "h2")
      (is (= "h2" (.getAttribute el model/attr-variant)))
      (is (= "h2" (.-variant el))))
    (testing "set to nil removes attribute, getter returns default"
      (set! (.-variant el) nil)
      (is (not (.hasAttribute el model/attr-variant)))
      (is (= "body1" (.-variant el))))))

(deftest align-property-test
  (let [^js el (append! (make-el))]
    (testing "default before any attribute is set"
      (is (= "left" (.-align el))))
    (testing "set and get"
      (set! (.-align el) "right")
      (is (= "right" (.getAttribute el model/attr-align)))
      (is (= "right" (.-align el))))
    (testing "set to nil removes attribute, getter returns default"
      (set! (.-align el) nil)
      (is (not (.hasAttribute el model/attr-align)))
      (is (= "left" (.-align el))))))

(deftest truncate-property-test
  (let [^js el (append! (make-el))]
    (testing "default is false"
      (is (false? (.-truncate el))))
    (testing "set true / false"
      (set! (.-truncate el) true)
      (is (.hasAttribute el model/attr-truncate))
      (set! (.-truncate el) false)
      (is (not (.hasAttribute el model/attr-truncate))))))

(deftest line-clamp-property-test
  (let [^js el (append! (make-el))]
    (testing "default is nil"
      (is (nil? (.-lineClamp el))))
    (testing "set and get"
      (set! (.-lineClamp el) 5)
      (is (= "5" (.getAttribute el model/attr-line-clamp)))
      (is (= 5 (.-lineClamp el))))
    (testing "set to nil removes attribute"
      (set! (.-lineClamp el) nil)
      (is (not (.hasAttribute el model/attr-line-clamp)))
      (is (nil? (.-lineClamp el))))))

;; ── Invalid / edge-case attribute values at component level ─────────────────
(deftest invalid-line-clamp-attribute-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (testing "zero is ignored"
      (.setAttribute el model/attr-line-clamp "0")
      (is (= "" (.getPropertyValue (.-style container) "-webkit-line-clamp"))))
    (testing "negative is ignored"
      (.setAttribute el model/attr-line-clamp "-5")
      (is (= "" (.getPropertyValue (.-style container) "-webkit-line-clamp"))))
    (testing "non-numeric is ignored"
      (.setAttribute el model/attr-line-clamp "abc")
      (is (= "" (.getPropertyValue (.-style container) "-webkit-line-clamp"))))))

(deftest case-insensitive-variant-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-variant "H3")
    (is (= "h3" (.getAttribute el "data-variant")))))

(deftest case-insensitive-align-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-align "CENTER")
    (is (= "center" (.getAttribute el "data-align")))))

;; ── Slotted content ──────────────────────────────────────────────────────────
(deftest slotted-content-test
  (let [^js el (make-el)]
    (set! (.-textContent el) "Hello world")
    (append! el)
    (is (= "Hello world" (.-textContent el)))))

;; ── The font family ──────────────────────────────────────────────────────────

(defn- font-in!
  "The font family of a new element inside a wrapper with `style`. Removes the wrapper."
  [style]
  (let [wrapper (.createElement js/document "div")
        el      (make-el)]
    (.setAttribute wrapper "style" style)
    (.appendChild wrapper el)
    (.appendChild (.-body js/document) wrapper)
    (let [font (.-fontFamily (js/getComputedStyle el))]
      (.remove wrapper)
      font)))

(deftest the-text-takes-the-font-family-of-the-page
  (is (.includes (font-in! "font-family:PageFontTest,serif") "PageFontTest")))

(deftest the-text-takes-the-font-family-of-the-theme-over-the-page
  (is (.includes (font-in! "font-family:PageFontTest,serif;--x-font-family:ThemeFontTest")
                 "ThemeFontTest")))

;; ── Gradient ─────────────────────────────────────────────────────────────────
(def ^:private transparent "rgba(0, 0, 0, 0)")

(defn- ^js text-style [^js el]
  (js/getComputedStyle (shadow-part el "[part=text]")))

(defn- ^js append-text! [^js el text]
  (set! (.-textContent el) text)
  (append! el))

(deftest text-part-wraps-the-slot-test
  (let [^js el   (append! (make-el))
        ^js text (shadow-part el "[part=text]")]
    (is (= "SPAN" (.-tagName text)))
    (is (= "SLOT" (.-tagName (.-firstElementChild text))))
    (is (identical? (shadow-part el "[part=container]") (.-parentElement text)))))

(deftest without-gradient-the-text-has-no-fill-test
  (let [^js el (append-text! (make-el) "Plain")]
    (is (= "none" (.-backgroundImage (text-style el))))
    (is (not= transparent (.-color (text-style el))))))

(deftest gradient-fills-the-text-with-the-theme-gradient-test
  (let [^js el (make-el)]
    (.setAttribute el "gradient" "")
    (append-text! el "Gradient")
    (let [^js s (text-style el)]
      (is (re-find #"^linear-gradient" (.-backgroundImage s)))
      (is (= "text" (or (.-backgroundClip s) (.-webkitBackgroundClip s))))
      (is (= transparent (.-color s))))))

(deftest gradient-follows-the-theme-tokens-test
  (let [^js el (make-el)]
    (.setAttribute el "gradient" "")
    (.setProperty (.-style el) "--x-color-primary" "rgb(1, 2, 3)")
    (append-text! el "Themed")
    (is (re-find #"rgb\(1, 2, 3\)" (.-backgroundImage (text-style el))))))

(deftest custom-gradient-wins-over-the-theme-test
  (let [^js el (make-el)]
    (.setAttribute el "gradient" "")
    (.setProperty (.-style el) "--x-color-primary" "rgb(1, 2, 3)")
    (.setProperty (.-style el) "--x-typography-gradient"
                  "linear-gradient(90deg, rgb(10, 20, 30), rgb(40, 50, 60))")
    (append-text! el "Custom")
    (let [image (.-backgroundImage (text-style el))]
      (is (re-find #"rgb\(10, 20, 30\)" image))
      (is (not (re-find #"rgb\(1, 2, 3\)" image))))))

(deftest gradient-property-reflects-the-attribute-test
  (let [^js el (append-text! (make-el) "Toggle")]
    (is (false? (.-gradient el)))
    (set! (.-gradient el) true)
    (is (.hasAttribute el "gradient"))
    (is (= transparent (.-color (text-style el))))
    (set! (.-gradient el) false)
    (is (not (.hasAttribute el "gradient")))
    (is (= "none" (.-backgroundImage (text-style el))))))

(deftest slotted-block-gets-its-own-gradient-test
  (let [^js el (make-el)
        ^js p  (.createElement js/document "p")]
    (.setAttribute el "gradient" "")
    (set! (.-textContent p) "A paragraph")
    (.appendChild el p)
    (append! el)
    (let [^js s (js/getComputedStyle p)]
      (is (re-find #"^linear-gradient" (.-backgroundImage s)))
      (is (= "text" (or (.-backgroundClip s) (.-webkitBackgroundClip s)))))))

(deftest slotted-block-has-no-fill-without-gradient-test
  (let [^js el (make-el)
        ^js p  (.createElement js/document "p")]
    (set! (.-textContent p) "A paragraph")
    (.appendChild el p)
    (append! el)
    (is (= "none" (.-backgroundImage (js/getComputedStyle p))))))

(deftest host-selection-of-gradient-text-is-solid-on-a-tint-test
  (let [^js plain (append-text! (make-el) "Plain")
        ^js el    (make-el)]
    (.setAttribute el "gradient" "")
    (append-text! el "Select me")
    (is (= transparent (.-backgroundColor (js/getComputedStyle plain "::selection"))))
    (is (not= transparent (.-backgroundColor (js/getComputedStyle el "::selection"))))
    (is (not= transparent (.-color (js/getComputedStyle el "::selection"))))))

(deftest host-selection-takes-the-typography-colour-test
  (let [^js el (make-el)]
    (.setAttribute el "gradient" "")
    (.setProperty (.-style el) "--x-typography-color" "rgb(9, 8, 7)")
    (append-text! el "Select me")
    (is (= "rgb(9, 8, 7)" (.-color (js/getComputedStyle el "::selection"))))))
