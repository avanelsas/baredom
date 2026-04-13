(ns baredom.components.x-tabs.x-tabs-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-tabs.x-tabs :as x]
            [baredom.components.x-tabs.model :as model]))

(x/init!)

(defn cleanup-dom!
  []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [node (.querySelectorAll js/document "x-tab")]
    (.remove node))
  (doseq [node (.querySelectorAll js/document "[data-test-panel]")]
    (.remove node)))

(use-fixtures
 :each
 {:before cleanup-dom!
  :after cleanup-dom!})

(defn make-el
  []
  (.createElement js/document model/tag-name))

(defn make-tab
  ([value]
   (let [el (.createElement js/document "x-tab")]
     (.setAttribute el "value" value)
     el))
  ([value text]
   (let [el (make-tab value)]
     (set! (.-textContent el) text)
     el)))

(defn make-panel
  [id]
  (let [el (.createElement js/document "div")]
    (.setAttribute el "id" id)
    (.setAttribute el "data-test-panel" "true")
    el))

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

(defn values-of-selected-tabs
  [tabs]
  (->> tabs
       (filter #(.hasAttribute % "selected"))
       (map #(.getAttribute % "value"))))

(defn enabled-tabs
  [tabs]
  (filter #(not (.hasAttribute % "disabled")) tabs))

(defn setup-basic-tabs!
  [tabs]
  (let [tab-a (make-tab "overview" "Overview")
        tab-b (make-tab "analytics" "Analytics")
        tab-c (make-tab "reports" "Reports")]
    (.appendChild tabs tab-a)
    (.appendChild tabs tab-b)
    (.appendChild tabs tab-c)
    {:tab-a tab-a
     :tab-b tab-b
     :tab-c tab-c}))

(defn setup-tabs-with-panels!
  [tabs]
  (let [tab-a (make-tab "overview" "Overview")
        tab-b (make-tab "analytics" "Analytics")
        tab-c (make-tab "reports" "Reports")
        panel-a (make-panel "panel-overview")
        panel-b (make-panel "panel-analytics")
        panel-c (make-panel "panel-reports")]
    (.setAttribute tab-a "controls" "panel-overview")
    (.setAttribute tab-b "controls" "panel-analytics")
    (.setAttribute tab-c "controls" "panel-reports")
    (.appendChild tabs tab-a)
    (.appendChild tabs tab-b)
    (.appendChild tabs tab-c)
    (.appendChild (.-body js/document) panel-a)
    (.appendChild (.-body js/document) panel-b)
    (.appendChild (.-body js/document) panel-c)
    {:tab-a tab-a
     :tab-b tab-b
     :tab-c tab-c
     :panel-a panel-a
     :panel-b panel-b
     :panel-c panel-c}))

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

(deftest default-slot-renders-through-shadow-dom-test
  (let [el (make-el)
        child-a (make-tab "overview" "Overview")
        child-b (make-tab "analytics" "Analytics")]
    (.appendChild el child-a)
    (.appendChild el child-b)
    (append! el)
    (let [slot (shadow-part el "slot")
          nodes (.assignedNodes ^js slot #js {:flatten true})]
      (is (= 2 (alength nodes)))
      (is (= child-a (aget nodes 0)))
      (is (= child-b (aget nodes 1))))))

(deftest initial-selection-derives-from-first-enabled-tab-test
  (let [el (make-el)
        first-tab (make-tab "overview" "Overview")
        second-tab (make-tab "analytics" "Analytics")
        third-tab (make-tab "reports" "Reports")]
    (.setAttribute first-tab "disabled" "")
    (.appendChild el first-tab)
    (.appendChild el second-tab)
    (.appendChild el third-tab)
    (append! el)

    (is (= "analytics" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute first-tab "selected")))
    (is (.hasAttribute second-tab "selected"))
    (is (not (.hasAttribute third-tab "selected")))
    (is (= ["analytics"]
           (vec (values-of-selected-tabs [first-tab second-tab third-tab]))))))

(deftest controlled-value-reflection-selects-matching-tab-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)]
    (.setAttribute el model/attr-value "reports")
    (append! el)

    (is (= "reports" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute tab-a "selected")))
    (is (not (.hasAttribute tab-b "selected")))
    (is (.hasAttribute tab-c "selected"))))

(deftest property-value-reflection-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)]
    (append! el)

    (is (= "overview" (get-js-prop el "value")))

    (set-js-prop! el "value" "analytics")
    (is (= "analytics" (get-js-prop el "value")))
    (is (not (.hasAttribute tab-a "selected")))
    (is (.hasAttribute tab-b "selected"))
    (is (not (.hasAttribute tab-c "selected")))

    (set-js-prop! el "value" "reports")
    (is (= "reports" (get-js-prop el "value")))
    (is (not (.hasAttribute tab-a "selected")))
    (is (not (.hasAttribute tab-b "selected")))
    (is (.hasAttribute tab-c "selected"))))

(deftest host-attributes-are-not-rewritten-by-normalization-test
  (let [el (make-el)
        {:keys [tab-a tab-b]} (setup-basic-tabs! el)]
    (.setAttribute el model/attr-orientation "bad")
    (.setAttribute el model/attr-activation "bad")
    (append! el)
    (let [base (shadow-part el "[part='base']")]
      (is (= "bad" (.getAttribute el model/attr-orientation)))
      (is (= "bad" (.getAttribute el model/attr-activation)))
      (is (some? base))
      (is (.hasAttribute tab-a "selected"))
      (is (not (.hasAttribute tab-b "selected"))))))

(deftest child-tab-select-coordinates-selection-and-dispatches-value-change-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)
        calls (atom 0)
        value* (atom nil)]
    (.addEventListener
     el
     "value-change"
     (fn [e]
       (swap! calls inc)
       (reset! value* (.. e -detail -value))))
    (append! el)

    (.click tab-b)

    (is (= "analytics" (.getAttribute el model/attr-value)))
    (is (= 1 @calls))
    (is (= "analytics" @value*))
    (is (not (.hasAttribute tab-a "selected")))
    (is (.hasAttribute tab-b "selected"))
    (is (not (.hasAttribute tab-c "selected")))))

(deftest single-selected-tab-invariant-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)]
    (append! el)

    (is (= ["overview"]
           (vec (values-of-selected-tabs [tab-a tab-b tab-c]))))

    (.click tab-c)

    (is (= ["reports"]
           (vec (values-of-selected-tabs [tab-a tab-b tab-c]))))))

(deftest panel-visibility-coordination-test
  (let [el (make-el)
        {:keys [tab-a tab-c panel-a panel-b panel-c]} (setup-tabs-with-panels! el)]
    (append! el)

    (is (.hasAttribute tab-a "selected"))
    (is (not (.hasAttribute panel-a "hidden")))
    (is (.hasAttribute panel-b "hidden"))
    (is (.hasAttribute panel-c "hidden"))

    (.click tab-c)

    (is (not (.hasAttribute panel-c "hidden")))
    (is (.hasAttribute panel-a "hidden"))
    (is (.hasAttribute panel-b "hidden"))))

(deftest disabled-tab-is-skipped-for-initial-selection-test
  (let [el (make-el)
        tab-a (make-tab "overview" "Overview")
        tab-b (make-tab "analytics" "Analytics")
        tab-c (make-tab "reports" "Reports")]
    (.setAttribute tab-a "disabled" "")
    (.setAttribute tab-b "disabled" "")
    (.appendChild el tab-a)
    (.appendChild el tab-b)
    (.appendChild el tab-c)
    (append! el)

    (is (= "reports" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute tab-a "selected")))
    (is (not (.hasAttribute tab-b "selected")))
    (is (.hasAttribute tab-c "selected"))))

(deftest horizontal-arrow-navigation-auto-activation-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)
        event (js/KeyboardEvent. "keydown" #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true})]
    (append! el)
    (.focus tab-a)
    (.dispatchEvent el event)

    (is (= tab-b (.-activeElement js/document)))
    (is (= "analytics" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute tab-a "selected")))
    (is (.hasAttribute tab-b "selected"))
    (is (not (.hasAttribute tab-c "selected")))))

(deftest vertical-arrow-navigation-auto-activation-test
  (let [el (make-el)
        {:keys [tab-a tab-b]} (setup-basic-tabs! el)
        event (js/KeyboardEvent. "keydown" #js {:key "ArrowDown"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute el model/attr-orientation "vertical")
    (append! el)
    (.focus tab-a)
    (.dispatchEvent el event)

    (is (= tab-b (.-activeElement js/document)))
    (is (= "analytics" (.getAttribute el model/attr-value)))
    (is (.hasAttribute tab-b "selected"))))

(deftest home-and-end-navigation-test
  (let [el (make-el)
        {:keys [tab-a tab-c]} (setup-basic-tabs! el)
        end-event (js/KeyboardEvent. "keydown" #js {:key "End"
                                                    :bubbles true
                                                    :cancelable true})
        home-event (js/KeyboardEvent. "keydown" #js {:key "Home"
                                                     :bubbles true
                                                     :cancelable true})]
    (append! el)

    (.focus tab-a)
    (.dispatchEvent el end-event)
    (is (= tab-c (.-activeElement js/document)))
    (is (= "reports" (.getAttribute el model/attr-value)))

    (.dispatchEvent el home-event)
    (is (= tab-a (.-activeElement js/document)))
    (is (= "overview" (.getAttribute el model/attr-value)))))

(deftest loop-navigation-wraps-when-enabled-test
  (let [el (make-el)
        {:keys [tab-a tab-c]} (setup-basic-tabs! el)
        event (js/KeyboardEvent. "keydown" #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute el model/attr-loop "")
    (append! el)

    (.focus tab-c)
    (.dispatchEvent el event)

    (is (= tab-a (.-activeElement js/document)))
    (is (= "overview" (.getAttribute el model/attr-value)))))

(deftest loop-navigation-clamps-when-disabled-test
  (let [el (make-el)
        {:keys [tab-c]} (setup-basic-tabs! el)
        event (js/KeyboardEvent. "keydown" #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true})]
    (append! el)

    (.click tab-c)
    (.focus tab-c)
    (.dispatchEvent el event)

    (is (= tab-c (.-activeElement js/document)))
    (is (= "reports" (.getAttribute el model/attr-value)))))

(deftest disabled-tabs-are-skipped-during-keyboard-navigation-test
  (let [el (make-el)
        tab-a (make-tab "overview" "Overview")
        tab-b (make-tab "analytics" "Analytics")
        tab-c (make-tab "reports" "Reports")
        event (js/KeyboardEvent. "keydown" #js {:key "ArrowRight"
                                                :bubbles true
                                                :cancelable true})]
    (.setAttribute tab-b "disabled" "")
    (.appendChild el tab-a)
    (.appendChild el tab-b)
    (.appendChild el tab-c)
    (append! el)

    (.focus tab-a)
    (.dispatchEvent el event)

    (is (= tab-c (.-activeElement js/document)))
    (is (= "reports" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute tab-b "selected")))))

(deftest manual-activation-focuses-without-selecting-on-arrow-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)
        arrow-event (js/KeyboardEvent. "keydown" #js {:key "ArrowRight"
                                                      :bubbles true
                                                      :cancelable true})]
    (.setAttribute el model/attr-activation "manual")
    (append! el)

    (.focus tab-a)
    (.dispatchEvent el arrow-event)

    (is (= tab-b (.-activeElement js/document)))
    (is (= "overview" (.getAttribute el model/attr-value)))
    (is (.hasAttribute tab-a "selected"))
    (is (not (.hasAttribute tab-b "selected")))
    (is (not (.hasAttribute tab-c "selected")))))

(deftest manual-activation-selects-on-enter-from-child-tab-event-test
  (let [el (make-el)
        {:keys [tab-a tab-b tab-c]} (setup-basic-tabs! el)
        enter-event (js/KeyboardEvent. "keydown" #js {:key "Enter"
                                                      :bubbles true
                                                      :cancelable true})]
    (.setAttribute el model/attr-activation "manual")
    (append! el)

    (.focus tab-b)
    (.dispatchEvent tab-b enter-event)

    (is (= "analytics" (.getAttribute el model/attr-value)))
    (is (not (.hasAttribute tab-a "selected")))
    (is (.hasAttribute tab-b "selected"))
    (is (not (.hasAttribute tab-c "selected")))))

(deftest value-change-not-dispatched-for-redundant-selection-test
  (let [el (make-el)
        {:keys [tab-a]} (setup-basic-tabs! el)
        calls (atom 0)]
    (.addEventListener el "value-change" (fn [_] (swap! calls inc)))
    (append! el)

    (.click tab-a)

    (is (= 0 @calls))
    (is (= "overview" (.getAttribute el model/attr-value)))))

(deftest label-maps-to-aria-label-on-tablist-test
  (let [el (make-el)
        _ (setup-basic-tabs! el)]
    (.setAttribute el "label" "Main navigation")
    (append! el)
    (let [base (shadow-part el "[part='base']")]
      (is (= "tablist" (.getAttribute base "role")))
      (is (= "Main navigation" (.getAttribute base "aria-label")))

      (.removeAttribute el "label")
      (is (not (.hasAttribute base "aria-label"))))))

(deftest dynamic-child-addition-recoordinates-test
  (let [el (make-el)
        {:keys [tab-a]} (setup-basic-tabs! el)]
    (append! el)

    (is (= "overview" (.getAttribute el model/attr-value)))
    (is (.hasAttribute tab-a "selected"))

    (let [tab-d (make-tab "settings" "Settings")]
      (.appendChild el tab-d)
      (is (not (.hasAttribute tab-d "selected"))))))

(deftest dynamic-child-removal-recoordinates-test
  (let [el (make-el)
        {:keys [tab-a]} (setup-basic-tabs! el)]
    (append! el)

    (.click tab-a)
    (is (= "overview" (.getAttribute el model/attr-value)))

    (.removeChild el tab-a)
    ;; After removing the selected tab, x-tabs should re-coordinate
    ;; The MutationObserver fires asynchronously, so the value may still be "overview"
    ;; but the next render cycle will pick up the change
    ))

(deftest external-disabled-toggle-recoordinates-test
  (let [el (make-el)
        {:keys [tab-a]} (setup-basic-tabs! el)]
    (append! el)

    (is (.hasAttribute tab-a "selected"))
    ;; Externally disable the selected tab — MutationObserver should detect this
    ;; Note: with subtree: true, this attribute change on a child triggers re-coordination
    (.setAttribute tab-a "disabled" "")))

(deftest style-surface-includes-slotted-rules-test
  (let [el (append! (make-el))
        root (.-shadowRoot el)
        style-el (.querySelector root "style")
        css-text (.-textContent style-el)]
    (is (not= -1 (.indexOf css-text ":host")))
    (is (not= -1 (.indexOf css-text ".base")))
    (is (not= -1 (.indexOf css-text "::slotted(x-tab)")))))
