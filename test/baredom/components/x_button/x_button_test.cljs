(ns baredom.components.x-button.x-button-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-button.model :as model]))

(x-button/init!)

(defn cleanup-fixture
  [f]
  (f)
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each cleanup-fixture)

(defn make-el
  []
  (.createElement js/document model/tag-name))

(defn append!
  [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-button
  [el]
  (.querySelector (.-shadowRoot el) "button"))

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
        seen (atom nil)]
    (.addEventListener
     el
     model/event-press
     (fn [event]
       (reset! seen (js->clj (.-detail event) :keywordize-keys true))))
    (.click (shadow-button el))
    (is (= {:source "programmatic"} @seen))))

(deftest disabled-blocks-press-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        seen (atom nil)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-press (fn [e] (reset! seen e)))
    (.click btn)
    (is (nil? @seen))))

(deftest loading-blocks-press-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        seen (atom nil)]
    (.setAttribute el model/attr-loading "")
    (.addEventListener el model/event-press (fn [e] (reset! seen e)))
    (.click btn)
    (is (nil? @seen))))

(deftest hover-events-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        hover-start-seen (atom false)
        hover-end-seen (atom false)]
    (.addEventListener el model/event-hover-start (fn [_] (reset! hover-start-seen true)))
    (.addEventListener el model/event-hover-end (fn [_] (reset! hover-end-seen true)))
    (.dispatchEvent btn (js/PointerEvent. "pointerenter" #js {:bubbles false}))
    (is @hover-start-seen)
    (.dispatchEvent btn (js/PointerEvent. "pointerleave" #js {:bubbles false}))
    (is @hover-end-seen)))

(deftest hover-suppressed-when-disabled-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        seen (atom false)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-hover-start (fn [_] (reset! seen true)))
    (.dispatchEvent btn (js/PointerEvent. "pointerenter" #js {:bubbles false}))
    (is (not @seen))))

(deftest press-lifecycle-test
  (let [el (append! (make-el))
        btn (shadow-button el)
        press-start-seen (atom nil)
        press-end-seen (atom nil)]
    (.addEventListener el model/event-press-start
                       (fn [e] (reset! press-start-seen
                                       (js->clj (.-detail e) :keywordize-keys true))))
    (.addEventListener el model/event-press-end
                       (fn [e] (reset! press-end-seen
                                       (js->clj (.-detail e) :keywordize-keys true))))
    (.dispatchEvent btn (js/PointerEvent. "pointerdown" #js {:bubbles true}))
    (is (= {:source "pointer"} @press-start-seen))
    (.dispatchEvent btn (js/PointerEvent. "pointerup" #js {:bubbles true}))
    (is (= {:source "pointer"} @press-end-seen))))

(deftest form-submit-test
  (let [form (.createElement js/document "form")
        el (make-el)
        submitted (atom false)]
    (.setAttribute el model/attr-type "submit")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.addEventListener form "submit" (fn [e] (.preventDefault e) (reset! submitted true)))
    (.click (shadow-button el))
    (is @submitted)
    (.remove form)))

(deftest form-reset-test
  (let [form (.createElement js/document "form")
        el (make-el)
        reset-fired (atom false)]
    (.setAttribute el model/attr-type "reset")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.addEventListener form "reset" (fn [_] (reset! reset-fired true)))
    (.click (shadow-button el))
    (is @reset-fired)
    (.remove form)))
