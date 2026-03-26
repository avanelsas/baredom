(ns app.components.x-table-row.x-table-row-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-table-row.x-table-row :as x]
            [app.components.x-table-row.model       :as model]))

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

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-has-root-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest shadow-structure-has-style-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "style")))))

(deftest shadow-structure-has-default-slot-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-child el "slot:not([name])")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-role-row-test
  (let [^js el (append! (make-el))]
    (is (= "row" (.getAttribute el "role")))))

(deftest default-no-aria-selected-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-selected")))))

(deftest default-no-aria-disabled-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-disabled")))))

(deftest default-no-aria-rowindex-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-rowindex")))))

(deftest default-tabindex-minus-one-test
  (let [^js el (append! (make-el))]
    (is (= -1 (.-tabIndex el)))))

(deftest default-no-data-selected-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el "data-selected")))))

(deftest default-no-data-interactive-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el "data-interactive")))))

;; ── selected attribute ───────────────────────────────────────────────────────
(deftest selected-sets-data-selected-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-selected "")
    (is (.hasAttribute el "data-selected"))))

(deftest selected-sets-aria-selected-true-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-selected "")
    (is (= "true" (.getAttribute el "aria-selected")))))

(deftest selected-removal-removes-data-selected-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-selected "")
    (.removeAttribute el model/attr-selected)
    (is (not (.hasAttribute el "data-selected")))))

(deftest selected-removal-without-interactive-removes-aria-selected-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-selected "")
    (.removeAttribute el model/attr-selected)
    (is (nil? (.getAttribute el "aria-selected")))))

;; ── interactive attribute ────────────────────────────────────────────────────
(deftest interactive-sets-data-interactive-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (is (.hasAttribute el "data-interactive"))))

(deftest interactive-sets-tabindex-zero-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (is (= 0 (.-tabIndex el)))))

(deftest interactive-not-selected-sets-aria-selected-false-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (is (= "false" (.getAttribute el "aria-selected")))))

(deftest interactive-and-selected-sets-aria-selected-true-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-selected "")
    (is (= "true" (.getAttribute el "aria-selected")))))

(deftest interactive-removal-removes-data-interactive-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (.removeAttribute el model/attr-interactive)
    (is (not (.hasAttribute el "data-interactive")))))

(deftest interactive-removal-restores-tabindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (.removeAttribute el model/attr-interactive)
    (is (= -1 (.-tabIndex el)))))

;; ── disabled attribute ───────────────────────────────────────────────────────
(deftest disabled-sets-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute el "aria-disabled")))))

(deftest disabled-removal-removes-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (nil? (.getAttribute el "aria-disabled")))))

(deftest disabled-with-interactive-keeps-tabindex-minus-one-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-disabled "")
    (is (= -1 (.-tabIndex el)))))

;; ── row-index attribute ──────────────────────────────────────────────────────
(deftest row-index-valid-sets-aria-rowindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-row-index "3")
    (is (= "3" (.getAttribute el "aria-rowindex")))))

(deftest row-index-zero-no-aria-rowindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-row-index "0")
    (is (nil? (.getAttribute el "aria-rowindex")))))

(deftest row-index-absent-no-aria-rowindex-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-rowindex")))))

(deftest row-index-removal-removes-aria-rowindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-row-index "2")
    (.removeAttribute el model/attr-row-index)
    (is (nil? (.getAttribute el "aria-rowindex")))))

;; ── x-table-row-click event ──────────────────────────────────────────────────
(deftest click-not-fired-when-not-interactive-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (.click el)
    (is (false? @fired))))

(deftest click-fires-when-interactive-test
  (let [^js el (append! (make-el))
        seen   (atom nil)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el model/event-click
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (.click el)
    (is (some? @seen))))

(deftest click-not-fired-when-disabled-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (.click el)
    (is (false? @fired))))

(deftest click-detail-shape-test
  (let [^js el (append! (make-el))
        seen   (atom nil)]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-row-index "4")
    (.addEventListener el model/event-click
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (.click el)
    (is (= 4     (:rowIndex @seen)))
    (is (= false (:selected @seen)))
    (is (= false (:disabled @seen)))))

(deftest click-detail-selected-reflects-state-test
  (let [^js el (append! (make-el))
        seen   (atom nil)]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-selected "")
    (.addEventListener el model/event-click
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (.click el)
    (is (= true (:selected @seen)))))

(deftest click-is-cancelable-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el model/event-click
                       (fn [^js e]
                         (reset! fired true)
                         (.preventDefault e)))
    (.click el)
    (is (true? @fired))))

(deftest click-fires-on-enter-keydown-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el "Enter")
    (is (true? @fired))))

(deftest click-fires-on-space-keydown-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el " ")
    (is (true? @fired))))

(deftest enter-not-fired-when-not-interactive-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.addEventListener el model/event-click (fn [_] (reset! fired true)))
    (fire-key! el "Enter")
    (is (false? @fired))))

;; ── x-table-row-connected event ──────────────────────────────────────────────
(deftest connected-fires-on-connect-test
  (let [^js el  (make-el)
        seen    (atom nil)]
    (.setAttribute el model/attr-selected "")
    (.setAttribute el model/attr-row-index "2")
    (.addEventListener js/document model/event-connected
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (append! el)
    (.removeEventListener js/document model/event-connected (fn [_]))
    (is (some? @seen))
    (is (= 2    (:rowIndex @seen)))
    (is (= true (:selected @seen)))))

(deftest connected-detail-interactive-field-test
  (let [^js el (make-el)
        seen   (atom nil)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener js/document model/event-connected
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (append! el)
    (.removeEventListener js/document model/event-connected (fn [_]))
    (is (= true (:interactive @seen)))))

;; ── x-table-row-disconnected event ───────────────────────────────────────────
(deftest disconnected-fires-on-remove-test
  (let [^js el  (append! (make-el))
        fired   (atom false)]
    (.addEventListener js/document model/event-disconnected
                       (fn [_] (reset! fired true)))
    (.remove el)
    (.removeEventListener js/document model/event-disconnected (fn [_]))
    (is (true? @fired))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest selected-property-default-test
  (let [^js el (append! (make-el))]
    (is (false? (.-selected el)))))

(deftest selected-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-selected el) true)
    (is (.hasAttribute el model/attr-selected))
    (is (true? (.-selected el)))))

(deftest selected-property-unset-test
  (let [^js el (append! (make-el))]
    (set! (.-selected el) true)
    (set! (.-selected el) false)
    (is (not (.hasAttribute el model/attr-selected)))
    (is (false? (.-selected el)))))

(deftest disabled-property-boolean-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))
    (set! (.-disabled el) false)
    (is (false? (.-disabled el)))))

(deftest interactive-property-boolean-test
  (let [^js el (append! (make-el))]
    (is (false? (.-interactive el)))
    (set! (.-interactive el) true)
    (is (true? (.-interactive el)))
    (set! (.-interactive el) false)
    (is (false? (.-interactive el)))))

(deftest row-index-property-default-nil-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-rowIndex el)))))

(deftest row-index-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-rowIndex el) 3)
    (is (= "3" (.getAttribute el model/attr-row-index)))
    (is (= 3 (.-rowIndex el)))))

(deftest row-index-property-invalid-removes-attribute-test
  (let [^js el (append! (make-el))]
    (set! (.-rowIndex el) 3)
    (set! (.-rowIndex el) 0)
    (is (not (.hasAttribute el model/attr-row-index)))))

;; ── Reconnect guard (no listener doubling) ───────────────────────────────────
(deftest reconnect-click-fires-once-test
  (let [^js el   (append! (make-el))
        counter  (atom 0)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el model/event-click (fn [_] (swap! counter inc)))

    ;; disconnect + reconnect
    (.remove el)
    (append! el)

    (.click el)
    (is (= 1 @counter))))
