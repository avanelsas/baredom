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
      (is (= "x-button:click"   (.-eventName r)))
      (is (= 42                 (.-value detail)))
      (is (false?               (.-cancelable r)))
      (is (false?               (.-defaultPrevented r))))))

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
    (let [r (model/make-record
             {:type :foo :tag "x" :event-name "" :detail nil
              :cancelable? false :default-prevented? false}
             0 0)]
      (is (= "foo" (.-type r))))))

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
  (testing "BAREDOM_TRACE_HISTORY must be exactly true (truthy strings ignored)"
    (is (false? (model/enabled? (fake-window {:flag "yes"}))))))

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
  "Build a JS record with the given core fields. componentId may be nil."
  [{:keys [id type tag t component-id]
    :or   {id 0 type "event/dispatch" tag "x-button" t 100 component-id 1}}]
  (js-obj "id" id "type" type "tag" tag "t" t "componentId" component-id))

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
