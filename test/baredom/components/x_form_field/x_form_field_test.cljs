(ns baredom.components.x-form-field.x-form-field-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-form-field.x-form-field :as x]
            [baredom.components.x-form-field.model :as model]))

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
      "x-form-field should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                       "shadow root should exist")
    (is (some? (shadow-part el "[part=field]"))          "field div should exist")
    (is (some? (shadow-part el "[part=label]"))          "label should exist")
    (is (some? (shadow-part el "[part=input-wrapper]"))  "input-wrapper should exist")
    (is (some? (shadow-part el "[part=input]"))          "input should exist")
    (is (some? (shadow-part el "[part=hint]"))           "hint span should exist")
    (is (some? (shadow-part el "[part=error]"))          "error span should exist")))

(deftest input-is-input-element-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (is (= "INPUT" (.-tagName input-el))
        "input part should be an <input> element")))

;; ---------------------------------------------------------------------------
;; label attribute
;; ---------------------------------------------------------------------------

(deftest label-sets-text-content-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label]")]
    (.setAttribute el model/attr-label "Email address")
    (is (= "Email address" (.-textContent label-el))
        "label attribute should set label text content")))

(deftest empty-label-hides-label-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label]")]
    (.setAttribute el model/attr-label "")
    (is (.contains (.-classList label-el) "label-hidden")
        "empty label should add label-hidden class")))

(deftest label-shown-when-set-test
  (let [el       (append! (make-el))
        label-el (shadow-part el "[part=label]")]
    (.setAttribute el model/attr-label "Name")
    (is (not (.contains (.-classList label-el) "label-hidden"))
        "non-empty label should not have label-hidden class")))

;; ---------------------------------------------------------------------------
;; type attribute
;; ---------------------------------------------------------------------------

(deftest type-sets-input-type-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-type "email")
    (is (= "email" (.-type input-el))
        "type=email should set input type to email")))

(deftest invalid-type-falls-back-to-text-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-type "color")
    (is (= "text" (.-type input-el))
        "invalid type should fall back to text")))

;; ---------------------------------------------------------------------------
;; placeholder attribute
;; ---------------------------------------------------------------------------

(deftest placeholder-sets-input-placeholder-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-placeholder "Enter email")
    (is (= "Enter email" (.-placeholder input-el))
        "placeholder attribute should set input placeholder")))

;; ---------------------------------------------------------------------------
;; hint attribute
;; ---------------------------------------------------------------------------

(deftest hint-sets-text-test
  (let [el      (append! (make-el))
        hint-el (shadow-part el "[part=hint]")]
    (.setAttribute el model/attr-hint "We will never share your email")
    (is (= "We will never share your email" (.-textContent hint-el))
        "hint attribute should set hint text")))

(deftest empty-hint-is-hidden-test
  (let [el      (append! (make-el))
        hint-el (shadow-part el "[part=hint]")]
    (is (.contains (.-classList hint-el) "hint-hidden")
        "hint element should be hidden when no hint text")))

(deftest hint-shown-when-set-test
  (let [el      (append! (make-el))
        hint-el (shadow-part el "[part=hint]")]
    (.setAttribute el model/attr-hint "Helper text")
    (is (not (.contains (.-classList hint-el) "hint-hidden"))
        "non-empty hint should not have hint-hidden class")))

;; ---------------------------------------------------------------------------
;; error attribute
;; ---------------------------------------------------------------------------

(deftest error-sets-text-test
  (let [el       (append! (make-el))
        error-el (shadow-part el "[part=error]")]
    (.setAttribute el model/attr-error "This field is required")
    (is (= "This field is required" (.-textContent error-el))
        "error attribute should set error text")))

(deftest error-sets-data-invalid-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-error "Invalid")
    (is (.hasAttribute el "data-invalid")
        "data-invalid should be set on host when error is present")))

(deftest error-sets-aria-invalid-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-error "Invalid value")
    (is (= "true" (.getAttribute input-el "aria-invalid"))
        "aria-invalid should be true on input when error is present")))

(deftest no-error-hides-error-el-test
  (let [el       (append! (make-el))
        error-el (shadow-part el "[part=error]")]
    (is (.contains (.-classList error-el) "error-hidden")
        "error element should be hidden when no error")))

(deftest clear-error-removes-data-invalid-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-error "Error")
    (.removeAttribute el model/attr-error)
    (is (not (.hasAttribute el "data-invalid"))
        "data-invalid should be removed when error attr removed")))

;; ---------------------------------------------------------------------------
;; disabled attribute
;; ---------------------------------------------------------------------------

(deftest disabled-disables-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-disabled "")
    (is (.-disabled input-el)
        "input should be disabled when disabled attribute is set")))

(deftest remove-disabled-enables-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (not (.-disabled input-el))
        "input should not be disabled after removing disabled attribute")))

;; ---------------------------------------------------------------------------
;; readonly attribute
;; ---------------------------------------------------------------------------

(deftest readonly-makes-input-readonly-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-readonly "")
    (is (.-readOnly input-el)
        "input should be readOnly when readonly attribute is set")))

;; ---------------------------------------------------------------------------
;; required attribute
;; ---------------------------------------------------------------------------

(deftest required-sets-input-required-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-required "")
    (is (.-required input-el)
        "input should be required when required attribute is set")))

(deftest required-sets-aria-required-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute input-el "aria-required"))
        "aria-required should be true when required attribute is set")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest value-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-value el) "hello")
    (is (= "hello" (.getAttribute el model/attr-value))
        "setting value property should reflect to attribute")))

(deftest value-property-read-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "hello")
    (is (= "hello" (.-value el))
        "value property should read from attribute")))

(deftest value-property-syncs-input-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (set! (.-value el) "synced")
    (is (= "synced" (.-value input-el))
        "setting value property should sync to shadow input")))

(deftest disabled-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled)
        "setting disabled=true should set attribute")
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled))
        "setting disabled=false should remove attribute")))

(deftest readonly-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly)
        "setting readOnly=true should set attribute")))

(deftest required-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-required el) true)
    (is (.hasAttribute el model/attr-required)
        "setting required=true should set attribute")))

(deftest label-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-label el) "Email")
    (is (= "Email" (.getAttribute el model/attr-label))
        "setting label property should reflect to attribute")
    (is (= "Email" (.-label el))
        "label property getter should read from attribute")))

(deftest type-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-type el) "email")
    (is (= "email" (.getAttribute el model/attr-type))
        "setting type property should reflect to attribute")
    (is (= "email" (.-type el))
        "type property getter should read from attribute")))

(deftest name-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-name el) "username")
    (is (= "username" (.getAttribute el model/attr-name))
        "setting name property should reflect to attribute")
    (is (= "username" (.-name el))
        "name property getter should read from attribute")))

(deftest placeholder-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-placeholder el) "Enter text")
    (is (= "Enter text" (.getAttribute el model/attr-placeholder))
        "setting placeholder property should reflect to attribute")
    (is (= "Enter text" (.-placeholder el))
        "placeholder property getter should read from attribute")))

(deftest autocomplete-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-autocomplete el) "email")
    (is (= "email" (.getAttribute el model/attr-autocomplete))
        "setting autocomplete property should reflect to attribute")
    (is (= "email" (.-autocomplete el))
        "autocomplete property getter should read from attribute")))

;; ---------------------------------------------------------------------------
;; aria-describedby conditional logic
;; ---------------------------------------------------------------------------

(deftest aria-describedby-hint-only-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-hint "Helper text")
    (is (= "hint" (.getAttribute input-el "aria-describedby"))
        "aria-describedby should be 'hint' when only hint is present")))

(deftest aria-describedby-error-only-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-error "Something wrong")
    (is (= "error" (.getAttribute input-el "aria-describedby"))
        "aria-describedby should be 'error' when only error is present")))

(deftest aria-describedby-hint-and-error-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-hint "Helper text")
    (.setAttribute el model/attr-error "Something wrong")
    (is (= "hint error" (.getAttribute input-el "aria-describedby"))
        "aria-describedby should be 'hint error' when both are present")))

(deftest aria-describedby-absent-when-neither-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (is (not (.hasAttribute input-el "aria-describedby"))
        "aria-describedby should be absent when neither hint nor error is present")))

;; ---------------------------------------------------------------------------
;; formResetCallback
;; ---------------------------------------------------------------------------

(deftest form-reset-callback-clears-value-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-value "some value")
    (.formResetCallback el)
    (is (not (.hasAttribute el model/attr-value))
        "formResetCallback should remove the value attribute")
    (is (= "" (.-value input-el))
        "formResetCallback should clear the shadow input value")))

;; ---------------------------------------------------------------------------
;; ElementInternals validity
;; ---------------------------------------------------------------------------

(deftest validity-custom-error-when-error-set-test
  ;; form-associated elements expose .validity directly on the element
  (let [el (append! (make-el))]
    (when (.-validity el)
      (.setAttribute el model/attr-error "Invalid email")
      (is (.-customError (.-validity el))
          "validity.customError should be true when error attribute is set"))))

(deftest validity-value-missing-when-required-and-empty-test
  (let [el (append! (make-el))]
    (when (.-validity el)
      (.setAttribute el model/attr-required "")
      (is (.-valueMissing (.-validity el))
          "validity.valueMissing should be true when required and value is empty"))))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest input-event-dispatched-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")
        received (atom nil)]
    (.setAttribute el model/attr-name "email")
    (.addEventListener el model/event-input
                       (fn [^js e] (reset! received e)))
    (set! (.-value input-el) "user@example.com")
    (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
    (is (some? @received)
        "x-form-field-input should be dispatched on input event")
    (is (= "email" (.-name (.-detail @received)))
        "event detail should contain field name")
    (is (= "user@example.com" (.-value (.-detail @received)))
        "event detail should contain current input value")))

(deftest change-event-dispatched-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")
        received (atom nil)]
    (.setAttribute el model/attr-name "username")
    (.addEventListener el model/event-change
                       (fn [^js e] (reset! received e)))
    (set! (.-value input-el) "alice")
    (.dispatchEvent input-el (js/Event. "change" #js {:bubbles true}))
    (is (some? @received)
        "x-form-field-change should be dispatched on change event")
    (is (= "alice" (.-value (.-detail @received)))
        "change event detail should contain current value")))

(deftest input-event-bubbles-composed-test
  (let [el       (append! (make-el))
        input-el (shadow-part el "[part=input]")
        received (atom nil)]
    (.addEventListener el model/event-input
                       (fn [^js e] (reset! received e)))
    (.dispatchEvent input-el (js/Event. "input" #js {:bubbles true}))
    (is (some? @received) "event should bubble and be composed")
    (is (.-bubbles @received) "event should bubble")
    (is (.-composed @received) "event should be composed")))
