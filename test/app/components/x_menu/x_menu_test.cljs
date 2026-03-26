(ns app.components.x-menu.x-menu-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing]]
            [app.components.x-menu.x-menu :as x]
            [app.components.x-menu.model :as model]
            [app.components.x-menu-item.x-menu-item :as x-item]
            [app.components.x-menu-item.model :as item-model]))

(x/init!)
(x-item/init!)

(defn cleanup-dom! []
  (doseq [node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove node))
  (doseq [node (array-seq (.querySelectorAll js/document item-model/tag-name))]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-menu []
  (.createElement js/document model/tag-name))

(defn make-trigger []
  (let [btn (.createElement js/document "button")]
    (.setAttribute btn "slot" "trigger")
    (set! (.-textContent btn) "Open")
    btn))

(defn make-item
  ([value text]
   (let [el (.createElement js/document item-model/tag-name)]
     (.setAttribute el "value" value)
     (set! (.-textContent el) text)
     el))
  ([value text opts]
   (let [el (make-item value text)]
     (when (:disabled opts) (.setAttribute el "disabled" ""))
     (when (:variant opts) (.setAttribute el "variant" (:variant opts)))
     (when (:type opts) (.setAttribute el "type" (:type opts)))
     el)))

(defn make-divider []
  (let [el (.createElement js/document item-model/tag-name)]
    (.setAttribute el "type" "divider")
    el))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-part [el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn setup-menu! []
  (let [menu (make-menu)
        trigger (make-trigger)
        item-a (make-item "a" "Item A")
        item-b (make-item "b" "Item B")
        item-c (make-item "c" "Item C")]
    (.appendChild menu trigger)
    (.appendChild menu item-a)
    (.appendChild menu item-b)
    (.appendChild menu item-c)
    (append! menu)
    {:menu menu :trigger trigger :item-a item-a :item-b item-b :item-c item-c}))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest renders-shadow-structure-test
  (let [menu (append! (make-menu))
        root (.-shadowRoot menu)]
    (is (some? root))
    (is (some? (shadow-part menu "[part='popup']")))
    (is (some? (.querySelector root "slot[name='trigger']")))
    (is (some? (.querySelector root "slot:not([name])")))))

(deftest popup-has-role-menu-test
  (let [menu (append! (make-menu))
        popup (shadow-part menu "[part='popup']")]
    (is (= "menu" (.getAttribute popup "role")))))

(deftest closed-by-default-test
  (let [{:keys [menu]} (setup-menu!)]
    (is (not (.hasAttribute menu "open")))))

(deftest opens-on-trigger-click-test
  (let [{:keys [menu trigger]} (setup-menu!)
        events (atom [])]
    (.addEventListener menu model/event-open (fn [e] (swap! events conj (.-type e))))
    (.click trigger)
    (is (.hasAttribute menu "open"))
    (is (= [model/event-open] @events))))

(deftest closes-on-trigger-click-when-open-test
  (let [{:keys [menu trigger]} (setup-menu!)
        close-events (atom [])]
    (.setAttribute menu "open" "")
    (.addEventListener menu model/event-close (fn [e] (swap! close-events conj e)))
    (.click trigger)
    (is (not (.hasAttribute menu "open")))
    (is (= 1 (count @close-events)))))

(deftest closes-on-escape-test
  (let [{:keys [menu trigger]} (setup-menu!)
        close-events (atom [])
        evt (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.addEventListener menu model/event-close (fn [e] (swap! close-events conj e)))
    (.dispatchEvent menu evt)
    (is (not (.hasAttribute menu "open")))
    (is (= 1 (count @close-events)))))

(deftest escape-ignored-when-closed-test
  (let [{:keys [menu]} (setup-menu!)
        close-events (atom [])
        evt (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true :cancelable true})]
    (.addEventListener menu model/event-close (fn [_] (swap! close-events conj true)))
    (.dispatchEvent menu evt)
    (is (= 0 (count @close-events)))))

(deftest closes-on-item-select-and-dispatches-x-menu-select-test
  (let [{:keys [menu item-a]} (setup-menu!)
        select-events (atom [])
        close-events (atom [])]
    (.setAttribute menu "open" "")
    (.addEventListener menu model/event-select (fn [e] (swap! select-events conj (.-detail e))))
    (.addEventListener menu model/event-close (fn [_] (swap! close-events conj true)))
    (.click item-a)
    (is (not (.hasAttribute menu "open")))
    (is (= 1 (count @select-events)))
    (is (= "a" (.-value (first @select-events))))
    (is (= 1 (count @close-events)))))

(deftest item-select-event-stopped-at-menu-test
  (let [{:keys [menu item-a]} (setup-menu!)
        body-received (atom false)]
    (.setAttribute menu "open" "")
    (.addEventListener (.-body js/document) item-model/event-item-select
                       (fn [_] (reset! body-received true)))
    (.click item-a)
    (.removeEventListener (.-body js/document) item-model/event-item-select
                          (fn [_] (reset! body-received true)))
    (is (= false @body-received))))

(deftest keyboard-nav-arrow-down-focuses-first-item-test
  (let [{:keys [menu trigger item-a]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.dispatchEvent menu evt)
    (is (= item-a (.-activeElement js/document)))))

(deftest keyboard-nav-arrow-down-moves-to-next-test
  (let [{:keys [menu item-a item-b item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.focus item-a)
    (.dispatchEvent menu evt)
    (is (= item-b (.-activeElement js/document)))))

(deftest keyboard-nav-arrow-up-wraps-to-last-test
  (let [{:keys [menu item-a item-b item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowUp" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.focus item-a)
    (.dispatchEvent menu evt)
    (is (= item-c (.-activeElement js/document)))))

(deftest keyboard-nav-arrow-down-wraps-to-first-test
  (let [{:keys [menu item-a item-b item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.focus item-c)
    (.dispatchEvent menu evt)
    (is (= item-a (.-activeElement js/document)))))

(deftest keyboard-nav-home-focuses-first-test
  (let [{:keys [menu item-a item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "Home" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.focus item-c)
    (.dispatchEvent menu evt)
    (is (= item-a (.-activeElement js/document)))))

(deftest keyboard-nav-end-focuses-last-test
  (let [{:keys [menu item-a item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "End" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.focus item-a)
    (.dispatchEvent menu evt)
    (is (= item-c (.-activeElement js/document)))))

(deftest keyboard-nav-skips-disabled-items-test
  (let [menu (make-menu)
        trigger (make-trigger)
        item-a (make-item "a" "Item A")
        item-b (make-item "b" "Item B" {:disabled true})
        item-c (make-item "c" "Item C")
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.appendChild menu trigger)
    (.appendChild menu item-a)
    (.appendChild menu item-b)
    (.appendChild menu item-c)
    (append! menu)
    (.setAttribute menu "open" "")
    (.focus item-a)
    (.dispatchEvent menu evt)
    (is (= item-c (.-activeElement js/document)))))

(deftest keyboard-nav-skips-dividers-test
  (let [menu (make-menu)
        trigger (make-trigger)
        item-a (make-item "a" "Item A")
        divider (make-divider)
        item-c (make-item "c" "Item C")
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.appendChild menu trigger)
    (.appendChild menu item-a)
    (.appendChild menu divider)
    (.appendChild menu item-c)
    (append! menu)
    (.setAttribute menu "open" "")
    (.focus item-a)
    (.dispatchEvent menu evt)
    (is (= item-c (.-activeElement js/document)))))

(deftest placement-attribute-reflected-to-popup-test
  (let [menu (make-menu)
        popup nil]
    (.setAttribute menu "placement" "top-end")
    (append! menu)
    (let [popup (shadow-part menu "[part='popup']")]
      (is (= "top-end" (.getAttribute popup "data-placement"))))))

(deftest placement-defaults-to-bottom-start-test
  (let [menu (append! (make-menu))
        popup (shadow-part menu "[part='popup']")]
    (is (= "bottom-start" (.getAttribute popup "data-placement")))))

(deftest label-sets-aria-label-on-popup-test
  (let [menu (make-menu)
        popup nil]
    (.setAttribute menu "label" "Actions")
    (append! menu)
    (let [popup (shadow-part menu "[part='popup']")]
      (is (= "Actions" (.getAttribute popup "aria-label"))))))

(deftest open-attribute-property-round-trip-test
  (let [menu (append! (make-menu))]
    (is (= false (.-open menu)))
    (set! (.-open menu) true)
    (is (.hasAttribute menu "open"))
    (is (= true (.-open menu)))
    (set! (.-open menu) false)
    (is (not (.hasAttribute menu "open")))))

(deftest arrow-down-on-closed-menu-opens-and-focuses-first-test
  (let [{:keys [menu item-a]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowDown" :bubbles true :cancelable true})]
    (.dispatchEvent menu evt)
    (is (.hasAttribute menu "open"))
    (is (= item-a (.-activeElement js/document)))))

(deftest arrow-up-on-closed-menu-opens-and-focuses-last-test
  (let [{:keys [menu item-c]} (setup-menu!)
        evt (js/KeyboardEvent. "keydown" #js {:key "ArrowUp" :bubbles true :cancelable true})]
    (.dispatchEvent menu evt)
    (is (.hasAttribute menu "open"))
    (is (= item-c (.-activeElement js/document)))))

(deftest tab-closes-menu-test
  (let [{:keys [menu]} (setup-menu!)
        close-events (atom [])
        evt (js/KeyboardEvent. "keydown" #js {:key "Tab" :bubbles true :cancelable true})]
    (.setAttribute menu "open" "")
    (.addEventListener menu model/event-close (fn [_] (swap! close-events conj true)))
    (.dispatchEvent menu evt)
    (is (not (.hasAttribute menu "open")))
    (is (= 1 (count @close-events)))))
