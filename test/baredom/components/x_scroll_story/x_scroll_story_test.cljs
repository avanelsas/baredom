(ns baredom.components.x-scroll-story.x-scroll-story-test
  (:require
   [cljs.test :refer [deftest testing is use-fixtures async]]
   [baredom.components.x-scroll-story.x-scroll-story :as x]
   [baredom.components.x-scroll-story.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove ^js node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) (str "[part=" selector "]")))

(defn add-steps!
  "Add step divs as children. Each id in `ids` becomes a child with that id."
  [^js el ids]
  (doseq [id ids]
    (let [div (.createElement js/document "div")]
      (set! (.-textContent div) (str "Step " id))
      (when id (.setAttribute div "id" (str id)))
      (set! (.. div -style -height) "100px")
      (set! (.. div -style -minHeight) "100px")
      (.appendChild el div))))

;; ── Registration ────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-scroll-story should be registered"))

;; ── Shadow DOM structure ────────────────────────────────────────────────────

(deftest shadow-dom-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)) "should have shadow root")
    (is (some? (shadow-part el "container")) "should have container part")
    (is (some? (shadow-part el "media")) "should have media part")
    (is (some? (shadow-part el "steps")) "should have steps part")
    (is (some? (shadow-part el "live")) "should have live region")))

;; ── Default state ───────────────────────────────────────────────────────────

(deftest default-state-test
  (let [el (append! (make-el))]
    (is (= "left" (.getAttribute el "data-layout")))
    (is (= -1 (.-activeIndex el)))
    (is (= 0 (.-progress el)))))

;; ── Attribute reflection ────────────────────────────────────────────────────

(deftest layout-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el "layout" "right")
    (is (= "right" (.getAttribute el "data-layout")))
    (is (= "right" (.-layout el)))

    (.setAttribute el "layout" "top")
    (is (= "top" (.getAttribute el "data-layout")))

    (.setAttribute el "layout" "invalid")
    (is (= "left" (.getAttribute el "data-layout")))))

(deftest threshold-property-test
  (let [el (append! (make-el))]
    (is (= 0.5 (.-threshold el)))
    (set! (.-threshold el) 0.7)
    (is (= "0.7" (.getAttribute el "threshold")))))

(deftest split-property-test
  (let [el (append! (make-el))]
    (is (= 0.5 (.-split el)))
    (set! (.-split el) 0.3)
    (is (= "0.3" (.getAttribute el "split")))))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.hasAttribute el "disabled")))
    (set! (.-disabled el) false)
    (is (false? (.hasAttribute el "disabled")))))

(deftest label-property-test
  (let [el (append! (make-el))]
    (is (= "" (.-label el)))
    (set! (.-label el) "My story")
    (is (= "My story" (.getAttribute el "label")))
    (let [container (shadow-part el "container")]
      (is (= "My story" (.getAttribute container "aria-label"))))))

;; ── Accessibility ───────────────────────────────────────────────────────────

(deftest accessibility-test
  (let [el (append! (make-el))
        container (shadow-part el "container")
        live (shadow-part el "live")]
    (is (= "region" (.getAttribute container "role")))
    (is (= "polite" (.getAttribute live "aria-live")))
    (is (= "true" (.getAttribute live "aria-atomic")))))

;; ── Slots ───────────────────────────────────────────────────────────────────

(deftest slot-structure-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        media-slot (.querySelector root "slot[name=media]")
        default-slot (.querySelector root "slot:not([name])")]
    (is (some? media-slot) "should have media slot")
    (is (some? default-slot) "should have default slot")))

;; ── Read-only properties ────────────────────────────────────────────────────

(deftest read-only-properties-test
  (let [el (append! (make-el))]
    (is (= -1 (.-activeIndex el)))
    (is (= 0 (.-progress el)))))

;; ── Reconnection ────────────────────────────────────────────────────────────

(deftest reconnection-test
  (let [el (append! (make-el))]
    (.remove el)
    (append! el)
    ;; Should not throw
    (is (some? (.-shadowRoot el)))))

;; ── Label removal ──────────────────────────────────────────────────────────

(deftest label-empty-removes-aria-label-test
  (let [el (append! (make-el))
        container (shadow-part el "container")]
    (.setAttribute el "label" "My story")
    (is (= "My story" (.getAttribute container "aria-label")))
    (.removeAttribute el "label")
    (is (nil? (.getAttribute container "aria-label")))))

;; ── Events (async — IntersectionObserver fires asynchronously) ─────────────

(deftest enter-event-fires-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-steps! el ["s1" "s2" "s3"])
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

(deftest enter-event-bubbles-and-is-composed-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-steps! el ["s1"])
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
  (async done
    (let [^js el (make-el)
          leave-events (atom [])]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-leave
                         (fn [^js e] (swap! leave-events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (.remove el)
         (is (pos? (count @leave-events))
             "leave event should fire on disconnect")
         (when (pos? (count @leave-events))
           (let [^js d (first @leave-events)]
             (is (number? (.-progress d)))))
         (append! el)
         (done))
       300))))

(deftest disabled-suppresses-scroll-test
  (async done
    (let [^js el (make-el)
          step-events (atom [])]
      (add-steps! el ["s1" "s2" "s3"])
      (set! (.. el -style -height) "200px")
      (.setAttribute el "disabled" "")
      (.addEventListener el model/event-step-change
                         (fn [^js e] (swap! step-events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (is (= -1 (.-activeIndex el))
             "activeIndex should stay -1 when disabled")
         (is (zero? (count @step-events))
             "no step-change events should fire when disabled")
         (done))
       300))))

(deftest disabled-cleans-active-state-test
  (async done
    (let [^js el (make-el)]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         ;; Now disable — should clear active state
         (.setAttribute el "disabled" "")
         (is (= -1 (.-activeIndex el))
             "activeIndex should be -1 after disabling")
         (let [children (.-children el)]
           (dotimes [i (.-length children)]
             (is (not (.hasAttribute (aget children i) "data-active"))
                 "data-active should be removed from all children")))
         (done))
       300))))

(deftest live-region-announces-enter-test
  (async done
    (let [^js el (make-el)]
      (add-steps! el ["s1"])
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js live (shadow-part el "live")]
           (is (some? live) "live region should exist"))
         (done))
       150))))

;; ── Event detail validation ─────────────────────────────────────────────────

(deftest enter-event-detail-shape-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-enter
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (is (pos? (count @events))
             "enter event should fire")
         (when (pos? (count @events))
           (let [^js d (first @events)]
             (is (number? (.-progress d)) "detail.progress should be a number")
             (is (<= 0 (.-progress d) 1) "detail.progress should be in [0,1]")))
         (done))
       400))))

(deftest progress-event-detail-shape-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (.addEventListener el model/event-progress
                         (fn [^js e] (swap! events conj (.-detail e))))
      (append! el)
      (js/setTimeout
       (fn []
         (when (pos? (count @events))
           (let [^js d (first @events)]
             (is (number? (.-progress d)) "detail.progress should be a number")
             (is (number? (.-activeIndex d)) "detail.activeIndex should be a number")
             ;; activeId may be nil or a string
             (is (or (nil? (.-activeId d))
                     (string? (.-activeId d)))
                 "detail.activeId should be nil or string")))
         (done))
       300))))

;; ── Autoplay ───────────────────────────────────────────────────────────────

(deftest autoplay-attribute-reflection-test
  (let [el (append! (make-el))]
    (is (false? (.-autoplay el)) "autoplay defaults to false")
    (set! (.-autoplay el) true)
    (is (true? (.hasAttribute el "autoplay")))
    (set! (.-autoplay el) false)
    (is (false? (.hasAttribute el "autoplay")))))

(deftest autoplay-speed-property-test
  (let [el (append! (make-el))]
    (is (= 50 (.-autoplaySpeed el)) "default speed is 50")
    (set! (.-autoplaySpeed el) 100)
    (is (= "100" (.getAttribute el "autoplay-speed")))))

(deftest autoplay-loop-property-test
  (let [el (append! (make-el))]
    (is (false? (.-autoplayLoop el)))
    (set! (.-autoplayLoop el) true)
    (is (true? (.hasAttribute el "autoplay-loop")))))

(deftest autoplay-indicator-property-test
  (let [el (append! (make-el))]
    (is (false? (.-autoplayIndicator el)))
    (set! (.-autoplayIndicator el) true)
    (is (true? (.hasAttribute el "autoplay-indicator")))))

(deftest autoplay-paused-default-test
  (let [el (append! (make-el))]
    (is (false? (.-autoplayPaused el))
        "autoplayPaused should default to false")))

(deftest autoplay-indicator-dom-test
  (let [el (append! (make-el))]
    (is (some? (shadow-part el "indicator"))
        "should have indicator part in shadow DOM")))

(deftest autoplay-disabled-interaction-test
  (async done
    (let [^js el (make-el)]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (.setAttribute el "autoplay" "")
      (.setAttribute el "disabled" "")
      (append! el)
      (js/setTimeout
       (fn []
         (is (false? (.hasAttribute el "tabindex"))
             "should not be focusable when disabled overrides autoplay")
         (done))
       300))))

(deftest autoplay-stops-on-disconnect-test
  (async done
    (let [^js el (make-el)]
      (add-steps! el ["s1" "s2"])
      (set! (.. el -style -height) "200px")
      (.setAttribute el "autoplay" "")
      (append! el)
      (js/setTimeout
       (fn []
         (.remove el)
         (is (false? (.-autoplayPaused el))
             "autoplayPaused should be false after disconnect")
         (is (false? (.hasAttribute el "data-autoplay-paused"))
             "data-autoplay-paused should be removed after disconnect")
         (append! el)
         (done))
       300))))
