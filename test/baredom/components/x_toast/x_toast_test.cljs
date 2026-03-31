(ns baredom.components.x-toast.x-toast-test
  (:require [cljs.test :refer-macros [deftest is testing async use-fixtures]]
            [baredom.components.x-toast.x-toast :as x]
            [baredom.components.x-toast.model   :as model]))

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
    (is (some? (shadow-part el "[part=inner]")))
    (is (some? (shadow-part el "[part=icon]")))
    (is (some? (shadow-part el "slot[name=icon]")))
    (is (some? (shadow-part el "[part=default-icon]")))
    (is (some? (shadow-part el "[part=body]")))
    (is (some? (shadow-part el "[part=heading]")))
    (is (some? (shadow-part el "[part=message]")))
    (is (some? (shadow-part el "[part=dismiss]")))
    (is (some? (shadow-part el "[part=progress]")))
    (is (some? (shadow-part el "[part=progress-bar]")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el        (append! (make-el))
        ^js container (shadow-part el "[part=container]")
        ^js icon      (shadow-part el "[part=icon]")
        ^js dismiss   (shadow-part el "[part=dismiss]")
        ^js heading   (shadow-part el "[part=heading]")
        ^js progress  (shadow-part el "[part=progress]")]
    (is (= "info"   (.getAttribute el "data-type")))
    (is (= "status" (.getAttribute container "role")))
    (is (= "inline" (.. icon -style -display)))
    (is (= ""       (.-textContent (shadow-part el "[part=message]"))))
    (is (= "none"   (.. heading -style -display)))
    (is (= "inline-flex" (.. dismiss -style -display)))
    (is (false? (.-disabled dismiss)))
    (is (= "none"   (.. progress -style -display)))))

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

;; ── heading attribute ────────────────────────────────────────────────────────
(deftest heading-attribute-test
  (let [^js el      (append! (make-el))
        ^js heading (shadow-part el "[part=heading]")]
    (testing "absent → hidden"
      (is (= "none" (.. heading -style -display))))
    (testing "set heading → visible"
      (.setAttribute el model/attr-heading "Hello")
      (is (= "Hello" (.-textContent heading)))
      (is (not= "none" (.. heading -style -display))))
    (testing "clear heading → hidden again"
      (.removeAttribute el model/attr-heading)
      (is (= "none" (.. heading -style -display))))))

;; ── message attribute ────────────────────────────────────────────────────────
(deftest message-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-message "Body text")
    (is (= "Body text"
           (.-textContent (shadow-part el "[part=message]"))))))

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

;; ── show-progress attribute ──────────────────────────────────────────────────
(deftest show-progress-hidden-by-default-test
  (let [^js el (append! (make-el))]
    (is (= "none" (.. (shadow-part el "[part=progress]") -style -display)))))

(deftest show-progress-without-timeout-stays-hidden-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-show-progress "")
    (is (= "none" (.. (shadow-part el "[part=progress]") -style -display)))))

(deftest show-progress-with-timeout-shows-bar-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-show-progress "")
    (.setAttribute el model/attr-timeout-ms "3000")
    (is (= "block" (.. (shadow-part el "[part=progress]") -style -display)))))

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

(deftest heading-property-test
  (let [^js el (append! (make-el))]
    (set! (.-heading el) "My heading")
    (is (= "My heading" (.getAttribute el model/attr-heading)))
    (is (= "My heading" (.-heading el)))))

(deftest message-property-test
  (let [^js el (append! (make-el))]
    (set! (.-message el) "My message")
    (is (= "My message" (.getAttribute el model/attr-message)))
    (is (= "My message" (.-message el)))))

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

(deftest show-progress-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-showProgress el)))
    (set! (.-showProgress el) true)
    (is (.hasAttribute el model/attr-show-progress))
    (is (true? (.-showProgress el)))
    (set! (.-showProgress el) false)
    (is (not (.hasAttribute el model/attr-show-progress)))))

;; ── dismiss() method ─────────────────────────────────────────────────────────
(deftest dismiss-method-exists-test
  (let [^js el (append! (make-el))]
    (is (fn? (.-dismiss el)))))

(deftest dismiss-method-fires-event-test
  (let [^js el  (append! (make-el))
        _       (.setAttribute el model/attr-message "Hello")
        events  (atom [])
        _       (.addEventListener el model/event-dismiss
                                   (fn [^js e] (swap! events conj (.-detail e))))]
    (.call (.-dismiss el) el)
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (= "info" (.-type d)))
      (is (= "api"  (.-reason d))))))

(deftest dismiss-method-custom-reason-test
  (let [^js el (append! (make-el))
        events (atom [])
        _      (.addEventListener el model/event-dismiss
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    (.call (.-dismiss el) el "toaster-remove")
    (is (= 1 (count @events)))
    (is (= "toaster-remove" (.-reason (first @events))))))

(deftest dismiss-method-is-cancelable-test
  (let [^js el (append! (make-el))]
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (.preventDefault e)))
    (.call (.-dismiss el) el)
    (is (.-isConnected el))))

(deftest dismiss-method-noop-when-disconnected-test
  (let [^js el (make-el)
        events (atom [])]
    (.addEventListener el model/event-dismiss
                       (fn [e] (swap! events conj e)))
    ;; Not appended to DOM
    (.call (.-dismiss el) el)
    (is (= 0 (count @events)))))

(deftest dismiss-method-bypasses-disabled-test
  (let [^js el (append! (make-el))
        events (atom [])
        _      (.addEventListener el model/event-dismiss
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    (.setAttribute el model/attr-disabled "")
    (.call (.-dismiss el) el)
    (is (= 1 (count @events)))))

;; ── x-toast-dismiss event on button click ────────────────────────────────────
(deftest dismiss-event-fires-on-click-test
  (let [^js el      (append! (make-el))
        _           (.setAttribute el model/attr-message "A message")
        _           (.setAttribute el model/attr-heading "A heading")
        _           (.setAttribute el model/attr-type "info")
        events      (atom [])
        _           (.addEventListener el model/event-dismiss
                                       (fn [^js e] (swap! events conj (.-detail e))))
        ^js btn     (shadow-part el "[part=dismiss]")]
    (.click btn)
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (= "info"      (.-type d)))
      (is (= "button"    (.-reason d)))
      (is (= "A heading" (.-heading d)))
      (is (= "A message" (.-message d))))))

(deftest dismiss-event-is-cancelable-test
  (let [^js el (append! (make-el))]
    (.addEventListener el model/event-dismiss
                       (fn [^js e] (.preventDefault e)))
    (.click (shadow-part el "[part=dismiss]"))
    (is (.-isConnected el))))

;; ── Keyboard: Escape dismisses ───────────────────────────────────────────────
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

;; ── a11y: tabindex and aria ──────────────────────────────────────────────────
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

(deftest dismiss-button-aria-label-test
  (let [^js el (append! (make-el))]
    (is (= "Dismiss toast"
           (.getAttribute (shadow-part el "[part=dismiss]") "aria-label")))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-fires-event-once-test
  (let [^js el (make-el)
        events (atom [])
        _      (.addEventListener el model/event-dismiss
                                  (fn [^js e] (swap! events conj (.-detail e))))]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.click (shadow-part el "[part=dismiss]"))
    (is (= 1 (count @events)))))

;; ── Timeout auto-dismiss ─────────────────────────────────────────────────────
(deftest timeout-auto-dismiss-test
  ;; Use a very short timeout so the test completes quickly.
  ;; The enter animation is suppressed by setting --x-toast-enter-duration to 0
  ;; so the timeout fires almost immediately after connection.
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-timeout-ms "80")
      ;; Suppress enter animation so timeout starts immediately
      (.setAttribute el "style" "--x-toast-enter-duration:0ms;")
      (let [events (atom [])]
        (.addEventListener el model/event-dismiss
                           (fn [^js e] (swap! events conj (.-detail e))))
        (append! el)
        ;; Fire the enter animation end manually since duration is 0
        ;; (animationend won't fire for 0-duration animations)
        (let [^js container (shadow-part el "[part=container]")]
          (.dispatchEvent container
                         (js/AnimationEvent. "animationend"
                                            #js {:bubbles true})))
        (js/setTimeout
         (fn []
           (is (= 1 (count @events)))
           (is (= "timeout" (.-reason (first @events))))
           (done))
         200)))))

(deftest timeout-not-scheduled-when-not-dismissible-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-timeout-ms "50")
      (.setAttribute el model/attr-dismissible "false")
      (.setAttribute el "style" "--x-toast-enter-duration:0ms;")
      (let [events (atom [])]
        (.addEventListener el model/event-dismiss
                           (fn [^js e] (swap! events conj (.-detail e))))
        (append! el)
        (let [^js container (shadow-part el "[part=container]")]
          (.dispatchEvent container
                         (js/AnimationEvent. "animationend"
                                            #js {:bubbles true})))
        (js/setTimeout
         (fn []
           (is (= 0 (count @events)))
           (done))
         150)))))

;; ── Progress bar activation ──────────────────────────────────────────────────
(deftest progress-bar-activates-after-enter-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-timeout-ms "3000")
      (.setAttribute el model/attr-show-progress "")
      (append! el)
      ;; Before enter animation ends, progress should not be active
      (is (not (.hasAttribute el "data-progress-active")))
      ;; Simulate enter animation completing
      (let [^js container (shadow-part el "[part=container]")]
        (.dispatchEvent container
                       (js/AnimationEvent. "animationend"
                                          #js {:bubbles true})))
      ;; After enter, progress should be active
      (js/setTimeout
       (fn []
         (is (.hasAttribute el "data-progress-active"))
         (done))
       20))))
