(ns app.components.x-container.x-container-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing]]
            [app.components.x-container.x-container :as x]
            [app.components.x-container.model :as model]))

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

(defn set-js-prop!
  [obj prop value]
  (js* "(~{}[~{}] = ~{})" obj prop value))

(defn get-js-prop
  [obj prop]
  (js* "(~{}[~{}])" obj prop))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest base-part-exists-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")
        slot (shadow-part el "slot")]
    (is (some? base))
    (is (some? slot))
    (is (= "DIV" (.-tagName base)))))

(deftest center-defaults-to-true-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= true (get-js-prop el "center")))
    (is (= "true" (.getAttribute base "data-center")))))

(deftest semantic-root-defaults-to-div-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= "DIV" (.-tagName base)))))

(deftest semantic-root-follows-as-attribute-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-as "section")
    (is (= "SECTION" (.-tagName (shadow-part el "[part='base']"))))

    (.setAttribute el model/attr-as "main")
    (is (= "MAIN" (.-tagName (shadow-part el "[part='base']"))))

    (.setAttribute el model/attr-as "nav")
    (is (= "NAV" (.-tagName (shadow-part el "[part='base']"))))))

(deftest invalid-as-normalizes-internally-test
  (let [el (append! (make-el))
        original-base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-as "weird")
    (is (= "DIV" (.-tagName (shadow-part el "[part='base']"))))
    (is (= original-base (shadow-part el "[part='base']")))))

(deftest label-maps-to-aria-label-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-label "Main content")
    (is (= "Main content" (.getAttribute base "aria-label")))

    (.removeAttribute el model/attr-label)
    (is (not (.hasAttribute base "aria-label")))))

(deftest size-and-padding-normalize-to-data-attributes-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-size "bad")
    (.setAttribute el model/attr-padding "bad")

    (is (= "lg" (.getAttribute base "data-size")))
    (is (= "md" (.getAttribute base "data-padding")))

    (.setAttribute el model/attr-size "xs")
    (.setAttribute el model/attr-padding "lg")

    (is (= "xs" (.getAttribute base "data-size")))
    (is (= "lg" (.getAttribute base "data-padding")))))

(deftest center-property-reflection-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= true (get-js-prop el "center")))
    (is (not (.hasAttribute el model/attr-center)))
    (is (= "true" (.getAttribute base "data-center")))

    (set-js-prop! el "center" false)
    (is (= false (get-js-prop el "center")))
    (is (= "false" (.getAttribute el model/attr-center)))
    (is (= "false" (.getAttribute base "data-center")))

    (set-js-prop! el "center" true)
    (is (= true (get-js-prop el "center")))
    (is (not (.hasAttribute el model/attr-center)))
    (is (= "true" (.getAttribute base "data-center")))))

(deftest fluid-property-reflection-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= false (get-js-prop el "fluid")))
    (is (= "false" (.getAttribute base "data-fluid")))

    (set-js-prop! el "fluid" true)
    (is (= true (get-js-prop el "fluid")))
    (is (.hasAttribute el model/attr-fluid))
    (is (= "true" (.getAttribute base "data-fluid")))

    (set-js-prop! el "fluid" false)
    (is (= false (get-js-prop el "fluid")))
    (is (not (.hasAttribute el model/attr-fluid)))
    (is (= "false" (.getAttribute base "data-fluid")))))

(deftest host-attributes-are-not-rewritten-by-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-size "bad")
    (.setAttribute el model/attr-padding "bad")
    (.setAttribute el model/attr-as "bad")

    (is (= "bad" (.getAttribute el model/attr-size)))
    (is (= "bad" (.getAttribute el model/attr-padding)))
    (is (= "bad" (.getAttribute el model/attr-as)))

    (is (= "lg" (.getAttribute base "data-size")))
    (is (= "md" (.getAttribute base "data-padding")))
    (is (= "DIV" (.-tagName base)))))

(deftest slot-content-renders-through-default-slot-test
  (let [el (append! (make-el))
        child (.createElement js/document "p")
        slot (shadow-part el "slot")]
    (set! (.-textContent child) "Hello container")
    (.appendChild el child)

    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 1 (alength nodes)))
      (is (= child (aget nodes 0))))))

(deftest semantic-root-replacement-preserves-slot-element-test
  (let [el (append! (make-el))
        initial-slot (shadow-part el "slot")]
    (.setAttribute el model/attr-as "section")
    (is (= "SECTION" (.-tagName (shadow-part el "[part='base']"))))
    (is (= initial-slot (shadow-part el "slot")))

    (.setAttribute el model/attr-as "article")
    (is (= "ARTICLE" (.-tagName (shadow-part el "[part='base']"))))
    (is (= initial-slot (shadow-part el "slot")))))

(deftest style-token-surface-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setProperty (.-style el) "--x-container-padding-block" "24px")
    (.setProperty (.-style el) "--x-container-radius" "16px")
    (is (some? base))))

(deftest dark-mode-ready-surface-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text "color-scheme:light dark")))
    (is (not= -1 (.indexOf css-text "@media (prefers-color-scheme: dark)")))
    (is (not= -1 (.indexOf css-text "--x-container-color:#e5e7eb")))))
