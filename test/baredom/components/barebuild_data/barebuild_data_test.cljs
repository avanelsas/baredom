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
(defn- make-data [src]
  (let [el (.createElement js/document model/tag-name)]
    (.setAttribute el "src" src)
    el))

(defn- append-body! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn- json-response [data status ok?]
  #js {:ok ok? :status status :json (fn [] (js/Promise.resolve data))})

(defn- stub-fetch-ok! [data status]
  (set! (.-fetch js/window)
        (fn [_url _opts] (js/Promise.resolve (json-response data status true)))))

;; setTimeout 0 runs after all microtasks (fetch.then → json.then) have drained,
;; so terminal state/events are settled by then.
(defn- after-settle [f]
  (js/setTimeout f 0))

(defn- collect-phases! [^js el a]
  (.addEventListener el model/event-data-state
                     (fn [^js e] (swap! a conj (:phase (.. e -detail -state))))))

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
         (is (= [:loading :loaded] @phases) "emits loading then loaded, in order")
         (let [st (.-state el)]
           (is (= :loaded (:phase st)))
           (is (= "world" (.-hello ^js (:data st))) "data carries the parsed body")
           (is (= 200 (:http-status st))))
         (done))))))

(deftest state-identical-between-reads-test
  (async done
    (stub-fetch-ok! #js {} 200)
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (is (identical? (.-state el) (.-state el))
             "the cached map is returned by reference (no per-read allocation)")
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
         (is (some? (.-state ^js @detail)) "detail carries :state")
         (is (nil? (.-id ^js @detail)) "no place-coupled id in detail")
         (is (nil? (.-name ^js @detail)) "no place-coupled name in detail")
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

;; ── Errors ─────────────────────────────────────────────────────────────────────────
(deftest http-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response nil 500 false))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [st (.-state el)]
           (is (= :error (:phase st)))
           (is (= "HTTP 500" (:error st)))
           (is (= 500 (:http-status st)) "http status surfaced on error"))
         (done))))))

(deftest network-error-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.reject (js/Error. "boom"))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [st (.-state el)]
           (is (= :error (:phase st)))
           (is (= "boom" (:error st)))
           (is (not (contains? st :http-status)) "no http-status for a transport failure"))
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
           (is (= :idle (:phase (.-state el))) "state reset to idle on disconnect")
           (done)))))))

;; ── Refresh during loading: no duplicate loading event ──────────────────────────
(deftest no-duplicate-loading-during-loading-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise. (fn [_ _]))))   ; stays pending
    (let [el     (make-data "/api/x")
          phases (atom [])]
      (collect-phases! el phases)
      (append-body! el)                                              ; → :loading (one event)
      (after-settle
       (fn []
         (.dispatchEvent el (js/CustomEvent. model/event-data-refresh))   ; aborts + re-enters :loading
         (after-settle
          (fn []
            (is (= [:loading] @phases)
                "refresh during loading aborts + restarts but emits no duplicate :loading")
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
        (append-body! el)                                          ; fetch A pending, :loading
        (after-settle
         (fn []
           (.dispatchEvent el (js/CustomEvent. model/event-data-refresh)) ; aborts A, fetch B pending
           (after-settle
            (fn []
              (let [[resolve-a resolve-b] @resolvers]
                (is (= 2 (count @resolvers)) "two fetches issued: superseded A and active B")
                (resolve-b (json-response #js {:which "B"} 200 true))   ; B settles first → :loaded B
                (resolve-a (json-response #js {:which "A"} 200 true))   ; A lands LAST, must no-op
                (after-settle
                 (fn []
                   (let [st (.-state el)]
                     (is (= :loaded (:phase st)))
                     (is (= "B" (.-which ^js (:data st)))
                         "the superseded fetch A's later response is ignored — B's state stands"))
                   (done))))))))))))

;; ── Invalid JSON body settles into :error ────────────────────────────────────────
(deftest invalid-json-becomes-error-test
  (async done
    (set! (.-fetch js/window)
          (fn [_ _]
            (js/Promise.resolve
             #js {:ok     true
                  :status 200
                  :json   (fn [] (js/Promise.reject (js/Error. "Unexpected token")))})))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (let [st (.-state el)]
           (is (= :error (:phase st)) "an ok response with an unparseable body is an error")
           (is (= "Invalid JSON: Unexpected token" (:error st)))
           (is (= 200 (:http-status st)) "the status that carried the bad body is preserved"))
         (done))))))

;; ── Reconnect re-fetches, no stale replay ────────────────────────────────────────
(deftest reconnect-refetches-test
  (async done
    (set! (.-fetch js/window) (fn [_ _] (js/Promise.resolve (json-response #js {:v 1} 200 true))))
    (let [el (make-data "/api/x")]
      (append-body! el)
      (after-settle
       (fn []
         (is (= :loaded (:phase (.-state el))))
         (.remove el)
         (is (= :idle (:phase (.-state el))) "idle after disconnect")
         (let [phases (atom [])]
           (collect-phases! el phases)
           (append-body! el)                       ; reconnect
           (after-settle
            (fn []
              (is (= [:loading :loaded] @phases)
                  "reconnect yields one :idle→:loading→:loaded, no stale replay of the prior :loaded")
              (done)))))))))
