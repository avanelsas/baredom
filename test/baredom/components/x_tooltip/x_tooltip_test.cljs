(ns baredom.components.x-tooltip.x-tooltip-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-tooltip.x-tooltip :as x]
            [baredom.components.x-tooltip.model     :as model]))

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

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=trigger]")))
    (is (some? (shadow-part el "[part=panel]")))
    (is (some? (shadow-part el "[part=arrow]")))
    (is (some? (shadow-part el "[part=body]")))
    (is (some? (shadow-part el "[part=text]")))
    (is (some? (shadow-part el "slot[name=content]")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el    (append! (make-el))
        ^js panel (shadow-part el "[part=panel]")]
    (is (= "tooltip" (.getAttribute panel "role")))
    (is (= "true"    (.getAttribute panel "aria-hidden")))
    (is (= "top"     (.getAttribute panel "data-placement")))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── text attribute ───────────────────────────────────────────────────────────
(deftest text-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello")
    (is (= "Hello" (.-textContent (shadow-part el "[part=text]"))))))

;; ── placement attribute ──────────────────────────────────────────────────────
(deftest placement-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-placement "bottom")
    (is (= "bottom" (.getAttribute (shadow-part el "[part=panel]") "data-placement")))))

(deftest placement-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-placement "center")
    (is (= "top" (.getAttribute (shadow-part el "[part=panel]") "data-placement")))))

;; ── open attribute ───────────────────────────────────────────────────────────
(deftest open-attribute-shows-panel-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (is (= "false" (.getAttribute (shadow-part el "[part=panel]") "aria-hidden")))))

(deftest open-removed-hides-panel-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (.removeAttribute el model/attr-open)
    (is (= "true" (.getAttribute (shadow-part el "[part=panel]") "aria-hidden")))))

;; ── disabled blocks show ─────────────────────────────────────────────────────
(deftest disabled-hides-open-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (.setAttribute el model/attr-disabled "")
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-text el) "Test tip")
    (is (= "Test tip" (.getAttribute el model/attr-text)))
    (is (= "Test tip" (.-text el)))))

(deftest placement-property-test
  (let [^js el (append! (make-el))]
    (set! (.-placement el) "right")
    (is (= "right" (.getAttribute el model/attr-placement)))
    (is (= "right" (.-placement el)))))

(deftest delay-property-test
  (let [^js el (append! (make-el))]
    (set! (.-delay el) 200)
    (is (= "200" (.getAttribute el model/attr-delay)))
    (is (= 200 (.-delay el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest open-property-test
  (let [^js el (append! (make-el))]
    (set! (.-open el) true)
    (is (.hasAttribute el model/attr-open))
    (set! (.-open el) false)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── show() / hide() methods ─────────────────────────────────────────────────
(deftest show-method-test
  (let [^js el (append! (make-el))]
    (.show el)
    (is (.hasAttribute el model/attr-open))))

(deftest hide-method-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.hide el)
    (is (not (.hasAttribute el model/attr-open)))))

(deftest show-method-fires-event-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-show (fn [_] (swap! events conj :show)))
    (.show el)
    (is (= 1 (count @events)))))

(deftest hide-method-fires-event-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.show el)
    (.addEventListener el model/event-hide (fn [_] (swap! events conj :hide)))
    (.hide el)
    (is (= 1 (count @events)))))

;; ── Escape key hides ─────────────────────────────────────────────────────────
(deftest escape-key-hides-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest escape-key-ignored-when-closed-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-hide (fn [_] (swap! events conj :hide)))
    (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (= 0 (count @events)))))

;; ── focusin shows immediately ────────────────────────────────────────────────
(deftest focusin-shows-immediately-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-delay "1000")
    (.dispatchEvent el (js/FocusEvent. "focusin" #js {:bubbles true}))
    (is (.hasAttribute el model/attr-open))))

;; ── focusout hides ───────────────────────────────────────────────────────────
(deftest focusout-hides-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.dispatchEvent el (js/FocusEvent. "focusout" #js {:bubbles true :relatedTarget nil}))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── pointerenter with delay ──────────────────────────────────────────────────
(deftest pointerenter-with-zero-delay-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-delay "0")
    (.dispatchEvent el (js/PointerEvent. "pointerenter" #js {:bubbles true}))
    (is (.hasAttribute el model/attr-open))))

(deftest pointerleave-hides-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.dispatchEvent el (js/PointerEvent. "pointerleave" #js {:bubbles true}))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest pointerleave-cancels-pending-show-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-delay "100")
      (.dispatchEvent el (js/PointerEvent. "pointerenter" #js {:bubbles true}))
      ;; Not yet open (delay = 100ms)
      (is (not (.hasAttribute el model/attr-open)))
      ;; Leave before delay elapses
      (.dispatchEvent el (js/PointerEvent. "pointerleave" #js {:bubbles true}))
      ;; Wait for more than the delay
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-open)))
         (done))
       200))))

;; ── aria-describedby on slotted trigger ──────────────────────────────────────
(deftest aria-describedby-set-on-trigger-test
  (async done
    (let [^js el  (make-el)
          ^js btn (.createElement js/document "button")]
      (set! (.-textContent btn) "Trigger")
      (.appendChild el btn)
      (append! el)
      ;; slotchange is async
      (js/setTimeout
       (fn []
         (let [panel-id (.getAttribute (shadow-part el "[part=panel]") "id")]
           (is (some? panel-id))
           (is (= panel-id (.getAttribute btn "aria-describedby"))))
         (done))
       50))))

;; ── aria-describedby removed on disconnect ───────────────────────────────────
(deftest aria-describedby-removed-on-disconnect-test
  (async done
    (let [^js el  (make-el)
          ^js btn (.createElement js/document "button")]
      (set! (.-textContent btn) "Trigger")
      (.appendChild el btn)
      (append! el)
      (js/setTimeout
       (fn []
         ;; Verify it's set
         (is (some? (.getAttribute btn "aria-describedby")))
         ;; Disconnect
         (.remove el)
         ;; Verify cleanup
         (is (nil? (.getAttribute btn "aria-describedby")))
         (done))
       50))))

;; ── show() blocked when disabled ─────────────────────────────────────────────
(deftest show-blocked-when-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.show el)
    (is (not (.hasAttribute el model/attr-open)))))

;; ── focusout does not hide when focus stays inside host ──────────────────────
(deftest focusout-inside-host-does-not-hide-test
  (let [^js el  (make-el)
        ^js btn (.createElement js/document "button")]
    (.appendChild el btn)
    (append! el)
    (.show el)
    (.dispatchEvent el (js/FocusEvent. "focusout" #js {:bubbles true :relatedTarget btn}))
    (is (.hasAttribute el model/attr-open))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-double-events-test
  (let [^js el (make-el)
        events (atom [])]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.addEventListener el model/event-show (fn [_] (swap! events conj :show)))
    (.show el)
    (is (= 1 (count @events)))))

;; ── Panel has role=tooltip ───────────────────────────────────────────────────
(deftest panel-role-tooltip-test
  (let [^js el (append! (make-el))]
    (is (= "tooltip" (.getAttribute (shadow-part el "[part=panel]") "role")))))

;; ── Panel has unique id ──────────────────────────────────────────────────────
(deftest panel-has-unique-id-test
  (let [^js el1 (append! (make-el))
        ^js el2 (append! (make-el))
        id1     (.getAttribute (shadow-part el1 "[part=panel]") "id")
        id2     (.getAttribute (shadow-part el2 "[part=panel]") "id")]
    (is (some? id1))
    (is (some? id2))
    (is (not= id1 id2))))
