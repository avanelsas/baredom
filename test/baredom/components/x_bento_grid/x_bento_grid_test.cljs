(ns baredom.components.x-bento-grid.x-bento-grid-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing]]
            [baredom.components.x-bento-grid.x-bento-grid :as x]
            [baredom.components.x-bento-grid.model :as model]))

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

(defn style-prop
  [el prop]
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
    (is (= "base" (.getAttribute base "part")))
    (is (= "base" (.-className base)))))

(deftest default-layout-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= "repeat(4,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))
    (is (= "auto" (style-prop base "--x-bento-grid-row-height")))
    (is (= "16px" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "16px" (style-prop base "--x-bento-grid-column-gap")))))

(deftest custom-columns-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-columns "3")
    (is (= "repeat(3,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))))

(deftest columns-clamping-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-columns "0")
    (is (= "repeat(1,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))
    (.setAttribute el model/attr-columns "20")
    (is (= "repeat(12,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))))

(deftest gap-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "lg")
    (is (= "24px" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "24px" (style-prop base "--x-bento-grid-column-gap")))

    (.setAttribute el model/attr-gap "bad")
    (is (= "16px" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "16px" (style-prop base "--x-bento-grid-column-gap")))))

(deftest row-gap-and-column-gap-override-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "lg")
    (.setAttribute el model/attr-row-gap "xl")
    (.setAttribute el model/attr-column-gap "sm")
    (is (= "32px" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "8px" (style-prop base "--x-bento-grid-column-gap")))))

(deftest row-height-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-row-height "120px")
    (is (= "120px" (style-prop base "--x-bento-grid-row-height")))))

(deftest attribute-removal-reverts-to-defaults-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-columns "6")
    (.setAttribute el model/attr-gap "xl")
    (.setAttribute el model/attr-row-height "200px")
    (is (= "repeat(6,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))

    (.removeAttribute el model/attr-columns)
    (.removeAttribute el model/attr-gap)
    (.removeAttribute el model/attr-row-height)
    (is (= "repeat(4,minmax(0,1fr))"
           (style-prop base "--x-bento-grid-columns")))
    (is (= "16px" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "auto" (style-prop base "--x-bento-grid-row-height")))))

(deftest slot-projects-children-test
  (let [el (append! (make-el))
        child-a (.createElement js/document "div")
        child-b (.createElement js/document "div")
        slot (shadow-part el "slot")]
    (.appendChild el child-a)
    (.appendChild el child-b)
    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 2 (alength nodes)))
      (is (= child-a (aget nodes 0)))
      (is (= child-b (aget nodes 1))))))

(deftest style-surface-includes-grid-rules-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text "display:grid")))
    (is (not= -1 (.indexOf css-text "grid-auto-flow:dense")))))

(deftest gap-tokens-none-xs-xl-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "gap" "none")
    (is (= "0" (style-prop base "--x-bento-grid-row-gap")))
    (is (= "0" (style-prop base "--x-bento-grid-column-gap")))
    (.setAttribute el "gap" "xs")
    (is (= "4px" (style-prop base "--x-bento-grid-row-gap")))
    (.setAttribute el "gap" "xl")
    (is (= "32px" (style-prop base "--x-bento-grid-row-gap")))))
