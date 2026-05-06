(ns baredom.components.x-spotlight-card.x-spotlight-card-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-spotlight-card.x-spotlight-card :as x]
            [baredom.components.x-spotlight-card.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures
 :each
 {:before cleanup-dom!
  :after  cleanup-dom!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-part [el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn set-js-prop! [obj prop value]
  (js* "(~{}[~{}] = ~{})" obj prop value))

(defn get-js-prop [obj prop]
  (js* "(~{}[~{}])" obj prop))

(defn pointer-event [name init]
  (js/PointerEvent. name init))

(defn host-css-var [el var-name]
  (.getPropertyValue (.-style el) var-name))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-parts-exist-test
  (let [el (append! (make-el))
        card      (shadow-part el "[part='card']")
        spotlight (shadow-part el "[part='spotlight']")
        content   (shadow-part el "[part='content']")
        slot      (shadow-part el "slot")]
    (is (some? card))
    (is (some? spotlight))
    (is (some? content))
    (is (some? slot))
    (is (= "DIV" (.-tagName card)))
    (is (= "true" (.getAttribute spotlight "aria-hidden")))))

(deftest default-state-data-attributes-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (is (= "elevated" (.getAttribute card "data-variant")))
    (is (= "lg"       (.getAttribute card "data-radius")))
    (is (= "md"       (.getAttribute card "data-padding")))
    (is (not (.hasAttribute card "data-static")))
    (is (not (.hasAttribute card "data-active")))))

(deftest default-state-css-variables-test
  (let [el (append! (make-el))]
    (is (= "var(--x-color-primary, #2563eb)" (host-css-var el "--x-spotlight-card-color")))
    (is (= "0.18"  (host-css-var el "--x-spotlight-card-intensity")))
    (is (= "200px" (host-css-var el "--x-spotlight-card-size")))))

(deftest variant-radius-padding-flip-data-attrs-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.setAttribute el model/attr-variant "bordered")
    (.setAttribute el model/attr-radius  "sm")
    (.setAttribute el model/attr-padding "lg")
    (is (= "bordered" (.getAttribute card "data-variant")))
    (is (= "sm"       (.getAttribute card "data-radius")))
    (is (= "lg"       (.getAttribute card "data-padding")))))

(deftest invalid-enum-values-fall-back-to-defaults-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.setAttribute el model/attr-variant "bogus")
    (.setAttribute el model/attr-radius  "bogus")
    (.setAttribute el model/attr-padding "bogus")
    (is (= "elevated" (.getAttribute card "data-variant")))
    (is (= "lg"       (.getAttribute card "data-radius")))
    (is (= "md"       (.getAttribute card "data-padding")))))

(deftest color-attribute-resolves-css-var-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-color "danger")
    (is (= "var(--x-color-danger, #dc2626)" (host-css-var el "--x-spotlight-card-color")))
    (.setAttribute el model/attr-color "success")
    (is (= "var(--x-color-success, #16a34a)" (host-css-var el "--x-spotlight-card-color")))))

(deftest intensity-and-size-attributes-resolve-css-vars-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-intensity "strong")
    (.setAttribute el model/attr-size "xl")
    (is (= "0.28"  (host-css-var el "--x-spotlight-card-intensity")))
    (is (= "360px" (host-css-var el "--x-spotlight-card-size")))))

(deftest property-accessors-reflect-attributes-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (set-js-prop! el "variant" "bordered")
    (set-js-prop! el "color"   "warning")
    (is (= "bordered" (get-js-prop el "variant")))
    (is (= "bordered" (.getAttribute el model/attr-variant)))
    (is (= "bordered" (.getAttribute card "data-variant")))
    (is (= "warning"  (.getAttribute el model/attr-color)))
    (is (= "var(--x-color-warning, #f59e0b)" (host-css-var el "--x-spotlight-card-color")))))

(deftest static-attribute-flips-data-static-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.setAttribute el model/attr-static "")
    (is (= "true" (.getAttribute card "data-static")))
    (.removeAttribute el model/attr-static)
    (is (not (.hasAttribute card "data-static")))))

(deftest pointer-enter-and-leave-toggle-active-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.dispatchEvent el (pointer-event "pointerenter" #js {}))
    (is (= "true" (.getAttribute card "data-active")))
    (.dispatchEvent el (pointer-event "pointerleave" #js {}))
    (is (not (.hasAttribute card "data-active")))))

(deftest pointer-move-updates-position-css-vars-test
  (let [el (append! (make-el))]
    (set! (.-getBoundingClientRect el)
          (fn [] #js {:left 0 :top 0 :width 100 :height 100}))
    (.dispatchEvent el (pointer-event "pointermove" #js {:clientX 50 :clientY 25}))
    (is (= "50%" (host-css-var el "--x-spotlight-card-x")))
    (is (= "25%" (host-css-var el "--x-spotlight-card-y")))))

(deftest disconnect-clears-active-state-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.dispatchEvent el (pointer-event "pointerenter" #js {}))
    (is (= "true" (.getAttribute card "data-active")))
    (.remove el)
    (is (not (.hasAttribute card "data-active")))))

(deftest static-disables-pointer-tracking-test
  (let [el (append! (make-el))
        card (shadow-part el "[part='card']")]
    (.setAttribute el model/attr-static "")
    (.dispatchEvent el (pointer-event "pointerenter" #js {}))
    ;; data-active is not toggled when listeners are detached
    (is (not (.hasAttribute card "data-active")))
    ;; data-static is what shows the spotlight in this mode
    (is (= "true" (.getAttribute card "data-static")))))

(deftest slot-content-renders-through-default-slot-test
  (let [el (append! (make-el))
        child (.createElement js/document "p")
        slot (shadow-part el "slot")]
    (set! (.-textContent child) "Hello spotlight")
    (.appendChild el child)
    (let [nodes (.assignedNodes slot #js {:flatten true})]
      (is (= 1 (alength nodes)))
      (is (= child (aget nodes 0))))))

(deftest dark-mode-and-reduced-motion-style-blocks-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text "color-scheme: light dark")))
    (is (not= -1 (.indexOf css-text "@media (prefers-color-scheme: dark)")))
    (is (not= -1 (.indexOf css-text "@media (prefers-reduced-motion: reduce)")))
    (is (not= -1 (.indexOf css-text "radial-gradient")))))
