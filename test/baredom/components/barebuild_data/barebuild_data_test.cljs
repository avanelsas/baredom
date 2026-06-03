(ns baredom.components.barebuild-data.barebuild-data-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.barebuild-data.barebuild-data :as data]
   [baredom.components.barebuild-data.model :as model]))

(data/init!)

(def ^:private original-fetch (.-fetch js/window))

(defn- cleanup! []
  (doseq [n (.querySelectorAll js/document model/tag-name)]
    (.remove n))
  (set! (.-fetch js/window) original-fetch))

(use-fixtures :each {:before cleanup! :after cleanup!})

;; ── Helpers ──────────────────────────────────────────────────────────────────
;; .state is a plain JS object { phase, data, error, httpStatus }; phase a string.
(defn- make-data [src]
  (let [el (.createElement js/document model/tag-name)]
    (.setAttribute el "src" src)
    el))

(defn- append-body! [^js el]
  (.appendChild (.-body js/document) el)
  el)

;; The component reads the body via `.text` (then JSON.parse), so an empty/204
;; body is a load-with-no-value rather than a parse error. The stub mirrors that:
;; it serves the JSON text of `data` (or an empty string when `data` is nil).
(defn- json-response [data status ok?]
  #js {:ok   ok?
       :status status
       :text (fn [] (js/Promise.resolve (if (some? data) (js/JSON.stringify data) "")))})

(defn- stub-fetch-ok! [data status]
  (set! (.-fetch js/window)
        (fn [_url _opts] (js/Promise.resolve (json-response data status true)))))

;; setTimeout 0 runs after all microtasks (fetch.then → json.then) have drained,
;; so terminal state/events are settled by then.
(defn- after-settle [f]
  (js/setTimeout f 0))

(defn- collect-phases! [^js el a]
  (.addEventListener el model/event-data-state
                     (fn [^js e] (swap! a conj (.-phase (.. e -detail -state))))))

;; ── Mount → load ────────────────────────────────────────────────────────────────
(deftest mount-fetches-and-loads-test
  (async done
    (stub-fetch-ok! #js {:hello "world"} 200)
    (let [el     (make-data "/api/x")
          phases (atom [])]
      (collect-phases! el phases)
      (append-body! el)
      (after-settle
       (fn []
         (is (= ["loading" "loaded"] @phases) "emits loading then loaded, in order")
         (let [^js st (.-state el)]
           (is (= "loaded" (.-phase st)))
           (is (= "world" (.-hello ^js (.-data st))) "data carries the parsed body")
           (is (= 200 (.-httpStatus st))))
         (done))))))

(deftest state-identical-between-reads-test
  (async done
    (stub-fetch-ok! #js {} 200)
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (is (identical? (.-state el) (.-state el))
             "the cached JS object is returned by reference (no per-read allocation)")
         (done))))))

;; ── Detail shape ──────────────────────────────────────────────────────────────────
(deftest data-state-detail-carries-only-state-test
  (async done
    (stub-fetch-ok! #js {} 200)
    (let [el     (make-data "/api/x")
          detail (atom nil)]
      (.addEventListener el model/event-data-state (fn [^js e] (reset! detail (.-detail e))))
      (append-body! el)
      (after-settle
       (fn []
         (is (some? (.-state ^js @detail)) "detail carries state")
         (is (nil? (.-id ^js @detail)) "no place-coupled id in detail")
         (is (nil? (.-name ^js @detail)) "no place-coupled name in detail")
         (done))))))

;; ── Cross-runtime readability: .state is plain JS (the bug this contract fixes) ──
(deftest state-is-plain-js-readable-test
  (async done
    (stub-fetch-ok! #js {:hello "world"} 200)
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           ;; A consumer with a DIFFERENT cljs.core reads these via JS interop only.
           (is (string? (.-phase st)) "phase is a JS string, not a keyword")
           (is (= "loaded" (.-phase st)))
           (is (= "world" (.-hello ^js (.-data st)))))
         (done))))))

;; ── Manual refresh ────────────────────────────────────────────────────────────────
(deftest refresh-refetches-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-data "/api/x")]
        (append-body! el)
        (after-settle
         (fn []
           (is (= 1 @calls) "fetched once on connect")
           (.dispatchEvent el (js/CustomEvent. model/event-data-refresh))
           (after-settle
            (fn []
              (is (= 2 @calls) "barebuild-data-refresh triggers a refetch")
              (done)))))))))

;; ── Change guard: distinct payloads re-fire ──────────────────────────────────────
(deftest distinct-payloads-refire-test
  ;; The change guard compares the payload BY REFERENCE: two `loaded` values that
  ;; share phase+httpStatus but carry DIFFERENT bodies are distinct and must each
  ;; fire. This is the exact path a mis-addressed payload field would break — a
  ;; loose string key reading `undefined` on both sides would compare them equal
  ;; and silently suppress the second event. Guards the `:payload-fn` accessor.
  (async done
    (let [el     (make-data "/api/x")
          loaded (atom [])]
      (.addEventListener el model/event-data-state
                         (fn [^js e]
                           (let [^js st (.. e -detail -state)]
                             (when (= "loaded" (.-phase st))
                               (swap! loaded conj (.-data st))))))
      (stub-fetch-ok! #js {:v "A"} 200)             ; first load: body A
      (append-body! el)
      (after-settle
       (fn []
         (stub-fetch-ok! #js {:v "B"} 200)          ; second load: DIFFERENT body, SAME status
         (.dispatchEvent el (js/CustomEvent. model/event-data-refresh))
         (after-settle
          (fn []
            (is (= 2 (count @loaded)) "two distinct payloads each fire a loaded event")
            (is (= "A" (.-v ^js (first @loaded))))
            (is (= "B" (.-v ^js (second @loaded))) "the second, distinct body re-fired (not deduped)")
            (done))))))))

;; ── Errors ─────────────────────────────────────────────────────────────────────────
(deftest http-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response nil 500 false))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "error" (.-phase st)))
           (is (= "HTTP 500" (.-error st)))
           (is (= 500 (.-httpStatus st)) "http status surfaced on error"))
         (done))))))

(deftest network-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.reject (js/Error. "boom"))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "error" (.-phase st)))
           (is (= "boom" (.-error st)))
           (is (nil? (.-httpStatus st)) "no http-status for a transport failure"))
         (done))))))

;; ── Abort on disconnect ───────────────────────────────────────────────────────────
(deftest abort-on-disconnect-test
  (async done
    (let [captured (atom nil)]
      (set! (.-fetch js/window)
            (fn [_ ^js opts]
              (reset! captured (.-signal opts))
              (js/Promise. (fn [_ _]))))   ; never resolves
      (let [el (make-data "/api/x")]
        (append-body! el)
        (after-settle
         (fn []
           (is (false? (.-aborted ^js @captured)) "not aborted while in-flight")
           (.remove el)
           (is (true? (.-aborted ^js @captured)) "in-flight fetch aborted on disconnect")
           (is (= "idle" (.-phase (.-state el))) "state reset to idle on disconnect")
           (done)))))))

;; ── Refresh during loading: no duplicate loading event ──────────────────────────
(deftest no-duplicate-loading-during-loading-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise. (fn [_ _]))))   ; stays pending
    (let [el     (make-data "/api/x")
          phases (atom [])]
      (collect-phases! el phases)
      (append-body! el)                                              ; → loading (one event)
      (after-settle
       (fn []
         (.dispatchEvent el (js/CustomEvent. model/event-data-refresh))   ; aborts + re-enters loading
         (after-settle
          (fn []
            (is (= ["loading"] @phases)
                "refresh during loading aborts + restarts but emits no duplicate loading")
            (done))))))))

;; ── Stale response cannot overwrite fresher state ────────────────────────────────
;; The settle! identity guard: when fetch A is superseded by fetch B (a refresh
;; mid-flight), A's late-arriving response must be a no-op even if it lands AFTER
;; B has already settled. Manually-resolved promises let A resolve last.
(deftest stale-response-does-not-overwrite-fresher-state-test
  (async done
    (let [resolvers (atom [])]
      (set! (.-fetch js/window)
            (fn [_url _opts]
              (js/Promise. (fn [resolve _reject] (swap! resolvers conj resolve)))))
      (let [el (make-data "/api/x")]
        (append-body! el)                                          ; fetch A pending, loading
        (after-settle
         (fn []
           (.dispatchEvent el (js/CustomEvent. model/event-data-refresh)) ; aborts A, fetch B pending
           (after-settle
            (fn []
              (let [[resolve-a resolve-b] @resolvers]
                (is (= 2 (count @resolvers)) "two fetches issued: superseded A and active B")
                (resolve-b (json-response #js {:which "B"} 200 true))   ; B settles first → loaded B
                (resolve-a (json-response #js {:which "A"} 200 true))   ; A lands LAST, must no-op
                (after-settle
                 (fn []
                   (let [^js st (.-state el)]
                     (is (= "loaded" (.-phase st)))
                     (is (= "B" (.-which ^js (.-data st)))
                         "the superseded fetch A's later response is ignored — B's state stands"))
                   (done))))))))))))

;; ── Invalid JSON body settles into error ──────────────────────────────────────────
(deftest invalid-json-becomes-error-test
  (async done
    (set! (.-fetch js/window)
          (fn [_ _]
            (js/Promise.resolve
             #js {:ok     true
                  :status 200
                  :text   (fn [] (js/Promise.resolve "{not valid json"))})))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "error" (.-phase st)) "an ok response with a non-empty unparseable body is an error")
           (is (.startsWith (.-error st) "Invalid JSON:") "the error message names the parse failure")
           (is (= 200 (.-httpStatus st)) "the status that carried the bad body is preserved"))
         (done))))))

;; ── 204 / empty body is a success, not a parse error (#3) ──────────────────────
(deftest empty-body-loads-with-nil-data-test
  (async done
    (set! (.-fetch js/window)
          (fn [_ _]
            (js/Promise.resolve
             #js {:ok true :status 204 :text (fn [] (js/Promise.resolve ""))})))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [^js st (.-state el)]
           (is (= "loaded" (.-phase st)) "a 204/empty body is a successful load, never an error")
           (is (nil? (.-data st)) "an empty body carries nil data")
           (is (= 204 (.-httpStatus st)) "the status is still surfaced"))
         (done))))))

;; ── Whitespace-only src is blank → idle, no fetch (#9) ─────────────────────────
(deftest whitespace-src-does-not-fetch-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-data "   ")]                      ; whitespace-only src
        (append-body! el)
        (after-settle
         (fn []
           (is (= 0 @calls) "a whitespace-only src is treated as blank — no fetch issued")
           (is (= "idle" (.-phase (.-state el))) "state stays idle for a blank src")
           (done)))))))

;; ── Removing src resets to idle (no stale loaded left published) ──────────────────
(deftest removing-src-resets-to-idle-test
  (async done
    (stub-fetch-ok! #js {:v 1} 200)
    (let [el     (make-data "/api/x")
          phases (atom [])]
      (append-body! el)
      (after-settle
       (fn []
         (is (= "loaded" (.-phase (.-state el))) "loaded while src is present")
         (collect-phases! el phases)
         (.removeAttribute el "src")                  ; drop the source
         (after-settle
          (fn []
            (is (= "idle" (.-phase (.-state el))) "state falls back to idle when src is removed")
            (is (= ["idle"] @phases) "exactly one idle transition event fires")
            (done))))))))

;; ── Invalidation matches on pathname + query, so paginated siblings stay distinct ──
;; (Regression guard for the match key: `.pathname` alone collapses `?page=1` and
;; `?page=2` to the same key and cross-invalidates; the key includes the query.)
(defn- fire-invalidate! [src]
  (.dispatchEvent js/document (js/CustomEvent. model/event-invalidate #js {:detail #js {:src src}})))

(deftest invalidation-distinguishes-query-strings-test
  (async done
    (let [calls (atom 0)]
      (set! (.-fetch js/window)
            (fn [_ _] (swap! calls inc) (js/Promise.resolve (json-response #js {} 200 true))))
      (let [el (make-data "/api/items?page=1")]
        (append-body! el)
        (after-settle
         (fn []
           (is (= 1 @calls) "fetched once on connect")
           (fire-invalidate! "/api/items?page=2")          ; different query → must NOT match
           (after-settle
            (fn []
              (is (= 1 @calls) "an invalidate for a different query string does not refetch")
              (fire-invalidate! "/api/items?page=1")        ; same path+query → must match
              (after-settle
               (fn []
                 (is (= 2 @calls) "an invalidate for the exact path+query refetches")
                 (done)))))))))))

;; ── Reconnect re-fetches, no stale replay ────────────────────────────────────────
(deftest reconnect-refetches-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response #js {:v 1} 200 true))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (is (= "loaded" (.-phase (.-state el))))
         (.remove el)
         (is (= "idle" (.-phase (.-state el))) "idle after disconnect")
         (let [phases (atom [])]
           (collect-phases! el phases)
           (append-body! el)                       ; reconnect
           (after-settle
            (fn []
              (is (= ["loading" "loaded"] @phases)
                  "reconnect yields one idle→loading→loaded, no stale replay of the prior loaded")
              (done)))))))))
