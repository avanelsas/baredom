(ns baredom.components.x-drop-zone.x-drop-zone-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-drop-zone.x-drop-zone :as zone]
            [baredom.components.x-drop-zone.model       :as model]
            [baredom.components.x-drag-panel.x-drag-panel :as panel]
            [baredom.components.x-drag-panel.model        :as panel-model]))

(zone/init!)
(panel/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [node (.querySelectorAll js/document panel-model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

;; ── Builders ─────────────────────────────────────────────────────────────────
;; Explicit dimensions throughout: an empty custom element has zero size in the
;; headless test page, and every insertion-geometry assertion reads real rects.

(defn ^js make-panel [kind value]
  (let [^js el (.createElement js/document panel-model/tag-name)]
    (.setAttribute el panel-model/attr-kind kind)
    (.setAttribute el panel-model/attr-value value)
    (.setAttribute el panel-model/attr-label (str "Panel " value))
    (set! (.. el -style -height) "100px")
    (set! (.. el -style -width) "200px")
    el))

(defn ^js make-zone [& {:keys [value accepts max-count panels]}]
  (let [^js el (.createElement js/document model/tag-name)]
    (when value     (.setAttribute el model/attr-value value))
    (when accepts   (.setAttribute el model/attr-accepts accepts))
    (when max-count (.setAttribute el model/attr-max (str max-count)))
    (set! (.. el -style -width) "220px")
    (doseq [p (or panels [])]
      (.appendChild el p))
    (.appendChild (.-body js/document) el)
    el))

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn caret-hidden? [^js el]
  (.hasAttribute (shadow-part el "[part=caret]") "hidden"))

(defn collect! [^js el event-name]
  (let [seen (atom [])]
    (.addEventListener el event-name
                       (fn capture-event [^js e] (swap! seen conj (.-detail e))))
    seen))

;; ── Registration and structure ───────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [^js el (make-zone)]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=zone]")))
    (is (some? (shadow-part el "[part=caret]")))
    (is (some? (shadow-part el "[part=empty]")))
    (testing "the caret starts hidden"
      (is (caret-hidden? el)))))

(deftest host-aria-test
  (let [^js el (make-zone)]
    (is (= "group" (.getAttribute el "role")))
    (is (= model/default-label (.getAttribute el "aria-label")))
    (testing "a custom label reaches the accessible name"
      (.setAttribute el model/attr-label "In progress")
      (is (= "In progress" (.getAttribute el "aria-label"))))))

;; ── Empty state ──────────────────────────────────────────────────────────────
(deftest empty-state-test
  (let [^js el (make-zone)]
    (testing "the empty section shows while the zone holds no panels"
      (is (not (.hasAttribute (shadow-part el "[part=empty]") "hidden"))))))

(deftest empty-state-hidden-with-children-test
  (let [^js el (make-zone :panels [(make-panel "task" "t-1")])]
    (is (.hasAttribute (shadow-part el "[part=empty]") "hidden"))))

;; ── Acceptance ───────────────────────────────────────────────────────────────
(deftest accepts-anything-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone)]
    (is (true? (zone/accepts-panel? z p)))))

(deftest accepts-kind-test
  (let [^js task (make-panel "task" "t-1")
        ^js note (make-panel "note" "n-1")
        ^js z    (make-zone :accepts "task bug")]
    (is (true?  (zone/accepts-panel? z task)))
    (is (false? (zone/accepts-panel? z note)))))

(deftest accepts-capacity-test
  (let [^js existing (make-panel "task" "t-1")
        ^js incoming (make-panel "task" "t-2")
        ^js z        (make-zone :max-count 1 :panels [existing])]
    (testing "a full zone refuses an outside panel"
      (is (false? (zone/accepts-panel? z incoming))))
    (testing "but never refuses a panel it already holds — that would block reordering"
      (is (true? (zone/accepts-panel? z existing))))))

(deftest accepts-disabled-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone)]
    (.setAttribute z model/attr-disabled "")
    (is (false? (zone/accepts-panel? z p)))))

;; ── Hover ────────────────────────────────────────────────────────────────────
(deftest hover-accepted-test
  (let [^js p     (make-panel "task" "t-1")
        ^js z     (make-zone :accepts "task")
        entered   (collect! z model/event-enter)]
    (zone/hover! z p)
    (is (= model/state-over (.getAttribute z model/attr-drag-state)))
    (is (= 1 (count @entered)))
    (is (= "task" (.-kind (first @entered))))
    (is (= "t-1"  (.-value (first @entered))))
    (testing "an accepted hover shows the caret"
      (is (false? (caret-hidden? z))))))

(deftest hover-rejected-test
  (let [^js p   (make-panel "note" "n-1")
        ^js z   (make-zone :accepts "task")
        entered (collect! z model/event-enter)]
    (zone/hover! z p)
    (testing "a refused panel still reports the hover, so an app can tell it apart from never hovering"
      (is (= 1 (count @entered))))
    (is (= model/state-reject (.getAttribute z model/attr-drag-state)))
    (testing "but no caret — there is no insertion point being offered"
      (is (caret-hidden? z)))))

(deftest unhover-test
  (let [^js p  (make-panel "task" "t-1")
        ^js z  (make-zone :accepts "task")
        left   (collect! z model/event-leave)]
    (zone/hover! z p)
    (zone/unhover! z p)
    (is (nil? (.getAttribute z model/attr-drag-state)))
    (is (= 1 (count @left)))
    (is (caret-hidden? z))))

(deftest unhover-without-hover-is-silent-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone)
        left  (collect! z model/event-leave)]
    (zone/unhover! z p)
    (is (= 0 (count @left)))))

;; ── Insertion index ──────────────────────────────────────────────────────────
(deftest index-at-test
  (let [^js a (make-panel "task" "a")
        ^js b (make-panel "task" "b")
        ^js z (make-zone :panels [a b])
        ^js incoming (make-panel "task" "c")
        ^js rect (.getBoundingClientRect z)]
    (testing "above every midpoint is the head"
      (is (= 0 (zone/index-at z incoming (.-left rect) (.-top rect)))))
    (testing "below every midpoint is the tail"
      (is (= 2 (zone/index-at z incoming (.-left rect) (+ (.-bottom rect) 500)))))))

(deftest index-excludes-dragged-panel-test
  (let [^js a (make-panel "task" "a")
        ^js z (make-zone :panels [a])
        ^js rect (.getBoundingClientRect z)]
    (testing "a panel dragged within its own zone is measured against the list without it,
              so dropping it back where it started yields its original index"
      (is (= 0 (zone/index-at z a (.-left rect) (.-top rect)))))))

;; ── Drop ─────────────────────────────────────────────────────────────────────
(deftest commit-drop-test
  (let [^js a    (make-panel "task" "a")
        ^js z    (make-zone :value "doing" :panels [a])
        ^js from (make-zone :value "todo")
        ^js incoming (make-panel "task" "c")
        dropped  (collect! z model/event-drop)
        ^js rect (.getBoundingClientRect z)]
    (zone/hover! z incoming)
    (zone/commit-drop! z incoming from (.-left rect) (.-top rect))
    (is (= 1 (count @dropped)))
    (let [^js d (first @dropped)]
      (is (= "task"  (.-kind d)))
      (is (= "c"     (.-value d)))
      (is (= 0       (.-index d)))
      (testing "both endpoints are opaque strings that survive a re-render"
        (is (= "todo"  (.-from d)))
        (is (= "doing" (.-to d))))
      (is (identical? incoming (.-panel d))))
    (testing "the zone never moves the panel — the app owns the landing"
      (is (nil? (.-parentElement incoming))))
    (testing "hover state is torn down by the drop"
      (is (nil? (.getAttribute z model/attr-drag-state)))
      (is (caret-hidden? z)))))

(deftest drop-from-outside-any-zone-test
  (let [^js z        (make-zone :value "doing")
        ^js incoming (make-panel "task" "c")
        dropped      (collect! z model/event-drop)
        ^js rect     (.getBoundingClientRect z)]
    (zone/commit-drop! z incoming nil (.-left rect) (.-top rect))
    (testing "nil `from` is distinct from a source zone with no value"
      (is (nil? (.-from (first @dropped)))))))

(deftest drop-from-unnamed-zone-test
  (let [^js z        (make-zone :value "doing")
        ^js from     (make-zone)
        ^js incoming (make-panel "task" "c")
        dropped      (collect! z model/event-drop)
        ^js rect     (.getBoundingClientRect z)]
    (zone/commit-drop! z incoming from (.-left rect) (.-top rect))
    (is (= "" (.-from (first @dropped))))))

(deftest commit-keyboard-drop-appends-test
  (let [^js a (make-panel "task" "a")
        ^js b (make-panel "task" "b")
        ^js z (make-zone :panels [a b])
        ^js incoming (make-panel "task" "c")
        dropped (collect! z model/event-drop)]
    (zone/commit-keyboard-drop! z incoming nil)
    (let [^js d (first @dropped)]
      (is (= 2 (.-index d)))
      (testing "a null source zone is reported rather than omitted"
        (is (nil? (.-from d)))))))

;; ── Pending window ───────────────────────────────────────────────────────────
(deftest pending-busy-test
  (let [^js z (make-zone)]
    (.setAttribute z model/attr-pending "")
    (is (= "true" (.getAttribute z "aria-busy")))
    (.removeAttribute z model/attr-pending)
    (is (nil? (.getAttribute z "aria-busy")))))

(deftest pending-index-shows-caret-test
  (let [^js a (make-panel "task" "a")
        ^js z (make-zone :panels [a])]
    (testing "pending alone is busy-only — the right rendering for an unordered bucket"
      (.setAttribute z model/attr-pending "")
      (is (caret-hidden? z)))
    (testing "adding an index reserves a position"
      (.setAttribute z model/attr-pending-index "1")
      (is (false? (caret-hidden? z))))
    (testing "clearing pending clears the reservation"
      (.removeAttribute z model/attr-pending)
      (is (caret-hidden? z)))))

;; ── reserve() / release() ────────────────────────────────────────────────────
;; Driven through the element methods rather than the namespace fns, so the
;; defineProperty wiring is covered too.

(deftest reserve-sets-the-whole-trio-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone :value "doing" :panels [p])]
    (.reserve z p 1)
    (is (.hasAttribute p panel-model/attr-pending))
    (is (.hasAttribute z model/attr-pending))
    (is (= "1" (.getAttribute z model/attr-pending-index)))
    (is (= "true" (.getAttribute z "aria-busy")))))

(deftest release-clears-the-whole-trio-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone :value "doing" :panels [p])]
    (.reserve z p 1)
    (.release z)
    (is (not (.hasAttribute p panel-model/attr-pending)))
    (is (not (.hasAttribute z model/attr-pending)))
    (is (nil? (.getAttribute z model/attr-pending-index)))))

(deftest release-is-idempotent-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone :panels [p])]
    (.reserve z p 0)
    (.release z)
    (.release z)
    (testing "releasing twice is harmless — nothing to strand"
      (is (not (.hasAttribute p panel-model/attr-pending)))
      (is (not (.hasAttribute z model/attr-pending))))))

(deftest release-without-reserve-is-inert-test
  (let [^js z (make-zone)]
    (.release z)
    (is (not (.hasAttribute z model/attr-pending)))))

(deftest re-reserving-frees-the-previous-panel-test
  (let [^js first-panel  (make-panel "task" "t-1")
        ^js second-panel (make-panel "task" "t-2")
        ^js z            (make-zone :panels [first-panel second-panel])]
    (.reserve z first-panel 0)
    (.reserve z second-panel 1)
    (testing "a second drop must not strand the first panel in permanent pending"
      (is (not (.hasAttribute first-panel panel-model/attr-pending))))
    (is (.hasAttribute second-panel panel-model/attr-pending))
    (is (= "1" (.getAttribute z model/attr-pending-index)))))

(deftest reserve-without-index-is-busy-only-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone :panels [p])]
    (.reserve z p nil)
    (is (.hasAttribute z model/attr-pending))
    (testing "no index reserves no position — right for an unordered bucket"
      (is (nil? (.getAttribute z model/attr-pending-index)))
      (is (caret-hidden? z)))))

(deftest reserve-survives-the-panel-being-destroyed-test
  (let [^js p (make-panel "task" "t-1")
        ^js z (make-zone :panels [p])]
    (.reserve z p 0)
    (.remove p)
    (.release z)
    (testing "a confirmation re-render that replaced the panel leaves nothing stale"
      (is (not (.hasAttribute z model/attr-pending))))))

(deftest pending-does-not-count-toward-capacity-test
  (let [^js existing (make-panel "task" "t-1")
        ^js incoming (make-panel "task" "t-2")
        ^js z        (make-zone :max-count 2 :panels [existing])]
    (.setAttribute z model/attr-pending "")
    (.setAttribute z model/attr-pending-index "1")
    (testing "a reservation is not an arrival — counting it would refuse a second
              drop the server may well accept"
      (is (true? (zone/accepts-panel? z incoming))))))

;; ── The component never touches light DOM ────────────────────────────────────
(deftest never-reorders-light-dom-test
  (let [^js a (make-panel "task" "a")
        ^js b (make-panel "task" "b")
        ^js z (make-zone :panels [a b])
        ^js incoming (make-panel "task" "c")
        ^js rect (.getBoundingClientRect z)]
    (zone/hover! z incoming)
    (zone/preview! z incoming (.-left rect) (.-top rect))
    (zone/commit-drop! z incoming nil (.-left rect) (.-top rect))
    (is (= 2 (.-length (.-children z))))
    (is (identical? a (aget (.-children z) 0)))
    (is (identical? b (aget (.-children z) 1)))))
