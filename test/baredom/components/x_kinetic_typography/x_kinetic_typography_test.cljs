(ns baredom.components.x-kinetic-typography.x-kinetic-typography-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [clojure.string :as str]
            [baredom.components.x-kinetic-typography.x-kinetic-typography :as xkt]
            [baredom.components.x-kinetic-typography.model :as model]))

(xkt/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-el [] (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-query [el sel]
  (.querySelector (.-shadowRoot el) sel))

(defn shadow-query-all [el sel]
  (.querySelectorAll (.-shadowRoot el) sel))

;; ── Registration ─────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────
(deftest shadow-root-exists-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest svg-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=svg]")))))

(deftest text-part-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=text]")))))

(deftest sr-only-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=sr-only]")))))

(deftest container-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=container]")))))

(deftest crawl-viewport-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=crawl-viewport]")))))

(deftest crawl-text-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=crawl-text]")))))

;; ── Default state ────────────────────────────────────────────────────────
(deftest default-a11y-empty-text-test
  (let [el (append! (make-el))]
    (is (= "presentation" (.getAttribute el "role")))
    (is (= "true" (.getAttribute el "aria-hidden")))))

(deftest svg-is-aria-hidden-test
  (let [el (append! (make-el))]
    (is (= "true" (.getAttribute (shadow-query el "[part=svg]") "aria-hidden")))))

;; ── Text attribute ───────────────────────────────────────────────────────
(deftest text-attr-updates-textpath-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello World")
    (let [tp (shadow-query el "textPath")]
      (is (= "Hello World" (.-textContent tp))))))

(deftest text-attr-updates-sr-only-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello World")
    (is (= "Hello World" (.-textContent (shadow-query el "[part=sr-only]"))))))

(deftest text-attr-sets-aria-label-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello")
    (is (= "img" (.getAttribute el "role")))
    (is (= "Hello" (.getAttribute el "aria-label")))))

(deftest empty-text-sets-presentation-role-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello")
    (.setAttribute el model/attr-text "")
    (is (= "presentation" (.getAttribute el "role")))))

;; ── Preset attribute ─────────────────────────────────────────────────────
(deftest preset-attr-updates-path-d-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-preset "circle")
    (let [path-el (shadow-query el "defs path")]
      (is (= (:d (get model/path-presets "circle"))
             (.getAttribute path-el "d"))))))

(deftest invalid-preset-falls-back-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-preset "hexagon")
    (let [path-el (shadow-query el "defs path")]
      (is (= (:d (get model/path-presets "wave"))
             (.getAttribute path-el "d"))))))

;; ── Custom path attribute ────────────────────────────────────────────────
(deftest custom-path-overrides-preset-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L100,100")
    (let [path-el (shadow-query el "defs path")]
      (is (= "M0,0 L100,100" (.getAttribute path-el "d"))))))

(deftest removing-path-restores-preset-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-path "M0,0 L100,100")
    (.removeAttribute el model/attr-path)
    (let [path-el (shadow-query el "defs path")]
      (is (= (:d (get model/path-presets "wave"))
             (.getAttribute path-el "d"))))))

;; ── Animation attribute ──────────────────────────────────────────────────
(deftest animation-scroll-sets-data-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "scroll")
    (is (= "scroll" (.getAttribute el "data-animation")))))

(deftest animation-none-removes-data-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-animation "scroll")
    (.setAttribute el model/attr-animation "none")
    (is (not (.hasAttribute el "data-animation")))))

(deftest animation-creates-smil-animate-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "scroll")
    (is (some? (shadow-query el "textPath animate")))))

(deftest animation-none-removes-smil-animate-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "scroll")
    (.setAttribute el model/attr-animation "none")
    (is (nil? (shadow-query el "textPath animate")))))

;; ── Effect attribute ─────────────────────────────────────────────────────
(deftest effect-attr-sets-data-effect-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-effect "opacity-wave")
    (is (.hasAttribute el "data-effect"))
    (is (str/includes? (.getAttribute el "data-effect") "opacity-wave"))))

(deftest effect-attr-multiple-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-effect "opacity-wave color-shift")
    (let [val (.getAttribute el "data-effect")]
      (is (str/includes? val "opacity-wave"))
      (is (str/includes? val "color-shift")))))

(deftest effect-none-removes-data-effect-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-effect "opacity-wave")
    (.setAttribute el model/attr-effect "none")
    (is (not (.hasAttribute el "data-effect")))))

;; ── Speed attribute ──────────────────────────────────────────────────────
(deftest speed-attr-affects-animate-dur-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "scroll")
    (.setAttribute el model/attr-speed "2")
    (let [anim (shadow-query el "textPath animate")]
      (when anim
        (is (= "5s" (.getAttribute anim "dur")))))))

;; ── Repeat attribute ─────────────────────────────────────────────────────
(deftest repeat-attr-repeats-text-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hi")
    (.setAttribute el model/attr-repeat "3")
    (let [tp (shadow-query el "textPath")]
      (is (str/includes? (.-textContent tp) "Hi")))))

;; ── Property accessors ───────────────────────────────────────────────────
(deftest text-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-text el) "Hello")
    (is (= "Hello" (.getAttribute el model/attr-text)))))

(deftest preset-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-preset el) "circle")
    (is (= "circle" (.getAttribute el model/attr-preset)))))

(deftest animation-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-animation el) "bounce")
    (is (= "bounce" (.getAttribute el model/attr-animation)))))

(deftest speed-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-speed el) "2.5")
    (is (= "2.5" (.getAttribute el model/attr-speed)))))

(deftest direction-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-direction el) "reverse")
    (is (= "reverse" (.getAttribute el model/attr-direction)))))

(deftest effect-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-effect el) "opacity-wave color-shift")
    (is (= "opacity-wave color-shift" (.getAttribute el model/attr-effect)))))

(deftest font-size-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-fontSize el) "32px")
    (is (= "32px" (.getAttribute el model/attr-font-size)))))

(deftest setting-empty-string-removes-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-preset el) "circle")
    (set! (.-preset el) "")
    (is (not (.hasAttribute el model/attr-preset)))))

(deftest echo-count-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-echo-count "3")
    (is (= "3" (.getAttribute el model/attr-echo-count)))))

;; ── Property getters ─────────────────────────────────────────────────────
(deftest text-property-getter-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-text "World")
    (is (= "World" (.-text el)))))

(deftest preset-property-getter-default-test
  (let [el ^js (append! (make-el))]
    (is (= "wave" (.-preset el)))))

(deftest animation-property-getter-default-test
  (let [el ^js (append! (make-el))]
    (is (= "scroll" (.-animation el)))))

;; ── Lifecycle ────────────────────────────────────────────────────────────
(deftest reconnect-preserves-rendering-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Reconnect")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "Reconnect" (.-textContent (shadow-query el "textPath"))))))

;; ── Decorative path line ─────────────────────────────────────────────────
(deftest path-line-part-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-query el "[part=path-line]")))))

(deftest path-line-mirrors-path-d-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-preset "circle")
    (let [path-line (shadow-query el "[part=path-line]")]
      (is (= (:d (get model/path-presets "circle"))
             (.getAttribute path-line "d"))))))

;; ── Crawl mode ───────────────────────────────────────────────────────────
(deftest crawl-sets-data-preset-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "A long time ago")
    (.setAttribute el model/attr-preset "crawl")
    (is (= "crawl" (.getAttribute el "data-preset")))))

(deftest crawl-updates-crawl-text-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "A long time ago")
    (.setAttribute el model/attr-preset "crawl")
    (is (= "A long time ago"
           (.-textContent (shadow-query el "[part=crawl-text]"))))))

(deftest crawl-removes-smil-animate-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "scroll")
    (.setAttribute el model/attr-preset "crawl")
    (is (nil? (shadow-query el "textPath animate")))))

(deftest switching-from-crawl-restores-svg-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello")
    (.setAttribute el model/attr-preset "crawl")
    (.setAttribute el model/attr-preset "wave")
    (is (not (.hasAttribute el "data-preset")))
    (is (= "Hello" (.-textContent (shadow-query el "textPath"))))))

;; ── Color wave effect ────────────────────────────────────────────────────
(deftest color-wave-creates-tspans-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hi")
    (.setAttribute el model/attr-effect "color-wave")
    (let [tspans (shadow-query-all el "textPath tspan")]
      (is (= 2 (.-length tspans))))))

(deftest color-wave-tspans-have-animation-style-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "AB")
    (.setAttribute el model/attr-effect "color-wave")
    (let [tspan (shadow-query el "textPath tspan")]
      (is (str/includes? (.-cssText (.-style tspan)) "xkt-color-wave")))))

;; ── Echo/trail ───────────────────────────────────────────────────────────
(deftest echo-count-creates-text-elements-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Echo")
    (.setAttribute el model/attr-echo-count "2")
    (let [echoes (shadow-query-all el "[part=text-echo]")]
      (is (= 2 (.-length echoes))))))

(deftest echo-count-zero-no-echoes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Echo")
    (.setAttribute el model/attr-echo-count "0")
    (let [echoes (shadow-query-all el "[part=text-echo]")]
      (is (= 0 (.-length echoes))))))

(deftest echo-elements-have-opacity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Echo")
    (.setAttribute el model/attr-echo-count "1")
    (let [echo (shadow-query el "[part=text-echo]")]
      (is (some? echo))
      (when echo
        (is (some? (.getAttribute echo "opacity")))))))

(deftest echo-elements-contain-same-text-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Trail")
    (.setAttribute el model/attr-echo-count "1")
    (let [echo (shadow-query el "[part=text-echo]")]
      (is (some? echo))
      (when echo
        (is (str/includes? (.-textContent echo) "Trail"))))))

(deftest removing-echo-count-clears-echoes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Echo")
    (.setAttribute el model/attr-echo-count "2")
    (.removeAttribute el model/attr-echo-count)
    (let [echoes (shadow-query-all el "[part=text-echo]")]
      (is (= 0 (.-length echoes))))))

(deftest crawl-mode-ignores-echoes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Crawl")
    (.setAttribute el model/attr-preset "crawl")
    (.setAttribute el model/attr-echo-count "2")
    (let [echoes (shadow-query-all el "[part=text-echo]")]
      (is (= 0 (.-length echoes))))))

;; ── Size gradient effect ────────────────────────────────────────────────
(deftest size-gradient-creates-tspans-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "AB")
    (.setAttribute el model/attr-start-size "10px")
    (.setAttribute el model/attr-end-size "30px")
    (let [tspans (shadow-query-all el "textPath tspan")]
      (is (= 2 (.-length tspans))))))

(deftest size-gradient-tspans-have-font-size-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "AB")
    (.setAttribute el model/attr-start-size "10px")
    (.setAttribute el model/attr-end-size "30px")
    (let [tspans (shadow-query-all el "textPath tspan")
          first-tspan  (aget tspans 0)
          second-tspan (aget tspans 1)]
      (is (= "10px" (.getAttribute first-tspan "font-size")))
      (is (= "30px" (.getAttribute second-tspan "font-size"))))))

;; ── Direction reverse ───────────────────────────────────────────────────
(deftest direction-reverse-scroll-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "scroll")
    (.setAttribute el model/attr-direction "reverse")
    (let [anim (shadow-query el "textPath animate")]
      (when anim
        (is (= "100%" (.getAttribute anim "from")))
        (is (= "0%"   (.getAttribute anim "to")))))))

(deftest direction-reverse-bounce-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Test")
    (.setAttribute el model/attr-animation "bounce")
    (.setAttribute el model/attr-direction "reverse")
    (let [anim (shadow-query el "textPath animate")]
      (when anim
        (is (= "100%;0%;100%" (.getAttribute anim "values")))))))

;; ── Repeat text with separator ──────────────────────────────────────────
(deftest repeat-attr-repeats-with-separator-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-text "Hi")
    (.setAttribute el model/attr-repeat "3")
    (let [tp      (shadow-query el "textPath")
          content (.-textContent tp)]
      (is (= 3 (count (re-seq #"Hi" content))))
      (is (str/includes? content "\u00B7")))))

;; ── Remaining property accessors ────────────────────────────────────────
(deftest echo-delay-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-echoDelay el) "0.5")
    (is (= "0.5" (.getAttribute el model/attr-echo-delay)))))

(deftest echo-opacity-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-echoOpacity el) "0.7")
    (is (= "0.7" (.getAttribute el model/attr-echo-opacity)))))

(deftest echo-scale-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-echoScale el) "0.9")
    (is (= "0.9" (.getAttribute el model/attr-echo-scale)))))

(deftest start-size-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-startSize el) "12px")
    (is (= "12px" (.getAttribute el model/attr-start-size)))))

(deftest end-size-property-reflects-attr-test
  (let [el ^js (append! (make-el))]
    (set! (.-endSize el) "48px")
    (is (= "48px" (.getAttribute el model/attr-end-size)))))
