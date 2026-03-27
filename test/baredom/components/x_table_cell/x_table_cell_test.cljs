(ns baredom.components.x-table-cell.x-table-cell-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-table-cell.x-table-cell :as x]
            [baredom.components.x-table-cell.model         :as model]))

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
    (is (some? (shadow-part el "[part=cell]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "slot:not([name])")))
    (is (some? (shadow-part el "[part=sort-btn]")))
    (is (some? (shadow-part el (str "slot[name='" model/slot-sort-icon "']"))))
    (is (some? (shadow-part el "[part=sort-icon-default]")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "cell" (.getAttribute el "role")))
    (is (nil? (.getAttribute el "aria-sort")))
    (is (nil? (.getAttribute el "aria-disabled")))
    (is (true? (.-hidden (shadow-part el "[part=sort-btn]"))))
    (is (= "" (.. el -style -gridColumn)))
    (is (= "" (.. el -style -gridRow)))))

;; ── type attribute → role ────────────────────────────────────────────────────
(deftest type-data-role-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "data")
    (is (= "cell" (.getAttribute el "role")))))

(deftest type-header-default-scope-columnheader-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (is (= "columnheader" (.getAttribute el "role")))))

(deftest type-header-scope-col-columnheader-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-scope "col")
    (is (= "columnheader" (.getAttribute el "role")))))

(deftest type-header-scope-colgroup-columnheader-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-scope "colgroup")
    (is (= "columnheader" (.getAttribute el "role")))))

(deftest type-header-scope-row-rowheader-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-scope "row")
    (is (= "rowheader" (.getAttribute el "role")))))

(deftest type-header-scope-rowgroup-rowheader-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-scope "rowgroup")
    (is (= "rowheader" (.getAttribute el "role")))))

(deftest type-unknown-falls-back-to-data-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "td")
    (is (= "cell" (.getAttribute el "role")))))

;; ── data-type attribute on host ──────────────────────────────────────────────
(deftest data-type-attribute-reflects-type-test
  (let [^js el (append! (make-el))]
    (is (= "data" (.getAttribute el "data-type")))
    (.setAttribute el model/attr-type "header")
    (is (= "header" (.getAttribute el "data-type")))))

;; ── sortable attribute ───────────────────────────────────────────────────────
(deftest sort-btn-visible-for-header-sortable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (is (false? (.-hidden (shadow-part el "[part=sort-btn]"))))))

(deftest sort-btn-hidden-for-data-sortable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "data")
    (.setAttribute el model/attr-sortable "")
    (is (true? (.-hidden (shadow-part el "[part=sort-btn]"))))))

(deftest sort-btn-hidden-for-header-not-sortable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (is (true? (.-hidden (shadow-part el "[part=sort-btn]"))))))

;; ── aria-sort ────────────────────────────────────────────────────────────────
(deftest aria-sort-none-when-sortable-direction-none-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (is (= "none" (.getAttribute el "aria-sort")))))

(deftest aria-sort-ascending-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-sort-direction "asc")
    (is (= "ascending" (.getAttribute el "aria-sort")))))

(deftest aria-sort-descending-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-sort-direction "desc")
    (is (= "descending" (.getAttribute el "aria-sort")))))

(deftest aria-sort-absent-when-not-sortable-direction-none-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-sort")))))

(deftest aria-sort-set-when-direction-non-none-even-without-sortable-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-sort-direction "asc")
    (is (= "ascending" (.getAttribute el "aria-sort")))))

;; ── sort button aria-label ───────────────────────────────────────────────────
(deftest sort-btn-aria-label-at-direction-none-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (is (= "Sort ascending" (.getAttribute (shadow-part el "[part=sort-btn]") "aria-label")))))

(deftest sort-btn-aria-label-at-direction-asc-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-sort-direction "asc")
    (is (= "Sort descending" (.getAttribute (shadow-part el "[part=sort-btn]") "aria-label")))))

(deftest sort-btn-aria-label-at-direction-desc-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-sort-direction "desc")
    (is (= "Remove sort" (.getAttribute (shadow-part el "[part=sort-btn]") "aria-label")))))

;; ── disabled attribute ───────────────────────────────────────────────────────
(deftest disabled-sets-aria-disabled-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute el "aria-disabled")))))

(deftest disabled-removes-aria-disabled-when-removed-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (nil? (.getAttribute el "aria-disabled")))))

(deftest disabled-sort-btn-tabindex-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-disabled "")
    (is (= -1 (.-tabIndex (shadow-part el "[part=sort-btn]"))))))

(deftest disabled-sort-btn-disabled-property-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-disabled "")
    (is (true? (.-disabled (shadow-part el "[part=sort-btn]"))))))

;; ── col-span / row-span → grid style ────────────────────────────────────────
(deftest col-span-default-no-grid-column-test
  (let [^js el (append! (make-el))]
    (is (= "" (.. el -style -gridColumn)))))

(deftest col-span-sets-grid-column-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-col-span "3")
    (is (= "span 3" (.. el -style -gridColumn)))))

(deftest col-span-1-clears-grid-column-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-col-span "3")
    (.setAttribute el model/attr-col-span "1")
    (is (= "" (.. el -style -gridColumn)))))

(deftest row-span-sets-grid-row-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-row-span "2")
    (is (= "span 2" (.. el -style -gridRow)))))

;; ── truncate attribute ───────────────────────────────────────────────────────
(deftest truncate-sets-data-truncate-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-truncate "")
    (is (.hasAttribute el "data-truncate"))))

(deftest truncate-removal-removes-data-truncate-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-truncate "")
    (.removeAttribute el model/attr-truncate)
    (is (not (.hasAttribute el "data-truncate")))))

;; ── sticky attribute ─────────────────────────────────────────────────────────
(deftest sticky-start-sets-data-sticky-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-sticky "start")
    (is (= "start" (.getAttribute el "data-sticky")))))

(deftest sticky-end-sets-data-sticky-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-sticky "end")
    (is (= "end" (.getAttribute el "data-sticky")))))

(deftest sticky-none-removes-data-sticky-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-sticky "start")
    (.setAttribute el model/attr-sticky "none")
    (is (not (.hasAttribute el "data-sticky")))))

;; ── x-table-cell-sort event ─────────────────────────────────────────────────
(deftest sort-event-fires-on-sort-btn-click-test
  (let [^js el (append! (make-el))
        seen   (atom nil)]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.addEventListener el model/event-sort
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (.click (shadow-part el "[part=sort-btn]"))
    (is (some? @seen))
    (is (= "asc"  (:direction @seen)))
    (is (= "none" (:previousDirection @seen)))))

(deftest sort-event-cycles-correctly-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")

    (let [d1 (atom nil)]
      (.addEventListener el model/event-sort
                         (fn [^js e] (reset! d1 (js->clj (.-detail e) :keywordize-keys true))))
      (.click (shadow-part el "[part=sort-btn]"))
      (is (= "asc" (:direction @d1)))
      (.removeEventListener el model/event-sort (fn [_])))

    (.setAttribute el model/attr-sort-direction "asc")
    (let [d2 (atom nil)]
      (.addEventListener el model/event-sort
                         (fn [^js e] (reset! d2 (js->clj (.-detail e) :keywordize-keys true))))
      (.click (shadow-part el "[part=sort-btn]"))
      (is (= "desc" (:direction @d2)))
      (is (= "asc"  (:previousDirection @d2))))))

(deftest sort-event-cancelable-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.addEventListener el model/event-sort
                       (fn [^js e]
                         (reset! fired true)
                         (.preventDefault e)))
    (.click (shadow-part el "[part=sort-btn]"))
    (is (true? @fired))))

(deftest sort-event-not-fired-when-disabled-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-sort (fn [_] (reset! fired true)))
    (.click (shadow-part el "[part=sort-btn]"))
    (is (false? @fired))))

(deftest sort-event-not-fired-for-data-type-test
  (let [^js el (append! (make-el))
        fired  (atom false)]
    (.setAttribute el model/attr-type "data")
    (.setAttribute el model/attr-sortable "")
    (.addEventListener el model/event-sort (fn [_] (reset! fired true)))
    (.click (shadow-part el "[part=sort-btn]"))
    (is (false? @fired))))

;; ── x-table-cell-connected event ─────────────────────────────────────────────
(deftest connected-event-fires-on-connect-test
  (let [^js el (make-el)
        seen   (atom nil)]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-col-span "2")
    (.addEventListener js/document model/event-connected
                       (fn [^js e] (reset! seen (js->clj (.-detail e) :keywordize-keys true))))
    (append! el)
    (.removeEventListener js/document model/event-connected (fn [_]))
    (is (some? @seen))
    (is (= "header" (:type @seen)))
    (is (= 2        (:colSpan @seen)))))

;; ── x-table-cell-disconnected event ──────────────────────────────────────────
(deftest disconnected-event-fires-on-disconnect-test
  (let [^js el    (append! (make-el))
        fired     (atom false)]
    (.addEventListener el model/event-disconnected
                       (fn [_] (reset! fired true)))
    (.remove el)
    (is (true? @fired))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest type-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "data" (.-type el)))))

(deftest type-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-type el) "header")
    (is (= "header" (.getAttribute el model/attr-type)))))

(deftest col-span-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 1 (.-colSpan el)))))

(deftest col-span-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-colSpan el) 3)
    (is (= "3" (.getAttribute el model/attr-col-span)))
    (is (= 3 (.-colSpan el)))))

(deftest row-span-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-rowSpan el) 2)
    (is (= "2" (.getAttribute el model/attr-row-span)))))

(deftest sortable-property-boolean-test
  (let [^js el (append! (make-el))]
    (is (false? (.-sortable el)))
    (set! (.-sortable el) true)
    (is (true? (.-sortable el)))
    (set! (.-sortable el) false)
    (is (false? (.-sortable el)))))

(deftest sort-direction-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "none" (.-sortDirection el)))))

(deftest sort-direction-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-sortDirection el) "asc")
    (is (= "asc" (.getAttribute el model/attr-sort-direction)))))

(deftest disabled-property-boolean-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))))

(deftest truncate-property-boolean-test
  (let [^js el (append! (make-el))]
    (is (false? (.-truncate el)))
    (set! (.-truncate el) true)
    (is (true? (.-truncate el)))))

;; ── Reconnect guard (no listener doubling) ───────────────────────────────────
(deftest reconnect-sort-event-fires-once-test
  (let [^js el   (append! (make-el))
        counter  (atom 0)]
    (.setAttribute el model/attr-type "header")
    (.setAttribute el model/attr-sortable "")
    (.addEventListener el model/event-sort (fn [_] (swap! counter inc)))

    ;; disconnect + reconnect
    (.remove el)
    (append! el)

    (.click (shadow-part el "[part=sort-btn]"))
    (is (= 1 @counter))))
