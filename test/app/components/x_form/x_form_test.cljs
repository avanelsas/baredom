(ns app.components.x-form.x-form-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [app.components.x-form.x-form :as x]
            [app.components.x-form.model :as model]))

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
      "x-form should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))              "shadow root should exist")
    (is (some? (shadow-part el "[part=root]")) "form[part=root] should exist")
    (is (some? (shadow-part el "slot"))        "default slot should exist")))

(deftest shadow-form-is-form-element-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (is (= "FORM" (.-tagName form-el))
        "root part should be a <form> element")))

(deftest shadow-form-always-has-novalidate-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (is (.hasAttribute form-el "novalidate")
        "shadow form always has novalidate attribute")))

;; ---------------------------------------------------------------------------
;; loading attribute
;; ---------------------------------------------------------------------------

(deftest loading-sets-aria-busy-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-loading "")
    (is (= "true" (.getAttribute form-el "aria-busy"))
        "aria-busy should be true when loading is set")))

(deftest loading-sets-data-loading-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-loading "")
    (is (.hasAttribute form-el "data-loading")
        "data-loading should be set on shadow form when loading")))

(deftest remove-loading-clears-aria-busy-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-loading "")
    (.removeAttribute el model/attr-loading)
    (is (not (.hasAttribute form-el "aria-busy"))
        "aria-busy should be removed when loading is removed")))

;; ---------------------------------------------------------------------------
;; autocomplete attribute
;; ---------------------------------------------------------------------------

(deftest autocomplete-forwarded-to-form-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-autocomplete "off")
    (is (= "off" (.getAttribute form-el "autocomplete"))
        "autocomplete attr should be forwarded to shadow form")))

(deftest autocomplete-defaults-to-on-test
  (let [el      (append! (make-el))
        form-el (shadow-part el "[part=root]")]
    (is (= "on" (.getAttribute form-el "autocomplete"))
        "autocomplete should default to \"on\"")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest loading-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-loading el) true)
    (is (.hasAttribute el model/attr-loading)
        "loading property setter should set attribute")
    (set! (.-loading el) false)
    (is (not (.hasAttribute el model/attr-loading))
        "loading property setter false should remove attribute")))

(deftest novalidate-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-novalidate el) true)
    (is (.hasAttribute el model/attr-novalidate)
        "novalidate property setter true should set attribute")
    (set! (.-novalidate el) false)
    (is (not (.hasAttribute el model/attr-novalidate))
        "novalidate property setter false should remove attribute")))

(deftest autocomplete-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-autocomplete el) "off")
    (is (= "off" (.getAttribute el model/attr-autocomplete))
        "autocomplete property should reflect to attribute")))

;; ---------------------------------------------------------------------------
;; x-form-submit event
;; ---------------------------------------------------------------------------

(deftest submit-fires-x-form-submit-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "input")
        received (atom nil)]
    (set! (.-name field-el)  "username")
    (set! (.-value field-el) "alice")
    (.appendChild el field-el)
    (.addEventListener el model/event-submit (fn [^js e] (reset! received e)))
    (.submit el)
    (is (some? @received)
        "x-form-submit should be dispatched on submit")))

(deftest submit-collects-values-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "input")
        received (atom nil)]
    (set! (.-name field-el)  "email")
    (set! (.-value field-el) "user@example.com")
    (.appendChild el field-el)
    (.addEventListener el model/event-submit
                       (fn [^js e] (reset! received (.-detail e))))
    (.submit el)
    (is (= "user@example.com" (aget (.-values @received) "email"))
        "submitted values should include field value")))

(deftest submit-excludes-disabled-fields-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "input")
        received (atom nil)]
    (set! (.-name field-el)  "hidden")
    (set! (.-value field-el) "secret")
    (.setAttribute field-el "disabled" "")
    (.appendChild el field-el)
    (.addEventListener el model/event-submit
                       (fn [^js e] (reset! received (.-detail e))))
    (.submit el)
    (is (= js/undefined (aget (.-values @received) "hidden"))
        "disabled fields should be excluded from values")))

(deftest submit-blocked-when-loading-test
  (let [el       (append! (make-el))
        received (atom nil)]
    (.setAttribute el model/attr-loading "")
    (.addEventListener el model/event-submit (fn [^js e] (reset! received e)))
    (.submit el)
    (is (nil? @received)
        "x-form-submit should NOT fire when loading is set")))

(deftest submit-event-is-cancelable-test
  (let [el       (append! (make-el))
        received (atom nil)]
    (.addEventListener el model/event-submit (fn [^js e] (reset! received e)))
    (.submit el)
    (when @received
      (is (.-cancelable @received)
          "x-form-submit should be cancelable"))))

;; ---------------------------------------------------------------------------
;; x-form-reset event
;; ---------------------------------------------------------------------------

(deftest reset-fires-x-form-reset-test
  (let [el       (append! (make-el))
        received (atom nil)]
    (.addEventListener el model/event-reset (fn [^js e] (reset! received e)))
    (.reset el)
    (is (some? @received)
        "x-form-reset should be dispatched on reset")))

;; ---------------------------------------------------------------------------
;; novalidate skips reportValidity
;; ---------------------------------------------------------------------------

(deftest novalidate-allows-submit-without-validity-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "input")
        received (atom nil)]
    (.setAttribute el model/attr-novalidate "")
    (set! (.-name field-el)     "email")
    (set! (.-required field-el) true)
    (set! (.-value field-el)    "")
    (.appendChild el field-el)
    (.addEventListener el model/event-submit (fn [^js e] (reset! received e)))
    (.submit el)
    (is (some? @received)
        "novalidate should allow submit even if required field is empty")))

;; ---------------------------------------------------------------------------
;; setFieldError / clearErrors
;; ---------------------------------------------------------------------------

(deftest set-field-error-sets-attr-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "x-form-field")]
    (.setAttribute field-el "name" "email")
    (.appendChild el field-el)
    (.setFieldError el "email" "Invalid email")
    (is (= "Invalid email" (.getAttribute field-el "error"))
        "setFieldError should set error attr on named field")))

(deftest set-field-error-empty-clears-attr-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "x-form-field")]
    (.setAttribute field-el "name" "email")
    (.setAttribute field-el "error" "old error")
    (.appendChild el field-el)
    (.setFieldError el "email" "")
    (is (not (.hasAttribute field-el "error"))
        "setFieldError with empty string should remove error attr")))

(deftest set-field-error-nil-clears-attr-test
  (let [el       (append! (make-el))
        field-el (.createElement js/document "x-form-field")]
    (.setAttribute field-el "name" "email")
    (.setAttribute field-el "error" "old error")
    (.appendChild el field-el)
    (.setFieldError el "email" nil)
    (is (not (.hasAttribute field-el "error"))
        "setFieldError with nil should remove error attr")))

(deftest clear-errors-removes-all-test
  (let [el      (append! (make-el))
        field-a (.createElement js/document "x-form-field")
        field-b (.createElement js/document "x-form-field")]
    (.setAttribute field-a "name"  "email")
    (.setAttribute field-a "error" "bad email")
    (.setAttribute field-b "name"  "password")
    (.setAttribute field-b "error" "too short")
    (.appendChild el field-a)
    (.appendChild el field-b)
    (.clearErrors el)
    (is (not (.hasAttribute field-a "error"))
        "clearErrors should remove error from field-a")
    (is (not (.hasAttribute field-b "error"))
        "clearErrors should remove error from field-b")))
