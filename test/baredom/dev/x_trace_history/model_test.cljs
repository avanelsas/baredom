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
