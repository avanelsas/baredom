(ns baredom.components.x-ripple-effect.x-ripple-effect-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-ripple-effect.x-ripple-effect :as x]
            [baredom.components.x-ripple-effect.model           :as model]))

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
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "slot")))
    (is (some? (shadow-part el "[part=filters]")))))

;; ── Ripple creation on pointerdown ──────────────────────────────────────────
(deftest pointerdown-creates-filter-test
  (let [^js el  (append! (make-el))
        ^js svg (shadow-part el "[part=filters]")]
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button   0
                                              :clientX  50
                                              :clientY  50
                                              :bubbles  true}))
    (is (pos? (.-length (.-childNodes svg)))
        "A filter element should be created in the SVG")))

;; ── Disabled suppresses ripple ──────────────────────────────────────────────
(deftest disabled-no-ripple-test
  (let [^js el  (append! (make-el))
        _       (.setAttribute el model/attr-disabled "")
        ^js svg (shadow-part el "[part=filters]")]
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button   0
                                              :clientX  50
                                              :clientY  50
                                              :bubbles  true}))
    (is (zero? (.-length (.-childNodes svg)))
        "No filter should be created when disabled")))

;; ── Right-click does not trigger ────────────────────────────────────────────
(deftest right-click-no-ripple-test
  (let [^js el  (append! (make-el))
        ^js svg (shadow-part el "[part=filters]")]
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button   2
                                              :clientX  50
                                              :clientY  50
                                              :bubbles  true}))
    (is (zero? (.-length (.-childNodes svg)))
        "Right-click should not create a ripple")))

;; ── x-ripple-effect-start event fires ───────────────────────────────────────
(deftest start-event-fires-test
  (let [^js el (append! (make-el))
        events (atom [])
        _      (.addEventListener el model/event-start
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button   0
                                              :clientX  100
                                              :clientY  200
                                              :bubbles  true}))
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (= 100 (.-x d)))
      (is (= 200 (.-y d))))))

;; ── x-ripple-effect-end event fires after animation ─────────────────────────
(deftest end-event-fires-test
  (async done
    (let [^js el   (append! (make-el))
          events   (atom [])
          _        (.setAttribute el model/attr-duration "100")
          _        (.addEventListener el model/event-end
                                      (fn [^js e] (swap! events conj (.-detail e))))]
      (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                           #js {:button   0
                                                :clientX  42
                                                :clientY  84
                                                :bubbles  true}))
      ;; Wait long enough for the animation to complete (duration=100ms + margin)
      (js/setTimeout
       (fn []
         (is (= 1 (count @events)) "end event should fire once after animation")
         (let [^js d (first @events)]
           (is (= 42 (.-x d)))
           (is (= 84 (.-y d))))
         (done))
       250))))

;; ── Filter cleanup after animation ─────────────────────────────────────────
(deftest filter-cleanup-test
  (async done
    (let [^js el  (append! (make-el))
          _       (.setAttribute el model/attr-duration "100")
          ^js svg (shadow-part el "[part=filters]")]
      (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                           #js {:button   0
                                                :clientX  50
                                                :clientY  50
                                                :bubbles  true}))
      (is (pos? (.-length (.-childNodes svg)))
          "Filter should exist during animation")
      ;; Wait for animation to complete
      (js/setTimeout
       (fn []
         (is (zero? (.-length (.-childNodes svg)))
             "Filter elements should be removed after animation completes")
         (done))
       250))))

;; ── Property accessor tests ─────────────────────────────────────────────────
(deftest intensity-property-test
  (let [^js el (append! (make-el))]
    (is (= 25 (.-intensity el)))
    (set! (.-intensity el) 50)
    (is (= "50" (.getAttribute el model/attr-intensity)))
    (is (= 50 (.-intensity el)))))

(deftest duration-property-test
  (let [^js el (append! (make-el))]
    (is (= 800 (.-duration el)))
    (set! (.-duration el) 400)
    (is (= "400" (.getAttribute el model/attr-duration)))
    (is (= 400 (.-duration el)))))

(deftest frequency-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.04 (.-frequency el)))
    (set! (.-frequency el) 0.1)
    (is (= "0.1" (.getAttribute el model/attr-frequency)))
    (is (= 0.1 (.-frequency el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Reconnect: no listener doubling ─────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (let [^js el (make-el)
        events (atom [])
        _      (.addEventListener el model/event-start
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    ;; First connect
    (.appendChild (.-body js/document) el)
    ;; Disconnect and reconnect
    (.remove el)
    (.appendChild (.-body js/document) el)
    ;; Trigger ripple — should fire exactly once
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button   0
                                              :clientX  50
                                              :clientY  50
                                              :bubbles  true}))
    (is (= 1 (count @events)))))

;; ── Multiple rapid clicks create multiple filters ───────────────────────────
(deftest multiple-clicks-test
  (let [^js el  (append! (make-el))
        ^js svg (shadow-part el "[part=filters]")]
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button 0 :clientX 10 :clientY 10 :bubbles true}))
    (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                         #js {:button 0 :clientX 20 :clientY 20 :bubbles true}))
    (is (= 2 (.-length (.-childNodes svg)))
        "Two rapid clicks should create two filter elements")))
