(ns baredom.components.x-drag-panel.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-drag-panel.model :as model]))

;; ── Normalisation ────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (testing "an empty map yields every default without throwing"
    (let [m (model/normalize {})]
      (is (= ""                     (:kind m)))
      (is (= ""                     (:value m)))
      (is (= model/default-label    (:label m)))
      (is (= model/grab-handle      (:grab m)))
      (is (= model/auto-scroll-auto (:auto-scroll m)))
      (is (false? (:disabled? m)))
      (is (false? (:pending? m)))
      (is (true?  (:draggable? m))))))

(deftest normalize-label-test
  (is (= model/default-label (model/normalize-label nil)))
  (is (= model/default-label (model/normalize-label "")))
  (is (= "Card A"            (model/normalize-label "Card A"))))

(deftest normalize-text-test
  (testing "absent and blank collapse to the empty string"
    (is (= "" (model/normalize-text nil)))
    (is (= "" (model/normalize-text "")))
    (is (= "task" (model/normalize-text "task")))))

(deftest normalize-grab-test
  (testing "only the exact surface literal opts out of handle mode"
    (is (= model/grab-surface (model/normalize-grab "surface")))
    (is (= model/grab-handle  (model/normalize-grab "handle")))
    (is (= model/grab-handle  (model/normalize-grab nil)))
    (is (= model/grab-handle  (model/normalize-grab "SURFACE")))
    (is (= model/grab-handle  (model/normalize-grab "nonsense")))))

(deftest normalize-auto-scroll-test
  (testing "unrecognised values keep auto-scroll on"
    (is (= model/auto-scroll-none (model/normalize-auto-scroll "none")))
    (is (= model/auto-scroll-auto (model/normalize-auto-scroll "auto")))
    (is (= model/auto-scroll-auto (model/normalize-auto-scroll nil)))
    (is (= model/auto-scroll-auto (model/normalize-auto-scroll "off")))))

(deftest draggable-test
  (testing "disabled and pending each independently suppress dragging"
    (is (true?  (:draggable? (model/normalize {}))))
    (is (false? (:draggable? (model/normalize {:disabled-present? true}))))
    (is (false? (:draggable? (model/normalize {:pending-present? true}))))
    (is (false? (:draggable? (model/normalize {:disabled-present? true
                                               :pending-present?  true}))))))

;; ── Drag geometry ────────────────────────────────────────────────────────────
(deftest drag-position-test
  (is (= {:x 90 :y 180}
         (model/drag-position {:pointer-x 100 :pointer-y 200
                               :offset-x  10  :offset-y  20})))
  (testing "a sparse drag map does not throw"
    (is (= {:x 0 :y 0} (model/drag-position {})))))

(deftest drag-delta-test
  (testing "the delta is relative to where the surface started"
    (is (= {:dx 40 :dy 60}
           (model/drag-delta {:pointer-x 100 :pointer-y 200
                              :offset-x  10  :offset-y  20
                              :start-x   50  :start-y   120}))))
  (testing "a press that never moved produces no translation"
    (is (= {:dx 0 :dy 0}
           (model/drag-delta {:pointer-x 100 :pointer-y 200
                              :offset-x  10  :offset-y  20
                              :start-x   90  :start-y   180})))))

;; ── Long press ───────────────────────────────────────────────────────────────
(deftest long-press-required-test
  (testing "only touch pointers in surface mode wait"
    (is (true? (model/long-press-required? {:pointer-type "touch"
                                            :grab model/grab-surface})))
    (is (false? (model/long-press-required? {:pointer-type "touch"
                                             :grab model/grab-handle})))
    (is (false? (model/long-press-required? {:pointer-type "mouse"
                                             :grab model/grab-surface})))
    (is (false? (model/long-press-required? {:pointer-type "pen"
                                             :grab model/grab-surface})))
    (is (false? (model/long-press-required? {})))))

(deftest travelled-test
  (testing "travel beyond the slop radius cancels a waiting press"
    (is (false? (model/travelled? {:start-x 0 :start-y 0
                                   :pointer-x 3 :pointer-y 3})))
    (is (true?  (model/travelled? {:start-x 0 :start-y 0
                                   :pointer-x 40 :pointer-y 0})))
    (is (true?  (model/travelled? {:start-x 0 :start-y 0
                                   :pointer-x 0 :pointer-y 5 :slop 2}))))
  (testing "a sparse map reads as no travel"
    (is (false? (model/travelled? {})))))

;; ── Auto-scroll ──────────────────────────────────────────────────────────────
(deftest edge-band-test
  (testing "the band is capped in pixels and as a fraction of the container"
    (is (= model/scroll-edge-max (model/edge-band 1000)))
    (is (= 15 (model/edge-band 100)))
    (is (= 0  (model/edge-band 0)))
    (is (= 0  (model/edge-band nil)))))

(deftest axis-velocity-test
  (let [args {:lo 0 :hi 1000}]
    (testing "no velocity away from either edge"
      (is (zero? (model/axis-velocity (assoc args :pos 500)))))
    (testing "negative toward lo, positive toward hi"
      (is (neg? (model/axis-velocity (assoc args :pos 10))))
      (is (pos? (model/axis-velocity (assoc args :pos 990)))))
    (testing "a pointer dragged outside the container does not exceed max speed"
      (is (= (- model/scroll-max-speed)
             (model/axis-velocity (assoc args :pos -500))))
      (is (= model/scroll-max-speed
             (model/axis-velocity (assoc args :pos 5000)))))
    (testing "a zero-sized or unmeasured container never scrolls"
      (is (zero? (model/axis-velocity {:pos 5 :lo 0 :hi 0})))
      (is (zero? (model/axis-velocity args))))))

(deftest scroll-delta-test
  (testing "delta is velocity over elapsed time, not per frame"
    (is (= 90 (model/scroll-delta 900 100)))
    (is (= 0  (model/scroll-delta 900 0)))
    (is (= 0  (model/scroll-delta nil 16)))))

;; ── Event details ────────────────────────────────────────────────────────────
(deftest event-detail-test
  (let [d (model/drag-start-detail "task" "t-1")]
    (is (= "task" (.-kind d)))
    (is (= "t-1"  (.-value d))))
  (let [d (model/drag-cancel-detail "task" "t-1" model/reason-escape)]
    (is (= model/reason-escape (.-reason d)))))

;; ── Metadata ─────────────────────────────────────────────────────────────────
(deftest property-api-test
  (testing "stays under the array-map threshold so adapter output is stable"
    (is (<= (count model/property-api) 8)))
  (testing "every property reflects an observed attribute"
    (let [observed (set (array-seq model/observed-attributes))]
      (doseq [[_ spec] model/property-api]
        (is (contains? observed (:reflects-attribute spec)))))))

(deftest event-schema-test
  (testing "no event claims to be cancelable — none has a default action"
    (doseq [[_ spec] model/event-schema]
      (is (false? (:cancelable spec))))))
