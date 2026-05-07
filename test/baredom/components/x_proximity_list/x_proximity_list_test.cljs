(ns baredom.components.x-proximity-list.x-proximity-list-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-proximity-list.x-proximity-list :as x]
            [baredom.components.x-proximity-list.model            :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn ^js make-list-with-items
  "Create a proximity-list with n button children."
  [n]
  (let [el (make-el)]
    (dotimes [i n]
      (let [btn (.createElement js/document "button")]
        (set! (.-textContent btn) (str "Item " i))
        (.appendChild el btn)))
    el))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=track]")))
    (is (some? (shadow-part el "slot")))))

;; ── A11y default role ───────────────────────────────────────────────────────
(deftest default-role-test
  (let [^js el (append! (make-el))]
    (is (= "list" (.getAttribute el "role")))))

(deftest preserves-existing-role-test
  (let [^js el (make-el)]
    (.setAttribute el "role" "menu")
    (append! el)
    (is (= "menu" (.getAttribute el "role")))))

;; ── Direction layout ────────────────────────────────────────────────────────
(deftest direction-default-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el model/attr-direction)))
    (is (= "horizontal" (.-direction el)))))

(deftest direction-vertical-flex-direction-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-direction "vertical")
    (append! el)
    (let [^js track (shadow-part el "[part=track]")
          style     (.getComputedStyle js/window track)]
      (is (= "column" (.getPropertyValue style "flex-direction"))))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest direction-property-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.-direction el)))
    (set! (.-direction el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-direction)))
    (is (= "vertical" (.-direction el)))))

(deftest radius-property-test
  (let [^js el (append! (make-el))]
    (is (= 120 (.-radius el)))
    (set! (.-radius el) 200)
    (is (= "200" (.getAttribute el model/attr-radius)))
    (is (= 200 (.-radius el)))))

(deftest max-scale-property-test
  (let [^js el (append! (make-el))]
    (is (= 1.5 (.-maxScale el)))
    (set! (.-maxScale el) 2.0)
    (is (= "2" (.getAttribute el model/attr-max-scale)))
    (is (= 2.0 (.-maxScale el)))))

(deftest lift-property-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-lift el)))
    (set! (.-lift el) 12)
    (is (= "12" (.getAttribute el model/attr-lift)))
    (is (= 12 (.-lift el)))))

(deftest gap-property-test
  (let [^js el (append! (make-el))]
    (is (= 8 (.-gap el)))
    (set! (.-gap el) 16)
    (is (= "16" (.getAttribute el model/attr-gap)))
    (is (= 16 (.-gap el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Gap CSS variable ────────────────────────────────────────────────────────
(deftest gap-css-var-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-gap "20")
    (append! el)
    (is (= "20px" (.. el -style (getPropertyValue model/css-gap))))))

;; ── Slotchange assigns tabindex ─────────────────────────────────────────────
(deftest tabindex-applied-test
  (async done
    (let [^js el (append! (make-list-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [^js items (.-children el)]
              (is (= "0" (.getAttribute (aget items 0) "tabindex")))
              (is (= "0" (.getAttribute (aget items 1) "tabindex")))
              (is (= "0" (.getAttribute (aget items 2) "tabindex"))))
            (done))))))))

(deftest tabindex-preserved-test
  (async done
    (let [^js el (make-el)
          ^js b1 (.createElement js/document "button")
          ^js b2 (.createElement js/document "button")]
      (.setAttribute b1 "tabindex" "-1")
      (.appendChild el b1)
      (.appendChild el b2)
      (append! el)
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (is (= "-1" (.getAttribute b1 "tabindex"))
                "Existing tabindex should be preserved")
            (is (= "0"  (.getAttribute b2 "tabindex"))
                "Missing tabindex should be set to 0")
            (done))))))))

;; ── Click select event ──────────────────────────────────────────────────────
(deftest select-event-on-click-test
  (async done
    (let [^js el       (append! (make-list-with-items 3))
          fired?       (volatile! false)
          received-idx (volatile! nil)
          received-src (volatile! nil)]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [^js e]
                                 (vreset! fired? true)
                                 (vreset! received-idx (.. e -detail -index))
                                 (vreset! received-src (.. e -detail -source))))
            (let [^js items (.-children el)
                  ^js btn   (aget items 1)]
              (.click btn))
            (js/setTimeout
             (fn []
               (is @fired? "x-proximity-list-select should fire on click")
               (is (= 1 @received-idx))
               (is (= "pointer" @received-src))
               (done))
             20))))))))

;; ── Disabled prevents select ────────────────────────────────────────────────
(deftest disabled-prevents-select-test
  (async done
    (let [^js el (append! (make-list-with-items 3))
          fired? (volatile! false)]
      (.setAttribute el model/attr-disabled "")
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [_] (vreset! fired? true)))
            (let [^js items (.-children el)
                  ^js btn   (aget items 0)]
              (.click btn))
            (js/setTimeout
             (fn []
               (is (not @fired?)
                   "select should NOT fire when disabled")
               (done))
             20))))))))

;; ── Keyboard arrow navigation ───────────────────────────────────────────────
(deftest keyboard-arrow-right-test
  (async done
    (let [^js el (append! (make-list-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [^js items (.-children el)]
              (.dispatchEvent el
                              (js/KeyboardEvent. "keydown"
                                                 #js {:key "ArrowRight" :bubbles true}))
              (.dispatchEvent el
                              (js/KeyboardEvent. "keydown"
                                                 #js {:key "ArrowRight" :bubbles true}))
              (js/setTimeout
               (fn []
                 (is (= (aget items 1) (.-activeElement js/document))
                     "Two ArrowRight presses should focus the second item")
                 (done))
               20)))))))))

(deftest keyboard-vertical-arrow-test
  (async done
    (let [^js el (make-list-with-items 3)]
      (.setAttribute el model/attr-direction "vertical")
      (append! el)
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [^js items (.-children el)]
              (.dispatchEvent el
                              (js/KeyboardEvent. "keydown"
                                                 #js {:key "ArrowDown" :bubbles true}))
              (js/setTimeout
               (fn []
                 (is (= (aget items 0) (.-activeElement js/document))
                     "ArrowDown in vertical mode should focus first item")
                 (done))
               20)))))))))

(deftest keyboard-enter-select-test
  (async done
    (let [^js el (append! (make-list-with-items 3))
          fired? (volatile! false)]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [^js e]
                                 (vreset! fired? true)
                                 (is (= "keyboard" (.. e -detail -source)))))
            (.dispatchEvent el
                            (js/KeyboardEvent. "keydown"
                                               #js {:key "ArrowRight" :bubbles true}))
            (js/setTimeout
             (fn []
               (.dispatchEvent el
                               (js/KeyboardEvent. "keydown"
                                                  #js {:key "Enter" :bubbles true}))
               (js/setTimeout
                (fn []
                  (is @fired? "Enter on focused item should dispatch select event")
                  (done))
                20))
             20))))))))

;; ── Reconnect ───────────────────────────────────────────────────────────────
(deftest reconnect-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (some? (.-shadowRoot el)))))
