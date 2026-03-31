(ns baredom.components.x-table.x-table-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [goog.object :as gobj]
            [baredom.components.x-table.x-table     :as x]
            [baredom.components.x-table.model       :as model]
            [baredom.components.x-table-row.x-table-row :as x-row]
            [baredom.components.x-table-row.model   :as row-model]
            [baredom.components.x-table-cell.x-table-cell :as x-cell]
            [baredom.components.x-table-cell.model  :as cell-model]))

(x/init!)
(x-row/init!)
(x-cell/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-table
  ([] (.createElement js/document model/tag-name))
  ([attrs]
   (let [^js el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs]
       (if (true? v)
         (.setAttribute el (name k) "")
         (.setAttribute el (name k) (str v))))
     el)))

(defn ^js make-row [] (.createElement js/document row-model/tag-name))
(defn ^js make-cell [] (.createElement js/document cell-model/tag-name))

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
  (let [^js el (append! (make-table))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=caption]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-table))]
    (is (= "table" (.getAttribute el "role")))
    (is (nil?      (.getAttribute el "aria-multiselectable")))
    (is (nil?      (.getAttribute el "aria-label")))
    (is (nil?      (.getAttribute el "aria-rowcount")))
    ;; Caption hidden by default
    (let [^js cap (shadow-part el "[part=caption]")]
      (is (.hasAttribute cap "hidden")))))

;; ── columns attribute ─────────────────────────────────────────────────────────
(deftest columns-integer-test
  (let [^js el (append! (make-table {:columns "3"}))]
    ;; grid-template-columns should be set on host style
    (is (= "repeat(3, 1fr)" (.. el -style -gridTemplateColumns)))))

(deftest columns-css-value-test
  (let [^js el (append! (make-table {:columns "2fr 1fr"}))]
    (is (= "2fr 1fr" (.. el -style -gridTemplateColumns)))))

(deftest columns-absent-test
  (let [^js el (append! (make-table))]
    (is (= "" (.. el -style -gridTemplateColumns)))))

;; ── caption attribute ─────────────────────────────────────────────────────────
(deftest caption-shows-when-set-test
  (let [^js el (append! (make-table {:caption "My Table"}))]
    (let [^js cap (shadow-part el "[part=caption]")]
      (is (not (.hasAttribute cap "hidden")))
      (is (= "My Table" (.-textContent cap))))
    (is (= "My Table" (.getAttribute el "aria-label")))))

(deftest caption-hidden-when-absent-test
  (let [^js el (append! (make-table))]
    (let [^js cap (shadow-part el "[part=caption]")]
      (is (.hasAttribute cap "hidden")))
    (is (nil? (.getAttribute el "aria-label")))))

(deftest caption-update-test
  (let [^js el (append! (make-table))]
    (.setAttribute el model/attr-caption "Updated")
    (let [^js cap (shadow-part el "[part=caption]")]
      (is (not (.hasAttribute cap "hidden")))
      (is (= "Updated" (.-textContent cap))))))

;; ── selectable attribute ──────────────────────────────────────────────────────
(deftest selectable-none-role-table-test
  (let [^js el (append! (make-table {:selectable "none"}))]
    (is (= "table" (.getAttribute el "role")))
    (is (nil? (.getAttribute el "aria-multiselectable")))))

(deftest selectable-single-role-grid-test
  (let [^js el (append! (make-table {:selectable "single"}))]
    (is (= "grid" (.getAttribute el "role")))
    (is (nil? (.getAttribute el "aria-multiselectable")))))

(deftest selectable-multi-role-grid-multiselectable-test
  (let [^js el (append! (make-table {:selectable "multi"}))]
    (is (= "grid" (.getAttribute el "role")))
    (is (= "true" (.getAttribute el "aria-multiselectable")))))

;; ── bordered / full-width / compact data attributes ──────────────────────────
(deftest data-attributes-test
  (let [^js el (append! (make-table {:bordered true :full-width true :compact true}))]
    (is (.hasAttribute el "data-bordered"))
    (is (.hasAttribute el "data-full-width"))
    (is (.hasAttribute el "data-compact"))))

;; ── row-count attribute ───────────────────────────────────────────────────────
(deftest row-count-test
  (let [^js el (append! (make-table {:row-count "25"}))]
    (is (= "25" (.getAttribute el "aria-rowcount")))))

(deftest row-count-absent-test
  (let [^js el (append! (make-table))]
    (is (nil? (.getAttribute el "aria-rowcount")))))

;; ── JS property API ───────────────────────────────────────────────────────────
(deftest property-columns-test
  (let [^js el (append! (make-table))]
    (set! (.-columns el) "4")
    (is (= "4" (.getAttribute el model/attr-columns)))))

(deftest property-selectable-test
  (let [^js el (append! (make-table))]
    (set! (.-selectable el) "multi")
    (is (= "multi" (.getAttribute el model/attr-selectable)))))

(deftest property-fullWidth-test
  (let [^js el (append! (make-table))]
    (set! (.-fullWidth el) true)
    (is (.hasAttribute el model/attr-full-width))
    (set! (.-fullWidth el) false)
    (is (not (.hasAttribute el model/attr-full-width)))))

(deftest property-rowCount-test
  (let [^js el (append! (make-table))]
    (set! (.-rowCount el) 50)
    (is (= "50" (.getAttribute el model/attr-row-count)))
    (is (= 50 (.-rowCount el)))))

;; ── x-table-sort event ───────────────────────────────────────────────────────
(deftest sort-event-fires-test
  (async done
    (let [^js table (append! (make-table {:columns "3"}))
          ^js row   (make-row)
          ^js cell0 (make-cell)
          ^js cell1 (make-cell)
          ^js cell2 (make-cell)
          events    (atom [])]

      (.setAttribute cell1 cell-model/attr-type "header")
      (.setAttribute cell1 cell-model/attr-sortable "")
      (.setAttribute cell1 cell-model/attr-sort-direction "none")

      (.appendChild row cell0)
      (.appendChild row cell1)
      (.appendChild row cell2)
      (.appendChild table row)

      (.addEventListener table model/event-sort
                         (fn [^js e]
                           (swap! events conj {:col-index (gobj/get (.-detail e) "colIndex")
                                               :direction (gobj/get (.-detail e) "direction")
                                               :prev-dir  (gobj/get (.-detail e) "previousDirection")})
                           (done)))

      ;; Simulate a sort click on cell1 (column index 1)
      ;; The cell-sort event is fired by x-table-cell on its sort button.
      ;; We fire it manually here to test x-table's coordination.
      (.dispatchEvent cell1
                      (js/CustomEvent. cell-model/event-sort
                                       #js {:detail     #js {:direction "asc" :previousDirection "none"}
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true})))))

;; ── single selection ─────────────────────────────────────────────────────────
(deftest single-selection-test
  (let [^js table (append! (make-table {:columns "2" :selectable "single"}))
        ^js row1  (make-row)
        ^js row2  (make-row)
        ^js row3  (make-row)]

    (.setAttribute row1 row-model/attr-interactive "")
    (.setAttribute row2 row-model/attr-interactive "")
    (.setAttribute row3 row-model/attr-interactive "")
    (.setAttribute row1 row-model/attr-selected "")

    (doseq [^js r [row1 row2 row3]]
      (let [^js c1 (make-cell)
            ^js c2 (make-cell)]
        (.appendChild r c1)
        (.appendChild r c2)
        (.appendChild table r)))

    ;; Click row2 — simulated via x-table-row-click event
    (.dispatchEvent row2
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 2 :selected false :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))

    ;; row1 should be deselected, row2 selected
    (is (not (.hasAttribute row1 "selected")))
    (is (.hasAttribute row2 "selected"))
    (is (not (.hasAttribute row3 "selected")))))

;; ── multi selection ──────────────────────────────────────────────────────────
(deftest multi-selection-test
  (let [^js table (append! (make-table {:columns "2" :selectable "multi"}))
        ^js row1  (make-row)
        ^js row2  (make-row)]

    (.setAttribute row1 row-model/attr-interactive "")
    (.setAttribute row2 row-model/attr-interactive "")

    (doseq [^js r [row1 row2]]
      (let [^js c1 (make-cell)
            ^js c2 (make-cell)]
        (.appendChild r c1)
        (.appendChild r c2)
        (.appendChild table r)))

    ;; Click both rows
    (.dispatchEvent row1
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 1 :selected false :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))
    (.dispatchEvent row2
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 2 :selected false :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))

    ;; Both should be selected
    (is (.hasAttribute row1 "selected"))
    (is (.hasAttribute row2 "selected"))))

;; ── no-selection mode ignores row clicks ─────────────────────────────────────
(deftest no-selection-ignores-clicks-test
  (let [^js table (append! (make-table {:columns "2"}))
        ^js row1  (make-row)]

    (.setAttribute row1 row-model/attr-interactive "")

    (let [^js c1 (make-cell)
          ^js c2 (make-cell)]
      (.appendChild row1 c1)
      (.appendChild row1 c2)
      (.appendChild table row1))

    (.dispatchEvent row1
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 1 :selected false :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))

    ;; Row should NOT be selected — x-table in none mode doesn't manage selection
    (is (not (.hasAttribute row1 "selected")))))

;; ── x-table-row-select event ────────────────────────────────────────────────
(deftest row-select-event-fires-test
  (async done
    (let [^js table (append! (make-table {:columns "2" :selectable "single"}))
          ^js row1  (make-row)
          events    (atom [])]

      (.setAttribute row1 row-model/attr-interactive "")

      (let [^js c1 (make-cell)
            ^js c2 (make-cell)]
        (.appendChild row1 c1)
        (.appendChild row1 c2)
        (.appendChild table row1))

      (.addEventListener table model/event-row-select
                         (fn [^js e]
                           (swap! events conj
                                  {:row-index (gobj/get (.-detail e) "rowIndex")
                                   :selected  (gobj/get (.-detail e) "selected")
                                   :mode      (gobj/get (.-detail e) "selectionMode")
                                   :bubbles   (.-bubbles e)
                                   :cancelable (.-cancelable e)})
                           (done)))

      (.dispatchEvent row1
                      (js/CustomEvent. row-model/event-click
                                       #js {:detail     #js {:rowIndex 1 :selected false :disabled false}
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true})))))

;; ── single-select toggle: clicking already-selected row deselects ───────────
(deftest single-select-toggle-deselect-test
  (let [^js table (append! (make-table {:columns "2" :selectable "single"}))
        ^js row1  (make-row)
        events    (atom [])]

    (.setAttribute row1 row-model/attr-interactive "")
    (.setAttribute row1 row-model/attr-selected "")

    (let [^js c1 (make-cell)
          ^js c2 (make-cell)]
      (.appendChild row1 c1)
      (.appendChild row1 c2)
      (.appendChild table row1))

    (.addEventListener table model/event-row-select
                       (fn [^js e]
                         (swap! events conj
                                {:selected (gobj/get (.-detail e) "selected")})))

    ;; Click the already-selected row
    (.dispatchEvent row1
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 1 :selected true :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))

    ;; Event fires with selected: false, row is deselected
    (is (= 1 (count @events)))
    (is (false? (:selected (first @events))))
    (is (not (.hasAttribute row1 "selected")))))

;; ── striped attribute ────────────────────────────────────────────────────────
(deftest striped-sets-data-stripe-on-even-rows-test
  (let [^js table (append! (make-table {:columns "2" :striped true}))
        ^js row1  (make-row)
        ^js row2  (make-row)
        ^js row3  (make-row)]

    (doseq [^js r [row1 row2 row3]]
      (let [^js c1 (make-cell)
            ^js c2 (make-cell)]
        (.appendChild r c1)
        (.appendChild r c2)
        (.appendChild table r)))

    ;; Row 1 (odd) → no stripe, Row 2 (even) → stripe, Row 3 (odd) → no stripe
    (is (not (.hasAttribute row1 "data-stripe")))
    (is (= "even" (.getAttribute row2 "data-stripe")))
    (is (not (.hasAttribute row3 "data-stripe")))))

(deftest striped-not-set-when-striped-absent-test
  (let [^js table (append! (make-table {:columns "2"}))
        ^js row1  (make-row)
        ^js row2  (make-row)]

    (doseq [^js r [row1 row2]]
      (let [^js c1 (make-cell)
            ^js c2 (make-cell)]
        (.appendChild r c1)
        (.appendChild r c2)
        (.appendChild table r)))

    (is (not (.hasAttribute row1 "data-stripe")))
    (is (not (.hasAttribute row2 "data-stripe")))))

;; ── property getters ─────────────────────────────────────────────────────────
(deftest property-caption-test
  (let [^js el (append! (make-table))]
    (set! (.-caption el) "Test Caption")
    (is (= "Test Caption" (.getAttribute el model/attr-caption)))
    (is (= "Test Caption" (.-caption el)))))

(deftest property-striped-test
  (let [^js el (append! (make-table))]
    (is (false? (.-striped el)))
    (set! (.-striped el) true)
    (is (.hasAttribute el model/attr-striped))
    (is (true? (.-striped el)))
    (set! (.-striped el) false)
    (is (not (.hasAttribute el model/attr-striped)))))

(deftest property-bordered-test
  (let [^js el (append! (make-table))]
    (is (false? (.-bordered el)))
    (set! (.-bordered el) true)
    (is (.hasAttribute el model/attr-bordered))
    (is (true? (.-bordered el)))))

(deftest property-compact-test
  (let [^js el (append! (make-table))]
    (is (false? (.-compact el)))
    (set! (.-compact el) true)
    (is (.hasAttribute el model/attr-compact))
    (is (true? (.-compact el)))))

;; ── reconnect does not double listeners ──────────────────────────────────────
(deftest reconnect-no-double-selection-test
  (let [^js table (append! (make-table {:columns "2" :selectable "single"}))
        ^js row1  (make-row)
        events    (atom [])]

    (.setAttribute row1 row-model/attr-interactive "")

    (let [^js c1 (make-cell)
          ^js c2 (make-cell)]
      (.appendChild row1 c1)
      (.appendChild row1 c2)
      (.appendChild table row1))

    ;; Disconnect and reconnect
    (.remove table)
    (append! table)

    (.addEventListener table model/event-row-select
                       (fn [_] (swap! events conj :fired)))

    (.dispatchEvent row1
                    (js/CustomEvent. row-model/event-click
                                     #js {:detail     #js {:rowIndex 1 :selected false :disabled false}
                                          :bubbles    true
                                          :composed   true
                                          :cancelable true}))

    ;; Should fire exactly once, not twice
    (is (= 1 (count @events)))))
