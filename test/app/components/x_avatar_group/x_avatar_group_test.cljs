(ns app.components.x-avatar-group.x-avatar-group-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [app.components.x-avatar-group.x-avatar-group :as x]
            [app.components.x-avatar-group.model          :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn make-avatar []
  (let [^js av (.createElement js/document "x-avatar")]
    (.setAttribute av "name" "Alice Bob")
    av))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=overflow]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "md"  (.getAttribute el "data-size")))
    (is (= "md"  (.getAttribute el "data-overlap")))
    (is (= "ltr" (.getAttribute el "data-direction")))
    (is (= "group" (.getAttribute el "role")))
    ;; No overflow visible when no children
    (is (= "none" (.. (shadow-part el "[part=overflow]") -style -display)))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-attribute-test
  (doseq [s ["xs" "sm" "md" "lg" "xl"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size s)
      (is (= s (.getAttribute el "data-size"))))))

;; ── Overlap attribute ────────────────────────────────────────────────────
(deftest overlap-attribute-test
  (doseq [o ["none" "sm" "md" "lg"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-overlap o)
      (is (= o (.getAttribute el "data-overlap"))))))

;; ── Direction attribute ───────────────────────────────────────────────────
(deftest direction-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-direction "rtl")
    (is (= "rtl" (.getAttribute el "data-direction")))))

;; ── Label attribute → aria-label ──────────────────────────────────────────
(deftest label-aria-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Team")
    (is (= "Team" (.getAttribute el "aria-label")))))

;; ── Overflow bubble visibility (async for slotchange) ────────────────────
(deftest no-overflow-without-max-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-max "3")
      (.appendChild el (make-avatar))
      (.appendChild el (make-avatar))
      (.appendChild el (make-avatar))
      ;; Wait for slotchange microtask
      (js/setTimeout
       (fn []
         (is (= "none" (.. (shadow-part el "[part=overflow]") -style -display)))
         (done))
       0))))

(deftest overflow-bubble-shown-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-max "2")
      (.appendChild el (make-avatar))
      (.appendChild el (make-avatar))
      (.appendChild el (make-avatar))
      (js/setTimeout
       (fn []
         (let [^js overflow (shadow-part el "[part=overflow]")]
           (is (not= "none" (.. overflow -style -display)))
           (is (= "+1" (.-textContent overflow))))
         (done))
       0))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest size-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "md" (.-size el)))))

(deftest overlap-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "md" (.-overlap el)))))

(deftest direction-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "ltr" (.-direction el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Reconnect: stable after remove/re-add ────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-size "lg")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "lg" (.getAttribute el "data-size")))))
