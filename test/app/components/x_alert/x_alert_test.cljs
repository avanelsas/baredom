(ns app.components.x-alert.x-alert-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-alert.x-alert :as x]
            [app.components.x-alert.model   :as model]))

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

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=icon]")))
    (is (some? (shadow-part el "slot[name=icon]")))
    (is (some? (shadow-part el "[part=default-icon]")))
    (is (some? (shadow-part el "[part=text]")))
    (is (some? (shadow-part el "[part=dismiss]")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")
        ^js icon      (shadow-part el "[part=icon]")
        ^js dismiss   (shadow-part el "[part=dismiss]")]
    (is (= "info"   (.getAttribute el "data-type")))
    (is (= "status" (.getAttribute container "role")))
    (is (= "inline" (.. icon -style -display)))
    (is (= ""       (.-textContent (shadow-part el "[part=text]"))))
    (is (= "inline-flex" (.. dismiss -style -display)))
    (is (false? (.-disabled dismiss)))))

;; ── type attribute ───────────────────────────────────────────────────────────
(deftest type-attribute-test
  (testing "success"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-type "success")
      (is (= "success" (.getAttribute el "data-type")))
      (is (= "status"  (.getAttribute (shadow-part el "[part=container]") "role")))))

  (testing "error sets role=alert"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-type "error")
      (is (= "alert" (.getAttribute (shadow-part el "[part=container]") "role")))))

  (testing "warning sets role=alert"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-type "warning")
      (is (= "alert" (.getAttribute (shadow-part el "[part=container]") "role"))))))

;; ── text attribute ───────────────────────────────────────────────────────────
(deftest text-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-text "Hello world")
    (is (= "Hello world"
           (.-textContent (shadow-part el "[part=text]"))))))

;; ── icon modes ───────────────────────────────────────────────────────────────
(deftest icon-default-shows-type-glyph-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-type "info")
    (let [^js di (shadow-part el "[part=default-icon]")]
      (is (not= "" (.-textContent di))))))

(deftest icon-custom-shows-custom-glyph-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-icon "★")
    (is (= "★" (.-textContent (shadow-part el "[part=default-icon]"))))))

(deftest icon-hidden-when-icon-none-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-icon "none")
    (is (= "none" (.. (shadow-part el "[part=icon]") -style -display)))))

;; ── dismissible ──────────────────────────────────────────────────────────────
(deftest dismissible-false-hides-button-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-dismissible "false")
    (is (= "none" (.. (shadow-part el "[part=dismiss]") -style -display)))))

(deftest dismissible-true-shows-button-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-dismissible "")
    (is (= "inline-flex" (.. (shadow-part el "[part=dismiss]") -style -display)))))

;; ── disabled ─────────────────────────────────────────────────────────────────
(deftest disabled-state-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "true"  (.getAttribute el "aria-disabled")))
    (is (true?     (.-disabled (shadow-part el "[part=dismiss]"))))
    (is (= -1      (.-tabIndex el)))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest type-property-test
  (let [^js el (append! (make-el))]
    (set! (.-type el) "warning")
    (is (= "warning" (.getAttribute el model/attr-type)))
    (is (= "warning" (.-type el)))))

(deftest text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-text el) "Test message")
    (is (= "Test message" (.getAttribute el model/attr-text)))
    (is (= "Test message" (.-text el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest dismissible-property-test
  (let [^js el (append! (make-el))]
    (set! (.-dismissible el) false)
    (is (false? (.-dismissible el)))
    (set! (.-dismissible el) true)
    (is (true? (.-dismissible el)))))

(deftest icon-property-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-icon el)))
    (.setAttribute el model/attr-icon "★")
    (is (= "★" (.-icon el)))
    (.removeAttribute el model/attr-icon)
    (is (nil? (.-icon el)))))

(deftest timeout-ms-property-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-timeoutMs el)))
    (set! (.-timeoutMs el) 5000)
    (is (= "5000" (.getAttribute el model/attr-timeout-ms)))
    (is (= 5000 (.-timeoutMs el)))
    (set! (.-timeoutMs el) nil)
    (is (not (.hasAttribute el model/attr-timeout-ms)))))

;; ── x-alert-dismiss event on button click ────────────────────────────────────
(deftest dismiss-event-fires-on-click-test
  (let [^js el      (append! (make-el))
        _           (.setAttribute el model/attr-text "A message")
        _           (.setAttribute el model/attr-type "info")
        events      (atom [])
        _           (.addEventListener el model/event-dismiss
                                       (fn [^js e] (swap! events conj (.-detail e))))
        ^js btn     (shadow-part el "[part=dismiss]")]
    (.click btn)
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (= "info"   (.-type d)))
      (is (= "button" (.-reason d))))))

(deftest dismiss-event-is-cancelable-test
  (let [^js el (append! (make-el))]
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (.preventDefault e)))
    (.click (shadow-part el "[part=dismiss]"))
    ;; Element should still be in DOM since event was cancelled
    (is (.-isConnected el))))

;; ── Keyboard: Escape dismisses ────────────────────────────────────────────────
(deftest escape-key-dismisses-test
  (let [^js el (append! (make-el))
        events (atom [])
        _      (.addEventListener el model/event-dismiss
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (= 1 (count @events)))
    (is (= "keyboard" (.-reason (first @events))))))

(deftest escape-key-ignored-when-disabled-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-dismiss (fn [e] (swap! events conj e)))
    (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (= 0 (count @events)))))

(deftest escape-key-ignored-when-not-dismissible-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-dismissible "false")
    (.addEventListener el model/event-dismiss (fn [e] (swap! events conj e)))
    (.dispatchEvent el (js/KeyboardEvent. "keydown" #js {:key "Escape" :bubbles true}))
    (is (= 0 (count @events)))))

;; ── a11y: tabindex ───────────────────────────────────────────────────────────
(deftest tabindex-dismissible-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-tabIndex el)))))

(deftest tabindex-not-dismissible-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-dismissible "false")
    (is (= -1 (.-tabIndex el)))))

(deftest aria-keyshortcuts-set-when-dismissible-test
  (let [^js el (append! (make-el))]
    (is (= "Escape" (.getAttribute el "aria-keyshortcuts")))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (let [^js el (make-el)
        events (atom [])
        _      (.addEventListener el model/event-dismiss
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    ;; First connect
    (.appendChild (.-body js/document) el)
    ;; Disconnect and reconnect
    (.remove el)
    (.appendChild (.-body js/document) el)
    ;; Click dismiss — should fire exactly once despite reconnect
    (.click (shadow-part el "[part=dismiss]"))
    (is (= 1 (count @events)))))
