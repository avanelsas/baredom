(ns baredom.components.barebuild-action.barebuild-action-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.barebuild-action.barebuild-action :as action]
   [baredom.components.barebuild-action.model :as model]))

(action/init!)

(def ^:private original-fetch (.-fetch js/window))
(def ^:private submit-event "test-submit")

(defn- cleanup! []
  (doseq [n (.querySelectorAll js/document model/tag-name)]
    (.remove n))
  (set! (.-fetch js/window) original-fetch))

(use-fixtures :each {:before cleanup! :after cleanup!})

;; ── Helpers ──────────────────────────────────────────────────────────────────
(defn- make-action [attrs]
  (let [el (.createElement js/document model/tag-name)]
    (doseq [[k v] attrs] (.setAttribute el k v))
    el))

(defn- append-body! [^js el]
  (.appendChild (.-body js/document) el)
  el)

;; The action reads the body via `.text` (then JSON.parse), so an empty body is a
;; success-with-no-value rather than a parse error — the stub mirrors that.
(defn- json-response [data status ok?]
  #js {:ok     ok?
       :status status
       :text   (fn [] (js/Promise.resolve (if (some? data) (js/JSON.stringify data) "")))})

(defn- stub-ok! [data status]
  (set! (.-fetch js/window)
        (fn [_url _opts] (js/Promise.resolve (json-response data status true)))))

(defn- after-settle [f] (js/setTimeout f 0))

(defn- fire-submit!
  "Dispatch the configured submit-event from a descendant so it bubbles to the
  action host (the action listens on its host — containment, no selectors)."
  [^js el values]
  (.dispatchEvent el (js/CustomEvent. submit-event
                                      #js {:bubbles true :cancelable true
                                           :detail  #js {:values values}})))

(defn- collect-phases! [^js el a]
  (.addEventListener el model/event-action-state
                     (fn [^js e] (swap! a conj (.-phase (.. e -detail -state))))))

;; ── Submit → success ──────────────────────────────────────────────────────────
(deftest submit-fetches-and-succeeds-test
  (async done
    (stub-ok! #js {:created true} 201)
    (let [el     (make-action {"name" "add" "action" "/api/things" "submit-event" submit-event})
          phases (atom [])]
      (collect-phases! el phases)
      (append-body! el)
      (fire-submit! el #js {:title "x"})
      (after-settle
       (fn []
         (is (= ["submitting" "success"] @phases) "emits submitting then success, in order")
         (let [^js st (.-state el)]
           (is (= "success" (.-phase st)))
           (is (= true (.-created ^js (.-response st))) "response carries the parsed body")
           (is (= 201 (.-httpStatus st))))
         (done))))))

(deftest detail-carries-name-and-state-test
  (async done
    (stub-ok! #js {} 200)
    (let [el     (make-action {"name" "add-task" "action" "/api/x" "submit-event" submit-event})
          detail (atom nil)]
      (.addEventListener el model/event-action-state (fn [^js e] (reset! detail (.-detail e))))
      (append-body! el)
      (fire-submit! el #js {:a 1})
      (after-settle
       (fn []
         (is (= "add-task" (.-name ^js @detail)) "name echoed at detail top level")
         (is (some? (.-state ^js @detail)) "detail carries state")
         (done))))))

(deftest posts-json-body-from-values-path-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_url ^js opts] (reset! captured opts)
              (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        (append-body! el)
        (fire-submit! el #js {:title "hello"})
        (after-settle
         (fn []
           (is (= "POST" (.-method ^js @captured)) "defaults to POST")
           (is (= "{\"title\":\"hello\"}" (.-body ^js @captured)) "body is the JSON of detail.values")
           (done)))))))

(deftest custom-method-and-values-path-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_url ^js opts] (reset! captured opts)
              (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-action {"name" "n" "action" "/api/x" "method" "PUT"
                             "submit-event" submit-event "values-path" "[:payload]"})]
        (append-body! el)
        (.dispatchEvent el (js/CustomEvent. submit-event
                                            #js {:bubbles true :cancelable true
                                                 :detail #js {:payload #js {:k "v"}}}))
        (after-settle
         (fn []
           (is (= "PUT" (.-method ^js @captured)))
           (is (= "{\"k\":\"v\"}" (.-body ^js @captured)) "values read from detail.payload")
           (done)))))))

;; ── Whitespace / missing action: no fetch (finding #1) ──────────────────────────
(deftest whitespace-action-does-not-fetch-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-action {"name" "n" "action" "   " "submit-event" submit-event})]
        (append-body! el)
        (fire-submit! el #js {:a 1})
        (after-settle
         (fn []
           (is (= 0 @calls) "a whitespace-only action is blank — no fetch (never POSTs to the page)")
           (is (= "idle" (.-phase (.-state el))) "state stays idle")
           (done)))))))

(deftest missing-action-does-not-fetch-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-action {"name" "n" "submit-event" submit-event})]
        (append-body! el)
        (fire-submit! el #js {:a 1})
        (after-settle
         (fn []
           (is (= 0 @calls) "a missing action no-ops")
           (done)))))))

(deftest no-values-at-path-does-not-fetch-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        (append-body! el)
        ;; detail with no `values` key → resolve-values misses → no empty-body POST
        (.dispatchEvent el (js/CustomEvent. submit-event
                                            #js {:bubbles true :cancelable true :detail #js {:other 1}}))
        (after-settle
         (fn []
           (is (= 0 @calls) "no values at values-path → no fetch")
           (done)))))))

;; ── Errors ───────────────────────────────────────────────────────────────────
(deftest http-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response nil 500 false))))
    (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
      (append-body! el)
      (fire-submit! el #js {:a 1})
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "error" (.-phase st)))
           (is (= "HTTP 500" (.-error st)))
           (is (= 500 (.-httpStatus st))))
         (done))))))

(deftest network-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.reject (js/Error. "boom"))))
    (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
      (append-body! el)
      (fire-submit! el #js {:a 1})
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "error" (.-phase st)))
           (is (= "boom" (.-error st)))
           (is (nil? (.-httpStatus st)) "no http-status for a transport failure"))
         (done))))))

;; ── 204 / empty body is a success(nil), not a parse error ───────────────────────
(deftest empty-body-succeeds-with-nil-response-test
  (async done
    (set! (.-fetch js/window)
          (fn [_ _] (js/Promise.resolve #js {:ok true :status 204 :text (fn [] (js/Promise.resolve ""))})))
    (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
      (append-body! el)
      (fire-submit! el #js {:a 1})
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "success" (.-phase st)) "a 204/empty body is a success, never a parse error")
           (is (nil? (.-response st)) "an empty body carries nil response")
           (is (= 204 (.-httpStatus st))))
         (done))))))

;; ── Abort on disconnect ─────────────────────────────────────────────────────────
(deftest abort-on-disconnect-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_ ^js opts] (reset! captured (.-signal opts)) (js/Promise. (fn [_ _]))))
      (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        (append-body! el)
        (fire-submit! el #js {:a 1})
        (after-settle
         (fn []
           (is (false? (.-aborted ^js @captured)) "not aborted while in-flight")
           (.remove el)
           (is (true? (.-aborted ^js @captured)) "in-flight fetch aborted on disconnect")
           (done)))))))

;; ── Stale response cannot overwrite fresher state (settle! identity guard) ───────
(deftest stale-response-does-not-overwrite-test
  (async done
    (let [resolvers (atom [])]
      (set! (.-fetch js/window)
            (fn [_ _] (js/Promise. (fn [resolve _reject] (swap! resolvers conj resolve)))))
      (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        (append-body! el)
        (fire-submit! el #js {:n 1})                       ; submit A pending
        (after-settle
         (fn []
           (fire-submit! el #js {:n 2})                    ; submit B aborts A
           (after-settle
            (fn []
              (let [[resolve-a resolve-b] @resolvers]
                (is (= 2 (count @resolvers)) "two submits issued")
                (resolve-b (json-response #js {:which "B"} 200 true))   ; B settles first
                (resolve-a (json-response #js {:which "A"} 200 true))   ; A lands LAST, must no-op
                (after-settle
                 (fn []
                   (is (= "B" (.-which ^js (.-response (.-state el))))
                       "the superseded submit A's later response is ignored")
                   (done))))))))))))

;; ── .submit() method bypasses the listener ──────────────────────────────────────
(deftest submit-method-bypasses-listener-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_ ^js opts] (reset! captured opts) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [^js el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        (append-body! el)
        (.submit el #js {:imperative true})
        (after-settle
         (fn []
           (is (= "{\"imperative\":true}" (.-body ^js @captured)) ".submit(values) posts directly")
           (done)))))))

;; ── valuesTransform applied before JSON-encode (payload-hygiene seam) ────────────
(deftest values-transform-applied-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_ ^js opts] (reset! captured opts)
              (js/Promise.resolve (json-response #js {} 200 true))))
      (let [^js el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})]
        ;; A transform that drops blank-string values (the without-blanks job).
        (set! (.-valuesTransform el)
              (fn drop-blanks [^js v]
                (let [out #js {}]
                  (doseq [k (js/Object.keys v)]
                    (when-not (= "" (aget v k)) (aset out k (aget v k))))
                  out)))
        (append-body! el)
        (fire-submit! el #js {:title "hi" :status ""})
        (after-settle
         (fn []
           (is (= "{\"title\":\"hi\"}" (.-body ^js @captured))
               "valuesTransform runs before JSON-encode (blank status dropped)")
           (done)))))))

;; ── detail.name is trimmed (same whitespace policy as invalidate-on's when-name) ──
(deftest detail-name-is-trimmed-test
  (async done
    (stub-ok! #js {} 200)
    (let [el     (make-action {"name" "  add-task  " "action" "/api/x" "submit-event" submit-event})
          detail (atom nil)]
      (.addEventListener el model/event-action-state (fn [^js e] (reset! detail (.-detail e))))
      (append-body! el)
      (fire-submit! el #js {:a 1})
      (after-settle
       (fn []
         (is (= "add-task" (.-name ^js @detail)) "detail.name is trimmed")
         (done))))))

;; ── A submit we won't service is not swallowed (default not prevented) ───────────
(deftest unserviced-submit-does-not-prevent-default-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response #js {} 200 true))))
    (let [el (make-action {"name" "n" "action" "/api/x" "submit-event" submit-event})
          ev (js/CustomEvent. submit-event
                              #js {:bubbles true :cancelable true :detail #js {:other 1}})]
      (append-body! el)
      (.dispatchEvent el ev)                       ; no `values` at the path → not serviced
      (after-settle
       (fn []
         (is (false? (.-defaultPrevented ev))
             "no preventDefault when there are no values to submit (emitter's default proceeds)")
         (done))))))
