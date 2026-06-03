(ns baredom.components.barebuild-invalidate-on.barebuild-invalidate-on-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [baredom.components.barebuild-invalidate-on.barebuild-invalidate-on :as io]
   [baredom.components.barebuild-invalidate-on.model :as model]))

(io/init!)

;; All effects here are synchronous (dispatchEvent runs listeners inline), so no
;; cljs.test/async is needed — but the fixture uses the map form regardless.
(defn- cleanup! []
  (doseq [n (.querySelectorAll js/document model/tag-name)] (.remove n))
  (doseq [n (.querySelectorAll js/document ".bio-test-source")] (.remove n)))

(use-fixtures :each {:before cleanup! :after cleanup!})

;; ── Helpers ──────────────────────────────────────────────────────────────────
(defn- mount-on-source!
  "Attach a <div> source to body, mount an invalidate-on with `attrs` inside it
  (so parentNode = the source), and return [source invalidate-on]."
  [attrs]
  (let [source (.createElement js/document "div")
        el     (.createElement js/document model/tag-name)]
    (set! (.-className source) "bio-test-source")
    (doseq [[k v] attrs] (.setAttribute el k v))
    (.appendChild (.-body js/document) source)
    (.appendChild source el)
    [source el]))

(defn- fire-source!
  "Dispatch the default source event (barebuild-action-state) on `source` with the
  given phase/name in detail — exactly the shape an action emits."
  [^js source phase nm]
  (.dispatchEvent source
                  (js/CustomEvent. model/default-source-event
                                   #js {:bubbles true
                                        :detail  #js {:name nm :state #js {:phase phase}}})))

(defn- with-invalidate-listener
  "Run `f` with a document-level barebuild-invalidate listener that collects each
  event's detail.src into an atom; `f` receives that atom. Removes the listener after."
  [f]
  (let [a (atom [])
        h (fn [^js e] (swap! a conj (.. e -detail -src)))]
    (.addEventListener js/document model/event-invalidate h)
    (try (f a)
      (finally (.removeEventListener js/document model/event-invalidate h)))))

(defn- with-console
  "Run `f` with console.error/warn captured; `f` receives a map of two atoms
  {:errors :warns}. Restores the originals after."
  [f]
  (let [orig-err (.-error js/console)
        orig-warn (.-warn js/console)
        errors (atom [])
        warns  (atom [])]
    (set! (.-error js/console) (fn [m] (swap! errors conj m)))
    (set! (.-warn  js/console) (fn [m] (swap! warns conj m)))
    (try (f {:errors errors :warns warns})
      (finally (set! (.-error js/console) orig-err)
               (set! (.-warn js/console) orig-warn)))))

;; ── Matching ───────────────────────────────────────────────────────────────────
(deftest when-phase-match-dispatches-invalidate-test
  (with-invalidate-listener
   (fn [seen]
     (let [[source _] (mount-on-source! {"when-phase" "success" "src" "/api/tasks"})]
       (fire-source! source "success" "add")
       (is (= ["/api/tasks"] @seen) "a when-phase match dispatches barebuild-invalidate with src")))))

(deftest when-phase-mismatch-does-not-dispatch-test
  (with-invalidate-listener
   (fn [seen]
     (let [[source _] (mount-on-source! {"when-phase" "success" "src" "/api/tasks"})]
       (fire-source! source "error" "add")
       (is (= [] @seen) "no invalidate when the phase does not match")))))

(deftest when-name-match-dispatches-test
  (with-invalidate-listener
   (fn [seen]
     (let [[source _] (mount-on-source! {"when-name" "add-task" "src" "/api/tasks"})]
       (fire-source! source "success" "add-task")
       (is (= ["/api/tasks"] @seen) "a when-name match dispatches")))))

(deftest both-matchers-are-anded-test
  (with-invalidate-listener
   (fn [seen]
     (let [[source _] (mount-on-source! {"when-phase" "success" "when-name" "add-task" "src" "/api/tasks"})]
       (fire-source! source "success" "other")           ; name mismatch
       (is (= [] @seen) "AND: phase matches but name does not → no dispatch")
       (fire-source! source "success" "add-task")         ; both match
       (is (= ["/api/tasks"] @seen) "AND: both match → dispatch")))))

(deftest no-matcher-set-warns-once-and-no-ops-test
  (with-console
   (fn [{:keys [warns errors]}]
     (with-invalidate-listener
      (fn [seen]
        ;; Neither matcher set: the config diagnostic is logged ONCE at connect (a
        ;; warn, alongside the orphan diagnostic) — NOT per source event. matches?
        ;; stays a pure predicate, so firing events logs nothing.
        (let [[source _] (mount-on-source! {"src" "/api/tasks"})]
          (is (= 1 (count @warns)) "warns once at connect that it will never match")
          (fire-source! source "success" "add")
          (fire-source! source "success" "add")
          (is (= [] @seen) "no invalidate when neither matcher is set")
          (is (= 1 (count @warns)) "no per-event spam: still just the one connect warn")
          (is (= 0 (count @errors)) "config issue is a warn, not a per-event error")))))))

;; ── Whitespace matcher is treated as unset ───────────────────────────────────────
(deftest whitespace-matcher-is-dead-not-active-test
  (with-console
   (fn [{:keys [warns]}]
     (with-invalidate-listener
      (fn [seen]
        ;; when-phase="   " trims to "" → treated as unset (get-attr-trimmed). With no
        ;; when-name either, that's the both-unset case → connect warn + never matches,
        ;; not a silent dead matcher that swallows events.
        (let [[source _] (mount-on-source! {"when-phase" "   " "src" "/api/tasks"})]
          (is (= 1 (count @warns)) "a whitespace matcher counts as unset → connect warn")
          (fire-source! source "success" "add")
          (is (= [] @seen) "and it never dispatches")))))))

;; ── Blank src warns but still dispatches (finding #4) ───────────────────────────
(deftest blank-src-warns-test
  (with-console
   (fn [{:keys [warns]}]
     (with-invalidate-listener
      (fn [seen]
        (let [[source _] (mount-on-source! {"when-phase" "success"})]   ; no src
          (fire-source! source "success" "add")
          (is (= [""] @seen) "still dispatches (unconditional), with an empty src")
          (is (= 1 (count @warns)) "warns that a blank src matches no broker")))))))

;; ── Manual trigger bypasses matchers + listener ─────────────────────────────────
(deftest invalidate-method-dispatches-unconditionally-test
  (with-invalidate-listener
   (fn [seen]
     (let [[_ ^js el] (mount-on-source! {"when-name" "never-matches" "src" "/api/tasks"})]
       (.invalidate el)
       (is (= ["/api/tasks"] @seen) ".invalidate() dispatches regardless of matchers")))))

;; ── Listener cleanup on disconnect ───────────────────────────────────────────────
(deftest disconnect-removes-source-listener-test
  (with-invalidate-listener
   (fn [seen]
     (let [[source el] (mount-on-source! {"when-phase" "success" "src" "/api/tasks"})]
       (.remove el)                                    ; disconnect
       (fire-source! source "success" "add")
       (is (= [] @seen) "no invalidate after disconnect — the source listener was removed")))))
