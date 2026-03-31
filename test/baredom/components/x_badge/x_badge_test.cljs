(ns baredom.components.x-badge.x-badge-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-badge.x-badge :as x]
            [baredom.components.x-badge.model   :as model]))

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

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=base]")))
    (is (some? (shadow-part el "[part=label]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "neutral" (.getAttribute el "data-variant")))
    (is (= "md"      (.getAttribute el "data-size")))
    (is (= "empty"   (.getAttribute el "data-mode")))))

;; ── Variant attribute ─────────────────────────────────────────────────────
(deftest variant-attribute-test
  (doseq [v ["neutral" "info" "success" "warning" "error"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant v)
      (is (= v (.getAttribute el "data-variant"))))))

(deftest variant-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-variant "bad")
    (is (= "neutral" (.getAttribute el "data-variant")))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-attribute-test
  (doseq [s ["sm" "md"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size s)
      (is (= s (.getAttribute el "data-size"))))))

;; ── Count mode ────────────────────────────────────────────────────────────
(deftest count-mode-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-count "7")
    (is (= "count" (.getAttribute el "data-mode")))
    (is (= "7"     (.-textContent (shadow-part el "[part=label]"))))))

(deftest count-capped-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-count "100")
    (.setAttribute el model/attr-max   "99")
    (is (= "99+" (.-textContent (shadow-part el "[part=label]"))))))

(deftest count-at-max-not-capped-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-count "99")
    (.setAttribute el model/attr-max   "99")
    (is (= "99" (.-textContent (shadow-part el "[part=label]"))))))

;; ── Text mode ─────────────────────────────────────────────────────────────
(deftest text-mode-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-text "NEW")
    (is (= "text" (.getAttribute el "data-mode")))
    (is (= "NEW"  (.-textContent (shadow-part el "[part=label]"))))))

;; ── Dot mode ─────────────────────────────────────────────────────────────
(deftest dot-mode-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-dot "")
    (is (= "dot" (.getAttribute el "data-mode")))
    (is (true? (.hasAttribute el "data-dot")))))

;; ── Pill modifier ─────────────────────────────────────────────────────────
(deftest pill-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-pill "")
    (is (true? (.hasAttribute el "data-pill")))
    (.removeAttribute el model/attr-pill)
    (is (false? (.hasAttribute el "data-pill")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest variant-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "neutral" (.-variant el)))))

(deftest size-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "md" (.-size el)))))

(deftest count-property-test
  (let [^js el (append! (make-el))]
    (set! (.-count el) 5)
    (is (= "5" (.getAttribute el model/attr-count)))))

(deftest max-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 99 (.-max el)))))

(deftest bool-property-pill-test
  (let [^js el (append! (make-el))]
    (set! (.-pill el) true)
    (is (true? (.-pill el)))
    (set! (.-pill el) false)
    (is (false? (.-pill el)))))

(deftest display-text-property-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-count "42")
    (is (= "42" (.-displayText el)))))

;; ── Slot mode ─────────────────────────────────────────────────────────────
;; slotchange fires as a microtask, so assertions run in Promise callbacks.
(deftest slot-mode-test
  (async done
    (let [^js el   (append! (make-el))
          ^js span (.createElement js/document "span")]
      (is (= "empty" (.getAttribute el "data-mode")))
      (.appendChild el span)
      (.then (js/Promise.resolve)
             (fn []
               (is (= "slot" (.getAttribute el "data-mode")))
               (.remove span)
               (.then (js/Promise.resolve)
                      (fn []
                        (is (= "empty" (.getAttribute el "data-mode")))
                        (done))))))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-count "5")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "5" (.-textContent (shadow-part el "[part=label]"))))))
