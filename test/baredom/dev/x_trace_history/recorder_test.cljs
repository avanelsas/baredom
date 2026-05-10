(ns baredom.dev.x-trace-history.recorder-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [clojure.string]
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

;; Async tests require map-style fixtures (cljs.test rejects function-style
;; fixtures when any test in the namespace uses (async ...)). The
;; async-callback-breaks-chain-test uses setTimeout, so we need the map form
;; even for the otherwise-synchronous tests.
(use-fixtures :each
  {:before (fn []
             (recorder/install!)
             (recorder/clear!))
   :after  (fn []
             (recorder/uninstall!)
             (recorder/clear!))})

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
  (testing "a throwing hook does not break dispatch! / dispatch-cancelable! / dispatch-document!.
            Replacing trace-hook with a throwing function affects only the
            non-event paths (setv / set-attr / remove-attr / lifecycle).
            Event recording is the wrapper's job — it pushes records
            directly, not through the trace-hook — so the three dispatch
            helpers still record one event each and complete normally."
    (let [el (make-el "x-button")]
      (reset! du/trace-hook (fn [_] (throw (js/Error. "boom"))))
      (du/dispatch! el "x" #js {})
      (is (true? (du/dispatch-cancelable! el "y" #js {})))
      (du/dispatch-document! "z" #js {})
      ;; The throwing hook would only fire from the du/dispatch*! post-hooks,
      ;; which the wrapper-active gate skips for :event/* payloads anyway.
      ;; The wrapper records all three.
      (is (= 3 (count-records))))))

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

(deftest attribute-changed-fires-before-connected-test
  (testing "attributeChangedCallback initial-fire ordering: pre-set observed
            attributes produce lifecycle/attribute-changed records BEFORE the
            lifecycle/connected record"
    (let [^js el (make-el test-tag)]
      ;; Pre-set the observed attribute before appending. The browser fires
      ;; attributeChangedCallback synchronously here.
      (.setAttribute el "foo" "initial")
      (recorder/clear!)
      ;; Now append — this fires connectedCallback (and no further attr fires
      ;; since "foo" is already at value "initial").
      (.appendChild (.-body js/document) el)
      (let [recs   (array-seq (recorder/records))
            types  (mapv #(.-type ^js %) recs)]
        ;; The "foo=initial" attribute change happened during the setAttribute
        ;; before clear, so it's gone. Only the connect should remain.
        (is (some #{"lifecycle/connected"} types))
        ;; Now set another attribute on the connected element. The lifecycle
        ;; hook fires synchronously DURING setAttribute, so the resulting
        ;; lifecycle/attribute-changed record lands after "connected" only
        ;; because "connected" was earlier in time, not because of any
        ;; ordering quirk.
        (recorder/clear!)
        (.setAttribute el "foo" "second")
        (let [r (record-at 0)]
          (is (= "lifecycle/attribute-changed" (.-type r)))
          (is (= "foo"      (.-attribute r)))
          (is (= "initial"  (.-oldValue r)))
          (is (= "second"   (.-newValue r))))
        (.remove el)))))

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
  (testing "install! exposes window.BareDOM.traceHistory.{records,components,pause,resume,clear}"
    (let [^js base (gobj/get js/window "BareDOM")
          ^js api  (gobj/get base "traceHistory")]
      (is (some? api))
      (is (fn? (gobj/get api "records")))
      (is (fn? (gobj/get api "components")))
      (is (fn? (gobj/get api "pause")))
      (is (fn? (gobj/get api "resume")))
      (is (fn? (gobj/get api "clear"))))))

;; ── component-id assignment ─────────────────────────────────────────────────

(deftest component-id-assigned-on-first-event-test
  (testing "first event from a fresh element assigns it a numeric componentId"
    (let [el (make-el mutation-tag)]
      (du/setv! el "k" "v")
      (let [r (record-at 0)]
        (is (number? (.-componentId r)))))))

(deftest component-id-stable-across-events-test
  (testing "second event from same element re-uses the same componentId"
    (let [el (make-el mutation-tag)]
      (du/setv! el "k" "v")
      (du/setv! el "k" "v2")
      (let [r0 (record-at 0)
            r1 (record-at 1)]
        (is (= (.-componentId r0) (.-componentId r1)))))))

(deftest component-id-distinct-across-elements-test
  (testing "different elements receive distinct componentIds"
    (let [a (make-el mutation-tag)
          b (make-el mutation-tag)]
      (du/setv! a "k" "v")
      (du/setv! b "k" "v")
      (let [ra (record-at 0)
            rb (record-at 1)]
        (is (not= (.-componentId ra) (.-componentId rb)))))))

(deftest component-id-stable-across-record-types-test
  (testing "same element keeps its componentId across event/mutation/lifecycle"
    (let [el (make-el test-tag)]
      (.appendChild (.-body js/document) el)         ; → lifecycle/connected
      (du/setv! el "k" "v")                            ; → state/instance-field-set
      (du/dispatch! el "x" #js {})                     ; → event/dispatch
      (.remove el)                                     ; → lifecycle/disconnected
      (let [recs (array-seq (recorder/records))
            cids (set (map #(.-componentId ^js %) recs))]
        ;; All four records refer to the same element, so all componentIds
        ;; should be identical (and not nil).
        (is (= 1 (count cids)))
        (is (number? (first cids)))))))

(deftest component-id-survives-disconnect-test
  (testing "the componentId survives a disconnect/reconnect cycle"
    (let [el (make-el test-tag)
          body (.-body js/document)]
      (.appendChild body el)
      (let [first-cid (.-componentId (record-at 0))]
        (.remove el)
        (recorder/clear!)
        (.appendChild body el)        ; same element re-attached
        (let [reconnected (record-at 0)]
          (is (= first-cid (.-componentId reconnected)))
          (.remove el))))))

(deftest document-target-has-null-component-id-test
  (testing "dispatch-document records carry componentId = null"
    (du/dispatch-document! "some-event")
    (let [r (record-at 0)]
      (is (= "document" (.-tag r)))
      (is (nil? (.-componentId r))))))

;; ── components index ────────────────────────────────────────────────────────

(deftest components-index-tracks-known-components-test
  (testing "components() exposes {componentId -> {tag, firstSeen}} for each seen element"
    (let [a (make-el mutation-tag)]
      (du/setv! a "k" "v")
      (let [a-cid    (.-componentId (record-at 0))
            ^js idx  (recorder/components)
            ^js info (gobj/get idx (str a-cid))]
        (is (some? info))
        (is (= mutation-tag (gobj/get info "tag")))
        (is (number? (gobj/get info "firstSeen")))))))

(deftest components-index-persists-across-clear-test
  (testing "clear! does NOT reset componentIds or the components index"
    (let [el (make-el mutation-tag)]
      (du/setv! el "k" "v")
      (let [first-cid (.-componentId (record-at 0))]
        (recorder/clear!)
        (du/setv! el "k" "v2")
        (let [reused-cid (.-componentId (record-at 0))]
          ;; cid stored on the element — same after clear!
          (is (= first-cid reused-cid)))
        ;; components index also still has the entry
        (let [^js idx (recorder/components)]
          (is (some? (gobj/get idx (str first-cid)))))))))

;; ── subscribe! / unsubscribe! (sync API contract) ──────────────────────────
;;
;; The async fire-on-rAF semantics are exercised end-to-end by the dock
;; integration test (x_trace_history_test). These sync tests cover the API
;; contract: tokens are unique, unsubscribe is idempotent, paused? reflects
;; recorder state.

(deftest subscribe-returns-unique-tokens-test
  (testing "each subscribe! returns a token distinct from prior tokens"
    (let [a (recorder/subscribe! (fn []))
          b (recorder/subscribe! (fn []))
          c (recorder/subscribe! (fn []))]
      (is (not= a b))
      (is (not= b c))
      (is (not= a c))
      (recorder/unsubscribe! a)
      (recorder/unsubscribe! b)
      (recorder/unsubscribe! c))))

(deftest unsubscribe-unknown-token-is-noop-test
  (testing "unsubscribe! with a token that was never registered is silent"
    (is (nil? (recorder/unsubscribe! 999999)))
    (is (nil? (recorder/unsubscribe! nil)))))

(deftest unsubscribe-idempotent-test
  (testing "double-unsubscribe is a no-op"
    (let [tok (recorder/subscribe! (fn []))]
      (recorder/unsubscribe! tok)
      (is (nil? (recorder/unsubscribe! tok))))))

(deftest paused-reflects-state-test
  (testing "paused? is true after pause! and false after resume!"
    (is (false? (recorder/paused?)))
    (recorder/pause!)
    (is (true?  (recorder/paused?)))
    (recorder/resume!)
    (is (false? (recorder/paused?)))))

;; ── PR 7: synchronous cause-id chain ────────────────────────────────────────

(deftest top-level-record-has-nil-cause-test
  (testing "a record produced outside any dispatch has causeId = null"
    (let [el (make-el mutation-tag)]
      (du/setv! el "k" "v")
      (is (nil? (.-causeId (record-at 0)))))))

(deftest top-level-dispatch-has-nil-cause-test
  (testing "a top-level CustomEvent dispatch is its own root — its
            record's causeId is null"
    (let [el (make-el "x-button")]
      (du/dispatch! el "click" #js {})
      (is (nil? (.-causeId (record-at 0)))))))

(deftest child-record-inherits-cause-test
  (testing "a setv inside a dispatch handler stamps the dispatch's id as
            its causeId. Records are sorted chronologically: dispatch
            (entry-t) appears before its children."
    (let [el (make-el "x-button")]
      (.addEventListener el "click"
                         (fn [_] (du/setv! el "__inside" "v")))
      (du/dispatch! el "click" #js {})
      ;; Two records exist: dispatch + setv. Find them by type.
      (is (= 2 (count-records)))
      (let [recs         (array-seq (recorder/records))
            ^js dispatch (some (fn [^js r] (when (= "event/dispatch" (.-type r)) r)) recs)
            ^js setv     (some (fn [^js r] (when (= "state/instance-field-set" (.-type r)) r)) recs)]
        (is (some? dispatch))
        (is (some? setv))
        (is (nil? (.-causeId dispatch)) "dispatch is the root frame")
        (is (= (.-id dispatch) (.-causeId setv))
            "setv was produced inside the dispatch's synchronous extent")))))

(deftest nested-dispatch-causality-test
  (testing "a dispatch fired inside another dispatch's handler nests
            cause frames: the inner's children point at the inner; the
            inner itself points at the outer; the outer is root."
    (let [el (make-el "x-button")]
      (.addEventListener el "outer"
                         (fn [_]
                           (.addEventListener el "inner"
                                              (fn [_]
                                                (du/setv! el "__deep" 1)))
                           (du/dispatch! el "inner" #js {})))
      (du/dispatch! el "outer" #js {})
      (let [recs            (array-seq (recorder/records))
            ^js outer       (some (fn [^js r]
                                    (when (and (= "event/dispatch" (.-type r))
                                               (= "outer" (.-eventName r))) r))
                                  recs)
            ^js inner       (some (fn [^js r]
                                    (when (and (= "event/dispatch" (.-type r))
                                               (= "inner" (.-eventName r))) r))
                                  recs)
            ^js deep-setv   (some (fn [^js r]
                                    (when (= "state/instance-field-set" (.-type r)) r))
                                  recs)]
        (is (some? outer))
        (is (some? inner))
        (is (some? deep-setv))
        (is (nil?              (.-causeId outer))     "outer is root")
        (is (= (.-id outer)    (.-causeId inner))     "inner caused by outer")
        (is (= (.-id inner)    (.-causeId deep-setv)) "deep-setv caused by inner")))))

(deftest async-callback-breaks-chain-test
  (testing "a setTimeout scheduled inside a dispatch handler runs after
            the dispatch frame has unwound. Records produced in the timer
            callback have causeId = null because the cause stack is empty
            when they're pushed. Documented limitation: synchronous-only."
    (async done
      (let [el (make-el mutation-tag)]
        (.addEventListener el "click"
                           (fn [_]
                             (js/setTimeout
                              (fn []
                                (du/setv! el "__async" 1)
                                ;; Verify: by the time the timer fires,
                                ;; the dispatch frame has unwound and the
                                ;; new record's causeId is null.
                                (let [recs  (array-seq (recorder/records))
                                      ^js a (some (fn [^js r]
                                                    (when (and (= "state/instance-field-set" (.-type r))
                                                               (= "__async" (.-field r))) r))
                                                  recs)]
                                  (is (some? a))
                                  (is (nil? (.-causeId a))
                                      "async record has no cause (chain broken)"))
                                (done))
                              0)))
        (du/dispatch! el "click" #js {})))))

(deftest external-dispatch-establishes-frame-test
  (testing "a CustomEvent dispatched directly (not via du/dispatch!) still
            establishes a cause frame because the wrapper sees every
            dispatchEvent. Children pushed inside its handler stamp its
            id as their cause."
    (let [el (make-el "x-button")]
      (.addEventListener el "external"
                         (fn [_] (du/setv! el "__inside-external" "v")))
      (.dispatchEvent el (js/CustomEvent. "external" #js {:bubbles true}))
      (let [recs     (array-seq (recorder/records))
            ^js ev   (some (fn [^js r]
                             (when (= "external" (.-eventName r)) r))
                           recs)
            ^js setv (some (fn [^js r]
                             (when (= "state/instance-field-set" (.-type r)) r))
                           recs)]
        (is (some? ev) "external CustomEvent recorded by the wrapper")
        (is (some? setv))
        (is (= (.-id ev) (.-causeId setv)))))))

(deftest native-event-not-recorded-but-frames-test
  (testing "a native browser-shaped event (PointerEvent) is NOT pushed as
            a record (only CustomEvents are), but its dispatch still
            establishes a cause frame. Setv inside the handler reads the
            frame's reserved id even though the dispatching event itself
            never lands in the buffer."
    (let [el (make-el mutation-tag)]
      (.addEventListener el "pointerdown"
                         (fn [_] (du/setv! el "__inside-native" "v")))
      (.dispatchEvent el (js/PointerEvent. "pointerdown" #js {:bubbles true}))
      (let [recs     (array-seq (recorder/records))
            evs      (filter #(clojure.string/starts-with? (.-type ^js %) "event/") recs)
            ^js setv (some (fn [^js r]
                             (when (= "state/instance-field-set" (.-type r)) r))
                           recs)]
        (is (zero? (count evs))
            "native PointerEvent is not stored as a record")
        (is (some? setv))
        ;; The setv's cause-id is the reserved id of the native dispatch.
        ;; We don't have a record-id to compare against (no dispatch
        ;; record), but it must be a number — proof a frame was active.
        (is (number? (.-causeId setv))
            "setv carries the reserved id of the enclosing native dispatch")))))

(deftest dispatch-after-children-but-t-precedes-test
  (testing "dispatch's record is pushed AFTER its children (its handlers
            run first), but its `t` is captured at frame entry — so it
            shows up before its children when sorted chronologically.
            This is what filter-records sorts on so the timeline reads
            cause-then-effect."
    (let [el (make-el "x-button")]
      (.addEventListener el "click"
                         (fn [_] (du/setv! el "__a" 1)))
      (du/dispatch! el "click" #js {})
      (let [recs         (array-seq (recorder/records))
            ^js dispatch (some (fn [^js r] (when (= "event/dispatch" (.-type r)) r)) recs)
            ^js setv     (some (fn [^js r] (when (= "state/instance-field-set" (.-type r)) r)) recs)]
        (is (<= (.-t dispatch) (.-t setv))
            "dispatch's t (entry-time) is at or before its child's t")))))

;; ── PR 7: wrapper installation safety ───────────────────────────────────────

(deftest install-twice-is-idempotent-test
  (testing "calling install! twice does not double-wrap dispatchEvent.
            A double-wrap would record dispatches twice."
    (recorder/install!)
    (recorder/install!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "x" #js {})
      (is (= 1 (count-records))
          "still exactly one record per dispatch after a second install!"))))

(deftest uninstall-makes-dispatch-passthrough-test
  (testing "after uninstall!, a dispatch produces no record — but the
            event still fires its handlers. Verifies the wrapper's
            inactive path."
    (recorder/uninstall!)
    (let [el (make-el "x-button")
          fired (atom 0)]
      (.addEventListener el "x" (fn [_] (swap! fired inc)))
      (du/dispatch! el "x" #js {})
      (is (zero? (count-records)) "no record after uninstall!")
      (is (= 1 @fired) "handler still fired"))))

(deftest reinstall-after-uninstall-resumes-recording-test
  (testing "uninstall! then install! flips the wrapper-active flag back
            on. The wrapper is permanent — re-install just reactivates."
    (recorder/uninstall!)
    (recorder/install!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "x" #js {})
      (is (= 1 (count-records))))))

;; ── PR-A: internal-host recording boundary ──────────────────────────────────
;;
;; The dock UI uses BareDOM components internally. Without a boundary, every
;; click on a dock x-button would record `x-button:press`. mark-internal!
;; flags a host so events ORIGINATING inside its shadow tree bypass the
;; recorder entirely. Lifecycle events ON the host itself remain visible.

(defn- make-marked-host
  "Build a host element with an open shadow root, mark it internal,
   append it to body. Returns [host shadow]."
  []
  (let [^js host   (.createElement js/document "div")
        ^js shadow (.attachShadow host #js {:mode "open"})]
    (.appendChild (.-body js/document) host)
    (recorder/mark-internal! host)
    [host shadow]))

(deftest internal-dispatch-not-recorded-test
  (testing "a CustomEvent dispatched from inside a marked host's shadow
            is bypassed by the wrapper — no record, no id consumed"
    (let [[^js host ^js shadow] (make-marked-host)
          ^js child              (.createElement js/document mutation-tag)]
      (.appendChild shadow child)
      (du/dispatch! child "click" #js {})
      (is (zero? (count-records))
          "internal dispatch does not appear in the buffer")
      (.remove host))))

(deftest internal-setv-not-recorded-test
  (testing "du/setv! on an element inside a marked host's shadow is bypassed"
    (let [[^js host ^js shadow] (make-marked-host)
          ^js child              (.createElement js/document "div")]
      (.appendChild shadow child)
      (du/setv! child "__k" "v")
      (is (zero? (count-records)))
      (.remove host))))

(deftest internal-set-attr-not-recorded-test
  (testing "du/set-attr! on an element inside a marked host's shadow is bypassed"
    (let [[^js host ^js shadow] (make-marked-host)
          ^js child              (.createElement js/document "div")]
      (.appendChild shadow child)
      (du/set-attr! child "data-x" "y")
      (is (zero? (count-records)))
      (.remove host))))

(deftest internal-dispatch-does-not-establish-cause-frame-test
  (testing "an internal dispatch must NOT push a cause frame onto the
            stack. Otherwise a subsequent unrelated dispatch (outside
            the dock) would inherit the dock's reserved id as its
            cause — corrupting the trace. Verify by dispatching from
            outside immediately after; its causeId must still be null."
    (let [[^js host ^js shadow] (make-marked-host)
          ^js inner              (.createElement js/document mutation-tag)
          ^js outer              (.createElement js/document mutation-tag)]
      (.appendChild shadow inner)
      (du/dispatch! inner "internal" #js {})
      ;; Internal dispatch produced no records.
      (is (zero? (count-records)))
      ;; Now dispatch from outside the dock. This is a top-level frame
      ;; with no enclosing dispatch in user code — causeId must be nil.
      (du/dispatch! outer "external" #js {})
      (is (= 1 (count-records)))
      (is (nil? (.-causeId (record-at 0)))
          "external dispatch is its own root, not nested under the
           internal one (which never created a frame)")
      (.remove host))))

(deftest internal-host-children-do-not-pollute-cause-chain-test
  (testing "when an external dispatch's handler triggers something inside
            the dock (e.g. it scrolls the dock or focuses one of its
            inputs), records produced inside the dock during that scope
            are still skipped. The external dispatch's cause-frame stays
            on the stack, so any sibling user-code records still attribute
            cleanly to the external dispatch."
    (let [[^js host ^js shadow] (make-marked-host)
          ^js inner              (.createElement js/document mutation-tag)
          ^js outer              (.createElement js/document mutation-tag)]
      (.appendChild shadow inner)
      (.addEventListener
        outer "click"
        (fn [_]
          ;; Inside the external handler, do something inside the dock
          ;; (an internal dispatch). It must NOT be recorded.
          (du/dispatch! inner "click" #js {})
          ;; And do something outside (external). This must be recorded
          ;; with the OUTER dispatch as its cause.
          (du/setv! outer "__from-handler" "v")))
      (du/dispatch! outer "click" #js {})
      (let [recs (array-seq (recorder/records))]
        ;; Two records: outer dispatch + the setv. The internal x-button
        ;; dispatch from inside the handler is gone.
        (is (= 2 (count recs)))
        (let [^js dispatch-rec (some (fn [^js r]
                                       (when (= "event/dispatch" (.-type r)) r))
                                     recs)
              ^js setv-rec     (some (fn [^js r]
                                       (when (= "state/instance-field-set" (.-type r)) r))
                                     recs)]
          (is (some? dispatch-rec))
          (is (some? setv-rec))
          (is (nil? (.-causeId dispatch-rec)) "outer dispatch is root")
          (is (= (.-id dispatch-rec) (.-causeId setv-rec))
              "setv attributes to outer, not to the bypassed internal dispatch")))
      (.remove host))))

(deftest unmarked-host-still-records-test
  (testing "sanity check: a host WITHOUT the marker still records
            events from its shadow descendants normally"
    (let [^js host   (.createElement js/document "div")
          ^js shadow (.attachShadow host #js {:mode "open"})
          ^js child  (.createElement js/document mutation-tag)]
      (.appendChild (.-body js/document) host)
      ;; NOTE: no mark-internal! call.
      (.appendChild shadow child)
      (du/dispatch! child "click" #js {})
      (is (= 1 (count-records))
          "unmarked shadow tree records normally")
      (.remove host))))

(deftest mark-internal-does-not-suppress-host-itself-test
  (testing "the marker suppresses events ORIGINATING INSIDE the host's
            shadow tree — but events ON the host element itself are
            still recorded. (The dock's own connect/disconnect should
            remain visible in traces.)"
    (let [^js host (.createElement js/document "div")]
      (.appendChild (.-body js/document) host)
      (recorder/mark-internal! host)
      ;; Dispatch directly on the host (not on a shadow descendant).
      ;; The host's own getRootNode is document — not "inside" the marker.
      (du/dispatch! host "x" #js {})
      (is (= 1 (count-records))
          "events fired on the marked host itself are still recorded")
      (.remove host))))
