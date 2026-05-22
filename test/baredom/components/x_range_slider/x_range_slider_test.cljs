(ns baredom.components.x-range-slider.x-range-slider-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-range-slider.x-range-slider :as x]
            [baredom.components.x-range-slider.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [^js form (.querySelectorAll js/document "form.x-range-slider-test-form")]
    (.remove form)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))
(defn css-var [^js el name] (.getPropertyValue (.-style el) name))

(defn- key-evt
  ([key] (key-evt key false))
  ([key shift?]
   (js/KeyboardEvent. "keydown"
                      #js {:key key :shiftKey shift? :bubbles true :cancelable true})))

(defn- pointer-evt [cx cy]
  (js/PointerEvent. "pointerdown"
                    #js {:pointerId 1 :clientX cx :clientY cy
                         :bubbles true :cancelable true}))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-range-slider should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                       "shadow root should exist")
    (is (some? (shadow-part el "[part=base]"))          "base div should exist")
    (is (some? (shadow-part el "[part=header]"))        "header div should exist")
    (is (some? (shadow-part el "[part=label-text]"))    "label-text span should exist")
    (is (some? (shadow-part el "[part=value-text]"))    "value-text span should exist")
    (is (some? (shadow-part el "[part=track]"))         "track div should exist")
    (is (some? (shadow-part el "[part=track-fill]"))    "track-fill div should exist")
    (is (some? (shadow-part el "[part=thumb-start]"))   "start thumb should exist")
    (is (some? (shadow-part el "[part=thumb-end]"))     "end thumb should exist")))

(deftest thumb-roles-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")
        thumb-e (shadow-part el "[part=thumb-end]")]
    (is (= "slider" (.getAttribute thumb-s "role")))
    (is (= "slider" (.getAttribute thumb-e "role")))
    (is (= "0" (.getAttribute thumb-s "tabindex")))
    (is (= "0" (.getAttribute thumb-e "tabindex")))
    (is (= "group" (.getAttribute (shadow-part el "[part=base]") "role")))))

;; ---------------------------------------------------------------------------
;; Default geometry
;; ---------------------------------------------------------------------------

(deftest default-geometry-test
  (let [el      (append! (make-el))
        base-el (shadow-part el "[part=base]")]
    (is (= "0.00%"   (css-var base-el "--_x-range-slider-start-pct")))
    (is (= "100.00%" (css-var base-el "--_x-range-slider-end-pct")))
    (is (= "0.00%"   (css-var base-el "--_x-range-slider-fill-left")))
    (is (= "100.00%" (css-var base-el "--_x-range-slider-fill-width")))))

(deftest default-size-test
  (let [el (append! (make-el))]
    (is (= "md" (.getAttribute (shadow-part el "[part=base]") "data-size")))))

;; ---------------------------------------------------------------------------
;; Attribute → geometry
;; ---------------------------------------------------------------------------

(deftest attribute-reflection-test
  (let [el      (append! (make-el))
        base-el (shadow-part el "[part=base]")]
    (.setAttribute el model/attr-start "20")
    (.setAttribute el model/attr-end "80")
    (is (= "20.00%" (css-var base-el "--_x-range-slider-start-pct")))
    (is (= "80.00%" (css-var base-el "--_x-range-slider-end-pct")))
    (is (= "20.00%" (css-var base-el "--_x-range-slider-fill-left")))
    (is (= "60.00%" (css-var base-el "--_x-range-slider-fill-width")))))

(deftest min-max-attrs-test
  (let [el      (append! (make-el))
        base-el (shadow-part el "[part=base]")]
    (.setAttribute el model/attr-min "0")
    (.setAttribute el model/attr-max "200")
    (.setAttribute el model/attr-start "50")
    (.setAttribute el model/attr-end "150")
    (is (= "25.00%" (css-var base-el "--_x-range-slider-start-pct")))
    (is (= "75.00%" (css-var base-el "--_x-range-slider-end-pct")))))

(deftest size-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute (shadow-part el "[part=base]") "data-size")))))

;; ---------------------------------------------------------------------------
;; Thumb ARIA — effective bounds
;; ---------------------------------------------------------------------------

(deftest thumb-aria-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")
        thumb-e (shadow-part el "[part=thumb-end]")]
    (.setAttribute el model/attr-start "20")
    (.setAttribute el model/attr-end "80")
    (is (= "20" (.getAttribute thumb-s "aria-valuenow")))
    (is (= "80" (.getAttribute thumb-e "aria-valuenow")))
    (is (= "0"  (.getAttribute thumb-s "aria-valuemin")))
    (is (= "80" (.getAttribute thumb-s "aria-valuemax"))
        "start thumb cannot pass the end value")
    (is (= "20" (.getAttribute thumb-e "aria-valuemin"))
        "end thumb cannot pass the start value")
    (is (= "100" (.getAttribute thumb-e "aria-valuemax")))))

(deftest thumb-aria-min-gap-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")
        thumb-e (shadow-part el "[part=thumb-end]")]
    (.setAttribute el model/attr-start "20")
    (.setAttribute el model/attr-end "80")
    (.setAttribute el model/attr-min-gap "10")
    (is (= "70" (.getAttribute thumb-s "aria-valuemax"))
        "start thumb effective max is end - min-gap")
    (is (= "30" (.getAttribute thumb-e "aria-valuemin"))
        "end thumb effective min is start + min-gap")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest string-properties-test
  (let [el (append! (make-el))]
    (set! (.-start el) "20")  (is (= "20" (.getAttribute el model/attr-start)))
    (set! (.-end el) "80")    (is (= "80" (.getAttribute el model/attr-end)))
    (set! (.-min el) "5")     (is (= "5" (.getAttribute el model/attr-min)))
    (set! (.-max el) "500")   (is (= "500" (.getAttribute el model/attr-max)))
    (set! (.-step el) "0.5")  (is (= "0.5" (.getAttribute el model/attr-step)))
    (set! (.-minGap el) "5")  (is (= "5" (.getAttribute el model/attr-min-gap)))
    (set! (.-name el) "range") (is (= "range" (.getAttribute el model/attr-name)))
    (set! (.-label el) "Price") (is (= "Price" (.getAttribute el model/attr-label)))
    (set! (.-size el) "lg")   (is (= "lg" (.getAttribute el model/attr-size)))))

(deftest boolean-properties-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly))
    (set! (.-readOnly el) false)
    (is (not (.hasAttribute el model/attr-readonly)))
    (set! (.-showValue el) true)
    (is (.hasAttribute el model/attr-show-value))
    (set! (.-showValue el) false)
    (is (not (.hasAttribute el model/attr-show-value)))))

;; ---------------------------------------------------------------------------
;; Label and show-value
;; ---------------------------------------------------------------------------

(deftest label-shows-header-test
  (let [el        (append! (make-el))
        header-el (shadow-part el "[part=header]")
        label-el  (shadow-part el "[part=label-text]")]
    (.setAttribute el model/attr-label "Price range")
    (is (not= "none" (.-display (.-style header-el))))
    (is (= "Price range" (.-textContent label-el)))))

(deftest show-value-test
  (let [el        (append! (make-el))
        header-el (shadow-part el "[part=header]")
        value-el  (shadow-part el "[part=value-text]")]
    (.setAttribute el model/attr-start "25")
    (.setAttribute el model/attr-end "75")
    (.setAttribute el model/attr-show-value "")
    (is (not= "none" (.-display (.-style header-el))))
    (is (= "25 – 75" (.-textContent value-el)))))

(deftest header-hidden-by-default-test
  (let [el (append! (make-el))]
    (is (= "none" (.-display (.-style (shadow-part el "[part=header]")))))))

;; ---------------------------------------------------------------------------
;; Keyboard — value changes
;; ---------------------------------------------------------------------------

(deftest keyboard-arrow-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")]
    (.setAttribute el model/attr-start "20")
    (.setAttribute el model/attr-end "80")
    (.dispatchEvent thumb-s (key-evt "ArrowRight"))
    (is (= "21" (.getAttribute el model/attr-start))
        "ArrowRight increments start by one step")))

(deftest keyboard-shift-arrow-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")]
    (.setAttribute el model/attr-start "20")
    (.setAttribute el model/attr-end "80")
    (.dispatchEvent thumb-s (key-evt "ArrowRight" true))
    (is (= "30" (.getAttribute el model/attr-start))
        "Shift+ArrowRight increments start by ten steps")))

(deftest keyboard-home-end-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")
        thumb-e (shadow-part el "[part=thumb-end]")]
    (.setAttribute el model/attr-start "50")
    (.setAttribute el model/attr-end "50")
    (.dispatchEvent thumb-s (key-evt "Home"))
    (is (= "0" (.getAttribute el model/attr-start)) "Home moves start to min")
    (.dispatchEvent thumb-e (key-evt "End"))
    (is (= "100" (.getAttribute el model/attr-end)) "End moves end to max")))

(deftest keyboard-clamp-test
  (testing "start thumb cannot cross the end thumb"
    (let [el      (append! (make-el))
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-start "80")
      (.setAttribute el model/attr-end "80")
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (is (= "80" (.getAttribute el model/attr-start))
          "start stays at the end value"))))

(deftest keyboard-min-gap-test
  (testing "start thumb keeps min-gap distance from end"
    (let [el      (append! (make-el))
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-start "50")
      (.setAttribute el model/attr-end "70")
      (.setAttribute el model/attr-min-gap "20")
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (is (= "50" (.getAttribute el model/attr-start))
          "start cannot move within min-gap of end"))))

;; ---------------------------------------------------------------------------
;; Readonly / disabled
;; ---------------------------------------------------------------------------

(deftest readonly-blocks-arrow-keys-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")]
    (.setAttribute el model/attr-start "50")
    (.setAttribute el model/attr-end "80")
    (.setAttribute el model/attr-readonly "")
    (let [evt (key-evt "ArrowRight")]
      (.dispatchEvent thumb-s evt)
      (is (true? (.-defaultPrevented evt)) "ArrowRight is consumed in readonly mode")
      (is (= "50" (.getAttribute el model/attr-start)) "value does not change"))))

(deftest readonly-allows-tab-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")]
    (.setAttribute el model/attr-readonly "")
    (let [evt (key-evt "Tab")]
      (.dispatchEvent thumb-s evt)
      (is (false? (.-defaultPrevented evt)) "Tab is not blocked in readonly mode"))))

(deftest disabled-thumb-tabindex-test
  (let [el      (append! (make-el))
        thumb-s (shadow-part el "[part=thumb-start]")
        thumb-e (shadow-part el "[part=thumb-end]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "-1" (.getAttribute thumb-s "tabindex")))
    (is (= "-1" (.getAttribute thumb-e "tabindex")))
    (.removeAttribute el model/attr-disabled)
    (is (= "0" (.getAttribute thumb-s "tabindex")))))

(deftest disabled-prevents-input-test
  (async done
    (let [el      (append! (make-el))
          fired   (atom false)
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-start "20")
      (.setAttribute el model/attr-end "80")
      (.setAttribute el model/attr-disabled "")
      (.addEventListener el model/event-input (fn [_] (reset! fired true)))
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (false? @fired) "input event should not fire when disabled")
         (is (= "20" (.getAttribute el model/attr-start)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest input-event-test
  (async done
    (let [el      (append! (make-el))
          seen    (atom nil)
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-min "0")
      (.setAttribute el model/attr-max "100")
      (.setAttribute el model/attr-start "20")
      (.setAttribute el model/attr-end "80")
      (.addEventListener
       el model/event-input
       (fn [^js ev]
         (reset! seen {:start (.-start (.-detail ev))
                       :end   (.-end (.-detail ev))
                       :min   (.-min (.-detail ev))
                       :max   (.-max (.-detail ev))})))
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "x-range-slider-input should fire")
         (is (= 21 (:start @seen)))
         (is (= 80 (:end @seen)))
         (is (= 0 (:min @seen)))
         (is (= 100 (:max @seen)))
         (done))
       0))))

(deftest change-event-test
  (async done
    (let [el      (append! (make-el))
          seen    (atom nil)
          thumb-e (shadow-part el "[part=thumb-end]")]
      (.setAttribute el model/attr-start "20")
      (.setAttribute el model/attr-end "60")
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen {:start (.-start (.-detail ev))
                       :end   (.-end (.-detail ev))})))
      (.dispatchEvent thumb-e (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "x-range-slider-change should fire")
         (is (= 20 (:start @seen)))
         (is (= 61 (:end @seen)))
         (done))
       0))))

(deftest change-request-detail-test
  (async done
    (let [el      (append! (make-el))
          seen    (atom nil)
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-start "20")
      (.setAttribute el model/attr-end "80")
      (.addEventListener
       el model/event-change-request
       (fn [^js ev]
         (reset! seen {:start         (.-start (.-detail ev))
                       :end           (.-end (.-detail ev))
                       :previous-start (.-previousStart (.-detail ev))
                       :previous-end   (.-previousEnd (.-detail ev))})))
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "x-range-slider-change-request should fire")
         (is (= 21 (:start @seen)))
         (is (= 80 (:end @seen)))
         (is (= 20 (:previous-start @seen)))
         (is (= 80 (:previous-end @seen)))
         (done))
       0))))

(deftest change-request-cancel-test
  (async done
    (let [el      (append! (make-el))
          thumb-s (shadow-part el "[part=thumb-start]")]
      (.setAttribute el model/attr-start "20")
      (.setAttribute el model/attr-end "80")
      (.addEventListener el model/event-change-request
                         (fn [^js ev] (.preventDefault ev)))
      (.dispatchEvent thumb-s (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (= "20" (.getAttribute el model/attr-start))
             "start should not change when change-request is cancelled")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Track click — moves the nearest thumb
;; ---------------------------------------------------------------------------

(deftest track-click-test
  (async done
    (let [el (make-el)]
      (set! (.. el -style -width) "240px")
      (append! el)
      (.setAttribute el model/attr-start "0")
      (.setAttribute el model/attr-end "100")
      (let [seen     (atom nil)
            track    (shadow-part el "[part=track]")
            ^js rect (.getBoundingClientRect track)
            cx       (+ (.-left rect) (* 0.3 (.-width rect)))
            cy       (.-top rect)]
        (.addEventListener
         el model/event-input
         (fn [^js ev]
           (reset! seen {:start (.-start (.-detail ev))
                         :end   (.-end (.-detail ev))})))
        (.dispatchEvent track (pointer-evt cx cy))
        (js/setTimeout
         (fn []
           (is (some? @seen) "a track click should move the nearest thumb")
           (is (= 30 (:start @seen)) "start thumb jumps to the click position")
           (is (= 100 (:end @seen)) "end thumb is untouched")
           (done))
         0)))))

;; ---------------------------------------------------------------------------
;; change fires only on a real move
;; ---------------------------------------------------------------------------

(deftest no-op-thumb-click-emits-no-change-test
  (let [el (make-el)]
    (set! (.. el -style -width) "240px")
    (append! el)
    (.setAttribute el model/attr-start "30")
    (.setAttribute el model/attr-end "70")
    (let [thumb-s  (shadow-part el "[part=thumb-start]")
          ^js rect (.getBoundingClientRect thumb-s)
          cx       (+ (.-left rect) (/ (.-width rect) 2))
          cy       (+ (.-top rect) (/ (.-height rect) 2))
          changed  (atom false)]
      (.addEventListener el model/event-change (fn [_] (reset! changed true)))
      ;; in-place grab of a thumb, released without moving it
      (.dispatchEvent thumb-s (pointer-evt cx cy))
      (.dispatchEvent thumb-s (js/PointerEvent. "pointerup"
                                                #js {:pointerId 1 :clientX cx
                                                     :clientY cy :bubbles true}))
      (is (false? @changed)
          "grabbing a thumb without moving it must not emit change"))))

(deftest track-jump-release-emits-change-test
  (let [el (make-el)]
    (set! (.. el -style -width) "240px")
    (append! el)
    (.setAttribute el model/attr-start "0")
    (.setAttribute el model/attr-end "100")
    (let [track    (shadow-part el "[part=track]")
          thumb-s  (shadow-part el "[part=thumb-start]")
          ^js rect (.getBoundingClientRect track)
          cx       (+ (.-left rect) (* 0.3 (.-width rect)))
          cy       (.-top rect)
          seen     (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js ev] (reset! seen (.-start (.-detail ev)))))
      (.dispatchEvent track (pointer-evt cx cy))
      (.dispatchEvent thumb-s (js/PointerEvent. "pointerup"
                                                #js {:pointerId 1 :clientX cx
                                                     :clientY cy :bubbles true}))
      (is (= 30 @seen)
          "a track jump followed by release emits change with the moved value"))))

;; ---------------------------------------------------------------------------
;; Interrupted drags must not resume
;; ---------------------------------------------------------------------------

(deftest pointer-cancel-stops-drag-test
  (let [el (make-el)]
    (set! (.. el -style -width) "240px")
    (append! el)
    (.setAttribute el model/attr-start "0")
    (.setAttribute el model/attr-end "100")
    (let [track    (shadow-part el "[part=track]")
          thumb-s  (shadow-part el "[part=thumb-start]")
          ^js rect (.getBoundingClientRect track)
          near-x   (+ (.-left rect) (* 0.2 (.-width rect)))
          far-x    (+ (.-left rect) (* 0.6 (.-width rect)))
          cy       (.-top rect)]
      (.dispatchEvent track (pointer-evt near-x cy))
      (.dispatchEvent thumb-s (js/PointerEvent. "pointercancel"
                                                #js {:pointerId 1 :bubbles true}))
      (let [after (.getAttribute el model/attr-start)
            fired (atom false)]
        (.addEventListener el model/event-input (fn [_] (reset! fired true)))
        (.dispatchEvent thumb-s (js/PointerEvent. "pointermove"
                                                  #js {:pointerId 1 :clientX far-x
                                                       :clientY cy :bubbles true}))
        (is (false? @fired)
            "a pointermove after pointercancel must not move a thumb")
        (is (= after (.getAttribute el model/attr-start))
            "start value is unchanged after the cancelled drag")))))

(deftest disconnect-clears-drag-test
  (let [el (make-el)]
    (set! (.. el -style -width) "240px")
    (append! el)
    (.setAttribute el model/attr-start "0")
    (.setAttribute el model/attr-end "100")
    (let [track    (shadow-part el "[part=track]")
          ^js rect (.getBoundingClientRect track)]
      (.dispatchEvent track (pointer-evt (+ (.-left rect) (* 0.2 (.-width rect)))
                                         (.-top rect)))
      (.remove el)
      (append! el)
      (let [thumb-s   (shadow-part el "[part=thumb-start]")
            ^js rect2 (.getBoundingClientRect (shadow-part el "[part=track]"))
            fired     (atom false)]
        (.addEventListener el model/event-input (fn [_] (reset! fired true)))
        (.dispatchEvent thumb-s (js/PointerEvent. "pointermove"
                                                  #js {:pointerId 1
                                                       :clientX (+ (.-left rect2)
                                                                   (* 0.6 (.-width rect2)))
                                                       :clientY (.-top rect2)
                                                       :bubbles true}))
        (is (false? @fired)
            "a drag must not survive a disconnect / reconnect")))))

;; ---------------------------------------------------------------------------
;; Form association
;; ---------------------------------------------------------------------------

(deftest form-reset-test
  (let [^js form (.createElement js/document "form")
        el       (make-el)]
    (.add (.-classList form) "x-range-slider-test-form")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.setAttribute el model/attr-start "30")
    (.setAttribute el model/attr-end "60")
    (let [base-el (shadow-part el "[part=base]")]
      (is (= "30.00%" (css-var base-el "--_x-range-slider-start-pct")))
      (.reset form)
      (is (not (.hasAttribute el model/attr-start)) "start attr removed on reset")
      (is (not (.hasAttribute el model/attr-end))   "end attr removed on reset")
      (is (= "0.00%"   (css-var base-el "--_x-range-slider-start-pct")))
      (is (= "100.00%" (css-var base-el "--_x-range-slider-end-pct"))))))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-start "33")
    (.setAttribute el model/attr-end "66")
    (.setAttribute el model/attr-label "Test")
    (.remove el)
    (.appendChild body el)
    (let [base-el  (shadow-part el "[part=base]")
          label-el (shadow-part el "[part=label-text]")]
      (is (= "33.00%" (css-var base-el "--_x-range-slider-start-pct"))
          "geometry should survive a reconnect")
      (is (= "Test" (.-textContent label-el))
          "label should survive a reconnect"))))
