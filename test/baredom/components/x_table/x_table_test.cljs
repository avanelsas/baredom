(ns baredom.components.x-table.x-table-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [goog.object :as gobj]
            [baredom.components.x-table.x-table     :as x]
            [baredom.components.x-table.model       :as model]
            [baredom.components.x-table-row.x-table-row :as x-row]
            [baredom.components.x-table-row.model   :as row-model]
            [baredom.components.x-table-cell.x-table-cell :as x-cell]
            [baredom.components.x-table-cell.model  :as cell-model]
            [baredom.test-writes :as writes]))

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

;; The table learns of its rows through slotchange, which runs after the change.
(defn- after-slotchange
  "Calls `f` after the slotchange of the changes made so far."
  [f]
  (js/setTimeout f 0))

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
    ;; Browser serialises 0 → 0px when reading back the computed style value.
    (is (= "repeat(3, minmax(0px, 1fr))" (.. el -style -gridTemplateColumns)))))

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
    (.setAttribute row1 row-model/attr-row-index "1")
    (.setAttribute row2 row-model/attr-row-index "2")
    (.setAttribute row3 row-model/attr-row-index "3")
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
    (.setAttribute row1 row-model/attr-row-index "1")
    (.setAttribute row2 row-model/attr-row-index "2")

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
      (.setAttribute row1 row-model/attr-row-index "1")

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
                                   :value     (gobj/get (.-detail e) "value")
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
    (.setAttribute row1 row-model/attr-row-index "1")
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

(defn- table-with-rows!
  "A table of `selectable` mode in the document, with `n` interactive rows. Returns the rows."
  [selectable n]
  (let [^js table (append! (make-table {:selectable selectable}))
        rows      (vec (repeatedly n make-row))]
    (doseq [[i ^js row] (map-indexed vector rows)]
      (.setAttribute row row-model/attr-interactive "")
      (.setAttribute row row-model/attr-row-index (str (inc i)))
      (.appendChild table row))
    (.addEventListener table model/event-row-select (fn [^js e] (.preventDefault e)))
    rows))

(defn- click-row!
  ([^js row] (click-row! row 1))
  ([^js row row-index]
   (.dispatchEvent row
                   (js/CustomEvent. row-model/event-click
                                    #js {:detail     #js {:rowIndex row-index :selected false :disabled false}
                                         :bubbles    true
                                         :composed   true
                                         :cancelable true}))))

(deftest a-cancelled-row-select-leaves-the-row-unselected-in-single-mode
  (let [[^js row] (table-with-rows! "single" 1)]
    (click-row! row)
    (is (not (.hasAttribute row row-model/attr-selected)))))

(deftest a-cancelled-row-select-leaves-the-selected-row-as-it-was-in-single-mode
  (let [[^js chosen ^js other] (table-with-rows! "single" 2)]
    (.setAttribute chosen row-model/attr-selected "")
    (click-row! other)
    (is (.hasAttribute chosen row-model/attr-selected))
    (is (not (.hasAttribute other row-model/attr-selected)))))

(deftest a-cancelled-row-select-leaves-the-row-unselected-in-multi-mode
  (let [[^js row] (table-with-rows! "multi" 1)]
    (click-row! row)
    (is (not (.hasAttribute row row-model/attr-selected)))))

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
    (async done
      (after-slotchange
       (fn []
         (is (not (.hasAttribute row1 "data-stripe")))
         (is (= "even" (.getAttribute row2 "data-stripe")))
         (is (not (.hasAttribute row3 "data-stripe")))
         (done))))))

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
    (.setAttribute row1 row-model/attr-row-index "1")

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

;; ── a moved row ──────────────────────────────────────────────────────────────
(deftest moved-row-restripes-test
  (let [^js table (append! (make-table {:columns "1" :striped true}))
        rows      (vec (repeatedly 3 make-row))
        [^js row1 ^js row2 ^js row3] rows]
    (doseq [^js r rows]
      (.appendChild table r))
    (.moveBefore table row3 row1)
    ;; Order is now row3, row1, row2: only the second row is even.
    (async done
      (after-slotchange
       (fn []
         (is (not (.hasAttribute row3 "data-stripe")))
         (is (= "even" (.getAttribute row1 "data-stripe")))
         (is (not (.hasAttribute row2 "data-stripe")))
         (done))))))

;; ── child watch ──────────────────────────────────────────────────────────────

(deftest removed-row-restripes-the-rows-below-test
  (async done
    (let [^js table (append! (make-table {:columns "1" :striped true}))
          rows      (vec (repeatedly 4 make-row))
          [_ ^js row2 ^js row3 ^js row4] rows]
      (doseq [^js r rows]
        (.appendChild table r))
      (.remove row2)
      (after-slotchange
       (fn []
         ;; Order is now row1, row3, row4: only the second row is even.
         (is (= "even" (.getAttribute row3 "data-stripe")))
         (is (not (.hasAttribute row4 "data-stripe")))
         (done))))))

(deftest removing-the-last-row-writes-no-stripes-test
  (async done
    (let [^js table (append! (make-table {:columns "1" :striped true}))
          rows      (vec (repeatedly 4 make-row))]
      (doseq [^js r rows]
        (.appendChild table r))
      (after-slotchange
       (fn []
         (let [n (writes/count-attr-writes! table ["data-stripe"])]
           (.remove ^js (peek rows))
           (after-slotchange
            (fn []
              (is (= 0 @n))
              (done)))))))))

(deftest a-caption-change-writes-no-stripes-test
  (async done
    (let [^js table (append! (make-table {:columns "1" :striped true}))]
      (dotimes [_ 4]
        (.appendChild table (make-row)))
      (after-slotchange
       (fn []
         (let [n (writes/count-attr-writes! table ["data-stripe"])]
           (.setAttribute table model/attr-caption "Changed")
           (after-slotchange
            (fn []
              (is (= 0 @n))
              (done)))))))))

;; ── Writes on other nodes ───────────────────────────────────────────────────
(deftest x-table-writes-only-what-it-declares-on-other-nodes-test
  (let [^js el (append! (make-table {:selectable "single"}))
        rows   (vec (repeatedly 3 make-row))]
    (doseq [[i ^js row] (map-indexed vector rows)]
      (.setAttribute row row-model/attr-interactive "")
      (.setAttribute row row-model/attr-row-index (str (inc i)))
      (.appendChild el row))
    (let [written (writes/foreign-writes! el (fn change-table []
                                               (.setAttribute el model/attr-striped "")
                                               (.setAttribute el model/attr-value "2")))]
      (is (= {:undeclared #{} :unexercised #{}} (writes/audit model/foreign-writes written))))))

;; ── value ────────────────────────────────────────────────────────────────────
(defn- ^js valued-row [v]
  (doto (make-row)
    (.setAttribute row-model/attr-interactive "")
    (.setAttribute row-model/attr-value v)))

(defn- table-of!
  "A table with `attrs` in the document, with a row for each of `row-values`. Returns the table
   and the rows."
  [attrs row-values]
  (let [^js table (append! (make-table attrs))
        rows      (mapv valued-row row-values)]
    (run! (fn [^js row] (.appendChild table row)) rows)
    [table rows]))

(defn- selected-flags [rows]
  (mapv (fn [^js row] (.hasAttribute row row-model/attr-selected)) rows))

(deftest value-selects-the-rows-it-names-test
  (async done
    (let [[_ rows] (table-of! {:selectable "multi" :value "b,c"} ["a" "b" "c"])]
      (after-slotchange
       (fn []
         (is (= [false true true] (selected-flags rows)))
         (done))))))

(deftest value-selects-rows-when-selectable-is-none-test
  (async done
    (let [[_ rows] (table-of! {:value "a"} ["a" "b"])]
      (after-slotchange
       (fn []
         (is (= [true false] (selected-flags rows)))
         (done))))))

(deftest value-is-a-list-in-single-mode-too-test
  (async done
    (let [[_ rows]  (table-of! {:selectable "single" :value "a,b"} ["a,b" "a" "b"])
          [_ json] (table-of! {:selectable "single" :value-format "json" :value "[\"a,b\"]"}
                               ["a,b" "a"])]
      (after-slotchange
       (fn []
         (is (= [false true true] (selected-flags rows)))
         (is (= [true false] (selected-flags json)))
         (done))))))

(deftest a-new-value-moves-the-selection-and-a-removed-value-clears-it-test
  (async done
    (let [[^js table rows] (table-of! {:selectable "single" :value "a"} ["a" "b"])]
      (after-slotchange
       (fn []
         (.setAttribute table model/attr-value "b")
         (is (= [false true] (selected-flags rows)))
         (.removeAttribute table model/attr-value)
         (is (= [false false] (selected-flags rows)))
         (done))))))

(deftest a-table-without-value-keeps-marked-rows-and-a-click-builds-value-from-them-test
  (async done
    (let [^js table (append! (make-table {:selectable "multi"}))
          rows      (mapv valued-row ["a" "b" "c"])]
      (doseq [^js row rows]
        (.appendChild table row))
      (.setAttribute (first rows) row-model/attr-selected "")
      (.setAttribute (peek rows) row-model/attr-selected "")
      (after-slotchange
       (fn []
         (is (not (.hasAttribute table model/attr-value)))
         (is (= [true false true] (selected-flags rows)))
         (click-row! (second rows))
         (is (= "a,b,c" (.getAttribute table model/attr-value)))
         (done))))))

(defn- row-select-log
  "Records the `value` and `selected` of each x-table-row-select that `table` sends, and refuses
   each when `refuse?`."
  [^js table refuse?]
  (let [seen (atom [])]
    (.addEventListener table model/event-row-select
                       (fn [^js e]
                         (when (identical? table (.-target e))
                           (swap! seen conj [(gobj/get (.-detail e) "value")
                                             (gobj/get (.-detail e) "selected")])
                           (when refuse? (.preventDefault e)))))
    seen))

(deftest a-click-asks-for-the-value-and-an-accepted-request-writes-it-test
  (async done
    (let [[^js table rows] (table-of! {:selectable "multi" :value "a"} ["a" "b"])
          seen             (row-select-log table false)]
      (after-slotchange
       (fn []
         (click-row! (second rows))
         (is (= [["a,b" true]] @seen))
         (is (= "a,b" (.getAttribute table model/attr-value)))
         (is (= [true true] (selected-flags rows)))
         (click-row! (first rows))
         (is (= ["b" false] (peek @seen)))
         (done))))))

(deftest a-refused-click-keeps-the-value-test
  (async done
    (let [[^js table rows] (table-of! {:selectable "single" :value "a"} ["a" "b"])
          seen             (row-select-log table true)]
      (after-slotchange
       (fn []
         (click-row! (second rows))
         (is (= [["b" true]] @seen))
         (is (= "a" (.getAttribute table model/attr-value)))
         (is (= [true false] (selected-flags rows)))
         (done))))))

(deftest a-row-without-value-or-row-index-sends-no-row-select-test
  (let [^js table (append! (make-table {:selectable "single"}))
        ^js row   (doto (make-row) (.setAttribute row-model/attr-interactive ""))
        ^js other (valued-row "b")
        seen      (row-select-log table false)]
    (.appendChild table row)
    (.appendChild table other)
    (click-row! row)
    (is (= [] @seen))
    (is (not (.hasAttribute row row-model/attr-selected)))
    (click-row! other)
    (is (= [["b" true]] @seen))))

(deftest a-click-on-a-row-that-csv-cannot-hold-asks-for-nothing-test
  (let [[^js table rows] (table-of! {:selectable "multi"} ["a,b" "a" "b"])
        seen             (row-select-log table false)
        warned           (atom 0)
        warn             (.-warn js/console)]
    (set! (.-warn js/console) (fn [& _] (swap! warned inc)))
    (try
      (click-row! (first rows))
      (finally
        (set! (.-warn js/console) warn)))
    (is (= [] @seen))
    (is (= 1 @warned))
    (is (not (.hasAttribute table model/attr-value)))))

(deftest json-format-holds-a-value-with-a-comma-test
  (async done
    (let [[^js table rows] (table-of! {:selectable "multi" :value-format "json" :value "[\"a,1\"]"}
                                      ["a,1" "b"])
          seen             (row-select-log table false)]
      (after-slotchange
       (fn []
         (is (= [true false] (selected-flags rows)))
         (click-row! (second rows))
         (is (= [["[\"a,1\",\"b\"]" true]] @seen))
         (done))))))

(defn- after-mutations
  "Calls `f` after the mutation observer callbacks of the changes made so far."
  [f]
  (js/setTimeout f 0))

(deftest a-new-row-value-shows-the-selection-again-test
  (async done
    (let [[_ rows] (table-of! {:selectable "single" :value "x"} ["y"])
          ^js row  (first rows)]
      (after-slotchange
       (fn []
         (is (= [false] (selected-flags rows)))
         (.setAttribute row row-model/attr-value "x")
         (after-mutations
          (fn []
            (is (= [true] (selected-flags rows)))
            (done))))))))

(deftest value-properties-reflect-their-attributes-test
  (let [^js table (append! (make-table))
        ^js row   (make-row)]
    (set! (.-value table) "a")
    (set! (.-valueFormat table) "json")
    (set! (.-value row) "r")
    (is (= "a" (.getAttribute table model/attr-value)))
    (is (= "json" (.getAttribute table model/attr-value-format)))
    (is (= "r" (.getAttribute row row-model/attr-value)))))

(deftest a-table-with-selectable-none-leaves-marked-rows-to-the-app-test
  (async done
    (let [^js table (append! (make-table))
          ^js row   (doto (valued-row "a") (.setAttribute row-model/attr-selected ""))
          ^js other (valued-row "b")]
      (.appendChild table row)
      (.appendChild table other)
      (after-slotchange
       (fn []
         (is (not (.hasAttribute table model/attr-value)))
         (.removeAttribute row row-model/attr-selected)
         (.setAttribute other row-model/attr-selected "")
         (.insertBefore table other row)
         (after-slotchange
          (fn []
            (is (= [false true] (selected-flags [row other])))
            (done))))))))

(deftest a-click-on-a-row-of-a-nested-table-leaves-the-outer-value-test
  (async done
    (let [[^js outer _] (table-of! {:selectable "multi" :value "a"} ["a"])
          ^js inner     (make-table {:selectable "multi"})
          ^js holder    (valued-row "h")
          ^js inner-row (valued-row "z")
          seen          (row-select-log outer false)]
      (.appendChild inner inner-row)
      (.appendChild holder inner)
      (.appendChild outer holder)
      (after-slotchange
       (fn []
         (click-row! inner-row)
         (is (= [] @seen))
         (is (= "a" (.getAttribute outer model/attr-value)))
         (is (= "z" (.getAttribute inner model/attr-value)))
         (done))))))

(deftest a-cancelled-row-click-asks-for-no-selection-test
  (let [[^js table rows] (table-of! {:selectable "single"} ["a"])
        ^js row          (first rows)
        seen             (row-select-log table false)]
    (.addEventListener row row-model/event-click (fn [^js e] (.preventDefault e)))
    (click-row! row)
    (is (= [] @seen))
    (is (not (.hasAttribute table model/attr-value)))))

(deftest a-reconnected-table-shows-a-row-value-changed-while-away-test
  (async done
    (let [[^js table rows] (table-of! {:selectable "single" :value "x"} ["y"])
          ^js row          (first rows)]
      (after-slotchange
       (fn []
         (.remove table)
         (.setAttribute row row-model/attr-value "x")
         (append! table)
         (is (= [true] (selected-flags rows)))
         (done))))))

(deftest the-row-select-detail-carries-the-row-index-of-the-click-test
  (let [[^js table rows] (table-of! {:selectable "multi"} ["a"])
        index            (atom nil)]
    (.addEventListener table model/event-row-select
                       (fn [^js e] (reset! index (gobj/get (.-detail e) "rowIndex"))))
    (click-row! (first rows) 7)
    (is (= 7 @index))))

;; ── sort as a request ────────────────────────────────────────────────────────
(defn- ^js sortable-header-in-table!
  "A table in the document with one sortable header cell. Returns the cell."
  []
  (let [^js table (append! (make-table {:columns "1"}))
        ^js row   (make-row)
        ^js cell  (make-cell)]
    (.setAttribute cell cell-model/attr-type "header")
    (.setAttribute cell cell-model/attr-sortable "")
    (.appendChild row cell)
    (.appendChild table row)
    cell))

(defn- click-sort! [^js cell]
  (.click (.querySelector (.-shadowRoot cell) "[part=sort-btn]")))

(deftest an-accepted-sort-writes-the-cell-direction-test
  (let [cell (sortable-header-in-table!)]
    (click-sort! cell)
    (is (= "asc" (.getAttribute cell cell-model/attr-sort-direction)))
    (click-sort! cell)
    (is (= "desc" (.getAttribute cell cell-model/attr-sort-direction)))))

(deftest a-cancelled-table-sort-refuses-the-cell-request-test
  (let [cell      (sortable-header-in-table!)
        ^js table (.closest cell model/tag-name)
        heard     (atom nil)
        hear!     (fn [^js e] (reset! heard e))]
    (.addEventListener table model/event-sort (fn [^js e] (.preventDefault e)))
    (.addEventListener js/document cell-model/event-sort hear!)
    (try
      (click-sort! cell)
      (finally
        (.removeEventListener js/document cell-model/event-sort hear!)))
    (is (some? @heard) "the cell request bubbles on to the page")
    (is (true? (some-> ^js @heard .-defaultPrevented)))
    (is (not (.hasAttribute cell cell-model/attr-sort-direction)))))

(deftest a-page-can-refuse-the-cell-request-of-a-table-test
  (let [cell    (sortable-header-in-table!)
        refuse! (fn [^js e] (.preventDefault e))]
    (.addEventListener js/document cell-model/event-sort refuse!)
    (try
      (click-sort! cell)
      (finally
        (.removeEventListener js/document cell-model/event-sort refuse!)))
    (is (not (.hasAttribute cell cell-model/attr-sort-direction)))))
