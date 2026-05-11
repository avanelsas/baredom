(ns baredom.dev.x-trace-history.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [clojure.string]
            [goog.object :as gobj]
            [baredom.dev.x-trace-history.model :as model]))

;; ── tag-of ──────────────────────────────────────────────────────────────────

(deftest tag-of-nil-test
  (testing "nil element falls back to 'document'"
    (is (= "document" (model/tag-of nil)))))

(deftest tag-of-document-test
  (testing "js/document falls back to 'document' (no tagName)"
    (is (= "document" (model/tag-of js/document)))))

(deftest tag-of-element-test
  (testing "element tagName lowercased"
    (let [el (.createElement js/document "X-BUTTON")]
      (is (= "x-button" (model/tag-of el))))))

;; ── make-record ─────────────────────────────────────────────────────────────

(deftest make-record-shape-test
  (testing "all schema fields populated; field names are string-keyed"
    (let [^js r (model/make-record
                 {:type :event/dispatch
                  :tag "x-button"
                  :component-id 42
                  :cause-id 13
                  :event-name "x-button:click"
                  :detail #js {:value 42}
                  :cancelable? false
                  :default-prevented? false}
                 7 100.5)
          ^js detail (.-detail r)]
      (is (= 1                  (.-schemaVersion r)))
      (is (= 7                  (.-id r)))
      (is (= 100.5              (.-t r)))
      (is (= "event/dispatch"   (.-type r)))
      (is (= "x-button"         (.-tag r)))
      (is (= 42                 (.-componentId r)))
      (is (= 13                 (.-causeId r)))
      (is (= "x-button:click"   (.-eventName r)))
      (is (= 42                 (.-value detail)))
      (is (false?               (.-cancelable r)))
      (is (false?               (.-defaultPrevented r))))))

(deftest make-record-default-cause-id-test
  (testing "absent :cause-id surfaces as null (top-level / no enclosing dispatch)"
    (let [^js r (model/make-record
                 {:type :event/dispatch
                  :tag "x-button"
                  :component-id 1
                  :event-name "click"
                  :detail nil
                  :cancelable? false
                  :default-prevented? false}
                 0 0)]
      (is (nil? (.-causeId r))))))

(deftest make-record-nil-component-id-test
  (testing "nil component-id surfaces as null (e.g. for document-target events)"
    (let [^js r (model/make-record
                 {:type :event/dispatch-document
                  :tag "document"
                  :component-id nil
                  :event-name "x-foo:disconnected"
                  :detail nil
                  :cancelable? false
                  :default-prevented? false}
                 0 0)]
      (is (nil? (.-componentId r))))))

(deftest make-record-cancelable-test
  (testing "cancelable + defaultPrevented preserved"
    (let [r (model/make-record
             {:type :event/dispatch-cancelable
              :tag "x-modal"
              :event-name "x-modal:before-close"
              :detail #js {}
              :cancelable? true
              :default-prevented? true}
             0 0)]
      (is (= "event/dispatch-cancelable" (.-type r)))
      (is (true? (.-cancelable r)))
      (is (true? (.-defaultPrevented r))))))

(deftest make-record-plain-keyword-type-test
  (testing "non-namespaced :type keyword"
    ;; make-record warns on unknown types (developer-typo guard). :foo
    ;; is not a real type — this test only checks that the type field
    ;; serialises plain keywords as 'foo' (not ':foo'). Silence the
    ;; expected console.warn so the test output stays clean.
    (let [orig (.-warn js/console)]
      (try
        (set! (.-warn js/console) (fn [& _] nil))
        (let [r (model/make-record
                 {:type :foo :tag "x" :event-name "" :detail nil
                  :cancelable? false :default-prevented? false}
                 0 0)]
          (is (= "foo" (.-type r))))
        (finally
          (set! (.-warn js/console) orig))))))

(deftest make-record-nil-detail-test
  (testing "nil detail surfaces as null"
    (let [r (model/make-record
             {:type :event/dispatch :tag "x-button" :event-name "x"
              :detail nil :cancelable? false :default-prevented? false}
             0 0)]
      (is (nil? (.-detail r))))))

(deftest make-record-cljs-detail-test
  (testing "CLJS-map detail is converted to JS"
    (let [^js r (model/make-record
                 {:type :event/dispatch :tag "x-button" :event-name "x"
                  :detail {:foo 1 :bar "two"}
                  :cancelable? false :default-prevented? false}
                 0 0)
          ^js detail (.-detail r)]
      (is (= 1     (.-foo detail)))
      (is (= "two" (.-bar detail))))))

(deftest make-record-falsy-flags-test
  (testing "cancelable? / default-prevented? coerce to boolean (no nil leak)"
    (let [r (model/make-record
             {:type :event/dispatch :tag "x" :event-name ""
              :detail nil :cancelable? nil :default-prevented? nil}
             0 0)]
      (is (false? (.-cancelable r)))
      (is (false? (.-defaultPrevented r))))))

;; ── make-record: state/instance-field-set ──────────────────────────────────

(deftest make-record-state-set-test
  (testing "state/instance-field-set has field + value, no event fields"
    (let [^js r (model/make-record
                 {:type :state/instance-field-set
                  :tag "x-button"
                  :field "__xButtonModel"
                  :value {:variant "primary" :size 42}}
                 0 0)
          ^js value (.-value r)]
      (is (= "state/instance-field-set" (.-type r)))
      (is (= "__xButtonModel"           (.-field r)))
      (is (= "primary"                  (.-variant value)))
      (is (= 42                         (.-size value)))
      (is (undefined?                   (.-eventName r)))
      (is (undefined?                   (.-attribute r))))))

;; ── make-record: dom/attribute-set + dom/attribute-removed ─────────────────

(deftest make-record-attr-set-test
  (testing "dom/attribute-set has attribute + value"
    (let [^js r (model/make-record
                 {:type :dom/attribute-set
                  :tag "x-button"
                  :attribute "disabled"
                  :value ""}
                 0 0)]
      (is (= "dom/attribute-set" (.-type r)))
      (is (= "disabled"          (.-attribute r)))
      (is (= ""                  (.-value r))))))

(deftest make-record-attr-removed-test
  (testing "dom/attribute-removed has attribute, no value"
    (let [^js r (model/make-record
                 {:type :dom/attribute-removed
                  :tag "x-button"
                  :attribute "disabled"}
                 0 0)]
      (is (= "dom/attribute-removed" (.-type r)))
      (is (= "disabled"              (.-attribute r)))
      (is (undefined?                (.-value r))))))

;; ── make-record: lifecycle records ──────────────────────────────────────────

(deftest make-record-lifecycle-connected-test
  (testing "lifecycle/connected has only common fields"
    (let [^js r (model/make-record
                 {:type :lifecycle/connected :tag "x-button"}
                 0 0)]
      (is (= "lifecycle/connected" (.-type r)))
      (is (= "x-button"            (.-tag r)))
      (is (undefined?              (.-eventName r)))
      (is (undefined?              (.-attribute r))))))

(deftest make-record-lifecycle-disconnected-test
  (testing "lifecycle/disconnected has only common fields"
    (let [^js r (model/make-record
                 {:type :lifecycle/disconnected :tag "x-button"}
                 0 0)]
      (is (= "lifecycle/disconnected" (.-type r))))))

(deftest make-record-lifecycle-attribute-changed-test
  (testing "lifecycle/attribute-changed has attribute, oldValue, newValue"
    (let [^js r (model/make-record
                 {:type :lifecycle/attribute-changed
                  :tag "x-button"
                  :attribute "disabled"
                  :old-value nil
                  :new-value ""}
                 0 0)]
      (is (= "lifecycle/attribute-changed" (.-type r)))
      (is (= "disabled" (.-attribute r)))
      (is (nil? (.-oldValue r)))
      (is (= "" (.-newValue r))))))

;; ── push-record ─────────────────────────────────────────────────────────────

(deftest push-record-under-capacity-test
  (testing "appends until capacity"
    (let [b0 model/empty-records
          b1 (model/push-record b0 :a 3)
          b2 (model/push-record b1 :b 3)
          b3 (model/push-record b2 :c 3)]
      (is (= 1 (count b1)))
      (is (= 3 (count b3)))
      (is (= [:a :b :c] (vec b3))))))

(deftest push-record-at-capacity-drops-oldest-test
  (testing "FIFO eviction at capacity"
    (let [b (-> model/empty-records
                (model/push-record :a 3)
                (model/push-record :b 3)
                (model/push-record :c 3)
                (model/push-record :d 3))]
      (is (= 3 (count b)))
      (is (= [:b :c :d] (vec b))))))

(deftest push-record-capacity-one-test
  (testing "capacity of 1 always keeps just the latest"
    (let [b (-> model/empty-records
                (model/push-record :a 1)
                (model/push-record :b 1)
                (model/push-record :c 1))]
      (is (= 1 (count b)))
      (is (= [:c] (vec b))))))

;; ── enabled? ────────────────────────────────────────────────────────────────

(defn- fake-window
  "Build a JS-shape window-like object for enabled? testing."
  [{:keys [search flag]}]
  (let [^js w (js-obj)]
    (gobj/set w "location" (js-obj "search" (or search "")))
    (when (some? flag)
      (gobj/set w "BAREDOM_TRACE_HISTORY" flag))
    w))

(deftest enabled-false-by-default-test
  (testing "no URL param + no flag = disabled"
    (is (false? (boolean (model/enabled? (fake-window {})))))))

(deftest enabled-via-url-param-test
  (testing "URL search containing baredom-trace-history activates"
    (is (true? (boolean (model/enabled?
                          (fake-window {:search "?baredom-trace-history"})))))))

(deftest enabled-via-window-flag-test
  (testing "window.BAREDOM_TRACE_HISTORY = true activates"
    (is (true? (model/enabled? (fake-window {:flag true}))))))

(deftest enabled-flag-truthy-but-not-true-test
  (testing "BAREDOM_TRACE_HISTORY must be exactly true or \"raw\"
            (other truthy strings ignored)"
    (is (false? (model/enabled? (fake-window {:flag "yes"}))))))

(deftest enabled-via-raw-flag-test
  (testing "BAREDOM_TRACE_HISTORY = \"raw\" activates (forensic mode is
            still an active mode)"
    (is (true? (boolean (model/enabled? (fake-window {:flag "raw"})))))))

;; ── forensic? ───────────────────────────────────────────────────────────────

(deftest forensic-false-by-default-test
  (testing "no flag, no URL = not forensic"
    (is (false? (model/forensic? (fake-window {}))))))

(deftest forensic-via-window-flag-raw-test
  (testing "BAREDOM_TRACE_HISTORY = \"raw\" enables forensic mode"
    (is (true? (model/forensic? (fake-window {:flag "raw"}))))))

(deftest forensic-via-flag-true-not-raw-test
  (testing "BAREDOM_TRACE_HISTORY = true is normal mode, NOT forensic"
    (is (false? (model/forensic? (fake-window {:flag true}))))))

(deftest forensic-via-url-param-raw-test
  (testing "URL containing ?baredom-trace-history=raw enables forensic"
    (is (true? (boolean
                (model/forensic?
                 (fake-window {:search "?baredom-trace-history=raw"})))))))

(deftest forensic-bare-url-param-not-forensic-test
  (testing "Bare ?baredom-trace-history (no =raw) is normal mode"
    (is (false? (model/forensic?
                 (fake-window {:search "?baredom-trace-history"}))))))

;; ── default-categories ──────────────────────────────────────────────────────

(deftest default-categories-normal-excludes-state-test
  (testing "in normal mode :state is the only excluded category — :events,
            :dom, :lifecycle are on so the timeline focuses on user-relevant
            records by default"
    (let [cats (model/default-categories false)]
      (is (contains? cats :events))
      (is (contains? cats :dom))
      (is (contains? cats :lifecycle))
      (is (not (contains? cats :state))))))

(deftest default-categories-forensic-includes-all-test
  (testing "in forensic mode every category is on — users opting in to
            =raw want every record visible"
    (is (= (set model/all-categories)
           (model/default-categories true)))))

;; ── categorize-type ─────────────────────────────────────────────────────────

(deftest categorize-type-events-test
  (is (= :events (model/categorize-type "event/dispatch")))
  (is (= :events (model/categorize-type "event/dispatch-cancelable")))
  (is (= :events (model/categorize-type "event/dispatch-document"))))

(deftest categorize-type-state-test
  (is (= :state (model/categorize-type "state/instance-field-set"))))

(deftest categorize-type-dom-test
  (is (= :dom (model/categorize-type "dom/attribute-set")))
  (is (= :dom (model/categorize-type "dom/attribute-removed"))))

(deftest categorize-type-lifecycle-test
  (is (= :lifecycle (model/categorize-type "lifecycle/connected")))
  (is (= :lifecycle (model/categorize-type "lifecycle/disconnected")))
  (is (= :lifecycle (model/categorize-type "lifecycle/attribute-changed"))))

(deftest categorize-type-other-test
  (is (= :other (model/categorize-type "unknown/type")))
  (is (= :other (model/categorize-type ""))))

;; ── filter-records / recent-rows ────────────────────────────────────────────

(defn- mk-rec
  "Tiny helper: build a JS record-shape with just the fields the filter
   reads."
  [id type tag t]
  (js-obj "id" id "type" type "tag" tag "t" t))

(def ^:private sample-records
  ;; oldest-first
  #js [(mk-rec 0 "event/dispatch"            "x-button" 100)
       (mk-rec 1 "lifecycle/connected"       "x-modal"  150)
       (mk-rec 2 "state/instance-field-set"  "x-button" 200)
       (mk-rec 3 "dom/attribute-set"         "x-modal"  250)
       (mk-rec 4 "event/dispatch"            "x-button" 300)])

(deftest filter-records-empty-spec-test
  (testing "nil tag + nil categories = match everything"
    (let [out (model/filter-records sample-records {})]
      (is (= 5 (count out)))
      (is (= [0 1 2 3 4] (mapv #(.-id ^js %) out))))))

(deftest filter-records-sorts-by-t-test
  (testing "filter-records sorts results by t ascending — required because
            PR 7 dispatch records have lower ids than the children they
            cause but later ring-buffer positions"
    (let [;; Simulate the PR 7 push pattern: child1 (t=110, id=1) is pushed
          ;; first, child2 (t=120, id=2) next, dispatch (t=100, id=0) last.
          recs #js [(mk-rec 1 "state/instance-field-set" "x-button" 110)
                    (mk-rec 2 "state/instance-field-set" "x-button" 120)
                    (mk-rec 0 "event/dispatch"           "x-button" 100)]
          out  (model/filter-records recs {})]
      (is (= [0 1 2]       (mapv #(.-id ^js %) out)))
      (is (= [100 110 120] (mapv #(.-t  ^js %) out))))))

(deftest filter-records-stable-when-already-sorted-test
  (testing "monotonic input passes through unchanged"
    (let [out (model/filter-records sample-records {})]
      (is (= [100 150 200 250 300] (mapv #(.-t ^js %) out))))))

(deftest filter-records-by-tag-test
  (testing "tag filter narrows to matching tag only"
    (let [out (model/filter-records sample-records {:tag "x-button"})]
      (is (= 3 (count out)))
      (is (every? #(= "x-button" (.-tag ^js %)) out)))))

(deftest filter-records-tag-all-sentinel-test
  (testing "'all' sentinel string also = match everything"
    (is (= 5 (count (model/filter-records sample-records {:tag "all"}))))))

(deftest filter-records-tag-blank-test
  (testing "blank tag = match everything"
    (is (= 5 (count (model/filter-records sample-records {:tag ""}))))))

(deftest filter-records-by-category-test
  (testing "category filter narrows to matching categories"
    (let [out (model/filter-records sample-records
                                    {:categories #{:events}})]
      (is (= 2 (count out)))
      (is (every? #(= "event/dispatch" (.-type ^js %)) out)))))

(deftest filter-records-empty-categories-test
  (testing "empty category set excludes everything"
    (is (zero? (count (model/filter-records sample-records
                                            {:categories #{}}))))))

(deftest filter-records-tag-and-category-test
  (testing "tag AND category combined"
    (let [out (model/filter-records sample-records
                                    {:tag "x-button"
                                     :categories #{:events}})]
      (is (= 2 (count out)))
      (is (every? (fn [^js r]
                    (and (= "x-button" (.-tag r))
                         (= "event/dispatch" (.-type r))))
                  out)))))

;; ── record-matches? ─────────────────────────────────────────────────────────

(deftest record-matches-empty-spec-test
  (testing "empty spec matches every record"
    (let [^js r (aget sample-records 0)]
      (is (true? (model/record-matches? r {}))))))

(deftest record-matches-tag-test
  (let [^js r (aget sample-records 0)]
    (is (true?  (model/record-matches? r {:tag "x-button"})))
    (is (false? (model/record-matches? r {:tag "x-modal"})))))

(deftest record-matches-tag-sentinels-test
  (testing "nil / blank / 'all' all match any tag"
    (let [^js r (aget sample-records 0)]
      (is (true? (model/record-matches? r {:tag nil})))
      (is (true? (model/record-matches? r {:tag ""})))
      (is (true? (model/record-matches? r {:tag "all"}))))))

(deftest record-matches-categories-test
  (let [^js evt   (aget sample-records 0)
        ^js state (aget sample-records 2)]
    (is (true?  (model/record-matches? evt   {:categories #{:events}})))
    (is (false? (model/record-matches? state {:categories #{:events}})))
    (is (true?  (model/record-matches? state {:categories #{:state :events}})))))

(deftest record-matches-empty-categories-test
  (testing "empty category set excludes everything"
    (is (false? (model/record-matches? (aget sample-records 0)
                                       {:categories #{}})))))

(deftest record-matches-tag-and-category-test
  (let [^js r (aget sample-records 0)]
    (is (true?  (model/record-matches? r {:tag "x-button"
                                          :categories #{:events}})))
    (is (false? (model/record-matches? r {:tag "x-button"
                                          :categories #{:state}})))
    (is (false? (model/record-matches? r {:tag "x-modal"
                                          :categories #{:events}})))))

;; ── payload-preview ─────────────────────────────────────────────────────────

(defn- preview-of [^js payload]
  (model/payload-preview (model/make-record payload 0 0)))

(deftest payload-preview-event-dispatch-test
  (let [p (preview-of {:type :event/dispatch :tag "x-button"
                       :event-name "x-button:click"
                       :detail #js {:value 42}
                       :cancelable? false :default-prevented? false})]
    (is (clojure.string/includes? p "x-button:click"))
    (is (clojure.string/includes? p "42"))))

(deftest payload-preview-event-cancelable-prevented-test
  (let [p (preview-of {:type :event/dispatch-cancelable :tag "x-modal"
                       :event-name "x-modal:before-close" :detail nil
                       :cancelable? true :default-prevented? true})]
    (is (clojure.string/includes? p "x-modal:before-close"))
    (is (clojure.string/includes? p "prevented"))))

(deftest payload-preview-state-test
  (let [p (preview-of {:type :state/instance-field-set :tag "x-button"
                       :field "__xButtonModel"
                       :value {:variant "primary"}})]
    (is (clojure.string/includes? p "__xButtonModel"))
    (is (clojure.string/includes? p "primary"))))

(deftest payload-preview-state-nil-value-test
  (let [p (preview-of {:type :state/instance-field-set :tag "x-button"
                       :field "k" :value nil})]
    (is (clojure.string/includes? p "null"))))

(deftest payload-preview-attr-set-test
  (let [p (preview-of {:type :dom/attribute-set :tag "x-button"
                       :attribute "disabled" :value ""})]
    (is (clojure.string/includes? p "disabled"))
    (is (clojure.string/includes? p "\"\""))))

(deftest payload-preview-attr-removed-test
  (let [p (preview-of {:type :dom/attribute-removed :tag "x-button"
                       :attribute "disabled"})]
    (is (clojure.string/includes? p "disabled"))
    (is (clojure.string/includes? p "removed"))))

(deftest payload-preview-lifecycle-connected-test
  (is (= "connected"
         (preview-of {:type :lifecycle/connected :tag "x-button"}))))

(deftest payload-preview-lifecycle-disconnected-test
  (is (= "disconnected"
         (preview-of {:type :lifecycle/disconnected :tag "x-button"}))))

(deftest payload-preview-lifecycle-attr-changed-test
  (let [p (preview-of {:type :lifecycle/attribute-changed :tag "x-button"
                       :attribute "disabled"
                       :old-value nil :new-value ""})]
    (is (clojure.string/includes? p "disabled"))
    (is (clojure.string/includes? p "→"))))

;; ── format-timestamp ────────────────────────────────────────────────────────

(deftest format-timestamp-millis-test
  (is (= "0ms"   (model/format-timestamp 0)))
  (is (= "999ms" (model/format-timestamp 999))))

(deftest format-timestamp-seconds-test
  (is (= "1.000s"  (model/format-timestamp 1000)))
  (is (= "12.345s" (model/format-timestamp 12345))))

(deftest format-timestamp-minutes-test
  (let [s (model/format-timestamp 65000)]
    (is (clojure.string/starts-with? s "1m"))
    (is (clojure.string/includes?    s "s"))))

(deftest format-timestamp-non-numeric-test
  (testing "nil / string / undefined inputs return empty string rather than
            throwing — guards against malformed imported records and stale
            tests poking format-timestamp directly"
    (is (= "" (model/format-timestamp nil)))
    (is (= "" (model/format-timestamp "12")))
    (is (= "" (model/format-timestamp js/undefined)))))

;; ── timeline: dot-color / lane-id-of / lane-label ───────────────────────────

(defn- mk-rec-with
  "Build a JS record with the given core fields. componentId may be nil.
   cause-id defaults to nil (record produced outside any dispatch)."
  [{:keys [id type tag t component-id cause-id]
    :or   {id 0 type "event/dispatch" tag "x-button" t 100 component-id 1
           cause-id nil}}]
  (js-obj "id" id "type" type "tag" tag "t" t
          "componentId" component-id "causeId" cause-id))

(deftest dot-color-by-category-test
  (is (= "#94e2d5" (model/dot-color (mk-rec-with {:type "event/dispatch"}))))
  (is (= "#f9e2af" (model/dot-color (mk-rec-with {:type "state/instance-field-set"}))))
  (is (= "#fab387" (model/dot-color (mk-rec-with {:type "dom/attribute-set"}))))
  (is (= "#cba6f7" (model/dot-color (mk-rec-with {:type "lifecycle/connected"}))))
  (is (= "#6c7086" (model/dot-color (mk-rec-with {:type "totally/made-up"})))))

(deftest lane-id-of-uses-component-id-test
  (is (= 7 (model/lane-id-of (mk-rec-with {:component-id 7})))))

(deftest lane-id-of-document-sentinel-test
  (testing "records without a componentId fall into the document lane"
    (is (= model/document-lane
           (model/lane-id-of (mk-rec-with {:component-id nil}))))))

(deftest lane-label-component-test
  (is (= "x-button #3" (model/lane-label {:lane-id 3 :tag "x-button"}))))

(deftest lane-label-document-test
  (is (= "document"
         (model/lane-label {:lane-id model/document-lane :tag "document"}))))

;; ── time-bounds ─────────────────────────────────────────────────────────────

(deftest time-bounds-empty-test
  (is (nil? (model/time-bounds []))))

(deftest time-bounds-single-record-test
  (testing "single record produces a span clamped to 1 (avoids divide-by-zero)"
    (let [b (model/time-bounds [(mk-rec-with {:t 500})])]
      (is (= 500 (:tmin b)))
      (is (= 500 (:tmax b)))
      (is (= 1   (:span b))))))

(deftest time-bounds-many-records-test
  (let [b (model/time-bounds [(mk-rec-with {:t 100})
                              (mk-rec-with {:t 250})
                              (mk-rec-with {:t 175})])]
    (is (= 100 (:tmin b)))
    (is (= 250 (:tmax b)))
    (is (= 150 (:span b)))))

;; ── time-x ──────────────────────────────────────────────────────────────────

(deftest time-x-at-tmin-is-zero-test
  (is (zero? (model/time-x 100 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-x-at-tmax-is-full-width-test
  (is (= 500 (model/time-x 200 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-x-midpoint-test
  (is (= 250 (model/time-x 150 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-x-clamps-out-of-range-test
  (testing "values outside [tmin, tmax] clamp to the plot edges"
    (let [bounds {:tmin 100 :tmax 200 :span 100}]
      (is (zero? (model/time-x 50  bounds 500)))   ; before
      (is (= 500 (model/time-x 999 bounds 500))))))  ; after

(deftest time-x-nil-bounds-test
  (testing "nil bounds yields x=0 instead of throwing"
    (is (zero? (model/time-x 100 {:tmin nil :tmax nil :span nil} 500)))))

;; ── time-from-x (inverse of time-x) ────────────────────────────────────────

(deftest time-from-x-at-zero-is-tmin-test
  (is (= 100 (model/time-from-x 0 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-from-x-at-full-width-is-tmax-test
  (is (= 200 (model/time-from-x 500 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-from-x-midpoint-test
  (is (= 150 (model/time-from-x 250 {:tmin 100 :tmax 200 :span 100} 500))))

(deftest time-from-x-clamps-out-of-range-test
  (let [bounds {:tmin 100 :tmax 200 :span 100}]
    (is (= 100 (model/time-from-x -50  bounds 500)))
    (is (= 200 (model/time-from-x 999  bounds 500)))))

(deftest time-from-x-zero-width-test
  (is (= 100 (model/time-from-x 50 {:tmin 100 :tmax 200 :span 100} 0))))

(deftest time-from-x-nil-bounds-test
  (is (zero? (model/time-from-x 100 nil 500)))
  (is (zero? (model/time-from-x 100 {:tmin nil :tmax nil :span nil} 500))))

(deftest time-x-and-back-is-identity-test
  (testing "time-x and time-from-x are inverses for in-range inputs"
    (let [bounds {:tmin 100 :tmax 1100 :span 1000}
          w      500]
      (doseq [t [100 250 500 800 1100]]
        (let [x  (model/time-x t bounds w)
              t' (model/time-from-x x bounds w)]
          (is (< (js/Math.abs (- t t')) 0.001)
              (str "round-trip for t=" t)))))))

;; ── nearest-record ─────────────────────────────────────────────────────────

(deftest nearest-record-empty-test
  (is (nil? (model/nearest-record [] 100))))

(deftest nearest-record-single-test
  (let [r (mk-rec-with {:t 500})]
    (is (= r (model/nearest-record [r] 999)))
    (is (= r (model/nearest-record [r] -10)))))

(deftest nearest-record-picks-closest-test
  (let [a (mk-rec-with {:id 0 :t 100})
        b (mk-rec-with {:id 1 :t 200})
        c (mk-rec-with {:id 2 :t 300})]
    (is (= a (model/nearest-record [a b c] 110)))
    (is (= b (model/nearest-record [a b c] 195)))
    (is (= c (model/nearest-record [a b c] 280)))))

(deftest nearest-record-tie-picks-last-test
  (testing "min-key biases to the LAST equal-distance record. Exact ties
            are negligible for scrubber UX; documenting the actual bias
            so future readers don't expect the opposite."
    (let [a (mk-rec-with {:id 0 :t 100})
          b (mk-rec-with {:id 1 :t 300})]
      (is (= b (model/nearest-record [a b] 200))))))

;; ── step-record ────────────────────────────────────────────────────────────

(def ^:private step-records
  [(mk-rec-with {:id 0 :t 100})
   (mk-rec-with {:id 1 :t 200})
   (mk-rec-with {:id 2 :t 300})])

(deftest step-record-empty-test
  (is (nil? (model/step-record [] 0 :next)))
  (is (nil? (model/step-record [] 0 :prev))))

(deftest step-record-from-nil-next-test
  (testing "no current selection: :next picks the first record"
    (is (= 0 (.-id ^js (model/step-record step-records nil :next))))))

(deftest step-record-from-nil-prev-test
  (testing "no current selection: :prev picks the last record"
    (is (= 2 (.-id ^js (model/step-record step-records nil :prev))))))

(deftest step-record-next-test
  (is (= 1 (.-id ^js (model/step-record step-records 0 :next))))
  (is (= 2 (.-id ^js (model/step-record step-records 1 :next)))))

(deftest step-record-prev-test
  (is (= 1 (.-id ^js (model/step-record step-records 2 :prev))))
  (is (= 0 (.-id ^js (model/step-record step-records 1 :prev)))))

(deftest step-record-at-edge-returns-nil-test
  (testing "stepping past either edge returns nil rather than wrapping"
    (is (nil? (model/step-record step-records 2 :next)))
    (is (nil? (model/step-record step-records 0 :prev)))))

(deftest step-record-skips-missing-ids-test
  (testing "step-record honours actual ids, not array index — gaps are fine"
    (let [recs [(mk-rec-with {:id 5  :t 100})
                (mk-rec-with {:id 12 :t 200})
                (mk-rec-with {:id 30 :t 300})]]
      (is (= 12 (.-id ^js (model/step-record recs 5  :next))))
      (is (= 5  (.-id ^js (model/step-record recs 12 :prev))))
      (is (= 30 (.-id ^js (model/step-record recs 12 :next)))))))

;; ── active-lanes ────────────────────────────────────────────────────────────

(deftest active-lanes-empty-test
  (is (= [] (model/active-lanes []))))

(deftest active-lanes-distinct-by-component-id-test
  (let [r1 (mk-rec-with {:id 0 :tag "x-button" :component-id 1 :t 100})
        r2 (mk-rec-with {:id 1 :tag "x-modal"  :component-id 2 :t 200})
        r3 (mk-rec-with {:id 2 :tag "x-button" :component-id 1 :t 300})
        ls (model/active-lanes [r1 r2 r3])]
    (is (= 2 (count ls)))
    (is (= [1 2] (mapv :lane-id ls)))))

(deftest active-lanes-document-lane-test
  (testing "records with nil componentId form a single document lane"
    (let [r1 (mk-rec-with {:id 0 :component-id nil :t 50})
          r2 (mk-rec-with {:id 1 :component-id nil :t 75})
          ls (model/active-lanes [r1 r2])]
      (is (= 1 (count ls)))
      (is (= model/document-lane (-> ls first :lane-id))))))

(deftest active-lanes-ordered-by-first-seen-test
  (testing "lanes order by oldest record (so older lanes stay at the top
            as new components register lanes below them)"
    (let [r1 (mk-rec-with {:id 0 :component-id 2 :t 100})
          r2 (mk-rec-with {:id 1 :component-id 1 :t 50})
          r3 (mk-rec-with {:id 2 :component-id 3 :t 200})
          ls (model/active-lanes [r1 r2 r3])]
      (is (= [1 2 3] (mapv :lane-id ls))))))

;; ── tooltip-text / timeline-hint ────────────────────────────────────────────

(deftest tooltip-text-includes-tag-and-cid-test
  (let [t (model/tooltip-text
           (model/make-record
            {:type :event/dispatch :tag "x-button" :component-id 4
             :event-name "x-button:click" :detail nil
             :cancelable? false :default-prevented? false}
            0 1234.5))]
    (is (clojure.string/includes? t "x-button"))
    (is (clojure.string/includes? t "#4"))
    (is (clojure.string/includes? t "x-button:click"))
    (is (clojure.string/includes? t "@ "))))

(deftest tooltip-text-document-no-cid-test
  (let [t (model/tooltip-text
           (model/make-record
            {:type :event/dispatch-document :tag "document" :component-id nil
             :event-name "x-table-row:disconnected" :detail nil
             :cancelable? false :default-prevented? false}
            0 100))]
    (is (clojure.string/includes? t "document"))
    (is (not (clojure.string/includes? t "#")))))

(deftest timeline-hint-empty-buffer-test
  (is (clojure.string/includes? (model/timeline-hint 0 0 nil 0)
                                "No records yet")))

(deftest timeline-hint-all-filtered-out-test
  (is (clojure.string/includes? (model/timeline-hint 0 5 nil 0)
                                "all hidden by filter")))

(deftest timeline-hint-with-records-test
  (let [s (model/timeline-hint 3 3 {:tmin 0 :tmax 1000 :span 1000} 2)]
    (is (clojure.string/includes? s "3 records"))
    (is (clojure.string/includes? s "2 lanes"))
    (is (clojure.string/includes? s "spanning"))))

(deftest timeline-hint-partial-filter-test
  (let [s (model/timeline-hint 1 5 {:span 500} 1)]
    (is (clojure.string/includes? s "1 of 5"))))

;; ── find-record-by-id ───────────────────────────────────────────────────────

(deftest find-record-by-id-hits-test
  (let [recs #js [(mk-rec-with {:id 0 :t 100})
                  (mk-rec-with {:id 1 :t 200})
                  (mk-rec-with {:id 2 :t 300})]]
    (is (= 1 (.-id ^js (model/find-record-by-id recs 1))))
    (is (= 2 (.-id ^js (model/find-record-by-id recs 2))))))

(deftest find-record-by-id-misses-test
  (let [recs #js [(mk-rec-with {:id 5 :t 100})]]
    (is (nil? (model/find-record-by-id recs 99)))
    (is (nil? (model/find-record-by-id recs nil)))
    (is (nil? (model/find-record-by-id recs "5"))
        "non-numeric id is rejected to avoid coercion surprises")))

;; ── cause-of / effects-of ───────────────────────────────────────────────────

(deftest cause-of-nil-when-no-cause-id-test
  (testing "record with causeId=nil (top-level / async) returns nil"
    (let [r    (mk-rec-with {:id 1 :cause-id nil})
          recs #js [r]]
      (is (nil? (model/cause-of recs r))))))

(deftest cause-of-resolves-cause-record-test
  (testing "record with causeId pointing into the buffer returns that record"
    (let [parent (mk-rec-with {:id 0 :t 100})
          child  (mk-rec-with {:id 1 :t 110 :cause-id 0})
          recs   #js [parent child]]
      (is (= 0 (.-id ^js (model/cause-of recs child)))))))

(deftest cause-of-evicted-test
  (testing "if the cause record is no longer in the buffer (evicted by
            ring-buffer rotation), cause-of returns nil"
    (let [child (mk-rec-with {:id 1 :cause-id 999})
          recs  #js [child]]
      (is (nil? (model/cause-of recs child))))))

(deftest effects-of-empty-test
  (testing "record with no effects returns total=0 and empty :records"
    (let [parent (mk-rec-with {:id 0 :t 100})
          recs   #js [parent]
          out    (model/effects-of recs parent)]
      (is (= 0 (:total out)))
      (is (empty? (:records out))))))

(deftest effects-of-direct-children-only-test
  (testing "only records whose causeId matches the parent's id count.
            Grandchildren (causeId pointing at a child) do not appear
            in the parent's :records list — that's the synchronous one-
            level traversal the detail pane needs."
    (let [parent (mk-rec-with {:id 0 :t 100})
          child  (mk-rec-with {:id 1 :t 110 :cause-id 0})
          grand  (mk-rec-with {:id 2 :t 120 :cause-id 1})
          recs   #js [parent child grand]
          out    (model/effects-of recs parent)]
      (is (= 1 (:total out)))
      (is (= [1] (mapv #(.-id ^js %) (:records out)))))))

(deftest effects-of-sorted-by-t-test
  (testing "effects come back in chronological order regardless of
            ring-buffer position"
    (let [parent (mk-rec-with {:id 0 :t 100})
          c1     (mk-rec-with {:id 3 :t 300 :cause-id 0})
          c2     (mk-rec-with {:id 1 :t 110 :cause-id 0})
          c3     (mk-rec-with {:id 2 :t 200 :cause-id 0})
          recs   #js [parent c1 c2 c3]
          out    (model/effects-of recs parent)]
      (is (= [1 2 3] (mapv #(.-id ^js %) (:records out)))))))

(deftest effects-of-truncates-at-display-limit-test
  (testing ":records is capped at effects-display-limit; :total is the
            un-capped count so the UI can render 'N of TOTAL'"
    (let [parent (mk-rec-with {:id 0 :t 100})
          kids   (vec (for [i (range (+ model/effects-display-limit 5))]
                        (mk-rec-with {:id (inc i) :t (+ 100 i) :cause-id 0})))
          recs   (apply array parent kids)
          out    (model/effects-of recs parent)]
      (is (= (+ model/effects-display-limit 5) (:total out)))
      (is (= model/effects-display-limit (count (:records out)))))))

;; ── Export envelope ─────────────────────────────────────────────────────────

(def ^:private export-ctx
  "Stable context map reused by every envelope test so individual cases
   only assert the shape they care about."
  {:exported-at 1715432100000
   :origin      "https://app.example.com/?baredom-trace-history"
   :user-agent  "Mozilla/5.0 test"
   :forensic?   false})

(deftest make-export-envelope-shape-test
  (testing "envelope carries every top-level field with the expected
            types and pass-throughs"
    (let [^js recs #js [#js {"id" 0 "t" 1.0 "type" "lifecycle/connected"}]
          comps    {0 {:tag "x-button" :first-seen 1.0}}
          sessions []
          ^js env  (model/make-export recs comps sessions export-ctx)]
      (is (= 1                          (.-schemaVersion env)))
      (is (= 1715432100000              (.-exportedAt env)))
      (is (= "https://app.example.com/?baredom-trace-history"
             (.-origin env)))
      (is (= "Mozilla/5.0 test"         (.-userAgent env)))
      (is (false?                       (.-forensic env)))
      (is (identical? recs              (.-records env))
          "records pass through by reference — schema avoids re-allocating
           an O(N) copy of the ring buffer just to export"))))

(deftest make-export-components-map-shape-test
  (testing "components index converts to string-keyed JS object with
            tag + firstSeen fields"
    (let [comps   {0 {:tag "x-button"   :first-seen 1.0}
                   7 {:tag "x-checkbox" :first-seen 5.5}}
          ^js env (model/make-export #js [] comps [] export-ctx)
          ^js c   (.-components env)
          ^js c0  (gobj/get c "0")
          ^js c7  (gobj/get c "7")]
      (is (= "x-button"   (.-tag c0)))
      (is (= 1.0          (.-firstSeen c0)))
      (is (= "x-checkbox" (.-tag c7)))
      (is (= 5.5          (.-firstSeen c7)))
      (is (= #{"0" "7"}   (set (gobj/getKeys c)))
          "keys are strings — schema commits to stringified ids so JSON
           objects round-trip cleanly"))))

(deftest make-export-sessions-shape-test
  (testing "sessions array carries id/label/startT/endT/startId/endId
            in wire-name form, dropping recorder-only :record-count"
    (let [sessions [{:id 0 :label "Session 0"
                     :start-t 100.5 :end-t 250.7
                     :start-id 42   :end-id 67}
                    {:id 1 :label "Active"
                     :start-t 300.0 :end-t nil
                     :start-id 70   :end-id nil}]
          ^js env  (model/make-export #js [] {} sessions export-ctx)
          ^js arr  (.-sessions env)]
      (is (= 2 (.-length arr)))
      (let [^js s0 (aget arr 0)
            ^js s1 (aget arr 1)]
        (is (= 0           (.-id s0)))
        (is (= "Session 0" (.-label s0)))
        (is (= 100.5       (.-startT s0)))
        (is (= 250.7       (.-endT s0)))
        (is (= 42          (.-startId s0)))
        (is (= 67          (.-endId s0)))
        (is (nil?          (.-endT s1))
            "open sessions surface end-t as null on the wire")
        (is (nil?          (.-endId s1))
            "open sessions surface end-id as null on the wire")))))

(deftest make-export-forensic-flag-test
  (testing "boolean coercion: truthy :forensic? lands as true; nil / false
            lands as false — never undefined"
    (let [ctx-on  (assoc export-ctx :forensic? true)
          ctx-nil (assoc export-ctx :forensic? nil)]
      (is (true?  (.-forensic ^js (model/make-export #js [] {} [] ctx-on))))
      (is (false? (.-forensic ^js (model/make-export #js [] {} [] ctx-nil)))))))

(deftest make-export-empty-buffer-test
  (testing "empty records / components / sessions still produce a valid
            envelope — useful for capturing 'nothing happened yet' as a
            baseline for a bug report"
    (let [^js env (model/make-export #js [] {} [] export-ctx)]
      (is (= 1 (.-schemaVersion env)))
      (is (= 0 (.-length (.-records env))))
      (is (= 0 (.-length (.-sessions env))))
      (is (zero? (count (gobj/getKeys (.-components env))))))))

(deftest make-export-stringifies-to-valid-json-test
  (testing "the envelope is JSON.stringify-clean: no circular refs, no
            symbol keys, parseable back to the same shape"
    (let [^js rec  (model/make-record
                    {:type :event/dispatch
                     :tag "x-button"
                     :component-id 0
                     :cause-id nil
                     :event-name "press"
                     :detail #js {:foo 1}
                     :cancelable? false
                     :default-prevented? false}
                    0 1.0)
          ^js env  (model/make-export #js [rec]
                                       {0 {:tag "x-button" :first-seen 0.5}}
                                       []
                                       export-ctx)
          json-str (js/JSON.stringify env)
          ^js back (js/JSON.parse json-str)
          ^js rec0 (aget (.-records back) 0)]
      (is (= 1               (.-schemaVersion back)))
      (is (= 1               (.-length (.-records back))))
      (is (= "event/dispatch" (.-type rec0))))))

;; ── download-filename ──────────────────────────────────────────────────────

(deftest download-filename-pad-and-extension-test
  (testing "zero-padded YYYYMMDD-HHmmss slug with .trace.json extension"
    ;; JS Date months are 0-indexed; March = 2.
    (let [d (js/Date. 2026 2 7 9 4 5)]
      (is (= "baredom-trace-20260307-090405.trace.json"
             (model/download-filename d))))))

(deftest download-filename-double-digit-fields-test
  (testing "two-digit fields are kept as-is, no leading zero stripping.
            Use Dec 31 (month=11 0-indexed) — Dec has 31 days, so the
            JS Date constructor doesn't roll the date forward like it
            would for an out-of-range day in a 30-day month."
    (let [d (js/Date. 2026 11 31 23 59 59)]
      (is (= "baredom-trace-20261231-235959.trace.json"
             (model/download-filename d))))))

(deftest download-filename-extension-constants-test
  (testing "filename composes from the exported prefix + extension
            constants so PR 11 import-side validation can reuse them
            without duplicating string literals"
    (let [d    (js/Date. 2026 0 1 0 0 0)
          name (model/download-filename d)]
      (is (clojure.string/starts-with? name model/filename-prefix))
      (is (clojure.string/ends-with?   name model/filename-extension)))))

;; ── Import — validate-envelope / parse-export ───────────────────────────────

(defn- mk-valid-record
  "Build a minimally valid record JS object (id, t, type set)."
  [id t type]
  (js-obj "schemaVersion" 1
          "id"            id
          "t"             t
          "type"          type
          "tag"           "x-button"
          "componentId"   nil
          "causeId"       nil))

(defn- mk-valid-envelope
  "Build a minimally valid envelope JS object — used as a baseline so
   per-test mutations isolate the field they exercise."
  []
  (js-obj "schemaVersion" 1
          "exportedAt"    1715432100000
          "origin"        "https://example.com/"
          "userAgent"     "test"
          "forensic"      false
          "components"    (js-obj)
          "sessions"      (array)
          "records"       (array (mk-valid-record 0 1.0 "lifecycle/connected"))))

(deftest validate-envelope-happy-path-test
  (testing "a well-formed envelope yields {:ok env} with the same
            reference passed through"
    (let [env       (mk-valid-envelope)
          {:keys [ok error]} (model/validate-envelope env)]
      (is (nil? error))
      (is (identical? env ok)))))

(deftest validate-envelope-not-object-test
  (testing "non-object input → error message naming the problem"
    (let [{:keys [error]} (model/validate-envelope nil)]
      (is (string? error))
      (is (clojure.string/includes? error "not a JSON object")))))

(deftest validate-envelope-missing-version-test
  (testing "envelope without schemaVersion → error"
    (let [^js env (mk-valid-envelope)]
      (gobj/remove env "schemaVersion")
      (let [{:keys [error]} (model/validate-envelope env)]
        (is (clojure.string/includes? error "schemaVersion"))))))

(deftest validate-envelope-version-mismatch-test
  (testing "envelope with a different schemaVersion → error names the
            mismatch so the user can tell what to do (re-export from
            the matching version, etc.)"
    (let [^js env (mk-valid-envelope)]
      (gobj/set env "schemaVersion" 999)
      (let [{:keys [error]} (model/validate-envelope env)]
        (is (clojure.string/includes? error "expected 1"))
        (is (clojure.string/includes? error "999"))))))

(deftest validate-envelope-records-not-array-test
  (testing "envelope.records that isn't an array → error"
    (let [^js env (mk-valid-envelope)]
      (gobj/set env "records" (js-obj))
      (let [{:keys [error]} (model/validate-envelope env)]
        (is (clojure.string/includes? error "not an array"))))))

(deftest validate-envelope-bad-record-test
  (testing "a record missing one of {id, t, type} fails validation
            with the index of the bad record so the caller can point
            the user at the right line"
    (let [^js env (mk-valid-envelope)
          ^js rs  (.-records env)]
      (.push rs (js-obj "id" 1 "t" 2.0))  ; missing :type
      (let [{:keys [error]} (model/validate-envelope env)]
        (is (clojure.string/includes? error "index 1"))))))

(deftest validate-envelope-empty-records-test
  (testing "an empty records array is valid — a no-activity baseline
            export still imports cleanly"
    (let [^js env (mk-valid-envelope)]
      (gobj/set env "records" (array))
      (let [{:keys [ok]} (model/validate-envelope env)]
        (is (some? ok))))))

;; ── Import — parse-export string path ───────────────────────────────────────

(deftest parse-export-valid-json-test
  (testing "a valid JSON string round-trips through parse-export to
            {:ok env}; the parsed envelope's records survives intact"
    (let [json-str (js/JSON.stringify (mk-valid-envelope))
          {:keys [ok error]} (model/parse-export json-str)]
      (is (nil? error))
      (is (= 1 (.-length (.-records ^js ok)))))))

(deftest parse-export-invalid-json-test
  (testing "non-JSON input → 'File is not valid JSON.' error; never
            throws. This is the user-facing message that lands in
            the dock hint."
    (let [{:keys [error]} (model/parse-export "{not json")]
      (is (= "File is not valid JSON." error)))))

(deftest parse-export-empty-string-test
  (testing "empty string is also rejected — JSON.parse('') throws."
    (let [{:keys [error]} (model/parse-export "")]
      (is (string? error)))))

(deftest parse-export-invalid-schema-test
  (testing "valid JSON that isn't a valid envelope falls through to
            validate-envelope and surfaces the structural reason"
    (let [{:keys [error]} (model/parse-export "{\"schemaVersion\":2}")]
      (is (clojure.string/includes? error "expected 1")))))

;; ── View predicates / view-id ──────────────────────────────────────────────

(deftest import-view?-test
  (testing "import-view? matches [:import N] but not :live or [:session N]"
    (is (true?  (model/import-view? [:import 0])))
    (is (true?  (model/import-view? [:import 99])))
    (is (false? (model/import-view? :live)))
    (is (false? (model/import-view? [:session 0])))
    (is (false? (model/import-view? nil)))
    (is (false? (model/import-view? [:other 5])))))

(deftest view-id-import-test
  (testing "view-id extracts the numeric id from both session and
            import views; nil for :live"
    (is (= 0   (model/view-id [:session 0])))
    (is (= 42  (model/view-id [:import  42])))
    (is (nil?  (model/view-id :live)))
    (is (nil?  (model/view-id [:other 5])))))
