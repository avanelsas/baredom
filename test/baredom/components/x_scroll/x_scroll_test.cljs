(ns baredom.components.x-scroll.x-scroll-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-scroll.x-scroll :as x-scroll]
            [baredom.components.x-scroll.model :as model]))

(x-scroll/init!)

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
  "Add n slide divs as children of el."
  [^js el n]
  (dotimes [i n]
    (let [div (.createElement js/document "div")]
      (set! (.-textContent div) (str "Slide " (inc i)))
      (.appendChild el div))))

(defn make-scroll-with-slides
  "Create an x-scroll element with n children, append to body, return element."
  [n]
  (let [el (make-el)]
    (add-slides! el n)
    ;; Give explicit size so slide-size calculations work
    (set! (.. el -style -width) "300px")
    (set! (.. el -style -height) "200px")
    (append! el)))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=viewport]")))
    (is (some? (shadow-part el "[part=track]")))
    (is (some? (shadow-part el "slot")))
    (is (some? (shadow-part el "[part=prev]")))
    (is (some? (shadow-part el "[part=next]")))
    (is (some? (shadow-part el "[part=indicators]")))
    (is (some? (shadow-part el "[part=live]")))))

;; ── Default state ───────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.getAttribute el "data-mode")))
    ;; Controls visible by default (no data-hide-controls)
    (is (not (.hasAttribute el "data-hide-controls")))
    ;; Tabindex for keyboard nav
    (is (= 0 (.-tabIndex el)))))

;; ── Accessibility ───────────────────────────────────────────────────────────
(deftest viewport-a11y-test
  (let [^js el (append! (make-el))
        ^js vp (shadow-part el "[part=viewport]")]
    (is (= "region"   (.getAttribute vp "role")))
    (is (= "carousel" (.getAttribute vp "aria-roledescription")))))

(deftest label-attribute-sets-aria-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Products")
    (is (= "Products" (.getAttribute (shadow-part el "[part=viewport]") "aria-label")))))

(deftest indicators-a11y-test
  (let [^js el (append! (make-el))]
    (let [^js ind (shadow-part el "[part=indicators]")]
      (is (= "tablist" (.getAttribute ind "role")))
      (is (= "Slide indicators" (.getAttribute ind "aria-label"))))))

(deftest live-region-exists-test
  (let [^js el (append! (make-el))
        ^js live (shadow-part el "[part=live]")]
    (is (= "polite" (.getAttribute live "aria-live")))
    (is (= "true"   (.getAttribute live "aria-atomic")))))

;; ── Mode attribute ──────────────────────────────────────────────────────────
(deftest mode-attribute-sets-data-mode-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-mode "vertical")
    (is (= "vertical" (.getAttribute el "data-mode")))))

;; ── Show controls ───────────────────────────────────────────────────────────
(deftest show-controls-false-hides-buttons-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-show-controls "false")
    (is (.hasAttribute el "data-hide-controls"))))

(deftest show-controls-true-shows-buttons-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-show-controls "false")
    (.removeAttribute el model/attr-show-controls)
    (is (not (.hasAttribute el "data-hide-controls")))))

;; ── Disabled state ──────────────────────────────────────────────────────────
(deftest disabled-sets-tabindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= -1 (.-tabIndex el)))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest mode-property-test
  (let [^js el (append! (make-el))]
    (set! (.-mode el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-mode)))
    (is (= "vertical" (.-mode el)))))

(deftest snap-property-test
  (let [^js el (append! (make-el))]
    (set! (.-snap el) "center")
    (is (= "center" (.getAttribute el model/attr-snap)))
    (is (= "center" (.-snap el)))))

(deftest loop-property-test
  (let [^js el (append! (make-el))]
    (set! (.-loop el) true)
    (is (.hasAttribute el model/attr-loop))
    (is (true? (.-loop el)))
    (set! (.-loop el) false)
    (is (not (.hasAttribute el model/attr-loop)))
    (is (false? (.-loop el)))))

(deftest auto-play-property-test
  (let [^js el (append! (make-el))]
    (set! (.-autoPlay el) true)
    (is (.hasAttribute el model/attr-auto-play))
    (is (true? (.-autoPlay el)))
    (set! (.-autoPlay el) false)
    (is (not (.hasAttribute el model/attr-auto-play)))))

(deftest interval-property-test
  (let [^js el (append! (make-el))]
    (set! (.-interval el) 3000)
    (is (= "3000" (.getAttribute el model/attr-interval)))
    (is (= 3000 (.-interval el)))))

(deftest show-controls-property-test
  (let [^js el (append! (make-el))]
    ;; Default true
    (is (true? (.-showControls el)))
    (set! (.-showControls el) false)
    (is (= "false" (.getAttribute el model/attr-show-controls)))
    (is (false? (.-showControls el)))
    (set! (.-showControls el) true)
    ;; Setting true removes attribute (default-true pattern)
    (is (not (.hasAttribute el model/attr-show-controls)))))

(deftest show-indicators-property-test
  (let [^js el (append! (make-el))]
    (set! (.-showIndicators el) true)
    (is (.hasAttribute el model/attr-show-indicators))
    (is (true? (.-showIndicators el)))))

(deftest active-index-property-test
  (let [^js el (append! (make-el))]
    (set! (.-activeIndex el) 3)
    (is (= "3" (.getAttribute el model/attr-active-index)))
    (is (= 3 (.-activeIndex el)))))

(deftest gap-property-test
  (let [^js el (append! (make-el))]
    (set! (.-gap el) 16)
    (is (= "16" (.getAttribute el model/attr-gap)))
    (is (= 16 (.-gap el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (is (true? (.-disabled el)))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest label-property-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "Carousel")
    (is (= "Carousel" (.getAttribute el model/attr-label)))
    (is (= "Carousel" (.-label el)))
    (set! (.-label el) "")
    (is (not (.hasAttribute el model/attr-label)))))

;; ── Indicators ──────────────────────────────────────────────────────────────
(deftest indicators-created-with-show-indicators-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.setAttribute el model/attr-show-indicators "")
      (append! el)
      ;; Wait for slotchange
      (js/setTimeout
       (fn []
         (let [^js ind (shadow-part el "[part=indicators]")
               dots (.-children ind)]
           (is (= 5 (.-length dots)))
           ;; First dot is active
           (is (= "true" (.getAttribute (aget dots 0) "aria-selected")))
           (is (= "false" (.getAttribute (aget dots 1) "aria-selected")))
           ;; Each has role=tab
           (is (= "tab" (.getAttribute (aget dots 0) "role"))))
         (done))
       50))))

;; ── Live region updates ─────────────────────────────────────────────────────
(deftest live-region-announces-slides-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-slides! el 3)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js live (shadow-part el "[part=live]")]
           (is (= "Slide 1 of 3" (.-textContent live))))
         (done))
       50))))

;; ── Control button states ───────────────────────────────────────────────────
(deftest prev-disabled-at-start-non-loop-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         (is (true?  (.-disabled (shadow-part el "[part=prev]"))))
         (is (false? (.-disabled (shadow-part el "[part=next]"))))
         (done))
       50))))

(deftest controls-enabled-with-loop-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.setAttribute el model/attr-loop "")
      (append! el)
      (js/setTimeout
       (fn []
         (is (false? (.-disabled (shadow-part el "[part=prev]"))))
         (is (false? (.-disabled (shadow-part el "[part=next]"))))
         (done))
       50))))

;; ── Events ──────────────────────────────────────────────────────────────────
(deftest change-event-fires-on-next-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (.next el)
         ;; Change event is dispatched synchronously
         (is (= 1 (count @events)))
         (let [^js d (first @events)]
           (is (= 1 (.-activeIndex d)))
           (is (= 0 (.-previousIndex d))))
         (done))
       50))))

(deftest loop-event-cancelable-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.setAttribute el model/attr-loop "")
      (.setAttribute el model/attr-active-index "4")
      ;; Cancel loop events
      (.addEventListener el model/event-loop
                         (fn [^js e] (.preventDefault e)))
      (append! el)
      (js/setTimeout
       (fn []
         (.next el)
         ;; Active index should remain 4 since loop was cancelled
         (is (= 4 (.-activeIndex el)))
         (done))
       50))))

(deftest events-bubble-and-are-composed-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      ;; Listen on body (bubble test)
      (.addEventListener (.-body js/document) model/event-change
                         (fn [^js e]
                           (swap! events conj {:bubbles (.-bubbles e)
                                               :composed (.-composed e)})))
      (append! el)
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 1 (count @events)))
         (is (true? (:bubbles (first @events))))
         (is (true? (:composed (first @events))))
         ;; Clean up body listener
         (done))
       50))))

;; ── Reconnect: no listener doubling ─────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-slides! el 5)
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      ;; Connect
      (append! el)
      (js/setTimeout
       (fn []
         ;; Disconnect and reconnect
         (.remove el)
         (.appendChild (.-body js/document) el)
         (js/setTimeout
          (fn []
            (.next el)
            (is (= 1 (count @events)))
            (done))
          50))
       50))))

;; ── Slotchange updates ─────────────────────────────────────────────────────
(deftest slotchange-updates-child-count-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (.setAttribute el model/attr-show-indicators "")
      (append! el)
      (js/setTimeout
       (fn []
         ;; Add slides dynamically
         (add-slides! el 4)
         (js/setTimeout
          (fn []
            (let [dots (.-children (shadow-part el "[part=indicators]"))]
              (is (= 4 (.-length dots))))
            (done))
          50))
       50))))

;; ── Keyboard navigation ────────────────────────────────────────────────────

(deftest arrow-right-navigates-next-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)
          events (atom [])]
      (.addEventListener el model/event-change
                         (fn [^js e] (swap! events conj (.-detail e))))
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "ArrowRight" :bubbles true}))
         (is (= 1 (count @events)))
         (let [^js d (first @events)]
           (is (= 1 (.-activeIndex d))))
         (done))
       50))))

(deftest arrow-left-navigates-prev-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-active-index "2")
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "ArrowLeft" :bubbles true}))
         (is (= 1 (.-activeIndex el)))
         (done))
       50))))

(deftest arrow-up-navigates-vertical-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-mode "vertical")
      (.setAttribute el model/attr-active-index "2")
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "ArrowUp" :bubbles true}))
         (is (= 1 (.-activeIndex el)))
         (done))
       50))))

(deftest arrow-down-navigates-vertical-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-mode "vertical")
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true}))
         (is (= 1 (.-activeIndex el)))
         (done))
       50))))

(deftest home-key-goes-to-first-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-active-index "3")
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Home" :bubbles true}))
         (is (= 0 (.-activeIndex el)))
         (done))
       50))))

(deftest end-key-goes-to-last-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "End" :bubbles true}))
         (is (= 4 (.-activeIndex el)))
         (done))
       50))))

(deftest keyboard-ignored-when-disabled-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-disabled "")
      (js/setTimeout
       (fn []
         (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "ArrowRight" :bubbles true}))
         (is (= 0 (.-activeIndex el)))
         (done))
       50))))

;; ── Start and end events ────────────────────────────────────────────────────

(deftest start-event-fires-on-navigation-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)
          events (atom [])]
      (.addEventListener el model/event-start
                         (fn [^js e] (swap! events conj (.-detail e))))
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 1 (count @events)))
         (let [^js d (first @events)]
           (is (= "forward" (.-direction d)))
           (is (= 0 (.-activeIndex d))))
         (done))
       50))))

(deftest end-event-fires-on-navigation-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)
          events (atom [])]
      (.addEventListener el model/event-end
                         (fn [^js e] (swap! events conj (.-detail e))))
      (js/setTimeout
       (fn []
         (.next el)
         ;; End event fires synchronously when prefers-reduced-motion
         ;; or after transitionend; check at least one fires within timeout
         (js/setTimeout
          (fn []
            (is (pos? (count @events)))
            (let [^js d (first @events)]
              (is (= 1 (.-activeIndex d)))
              (is (nil? (.-scrollPosition d))))
            (done))
          500))
       50))))

;; ── Method tests ────────────────────────────────────────────────────────────

(deftest next-method-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 1 (.-activeIndex el)))
         (.next el)
         (is (= 2 (.-activeIndex el)))
         (done))
       50))))

(deftest prev-method-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-active-index "3")
      (js/setTimeout
       (fn []
         (.prev el)
         (is (= 2 (.-activeIndex el)))
         (done))
       50))))

(deftest next-clamps-at-end-non-loop-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 3)]
      (.setAttribute el model/attr-active-index "2")
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 2 (.-activeIndex el)))
         (done))
       50))))

(deftest prev-clamps-at-start-non-loop-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 3)]
      (js/setTimeout
       (fn []
         (.prev el)
         (is (= 0 (.-activeIndex el)))
         (done))
       50))))

(deftest goto-method-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (js/setTimeout
       (fn []
         (.goTo el 3)
         (is (= 3 (.-activeIndex el)))
         (done))
       50))))

;; ── Autoplay ────────────────────────────────────────────────────────────────

(deftest autoplay-advances-slides-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-auto-play "")
      (.setAttribute el model/attr-interval "600")
      (js/setTimeout
       (fn []
         ;; After ~700ms with 600ms interval, should have advanced at least once
         (is (pos? (.-activeIndex el)))
         (done))
       800))))

(deftest autoplay-stops-when-disabled-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (.setAttribute el model/attr-auto-play "")
      (.setAttribute el model/attr-interval "600")
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-disabled "")
         (let [idx (.-activeIndex el)]
           (js/setTimeout
            (fn []
              (is (= idx (.-activeIndex el)))
              (done))
            800)))
       100))))

;; ── Edge cases ──────────────────────────────────────────────────────────────

(deftest next-disabled-at-last-slide-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 3)]
      (.setAttribute el model/attr-active-index "2")
      (js/setTimeout
       (fn []
         (is (true? (.-disabled (shadow-part el "[part=next]"))))
         (is (false? (.-disabled (shadow-part el "[part=prev]"))))
         (done))
       50))))

(deftest live-region-updates-on-navigation-test
  (cljs.test/async done
    (let [^js el (make-scroll-with-slides 5)]
      (js/setTimeout
       (fn []
         (.next el)
         (let [^js live (shadow-part el "[part=live]")]
           (is (= "Slide 2 of 5" (.-textContent live))))
         (done))
       50))))
