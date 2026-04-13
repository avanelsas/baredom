(ns baredom.components.x-dropdown.x-dropdown-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-dropdown.x-dropdown :as x]
            [baredom.components.x-dropdown.model :as model]))

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
      "x-dropdown should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                          "shadow root must exist")
    (is (some? (shadow-part el "[part=trigger]"))          "trigger must exist")
    (is (some? (shadow-part el "[part=trigger-label]"))    "trigger-label must exist")
    (is (some? (shadow-part el "[part=chevron]"))          "chevron must exist")
    (is (some? (shadow-part el "[part=panel]"))            "panel must exist")
    (is (some? (shadow-part el "[part=panel-inner]"))      "panel-inner must exist")))

;; ---------------------------------------------------------------------------
;; Default state
;; ---------------------------------------------------------------------------

(deftest default-closed-state-test
  (let [el      (append! (make-el))
        trigger (shadow-part el "[part=trigger]")]
    (is (not (.hasAttribute el model/attr-open))
        "should not have [open] by default")
    (is (= "false" (.getAttribute trigger "aria-expanded"))
        "aria-expanded should default to \"false\"")))

(deftest default-aria-attributes-test
  (let [el      (append! (make-el))
        trigger (shadow-part el "[part=trigger]")]
    (is (= "true" (.getAttribute trigger "aria-haspopup"))
        "trigger should have aria-haspopup=true")
    (is (= "panel" (.getAttribute trigger "aria-controls"))
        "trigger should reference panel by id")))

(deftest default-placement-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (is (= model/default-placement (.getAttribute panel "data-placement"))
        "panel should default to bottom-start placement")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest open-attr-sets-aria-expanded-test
  (let [el      (append! (make-el))
        trigger (shadow-part el "[part=trigger]")]
    (.setAttribute el model/attr-open "")
    (is (= "true" (.getAttribute trigger "aria-expanded"))
        "aria-expanded should be \"true\" when [open] is set")))

(deftest disabled-attr-disables-trigger-test
  (let [el      (append! (make-el))
        trigger (shadow-part el "[part=trigger]")]
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute trigger "disabled")
        "trigger should have [disabled] when element is disabled")))

(deftest label-attr-sets-text-test
  (let [el    (append! (make-el))
        label (shadow-part el "[part=trigger-label]")]
    (.setAttribute el model/attr-label "Actions")
    (is (= "Actions" (.-textContent label))
        "trigger-label text should match label attribute")))

(deftest placement-attr-updates-panel-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (.setAttribute el model/attr-placement "top-end")
    (is (= "top-end" (.getAttribute panel "data-placement"))
        "panel data-placement should match placement attribute")))

(deftest invalid-placement-falls-back-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (.setAttribute el model/attr-placement "invalid-value")
    (is (= model/default-placement (.getAttribute panel "data-placement"))
        "panel data-placement should fall back to default for invalid placement")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest open-property-test
  (let [el (append! (make-el))]
    (set! (.-open el) true)
    (is (.hasAttribute el model/attr-open))
    (is (= true (.-open el)))
    (set! (.-open el) false)
    (is (not (.hasAttribute el model/attr-open)))))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest label-property-test
  (let [el (append! (make-el))]
    (set! (.-label el) "My Label")
    (is (= "My Label" (.getAttribute el model/attr-label)))))

(deftest placement-property-test
  (let [el (append! (make-el))]
    (set! (.-placement el) "top-start")
    (is (= "top-start" (.getAttribute el model/attr-placement)))))

;; ---------------------------------------------------------------------------
;; Public methods
;; ---------------------------------------------------------------------------

(deftest toggle-method-opens-test
  (async done
    (let [el (append! (make-el))]
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (.hasAttribute el model/attr-open)
             "toggle() should open when closed")
         (done))
       0))))

(deftest toggle-method-closes-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-open "")
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "toggle() should close when open")
         (done))
       0))))

(deftest show-method-opens-test
  (async done
    (let [el (append! (make-el))]
      (.show el)
      (js/setTimeout
       (fn []
         (is (.hasAttribute el model/attr-open)
             "show() should open the panel")
         (done))
       0))))

(deftest show-method-idempotent-test
  (async done
    (let [el     (append! (make-el))
          count  (atom 0)]
      (.setAttribute el model/attr-open "")
      (.addEventListener el model/event-toggle (fn [_] (swap! count inc)))
      (.show el)
      (js/setTimeout
       (fn []
         (is (.hasAttribute el model/attr-open) "panel remains open")
         (is (= 0 @count) "show() on already-open panel fires no event")
         (done))
       0))))

(deftest hide-method-closes-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-open "")
      (.hide el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "hide() should close the panel")
         (done))
       0))))

(deftest hide-method-idempotent-test
  (async done
    (let [el    (append! (make-el))
          count (atom 0)]
      (.addEventListener el model/event-toggle (fn [_] (swap! count inc)))
      (.hide el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open)) "panel remains closed")
         (is (= 0 @count) "hide() on already-closed panel fires no event")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest toggle-event-fires-before-change-test
  (async done
    (let [el    (append! (make-el))
          order (atom [])]
      (.addEventListener el model/event-toggle
                         (fn [_] (swap! order conj :toggle)))
      (.addEventListener el model/event-change
                         (fn [_] (swap! order conj :change)))
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (= [:toggle :change] @order)
             "toggle event must fire before change event")
         (done))
       0))))

(deftest toggle-event-is-cancelable-test
  (async done
    (let [el (append! (make-el))]
      (.addEventListener el model/event-toggle
                         (fn [^js ev] (.preventDefault ev)))
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "open should not change when toggle event is cancelled")
         (done))
       0))))

(deftest toggle-event-detail-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener el model/event-toggle
                         (fn [^js ev]
                           (reset! seen {:open   (.-open (.-detail ev))
                                         :source (.-source (.-detail ev))})))
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (= {:open true :source "programmatic"} @seen))
         (done))
       0))))

(deftest change-event-detail-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js ev]
                           (reset! seen {:open (.-open (.-detail ev))})))
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (= {:open true} @seen))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Disabled blocks toggle
;; ---------------------------------------------------------------------------

(deftest disabled-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "disabled dropdown should not open on toggle()")
         (done))
       0))))

(deftest disabled-blocks-show-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.show el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "disabled dropdown should not open on show()")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-open "")
    (.setAttribute el model/attr-label "Actions")
    (.setAttribute el model/attr-placement "top-start")
    (.remove el)
    (.appendChild body el)
    (let [trigger (shadow-part el "[part=trigger]")
          label   (shadow-part el "[part=trigger-label]")
          panel   (shadow-part el "[part=panel]")]
      (is (= "true" (.getAttribute trigger "aria-expanded"))
          "aria-expanded should be preserved after reconnect")
      (is (= "Actions" (.-textContent label))
          "label text should be preserved after reconnect")
      (is (= "top-start" (.getAttribute panel "data-placement"))
          "placement should be preserved after reconnect"))))
