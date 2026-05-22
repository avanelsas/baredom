(ns baredom.components.x-calendar.x-calendar-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-calendar.x-calendar :as x]
            [baredom.components.x-calendar.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el s]
  (.querySelector (.-shadowRoot el) s))

(defn shadow-all [^js el s]
  (.querySelectorAll (.-shadowRoot el) s))

(defn ^js day-cell [^js el iso]
  (.querySelector (.-shadowRoot el) (str "[data-iso=\"" iso "\"]")))

(defn key-down! [^js el key]
  (.dispatchEvent (shadow-part el "[part=grid]")
                  (js/KeyboardEvent. "keydown"
                                     #js {:key key :bubbles true :cancelable true})))

(defn calendar-key-down! [^js el key]
  (.dispatchEvent (shadow-part el "[part=calendar]")
                  (js/KeyboardEvent. "keydown"
                                     #js {:key key :bubbles true :cancelable true})))

(defn week-num-texts [^js el]
  (let [^js nl (shadow-all el "[part=weeknum]")]
    (mapv (fn [i] (.-textContent (.item nl i)))
          (range (.-length nl)))))

;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "Custom element is registered"))

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                       "shadow root present")
    (is (some? (shadow-part el "[part=calendar]"))      "calendar part")
    (is (some? (shadow-part el "[part=header]"))        "header part")
    (is (some? (shadow-part el "[part=monthlabel]"))    "monthlabel part")
    (is (some? (shadow-part el "[data-dir=prev]"))      "prev nav button")
    (is (some? (shadow-part el "[data-dir=next]"))      "next nav button")
    (is (some? (shadow-part el "[part=jump]"))          "jump panel part")
    (is (some? (shadow-part el "[part=weekdays]"))      "weekdays part")
    (is (some? (shadow-part el "[part=grid]"))          "grid part")))

(deftest renders-42-day-cells-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (is (= 42 (.-length (shadow-all el "[part=day]")))
        "calendar grid always renders 42 day cells")))

(deftest weekday-header-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-locale "en-US")
    (let [days (shadow-all el "[part=weekday]")]
      (is (= 7 (.-length days)) "seven weekday headers")
      (is (= "Sun" (.-textContent (.item days 0)))
          "Sunday-start by default"))))

(deftest first-day-of-week-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-locale "en-US")
    (.setAttribute el model/attr-first-day-of-week "monday")
    (is (= "Mon" (.-textContent (.item (shadow-all el "[part=weekday]") 0)))
        "Monday-start rotates the weekday header")))

(deftest single-select-click-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.click (day-cell el "2026-05-15"))
    (is (= "2026-05-15" (.getAttribute el model/attr-value))
        "clicking a day sets the value attribute")
    (is (= "true" (.getAttribute (day-cell el "2026-05-15") "data-selected"))
        "the selected day cell is marked")))

(deftest value-reflects-as-property-test
  (let [el (append! (make-el))]
    (set! (.-value el) "2026-03-09")
    (is (= "2026-03-09" (.getAttribute el model/attr-value)))
    (is (= "true" (.getAttribute (day-cell el "2026-03-09") "data-selected")))))

(deftest range-select-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mode "range")
    (.setAttribute el model/attr-month "2026-05")
    (testing "first click sets start"
      (.click (day-cell el "2026-05-10"))
      (is (= "2026-05-10" (.getAttribute el model/attr-start)))
      (is (nil? (.getAttribute el model/attr-end))))
    (testing "second click sets end"
      (.click (day-cell el "2026-05-20"))
      (is (= "2026-05-10" (.getAttribute el model/attr-start)))
      (is (= "2026-05-20" (.getAttribute el model/attr-end))))
    (testing "interior days are marked in-range"
      (is (= "true" (.getAttribute (day-cell el "2026-05-15") "data-in-range"))))))

(deftest month-navigation-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.click (shadow-part el "[data-dir=next]"))
    (is (= "2026-06" (.getAttribute el model/attr-month))
        "next advances the displayed month")
    (.click (shadow-part el "[data-dir=prev]"))
    (is (= "2026-05" (.getAttribute el model/attr-month))
        "prev steps the displayed month back")))

(deftest navigate-event-test
  (let [el      (append! (make-el))
        seen    (atom nil)]
    (.setAttribute el model/attr-month "2026-05")
    (.addEventListener el model/event-navigate
                       (fn [^js e] (reset! seen (.. e -detail -month))))
    (.click (shadow-part el "[data-dir=next]"))
    (is (= "2026-06" @seen) "x-calendar-navigate fires with the new month")))

(deftest keyboard-navigation-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2026-05-15")
    (key-down! el "ArrowRight")
    (is (= "2026-05-16" (.getAttribute (shadow-part el "[tabindex=\"0\"]") "data-iso"))
        "ArrowRight moves roving focus one day forward")
    (key-down! el "ArrowDown")
    (is (= "2026-05-23" (.getAttribute (shadow-part el "[tabindex=\"0\"]") "data-iso"))
        "ArrowDown moves roving focus one week forward")))

(deftest keyboard-select-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2026-05-15")
    (key-down! el "ArrowRight")
    (key-down! el "Enter")
    (is (= "2026-05-16" (.getAttribute el model/attr-value))
        "Enter selects the focused day")))

(deftest keyboard-crosses-month-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-value "2026-05-31")
    (key-down! el "ArrowRight")
    (is (= "2026-06" (.getAttribute el model/attr-month))
        "navigating past month end shifts the displayed month")))

(deftest min-max-disables-cells-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-min "2026-05-10")
    (.setAttribute el model/attr-max "2026-05-20")
    (is (= "true" (.getAttribute (day-cell el "2026-05-05") "data-disabled"))
        "day before min is disabled")
    (is (= "true" (.getAttribute (day-cell el "2026-05-25") "data-disabled"))
        "day after max is disabled")
    (is (= "false" (.getAttribute (day-cell el "2026-05-15") "data-disabled"))
        "day within bounds is enabled")))

(deftest disabled-dates-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-disabled-dates "2026-05-12 2026-05-13")
    (is (= "true" (.getAttribute (day-cell el "2026-05-12") "data-disabled")))
    (is (= "true" (.getAttribute (day-cell el "2026-05-13") "data-disabled")))
    (testing "clicking a disabled date does not select it"
      (.click (day-cell el "2026-05-12"))
      (is (nil? (.getAttribute el model/attr-value))))))

(deftest week-numbers-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (is (zero? (.-length (shadow-all el "[part=weeknum]")))
        "no week-number cells by default")
    (.setAttribute el model/attr-show-week-numbers "")
    (is (= 6 (.-length (shadow-all el "[part=weeknum]")))
        "six week-number cells when enabled")))

(deftest quick-jump-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (let [^js jump (shadow-part el "[part=jump]")]
      (is (.hasAttribute jump "hidden") "jump panel hidden initially")
      (.click (shadow-part el "[part=monthlabel]"))
      (is (not (.hasAttribute jump "hidden")) "clicking the label opens the panel")
      (.click (shadow-part el "[data-month-index=\"0\"]"))
      (is (= "2026-01" (.getAttribute el model/attr-month))
          "picking a month jumps the calendar")
      (is (.hasAttribute jump "hidden") "panel closes after a jump"))))

(deftest quick-jump-current-month-closes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (let [^js jump (shadow-part el "[part=jump]")]
      (.click (shadow-part el "[part=monthlabel]"))
      (is (not (.hasAttribute jump "hidden")) "panel opens")
      ;; May is month index 4 — the already-displayed month. Picking it is a
      ;; no-op attribute write, so the panel must close without a re-render.
      (.click (shadow-part el "[data-month-index=\"4\"]"))
      (is (.hasAttribute jump "hidden")
          "picking the already-displayed month still closes the panel"))))

(deftest quick-jump-escape-closes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (let [^js jump (shadow-part el "[part=jump]")]
      (.click (shadow-part el "[part=monthlabel]"))
      (is (not (.hasAttribute jump "hidden")) "panel opens")
      (calendar-key-down! el "Escape")
      (is (.hasAttribute jump "hidden") "Escape closes the quick-jump panel"))))

(deftest navigate-event-not-fired-when-month-unchanged-test
  (let [el    (append! (make-el))
        count (atom 0)]
    (.setAttribute el model/attr-month "2026-05")
    (.addEventListener el model/event-navigate (fn [_] (swap! count inc)))
    (.click (shadow-part el "[part=monthlabel]"))
    (.click (shadow-part el "[data-month-index=\"4\"]"))
    (is (zero? @count)
        "jumping to the already-displayed month fires no x-calendar-navigate")))

(deftest range-external-start-completes-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-mode "range")
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-start "2026-05-10")
    (testing "a click after an externally-set start completes the range"
      (.click (day-cell el "2026-05-20"))
      (is (= "2026-05-10" (.getAttribute el model/attr-start)))
      (is (= "2026-05-20" (.getAttribute el model/attr-end))))))

(deftest disabled-calendar-has-no-tabbable-cell-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-disabled "")
    (is (nil? (shadow-part el "[part=day][tabindex=\"0\"]"))
        "a disabled calendar exposes no tab-reachable day cell")))

(deftest invalid-locale-does-not-crash-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-locale "en_US")
    (is (= 42 (.-length (shadow-all el "[part=day]")))
        "a malformed locale falls back to the default instead of throwing")))

(deftest week-numbers-first-day-independent-test
  (let [sun (append! (make-el))
        mon (append! (make-el))]
    (doseq [^js el [sun mon]]
      (.setAttribute el model/attr-month "2026-05")
      (.setAttribute el model/attr-show-week-numbers ""))
    (.setAttribute mon model/attr-first-day-of-week "monday")
    (is (= (week-num-texts sun) (week-num-texts mon))
        "ISO week numbers are identical regardless of first-day-of-week")))

(deftest change-event-test
  (let [el   (append! (make-el))
        seen (atom nil)]
    (.setAttribute el model/attr-month "2026-05")
    (.addEventListener el model/event-change
                       (fn [^js e] (reset! seen (.-detail e))))
    (.click (day-cell el "2026-05-08"))
    (is (some? @seen) "x-calendar-change fires on selection")
    (is (= "2026-05-08" (.-value @seen)) "detail carries the selected value")
    (is (= "single" (.-mode @seen)) "detail carries the mode")))

(deftest disabled-calendar-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-month "2026-05")
    (.setAttribute el model/attr-disabled "")
    (.click (day-cell el "2026-05-15"))
    (is (nil? (.getAttribute el model/attr-value))
        "a disabled calendar ignores day clicks")
    (is (= "true" (.getAttribute el "aria-disabled"))
        "host is marked aria-disabled")))

(deftest go-to-month-method-test
  (let [el (append! (make-el))]
    (.goToMonth el "2026-09")
    (is (= "2026-09" (.getAttribute el model/attr-month)))))

(deftest clear-method-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "2026-05-15")
    (.clear el)
    (is (nil? (.getAttribute el model/attr-value))
        "clear() removes the selection")))

(deftest today-marked-test
  (let [el        (append! (make-el))
        today     (js/Date. (js/Date.now))
        today-pad (fn [n] (if (< n 10) (str "0" n) (str n)))
        today-iso (str (.getUTCFullYear today)
                       "-" (today-pad (inc (.getUTCMonth today)))
                       "-" (today-pad (.getUTCDate today)))
        ^js cell  (day-cell el today-iso)]
    (is (some? cell) "today's cell is in the default view")
    (is (= "true" (.getAttribute cell "data-today"))
        "today's cell is flagged")))
