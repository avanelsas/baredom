(ns baredom.dev.x-trace-history.recorder-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.dev.x-trace-history.recorder :as recorder]))

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
      (is (= 1 (count-records)))
      ;; reinstall for the after-fixture cleanup contract
      (recorder/install!))))

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
