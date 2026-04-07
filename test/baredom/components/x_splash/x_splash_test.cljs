(ns baredom.components.x-splash.x-splash-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-splash.x-splash :as x]
            [baredom.components.x-splash.model   :as model]))

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
    (is (some? (shadow-part el "[part=overlay]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "slot")))
    (is (some? (shadow-part el "[part=spinner]")))
    (is (some? (shadow-part el "[part=progress]")))
    (is (some? (shadow-part el "[part=bar]")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el          (append! (make-el))
        ^js overlay-el  (shadow-part el "[part=overlay]")
        ^js spinner-el  (shadow-part el "[part=spinner]")
        ^js progress-el (shadow-part el "[part=progress]")]
    ;; Not active by default — overlay hidden
    (is (= "none" (.. overlay-el -style -display)))
    ;; Spinner shown by default
    (is (= "block" (.. spinner-el -style -display)))
    ;; Progress hidden by default
    (is (= "none" (.. progress-el -style -display)))
    ;; Data attributes set
    (is (= "default" (.getAttribute el "data-variant")))
    (is (= "solid"   (.getAttribute el "data-overlay")))))

;; ── Active attribute ─────────────────────────────────────────────────────────
(deftest active-attribute-shows-overlay-test
  (let [^js el         (append! (make-el))
        ^js overlay-el (shadow-part el "[part=overlay]")]
    (.setAttribute el model/attr-active "")
    ;; Inline display should be cleared so CSS :host([active]) rule takes effect
    (is (= "" (.. overlay-el -style -display)))))

;; ── Variant attribute ────────────────────────────────────────────────────────
(deftest variant-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-variant "minimal")
    (is (= "minimal" (.getAttribute el "data-variant")))
    (.setAttribute el model/attr-variant "branded")
    (is (= "branded" (.getAttribute el "data-variant")))))

;; ── Overlay attribute ────────────────────────────────────────────────────────
(deftest overlay-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-overlay "blur")
    (is (= "blur" (.getAttribute el "data-overlay")))
    (.setAttribute el model/attr-overlay "transparent")
    (is (= "transparent" (.getAttribute el "data-overlay")))))

;; ── Spinner attribute ────────────────────────────────────────────────────────
(deftest spinner-false-hides-spinner-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-spinner "false")
    (is (= "none" (.. (shadow-part el "[part=spinner]") -style -display)))))

(deftest spinner-default-shows-spinner-test
  (let [^js el (append! (make-el))]
    (is (= "block" (.. (shadow-part el "[part=spinner]") -style -display)))))

;; ── Progress attribute ───────────────────────────────────────────────────────
(deftest progress-attribute-shows-bar-test
  (let [^js el          (append! (make-el))
        ^js progress-el (shadow-part el "[part=progress]")
        ^js bar-el      (shadow-part el "[part=bar]")]
    (.setAttribute el model/attr-progress "50")
    (is (= "block" (.. progress-el -style -display)))
    (is (= "50%"   (.. bar-el -style -width)))
    (is (= "50"    (.getAttribute progress-el "aria-valuenow")))))

(deftest progress-clamps-to-100-test
  (let [^js el     (append! (make-el))
        ^js bar-el (shadow-part el "[part=bar]")]
    (.setAttribute el model/attr-progress "150")
    (is (= "100%" (.. bar-el -style -width)))))

(deftest progress-removal-hides-bar-test
  (let [^js el          (append! (make-el))
        ^js progress-el (shadow-part el "[part=progress]")]
    (.setAttribute el model/attr-progress "50")
    (.removeAttribute el model/attr-progress)
    (is (= "none" (.. progress-el -style -display)))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest active-property-test
  (let [^js el (append! (make-el))]
    (set! (.-active el) true)
    (is (.hasAttribute el model/attr-active))
    (is (true? (.-active el)))
    (set! (.-active el) false)
    (is (not (.hasAttribute el model/attr-active)))
    (is (false? (.-active el)))))

(deftest variant-property-test
  (let [^js el (append! (make-el))]
    (set! (.-variant el) "minimal")
    (is (= "minimal" (.getAttribute el model/attr-variant)))
    (is (= "minimal" (.-variant el)))))

(deftest progress-property-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-progress el)))
    (set! (.-progress el) 75)
    (is (= "75" (.getAttribute el model/attr-progress)))
    (is (= 75 (.-progress el)))
    (set! (.-progress el) nil)
    (is (not (.hasAttribute el model/attr-progress)))))

(deftest spinner-property-test
  (let [^js el (append! (make-el))]
    (is (true? (.-spinner el)))
    (set! (.-spinner el) false)
    (is (false? (.-spinner el)))
    (set! (.-spinner el) true)
    (is (true? (.-spinner el)))))

(deftest overlay-property-test
  (let [^js el (append! (make-el))]
    (set! (.-overlay el) "blur")
    (is (= "blur" (.getAttribute el model/attr-overlay)))
    (is (= "blur" (.-overlay el)))))

;; ── ARIA attributes ──────────────────────────────────────────────────────────
(deftest aria-attributes-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-active "")
    (is (= "status"  (.getAttribute el "role")))
    (is (= "polite"  (.getAttribute el "aria-live")))
    (is (= "true"    (.getAttribute el "aria-busy")))
    (is (= "Loading" (.getAttribute el "aria-label")))))

(deftest progress-has-progressbar-role-test
  (let [^js el          (append! (make-el))
        ^js progress-el (shadow-part el "[part=progress]")]
    (is (= "progressbar" (.getAttribute progress-el "role")))
    (is (= "0"           (.getAttribute progress-el "aria-valuemin")))
    (is (= "100"         (.getAttribute progress-el "aria-valuemax")))))

;; ── x-splash-hidden event ────────────────────────────────────────────────────
(deftest hidden-event-fires-test
  (async done
    (let [^js el  (append! (make-el))
          fired?  (atom false)]
      (.setAttribute el model/attr-active "")
      (.addEventListener el model/event-hidden
                         (fn [^js e]
                           (reset! fired? true)
                           (is (true? (.-bubbles e)))
                           (is (true? (.-composed e)))
                           (is (false? (.-cancelable e)))
                           (done)))
      ;; Remove active to trigger fade-out → hidden event
      (.removeAttribute el model/attr-active)
      ;; Failsafe: if event never fires, fail after 1s
      (js/setTimeout (fn []
                       (when-not @fired?
                         (is false "x-splash-hidden event did not fire within 1s")
                         (done)))
                     1000))))

(deftest aria-busy-removed-after-hide-test
  (async done
    (let [^js el  (append! (make-el))
          fired?  (atom false)]
      (.setAttribute el model/attr-active "")
      (is (= "true" (.getAttribute el "aria-busy")))
      (.addEventListener el model/event-hidden
                         (fn [_]
                           (reset! fired? true)
                           (is (nil? (.getAttribute el "aria-busy")))
                           (done)))
      (.removeAttribute el model/attr-active)
      (js/setTimeout (fn []
                       (when-not @fired?
                         (is false "x-splash-hidden event did not fire within 1s")
                         (done)))
                     1000))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-errors-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    ;; Should render without errors
    (is (some? (.-shadowRoot el)))))
