(ns baredom.components.x-stepper.x-stepper-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-stepper.x-stepper :as x]
            [baredom.components.x-stepper.model     :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn ^js shadow-parts [^js el selector]
  (.querySelectorAll (.-shadowRoot el) selector))

(defn ^js step-part [^js step-el selector]
  ;; Query within a step div (not the shadow root)
  (.querySelector step-el selector))

(defn steps-json [& labels]
  (js/JSON.stringify
   (clj->js (map (fn [l] {:label l}) labels))))

;; ── Registration ──────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=container]")))
    (is (= 3 (.-length (shadow-parts el "[part=step]"))))
    (is (some? (shadow-part el "[part=step-indicator]")))
    (is (some? (shadow-part el "[part=step-track]")))
    (is (some? (shadow-part el "[part=step-content]")))
    (is (some? (shadow-part el "[part=step-label]")))
    (is (some? (shadow-part el "[part=step-connector]")))))

;; ── Default data-orientation ──────────────────────────────────────────────────
(deftest default-orientation-attribute-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.getAttribute el "data-orientation")))))

;; ── steps attribute (integer) ─────────────────────────────────────────────────
(deftest steps-integer-renders-nodes-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "4")
    (is (= 4 (.-length (shadow-parts el "[part=step]"))))))

;; ── steps attribute (JSON) ────────────────────────────────────────────────────
(deftest steps-json-renders-labels-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps (steps-json "Account" "Details" "Confirm"))
    (let [labels (shadow-parts el "[part=step-label]")]
      (is (= 3 (.-length labels)))
      (is (= "Account" (.-textContent (aget labels 0))))
      (is (= "Details" (.-textContent (aget labels 1))))
      (is (= "Confirm" (.-textContent (aget labels 2)))))))

;; ── step states ───────────────────────────────────────────────────────────────
(deftest step-states-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (.setAttribute el model/attr-current "1")
    (let [steps (shadow-parts el "[part=step]")]
      (is (= "complete" (.getAttribute (aget steps 0) "data-state")))
      (is (= "current"  (.getAttribute (aget steps 1) "data-state")))
      (is (= "upcoming" (.getAttribute (aget steps 2) "data-state"))))))

;; ── current step aria-current ─────────────────────────────────────────────────
(deftest current-step-aria-current-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (.setAttribute el model/attr-current "1")
    (let [steps   (shadow-parts el "[part=step]")
          ^js cur (aget steps 1)
          ^js btn (step-part cur "[part=step-indicator]")]
      (is (= "step" (.getAttribute btn "aria-current")))
      (is (nil? (.getAttribute (step-part (aget steps 0) "[part=step-indicator]") "aria-current"))))))

;; ── indicator content (checkmark vs number) ───────────────────────────────────
(deftest indicator-content-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (.setAttribute el model/attr-current "1")
    (let [steps (shadow-parts el "[part=step]")
          ^js n0 (step-part (aget steps 0) "[part=step-number]")
          ^js n1 (step-part (aget steps 1) "[part=step-number]")
          ^js n2 (step-part (aget steps 2) "[part=step-number]")]
      (is (= "✓" (.-textContent n0)))
      (is (= "2" (.-textContent n1)))
      (is (= "3" (.-textContent n2))))))

;; ── description shown/hidden ──────────────────────────────────────────────────
(deftest description-visible-when-present-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps
                   (js/JSON.stringify
                    (clj->js [{:label "A" :description "desc text"}
                               {:label "B"}])))
    (let [descs (shadow-parts el "[part=step-description]")]
      (is (= "block" (.. (aget descs 0) -style -display)))
      (is (= "none"  (.. (aget descs 1) -style -display))))))

;; ── connector hidden on last step ─────────────────────────────────────────────
;; Note: CSS hides it via :last-child — we test the part exists on non-last steps.
(deftest connector-exists-on-all-steps-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "2")
    (is (= 2 (.-length (shadow-parts el "[part=step-connector]"))))))

;; ── orientation attribute ─────────────────────────────────────────────────────
(deftest vertical-orientation-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-orientation "vertical")
    (is (= "vertical" (.getAttribute el "data-orientation")))))

;; ── size attribute ────────────────────────────────────────────────────────────
(deftest size-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "sm")
    (is (= "sm" (.getAttribute el "data-size")))
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute el "data-size")))))

;; ── disabled state ────────────────────────────────────────────────────────────
(deftest disabled-buttons-have-tabindex-minus-one-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (.setAttribute el model/attr-disabled "")
    (let [btns (shadow-parts el "[part=step-indicator]")]
      (is (= "-1" (.getAttribute (aget btns 0) "tabindex")))
      (is (= "-1" (.getAttribute (aget btns 2) "tabindex"))))))

(deftest disabled-buttons-have-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "3")
    (.setAttribute el model/attr-disabled "")
    (let [btns (shadow-parts el "[part=step-indicator]")]
      (is (= "true" (.getAttribute (aget btns 0) "aria-disabled")))
      (is (= "true" (.getAttribute (aget btns 1) "aria-disabled")))
      (is (= "true" (.getAttribute (aget btns 2) "aria-disabled"))))))

;; ── x-stepper-change event ────────────────────────────────────────────────────
(deftest change-event-fires-on-step-click-test
  (let [^js el  (append! (make-el))
        _       (.setAttribute el model/attr-steps "3")
        _       (.setAttribute el model/attr-current "0")
        events  (atom [])
        _       (.addEventListener el model/event-change
                                   (fn [^js e] (swap! events conj (.-detail e))))
        ;; Click the third step indicator (index 2)
        steps   (shadow-parts el "[part=step]")
        ^js btn (step-part (aget steps 2) "[part=step-indicator]")]
    (.click btn)
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (= 0 (.-from d)))
      (is (= 2 (.-to d))))))

(deftest change-event-updates-current-attribute-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-steps "3")
        _      (.setAttribute el model/attr-current "0")
        steps  (shadow-parts el "[part=step]")
        ^js btn (step-part (aget steps 1) "[part=step-indicator]")]
    (.click btn)
    (is (= "1" (.getAttribute el model/attr-current)))))

(deftest change-event-is-cancelable-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-steps "3")
        _      (.setAttribute el model/attr-current "0")
        _      (.addEventListener el model/event-change
                                   (fn [^js e] (.preventDefault e)))
        steps  (shadow-parts el "[part=step]")
        ^js btn (step-part (aget steps 2) "[part=step-indicator]")]
    (.click btn)
    ;; current should remain 0 since event was cancelled
    (is (= "0" (.getAttribute el model/attr-current)))))

(deftest click-current-step-fires-no-event-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-steps "3")
        _      (.setAttribute el model/attr-current "1")
        events (atom [])
        _      (.addEventListener el model/event-change
                                   (fn [e] (swap! events conj e)))
        steps  (shadow-parts el "[part=step]")
        ^js btn (step-part (aget steps 1) "[part=step-indicator]")]
    (.click btn)
    (is (= 0 (count @events)))))

(deftest disabled-stepper-fires-no-event-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-steps "3")
        _      (.setAttribute el model/attr-current "0")
        _      (.setAttribute el model/attr-disabled "")
        events (atom [])
        _      (.addEventListener el model/event-change
                                   (fn [e] (swap! events conj e)))
        steps  (shadow-parts el "[part=step]")
        ^js btn (step-part (aget steps 2) "[part=step-indicator]")]
    (.click btn)
    (is (= 0 (count @events)))))

;; ── Property accessors ────────────────────────────────────────────────────────
(deftest steps-property-test
  (let [^js el (append! (make-el))]
    (set! (.-steps el) "3")
    (is (= "3" (.getAttribute el model/attr-steps)))
    (is (= "3" (.-steps el)))))

(deftest current-property-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-steps "5")
    (set! (.-current el) 3)
    (is (= "3" (.getAttribute el model/attr-current)))
    (is (= 3 (.-current el)))))

(deftest orientation-property-test
  (let [^js el (append! (make-el))]
    (set! (.-orientation el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-orientation)))
    (is (= "vertical" (.-orientation el)))))

(deftest size-property-test
  (let [^js el (append! (make-el))]
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))
    (is (= "lg" (.-size el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (let [^js el (make-el)
        _      (.setAttribute el model/attr-steps "2")
        events (atom [])
        _      (.addEventListener el model/event-change
                                   (fn [e] (swap! events conj e)))]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [steps  (shadow-parts el "[part=step]")
          ^js btn (step-part (aget steps 1) "[part=step-indicator]")]
      (.click btn)
      (is (= 1 (count @events))))))
