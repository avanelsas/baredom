(ns app.components.x-select.x-select-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [app.components.x-select.x-select :as x]
            [app.components.x-select.model :as model]))

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
      "x-select should be registered in customElements"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                    "shadow root should exist")
    (is (some? (shadow-part el "[part=wrapper]"))     "wrapper div should exist")
    (is (some? (shadow-part el "[part=select]"))      "select element should exist")
    (is (some? (shadow-part el "[part=chevron]"))     "chevron span should exist")
    (is (some? (.querySelector (.-shadowRoot el) "slot")) "slot should exist")))

;; ---------------------------------------------------------------------------
;; Boolean properties
;; ---------------------------------------------------------------------------

(deftest boolean-property-disabled-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled)
        "setting disabled property should set disabled attr")
    (is (= true (.-disabled el)))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest boolean-property-required-test
  (let [el (append! (make-el))]
    (set! (.-required el) true)
    (is (.hasAttribute el model/attr-required)
        "setting required property should set required attr")
    (set! (.-required el) false)
    (is (not (.hasAttribute el model/attr-required)))))

;; ---------------------------------------------------------------------------
;; String property
;; ---------------------------------------------------------------------------

(deftest string-property-value-test
  (let [el (append! (make-el))]
    (set! (.-value el) "b")
    (is (= "b" (.getAttribute el model/attr-value))
        "setting value property should reflect to value attr")))

;; ---------------------------------------------------------------------------
;; Enum size normalization
;; ---------------------------------------------------------------------------

(deftest enum-size-normalization-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-size "invalid")
    (let [^js wrapper (shadow-part el "[part=wrapper]")]
      (is (= "md" (.getAttribute wrapper "data-size"))
          "invalid size attr should normalize to \"md\" on wrapper"))))

;; ---------------------------------------------------------------------------
;; Disabled syncs to internal select
;; ---------------------------------------------------------------------------

(deftest disabled-syncs-to-select-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (let [^js sel (shadow-part el "[part=select]")]
      (is (= true (.-disabled sel))
          "disabled attr should propagate to internal select element"))))

;; ---------------------------------------------------------------------------
;; Placeholder shows and hides
;; ---------------------------------------------------------------------------

(deftest placeholder-shows-hides-test
  (let [el (append! (make-el))]
    ;; No placeholder yet — option should be hidden
    (let [^js ph (.querySelector (.-shadowRoot el) "option[data-placeholder]")]
      (is (= true (.-hidden ph))
          "placeholder option should be hidden when no placeholder attr"))
    ;; Set placeholder
    (.setAttribute el model/attr-placeholder "Pick one")
    (let [^js ph (.querySelector (.-shadowRoot el) "option[data-placeholder]")]
      (is (= false (.-hidden ph))
          "placeholder option should be visible when placeholder attr is set")
      (is (= "Pick one" (.-textContent ph))))
    ;; Remove placeholder
    (.removeAttribute el model/attr-placeholder)
    (let [^js ph (.querySelector (.-shadowRoot el) "option[data-placeholder]")]
      (is (= true (.-hidden ph))
          "placeholder option should hide again when attr is removed"))))

;; ---------------------------------------------------------------------------
;; Option sync (async — slotchange fires in microtask)
;; ---------------------------------------------------------------------------

(deftest option-sync-test
  (async done
    (let [el   (append! (make-el))
          opt1 (.createElement js/document "option")
          opt2 (.createElement js/document "option")]
      (x/set-attr! opt1 "value" "a")
      (set! (.-textContent opt1) "Option A")
      (x/set-attr! opt2 "value" "b")
      (set! (.-textContent opt2) "Option B")
      (.appendChild el opt1)
      (.appendChild el opt2)
      (js/setTimeout
       (fn []
         (let [^js sel    (shadow-part el "[part=select]")
               options    (.querySelectorAll sel "option:not([data-placeholder])")]
           (is (= 2 (alength options))
               "internal select should have 2 synced options")
           (is (= "a" (.-value (aget options 0))))
           (is (= "b" (.-value (aget options 1))))
           (done)))
       0))))

;; ---------------------------------------------------------------------------
;; Value syncs to internal select after options loaded (async)
;; ---------------------------------------------------------------------------

(deftest value-syncs-to-select-test
  (async done
    (let [el  (append! (make-el))
          opt (.createElement js/document "option")]
      (x/set-attr! opt "value" "x")
      (.appendChild el opt)
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "x")
         (let [^js sel (shadow-part el "[part=select]")]
           (is (= "x" (.-value sel))
               "internal select value should match value attr after options exist"))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; select-change event fires on native change (async)
;; ---------------------------------------------------------------------------

(deftest select-change-event-test
  (async done
    (let [el     (append! (make-el))
          opt-a  (.createElement js/document "option")
          opt-b  (.createElement js/document "option")
          seen   (atom nil)]
      (x/set-attr! opt-a "value" "alpha")
      (set! (.-textContent opt-a) "Alpha")
      (x/set-attr! opt-b "value" "beta")
      (set! (.-textContent opt-b) "Beta")
      (.appendChild el opt-a)
      (.appendChild el opt-b)
      (.addEventListener el model/event-select-change
                         (fn [^js ev]
                           (reset! seen {:value (.-value (.-detail ev))
                                         :label (.-label (.-detail ev))})))
      (js/setTimeout
       (fn []
         ;; Programmatically change the internal select value, then fire native change
         (let [^js sel (shadow-part el "[part=select]")]
           (set! (.-value sel) "beta")
           (.dispatchEvent sel (js/Event. "change" #js {:bubbles true})))
         (js/setTimeout
          (fn []
            (is (some? @seen) "select-change event should have fired")
            (is (= "beta"  (:value @seen)))
            (is (= "Beta"  (:label @seen)))
            (done))
          0))
       0))))

;; ---------------------------------------------------------------------------
;; value attr is NOT auto-set on user change
;; ---------------------------------------------------------------------------

(deftest value-attr-not-auto-set-on-change-test
  (async done
    (let [el    (append! (make-el))
          opt-a (.createElement js/document "option")
          opt-b (.createElement js/document "option")]
      (x/set-attr! opt-a "value" "p")
      (x/set-attr! opt-b "value" "q")
      (.appendChild el opt-a)
      (.appendChild el opt-b)
      (js/setTimeout
       (fn []
         (let [^js sel (shadow-part el "[part=select]")]
           (set! (.-value sel) "q")
           (.dispatchEvent sel (js/Event. "change" #js {:bubbles true})))
         (js/setTimeout
          (fn []
            (is (not (.hasAttribute el model/attr-value))
                "host value attr should NOT be auto-set after user change")
            (done))
          0))
       0))))
