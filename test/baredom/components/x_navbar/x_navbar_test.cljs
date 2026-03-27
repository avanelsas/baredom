(ns baredom.components.x-navbar.x-navbar-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing async]]
            [baredom.components.x-navbar.x-navbar :as x-navbar]
            [baredom.components.x-navbar.model :as model]))

(x-navbar/init!)

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

(defn dispatch-click!
  [el]
  (.dispatchEvent
   el
   (js/MouseEvent.
    "click"
    #js {:bubbles true
         :composed true
         :cancelable true})))

(defn next-tick
  [f]
  (js/setTimeout f 0))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest boolean-property-reflection-test
  (let [el (append! (make-el))]
    (set-js-prop! el "sticky" true)
    (is (.hasAttribute el model/attr-sticky))
    (is (= true (get-js-prop el "sticky")))
    (set-js-prop! el "sticky" false)
    (is (not (.hasAttribute el model/attr-sticky)))
    (is (= false (get-js-prop el "sticky")))

    (set-js-prop! el "elevated" true)
    (is (.hasAttribute el model/attr-elevated))
    (is (= true (get-js-prop el "elevated")))
    (set-js-prop! el "elevated" false)
    (is (not (.hasAttribute el model/attr-elevated)))
    (is (= false (get-js-prop el "elevated")))))

(deftest normalized-host-attributes-are-not-rewritten-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")
        bar (shadow-part el "[part='bar']")]
    (.setAttribute el model/attr-orientation "bad")
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-breakpoint "bad")
    (.setAttribute el model/attr-alignment "bad")

    (is (= "bad" (.getAttribute el model/attr-orientation)))
    (is (= "bad" (.getAttribute el model/attr-variant)))
    (is (= "bad" (.getAttribute el model/attr-breakpoint)))
    (is (= "bad" (.getAttribute el model/attr-alignment)))

    (is (= "horizontal" (.getAttribute base "data-orientation")))
    (is (= "default" (.getAttribute base "data-variant")))
    (is (= "md" (.getAttribute base "data-breakpoint")))
    (is (= "space-between" (.getAttribute bar "data-alignment")))))

(deftest landmark-label-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-label "Main navigation")
    (is (= "Main navigation" (.getAttribute base "aria-label")))))

(deftest navigate-event-test
  (let [el (append! (make-el))
        link (.createElement js/document "a")
        seen (atom nil)]
    (.setAttribute link "href" "#docs")
    (set! (.-textContent link) "Docs")

    (.addEventListener
     link
     "click"
     (fn [event]
       (.preventDefault event)))

    (.appendChild el link)

    (.addEventListener
     el
     model/event-navigate
     (fn [event]
       (reset! seen (js->clj (.-detail event) :keywordize-keys true))))

    (dispatch-click! link)

    (is (= "#docs" (:href @seen)))
    (is (= "pointer" (:source @seen)))))

(deftest brand-activate-event-test
  (let [el (append! (make-el))
        brand (.createElement js/document "a")
        seen (atom nil)]
    (.setAttribute brand "slot" model/slot-brand)
    (.setAttribute brand "href" "#home")
    (set! (.-textContent brand) "Acme")

    (.addEventListener
     brand
     "click"
     (fn [event]
       (.preventDefault event)))

    (.appendChild el brand)

    (.addEventListener
     el
     model/event-brand-activate
     (fn [event]
       (reset! seen (js->clj (.-detail event) :keywordize-keys true))))

    (dispatch-click! brand)

    (is (= {:source "pointer"} @seen))))

(deftest slot-presence-markers-test
  (async done
    (let [el (append! (make-el))
          brand (.createElement js/document "span")
          action (.createElement js/document "button")
          toggle (.createElement js/document "button")
          brand-part (shadow-part el "[part='brand']")
          actions-part (shadow-part el "[part='actions']")
          toggle-part (shadow-part el "[part='toggle']")]
      (.setAttribute brand "slot" model/slot-brand)
      (set! (.-textContent brand) "Acme")

      (.setAttribute action "slot" model/slot-actions)
      (.setAttribute action "type" "button")
      (set! (.-textContent action) "Login")

      (.setAttribute toggle "slot" model/slot-toggle)
      (.setAttribute toggle "type" "button")
      (set! (.-textContent toggle) "Menu")

      (.appendChild el brand)
      (.appendChild el action)
      (.appendChild el toggle)

      (next-tick
       (fn []
         (is (= "true" (.getAttribute brand-part "data-has-brand")))
         (is (= "true" (.getAttribute actions-part "data-has-actions")))
         (is (= "true" (.getAttribute toggle-part "data-has-toggle")))
         (done))))))

(deftest height-and-alignment-token-surfaces-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")
        bar (shadow-part el "[part='bar']")]
    (.setProperty (.-style el) "--x-navbar-height" "72px")
    (.setProperty (.-style el) "--x-navbar-align-items" "end")
    (is (some? base))
    (is (some? bar))))

(deftest no-render-loop-on-normalization-test
  (let [el (append! (make-el))
        base (shadow-part el "[part='base']")
        bar (shadow-part el "[part='bar']")]
    (.setAttribute el model/attr-variant "oops")
    (.setAttribute el model/attr-alignment "oops")
    (.setAttribute el model/attr-breakpoint "oops")

    (is (= "default" (.getAttribute base "data-variant")))
    (is (= "space-between" (.getAttribute bar "data-alignment")))
    (is (= "md" (.getAttribute base "data-breakpoint")))))
