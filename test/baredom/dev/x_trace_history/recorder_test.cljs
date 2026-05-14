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
;;
;; Most recorder tests fire multiple records with the same key in rapid
;; succession (the "monotonic ids" / "lifecycle ordering" / "session
;; bounds" assertions all assume every record lands). Forensic mode
;; disables the PR 9 sample-rate cap so those assertions hold; the
;; rate-limit-specific tests below explicitly toggle it back.
(use-fixtures :each
  {:before (fn []
             (gobj/set js/window "BAREDOM_TRACE_HISTORY" "raw")
             (recorder/install!)
             (recorder/clear!))
   :after  (fn []
             (recorder/uninstall!)
             (recorder/clear!)
             (gobj/remove js/window "BAREDOM_TRACE_HISTORY"))})

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

(deftest pause-mid-dispatch-keeps-cause-record-test
  (testing "if a handler calls pause! after producing records, the
            dispatch record is still pushed so handler records' causeId
            resolves to a real record"
    (let [el (make-el "x-button")]
      (.addEventListener el "x-button:click"
                         (fn [_]
                           (du/setv! el "__field" "before-pause")
                           (recorder/pause!)
                           (du/setv! el "__field" "after-pause")))
      (du/dispatch! el "x-button:click" #js {:v 1})
      (recorder/resume!)
      (let [recs       (vec (array-seq (recorder/records)))
            by-id      (into {} (map (fn [^js r] [(.-id r) r])) recs)
            handler-rs (filter (fn [^js r] (= "state/instance-field-set" (.-type r))) recs)
            dispatch-r (some (fn [^js r] (when (= "event/dispatch" (.-type r)) r)) recs)]
        (is (some? dispatch-r)
            "dispatch record force-pushed despite mid-dispatch pause")
        (is (= 1 (count handler-rs))
            "only the pre-pause handler record landed; the post-pause one was dropped")
        (let [^js handler-r (first handler-rs)
              cid           (.-causeId handler-r)]
          (is (number? cid))
          (is (contains? by-id cid)
              "handler record's causeId resolves to a real dispatch record"))))))

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

(deftest set-attr-untraced-produces-no-record-test
  (testing "du/set-attr-untraced! mutates the attribute but does NOT fire
            the trace hook — the dom write is real, the trace is silent.
            Used by animation hot paths so per-frame writes don't flood
            the recorder."
    (let [el (make-el mutation-tag)]
      (du/set-attr-untraced! el "data-x" "1")
      (is (= "1" (.getAttribute el "data-x"))
          "attribute IS set on the element")
      (is (= 0 (count-records))
          "no record was emitted to the trace recorder"))))

(deftest remove-attr-untraced-produces-no-record-test
  (testing "du/remove-attr-untraced! mirrors set-attr-untraced! — removes
            the attribute, no record emitted."
    (let [el (make-el mutation-tag)]
      (.setAttribute el "data-x" "1")
      (recorder/clear!)
      (du/remove-attr-untraced! el "data-x")
      (is (not (.hasAttribute el "data-x"))
          "attribute IS removed from the element")
      (is (= 0 (count-records))
          "no record was emitted to the trace recorder"))))

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

(deftest programmatic-native-event-not-recorded-but-frames-test
  (testing "a programmatically-dispatched native-shaped event
            (PointerEvent constructed in JS and fed to dispatchEvent) is
            NOT pushed as a record (only CustomEvents are), but its
            dispatch still establishes a cause frame. Setv inside the
            handler reads the frame's reserved id even though the
            dispatching event itself never lands in the buffer.

            Note: real user-initiated events (browser-trusted clicks,
            keys, pointermoves) are NOT delivered through the JS-
            visible dispatchEvent and therefore do not establish a
            cause frame in any browser. The cause chain anchors on
            the first programmatic dispatch in the call stack."
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

(deftest dispatch-on-non-node-event-target-does-not-throw-test
  (testing "Found in PR 19 smoke-testing the Angular adapter: Angular
            (and many vanilla apps) dispatch CustomEvents on window —
            popstate, resize, hashchange — and on XHR/MessagePort
            instances. All are EventTargets, none have getRootNode.
            The internal-host walk must short-circuit instead of
            throwing TypeError 'a.getRootNode is not a function'.

            The classifier currently lumps window dispatches in with
            element dispatches as `event/dispatch` (only `document`
            is special-cased). That semantic is preserved; this test
            only asserts the wrapper survives the crash and the
            record is captured."
    (.dispatchEvent js/window
                    (js/CustomEvent. "window-event"
                                     #js {:bubbles    false
                                          :cancelable false}))
    (is (= 1 (count-records))
        "dispatch on window survived the wrapper and produced a record")
    (let [^js r (aget (recorder/records) 0)]
      (is (string? (.-type r))
          "type field populated — wrapper finished its classify path")
      (is (nil? (.-componentId r))
          "non-element targets have no componentId"))))

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

;; ── PR 8: bounded recording sessions ────────────────────────────────────────

(deftest start-session-opens-active-session-test
  (testing "start-session! returns a numeric id and marks it active"
    (let [id (recorder/start-session!)]
      (is (number? id))
      (is (= id (recorder/active-session-id)))
      (recorder/stop-session!))))

(deftest stop-session-closes-active-session-test
  (testing "stop-session! clears active-session-id"
    (recorder/start-session!)
    (recorder/stop-session!)
    (is (nil? (recorder/active-session-id)))))

(deftest stop-session-noop-when-nothing-active-test
  (testing "stop-session! with no active session does nothing"
    (is (nil? (recorder/active-session-id)))
    (recorder/stop-session!)
    (is (nil? (recorder/active-session-id)))))

(deftest sessions-metadata-shape-test
  (testing "sessions returns JS objects with id, label, startT, endT, recordCount"
    (let [el (make-el "x-button")]
      (du/dispatch! el "before" #js {})
      (let [id (recorder/start-session!)]
        (du/dispatch! el "during" #js {})
        (recorder/stop-session!)
        (let [^js arr (recorder/sessions)
              ^js s   (aget arr 0)]
          (is (= 1 (.-length arr)))
          (is (= id (.-id s)))
          (is (= (str "Session " id) (.-label s)))
          (is (number? (.-startT s)))
          (is (number? (.-endT s)) "endT set after stop"))))))

(deftest sessions-active-has-null-end-t-test
  (testing "an active session reports endT as null"
    (recorder/start-session!)
    (let [^js s (aget (recorder/sessions) 0)]
      (is (nil? (.-endT s))))
    (recorder/stop-session!)))

(deftest session-records-filters-by-t-range-test
  (testing "session-records returns only the records pushed inside the
            session's t range"
    (let [el (make-el "x-button")]
      (du/dispatch! el "before" #js {})
      (let [id (recorder/start-session!)]
        (du/dispatch! el "inside-1" #js {})
        (du/dispatch! el "inside-2" #js {})
        (recorder/stop-session!)
        (du/dispatch! el "after" #js {})
        (let [^js arr (recorder/session-records id)
              ^js r0  (aget arr 0)
              ^js r1  (aget arr 1)]
          (is (= 2 (.-length arr)))
          (is (= "inside-1" (.-eventName r0)))
          (is (= "inside-2" (.-eventName r1))))))))

(deftest session-records-empty-for-unknown-id-test
  (testing "session-records of a non-existent id returns empty array"
    (is (zero? (.-length (recorder/session-records 9999))))))

(deftest session-record-count-reflects-live-buffer-test
  (testing "recordCount on the sessions metadata is computed from the
            live ring buffer at call time, not stored — adding events
            inside an open session bumps the count"
    (recorder/start-session!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "a" #js {})
      (du/dispatch! el "b" #js {}))
    (let [^js s (aget (recorder/sessions) 0)]
      (is (= 2 (.-recordCount s))))
    (recorder/stop-session!)))

(deftest back-to-back-start-auto-stops-prior-test
  (testing "calling start-session! while a session is active stops the
            previous one (with a console warn) and opens a new one"
    ;; The second start-session! intentionally fires a console.warn
    ;; — silence it so the test output stays clean while still
    ;; verifying the behaviour (auto-stop + fresh id).
    (let [orig (.-warn js/console)]
      (try
        (set! (.-warn js/console) (fn [& _] nil))
        (let [a (recorder/start-session!)
              b (recorder/start-session!)]
          (is (not= a b) "second start gets a fresh id")
          (is (= b (recorder/active-session-id)))
          (let [^js arr     (recorder/sessions)
                ^js first-s (aget arr 0)]
            (is (= 2 (.-length arr)))
            (is (some? (.-endT first-s)) "first session was auto-stopped"))
          (recorder/stop-session!))
        (finally
          (set! (.-warn js/console) orig))))))

(deftest clear-drops-all-sessions-test
  (testing "clear! empties sessions alongside the ring buffer"
    (recorder/start-session!)
    (recorder/stop-session!)
    (recorder/start-session!)
    (recorder/stop-session!)
    (is (= 2 (.-length (recorder/sessions))))
    (recorder/clear!)
    (is (zero? (.-length (recorder/sessions))))
    (is (nil? (recorder/active-session-id)))))

(deftest window-api-includes-session-keys-test
  (testing "install! exposes startSession, stopSession, sessions, sessionRecords"
    (let [^js base (gobj/get js/window "BareDOM")
          ^js api  (gobj/get base "traceHistory")]
      (is (fn? (gobj/get api "startSession")))
      (is (fn? (gobj/get api "stopSession")))
      (is (fn? (gobj/get api "sessions")))
      (is (fn? (gobj/get api "sessionRecords"))))))

;; ── PR 9: sample-rate cap ───────────────────────────────────────────────────
;;
;; The shared fixture installs in forensic mode (=raw) so most tests
;; aren't subject to the 16ms cap. These tests temporarily flip the
;; flag back to normal mode to actually exercise the cap.

(defn- with-normal-mode!
  "Reinstall the recorder in normal (non-forensic) mode for the
   duration of `f`. Restores forensic mode + clears state on the way
   out so the next test starts clean."
  [f]
  (recorder/uninstall!)
  (gobj/set js/window "BAREDOM_TRACE_HISTORY" true)
  (recorder/install!)
  (recorder/clear!)
  (try (f)
       (finally
         (recorder/uninstall!)
         (gobj/set js/window "BAREDOM_TRACE_HISTORY" "raw")
         (recorder/install!)
         (recorder/clear!))))

(deftest forensic-mode-fn-reflects-install-mode-test
  (testing "recorder/forensic-mode? returns true after the shared fixture's
            =raw install, and false after a normal-mode reinstall"
    (is (true? (recorder/forensic-mode?))
        "fixture installed in forensic mode")
    (with-normal-mode!
     #(is (false? (recorder/forensic-mode?))))))

(deftest rate-limit-drops-duplicate-setv-within-window-test
  (testing "two du/setv! calls on the same element + field within 16ms
            land as a single record"
    (with-normal-mode!
     (fn []
       (let [el (make-el mutation-tag)]
         ;; Prime: first setv records and assigns a componentId.
         (du/setv! el "k" "v1")
         (is (= 1 (count-records)))
         ;; Second setv (same key, immediately) is dropped by the cap.
         (du/setv! el "k" "v2")
         (is (= 1 (count-records))
             "duplicate within 16ms is dropped"))))))

(deftest rate-limit-allows-different-key-test
  (testing "two setv! on the same element with DIFFERENT field names
            both record — keys are (componentId, field)"
    (with-normal-mode!
     (fn []
       (let [el (make-el mutation-tag)]
         (du/setv! el "k1" "v1")
         (du/setv! el "k2" "v2")
         (is (= 2 (count-records))))))))

(deftest rate-limit-allows-different-component-test
  (testing "the same field on different elements both record"
    (with-normal-mode!
     (fn []
       (let [a (make-el mutation-tag)
             b (make-el mutation-tag)]
         (du/setv! a "k" "v")
         (du/setv! b "k" "v")
         (is (= 2 (count-records))))))))

(deftest rate-limit-exempts-lifecycle-test
  (testing "rapid attribute changes through native setAttribute fire
            lifecycle/attribute-changed records — those are exempt
            from the cap so reconnection storms remain visible"
    (with-normal-mode!
     (fn []
       (with-test-el
        (fn [^js el]
          (recorder/clear!)
          (.setAttribute el "foo" "1")
          (.setAttribute el "foo" "2")
          (.setAttribute el "foo" "3")
          (let [recs   (array-seq (recorder/records))
                lc-cnt (count (filter #(= "lifecycle/attribute-changed"
                                          (.-type ^js %)) recs))]
            (is (= 3 lc-cnt) "all three lifecycle records preserved"))))))))

(deftest rate-limit-resets-on-clear-test
  (testing "clear! drops the rate-limit map so a setv after clear
            records immediately even if the same key fired pre-clear"
    (with-normal-mode!
     (fn []
       (let [el (make-el mutation-tag)]
         (du/setv! el "k" "v1")
         (du/setv! el "k" "v2")  ; dropped by cap
         (is (= 1 (count-records)))
         (recorder/clear!)
         (du/setv! el "k" "v3")
         (is (= 1 (count-records))
             "post-clear setv records — map was reset"))))))

(deftest rate-limit-bypassed-in-forensic-mode-test
  (testing "fixture-installed forensic mode lets back-to-back same-key
            records both land — the explicit baseline for all the
            other tests in this namespace that fire dups by design"
    (let [el (make-el mutation-tag)]
      (du/setv! el "k" "v1")
      (du/setv! el "k" "v2")
      (du/setv! el "k" "v3")
      (is (= 3 (count-records))))))

(deftest rate-limit-event-dispatch-via-wrapper-test
  (testing "the dispatch wrapper rate-limits CustomEvents the same way:
            two dispatches with the same event-name on the same element
            within 16ms yield one record. The first goes through normal
            cause-frame logic; the second passes through silently."
    (with-normal-mode!
     (fn []
       (let [el (make-el mutation-tag)]
         ;; Prime to get a componentId stamped.
         (du/setv! el "__init" "v")
         (recorder/clear!)
         ;; Two CustomEvent dispatches with the same name back-to-back.
         (du/dispatch! el "click" #js {})
         (du/dispatch! el "click" #js {})
         (let [recs (filter #(= "event/dispatch" (.-type ^js %))
                            (array-seq (recorder/records)))]
           (is (= 1 (count recs)) "second dispatch was rate-limited")))))))

(deftest rate-limit-first-record-from-fresh-element-passes-test
  (testing "before an element has a componentId stamped, rate-limit-key
            returns nil and the record always passes. Verifies the
            first event from any fresh element lands."
    (with-normal-mode!
     (fn []
       ;; A fresh element with no prior interaction. Even if some other
       ;; test left rate-limit state in place, this element's cid is
       ;; nil so the cap doesn't apply.
       (let [el (make-el mutation-tag)]
         (du/setv! el "first-ever" "v")
         (is (= 1 (count-records))))))))

;; ── Shadow-internal attribution to owning host ──────────────────────────────
;;
;; When du/set-attr! is called on a shadow-internal element (e.g. the
;; inner <button> of x-button receiving a `data-hover` write), the record
;; should attribute to the owning custom-element host: same cid, host's
;; tag, and rate-limited under the host's bucket. Without host
;; resolution, each shadow-internal node would become its own
;; cid-less anonymous "component" and rate-limiting would never engage.

(defn- make-shadow-host
  "Create a real custom-element-like host (hyphenated tag) attached to
   the body, with an open shadow root containing a single inner element
   the test will mutate. Returns [host inner]."
  []
  (let [^js host    (.createElement js/document "x-trace-shadow-host")
        ^js shadow  (.attachShadow host #js {:mode "open"})
        ^js inner   (.createElement js/document "button")]
    (.appendChild shadow inner)
    (.appendChild (.-body js/document) host)
    [host inner]))

(deftest shadow-internal-mutation-attributes-to-host-test
  (testing "du/set-attr! on a shadow-internal node produces a record
            whose tag is the host's tag — confirms find-owning-host
            walks across the shadow boundary."
    (let [[^js host ^js inner] (make-shadow-host)]
      (du/set-attr! inner "data-hover" "true")
      (is (= 1 (count-records)))
      (let [r (record-at 0)]
        (is (= "x-trace-shadow-host" (.-tag r))
            "record's tag is the host's tag, not the inner element's")
        (is (= "data-hover" (.-attribute r))
            "the attribute being mutated is preserved verbatim"))
      (.remove host))))

(deftest shadow-internal-uses-host-componentId-test
  (testing "two shadow-internal mutations on the same host share a cid;
            a third mutation on a separate host gets a distinct cid."
    (let [[^js host-a ^js inner-a] (make-shadow-host)
          [^js host-b ^js inner-b] (make-shadow-host)]
      (du/set-attr! inner-a "data-x" "1")
      (du/set-attr! inner-a "data-y" "2")
      (du/set-attr! inner-b "data-x" "1")
      (is (= 3 (count-records)))
      (let [cid-a-1 (.-componentId (record-at 0))
            cid-a-2 (.-componentId (record-at 1))
            cid-b   (.-componentId (record-at 2))]
        (is (= cid-a-1 cid-a-2)
            "both mutations on host-a's shadow share host-a's cid")
        (is (not= cid-a-1 cid-b)
            "mutation on host-b's shadow gets a distinct cid"))
      (.remove host-a)
      (.remove host-b))))

(deftest shadow-internal-rate-limited-under-host-test
  (testing "two du/set-attr! on the same shadow-internal node within
            16ms collapse to one record because the rate-limit key uses
            the host's cid + attribute name."
    (with-normal-mode!
     (fn []
       (let [[^js host ^js inner] (make-shadow-host)]
         ;; First write primes the host's cid and the rate-limit key.
         (du/set-attr! inner "data-hover" "true")
         (is (= 1 (count-records)))
         ;; Second write of the same attribute within 16ms is dropped.
         (du/set-attr! inner "data-hover" "false")
         (is (= 1 (count-records))
             "duplicate (host-cid, data-hover) within 16ms is dropped")
         ;; A different attribute on the same shadow node is a separate
         ;; bucket — it records.
         (du/set-attr! inner "data-active" "true")
         (is (= 2 (count-records))
             "different attribute is a different rate-limit key")
         (.remove host))))))

;; ── Export — JSON envelope from live state ─────────────────────────────────

(deftest export-empty-envelope-test
  (testing "calling export! before any records have flowed still
            produces a valid envelope — a useful 'no activity' baseline
            for bug reports. With the components filter (PR-this-branch)
            the components map is guaranteed empty when records is
            empty: no records means no referenced cids means no
            entries survive the filter."
    (let [^js env (recorder/export!)]
      (is (= 1 (.-schemaVersion env)))
      (is (= 0 (.-length (.-records env))))
      (is (= 0 (.-length (.-sessions env))))
      (is (zero? (count (gobj/getKeys (.-components env))))
          "components filtered to empty when no records reference any cid"))))

(deftest export-includes-current-records-test
  (testing "every record produced before export! shows up in the
            envelope's records array, in the same order"
    (let [el (make-el "x-button")]
      (du/dispatch! el "click" #js {:value 1})
      (du/setv!     el "field"  "value")
      (let [^js env (recorder/export!)
            ^js rs  (.-records env)]
        (is (= (count-records) (.-length rs)))
        (is (= "event/dispatch"          (.-type (aget rs 0))))
        (is (= "state/instance-field-set" (.-type (aget rs 1))))))))

(deftest export-components-cover-every-record-componentId-test
  (testing "every componentId referenced by a record in envelope.records
            also appears as a key in envelope.components — the side-index
            is the resolution path for componentId-to-tag lookups, and a
            record pointing at a missing entry breaks downstream tools
            (importer, viewer)"
    (let [a (make-el "x-button")
          b (make-el "x-checkbox")]
      (du/dispatch! a "press" #js {})
      (du/dispatch! b "x-checkbox-change" #js {})
      (du/setv!     a "f" "v")
      (let [^js env       (recorder/export!)
            ^js rs        (.-records env)
            ^js comps     (.-components env)
            seen-cids     (->> (array-seq rs)
                               (map (fn [^js r] (.-componentId r)))
                               (remove nil?)
                               distinct)]
        (doseq [cid seen-cids]
          (is (some? (gobj/get comps (str cid)))
              (str "componentId " cid " referenced by a record but
                    missing from envelope.components")))))))

(deftest export-components-side-index-test
  (testing "the components map carries every element referenced by a
            record in the export, keyed by stringified id. Filter
            keeps cids that appear in records and drops the rest."
    (let [a (make-el "x-button")
          b (make-el "x-checkbox")]
      (du/dispatch! a "press" #js {})
      (du/dispatch! b "x-checkbox-change" #js {})
      (let [^js env  (recorder/export!)
            ^js c    (.-components env)
            tags     (->> (gobj/getKeys c)
                          (map (fn [k] (.-tag ^js (gobj/get c k))))
                          set)]
        (is (contains? tags "x-button"))
        (is (contains? tags "x-checkbox"))))))

(deftest export-sessions-metadata-test
  (testing "active and closed sessions both survive into the envelope
            with their start/end ids; an open session still carries
            endT=null / endId=null"
    (let [el (make-el "x-button")]
      (recorder/start-session!)
      (du/dispatch! el "press" #js {})
      (recorder/stop-session!)
      (recorder/start-session!)              ; second session, still open
      (du/dispatch! el "press2" #js {})
      (let [^js env  (recorder/export!)
            ^js arr  (.-sessions env)
            ^js s0   (aget arr 0)
            ^js s1   (aget arr 1)]
        (is (= 2 (.-length arr)))
        (is (number? (.-endT s0)))
        (is (number? (.-endId s0)))
        (is (nil? (.-endT s1)) "open session keeps endT null on the wire")
        (is (nil? (.-endId s1)) "open session keeps endId null on the wire"))
      (recorder/stop-session!))))

(deftest export-forensic-flag-mirrors-install-mode-test
  (testing "the envelope's `forensic` flag reflects the recorder's
            install-time mode (the fixture installs in raw, so true)"
    (let [^js env (recorder/export!)]
      (is (true? (.-forensic env))))))

(deftest export-context-fields-present-test
  (testing "exportedAt is a wall-clock timestamp; origin and userAgent
            are populated from window / navigator"
    (let [before  (.getTime (js/Date.))
          ^js env (recorder/export!)
          after   (.getTime (js/Date.))]
      (is (<= before (.-exportedAt env) after))
      (is (string? (.-origin env)))
      (is (string? (.-userAgent env))))))

(deftest export-while-paused-test
  (testing "export! is read-only — pausing the recorder does NOT
            invalidate it. The current buffer is exportable at any
            time, which is what makes 'send me your trace' usable
            after the user clicks Pause."
    (let [el (make-el "x-button")]
      (du/dispatch! el "press" #js {})
      (recorder/pause!)
      (let [^js env (recorder/export!)]
        (is (= 1 (.-schemaVersion env)))
        (is (pos? (.-length (.-records env)))
            "records captured before pause are still in the envelope")
        (is (true? (recorder/paused?))
            "pause state unchanged by export"))
      (recorder/resume!))))

(deftest export-stringifies-cleanly-test
  (testing "the envelope round-trips through JSON.stringify / parse with
            the schema-stable fields intact. JSON.stringify drops
            functions / undefined silently, so a clean parse is the
            integration check that the wire is free of non-JSON values."
    (let [el (make-el "x-button")]
      (du/dispatch! el "press" #js {:n 1})
      (let [^js env  (recorder/export!)
            json-str (js/JSON.stringify env)
            ^js back (js/JSON.parse json-str)
            ^js rec0 (aget (.-records back) 0)]
        (is (= 1                (.-schemaVersion back)))
        (is (= (.-length (.-records env))
               (.-length (.-records back))))
        (is (= "event/dispatch" (.-type rec0)))))))

(deftest export-is-on-window-api-test
  (testing "window.BareDOM.traceHistory exposes both `export` and
            `download` so consumers can call them from the console or
            wire their own buttons"
    (let [^js api (gobj/getValueByKeys js/window "BareDOM" "traceHistory")]
      (is (fn? (gobj/get api "export")))
      (is (fn? (gobj/get api "download"))))))

(deftest download-trace-cleans-up-on-click-throw-test
  (testing "if anchor.click throws (patched prototype, exotic CSP),
            download-trace! still revokes the object URL — the
            try/finally in trigger-download! guarantees we don't
            leak Blobs even on the unhappy path. We also assert no
            stray anchor is left under <body> after the call returns."
    (let [orig-create  js/URL.createObjectURL
          orig-revoke  js/URL.revokeObjectURL
          orig-click   (.. js/HTMLAnchorElement -prototype -click)
          revoked      (atom [])
          stub-create  (fn [^js _blob] "blob:throw-test")
          stub-revoke  (fn [url] (swap! revoked conj url))
          stub-click   (fn [] (throw (js/Error. "simulated click throw")))
          anchors-before (.-length (.querySelectorAll
                                    js/document
                                    "a[download$='.trace.json']"))]
      (try
        (set! js/URL.createObjectURL stub-create)
        (set! js/URL.revokeObjectURL stub-revoke)
        (set! (.. js/HTMLAnchorElement -prototype -click) stub-click)
        (is (thrown? js/Error (recorder/download-trace!))
            "the click error propagates — we don't swallow it")
        (is (= ["blob:throw-test"] @revoked)
            "object URL was revoked despite the throw")
        (is (= anchors-before
               (.-length (.querySelectorAll
                          js/document
                          "a[download$='.trace.json']")))
            "no stray download anchor left under <body>")
        (finally
          (set! js/URL.createObjectURL orig-create)
          (set! js/URL.revokeObjectURL orig-revoke)
          (set! (.. js/HTMLAnchorElement -prototype -click) orig-click))))))

(deftest download-trace-builds-blob-and-triggers-anchor-test
  (testing "download-trace! constructs an object-URL-backed anchor,
            clicks it (browser save dialog), and revokes the URL.
            The test stubs URL.createObjectURL / revokeObjectURL +
            HTMLAnchorElement.click to verify the sequence without
            actually invoking a download prompt."
    (let [orig-create  js/URL.createObjectURL
          orig-revoke  js/URL.revokeObjectURL
          orig-click   (.. js/HTMLAnchorElement -prototype -click)
          created      (atom [])
          revoked      (atom [])
          clicked      (atom 0)
          stub-create  (fn [^js blob]
                         (let [url (str "blob:test-" (count @created))]
                           (swap! created conj #js [blob url])
                           url))
          stub-revoke  (fn [url] (swap! revoked conj url))
          stub-click   (fn []
                         (this-as ^js this
                           (swap! clicked inc)
                           (is (string? (.-download this))
                               "anchor.download attribute is set so the
                                browser uses our filename")
                           (is (clojure.string/ends-with?
                                (.-download this) ".trace.json")
                               "filename ends with .trace.json")
                           (is (clojure.string/starts-with?
                                (.-href this) "blob:")
                               "anchor.href is a blob URL")))]
      (try
        (set! js/URL.createObjectURL stub-create)
        (set! js/URL.revokeObjectURL stub-revoke)
        (set! (.. js/HTMLAnchorElement -prototype -click) stub-click)
        (let [el (make-el "x-button")]
          (du/dispatch! el "press" #js {}))
        (recorder/download-trace!)
        (is (= 1 @clicked)        "anchor clicked exactly once")
        (is (= 1 (count @created)) "one Blob created")
        (is (= 1 (count @revoked)) "object URL revoked after click")
        (is (= (second (first @created)) (first @revoked))
            "revoked URL matches the one we created")
        (let [^js blob (aget (first @created) 0)]
          (is (= "application/json" (.-type blob))
              "Blob type signals JSON for the OS save dialog"))
        (finally
          (set! js/URL.createObjectURL orig-create)
          (set! js/URL.revokeObjectURL orig-revoke)
          (set! (.. js/HTMLAnchorElement -prototype -click) orig-click))))))

;; ── PR 11: Import — load envelopes back into the recorder ──────────────────

(defn- mk-import-record
  "Build a minimal valid record JS object — only the fields the
   recorder/importer cares about."
  [id t type]
  (js-obj "schemaVersion" 1
          "id"            id
          "t"             t
          "type"          type
          "tag"           "x-button"
          "componentId"   nil
          "causeId"       nil))

(defn- mk-import-envelope
  ([] (mk-import-envelope (array (mk-import-record 0 1.0 "lifecycle/connected"))))
  ([^js records]
   (js-obj "schemaVersion" 1
           "exportedAt"    1715432100000
           "origin"        "https://example.com/"
           "userAgent"     "test"
           "forensic"      false
           "components"    (js-obj)
           "sessions"      (array)
           "records"       records)))

(defn- clear-imports!
  "Tests in this section may leave imports across the recorder atom
   even though clear! preserves them by design — so reset the slot
   explicitly to keep test isolation."
  []
  (doseq [^js imp (array-seq (recorder/imports))]
    (recorder/remove-import! (.-id imp))))

(deftest import-trace!-with-object-test
  (testing "import-trace! accepts an already-parsed JS object and
            returns the new import id; the import appears in
            recorder/imports() with a default label"
    (clear-imports!)
    (let [env (mk-import-envelope)
          id  (recorder/import-trace! env)]
      (is (number? id) "import id is a number")
      (let [^js imps (recorder/imports)
            ^js imp0 (aget imps 0)]
        (is (= 1 (.-length imps)))
        (is (= id (.-id imp0)))
        (is (= "Import 0" (.-label imp0))
            "blank label falls back to 'Import N'")
        (is (= 1 (.-recordCount imp0)))))))

(deftest import-trace!-with-string-test
  (testing "import-trace! accepts a JSON string and parses it
            internally — same return contract as the object path"
    (clear-imports!)
    (let [json-str (js/JSON.stringify (mk-import-envelope))
          id       (recorder/import-trace! json-str "bug-report")]
      (is (number? id))
      (let [^js imps (recorder/imports)
            ^js imp0 (aget imps 0)]
        (is (= "bug-report" (.-label imp0))
            "explicit label survives")))))

(deftest import-trace!-invalid-rejects-test
  (testing "import-trace! returns nil and logs to console.warn when
            the envelope is malformed. The recorder's :imports slot
            stays unchanged so a bad drop doesn't corrupt the dock."
    (clear-imports!)
    (let [^js env (mk-import-envelope)]
      (gobj/set env "schemaVersion" 999)
      (let [before (.-length (recorder/imports))
            id     (recorder/import-trace! env)
            after  (.-length (recorder/imports))]
        (is (nil? id))
        (is (= before after) ":imports unchanged on rejection")))))

(deftest import-trace!-monotonic-ids-test
  (testing "ids are monotonically increasing across imports — the
            dock's view-key encoding relies on this for chip identity"
    (clear-imports!)
    (let [id-a (recorder/import-trace! (mk-import-envelope) "a")
          id-b (recorder/import-trace! (mk-import-envelope) "b")
          id-c (recorder/import-trace! (mk-import-envelope) "c")]
      (is (< id-a id-b id-c)))))

(deftest import-records-returns-stored-records-test
  (testing "import-records resolves an import id back to its records
            array, in the order they were stored"
    (clear-imports!)
    (let [recs (array (mk-import-record 0 1.0 "event/dispatch")
                      (mk-import-record 1 2.0 "lifecycle/connected"))
          env  (mk-import-envelope recs)
          id   (recorder/import-trace! env)
          ^js  out (recorder/import-records id)]
      (is (= 2 (.-length out)))
      (is (= "event/dispatch"      (.-type (aget out 0))))
      (is (= "lifecycle/connected" (.-type (aget out 1)))))))

(deftest import-records-unknown-id-test
  (testing "asking for records under an unknown id returns an empty
            array — the dock falls back to :live silently in that case"
    (is (= 0 (.-length (recorder/import-records 999999))))))

(deftest remove-import!-drops-entry-test
  (testing "remove-import! removes one import by id; subsequent calls
            with the same id are no-ops"
    (clear-imports!)
    (let [id (recorder/import-trace! (mk-import-envelope))]
      (is (= 1 (.-length (recorder/imports))))
      (recorder/remove-import! id)
      (is (= 0 (.-length (recorder/imports))))
      ;; second call is a no-op, not an error
      (recorder/remove-import! id)
      (is (= 0 (.-length (recorder/imports)))))))

(deftest clear!-preserves-imports-test
  (testing "Clear empties the live ring buffer and drops sessions
            (per the existing contract) but PRESERVES imports — the
            user dragged them in deliberately"
    (clear-imports!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "press" #js {}))
    (recorder/import-trace! (mk-import-envelope) "preserved")
    (is (pos? (count-records)))
    (is (= 1 (.-length (recorder/imports))))
    (recorder/clear!)
    (is (= 0 (count-records))           "live buffer empty after clear")
    (is (= 1 (.-length (recorder/imports)))
        "imports survive clear")
    (clear-imports!)))

(deftest import-roundtrip-export-then-import-test
  (testing "the export → JSON → import path round-trips: records
            inside the envelope come back identical from
            import-records"
    (clear-imports!)
    (let [el (make-el "x-button")]
      (du/dispatch! el "press" #js {:n 1})
      (du/setv!     el "field"  "value"))
    (let [^js env  (recorder/export!)
          json-str (js/JSON.stringify env)
          before-n (.-length (.-records env))
          id       (recorder/import-trace! json-str "from-export")
          ^js back (recorder/import-records id)]
      (is (number? id))
      (is (= before-n (.-length back))
          "all records survive the JSON round trip")
      ;; The 'tag' field should pass through unchanged.
      (when (pos? before-n)
        (is (= (.-tag (aget (.-records env) 0))
               (.-tag (aget back 0))))))
    (clear-imports!)))

(deftest import-window-api-exposed-test
  (testing "window.BareDOM.traceHistory carries import / imports /
            importRecords / removeImport so consumers can drive the
            flow from the console"
    (let [^js api (gobj/getValueByKeys js/window "BareDOM" "traceHistory")]
      (is (fn? (gobj/get api "import")))
      (is (fn? (gobj/get api "imports")))
      (is (fn? (gobj/get api "importRecords")))
      (is (fn? (gobj/get api "removeImport"))))))

;; ── observed-tags ───────────────────────────────────────────────────────────
;;
;; Backs the dock's tag-filter dropdown without a static dependency on
;; the component registry — see x_trace_history.cljs/sync-tag-options!.
;; The contract is: the returned vector is sorted, distinct, and merges
;; live-observed component tags with tags carried in any imported
;; envelope. PR 12 introduced this to keep the x-trace-history ESM
;; bundle decoupled from the rest of the component library.
;;
;; The recorder's :components index is monotonic for the page lifetime
;; — clear! intentionally preserves it (component ids stored on live
;; elements would otherwise collide with reused ids). Tests therefore
;; assert tag *presence* against a snapshot, not exact-vector equality.

(deftest observed-tags-is-sorted-and-deduped-test
  (testing "returned vector is in sorted order and contains no
            duplicates, regardless of dispatch order"
    (clear-imports!)
    (du/dispatch! (make-el "x-button")   "press"  #js {})
    (du/dispatch! (make-el "x-checkbox") "toggle" #js {})
    (du/dispatch! (make-el "x-button")   "press"  #js {})
    (let [tags (recorder/observed-tags)]
      (is (= tags (sort tags))     "result is sorted")
      (is (= (count tags) (count (distinct tags)))
          "no duplicates"))))

(deftest observed-tags-tracks-live-dispatches-test
  (testing "every distinct tag that fires through the dispatch hook
            appears in observed-tags"
    (clear-imports!)
    (du/dispatch! (make-el "x-button")   "press"  #js {})
    (du/dispatch! (make-el "x-checkbox") "toggle" #js {})
    (let [tags (set (recorder/observed-tags))]
      (is (contains? tags "x-button"))
      (is (contains? tags "x-checkbox")))))

(deftest observed-tags-includes-import-component-tags-test
  (testing "components carried in an imported envelope appear in
            observed-tags, merged with live tags. Lets the dock's tag
            filter target imported records the user has not yet
            interacted with live."
    (clear-imports!)
    (let [^js env (mk-import-envelope)]
      (gobj/set env "components"
                (js-obj "0" (js-obj "tag" "x-import-tag-a" "firstSeen" 0)
                        "1" (js-obj "tag" "x-import-tag-b" "firstSeen" 1)))
      (recorder/import-trace! env "with-tags"))
    (let [tags (set (recorder/observed-tags))]
      (is (contains? tags "x-import-tag-a"))
      (is (contains? tags "x-import-tag-b")))
    (clear-imports!)))

(deftest observed-tags-survives-empty-import-components-test
  (testing "an imported envelope with no components index does not
            blow up observed-tags. Older traces or hand-crafted
            envelopes may have an empty components map."
    (clear-imports!)
    (recorder/import-trace! (mk-import-envelope) "empty-comps")
    (is (vector? (recorder/observed-tags))
        "still returns a vector even with empty-components imports")
    (clear-imports!)))
