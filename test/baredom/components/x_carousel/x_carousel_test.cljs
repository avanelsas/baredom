(ns baredom.components.x-carousel.x-carousel-test
  (:require [cljs.test :refer-macros [deftest is testing async use-fixtures]]
            [baredom.components.x-carousel.x-carousel :as x]
            [baredom.components.x-carousel.model       :as model]))

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

(defn add-slides!
  "Add n div children as slides to el."
  [^js el n]
  (dotimes [i n]
    (let [^js d (.createElement js/document "div")]
      (set! (.-textContent d) (str "Slide " (inc i)))
      (set! (.. d -style -width) "100%")
      (set! (.. d -style -height) "100%")
      (set! (.. d -style -background) (str "hsl(" (* i 90) ",70%,80%)"))
      (.appendChild el d))))

(defn wait-frame
  "Wait one animation frame then call cb."
  [cb]
  (js/requestAnimationFrame (fn [_] (cb))))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=viewport]")))
    (is (some? (shadow-part el "[part=track]")))
    (is (some? (shadow-part el "slot")))
    (is (some? (shadow-part el "[part=prev-btn]")))
    (is (some? (shadow-part el "[part=next-btn]")))
    (is (some? (shadow-part el "[part=dots]")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "region" (.getAttribute el "role")))
    (is (= "carousel" (.getAttribute el "aria-roledescription")))
    (is (= "horizontal" (.getAttribute el "data-direction")))
    (is (= "slide" (.getAttribute el "data-transition")))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest current-slide-property-test
  (let [^js el (append! (make-el))]
    (add-slides! el 3)
    (set! (.-currentSlide el) 2)
    (is (= "2" (.getAttribute el model/attr-current)))
    (is (= 2   (.-currentSlide el)))))

(deftest autoplay-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-autoplay el)))
    (set! (.-autoplay el) true)
    (is (.hasAttribute el model/attr-autoplay))
    (is (true? (.-autoplay el)))
    (set! (.-autoplay el) false)
    (is (not (.hasAttribute el model/attr-autoplay)))))

(deftest interval-property-test
  (let [^js el (append! (make-el))]
    (is (= 5000 (.-interval el)))
    (set! (.-interval el) 3000)
    (is (= "3000" (.getAttribute el model/attr-interval)))
    (is (= 3000 (.-interval el)))))

(deftest loop-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-loop el)))
    (set! (.-loop el) true)
    (is (.hasAttribute el model/attr-loop))
    (set! (.-loop el) false)
    (is (not (.hasAttribute el model/attr-loop)))))

(deftest arrows-property-test
  (let [^js el (append! (make-el))]
    ;; Default true (attribute absent)
    (is (true? (.-arrows el)))
    (set! (.-arrows el) false)
    (is (= "false" (.getAttribute el model/attr-arrows)))
    (is (false? (.-arrows el)))
    (set! (.-arrows el) true)
    (is (not (.hasAttribute el model/attr-arrows)))))

(deftest dots-property-test
  (let [^js el (append! (make-el))]
    (is (true? (.-dots el)))
    (set! (.-dots el) false)
    (is (= "false" (.getAttribute el model/attr-dots)))
    (is (false? (.-dots el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest transition-property-test
  (let [^js el (append! (make-el))]
    (is (= "slide" (.-transition el)))
    (set! (.-transition el) "fade")
    (is (= "fade" (.getAttribute el model/attr-transition)))
    (is (= "fade" (.-transition el)))))

(deftest direction-property-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.-direction el)))
    (set! (.-direction el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-direction)))
    (is (= "vertical" (.-direction el)))))

(deftest peek-property-test
  (let [^js el (append! (make-el))]
    (is (= "0px" (.-peek el)))
    (set! (.-peek el) "40px")
    (is (= "40px" (.getAttribute el model/attr-peek)))
    (is (= "40px" (.-peek el)))))

(deftest slide-count-read-only-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-slideCount el)))))

;; ── Slotchange updates slide count ──────────────────────────────────────────
(deftest slotchange-updates-count-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 4)
      ;; slotchange fires asynchronously
      (wait-frame
       (fn []
         (is (= 4 (.-slideCount el)))
         (done))))))

;; ── Arrow navigation ────────────────────────────────────────────────────────
(deftest arrow-click-navigates-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (wait-frame
       (fn []
         (let [^js nxt (shadow-part el "[part=next-btn]")]
           (.click nxt)
           (is (= 1 (count @events)))
           (let [^js d (first @events)]
             (is (= 1 (.-index d)))
             (is (= 0 (.-previousIndex d)))
             (is (= "arrow" (.-reason d))))
           (is (= "1" (.getAttribute el model/attr-current)))
           (done)))))))

;; ── Dot click navigation ────────────────────────────────────────────────────
(deftest dot-click-navigates-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (wait-frame
       (fn []
         ;; Click the third dot
         (let [^js dot (.querySelector (shadow-part el "[part=dots]") "[data-index='2']")]
           (.click dot)
           (is (= 1 (count @events)))
           (let [^js d (first @events)]
             (is (= 2 (.-index d)))
             (is (= "dot" (.-reason d))))
           (done)))))))

;; ── Keyboard navigation (horizontal) ────────────────────────────────────────
(deftest keyboard-horizontal-test
  (async done
    (let [^js el (append! (make-el))
          ^js vp (shadow-part el "[part=viewport]")]
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowRight" :bubbles true}))
         (is (= "1" (.getAttribute el model/attr-current)))
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowLeft" :bubbles true}))
         (is (= "0" (.getAttribute el model/attr-current)))
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "End" :bubbles true}))
         (is (= "2" (.getAttribute el model/attr-current)))
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "Home" :bubbles true}))
         (is (= "0" (.getAttribute el model/attr-current)))
         (done))))))

;; ── Keyboard navigation (vertical) ──────────────────────────────────────────
(deftest keyboard-vertical-test
  (async done
    (let [^js el (append! (make-el))
          ^js vp (shadow-part el "[part=viewport]")]
      (.setAttribute el model/attr-direction "vertical")
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true}))
         (is (= "1" (.getAttribute el model/attr-current)))
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowUp" :bubbles true}))
         (is (= "0" (.getAttribute el model/attr-current)))
         ;; Horizontal keys should NOT work in vertical mode
         (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowRight" :bubbles true}))
         (is (= "0" (.getAttribute el model/attr-current)))
         (done))))))

;; ── Loop wrapping ───────────────────────────────────────────────────────────
(deftest loop-wrapping-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-loop "")
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.setAttribute el model/attr-current "2")
         (let [^js nxt (shadow-part el "[part=next-btn]")]
           (.click nxt)
           (is (= "0" (.getAttribute el model/attr-current))))
         (done))))))

;; ── No-loop boundary ────────────────────────────────────────────────────────
(deftest no-loop-boundary-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.setAttribute el model/attr-current "2")
         (let [^js nxt (shadow-part el "[part=next-btn]")]
           (.click nxt)
           ;; Should stay at 2
           (is (= "2" (.getAttribute el model/attr-current))))
         (done))))))

;; ── Disabled blocks interaction ─────────────────────────────────────────────
(deftest disabled-blocks-interaction-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.setAttribute el model/attr-disabled "")
      (add-slides! el 3)
      (.addEventListener el model/event-change (fn [e] (swap! events conj e)))
      (wait-frame
       (fn []
         (let [^js vp (shadow-part el "[part=viewport]")]
           (.dispatchEvent vp (js/KeyboardEvent. "keydown" #js {:key "ArrowRight" :bubbles true}))
           (is (= 0 (count @events)))
           (is (= "0" (or (.getAttribute el model/attr-current) "0")))
           (done)))))))

;; ── Event cancelable ────────────────────────────────────────────────────────
(deftest event-cancelable-test
  (async done
    (let [^js el (append! (make-el))]
      (.addEventListener el model/event-change
                         (fn [^js e] (.preventDefault e)))
      (add-slides! el 3)
      (wait-frame
       (fn []
         (let [^js nxt (shadow-part el "[part=next-btn]")]
           (.click nxt)
           ;; Should remain at 0 since event was cancelled
           (is (= "0" (or (.getAttribute el model/attr-current) "0")))
           (done)))))))

;; ── Single slide hides controls ─────────────────────────────────────────────
(deftest single-slide-hides-controls-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 1)
      (wait-frame
       (fn []
         (is (= "none" (.. (shadow-part el "[part=prev-btn]") -style -display)))
         (is (= "none" (.. (shadow-part el "[part=next-btn]") -style -display)))
         (is (= "none" (.. (shadow-part el "[part=dots]") -style -display)))
         (done))))))

;; ── Zero slides ─────────────────────────────────────────────────────────────
(deftest zero-slides-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-slideCount el)))
    ;; Should not throw
    (is (= "none" (.. (shadow-part el "[part=prev-btn]") -style -display)))))

;; ── Dots count matches slides ───────────────────────────────────────────────
(deftest dots-count-matches-slides-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 4)
      (wait-frame
       (fn []
         (let [^js dots (shadow-part el "[part=dots]")
               dot-count (.-length (.-children dots))]
           (is (= 4 dot-count))
           (done)))))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (.appendChild (.-body js/document) el)
      (wait-frame
       (fn []
         ;; Disconnect and reconnect
         (.remove el)
         (.appendChild (.-body js/document) el)
         (wait-frame
          (fn []
            (.click (shadow-part el "[part=next-btn]"))
            (is (= 1 (count @events)))
            (done))))))))

;; ── Fade mode ───────────────────────────────────────────────────────────────
(deftest fade-mode-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-transition "fade")
      (add-slides! el 3)
      (wait-frame
       (fn []
         (is (= "fade" (.getAttribute el "data-transition")))
         ;; First slide should be visible, second hidden
         (let [^js slot (shadow-part el "slot")
               children (.assignedElements slot)
               ^js first-child (aget children 0)
               ^js second-child (aget children 1)]
           (is (= "1" (.. first-child -style -opacity)))
           (is (= "0" (.. second-child -style -opacity))))
         (done))))))

;; ── Vertical direction ──────────────────────────────────────────────────────
(deftest vertical-direction-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-direction "vertical")
    (is (= "vertical" (.getAttribute el "data-direction")))
    (is (= "vertical" (.-direction el)))))

;; ── Dots active state updates ───────────────────────────────────────────────
(deftest dots-active-state-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 3)
      (wait-frame
       (fn []
         ;; First dot should be active
         (let [^js dots (shadow-part el "[part=dots]")
               ^js first-dot (aget (.-children dots) 0)
               ^js second-dot (aget (.-children dots) 1)]
           (is (= "true" (.getAttribute first-dot "aria-current")))
           (is (= "false" (.getAttribute second-dot "aria-current")))
           ;; Navigate to slide 1
           (.click (shadow-part el "[part=next-btn]"))
           (let [^js first-dot2 (aget (.-children dots) 0)
                 ^js second-dot2 (aget (.-children dots) 1)]
             (is (= "false" (.getAttribute first-dot2 "aria-current")))
             (is (= "true" (.getAttribute second-dot2 "aria-current")))))
         (done))))))

;; ── Public methods ──────────────────────────────────────────────────────────
(deftest next-method-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.next el)
         (is (= 1 (.-currentSlide el)))
         (done))))))

(deftest previous-method-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 3)
      (wait-frame
       (fn []
         (.next el)
         (.previous el)
         (is (= 0 (.-currentSlide el)))
         (done))))))

(deftest go-to-method-test
  (async done
    (let [^js el (append! (make-el))]
      (add-slides! el 5)
      (wait-frame
       (fn []
         (.goTo el 3)
         (is (= 3 (.-currentSlide el)))
         (done))))))

;; ── Event reason: "api" via public methods ─────────────────────────────────
(deftest api-reason-next-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (wait-frame
       (fn []
         (.next el)
         (is (= 1 (count @events)))
         (is (= "api" (.-reason (first @events))))
         (done))))))

(deftest api-reason-previous-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (wait-frame
       (fn []
         ;; Go to slide 1 first, then back
         (.next el)
         (reset! events [])
         (.previous el)
         (is (= 1 (count @events)))
         (is (= "api" (.-reason (first @events))))
         (done))))))

(deftest api-reason-go-to-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (add-slides! el 5)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (wait-frame
       (fn []
         (.goTo el 3)
         (is (= 1 (count @events)))
         (is (= "api" (.-reason (first @events))))
         (done))))))

;; ── Autoplay ───────────────────────────────────────────────────────────────
(deftest autoplay-advances-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (.setAttribute el model/attr-autoplay "")
      (.setAttribute el model/attr-interval "150")
      (.setAttribute el model/attr-loop "")
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      ;; Wait long enough for at least one autoplay tick
      (js/setTimeout
       (fn []
         (is (>= (count @events) 1) "autoplay should fire at least one change event")
         (is (= "autoplay" (.-reason (first @events))))
         (.remove el)
         (done))
       400))))

(deftest autoplay-pauses-on-pointerenter-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-autoplay "")
      (.setAttribute el model/attr-interval "150")
      (.setAttribute el model/attr-loop "")
      (add-slides! el 3)
      (append! el)
      ;; Simulate hover — should pause autoplay
      (.dispatchEvent el (js/PointerEvent. "pointerenter" #js {:bubbles true}))
      (let [idx-before (.-currentSlide el)]
        (js/setTimeout
         (fn []
           ;; Should NOT have advanced while hovered
           (is (= idx-before (.-currentSlide el))
               "autoplay should pause on pointerenter")
           ;; Resume
           (.dispatchEvent el (js/PointerEvent. "pointerleave" #js {:bubbles true}))
           (js/setTimeout
            (fn []
              (is (> (.-currentSlide el) idx-before)
                  "autoplay should resume on pointerleave")
              (.remove el)
              (done))
            400))
         300)))))

(deftest autoplay-pauses-on-focusin-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-autoplay "")
      (.setAttribute el model/attr-interval "150")
      (.setAttribute el model/attr-loop "")
      (add-slides! el 3)
      (append! el)
      ;; Simulate focus inside
      (.dispatchEvent el (js/FocusEvent. "focusin" #js {:bubbles true}))
      (let [idx-before (.-currentSlide el)]
        (js/setTimeout
         (fn []
           (is (= idx-before (.-currentSlide el))
               "autoplay should pause on focusin")
           (.remove el)
           (done))
         300)))))

(deftest autoplay-stops-on-disconnect-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (.setAttribute el model/attr-autoplay "")
      (.setAttribute el model/attr-interval "150")
      (.setAttribute el model/attr-loop "")
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      ;; Wait a tick, then disconnect
      (wait-frame
       (fn []
         (.remove el)
         (let [count-at-remove (count @events)]
           (js/setTimeout
            (fn []
              ;; Should not have advanced further after disconnect
              (is (= count-at-remove (count @events))
                  "autoplay should stop on disconnect")
              (done))
            300)))))))

;; ── Drag / swipe ───────────────────────────────────────────────────────────
(deftest drag-navigates-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (wait-frame
       (fn []
         (let [^js track (.querySelector (.-shadowRoot el) "[part=track]")]
           ;; Simulate a swipe left: pointerdown -> pointermove -> pointerup
           (.dispatchEvent track
                          (js/PointerEvent. "pointerdown"
                                           #js {:clientX 200 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointermove"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointerup"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           ;; Should have navigated forward (swipe left = next)
           (wait-frame
            (fn []
              (is (= 1 (count @events)) "drag should fire one change event")
              (when (pos? (count @events))
                (is (= "drag" (.-reason (first @events)))))
              (done)))))))))

(deftest drag-disabled-in-fade-mode-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (.setAttribute el model/attr-transition "fade")
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (wait-frame
       (fn []
         (let [^js track (.querySelector (.-shadowRoot el) "[part=track]")]
           (.dispatchEvent track
                          (js/PointerEvent. "pointerdown"
                                           #js {:clientX 200 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointermove"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointerup"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (wait-frame
            (fn []
              (is (= 0 (count @events)) "drag should be disabled in fade mode")
              (done)))))))))

(deftest drag-disabled-when-disabled-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (.setAttribute el model/attr-disabled "")
      (add-slides! el 3)
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (wait-frame
       (fn []
         (let [^js track (.querySelector (.-shadowRoot el) "[part=track]")]
           (.dispatchEvent track
                          (js/PointerEvent. "pointerdown"
                                           #js {:clientX 200 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointermove"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (.dispatchEvent track
                          (js/PointerEvent. "pointerup"
                                           #js {:clientX 50 :clientY 100
                                                :pointerId 1 :bubbles true}))
           (wait-frame
            (fn []
              (is (= 0 (count @events)) "drag should be disabled when disabled")
              (done)))))))))
