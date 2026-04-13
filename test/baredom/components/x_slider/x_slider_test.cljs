(ns baredom.components.x-slider.x-slider-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-slider.x-slider :as x]
            [baredom.components.x-slider.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-slider should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                      "shadow root should exist")
    (is (some? (shadow-part el "[part=base]"))          "base div should exist")
    (is (some? (shadow-part el "[part=header]"))        "header div should exist")
    (is (some? (shadow-part el "[part=label-text]"))    "label-text span should exist")
    (is (some? (shadow-part el "[part=value-text]"))    "value-text span should exist")
    (is (some? (shadow-part el "[part=input]"))         "input should exist")
    (is (= "range" (.getAttribute (shadow-part el "[part=input]") "type"))
        "input type should be range")))

;; ---------------------------------------------------------------------------
;; Default attribute state
;; ---------------------------------------------------------------------------

(deftest default-aria-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (is (= "0"   (.getAttribute input-el "aria-valuenow"))  "aria-valuenow defaults to 0")
    (is (= "0"   (.getAttribute input-el "aria-valuemin"))  "aria-valuemin defaults to 0")
    (is (= "100" (.getAttribute input-el "aria-valuemax"))  "aria-valuemax defaults to 100")))

(deftest default-fill-css-var-test
  (let [el (append! (make-el))]
    (is (= "0.00%"
           (.getPropertyValue (.-style el) "--_x-slider-fill"))
        "fill CSS var should be 0.00% at default value 0")))

(deftest default-size-test
  (let [el      (append! (make-el))
        base-el (shadow-part el "[part=base]")]
    (is (= "md" (.getAttribute base-el "data-size"))
        "default size should be md")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest value-attr-updates-fill-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "50")
    (is (= "50.00%"
           (.getPropertyValue (.-style el) "--_x-slider-fill"))
        "fill CSS var should be 50.00% when value=50")))

(deftest value-attr-updates-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-value "75")
    (is (= "75" (.-value input-el))
        "native input value should reflect attribute")))

(deftest value-attr-updates-aria-valuenow-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-value "30")
    (is (= "30" (.getAttribute input-el "aria-valuenow")))))

(deftest min-max-attrs-update-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-min "10")
    (.setAttribute el model/attr-max "200")
    (is (= "10"  (.getAttribute input-el "min")))
    (is (= "200" (.getAttribute input-el "max")))))

(deftest step-attr-updates-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-step "5")
    (is (= "5" (.getAttribute input-el "step")))))

(deftest step-any-attr-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-step "any")
    (is (= "any" (.getAttribute input-el "step")))))

(deftest disabled-attr-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute el "data-disabled")       "host should have data-disabled")
    (is (.hasAttribute input-el "disabled")      "native input should have disabled attr")))

(deftest readonly-attr-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (is (.hasAttribute el "data-readonly")              "host should have data-readonly")
    (is (= "true" (.getAttribute input-el "aria-readonly"))
        "aria-readonly should be true on input")))

(deftest size-attr-test
  (let [el      (append! (make-el))
        base-el (shadow-part el "[part=base]")]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute base-el "data-size")))))

;; ---------------------------------------------------------------------------
;; Label and show-value
;; ---------------------------------------------------------------------------

(deftest label-attr-shows-header-test
  (let [el        (append! (make-el))
        header-el (shadow-part el "[part=header]")
        label-el  (shadow-part el "[part=label-text]")]
    (.setAttribute el model/attr-label "Volume")
    (is (not= "none" (.-display (.-style header-el)))
        "header should be visible when label is set")
    (is (= "Volume" (.-textContent label-el))
        "label-text should show label value")))

(deftest show-value-attr-shows-header-test
  (let [el        (append! (make-el))
        header-el (shadow-part el "[part=header]")
        value-el  (shadow-part el "[part=value-text]")]
    (.setAttribute el model/attr-value "42")
    (.setAttribute el model/attr-show-value "")
    (is (not= "none" (.-display (.-style header-el)))
        "header should be visible when show-value is set")
    (is (= "42" (.-textContent value-el))
        "value-text should show current value")))

(deftest no-label-no-show-value-hides-header-test
  (let [el        (append! (make-el))
        header-el (shadow-part el "[part=header]")]
    (is (= "none" (.-display (.-style header-el)))
        "header should be hidden by default")))

;; ---------------------------------------------------------------------------
;; ARIA — label fallback
;; ---------------------------------------------------------------------------

(deftest label-fallback-aria-label-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-label "Brightness")
    (is (= "Brightness" (.getAttribute input-el "aria-label"))
        "label should fall back as aria-label when no explicit aria-label")))

(deftest explicit-aria-label-overrides-label-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-label "Volume")
    (.setAttribute el model/attr-aria-label "Audio volume")
    (is (= "Audio volume" (.getAttribute input-el "aria-label"))
        "explicit aria-label takes precedence over label fallback")))

(deftest aria-labelledby-forwarded-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-aria-labelledby "lbl-1")
    (is (= "lbl-1" (.getAttribute input-el "aria-labelledby")))))

(deftest aria-describedby-forwarded-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-aria-describedby "hint-1")
    (is (= "hint-1" (.getAttribute input-el "aria-describedby")))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest string-property-value-test
  (let [el (append! (make-el))]
    (set! (.-value el) "60")
    (is (= "60" (.getAttribute el model/attr-value)))
    (is (= "60" (.-value el)))))

(deftest string-property-min-test
  (let [el (append! (make-el))]
    (set! (.-min el) "5")
    (is (= "5" (.getAttribute el model/attr-min)))))

(deftest string-property-max-test
  (let [el (append! (make-el))]
    (set! (.-max el) "500")
    (is (= "500" (.getAttribute el model/attr-max)))))

(deftest string-property-step-test
  (let [el (append! (make-el))]
    (set! (.-step el) "0.1")
    (is (= "0.1" (.getAttribute el model/attr-step)))))

(deftest boolean-property-disabled-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest boolean-property-readonly-test
  (let [el (append! (make-el))]
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly))
    (set! (.-readOnly el) false)
    (is (not (.hasAttribute el model/attr-readonly)))))

(deftest boolean-property-show-value-test
  (let [el (append! (make-el))]
    (set! (.-showValue el) true)
    (is (.hasAttribute el model/attr-show-value))
    (set! (.-showValue el) false)
    (is (not (.hasAttribute el model/attr-show-value)))))

(deftest string-property-name-test
  (let [el (append! (make-el))]
    (set! (.-name el) "volume")
    (is (= "volume" (.getAttribute el model/attr-name)))))

(deftest string-property-label-test
  (let [el (append! (make-el))]
    (set! (.-label el) "Volume")
    (is (= "Volume" (.getAttribute el model/attr-label)))))

(deftest string-property-size-test
  (let [el (append! (make-el))]
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))))

;; ---------------------------------------------------------------------------
;; x-slider-input event (async)
;; ---------------------------------------------------------------------------

(deftest input-event-test
  (async done
    (let [el       (append! (make-el))
          seen     (atom nil)
          input-el (shadow-part el "[part=input]")]
      (.setAttribute el model/attr-min "0")
      (.setAttribute el model/attr-max "100")
      (.addEventListener
       el model/event-input
       (fn [^js ev]
         (reset! seen {:value (.-value (.-detail ev))
                       :min   (.-min (.-detail ev))
                       :max   (.-max (.-detail ev))})))
      ;; Simulate user dragging by setting native input value and firing input event
      (set! (.-value input-el) "40")
      (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen)      "x-slider-input event should have fired")
         (is (= 40 (:value @seen)))
         (is (= 0  (:min @seen)))
         (is (= 100 (:max @seen)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; x-slider-change event (async)
;; ---------------------------------------------------------------------------

(deftest change-event-test
  (async done
    (let [el       (append! (make-el))
          seen     (atom nil)
          input-el (shadow-part el "[part=input]")]
      (.setAttribute el model/attr-min "0")
      (.setAttribute el model/attr-max "100")
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen {:value (.-value (.-detail ev))
                       :min   (.-min (.-detail ev))
                       :max   (.-max (.-detail ev))})))
      (set! (.-value input-el) "75")
      (.dispatchEvent input-el (js/Event. "change" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen)       "x-slider-change event should have fired")
         (is (= 75 (:value @seen)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Value clamping
;; ---------------------------------------------------------------------------

(deftest value-clamped-to-max-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-max "100")
    (.setAttribute el model/attr-value "150")
    (is (= "100.00%"
           (.getPropertyValue (.-style el) "--_x-slider-fill"))
        "fill should be 100% when value exceeds max")))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-value "33")
    (.setAttribute el model/attr-label "Test")
    (.remove el)
    (.appendChild body el)
    (let [input-el (shadow-part el "[part=input]")
          label-el (shadow-part el "[part=label-text]")]
      (is (= "33" (.-value input-el))
          "input value should be preserved after reconnect")
      (is (= "Test" (.-textContent label-el))
          "label should be preserved after reconnect"))))

;; ---------------------------------------------------------------------------
;; Keyboard — readonly blocks value-changing keys but allows Tab
;; ---------------------------------------------------------------------------

(defn- dispatch-key! [^js target key]
  (let [evt (js/KeyboardEvent. "keydown"
              #js {:key key :bubbles true :cancelable true})]
    (.dispatchEvent target evt)
    evt))

(deftest readonly-blocks-arrow-keys-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (.setAttribute el model/attr-value "50")
    (let [evt (dispatch-key! input-el "ArrowRight")]
      (is (true? (.-defaultPrevented evt))
          "ArrowRight should be prevented in readonly mode"))))

(deftest readonly-blocks-home-end-keys-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (let [evt-home (dispatch-key! input-el "Home")
          evt-end  (dispatch-key! input-el "End")]
      (is (true? (.-defaultPrevented evt-home))
          "Home should be prevented in readonly mode")
      (is (true? (.-defaultPrevented evt-end))
          "End should be prevented in readonly mode"))))

(deftest readonly-allows-tab-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (let [evt (dispatch-key! input-el "Tab")]
      (is (false? (.-defaultPrevented evt))
          "Tab should NOT be prevented in readonly mode"))))

(deftest non-readonly-allows-arrow-keys-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (let [evt (dispatch-key! input-el "ArrowRight")]
      (is (false? (.-defaultPrevented evt))
          "ArrowRight should not be prevented when not readonly"))))

;; ---------------------------------------------------------------------------
;; ARIA sync after attribute changes
;; ---------------------------------------------------------------------------

(deftest aria-valuemin-updates-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-min "10")
    (is (= "10" (.getAttribute input-el "aria-valuemin"))
        "aria-valuemin should sync with min attribute")))

(deftest aria-valuemax-updates-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-max "200")
    (is (= "200" (.getAttribute input-el "aria-valuemax"))
        "aria-valuemax should sync with max attribute")))

(deftest aria-readonly-false-by-default-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (is (= "false" (.getAttribute input-el "aria-readonly"))
        "aria-readonly should be false by default")))

;; ---------------------------------------------------------------------------
;; Form reset
;; ---------------------------------------------------------------------------

(deftest form-reset-restores-default-value-test
  (let [^js form (.createElement js/document "form")
        el       (make-el)]
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.setAttribute el model/attr-value "75")
    (let [input-el (shadow-part el "[part=input]")]
      (is (= "75" (.-value input-el))
          "value should be 75 before reset")
      (.reset form)
      (is (= "0" (.-value input-el))
          "value should reset to default (0) after form reset"))
    (.remove form)))

;; ---------------------------------------------------------------------------
;; Disabled prevents events
;; ---------------------------------------------------------------------------

(deftest disabled-prevents-input-event-test
  (async done
    (let [el       (append! (make-el))
          fired    (atom false)
          input-el (shadow-part el "[part=input]")]
      (.setAttribute el model/attr-disabled "")
      (.addEventListener el model/event-input
        (fn [_] (reset! fired true)))
      (set! (.-value input-el) "50")
      (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (false? @fired)
             "x-slider-input should not fire when disabled")
         (done))
       0))))
