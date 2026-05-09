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
  (recorder/clear!))

(defn- before-fixture
  "Prep: install hooks fresh for each test."
  []
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
                  (set! (.-checked cb) false)
                  (.dispatchEvent cb (js/Event. "change" #js {:bubbles true})))
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
            (let [^js sel (query dock "[data-x-th-tag]")]
              (set! (.-value sel) "x-button")
              (.dispatchEvent sel (js/Event. "change" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (is (= 2 (dot-count dock)))
                (is (= 1 (lane-count dock)))
                (done)))))))))

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
            (let [^js cb (query dock "[data-x-th-cat='events']")]
              (set! (.-checked cb) false)
              (.dispatchEvent cb (js/Event. "change" #js {:bubbles true})))
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
