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

(defn- row-count [^js dock-el]
  (.-length (query-all dock-el ".row")))

;; ---------------------------------------------------------------------------
;; Mount + skeleton
;; ---------------------------------------------------------------------------

(deftest mount-creates-shadow-skeleton-test
  (testing "mounting <x-trace-history> attaches a shadow root with the dock skeleton"
    (let [^js el (mount-dock!)]
      (is (some? (shadow-of el)))
      (is (some? (query el ".dock")))
      (is (some? (query el "[data-x-th-list]")))
      (is (some? (query el "[data-x-th-action='pause']")))
      (is (some? (query el "[data-x-th-action='clear']"))))))

(deftest empty-state-shows-placeholder-test
  (testing "with no records, the list area shows the empty hint"
    (let [^js el (mount-dock!)]
      (is (some? (query el ".empty"))))))

;; ---------------------------------------------------------------------------
;; Records → rows
;; ---------------------------------------------------------------------------

(deftest dispatch-renders-row-test
  (testing "a single dispatch produces one row in the dock"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {:value 7})
        (after-frames 2
          (fn []
            (is (= 1 (row-count dock)))
            (let [^js row (query dock ".row")]
              (is (some? row))
              (is (clojure.string/includes? (.-textContent row) "x-button:click"))
              (is (clojure.string/includes? (.-textContent row) "x-button")))
            (done)))))))

(deftest multiple-dispatches-newest-first-test
  (testing "rows render newest-first regardless of insertion order"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "first"  #js {})
        (du/dispatch! btn "second" #js {})
        (du/dispatch! btn "third"  #js {})
        (after-frames 2
          (fn []
            (let [rows  (query-all dock ".row")
                  texts (mapv (fn [^js r] (.-textContent r))
                              (array-seq rows))]
              (is (= 3 (count texts)))
              (is (clojure.string/includes? (first texts) "third"))
              (is (clojure.string/includes? (last texts)  "first")))
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

;; ---------------------------------------------------------------------------
;; Detail pane
;; ---------------------------------------------------------------------------

(deftest click-row-opens-detail-test
  (testing "clicking a row reveals the JSON detail pane for that record"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {:n 99})
        (after-frames 2
          (fn []
            (let [^js row (query dock ".row")]
              (.click row))
            (after-frames 1
              (fn []
                (let [^js detail (query dock "[data-x-th-detail]")]
                  (is (false? (.hasAttribute detail "hidden")))
                  (is (clojure.string/includes? (.-textContent detail) "x-button:click"))
                  (is (clojure.string/includes? (.-textContent detail) "schemaVersion")))
                (done)))))))))

(deftest splitter-visibility-tracks-detail-test
  (testing "splitter is hidden until a row is selected, then visible while
            the detail pane is open, then hidden again on close"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            (let [^js splitter (query dock "[data-x-th-splitter]")]
              (is (true? (.hasAttribute splitter "hidden")) "hidden when nothing selected")
              (.click ^js (query dock ".row"))
              (after-frames 1
                (fn []
                  (is (false? (.hasAttribute splitter "hidden")) "visible after selection")
                  (.click ^js (query dock ".row"))
                  (after-frames 1
                    (fn []
                      (is (true? (.hasAttribute splitter "hidden")) "hidden after deselection")
                      (done))))))))))))

(deftest filter-clears-stale-selection-test
  (testing "selecting a row, then filtering it out, hides the detail pane
            (rather than leaving an orphan record visible)"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (du/setv!     btn "__x" "v")
        (after-frames 2
          (fn []
            ;; Select the event row.
            (.click ^js (query dock ".row.cat-events"))
            (after-frames 1
              (fn []
                (let [^js detail (query dock "[data-x-th-detail]")]
                  (is (false? (.hasAttribute detail "hidden"))))
                ;; Uncheck the events category — selected record is now invisible.
                (let [^js cb (query dock "[data-x-th-cat='events']")]
                  (set! (.-checked cb) false)
                  (.dispatchEvent cb (js/Event. "change" #js {:bubbles true})))
                (after-frames 1
                  (fn []
                    (let [^js detail (query dock "[data-x-th-detail]")]
                      (is (true? (.hasAttribute detail "hidden"))))
                    (done)))))))))))

(deftest click-row-twice-closes-detail-test
  (testing "clicking the same row a second time hides the detail pane"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x-button:click" #js {})
        (after-frames 2
          (fn []
            ;; First click: open detail. Each click triggers a re-render that
            ;; replaces the list innerHTML, so the row reference must be
            ;; re-queried before the second click.
            (.click ^js (query dock ".row"))
            (after-frames 1
              (fn []
                (.click ^js (query dock ".row"))
                (after-frames 1
                  (fn []
                    (let [^js detail (query dock "[data-x-th-detail]")]
                      (is (true? (.hasAttribute detail "hidden"))))
                    (done)))))))))))

;; ---------------------------------------------------------------------------
;; Filters
;; ---------------------------------------------------------------------------

(deftest tag-filter-narrows-rows-test
  (testing "selecting a tag in the dropdown filters out other tags"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js mod  (.createElement js/document "x-modal")]
        (du/dispatch! btn "click" #js {})
        (du/dispatch! mod "open"  #js {})
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (is (= 3 (row-count dock)))
            (let [^js sel (query dock "[data-x-th-tag]")]
              (set! (.-value sel) "x-button")
              (.dispatchEvent sel (js/Event. "change" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (is (= 2 (row-count dock)))
                (let [rows (array-seq (query-all dock ".row"))]
                  (is (every? #(clojure.string/includes? (.-textContent ^js %) "x-button")
                              rows)))
                (done)))))))))

(deftest category-filter-narrows-rows-test
  (testing "unchecking a category checkbox hides records of that category"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "click" #js {})
        (du/setv!     btn "__x" "v")
        (after-frames 2
          (fn []
            (is (= 2 (row-count dock)))
            (let [^js cb (query dock "[data-x-th-cat='events']")]
              (set! (.-checked cb) false)
              (.dispatchEvent cb (js/Event. "change" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (is (= 1 (row-count dock)))
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

(deftest clear-button-empties-list-test
  (testing "clicking the clear button empties the list and the count badge"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "x" #js {})
        (du/dispatch! btn "y" #js {})
        (after-frames 2
          (fn []
            (is (= 2 (row-count dock)))
            (let [^js clear-btn (query dock "[data-x-th-action='clear']")]
              (.click clear-btn))
            (after-frames 2
              (fn []
                (is (zero? (row-count dock)))
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
            (is (= 1 (row-count dock)))
            (.remove dock)
            ;; After disconnect the dock should hold no subscriber token.
            (is (nil? (gobj/get dock model/k-sub-token)))
            ;; Further dispatches must not blow up despite the dock being gone.
            (du/dispatch! btn "after" #js {})
            (after-frames 2
              (fn []
                ;; Recorder still recorded the post-disconnect event.
                (is (= 2 (.-length (recorder/records))))
                (done)))))))))

(deftest coalesced-renders-survive-burst-test
  (testing "a burst of dispatches renders correctly (single rAF coalesces all)"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (dotimes [i 10]
          (du/dispatch! btn (str "ev-" i) #js {}))
        (after-frames 2
          (fn []
            (is (= 10 (row-count dock)))
            (done)))))))
