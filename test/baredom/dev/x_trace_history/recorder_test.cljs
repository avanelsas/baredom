(ns baredom.dev.x-trace-history.recorder-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.utils.component :as comp]
            [baredom.dev.x-trace-history.recorder :as recorder]))

(def ^:private test-tag "x-trace-test-element")

;; Register a minimal custom element once for the lifecycle integration tests.
(comp/register!
 test-tag
 {:observed-attributes #js ["foo"]
  :connected-fn        (fn [_])
  :disconnected-fn     (fn [_])
  :attribute-changed-fn (fn [_ _ _ _])})

;; Counters that prove user lifecycle fns ran. Used by
;; lifecycle-hook-exception-isolation-test to verify that a throwing hook
;; does not block downstream user-fn invocation.
(def ^:private counted-tag "x-trace-counted-element")
(def ^:private connect-count    (atom 0))
(def ^:private disconnect-count (atom 0))
(def ^:private attr-count       (atom 0))

(comp/register!
 counted-tag
 {:observed-attributes  #js ["foo"]
  :connected-fn         (fn [_] (swap! connect-count inc))
  :disconnected-fn      (fn [_] (swap! disconnect-count inc))
  :attribute-changed-fn (fn [_ _ _ _] (swap! attr-count inc))})

;; Tag used by mutation tests. Deliberately NOT registered as a custom
;; element so that .setAttribute / .removeAttribute do not synchronously
;; fire attributeChangedCallback (which would interleave lifecycle records
;; with the dom/* records the mutation tests want to inspect in isolation).
(def ^:private mutation-tag "x-trace-mutation-target")

(defn install-fixture
  [f]
  (recorder/install!)
  (recorder/clear!)
  (f)
  (recorder/uninstall!)
  (recorder/clear!))

(use-fixtures :each install-fixture)

(defn- make-el
  [tag]
  (.createElement js/document tag))

(defn- count-records []
  (.-length (recorder/records)))

(defn- ^js record-at [i]
  (aget (recorder/records) i))

;; ── dispatch! ───────────────────────────────────────────────────────────────

(deftest records-dispatch-test
  (testing "du/dispatch! produces a record with tag, eventName, detail"
    (let [el (make-el "x-button")]
      (du/dispatch! el "x-button:click" #js {:value 42})
      (is (= 1 (count-records)))
      (let [r (record-at 0)]
        (is (= "event/dispatch" (.-type r)))
        (is (= "x-button"       (.-tag r)))
        (is (= "x-button:click" (.-eventName r)))
        (is (= 42               (.. r -detail -value)))
        (is (false?             (.-cancelable r)))
        (is (false?             (.-defaultPrevented r)))))))

;; ── dispatch-cancelable! ────────────────────────────────────────────────────

(deftest records-cancelable-not-prevented-test
  (testing "uncancelled dispatch records defaultPrevented=false"
    (let [el (make-el "x-modal")]
      (du/dispatch-cancelable! el "x-modal:before-close" #js {})
      (let [r (record-at 0)]
        (is (= "event/dispatch-cancelable" (.-type r)))
        (is (true?  (.-cancelable r)))
        (is (false? (.-defaultPrevented r)))))))

(deftest records-cancelable-prevented-test
  (testing "cancelled dispatch records defaultPrevented=true"
    (let [el (make-el "x-modal")]
      (.addEventListener el "x-modal:before-close"
                         (fn [^js e] (.preventDefault e)))
      (du/dispatch-cancelable! el "x-modal:before-close" #js {})
      (let [r (record-at 0)]
        (is (true? (.-cancelable r)))
        (is (true? (.-defaultPrevented r)))))))

(deftest cancelable-return-value-preserved-test
  (testing "dispatch-cancelable! still returns true when not cancelled"
    (let [el (make-el "x-modal")]
      (is (true? (du/dispatch-cancelable! el "x-modal:before-close" #js {}))))))

(deftest cancelable-return-value-false-on-prevent-test
  (testing "dispatch-cancelable! returns false when preventDefault was called"
    (let [el (make-el "x-modal")]
      (.addEventListener el "x-modal:before-close"
                         (fn [^js e] (.preventDefault e)))
      (is (false? (du/dispatch-cancelable! el "x-modal:before-close" #js {}))))))

;; ── dispatch-document! ──────────────────────────────────────────────────────

(deftest records-dispatch-document-no-detail-test
  (testing "single-arity records tag='document' and detail=null"
    (du/dispatch-document! "x-table-row:disconnected")
    (let [r (record-at 0)]
      (is (= "event/dispatch-document" (.-type r)))
      (is (= "document"                (.-tag r)))
      (is (nil?                        (.-detail r))))))

(deftest records-dispatch-document-with-detail-test
  (testing "two-arity records tag='document' with detail"
    (du/dispatch-document! "x-timeline-item:disconnected" #js {:index 3})
    (let [r (record-at 0)]
      (is (= "document" (.-tag r)))
      (is (= 3          (.. r -detail -index))))))

;; ── ids + ordering ──────────────────────────────────────────────────────────

(deftest ids-monotonic-test
  (testing "consecutive records receive monotonically increasing ids"
    (let [el (make-el "x-button")]
      (dotimes [i 5]
        (du/dispatch! el (str "ev-" i) #js {}))
      (is (= [0 1 2 3 4]
             (mapv #(.-id %) (array-seq (recorder/records))))))))

(deftest oldest-first-ordering-test
  (testing "records returned oldest-first"
    (let [el (make-el "x-button")]
      (du/dispatch! el "first" #js {})
      (du/dispatch! el "second" #js {})
      (is (= "first"  (.-eventName (record-at 0))))
      (is (= "second" (.-eventName (record-at 1)))))))

;; ── pause / resume ──────────────────────────────────────────────────────────

(deftest pause-stops-recording-test
  (testing "dispatches between pause! and resume! are dropped"
    (let [el (make-el "x-button")]
      (du/dispatch! el "before" #js {})
      (recorder/pause!)
      (du/dispatch! el "during" #js {})
      (du/dispatch! el "during-2" #js {})
      (recorder/resume!)
      (du/dispatch! el "after" #js {})
      (is (= 2 (count-records)))
      (is (= "before" (.-eventName (record-at 0))))
      (is (= "after"  (.-eventName (record-at 1)))))))

;; ── clear ───────────────────────────────────────────────────────────────────

(deftest clear-empties-and-resets-id-test
  (testing "clear! empties buffer; next record starts at id=0"
    (let [el (make-el "x-button")]
      (du/dispatch! el "a" #js {})
      (du/dispatch! el "b" #js {})
      (recorder/clear!)
      (is (= 0 (count-records)))
      (du/dispatch! el "c" #js {})
      (is (= 0 (.-id (record-at 0)))))))

;; ── uninstall ───────────────────────────────────────────────────────────────

(deftest uninstall-stops-hook-test
  (testing "after uninstall!, dispatches are no longer recorded"
    (let [el (make-el "x-button")]
      (du/dispatch! el "before-uninstall" #js {})
      (recorder/uninstall!)
      (du/dispatch! el "after-uninstall" #js {})
      ;; only the pre-uninstall record should be in the buffer
      (is (= 1 (count-records))))))

;; ── hook is exception-safe ──────────────────────────────────────────────────

(deftest hook-exception-isolation-test
  (testing "a throwing hook does not break dispatch! / dispatch-cancelable! / dispatch-document!"
    (let [el (make-el "x-button")]
      (reset! du/trace-hook (fn [_] (throw (js/Error. "boom"))))
      ;; All three dispatch helpers must complete normally even when the hook throws.
      (du/dispatch! el "x" #js {})
      (is (true? (du/dispatch-cancelable! el "y" #js {})))
      (du/dispatch-document! "z" #js {})
      ;; No records added (the throwing hook never wrote to state).
      (is (= 0 (count-records))))))

(deftest hook-reinstall-test
  (testing "install! is re-entrant — repeated calls refresh both hook references"
    (reset! du/trace-hook (fn [_] :sentinel))
    (reset! comp/lifecycle-hook (fn [_] :sentinel))
    (recorder/install!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "after-reinstall" #js {})
      (is (= 1 (count-records)))
      (is (= "after-reinstall" (.-eventName (record-at 0)))))))

;; ── state-mutation hooks ────────────────────────────────────────────────────

(deftest records-setv-test
  (testing "du/setv! produces a state/instance-field-set record"
    (let [el (make-el mutation-tag)]
      (du/setv! el "__xButtonModel" {:variant "primary" :size 42})
      (let [^js r (record-at 0)
            ^js value (.-value r)]
        (is (= "state/instance-field-set" (.-type r)))
        (is (= mutation-tag                (.-tag r)))
        (is (= "__xButtonModel"            (.-field r)))
        (is (= "primary"                   (.-variant value)))
        (is (= 42                          (.-size value)))))))

(deftest records-set-attr-test
  (testing "du/set-attr! produces a dom/attribute-set record"
    (let [el (make-el mutation-tag)]
      (du/set-attr! el "variant" "primary")
      (let [r (record-at 0)]
        (is (= "dom/attribute-set" (.-type r)))
        (is (= "variant"           (.-attribute r)))
        (is (= "primary"           (.-value r)))))))

(deftest records-remove-attr-test
  (testing "du/remove-attr! produces a dom/attribute-removed record"
    (let [el (make-el mutation-tag)]
      (.setAttribute el "variant" "primary")  ; native, not via du; pre-state
      (recorder/clear!)                       ; isolate the next record
      (du/remove-attr! el "variant")
      (let [r (record-at 0)]
        (is (= "dom/attribute-removed" (.-type r)))
        (is (= "variant"               (.-attribute r)))))))

(deftest records-set-bool-attr-true-test
  (testing "du/set-bool-attr! true delegates to set-attr!, produces dom/attribute-set"
    (let [el (make-el mutation-tag)]
      (du/set-bool-attr! el "disabled" true)
      (let [r (record-at 0)]
        (is (= "dom/attribute-set" (.-type r)))
        (is (= "disabled"          (.-attribute r)))
        (is (= ""                  (.-value r)))
        (is (= 1 (count-records)))))))  ; single record, not double

(deftest records-set-bool-attr-false-test
  (testing "du/set-bool-attr! false delegates to remove-attr!, produces dom/attribute-removed"
    (let [el (make-el mutation-tag)]
      (.setAttribute el "disabled" "")
      (recorder/clear!)
      (du/set-bool-attr! el "disabled" false)
      (let [r (record-at 0)]
        (is (= "dom/attribute-removed" (.-type r)))
        (is (= 1 (count-records)))))))

;; ── lifecycle hooks ─────────────────────────────────────────────────────────

(defn- with-test-el
  "Append a test custom-element instance to body, run f with it, then remove."
  [f]
  (let [el (make-el test-tag)]
    (.appendChild (.-body js/document) el)
    (try
      (f el)
      (finally
        (.remove el)))))

(deftest records-lifecycle-connected-test
  (testing "appending a registered element produces a lifecycle/connected record"
    (with-test-el
      (fn [_el]
        (let [recs (filter #(= "lifecycle/connected" (.-type %))
                           (array-seq (recorder/records)))]
          (is (= 1 (count recs)))
          (is (= test-tag (.-tag (first recs)))))))))

(deftest records-lifecycle-disconnected-test
  (testing "removing a registered element produces a lifecycle/disconnected record"
    (let [el (make-el test-tag)]
      (.appendChild (.-body js/document) el)
      (recorder/clear!)
      (.remove el)
      (let [r (record-at 0)]
        (is (= "lifecycle/disconnected" (.-type r)))
        (is (= test-tag (.-tag r)))))))

(deftest records-lifecycle-attribute-changed-test
  (testing "setAttribute on a registered element fires lifecycle/attribute-changed"
    (with-test-el
      (fn [^js el]
        (recorder/clear!)
        ;; native setAttribute (not via du) — verifies external mutations are seen
        (.setAttribute el "foo" "bar")
        (let [recs     (array-seq (recorder/records))
              ^js ac   (first (filter #(= "lifecycle/attribute-changed" (.-type ^js %)) recs))]
          (is (some? ac))
          (is (= "foo" (.-attribute ac)))
          (is (nil?    (.-oldValue ac)))
          (is (= "bar" (.-newValue ac))))))))

(deftest records-internal-attr-write-fires-both-hooks-test
  (testing "du/set-attr! produces dom/attribute-set AND lifecycle/attribute-changed"
    (with-test-el
      (fn [^js el]
        (recorder/clear!)
        (du/set-attr! el "foo" "v")
        (let [types (mapv #(.-type %) (array-seq (recorder/records)))]
          (is (some #{"dom/attribute-set"} types))
          (is (some #{"lifecycle/attribute-changed"} types)))))))

;; ── lifecycle hook is exception-safe ────────────────────────────────────────

(deftest lifecycle-hook-exception-isolation-test
  (testing "a throwing lifecycle hook does not block user lifecycle fns"
    (reset! connect-count    0)
    (reset! disconnect-count 0)
    (reset! attr-count       0)
    (reset! comp/lifecycle-hook (fn [_] (throw (js/Error. "boom"))))
    (let [el (make-el counted-tag)]
      (.appendChild (.-body js/document) el)
      (.setAttribute el "foo" "x")
      (.remove el))
    ;; All three user lifecycle fns must have run despite the bad hook.
    (is (= 1 @connect-count)    "connectedCallback must reach user-fn")
    (is (= 1 @attr-count)        "attributeChangedCallback must reach user-fn")
    (is (= 1 @disconnect-count) "disconnectedCallback must reach user-fn")))

;; ── window API installation ─────────────────────────────────────────────────

(deftest window-api-installed-test
  (testing "install! exposes window.BareDOM.traceHistory.{records,pause,resume,clear}"
    (let [^js base (gobj/get js/window "BareDOM")
          ^js api  (gobj/get base "traceHistory")]
      (is (some? api))
      (is (fn? (gobj/get api "records")))
      (is (fn? (gobj/get api "pause")))
      (is (fn? (gobj/get api "resume")))
      (is (fn? (gobj/get api "clear"))))))
