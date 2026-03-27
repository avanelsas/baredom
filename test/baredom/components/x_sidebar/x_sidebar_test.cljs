(ns baredom.components.x-sidebar.x-sidebar-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-sidebar.model :as model]
            [baredom.components.x-sidebar.x-sidebar :as sidebar]))

(defn cleanup-dom!
  []
  (doseq [el (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove el)))

(use-fixtures :each
              {:before (fn [] nil)
               :after cleanup-dom!})

(defn make-element!
  []
  (sidebar/init!)
  (let [el (.createElement js/document model/tag-name)]
    (.appendChild (.-body js/document) el)
    el))

(deftest custom-element-registers-test
  (testing "element definition exists"
    (sidebar/init!)
    (is (some? (.get js/customElements model/tag-name)))))

(deftest default-closed-state-test
  (testing "connected callback does not force open by default"
    (let [el (make-element!)]
      (is (false? (.hasAttribute el model/attr-open)))
      (is (false? (.-open el))))))

(deftest property-reflection-test
  (testing "open and collapsed properties reflect to attributes"
    (let [el (make-element!)]
      ;; collapsed
      (set! (.-collapsed el) true)
      (is (.hasAttribute el model/attr-collapsed))
      (is (true? (.-collapsed el)))
      (set! (.-collapsed el) false)
      (is (false? (.hasAttribute el model/attr-collapsed)))
      (is (false? (.-collapsed el)))

      ;; open
      (is (false? (.hasAttribute el model/attr-open)))
      (is (false? (.-open el)))
      (set! (.-open el) true)
      (is (.hasAttribute el model/attr-open))
      (is (true? (.-open el)))
      (set! (.-open el) false)
      (is (false? (.hasAttribute el model/attr-open)))
      (is (false? (.-open el))))))

(deftest slot-lives-inside-panel-test
  (testing "slot is inside animated panel"
    (let [el (make-element!)
          root (.-shadowRoot el)
          panel (.querySelector root ".panel")
          slot (.querySelector root "slot")]
      (is (= panel (.-parentElement slot))))))

(deftest toggle-event-fires-on-programmatic-open-and-close-test
  (testing "toggle fires when open changes due to property change"
    (let [el (make-element!)
          events (atom [])]
      (.addEventListener el model/event-toggle
                         (fn [event]
                           (swap! events conj (js->clj (.-detail event) :keywordize-keys true))))
      (set! (.-open el) true)
      (set! (.-open el) false)
      (is (= [{:open true}
              {:open false}]
             @events)))))

(deftest dismiss-event-fires-on-backdrop-click-test
  (testing "dismiss then toggle fire for overlay backdrop click"
    (let [el (make-element!)
          all-events (atom [])
          root (.-shadowRoot el)
          backdrop (.querySelector root ".backdrop")]
      (.setAttribute el model/attr-variant model/variant-overlay)
      (set! (.-open el) true)

      (.addEventListener el model/event-dismiss
                         (fn [event]
                           (swap! all-events conj {:name model/event-dismiss
                                                   :detail (js->clj (.-detail event) :keywordize-keys true)})))
      (.addEventListener el model/event-toggle
                         (fn [event]
                           (swap! all-events conj {:name model/event-toggle
                                                   :detail (js->clj (.-detail event) :keywordize-keys true)})))

      (.dispatchEvent backdrop (js/MouseEvent. "click" #js {:bubbles true}))

      (is (= [{:name "dismiss" :detail {:reason "backdrop"}}
              {:name "toggle" :detail {:open false}}]
             @all-events)))))

(deftest dismiss-event-fires-on-escape-test
  (testing "dismiss then toggle fire for escape key"
    (let [el (make-element!)
          all-events (atom [])
          root (.-shadowRoot el)]
      (.setAttribute el model/attr-variant model/variant-overlay)
      (set! (.-open el) true)

      (.addEventListener el model/event-dismiss
                         (fn [event]
                           (swap! all-events conj {:name model/event-dismiss
                                                   :detail (js->clj (.-detail event) :keywordize-keys true)})))
      (.addEventListener el model/event-toggle
                         (fn [event]
                           (swap! all-events conj {:name model/event-toggle
                                                   :detail (js->clj (.-detail event) :keywordize-keys true)})))

      (.dispatchEvent root (js/KeyboardEvent. "keydown" #js {:key "Escape"
                                                             :bubbles true}))

      (is (= [{:name "dismiss" :detail {:reason "escape"}}
              {:name "toggle" :detail {:open false}}]
             @all-events)))))

(deftest aria-hidden-updates-test
  (testing "aria-hidden reflects open state"
    (let [el (make-element!)
          sidebar-el (.querySelector (.-shadowRoot el) ".sidebar")]
      (is (= "true" (.getAttribute sidebar-el "aria-hidden")))
      (set! (.-open el) true)
      (is (= "false" (.getAttribute sidebar-el "aria-hidden")))
      (set! (.-open el) false)
      (is (= "true" (.getAttribute sidebar-el "aria-hidden"))))))

(deftest host-state-projection-test
  (testing "host data attributes mirror computed state"
    (let [el (make-element!)]
      (.setAttribute el model/attr-breakpoint "0")
      (.setAttribute el model/attr-placement model/placement-right)
      (.setAttribute el model/attr-variant model/variant-overlay)
      (is (= "right" (.getAttribute el "data-placement")))
      (is (= "overlay" (.getAttribute el "data-effective-variant"))))))

(deftest small-viewport-forces-modal-test
  (testing "viewport below breakpoint forces modal"
    (let [orig-inner-width (.-innerWidth js/window)
          el (make-element!)]
      (try
        (js/Object.defineProperty js/window "innerWidth" #js {:value 375
                                                              :configurable true})
        (.setAttribute el model/attr-variant model/variant-docked)
        (.setAttribute el model/attr-collapsed "")
        (is (= "modal" (.getAttribute el "data-effective-variant")))
        (is (= "false" (.getAttribute el "data-collapsed")))
        (finally
          (js/Object.defineProperty js/window "innerWidth" #js {:value orig-inner-width
                                                                :configurable true}))))))

(deftest overlay-backdrop-activates-when-open-test
  (testing "overlay variant shows backdrop when open"
    (let [el (make-element!)
          root (.-shadowRoot el)
          backdrop (.querySelector root ".backdrop")]
      (.setAttribute el model/attr-variant model/variant-overlay)
      (set! (.-open el) true)
      (is (false? (.-hidden backdrop)))
      (is (= "true" (.getAttribute backdrop "data-active"))))))
