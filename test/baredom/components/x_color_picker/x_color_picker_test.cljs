(ns baredom.components.x-color-picker.x-color-picker-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-color-picker.x-color-picker :as x]
            [baredom.components.x-color-picker.model          :as model]))

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
  (is (some? (.get js/customElements model/tag-name)))
  (is (= "x-color-picker" model/tag-name)))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=trigger]")))
    (is (some? (shadow-part el "[part=panel]")))
    (is (some? (shadow-part el "[part=area]")))
    (is (some? (shadow-part el "[part=area-thumb]")))
    (is (some? (shadow-part el "[part=hue-strip]")))
    (is (some? (shadow-part el "[part=hue-thumb]")))
    (is (some? (shadow-part el "[part=alpha-strip]")))
    (is (some? (shadow-part el "[part=alpha-thumb]")))
    (is (some? (shadow-part el "[part=controls]")))
    (is (some? (shadow-part el "[part=preview]")))
    (is (some? (shadow-part el "[part=hex-input]")))
    (is (some? (shadow-part el "[part=copy]")))
    (is (some? (shadow-part el "[part=swatches]")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))
        ^js hex-input (shadow-part el "[part=hex-input]")]
    (testing "default value is #000000"
      (is (= "#000000" (.-value hex-input))))
    (testing "default mode is inline"
      (is (= "inline" (.getAttribute el "data-mode"))))
    (testing "alpha strip hidden by default"
      (is (= "none" (.-display (.-style (shadow-part el "[part=alpha-strip]"))))))))

;; ── Value attribute ──────────────────────────────────────────────────────────
(deftest value-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value "#ff0000")
    (let [^js hex-input (shadow-part el "[part=hex-input]")]
      (is (= "#ff0000" (.-value hex-input))))))

(deftest value-attribute-3-digit-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value "#f00")
    (let [^js hex-input (shadow-part el "[part=hex-input]")]
      (is (= "#ff0000" (.-value hex-input))))))

;; ── Alpha attribute ──────────────────────────────────────────────────────────
(deftest alpha-shows-strip-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-alpha "")
    (is (not= "none" (.-display (.-style (shadow-part el "[part=alpha-strip]")))))))

;; ── Mode and popover ─────────────────────────────────────────────────────────
(deftest popover-mode-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-mode "popover")
    (is (= "popover" (.getAttribute el "data-mode")))
    (testing "panel hidden when not open"
      (let [^js panel (shadow-part el "[part=panel]")
            style (js/getComputedStyle panel)]
        (is (= "none" (.getPropertyValue style "display")))))
    (testing "panel visible when open"
      (.setAttribute el model/attr-open "")
      (let [^js panel (shadow-part el "[part=panel]")
            style (js/getComputedStyle panel)]
        (is (not= "none" (.getPropertyValue style "display")))))))

;; ── Disabled ─────────────────────────────────────────────────────────────────
(deftest disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (let [^js hex-input (shadow-part el "[part=hex-input]")]
      (is (true? (.-disabled hex-input))))))

;; ── Readonly ─────────────────────────────────────────────────────────────────
(deftest readonly-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-readonly "")
    (let [^js hex-input (shadow-part el "[part=hex-input]")]
      (is (true? (.-disabled hex-input))))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest property-value-test
  (let [^js el (append! (make-el))]
    (set! (.-value el) "#00ff00")
    (is (= "#00ff00" (.getAttribute el model/attr-value)))
    (is (= "#00ff00" (.-value el)))))

(deftest property-disabled-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (true? (.hasAttribute el model/attr-disabled)))
    (set! (.-disabled el) false)
    (is (false? (.hasAttribute el model/attr-disabled)))))

(deftest property-mode-test
  (let [^js el (append! (make-el))]
    (set! (.-mode el) "popover")
    (is (= "popover" (.getAttribute el model/attr-mode)))))

;; ── Swatches ─────────────────────────────────────────────────────────────────
(deftest swatches-rendered-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-swatches "#ff0000,#00ff00,#0000ff")
    (let [^js swatches (shadow-part el "[part=swatches]")
          buttons (.querySelectorAll swatches "[part=swatch]")]
      (is (= 3 (.-length buttons))))))

(deftest swatches-invalid-filtered-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-swatches "#ff0000,bad,#0000ff")
    (let [buttons (.querySelectorAll (shadow-part el "[part=swatches]") "[part=swatch]")]
      (is (= 2 (.-length buttons))))))

;; ── Hex input change event ───────────────────────────────────────────────────
(deftest hex-input-enter-fires-change-test
  (async done
    (let [^js el (append! (make-el))
          ^js hex-input (shadow-part el "[part=hex-input]")
          events (atom [])]
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (set! (.-value hex-input) "#ff5500")
      (.dispatchEvent hex-input
                      (js/KeyboardEvent. "keydown"
                                         #js {:key "Enter"
                                              :bubbles true
                                              :cancelable true}))
      (js/setTimeout
       (fn []
         (is (= 1 (count @events)))
         (is (= "#ff5500" (.-value (first @events))))
         (done))
       50))))

;; ── Swatch click fires change event ─────────────────────────────────────────
(deftest swatch-click-fires-change-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-swatches "#ff0000,#00ff00")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (let [^js first-swatch (.querySelector (shadow-part el "[part=swatches]") "[part=swatch]")]
        (.click first-swatch)
        (js/setTimeout
         (fn []
           (is (= 1 (count @events)))
           (is (= "#ff0000" (.-value (first @events))))
           (done))
         50)))))

;; ── ARIA attributes ──────────────────────────────────────────────────────────
(deftest area-aria-test
  (let [^js el (append! (make-el))
        ^js area (shadow-part el "[part=area]")]
    (is (= "slider" (.getAttribute area "role")))
    (is (some? (.getAttribute area "aria-valuetext")))))

(deftest hue-strip-aria-test
  (let [^js el (append! (make-el))
        ^js hue (shadow-part el "[part=hue-strip]")]
    (is (= "slider" (.getAttribute hue "role")))
    (is (= "Hue"    (.getAttribute hue "aria-label")))
    (is (= "0"      (.getAttribute hue "aria-valuemin")))
    (is (= "360"    (.getAttribute hue "aria-valuemax")))))

;; ── Form association ─────────────────────────────────────────────────────────
(deftest form-associated-test
  (let [^js klass (.get js/customElements model/tag-name)]
    (is (true? (.-formAssociated klass)))))

;; ── Alpha strip ARIA ─────────────────────────────────────────────────────────
(deftest alpha-strip-aria-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-alpha "")
    (let [^js alpha (shadow-part el "[part=alpha-strip]")]
      (is (= "slider"   (.getAttribute alpha "role")))
      (is (= "Opacity"  (.getAttribute alpha "aria-label")))
      (is (= "0"        (.getAttribute alpha "aria-valuemin")))
      (is (= "100"      (.getAttribute alpha "aria-valuemax"))))))

;; ── Reconnect stability ─────────────────────────────────────────────────────
(deftest reconnect-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value "#ff0000")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [^js hex-input (shadow-part el "[part=hex-input]")]
      (is (= "#ff0000" (.-value hex-input))))))

;; ── Disabled prevents swatch click ──────────────────────────────────────────
(deftest disabled-prevents-swatch-click-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-swatches "#ff0000,#00ff00")
      (.setAttribute el model/attr-disabled "")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (let [^js first-swatch (.querySelector (shadow-part el "[part=swatches]") "[part=swatch]")]
        (.click first-swatch)
        (js/setTimeout
         (fn []
           (is (= 0 (count @events)) "disabled should prevent change event")
           (done))
         50)))))

;; ── Readonly prevents swatch click ──────────────────────────────────────────
(deftest readonly-prevents-swatch-click-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-swatches "#ff0000,#00ff00")
      (.setAttribute el model/attr-readonly "")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (let [^js first-swatch (.querySelector (shadow-part el "[part=swatches]") "[part=swatch]")]
        (.click first-swatch)
        (js/setTimeout
         (fn []
           (is (= 0 (count @events)) "readonly should prevent change event")
           (done))
         50)))))

;; ── Keyboard navigation — area arrow keys ───────────────────────────────────
(deftest area-arrow-key-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-value "#808080")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (let [^js area (shadow-part el "[part=area]")]
        (.dispatchEvent area
                        (js/KeyboardEvent. "keydown"
                                           #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true}))
        (js/setTimeout
         (fn []
           (is (pos? (count @events)) "arrow key should fire change event")
           (done))
         50)))))

;; ── Keyboard navigation — hue strip ─────────────────────────────────────────
(deftest hue-strip-arrow-key-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-value "#ff0000")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (let [^js hue-strip (shadow-part el "[part=hue-strip]")]
        (.dispatchEvent hue-strip
                        (js/KeyboardEvent. "keydown"
                                           #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true}))
        (js/setTimeout
         (fn []
           (is (pos? (count @events)) "hue arrow key should fire change event")
           (done))
         50)))))

;; ── Popover escape closes ───────────────────────────────────────────────────
(deftest popover-escape-closes-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-mode "popover")
    (.setAttribute el model/attr-open "")
    (is (true? (.hasAttribute el model/attr-open)))
    (let [^js panel (shadow-part el "[part=panel]")]
      (.dispatchEvent panel
                      (js/KeyboardEvent. "keydown"
                                         #js {:key "Escape"
                                              :bubbles true
                                              :cancelable true})))
    (is (false? (.hasAttribute el model/attr-open)) "Escape should close popover")))
