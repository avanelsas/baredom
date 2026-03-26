(ns app.components.x-search-field.x-search-field-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [app.components.x-search-field.x-search-field :as x]
            [app.components.x-search-field.model :as model]))

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
      "x-search-field should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest renders-input-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))              "shadow root should exist")
    (is (some? (shadow-part el "[part=wrapper]")) "wrapper div should exist")
    (is (some? (shadow-part el "[part=icon]"))    "icon span should exist")
    (is (some? (shadow-part el "[part=input]"))   "input should exist")
    (is (some? (shadow-part el "[part=clear]"))   "clear button should exist")))

(deftest input-type-search-test
  (let [el      (append! (make-el))
        input   (shadow-part el "[part=input]")]
    (is (= "search" (.-type input))
        "input type should be \"search\"")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest label-aria-test
  (let [el    (append! (make-el))
        input (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-label "Site search")
    (is (= "Site search" (.getAttribute input "aria-label"))
        "aria-label on input should reflect label attribute")))

(deftest placeholder-test
  (let [el    (append! (make-el))
        input (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-placeholder "Search…")
    (is (= "Search…" (.-placeholder input)))))

(deftest disabled-test
  (let [el    (append! (make-el))
        input (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-disabled "")
    (is (= true (.-disabled input)))))

(deftest required-aria-test
  (let [el    (append! (make-el))
        input (shadow-part el "[part=input]")]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute input "aria-required")))))

;; ---------------------------------------------------------------------------
;; Clear button visibility
;; ---------------------------------------------------------------------------

(deftest clear-button-hidden-when-empty-test
  (let [el    (append! (make-el))
        clear (shadow-part el "[part=clear]")]
    (is (.contains (.-classList clear) "clear-hidden")
        "clear button should be hidden when value is empty")))

(deftest clear-button-visibility-test
  (async done
    (let [el    (append! (make-el))
          input (shadow-part el "[part=input]")
          clear (shadow-part el "[part=clear]")]
      ;; Simulate typing
      (set! (.-value input) "hello")
      (.dispatchEvent input (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (not (.contains (.-classList clear) "clear-hidden"))
             "clear button should be visible when input has value")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Events (async)
;; ---------------------------------------------------------------------------

(deftest search-event-test
  (async done
    (let [el    (append! (make-el))
          input (shadow-part el "[part=input]")
          seen  (atom nil)]
      (.setAttribute el model/attr-name "q")
      (set! (.-value input) "clojure")
      (.addEventListener
       el model/event-search
       (fn [^js ev]
         (reset! seen {:name  (.-name (.-detail ev))
                       :value (.-value (.-detail ev))})))
      (.dispatchEvent input (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen)       "x-search-field-search event should fire")
         (is (= "q"       (:name @seen)))
         (is (= "clojure" (:value @seen)))
         (done))
       0))))

(deftest clear-event-test
  (async done
    (let [el    (append! (make-el))
          input (shadow-part el "[part=input]")
          clear (shadow-part el "[part=clear]")
          seen  (atom nil)]
      (.setAttribute el model/attr-name "q")
      (set! (.-value input) "hello")
      (.dispatchEvent input (js/Event. "input" #js {:bubbles true}))
      (.addEventListener
       el model/event-clear
       (fn [^js ev]
         (reset! seen {:name (.-name (.-detail ev))})))
      (js/setTimeout
       (fn []
         (.click clear)
         (js/setTimeout
          (fn []
            (is (some? @seen)  "x-search-field-clear event should fire")
            (is (= "q" (:name @seen)))
            (is (= "" (.-value input)) "input should be cleared")
            (done))
          0))
       0))))

(deftest input-event-test
  (async done
    (let [el    (append! (make-el))
          input (shadow-part el "[part=input]")
          seen  (atom nil)]
      (.setAttribute el model/attr-name "q")
      (.addEventListener
       el model/event-input
       (fn [^js ev]
         (reset! seen {:name  (.-name (.-detail ev))
                       :value (.-value (.-detail ev))})))
      (set! (.-value input) "test")
      (.dispatchEvent input (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen)    "x-search-field-input event should fire")
         (is (= "q"    (:name @seen)))
         (is (= "test" (:value @seen)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest value-property-test
  (let [el (append! (make-el))]
    (set! (.-value el) "hello")
    (is (= "hello" (.-value el)))))

(deftest name-property-test
  (let [el (append! (make-el))]
    (set! (.-name el) "q")
    (is (= "q" (.getAttribute el model/attr-name)))))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest required-property-test
  (let [el (append! (make-el))]
    (set! (.-required el) true)
    (is (.hasAttribute el model/attr-required))
    (set! (.-required el) false)
    (is (not (.hasAttribute el model/attr-required)))))

;; ---------------------------------------------------------------------------
;; Validity
;; ---------------------------------------------------------------------------

(deftest required-validity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    ;; value is empty — reportValidity should return false
    (is (= false (.reportValidity el))
        "reportValidity should return false when required and empty")))

(deftest required-validity-filled-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    (set! (.-value el) "something")
    (is (= true (.reportValidity el))
        "reportValidity should return true when required and filled")))
