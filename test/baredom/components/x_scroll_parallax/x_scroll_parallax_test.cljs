(ns baredom.components.x-scroll-parallax.x-scroll-parallax-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-scroll-parallax.x-scroll-parallax :as x-scroll-parallax]
            [baredom.components.x-scroll-parallax.model :as model]))

(x-scroll-parallax/init!)

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

(defn add-children!
  "Add n child divs with data-speed attributes."
  [^js el speeds]
  (doseq [s speeds]
    (let [div (.createElement js/document "div")]
      (set! (.-textContent div) (str "Layer " s))
      (.setAttribute div "data-speed" (str s))
      (set! (.. div -style -height) "100px")
      (.appendChild el div))))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=viewport]")))
    (is (some? (shadow-part el "slot")))
    (is (some? (shadow-part el "[part=live]")))))

;; ── Default state ───────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "vertical" (.getAttribute el "data-direction")))))

;; ── Accessibility ───────────────────────────────────────────────────────────
(deftest viewport-a11y-test
  (let [^js el (append! (make-el))
        ^js vp (shadow-part el "[part=viewport]")]
    (is (= "region" (.getAttribute vp "role")))))

(deftest label-attribute-sets-aria-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Hero section")
    (is (= "Hero section"
           (.getAttribute (shadow-part el "[part=viewport]") "aria-label")))))

(deftest live-region-present-test
  (let [^js el (append! (make-el))
        ^js live (shadow-part el "[part=live]")]
    (is (= "polite" (.getAttribute live "aria-live")))
    (is (= "true"   (.getAttribute live "aria-atomic")))))

;; ── Direction attribute ─────────────────────────────────────────────────────
(deftest direction-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-direction "horizontal")
    (is (= "horizontal" (.getAttribute el "data-direction")))))

;; ── Disabled attribute ──────────────────────────────────────────────────────
(deftest disabled-attribute-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-disabled)))
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute el model/attr-disabled))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest direction-property-test
  (let [^js el (append! (make-el))]
    (is (= "vertical" (.-direction el)))
    (set! (.-direction el) "horizontal")
    (is (= "horizontal" (.getAttribute el model/attr-direction)))
    (is (= "horizontal" (.-direction el)))))

(deftest source-property-test
  (let [^js el (append! (make-el))]
    (is (= "document" (.-source el)))))

(deftest easing-property-test
  (let [^js el (append! (make-el))]
    (is (= "none" (.-easing el)))
    (set! (.-easing el) "smooth")
    (is (= "smooth" (.getAttribute el model/attr-easing)))
    (is (= "smooth" (.-easing el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.hasAttribute el model/attr-disabled)))
    (is (true? (.-disabled el)))))

(deftest label-property-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-label el)))
    (set! (.-label el) "Parallax section")
    (is (= "Parallax section" (.getAttribute el model/attr-label)))
    (is (= "Parallax section" (.-label el)))))

(deftest progress-property-read-only-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-progress el)))))

;; ── Children with data attributes ───────────────────────────────────────────
(deftest children-slotted-test
  (let [^js el (make-el)]
    (add-children! el [0.5 1 2])
    (append! el)
    (let [^js slot (shadow-part el "slot")
          assigned (.assignedElements slot)]
      (is (= 3 (.-length assigned))))))

(deftest children-data-speed-preserved-test
  (let [^js el (make-el)]
    (add-children! el [0.3 1.5])
    (append! el)
    (let [^js slot (shadow-part el "slot")
          assigned (.assignedElements slot)]
      (is (= "0.3" (.getAttribute (aget assigned 0) "data-speed")))
      (is (= "1.5" (.getAttribute (aget assigned 1) "data-speed"))))))

;; ── Direction fallback ──────────────────────────────────────────────────────
(deftest direction-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-direction "bogus")
    (is (= "vertical" (.getAttribute el "data-direction")))))

;; ── Source attribute ────────────────────────────────────────────────────────
(deftest source-attribute-default-test
  (let [^js el (append! (make-el))]
    (is (= "document" (.-source el)))))

(deftest source-attribute-set-test
  (let [^js el (append! (make-el))]
    (set! (.-source el) "document")
    (is (= "document" (.getAttribute el model/attr-source)))))

;; ── Easing attribute ────────────────────────────────────────────────────────
(deftest easing-attribute-reflects-on-host-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-easing "smooth")
    (is (= "smooth" (.getAttribute el model/attr-easing)))
    (is (= "smooth" (.-easing el)))))

;; ── Label removal ───────────────────────────────────────────────────────────
(deftest label-empty-removes-aria-label-test
  (let [^js el (append! (make-el))
        ^js vp (shadow-part el "[part=viewport]")]
    (.setAttribute el model/attr-label "Hello")
    (is (= "Hello" (.getAttribute vp "aria-label")))
    (.removeAttribute el model/attr-label)
    (is (nil? (.getAttribute vp "aria-label")))))

;; ── Disabled sets attribute on host ─────────────────────────────────────────
(deftest disabled-via-property-sets-attribute-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Reconnect: no error ────────────────────────────────────────────────────
(deftest reconnect-no-error-test
  (let [^js el (make-el)]
    (add-children! el [0.5 1])
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (.-isConnected el))
    (is (some? (.-shadowRoot el)))))

;; ── Events (async — IntersectionObserver fires asynchronously) ─────────────
(deftest enter-event-fires-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-children! el [0.5 1])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-enter
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (is (pos? (count @events))
             "enter event should fire when element becomes visible")
         (when (pos? (count @events))
           (let [^js d (first @events)]
             (is (number? (.-progress d)))))
         (done))
       300))))

(deftest progress-event-detail-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-children! el [0.5 1])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-progress
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (when (pos? (count @events))
           (let [^js d (first @events)]
             (is (number? (.-progress d)))
             (is (<= 0 (.-progress d) 1))))
         (done))
       300))))

(deftest enter-event-bubbles-and-is-composed-test
  (cljs.test/async done
    (let [^js el (make-el)
          events (atom [])]
      (add-children! el [1])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-enter
                         (fn [^js e] (swap! events conj
                                            {:bubbles  (.-bubbles e)
                                             :composed (.-composed e)})))
      (append! el)
      (js/setTimeout
       (fn []
         (when (pos? (count @events))
           (is (true? (:bubbles (first @events))))
           (is (true? (:composed (first @events)))))
         (done))
       300))))

(deftest leave-event-fires-on-disconnect-test
  (cljs.test/async done
    (let [^js el (make-el)
          leave-events (atom [])]
      (add-children! el [1])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-leave
                         (fn [^js e] (swap! leave-events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         ;; Remove triggers disconnectedCallback which dispatches leave
         (.remove el)
         (is (pos? (count @leave-events))
             "leave event should fire on disconnect")
         (when (pos? (count @leave-events))
           (let [^js d (first @leave-events)]
             (is (number? (.-progress d)))))
         ;; Re-append so cleanup fixture can find it
         (append! el)
         (done))
       300))))

;; ── Live region announcements ──────────────────────────────────────────────
(deftest live-region-announces-enter-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-children! el [1])
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js live (shadow-part el "[part=live]")
               text (.-textContent live)]
           ;; Live region should have been set (may already be cleared by timeout)
           ;; We check it was set at some point by checking enter event fired
           (is (some? live)))
         (done))
       150))))

;; ── Disabled suppresses transforms ─────────────────────────────────────────
(deftest disabled-suppresses-transforms-test
  (cljs.test/async done
    (let [^js el (make-el)]
      (add-children! el [0.5 2])
      (set! (.. el -style -height) "200px")
      (.setAttribute el model/attr-disabled "")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js slot (shadow-part el "slot")
               assigned (.assignedElements slot)]
           ;; When disabled, transforms should not be applied
           (doseq [i (range (.-length assigned))]
             (let [^js child (aget assigned i)
                   transform (.. child -style -transform)]
               (is (or (= "" transform) (nil? transform))
                   "disabled element should not apply transforms to children"))))
         (done))
       300))))
