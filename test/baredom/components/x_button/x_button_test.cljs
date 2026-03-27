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
