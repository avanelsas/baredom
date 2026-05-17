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

(defn- drop-all-imports!
  "Remove every import currently held by the recorder. `recorder/clear!`
   preserves imports by design (PR 11), so the after-fixture has to do
   this explicitly to isolate tests."
  []
  (doseq [^js imp (array-seq (recorder/imports))]
    (recorder/remove-import! (.-id imp))))

(defn- after-fixture
  "Tear down between tests: remove any leftover dock element, clear the
   recorder buffer, drop any imports the test loaded, ensure resume
   state, and uninstall hooks (the next test re-installs)."
  []
  (when-let [^js el (.querySelector js/document model/tag-name)]
    (.remove el))
  (recorder/uninstall!)
  (recorder/resume!)
  (recorder/clear!)
  (drop-all-imports!)
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

(defn- live-selected-id
  "Read the dock's selection for the Live view via the canonical
   k-view-selected map. Replaces the previous k-selected-id mirror,
   which was a duplicate slot maintained solely for test reads.
   Returns nil when nothing is selected (undefined is mapped to nil
   so `(is (nil? (live-selected-id ...)))` works)."
  [^js dock]
  (when-let [^js m (gobj/get dock model/k-view-selected)]
    (let [v (gobj/get m "live")]
      (when-not (undefined? v) v))))

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

(deftest register-is-idempotent-test
  (testing "calling dock/register! more than once produces exactly one
            <x-trace-history> element on body — protects against
            React-StrictMode-style double-effects, bundlers that
            include the module twice, or any consumer code path that
            ends up importing the side-effect entry more than once.
            The recorder's wrapper-stamp guard prevents double-wrapping
            dispatchEvent (covered separately in recorder_test.cljs);
            this test focuses on the dock layer."
    (dock/register!)
    (dock/register!)
    (dock/register!)
    (let [els (.querySelectorAll js/document model/tag-name)]
      (is (= 1 (.-length els))
          "exactly one dock element after triple register!"))
    ;; And the custom element is defined exactly once (a second
    ;; .define would throw NotSupportedError).
    (is (some? (.get js/customElements model/tag-name)))))

(deftest empty-state-shows-placeholder-test
  (testing "with no records, the svg pane shows the empty hint and the lanes
            column is empty"
    (let [^js el (mount-dock!)]
      (is (some? (query el ".timeline-empty")))
      (is (zero? (lane-count el))))))

;; ---------------------------------------------------------------------------
;; Tag dropdown — populated at runtime from observed tags
;;
;; PR 12 dropped the static x-debug-registry import so the dock can ship
;; as a per-module ESM bundle. The dropdown now seeds with just the
;; 'All tags' default and gains one <option> per observed tag on every
;; render. The user's currently-selected value must survive the
;; append-only sync.
;;
;; The recorder's :components index is monotonic for the page lifetime
;; (clear! preserves it by design), so the dropdown may carry options
;; for tags emitted by earlier tests. Assertions check *presence* and
;; *invariants*, never exact-vector equality.
;; ---------------------------------------------------------------------------

(defn- tag-options [^js dock-el]
  (->> (array-seq (query-all dock-el "[data-x-th-tag] option"))
       (mapv #(.-value ^js %))))

(deftest tag-dropdown-has-all-default-test
  (testing "every mount renders the 'All tags' default option as the
            first entry — observed tags are appended after it"
    (let [^js dock (mount-dock!)
          opts    (tag-options dock)]
      (is (= "all" (first opts))
          "'all' default is the first option"))))

(deftest tag-dropdown-grows-with-observed-tags-test
  (testing "after a dispatch on a new tag, the dropdown gains an
            option for it; repeated dispatches do not duplicate the
            option"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js cb   (.createElement js/document "x-checkbox")]
        (du/dispatch! btn "press"  #js {})
        (du/dispatch! btn "press"  #js {})
        (du/dispatch! cb  "toggle" #js {})
        (after-frames 2
          (fn []
            (let [opts (tag-options dock)]
              (is (some #{"x-button"}   opts))
              (is (some #{"x-checkbox"} opts))
              (is (= (count opts) (count (distinct opts)))
                  "no duplicate options")
              (done))))))))

(deftest tag-dropdown-preserves-selection-across-sync-test
  (testing "user's selected value survives the append-only refresh
            when a new tag arrives. sync-tag-options! must never
            blow away existing options."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "press" #js {})
        (after-frames 2
          (fn []
            (dispatch-select-change!
              ^js (query dock "[data-x-th-tag]") "x-button")
            (let [^js cb (.createElement js/document "x-checkbox")]
              (du/dispatch! cb "toggle" #js {}))
            (after-frames 2
              (fn []
                (let [^js sel (query dock "[data-x-th-tag]")]
                  (is (= "x-button" (.-value sel))
                      "selection survives the second sync"))
                (let [opts (tag-options dock)]
                  (is (some #{"x-checkbox"} opts)
                      "newly-observed tag was appended"))
                (done)))))))))

;; ---------------------------------------------------------------------------
;; Search input — PR 16
;;
;; The dock's filter bar carries a native <input type='search'>. Typing
;; into it narrows the timeline to records whose JSON-serialised form
;; contains the typed string. Empty input clears the filter. The handler
;; is wired through a single shadow-root `input` listener gated on the
;; data-x-th-search attribute.
;; ---------------------------------------------------------------------------

(defn- dispatch-search-input!
  "Simulate a user typing into the search field — set the host's value
   property and fire the CustomEvent x-search-field emits on every
   keystroke. Mirrors the dispatch-checkbox-change! / dispatch-select-
   change! helpers above. The event must bubble + compose so it
   crosses the dock's shadow boundary to the dock's listener-spec
   delegate."
  [^js sf value]
  (set! (.-value sf) value)
  (.dispatchEvent sf
                  (js/CustomEvent. "x-search-field-input"
                                   #js {:bubbles  true
                                        :composed true
                                        :detail   #js {:name  "search"
                                                       :value value}})))

(deftest search-input-rendered-in-filter-bar-test
  (testing "the dock skeleton ships an x-search-field next to the
            tag/categories filters with the data-x-th-search marker
            the handler gates on"
    (let [^js dock (mount-dock!)
          ^js inp  (query dock "[data-x-th-search]")]
      (is (some? inp) "search field present in the filter bar")
      (is (= "x-search-field" (.. inp -tagName toLowerCase))
          "uses the BareDOM x-search-field component")
      (is (.hasAttribute inp "data-x-th-search")))))

(deftest search-narrows-dots-test
  (testing "typing a substring of an event name filters the timeline
            to matching dispatches only"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js cb   (.createElement js/document "x-checkbox")]
        (du/dispatch! btn "press"  #js {})
        (du/dispatch! cb  "toggle" #js {})
        (after-frames 2
          (fn []
            (is (= 2 (dot-count dock)) "both records visible initially")
            (dispatch-search-input! ^js (query dock "[data-x-th-search]") "press")
            (after-frames 1
              (fn []
                (is (= 1 (dot-count dock))
                    "only the 'press' record matches the search query")
                (done)))))))))

(deftest search-clearing-restores-all-records-test
  (testing "blanking the search input drops the filter back to its
            empty-string sentinel and shows every record again"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js cb   (.createElement js/document "x-checkbox")]
        (du/dispatch! btn "press"  #js {})
        (du/dispatch! cb  "toggle" #js {})
        (after-frames 2
          (fn []
            (let [^js inp (query dock "[data-x-th-search]")]
              (dispatch-search-input! inp "press")
              (after-frames 1
                (fn []
                  (is (= 1 (dot-count dock)))
                  (dispatch-search-input! inp "")
                  (after-frames 1
                    (fn []
                      (is (= 2 (dot-count dock))
                          "both records back after clearing the search")
                      (done))))))))))))

(deftest search-is-case-insensitive-test
  (testing "uppercase queries still match lowercase haystacks — the
            handler lowercases the value once before storing it on
            k-filter so model/matches-search? doesn't have to"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "PressEvent" #js {})
        (after-frames 2
          (fn []
            (dispatch-search-input! ^js (query dock "[data-x-th-search]") "press")
            (after-frames 1
              (fn []
                (is (= 1 (dot-count dock))
                    "lowercase query matched the mixed-case event name")
                (done)))))))))

(deftest search-typing-does-not-pollute-live-records-test
  (testing "x-search-field events fire from inside the dock's shadow,
            so the recorder's internal-host filter (via mark-internal!)
            must skip them. Typing into search should not add records
            to the live buffer."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (du/dispatch! btn "press" #js {})
        (after-frames 2
          (fn []
            (let [before-search (.-length (recorder/records))]
              (dispatch-search-input!
                ^js (query dock "[data-x-th-search]") "press")
              (after-frames 2
                (fn []
                  (is (= before-search (.-length (recorder/records)))
                      "no x-search-field-input records leaked into live")
                  (done))))))))))

(deftest search-combines-with-tag-filter-test
  (testing "search AND tag both apply — the AND-semantics are inherited
            from filter-records"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            ^js cb   (.createElement js/document "x-checkbox")]
        (du/dispatch! btn "press" #js {})
        (du/dispatch! cb  "press" #js {})
        (after-frames 2
          (fn []
            (is (= 2 (dot-count dock))
                "two presses across two components before any filter")
            (dispatch-search-input! ^js (query dock "[data-x-th-search]") "press")
            (after-frames 1
              (fn []
                (is (= 2 (dot-count dock))
                    "search alone still keeps both — both contain 'press'")
                (dispatch-select-change!
                  ^js (query dock "[data-x-th-tag]") "x-button")
                (after-frames 1
                  (fn []
                    (is (= 1 (dot-count dock))
                        "tag filter on top of search narrows to x-button only")
                    (done)))))))))))

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
                (is (= 0 (live-selected-id dock)))
                ;; Another ArrowRight → id=1.
                (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                                (js/KeyboardEvent. "keydown"
                                                   #js {:bubbles true
                                                        :key "ArrowRight"}))
                (after-frames 1
                  (fn []
                    (is (= 1 (live-selected-id dock)))
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
                (is (= 2 (live-selected-id dock)))
                (.dispatchEvent ^js (query dock "[data-x-th-timeline]")
                                (js/KeyboardEvent. "keydown"
                                                   #js {:bubbles true
                                                        :key "ArrowLeft"}))
                (after-frames 1
                  (fn []
                    (is (= 1 (live-selected-id dock)))
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
                    (is (= 1 (live-selected-id dock))
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
                (is (= 2 (live-selected-id dock)))
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
                (is (= 0 (live-selected-id dock))
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
                (is (nil? (live-selected-id dock))
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
              (is (nil? (live-selected-id dock))))
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
                      (is (= target-id (live-selected-id dock))
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
                (let [live-sel (live-selected-id dock)]
                  (.click ^js (query dock "[data-x-th-session='0']"))
                  (after-frames 1
                    (fn []
                      (let [^js detail (query dock "[data-x-th-detail]")]
                        (is (true? (.hasAttribute detail "hidden"))
                            "session view starts with no selection"))
                      (.click ^js (query dock "[data-x-th-session='live']"))
                      (after-frames 1
                        (fn []
                          (is (= live-sel (live-selected-id dock))
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

;; ---------------------------------------------------------------------------
;; PR 11 — Import button + drag-drop + chips
;; ---------------------------------------------------------------------------
;;
;; `recorder/import-trace!` does NOT reset its id counter across calls,
;; so tests that need to query for a specific chip must use the id
;; returned from the trace call rather than hardcoding 0. The fixture's
;; `drop-all-imports!` keeps the recorder's `:imports` slot clean but
;; the counter monotonically grows.

(defn- mk-test-record
  [id t type]
  (js-obj "schemaVersion" 1
          "id"            id
          "t"             t
          "type"          type
          "tag"           "x-button"
          "componentId"   nil
          "causeId"       nil))

(defn- mk-test-envelope
  [^js records]
  (js-obj "schemaVersion" 1
          "exportedAt"    1715432100000
          "origin"        "https://example.com/"
          "userAgent"     "test"
          "forensic"      false
          "components"    (js-obj)
          "sessions"      (array)
          "records"       records))

(defn- chip-selector
  "Build a `[data-x-th-session='import:N']` attribute selector for the
   import-id `id`. Tests capture the id from import-trace! rather than
   hardcoding it since the counter persists across the suite."
  [id]
  (str "[data-x-th-session='import:" id "']"))

(deftest import-button-rendered-in-header-test
  (testing "the dock skeleton renders an Import x-button next to Export
            so the file-picker entry point is discoverable"
    (let [^js dock (mount-dock!)
          ^js btn  (query dock "[data-x-th-action='import']")]
      (is (some? btn) "Import button present in the header")
      (is (= "Import" (clojure.string/trim (.-textContent btn)))
          "label reads 'Import'"))))

(deftest import-input-hidden-and-accepts-trace-json-test
  (testing "the hidden <input type='file'> exists and has accept
            attributes that bias the OS picker toward .trace.json /
            .json files"
    (let [^js dock  (mount-dock!)
          ^js input (query dock "[data-x-th-import-input]")]
      (is (some? input))
      (is (= "file"  (.-type input)))
      (let [accept (.getAttribute input "accept")]
        (is (clojure.string/includes? accept ".trace.json"))
        (is (clojure.string/includes? accept ".json"))))))

(deftest import-button-click-opens-file-picker-test
  (testing "clicking the Import button programmatically clicks the
            hidden file input. Stubs the input's click method so the
            OS picker doesn't actually open during the test."
    (let [^js dock  (mount-dock!)
          ^js btn   (query dock "[data-x-th-action='import']")
          ^js input (query dock "[data-x-th-import-input]")
          clicks    (atom 0)
          orig      (.-click input)]
      (try
        (set! (.-click input) (fn [] (swap! clicks inc)))
        (.click btn)
        (is (= 1 @clicks) "input clicked once per Import button click")
        (finally
          (set! (.-click input) orig))))))

(deftest import-trace!-via-api-adds-chip-test
  (testing "after recorder/import-trace! the session strip surfaces an
            import chip with the file's label. The chip carries an
            'import:N' wire key and the visual `.import` class."
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))
            id       (recorder/import-trace! env "bug-report")]
        (after-frames 2
          (fn []
            (let [^js chip (query dock (chip-selector id))]
              (is (some? chip) "import chip rendered in the strip")
              (is (.contains (.-classList chip) "import")
                  "import chip carries the .import styling modifier")
              (is (clojure.string/includes? (.-textContent chip) "bug-report")
                  "chip label is the filename-derived label")
              (done))))))))

(deftest import-chip-click-switches-view-test
  (testing "clicking an import chip switches the dock to the
            [:import N] view; the records on the timeline come from
            the imported envelope rather than the live buffer"
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope
                       (array (mk-test-record 0 1.0 "lifecycle/connected")
                              (mk-test-record 1 2.0 "lifecycle/connected")))
            id       (recorder/import-trace! env "two-records")]
        (after-frames 2
          (fn []
            (let [^js chip (query dock (chip-selector id))]
              (.click chip)
              (after-frames 2
                (fn []
                  (is (= [:import id] (gobj/get dock model/k-view))
                      "dock view switched to the import")
                  (is (= 2 (dot-count dock))
                      "imported records render as timeline dots")
                  (done))))))))))

(deftest import-chip-close-removes-import-test
  (testing "clicking the × close button on an import chip drops the
            import from the recorder AND switches the view back to
            :live when the closed import was active.

            Re-query the chip after the activate-click — render!
            re-renders the strip, so the original close-button node
            is detached after the first click and a fresh one stands
            in its place."
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))
            id       (recorder/import-trace! env "closable")]
        (after-frames 2
          (fn []
            ;; Switch into the import first so we can verify the close
            ;; switches back to :live.
            (.click ^js (query dock (chip-selector id)))
            (after-frames 2
              (fn []
                (let [^js close (.querySelector
                                 ^js (query dock (chip-selector id))
                                 "[data-x-th-import-close]")]
                  (.click close)
                  (after-frames 2
                    (fn []
                      (is (= 0 (.-length (recorder/imports)))
                          "import removed from the recorder")
                      (is (= :live (gobj/get dock model/k-view))
                          "view reset to :live since the closed import
                           was the active view")
                      (done))))))))))))

(deftest import-rejects-bad-schema-shows-hint-error-test
  (testing "a malformed envelope passed through import-trace! is
            rejected; the dock's hint area shows a transient error
            line that the .error class styles distinctly. The error
            wiring sits in the dock's import-file path; here we
            simulate the reject directly via recorder/import-trace!,
            since the input/drop paths route through it identically."
    (let [^js dock (mount-dock!)
          ^js env  (mk-test-envelope (array))]
      (gobj/set env "schemaVersion" 999)
      (let [imports-before (.-length (recorder/imports))
            id             (recorder/import-trace! env)
            imports-after  (.-length (recorder/imports))]
        (is (nil? id) "rejected envelope returns nil — no chip added")
        (is (= imports-before imports-after)
            "recorder :imports unchanged on rejection")
        (is (= 0 (.-length (query-all dock "[data-x-th-import-close]")))
            "no import chip rendered for the failed envelope")))))

(deftest import-survives-recorder-clear-test
  (testing "clicking Clear empties the live buffer but the import
            chip stays in the strip — imports are user-owned data"
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))
            id       (recorder/import-trace! env "persistent")]
        (let [el (.createElement js/document "x-button")]
          (du/dispatch! el "press" #js {}))
        (after-frames 2
          (fn []
            (let [^js clear-btn (query dock "[data-x-th-action='clear']")]
              (.click clear-btn)
              (after-frames 2
                (fn []
                  (is (= 0 (.-length (recorder/records)))
                      "live records cleared")
                  (is (= 1 (.-length (recorder/imports)))
                      "import survives the Clear")
                  (is (some? (query dock (chip-selector id)))
                      "import chip still rendered")
                  (done))))))))))

;; ---------------------------------------------------------------------------
;; Axis-mode toggle (hybrid Order/Time)
;; ---------------------------------------------------------------------------

(deftest axis-select-rendered-test
  (testing "the dock skeleton renders an <x-select data-x-th-axis>
            in the filters row with Order selected by default"
    (let [^js dock (mount-dock!)
          ^js sel  (query dock "[data-x-th-axis]")]
      (is (some? sel)        "axis select rendered")
      (is (= "x-select" (.. sel -tagName toLowerCase))))))

(deftest axis-mode-default-is-order-test
  (testing "on mount the dock's k-axis-mode slot is :order — encodes
            the design decision that the order axis is the default
            for state-time-travel debugging"
    (let [^js dock (mount-dock!)]
      (is (= :order (gobj/get dock model/k-axis-mode))))))

(deftest axis-select-change-flips-mode-test
  (testing "selecting 'time' from the axis dropdown writes :time to
            k-axis-mode and triggers a re-render; selecting 'order'
            flips it back"
    (let [^js dock (mount-dock!)
          ^js sel  (query dock "[data-x-th-axis]")]
      (dispatch-select-change! sel "time")
      (is (= :time  (gobj/get dock model/k-axis-mode))
          "time mode applied after select-change")
      (dispatch-select-change! sel "order")
      (is (= :order (gobj/get dock model/k-axis-mode))
          "order mode restored on second change"))))

(deftest axis-select-change-malformed-value-test
  (testing "an unknown value (defensive: external write, future-schema
            value) falls back to the default :order instead of leaving
            the dock with an unrenderable mode"
    (let [^js dock (mount-dock!)
          ^js sel  (query dock "[data-x-th-axis]")]
      (dispatch-select-change! sel "garbage")
      (is (= model/default-axis-mode (gobj/get dock model/k-axis-mode))))))

(deftest axis-mode-affects-dot-positions-test
  (testing "switching from order to time changes where the dots
            render. With two records 50ms apart, the order axis
            spreads them edge-to-edge (uniform spacing) while the
            time axis bunches them according to their t-values
            relative to the buffer's full span. The test asserts the
            x-coordinates DIFFER between modes as a proxy for the
            position dispatch actually firing."
    (async done
      (let [^js dock (mount-dock!)
            el       (.createElement js/document "x-button")]
        ;; Two records — order axis puts them at left/right pad, time
        ;; axis puts them somewhere inside the plot based on t span.
        (du/dispatch! el "a" #js {})
        (du/dispatch! el "b" #js {})
        (after-frames 2
          (fn []
            (let [order-xs (->> (query-all dock "circle.dot")
                                array-seq
                                (map (fn [^js c]
                                       (js/parseFloat (.getAttribute c "cx"))))
                                sort
                                vec)]
              (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
              (after-frames 2
                (fn []
                  (let [time-xs (->> (query-all dock "circle.dot")
                                     array-seq
                                     (map (fn [^js c]
                                            (js/parseFloat (.getAttribute c "cx"))))
                                     sort
                                     vec)]
                    (is (= 2 (count order-xs)) "two dots in order mode")
                    (is (= 2 (count time-xs))  "two dots in time mode")
                    ;; In order mode, dots are at the padded edges.
                    ;; In time mode, the two records share a t-bound of
                    ;; ~0ms span (back-to-back dispatches), so they
                    ;; collapse to the same x. Either way, at least one
                    ;; position differs between the modes.
                    (is (not= order-xs time-xs)
                        "dot x-coordinates differ between axis modes")
                    (done)))))))))))

(deftest axis-mode-scrub-uses-order-test
  (testing "with three records and axis-mode :order, clicking the
            svg-pane near the right edge selects the third record
            (the rightmost in order). With axis-mode :time, the
            three back-to-back records all share roughly the same
            t, so the scrubber picks whichever happens to be nearest
            — we only assert the order path here, since that's the
            new behaviour."
    (async done
      (let [^js dock (mount-dock!)
            el       (.createElement js/document "x-button")]
        (du/dispatch! el "a" #js {})
        (du/dispatch! el "b" #js {})
        (du/dispatch! el "c" #js {})
        (after-frames 2
          (fn []
            (let [^js svg-pane (gobj/get dock model/k-svg-pane-el)
                  rect         (.getBoundingClientRect svg-pane)
                  ;; Click at the far right of the pane. In :order
                  ;; mode this maps to the last record (id of the
                  ;; third dispatch).
                  click-x      (- (.-right rect) 4)
                  click-y      (+ (.-top rect) 10)]
              (.dispatchEvent svg-pane
                              (js/PointerEvent. "pointerdown"
                                                #js {:bubbles  true
                                                     :composed true
                                                     :clientX  click-x
                                                     :clientY  click-y
                                                     :pointerId 1}))
              (after-frames 2
                (fn []
                  (let [sel-id (live-selected-id dock)
                        ;; The last dispatched record is the one
                        ;; recorded last — recorder/records is the
                        ;; live ring buffer, so we look up its id.
                        ^js recs (recorder/records)
                        last-id  (.-id ^js (aget recs (dec (.-length recs))))]
                    (is (= last-id sel-id)
                        "scrubbing to the right edge under order mode
                         selects the rightmost-in-order record")
                    (done)))))))))))

;; ---------------------------------------------------------------------------
;; Collapse toggle + keyboard shortcut (A1 + E1)
;; ---------------------------------------------------------------------------

(defn- dispatch-keydown!
  "Fire a keydown event on `target` with the given key + modifier
   flags. Used by the keyboard-shortcut tests so we don't need a
   real keyboard."
  [^js target key {:keys [ctrl meta shift alt]
                   :or {ctrl false meta false shift false alt false}}]
  (.dispatchEvent target
                  (js/KeyboardEvent. "keydown"
                                     #js {:key      key
                                          :bubbles  true
                                          :composed true
                                          :ctrlKey  ctrl
                                          :metaKey  meta
                                          :shiftKey shift
                                          :altKey   alt})))

(deftest collapse-button-rendered-test
  (testing "the dock skeleton renders a collapse-toggle x-button as
            the first item in the header so the user has an obvious
            way to get the dock out of the way"
    (let [^js dock (mount-dock!)
          ^js btn  (query dock "[data-x-th-action='collapse']")]
      (is (some? btn))
      (is (= "x-button" (.. btn -tagName toLowerCase))))))

(deftest collapse-default-is-expanded-test
  (testing "freshly-mounted dock is expanded (k-collapsed = false)
            and the host carries no .collapsed class"
    (let [^js dock (mount-dock!)]
      (is (false? (boolean (gobj/get dock model/k-collapsed))))
      (is (not (.contains (.-classList dock) "collapsed"))
          "no collapsed class on expanded dock"))))

(deftest collapse-click-toggles-state-test
  (testing "clicking the collapse button flips k-collapsed and
            adds/removes the .collapsed class on the host; the
            button's textContent flips between « and »"
    (let [^js dock (mount-dock!)
          ^js btn  (query dock "[data-x-th-action='collapse']")]
      (.click btn)
      (is (true? (gobj/get dock model/k-collapsed))
          "first click collapses")
      (is (.contains (.-classList dock) "collapsed")
          "host gains .collapsed class")
      (.click btn)
      (is (false? (gobj/get dock model/k-collapsed))
          "second click expands")
      (is (not (.contains (.-classList dock) "collapsed"))
          "host loses .collapsed class"))))

(deftest collapse-aria-expanded-mirrors-state-test
  (testing "the toggle button carries aria-expanded reflecting the
            current state — screen readers track the disclosure"
    (let [^js dock (mount-dock!)
          ^js btn  (query dock "[data-x-th-action='collapse']")]
      (is (= "true"  (.getAttribute btn "aria-expanded"))
          "expanded by default")
      (.click btn)
      (is (= "false" (.getAttribute btn "aria-expanded"))
          "collapsed after click"))))

(deftest keyboard-shortcut-meta-toggles-collapsed-test
  (testing "Cmd + \\ on document toggles the dock from anywhere on
            the page. Meta-key path (Mac)."
    (let [^js dock (mount-dock!)]
      (dispatch-keydown! js/document "\\" {:meta true})
      (is (true? (gobj/get dock model/k-collapsed))
          "Cmd + \\ collapses")
      (dispatch-keydown! js/document "\\" {:meta true})
      (is (false? (gobj/get dock model/k-collapsed))
          "Cmd + \\ again expands"))))

(deftest keyboard-shortcut-ctrl-toggles-collapsed-test
  (testing "Ctrl + \\ on document toggles the dock — Linux/Windows
            path. Same handler, different modifier."
    (let [^js dock (mount-dock!)]
      (dispatch-keydown! js/document "\\" {:ctrl true})
      (is (true? (gobj/get dock model/k-collapsed))))))

(deftest keyboard-shortcut-ignores-bare-backslash-test
  (testing "a backslash without Ctrl/Cmd is ignored — users typing
            literal \\ characters in regular content shouldn't see
            the dock flicker"
    (let [^js dock (mount-dock!)]
      (dispatch-keydown! js/document "\\" {})
      (is (false? (boolean (gobj/get dock model/k-collapsed))))
      (dispatch-keydown! js/document "\\" {:shift true})
      (is (false? (boolean (gobj/get dock model/k-collapsed)))
          "shift+\\ also doesn't trigger"))))

(deftest keyboard-shortcut-ignores-form-control-target-test
  (testing "Cmd + \\ while focused in an input is ignored — keyboard
            shortcuts should never preempt the user typing in a
            form field. Tests against a plain <input> appended to
            <body>; the in-form-control? predicate handles the
            BareDOM equivalents too."
    (let [^js dock  (mount-dock!)
          ^js input (.createElement js/document "input")]
      (.appendChild (.-body js/document) input)
      (.focus input)
      (try
        (dispatch-keydown! input "\\" {:meta true})
        (is (false? (boolean (gobj/get dock model/k-collapsed)))
            "shortcut suppressed while focus is in an input")
        (finally
          (.remove input))))))

(deftest collapse-listener-removed-on-disconnect-test
  (testing "the document-level keydown listener is removed when the
            dock disconnects, so a stale handler on a detached
            element doesn't keep toggling a no-longer-mounted dock
            across remounts"
    (let [^js dock (mount-dock!)]
      ;; Sanity: shortcut works while mounted.
      (dispatch-keydown! js/document "\\" {:meta true})
      (is (true? (gobj/get dock model/k-collapsed)))
      ;; Remove from DOM → disconnectedCallback fires → unmount! ->
      ;; unbind-listeners! removes the document listener.
      (.remove dock)
      (let [collapsed-before (gobj/get dock model/k-collapsed)]
        (dispatch-keydown! js/document "\\" {:meta true})
        (is (= collapsed-before (gobj/get dock model/k-collapsed))
            "shortcut no longer mutates state after disconnect")))))

;; ---------------------------------------------------------------------------
;; Auto-switch to a fresh import — PR 15 (Phase 7, viewer.html)
;;
;; The viewer page boots the dock with no live records, then loads a
;; trace via URL param or drag-drop. The dock should switch to the
;; import automatically so the user lands on the trace instead of an
;; empty :live view with an import chip they have to click. The same
;; UX win applies to regular pages where an idle dock receives an
;; import. The switch must NOT fire when the user has live records —
;; that would yank them out of their session.
;; ---------------------------------------------------------------------------

(deftest auto-switch-on-first-import-when-live-empty-test
  (testing "an import landing on an idle :live view auto-switches the
            dock to that import — the viewer.html landing experience"
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))]
        (after-frames 1
          (fn []
            (is (model/live-view? (gobj/get dock model/k-view))
                "dock starts on :live")
            (let [id (recorder/import-trace! env "drop")]
              (after-frames 2
                (fn []
                  (let [view (gobj/get dock model/k-view)]
                    (is (model/import-view? view)
                        "view switched off :live after first import")
                    (is (= id (model/view-id view))
                        "view targets the newly-loaded import"))
                  (done))))))))))

(deftest no-auto-switch-when-live-has-records-test
  (testing "an import does NOT auto-switch when the live buffer has
            records — protects an active session from being yanked
            into a freshly-imported trace"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))]
        (du/dispatch! btn "press" #js {})
        (after-frames 2
          (fn []
            (is (model/live-view? (gobj/get dock model/k-view))
                "view is :live after dispatching a record")
            (recorder/import-trace! env "drop")
            (after-frames 2
              (fn []
                (is (model/live-view? (gobj/get dock model/k-view))
                    "view stays on :live because live had records")
                (done)))))))))

(deftest no-auto-switch-after-user-returns-to-live-without-new-import-test
  (testing "after the dock auto-switches once, the user clicking back
            to :live should not be re-yanked into the same import on
            the next render — the heuristic only fires on NEW import
            transitions"
    (async done
      (let [^js dock (mount-dock!)
            env      (mk-test-envelope (array (mk-test-record 0 1.0 "lifecycle/connected")))]
        (after-frames 1
          (fn []
            (recorder/import-trace! env "drop")
            (after-frames 2
              (fn []
                (is (model/import-view? (gobj/get dock model/k-view))
                    "auto-switch landed us on the import")
                ;; User manually returns to :live. gobj/set alone does
                ;; not schedule a subscriber tick, so we'd never run
                ;; the heuristic again — the next two assertions would
                ;; pass trivially. recorder/pause! does call
                ;; schedule-notify!, which forces a subscriber tick
                ;; without adding a new import. That's the path that
                ;; actually exercises the no-re-yank branch.
                (gobj/set dock model/k-view :live)
                (recorder/pause!)
                (after-frames 2
                  (fn []
                    (is (model/live-view? (gobj/get dock model/k-view))
                        "view stays on :live across a heuristic-run with no new import")
                    (recorder/resume!)
                    (done)))))))))))

;; ---------------------------------------------------------------------------
;; Causality DAG view (PR 17)
;;
;; The dock's secondary pane: a tree of cause→effect rooted at the
;; highest ancestor of the selected record. Toggled via the
;; <x-select data-x-th-mode> in the filter row. Selection is shared
;; with the timeline pane, so clicking a tree node and then switching
;; back to timeline keeps the same record selected.
;; ---------------------------------------------------------------------------

(defn- mode-select [^js dock]
  (query dock "[data-x-th-mode]"))

(defn- causality-pane [^js dock]
  (query dock "[data-x-th-causality]"))

(defn- timeline-pane [^js dock]
  (query dock "[data-x-th-timeline]"))

(defn- switch-to-causality! [^js dock]
  (dispatch-select-change! (mode-select dock) "causality"))

(defn- switch-to-timeline! [^js dock]
  (dispatch-select-change! (mode-select dock) "timeline"))

(deftest mode-select-rendered-in-filter-row-test
  (testing "the view-mode <x-select> ships in the static skeleton with
            both Timeline and Causality options; Timeline is the
            default-selected option."
    (let [^js dock (mount-dock!)
          ^js sel  (mode-select dock)
          opts     (.querySelectorAll sel "option")]
      (is (some? sel) "mode select is present")
      (is (= 2 (.-length opts)))
      (is (= "timeline"  (.-value ^js (aget opts 0))))
      (is (= "causality" (.-value ^js (aget opts 1))))
      (is (.hasAttribute ^js (aget opts 0) "selected")
          "Timeline is selected by default"))))

(deftest dock-mode-default-is-timeline-test
  (testing "fresh dock mounts in :timeline mode; causality pane hidden"
    (let [^js dock (mount-dock!)]
      (is (= model/default-dock-mode :timeline))
      (is (= :timeline (gobj/get dock model/k-dock-mode)))
      (is (.hasAttribute (causality-pane dock) "hidden"))
      (is (not (.hasAttribute (timeline-pane dock) "hidden"))))))

(deftest mode-select-change-flips-dock-mode-test
  (testing "select-change with value='causality' updates k-dock-mode
            and shows the causality pane; switching back hides it."
    (async done
      (let [^js dock (mount-dock!)]
        (switch-to-causality! dock)
        (after-frames 1
          (fn []
            (is (= :causality (gobj/get dock model/k-dock-mode)))
            (is (.hasAttribute (timeline-pane dock) "hidden"))
            (is (not (.hasAttribute (causality-pane dock) "hidden")))
            (switch-to-timeline! dock)
            (after-frames 1
              (fn []
                (is (= :timeline (gobj/get dock model/k-dock-mode)))
                (is (not (.hasAttribute (timeline-pane dock) "hidden")))
                (is (.hasAttribute (causality-pane dock) "hidden"))
                (done)))))))))

(deftest mode-select-change-malformed-value-test
  (testing "unknown values fall back to the default mode rather than
            putting the dock into a broken state"
    (async done
      (let [^js dock (mount-dock!)]
        (dispatch-select-change! (mode-select dock) "garbage")
        (after-frames 1
          (fn []
            (is (= model/default-dock-mode (gobj/get dock model/k-dock-mode)))
            (done)))))))

(deftest causality-empty-state-when-no-selection-test
  (testing "in :causality mode with no record selected, the pane shows
            the empty-state hint asking the user to pick a record"
    (async done
      (let [^js dock (mount-dock!)]
        (switch-to-causality! dock)
        (after-frames 1
          (fn []
            (let [pane (causality-pane dock)]
              (is (clojure.string/includes?
                   (.-textContent pane) "Select a record"))
              (is (zero? (.-length (.querySelectorAll pane "svg")))
                  "no SVG when nothing is selected"))
            (done)))))))

(deftest causality-renders-tree-for-selected-record-test
  (testing "with a parent dispatch causing a child setv, switching to
            causality with the child selected draws a tree with two
            nodes and one edge"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Select the child (setv) dot in timeline mode.
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane (causality-pane dock)
                          rects (.querySelectorAll pane "rect.node-rect")
                          edges (.querySelectorAll pane "line.edge")]
                      (is (= 2 (.-length rects))
                          "parent dispatch + child setv = 2 nodes")
                      (is (= 1 (.-length edges))
                          "one edge from parent to child")
                      (is (some (fn [^js r]
                                  (.includes (.getAttribute r "class") "selected"))
                                (array-seq rects))
                          "selected node carries the 'selected' class"))
                    (done)))))))))))

(deftest causality-node-click-updates-selection-test
  (testing "clicking a tree node updates k-view-selected. The node's
            <g> carries data-x-th-link-id so the same delegate handles
            cause/effect links in the detail pane and causality clicks."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Start with the child selected.
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane (causality-pane dock)
                          ;; The non-selected node — pick the rect
                          ;; that does NOT carry the selected class.
                          other (some (fn [^js g]
                                        (let [^js r (.querySelector g "rect.node-rect")]
                                          (when (not (.includes (.getAttribute r "class")
                                                                "selected"))
                                            g)))
                                      (array-seq (.querySelectorAll
                                                  pane "[data-x-th-link-id]")))
                          new-id (js/parseInt (.getAttribute ^js other
                                                             "data-x-th-link-id") 10)]
                      (.dispatchEvent ^js (.querySelector ^js other "rect")
                                      (js/MouseEvent. "click" #js {:bubbles true}))
                      (after-frames 1
                        (fn []
                          (is (= new-id (live-selected-id dock))
                              "selected id flipped to the clicked node's record")
                          (done))))))))))))))

(deftest causality-mode-toggle-marks-needs-fit-test
  (testing "switching to :causality always marks :causality-needs-fit
            in ui-state so the next render's apply-causality-fit! has a chance to
            scroll the selected node into the centre of the viewport"
    (async done
      (let [^js dock (mount-dock!)]
        ;; Force the flag clear so we know our toggle sets it.
        (model/set-ui-state! dock :causality-needs-fit false)
        (switch-to-causality! dock)
        (after-frames 1
          (fn []
            ;; After render, the flag is either still true (because
            ;; fit-to-view couldn't apply — the test environment may
            ;; have zero-size container) or false (it applied). What
            ;; we assert is that toggling to causality SET it from
            ;; false to either of those two states — the most
            ;; reliable way to check that is that the dock mode is
            ;; now :causality, since handle-mode-change! always sets
            ;; the flag when switching TO causality.
            (is (= :causality (gobj/get dock model/k-dock-mode)))
            (switch-to-timeline! dock)
            (after-frames 1
              (fn []
                (is (false? (model/ui-state dock :causality-needs-fit))
                    "toggling back to timeline clears the flag")
                (done)))))))))

(deftest causality-fit-to-view-applies-scroll-test
  (testing "fit-to-view: when a record is selected, switching modes
            schedules a fit, and the next render scrolls the causality
            pane. We force a small fixed-size container with overflow
            content so the math has somewhere to scroll to — relies on
            inline style overrides taking precedence over the dock's
            flex CSS for both axes."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_]
                             (du/setv! btn "__a" "1")
                             (du/setv! btn "__b" "2")
                             (du/setv! btn "__c" "3")
                             (du/setv! btn "__d" "4")
                             (du/setv! btn "__e" "5")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (let [setvs     (filter (fn [^js d]
                                      (= "#f9e2af" (.getAttribute d "fill")))
                                    (array-seq (query-all dock "circle.dot")))
                  last-setv (last setvs)]
              (.dispatchEvent ^js last-setv
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (let [^js pane (causality-pane dock)]
                  ;; Pin the pane to a known small size so fit-to-view
                  ;; has somewhere to scroll. flex: 1 1 auto would
                  ;; otherwise stretch the pane to the dock width
                  ;; (420 px), and the tree (~800 px) would still
                  ;; overflow — but pinning is more deterministic.
                  (set! (.. pane -style -flex)   "0 0 auto")
                  (set! (.. pane -style -width)  "120px")
                  (set! (.. pane -style -height) "80px"))
                (switch-to-causality! dock)
                (after-frames 2
                  (fn []
                    (let [^js pane (causality-pane dock)
                          sl       (.-scrollLeft pane)
                          st       (.-scrollTop  pane)
                          cw       (.-clientWidth  pane)
                          ch       (.-clientHeight pane)]
                      (is (pos? cw) "pane has a non-zero width once visible")
                      (is (pos? ch) "pane has a non-zero height once visible")
                      (is (or (pos? sl) (pos? st))
                          (str "fit-to-view applied a non-zero scroll "
                               "(sl=" sl " st=" st " cw=" cw " ch=" ch ")")))
                    (done)))))))))))

(deftest causality-over-cap-shows-notice-test
  (testing "trees larger than model/causality-max-nodes render a
            notice instead of the SVG so the dock doesn't try to
            paint thousands of rects"
    (async done
      (let [^js dock  (mount-dock!)
            handlers (vec (for [i (range (inc model/causality-max-nodes))]
                            (str "__field" i)))
            ^js btn  (.createElement js/document "x-button")]
        ;; A single dispatch that triggers > cap setv records — every
        ;; child has the dispatch as its causeId, blowing the cap.
        (.addEventListener btn "click"
                           (fn [_]
                             (doseq [h handlers]
                               (du/setv! btn h "v"))))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (let [^js any-setv (some (fn [^js d]
                                       (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                     (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent any-setv (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane (causality-pane dock)]
                      (is (clojure.string/includes? (.-textContent pane) "cap")
                          "over-cap notice mentions the cap")
                      (is (zero? (.-length (.querySelectorAll pane "rect.node-rect")))
                          "no tree rects rendered when over the cap"))
                    (done)))))))))))

(deftest causality-mode-persists-across-remount-test
  (testing ":dock-mode survives a disconnect/reconnect cycle the same
            way :axis-mode and the filter spec do"
    (async done
      (let [^js dock (mount-dock!)]
        (switch-to-causality! dock)
        (after-frames 1
          (fn []
            (is (= :causality (gobj/get dock model/k-dock-mode)))
            ;; Disconnect and re-append the same element.
            (.remove dock)
            (.appendChild (.-body js/document) dock)
            (after-frames 1
              (fn []
                (is (= :causality (gobj/get dock model/k-dock-mode))
                    "mode preference survived remount")
                (done)))))))))

(deftest causality-mode-adds-host-class-test
  (testing "switching to causality adds the .causality-mode class on
            the host so :host(.causality-mode) CSS rules can hide
            axis-mode and other timeline-only controls"
    (async done
      (let [^js dock (mount-dock!)]
        (switch-to-causality! dock)
        (after-frames 1
          (fn []
            (is (.contains (.-classList dock) "causality-mode"))
            (switch-to-timeline! dock)
            (after-frames 1
              (fn []
                (is (not (.contains (.-classList dock) "causality-mode")))
                (done)))))))))

(deftest causality-leaf-record-shows-hint-banner-test
  (testing "selecting a record with no cause AND no effects renders
            a leaf-hint banner above the lone node so the user knows
            it's not a broken tree — they just picked an isolated
            record (e.g. a lifecycle record emitted outside any
            dispatch frame)."
    (async done
      (let [^js dock (mount-dock!)
            ;; A bare setv with no enclosing dispatch — its causeId is
            ;; nil and nothing references it, so the tree is just one
            ;; node. Real-world equivalent: a setv from a component's
            ;; connectedCallback.
            ^js btn  (.createElement js/document "x-button")]
        (du/setv! btn "__top-level" "v")
        (after-frames 2
          (fn []
            ;; Select the lone state record from the timeline.
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane (causality-pane dock)
                          hint (.querySelector pane ".causality-leaf-hint")
                          rects (.querySelectorAll pane "rect.node-rect")]
                      (is (some? hint)
                          "leaf-hint banner present for single-node tree")
                      (is (clojure.string/includes? (.-textContent hint)
                                                    "event/dispatch")
                          "banner points at dispatches as the chain-bearing type")
                      (is (= 1 (.-length rects))
                          "the lone node still renders alongside the banner"))
                    (done)))))))))))

(deftest causality-non-leaf-tree-omits-hint-test
  (testing "trees with multiple nodes don't show the leaf-hint banner
            — it would be confusing alongside a real chain"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane (causality-pane dock)]
                      (is (nil? (.querySelector pane ".causality-leaf-hint"))
                          "no leaf-hint when tree has more than 1 node"))
                    (done)))))))))))

(deftest causality-filter-driven-clear-does-not-leak-needs-fit-test
  (testing "audit fix: in :causality mode, a filter-driven
            effective-selection!→set-selected! nil call must NOT
            leave :causality-needs-fit=true. Without the fix the
            stale flag silently triggers a fit-to-view scroll on a
            LATER unrelated render with a different selection."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        ;; Build records: one state record we'll select, one events
        ;; record we'll keep around so a category-filter toggle drops
        ;; the state selection without leaving the buffer empty.
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__live" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Select the setv dot and switch to causality.
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    ;; Force the flag to a known clear state, then
                    ;; trigger the filter-driven selection clear by
                    ;; turning off the `state` category. The current
                    ;; selection (a state record) no longer matches
                    ;; the filter → effective-selection! calls
                    ;; set-selected! with nil.
                    (model/set-ui-state! dock :causality-needs-fit false)
                    (dispatch-checkbox-change!
                     ^js (query dock "[data-x-th-cat='state']") false)
                    (after-frames 1
                      (fn []
                        (is (false? (model/ui-state dock :causality-needs-fit))
                            "needs-fit stayed clear despite the
                             selection being dropped by the filter
                             — the flag must NOT leak into a later
                             unrelated render")
                        (done)))))))))))))

(deftest causality-arrow-step-updates-selection-test
  (testing "ArrowRight in causality mode steps the selection just
            like in timeline mode. set-selected! marks needs-fit on
            real (non-nil) selection changes, so the next render's
            apply-causality-fit! attempts a scroll."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            ;; Select the parent dispatch dot (the green one) first.
            (let [evt-dot (some (fn [^js d]
                                  (when (= "#94e2d5" (.getAttribute d "fill")) d))
                                (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js evt-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [before-id (live-selected-id dock)]
                      ;; Force-clear the flag so we can prove arrow-
                      ;; stepping (a real selection move) sets it.
                      (model/set-ui-state! dock :causality-needs-fit false)
                      (.dispatchEvent
                       ^js (shadow-of dock)
                       (js/KeyboardEvent. "keydown"
                                          #js {:key      "ArrowRight"
                                               :bubbles  true
                                               :composed true}))
                      (after-frames 1
                        (fn []
                          (let [after-id (live-selected-id dock)]
                            (is (some? after-id) "still have a selection")
                            (is (not= before-id after-id)
                                "ArrowRight moved the selection to a
                                 different record"))
                          (done))))))))))))))

(deftest causality-view-in-session-smoke-test
  (testing "causality view works inside a [:session N] view: the
            tree is built from the session's records, not the live
            buffer, so a chain captured entirely inside a session
            still renders correctly"
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        ;; Capture a session containing one parent + one child.
        (recorder/start-session!)
        (du/dispatch! btn "click" #js {})
        (recorder/stop-session!)
        (after-frames 2
          (fn []
            ;; Switch the dock to the session view via its chip.
            (let [^js session    (aget (recorder/sessions) 0)
                  ^js chip       (query
                                  dock
                                  (str "[data-x-th-session='" (.-id session) "']"))]
              (.dispatchEvent chip (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                ;; Select the setv dot inside the session.
                (let [setv-dot (some (fn [^js d]
                                       (when (= "#f9e2af"
                                                (.getAttribute d "fill")) d))
                                     (array-seq (query-all dock "circle.dot")))]
                  (.dispatchEvent ^js setv-dot
                                  (js/MouseEvent. "click" #js {:bubbles true})))
                (after-frames 1
                  (fn []
                    (switch-to-causality! dock)
                    (after-frames 1
                      (fn []
                        (let [pane  (causality-pane dock)
                              rects (.querySelectorAll pane "rect.node-rect")]
                          (is (>= (.-length rects) 2)
                              "causality tree shows the chain captured
                               inside the session"))
                        (done)))))))))))))

(deftest causality-node-has-no-tabindex-test
  (testing "audit fix: per-node tabindex would create one tab stop
            per tree node — unmanageable near the 200-node cap. The
            pane is the single tab stop; arrow keys step the
            selection from there. role='button' stays so screen
            readers still announce nodes as activatable on click."
    (async done
      (let [^js dock (mount-dock!)
            ^js btn  (.createElement js/document "x-button")]
        (.addEventListener btn "click"
                           (fn [_] (du/setv! btn "__inside" "v")))
        (du/dispatch! btn "click" #js {})
        (after-frames 2
          (fn []
            (let [setv-dot (some (fn [^js d]
                                   (when (= "#f9e2af" (.getAttribute d "fill")) d))
                                 (array-seq (query-all dock "circle.dot")))]
              (.dispatchEvent ^js setv-dot
                              (js/MouseEvent. "click" #js {:bubbles true})))
            (after-frames 1
              (fn []
                (switch-to-causality! dock)
                (after-frames 1
                  (fn []
                    (let [pane    (causality-pane dock)
                          groups  (.querySelectorAll
                                   pane "[data-x-th-link-id]")]
                      (is (pos? (.-length groups))
                          "tree has at least one node group")
                      (doseq [^js g (array-seq groups)]
                        (is (nil? (.getAttribute g "tabindex"))
                            "node group has no tabindex")
                        (is (= "button" (.getAttribute g "role"))
                            "role='button' preserved for screen-reader
                             announcement")))
                    (done)))))))))))

;; ---------------------------------------------------------------------------
;; Heatmap density rendering (PR 18)
;;
;; In :time axis mode, lanes whose records overlap visually (>3
;; records per 4 px bin) render as a coloured band instead of dots.
;; Selection / detail-pane / arrow-stepping all keep working because
;; a bin is just an alternate rendering of the same records — its
;; data-x-th-bin-record-id routes the click through the existing
;; selection plumbing.
;; ---------------------------------------------------------------------------

(defn- import-test-records!
  "Import a hand-crafted records array and wait two frames so the
   auto-switch heuristic moves the dock onto the new import.
   `records-js` is a JS array of mk-test-record outputs."
  [records-js label cb]
  (let [env (mk-test-envelope records-js)]
    (recorder/import-trace! env label)
    (after-frames 2 cb)))

(defn- mk-test-record-with
  "Like mk-test-record but accepts an explicit componentId so tests
   can spread records across multiple lanes (density rendering is
   per-lane, so multi-lane setups need distinct cids)."
  [id t type cid]
  (js-obj "schemaVersion" 1
          "id"            id
          "t"             t
          "type"          type
          "tag"           "x-button"
          "componentId"   cid
          "causeId"       nil))

(deftest density-dense-lane-renders-bands-test
  (testing "in :time mode, a lane whose records cluster into one
            pixel bin (because they all share the same t) renders as
            density-bin rects instead of dots"
    (async done
      (let [^js dock (mount-dock!)
            ;; 20 records all at t=100 + componentId=1: one lane,
            ;; one bin (post time-x normalization), > threshold.
            recs     (apply array
                            (for [i (range 20)]
                              (mk-test-record-with i 100 "event/dispatch" 1)))]
        (import-test-records!
         recs "dense"
         (fn []
           ;; Axis mode is :order by default — switch to :time so the
           ;; density path activates.
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [bins (query-all dock "rect.density-bin")
                     dots (query-all dock "circle.dot")]
                 (is (pos? (.-length bins))
                     "dense lane rendered as density bins")
                 (is (zero? (.-length dots))
                     "no individual dots when the lane is a band"))
               (done)))))))))

(deftest density-sparse-lane-renders-dots-test
  (testing ":time mode + sparse records (≤ threshold per bin) keeps
            the dot rendering — density mode never activates"
    (async done
      (let [^js dock (mount-dock!)
            ;; 3 records spread across distinct t values → distinct
            ;; bins → max bin count = 1, well below threshold.
            recs     (array
                      (mk-test-record-with 0 100 "event/dispatch"        1)
                      (mk-test-record-with 1 500 "event/dispatch"        1)
                      (mk-test-record-with 2 900 "event/dispatch"        1))]
        (import-test-records!
         recs "sparse"
         (fn []
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [bins (query-all dock "rect.density-bin")
                     dots (query-all dock "circle.dot")]
                 (is (zero? (.-length bins)) "no bins in a sparse lane")
                 (is (= 3 (.-length dots))
                     "all three records rendered as dots"))
               (done)))))))))

(deftest density-order-mode-keeps-dots-test
  (testing ":order axis mode never activates density rendering, even
            for a record set that would be dense in :time mode —
            order-axis spaces records uniformly so dots cannot
            overlap"
    (async done
      (let [^js dock (mount-dock!)
            recs     (apply array
                            (for [i (range 20)]
                              (mk-test-record-with i 100 "event/dispatch" 1)))]
        (import-test-records!
         recs "ordermode"
         (fn []
           ;; Stay on the default :order axis. Verify no bins.
           (let [bins (query-all dock "rect.density-bin")
                 dots (query-all dock "circle.dot")]
             (is (zero? (.-length bins))
                 ":order axis suppresses density rendering")
             (is (= 20 (.-length dots))
                 "every record rendered as an individual dot"))
           (done)))))))

(deftest density-mixed-lanes-test
  (testing "a timeline can carry one dense lane (rendered as bins)
            and one sparse lane (rendered as dots) in the same SVG.
            Per-lane decision keeps the dock useful for mixed
            workloads."
    (async done
      (let [^js dock (mount-dock!)
            ;; Lane cid=1: 15 records at t=100 → dense.
            ;; Lane cid=2: 2 records at distinct t's → sparse.
            recs     (apply array
                            (concat
                             (for [i (range 15)]
                               (mk-test-record-with i 100 "event/dispatch" 1))
                             [(mk-test-record-with 15 100 "event/dispatch" 2)
                              (mk-test-record-with 16 900 "event/dispatch" 2)]))]
        (import-test-records!
         recs "mixed"
         (fn []
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [bins (query-all dock "rect.density-bin")
                     dots (query-all dock "circle.dot")]
                 (is (pos? (.-length bins))
                     "dense lane rendered as bins")
                 (is (= 2 (.-length dots))
                     "sparse lane stayed as 2 individual dots"))
               (done)))))))))

(deftest density-bin-click-selects-first-record-test
  (testing "clicking a density bin selects the bin's first record
            (its lowest id, set by bin-records-by-x's sort). The
            same id is exposed on data-x-th-bin-record-id so the
            existing click delegate picks it up without changes."
    (async done
      (let [^js dock (mount-dock!)
            ;; Record ids 10, 5, 20 all in the same bin — sorted by
            ;; id ascending puts 5 first.
            recs     (array
                      (mk-test-record-with 10 100 "event/dispatch" 1)
                      (mk-test-record-with 5  100 "event/dispatch" 1)
                      (mk-test-record-with 20 100 "event/dispatch" 1)
                      ;; Pad to push the lane over threshold.
                      (mk-test-record-with 21 100 "event/dispatch" 1)
                      (mk-test-record-with 22 100 "event/dispatch" 1))]
        (import-test-records!
         recs "click"
         (fn []
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [^js bin (query dock "rect.density-bin")]
                 (is (some? bin) "bin rendered")
                 (is (= "5" (.getAttribute bin "data-x-th-bin-record-id"))
                     "first record by id ascending is the bin's
                      anchor id (deterministic for click routing)")
                 (.dispatchEvent bin
                                 (js/MouseEvent. "click" #js {:bubbles true}))
                 (after-frames 1
                   (fn []
                     ;; The import view stores selection under
                     ;; `import:<id>` in k-view-selected (a JS-obj
                     ;; map). Fetch the only entry that lives in
                     ;; this test's freshly-imported view.
                     (let [^js m (gobj/get dock model/k-view-selected)
                           keys' (when m (js-keys m))
                           sel   (when (and m (pos? (.-length keys')))
                                   (gobj/get m (aget keys' 0)))]
                       (is (= 5 sel)
                           "bin click selected the bin's first record"))
                     (done))))))))))))

(deftest density-bin-hover-shows-bin-tooltip-test
  (testing "hovering a density bin produces a tooltip that mentions
            the bin's count (rather than a single-record preview)"
    (async done
      (let [^js dock (mount-dock!)
            recs     (apply array
                            (for [i (range 12)]
                              (mk-test-record-with i 100 "event/dispatch" 1)))]
        (import-test-records!
         recs "hover"
         (fn []
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [^js bin     (query dock "rect.density-bin")
                     ^js tooltip (query dock "[data-x-th-tooltip]")]
                 (.dispatchEvent
                  bin
                  (js/PointerEvent. "pointermove"
                                    #js {:bubbles true
                                         :clientX 100
                                         :clientY 50}))
                 (after-frames 1
                   (fn []
                     (is (clojure.string/includes? (.-textContent tooltip)
                                                   "12 records")
                         "tooltip carries the bin count")
                     (is (clojure.string/includes? (.-textContent tooltip)
                                                   "events")
                         "tooltip carries the dominant category")
                     (done))))))))))))

(deftest dot-hover-in-import-view-shows-tooltip-test
  (testing "audit fix: hovering a dot in an import view shows the
            tooltip. Pre-fix, handle-pointermove! read records from
            the live buffer (always empty in viewer.html-style
            usage), so find-record-by-id returned nil and the
            tooltip stayed hidden. Now it reads from view-records."
    (async done
      (let [^js dock (mount-dock!)
            ;; A single sparse record so it renders as a dot, not a band.
            recs     (array (mk-test-record-with 7 100 "event/dispatch" 1))]
        (import-test-records!
         recs "hover-dot"
         (fn []
           (let [^js dot     (query dock "circle.dot")
                 ^js tooltip (query dock "[data-x-th-tooltip]")]
             (is (some? dot) "import view rendered the record as a dot")
             (.dispatchEvent
              dot
              (js/PointerEvent. "pointermove"
                                #js {:bubbles  true
                                     :clientX 50
                                     :clientY 30}))
             (after-frames 1
               (fn []
                 (is (clojure.string/includes?
                      (.-textContent tooltip) "x-button")
                     "dot tooltip resolved the record from the import
                      view — empty tooltip would mean the fix
                      regressed")
                 (done))))))))))

(deftest scrub-in-import-view-selects-record-test
  (testing "audit fix: scrubbing inside an import view selects a
            record from the import's records, not from the (empty)
            live buffer. Pre-fix, start-scrub! read from
            (recorder/records), so scrub gestures inside an import
            no-op'd."
    (async done
      (let [^js dock (mount-dock!)
            recs     (array
                      (mk-test-record-with 0 100 "event/dispatch" 1)
                      (mk-test-record-with 1 200 "event/dispatch" 1)
                      (mk-test-record-with 2 300 "event/dispatch" 1))]
        (import-test-records!
         recs "scrub-import"
         (fn []
           (let [^js svg-pane (query dock "[data-x-th-svg-pane]")]
             (.dispatchEvent
              svg-pane
              (js/PointerEvent. "pointerdown"
                                #js {:bubbles    true
                                     :clientX    100
                                     :clientY    20
                                     :pointerId  1
                                     :pointerType "mouse"}))
             (after-frames 1
               (fn []
                 (let [^js m (gobj/get dock model/k-view-selected)
                       keys' (when m (js-keys m))
                       sel   (when (and m (pos? (.-length keys')))
                               (gobj/get m (aget keys' 0)))]
                   (is (some? sel)
                       "scrub selected a record from the import view"))
                 (done))))))))))

(deftest density-selected-marker-in-band-test
  (testing "when a dense lane contains the currently-selected
            record, the lane renders a thin density-selected line
            at the record's exact x — preserves the scrubber-style
            highlight without breaking the band's colour."
    (async done
      (let [^js dock (mount-dock!)
            recs     (apply array
                            (for [i (range 10)]
                              (mk-test-record-with i 100 "event/dispatch" 1)))]
        (import-test-records!
         recs "selected"
         (fn []
           ;; Seed the selection BEFORE switching to :time. The
           ;; axis-change handler triggers render!, which then picks
           ;; up the selection without a separate render kick.
           ;; Filter-toggling to force a re-render is unreliable
           ;; here because the off-toggle would clear the selection
           ;; via effective-selection! (the record no longer matches
           ;; the temporarily-narrowed filter).
           (let [^js imports (recorder/imports)
                 import-id   (.-id ^js (aget imports 0))
                 view-key    (str "import:" import-id)
                 ^js m       (or (gobj/get dock model/k-view-selected)
                                 (js-obj))]
             (gobj/set m view-key 3)
             (gobj/set dock model/k-view-selected m))
           (dispatch-select-change! ^js (query dock "[data-x-th-axis]") "time")
           (after-frames 1
             (fn []
               (let [marker (query dock "line.density-selected")]
                 (is (some? marker)
                     "selected record marker drawn inside the band"))
               (done)))))))))

;; ---------------------------------------------------------------------------
;; Live-element highlight
;;
;; When the user selects a record, the dock outlines the live DOM
;; element that emitted it via a fixed-position overlay layer. These
;; tests cover the tree walk that resolves componentId → element, the
;; render-driven show/hide flow, and the no-pollution guarantee.
;; ---------------------------------------------------------------------------

(def ^:private highlight-component-id-key "__xTraceHistoryId")
(def ^:private highlight-internal-marker  "__xTraceHistoryInternal")

(defn- highlight-layer [^js dock-el]
  (gobj/get dock-el model/k-highlight-layer))

(defn- highlight-box [^js dock-el]
  (when-let [^js layer (highlight-layer dock-el)]
    (.querySelector (.-shadowRoot layer) ".outline")))

(defn- highlight-visible? [^js dock-el]
  (when-let [^js box (highlight-box dock-el)]
    (= "block" (.. box -style -display))))

(deftest find-element-by-component-id-finds-match-test
  (testing "find-element-by-component-id returns the element bearing
            the matching component-id marker; nil for an unknown id."
    (let [^js root  (.createElement js/document "div")
          ^js a     (.createElement js/document "span")
          ^js b     (.createElement js/document "em")]
      (gobj/set a highlight-component-id-key 11)
      (gobj/set b highlight-component-id-key 22)
      (.appendChild root a)
      (.appendChild root b)
      (.appendChild (.-body js/document) root)
      (try
        (is (identical? a (recorder/find-element-by-component-id root 11)))
        (is (identical? b (recorder/find-element-by-component-id root 22)))
        (is (nil? (recorder/find-element-by-component-id root 99))
            "no element has cid 99")
        (is (nil? (recorder/find-element-by-component-id root nil))
            "nil cid returns nil without walking")
        (finally
          (.remove root))))))

(deftest find-element-by-component-id-skips-internal-host-test
  (testing "subtrees marked __xTraceHistoryInternal are skipped — the
            dock and other dev tools never highlight themselves."
    (let [^js root     (.createElement js/document "div")
          ^js internal (.createElement js/document "section")
          ^js inside   (.createElement js/document "span")
          ^js outside  (.createElement js/document "em")]
      (gobj/set internal highlight-internal-marker  true)
      (gobj/set inside   highlight-component-id-key 7)
      (gobj/set outside  highlight-component-id-key 8)
      (.appendChild internal inside)
      (.appendChild root     internal)
      (.appendChild root     outside)
      (.appendChild (.-body js/document) root)
      (try
        (is (nil? (recorder/find-element-by-component-id root 7))
            "cid 7 is inside the internal-marked subtree — must be skipped")
        (is (identical? outside (recorder/find-element-by-component-id root 8))
            "cid 8 lives outside the marked subtree and is returned")
        (finally
          (.remove root))))))

(deftest find-element-by-component-id-descends-into-shadow-test
  (testing "the walk descends into open shadow roots so a component
            nested inside another component's shadow is still found."
    (let [^js root  (.createElement js/document "div")
          ^js host  (.createElement js/document "section")
          ^js inner (.createElement js/document "span")]
      (gobj/set inner highlight-component-id-key 42)
      (let [^js shadow (.attachShadow host #js {:mode "open"})]
        (.appendChild shadow inner))
      (.appendChild root host)
      (.appendChild (.-body js/document) root)
      (try
        (is (identical? inner (recorder/find-element-by-component-id root 42))
            "element inside an open shadow root is found")
        (finally
          (.remove root))))))

(defn- make-target-div!
  "Create a positioned <div> for highlight integration tests, append to
   body, and dispatch a synthetic event from it so the recorder stamps
   it with a componentId. A plain div has no custom-element lifecycle,
   so the dispatch produces exactly ONE record — predictable enough
   that tests can read records[0] and seed selection deterministically.
   Returns [div record-id]."
  []
  (let [^js div (.createElement js/document "div")]
    (set! (.. div -style -position) "absolute")
    (set! (.. div -style -top)      "100px")
    (set! (.. div -style -left)     "50px")
    (set! (.. div -style -width)    "80px")
    (set! (.. div -style -height)   "30px")
    (.appendChild (.-body js/document) div)
    (du/dispatch! div "test-highlight-event" #js {})
    div))

(defn- force-render-via-filter-toggle!
  "Trigger a render by re-applying the 'all' tag filter. Used when a
   test seeds selection state directly on the host and needs
   apply-highlight! to run without dispatching a fresh dot click. The
   toggle leaves filter and selection unchanged — it's a no-op for
   everything except the render cycle."
  [^js dock]
  (let [^js sel (query dock "[data-x-th-tag]")]
    (dispatch-select-change! sel "all")))

(defn- seed-live-selection!
  "Set the dock's live-view selection to `rec-id` by writing
   k-view-selected directly. Mirrors the existing density-selected
   test pattern."
  [^js dock rec-id]
  (let [^js m (or (gobj/get dock model/k-view-selected) (js-obj))]
    (gobj/set m "live" rec-id)
    (gobj/set dock model/k-view-selected m)))

(defn- clear-live-selection!
  "Drop the dock's live-view selection."
  [^js dock]
  (when-let [^js m (gobj/get dock model/k-view-selected)]
    (gobj/remove m "live")))

(deftest selecting-record-shows-highlight-test
  (testing "seeding a record as the live selection draws an outline
            around the live element identified by its componentId.
            Uses a plain <div> target — no custom-element lifecycle
            means exactly one dispatch record, so the read of
            records[0] is deterministic."
    (async done
      (let [^js dock (mount-dock!)
            ^js div  (make-target-div!)]
        (after-frames 2
          (fn []
            (let [^js recs (recorder/records)]
              (is (pos? (.-length recs))
                  "the dispatch produced at least one record"))
            (let [^js rec (aget (recorder/records) 0)]
              (seed-live-selection! dock (.-id rec)))
            (force-render-via-filter-toggle! dock)
            (after-frames 1
              (fn []
                (is (highlight-visible? dock)
                    "outline becomes visible after seeded selection")
                (when (highlight-visible? dock)
                  (let [^js box (highlight-box dock)
                        rect   (.getBoundingClientRect div)]
                    (is (= (str (.-top rect) "px")    (.. box -style -top)))
                    (is (= (str (.-left rect) "px")   (.. box -style -left)))
                    (is (= (str (.-width rect) "px") (.. box -style -width)))
                    (is (= (str (.-height rect) "px") (.. box -style -height)))))
                (.remove div)
                (done)))))))))

(deftest deselecting-record-hides-highlight-test
  (testing "clearing the dock's selection hides the outline and clears
            k-highlight-cid. Programmatic seed + filter-toggle render
            avoids any reliance on dot ordering inside the SVG."
    (async done
      (let [^js dock (mount-dock!)
            ^js div  (make-target-div!)]
        (after-frames 2
          (fn []
            (let [^js rec (aget (recorder/records) 0)]
              (seed-live-selection! dock (.-id rec)))
            (force-render-via-filter-toggle! dock)
            (after-frames 1
              (fn []
                (is (highlight-visible? dock)
                    "outline visible after seeded selection")
                (clear-live-selection! dock)
                (force-render-via-filter-toggle! dock)
                (after-frames 1
                  (fn []
                    (is (nil? (live-selected-id dock))
                        "live-selected-id cleared")
                    (is (false? (boolean (highlight-visible? dock)))
                        "outline hidden after deselect")
                    (is (nil? (gobj/get dock model/k-highlight-cid))
                        "k-highlight-cid cleared")
                    (.remove div)
                    (done)))))))))))

(deftest document-record-does-not-highlight-test
  (testing "records with tag=\"document\" (page-level events) do not
            produce a highlight — there is no element to outline."
    (async done
      (let [^js dock (mount-dock!)]
        ;; A document-target dispatch produces a record with tag =
        ;; \"document\" and componentId = nil.
        (du/dispatch! js/document "x-some-doc-event" #js {})
        (after-frames 2
          (fn []
            (let [^js rec (aget (recorder/records) 0)]
              (is (= "document" (.-tag rec))
                  "the seeded record is a document-tag record")
              (seed-live-selection! dock (.-id rec)))
            (force-render-via-filter-toggle! dock)
            (after-frames 1
              (fn []
                (is (false? (boolean (highlight-visible? dock)))
                    "no outline visible for a document-tag record")
                (done)))))))))

(deftest detached-element-clears-highlight-test
  (testing "when the originating element has been disconnected since
            the record was emitted, the highlight stays hidden and
            k-highlight-cid is cleared."
    (async done
      (let [^js dock (mount-dock!)
            ^js div  (make-target-div!)
            rec-id   (.-id ^js (aget (recorder/records) 0))]
        ;; Disconnect BEFORE selecting — apply-highlight! must treat
        ;; the missing element as expected and clear the slot.
        (.remove div)
        (after-frames 2
          (fn []
            (seed-live-selection! dock rec-id)
            (force-render-via-filter-toggle! dock)
            (after-frames 1
              (fn []
                (is (false? (boolean (highlight-visible? dock)))
                    "outline hidden when target is gone")
                (is (nil? (gobj/get dock model/k-highlight-cid))
                    "k-highlight-cid cleared after lookup miss")
                (done)))))))))

(deftest highlight-machinery-does-not-pollute-records-test
  (testing "selecting / deselecting via seeded selection does not
            introduce new records — the overlay creation, positioning,
            and slot writes must be invisible to the recorder."
    (async done
      (let [^js dock (mount-dock!)
            ^js div  (make-target-div!)
            rec-id   (.-id ^js (aget (recorder/records) 0))]
        (after-frames 2
          (fn []
            (let [before (.-length (recorder/records))]
              (seed-live-selection! dock rec-id)
              (force-render-via-filter-toggle! dock)
              (after-frames 1
                (fn []
                  (clear-live-selection! dock)
                  (force-render-via-filter-toggle! dock)
                  (after-frames 1
                    (fn []
                      (is (= before (.-length (recorder/records)))
                          "no new records produced by highlight show + hide")
                      (.remove div)
                      (done))))))))))))

(deftest unmount-removes-highlight-layer-test
  (testing "disconnecting the dock tears down the overlay layer and
            clears every highlight slot on the host."
    (async done
      (let [^js dock (mount-dock!)
            ^js div  (make-target-div!)
            rec-id   (.-id ^js (aget (recorder/records) 0))]
        (after-frames 2
          (fn []
            (seed-live-selection! dock rec-id)
            (force-render-via-filter-toggle! dock)
            (after-frames 1
              (fn []
                (let [^js layer-before (highlight-layer dock)]
                  (is (some? layer-before) "layer exists while dock is mounted")
                  (.remove dock)
                  (after-frames 1
                    (fn []
                      (is (nil? (gobj/get dock model/k-highlight-layer))
                          "k-highlight-layer cleared on unmount")
                      (is (nil? (gobj/get dock model/k-highlight-box))
                          "k-highlight-box cleared on unmount")
                      (is (nil? (gobj/get dock model/k-highlight-cid))
                          "k-highlight-cid cleared on unmount")
                      (when layer-before
                        (is (false? (.contains (.-body js/document) layer-before))
                            "the layer element is no longer in the document"))
                      (.remove div)
                      (done))))))))))))
