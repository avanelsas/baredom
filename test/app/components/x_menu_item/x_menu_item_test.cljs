(ns app.components.x-menu-item.x-menu-item-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing async]]
            [app.components.x-menu-item.x-menu-item :as x]
            [app.components.x-menu-item.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-item
  ([] (.createElement js/document model/tag-name))
  ([value]
   (let [el (.createElement js/document model/tag-name)]
     (.setAttribute el "value" value)
     el))
  ([value text]
   (let [el (make-item value)]
     (set! (.-textContent el) text)
     el)))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-part [el selector]
  (.querySelector (.-shadowRoot el) selector))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest renders-shadow-structure-test
  (let [el (append! (make-item "a" "Item A"))
        root (.-shadowRoot el)
        base (shadow-part el "[part='base']")]
    (is (some? root))
    (is (some? base))
    (is (some? (shadow-part el "[part='icon']")))
    (is (some? (.querySelector root "slot[name='icon']")))
    (is (some? (.querySelector root "slot:not([name])")))))

(deftest renders-menuitem-role-test
  (let [el (append! (make-item "a" "Item A"))]
    (is (= "menuitem" (.getAttribute el "role")))
    (is (= "-1" (.getAttribute el "tabindex")))))

(deftest renders-separator-role-test
  (let [el (append! (make-item))]
    (.setAttribute el "type" "divider")
    (is (= "separator" (.getAttribute el "role")))
    (is (nil? (.getAttribute el "tabindex")))))

(deftest divider-no-click-response-test
  (let [el (append! (make-item))
        fired? (atom false)]
    (.setAttribute el "type" "divider")
    (.addEventListener el model/event-item-select (fn [_] (reset! fired? true)))
    (.click el)
    (is (= false @fired?))))

(deftest disabled-test
  (let [el (append! (make-item "a" "Item A"))
        fired? (atom false)]
    (.setAttribute el "disabled" "")
    (.addEventListener el model/event-item-select (fn [_] (reset! fired? true)))
    (.click el)
    (is (= false @fired?))
    (is (= "true" (.getAttribute el "aria-disabled")))))

(deftest variant-danger-test
  (let [el (append! (make-item "a" "Item A"))
        base (shadow-part el "[part='base']")]
    (.setAttribute el "variant" "danger")
    (is (= "danger" (.getAttribute base "data-variant")))))

(deftest click-selects-test
  (let [el (append! (make-item "my-value" "Click me"))
        detail (atom nil)]
    (.addEventListener el model/event-item-select
                       (fn [e] (reset! detail (.-detail e))))
    (.click el)
    (is (some? @detail))
    (is (= "my-value" (.-value @detail)))))

(deftest enter-selects-test
  (let [el (append! (make-item "enter-val" "Press Enter"))
        detail (atom nil)
        evt (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true :cancelable true})]
    (.addEventListener el model/event-item-select
                       (fn [e] (reset! detail (.-detail e))))
    (.dispatchEvent el evt)
    (is (some? @detail))
    (is (= "enter-val" (.-value @detail)))))

(deftest space-selects-test
  (let [el (append! (make-item "space-val" "Press Space"))
        detail (atom nil)
        evt (js/KeyboardEvent. "keydown" #js {:key " " :bubbles true :cancelable true})]
    (.addEventListener el model/event-item-select
                       (fn [e] (reset! detail (.-detail e))))
    (.dispatchEvent el evt)
    (is (some? @detail))
    (is (= "space-val" (.-value @detail)))))

(deftest icon-slot-test
  (async done
    (let [el (append! (make-item "icon-val" "With Icon"))
          icon (.createElement js/document "span")]
      (.setAttribute icon "slot" "icon")
      (set! (.-textContent icon) "★")
      (.appendChild el icon)
      ; slotchange fires as a microtask; defer assertion to next task
      (js/setTimeout
       (fn []
         (is (.hasAttribute el "has-icon"))
         (done))
       0))))

(deftest property-value-reflects-attribute-test
  (let [el (append! (make-item "initial"))]
    (is (= "initial" (.-value el)))
    (set! (.-value el) "changed")
    (is (= "changed" (.getAttribute el "value")))
    (is (= "changed" (.-value el)))))

(deftest property-disabled-reflects-attribute-test
  (let [el (append! (make-item "a"))]
    (is (= false (.-disabled el)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el "disabled"))
    (is (= true (.-disabled el)))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el "disabled")))))

(deftest event-bubbles-and-composed-test
  (let [el (append! (make-item "bubble-val" "Bubbles"))
        received (atom nil)]
    (.addEventListener (.-body js/document) model/event-item-select
                       (fn [e] (reset! received (.-detail e))))
    (.click el)
    (.removeEventListener (.-body js/document) model/event-item-select
                          (fn [e] (reset! received (.-detail e))))
    (is (some? @received))
    (is (= "bubble-val" (.-value @received)))))
