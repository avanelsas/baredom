(ns baredom.components.x-scroll-timeline.x-scroll-timeline-test
  (:require
   [cljs.test :refer [deftest testing is use-fixtures async]]
   [baredom.components.x-scroll-timeline.x-scroll-timeline :as tl]
   [baredom.components.x-scroll-timeline.model :as model]))

;; ── Fixture: clean up after each test ───────────────────────────────────────
(use-fixtures :each
  {:after (fn []
            (let [els (.querySelectorAll js/document model/tag-name)]
              (dotimes [i (.-length els)]
                (.remove (aget els i)))))})

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- create-timeline
  "Create an x-scroll-timeline element with optional attrs and child entries."
  ([] (create-timeline {}))
  ([{:keys [attrs entries]}]
   (tl/init!)
   (let [el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs]
       (.setAttribute el (name k) (str v)))
     (doseq [entry-data (or entries [{} {} {}])]
       (let [child (.createElement js/document "div")]
         (when-let [date-val (:data-date entry-data)]
           (.setAttribute child "data-date" date-val))
         (when-let [id-val (:id entry-data)]
           (.setAttribute child "id" id-val))
         (set! (.-textContent child) (or (:text entry-data) "Entry content"))
         (.appendChild el child)))
     (.appendChild (.-body js/document) el)
     el)))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (testing "registers the custom element"
    (tl/init!)
    (is (some? (.get js/customElements model/tag-name)))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-dom-test
  (testing "has shadow root with expected parts"
    (let [^js el (create-timeline)]
      (is (some? (.-shadowRoot el)))
      (let [^js root (.-shadowRoot el)]
        (is (some? (.querySelector root "[part=container]")))
        (is (some? (.querySelector root "[part=track]")))
        (is (some? (.querySelector root "[part=track-line]")))
        (is (some? (.querySelector root "[part=track-fill]")))
        (is (some? (.querySelector root "[part=track-svg]")))
        (is (some? (.querySelector root "[part=entries]")))
        (is (some? (.querySelector root "[part=live]")))))))

;; ── Default attributes ──────────────────────────────────────────────────────
(deftest default-attrs-test
  (testing "default layout is alternating"
    (let [^js el (create-timeline)]
      (is (= "alternating" (.getAttribute el "data-layout")))))
  (testing "default track is straight"
    (let [^js el (create-timeline)]
      (is (= "straight" (.getAttribute el "data-track")))))
  (testing "default marker is dot"
    (let [^js el (create-timeline)]
      (is (= "dot" (.getAttribute el "data-marker"))))))

;; ── Attribute reflection ────────────────────────────────────────────────────
(deftest attribute-reflection-test
  (testing "layout attribute updates data-layout"
    (let [^js el (create-timeline {:attrs {:layout "left"}})]
      (is (= "left" (.getAttribute el "data-layout")))))
  (testing "track attribute updates data-track"
    (let [^js el (create-timeline {:attrs {:track "curved"}})]
      (is (= "curved" (.getAttribute el "data-track"))))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest property-accessors-test
  (testing "layout property get/set"
    (let [^js el (create-timeline)]
      (is (= "alternating" (.-layout el)))
      (set! (.-layout el) "right")
      (is (= "right" (.-layout el)))
      (is (= "right" (.getAttribute el "layout")))))
  (testing "track property get/set"
    (let [^js el (create-timeline)]
      (is (= "straight" (.-track el)))
      (set! (.-track el) "curved")
      (is (= "curved" (.-track el)))))
  (testing "threshold property get/set"
    (let [^js el (create-timeline)]
      (is (= 0.5 (.-threshold el)))
      (set! (.-threshold el) 0.3)
      (is (= 0.3 (.-threshold el)))))
  (testing "disabled property get/set"
    (let [^js el (create-timeline)]
      (is (false? (.-disabled el)))
      (set! (.-disabled el) true)
      (is (true? (.-disabled el)))
      (is (.hasAttribute el "disabled"))))
  (testing "noProgress property get/set"
    (let [^js el (create-timeline)]
      (is (false? (.-noProgress el)))
      (set! (.-noProgress el) true)
      (is (true? (.-noProgress el)))
      (is (.hasAttribute el "no-progress"))))
  (testing "marker property get/set"
    (let [^js el (create-timeline)]
      (is (= "dot" (.-marker el)))
      (set! (.-marker el) "ring")
      (is (= "ring" (.-marker el)))))
  (testing "activeIndex is read-only"
    (let [^js el (create-timeline)]
      (is (= -1 (.-activeIndex el)))))
  (testing "progress is read-only"
    (let [^js el (create-timeline)]
      (is (= 0 (.-progress el))))))

;; ── Child data-side assignment ──────────────────────────────────────────────
(deftest child-side-assignment-test
  (testing "alternating layout assigns alternating sides"
    (let [^js el (create-timeline)]
      ;; Wait for slotchange + rAF
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [children (.-children el)]
                (is (= "left"  (.getAttribute (aget children 0) "data-side")))
                (is (= "right" (.getAttribute (aget children 1) "data-side")))
                (is (= "left"  (.getAttribute (aget children 2) "data-side"))))
              (done)))))))))

;; ── Marker creation ─────────────────────────────────────────────────────────
(deftest marker-creation-test
  (testing "markers are created in shadow DOM"
    (let [^js el (create-timeline)]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [^js root (.-shadowRoot el)
                    markers (.querySelectorAll root ".tl-marker")]
                (is (= 3 (.-length markers))))
              (done)))))))))

;; ── Date labels ─────────────────────────────────────────────────────────────
(deftest date-label-test
  (testing "date labels created for entries with data-date"
    (let [^js el (create-timeline
                  {:entries [{:data-date "2024-01" :text "First"}
                             {:text "No date"}
                             {:data-date "2024-03" :text "Third"}]})]
      (async done
        ;; slotchange → rebuild-layout! (sync) → inner rAF for positioning
        (js/setTimeout
         (fn []
           (let [^js root (.-shadowRoot el)
                 dates (.querySelectorAll root ".tl-date")]
             (is (= 2 (.-length dates)))
             (when (= 2 (.-length dates))
               (is (= "2024-01" (.-textContent (aget dates 0))))
               (is (= "2024-03" (.-textContent (aget dates 1))))))
           (done))
         100)))))

;; ── Container role ──────────────────────────────────────────────────────────
(deftest a11y-test
  (testing "container has role feed"
    (let [^js el (create-timeline)
          ^js root (.-shadowRoot el)
          ^js container (.querySelector root "[part=container]")]
      (is (= "feed" (.getAttribute container "role")))))
  (testing "aria-label set from label attribute"
    (let [^js el (create-timeline {:attrs {:label "Project history"}})
          ^js root (.-shadowRoot el)
          ^js container (.querySelector root "[part=container]")]
      (is (= "Project history" (.getAttribute container "aria-label")))))
  (testing "live region exists"
    (let [^js el (create-timeline)
          ^js root (.-shadowRoot el)
          ^js live (.querySelector root "[part=live]")]
      (is (= "polite" (.getAttribute live "aria-live"))))))

;; ── Autoplay property accessors ─────────────────────────────────────────────
(deftest autoplay-property-accessors-test
  (testing "autoplay property get/set"
    (let [^js el (create-timeline)]
      (is (false? (.-autoplay el)))
      (set! (.-autoplay el) true)
      (is (true? (.-autoplay el)))
      (is (.hasAttribute el "autoplay"))))
  (testing "autoplaySpeed property get/set"
    (let [^js el (create-timeline)]
      (is (= 50 (.-autoplaySpeed el)))
      (set! (.-autoplaySpeed el) 80)
      (is (= 80 (.-autoplaySpeed el)))
      (is (= "80" (.getAttribute el "autoplay-speed")))))
  (testing "autoplayLoop property get/set"
    (let [^js el (create-timeline)]
      (is (false? (.-autoplayLoop el)))
      (set! (.-autoplayLoop el) true)
      (is (true? (.-autoplayLoop el)))
      (is (.hasAttribute el "autoplay-loop"))))
  (testing "autoplayIndicator property get/set"
    (let [^js el (create-timeline)]
      (is (false? (.-autoplayIndicator el)))
      (set! (.-autoplayIndicator el) true)
      (is (true? (.-autoplayIndicator el)))
      (is (.hasAttribute el "autoplay-indicator"))))
  (testing "autoplayPaused is read-only and defaults to false"
    (let [^js el (create-timeline)]
      (is (false? (.-autoplayPaused el))))))

;; ── Autoplay indicator DOM ──────────────────────────────────────────────────
(deftest autoplay-indicator-dom-test
  (testing "indicator element exists in shadow DOM"
    (let [^js el (create-timeline {:attrs {:autoplay-indicator ""}})
          ^js root (.-shadowRoot el)]
      (is (some? (.querySelector root "[part=indicator]"))))))

;; ── Autoplay observed attributes ────────────────────────────────────────────
(deftest autoplay-observed-attributes-test
  (testing "autoplay attributes are observed"
    (let [attrs (set (js->clj model/observed-attributes))]
      (is (contains? attrs "autoplay"))
      (is (contains? attrs "autoplay-speed"))
      (is (contains? attrs "autoplay-loop"))
      (is (contains? attrs "autoplay-indicator")))))

;; ── Disabled state ──────────────────────────────────────────────────────────
(deftest disabled-state-test
  (testing "disabled attribute freezes active index"
    (let [^js el (create-timeline)]
      (is (= -1 (.-activeIndex el)))
      (.setAttribute el "disabled" "")
      (is (= -1 (.-activeIndex el)))))
  (testing "disabled reflects as data attribute"
    (let [^js el (create-timeline {:attrs {:disabled ""}})]
      (is (.hasAttribute el "disabled")))))

;; ── Disabled stops scroll tracking ──────────────────────────────────────────
(deftest disabled-cleans-entry-attrs-test
  (testing "disabling cleans data-active from children"
    (let [^js el (create-timeline)]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              ;; Manually set data-active on first child to simulate active state
              (let [^js first-child (aget (.-children el) 0)]
                (.setAttribute first-child "data-active" "")
                (is (.hasAttribute first-child "data-active"))
                ;; Disable the component
                (.setAttribute el "disabled" "")
                ;; data-active should be cleaned
                (is (not (.hasAttribute first-child "data-active"))))
              (done)))))))))

;; ── Curved track SVG ────────────────────────────────────────────────────────
(deftest curved-track-svg-test
  (testing "curved track shows SVG elements"
    (let [^js el (create-timeline {:attrs {:track "curved"}})]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [^js root (.-shadowRoot el)
                    ^js svg (.querySelector root "[part=track-svg]")
                    ^js track-path (.querySelector root "[part=track-path]")
                    ^js fill-path (.querySelector root "[part=track-fill-path]")]
                ;; SVG and paths should exist
                (is (some? svg))
                (is (some? track-path))
                (is (some? fill-path))
                ;; fill-path should have pathLength for progress animation
                (is (= "1" (.getAttribute fill-path "pathLength"))))
              (done)))))))))

;; ── Left layout side assignment ─────────────────────────────────────────────
(deftest left-layout-side-assignment-test
  (testing "left layout assigns all children to right side"
    (let [^js el (create-timeline {:attrs {:layout "left"}})]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [children (.-children el)]
                (is (= "right" (.getAttribute (aget children 0) "data-side")))
                (is (= "right" (.getAttribute (aget children 1) "data-side")))
                (is (= "right" (.getAttribute (aget children 2) "data-side"))))
              (done)))))))))

;; ── Right layout side assignment ────────────────────────────────────────────
(deftest right-layout-side-assignment-test
  (testing "right layout assigns all children to left side"
    (let [^js el (create-timeline {:attrs {:layout "right"}})]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [children (.-children el)]
                (is (= "left" (.getAttribute (aget children 0) "data-side")))
                (is (= "left" (.getAttribute (aget children 1) "data-side")))
                (is (= "left" (.getAttribute (aget children 2) "data-side"))))
              (done)))))))))

;; ── Data-index assignment ───────────────────────────────────────────────────
(deftest data-index-assignment-test
  (testing "children receive data-index attributes"
    (let [^js el (create-timeline)]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [children (.-children el)]
                (is (= "0" (.getAttribute (aget children 0) "data-index")))
                (is (= "1" (.getAttribute (aget children 1) "data-index")))
                (is (= "2" (.getAttribute (aget children 2) "data-index"))))
              (done)))))))))

;; ── Layout attribute changes trigger side reassignment ──────────────────────
(deftest layout-change-reassigns-sides-test
  (testing "changing layout from alternating to left updates data-side"
    (let [^js el (create-timeline)]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              ;; Verify initial alternating layout
              (let [children (.-children el)]
                (is (= "left" (.getAttribute (aget children 0) "data-side")))
                ;; Switch to left layout
                (.setAttribute el "layout" "left")
                ;; Wait for rebuild
                (js/requestAnimationFrame
                 (fn [_]
                   (js/requestAnimationFrame
                    (fn [_]
                      (let [children (.-children el)]
                        (is (= "right" (.getAttribute (aget children 0) "data-side")))
                        (is (= "right" (.getAttribute (aget children 1) "data-side"))))
                      (done))))))))))))))

;; ── No-progress hides track fill ────────────────────────────────────────────
(deftest no-progress-attribute-test
  (testing "no-progress attribute is reflected"
    (let [^js el (create-timeline {:attrs {:no-progress ""}})]
      (is (true? (.-noProgress el))))))

;; ── Label attribute sets aria-label ─────────────────────────────────────────
(deftest label-change-updates-aria-test
  (testing "changing label updates aria-label on container"
    (let [^js el (create-timeline {:attrs {:label "First label"}})
          ^js root (.-shadowRoot el)
          ^js container (.querySelector root "[part=container]")]
      (is (= "First label" (.getAttribute container "aria-label")))
      (.setAttribute el "label" "Updated label")
      (is (= "Updated label" (.getAttribute container "aria-label"))))))

;; ── Data attributes on host ─────────────────────────────────────────────────
(deftest host-data-attributes-test
  (testing "host reflects layout, track, marker as data attributes"
    (let [^js el (create-timeline {:attrs {:layout "right" :track "curved" :marker "ring"}})]
      (is (= "right"  (.getAttribute el "data-layout")))
      (is (= "curved" (.getAttribute el "data-track")))
      (is (= "ring"   (.getAttribute el "data-marker"))))))

;; ── Reconnect does not duplicate listeners ──────────────────────────────────
(deftest reconnect-no-duplicate-listeners-test
  (testing "disconnect + reconnect does not double up markers"
    (let [^js el (create-timeline)]
      (async done
        (js/requestAnimationFrame
         (fn [_]
           (js/requestAnimationFrame
            (fn [_]
              (let [^js root (.-shadowRoot el)
                    markers-before (.-length (.querySelectorAll root ".tl-marker"))]
                ;; Disconnect and reconnect
                (.remove el)
                (.appendChild (.-body js/document) el)
                ;; Wait for slotchange → rebuild
                (js/requestAnimationFrame
                 (fn [_]
                   (js/requestAnimationFrame
                    (fn [_]
                      (let [markers-after (.-length (.querySelectorAll root ".tl-marker"))]
                        (is (= markers-before markers-after)))
                      (done))))))))))))))

;; ── Entries without data-date ───────────────────────────────────────────────
(deftest entries-without-dates-test
  (testing "entries without data-date do not create date labels"
    (let [^js el (create-timeline
                  {:entries [{:text "No date 1"}
                             {:text "No date 2"}
                             {:text "No date 3"}]})]
      (async done
        (js/setTimeout
         (fn []
           (let [^js root (.-shadowRoot el)
                 dates (.querySelectorAll root ".tl-date")]
             (is (= 0 (.-length dates))))
           (done))
         100)))))
