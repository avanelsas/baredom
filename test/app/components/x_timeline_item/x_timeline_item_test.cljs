(ns app.components.x-timeline-item.x-timeline-item-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-timeline-item.x-timeline-item :as x]
            [app.components.x-timeline-item.model           :as model]))

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

(defn ^js shadow-child [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn fire-key! [^js el key]
  (.dispatchEvent el
                  (js/KeyboardEvent. "keydown"
                                     #js {:key key :bubbles true :cancelable true})))

;; ── Registration ──────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────────
(deftest shadow-has-root-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest shadow-has-style-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "style")))))

(deftest shadow-has-timeline-item-div-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el ".timeline-item")))))

(deftest shadow-has-marker-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el ".marker")))))

(deftest shadow-has-connector-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el ".connector")))))

(deftest shadow-has-title-el-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el ".title")))))

(deftest shadow-has-default-slot-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "slot:not([name])")))))

(deftest shadow-has-icon-slot-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "slot[name='icon']")))))

(deftest shadow-has-label-slot-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "slot[name='label']")))))

(deftest shadow-has-actions-slot-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "slot[name='actions']")))))

;; ── Default state ─────────────────────────────────────────────────────────────
(deftest default-role-listitem-test
  (let [^js el (append! (make-el))]
    (is (= "listitem" (.getAttribute el "role")))))

(deftest default-data-status-pending-test
  (let [^js el (append! (make-el))]
    (is (= "pending" (.getAttribute el "data-status")))))

(deftest default-data-position-start-test
  (let [^js el (append! (make-el))]
    (is (= "start" (.getAttribute el "data-position")))))

(deftest default-tabindex-zero-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-tabIndex el)))))

(deftest default-no-aria-disabled-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-disabled")))))

(deftest default-title-hidden-test
  (let [^js el (append! (make-el))]
    (is (.hasAttribute (shadow-child el ".title") "hidden"))))

(deftest default-marker-icon-is-pending-glyph-test
  (let [^js el (append! (make-el))]
    (is (= "○" (.-textContent (shadow-child el ".default-icon"))))))

;; ── status attribute ──────────────────────────────────────────────────────────
(deftest status-active-sets-data-status-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "active")
    (is (= "active" (.getAttribute el "data-status")))))

(deftest status-complete-sets-data-status-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "complete")
    (is (= "complete" (.getAttribute el "data-status")))))

(deftest status-error-sets-data-status-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "error")
    (is (= "error" (.getAttribute el "data-status")))))

(deftest status-warning-sets-data-status-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "warning")
    (is (= "warning" (.getAttribute el "data-status")))))

(deftest status-complete-updates-marker-icon-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "complete")
    (is (= "✓" (.-textContent (shadow-child el ".default-icon"))))))

(deftest status-error-updates-marker-icon-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "error")
    (is (= "✕" (.-textContent (shadow-child el ".default-icon"))))))

;; ── title attribute ───────────────────────────────────────────────────────────
(deftest title-renders-in-shadow-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-title "My Event")
    (let [^js title-el (shadow-child el ".title")]
      (is (= "My Event" (.-textContent title-el)))
      (is (not (.hasAttribute title-el "hidden"))))))

(deftest title-removal-hides-title-el-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-title "Event")
    (.removeAttribute el model/attr-title)
    (is (.hasAttribute (shadow-child el ".title") "hidden"))))

(deftest title-sets-host-aria-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-title "Launch")
    (is (= "Launch" (.getAttribute el "aria-label")))))

;; ── label attribute ───────────────────────────────────────────────────────────
(deftest label-renders-in-fallback-span-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Jan 2024")
    (is (= "Jan 2024" (.-textContent (shadow-child el ".label-text"))))))

(deftest label-sets-host-aria-label-when-no-title-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Jan 2024")
    (is (= "Jan 2024" (.getAttribute el "aria-label")))))

;; ── position attribute ────────────────────────────────────────────────────────
(deftest position-end-sets-data-position-end-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-position "end")
    (is (= "end" (.getAttribute el "data-position")))))

;; ── data-position (parent override) ──────────────────────────────────────────
(deftest data-position-overrides-position-attr-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-position "start")
    (.setAttribute el model/data-attr-position "end")
    (is (= "end" (.getAttribute el "data-position")))))

;; ── connector attribute ───────────────────────────────────────────────────────
(deftest connector-dashed-sets-data-connector-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-connector "dashed")
    (is (= "dashed" (.getAttribute el "data-connector")))))

(deftest connector-none-sets-data-connector-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-connector "none")
    (is (= "none" (.getAttribute el "data-connector")))))

(deftest connector-solid-removes-data-connector-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-connector "dashed")
    (.setAttribute el model/attr-connector "solid")
    (is (nil? (.getAttribute el "data-connector")))))

;; ── data-last (parent sets) ───────────────────────────────────────────────────
;; Note: data-last is observed so setting it triggers attributeChangedCallback.
;; The CSS :host([data-last]) .connector { display:none } hides the connector.
;; We verify the attribute is preserved on the host (parent set it; item keeps it).
(deftest data-last-attribute-observed-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/data-attr-last "")
    (is (.hasAttribute el model/data-attr-last))))

;; ── disabled attribute ────────────────────────────────────────────────────────
(deftest disabled-sets-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute el "aria-disabled")))))

(deftest disabled-sets-tabindex-minus-one-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= -1 (.-tabIndex el)))))

(deftest disabled-removal-clears-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (nil? (.getAttribute el "aria-disabled")))))

(deftest disabled-removal-restores-tabindex-zero-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (= 0 (.-tabIndex el)))))

;; ── x-timeline-item-click event ──────────────────────────────────────────────
(deftest click-fires-when-not-disabled-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (.click el)
    (is (true? @fired))))

(deftest click-not-fired-when-disabled-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (.click el)
    (is (false? @fired))))

(deftest click-detail-shape-test
  (let [^js el (append! (make-el))
        seen   (atom nil)]
    (.setAttribute el model/attr-status "active")
    (.setAttribute el model/attr-label "Jan")
    (.addEventListener el model/event-click
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (.click el)
    (is (= "active" (:status @seen)))
    (is (= "Jan" (:label @seen)))))

(deftest click-is-cancelable-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click
                       (fn [^js e]
                         (reset! fired true)
                         (.preventDefault e)))
    (.click el)
    (is (true? @fired))))

(deftest click-fires-on-enter-key-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el "Enter")
    (is (true? @fired))))

(deftest click-fires-on-space-key-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el " ")
    (is (true? @fired))))

(deftest enter-key-not-fired-when-disabled-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el "Enter")
    (is (false? @fired))))

;; ── x-timeline-item-connected event ──────────────────────────────────────────
(deftest connected-fires-on-mount-test
  (let [^js el (make-el)
        seen   (atom nil)]
    (.setAttribute el model/attr-status "complete")
    (.setAttribute el model/attr-label "Jan 2024")
    (.addEventListener el model/event-connected
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (append! el)
    (is (some? @seen))
    (is (= "complete" (:status @seen)))
    (is (= "Jan 2024" (:label @seen)))
    (is (= "start" (:position @seen)))
    (is (= false (:disabled @seen)))))

(deftest connected-detail-position-end-test
  (let [^js el (make-el)
        seen   (atom nil)]
    (.setAttribute el model/attr-position "end")
    (.addEventListener el model/event-connected
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (append! el)
    (is (= "end" (:position @seen)))))

;; ── x-timeline-item-disconnected event ───────────────────────────────────────
(deftest disconnected-fires-on-remove-test
  (let [^js el  (append! (make-el))
        fired   (atom false)]
    (.addEventListener js/document model/event-disconnected
                       (fn [_] (reset! fired true)))
    (.remove el)
    (.removeEventListener js/document model/event-disconnected (fn [_]))
    (is (true? @fired))))

;; ── Property accessors ────────────────────────────────────────────────────────
(deftest label-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-label el)))))

(deftest label-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "Q1")
    (is (= "Q1" (.getAttribute el model/attr-label)))
    (is (= "Q1" (.-label el)))))

(deftest status-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "pending" (.-status el)))))

(deftest status-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-status el) "active")
    (is (= "active" (.getAttribute el model/attr-status)))
    (is (= "active" (.-status el)))))

(deftest disabled-property-default-false-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))))

(deftest disabled-property-set-true-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))
    (is (.hasAttribute el model/attr-disabled))))

(deftest disabled-property-set-false-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (set! (.-disabled el) false)
    (is (false? (.-disabled el)))
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest connector-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "solid" (.-connector el)))))

(deftest position-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "start" (.-position el)))))

;; ── Reconnect guard ───────────────────────────────────────────────────────────
(deftest reconnect-click-fires-once-test
  (let [^js el  (append! (make-el))
        counter (atom 0)]
    (.addEventListener el model/event-click (fn [_] (swap! counter inc)))
    (.remove el)
    (append! el)
    (.click el)
    (is (= 1 @counter))))
