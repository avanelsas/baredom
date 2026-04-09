(ns baredom.components.x-morph-stack.x-morph-stack-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-morph-stack.x-morph-stack :as x]
            [baredom.components.x-morph-stack.model         :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-state [name text]
  (let [d (.createElement js/document "div")]
    (.setAttribute d "slot" model/slot-state)
    (.setAttribute d model/attr-data-state name)
    (set! (.-textContent d) text)
    d))

(defn ^js make-stack [states]
  (let [el (.createElement js/document model/tag-name)]
    (doseq [s states]
      (.appendChild el s))
    (.appendChild (.-body js/document) el)
    el))

(defn ^js shadow-part [^js el sel]
  (.querySelector (.-shadowRoot el) sel))

;; ── Registration & shadow structure ──────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=viewport]")))
    (is (some? (shadow-part el "slot[name=state]")))))

;; ── First state visible by default ───────────────────────────────────────────
(deftest first-state-visible-test
  (let [^js a (make-state "a" "A")
        ^js b (make-state "b" "B")
        ^js el (make-stack [a b])]
    ;; data-active-state mirrored
    (is (= "a" (.getAttribute el model/attr-data-active-state)))
    (is (not= "none" (.. a -style -display)))
    (is (= "none" (.. b -style -display)))))

;; ── states() method ──────────────────────────────────────────────────────────
(deftest states-method-test
  (let [^js el (make-stack [(make-state "a" "A")
                            (make-state "b" "B")
                            (make-state "c" "C")])
        out (.states el)]
    (is (= 3 (.-length out)))
    (is (= "a" (aget out 0)))
    (is (= "c" (aget out 2)))))

;; ── activeState property round trip ──────────────────────────────────────────
(deftest active-state-property-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (set! (.-activeState el) "b")
    (is (= "b" (.getAttribute el model/attr-active-state)))
    (is (= "b" (.-activeState el)))))

;; ── Number properties ────────────────────────────────────────────────────────
(deftest number-property-roundtrip-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (set! (.-stiffness el) 250)
    (set! (.-damping el)   30)
    (set! (.-mass el)      2)
    (is (= 250 (.-stiffness el)))
    (is (= 30  (.-damping el)))
    (is (= 2   (.-mass el)))))

;; ── Boolean properties ───────────────────────────────────────────────────────
(deftest bool-property-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (set! (.-goo el) true)
    (is (.hasAttribute el model/attr-goo))
    (set! (.-goo el) false)
    (is (not (.hasAttribute el model/attr-goo)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))))

;; ── Disabled / reduced motion → instant swap ─────────────────────────────────
(deftest disabled-instant-swap-test
  (async done
   (let [^js a  (make-state "a" "A")
         ^js b  (make-state "b" "B")
         ^js el (make-stack [a b])
         events (atom [])]
     (.setAttribute el model/attr-disabled "")
     (.addEventListener el model/event-changed
                        (fn [^js e]
                          (swap! events conj (.. e -detail -to))
                          (when (= "b" (.. e -detail -to))
                            ;; visibility was swapped (no ghosts created in disabled mode)
                            (is (= "b" (.getAttribute el model/attr-data-active-state)))
                            (is (= "none" (.. a -style -display)))
                            (is (not= "none" (.. b -style -display)))
                            (done))))
     (.goTo el "b"))))

;; ── change event is cancelable ───────────────────────────────────────────────
(deftest change-cancelable-test
  (let [^js a  (make-state "a" "A")
        ^js b  (make-state "b" "B")
        ^js el (make-stack [a b])]
    (.addEventListener el model/event-change
                       (fn [^js e] (.preventDefault e)))
    (.goTo el "b")
    (is (= "a" (.getAttribute el model/attr-data-active-state)))
    (is (not= "none" (.. a -style -display)))))

;; ── unknown state is no-op ───────────────────────────────────────────────────
(deftest unknown-state-noop-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (.goTo el "zzz")
    (is (= "a" (.getAttribute el model/attr-data-active-state)))))

;; ── next / prev wrap ─────────────────────────────────────────────────────────
(deftest next-prev-wrap-test
  (let [^js el (make-stack [(make-state "a" "A")
                            (make-state "b" "B")
                            (make-state "c" "C")])]
    (.setAttribute el model/attr-disabled "")
    (.next el)
    (is (= "b" (.getAttribute el model/attr-data-active-state)))
    (.next el)
    (is (= "c" (.getAttribute el model/attr-data-active-state)))
    (.next el)
    (is (= "a" (.getAttribute el model/attr-data-active-state)) "wraps to first")
    (.prev el)
    (is (= "c" (.getAttribute el model/attr-data-active-state)) "wraps to last")))

;; ── Slot mutation: removing active state falls back ──────────────────────────
(deftest slot-removal-fallback-test
  (async done
   (let [^js a  (make-state "a" "A")
         ^js b  (make-state "b" "B")
         ^js el (make-stack [a b])]
     (.setAttribute el model/attr-disabled "")
     (.goTo el "b")
     ;; Remove the active state.
     (.remove b)
     ;; slotchange is async; wait a microtask + frame.
     (.requestAnimationFrame js/window
                             (fn [_]
                               (is (some? (.getAttribute el model/attr-data-active-state)))
                               ;; Should fall back to remaining state ("a").
                               (is (= "a" (.getAttribute el model/attr-data-active-state)))
                               (done))))))

;; ── Goo attribute is a property round-trip ──────────────────────────────────
(deftest goo-attribute-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])]
    (.setAttribute el model/attr-goo "")
    (is (.-goo el))
    (.removeAttribute el model/attr-goo)
    (is (not (.-goo el)))))

;; ── activeIndex property round trip ──────────────────────────────────────────
(deftest active-index-property-test
  (let [^js el (make-stack [(make-state "a" "A")
                            (make-state "b" "B")
                            (make-state "c" "C")])]
    (set! (.-activeIndex el) 2)
    (is (= "2" (.getAttribute el model/attr-active-index)))
    (is (= 2 (.-activeIndex el)))
    (set! (.-activeIndex el) nil)
    (is (not (.hasAttribute el model/attr-active-index)))
    (is (nil? (.-activeIndex el)))))

;; ── Attribute-driven transition fires change with reason "attribute" ─────────
(deftest attribute-driven-change-reason-test
  (async done
   (let [^js a  (make-state "a" "A")
         ^js b  (make-state "b" "B")
         ^js el (make-stack [a b])
         seen   (atom nil)]
     (.setAttribute el model/attr-disabled "")
     (.addEventListener el model/event-change
                        (fn [^js e] (reset! seen (.. e -detail -reason))))
     (.addEventListener el model/event-changed
                        (fn [^js _e]
                          (is (= "attribute" @seen))
                          (is (= "b" (.getAttribute el model/attr-data-active-state)))
                          (done)))
     (.setAttribute el model/attr-active-state "b"))))

;; ── Method-driven transition fires change with reason "method" ───────────────
(deftest method-driven-change-reason-test
  (let [^js el (make-stack [(make-state "a" "A") (make-state "b" "B")])
        seen   (atom nil)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-change
                       (fn [^js e] (reset! seen (.. e -detail -reason))))
    (.goTo el "b")
    (is (= "method" @seen))))

;; ── next/prev are no-ops on a single-state stack ─────────────────────────────
(deftest next-prev-single-state-test
  (let [^js el (make-stack [(make-state "only" "Only")])]
    (.setAttribute el model/attr-disabled "")
    (.next el)
    (is (= "only" (.getAttribute el model/attr-data-active-state)))
    (.prev el)
    (is (= "only" (.getAttribute el model/attr-data-active-state)))))

;; ── next/prev are safe on a zero-state stack ─────────────────────────────────
(deftest next-prev-zero-state-test
  (let [^js el (.createElement js/document model/tag-name)]
    (.appendChild (.-body js/document) el)
    ;; Should not throw.
    (.next el)
    (.prev el)
    (.goTo el "anything")
    (is (nil? (.getAttribute el model/attr-data-active-state)))))
