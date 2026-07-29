(ns baredom.components.x-drag-panel.x-drag-panel-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-drag-panel.x-drag-panel :as panel]
            [baredom.components.x-drag-panel.model        :as model]
            [baredom.components.x-drop-zone.x-drop-zone   :as zone]
            [baredom.components.x-drop-zone.model         :as zone-model]))

(panel/init!)
(zone/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [node (.querySelectorAll js/document zone-model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

;; ── Builders ─────────────────────────────────────────────────────────────────
;; Explicit dimensions: an empty custom element has zero size in the headless
;; test page, and the lift path reads a real bounding rect.

(defn ^js make-el []
  (let [^js el (.createElement js/document model/tag-name)]
    (.setAttribute el model/attr-kind "task")
    (.setAttribute el model/attr-value "t-1")
    (set! (.. el -style -width) "200px")
    (set! (.. el -style -height) "100px")
    el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn collect! [^js el event-name]
  (let [seen (atom [])]
    (.addEventListener el event-name
                       (fn capture-event [^js e] (swap! seen conj (.-detail e))))
    seen))

(defn press!
  "Dispatch a pointerdown on the handle. Dispatching on the handle rather than
  the host matters: the grab check reads composedPath, which only contains the
  handle when the event actually originated there."
  [^js el]
  (let [^js handle (shadow-part el "[part=handle]")
        ^js rect   (.getBoundingClientRect el)]
    (.dispatchEvent handle
                    (js/PointerEvent. "pointerdown"
                                      #js {:bubbles     true
                                           :composed    true
                                           :cancelable  true
                                           :button      0
                                           :pointerId   1
                                           :pointerType "mouse"
                                           :clientX     (.-left rect)
                                           :clientY     (.-top rect)}))))

(defn escape! []
  (.dispatchEvent js/window (js/KeyboardEvent. "keydown" #js {:key "Escape"})))

;; ── Registration and structure ───────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=panel]")))
    (is (some? (shadow-part el "[part=handle]")))
    (is (some? (shadow-part el "[part=grip]")))
    (is (some? (shadow-part el "[part=body]")))
    (is (some? (shadow-part el "slot[name=header]")))))

;; ── Handle accessibility ─────────────────────────────────────────────────────
(deftest handle-aria-test
  (let [^js el     (append! (make-el))
        ^js handle (shadow-part el "[part=handle]")]
    (is (= "button" (.getAttribute handle "role")))
    (is (= model/default-label (.getAttribute handle "aria-label")))
    (is (= "0" (.getAttribute handle "tabindex")))
    (.setAttribute el model/attr-label "Rewrite the parser")
    (is (= "Rewrite the parser" (.getAttribute handle "aria-label")))))

(deftest disabled-removes-from-tab-order-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "-1" (.getAttribute (shadow-part el "[part=handle]") "tabindex")))))

(deftest pending-stays-tab-reachable-test
  (let [^js el     (append! (make-el))
        _          (.setAttribute el model/attr-pending "")
        ^js handle (shadow-part el "[part=handle]")]
    (testing "a stalled move must stay discoverable by keyboard — that is the
              whole reason the box is preserved"
      (is (= "0" (.getAttribute handle "tabindex"))))
    (is (= "true" (.getAttribute handle "aria-busy")))
    (.removeAttribute el model/attr-pending)
    (is (nil? (.getAttribute handle "aria-busy")))))

;; ── Property reflection ──────────────────────────────────────────────────────
(deftest property-reflection-test
  (let [^js el (append! (make-el))]
    (set! (.-kind el) "bug")
    (is (= "bug" (.getAttribute el model/attr-kind)))
    (set! (.-grab el) model/grab-surface)
    (is (= model/grab-surface (.getAttribute el model/attr-grab)))
    (set! (.-pending el) true)
    (is (.hasAttribute el model/attr-pending))
    (set! (.-pending el) false)
    (is (not (.hasAttribute el model/attr-pending)))
    (testing "absent string properties read as their declared default"
      (is (= model/default-label (.-label el)))
      (is (= model/auto-scroll-auto (.-autoScroll el))))))

;; ── Pointer drag ─────────────────────────────────────────────────────────────
(deftest pointer-drag-arms-test
  (let [^js el  (append! (make-el))
        started (collect! el model/event-drag-start)]
    (press! el)
    (is (.hasAttribute el "data-dragging"))
    (is (= 1 (count @started)))
    (is (= "task" (.-kind (first @started))))
    (is (= "t-1"  (.-value (first @started))))
    (escape!)))

(deftest drag-preserves-source-box-test
  (let [^js el (append! (make-el))
        before (.-height (.getBoundingClientRect el))]
    (press! el)
    (testing "the host holds the vacated box open so no zone reflows mid-drag"
      (is (= before (.-height (.getBoundingClientRect el)))))
    (escape!)))

(deftest escape-cancels-pointer-drag-test
  (let [^js el    (append! (make-el))
        cancelled (collect! el model/event-drag-cancel)]
    (press! el)
    (escape!)
    (is (not (.hasAttribute el "data-dragging")))
    (is (= 1 (count @cancelled)))
    (is (= model/reason-escape (.-reason (first @cancelled))))))

(deftest disabled-panel-does-not-arm-test
  (let [^js el  (append! (make-el))
        _       (.setAttribute el model/attr-disabled "")
        started (collect! el model/event-drag-start)]
    (press! el)
    (is (not (.hasAttribute el "data-dragging")))
    (is (= 0 (count @started)))))

(deftest pending-panel-does-not-arm-test
  (let [^js el  (append! (make-el))
        _       (.setAttribute el model/attr-pending "")
        started (collect! el model/event-drag-start)]
    (testing "a panel whose first drop is still in flight cannot start a second"
      (press! el)
      (is (not (.hasAttribute el "data-dragging")))
      (is (= 0 (count @started))))))

;; ── Keyboard drag ────────────────────────────────────────────────────────────
(defn key-on-handle! [^js el k]
  (.dispatchEvent (shadow-part el "[part=handle]")
                  (js/KeyboardEvent. "keydown"
                                     #js {:key k :bubbles true :composed true})))

(defn ^js make-zone []
  (let [^js z (.createElement js/document zone-model/tag-name)]
    (.setAttribute z zone-model/attr-accepts "task")
    (.appendChild (.-body js/document) z)
    z))

(deftest keyboard-pickup-test
  (let [^js z   (make-zone)
        ^js el  (append! (make-el))
        started (collect! el model/event-drag-start)]
    (key-on-handle! el " ")
    (is (= 1 (count @started)))
    (is (.hasAttribute el "data-dragging"))
    (testing "the first candidate zone reports the hover"
      (is (= zone-model/state-over (.getAttribute z zone-model/attr-drag-state))))
    (key-on-handle! el "Escape")))

(deftest keyboard-commit-test
  (let [^js z   (make-zone)
        ^js el  (append! (make-el))
        dropped (collect! z zone-model/event-drop)]
    (key-on-handle! el " ")
    (key-on-handle! el "Enter")
    (is (= 1 (count @dropped)))
    (is (= "t-1" (.-value (first @dropped))))
    (testing "pointer and keyboard converge on one event, so an app handles one path"
      (is (= 0 (.-index (first @dropped)))))
    (is (not (.hasAttribute el "data-dragging")))
    (testing "the panel is still where it was — the app owns the landing"
      (is (identical? (.-body js/document) (.-parentElement el))))))

(deftest keyboard-escape-cancels-test
  (let [^js z     (make-zone)
        ^js el    (append! (make-el))
        cancelled (collect! el model/event-drag-cancel)]
    (key-on-handle! el " ")
    (key-on-handle! el "Escape")
    (is (= 1 (count @cancelled)))
    (is (= model/reason-escape (.-reason (first @cancelled))))
    (is (nil? (.getAttribute z zone-model/attr-drag-state)))))

(deftest keyboard-pickup-without-zones-is-inert-test
  (let [^js el  (append! (make-el))
        started (collect! el model/event-drag-start)]
    (key-on-handle! el " ")
    (testing "nothing to drop onto means nothing was picked up"
      (is (= 0 (count @started)))
      (is (not (.hasAttribute el "data-dragging"))))))
