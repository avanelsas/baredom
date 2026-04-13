(ns baredom.components.x-tab.x-tab-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-tab.x-tab :as x]
            [baredom.components.x-tab.model :as model]))

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

(deftest default-a11y-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= "tab" (.getAttribute el "role")))
    (is (= "false" (.getAttribute el "aria-selected")))
    (is (not (.hasAttribute el "aria-disabled")))
    (is (= "-1" (.getAttribute el "tabindex")))
    (is (not (.hasAttribute el "aria-controls")))
    (is (not (.hasAttribute el "aria-label")))
    (is (= "false" (.getAttribute base "data-selected")))
    (is (= "false" (.getAttribute base "data-disabled")))
    (is (= "md" (.getAttribute base "data-size")))
    (is (= "default" (.getAttribute base "data-variant")))
    (is (= "horizontal" (.getAttribute base "data-orientation")))))

(deftest selected-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-selected "")
    (is (= "true" (.getAttribute el "aria-selected")))
    (is (= "0" (.getAttribute el "tabindex")))
    (is (= "true" (.getAttribute base "data-selected")))))

(deftest disabled-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-disabled "")
    (is (= "false" (.getAttribute el "aria-selected")))
    (is (= "true" (.getAttribute el "aria-disabled")))
    (is (= "-1" (.getAttribute el "tabindex")))
    (is (= "true" (.getAttribute base "data-disabled")))))

(deftest selected-and-disabled-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-selected "")
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute el "aria-selected")))
    (is (= "true" (.getAttribute el "aria-disabled")))
    (is (= "-1" (.getAttribute el "tabindex")))
    (is (= "true" (.getAttribute base "data-selected")))
    (is (= "true" (.getAttribute base "data-disabled")))))

(deftest controls-and-label-map-to-aria-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-controls "panel-overview")
    (.setAttribute el model/attr-label "Overview tab")
    (is (= "panel-overview" (.getAttribute el "aria-controls")))
    (is (= "Overview tab" (.getAttribute el "aria-label")))

    (.removeAttribute el model/attr-controls)
    (.removeAttribute el model/attr-label)
    (is (not (.hasAttribute el "aria-controls")))
    (is (not (.hasAttribute el "aria-label")))))

(deftest size-variant-and-orientation-normalize-to-data-attributes-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-size "bad")
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-orientation "bad")

    (is (= "md" (.getAttribute base "data-size")))
    (is (= "default" (.getAttribute base "data-variant")))
    (is (= "horizontal" (.getAttribute base "data-orientation")))

    (.setAttribute el model/attr-size "lg")
    (.setAttribute el model/attr-variant "pill")
    (.setAttribute el model/attr-orientation "vertical")

    (is (= "lg" (.getAttribute base "data-size")))
    (is (= "pill" (.getAttribute base "data-variant")))
    (is (= "vertical" (.getAttribute base "data-orientation")))))

(deftest selected-property-reflection-test
  (let [el (append! (make-el))]
    (is (= false (get-js-prop el "selected")))
    (is (not (.hasAttribute el model/attr-selected)))

    (set-js-prop! el "selected" true)
    (is (= true (get-js-prop el "selected")))
    (is (.hasAttribute el model/attr-selected))
    (is (= "true" (.getAttribute el "aria-selected")))
    (is (= "0" (.getAttribute el "tabindex")))

    (set-js-prop! el "selected" false)
    (is (= false (get-js-prop el "selected")))
    (is (not (.hasAttribute el model/attr-selected)))
    (is (= "false" (.getAttribute el "aria-selected")))
    (is (= "-1" (.getAttribute el "tabindex")))))

(deftest disabled-property-reflection-test
  (let [el (append! (make-el))]
    (is (= false (get-js-prop el "disabled")))
    (is (not (.hasAttribute el model/attr-disabled)))

    (set-js-prop! el "disabled" true)
    (is (= true (get-js-prop el "disabled")))
    (is (.hasAttribute el model/attr-disabled))
    (is (= "true" (.getAttribute el "aria-disabled")))
    (is (= "-1" (.getAttribute el "tabindex")))

    (set-js-prop! el "disabled" false)
    (is (= false (get-js-prop el "disabled")))
    (is (not (.hasAttribute el model/attr-disabled)))
    (is (not (.hasAttribute el "aria-disabled")))))

(deftest value-property-reflection-test
  (let [el (append! (make-el))]
    (is (nil? (get-js-prop el "value")))
    (is (not (.hasAttribute el model/attr-value)))

    (set-js-prop! el "value" "analytics")
    (is (= "analytics" (get-js-prop el "value")))
    (is (= "analytics" (.getAttribute el model/attr-value)))

    (set-js-prop! el "value" nil)
    (is (nil? (get-js-prop el "value")))
    (is (not (.hasAttribute el model/attr-value)))))

(deftest host-attributes-are-not-rewritten-by-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-size "bad")
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-orientation "bad")

    (is (= "bad" (.getAttribute el model/attr-size)))
    (is (= "bad" (.getAttribute el model/attr-variant)))
    (is (= "bad" (.getAttribute el model/attr-orientation)))

    (is (= "md" (.getAttribute base "data-size")))
    (is (= "default" (.getAttribute base "data-variant")))
    (is (= "horizontal" (.getAttribute base "data-orientation")))))

(deftest slot-content-renders-through-default-slot-test
  (let [el (append! (make-el))
        child (.createElement js/document "span")
        slot (shadow-part el "slot")]
    (set! (.-textContent child) "Overview")
    (.appendChild el child)

    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 1 (alength nodes)))
      (is (= child (aget nodes 0))))))

(deftest click-dispatches-tab-select-when-enabled-and-not-selected-test
  (let [el (append! (make-el))
        calls (atom 0)
        value* (atom nil)]
    (.setAttribute el model/attr-value "reports")
    (.addEventListener
     el
     "tab-select"
     (fn [e]
       (swap! calls inc)
       (reset! value* (.. e -detail -value))))

    (.click el)

    (is (= 1 @calls))
    (is (= "reports" @value*))))

(deftest click-does-not-dispatch-when-selected-test
  (let [el (append! (make-el))
        calls (atom 0)]
    (.setAttribute el model/attr-selected "")
    (.addEventListener el "tab-select" (fn [_] (swap! calls inc)))

    (.click el)

    (is (= 0 @calls))))

(deftest click-does-not-dispatch-when-disabled-test
  (let [el (append! (make-el))
        calls (atom 0)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el "tab-select" (fn [_] (swap! calls inc)))

    (.click el)

    (is (= 0 @calls))))

(deftest enter-dispatches-tab-select-test
  (let [el (append! (make-el))
        calls (atom 0)
        value* (atom nil)
        event (js/KeyboardEvent. "keydown" #js {:key "Enter"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute el model/attr-value "analytics")
    (.addEventListener
     el
     "tab-select"
     (fn [e]
       (swap! calls inc)
       (reset! value* (.. e -detail -value))))

    (.dispatchEvent el event)

    (is (= 1 @calls))
    (is (= "analytics" @value*))))

(deftest space-dispatches-tab-select-test
  (let [el (append! (make-el))
        calls (atom 0)
        event (js/KeyboardEvent. "keydown" #js {:key " "
                                                :bubbles true
                                                :cancelable true})]
    (.addEventListener el "tab-select" (fn [_] (swap! calls inc)))

    (.dispatchEvent el event)

    (is (= 1 @calls))))

(deftest keyboard-does-not-dispatch-when-disabled-or-selected-test
  (let [disabled-el (append! (make-el))
        selected-el (append! (make-el))
        disabled-calls (atom 0)
        selected-calls (atom 0)
        event (js/KeyboardEvent. "keydown" #js {:key "Enter"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute disabled-el model/attr-disabled "")
    (.setAttribute selected-el model/attr-selected "")

    (.addEventListener disabled-el "tab-select" (fn [_] (swap! disabled-calls inc)))
    (.addEventListener selected-el "tab-select" (fn [_] (swap! selected-calls inc)))

    (.dispatchEvent disabled-el event)
    (.dispatchEvent selected-el event)

    (is (= 0 @disabled-calls))
    (is (= 0 @selected-calls))))

(deftest style-surface-includes-state-and-motion-rules-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text ":host")))
    (is (not= -1 (.indexOf css-text ".base")))
    (is (not= -1 (.indexOf css-text "data-selected='true'")))
    (is (not= -1 (.indexOf css-text "data-disabled='true'")))
    (is (not= -1 (.indexOf css-text ":focus-visible")))
    (is (not= -1 (.indexOf css-text "@media (prefers-reduced-motion: reduce)")))))
