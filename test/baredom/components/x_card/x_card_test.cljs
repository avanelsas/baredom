(ns baredom.components.x-card.x-card-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing]]
            [baredom.components.x-card.x-card :as x]
            [baredom.components.x-card.model :as model]))

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

(deftest default-role-and-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (nil? (.getAttribute el "role")))
    (is (not (.hasAttribute el "tabindex")))
    (is (not (.hasAttribute el "aria-disabled")))
    (is (not (.hasAttribute el "aria-label")))
    (is (= "elevated" (.getAttribute base "data-variant")))
    (is (= "md" (.getAttribute base "data-padding")))
    (is (= "lg" (.getAttribute base "data-radius")))
    (is (not (.hasAttribute base "data-interactive")))
    (is (not (.hasAttribute base "data-disabled")))))

(deftest label-maps-to-aria-label-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Card label")
    (is (= "Card label" (.getAttribute el "aria-label")))

    (.removeAttribute el model/attr-label)
    (is (not (.hasAttribute el "aria-label")))))

(deftest variant-padding-radius-normalize-to-data-attributes-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-padding "bad")
    (.setAttribute el model/attr-radius "bad")

    (is (= "elevated" (.getAttribute base "data-variant")))
    (is (= "md" (.getAttribute base "data-padding")))
    (is (= "lg" (.getAttribute base "data-radius")))

    (.setAttribute el model/attr-variant "outlined")
    (.setAttribute el model/attr-padding "lg")
    (.setAttribute el model/attr-radius "xl")

    (is (= "outlined" (.getAttribute base "data-variant")))
    (is (= "lg" (.getAttribute base "data-padding")))
    (is (= "xl" (.getAttribute base "data-radius")))))

(deftest interactive-property-reflection-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= false (get-js-prop el "interactive")))
    (is (not (.hasAttribute el model/attr-interactive)))
    (is (not (.hasAttribute base "data-interactive")))
    (is (nil? (.getAttribute el "role")))
    (is (not (.hasAttribute el "tabindex")))

    (set-js-prop! el "interactive" true)
    (is (= true (get-js-prop el "interactive")))
    (is (.hasAttribute el model/attr-interactive))
    (is (= "true" (.getAttribute base "data-interactive")))
    (is (= "button" (.getAttribute el "role")))
    (is (= "0" (.getAttribute el "tabindex")))

    (set-js-prop! el "interactive" false)
    (is (= false (get-js-prop el "interactive")))
    (is (not (.hasAttribute el model/attr-interactive)))
    (is (not (.hasAttribute base "data-interactive")))
    (is (nil? (.getAttribute el "role")))
    (is (not (.hasAttribute el "tabindex")))))

(deftest disabled-property-reflection-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (is (= false (get-js-prop el "disabled")))
    (is (not (.hasAttribute el model/attr-disabled)))
    (is (not (.hasAttribute base "data-disabled")))
    (is (not (.hasAttribute el "aria-disabled")))

    (set-js-prop! el "disabled" true)
    (is (= true (get-js-prop el "disabled")))
    (is (.hasAttribute el model/attr-disabled))
    (is (= "true" (.getAttribute base "data-disabled")))
    (is (= "true" (.getAttribute el "aria-disabled")))

    (set-js-prop! el "disabled" false)
    (is (= false (get-js-prop el "disabled")))
    (is (not (.hasAttribute el model/attr-disabled)))
    (is (not (.hasAttribute base "data-disabled")))
    (is (not (.hasAttribute el "aria-disabled")))))

(deftest interactive-and-disabled-derive-button-disabled-state-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-disabled "")

    (is (= "button" (.getAttribute el "role")))
    (is (= "-1" (.getAttribute el "tabindex")))
    (is (= "true" (.getAttribute el "aria-disabled")))
    (is (= "true" (.getAttribute base "data-interactive")))
    (is (= "true" (.getAttribute base "data-disabled")))))

(deftest host-attributes-are-not-rewritten-by-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-padding "bad")
    (.setAttribute el model/attr-radius "bad")

    (is (= "bad" (.getAttribute el model/attr-variant)))
    (is (= "bad" (.getAttribute el model/attr-padding)))
    (is (= "bad" (.getAttribute el model/attr-radius)))

    (is (= "elevated" (.getAttribute base "data-variant")))
    (is (= "md" (.getAttribute base "data-padding")))
    (is (= "lg" (.getAttribute base "data-radius")))))

(deftest slot-content-renders-through-default-slot-test
  (let [el (append! (make-el))
        child (.createElement js/document "p")
        slot (shadow-part el "slot")]
    (set! (.-textContent child) "Hello card")
    (.appendChild el child)

    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 1 (alength nodes)))
      (is (= child (aget nodes 0))))))

(deftest press-event-dispatches-on-click-when-interactive-test
  (let [el (append! (make-el))
        calls (atom 0)]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el "press" (fn [_] (swap! calls inc)))

    (.click el)

    (is (= 1 @calls))))

(deftest press-event-dispatches-on-enter-when-interactive-test
  (let [el (append! (make-el))
        calls (atom 0)
        event (js/KeyboardEvent. "keydown" #js {:key "Enter"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el "press" (fn [_] (swap! calls inc)))

    (.dispatchEvent el event)

    (is (= 1 @calls))))

(deftest press-event-dispatches-on-space-when-interactive-test
  (let [el (append! (make-el))
        calls (atom 0)
        event (js/KeyboardEvent. "keydown" #js {:key " "
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute el model/attr-interactive "")
    (.addEventListener el "press" (fn [_] (swap! calls inc)))

    (.dispatchEvent el event)

    (is (= 1 @calls))))

(deftest disabled-prevents-press-test
  (let [el (append! (make-el))
        calls (atom 0)
        enter-event (js/KeyboardEvent. "keydown" #js {:key "Enter"
                                                      :bubbles true
                                                      :cancelable true})
        space-event (js/KeyboardEvent. "keydown" #js {:key " "
                                                      :bubbles true
                                                      :cancelable true})]
    (.setAttribute el model/attr-interactive "")
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el "press" (fn [_] (swap! calls inc)))

    (.click el)
    (.dispatchEvent el enter-event)
    (.dispatchEvent el space-event)

    (is (= 0 @calls))))

(deftest style-token-surface-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setProperty (.-style el) "--x-card-padding-md" "24px")
    (.setProperty (.-style el) "--x-card-radius-lg" "16px")
    (.setProperty (.-style el) "--x-card-focus-ring" "rgb(255 0 0 / 0.5)")
    (is (some? base))))

(deftest dark-mode-ready-surface-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text "color-scheme: light dark")))
    (is (not= -1 (.indexOf css-text "@media (prefers-color-scheme: dark)")))
    (is (not= -1 (.indexOf css-text "@media (prefers-reduced-motion: reduce)")))))
