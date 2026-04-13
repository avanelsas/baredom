(ns baredom.components.x-chip.x-chip-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-chip.x-chip :as x]
            [baredom.components.x-chip.model  :as model]))

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

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest idempotent-registration-test
  (x/init!)
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=label]")))
    (is (some? (shadow-part el "[part=remove]")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-removable-true-test
  "removable defaults to true when attribute absent"
  (let [^js el (append! (make-el))]
    (is (true? (.hasAttribute el "data-removable")))))

(deftest default-not-disabled-test
  (let [^js el (append! (make-el))]
    (is (false? (.hasAttribute el model/attr-disabled)))))

(deftest default-tabindex-removable-test
  "tabIndex should be 0 when removable and not disabled"
  (let [^js el (append! (make-el))]
    (is (= 0 (.-tabIndex el)))))

;; ── Label rendering ───────────────────────────────────────────────────────
(deftest label-text-rendered-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Clojure")
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (= "Clojure" (.-textContent lbl))))))

(deftest label-empty-default-test
  (let [^js el (append! (make-el))
        ^js lbl (shadow-part el "[part=label]")]
    (is (= "" (.-textContent lbl)))))

;; ── Removable attribute ───────────────────────────────────────────────────
(deftest removable-false-removes-data-attr-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-removable "false")
    (is (false? (.hasAttribute el "data-removable")))))

(deftest removable-zero-removes-data-attr-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-removable "0")
    (is (false? (.hasAttribute el "data-removable")))))

(deftest removable-absent-defaults-to-true-test
  (let [^js el (append! (make-el))]
    (.removeAttribute el model/attr-removable)
    (is (true? (.hasAttribute el "data-removable")))))

;; ── Disabled attribute ────────────────────────────────────────────────────
(deftest disabled-sets-tabindex-negative-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= -1 (.-tabIndex el)))))

(deftest disabled-removes-aria-keyshortcuts-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (nil? (.getAttribute el "aria-keyshortcuts")))))

(deftest disabled-disables-remove-button-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (let [^js btn (shadow-part el "[part=remove]")]
      (is (true? (.hasAttribute btn "disabled"))))))

(deftest enabled-enables-remove-button-test
  (let [^js el (append! (make-el))]
    (.removeAttribute el model/attr-disabled)
    (let [^js btn (shadow-part el "[part=remove]")]
      (is (false? (.hasAttribute btn "disabled"))))))

;; ── aria-keyshortcuts ─────────────────────────────────────────────────────
(deftest aria-keyshortcuts-set-when-eligible-test
  (let [^js el (append! (make-el))]
    (is (= "Backspace Delete" (.getAttribute el "aria-keyshortcuts")))))

(deftest aria-keyshortcuts-absent-when-not-removable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-removable "false")
    (is (nil? (.getAttribute el "aria-keyshortcuts")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest label-property-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "React")
    (is (= "React" (.getAttribute el model/attr-label)))))

(deftest value-property-test
  (let [^js el (append! (make-el))]
    (set! (.-value el) "react-tag")
    (is (= "react-tag" (.getAttribute el model/attr-value)))))

(deftest removable-bool-property-true-test
  (let [^js el (append! (make-el))]
    (set! (.-removable el) true)
    (is (true? (.hasAttribute el model/attr-removable)))))

(deftest removable-bool-property-false-test
  (let [^js el (append! (make-el))]
    (set! (.-removable el) false)
    (is (false? (.hasAttribute el model/attr-removable)))))

(deftest disabled-bool-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (true? (.hasAttribute el model/attr-disabled)))
    (set! (.-disabled el) false)
    (is (false? (.hasAttribute el model/attr-disabled)))))

;; ── Event dispatch ────────────────────────────────────────────────────────
(deftest remove-event-dispatched-on-button-click-test
  (async done
    (let [^js el     (append! (make-el))
          _          (.setAttribute el model/attr-label "Go")
          _          (.setAttribute el model/attr-value "go-lang")
          received   (atom nil)]
      (.addEventListener el model/event-remove
                         (fn [^js ev]
                           (reset! received ev)
                           (done)))
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest remove-event-detail-contains-value-and-label-test
  (async done
    (let [^js el (append! (make-el))
          _      (.setAttribute el model/attr-label "Go")
          _      (.setAttribute el model/attr-value "go-lang")]
      (.addEventListener el model/event-remove
                         (fn [^js ev]
                           (let [^js d (.-detail ev)]
                             (is (= "go-lang" (.-value d)))
                             (is (= "Go"      (.-label d))))
                           (done)))
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest remove-event-value-falls-back-to-label-test
  (async done
    (let [^js el (append! (make-el))
          _      (.setAttribute el model/attr-label "Rust")]
      (.addEventListener el model/event-remove
                         (fn [^js ev]
                           (let [^js d (.-detail ev)]
                             (is (= "Rust" (.-value d)))
                             (is (= "Rust" (.-label d))))
                           (done)))
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest remove-event-cancelable-test
  (async done
    (let [^js el   (append! (make-el))
          _        (.setAttribute el model/attr-label "Java")
          removed? (atom false)]
      ;; Prevent default to cancel removal
      (.addEventListener el model/event-remove
                         (fn [^js ev]
                           (.preventDefault ev)))
      ;; Listen for actual DOM removal
      (js/setTimeout
       (fn []
         ;; Element should still be in the DOM because removal was cancelled
         (is (some? (.-parentNode el)))
         (is (false? (.hasAttribute el "data-exiting")))
         (reset! removed? true)
         (done))
       100)
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest disabled-chip-does-not-dispatch-remove-test
  (async done
    (let [^js el    (append! (make-el))
          _         (.setAttribute el model/attr-disabled "")
          received? (atom false)]
      (.addEventListener el model/event-remove
                         (fn [_] (reset! received? true)))
      (js/setTimeout
       (fn []
         (is (false? @received?))
         (done))
       100)
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest non-removable-chip-does-not-dispatch-remove-test
  (async done
    (let [^js el    (append! (make-el))
          _         (.setAttribute el model/attr-removable "false")
          received? (atom false)]
      (.addEventListener el model/event-remove
                         (fn [_] (reset! received? true)))
      (js/setTimeout
       (fn []
         (is (false? @received?))
         (done))
       100)
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

(deftest remove-event-bubbles-test
  (async done
    (let [^js el    (append! (make-el))
          ^js body  (.-body js/document)
          received? (atom false)]
      (.setAttribute el model/attr-label "Bubble")
      (let [on-event (fn on-event [_]
                       (reset! received? true)
                       (.removeEventListener body model/event-remove on-event)
                       (is (true? @received?) "x-chip-remove bubbled to document.body")
                       (done))]
        (.addEventListener body model/event-remove on-event))
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

;; ── Exit animation ────────────────────────────────────────────────────────
(deftest exit-animation-sets-data-exiting-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-label "Exit me")
      (.addEventListener el model/event-remove
                         (fn [^js _ev]
                           ;; Check that data-exiting is set after event fires
                           (js/setTimeout
                            (fn []
                              (is (true? (.hasAttribute el "data-exiting")))
                              (done))
                            10)))
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))

;; ── Reconnect stability ───────────────────────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-label "Persistent")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (= "Persistent" (.-textContent lbl))))))

(deftest reconnect-no-duplicate-shadow-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (let [root1 (.-shadowRoot el)]
      (.remove el)
      (.appendChild (.-body js/document) el)
      (is (= root1 (.-shadowRoot el))))))

(deftest reconnect-event-fires-once-test
  "After reconnect, clicking remove should fire exactly one event."
  (async done
    (let [^js el    (make-el)
          count     (atom 0)]
      (.setAttribute el model/attr-label "Once")
      (.appendChild (.-body js/document) el)
      (.remove el)
      (.appendChild (.-body js/document) el)
      (.addEventListener el model/event-remove
                         (fn [_] (swap! count inc)))
      (js/setTimeout
       (fn []
         (is (= 1 @count))
         (done))
       100)
      (let [^js btn (shadow-part el "[part=remove]")]
        (.click btn)))))
