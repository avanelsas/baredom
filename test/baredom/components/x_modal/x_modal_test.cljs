(ns baredom.components.x-modal.x-modal-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-modal.x-modal :as x]
            [baredom.components.x-modal.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el sel]
  (.querySelector (.-shadowRoot el) sel))

;; ── Registration ──────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ──────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=backdrop]")))
    (is (some? (shadow-part el "[part=dialog]")))
    (is (some? (shadow-part el "[part=header]")))
    (is (some? (shadow-part el "[part=body]")))
    (is (some? (shadow-part el "[part=footer]")))))

;; ── Closed by default ─────────────────────────────────────────────────────────
(deftest closed-by-default-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-open)))
    (is (= "false" (.getAttribute el "data-open")))))

;; ── Default size ──────────────────────────────────────────────────────────────
(deftest default-size-test
  (let [^js el (append! (make-el))]
    (is (= model/default-size (.getAttribute el "data-size")))))

;; ── Default label ─────────────────────────────────────────────────────────────
(deftest default-label-test
  (let [^js el     (append! (make-el))
        ^js dialog (shadow-part el "[part=dialog]")]
    (is (= model/default-label (.getAttribute dialog "aria-label")))))

;; ── open attribute sets data-open=true ───────────────────────────────────────
(deftest open-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (is (.hasAttribute el model/attr-open))
    (is (= "true" (.getAttribute el "data-open")))))

;; ── size attribute reflected in data-size ────────────────────────────────────
(deftest size-attribute-test
  (doseq [s ["sm" "md" "lg" "xl" "full"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size s)
      (is (= s (.getAttribute el "data-size"))))))

(deftest invalid-size-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "huge")
    (is (= model/default-size (.getAttribute el "data-size")))))

;; ── custom label attribute ────────────────────────────────────────────────────
(deftest custom-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Confirm Delete")
    (let [^js dialog (shadow-part el "[part=dialog]")]
      (is (= "Confirm Delete" (.getAttribute dialog "aria-label"))))))

;; ── dialog role and aria-modal ────────────────────────────────────────────────
(deftest dialog-aria-attrs-test
  (let [^js el     (append! (make-el))
        ^js dialog (shadow-part el "[part=dialog]")]
    (is (= "dialog" (.getAttribute dialog "role")))
    (is (= "true" (.getAttribute dialog "aria-modal")))))

;; ── show/hide/toggle methods ──────────────────────────────────────────────────
(deftest show-method-test
  (let [^js el (append! (make-el))]
    (.show el)
    (is (.hasAttribute el model/attr-open))))

(deftest hide-method-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (.hide el)
    (is (not (.hasAttribute el model/attr-open)))))

(deftest toggle-method-opens-test
  (let [^js el (append! (make-el))]
    (.toggle el)
    (is (.hasAttribute el model/attr-open))))

(deftest toggle-method-closes-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (.toggle el)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── open property ─────────────────────────────────────────────────────────────
(deftest open-property-get-test
  (let [^js el (append! (make-el))]
    (is (= false (.-open el)))
    (.setAttribute el model/attr-open "")
    (is (= true (.-open el)))))

(deftest open-property-set-true-test
  (let [^js el (append! (make-el))]
    (set! (.-open el) true)
    (is (.hasAttribute el model/attr-open))))

(deftest open-property-set-false-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (set! (.-open el) false)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── size property ─────────────────────────────────────────────────────────────
(deftest size-property-get-default-test
  (let [^js el (append! (make-el))]
    (is (= model/default-size (.-size el)))))

(deftest size-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))))

;; ── label property ────────────────────────────────────────────────────────────
(deftest label-property-get-default-test
  (let [^js el (append! (make-el))]
    (is (= model/default-label (.-label el)))))

(deftest label-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "Settings")
    (is (= "Settings" (.getAttribute el model/attr-label)))))

;; ── toggle event fires on open ────────────────────────────────────────────────
(deftest toggle-event-on-open-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! events conj e)))
    (.setAttribute el model/attr-open "")
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (= true (.-open (.-detail ev)))))))

;; ── toggle event fires on close ───────────────────────────────────────────────
(deftest toggle-event-on-close-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-open "")
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! events conj e)))
    (.removeAttribute el model/attr-open)
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (= false (.-open (.-detail ev)))))))

;; ── dismiss event on backdrop click ──────────────────────────────────────────
(deftest dismiss-event-backdrop-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-open "")
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (swap! events conj e)))
    (.click (shadow-part el "[part=backdrop]"))
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (= "backdrop" (.-reason (.-detail ev)))))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── backdrop click fires exactly one toggle (close) ───────────────────────────
(deftest backdrop-click-closes-modal-test
  (let [^js el  (append! (make-el))
        toggles (atom [])]
    (.setAttribute el model/attr-open "")
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! toggles conj e)))
    (.click (shadow-part el "[part=backdrop]"))
    (is (= 1 (count @toggles)))
    (is (= false (.-open (.-detail (first @toggles)))))))

;; ── reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-listener-doubling-test
  (let [^js el (make-el)
        events (atom [])]
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (swap! events conj e)))
    (.appendChild (.-body js/document) el)
    (.setAttribute el model/attr-open "")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.setAttribute el model/attr-open "")
    (.click (shadow-part el "[part=backdrop]"))
    (is (= 1 (count @events)))))

;; ── toggle event not fired without open state change ─────────────────────────
(deftest toggle-not-fired-on-non-open-attr-change-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! events conj e)))
    (.setAttribute el model/attr-label "New label")
    (is (= 0 (count @events)))))

;; ── Escape key dismissal ──────────────────────────────────────────────────────
(deftest escape-key-dismiss-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-open "")
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (swap! events conj e)))
    (let [^js dialog (shadow-part el "[part=dialog]")
          ^js ev     (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true})]
      (.dispatchEvent dialog ev))
    (is (= 1 (count @events)))
    (is (= "escape" (.-reason (.-detail (first @events)))))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Focus trap: first focusable element gets focus on open ───────────────────
(deftest focus-trap-focuses-first-element-test
  (async done
    (let [^js el  (append! (make-el))
          ^js btn (.createElement js/document "button")]
      (set! (.-textContent btn) "Close")
      (.appendChild el btn)
      (.setAttribute el model/attr-open "")
      ;; activate-focus-trap! defers via setTimeout 0
      (js/setTimeout
       (fn []
         (is (= btn (.-activeElement js/document)))
         (done))
       50))))

;; ── Focus trap: Tab wraps from last to first ──────────────────────────────────
(deftest focus-trap-tab-wraps-test
  (async done
    (let [^js el   (append! (make-el))
          ^js btn1 (.createElement js/document "button")
          ^js btn2 (.createElement js/document "button")]
      (.appendChild el btn1)
      (.appendChild el btn2)
      (.setAttribute el model/attr-open "")
      (js/setTimeout
       (fn []
         (.focus btn2)
         (let [^js dialog (shadow-part el "[part=dialog]")
               ^js ev     (js/KeyboardEvent. "keydown" #js {:key "Tab" :shiftKey false :bubbles true :cancelable true})]
           (.dispatchEvent dialog ev))
         (is (= btn1 (.-activeElement js/document)))
         (done))
       50))))

;; ── Focus trap: Shift+Tab wraps from first to last ────────────────────────────
(deftest focus-trap-shift-tab-wraps-test
  (async done
    (let [^js el   (append! (make-el))
          ^js btn1 (.createElement js/document "button")
          ^js btn2 (.createElement js/document "button")]
      (.appendChild el btn1)
      (.appendChild el btn2)
      (.setAttribute el model/attr-open "")
      (js/setTimeout
       (fn []
         (.focus btn1)
         (let [^js dialog (shadow-part el "[part=dialog]")
               ^js ev     (js/KeyboardEvent. "keydown" #js {:key "Tab" :shiftKey true :bubbles true :cancelable true})]
           (.dispatchEvent dialog ev))
         (is (= btn2 (.-activeElement js/document)))
         (done))
       50))))

;; ── Focus trap: focus restored on close ───────────────────────────────────────
(deftest focus-trap-restores-focus-on-close-test
  (async done
    (let [^js trigger (.createElement js/document "button")
          _           (.appendChild (.-body js/document) trigger)
          ^js el      (append! (make-el))
          ^js btn     (.createElement js/document "button")]
      (.appendChild el btn)
      (.focus trigger)
      (.setAttribute el model/attr-open "")
      (js/setTimeout
       (fn []
         (.removeAttribute el model/attr-open)
         (is (= trigger (.-activeElement js/document)))
         (.remove trigger)
         (done))
       50))))
