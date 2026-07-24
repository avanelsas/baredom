(ns baredom.components.validity-api-test
  "Every form-associated component must expose the native validation surface
   (baredom.utils.forms/install-validity-api!). ElementInternals does not mirror
   it onto the host, so a component that forgets the call leaves consumers
   unable to validate it other than by attempting a submit."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-checkbox.x-checkbox :as x-checkbox]
            [baredom.components.x-color-picker.x-color-picker :as x-color-picker]
            [baredom.components.x-combobox.x-combobox :as x-combobox]
            [baredom.components.x-currency-field.x-currency-field :as x-currency-field]
            [baredom.components.x-date-picker.x-date-picker :as x-date-picker]
            [baredom.components.x-file-upload.x-file-upload :as x-file-upload]
            [baredom.components.x-form-field.x-form-field :as x-form-field]
            [baredom.components.x-multi-combobox.x-multi-combobox :as x-multi-combobox]
            [baredom.components.x-otp-input.x-otp-input :as x-otp-input]
            [baredom.components.x-radio.x-radio :as x-radio]
            [baredom.components.x-range-slider.x-range-slider :as x-range-slider]
            [baredom.components.x-rating.x-rating :as x-rating]
            [baredom.components.x-search-field.x-search-field :as x-search-field]
            [baredom.components.x-select.x-select :as x-select]
            [baredom.components.x-slider.x-slider :as x-slider]
            [baredom.components.x-switch.x-switch :as x-switch]
            [baredom.components.x-text-area.x-text-area :as x-text-area]))

(def form-associated-tags
  ["x-checkbox" "x-color-picker" "x-combobox" "x-currency-field" "x-date-picker"
   "x-file-upload" "x-form-field" "x-multi-combobox" "x-otp-input" "x-radio"
   "x-range-slider" "x-rating" "x-search-field" "x-select" "x-slider"
   "x-switch" "x-text-area"])

(defn init-all! []
  (x-checkbox/init!) (x-color-picker/init!) (x-combobox/init!)
  (x-currency-field/init!) (x-date-picker/init!) (x-file-upload/init!)
  (x-form-field/init!) (x-multi-combobox/init!) (x-otp-input/init!)
  (x-radio/init!) (x-range-slider/init!) (x-rating/init!)
  (x-search-field/init!) (x-select/init!) (x-slider/init!)
  (x-switch/init!) (x-text-area/init!))

(init-all!)

(defn cleanup-dom! []
  (doseq [tag form-associated-tags]
    (doseq [^js node (.querySelectorAll js/document tag)]
      (.remove node)))
  (doseq [^js node (.querySelectorAll js/document "form[data-validity-api-test]")]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js mount! [tag]
  (let [^js el (.createElement js/document tag)]
    (.appendChild (.-body js/document) el)
    el))

(deftest every-form-associated-component-exposes-validity-test
  (doseq [tag form-associated-tags]
    (testing tag
      (let [^js el (mount! tag)]
        (is (some? (.-validity el))
            (str tag " exposes validity"))
        (is (string? (.-validationMessage el))
            (str tag " exposes validationMessage"))
        (is (boolean? (.-willValidate el))
            (str tag " exposes willValidate"))
        (is (fn? (.-checkValidity el))
            (str tag " exposes checkValidity()"))
        (is (fn? (.-reportValidity el))
            (str tag " exposes reportValidity()"))
        (is (boolean? (.checkValidity el))
            (str tag " checkValidity() returns a boolean"))))))

(deftest validity-reflects-the-required-constraint-test
  (doseq [tag ["x-form-field" "x-text-area" "x-select" "x-combobox"
               "x-multi-combobox" "x-date-picker"]]
    (testing tag
      (let [^js el (mount! tag)]
        (.setAttribute el "required" "")
        (is (false? (.checkValidity el))
            (str tag " required and empty fails validation"))
        (is (true? (.. el -validity -valueMissing))
            (str tag " reports valueMissing"))
        (is (not= "" (.-validationMessage el))
            (str tag " carries a validation message"))))))

(deftest validity-reflects-the-error-attribute-test
  (doseq [tag ["x-form-field" "x-text-area" "x-select" "x-combobox"
               "x-multi-combobox" "x-date-picker" "x-currency-field"]]
    (testing tag
      (let [^js el (mount! tag)]
        (.setAttribute el "error" "Boom")
        (is (true? (.. el -validity -customError))
            (str tag " reports customError"))
        (is (= "Boom" (.-validationMessage el))
            (str tag " reports the error text as the validation message"))
        (.removeAttribute el "error")
        (is (true? (.checkValidity el))
            (str tag " is valid again once the error clears"))))))

(deftest form-and-labels-track-the-owning-form-test
  (testing "form is nil outside a <form>"
    (let [^js el (mount! "x-form-field")]
      (is (nil? (.-form el)))))
  (testing "form resolves the owning <form> and labels its <label>s"
    (let [^js form  (.createElement js/document "form")
          ^js label (.createElement js/document "label")
          ^js el    (.createElement js/document "x-form-field")]
      (.setAttribute form "data-validity-api-test" "")
      (.setAttribute el "id" "vat-field")
      (.setAttribute label "for" "vat-field")
      (.appendChild form label)
      (.appendChild form el)
      (.appendChild (.-body js/document) form)
      (is (= form (.-form el)) "form resolves the owning <form>")
      (is (= 1 (.-length (.-labels el))) "labels lists the associated <label>"))))
