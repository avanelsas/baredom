(ns baredom.form-disabled-test
  (:require [baredom.components-table :as table]
            [baredom.test-elements :as elements]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(def ^:private form-controls
  "The tag of every form control of the registry."
  #{"x-button" "x-checkbox" "x-color-picker" "x-combobox" "x-currency-field" "x-date-picker"
    "x-file-upload" "x-form-field" "x-multi-combobox" "x-otp-input" "x-particle-button" "x-radio"
    "x-range-slider" "x-rating" "x-search-field" "x-select" "x-slider" "x-switch" "x-text-area"})

(def ^:private attr-disabled "disabled")

(use-fixtures :each {:after elements/remove-all!})

(def ^:private marks
  "The attributes by which an element shows that it is disabled."
  ["disabled" "aria-disabled" "data-disabled" "tabindex" "inert"])

(defn- marks-of [^js el]
  (into {}
        (comp (filter (fn [mark] (.hasAttribute el mark)))
              (map (fn [mark] [mark (.getAttribute el mark)])))
        marks))

(defn- shown
  "What `el` shows of being disabled: the marks on it without `disabled`, and the marks on each
   element of its shadow tree."
  [^js el]
  {:host   (dissoc (marks-of el) attr-disabled)
   :shadow (mapv marks-of (array-seq (.querySelectorAll (.-shadowRoot el) "*")))})

(defn- register-all! []
  (doseq [[register!] table/components]
    (register!)))

(defn- form-associated? [tag]
  (true? (.-formAssociated (.get js/customElements tag))))

(deftest the-form-controls-are-the-form-associated-components
  (register-all!)
  (is (= form-controls
         (set (filter form-associated? (map (comp :tag-name second) table/components))))))

(deftest a-control-follows-its-fieldset-both-ways
  (register-all!)
  (doseq [tag (sort form-controls)]
    (testing tag
      (let [enabled                    (shown (elements/in-body! tag {}))
            disabled                   (shown (elements/in-body! tag {attr-disabled ""}))
            [^js fieldset ^js control] (elements/in-fieldset! tag {})]
        (is (not= enabled disabled) "a disabled control shows something else")
        (set! (.-disabled fieldset) true)
        (is (= disabled (shown control)) "in a disabled fieldset it shows what a disabled control shows")
        (is (false? (.hasAttribute control attr-disabled)) "it gets no disabled attribute")
        (set! (.-disabled fieldset) false)
        (is (= enabled (shown control)) "in the enabled fieldset it shows what an enabled control shows")))))

(deftest a-control-with-its-own-attribute-stays-disabled-when-its-fieldset-is-enabled
  (register-all!)
  (doseq [tag (sort form-controls)]
    (testing tag
      (let [disabled                   (shown (elements/in-body! tag {attr-disabled ""}))
            [^js fieldset ^js control] (elements/in-fieldset! tag {attr-disabled ""})]
        (set! (.-disabled fieldset) true)
        (set! (.-disabled fieldset) false)
        (is (= disabled (shown control)) "it shows what a disabled control shows")
        (is (true? (.hasAttribute control attr-disabled)) "it keeps its attribute")))))
