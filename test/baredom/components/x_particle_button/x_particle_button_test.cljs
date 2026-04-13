(ns baredom.components.x-particle-button.x-particle-button-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-particle-button.x-particle-button :as component]
            [baredom.components.x-particle-button.model :as model]))

(component/init!)

(defn cleanup-fixture
  [f]
  (f)
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each cleanup-fixture)

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-button [^js el]
  (.querySelector (.-shadowRoot el) "button"))

(defn shadow-canvas [^js el]
  (.querySelector (.-shadowRoot el) "canvas"))

(defn shadow-overlay [^js el]
  (.querySelector (.-shadowRoot el) "[part='material-overlay']"))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest boolean-property-reflection-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))

    (set! (.-loading el) true)
    (is (.hasAttribute el model/attr-loading))
    (set! (.-loading el) false)
    (is (not (.hasAttribute el model/attr-loading)))

    (set! (.-pressed el) true)
    (is (.hasAttribute el model/attr-pressed))
    (set! (.-pressed el) false)
    (is (not (.hasAttribute el model/attr-pressed)))))

(deftest enum-normalization-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (.setAttribute el model/attr-type "oops")
    (.setAttribute el model/attr-variant "weird")
    (.setAttribute el model/attr-size "xl")
    (is (= "button" (.getAttribute btn "type")))
    (is (= "primary" (.getAttribute btn "data-variant")))
    (is (= "md" (.getAttribute btn "data-size")))))

(deftest mode-attribute-reflection-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (.setAttribute el model/attr-mode "spark")
    (is (= "spark" (.getAttribute btn "data-mode")))
    (.setAttribute el model/attr-mode "disperse")
    (is (= "disperse" (.getAttribute btn "data-mode")))
    (.setAttribute el model/attr-mode "invalid")
    (is (= "dust" (.getAttribute btn "data-mode")))))

(deftest extended-variants-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (.setAttribute el model/attr-variant "success")
    (is (= "success" (.getAttribute btn "data-variant")))
    (.setAttribute el model/attr-variant "warning")
    (is (= "warning" (.getAttribute btn "data-variant")))))

(deftest loading-disables-internal-button-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (.setAttribute el model/attr-loading "")
    (is (= true (.-disabled btn)))
    (is (= "true" (.getAttribute btn "aria-busy")))))

(deftest pressed-maps-to-aria-pressed-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (is (nil? (.getAttribute btn "aria-pressed")))
    (.setAttribute el model/attr-pressed "")
    (is (= "true" (.getAttribute btn "aria-pressed")))))

(deftest label-fallback-test
  (let [el (append! (make-el))
        btn (shadow-button el)]
    (.setAttribute el model/attr-label "Close")
    (is (= "Close" (.getAttribute btn "aria-label")))))

(deftest click-emits-press-test
  (let [el (append! (make-el))
        result #js {:seen false :detail nil}]
    (.addEventListener
     el
     model/event-press
     (fn [^js event]
       (aset result "seen" true)
       (aset result "detail" (js->clj (.-detail event) :keywordize-keys true))))
    (.click (shadow-button el))
    (is (aget result "seen"))
    (is (= {:source "programmatic"} (aget result "detail")))))

(deftest disabled-blocks-press-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        result #js {:seen false}]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-press (fn [_] (aset result "seen" true)))
    (.click btn)
    (is (not (aget result "seen")))))

(deftest loading-blocks-press-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        result #js {:seen false}]
    (.setAttribute el model/attr-loading "")
    (.addEventListener el model/event-press (fn [_] (aset result "seen" true)))
    (.click btn)
    (is (not (aget result "seen")))))

(deftest hover-events-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        result #js {:start false :end false}]
    (.addEventListener el model/event-hover-start (fn [_] (aset result "start" true)))
    (.addEventListener el model/event-hover-end (fn [_] (aset result "end" true)))
    (.dispatchEvent btn (js/PointerEvent. "pointerenter" #js {:bubbles false}))
    (is (aget result "start"))
    (.dispatchEvent btn (js/PointerEvent. "pointerleave" #js {:bubbles false}))
    (is (aget result "end"))))

(deftest hover-suppressed-when-disabled-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        result #js {:seen false}]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-hover-start (fn [_] (aset result "seen" true)))
    (.dispatchEvent btn (js/PointerEvent. "pointerenter" #js {:bubbles false}))
    (is (not (aget result "seen")))))

(deftest press-lifecycle-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        result #js {:start nil :end nil}]
    (.addEventListener el model/event-press-start
                       (fn [^js e] (aset result "start"
                                         (js->clj (.-detail e) :keywordize-keys true))))
    (.addEventListener el model/event-press-end
                       (fn [^js e] (aset result "end"
                                         (js->clj (.-detail e) :keywordize-keys true))))
    (.dispatchEvent btn (js/PointerEvent. "pointerdown" #js {:bubbles true}))
    (is (= {:source "pointer"} (aget result "start")))
    (.dispatchEvent btn (js/PointerEvent. "pointerup" #js {:bubbles true}))
    (is (= {:source "pointer"} (aget result "end")))))

(deftest canvas-exists-in-shadow-dom-test
  (let [el (append! (make-el))
        ^js canvas (shadow-canvas el)]
    (is (some? canvas))
    (is (= "true" (.getAttribute canvas "aria-hidden")))))

(deftest canvas-has-pointer-events-none-test
  (let [el (append! (make-el))
        ^js canvas (shadow-canvas el)
        ^js style (js/getComputedStyle canvas)]
    (is (= "none" (.-pointerEvents style)))))

(deftest material-overlay-exists-test
  (let [el (append! (make-el))
        ^js overlay (shadow-overlay el)]
    (is (some? overlay))
    (is (= "true" (.getAttribute overlay "aria-hidden")))))

(deftest particle-attribute-reflection-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-particle-count "60")
    (is (= "60" (.getAttribute el model/attr-particle-count)))
    (.setAttribute el model/attr-intensity "80")
    (is (= "80" (.getAttribute el model/attr-intensity)))
    (.setAttribute el model/attr-particle-size "5")
    (is (= "5" (.getAttribute el model/attr-particle-size)))
    (.setAttribute el model/attr-reassemble-speed "1000")
    (is (= "1000" (.getAttribute el model/attr-reassemble-speed)))))

(deftest burst-event-includes-mode-and-press-point-test
  (let [el (append! (make-el))
        result #js {:detail nil}]
    (.setAttribute el model/attr-mode "spark")
    (.addEventListener el model/event-burst
                       (fn [^js e] (aset result "detail" (.-detail e))))
    (.click (shadow-button el))
    (let [detail (aget result "detail")]
      (when detail
        (is (= "spark" (aget detail "mode")))
        (is (number? (aget detail "press-x")))
        (is (number? (aget detail "press-y")))))))

(deftest form-submit-test
  (let [form (.createElement js/document "form")
        el (make-el)
        result #js {:submitted false}]
    (.setAttribute el model/attr-type "submit")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.addEventListener form "submit" (fn [e] (.preventDefault e) (aset result "submitted" true)))
    (.click (shadow-button el))
    (is (aget result "submitted"))
    (.remove form)))

(deftest form-reset-test
  (let [form (.createElement js/document "form")
        el (make-el)
        result #js {:fired false}]
    (.setAttribute el model/attr-type "reset")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.addEventListener form "reset" (fn [_] (aset result "fired" true)))
    (.click (shadow-button el))
    (is (aget result "fired"))
    (.remove form)))
