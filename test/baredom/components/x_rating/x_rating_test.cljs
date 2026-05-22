(ns baredom.components.x-rating.x-rating-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-rating.x-rating :as x]
            [baredom.components.x-rating.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [^js form (.querySelectorAll js/document "form.x-rating-test-form")]
    (.remove form)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))
(defn ^js stars-el [^js el] (shadow-part el "[part=stars]"))

(defn- star-spans [^js el]
  (.querySelectorAll (.-shadowRoot el) "[part=star]"))

(defn- key-evt [key]
  (js/KeyboardEvent. "keydown"
                     #js {:key key :bubbles true :cancelable true}))

(defn- pointer-evt [type cx cy]
  (js/PointerEvent. type
                    #js {:pointerId 1 :clientX cx :clientY cy
                         :bubbles true :cancelable true}))

(defn- star-x
  "clientX at fraction `frac` within star index `idx` of the stars row."
  [^js el idx frac]
  (let [^js rect (.getBoundingClientRect (stars-el el))
        pitch    (/ (.-width rect) (.-length (star-spans el)))]
    (+ (.-left rect) (* (+ idx frac) pitch))))

(defn- stars-cy [^js el]
  (let [^js rect (.getBoundingClientRect (stars-el el))]
    (+ (.-top rect) (/ (.-height rect) 2))))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-rating should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                    "shadow root should exist")
    (is (some? (shadow-part el "[part=base]"))       "base div should exist")
    (is (some? (shadow-part el "[part=label-text]")) "label-text span should exist")
    (is (some? (shadow-part el "[part=stars]"))      "stars div should exist")
    (is (= model/default-max (.-length (star-spans el)))
        "should render `default-max` star cells")))

(deftest slider-role-test
  (let [el    (append! (make-el))
        stars (stars-el el)]
    (is (= "slider" (.getAttribute stars "role")))
    (is (= "0" (.getAttribute stars "tabindex")))))

(deftest default-aria-test
  (let [el    (append! (make-el))
        stars (stars-el el)]
    (is (= "0" (.getAttribute stars "aria-valuemin")))
    (is (= "5" (.getAttribute stars "aria-valuemax")))
    (is (= "0" (.getAttribute stars "aria-valuenow")))
    (is (= "No rating" (.getAttribute stars "aria-valuetext")))
    (is (= "Rating" (.getAttribute stars "aria-label"))
        "an unlabelled rating still has an accessible name")))

(deftest default-size-test
  (let [el (append! (make-el))]
    (is (= "md" (.getAttribute (shadow-part el "[part=base]") "data-size")))))

;; ---------------------------------------------------------------------------
;; Attribute → DOM
;; ---------------------------------------------------------------------------

(deftest max-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-max "8")
    (is (= 8 (.-length (star-spans el)))
        "changing max rebuilds the star row")))

(deftest extreme-max-is-capped-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-max "100000")
    (is (= model/max-stars-cap (.-length (star-spans el)))
        "an extreme max is capped so the DOM stays bounded")))

(deftest shape-attr-test
  (let [el (append! (make-el))]
    (testing "default shape is a star"
      (let [^js path (.querySelector (aget (star-spans el) 0) "path")]
        (is (.startsWith (.getAttribute path "d") "M12 17.27"))))
    (.setAttribute el model/attr-shape "heart")
    (testing "shape=heart rebuilds with the heart path"
      (let [^js path (.querySelector (aget (star-spans el) 0) "path")]
        (is (.startsWith (.getAttribute path "d") "M12 21.35"))))))

(deftest value-fill-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "3")
    (let [spans (star-spans el)]
      (dotimes [i 5]
        (let [^js star (aget spans i)]
          (if (< i 3)
            (do (is (= "full" (.getAttribute star "data-fill")))
                (is (= "100%" (.getPropertyValue (.-style star)
                                                 "--_x-rating-star-fill"))))
            (do (is (= "empty" (.getAttribute star "data-fill")))
                (is (= "0%" (.getPropertyValue (.-style star)
                                               "--_x-rating-star-fill"))))))))))

(deftest half-fill-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-precision "half")
    (.setAttribute el model/attr-value "2.5")
    (let [^js star (aget (star-spans el) 2)]
      (is (= "half" (.getAttribute star "data-fill")))
      (is (= "50%" (.getPropertyValue (.-style star) "--_x-rating-star-fill"))))))

(deftest size-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute (shadow-part el "[part=base]") "data-size")))))

(deftest aria-valuetext-attr-test
  (let [el    (append! (make-el))
        stars (stars-el el)]
    (.setAttribute el model/attr-value "3")
    (is (= "3" (.getAttribute stars "aria-valuenow")))
    (is (= "3 out of 5 stars" (.getAttribute stars "aria-valuetext")))
    (.setAttribute el model/attr-shape "heart")
    (is (= "3 out of 5 hearts" (.getAttribute stars "aria-valuetext"))
        "aria-valuetext noun follows the shape")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest string-properties-test
  (let [el (append! (make-el))]
    (set! (.-value el) "3")      (is (= "3" (.getAttribute el model/attr-value)))
    (set! (.-max el) "10")       (is (= "10" (.getAttribute el model/attr-max)))
    (set! (.-precision el) "half") (is (= "half" (.getAttribute el model/attr-precision)))
    (set! (.-shape el) "heart")  (is (= "heart" (.getAttribute el model/attr-shape)))
    (set! (.-name el) "rate")    (is (= "rate" (.getAttribute el model/attr-name)))
    (set! (.-label el) "Stars")  (is (= "Stars" (.getAttribute el model/attr-label)))
    (set! (.-size el) "lg")      (is (= "lg" (.getAttribute el model/attr-size)))))

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
    (set! (.-allowClear el) true)
    (is (.hasAttribute el model/attr-allow-clear))
    (set! (.-allowClear el) false)
    (is (not (.hasAttribute el model/attr-allow-clear)))))

;; ---------------------------------------------------------------------------
;; Label
;; ---------------------------------------------------------------------------

(deftest label-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label-text]")]
    (is (= "none" (.-display (.-style label-el)))
        "label is hidden when no label attribute is set")
    (.setAttribute el model/attr-label "Rate this")
    (is (not= "none" (.-display (.-style label-el))))
    (is (= "Rate this" (.-textContent label-el)))))

;; ---------------------------------------------------------------------------
;; Pointer interaction
;; ---------------------------------------------------------------------------

(deftest pointer-click-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js ev] (reset! seen (.-value (.-detail ev)))))
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 2 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= "3" (.getAttribute el model/attr-value))
             "clicking the third star sets value to 3")
         (is (= 3 @seen) "x-rating-change should fire with the new value")
         (done))
       0))))

(deftest pointer-half-click-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-precision "half")
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 2 0.25) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= "2.5" (.getAttribute el model/attr-value))
             "clicking the left half of the third star sets value to 2.5")
         (done))
       0))))

(deftest hover-preview-test
  (async done
    (let [el   (append! (make-el))
          seen (atom [])]
      (.addEventListener el model/event-hover
                         (fn [^js ev] (swap! seen conj (.-value (.-detail ev)))))
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointermove" (star-x el 3 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= 4 (last @seen)) "hover over the fourth star reports value 4")
         (is (not (.hasAttribute el model/attr-value))
             "hover preview must not write the value attribute")
         (is (.hasAttribute (stars-el el) "data-preview")
             "the stars row is marked as previewing")
         (.dispatchEvent (stars-el el) (pointer-evt "pointerleave" 0 0))
         (js/setTimeout
          (fn []
            (is (nil? (last @seen)) "leaving the row reports a null value")
            (is (not (.hasAttribute (stars-el el) "data-preview"))
                "the preview marker is cleared on leave")
            (done))
          0))
       0))))

(deftest pointer-click-clears-preview-test
  (async done
    (let [el (append! (make-el))]
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointermove" (star-x el 3 0.5) (stars-cy el)))
      (is (.hasAttribute (stars-el el) "data-preview")
          "moving the pointer over the row starts a preview")
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 3 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute (stars-el el) "data-preview"))
             "committing a click discards the hover-preview state")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Keyboard interaction
;; ---------------------------------------------------------------------------

(deftest keyboard-arrow-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2")
    (.dispatchEvent (stars-el el) (key-evt "ArrowRight"))
    (is (= "3" (.getAttribute el model/attr-value))
        "ArrowRight increments by one star")
    (.dispatchEvent (stars-el el) (key-evt "ArrowLeft"))
    (is (= "2" (.getAttribute el model/attr-value))
        "ArrowLeft decrements by one star")))

(deftest keyboard-half-arrow-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-precision "half")
    (.setAttribute el model/attr-value "2")
    (.dispatchEvent (stars-el el) (key-evt "ArrowRight"))
    (is (= "2.5" (.getAttribute el model/attr-value))
        "ArrowRight steps by 0.5 in half precision")))

(deftest keyboard-home-end-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "3")
    (.dispatchEvent (stars-el el) (key-evt "Home"))
    (is (= "0" (.getAttribute el model/attr-value)) "Home clears to 0")
    (.dispatchEvent (stars-el el) (key-evt "End"))
    (is (= "5" (.getAttribute el model/attr-value)) "End jumps to max")))

(deftest keyboard-clamp-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "5")
    (.dispatchEvent (stars-el el) (key-evt "ArrowRight"))
    (is (= "5" (.getAttribute el model/attr-value))
        "ArrowRight at max does not exceed the star count")))

;; ---------------------------------------------------------------------------
;; Clearing
;; ---------------------------------------------------------------------------

(deftest allow-clear-pointer-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-allow-clear "")
      (.setAttribute el model/attr-value "3")
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 2 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= "0" (.getAttribute el model/attr-value))
             "clicking the already-selected star clears the rating")
         (done))
       0))))

(deftest allow-clear-keyboard-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-allow-clear "")
    (.setAttribute el model/attr-value "3")
    (.dispatchEvent (stars-el el) (key-evt "Delete"))
    (is (= "0" (.getAttribute el model/attr-value))
        "Delete clears the rating when allow-clear is set")))

(deftest no-clear-without-allow-clear-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-value "3")
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 2 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= "3" (.getAttribute el model/attr-value))
             "without allow-clear, re-clicking the selected star is a no-op")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Readonly / disabled
;; ---------------------------------------------------------------------------

(deftest readonly-blocks-changes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "3")
    (.setAttribute el model/attr-readonly "")
    (let [evt (key-evt "ArrowRight")]
      (.dispatchEvent (stars-el el) evt)
      (is (true? (.-defaultPrevented evt)) "ArrowRight is consumed in readonly mode")
      (is (= "3" (.getAttribute el model/attr-value)) "value does not change"))))

(deftest readonly-focusable-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-readonly "")
    (is (= "0" (.getAttribute (stars-el el) "tabindex"))
        "a readonly rating stays focusable")))

(deftest disabled-test
  (let [el    (append! (make-el))
        stars (stars-el el)]
    (.setAttribute el model/attr-value "3")
    (.setAttribute el model/attr-disabled "")
    (is (= "-1" (.getAttribute stars "tabindex")))
    (is (= "true" (.getAttribute stars "aria-disabled")))
    (.dispatchEvent stars (key-evt "ArrowRight"))
    (is (= "3" (.getAttribute el model/attr-value))
        "a disabled rating ignores keyboard input")))

;; ---------------------------------------------------------------------------
;; change-request
;; ---------------------------------------------------------------------------

(deftest change-request-detail-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.setAttribute el model/attr-value "2")
      (.addEventListener
       el model/event-change-request
       (fn [^js ev]
         (reset! seen {:value         (.-value (.-detail ev))
                       :previousValue (.-previousValue (.-detail ev))})))
      (.dispatchEvent (stars-el el) (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (= {:value 3 :previousValue 2} @seen)
             "change-request carries the proposed and previous values")
         (done))
       0))))

(deftest change-request-cancel-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-value "2")
      (.addEventListener el model/event-change-request
                         (fn [^js ev] (.preventDefault ev)))
      (.dispatchEvent (stars-el el) (key-evt "ArrowRight"))
      (js/setTimeout
       (fn []
         (is (= "2" (.getAttribute el model/attr-value))
             "value does not change when change-request is cancelled")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Form association
;; ---------------------------------------------------------------------------

(deftest form-submit-test
  (let [^js form (.createElement js/document "form")
        el       (make-el)]
    (.add (.-classList form) "x-rating-test-form")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.setAttribute el model/attr-name "rating")
    (.setAttribute el model/attr-value "4")
    (let [data (js/FormData. form)]
      (is (= "4" (.get data "rating"))
          "the rating is submitted under its name attribute"))))

(deftest form-reset-test
  (let [^js form (.createElement js/document "form")
        el       (make-el)]
    (.add (.-classList form) "x-rating-test-form")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.setAttribute el model/attr-value "4")
    (.reset form)
    (is (not (.hasAttribute el model/attr-value)) "value attr removed on reset")
    (is (= "empty" (.getAttribute (aget (star-spans el) 0) "data-fill"))
        "stars are cleared on reset")))

;; ---------------------------------------------------------------------------
;; Reconnect stability + listener survival
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-value "3")
    (.setAttribute el model/attr-label "Rate")
    (.remove el)
    (.appendChild body el)
    (is (= "full" (.getAttribute (aget (star-spans el) 2) "data-fill"))
        "fill state should survive a reconnect")
    (is (= "Rate" (.-textContent (shadow-part el "[part=label-text]")))
        "label should survive a reconnect")))

(deftest max-rebuild-keeps-listeners-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-max "7")
      (.dispatchEvent (stars-el el)
                      (pointer-evt "pointerdown" (star-x el 3 0.5) (stars-cy el)))
      (js/setTimeout
       (fn []
         (is (= "4" (.getAttribute el model/attr-value))
             "a pointer click still commits after the star row is rebuilt")
         (done))
       0))))
