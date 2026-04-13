(ns baredom.components.x-bento-item.x-bento-item-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-bento-item.x-bento-item :as x]
            [baredom.components.x-bento-item.model :as model]))

(x/init!)

(defn cleanup-dom!
  []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures
 :each
 {:before cleanup-dom!
  :after cleanup-dom!})

(defn make-el
  []
  (.createElement js/document model/tag-name))

(defn append!
  [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-part
  [el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn host-style-prop
  [^js el prop]
  (.getPropertyValue (.-style el) prop))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest base-part-and-slot-exist-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        base (shadow-part el "[part='base']")
        slot (shadow-part el "slot")]
    (is (some? root))
    (is (some? base))
    (is (some? slot))
    (is (= "DIV" (.-tagName base)))
    (is (= "base" (.getAttribute base "part")))))

(deftest default-span-styles-test
  (let [el (append! (make-el))]
    (is (= "span 1" (host-style-prop el "grid-column")))
    (is (= "span 1" (host-style-prop el "grid-row")))
    (is (= "" (host-style-prop el "order")))))

(deftest col-span-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-col-span "2")
    (is (= "span 2" (host-style-prop el "grid-column")))))

(deftest row-span-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-row-span "3")
    (is (= "span 3" (host-style-prop el "grid-row")))))

(deftest order-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-order "5")
    (is (= "5" (host-style-prop el "order")))))

(deftest span-clamping-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-col-span "0")
    (is (= "span 1" (host-style-prop el "grid-column")))
    (.setAttribute el model/attr-col-span "10")
    (is (= "span 6" (host-style-prop el "grid-column")))
    (.setAttribute el model/attr-row-span "99")
    (is (= "span 6" (host-style-prop el "grid-row")))))

(deftest invalid-span-falls-back-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-col-span "abc")
    (is (= "span 1" (host-style-prop el "grid-column")))
    (.setAttribute el model/attr-row-span "")
    (is (= "span 1" (host-style-prop el "grid-row")))))

(deftest attribute-removal-reverts-to-defaults-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-col-span "3")
    (.setAttribute el model/attr-row-span "2")
    (.setAttribute el model/attr-order "1")
    (is (= "span 3" (host-style-prop el "grid-column")))
    (is (= "span 2" (host-style-prop el "grid-row")))
    (is (= "1" (host-style-prop el "order")))

    (.removeAttribute el model/attr-col-span)
    (.removeAttribute el model/attr-row-span)
    (.removeAttribute el model/attr-order)
    (is (= "span 1" (host-style-prop el "grid-column")))
    (is (= "span 1" (host-style-prop el "grid-row")))
    (is (= "" (host-style-prop el "order")))))

(deftest slot-projects-children-test
  (let [el (append! (make-el))
        child (.createElement js/document "div")
        slot (shadow-part el "slot")]
    (.appendChild el child)
    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 1 (alength nodes)))
      (is (= child (aget nodes 0))))))
