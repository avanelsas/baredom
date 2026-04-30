(ns baredom.components.x-currency-field.x-currency-field-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-currency-field.x-currency-field :as x]
            [baredom.components.x-currency-field.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el sel] (.querySelector (.-shadowRoot el) sel))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-currency-field should be registered"))

(deftest form-associated-test
  (let [cls (.get js/customElements model/tag-name)]
    (is (.-formAssociated cls)
        "formAssociated should be true on the element class")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest renders-input-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                        "shadow root should exist")
    (is (some? (shadow-part el "[part=field]"))           "field div should exist")
    (is (some? (shadow-part el "[part=label]"))           "label should exist")
    (is (some? (shadow-part el "[part=input-wrapper]"))   "input-wrapper should exist")
    (is (some? (shadow-part el "[part=symbol]"))          "symbol span should exist")
    (is (some? (shadow-part el "[part=input]"))           "input should exist")
    (is (some? (shadow-part el "[part=hint]"))            "hint span should exist")
    (is (some? (shadow-part el "[part=error]"))           "error span should exist")))

;; ---------------------------------------------------------------------------
;; label attribute
;; ---------------------------------------------------------------------------

(deftest label-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label]")]
    (.setAttribute el model/attr-label "Price")
    (is (= "Price" (.-textContent label-el))
        "label attribute should set label text content")
    (is (not (.contains (.-classList label-el) "label-hidden"))
        "non-empty label should not be hidden")))

(deftest empty-label-hidden-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label]")]
    (is (.contains (.-classList label-el) "label-hidden")
        "label should be hidden when not set")))

;; ---------------------------------------------------------------------------
;; hint attribute
;; ---------------------------------------------------------------------------

(deftest hint-test
  (let [el      (append! (make-el))
        hint-el (shadow-part el "[part=hint]")]
    (.setAttribute el model/attr-hint "Enter the total amount")
    (is (= "Enter the total amount" (.-textContent hint-el))
        "hint attribute should set hint text")
    (is (not (.contains (.-classList hint-el) "hint-hidden"))
        "non-empty hint should not be hidden")))

;; ---------------------------------------------------------------------------
;; error attribute
;; ---------------------------------------------------------------------------

(deftest error-test
  (let [el       (append! (make-el))
        error-el (shadow-part el "[part=error]")]
    (.setAttribute el model/attr-error "Amount is required")
    (is (= "Amount is required" (.-textContent error-el))
        "error attribute should set error text")
    (is (.hasAttribute el "data-invalid")
        "data-invalid should be set on host when error is present")
    (is (not (.contains (.-classList error-el) "error-hidden"))
        "error element should not be hidden")))

(deftest no-error-hides-error-el-test
  (let [el       (append! (make-el))
        error-el (shadow-part el "[part=error]")]
    (is (.contains (.-classList error-el) "error-hidden")
        "error element should be hidden when no error")))

;; ---------------------------------------------------------------------------
;; disabled / required / readonly
;; ---------------------------------------------------------------------------

(deftest disabled-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-disabled "")
    (is (.-disabled input-el)
        "input should be disabled when disabled attribute is set")))

(deftest required-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-required "")
    (is (.-required input-el)
        "input should be required when required attribute is set")))

(deftest readonly-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (is (.-readOnly input-el)
        "input should be readOnly when readonly attribute is set")))

;; ---------------------------------------------------------------------------
;; Currency symbol
;; ---------------------------------------------------------------------------

(deftest currency-symbol-test
  (let [el        (append! (make-el))
        symbol-el (shadow-part el "[part=symbol]")]
    (.setAttribute el model/attr-currency "USD")
    (is (= "$" (.-textContent symbol-el))
        "USD should show $ symbol")))

(deftest currency-symbol-eur-test
  (let [el        (append! (make-el))
        symbol-el (shadow-part el "[part=symbol]")]
    (.setAttribute el model/attr-currency "EUR")
    (is (= "€" (.-textContent symbol-el))
        "EUR should show € symbol")))

;; ---------------------------------------------------------------------------
;; Value property
;; ---------------------------------------------------------------------------

(deftest value-property-get-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "1234.56")
    (is (= "1234.56" (.-value el))
        "value property getter should return raw value attribute")))

(deftest value-property-set-test
  (let [el (append! (make-el))]
    (set! (.-value el) "500")
    (is (= "500" (.getAttribute el model/attr-value))
        "setting value property should reflect to attribute")))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest input-event-test
  (async done
    (let [el       (append! (make-el))
          input-el (shadow-part el "[part=input]")
          received (atom nil)]
      (.setAttribute el model/attr-name "price")
      (.addEventListener el model/event-input
                         (fn [^js e] (reset! received e)))
      (set! (.-value input-el) "100")
      (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @received)    "x-currency-field-input should be dispatched")
         (is (= "price" (.-name (.-detail @received)))   "detail.name should match")
         (is (= "100"   (.-value (.-detail @received)))  "detail.value should be raw input value")
         (done))
       0))))

(deftest change-event-test
  (async done
    (let [el       (append! (make-el))
          input-el (shadow-part el "[part=input]")
          received (atom nil)]
      (.setAttribute el model/attr-name "price")
      (.addEventListener el model/event-change
                         (fn [^js e] (reset! received e)))
      (set! (.-value input-el) "1234.5")
      (.dispatchEvent input-el (js/Event. "change" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @received)    "x-currency-field-change should be dispatched")
         (is (= "price" (.-name (.-detail @received)))   "detail.name should match")
         (is (= "1234.5" (.-value (.-detail @received))) "detail.value should be canonical")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Validity
;; ---------------------------------------------------------------------------

(deftest validity-required-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    ;; value is empty by default
    (is (not (.checkValidity el))
        "required field with empty value should be invalid")))

(deftest validity-range-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-min "10")
    (set! (.-value input-el) "5")
    (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
    (is (not (.checkValidity el))
        "value below min should be invalid")))

;; ---------------------------------------------------------------------------
;; Blur formatting
;; ---------------------------------------------------------------------------

(deftest blur-formats-display-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    ;; Setting value attribute on an unfocused element triggers
    ;; attributeChangedCallback → render! which formats the display
    (.setAttribute el model/attr-currency "USD")
    (.setAttribute el model/attr-value "1234.56")
    (let [displayed (.-value input-el)]
      (is (not= "1234.56" displayed)
          "unfocused input should show formatted value, not raw")
      (is (re-find #"1.234" displayed)
          "formatted value should contain grouping separator"))))

;; ---------------------------------------------------------------------------
;; Form reset
;; ---------------------------------------------------------------------------

(deftest form-reset-test
  (async done
    (let [form     (.createElement js/document "form")
          el       (make-el)
          input-el (do (.appendChild form el)
                       (.appendChild (.-body js/document) form)
                       (shadow-part el "[part=input]"))]
      (.setAttribute el model/attr-value "100")
      (js/setTimeout
       (fn []
         (.reset form)
         (js/setTimeout
          (fn []
            (is (= "" (.-value input-el))
                "form reset should clear the input")
            (.remove form)
            (done))
          0))
       0))))

;; ---------------------------------------------------------------------------
;; Cancelable change-request
;; ---------------------------------------------------------------------------

(deftest input-dispatches-change-request-test
  (async done
    (let [el       (append! (make-el))
          input-el (shadow-part el "[part=input]")
          seen     (atom nil)]
      (.setAttribute el model/attr-value "10")
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change-request
           (fn [^js ev]
             (reset! seen {:value          (.-value (.-detail ev))
                           :previous-value (.-previousValue (.-detail ev))})))
         ;; Focus to get raw mode, then type a new value
         (.focus input-el)
         (set! (.-value input-el) "20")
         (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
         (js/setTimeout
          (fn []
            (is (some? @seen) "change-request event should fire")
            (is (= "20" (:value @seen)))
            (is (= "10" (:previous-value @seen)))
            (done))
          0))
       0))))

(deftest change-request-can-be-cancelled-test
  (async done
    (let [el       (append! (make-el))
          input-el (shadow-part el "[part=input]")]
      (.setAttribute el model/attr-value "10")
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change-request
           (fn [^js ev] (.preventDefault ev)))
         (.focus input-el)
         (set! (.-value input-el) "99")
         (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
         (js/setTimeout
          (fn []
            (is (= "10" (.-value input-el))
                "input value should revert when change-request is cancelled")
            (done))
          0))
       0))))
