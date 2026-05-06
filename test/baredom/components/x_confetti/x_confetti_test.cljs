(ns baredom.components.x-confetti.x-confetti-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-confetti.x-confetti :as x]
            [baredom.components.x-confetti.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures
  :each
  {:before cleanup-dom!
   :after  cleanup-dom!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-part [el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn set-js-prop! [obj prop value]
  (js* "(~{}[~{}] = ~{})" obj prop value))

(defn get-js-prop [obj prop]
  (js* "(~{}[~{}])" obj prop))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [el     (append! (make-el))
        wrap   (shadow-part el "[part='canvas-wrap']")
        canvas (shadow-part el "[part='canvas']")]
    (is (some? wrap))
    (is (some? canvas))
    (is (= "CANVAS" (.-tagName canvas)))))

(deftest defaults-applied-on-connect-test
  (let [el (append! (make-el))]
    (is (= "overlay" (.getAttribute el "data-mode")))
    (is (= "true"    (.getAttribute el "aria-hidden")))
    (is (= "-1"      (.getAttribute el "tabindex")))))

(deftest mode-attribute-flips-data-mode-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mode "inline")
    (is (= "inline" (.getAttribute el "data-mode")))
    (.setAttribute el model/attr-mode "overlay")
    (is (= "overlay" (.getAttribute el "data-mode")))))

(deftest invalid-mode-falls-back-to-overlay-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mode "bogus")
    (is (= "overlay" (.getAttribute el "data-mode")))))

(deftest property-reflectors-test
  (let [el (append! (make-el))]
    (set-js-prop! el "count" 42)
    (is (= "42" (.getAttribute el model/attr-count)))
    (is (= 42  (get-js-prop el "count")))

    (set-js-prop! el "origin" "center")
    (is (= "center" (.getAttribute el model/attr-origin)))

    (set-js-prop! el "disabled" true)
    (is (.hasAttribute el model/attr-disabled))
    (set-js-prop! el "disabled" false)
    (is (not (.hasAttribute el model/attr-disabled)))

    (set-js-prop! el "autoFire" true)
    (is (.hasAttribute el model/attr-auto-fire))))

(deftest fire-method-exists-test
  (let [el (append! (make-el))]
    (is (fn? (get-js-prop el "fire")))))

(deftest fire-dispatches-fire-event-test
  (let [el      (append! (make-el))
        events  (atom [])
        handler (fn [^js e] (swap! events conj e))]
    (.addEventListener el model/event-fire handler)
    (.fire el #js {:count 10})
    (is (= 1 (count @events)))
    (let [^js e (first @events)
          d (.-detail e)]
      (is (= 10       (.-count d)))
      (is (= "top"    (.-origin d))))))

(deftest fire-respects-disabled-test
  (let [el      (append! (make-el))
        events  (atom [])
        handler (fn [^js e] (swap! events conj e))]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-fire handler)
    (.fire el nil)
    (is (= 0 (count @events)))))

(deftest fire-with-origin-override-test
  (let [el      (append! (make-el))
        events  (atom [])
        handler (fn [^js e] (swap! events conj (.-origin (.-detail e))))]
    (.addEventListener el model/event-fire handler)
    (.fire el #js {:origin "center"})
    (is (= ["center"] @events))))

(deftest fire-clamps-count-test
  (let [el      (append! (make-el))
        events  (atom [])
        handler (fn [^js e] (swap! events conj (.-count (.-detail e))))]
    (.addEventListener el model/event-fire handler)
    (.fire el #js {:count 10000})
    (is (= [model/count-max] @events))
    (.fire el #js {:count 0})
    (is (= [model/count-max model/count-min] @events))))

(deftest auto-fire-fires-once-on-connect-test
  (let [el     (make-el)
        events (atom 0)]
    (.setAttribute el model/attr-auto-fire "")
    (.addEventListener el model/event-fire (fn [_] (swap! events inc)))
    (append! el)
    (is (= 1 @events))))

(deftest auto-fire-with-disabled-does-not-fire-test
  (let [el     (make-el)
        events (atom 0)]
    (.setAttribute el model/attr-auto-fire "")
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-fire (fn [_] (swap! events inc)))
    (append! el)
    (is (= 0 @events))))

(deftest unknown-attribute-does-not-restart-burst-test
  ;; Changing arbitrary attributes after fire() should not re-emit fire events.
  (let [el     (append! (make-el))
        events (atom 0)]
    (.addEventListener el model/event-fire (fn [_] (swap! events inc)))
    (.fire el nil)
    (.setAttribute el model/attr-mode "inline")
    (.setAttribute el model/attr-spread "45")
    (is (= 1 @events))))
