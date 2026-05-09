(ns baredom.dev.x-trace-history.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
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
