(ns baredom.components.x-breadcrumbs.x-breadcrumbs-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-breadcrumbs.x-breadcrumbs :as x]
            [baredom.components.x-breadcrumbs.model         :as model]))

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

(defn add-crumb! [^js el text]
  (let [^js a (.createElement js/document "a")]
    (.setAttribute a "href" "#")
    (set! (.-textContent a) text)
    (.appendChild el a)
    a))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=root]")))
    (is (some? (shadow-part el "[part=list]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "md"      (.getAttribute el "data-size")))
    (is (= "default" (.getAttribute el "data-variant")))
    ;; Nav should have a default aria-label
    (is (= "Breadcrumb" (.getAttribute (shadow-part el "[part=root]") "aria-label")))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-attribute-test
  (doseq [s ["sm" "md" "lg"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size s)
      (is (= s (.getAttribute el "data-size"))))))

;; ── Variant attribute ─────────────────────────────────────────────────────
(deftest variant-attribute-test
  (doseq [v ["default" "subtle" "text"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant v)
      (is (= v (.getAttribute el "data-variant"))))))

;; ── aria-label propagates to nav ──────────────────────────────────────────
(deftest aria-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-aria-label "Page breadcrumbs")
    (is (= "Page breadcrumbs" (.getAttribute (shadow-part el "[part=root]") "aria-label")))))

;; ── Crumb rendering (async for slotchange) ────────────────────────────────
(deftest crumbs-rendered-test
  (async done
    (let [^js el (append! (make-el))]
      (add-crumb! el "Home")
      (add-crumb! el "Products")
      (add-crumb! el "Shoes")
      (js/setTimeout
       (fn []
         (let [^js ol    (shadow-part el "[part=list]")
               crumbs    (.querySelectorAll ol ".crumb")]
           (is (= 3 (.-length crumbs))))
         (done))
       0))))

(deftest last-crumb-gets-aria-current-test
  (async done
    (let [^js el (append! (make-el))]
      (add-crumb! el "Home")
      (add-crumb! el "Products")
      (add-crumb! el "Shoes")
      (js/setTimeout
       (fn []
         (let [^js ol    (shadow-part el "[part=list]")
               crumbs    (array-seq (.querySelectorAll ol ".crumb"))
               last-crumb (last crumbs)]
           (is (some? (when last-crumb (.getAttribute last-crumb "data-current")))))
         (done))
       0))))

(deftest preserve-aria-current-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-preserve-aria-current "")
      (add-crumb! el "Home")
      (add-crumb! el "Products")
      (js/setTimeout
       (fn []
         (let [^js ol    (shadow-part el "[part=list]")
               crumbs    (array-seq (.querySelectorAll ol ".crumb"))]
           ;; With preserve-aria-current, no crumb should get data-current auto-assigned
           (is (every? #(nil? (.getAttribute % "data-current")) crumbs)))
         (done))
       0))))

;; ── Collapse / ellipsis ───────────────────────────────────────────────────
(deftest ellipsis-shown-when-max-items-exceeded-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-max-items "3")
      (add-crumb! el "Home")
      (add-crumb! el "Category")
      (add-crumb! el "Sub")
      (add-crumb! el "Product")
      (add-crumb! el "Detail")
      (js/setTimeout
       (fn []
         (let [^js ol (shadow-part el "[part=list]")
               ellipsis (.querySelector ol ".ellipsis")]
           (is (some? ellipsis)))
         (done))
       0))))

(deftest no-ellipsis-when-under-max-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-max-items "10")
      (add-crumb! el "Home")
      (add-crumb! el "Products")
      (js/setTimeout
       (fn []
         (let [^js ol (shadow-part el "[part=list]")
               ellipsis (.querySelector ol ".ellipsis")]
           (is (nil? ellipsis)))
         (done))
       0))))

;; ── Disabled state ────────────────────────────────────────────────────────
(deftest disabled-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute el model/attr-disabled))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest separator-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "/" (.-separator el)))))

(deftest separator-property-roundtrip-test
  (let [^js el (append! (make-el))]
    (set! (.-separator el) ">")
    (is (= ">" (.getAttribute el model/attr-separator)))
    (is (= ">" (.-separator el)))))

(deftest size-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "md" (.-size el)))))

(deftest items-before-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 1 (aget el "items-before")))))

(deftest items-after-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 2 (aget el "items-after")))))

;; ── Reconnect: stable after remove/re-add ────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-size "lg")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= "lg" (.getAttribute el "data-size")))))
