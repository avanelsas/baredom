(ns baredom.components.x-grid.x-grid-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing]]
            [baredom.components.x-grid.x-grid :as x]
            [baredom.components.x-grid.model :as model]))

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
    (is (= "md" (.getAttribute base "data-gap")))
    (is (= "repeat(auto-fit,minmax(16rem,1fr))"
           (style-prop base "--x-grid-columns")))
    (is (= "16px" (style-prop base "--x-grid-row-gap")))
    (is (= "16px" (style-prop base "--x-grid-column-gap")))
    (is (= "stretch" (style-prop base "--x-grid-align-items")))
    (is (= "stretch" (style-prop base "--x-grid-justify-items")))
    (is (= "row" (style-prop base "--x-grid-auto-flow")))))

(deftest default-slot-renders-through-shadow-dom-test
  (let [el (append! (make-el))
        child-a (.createElement js/document "div")
        child-b (.createElement js/document "div")
        slot (shadow-part el "slot")]
    (set! (.-textContent child-a) "A")
    (set! (.-textContent child-b) "B")
    (.appendChild el child-a)
    (.appendChild el child-b)

    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 2 (alength nodes)))
      (is (= child-a (aget nodes 0)))
      (is (= child-b (aget nodes 1))))))

(deftest explicit-columns-take-precedence-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-columns "repeat(3, minmax(0, 1fr))")
    (.setAttribute el model/attr-min-column-size "24rem")

    (is (= "repeat(3, minmax(0, 1fr))"
           (style-prop base "--x-grid-columns")))))

(deftest min-column-size-drives-responsive-template-when-columns-absent-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-min-column-size "20rem")

    (is (= "repeat(auto-fit,minmax(20rem,1fr))"
           (style-prop base "--x-grid-columns")))))

(deftest empty-columns-falls-back-to-responsive-template-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-columns "")
    (.setAttribute el model/attr-min-column-size "18rem")

    (is (= "repeat(auto-fit,minmax(18rem,1fr))"
           (style-prop base "--x-grid-columns")))))

(deftest empty-min-column-size-normalizes-to-default-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-min-column-size "")

    (is (= "repeat(auto-fit,minmax(16rem,1fr))"
           (style-prop base "--x-grid-columns")))))

(deftest gap-normalizes-to-css-lengths-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "lg")

    (is (= "lg" (.getAttribute base "data-gap")))
    (is (= "24px" (style-prop base "--x-grid-row-gap")))
    (is (= "24px" (style-prop base "--x-grid-column-gap")))

    (.setAttribute el model/attr-gap "bad")

    (is (= "md" (.getAttribute base "data-gap")))
    (is (= "16px" (style-prop base "--x-grid-row-gap")))
    (is (= "16px" (style-prop base "--x-grid-column-gap")))))

(deftest row-gap-and-column-gap-override-shared-gap-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "lg")
    (.setAttribute el model/attr-row-gap "xl")
    (.setAttribute el model/attr-column-gap "sm")

    (is (= "32px" (style-prop base "--x-grid-row-gap")))
    (is (= "8px" (style-prop base "--x-grid-column-gap")))))

(deftest invalid-row-gap-and-column-gap-fall-back-to-shared-gap-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "sm")
    (.setAttribute el model/attr-row-gap "bad")
    (.setAttribute el model/attr-column-gap "bad")

    (is (= "8px" (style-prop base "--x-grid-row-gap")))
    (is (= "8px" (style-prop base "--x-grid-column-gap")))))

(deftest align-and-justify-normalize-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-align-items "center")
    (.setAttribute el model/attr-justify-items "end")

    (is (= "center" (style-prop base "--x-grid-align-items")))
    (is (= "end" (style-prop base "--x-grid-justify-items")))

    (.setAttribute el model/attr-align-items "bad")
    (.setAttribute el model/attr-justify-items "bad")

    (is (= "stretch" (style-prop base "--x-grid-align-items")))
    (is (= "stretch" (style-prop base "--x-grid-justify-items")))))

(deftest auto-flow-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-auto-flow "column-dense")
    (is (= "column dense" (style-prop base "--x-grid-auto-flow")))

    (.setAttribute el model/attr-auto-flow "row-dense")
    (is (= "row dense" (style-prop base "--x-grid-auto-flow")))

    (.setAttribute el model/attr-auto-flow "dense")
    (is (= "dense" (style-prop base "--x-grid-auto-flow")))

    (.setAttribute el model/attr-auto-flow "bad")
    (is (= "row" (style-prop base "--x-grid-auto-flow")))))

(deftest inline-host-attribute-is-supported-test
  (let [el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-inline)))
    (.setAttribute el model/attr-inline "")
    (is (.hasAttribute el model/attr-inline))
    (.removeAttribute el model/attr-inline)
    (is (not (.hasAttribute el model/attr-inline)))))

(deftest host-attributes-are-not-rewritten-by-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-gap "bad")
    (.setAttribute el model/attr-row-gap "bad")
    (.setAttribute el model/attr-column-gap "bad")
    (.setAttribute el model/attr-align-items "bad")
    (.setAttribute el model/attr-justify-items "bad")
    (.setAttribute el model/attr-auto-flow "bad")

    (is (= "bad" (.getAttribute el model/attr-gap)))
    (is (= "bad" (.getAttribute el model/attr-row-gap)))
    (is (= "bad" (.getAttribute el model/attr-column-gap)))
    (is (= "bad" (.getAttribute el model/attr-align-items)))
    (is (= "bad" (.getAttribute el model/attr-justify-items)))
    (is (= "bad" (.getAttribute el model/attr-auto-flow)))

    (is (= "md" (.getAttribute base "data-gap")))
    (is (= "16px" (style-prop base "--x-grid-row-gap")))
    (is (= "16px" (style-prop base "--x-grid-column-gap")))
    (is (= "stretch" (style-prop base "--x-grid-align-items")))
    (is (= "stretch" (style-prop base "--x-grid-justify-items")))
    (is (= "row" (style-prop base "--x-grid-auto-flow")))))

(deftest style-surface-includes-grid-rules-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text ".base")))
    (is (not= -1 (.indexOf css-text "display:grid")))
    (is (not= -1 (.indexOf css-text "grid-template-columns:var(--x-grid-columns)")))))

(deftest gap-tokens-none-xs-xl-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "gap" "none")
    (is (= "0" (style-prop base "--x-grid-row-gap")))
    (is (= "0" (style-prop base "--x-grid-column-gap")))
    (.setAttribute el "gap" "xs")
    (is (= "4px" (style-prop base "--x-grid-row-gap")))
    (.setAttribute el "gap" "xl")
    (is (= "32px" (style-prop base "--x-grid-row-gap")))))

(deftest align-start-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "align-items" "start")
    (is (= "start" (style-prop base "--x-grid-align-items")))
    (.setAttribute el "justify-items" "start")
    (is (= "start" (style-prop base "--x-grid-justify-items")))))

(deftest auto-flow-column-and-row-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "auto-flow" "column")
    (is (= "column" (style-prop base "--x-grid-auto-flow")))
    (.setAttribute el "auto-flow" "row")
    (is (= "row" (style-prop base "--x-grid-auto-flow")))))

(deftest attribute-removal-reverts-to-defaults-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "gap" "xl")
    (is (= "32px" (style-prop base "--x-grid-row-gap")))
    (.removeAttribute el "gap")
    (is (= "16px" (style-prop base "--x-grid-row-gap")))
    (is (= "stretch" (style-prop base "--x-grid-align-items")))))
