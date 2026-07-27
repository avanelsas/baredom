(ns baredom.components.x-floating-panel.x-floating-panel-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-floating-panel.x-floating-panel :as x]
            [baredom.components.x-floating-panel.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [^js node (.querySelectorAll js/document "[data-fp-fixture]")]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el sel]
  (.querySelector (.-shadowRoot el) sel))

(defn ^js panel-of [^js el]
  (shadow-part el "[part=panel]"))

(defn ^js handle-of [^js el]
  (shadow-part el "[part=handle]"))

(defn ^js close-of [^js el]
  (shadow-part el "[part=close]"))

(defn- inline-style [^js node prop]
  (.getPropertyValue (.-style node) prop))

(defn- pointer-event [type-name x y]
  (js/PointerEvent. type-name
                    #js {:clientX   x
                         :clientY   y
                         :pointerId 1
                         :button    0
                         :bubbles   true
                         :cancelable true}))

(defn- key-event [key-name shift?]
  (js/KeyboardEvent. "keydown"
                     #js {:key        key-name
                          :shiftKey   shift?
                          :bubbles    true
                          :cancelable true}))

(defn- drag! [^js el from-x from-y to-x to-y]
  (let [^js handle (handle-of el)]
    (.dispatchEvent handle (pointer-event "pointerdown" from-x from-y))
    (.dispatchEvent handle (pointer-event "pointermove" to-x to-y))
    (.dispatchEvent handle (pointer-event "pointerup" to-x to-y))))

(defn- next-frame [f]
  (js/requestAnimationFrame (fn [_] (js/requestAnimationFrame (fn [_] (f))))))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ─────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (panel-of el)))
    (is (some? (handle-of el)))
    (is (some? (close-of el)))
    (is (some? (shadow-part el "[part=body]")))))

(deftest panel-is-a-non-modal-dialog-test
  (let [^js el    (append! (make-el))
        ^js panel (panel-of el)]
    (is (= "dialog" (.getAttribute panel "role")))
    (is (= "false" (.getAttribute panel "aria-modal")))))

(deftest handle-is-keyboard-reachable-test
  (testing "WCAG 2.2 SC 2.5.7 requires a single-pointer alternative to drag"
    (let [^js handle (handle-of (append! (make-el)))]
      (is (= "0" (.getAttribute handle "tabindex")))
      (is (= "button" (.getAttribute handle "role")))
      (is (some? (.getAttribute handle "aria-label"))))))

(deftest handle-does-not-promise-an-activation-test
  (testing "the handle is never activated — Enter and Space do nothing — so a
            bare role=button would mislead. aria-roledescription renames it and
            aria-keyshortcuts advertises the keys that do work"
    (let [^js handle (handle-of (append! (make-el)))]
      (is (= "Drag handle" (.getAttribute handle "aria-roledescription")))
      (is (= "ArrowUp ArrowDown ArrowLeft ArrowRight"
             (.getAttribute handle "aria-keyshortcuts"))))))

(deftest enter-and-space-do-not-move-the-panel-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.dispatchEvent handle (key-event "Enter" false))
    (.dispatchEvent handle (key-event " " false))
    (is (not (.hasAttribute el model/attr-x)))))

;; ── Open / closed ────────────────────────────────────────────────────────────
(deftest closed-by-default-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-open)))))

(deftest show-hide-toggle-test
  (let [^js el (append! (make-el))]
    (.show el)
    (is (.hasAttribute el model/attr-open))
    (.hide el)
    (is (not (.hasAttribute el model/attr-open)))
    (.toggle el)
    (is (.hasAttribute el model/attr-open))
    (.toggle el)
    (is (not (.hasAttribute el model/attr-open)))))

(deftest toggle-event-test
  (let [^js el  (append! (make-el))
        seen    (atom [])]
    (.addEventListener el model/event-toggle
                       (fn [^js e] (swap! seen conj (.. e -detail -open))))
    (.show el)
    (.hide el)
    (is (= [true false] @seen))))

(deftest toggle-event-not-repeated-test
  (testing "the model-change guard suppresses a redundant open"
    (let [^js el (append! (make-el))
          seen   (atom 0)]
      (.addEventListener el model/event-toggle (fn [_] (swap! seen inc)))
      (.show el)
      (.show el)
      (.setAttribute el model/attr-open "")
      (is (= 1 @seen)))))

;; ── Position ─────────────────────────────────────────────────────────────────
(deftest unpositioned-panel-has-no-inline-offsets-test
  (testing "absent x/y leaves the CSS default placement in charge"
    (let [^js panel (panel-of (append! (make-el)))]
      (is (= "" (inline-style panel "left")))
      (is (= "" (inline-style panel "top"))))))

(deftest position-attributes-become-inline-offsets-test
  (let [^js el    (append! (make-el))
        ^js panel (panel-of el)]
    (.setAttribute el model/attr-x "120")
    (.setAttribute el model/attr-y "80")
    (is (= "120px" (inline-style panel "left")))
    (is (= "80px" (inline-style panel "top")))))

(deftest removing-position-restores-css-default-test
  (let [^js el    (append! (make-el))
        ^js panel (panel-of el)]
    (.setAttribute el model/attr-x "120")
    (is (= "120px" (inline-style panel "left")))
    (.removeAttribute el model/attr-x)
    (is (= "" (inline-style panel "left")))))

(deftest invalid-position-is-ignored-test
  (testing "garbage coordinates fall through to the CSS default, not to zero"
    (let [^js el    (append! (make-el))
          ^js panel (panel-of el)]
      (.setAttribute el model/attr-x "left")
      (is (= "" (inline-style panel "left"))))))

(deftest position-properties-reflect-test
  (let [^js el (append! (make-el))]
    (set! (.-x el) 40)
    (set! (.-y el) 60)
    (is (= "40" (.getAttribute el model/attr-x)))
    (is (= "60" (.getAttribute el model/attr-y)))
    (is (= 40 (.-x el)))
    (is (= 60 (.-y el)))
    (set! (.-x el) nil)
    (is (not (.hasAttribute el model/attr-x)))
    (is (nil? (.-x el)))))

;; ── Dragging ─────────────────────────────────────────────────────────────────
(deftest drag-commits-position-test
  (let [^js el (append! (make-el))]
    (.show el)
    (drag! el 100 100 260 220)
    (is (.hasAttribute el model/attr-x))
    (is (.hasAttribute el model/attr-y))))

(deftest drag-moves-by-the-pointer-delta-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.setAttribute el model/attr-x "100")
    (.setAttribute el model/attr-y "100")
    (drag! el 150 150 200 180)
    (is (= 150 (.-x el)))
    (is (= 130 (.-y el)))))

(deftest drag-dispatches-one-move-event-test
  (testing "the pointer path emits a single move on drop, never per frame"
    (let [^js el (append! (make-el))
          seen   (atom [])]
      (.show el)
      (.addEventListener el model/event-move
                         (fn [^js e] (swap! seen conj (.. e -detail -source))))
      (drag! el 100 100 180 160)
      (is (= [model/source-pointer] @seen)))))

(deftest drag-frame-updates-position-before-drop-test
  (async done
    (let [^js el (append! (make-el))]
      (.show el)
      (.setAttribute el model/attr-x "100")
      (.setAttribute el model/attr-y "100")
      (let [^js handle (handle-of el)]
        (.dispatchEvent handle (pointer-event "pointerdown" 150 150))
        (.dispatchEvent handle (pointer-event "pointermove" 200 200))
        (next-frame
         (fn []
           (is (= 150 (.-x el)))
           (is (= 150 (.-y el)))
           (.dispatchEvent handle (pointer-event "pointerup" 200 200))
           (done)))))))

(deftest drop-commits-the-position-the-last-frame-wrote-test
  (testing "position is derived from one drag value, so the frame write and the
            drop commit cannot disagree"
    (async done
      (let [^js el     (append! (make-el))
            ^js handle (handle-of el)]
        (.show el)
        (.setAttribute el model/attr-x "100")
        (.setAttribute el model/attr-y "100")
        (.dispatchEvent handle (pointer-event "pointerdown" 150 150))
        (.dispatchEvent handle (pointer-event "pointermove" 240 210))
        (next-frame
         (fn []
           (let [mid-x (.-x el)
                 mid-y (.-y el)]
             (.dispatchEvent handle (pointer-event "pointerup" 240 210))
             (is (= mid-x (.-x el)))
             (is (= mid-y (.-y el)))
             (done))))))))

(deftest drag-is-clamped-to-the-viewport-test
  (let [^js el (append! (make-el))]
    (.show el)
    (drag! el 100 100 -9999 -9999)
    (is (= 0 (.-y el)))
    (is (>= (.-x el) (- model/min-visible (.-innerWidth js/window))))))

(deftest close-button-press-does-not-start-a-drag-test
  (testing "the close button lives inside the drag surface; pressing it must
            not be interpreted as a grab"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-closable "")
      (.show el)
      (let [^js close (close-of el)
            seen      (atom [])]
        (.addEventListener el model/event-move (fn [_] (swap! seen conj :move)))
        (.dispatchEvent close (pointer-event "pointerdown" 300 110))
        (.dispatchEvent close (pointer-event "pointerup" 300 110))
        (is (empty? @seen))
        (is (not (.hasAttribute el model/attr-x)))))))

(deftest no-drag-opt-out-in-slotted-header-test
  (testing "header content marked data-no-drag is not a grab surface"
    (let [^js el  (append! (make-el))
          ^js btn (.createElement js/document "button")]
      (.setAttribute btn "slot" "header")
      (.setAttribute btn "data-no-drag" "")
      (.appendChild el btn)
      (.show el)
      (let [seen (atom [])]
        (.addEventListener el model/event-move (fn [_] (swap! seen conj :move)))
        (.dispatchEvent btn (pointer-event "pointerdown" 300 110))
        (.dispatchEvent btn (pointer-event "pointerup" 300 110))
        (is (empty? @seen))))))

(deftest click-on-the-handle-is-not-a-drag-test
  (testing "a press that never moves must not announce a move, and must not
            silently pin an unpositioned panel to viewport coordinates"
    (let [^js el     (append! (make-el))
          ^js handle (handle-of el)
          seen       (atom [])]
      (.show el)
      (.addEventListener el model/event-move (fn [_] (swap! seen conj :move)))
      (.dispatchEvent handle (pointer-event "pointerdown" 150 150))
      (.dispatchEvent handle (pointer-event "pointerup" 150 150))
      (is (empty? @seen))
      (is (not (.hasAttribute el model/attr-x)))
      (is (not (.hasAttribute el model/attr-y))))))

(deftest pointer-cancel-announces-the-final-position-test
  (testing "frames already moved the panel, so an interrupted drag must still
            announce where it ended — otherwise a consumer persisting position
            silently drifts out of sync with the DOM"
    (let [^js el     (append! (make-el))
          ^js handle (handle-of el)
          seen       (atom [])]
      (.show el)
      (.setAttribute el model/attr-x "100")
      (.setAttribute el model/attr-y "100")
      (.addEventListener el model/event-move
                         (fn [^js e] (swap! seen conj [(.. e -detail -x)
                                                       (.. e -detail -y)])))
      (.dispatchEvent handle (pointer-event "pointerdown" 150 150))
      (.dispatchEvent handle (pointer-event "pointermove" 200 180))
      (.dispatchEvent handle (pointer-event "pointercancel" 200 180))
      (is (= [[150 130]] @seen))
      (is (= 150 (.-x el))))))

;; ── Keyboard positioning ─────────────────────────────────────────────────────
(deftest arrow-keys-move-the-panel-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.setAttribute el model/attr-x "100")
    (.setAttribute el model/attr-y "100")
    (.dispatchEvent handle (key-event "ArrowRight" false))
    (is (= 110 (.-x el)))
    (.dispatchEvent handle (key-event "ArrowDown" false))
    (is (= 110 (.-y el)))))

(deftest shift-arrow-moves-one-pixel-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.setAttribute el model/attr-x "100")
    (.setAttribute el model/attr-y "100")
    (.dispatchEvent handle (key-event "ArrowLeft" true))
    (is (= 99 (.-x el)))))

(deftest step-attribute-sets-the-arrow-distance-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.setAttribute el model/attr-step "25")
    (.setAttribute el model/attr-x "100")
    (.setAttribute el model/attr-y "100")
    (.dispatchEvent handle (key-event "ArrowRight" false))
    (is (= 125 (.-x el)))))

(deftest step-property-reflects-test
  (let [^js el (append! (make-el))]
    (is (= model/default-step (.-step el)))
    (set! (.-step el) 25)
    (is (= "25" (.getAttribute el model/attr-step)))
    (is (= 25 (.-step el)))))

(deftest invalid-step-falls-back-to-default-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.setAttribute el model/attr-step "0")
    (.setAttribute el model/attr-x "100")
    (.setAttribute el model/attr-y "100")
    (.dispatchEvent handle (key-event "ArrowRight" false))
    (is (= (+ 100 model/default-step) (.-x el)))))

(deftest arrow-keys-dispatch-a-keyboard-move-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)
        seen       (atom [])]
    (.show el)
    (.addEventListener el model/event-move
                       (fn [^js e] (swap! seen conj (.. e -detail -source))))
    (.dispatchEvent handle (key-event "ArrowUp" false))
    (is (= [model/source-keyboard] @seen))))

(deftest non-arrow-keys-do-not-move-test
  (let [^js el     (append! (make-el))
        ^js handle (handle-of el)]
    (.show el)
    (.dispatchEvent handle (key-event "Enter" false))
    (is (not (.hasAttribute el model/attr-x)))))

;; ── Dismissal ────────────────────────────────────────────────────────────────
(deftest close-button-dismisses-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-closable "")
    (.show el)
    (.click (close-of el))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest close-button-dispatches-dismiss-request-test
  (let [^js el (append! (make-el))
        seen   (atom [])]
    (.setAttribute el model/attr-closable "")
    (.show el)
    (.addEventListener el model/event-dismiss-request
                       (fn [^js e] (swap! seen conj (.. e -detail -reason))))
    (.click (close-of el))
    (is (= [model/reason-close-button] @seen))))

(deftest dismiss-request-is-cancelable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-closable "")
    (.show el)
    (.addEventListener el model/event-dismiss-request
                       (fn [^js e] (.preventDefault e)))
    (.click (close-of el))
    (is (.hasAttribute el model/attr-open))))

(deftest escape-dismisses-when-closable-test
  (let [^js el    (append! (make-el))
        ^js panel (panel-of el)]
    (.setAttribute el model/attr-closable "")
    (.show el)
    (.dispatchEvent panel (key-event "Escape" false))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest escape-is-inert-when-not-closable-test
  (testing "a non-dismissible panel must not vanish while the user types in it"
    (let [^js el    (append! (make-el))
          ^js panel (panel-of el)]
      (.show el)
      (.dispatchEvent panel (key-event "Escape" false))
      (is (.hasAttribute el model/attr-open)))))

(deftest programmatic-hide-does-not-request-dismiss-test
  (let [^js el (append! (make-el))
        seen   (atom 0)]
    (.setAttribute el model/attr-closable "")
    (.show el)
    (.addEventListener el model/event-dismiss-request (fn [_] (swap! seen inc)))
    (.hide el)
    (is (zero? @seen))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Accessible name ──────────────────────────────────────────────────────────
(deftest default-label-test
  (let [^js panel (panel-of (append! (make-el)))]
    (is (= model/default-label (.getAttribute panel "aria-label")))))

(deftest label-attribute-names-the-dialog-test
  (let [^js el    (append! (make-el))
        ^js panel (panel-of el)]
    (.setAttribute el model/attr-label "Inspector")
    (is (= "Inspector" (.getAttribute panel "aria-label")))))

(deftest slotted-header-names-the-dialog-test
  (async done
    (let [^js el    (append! (make-el))
          ^js title (.createElement js/document "span")]
      (.setAttribute title "slot" "header")
      (set! (.-textContent title) "Layers")
      (.appendChild el title)
      (next-frame
       (fn []
         (let [^js panel (panel-of el)]
           (is (= "title" (.getAttribute panel "aria-labelledby")))
           (is (not (.hasAttribute panel "aria-label")))
           (done)))))))

;; ── Focus behaviour ──────────────────────────────────────────────────────────
(deftest does-not-steal-focus-by-default-test
  (async done
    (let [^js outside (.createElement js/document "button")]
      (.setAttribute outside "data-fp-fixture" "")
      (.appendChild (.-body js/document) outside)
      (.focus outside)
      (let [^js el (append! (make-el))]
        (.show el)
        (next-frame
         (fn []
           (is (= outside (.-activeElement js/document)))
           (done)))))))

(deftest focus-on-open-focuses-the-handle-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-focus-on-open "")
      (.show el)
      (next-frame
       (fn []
         (is (= (handle-of el) (.-activeElement (.-shadowRoot el))))
         (done))))))

(deftest focus-is-restored-on-close-test
  (async done
    (let [^js outside (.createElement js/document "button")]
      (.setAttribute outside "data-fp-fixture" "")
      (.appendChild (.-body js/document) outside)
      (.focus outside)
      (let [^js el (append! (make-el))]
        (.setAttribute el model/attr-focus-on-open "")
        (.show el)
        (next-frame
         (fn []
           (.hide el)
           (next-frame
            (fn []
              (is (= outside (.-activeElement js/document)))
              (done)))))))))

;; ── Viewport resize ──────────────────────────────────────────────────────────
(deftest resize-reclamps-an-offscreen-panel-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.setAttribute el model/attr-x "99999")
    (.setAttribute el model/attr-y "99999")
    (.dispatchEvent js/window (js/Event. "resize"))
    (is (< (.-x el) (.-innerWidth js/window)))
    (is (< (.-y el) (.-innerHeight js/window)))))

(deftest offscreen-start-position-is-pulled-into-reach-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-x "99999")
      (.setAttribute el model/attr-y "99999")
      (.show el)
      (next-frame
       (fn []
         (is (< (.-x el) (.-innerWidth js/window)))
         (is (< (.-y el) (.-innerHeight js/window)))
         (done))))))

(deftest onscreen-start-position-is-left-alone-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-x "60")
      (.setAttribute el model/attr-y "70")
      (.show el)
      (next-frame
       (fn []
         (is (= 60 (.-x el)))
         (is (= 70 (.-y el)))
         (done))))))

(deftest resize-leaves-an-unpositioned-panel-alone-test
  (testing "no x/y means the CSS default governs — nothing to re-clamp"
    (let [^js el (append! (make-el))]
      (.show el)
      (.dispatchEvent js/window (js/Event. "resize"))
      (is (not (.hasAttribute el model/attr-x)))
      (is (not (.hasAttribute el model/attr-y))))))

;; ── Boolean properties ───────────────────────────────────────────────────────
(deftest boolean-properties-reflect-test
  (let [^js el (append! (make-el))]
    (set! (.-closable el) true)
    (is (.hasAttribute el model/attr-closable))
    (set! (.-resizable el) true)
    (is (.hasAttribute el model/attr-resizable))
    (set! (.-focusOnOpen el) true)
    (is (.hasAttribute el model/attr-focus-on-open))
    (set! (.-closable el) false)
    (is (not (.hasAttribute el model/attr-closable)))))

(deftest label-property-reflects-test
  (let [^js el (append! (make-el))]
    (is (= model/default-label (.-label el)))
    (set! (.-label el) "Tools")
    (is (= "Tools" (.getAttribute el model/attr-label)))))

;; ── Teardown ─────────────────────────────────────────────────────────────────
(deftest disconnect-removes-window-listener-test
  (testing "a detached panel must not react to viewport resizes"
    (let [^js el (append! (make-el))]
      (.show el)
      (.setAttribute el model/attr-x "99999")
      (.remove el)
      (.dispatchEvent js/window (js/Event. "resize"))
      (is (= "99999" (.getAttribute el model/attr-x))))))

(deftest reconnect-restores-behaviour-test
  (let [^js el (append! (make-el))]
    (.show el)
    (.remove el)
    (append! el)
    (drag! el 100 100 220 200)
    (is (.hasAttribute el model/attr-x))))
