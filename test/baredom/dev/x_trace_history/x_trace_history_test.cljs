(ns baredom.dev.x-trace-history.x-trace-history-test
  "Integration tests for the dock element. Async because recorder→dock
   notifications are coalesced through requestAnimationFrame; tests wait
   one or two frames before asserting rendered DOM state."
  (:require
   [cljs.test :refer-macros [deftest is testing use-fixtures async]]
   [clojure.string]
   [goog.object :as gobj]
   [baredom.utils.dom :as du]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]
   [baredom.dev.x-trace-history.x-trace-history :as dock]))

;; ---------------------------------------------------------------------------
;; Setup
;; ---------------------------------------------------------------------------

;; Register the dock custom element + recorder hooks once. After this the
;; tag is defined for the rest of the test page.
(dock/register!)

(defn- after-fixture
  "Tear down between tests: remove any leftover dock element, clear the
   recorder buffer, ensure resume state, and uninstall hooks (the next test
   re-installs)."
  []
  (when-let [^js el (.querySelector js/document model/tag-name)]
    (.remove el))
  (recorder/uninstall!)
  (recorder/resume!)
  (recorder/clear!)
  (gobj/remove js/window "BAREDOM_TRACE_HISTORY"))

(defn- before-fixture
  "Prep: install hooks fresh for each test in forensic mode. Forensic
   mode disables the PR 9 sample-rate cap and defaults all category
   filters ON, so dock tests that assume every record lands and that
   :state records show up by default keep working. Tests that need
   normal-mode behavior (filter-default-state-off, rate-limit cap)
   override the flag inline."
  []
  (gobj/set js/window "BAREDOM_TRACE_HISTORY" "raw")
  (recorder/install!)
  (recorder/clear!))

(use-fixtures :each {:before before-fixture
                     :after  after-fixture})

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- mount-dock!
  "Create a fresh <x-trace-history> element and append it to body."
  []
  (let [^js el (.createElement js/document model/tag-name)]
    (.appendChild (.-body js/document) el)
    el))

(defn- after-frames
  "Run thunk f after `n` animation frames. Two frames is enough for any
   recorder-triggered rAF to fire and for the resulting render to flush."
  [n f]
  (if (zero? n)
    (f)
    (js/requestAnimationFrame (fn [] (after-frames (dec n) f)))))

(defn- ^js shadow-of [^js dock-el]
  (gobj/get dock-el model/k-shadow))

(defn- query [^js dock-el sel]
  (.querySelector (shadow-of dock-el) sel))

(defn- query-all [^js dock-el sel]
  (.querySelectorAll (shadow-of dock-el) sel))

(defn- dot-count [^js dock-el]
  (.-length (query-all dock-el "circle.dot")))

(defn- lane-count [^js dock-el]
  (.-length (query-all dock-el ".lane-label")))

(defn- dispatch-checkbox-change!
  "Simulate the CustomEvent x-checkbox fires when its checked state
   toggles. Keeps the host's `checked` attribute in sync so subsequent
   query / state reads observe the new value."
  [^js cb checked?]
  (if checked? (.setAttribute cb "checked" "") (.removeAttribute cb "checked"))
  (.dispatchEvent cb
                  (js/CustomEvent. "x-checkbox-change"
                                   #js {:bubbles true
                                        :composed true
                                        :detail #js {:value   (or (.getAttribute cb "value") "on")
                                                     :checked checked?}})))

(defn- dispatch-select-change!
  "Simulate the CustomEvent x-select fires after the user picks an
   option. Sets the host `value` first so subsequent reads observe
   the new value."
  [^js sel value]
  (set! (.-value sel) value)
  (.dispatchEvent sel
                  (js/CustomEvent. "select-change"
                                   #js {:bubbles true
                                        :composed true
                                        :detail #js {:value value
                                                     :label value}})))

;; ---------------------------------------------------------------------------
;; Mount + skeleton
;; ---------------------------------------------------------------------------

(deftest mount-creates-shadow-skeleton-test
  (testing "mounting <x-trace-history> attaches a shadow root with the dock skeleton"
    (let [^js el (mount-dock!)]
      (is (some? (shadow-of el)))
      (is (some? (query el ".dock")))
      (is (some? (query el "[data-x-th-timeline]")))
      (is (some? (query el "[data-x-th-lanes]")))
      (is (some? (query el "[data-x-th-svg-pane]")))
      (is (some? (query el "[data-x-th-tooltip]")))
      (is (some? (query el "[data-x-th-action='pause']")))
      (is (some? (query el "[data-x-th-action='clear']"))))))

(deftest empty-state-shows-placeholder-test
  (testing "with no records, the svg pane shows the empty hint and the lanes
            column is empty"
    (let [^js el (mount-dock!)]
      (is (some? (query el ".timeline-empty")))
      (is (zero? (lane-count el))))))

;; ---------------------------------------------------------------------------
;; Records → dots
;; ---------------------------------------------------------------------------

(deftest dispatch-renders-dot-test
  (testing "a single dispatch produces one dot in the timeline SVG"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {:value 7})
        (after-frames 2
          (fn []
            (is (= 1 (dot-count dock)))
            (is (= 1 (lane-count dock)))
            (let [^js label (query dock ".lane-label")]
              (is (clojure.string/includes? (.-textContent label) "x-button")))
            (done)))))))

(deftest multiple-events-one-lane-per-component-test
  (testing "events from the same instance share a lane; different instances
            get distinct lanes"
    (async done
      (let [^js dock  (mount-dock!)
            ^js a     (.createElement js/document "x-button")
            ^js b     (.createElement js/document "x-button")]
        (du/dispatch! a "click" #js {})
        (du/dispatch! a "click" #js {})
        (du/dispatch! b "click" #js {})
        (after-frames 2
          (fn []
            (is (= 3 (dot-count dock)))
            (is (= 2 (lane-count dock)))
            (done)))))))

(deftest count-reflects-record-total-test
  (testing "the count badge shows the filtered record total"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (dotimes [i 4]
          (du/dispatch! btn (str "ev-" i) #js {}))
        (after-frames 2
          (fn []
            (let [^js c (query dock "[data-x-th-count]")]
              (is (= "4" (.-textContent c))))
            (done)))))))

(deftest dot-color-reflects-category-test
  (testing "dot fill colour comes from the record's category palette"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "click" #js {})
        (du/setv!     btn "__x" "v")
        (after-frames 2
          (fn []
            (let [dots  (array-seq (query-all dock "circle.dot"))
                  fills (set (map (fn [^js d] (.getAttribute d "fill")) dots))]
              (is (contains? fills "#94e2d5") "events colour present")
              (is (contains? fills "#f9e2af") "state colour present"))
            (done)))))))

;; ---------------------------------------------------------------------------
;; Detail pane
;; ---------------------------------------------------------------------------

(deftest click-dot-opens-detail-test
  (testing "clicking a dot reveals the JSON detail pane for that record"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {:n 99})
        (after-frames 2
          (fn []
            (let [^js dot (query dock "circle.dot")]
              (.dispatchEvent dot (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (let [^js detail (query dock "[data-x-th-detail]")]
                  (is (false? (.hasAttribute detail "hidden")))
                  (is (clojure.string/includes? (.-textContent detail) "x-button:click"))
                  (is (clojure.string/includes? (.-textContent detail) "schemaVersion")))
                (done)))))))))

(deftest click-dot-twice-closes-detail-test
  (testing "clicking the same dot a second time hides the detail pane"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (.dispatchEvent ^js (query dock "circle.dot")
                            (js/MouseEvent. "click" #js {:bubbles true}))
            (after-frames 1
              (fn []
                ;; The render replaces the SVG content, so re-query the dot.
                (.dispatchEvent ^js (query dock "circle.dot")
                                (js/MouseEvent. "click" #js {:bubbles true}))
                (after-frames 1
                  (fn []
                    (let [^js detail (query dock "[data-x-th-detail]")]
                      (is (true? (.hasAttribute detail "hidden"))))
                    (done)))))))))))

(deftest splitter-visibility-tracks-detail-test
  (testing "splitter is hidden until a dot is selected, then visible while
            the detail pane is open, then hidden again on close"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (let [^js splitter (query dock "[data-x-th-splitter]")]
              (is (true? (.hasAttribute splitter "hidden")) "hidden when nothing selected")
              (.dispatchEvent ^js (query dock "circle.dot")
                              (js/MouseEvent. "click" #js {:bubbles true}))
              (after-frames 1
                (fn []
                  (is (false? (.hasAttribute splitter "hidden")) "visible after selection")
                  (.dispatchEvent ^js (query dock "circle.dot")
                                  (js/MouseEvent. "click" #js {:bubbles true}))
                  (after-frames 1
                    (fn []
                      (is (true? (.hasAttribute splitter "hidden")) "hidden after deselection")
                      (done))))))))))))

(deftest filter-clears-stale-selection-test
  (testing "selecting a dot, then filtering its category out, hides the detail"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (du/setv!     btn "__x" "v")
        (after-frames 2
          (fn []
            ;; Click the events-coloured dot.
            (let [events-dot (some (fn [^js d]
                                     (when (= "#94e2d5" (.getAttribute d "fill")) d))
                                   (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js events-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (is (false? (.hasAttribute ^js (query dock "[data-x-th-detail]") "hidden")))
                (let [^js cb (query dock "[data-x-th-cat='events']")]
                  (dispatch-checkbox-change! cb false))
                (after-frames 1
                  (fn []
                    (is (true? (.hasAttribute ^js (query dock "[data-x-th-detail]") "hidden")))
                    (done)))))))))))

;; ---------------------------------------------------------------------------
;; Tooltip
;; ---------------------------------------------------------------------------

(deftest hover-dot-shows-tooltip-test
  (testing "moving the pointer onto a dot shows the tooltip with the record's
            tag and event name; moving off hides it again"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {:n 7})
        (after-frames 2
          (fn []
            (let [^js dot     (query dock "circle.dot")
                  ^js tooltip (query dock "[data-x-th-tooltip]")]
              (is (true? (.hasAttribute tooltip "hidden")))
              (.dispatchEvent dot (js/PointerEvent. "pointermove"
                                                   #js {:bubbles true
                                                        :clientX 100
                                                        :clientY 100}))
              (is (false? (.hasAttribute tooltip "hidden")))
              (is (clojure.string/includes? (.-textContent tooltip) "x-button:click"))
              ;; pointerleave on the timeline container hides it.
              (let [^js timeline (query dock "[data-x-th-timeline]")]
                (.dispatchEvent timeline (js/PointerEvent. "pointerleave"
                                                          #js {:bubbles false})))
              (is (true? (.hasAttribute tooltip "hidden"))))
            (done)))))))

;; ---------------------------------------------------------------------------
;; Filters
;; ---------------------------------------------------------------------------

(deftest tag-filter-narrows-dots-test
  (testing "selecting a tag in the dropdown filters dots from other tags"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js mod  (.createElement js/document "x-modal")]
        (du/dispatch! btn "click" #js {})
        (du/dispatch! mod "open"  #js {})
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (is (= 3 (dot-count dock)))
            (is (= 2 (lane-count dock)))
            (dispatch-select-change! ^js (query dock "[data-x-th-tag]") "x-button")
            (after-frames 1
              (fn []
                (is (= 2 (dot-count dock)))
                (is (= 1 (lane-count dock)))
                (done)))))))))

(deftest click-lane-label-toggles-tag-filter-test
  (testing "clicking a lane label sets the tag filter to that lane's tag,
            updates the dropdown, and adds the .active class. Clicking
            the same lane again clears the filter."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js mod  (.createElement js/document "x-modal")]
        (du/dispatch! btn "click" #js {})
        (du/dispatch! mod "open"  #js {})
        (after-frames 2
          (fn []
            (is (= 2 (lane-count dock)))
            (let [labels    (array-seq (query-all dock ".lane-label"))
                  btn-label (some (fn [^js l]
                                    (when (= "x-button"
                                             (.getAttribute l "data-x-th-lane-tag"))
                                      l))
                                  labels)]
              (.click ^js btn-label))
            (after-frames 1
              (fn []
                (is (= 1 (lane-count dock)))
                (let [^js sel (query dock "[data-x-th-tag]")]
                  (is (= "x-button" (.-value sel))))
                (is (some? (query dock ".lane-label.active")))
                ;; Click the (now sole) active lane again → toggle off.
                (.click ^js (query dock ".lane-label.active"))
                (after-frames 1
                  (fn []
                    (is (= 2 (lane-count dock)))
                    (let [^js sel (query dock "[data-x-th-tag]")]
                      (is (= "all" (.-value sel))))
                    (is (nil? (query dock ".lane-label.active")))
                    (done)))))))))))

(deftest category-filter-narrows-dots-test
  (testing "unchecking a category checkbox hides records of that category"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "click" #js {})
        (du/setv!     btn "__x" "v")
        (after-frames 2
          (fn []
            (is (= 2 (dot-count dock)))
            (dispatch-checkbox-change! ^js (query dock "[data-x-th-cat='events']") false)
            (after-frames 1
              (fn []
                (is (= 1 (dot-count dock)))
                (done)))))))))

;; ---------------------------------------------------------------------------
;; Pause / clear buttons
;; ---------------------------------------------------------------------------

(deftest pause-button-toggles-recorder-test
  (testing "clicking the pause button calls recorder/pause! and updates label"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (query dock "[data-x-th-action='pause']")]
        (.click btn)
        (after-frames 2
          (fn []
            (is (true? (recorder/paused?)))
            (is (= "Resume" (.-textContent btn)))
            (.click btn)
            (after-frames 2
              (fn []
                (is (false? (recorder/paused?)))
                (is (= "Pause" (.-textContent btn)))
                (done)))))))))

(deftest clear-button-empties-timeline-test
  (testing "clicking the clear button empties the SVG and the count badge"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x" #js {})
        (du/dispatch! btn "y" #js {})
        (after-frames 2
          (fn []
            (is (= 2 (dot-count dock)))
            (let [^js clear-btn (query dock "[data-x-th-action='clear']")]
              (.click clear-btn))
            (after-frames 2
              (fn []
                (is (zero? (dot-count dock)))
                (let [^js c (query dock "[data-x-th-count]")]
                  (is (= "0" (.-textContent c))))
                (done)))))))))

;; ---------------------------------------------------------------------------
;; Subscriber semantics (verified end-to-end via the dock)
;; ---------------------------------------------------------------------------

(deftest unsubscribe-on-disconnect-test
  (testing "removing the dock element unsubscribes it from recorder updates"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "before" #js {})
        (after-frames 2
          (fn []
            (is (= 1 (dot-count dock)))
            (.remove dock)
            (is (nil? (gobj/get dock model/k-sub-token)))
            (du/dispatch! btn "after" #js {})
            (after-frames 2
              (fn []
                (is (= 2 (.-length (recorder/records))))
                (done)))))))))

;; ---------------------------------------------------------------------------
;; Scrubber + keyboard step (PR 6)
;; ---------------------------------------------------------------------------

(deftest scrubber-hidden-when-nothing-selected-test
  (testing "no <line.scrubber> rendered until a record is selected"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (is (nil? (query dock "line.scrubber")))
            (done)))))))

(deftest scrubber-appears-when-record-selected-test
  (testing "selecting a record renders a <line.scrubber> in the SVG"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (.dispatchEvent ^js (query dock "circle.dot")
                            (js/MouseEvent. "click" #js {:bubbles true}))
            (after-frames 1
              (fn []
                (is (some? (query dock "line.scrubber")))
                (done)))))))))

(deftest pointerdown-focuses-timeline-test
  (testing "pointerdown anywhere in the timeline focuses it so keydown
            fires for subsequent arrow-key presses"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x" #js {})
        (after-frames 2
          (fn []
            (let [^js shadow   (shadow-of dock)
                  ^js timeline (query dock "[data-x-th-timeline]")
                  ^js svg-pane (query dock "[data-x-th-svg-pane]")]
              (.dispatchEvent svg-pane
                              (js/PointerEvent. "pointerdown"
                                                #js {:bubbles true
                                                     :clientX 50
                                                     :clientY 10
                                                     :pointerId 1}))
              ;; Inside an open shadow root, document.activeElement returns
              ;; the host; the shadow root has its own activeElement.
              (is (= timeline (.-activeElement shadow))))
            (done)))))))

(deftest pointerdown-on-svg-pane-selects-nearest-test
  (testing "pointerdown on empty SVG space selects the nearest filtered record
            and shows the scrubber"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x" #js {})
        (du/dispatch! btn "y" #js {})
        (after-frames 2
          (fn []
            (let [^js svg-pane (query dock "[data-x-th-svg-pane]")]
              (.dispatchEvent svg-pane
                              (js/PointerEvent. "pointerdown"
                                                #js {:bubbles true
                                                     :clientX 100
                                                     :clientY 50
                                                     :pointerId 1})))
            (after-frames 1
              (fn []
                ;; A record is selected (detail visible) and the scrubber
                ;; line is present.
                (is (some? (query dock "line.scrubber")))
                (is (false? (.hasAttribute ^js (query dock "[data-x-th-detail]")
                                           "hidden")))
                (done)))))))))

(deftest keyboard-arrow-right-steps-next-test
  (testing "ArrowRight on the focused timeline steps to the next record"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (du/dispatch! btn "b" #js {})
        (du/dispatch! btn "c" #js {})
        (after-frames 2
          (fn []
            ;; No selection yet → ArrowRight selects the first record.
            (let [^js timeline (query dock "[data-x-th-timeline]")]
              (.dispatchEvent timeline
                              (js/KeyboardEvent. "keydown"
                                                 #js {:bubbles true
                                                      :key "ArrowRight"})))
            (after-frames 1
              (fn []
                (is (= 0 (gobj/get dock model/k-selected-id)))
                ;; Another ArrowRight → id=1.
                (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                                (js/KeyboardEvent. "keydown"
                                                   #js {:bubbles true
                                                        :key "ArrowRight"}))
                (after-frames 1
                  (fn []
                    (is (= 1 (gobj/get dock model/k-selected-id)))
                    (done)))))))))))

(deftest keyboard-arrow-left-steps-prev-test
  (testing "ArrowLeft steps to the previous record; from no selection,
            picks the most recent"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (du/dispatch! btn "b" #js {})
        (du/dispatch! btn "c" #js {})
        (after-frames 2
          (fn []
            (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                            (js/KeyboardEvent. "keydown"
                                               #js {:bubbles true
                                                    :key "ArrowLeft"}))
            (after-frames 1
              (fn []
                (is (= 2 (gobj/get dock model/k-selected-id)))
                (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                                (js/KeyboardEvent. "keydown"
                                                   #js {:bubbles true
                                                        :key "ArrowLeft"}))
                (after-frames 1
                  (fn []
                    (is (= 1 (gobj/get dock model/k-selected-id)))
                    (done)))))))))))

(deftest keyboard-step-respects-filter-test
  (testing "ArrowRight only steps through records that pass the active filter"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "click" #js {})    ; id 0, events
        (du/setv!     btn "__x" "v")          ; id 1, state
        (du/dispatch! btn "click" #js {})    ; id 2, events
        (after-frames 2
          (fn []
            ;; Uncheck events: only the state record (id 1) remains.
            (dispatch-checkbox-change! ^js (query dock "[data-x-th-cat='events']") false)
            (after-frames 1
              (fn []
                (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                                (js/KeyboardEvent. "keydown"
                                                   #js {:bubbles true
                                                        :key "ArrowRight"}))
                (after-frames 1
                  (fn []
                    (is (= 1 (gobj/get dock model/k-selected-id))
                        "first filtered record is id 1 (state)")
                    (done)))))))))))

(deftest keyboard-handler-fires-on-repeated-keydown-test
  (testing "the keydown handler advances selection on every dispatch, not
            just the first. Verifies handler reentrancy after render!
            replaces innerHTML — does NOT reproduce real-browser focus
            survival, since dispatchEvent bypasses focus."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (du/dispatch! btn "b" #js {})
        (du/dispatch! btn "c" #js {})
        (du/dispatch! btn "d" #js {})
        (after-frames 2
          (fn []
            (let [^js timeline (query dock "[data-x-th-timeline]")
                  press!       (fn []
                                 (.dispatchEvent timeline
                                                 (js/KeyboardEvent. "keydown"
                                                                    #js {:bubbles true
                                                                         :key "ArrowRight"})))]
              (press!) (press!) (press!))
            (after-frames 1
              (fn []
                ;; nil → 0 → 1 → 2
                (is (= 2 (gobj/get dock model/k-selected-id)))
                (done)))))))))

(deftest keyboard-arrow-on-button-still-steps-test
  (testing "ArrowRight while a header button has focus DOES step the
            timeline — buttons have no native arrow semantics, so we
            don't ignore them like we do for select / input / textarea"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (du/dispatch! btn "b" #js {})
        (after-frames 2
          (fn []
            (let [^js pause (query dock "[data-x-th-action='pause']")]
              (.dispatchEvent pause
                              (js/KeyboardEvent. "keydown"
                                                 #js {:bubbles true
                                                      :key "ArrowRight"})))
            (after-frames 1
              (fn []
                (is (= 0 (gobj/get dock model/k-selected-id))
                    "selection advanced to first record despite focus
                     being on the pause button")
                (done)))))))))

(deftest keyboard-arrow-in-select-passes-through-test
  (testing "ArrowRight on the tag-select dropdown does NOT step the
            timeline — the form control keeps its native behaviour"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (du/dispatch! btn "b" #js {})
        (after-frames 2
          (fn []
            (let [^js sel (query dock "[data-x-th-tag]")]
              (.dispatchEvent sel
                              (js/KeyboardEvent. "keydown"
                                                 #js {:bubbles true
                                                      :key "ArrowRight"})))
            (after-frames 1
              (fn []
                (is (nil? (gobj/get dock model/k-selected-id))
                    "selection did not advance because target was a select")
                (done)))))))))

(deftest keyboard-other-key-is-passthrough-test
  (testing "non-arrow keys do not change selection or preventDefault"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "a" #js {})
        (after-frames 2
          (fn []
            (let [^js timeline (query dock "[data-x-th-timeline]")
                  ^js evt      (js/KeyboardEvent. "keydown"
                                                  #js {:bubbles true
                                                       :key "Enter"
                                                       :cancelable true})]
              (.dispatchEvent timeline evt)
              (is (false? (.-defaultPrevented evt)))
              (is (nil? (gobj/get dock model/k-selected-id))))
            (done)))))))

(deftest coalesced-renders-survive-burst-test
  (testing "a burst of dispatches renders one dot each (single rAF coalesces)"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (dotimes [i 10]
          (du/dispatch! btn (str "ev-" i) #js {}))
        (after-frames 2
          (fn []
            (is (= 10 (dot-count dock)))
            (done)))))))

;; ---------------------------------------------------------------------------
;; PR 7 — detail-pane cause / effect navigation
;; ---------------------------------------------------------------------------

(deftest detail-pane-shows-effects-section-test
  (testing "selecting a dispatch that caused other records reveals an
            'Effects (N)' section with one button per effect"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Find the dispatch dot and click it.
            (let [dots         (array-seq (query-all dock "circle.dot"))
                  dispatch-dot (some (fn [^js d]
                                       (when (= "#94e2d5"
                                                (.getAttribute d "fill")) d))
                                     dots)]
              (.dispatchEvent ^js dispatch-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (let [^js detail (query dock "[data-x-th-detail]")
                      effects   (query dock "[data-x-th-detail-effects]")
                      links     (.querySelectorAll detail "[data-x-th-link-id]")]
                  (is (some? effects))
                  (is (= 1 (.-length links)))
                  (is (clojure.string/includes? (.-textContent effects) "Effects (1)")))
                (done)))))))))

(deftest detail-pane-shows-cause-section-test
  (testing "selecting a child record reveals a 'Caused by' section with
            a single link to the parent dispatch"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Click the state-coloured dot (the setv).
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af"
                                            (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (let [cause (query dock "[data-x-th-detail-cause]")
                      links (.querySelectorAll cause "[data-x-th-link-id]")]
                  (is (some? cause))
                  (is (= 1 (.-length links)))
                  (is (clojure.string/includes? (.-textContent cause) "Caused by")))
                (done)))))))))

(deftest detail-pane-no-cause-section-when-top-level-test
  (testing "a record with no cause has no 'Caused by' section"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        ;; Dispatch with no listeners — no children produced; this dispatch
        ;; is itself top-level (causeId nil).
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (.dispatchEvent ^js (query dock "circle.dot")
                            (js/MouseEvent. "click" #js {:bubbles true}))
            (after-frames 1
              (fn []
                (is (nil? (query dock "[data-x-th-detail-cause]")))
                (is (nil? (query dock "[data-x-th-detail-effects]")))
                (done)))))))))

(deftest filters-do-not-pollute-trace-test
  (testing "PR-C + PR-A: toggling each of the 4 category x-checkboxes
            and the tag x-select fires their typed change events but
            the recorder's internal-host boundary suppresses them —
            the trace stays empty even though the filter spec changed."
    (async done
      (let [^js dock (mount-dock!)]
        ;; Toggle every category checkbox off, then back on.
        (doseq [cat ["events" "state" "dom" "lifecycle"]]
          (dispatch-checkbox-change! ^js (query dock (str "[data-x-th-cat='" cat "']")) false)
          (dispatch-checkbox-change! ^js (query dock (str "[data-x-th-cat='" cat "']")) true))
        ;; Move the tag filter twice.
        (dispatch-select-change! ^js (query dock "[data-x-th-tag]") "x-button")
        (dispatch-select-change! ^js (query dock "[data-x-th-tag]") "all")
        (after-frames 2
          (fn []
            (is (zero? (.-length (recorder/records)))
                "no trace records added — boundary suppressed all filter events")
            (done)))))))

(deftest pause-button-press-stays-out-of-trace-test
  (testing "PR-B + PR-A: clicking the inner native button inside the
            Pause x-button fires its press / press-start / press-end
            CustomEvents, but the recorder's internal-host boundary
            suppresses them — the trace stays empty even though pause
            actually toggled."
    (async done
      (let [^js dock      (mount-dock!)
            ^js host      (query dock "[data-x-th-action='pause']")
            ^js inner-btn (.querySelector ^js (.-shadowRoot host) "button")]
        (is (some? inner-btn) "x-button exposes its inner button via open shadow")
        ;; Trigger x-button's full click chain so it dispatches press,
        ;; press-start, press-end, etc.
        (.click inner-btn)
        (after-frames 2
          (fn []
            (is (true? (recorder/paused?)) "pause toggled via bubbled click")
            (is (zero? (.-length (recorder/records)))
                "no records added — boundary suppressed all dock-internal events")
            (done)))))))

(deftest pause-button-pressed-attribute-reflects-state-test
  (testing "refresh-pause-btn! sets the x-button :pressed property,
            which reflects to the `pressed` attribute and shows the
            paused styling"
    (async done
      (let [^js dock (mount-dock!)
            ^js host (query dock "[data-x-th-action='pause']")]
        (is (false? (.hasAttribute host "pressed")) "starts unpressed")
        (recorder/pause!)
        (after-frames 2
          (fn []
            (is (true? (.hasAttribute host "pressed")) "pressed reflects after pause")
            (recorder/resume!)
            (after-frames 2
              (fn []
                (is (false? (.hasAttribute host "pressed")) "pressed reflects after resume")
                (done)))))))))

(deftest detail-pane-link-click-jumps-selection-test
  (testing "clicking an effect link in the detail pane moves the
            selection to the linked record"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Select the dispatch dot first.
            (let [dispatch-dot (some (fn [^js d]
                                       (when (= "#94e2d5"
                                                (.getAttribute d "fill")) d))
                                     (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js dispatch-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                ;; The dispatch's effect link points at the setv. Click it.
                (let [^js link (query dock
                                      "[data-x-th-detail-effects] [data-x-th-link-id]")
                      target-id (js/parseInt
                                 (.getAttribute link "data-x-th-link-id") 10)]
                  (is (some? link))
                  (.dispatchEvent link
                                  (js/MouseEvent. "click" #js {:bubbles true}))
                  (after-frames 1
                    (fn []
                      (is (= target-id (gobj/get dock model/k-selected-id))
                          "selection moved to the linked record")
                      (done))))))))))))

;; ---------------------------------------------------------------------------
;; PR 8 — Record/Stop sessions UI
;; ---------------------------------------------------------------------------

(deftest record-button-toggles-active-session-test
  (testing "clicking the Record x-button starts a session; clicking
            again stops it. Label flips to 'Stop' while active."
    (async done
      (let [^js dock (mount-dock!)
            ^js host (query dock "[data-x-th-action='record']")]
        (is (= "Record" (.-textContent host)) "starts in idle state")
        (.click host)
        (after-frames 2
          (fn []
            (is (some? (recorder/active-session-id)) "session opened")
            (is (= "Stop" (.-textContent host)) "label flips to Stop")
            (.click host)
            (after-frames 2
              (fn []
                (is (nil? (recorder/active-session-id)) "session closed")
                (is (= "Record" (.-textContent host)) "label back to Record")
                (done)))))))))

(deftest sessions-strip-hidden-when-no-sessions-test
  (testing "the strip is hidden until at least one session exists"
    (let [^js dock     (mount-dock!)
          ^js strip-el (query dock "[data-x-th-sessions]")]
      (is (true? (.hasAttribute strip-el "hidden"))))))

(deftest sessions-strip-shows-live-and-session-chip-test
  (testing "after a recording, the strip becomes visible with a Live
            chip + one chip per session, and Live is the active chip
            by default"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (recorder/start-session!)
        (du/dispatch! btn "click" #js {})
        (recorder/stop-session!)
        (after-frames 2
          (fn []
            (let [^js strip (query dock "[data-x-th-sessions]")
                  chips     (.querySelectorAll strip "[data-x-th-session]")]
              (is (false? (.hasAttribute strip "hidden")))
              (is (= 2 (.-length chips)) "Live + Session 0")
              (let [^js live-chip (aget chips 0)]
                (is (= "live" (.getAttribute live-chip "data-x-th-session")))
                (is (.contains (.-classList live-chip) "active")
                    "Live is the active chip by default"))
              (done))))))))

(deftest session-chip-click-switches-view-test
  (testing "clicking a session chip activates that session as the view"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            id       (recorder/start-session!)]
        (du/dispatch! btn "click" #js {})
        (recorder/stop-session!)
        (after-frames 2
          (fn []
            (let [^js chip (query dock
                                  (str "[data-x-th-session='" id "']"))]
              (.click chip))
            (after-frames 1
              (fn []
                (is (= [:session 0] (gobj/get dock model/k-view))
                    "view switched to the clicked session")
                (let [^js active (query dock ".session-chip.active")]
                  (is (= "0" (.getAttribute active "data-x-th-session"))))
                (done)))))))))

(deftest live-view-shows-all-records-test
  (testing "switching back to the Live chip restores the full timeline"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "before" #js {})
        (recorder/start-session!)
        (du/dispatch! btn "inside" #js {})
        (recorder/stop-session!)
        (after-frames 2
          (fn []
            (.click ^js (query dock "[data-x-th-session='0']"))
            (after-frames 1
              (fn []
                (is (= 1 (dot-count dock))
                    "session view shows only the in-session record")
                (.click ^js (query dock "[data-x-th-session='live']"))
                (after-frames 1
                  (fn []
                    (is (= 2 (dot-count dock))
                        "live view shows both records")
                    (is (= :live (gobj/get dock model/k-view)))
                    (done)))))))))))

(deftest per-session-scrubber-state-test
  (testing "each view keeps its own selection; switching back to Live
            preserves what was selected there"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "live-only" #js {})
        (recorder/start-session!)
        (du/dispatch! btn "in-session" #js {})
        (recorder/stop-session!)
        (after-frames 2
          (fn []
            (.dispatchEvent ^js (first (array-seq (query-all dock "circle.dot")))
                            (js/MouseEvent. "click" #js {:bubbles true}))
            (after-frames 1
              (fn []
                (let [live-sel (gobj/get dock model/k-selected-id)]
                  (.click ^js (query dock "[data-x-th-session='0']"))
                  (after-frames 1
                    (fn []
                      (let [^js detail (query dock "[data-x-th-detail]")]
                        (is (true? (.hasAttribute detail "hidden"))
                            "session view starts with no selection"))
                      (.click ^js (query dock "[data-x-th-session='live']"))
                      (after-frames 1
                        (fn []
                          (is (= live-sel (gobj/get dock model/k-selected-id))
                              "live selection preserved across view switch")
                          (done))))))))))))))

;; ---------------------------------------------------------------------------
;; PR 9 — :state default off + forensic-mode mount
;; ---------------------------------------------------------------------------
;;
;; The shared fixture installs in forensic mode so :state shows by
;; default. These tests force normal-mode reinstall so we can verify
;; the production default (state checkbox unchecked, :state filtered
;; out of the dock view).

(defn- enter-normal-mode!
  "Uninstall, flip the activation flag to non-forensic, reinstall, clear.
   Pair with leave-normal-mode! at the end of the test. Used pair-wise
   for sync tests; for async tests, also call leave at the end of the
   final after-frames callback."
  []
  (recorder/uninstall!)
  (gobj/set js/window "BAREDOM_TRACE_HISTORY" true)
  (recorder/install!)
  (recorder/clear!))

(defn- leave-normal-mode!
  "Restore the fixture's forensic install."
  []
  (recorder/uninstall!)
  (gobj/set js/window "BAREDOM_TRACE_HISTORY" "raw")
  (recorder/install!)
  (recorder/clear!))

(defn- with-normal-mode!
  "Reinstall the recorder in normal (non-forensic) mode for the
   duration of `f`. Sync only — the finally cleanup runs as soon as
   `f` returns, which is wrong for tests that schedule async work
   inside `f`. Async tests should call enter/leave manually around
   their done callback."
  [f]
  (enter-normal-mode!)
  (try (f) (finally (leave-normal-mode!))))

(deftest state-checkbox-unchecked-by-default-in-normal-mode-test
  (testing "in normal mode the :state x-checkbox is rendered unchecked
            so the timeline default focuses on user-relevant events"
    (with-normal-mode!
     #(let [^js dock     (mount-dock!)
            ^js state-cb (query dock "[data-x-th-cat='state']")
            ^js events-cb (query dock "[data-x-th-cat='events']")]
        (is (false? (.hasAttribute state-cb "checked"))
            ":state checkbox unchecked")
        (is (true? (.hasAttribute events-cb "checked"))
            ":events checkbox still checked")))))

(deftest state-checkbox-checked-in-forensic-mode-test
  (testing "the shared forensic-mode fixture leaves all four category
            checkboxes checked"
    (let [^js dock     (mount-dock!)
          ^js state-cb (query dock "[data-x-th-cat='state']")]
      (is (true? (.hasAttribute state-cb "checked"))))))

(deftest state-filter-applies-by-default-in-normal-mode-test
  (testing "in normal mode, a state record fires but the dock filter
            keeps it off the timeline (until the user re-checks the
            :state category)"
    (async done
      (enter-normal-mode!)
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "click" #js {})
        (du/setv!     btn "__field" "v")
        (after-frames 2
          (fn []
            (is (= 1 (dot-count dock))
                "only the events dot shows; the state record is filtered")
            (leave-normal-mode!)
            (done)))))))

;; ---------------------------------------------------------------------------
;; PR 10 — Export button → download path
;; ---------------------------------------------------------------------------
;;
;; The dock's Export button calls recorder/download-trace!, which builds
;; a Blob and clicks a synthetic anchor. To verify the wiring without
;; actually triggering a save-dialog, these tests stub URL.createObjectURL,
;; URL.revokeObjectURL, and HTMLAnchorElement.prototype.click on entry
;; and restore them on exit. The stubs collect the Blob and the anchor
;; state so assertions can inspect the bytes that would have been saved.

(defn- install-download-stubs!
  "Replace the three download-path globals with capturing stubs. Returns
   a map of {:orig, :captured} the test can hand to restore + assertions."
  []
  (let [orig-create  js/URL.createObjectURL
        orig-revoke  js/URL.revokeObjectURL
        orig-click   (.. js/HTMLAnchorElement -prototype -click)
        captured     (atom {:created [] :revoked [] :clicks []})
        stub-create  (fn [^js blob]
                       (let [url (str "blob:test-"
                                      (count (:created @captured)))]
                         (swap! captured update :created conj
                                {:blob blob :url url})
                         url))
        stub-revoke  (fn [url]
                       (swap! captured update :revoked conj url))
        stub-click   (fn []
                       (this-as ^js this
                         (swap! captured update :clicks conj
                                {:download (.-download this)
                                 :href     (.-href this)})))]
    (set! js/URL.createObjectURL stub-create)
    (set! js/URL.revokeObjectURL stub-revoke)
    (set! (.. js/HTMLAnchorElement -prototype -click) stub-click)
    {:orig     {:create orig-create
                :revoke orig-revoke
                :click  orig-click}
     :captured captured}))

(defn- restore-download-stubs!
  "Symmetric tear-down for install-download-stubs!."
  [{{:keys [create revoke click]} :orig}]
  (set! js/URL.createObjectURL create)
  (set! js/URL.revokeObjectURL revoke)
  (set! (.. js/HTMLAnchorElement -prototype -click) click))

(deftest export-button-rendered-in-header-test
  (testing "the dock skeleton renders an Export x-button with the
            data-x-th-action='export' marker the click delegate
            dispatches on. Verifies the user-facing label so a
            rename of the button (e.g. 'Download') doesn't sneak
            through without updating the docs / a11y."
    (let [^js dock (mount-dock!)
          ^js btn  (query dock "[data-x-th-action='export']")]
      (is (some? btn) "Export button present in the header")
      (is (= "x-button" (.. btn -tagName toLowerCase))
          "rendered as an <x-button> like the other action buttons")
      (is (= "Export" (clojure.string/trim (.-textContent btn)))
          "button label is the documented 'Export' string"))))

(deftest export-click-triggers-anchor-click-test
  (testing "clicking the Export button creates a Blob, clicks a synthetic
            anchor with a .trace.json download name, and revokes the URL.
            One full download cycle per click."
    (let [{:keys [captured] :as ctx} (install-download-stubs!)]
      (try
        (let [^js dock (mount-dock!)
              ^js btn  (query dock "[data-x-th-action='export']")
              ^js el   (.createElement js/document "x-button")]
          (du/dispatch! el "press" #js {:n 1})
          (.click btn)
          (let [{:keys [created revoked clicks]} @captured]
            (is (= 1 (count created)) "exactly one Blob created")
            (is (= 1 (count clicks))  "exactly one anchor click")
            (is (= 1 (count revoked)) "object URL revoked after click")
            (is (= (:url (first created)) (first revoked))
                "the URL we created is the same URL we revoked — no
                 leaked Blob, no double-revoke of a stale handle")
            (let [{:keys [download href]} (first clicks)]
              (is (clojure.string/starts-with? download
                                                model/filename-prefix)
                  "filename starts with the schema prefix")
              (is (clojure.string/ends-with? download
                                              model/filename-extension)
                  "filename ends with .trace.json")
              (is (= (:url (first created)) href)
                  "anchor.href is the same blob URL we created")
              (is (clojure.string/starts-with? href "blob:")
                  "anchor href is the blob URL"))
            (let [^js blob (:blob (first created))]
              (is (= "application/json" (.-type blob))
                  "Blob type signals JSON for the save dialog"))))
        (finally
          (restore-download-stubs! ctx))))))

(deftest export-button-does-not-mutate-recorder-state-test
  (testing "Export is read-only: clicking it must not pause / resume /
            clear / change the view or selection. Sanity check the
            common 'I want to download before I clear' workflow."
    (let [ctx (install-download-stubs!)]
      (try
        (let [^js dock (mount-dock!)
              ^js el   (.createElement js/document "x-button")]
          (du/dispatch! el "press" #js {})
          (let [paused-before  (recorder/paused?)
                view-before    (gobj/get dock model/k-view)
                count-before   (.-length (recorder/records))
                ^js btn        (query dock "[data-x-th-action='export']")]
            (.click btn)
            (is (= paused-before  (recorder/paused?))
                "paused state untouched")
            (is (= view-before    (gobj/get dock model/k-view))
                "view untouched")
            (is (= count-before   (.-length (recorder/records)))
                "records untouched")))
        (finally
          (restore-download-stubs! ctx))))))
