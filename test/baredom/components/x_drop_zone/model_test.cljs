(ns baredom.components.x-drop-zone.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-drop-zone.model :as model]))

;; ── Normalisation ────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (testing "an empty map yields every default without throwing"
    (let [m (model/normalize {})]
      (is (= "" (:value m)))
      (is (nil? (:accept-kinds m)))
      (is (nil? (:capacity m)))
      (is (= model/default-label (:label m)))
      (is (false? (:disabled? m)))
      (is (false? (:pending? m)))
      (is (nil? (:pending-index m)))
      (is (false? (:animate-moves? m))))))

(deftest parse-accepts-test
  (testing "absent and blank both mean accept anything, not accept nothing"
    (is (nil? (model/parse-accepts nil)))
    (is (nil? (model/parse-accepts "")))
    (is (nil? (model/parse-accepts "   "))))
  (testing "whitespace-separated kinds become a set"
    (is (= #{"task"}          (model/parse-accepts "task")))
    (is (= #{"task" "bug"}    (model/parse-accepts "task bug")))
    (is (= #{"task" "bug"}    (model/parse-accepts "  task   bug  ")))))

(deftest parse-count-test
  (testing "unreadable values are unset rather than zero"
    (is (nil? (model/parse-count nil)))
    (is (nil? (model/parse-count "")))
    (is (nil? (model/parse-count "abc")))
    (is (nil? (model/parse-count "-1"))))
  (testing "zero is a legitimate capacity"
    (is (= 0 (model/parse-count "0")))
    (is (= 5 (model/parse-count "5")))
    (is (= 5 (model/parse-count " 5 ")))))

;; ── Acceptance ───────────────────────────────────────────────────────────────
(deftest accepts-anything-test
  (testing "a zone with no accepts takes any kind"
    (is (true? (model/accepts? {:kind "task" :child-count 0})))
    (is (true? (model/accepts? {:kind "" :child-count 0})))))

(deftest accepts-kind-test
  (let [base {:accept-kinds #{"task" "bug"} :child-count 0}]
    (is (true?  (model/accepts? (assoc base :kind "task"))))
    (is (true?  (model/accepts? (assoc base :kind "bug"))))
    (is (false? (model/accepts? (assoc base :kind "note"))))
    (is (false? (model/accepts? (assoc base :kind ""))))))

(deftest accepts-capacity-test
  (let [base {:capacity 2 :kind "task"}]
    (is (true?  (model/accepts? (assoc base :child-count 0))))
    (is (true?  (model/accepts? (assoc base :child-count 1))))
    (is (false? (model/accepts? (assoc base :child-count 2))))
    (is (false? (model/accepts? (assoc base :child-count 9)))))
  (testing "a capacity of zero closes the zone"
    (is (false? (model/accepts? {:capacity 0 :kind "task" :child-count 0}))))
  (testing "an absent child count is treated as empty"
    (is (true? (model/accepts? {:capacity 1 :kind "task"})))))

(deftest accepts-disabled-test
  (is (false? (model/accepts? {:disabled? true :kind "task" :child-count 0}))))

(deftest drag-state-test
  (is (nil? (model/drag-state {})))
  (is (nil? (model/drag-state {:accepted? true})))
  (is (= model/state-over   (model/drag-state {:hovering? true :accepted? true})))
  (is (= model/state-reject (model/drag-state {:hovering? true :accepted? false}))))

;; ── Insertion geometry ───────────────────────────────────────────────────────
(def ^:private spans
  [{:start 0   :end 100}
   {:start 110 :end 210}
   {:start 220 :end 320}])

(deftest span-midpoints-test
  (is (= [50 160 270] (model/span-midpoints spans)))
  (is (= [] (model/span-midpoints []))))

(deftest insert-index-test
  (let [mids (model/span-midpoints spans)]
    (testing "before every midpoint is the head of the list"
      (is (= 0 (model/insert-index mids 0)))
      (is (= 0 (model/insert-index mids 49))))
    (testing "past a midpoint means after that child"
      (is (= 1 (model/insert-index mids 51)))
      (is (= 2 (model/insert-index mids 161))))
    (testing "past every midpoint is the tail"
      (is (= 3 (model/insert-index mids 999))))
    (testing "an empty zone always yields index zero"
      (is (= 0 (model/insert-index [] 500))))))

(deftest boundary-offsets-test
  (testing "an empty zone yields one centred offset"
    (is (= [50] (model/boundary-offsets [] 0 100))))
  (testing "n children yield n+1 boundaries, at the gaps"
    (let [b (model/boundary-offsets spans 0 400)]
      (is (= 4 (count b)))
      (is (= 0   (first b)))
      (is (= 105 (nth b 1)))
      (is (= 215 (nth b 2)))
      (is (= 320 (last b))))))

(deftest caret-offset-test
  (let [b (model/boundary-offsets spans 0 400)]
    (is (= 0   (model/caret-offset b 0)))
    (is (= 320 (model/caret-offset b 3)))
    (testing "a stale author-set index parks at the end rather than throwing"
      (is (= 320 (model/caret-offset b 99))))
    (testing "a negative index parks at the head"
      (is (= 0 (model/caret-offset b -5))))
    (testing "no index and no boundaries render nothing"
      (is (nil? (model/caret-offset b nil)))
      (is (nil? (model/caret-offset [] 0))))))

;; ── Event details ────────────────────────────────────────────────────────────
(deftest hover-detail-test
  (let [d (model/hover-detail "task" "t-1")]
    (is (= "task" (.-kind d)))
    (is (= "t-1"  (.-value d)))))

(deftest normalize-value-test
  (testing "the zone's identity key collapses absent to empty, like the panel's"
    (is (= ""      (model/normalize-value nil)))
    (is (= ""      (model/normalize-value "")))
    (is (= "doing" (model/normalize-value "doing")))))

(deftest drop-detail-test
  (let [panel #js {}
        d     (model/drop-detail "task" "t-1" "todo" "doing" 2 panel)]
    (is (= "task"  (.-kind d)))
    (is (= "t-1"   (.-value d)))
    (is (= "todo"  (.-from d)))
    (is (= "doing" (.-to d)))
    (is (= 2       (.-index d)))
    (is (identical? panel (.-panel d)))))

(deftest drop-detail-nil-source-test
  (testing "nil `from` means the panel came from outside any zone — distinct
            from a source zone the author gave no value"
    (let [no-source   (model/drop-detail "task" "t-1" nil "doing" 0 #js {})
          unnamed-src (model/drop-detail "task" "t-1" ""  "doing" 0 #js {})]
      (is (nil? (.-from no-source)))
      (is (= "" (.-from unnamed-src))))))

;; ── Methods ──────────────────────────────────────────────────────────────────
(deftest method-api-test
  (testing "reserve/release are declared so the adapters generate signatures"
    (is (contains? model/method-api :reserve))
    (is (contains? model/method-api :release))
    (is (= [] (:args (:release model/method-api)))))
  (testing "every arg carries the {:name :type} shape the codegen expects"
    (doseq [[_ spec] model/method-api]
      (doseq [arg (:args spec)]
        (is (string? (:name arg)))
        (is (some? (:type arg)))))))

;; ── Metadata ─────────────────────────────────────────────────────────────────
(deftest property-api-test
  (testing "stays under the array-map threshold so adapter output is stable"
    (is (<= (count model/property-api) 8)))
  (testing "every property reflects an observed attribute"
    (let [observed (set (array-seq model/observed-attributes))]
      (doseq [[_ spec] model/property-api]
        (is (contains? observed (:reflects-attribute spec)))))))

(deftest event-schema-test
  (testing "no event claims to be cancelable — the app owns the landing"
    (doseq [[_ spec] model/event-schema]
      (is (false? (:cancelable spec))))))
