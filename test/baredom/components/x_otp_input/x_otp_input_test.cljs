(ns baredom.components.x-otp-input.x-otp-input-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-otp-input.x-otp-input :as x]
            [baredom.components.x-otp-input.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))
(defn ^js shadow-parts [^js el selector] (.querySelectorAll (.-shadowRoot el) selector))

(defn slot-at [^js el index]
  (aget (shadow-parts el "input[part=slot]") index))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-otp-input should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest renders-default-slots-test
  (let [el    (append! (make-el))
        slots (shadow-parts el "input[part=slot]")]
    (is (some? (.-shadowRoot el))                "shadow root should exist")
    (is (some? (shadow-part el "[part=root]"))   "root group should exist")
    (is (= 6 (.-length slots))
        "default length renders 6 slot inputs")))

(deftest custom-length-renders-correct-slots-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-length "4")
    (is (= 4 (.-length (shadow-parts el "input[part=slot]"))))))

(deftest length-clamps-to-cap-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-length "99")
    (is (= model/max-length-cap (.-length (shadow-parts el "input[part=slot]"))))))

(deftest first-slot-has-one-time-code-autocomplete-test
  (let [el       (append! (make-el))
        ^js slot (slot-at el 0)]
    (is (= "one-time-code" (.getAttribute slot "autocomplete"))
        "first slot uses one-time-code so iOS / Chrome SMS autofill works")))

(deftest non-first-slots-disable-autocomplete-test
  (let [el (append! (make-el))]
    (is (= "off" (.getAttribute (slot-at el 1) "autocomplete")))
    (is (= "off" (.getAttribute (slot-at el 5) "autocomplete")))))

(deftest numeric-type-uses-numeric-inputmode-test
  (let [el (append! (make-el))]
    (is (= "numeric" (.getAttribute (slot-at el 0) "inputmode")))))

(deftest alpha-type-uses-text-inputmode-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-type "alpha")
    (is (= "text" (.getAttribute (slot-at el 0) "inputmode")))))

(deftest mask-changes-slot-input-type-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mask "")
    (is (= "password" (.-type (slot-at el 0))))))

(deftest aria-label-on-root-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Verification code")
    (is (= "Verification code"
           (.getAttribute (shadow-part el "[part=root]") "aria-label")))))

;; ---------------------------------------------------------------------------
;; Initial value distribution
;; ---------------------------------------------------------------------------

(deftest initial-value-distributes-into-slots-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "1234")
    (is (= "1" (.-value (slot-at el 0))))
    (is (= "2" (.-value (slot-at el 1))))
    (is (= "3" (.-value (slot-at el 2))))
    (is (= "4" (.-value (slot-at el 3))))
    (is (= "" (.-value (slot-at el 4))))
    (is (= "" (.-value (slot-at el 5))))))

(deftest initial-value-filters-invalid-chars-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "1a2b3c")
    (is (= "123" (.-value el))
        "value attribute is normalized")))

(deftest initial-value-truncates-to-length-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-length "4")
    (.setAttribute el model/attr-value "123456789")
    (is (= "1234" (.-value el)))))

;; ---------------------------------------------------------------------------
;; Disabled / readonly
;; ---------------------------------------------------------------------------

(deftest disabled-propagates-to-slots-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= true (.-disabled (slot-at el 0))))
    (is (= true (.-disabled (slot-at el 5))))))

(deftest readonly-propagates-to-slots-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-readonly "")
    (is (= true (.-readOnly (slot-at el 0))))))

;; ---------------------------------------------------------------------------
;; Required / aria-required
;; ---------------------------------------------------------------------------

(deftest required-sets-aria-required-on-first-slot-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute (slot-at el 0) "aria-required")))
    (is (nil? (.getAttribute (slot-at el 1) "aria-required"))
        "non-first slots do not duplicate aria-required")))

;; ---------------------------------------------------------------------------
;; Error
;; ---------------------------------------------------------------------------

(deftest error-sets-aria-invalid-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-error "Wrong code")
    (is (= "true" (.getAttribute (slot-at el 0) "aria-invalid")))
    (is (= "true" (.getAttribute (slot-at el 5) "aria-invalid")))))

;; ---------------------------------------------------------------------------
;; Value property
;; ---------------------------------------------------------------------------

(deftest value-property-test
  (let [el (append! (make-el))]
    (set! (.-value el) "9876")
    (is (= "9876" (.-value el)))
    (is (= "9" (.-value (slot-at el 0))))
    (is (= "6" (.-value (slot-at el 3))))))

(deftest value-property-clears-on-empty-test
  (let [el (append! (make-el))]
    (set! (.-value el) "1234")
    (set! (.-value el) "")
    (is (= "" (.-value el)))
    (is (= "" (.-value (slot-at el 0))))))

;; ---------------------------------------------------------------------------
;; Property reflection
;; ---------------------------------------------------------------------------

(deftest length-property-test
  (let [el (append! (make-el))]
    (set! (.-length el) 4)
    (is (= "4" (.getAttribute el model/attr-length)))
    (is (= 4 (.-length (shadow-parts el "input[part=slot]"))))))

(deftest type-property-test
  (let [el (append! (make-el))]
    (set! (.-type el) "alphanumeric")
    (is (= "alphanumeric" (.getAttribute el model/attr-type)))))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ---------------------------------------------------------------------------
;; Methods
;; ---------------------------------------------------------------------------

(deftest clear-method-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "1234")
    (.clear el)
    (is (= "" (.-value el)))
    (is (= "" (.-value (slot-at el 0))))))

(deftest focus-method-focuses-first-empty-slot-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "12")
    (.focus el)
    (is (= (slot-at el 2) (.-activeElement (.-shadowRoot el)))
        "focus jumps to first empty slot")))

(deftest focus-method-without-value-focuses-slot-zero-test
  (let [el (append! (make-el))]
    (.focus el)
    (is (= (slot-at el 0) (.-activeElement (.-shadowRoot el))))))

;; ---------------------------------------------------------------------------
;; User input — auto-advance
;; ---------------------------------------------------------------------------

(deftest typing-auto-advances-focus-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)]
      (.focus slot)
      (set! (.-value slot) "5")
      (.dispatchEvent slot (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (= "5" (.-value el))
             "host value reflects typed character")
         (is (= (slot-at el 1) (.-activeElement (.-shadowRoot el)))
             "focus advances to next slot")
         (done))
       0))))

(deftest typing-non-allowed-char-is-filtered-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)]
      ;; Default type is numeric — typing 'a' should be filtered out
      (set! (.-value slot) "a")
      (.dispatchEvent slot (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (= "" (.-value slot))
             "non-numeric char is stripped")
         (is (= "" (.-value el)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Backspace navigation
;; ---------------------------------------------------------------------------

(deftest backspace-on-empty-slot-moves-focus-back-test
  (async done
    (let [el        (append! (make-el))
          ^js slot1 (slot-at el 1)]
      (.setAttribute el model/attr-value "1")
      (.focus slot1)
      (.dispatchEvent slot1 (js/KeyboardEvent. "keydown"
                                                #js {:key "Backspace" :bubbles true}))
      (js/setTimeout
       (fn []
         (is (= "" (.-value el))
             "backspace clears previous slot's value")
         (is (= (slot-at el 0) (.-activeElement (.-shadowRoot el)))
             "focus moves back to slot 0")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Arrow navigation
;; ---------------------------------------------------------------------------

(deftest arrow-right-moves-focus-test
  (let [el        (append! (make-el))
        ^js slot0 (slot-at el 0)]
    (.focus slot0)
    (.dispatchEvent slot0 (js/KeyboardEvent. "keydown"
                                              #js {:key "ArrowRight" :bubbles true}))
    (is (= (slot-at el 1) (.-activeElement (.-shadowRoot el))))))

(deftest arrow-left-moves-focus-test
  (let [el        (append! (make-el))
        ^js slot2 (slot-at el 2)]
    (.focus slot2)
    (.dispatchEvent slot2 (js/KeyboardEvent. "keydown"
                                              #js {:key "ArrowLeft" :bubbles true}))
    (is (= (slot-at el 1) (.-activeElement (.-shadowRoot el))))))

;; ---------------------------------------------------------------------------
;; Paste handling
;; ---------------------------------------------------------------------------

(defn make-paste-event
  "Build a paste event with a stubbed `clipboardData`. Chrome's ClipboardEvent
  constructor does not honour the clipboardData option, so we define the property
  on the dispatched event directly."
  [text]
  (let [^js evt (js/Event. "paste" #js {:bubbles true :cancelable true})]
    (js/Object.defineProperty
     evt "clipboardData"
     #js {:value (js-obj "getData" (fn [_type] text))})
    evt))

(deftest paste-distributes-across-slots-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)]
      (.focus slot)
      (.dispatchEvent slot (make-paste-event "123456"))
      (js/setTimeout
       (fn []
         (is (= "123456" (.-value el)))
         (is (= "1" (.-value (slot-at el 0))))
         (is (= "6" (.-value (slot-at el 5))))
         (done))
       0))))

(deftest paste-filters-invalid-chars-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)]
      (.focus slot)
      ;; Default type numeric — letters should be stripped
      (.dispatchEvent slot (make-paste-event "1a2b3c4d"))
      (js/setTimeout
       (fn []
         (is (= "1234" (.-value el))
             "letters are stripped from pasted text")
         (done))
       0))))

(deftest paste-preserves-tail-after-cursor-test
  (async done
    (let [el        (append! (make-el))
          ^js slot2 (slot-at el 2)]
      (.setAttribute el model/attr-value "12345")
      (.focus slot2)
      (.dispatchEvent slot2 (make-paste-event "9"))
      (js/setTimeout
       (fn []
         (is (= "12945" (.-value el))
             "paste at slot 2 keeps head '12' and tail '45'")
         (done))
       0))))

(deftest paste-empty-after-filter-is-noop-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)
          seen     (atom 0)]
      (.setAttribute el model/attr-value "12")
      (.addEventListener el model/event-input
                         (fn [_ev] (swap! seen inc)))
      (.focus slot)
      ;; Numeric type — pasting only letters filters to empty; value unchanged
      (.dispatchEvent slot (make-paste-event "abc"))
      (js/setTimeout
       (fn []
         (is (= "12" (.-value el)) "value unchanged")
         (is (= 0 @seen) "no input event when nothing changed")
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; SMS autofill / multi-char input distribution
;; ---------------------------------------------------------------------------

(deftest sms-autofill-distributes-into-slots-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)]
      ;; SMS autofill writes the entire code into slot 0 in one input event,
      ;; bypassing maxlength=1. The component must distribute it across slots.
      (.focus slot)
      (set! (.-value slot) "123456")
      (.dispatchEvent slot (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (= "123456" (.-value el))
             "host value reflects the full autofilled code")
         (is (= "1" (.-value (slot-at el 0))))
         (is (= "6" (.-value (slot-at el 5))))
         (done))
       0))))

(deftest sms-autofill-fires-complete-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)
          seen     (atom nil)]
      (.setAttribute el model/attr-name "otp")
      (.addEventListener el model/event-complete
                         (fn [^js ev]
                           (reset! seen {:name  (.-name (.-detail ev))
                                         :value (.-value (.-detail ev))})))
      (.focus slot)
      (set! (.-value slot) "123456")
      (.dispatchEvent slot (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen) "complete event fires when autofill fills the field")
         (is (= "otp"    (:name @seen)))
         (is (= "123456" (:value @seen)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest input-event-test
  (async done
    (let [el       (append! (make-el))
          ^js slot (slot-at el 0)
          seen     (atom nil)]
      (.setAttribute el model/attr-name "code")
      (.addEventListener
       el model/event-input
       (fn [^js ev]
         (reset! seen {:name     (.-name (.-detail ev))
                       :value    (.-value (.-detail ev))
                       :complete (.-complete (.-detail ev))})))
      (set! (.-value slot) "5")
      (.dispatchEvent slot (js/Event. "input" #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (some? @seen))
         (is (= "code"  (:name @seen)))
         (is (= "5"     (:value @seen)))
         (is (= false   (:complete @seen)))
         (done))
       0))))

(deftest complete-event-test
  (async done
    (let [el     (append! (make-el))
          seen   (atom nil)]
      (.setAttribute el model/attr-length "3")
      (.setAttribute el model/attr-name "otp")
      (.addEventListener
       el model/event-complete
       (fn [^js ev]
         (reset! seen {:name  (.-name (.-detail ev))
                       :value (.-value (.-detail ev))})))
      ;; Type into each slot one by one to trigger full fill
      (let [s0 (slot-at el 0) s1 (slot-at el 1) s2 (slot-at el 2)]
        (set! (.-value s0) "1")
        (.dispatchEvent s0 (js/Event. "input" #js {:bubbles true}))
        (set! (.-value s1) "2")
        (.dispatchEvent s1 (js/Event. "input" #js {:bubbles true}))
        (set! (.-value s2) "3")
        (.dispatchEvent s2 (js/Event. "input" #js {:bubbles true})))
      (js/setTimeout
       (fn []
         (is (some? @seen)         "complete event should fire when all slots filled")
         (is (= "otp" (:name @seen)))
         (is (= "123" (:value @seen)))
         (done))
       0))))

;; ---------------------------------------------------------------------------
;; Validity
;; ---------------------------------------------------------------------------

(deftest required-empty-fails-validity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    (is (= false (.checkValidity el))
        "required + empty fails validation")))

(deftest required-partial-fails-validity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    (.setAttribute el model/attr-value "12")
    (is (= false (.checkValidity el))
        "required + partial fill fails validation")))

(deftest required-full-passes-validity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-required "")
    (.setAttribute el model/attr-value "123456")
    (is (= true (.checkValidity el))
        "required + full fill passes validation")))

(deftest error-attribute-fails-validity-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-error "Server says nope")
    (is (= false (.checkValidity el))
        "error attribute makes validity fail (customError)")))

;; ---------------------------------------------------------------------------
;; Form integration (formResetCallback)
;; ---------------------------------------------------------------------------

(deftest form-reset-clears-value-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "1234")
    (when-let [^js cb (.-formResetCallback el)]
      (.call cb el))
    (is (= "" (.-value el)))))

(deftest form-disabled-callback-syncs-attribute-test
  (let [el (append! (make-el))]
    (when-let [^js cb (.-formDisabledCallback el)]
      (.call cb el true))
    (is (.hasAttribute el model/attr-disabled))
    (when-let [^js cb (.-formDisabledCallback el)]
      (.call cb el false))
    (is (not (.hasAttribute el model/attr-disabled)))))
