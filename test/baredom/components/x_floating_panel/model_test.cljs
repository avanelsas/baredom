(ns baredom.components.x-floating-panel.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-floating-panel.model :as model]))

;; ── normalize-label ──────────────────────────────────────────────────────────
(deftest normalize-label-test
  (testing "non-empty string is returned as-is"
    (is (= "Inspector" (model/normalize-label "Inspector"))))
  (testing "empty string falls back to default"
    (is (= model/default-label (model/normalize-label ""))))
  (testing "nil falls back to default"
    (is (= model/default-label (model/normalize-label nil))))
  (testing "default-label is Floating panel"
    (is (= "Floating panel" model/default-label))))

;; ── parse-coordinate ─────────────────────────────────────────────────────────
(deftest parse-coordinate-test
  (testing "integers parse"
    (is (= 120 (model/parse-coordinate "120"))))
  (testing "negative values parse — a panel may hang off the left edge"
    (is (= -40 (model/parse-coordinate "-40"))))
  (testing "fractional values parse"
    (is (= 12.5 (model/parse-coordinate "12.5"))))
  (testing "surrounding whitespace is tolerated"
    (is (= 8 (model/parse-coordinate "  8  "))))
  (testing "zero parses rather than being confused with absent"
    (is (= 0 (model/parse-coordinate "0"))))
  (testing "nil is absent"
    (is (nil? (model/parse-coordinate nil))))
  (testing "empty and blank strings are absent"
    (is (nil? (model/parse-coordinate "")))
    (is (nil? (model/parse-coordinate "   "))))
  (testing "non-numeric input is absent, not zero"
    (is (nil? (model/parse-coordinate "left")))
    (is (nil? (model/parse-coordinate "NaN"))))
  (testing "infinities are rejected"
    (is (nil? (model/parse-coordinate "Infinity")))))

;; ── parse-step ───────────────────────────────────────────────────────────────
(deftest parse-step-test
  (testing "a positive number is accepted"
    (is (= 25 (model/parse-step "25")))
    (is (= 2.5 (model/parse-step "2.5"))))
  (testing "absent falls back to the default"
    (is (= model/default-step (model/parse-step nil)))
    (is (= model/default-step (model/parse-step ""))))
  (testing "non-numeric falls back to the default"
    (is (= model/default-step (model/parse-step "fast"))))
  (testing "zero and negatives fall back — a step must move the panel"
    (is (= model/default-step (model/parse-step "0")))
    (is (= model/default-step (model/parse-step "-5"))))
  (testing "default-step is 10"
    (is (= 10 model/default-step))))

;; ── arrow-delta ──────────────────────────────────────────────────────────────
(deftest arrow-delta-test
  (testing "each arrow key maps to a unit direction"
    (is (= {:dx -1 :dy 0} (model/arrow-delta "ArrowLeft")))
    (is (= {:dx 1  :dy 0} (model/arrow-delta "ArrowRight")))
    (is (= {:dx 0  :dy -1} (model/arrow-delta "ArrowUp")))
    (is (= {:dx 0  :dy 1} (model/arrow-delta "ArrowDown"))))
  (testing "non-arrow keys yield nil so the handler can ignore them"
    (is (nil? (model/arrow-delta "Enter")))
    (is (nil? (model/arrow-delta "Escape")))
    (is (nil? (model/arrow-delta nil)))))

;; ── drag-position ────────────────────────────────────────────────────────────
(deftest drag-position-test
  (testing "pointer minus grab offset gives the panel top-left"
    (is (= {:x 90 :y 140}
           (model/drag-position {:pointer-x 100 :pointer-y 150
                                 :offset-x  10  :offset-y  10}))))
  (testing "grabbing at the origin tracks the pointer exactly"
    (is (= {:x 42 :y 7}
           (model/drag-position {:pointer-x 42 :pointer-y 7
                                 :offset-x  0  :offset-y  0})))))

;; ── drag-moved? ──────────────────────────────────────────────────────────────
(def ^:private grab
  {:pointer-id 1 :offset-x 10 :offset-y 10
   :pointer-x 110 :pointer-y 110 :start-x 100 :start-y 100})

(deftest drag-moved?-test
  (testing "a press that never travelled is a click, not a drag"
    (is (false? (model/drag-moved? grab))))
  (testing "any travel counts"
    (is (true? (model/drag-moved? (assoc grab :pointer-x 111))))
    (is (true? (model/drag-moved? (assoc grab :pointer-y 111))))))

;; ── nudge-position ───────────────────────────────────────────────────────────
(deftest nudge-position-test
  (testing "a step is applied in the given direction"
    (is (= {:x 90 :y 100}
           (model/nudge-position {:x 100 :y 100 :dx -1 :dy 0 :step 10})))
    (is (= {:x 100 :y 110}
           (model/nudge-position {:x 100 :y 100 :dx 0 :dy 1 :step 10}))))
  (testing "the fine step moves one pixel"
    (is (= {:x 101 :y 100}
           (model/nudge-position {:x 100 :y 100 :dx 1 :dy 0
                                  :step model/fine-step}))))
  (testing "nil coordinates are treated as the origin"
    (is (= {:x 10 :y 0}
           (model/nudge-position {:x nil :y nil :dx 1 :dy 0 :step 10})))))

;; ── clamp-position ───────────────────────────────────────────────────────────
(def ^:private viewport
  {:w 300 :handle-h 30 :vw 1000 :vh 800})

(defn- clamp [x y]
  (model/clamp-position (merge viewport {:x x :y y})))

(deftest clamp-position-test
  (testing "a position well inside the viewport is untouched"
    (is (= {:x 100 :y 100} (clamp 100 100))))
  (testing "the panel may hang off the left edge but stays min-visible on screen"
    (is (= {:x (- model/min-visible 300) :y 0} (clamp -9999 -9999))))
  (testing "the panel may hang off the right edge but stays min-visible on screen"
    (is (= {:x (- 1000 model/min-visible) :y 0} (clamp 9999 0))))
  (testing "the handle never goes above the viewport top"
    (is (= 0 (:y (clamp 100 -50)))))
  (testing "the handle bar remains fully on screen at the bottom"
    (is (= (- 800 30) (:y (clamp 100 9999)))))
  (testing "a panel narrower than min-visible cannot be pushed off screen"
    (is (= {:x 0 :y 0}
           (model/clamp-position {:x -100 :y 0 :w 20 :handle-h 30
                                  :vw 1000 :vh 800}))))
  (testing "a viewport shorter than the handle bar clamps to the top"
    (is (= 0 (:y (model/clamp-position {:x 0 :y 500 :w 300 :handle-h 30
                                        :vw 1000 :vh 20}))))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (testing "a sparse map yields strict booleans, never nil"
    (let [m (model/normalize {})]
      (is (false? (:open? m)))
      (is (false? (:closable? m)))
      (is (false? (:focus-on-open? m)))
      (is (false? (:header-slotted? m)))
      (is (nil? (:x m)))
      (is (nil? (:y m)))
      (is (= model/default-step (:step m)))
      (is (= model/default-label (:label m))))))

(deftest normalize-omits-resizable-test
  (testing "resizable drives CSS only — carrying it in the model would add a
            field to compare on every attribute change for no effect"
    (is (not (contains? (model/normalize {}) :resizable?)))))

(deftest normalize-full-test
  (testing "every raw input is projected onto the view model"
    (let [m (model/normalize {:open-present?          true
                              :x-raw                  "40"
                              :y-raw                  "80"
                              :closable-present?      true
                              :label-raw              "Inspector"
                              :focus-on-open-present? true
                              :step-raw               "25"
                              :header-slotted?        true})]
      (is (true? (:open? m)))
      (is (= 40 (:x m)))
      (is (= 80 (:y m)))
      (is (true? (:closable? m)))
      (is (= 25 (:step m)))
      (is (= "Inspector" (:label m)))
      (is (true? (:focus-on-open? m)))
      (is (true? (:header-slotted? m))))))

(deftest normalize-invalid-coordinates-test
  (testing "invalid coordinates normalise to nil, not zero"
    (let [m (model/normalize {:x-raw "left" :y-raw ""})]
      (is (nil? (:x m)))
      (is (nil? (:y m))))))

;; ── Event detail builders ────────────────────────────────────────────────────
(deftest toggle-event-detail-test
  (is (true? (.-open (model/toggle-event-detail true))))
  (is (false? (.-open (model/toggle-event-detail false)))))

(deftest dismiss-event-detail-test
  (is (= model/reason-escape
         (.-reason (model/dismiss-event-detail model/reason-escape)))))

(deftest move-event-detail-test
  (let [d (model/move-event-detail 10 20 model/source-pointer)]
    (is (= 10 (.-x d)))
    (is (= 20 (.-y d)))
    (is (= "pointer" (.-source d)))))

;; ── Metadata ─────────────────────────────────────────────────────────────────
(deftest observed-attributes-test
  (let [attrs (set (array-seq model/observed-attributes))]
    (is (contains? attrs "open"))
    (is (contains? attrs "x"))
    (is (contains? attrs "y"))
    (is (contains? attrs "closable"))
    (is (contains? attrs "resizable"))
    (is (contains? attrs "label"))
    (is (contains? attrs "focus-on-open"))
    (is (contains? attrs "step"))))

(deftest property-api-test
  (testing "every property reflects an observed attribute"
    (let [attrs (set (array-seq model/observed-attributes))]
      (doseq [[_ spec] model/property-api]
        (is (contains? attrs (:reflects-attribute spec))))))
  (testing "coordinates are numbers"
    (is (= 'number (:type (:x model/property-api))))
    (is (= 'number (:type (:y model/property-api))))))

(deftest event-schema-test
  (testing "only the dismiss request is cancelable"
    (is (false? (:cancelable (get model/event-schema model/event-toggle))))
    (is (true? (:cancelable (get model/event-schema model/event-dismiss-request))))
    (is (false? (:cancelable (get model/event-schema model/event-move))))))

(deftest method-api-test
  (is (= #{:show :hide :toggle} (set (keys model/method-api)))))
