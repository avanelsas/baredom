(ns baredom.components.x-welcome-tour.x-welcome-tour-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-welcome-tour.x-welcome-tour :as tour]
            [baredom.components.x-welcome-tour.model :as model]
            [baredom.components.x-welcome-tour-step.x-welcome-tour-step :as step]
            [baredom.components.x-welcome-tour-step.model :as step-model]))

;; ── Setup ───────────────────────────────────────────────────────────────────
(step/register!)
(tour/register!)

(defn cleanup-dom! []
  ;; Remove tour and step elements
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [^js node (.querySelectorAll js/document step-model/tag-name)]
    (.remove node))
  ;; Remove target divs created by make-tour
  (doseq [^js node (.querySelectorAll js/document "[id^='target-']")]
    (.remove node))
  ;; Clean up overlay layers
  (when-let [^js root (.getElementById js/document "__xOverlayRoot")]
    (.remove root)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-tour
  "Create a tour with N target divs and step elements."
  [n]
  (let [^js tour-el (.createElement js/document model/tag-name)]
    (dotimes [i n]
      (let [^js target (.createElement js/document "div")
            ^js step-el (.createElement js/document step-model/tag-name)]
        (set! (.-id target) (str "target-" i))
        (set! (.. target -style -width) "100px")
        (set! (.. target -style -height) "40px")
        (set! (.. target -style -position) "absolute")
        (set! (.. target -style -left) (str (* i 200) "px"))
        (set! (.. target -style -top) "100px")
        (.appendChild (.-body js/document) target)
        (.setAttribute step-el "target" (str "#target-" i))
        (.setAttribute step-el "title" (str "Step " (inc i)))
        (let [^js content (.createElement js/document "p")]
          (set! (.-textContent content) (str "Content for step " (inc i)))
          (.appendChild step-el content))
        (.appendChild tour-el step-el)))
    tour-el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

;; ── Registration & structure ────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-dom-test
  (let [^js el (append! (make-tour 1))]
    (is (some? (.-shadowRoot el)))))

;; ── Properties ──────────────────────────────────────────────────────────────
(deftest total-steps-property-test
  (let [^js el (append! (make-tour 3))]
    (is (= 3 (.-totalSteps el)))))

(deftest step-property-reflects-test
  (let [^js el (append! (make-tour 3))]
    (set! (.-step el) 1)
    (is (= "1" (.getAttribute el "step")))))

(deftest step-property-get-test
  (let [^js el (append! (make-tour 3))]
    (.setAttribute el "step" "2")
    (is (= 2 (.-step el)))))

(deftest open-property-reflects-test
  (let [^js el (append! (make-tour 1))]
    (set! (.-open el) true)
    (is (= "" (.getAttribute el "open")))
    (set! (.-open el) false)
    (is (nil? (.getAttribute el "open")))))

(deftest connector-property-reflects-test
  (let [^js el (append! (make-tour 1))]
    (set! (.-connector el) "curve")
    (is (= "curve" (.getAttribute el "connector")))))

(deftest connector-property-get-test
  (let [^js el (append! (make-tour 1))]
    (.setAttribute el "connector" "line")
    (is (= "line" (.-connector el)))))

(deftest connector-property-default-test
  (let [^js el (append! (make-tour 1))]
    (is (= "arrow" (.-connector el)))))

(deftest prev-label-property-test
  (let [^js el (append! (make-tour 1))]
    (is (= "Back" (.-prevLabel el)))
    (set! (.-prevLabel el) "Previous")
    (is (= "Previous" (.getAttribute el "prev-label")))))

(deftest next-label-property-test
  (let [^js el (append! (make-tour 1))]
    (is (= "Next" (.-nextLabel el)))
    (set! (.-nextLabel el) "Continue")
    (is (= "Continue" (.getAttribute el "next-label")))))

(deftest done-label-property-test
  (let [^js el (append! (make-tour 1))]
    (is (= "Done" (.-doneLabel el)))
    (set! (.-doneLabel el) "Finish")
    (is (= "Finish" (.getAttribute el "done-label")))))

(deftest skip-label-property-test
  (let [^js el (append! (make-tour 1))]
    (is (= "Skip" (.-skipLabel el)))
    (set! (.-skipLabel el) "Exit")
    (is (= "Exit" (.getAttribute el "skip-label")))))

;; ── Open / close ────────────────────────────────────────────────────────────
(deftest open-creates-overlay-test
  (async done
    (let [^js el (append! (make-tour 2))]
      (.setAttribute el "open" "")
      (js/setTimeout
       (fn []
         (let [^js overlay (.getElementById js/document "__xOverlayRoot")]
           (is (some? overlay) "Overlay root should exist when tour is open"))
         (done))
       100))))

(deftest close-removes-overlay-test
  (async done
    (let [^js el (append! (make-tour 2))]
      (.start el)
      (js/setTimeout
       (fn []
         (.skip el)
         (js/setTimeout
          (fn []
            (is (false? (.-open el)))
            (done))
          100))
       100))))

;; ── start / next / prev / goTo ──────────────────────────────────────────────
(deftest start-event-fires-test
  (async done
    (let [^js el   (append! (make-tour 2))
          events   (atom [])]
      (.addEventListener el model/event-start
                         (fn [^js _e] (swap! events conj true)))
      (.start el)
      (js/setTimeout
       (fn []
         (is (= 1 (count @events)))
         (is (true? (.-open el)))
         (is (= 0 (.-step el)))
         (done))
       100))))

(deftest next-advances-step-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 1 (.-step el)))
         (done))
       100))))

(deftest prev-goes-back-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (.prev el)
         (is (= 0 (.-step el)))
         (done))
       100))))

(deftest prev-at-first-step-stays-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.prev el)
         (is (= 0 (.-step el)) "prev at step 0 should remain at 0")
         (done))
       100))))

(deftest go-to-test
  (async done
    (let [^js el (append! (make-tour 5))]
      (.start el)
      (js/setTimeout
       (fn []
         (.goTo el 3)
         (is (= 3 (.-step el)))
         (done))
       100))))

(deftest go-to-clamps-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.goTo el 99)
         (is (= 2 (.-step el)) "goTo beyond range should clamp to last")
         (done))
       100))))

;; ── complete / skip ─────────────────────────────────────────────────────────
(deftest complete-closes-tour-test
  (async done
    (let [^js el     (append! (make-tour 2))
          completed? (atom false)]
      (.addEventListener el model/event-complete
                         (fn [^js _e] (reset! completed? true)))
      (.start el)
      (js/setTimeout
       (fn []
         (.complete el)
         (is (false? (.-open el)))
         (is (true? @completed?))
         (done))
       100))))

(deftest complete-detail-has-steps-completed-test
  (async done
    (let [^js el (append! (make-tour 3))
          detail (atom nil)]
      (.addEventListener el model/event-complete
                         (fn [^js e] (reset! detail (.-detail e))))
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (.next el)
         (.complete el)
         (is (= 3 (.-stepsCompleted @detail)))
         (done))
       100))))

(deftest skip-closes-tour-test
  (async done
    (let [^js el   (append! (make-tour 2))
          skipped? (atom false)]
      (.addEventListener el model/event-skip
                         (fn [^js _e] (reset! skipped? true)))
      (.start el)
      (js/setTimeout
       (fn []
         (.skip el)
         (is (false? (.-open el)))
         (is (true? @skipped?))
         (done))
       100))))

(deftest skip-detail-has-current-step-test
  (async done
    (let [^js el (append! (make-tour 3))
          detail (atom nil)]
      (.addEventListener el model/event-skip
                         (fn [^js e] (reset! detail (.-detail e))))
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (.skip el)
         (is (= 1 (.-step @detail)))
         (done))
       100))))

(deftest last-step-next-completes-test
  (async done
    (let [^js el     (append! (make-tour 2))
          completed? (atom false)]
      (.addEventListener el model/event-complete
                         (fn [^js _e] (reset! completed? true)))
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)  ;; step 0 -> 1 (last)
         (.next el)  ;; should complete
         (is (true? @completed?))
         (is (false? (.-open el)))
         (done))
       100))))

;; ── step-change event ───────────────────────────────────────────────────────
(deftest step-change-event-fires-test
  (async done
    (let [^js el (append! (make-tour 3))
          events (atom [])]
      (.addEventListener el model/event-step-change
                         (fn [^js e]
                           (swap! events conj
                                  {:step (.. e -detail -step)
                                   :prev (.. e -detail -previousStep)})))
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 1 (count @events)))
         (is (= {:step 1 :prev 0} (first @events)))
         (done))
       100))))

(deftest step-change-cancelable-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.addEventListener el model/event-step-change
                         (fn [^js e] (.preventDefault e)))
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (is (= 0 (.-step el)) "Step should not advance when event is cancelled")
         (done))
       100))))

(deftest step-change-cancel-blocks-prev-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.next el)
         (.addEventListener el model/event-step-change
                            (fn [^js e] (.preventDefault e)))
         (.prev el)
         (is (= 1 (.-step el)) "Step should not go back when event is cancelled")
         (done))
       100))))

;; ── Edge cases ──────────────────────────────────────────────────────────────
(deftest zero-steps-tour-test
  (testing "Tour with 0 steps does not crash"
    (let [^js el (.createElement js/document model/tag-name)]
      (append! el)
      (is (= 0 (.-totalSteps el))))))

(deftest zero-steps-start-test
  (async done
    (let [^js el (.createElement js/document model/tag-name)]
      (append! el)
      (.start el)
      (js/setTimeout
       (fn []
         ;; Should open but not crash
         (is (true? (.-open el)))
         (.skip el)
         (done))
       100))))

(deftest missing-target-does-not-crash-test
  (async done
    (let [^js tour-el (.createElement js/document model/tag-name)
          ^js step-el (.createElement js/document step-model/tag-name)]
      (.setAttribute step-el "target" "#nonexistent-element")
      (.setAttribute step-el "title" "Missing")
      (.appendChild tour-el step-el)
      (append! tour-el)
      (.start tour-el)
      (js/setTimeout
       (fn []
         ;; Should open without throwing — cutout hidden off-screen
         (is (true? (.-open tour-el)))
         (.skip tour-el)
         (done))
       100))))

;; ── Counter and dots attributes ─────────────────────────────────────────────
(deftest counter-attribute-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.setAttribute el "counter" "")
      (.start el)
      (js/setTimeout
       (fn []
         ;; Tour is open with counter enabled — verify no crash
         (is (true? (.-open el)))
         (.skip el)
         (done))
       100))))

(deftest dots-attribute-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.setAttribute el "dots" "")
      (.start el)
      (js/setTimeout
       (fn []
         (is (true? (.-open el)))
         (.skip el)
         (done))
       100))))

;; ── Keyboard interaction ────────────────────────────────────────────────────
(deftest escape-key-skips-tour-test
  (async done
    (let [^js el   (append! (make-tour 2))
          skipped? (atom false)]
      (.addEventListener el model/event-skip
                         (fn [^js _e] (reset! skipped? true)))
      (.start el)
      (js/setTimeout
       (fn []
         ;; Dispatch Escape on the layer shadow root
         (let [layer-refs (aget el "__xWelcomeTourLayer")]
           (when layer-refs
             (let [^js layer  (:layer layer-refs)
                   ^js shadow (.-shadowRoot layer)
                   ^js ev     (js/KeyboardEvent. "keydown"
                                                 #js {:key     "Escape"
                                                      :bubbles true})]
               (.dispatchEvent shadow ev))))
         (js/setTimeout
          (fn []
            (is (true? @skipped?))
            (is (false? (.-open el)))
            (done))
          50))
       100))))

(deftest arrow-right-advances-step-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (let [layer-refs (aget el "__xWelcomeTourLayer")]
           (when layer-refs
             (let [^js layer  (:layer layer-refs)
                   ^js shadow (.-shadowRoot layer)
                   ^js ev     (js/KeyboardEvent. "keydown"
                                                 #js {:key     "ArrowRight"
                                                      :bubbles true})]
               (.dispatchEvent shadow ev))))
         (js/setTimeout
          (fn []
            (is (= 1 (.-step el)))
            (done))
          50))
       100))))

(deftest arrow-left-goes-back-test
  (async done
    (let [^js el (append! (make-tour 3))]
      (.start el)
      (js/setTimeout
       (fn []
         (.next el) ;; go to step 1
         (let [layer-refs (aget el "__xWelcomeTourLayer")]
           (when layer-refs
             (let [^js layer  (:layer layer-refs)
                   ^js shadow (.-shadowRoot layer)
                   ^js ev     (js/KeyboardEvent. "keydown"
                                                 #js {:key     "ArrowLeft"
                                                      :bubbles true})]
               (.dispatchEvent shadow ev))))
         (js/setTimeout
          (fn []
            (is (= 0 (.-step el)))
            (done))
          50))
       100))))

;; ── Accessibility ───────────────────────────────────────────────────────────
(deftest popover-has-dialog-role-test
  (async done
    (let [^js el (append! (make-tour 2))]
      (.start el)
      (js/setTimeout
       (fn []
         (let [layer-refs (aget el "__xWelcomeTourLayer")]
           (when layer-refs
             (let [^js popover (:popover layer-refs)]
               (is (= "dialog" (.getAttribute popover "role"))))))
         (.skip el)
         (done))
       100))))
