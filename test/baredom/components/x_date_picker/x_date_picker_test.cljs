(ns baredom.components.x-date-picker.x-date-picker-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [goog.object :as gobj]
            [baredom.components.x-date-picker.x-date-picker :as x]
            [baredom.components.x-date-picker.model :as model]))

(x/init!)

(defn cleanup-dom!
  []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el
  []
  (.createElement js/document model/tag-name))

(defn ^js append!
  [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part
  [^js el s]
  (.querySelector (.-shadowRoot el) s))

;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "Custom element should be registered"))

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))            "Shadow root present")
    (is (some? (shadow-part el "[part=container]"))  "container part")
    (is (some? (shadow-part el "[part=input]"))      "input part")
    (is (some? (shadow-part el "[part=btn]"))        "btn part")
    (is (some? (shadow-part el "[part=popover]"))    "popover part")
    (is (some? (shadow-part el "[part=nav]"))        "nav part")
    (is (some? (shadow-part el "[part=monthlabel]")) "monthlabel part")
    (is (some? (shadow-part el "[part=weekdays]"))   "weekdays part")
    (is (some? (shadow-part el "[part=grid]"))       "grid part")))

(deftest mode-default-test
  (let [el (append! (make-el))]
    (is (nil? (.getAttribute el model/attr-mode))
        "mode attr absent by default")
    ;; mode property returns nil (not "single") when not set
    (is (nil? (.-mode el))
        "mode property returns nil when not set")))

(deftest mode-property-test
  (let [el (append! (make-el))]
    (set! (.-mode el) "range")
    (is (= "range" (.getAttribute el model/attr-mode)))
    (set! (.-mode el) "single")
    (is (= "single" (.getAttribute el model/attr-mode)))))

(deftest value-attr-sets-display-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2024-06-15")
    (let [^js inp (shadow-part el "[part=input]")]
      (is (= "2024-06-15" (.-value inp))
          "Input displays ISO value when format not set"))))

(deftest value-property-test
  (let [el (append! (make-el))]
    (set! (.-value el) "2024-03-01")
    (is (= "2024-03-01" (.getAttribute el model/attr-value)))
    (is (= "2024-03-01" (.-value el)))))

(deftest start-end-properties-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mode "range")
    (set! (.-start el) "2024-01-01")
    (set! (.-end el) "2024-01-31")
    (is (= "2024-01-01" (.getAttribute el model/attr-start)))
    (is (= "2024-01-31" (.getAttribute el model/attr-end)))))

(deftest open-property-test
  (let [el (append! (make-el))]
    (is (= false (.-open el)) "Initially closed")
    (set! (.-open el) true)
    (is (= true (.-open el)))
    (is (.hasAttribute el "open"))
    (set! (.-open el) false)
    (is (not (.hasAttribute el "open")))))

(deftest open-shows-popover-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js pop (shadow-part el "[part=popover]")]
      (is (some? pop)))))

(deftest calendar-nav-btn-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js prev (shadow-part el "[part=navbtn][data-nav=prev]")
          ^js next (shadow-part el "[part=navbtn][data-nav=next]")
          ^js lbl  (shadow-part el "[part=monthlabel]")]
      (is (some? prev) "prev nav button present")
      (is (some? next) "next nav button present")
      (let [initial-text (.-textContent lbl)]
        (.click next)
        (let [next-text (.-textContent lbl)]
          (is (not= initial-text next-text) "Month label changes on next nav"))))))

(deftest calendar-grid-42-cells-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js grid (shadow-part el "[part=grid]")
          ^js days (.querySelectorAll grid "[part=day]")]
      (is (= 42 (alength days)) "Grid always contains 42 day cells"))))

(deftest weekday-headers-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js wd   (shadow-part el "[part=weekdays]")
          ^js hdrs (.querySelectorAll wd "[part=weekday]")]
      (is (= 7 (alength hdrs)) "7 weekday headers"))))

(deftest day-selection-single-test
  (async done
    (let [el    (append! (make-el))
          fired (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js e] (reset! fired (.-detail e))))
      (.setAttribute el "open" "")
      (let [^js grid (shadow-part el "[part=grid]")
            ;; find a day in the current month that is not disabled
            ^js day  (.querySelector grid "[part=day][data-outside=false][data-disabled=false]")]
        (when day
          (let [iso (.getAttribute day "data-iso")]
            (.click day)
            (js/setTimeout
             (fn []
               (is (= iso (.getAttribute el model/attr-value))
                   "Clicking a day sets value attr")
               (is (some? @fired) "change event fired")
               (done))
             0)))
        (when-not day
          ;; No clickable day found — just done
          (done)))))  )

(deftest day-selection-range-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-mode "range")
      (.setAttribute el "open" "")
      (let [^js grid (shadow-part el "[part=grid]")
            days     (.querySelectorAll grid "[part=day][data-outside=false][data-disabled=false]")
            ^js day1 (when (>= (alength days) 2) (aget days 0))]
        (if day1
          (do
            (.click day1)
            ;; Re-query: render! rebuilds grid children after first click
            (let [days2    (.querySelectorAll grid "[part=day][data-outside=false][data-disabled=false]")
                  ^js day2 (when (>= (alength days2) 2) (aget days2 1))]
              (if day2
                (do
                  (.click day2)
                  (js/setTimeout
                   (fn []
                     (is (some? (.getAttribute el model/attr-start)) "start attr set after two clicks")
                     (is (some? (.getAttribute el model/attr-end))   "end attr set after two clicks")
                     (done))
                   0))
                (done))))
          (done))))))

(deftest disabled-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (let [^js inp (shadow-part el "[part=input]")]
      (is (.-disabled inp) "input disabled when element is disabled"))))

(deftest clear-method-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2024-06-15")
    (.clear el)
    (is (nil? (.getAttribute el model/attr-value)) "clear() removes value attr")))

(deftest btn-toggles-open-test
  (let [el  (append! (make-el))
        ^js btn (shadow-part el "[part=btn]")]
    (is (not (.hasAttribute el "open")) "initially closed")
    (.click btn)
    (is (.hasAttribute el "open") "btn click opens popover")
    (.click btn)
    (is (not (.hasAttribute el "open")) "second btn click closes popover")))

(deftest iso->date-roundtrip-test
  (testing "model-level: iso->date followed by date->iso is identity"
    (let [dates ["2024-01-01" "2024-02-29" "2024-12-31" "2026-03-18"]]
      (doseq [iso dates]
        (is (= iso (model/date->iso (model/iso->date iso)))
            (str "Round-trip for " iso))))))

(deftest change-request-cancelable-test
  (let [el    (append! (make-el))]
    (.setAttribute el "open" "")
    (.addEventListener el model/event-change-request
                       (fn [^js e] (.preventDefault e)))
    (let [^js grid (shadow-part el "[part=grid]")
          ^js day  (.querySelector grid "[part=day][data-outside=false][data-disabled=false]")]
      (when day (.click day))
      (is (nil? (.getAttribute el model/attr-value))
          "value should not be set when change-request is cancelled"))))

;; ---------------------------------------------------------------------------
;; ARIA tests
;; ---------------------------------------------------------------------------

(deftest input-combobox-aria-test
  (let [el (append! (make-el))
        ^js inp (shadow-part el "[part=input]")]
    (is (= "combobox" (.getAttribute inp "role"))
        "input has role=combobox")
    (is (= "dialog" (.getAttribute inp "aria-haspopup"))
        "input has aria-haspopup=dialog")
    (is (= "false" (.getAttribute inp "aria-expanded"))
        "aria-expanded is false when closed")
    (.setAttribute el "open" "")
    (is (= "true" (.getAttribute inp "aria-expanded"))
        "aria-expanded is true when open")))

(deftest day-cell-aria-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js grid (shadow-part el "[part=grid]")
          ^js day  (.querySelector grid "[part=day]")]
      (is (= "gridcell" (.getAttribute day "role"))
          "day cell has role=gridcell")
      (is (some? (.getAttribute day "aria-selected"))
          "day cell has aria-selected")
      (is (some? (.getAttribute day "aria-disabled"))
          "day cell has aria-disabled"))))

;; ---------------------------------------------------------------------------
;; Readonly test
;; ---------------------------------------------------------------------------

(deftest readonly-prevents-selection-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-readonly "")
    (.setAttribute el "open" "")
    (let [^js grid (shadow-part el "[part=grid]")
          ^js day  (.querySelector grid "[part=day][data-outside=false][data-disabled=false]")]
      (when day (.click day))
      (is (nil? (.getAttribute el model/attr-value))
          "clicking a day does not set value when readonly"))))

;; ---------------------------------------------------------------------------
;; Keyboard navigation tests
;; ---------------------------------------------------------------------------

(deftest grid-keyboard-escape-test
  (let [el (append! (make-el))]
    (.setAttribute el "open" "")
    (let [^js grid (shadow-part el "[part=grid]")
          ^js day  (.querySelector grid "[part=day][data-outside=false]")]
      (when day
        (.focus day)
        (.dispatchEvent day (js/KeyboardEvent. "keydown"
                            #js {:key "Escape" :bubbles true}))
        (is (not (.hasAttribute el "open"))
            "Escape closes the calendar")))))

(deftest grid-keyboard-enter-selects-test
  (async done
    (let [el    (append! (make-el))
          fired (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js e] (reset! fired (.-detail e))))
      (.setAttribute el "open" "")
      ;; Wait for render to complete
      (js/setTimeout
       (fn []
         (let [^js grid (shadow-part el "[part=grid]")
               ;; Find the cell with tabindex=0 (focused by open-popover!)
               ^js day  (.querySelector grid "[tabindex=\"0\"]")]
           (if day
             (let [iso (.getAttribute day "data-iso")]
               (.dispatchEvent day (js/KeyboardEvent. "keydown"
                                   #js {:key "Enter" :bubbles true}))
               (js/setTimeout
                (fn []
                  (is (= iso (.getAttribute el model/attr-value))
                      "Enter selects the focused date")
                  (is (some? @fired) "change event fired on Enter")
                  (done))
                0))
             (done))))
       0))))

(deftest event-detail-single-mode-test
  (async done
    (let [el    (append! (make-el))
          fired (atom nil)]
      (.addEventListener el model/event-change
                         (fn [^js e] (reset! fired (.-detail e))))
      (.setAttribute el "open" "")
      (let [^js grid (shadow-part el "[part=grid]")
            ^js day  (.querySelector grid "[part=day][data-outside=false][data-disabled=false]")]
        (when day
          (let [iso (.getAttribute day "data-iso")]
            (.click day)
            (js/setTimeout
             (fn []
               (is (some? @fired) "change event fired")
               (when @fired
                 (is (= iso (gobj/get @fired "value"))
                     "detail.value is the selected ISO date")
                 (is (= "single" (gobj/get @fired "mode"))
                     "detail.mode is 'single'")
                 (is (= "click" (gobj/get @fired "reason"))
                     "detail.reason is 'click'"))
               (done))
             0)))
        (when-not day (done))))))
