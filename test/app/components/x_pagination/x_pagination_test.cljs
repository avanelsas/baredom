(ns app.components.x-pagination.x-pagination-test
  (:require [cljs.test :refer-macros [deftest is async use-fixtures]]
            [app.components.x-pagination.x-pagination :as comp]
            [app.components.x-pagination.model :as model]))

(use-fixtures :each
  {:before (fn [] (comp/register!))
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
