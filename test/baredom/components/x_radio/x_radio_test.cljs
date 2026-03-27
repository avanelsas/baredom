(ns baredom.components.x-radio.x-radio-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-radio.x-radio :as x]
            [baredom.components.x-radio.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))

(defn ^js make-radio
  ([] (append! (make-el)))
  ([name-val] (let [el (make-el)]
                (.setAttribute el model/attr-name name-val)
                (append! el))))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-radio should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (make-radio)]
    (is (some? (.-shadowRoot el))                "shadow root should exist")
    (is (some? (shadow-part el "[part=control]")) "control button should exist")
    (is (some? (shadow-part el "[part=dot]"))     "dot span should exist")))

(deftest control-has-radio-role-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (is (= "radio" (.getAttribute control "role"))
        "control should have role=\"radio\"")))

;; ---------------------------------------------------------------------------
;; Default attribute state
;; ---------------------------------------------------------------------------

(deftest default-aria-checked-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (is (= "false" (.getAttribute control "aria-checked"))
        "aria-checked should default to \"false\"")))

(deftest default-tabindex-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (is (= 0 (.-tabIndex control))
        "solo radio tabIndex should default to 0")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest checked-attr-updates-aria-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-checked "")
    (is (= "true" (.getAttribute control "aria-checked")))
    (is (.hasAttribute el "data-checked"))))

(deftest disabled-attr-updates-control-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute control "aria-disabled")))
    (is (.hasAttribute control "disabled"))
    (is (.hasAttribute el "data-disabled"))))

(deftest readonly-attr-updates-aria-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-readonly "")
    (is (= "true" (.getAttribute control "aria-readonly")))))

(deftest required-attr-updates-aria-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute control "aria-required")))))

(deftest aria-label-forwarded-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-label "Option A")
    (is (= "Option A" (.getAttribute control "aria-label")))))

(deftest aria-describedby-forwarded-test
  (let [el      (make-radio)
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-describedby "hint-1")
    (is (= "hint-1" (.getAttribute control "aria-describedby")))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest boolean-property-checked-test
  (let [el (make-radio)]
    (set! (.-checked el) true)
    (is (.hasAttribute el model/attr-checked))
    (is (= true (.-checked el)))
    (set! (.-checked el) false)
    (is (not (.hasAttribute el model/attr-checked)))))

(deftest boolean-property-disabled-test
  (let [el (make-radio)]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest boolean-property-readonly-test
  (let [el (make-radio)]
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly))
    (set! (.-readOnly el) false)
    (is (not (.hasAttribute el model/attr-readonly)))))

(deftest string-property-name-test
  (let [el (make-radio)]
    (set! (.-name el) "color")
    (is (= "color" (.getAttribute el model/attr-name)))))

(deftest string-property-value-test
  (let [el (make-radio)]
    (set! (.-value el) "red")
    (is (= "red" (.getAttribute el model/attr-value)))))

;; ---------------------------------------------------------------------------
;; Selection events (async)
;; ---------------------------------------------------------------------------

(deftest select-dispatches-change-request-test
  (async done
    (let [el   (make-radio)
          seen (atom nil)]
      (.addEventListener
       el model/event-change-request
       (fn [^js ev]
         (reset! seen {:next-checked (.-nextChecked (.-detail ev))
                       :prev-checked (.-previousChecked (.-detail ev))
                       :value        (.-value (.-detail ev))})))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "change-request event should fire")
         (is (= false (:prev-checked @seen)))
         (is (= true  (:next-checked @seen)))
         (is (= "on"  (:value @seen)))
         (done))
       0))))

(deftest select-dispatches-change-after-request-test
  (async done
    (let [el   (make-radio)
          seen (atom nil)]
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen {:checked (.-checked (.-detail ev))
                       :value   (.-value (.-detail ev))})))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "change event should fire")
         (is (= true  (:checked @seen)))
         (is (= "on"  (:value @seen)))
         (done))
       0))))

(deftest select-can-be-cancelled-test
  (async done
    (let [el (make-radio)]
      (.addEventListener
       el model/event-change-request
       (fn [^js ev] (.preventDefault ev)))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "checked attr should NOT be set when change-request is cancelled")
         (done))
       0))))

(deftest disabled-blocks-selection-test
  (async done
    (let [el (make-radio)]
      (.setAttribute el model/attr-disabled "")
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "disabled radio should not select")
         (done))
       0))))

(deftest already-checked-does-not-refire-test
  (async done
    (let [el    (make-radio)
          count (atom 0)]
      (.setAttribute el model/attr-checked "")
      (.addEventListener el model/event-change (fn [_] (swap! count inc)))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (= 0 @count) "clicking an already-checked radio should not fire change")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Group mutual exclusion
;; ---------------------------------------------------------------------------

(deftest group-mutual-exclusion-test
  (async done
    (let [r1 (make-radio "fruit")
          r2 (make-radio "fruit")
          r3 (make-radio "fruit")]
      (.setAttribute r1 model/attr-checked "")
      ;; Select r2 — r1 should become unchecked
      (.click (shadow-part r2 "[part=control]"))
      (js/setTimeout
       (fn []
         (is (.hasAttribute r2 model/attr-checked)  "r2 should be checked")
         (is (not (.hasAttribute r1 model/attr-checked)) "r1 should be unchecked")
         (is (not (.hasAttribute r3 model/attr-checked)) "r3 should remain unchecked")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Roving tabindex
;; ---------------------------------------------------------------------------

(deftest roving-tabindex-checked-gets-zero-test
  (async done
    (let [r1 (make-radio "shape")
          r2 (make-radio "shape")
          r3 (make-radio "shape")]
      (.click (shadow-part r2 "[part=control]"))
      (js/setTimeout
       (fn []
         (is (= 0  (.-tabIndex (shadow-part r2 "[part=control]")))  "checked radio → tabIndex 0")
         (is (= -1 (.-tabIndex (shadow-part r1 "[part=control]")))  "unchecked radio → tabIndex -1")
         (is (= -1 (.-tabIndex (shadow-part r3 "[part=control]")))  "unchecked radio → tabIndex -1")
         (done))
       0))))

(deftest roving-tabindex-none-checked-first-gets-zero-test
  (let [r1 (make-radio "color")
        r2 (make-radio "color")]
    (is (= 0  (.-tabIndex (shadow-part r1 "[part=control]"))) "first radio gets tabIndex 0 when none checked")
    (is (= -1 (.-tabIndex (shadow-part r2 "[part=control]"))) "second radio gets tabIndex -1 when none checked")))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-checked "")
    (.remove el)
    (.appendChild body el)
    (let [control (shadow-part el "[part=control]")]
      (is (= "true" (.getAttribute control "aria-checked"))
          "state should be preserved after reconnect"))))
