(ns baredom.components.x-combobox.x-combobox-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-combobox.x-combobox :as x]
            [baredom.components.x-combobox.model      :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el
  "Creates an x-combobox with 4 country options."
  []
  (let [^js el (.createElement js/document model/tag-name)]
    (doseq [[v l] [["us" "United States"] ["uk" "United Kingdom"]
                   ["nl" "Netherlands"] ["de" "Germany"]]]
      (let [^js opt (.createElement js/document "option")]
        (.setAttribute opt "value" v)
        (set! (.-textContent opt) l)
        (.appendChild el opt)))
    el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=wrapper]")))
    (is (some? (shadow-part el "[part=input]")))
    (is (some? (shadow-part el "[part=clear]")))
    (is (some? (shadow-part el "[part=chevron]")))
    (is (some? (shadow-part el "[part=panel]")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el    (append! (make-el))
        ^js input (shadow-part el "[part=input]")
        ^js panel (shadow-part el "[part=panel]")]
    (is (= "combobox" (.getAttribute input "role")))
    (is (= "false"    (.getAttribute input "aria-expanded")))
    (is (= "list"     (.getAttribute input "aria-autocomplete")))
    (is (= "listbox"  (.getAttribute panel "role")))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Placeholder attribute ────────────────────────────────────────────────────
(deftest placeholder-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-placeholder "Pick a country")
    (is (= "Pick a country" (.-placeholder (shadow-part el "[part=input]"))))))

;; ── Value attribute shows selected label ─────────────────────────────────────
(deftest value-shows-label-test
  (async done
    (let [^js el (append! (make-el))]
      ;; Wait for slotchange to sync options
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "nl")
         ;; Trigger render by reading back
         (let [^js input (shadow-part el "[part=input]")]
           (is (= "Netherlands" (.-value input))))
         (done))
       50))))

;; ── Disabled state ───────────────────────────────────────────────────────────
(deftest disabled-blocks-open-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.show el)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest value-property-test
  (let [^js el (append! (make-el))]
    (set! (.-value el) "uk")
    (is (= "uk" (.getAttribute el model/attr-value)))
    (is (= "uk" (.-value el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest placement-property-test
  (let [^js el (append! (make-el))]
    (set! (.-placement el) "top-end")
    (is (= "top-end" (.getAttribute el model/attr-placement)))))

;; ── show() / hide() methods ─────────────────────────────────────────────────
(deftest show-method-test
  (let [^js el (append! (make-el))]
    (.show el)
    (is (.hasAttribute el model/attr-open))))

(deftest hide-method-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.hide el)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Open sets aria-expanded ──────────────────────────────────────────────────
(deftest open-sets-aria-expanded-test
  (let [^js el (append! (make-el))]
    (.show el)
    (is (= "true" (.getAttribute (shadow-part el "[part=input]") "aria-expanded")))))

;; ── Panel has unique id and aria-controls matches ────────────────────────────
(deftest panel-id-matches-aria-controls-test
  (let [^js el    (append! (make-el))
        ^js input (shadow-part el "[part=input]")
        ^js panel (shadow-part el "[part=panel]")]
    (is (= (.getAttribute panel "id")
           (.getAttribute input "aria-controls")))))

;; ── Toggle event fires ───────────────────────────────────────────────────────
(deftest toggle-event-fires-on-show-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! events conj (.-detail e))))
    (.show el)
    (is (= 1 (count @events)))
    (is (= true (.-open (first @events))))))

(deftest toggle-event-cancelable-test
  (let [^js el (append! (make-el))]
    (.addEventListener el model/event-toggle
                       (fn [^js e] (.preventDefault e)))
    (.show el)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Keyboard: Enter selects active option ────────────────────────────────────
(deftest enter-selects-option-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change
                            (fn [^js e] (swap! events conj (.-detail e))))
         (.show el)
         ;; Enter should select first option (idx 0)
         (.dispatchEvent (shadow-part el "[part=input]")
                         (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true}))
         (is (= 1 (count @events)))
         (is (= "us" (.-value (first @events))))
         (is (not (.hasAttribute el model/attr-open)))
         (done))
       50))))

;; ── Keyboard: Escape closes panel ────────────────────────────────────────────
(deftest escape-closes-panel-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.dispatchEvent (shadow-part el "[part=input]")
                    (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Keyboard: ArrowDown navigates ────────────────────────────────────────────
(deftest arrow-down-opens-when-closed-test
  (let [^js el (append! (make-el))]
    (.dispatchEvent (shadow-part el "[part=input]")
                    (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true}))
    (is (.hasAttribute el model/attr-open))))

;; ── Change event fires on selection ──────────────────────────────────────────
(deftest change-event-on-clear-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "us")
         (.addEventListener el model/event-change
                            (fn [^js e] (swap! events conj (.-detail e))))
         (.click (shadow-part el "[part=clear]"))
         (is (= 1 (count @events)))
         (is (= "" (.-value (first @events))))
         (done))
       50))))

;; ── Option click selection ────────────────────────────────────────────────────
(deftest option-click-selects-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change
                            (fn [^js e] (swap! events conj (.-detail e))))
         (.show el)
         (let [^js panel (shadow-part el "[part=panel]")
               ^js opts  (.querySelectorAll panel "[data-value]")]
           (.click (aget opts 1)))
         (is (= 1 (count @events)))
         (is (= "uk" (.-value (first @events))))
         (is (not (.hasAttribute el model/attr-open)))
         (done))
       50))))

;; ── Filtering: typing filters options ────────────────────────────────────────
(deftest typing-filters-options-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.show el)
         (let [^js input (shadow-part el "[part=input]")]
           (set! (.-value input) "united")
           (.dispatchEvent input (js/Event. "input" #js {:bubbles true})))
         (let [^js panel (shadow-part el "[part=panel]")
               ^js opts  (.querySelectorAll panel "[data-value]")]
           (is (= 2 (.-length opts))))
         (done))
       50))))

;; ── ArrowUp opens panel when closed ──────────────────────────────────────────
(deftest arrow-up-opens-when-closed-test
  (let [^js el (append! (make-el))]
    (.dispatchEvent (shadow-part el "[part=input]")
                    (js/KeyboardEvent. "keydown" #js {:key "ArrowUp" :bubbles true}))
    (is (.hasAttribute el model/attr-open))))

;; ── Home/End keys ────────────────────────────────────────────────────────────
(deftest home-key-jumps-to-first-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.show el)
         (let [^js input (shadow-part el "[part=input]")]
           ;; Move down twice, then Home
           (.dispatchEvent input (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true}))
           (.dispatchEvent input (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true}))
           (.dispatchEvent input (js/KeyboardEvent. "keydown" #js {:key "Home" :bubbles true})))
         (let [^js panel (shadow-part el "[part=panel]")
               ^js active (.querySelector panel "[data-active]")]
           (is (some? active))
           (is (= "us" (.getAttribute active "data-value"))))
         (done))
       50))))

(deftest end-key-jumps-to-last-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.show el)
         (.dispatchEvent (shadow-part el "[part=input]")
                         (js/KeyboardEvent. "keydown" #js {:key "End" :bubbles true}))
         (let [^js panel (shadow-part el "[part=panel]")
               ^js active (.querySelector panel "[data-active]")]
           (is (some? active))
           (is (= "de" (.getAttribute active "data-value"))))
         (done))
       50))))

;; ── focusout closes panel ────────────────────────────────────────────────────
(deftest focusout-closes-panel-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.dispatchEvent el (js/FocusEvent. "focusout" #js {:bubbles true :relatedTarget nil}))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Input reverts on close without selection ─────────────────────────────────
(deftest input-reverts-on-close-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "nl")
         (.show el)
         ;; Type something without selecting
         (let [^js input (shadow-part el "[part=input]")]
           (set! (.-value input) "xyz")
           (.dispatchEvent input (js/Event. "input" #js {:bubbles true})))
         ;; Close via Escape
         (.dispatchEvent (shadow-part el "[part=input]")
                         (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
         ;; Input should revert to selected label
         (is (= "Netherlands" (.-value (shadow-part el "[part=input]"))))
         (done))
       50))))

;; ── Placement attribute ──────────────────────────────────────────────────────
(deftest placement-reflects-to-panel-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-placement "top-start")
    (is (= "top-start" (.getAttribute (shadow-part el "[part=panel]") "data-placement")))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-double-events-test
  (let [^js el (make-el)
        events (atom [])]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.addEventListener el model/event-toggle
                       (fn [^js _e] (swap! events conj :toggle)))
    (.show el)
    (is (= 1 (count @events)))))

;; ── data-has-value drives clear button ───────────────────────────────────────
(deftest data-has-value-set-when-value-present-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "de")
         (is (.hasAttribute el "data-has-value"))
         (.removeAttribute el model/attr-value)
         (is (not (.hasAttribute el "data-has-value")))
         (done))
       50))))

;; ── Cancelable change-request ───────────────────────────────────────────────
(deftest enter-dispatches-change-request-test
  (async done
    (let [^js el (append! (make-el))
          seen   (atom nil)]
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change-request
           (fn [^js ev]
             (reset! seen {:value          (.-value (.-detail ev))
                           :previous-value (.-previousValue (.-detail ev))})))
         (.show el)
         (.dispatchEvent (shadow-part el "[part=input]")
                         (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true}))
         (is (some? @seen) "change-request event should fire")
         (is (= "us" (:value @seen)))
         (is (= ""   (:previous-value @seen)))
         (done))
       50))))

(deftest change-request-can-be-cancelled-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.addEventListener el model/event-change-request
           (fn [^js ev] (.preventDefault ev)))
         (.show el)
         (.dispatchEvent (shadow-part el "[part=input]")
                         (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true}))
         (is (nil? (.getAttribute el model/attr-value))
             "value should NOT be set when change-request is cancelled")
         (done))
       50))))
