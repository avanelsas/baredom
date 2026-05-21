(ns baredom.components.x-split-pane.x-split-pane-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-split-pane.x-split-pane :as x]
            [baredom.components.x-split-pane.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))

(defn- dispatch-key! [^js target key]
  (.dispatchEvent target
                  (js/KeyboardEvent. "keydown"
                                     #js {:key key :bubbles true :cancelable true})))

(defn- pointer-event [type x y]
  (js/PointerEvent. type #js {:bubbles    true
                              :cancelable true
                              :clientX    x
                              :clientY    y
                              :pointerId  1}))

;; setPointerCapture rejects synthetic pointer ids, so stub the three
;; capture methods on the divider — this drives the real drag handlers
;; without patching the component.
(defn- stub-pointer-capture! [^js el]
  (set! (.-setPointerCapture el)     (fn [_id] nil))
  (set! (.-releasePointerCapture el) (fn [_id] nil))
  (set! (.-hasPointerCapture el)     (fn [_id] true)))

(defn- size-host! [^js el w h]
  (set! (.-width  (.-style el)) w)
  (set! (.-height (.-style el)) h)
  el)

;; ---------------------------------------------------------------------------
;; Registration & structure
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-split-pane should be registered"))

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                       "shadow root should exist")
    (is (some? (shadow-part el "[part=start-pane]"))    "start pane should exist")
    (is (some? (shadow-part el "[part=divider]"))       "divider should exist")
    (is (some? (shadow-part el "[part=divider-line]"))  "divider line should exist")
    (is (some? (shadow-part el "[part=end-pane]"))      "end pane should exist")))

(deftest named-slots-test
  (let [el (append! (make-el))]
    (is (= "start" (.getAttribute (shadow-part el "[part=start-pane] slot") "name"))
        "start pane should host the named 'start' slot")
    (is (= "end" (.getAttribute (shadow-part el "[part=end-pane] slot") "name"))
        "end pane should host the named 'end' slot")))

(deftest divider-aria-roles-test
  (let [el      (append! (make-el))
        divider (shadow-part el "[part=divider]")]
    (is (= "separator" (.getAttribute divider "role")))
    (is (= "0"   (.getAttribute divider "aria-valuemin")))
    (is (= "100" (.getAttribute divider "aria-valuemax")))
    (is (= "start-pane" (.getAttribute divider "aria-controls")))))

;; ---------------------------------------------------------------------------
;; Sizing
;; ---------------------------------------------------------------------------

(deftest default-position-test
  (let [el (append! (make-el))]
    (is (= "50%" (.-flexBasis (.-style (shadow-part el "[part=start-pane]"))))
        "start pane should default to a 50% flex-basis")))

(deftest position-attr-updates-flex-basis-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-position "30")
    (is (= "30%" (.-flexBasis (.-style (shadow-part el "[part=start-pane]")))))))

(deftest position-attr-clamped-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-position "150")
    (is (= "100%" (.-flexBasis (.-style (shadow-part el "[part=start-pane]"))))
        "position should clamp to 100%")))

(deftest min-start-applies-min-width-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-min-start "120")
    (is (= "120px" (.-minWidth (.-style (shadow-part el "[part=start-pane]")))))))

(deftest min-end-applies-min-width-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-min-end "80")
    (is (= "80px" (.-minWidth (.-style (shadow-part el "[part=end-pane]")))))))

(deftest vertical-min-applies-min-height-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-orientation "vertical")
    (.setAttribute el model/attr-min-start "90")
    (let [start-style (.-style (shadow-part el "[part=start-pane]"))]
      (is (= "90px" (.-minHeight start-style)) "vertical split applies the minimum on the block axis")
      (is (= "0px"  (.-minWidth start-style))  "the inline-axis minimum is cleared"))))

;; ---------------------------------------------------------------------------
;; Orientation
;; ---------------------------------------------------------------------------

(deftest default-orientation-test
  (let [el (append! (make-el))]
    (is (= "horizontal" (.getAttribute el "data-orientation")))
    (is (= "vertical"   (.getAttribute (shadow-part el "[part=divider]") "aria-orientation"))
        "a horizontal split is separated by a vertical bar")))

(deftest vertical-orientation-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-orientation "vertical")
    (is (= "vertical"   (.getAttribute el "data-orientation")))
    (is (= "horizontal" (.getAttribute (shadow-part el "[part=divider]") "aria-orientation")))))

;; ---------------------------------------------------------------------------
;; Divider state
;; ---------------------------------------------------------------------------

(deftest divider-tabindex-test
  (let [el (append! (make-el))]
    (is (= "0" (.getAttribute (shadow-part el "[part=divider]") "tabindex"))
        "the divider should be focusable by default")))

(deftest disabled-removes-tabindex-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (not (.hasAttribute (shadow-part el "[part=divider]") "tabindex"))
        "a disabled split-pane removes the divider from the tab order")))

(deftest aria-valuenow-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-position "40")
    (is (= "40" (.getAttribute (shadow-part el "[part=divider]") "aria-valuenow")))))

(deftest divider-label-test
  (let [el (append! (make-el))]
    (is (= "Resize panels" (.getAttribute (shadow-part el "[part=divider]") "aria-label"))
        "the divider has a default accessible label")
    (.setAttribute el model/attr-divider-label "Resize columns")
    (is (= "Resize columns" (.getAttribute (shadow-part el "[part=divider]") "aria-label")))))

;; ---------------------------------------------------------------------------
;; Keyboard resizing
;; ---------------------------------------------------------------------------

(deftest keyboard-increments-position-test
  (let [el (append! (make-el))]
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowRight")
    (is (= "51" (.getAttribute el model/attr-position)))))

(deftest keyboard-decrements-position-test
  (let [el (append! (make-el))]
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowLeft")
    (is (= "49" (.getAttribute el model/attr-position)))))

(deftest keyboard-home-end-test
  (let [el      (append! (make-el))
        divider (shadow-part el "[part=divider]")]
    (dispatch-key! divider "Home")
    (is (= "0" (.getAttribute el model/attr-position)) "Home jumps to the minimum")
    (dispatch-key! divider "End")
    (is (= "100" (.getAttribute el model/attr-position)) "End jumps to the maximum")))

(deftest keyboard-vertical-uses-down-arrow-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-orientation "vertical")
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowDown")
    (is (= "51" (.getAttribute el model/attr-position)))))

(deftest disabled-blocks-keyboard-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowRight")
    (is (nil? (.getAttribute el model/attr-position))
        "a disabled split-pane ignores keyboard resizing")))

(deftest pointermove-without-drag-is-noop-test
  (let [el (append! (make-el))]
    (.dispatchEvent (shadow-part el "[part=divider]")
                    (js/Event. "pointermove" #js {:bubbles true}))
    (is (nil? (.getAttribute el model/attr-position))
        "pointermove without an active drag should not change position")))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest resize-event-test
  (let [el   (append! (make-el))
        seen (atom nil)]
    (.addEventListener el model/event-resize
                       (fn [^js ev] (reset! seen {:position    (.. ev -detail -position)
                                                  :orientation (.. ev -detail -orientation)})))
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowRight")
    (is (= 51 (:position @seen))            "x-split-pane-resize should carry the new position")
    (is (= "horizontal" (:orientation @seen)))))

(deftest resize-end-event-test
  (let [el   (append! (make-el))
        seen (atom nil)]
    (.addEventListener el model/event-resize-end
                       (fn [^js ev] (reset! seen (.. ev -detail -position))))
    (dispatch-key! (shadow-part el "[part=divider]") "ArrowRight")
    (is (= 51 @seen) "x-split-pane-resize-end should fire on a keyboard commit")))

;; ---------------------------------------------------------------------------
;; Pointer drag
;; ---------------------------------------------------------------------------

(deftest pointer-drag-updates-position-test
  (let [el      (size-host! (append! (make-el)) "400px" "200px")
        divider (shadow-part el "[part=divider]")
        host    (.getBoundingClientRect el)
        left    (.-left host)
        right   (.-right host)]
    (stub-pointer-capture! divider)
    (.dispatchEvent divider (pointer-event "pointerdown" (+ left 200) 100))
    (.dispatchEvent divider (pointer-event "pointermove" (- left 50) 100))
    (is (= "0" (.getAttribute el model/attr-position))
        "dragging past the left edge pins the start panel to 0%")
    (.dispatchEvent divider (pointer-event "pointermove" (+ right 50) 100))
    (is (= "100" (.getAttribute el model/attr-position))
        "dragging past the right edge pins the start panel to 100%")
    (.dispatchEvent divider (pointer-event "pointerup" (+ right 50) 100))))

(deftest pointer-drag-respects-min-start-test
  (let [el      (size-host! (append! (make-el)) "400px" "200px")
        divider (shadow-part el "[part=divider]")]
    (.setAttribute el model/attr-min-start "100")
    (let [rect     (.getBoundingClientRect el)
          left     (.-left rect)
          ;; Mirrors clamp-by-mins: min-start px → equivalent percentage.
          expected (str (* 100 (/ 100 (.-width rect))))]
      (stub-pointer-capture! divider)
      (.dispatchEvent divider (pointer-event "pointerdown" (+ left 200) 100))
      (.dispatchEvent divider (pointer-event "pointermove" (- left 50) 100))
      (is (= expected (.getAttribute el model/attr-position))
          "min-start clamps the divider to its equivalent percentage"))))

(deftest pointer-drag-fires-resize-events-test
  (let [el       (size-host! (append! (make-el)) "400px" "200px")
        divider  (shadow-part el "[part=divider]")
        resizes  (atom 0)
        end-seen (atom nil)]
    (.addEventListener el model/event-resize     (fn [_] (swap! resizes inc)))
    (.addEventListener el model/event-resize-end (fn [^js ev] (reset! end-seen (.. ev -detail -position))))
    (let [left (.-left (.getBoundingClientRect el))]
      (stub-pointer-capture! divider)
      (.dispatchEvent divider (pointer-event "pointerdown" (+ left 200) 100))
      (.dispatchEvent divider (pointer-event "pointermove" (+ left 100) 100))
      (.dispatchEvent divider (pointer-event "pointerup"   (+ left 100) 100))
      (is (pos? @resizes)   "pointer drag should fire x-split-pane-resize")
      (is (some? @end-seen) "pointerup should fire x-split-pane-resize-end"))))

(deftest pointer-drag-vertical-test
  (let [el      (size-host! (append! (make-el)) "300px" "400px")
        divider (shadow-part el "[part=divider]")]
    (.setAttribute el model/attr-orientation "vertical")
    (let [rect   (.getBoundingClientRect el)
          top    (.-top rect)
          bottom (.-bottom rect)]
      (stub-pointer-capture! divider)
      (.dispatchEvent divider (pointer-event "pointerdown" 150 (+ top 200)))
      (.dispatchEvent divider (pointer-event "pointermove" 150 (- top 50)))
      (is (= "0" (.getAttribute el model/attr-position))
          "a vertical drag past the top edge pins to 0%")
      (.dispatchEvent divider (pointer-event "pointermove" 150 (+ bottom 50)))
      (is (= "100" (.getAttribute el model/attr-position))
          "a vertical drag past the bottom edge pins to 100%")
      (.dispatchEvent divider (pointer-event "pointerup" 150 (+ bottom 50))))))

(deftest disabled-blocks-pointer-drag-test
  (let [el      (size-host! (append! (make-el)) "400px" "200px")
        divider (shadow-part el "[part=divider]")]
    (.setAttribute el model/attr-disabled "")
    (let [left (.-left (.getBoundingClientRect el))]
      (stub-pointer-capture! divider)
      (.dispatchEvent divider (pointer-event "pointerdown" (+ left 200) 100))
      (.dispatchEvent divider (pointer-event "pointermove" (+ left 50)  100))
      (is (nil? (.getAttribute el model/attr-position))
          "a disabled split-pane ignores pointer drag"))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest position-property-test
  (let [el (append! (make-el))]
    (is (= 50 (.-position el)) "position defaults to 50")
    (set! (.-position el) 30)
    (is (= "30" (.getAttribute el model/attr-position)))
    (is (= 30 (.-position el)))))

(deftest orientation-property-test
  (let [el (append! (make-el))]
    (is (= "horizontal" (.-orientation el)))
    (set! (.-orientation el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-orientation)))
    (is (= "vertical" (.-orientation el)))))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest min-start-property-test
  (let [el (append! (make-el))]
    (set! (.-minStart el) 120)
    (is (= "120" (.getAttribute el model/attr-min-start)))
    (is (= 120 (.-minStart el)))))

(deftest min-end-property-test
  (let [el (append! (make-el))]
    (set! (.-minEnd el) 80)
    (is (= "80" (.getAttribute el model/attr-min-end)))
    (is (= 80 (.-minEnd el)))))

(deftest divider-label-property-test
  (let [el (append! (make-el))]
    (is (= "Resize panels" (.-dividerLabel el)))
    (set! (.-dividerLabel el) "Resize")
    (is (= "Resize" (.getAttribute el model/attr-divider-label)))
    (is (= "Resize" (.-dividerLabel el)))))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-position "30")
    (.remove el)
    (.appendChild body el)
    (is (= "30%" (.-flexBasis (.-style (shadow-part el "[part=start-pane]"))))
        "position should remain applied after a disconnect/reconnect cycle")))
