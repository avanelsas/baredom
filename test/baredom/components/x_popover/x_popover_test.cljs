(ns baredom.components.x-popover.x-popover-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-popover.x-popover :as x]
            [baredom.components.x-popover.model :as model]))

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
      "x-popover should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                          "shadow root must exist")
    (is (some? (shadow-part el "[part=trigger]"))          "trigger must exist")
    (is (some? (shadow-part el "[part=panel]"))            "panel must exist")
    (is (some? (shadow-part el "[part=arrow]"))            "arrow must exist")
    (is (some? (shadow-part el "[part=header]"))           "header must exist")
    (is (some? (shadow-part el "[part=heading]"))          "heading must exist")
    (is (some? (shadow-part el "[part=close-button]"))     "close-button must exist")
    (is (some? (shadow-part el "[part=body]"))             "body must exist")
    (is (some? (shadow-part el "[part=footer]"))           "footer must exist")
    (is (some? (shadow-part el "slot[name=trigger]"))      "trigger slot must exist")
    (is (some? (shadow-part el "slot:not([name])"))        "default body slot must exist")
    (is (some? (shadow-part el "slot[name=footer]"))       "footer slot must exist")))

;; ---------------------------------------------------------------------------
;; Default state
;; ---------------------------------------------------------------------------

(deftest default-state-test
  (let [el        (append! (make-el))
        panel     (shadow-part el "[part=panel]")
        close-btn (shadow-part el "[part=close-button]")]
    (is (not (.hasAttribute el model/attr-open))
        "should not have [open] by default")
    (is (.hasAttribute panel "inert")
        "panel should have [inert] when closed by default")
    (is (= model/default-placement (.getAttribute panel "data-placement"))
        "panel data-placement should default to bottom-start")
    (is (not (.hasAttribute close-btn "hidden"))
        "close button should be visible by default")
    (is (= model/default-close-label (.getAttribute close-btn "aria-label"))
        "close button should have default aria-label")))

;; ---------------------------------------------------------------------------
;; Attribute → DOM updates
;; ---------------------------------------------------------------------------

(deftest open-attr-removes-inert-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (.setAttribute el model/attr-open "")
    (is (not (.hasAttribute panel "inert"))
        "panel should not have [inert] when [open] is set")))

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

(deftest heading-attr-sets-text-test
  (let [el      (append! (make-el))
        heading (shadow-part el "[part=heading]")]
    (.setAttribute el model/attr-heading "My Title")
    (is (= "My Title" (.-textContent heading))
        "heading textContent should match heading attribute")))

(deftest close-label-attr-updates-btn-test
  (let [el        (append! (make-el))
        close-btn (shadow-part el "[part=close-button]")]
    (.setAttribute el model/attr-close-label "Dismiss")
    (is (= "Dismiss" (.getAttribute close-btn "aria-label"))
        "close button aria-label should match close-label attribute")))

(deftest no-close-attr-hides-btn-test
  (let [el        (append! (make-el))
        close-btn (shadow-part el "[part=close-button]")]
    (.setAttribute el model/attr-no-close "")
    (is (.hasAttribute close-btn "hidden")
        "close button should be hidden when [no-close] is set")))

;; ---------------------------------------------------------------------------
;; aria-labelledby
;; ---------------------------------------------------------------------------

(deftest aria-labelledby-set-when-heading-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (.setAttribute el model/attr-heading "My Panel")
    (is (= "popover-heading" (.getAttribute panel "aria-labelledby"))
        "panel should have aria-labelledby when heading is set")))

(deftest aria-labelledby-removed-when-heading-empty-test
  (let [el    (append! (make-el))
        panel (shadow-part el "[part=panel]")]
    (.setAttribute el model/attr-heading "My Panel")
    (.setAttribute el model/attr-heading "")
    (is (not (.hasAttribute panel "aria-labelledby"))
        "panel should not have aria-labelledby when heading is empty")))

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

(deftest no-close-property-test
  (let [el (append! (make-el))]
    (set! (.-noClose el) true)
    (is (.hasAttribute el model/attr-no-close))
    (set! (.-noClose el) false)
    (is (not (.hasAttribute el model/attr-no-close)))))

(deftest placement-property-test
  (let [el (append! (make-el))]
    (set! (.-placement el) "top-start")
    (is (= "top-start" (.getAttribute el model/attr-placement)))))

(deftest heading-property-test
  (let [el (append! (make-el))]
    (set! (.-heading el) "Hello")
    (is (= "Hello" (.getAttribute el model/attr-heading)))))

(deftest close-label-property-test
  (let [el (append! (make-el))]
    (set! (.-closeLabel el) "Dismiss")
    (is (= "Dismiss" (.getAttribute el model/attr-close-label)))))

;; ---------------------------------------------------------------------------
;; Public methods
;; ---------------------------------------------------------------------------

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
    (let [el    (append! (make-el))
          count (atom 0)]
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
      (.show el)
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
      (.show el)
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
      (.show el)
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
      (.show el)
      (js/setTimeout
       (fn []
         (is (= {:open true} @seen))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Disabled blocks toggle
;; ---------------------------------------------------------------------------

(deftest disabled-blocks-show-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.show el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "disabled popover should not open on show()")
         (done))
       0))))

(deftest disabled-blocks-hide-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-open "")
      (.setAttribute el model/attr-disabled "")
      (.hide el)
      (js/setTimeout
       (fn []
         (is (.hasAttribute el model/attr-open)
             "disabled popover should not close on hide()")
         (done))
       0))))

(deftest disabled-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.toggle el)
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "disabled popover should not open on toggle()")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Header hidden when empty + no-close
;; ---------------------------------------------------------------------------

(deftest header-hidden-when-empty-and-no-close-test
  (let [el     (append! (make-el))
        header (shadow-part el "[part=header]")]
    (.setAttribute el model/attr-no-close "")
    (is (.hasAttribute header "hidden")
        "header should be hidden when heading is empty and no-close is set")))

(deftest header-visible-when-heading-present-test
  (let [el     (append! (make-el))
        header (shadow-part el "[part=header]")]
    (.setAttribute el model/attr-no-close "")
    (.setAttribute el model/attr-heading "Title")
    (is (not (.hasAttribute header "hidden"))
        "header should be visible when heading is non-empty")))

(deftest header-visible-when-close-button-shown-test
  (let [el     (append! (make-el))
        header (shadow-part el "[part=header]")]
    (is (not (.hasAttribute header "hidden"))
        "header should be visible when close button is shown (default)")))

;; ---------------------------------------------------------------------------
;; Trigger click interaction
;; ---------------------------------------------------------------------------

(deftest trigger-click-opens-test
  (async done
    (let [el         (append! (make-el))
          trigger-el (shadow-part el "[part=trigger]")]
      (.dispatchEvent trigger-el (js/MouseEvent. "click" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (.hasAttribute el model/attr-open)
             "clicking trigger should open the popover")
         (done))
       0))))

(deftest trigger-click-closes-test
  (async done
    (let [el         (append! (make-el))
          trigger-el (shadow-part el "[part=trigger]")]
      (.setAttribute el model/attr-open "")
      (.dispatchEvent trigger-el (js/MouseEvent. "click" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "clicking trigger should close an open popover")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Close button click interaction
;; ---------------------------------------------------------------------------

(deftest close-button-click-closes-test
  (async done
    (let [el        (append! (make-el))
          close-btn (shadow-part el "[part=close-button]")]
      (.setAttribute el model/attr-open "")
      (.dispatchEvent close-btn (js/MouseEvent. "click" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open))
             "clicking close button should close the popover")
         (done))
       0))))

(deftest close-button-event-source-test
  (async done
    (let [el        (append! (make-el))
          close-btn (shadow-part el "[part=close-button]")
          seen      (atom nil)]
      (.setAttribute el model/attr-open "")
      (.addEventListener el model/event-toggle
                         (fn [^js ev]
                           (reset! seen (.-source (.-detail ev)))))
      (.dispatchEvent close-btn (js/MouseEvent. "click" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (= "close-button" @seen)
             "close button click should report source as close-button")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Escape key interaction
;; ---------------------------------------------------------------------------

(deftest escape-key-closes-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-open "")
      ;; Document listeners are attached on next tick, so wait first
      (js/setTimeout
       (fn []
         (.dispatchEvent js/document
                         (js/KeyboardEvent. "keydown"
                                            #js {:key     "Escape"
                                                 :bubbles true}))
         (js/setTimeout
          (fn []
            (is (not (.hasAttribute el model/attr-open))
                "Escape key should close the popover")
            (done))
          0))
       10))))

(deftest escape-event-source-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.setAttribute el model/attr-open "")
      (.addEventListener el model/event-toggle
                         (fn [^js ev]
                           (reset! seen (.-source (.-detail ev)))))
      (js/setTimeout
       (fn []
         (.dispatchEvent js/document
                         (js/KeyboardEvent. "keydown"
                                            #js {:key     "Escape"
                                                 :bubbles true}))
         (js/setTimeout
          (fn []
            (is (= "escape" @seen)
                "Escape should report source as escape")
            (done))
          0))
       10))))

;; ---------------------------------------------------------------------------
;; Outside click interaction
;; ---------------------------------------------------------------------------

(deftest outside-click-closes-test
  (async done
    (let [el      (append! (make-el))
          outside (.createElement js/document "div")]
      (.appendChild (.-body js/document) outside)
      (.setAttribute el model/attr-open "")
      ;; Document listeners are attached on next tick
      (js/setTimeout
       (fn []
         (.dispatchEvent outside
                         (js/MouseEvent. "click"
                                         #js {:bubbles  true
                                              :composed true}))
         (js/setTimeout
          (fn []
            (is (not (.hasAttribute el model/attr-open))
                "clicking outside should close the popover")
            (.remove outside)
            (done))
          0))
       10))))

;; ---------------------------------------------------------------------------
;; Reconnect stability
;; ---------------------------------------------------------------------------

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-open "")
    (.setAttribute el model/attr-heading "My Panel")
    (.setAttribute el model/attr-placement "top-start")
    (.remove el)
    (.appendChild body el)
    (let [panel   (shadow-part el "[part=panel]")
          heading (shadow-part el "[part=heading]")]
      (is (not (.hasAttribute panel "inert"))
          "panel should not be inert after reconnect")
      (is (= "My Panel" (.-textContent heading))
          "heading text should be preserved after reconnect")
      (is (= "top-start" (.getAttribute panel "data-placement"))
          "placement should be preserved after reconnect"))))
