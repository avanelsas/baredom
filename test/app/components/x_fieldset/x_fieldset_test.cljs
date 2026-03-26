(ns app.components.x-fieldset.x-fieldset-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-fieldset.x-fieldset :as x]
            [app.components.x-fieldset.model :as model]))

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
      "x-fieldset should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                    "shadow root should exist")
    (is (some? (shadow-part el "[part=root]"))        "root div should exist")
    (is (some? (shadow-part el "[part=legend]"))      "legend div should exist")
    (is (some? (shadow-part el "[part=content]"))     "content div should exist")
    (is (some? (shadow-part el "slot"))               "default slot should exist")))

(deftest root-role-test
  (let [el      (append! (make-el))
        root-el (shadow-part el "[part=root]")]
    (is (= "group" (.getAttribute root-el "role"))
        "root should have role=group")))

;; ---------------------------------------------------------------------------
;; Legend attribute
;; ---------------------------------------------------------------------------

(deftest legend-text-renders-test
  (let [el        (append! (make-el))
        legend-el (shadow-part el "[part=legend]")]
    (.setAttribute el model/attr-legend "Personal Info")
    (is (= "Personal Info" (.-textContent legend-el))
        "legend text should render in [part=legend]")))

(deftest empty-legend-hides-test
  (let [el        (append! (make-el))
        legend-el (shadow-part el "[part=legend]")]
    (is (.hasAttribute legend-el "hidden")
        "legend with no text should be hidden")))

(deftest legend-visible-removes-hidden-test
  (let [el        (append! (make-el))
        legend-el (shadow-part el "[part=legend]")]
    (.setAttribute el model/attr-legend "Billing")
    (is (not (.hasAttribute legend-el "hidden"))
        "non-empty legend should not be hidden")))

(deftest legend-sets-aria-labelledby-test
  (let [el      (append! (make-el))
        root-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-legend "Address")
    (is (= "x-fieldset-legend" (.getAttribute root-el "aria-labelledby"))
        "aria-labelledby should be set on root when legend is visible")))

(deftest empty-legend-removes-aria-labelledby-test
  (let [el      (append! (make-el))
        root-el (shadow-part el "[part=root]")]
    (is (nil? (.getAttribute root-el "aria-labelledby"))
        "aria-labelledby should not be set when legend is empty")))

;; ---------------------------------------------------------------------------
;; Disabled attribute
;; ---------------------------------------------------------------------------

(deftest disabled-sets-data-disabled-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute el "data-disabled")
        "data-disabled should be set on host when disabled")))

(deftest disabled-removed-clears-data-disabled-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (not (.hasAttribute el "data-disabled"))
        "data-disabled should be removed when disabled attr removed")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest legend-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-legend el) "Shipping")
    (is (= "Shipping" (.getAttribute el model/attr-legend))
        "setting legend property should reflect to attribute")))

(deftest legend-property-read-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-legend "Contact")
    (is (= "Contact" (.-legend el))
        "legend property should read from attribute")))

(deftest disabled-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled)
        "setting disabled=true should set attribute")
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled))
        "setting disabled=false should remove attribute")))

;; ---------------------------------------------------------------------------
;; aria-label forwarding
;; ---------------------------------------------------------------------------

(deftest aria-label-forwarded-to-root-test
  (let [el      (append! (make-el))
        root-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-aria-label "Form section")
    (is (= "Form section" (.getAttribute root-el "aria-label"))
        "aria-label should be forwarded to root")
    (is (nil? (.getAttribute root-el "aria-labelledby"))
        "aria-labelledby should be removed when aria-label is present")))

(deftest aria-describedby-forwarded-to-root-test
  (let [el      (append! (make-el))
        root-el (shadow-part el "[part=root]")]
    (.setAttribute el model/attr-aria-describedby "desc-1")
    (is (= "desc-1" (.getAttribute root-el "aria-describedby"))
        "aria-describedby should be forwarded to root")))
