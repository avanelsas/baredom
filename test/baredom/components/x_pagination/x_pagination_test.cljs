(ns baredom.components.x-pagination.x-pagination-test
  (:require [cljs.test :refer-macros [deftest is async use-fixtures]]
            [baredom.components.x-pagination.x-pagination :as comp]
            [baredom.components.x-pagination.model :as model]))

(use-fixtures :each
  {:before (fn [] (comp/init!))
   :after  (fn []
             (doseq [^js el (array-seq (.querySelectorAll js/document model/tag-name))]
               (.removeChild (.-body js/document) el)))})

(defn- make-el
  ([] (make-el {}))
  ([attrs]
   (let [^js el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs]
       (.setAttribute el k v))
     (.appendChild (.-body js/document) el)
     el)))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-dom-structure-test
  (let [^js el  (make-el)
        root    (.-shadowRoot el)
        nav     (.querySelector root "[part~='nav']")
        ol      (.querySelector root "[part~='list']")
        prev    (.querySelector root "[part~='button-prev']")
        nxt     (.querySelector root "[part~='button-next']")]
    (is (some? nav))
    (is (some? ol))
    (is (some? prev))
    (is (some? nxt))))

;; ── Default rendering ─────────────────────────────────────────────────────
(deftest default-aria-label-test
  (let [^js el (make-el)
        root   (.-shadowRoot el)
        nav    (.querySelector root "[part~='nav']")]
    (is (= model/default-label (.getAttribute nav "aria-label")))))

(deftest custom-label-test
  (let [^js el (make-el {"label" "Page navigation"})
        root   (.-shadowRoot el)
        nav    (.querySelector root "[part~='nav']")]
    (is (= "Page navigation" (.getAttribute nav "aria-label")))))

;; ── Current page ──────────────────────────────────────────────────────────
(deftest current-page-has-aria-current-test
  (let [^js el  (make-el {"page" "3" "total-pages" "10"})
        root    (.-shadowRoot el)
        current (.querySelector root "[aria-current='page']")]
    (is (some? current))
    (is (= "3" (.getAttribute current "data-page")))))

;; ── Prev/Next disabled state ──────────────────────────────────────────────
(deftest prev-disabled-on-first-page-test
  (let [^js el   (make-el {"page" "1" "total-pages" "5"})
        root     (.-shadowRoot el)
        prev-btn (.querySelector root "[part~='button-prev']")]
    (is (.hasAttribute prev-btn "disabled"))))

(deftest prev-enabled-when-not-first-test
  (let [^js el   (make-el {"page" "3" "total-pages" "5"})
        root     (.-shadowRoot el)
        prev-btn (.querySelector root "[part~='button-prev']")]
    (is (not (.hasAttribute prev-btn "disabled")))))

(deftest next-disabled-on-last-page-test
  (let [^js el   (make-el {"page" "5" "total-pages" "5"})
        root     (.-shadowRoot el)
        next-btn (.querySelector root "[part~='button-next']")]
    (is (.hasAttribute next-btn "disabled"))))

(deftest next-enabled-when-not-last-test
  (let [^js el   (make-el {"page" "3" "total-pages" "5"})
        root     (.-shadowRoot el)
        next-btn (.querySelector root "[part~='button-next']")]
    (is (not (.hasAttribute next-btn "disabled")))))

;; ── Page button count ─────────────────────────────────────────────────────
(deftest page-buttons-all-visible-test
  ;; 5 pages, siblings 1, boundary 1: all 5 fit without ellipsis
  (let [^js el (make-el {"page" "3" "total-pages" "5"})
        root   (.-shadowRoot el)
        btns   (array-seq (.querySelectorAll root "[part~='button-page']"))]
    (is (= 5 (count btns)))))

;; ── Ellipsis ──────────────────────────────────────────────────────────────
(deftest ellipsis-appears-for-large-range-test
  (let [^js el   (make-el {"page" "1" "total-pages" "20"})
        root     (.-shadowRoot el)
        ellipsis (.querySelector root "[part~='ellipsis']")]
    (is (some? ellipsis))))

(deftest no-ellipsis-for-small-range-test
  (let [^js el   (make-el {"page" "3" "total-pages" "5"})
        root     (.-shadowRoot el)
        ellipsis (.querySelector root "[part~='ellipsis']")]
    (is (nil? ellipsis))))

;; ── Disabled attribute ────────────────────────────────────────────────────
(deftest disabled-disables-prev-next-test
  (let [^js el   (make-el {"page" "3" "total-pages" "5" "disabled" ""})
        root     (.-shadowRoot el)
        prev-btn (.querySelector root "[part~='button-prev']")
        next-btn (.querySelector root "[part~='button-next']")]
    (is (.hasAttribute prev-btn "disabled"))
    (is (.hasAttribute next-btn "disabled"))))

;; ── Events ────────────────────────────────────────────────────────────────
(deftest page-change-fires-on-page-click-test
  (async done
    (let [^js el   (make-el {"page" "3" "total-pages" "10"})
          root     (.-shadowRoot el)
          received (atom nil)]
      (.addEventListener el model/event-page-change
                         (fn [^js ev]
                           (reset! received (.-page (.-detail ev)))
                           (done)))
      ;; Click page 1 button
      (let [btns   (array-seq (.querySelectorAll root "[part~='button-page']"))
            page-1 (first btns)]
        (.click page-1)))))

(deftest page-change-fires-on-next-click-test
  (async done
    (let [^js el   (make-el {"page" "3" "total-pages" "10"})
          root     (.-shadowRoot el)
          received (atom nil)]
      (.addEventListener el model/event-page-change
                         (fn [^js ev]
                           (reset! received (.-page (.-detail ev)))
                           (when (some? @received) (done))))
      (let [next-btn (.querySelector root "[part~='button-next']")]
        (.click next-btn)))))

(deftest page-change-fires-on-prev-click-test
  (async done
    (let [^js el   (make-el {"page" "3" "total-pages" "10"})
          root     (.-shadowRoot el)
          received (atom nil)]
      (.addEventListener el model/event-page-change
                         (fn [^js ev]
                           (reset! received (.-page (.-detail ev)))
                           (when (some? @received) (done))))
      (let [prev-btn (.querySelector root "[part~='button-prev']")]
        (.click prev-btn)))))

;; ── Attribute change triggers re-render ───────────────────────────────────
(deftest attribute-change-rerenders-test
  (let [^js el (make-el {"page" "1" "total-pages" "5"})
        root   (.-shadowRoot el)]
    (.setAttribute el "page" "3")
    (let [current (.querySelector root "[aria-current='page']")]
      (is (= "3" (.getAttribute current "data-page"))))))

;; ── Reflected properties ──────────────────────────────────────────────────
(deftest page-property-getter-test
  (let [^js el (make-el {"page" "4" "total-pages" "10"})]
    (is (= 4 (.-page el)))))

(deftest page-property-setter-test
  (let [^js el (make-el {"page" "1" "total-pages" "10"})]
    (set! (.-page el) 7)
    (is (= "7" (.getAttribute el model/attr-page)))))

(deftest the-count-properties-reflect-their-attributes
  (let [^js el (make-el {"page" "1" "total-pages" "5"})]
    (set! (.-totalPages el) 20)
    (set! (.-siblingCount el) 2)
    (set! (.-boundaryCount el) 3)
    (is (= ["20" "2" "3"]
           (mapv #(.getAttribute el %) ["total-pages" "sibling-count" "boundary-count"])))
    (is (= [20 2 3] [(.-totalPages el) (.-siblingCount el) (.-boundaryCount el)]))))

(deftest disabled-property-getter-test
  (let [^js el (make-el {})]
    (is (false? (.-disabled el)))))

(deftest disabled-property-setter-test
  (let [^js el (make-el {})]
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))
    (is (.hasAttribute el model/attr-disabled))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-sets-data-size-test
  (let [^js el (make-el {"size" "lg"})]
    (is (= "lg" (.getAttribute el "data-size")))))

;; ── Cancelable change-request ────────────────────────────────────────────
(deftest next-dispatches-change-request-test
  (async done
    (let [^js el   (make-el {"page" "1" "total-pages" "5"})
          root     (.-shadowRoot el)
          seen     (atom nil)]
      (.addEventListener el model/event-change-request
        (fn [^js ev]
          (reset! seen {:page          (.-page (.-detail ev))
                        :previous-page (.-previousPage (.-detail ev))})))
      (let [next-btn (.querySelector root "[part~='button-next']")]
        (.click next-btn))
      (js/setTimeout
       (fn []
         (is (some? @seen) "change-request event should fire")
         (is (= 2 (:page @seen)))
         (is (= 1 (:previous-page @seen)))
         (done))
       0))))

(deftest change-request-can-be-cancelled-test
  (async done
    (let [^js el   (make-el {"page" "1" "total-pages" "5"})
          root     (.-shadowRoot el)]
      (.addEventListener el model/event-change-request
        (fn [^js ev] (.preventDefault ev)))
      (let [next-btn (.querySelector root "[part~='button-next']")]
        (.click next-btn))
      (js/setTimeout
       (fn []
         (is (= "1" (.getAttribute el "page"))
             "page should NOT change when change-request is cancelled")
         (done))
       0))))

;; ── Page items keep their nodes ───────────────────────────────────────────
(defn- page-button [^js el n]
  (.querySelector (.-shadowRoot el) (str "button[data-page='" n "']")))

(defn- follow-page-change! [^js el]
  (.addEventListener el model/event-page-change
                     (fn [^js ev]
                       (.setAttribute el "page" (str (.-page (.-detail ev)))))))

(deftest clicked-page-button-keeps-its-node-and-focus-test
  (let [^js el  (make-el {"page" "3" "total-pages" "10"})
        ^js btn (page-button el 4)]
    (follow-page-change! el)
    (.focus btn)
    (.click btn)
    (is (= "4" (.getAttribute el "page")))
    (is (identical? btn (page-button el 4)))
    (is (true? (.-isConnected btn)))
    (is (identical? btn (.-activeElement (.-shadowRoot el))))
    (is (= "page" (.getAttribute btn "aria-current")))))

(deftest page-items-that-stay-keep-their-nodes-test
  (let [^js el (make-el {"page" "3" "total-pages" "10"})
        before (mapv (partial page-button el) [1 3 4 10])]
    (.setAttribute el "page" "4")
    (is (every? true? (map identical? before (mapv (partial page-button el) [1 3 4 10]))))
    (is (nil? (page-button el 2)))
    (is (some? (page-button el 5)))))

(deftest previous-current-page-loses-its-mark-test
  (let [^js el (make-el {"page" "3" "total-pages" "10"})]
    (.setAttribute el "page" "4")
    (is (false? (.hasAttribute (page-button el 3) "aria-current")))
    (is (false? (.hasAttribute (page-button el 3) "data-current")))
    (is (= 1 (.-length (.querySelectorAll (.-shadowRoot el) "[aria-current='page']"))))))

(defn- item-label
  "What a list item shows: its page, a gap, or its own part name."
  [^js li]
  (or (.getAttribute li "data-page")
      (when (.querySelector li "[part~='ellipsis']") "…")
      (second (.split (.getAttribute li "part") " "))))

(defn- item-labels [^js el]
  (mapv item-label (array-seq (.-children (.querySelector (.-shadowRoot el) "[part~='list']")))))

(deftest page-items-are-in-page-order-between-prev-and-next-test
  (let [^js el (make-el {"page" "3" "total-pages" "10"})]
    (.setAttribute el "page" "8")
    (.setAttribute el "page" "2")
    (is (= ["item-prev" "1" "2" "3" "…" "10" "item-next"] (item-labels el)))))

(deftest fewer-total-pages-drops-items-and-keeps-the-rest-test
  (let [^js el (make-el {"page" "2" "total-pages" "10"})
        before (mapv (partial page-button el) [1 2 3])]
    (.setAttribute el "total-pages" "3")
    (is (= ["item-prev" "1" "2" "3" "item-next"] (item-labels el)))
    (is (every? true? (map identical? before (mapv (partial page-button el) [1 2 3]))))))

(defn- changes-while
  "The mutation records of the shadow tree of `el` while `f` runs."
  [^js el f]
  (let [observer (js/MutationObserver. (fn [_ _]))]
    (.observe observer (.-shadowRoot el) #js {:subtree true :attributes true :childList true :characterData true})
    (f)
    (let [records (array-seq (.takeRecords observer))]
      (.disconnect observer)
      records)))

(defn- changed-page [^js record]
  (.getAttribute (.-target record) "data-page"))

(deftest attribute-write-that-leaves-the-model-equal-changes-nothing-test
  (let [^js el (make-el {"page" "5" "total-pages" "5"})]
    (is (empty? (changes-while el (fn [] (.setAttribute el "page" "99")))))))

(deftest page-change-writes-only-the-buttons-whose-state-changed-test
  (let [^js el  (make-el {"page" "1" "total-pages" "3"})
        records (changes-while el (fn [] (.setAttribute el "page" "2")))]
    (is (= #{"1" "2"} (set (keep changed-page records))))))

(deftest disabled-reaches-page-buttons-that-stay-test
  (let [^js el (make-el {"page" "3" "total-pages" "10"})]
    (.setAttribute el "disabled" "")
    (is (true? (.hasAttribute (page-button el 3) "disabled")))
    (.removeAttribute el "disabled")
    (is (false? (.hasAttribute (page-button el 3) "disabled")))))
