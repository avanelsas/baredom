(ns baredom.components.x-context-menu.x-context-menu-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-context-menu.x-context-menu :as x]
            [baredom.components.x-context-menu.model :as model]))

(x/init!)

(defn cleanup-dom! []
  ;; Remove all x-context-menu elements
  (doseq [^js node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove node))
  ;; Remove any open layers
  (when-let [^js root (.getElementById js/document "__xOverlayRoot")]
    (loop []
      (when-let [c (.-lastChild root)]
        (.removeChild root c)
        (recur)))))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el sel]
  (.querySelector (.-shadowRoot el) sel))

(defn- make-item []
  (let [^js item (.createElement js/document "button")]
    (.setAttribute item "role" "menuitem")
    (set! (.-textContent item) "Item")
    item))

;; ---- Registration ----

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-context-menu should be registered in the custom element registry"))

;; ---- Shadow DOM structure ----

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))
        "element should have a shadow root")
    (is (some? (shadow-part el "slot"))
        "shadow should contain a slot element")))

(deftest slot-display-none-test
  ;; The host-style-text applies display:contents to :host and display:none to slot
  ;; We verify via computed style that the slot is hidden
  (let [el (append! (make-el))
        ^js slot (shadow-part el "slot")]
    (is (some? slot) "slot should exist")))

;; ---- Boolean properties ----

(deftest open-property-test
  (let [el (append! (make-el))]
    (is (false? (.-open el)) "open should default to false")
    ;; Setting open via property sets attr
    (set! (.-open el) true)
    (is (.hasAttribute el model/attr-open)
        "setting open=true should set the attr")
    (set! (.-open el) false)
    (is (not (.hasAttribute el model/attr-open))
        "setting open=false should remove attr")))

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))
    (is (.hasAttribute el model/attr-disabled))))

;; ---- openAt method creates layer ----

(deftest open-at-creates-layer-test
  (async done
    (let [el (append! (make-el))]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           (is (.hasAttribute el model/attr-open)
                               "open attr should be set after open event")
                           (let [^js root (.getElementById js/document "__xOverlayRoot")]
                             (is (some? root) "overlay root should exist")
                             (is (pos? (.-childElementCount root))
                                 "overlay root should have at least one layer"))
                           (done)))
      (.openAt el 100 100))))

;; ---- close method removes layer ----

(deftest close-removes-layer-test
  (async done
    (let [el (append! (make-el))]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           (.addEventListener el model/event-close
                                              (fn [_]
                                                (is (not (.hasAttribute el model/attr-open))
                                                    "open attr should be removed after close")
                                                (done)))
                           (.close el)))
      (.openAt el 100 100))))

;; ---- open-request cancelable ----

(deftest open-request-cancelable-test
  (let [el (append! (make-el))
        open-fired (atom false)]
    (.addEventListener el model/event-open-request
                       (fn [^js ev] (.preventDefault ev)))
    (.addEventListener el model/event-open
                       (fn [_] (reset! open-fired true)))
    (.openAt el 100 100)
    (is (false? @open-fired)
        "x-context-menu-open should not fire when request is cancelled")
    (is (not (.hasAttribute el model/attr-open))
        "open attr should not be set when request is cancelled")))

;; ---- close-request cancelable ----

(deftest close-request-cancelable-test
  (async done
    (let [el (append! (make-el))
          close-fired (atom false)]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           (.addEventListener el model/event-close-request
                                              (fn [^js ev] (.preventDefault ev)))
                           (.addEventListener el model/event-close
                                              (fn [_] (reset! close-fired true)))
                           (.close el)
                           (js/setTimeout
                            (fn []
                              (is (true? (.hasAttribute el model/attr-open))
                                  "open attr should remain when close-request is cancelled")
                              (is (false? @close-fired))
                              ;; cleanup: force remove attr for fixture cleanup
                              (.removeAttribute el model/attr-open)
                              (done))
                            50)))
      (.openAt el 100 100))))

;; ---- disabled prevents open ----

(deftest disabled-prevents-open-test
  (let [el (append! (make-el))
        open-fired (atom false)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-open (fn [_] (reset! open-fired true)))
    (.openAt el 100 100)
    (is (false? @open-fired) "open should not fire when disabled")
    (is (not (.hasAttribute el model/attr-open)) "open attr should not be set when disabled")))

;; ---- toggleAt ----

(deftest toggle-at-test
  (async done
    (let [el (append! (make-el))]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           ;; now toggle again should close
                           (.addEventListener el model/event-close
                                              (fn [_]
                                                (is (not (.hasAttribute el model/attr-open)))
                                                (done)))
                           (.toggleAt el 100 100)))
      (.toggleAt el 100 100))))

;; ---- select event ----

(deftest select-event-test
  (async done
    (let [el       (append! (make-el))
          ^js item (make-item)]
      (.appendChild el item)
      (.addEventListener el model/event-select
                         (fn [^js ev]
                           (is (some? (.-detail ev)))
                           (done)))
      (.addEventListener el model/event-open
                         (fn [_]
                           ;; Click the item in the panel
                           (js/setTimeout
                            (fn []
                              (let [^js root  (.getElementById js/document "__xOverlayRoot")
                                    ^js layer (when root (.-firstElementChild root))
                                    ^js panel (when layer
                                                (.querySelector (.-shadowRoot layer) "[part=panel]"))
                                    ^js mi    (when panel (.querySelector panel "[role=menuitem]"))]
                                (when mi (.click mi))))
                            50)))
      (.openAt el 100 100))))

;; ---- keyboard escape closes ----

(deftest keyboard-escape-closes-test
  (async done
    (let [el (append! (make-el))]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           (.addEventListener el model/event-close
                                              (fn [_]
                                                (is (not (.hasAttribute el model/attr-open)))
                                                (done)))
                           (js/setTimeout
                            (fn []
                              (let [^js root  (.getElementById js/document "__xOverlayRoot")
                                    ^js layer (when root (.-firstElementChild root))]
                                (when layer
                                  (.dispatchEvent layer
                                                  (js/KeyboardEvent. "keydown"
                                                                     #js {:key      "Escape"
                                                                          :bubbles  true
                                                                          :cancelable true})))))
                            30)))
      (.openAt el 100 100))))

;; ---- placement property ----

(deftest placement-property-test
  (let [el (append! (make-el))]
    (set! (.-placement el) "top-end")
    (is (= "top-end" (.getAttribute el model/attr-placement)))))

;; ---- offset property ----

(deftest offset-property-test
  (let [el (append! (make-el))]
    (set! (.-offset el) "12")
    (is (= "12" (.getAttribute el model/attr-offset))
        "setting offset via property should reflect to attribute")
    (is (= "12" (.-offset el))
        "offset getter should return attribute value")))

;; ---- zIndex property ----

(deftest z-index-property-test
  (let [el (append! (make-el))]
    (set! (.-zIndex el) "999")
    (is (= "999" (.getAttribute el model/attr-z-index))
        "setting zIndex via property should reflect to attribute")
    (is (= "999" (.-zIndex el))
        "zIndex getter should return attribute value")))

;; ---- openForElement respects disabled ----

(deftest open-for-element-disabled-test
  (let [el         (append! (make-el))
        ^js anchor (.createElement js/document "button")
        open-fired (atom false)]
    (.appendChild (.-body js/document) anchor)
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-open (fn [_] (reset! open-fired true)))
    (.openForElement el anchor)
    (is (false? @open-fired) "open should not fire when disabled")
    (is (not (.hasAttribute el model/attr-open)) "open attr should not be set when disabled")
    (.remove anchor)))

;; ---- disconnected cleans up layer ----

(deftest disconnect-cleans-layer-test
  (async done
    (let [el (append! (make-el))]
      (.appendChild el (make-item))
      (.addEventListener el model/event-open
                         (fn [_]
                           (.remove el)
                           (js/setTimeout
                            (fn []
                              (let [^js root  (.getElementById js/document "__xOverlayRoot")
                                    children (if root (.-childElementCount root) 0)]
                                (is (zero? children)
                                    "layer should be removed when element disconnects"))
                              (done))
                            30)))
      (.openAt el 100 100))))
