(ns baredom.components.x-checkbox.x-checkbox-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-checkbox.x-checkbox :as x]
            [baredom.components.x-checkbox.model :as model]))

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
      "x-checkbox should be registered"))

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
    (is (some? (shadow-part el "[part=control]"))     "control button should exist")
    (is (some? (shadow-part el "[part=box]"))         "box span should exist")
    (is (some? (shadow-part el "[part=check]"))       "check span should exist")
    (is (= "checkbox" (.getAttribute (shadow-part el "[part=control]") "role"))
        "control must have role=\"checkbox\"")))

;; ---------------------------------------------------------------------------
;; Default attribute state
;; ---------------------------------------------------------------------------

(deftest default-aria-checked-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (is (= "false" (.getAttribute control "aria-checked"))
        "aria-checked should default to \"false\"")))

(deftest default-tabindex-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (is (= 0 (.-tabIndex control))
        "tabIndex should default to 0")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest checked-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-checked "")
    (is (= "true" (.getAttribute control "aria-checked")))
    (is (.hasAttribute el "data-checked"))))

(deftest checked-false-string-not-checked-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-checked "false")
    (is (= "false" (.getAttribute control "aria-checked"))
        "checked=\"false\" must not be treated as checked")
    (is (not (.hasAttribute el "data-checked")))))

(deftest indeterminate-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-indeterminate "")
    (is (= "mixed" (.getAttribute control "aria-checked")))
    (is (.hasAttribute el "data-indeterminate"))))

(deftest disabled-attr-updates-control-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute control "aria-disabled")))
    (is (.hasAttribute control "disabled"))
    (is (= -1 (.-tabIndex control)))
    (is (.hasAttribute el "data-disabled"))))

(deftest readonly-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-readonly "")
    (is (= "true" (.getAttribute control "aria-readonly")))))

(deftest required-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute control "aria-required")))))

(deftest aria-label-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-label "Accept terms")
    (is (= "Accept terms" (.getAttribute control "aria-label")))))

(deftest aria-describedby-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-describedby "hint-1")
    (is (= "hint-1" (.getAttribute control "aria-describedby")))))

(deftest aria-labelledby-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-labelledby "lbl-1")
    (is (= "lbl-1" (.getAttribute control "aria-labelledby")))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest boolean-property-checked-test
  (let [el (append! (make-el))]
    (set! (.-checked el) true)
    (is (.hasAttribute el model/attr-checked))
    (is (= true (.-checked el)))
    (set! (.-checked el) false)
    (is (not (.hasAttribute el model/attr-checked)))))

(deftest boolean-property-disabled-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest boolean-property-indeterminate-test
  (let [el (append! (make-el))]
    (set! (.-indeterminate el) true)
    (is (.hasAttribute el model/attr-indeterminate))
    (set! (.-indeterminate el) false)
    (is (not (.hasAttribute el model/attr-indeterminate)))))

(deftest boolean-property-readonly-test
  (let [el (append! (make-el))]
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly))
    (set! (.-readOnly el) false)
    (is (not (.hasAttribute el model/attr-readonly)))))

(deftest string-property-name-test
  (let [el (append! (make-el))]
    (set! (.-name el) "subscribe")
    (is (= "subscribe" (.getAttribute el model/attr-name)))))

(deftest string-property-value-test
  (let [el (append! (make-el))]
    (set! (.-value el) "yes")
    (is (= "yes" (.getAttribute el model/attr-value)))))

;; ---------------------------------------------------------------------------
;; Toggle events (async)
;; ---------------------------------------------------------------------------

(deftest toggle-dispatches-change-request-test
  (async done
    (let [el     (append! (make-el))
          seen   (atom nil)]
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

(deftest toggle-dispatches-change-after-request-test
  (async done
    (let [el    (append! (make-el))
          seen  (atom nil)]
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

(deftest toggle-can-be-cancelled-test
  (async done
    (let [el (append! (make-el))]
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

(deftest disabled-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "disabled checkbox should not toggle")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el    (make-el)
        body  (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-checked "")
    (.remove el)
    (.appendChild body el)
    (let [control (shadow-part el "[part=control]")]
      (is (= "true" (.getAttribute control "aria-checked"))
          "state should be preserved after reconnect"))))
