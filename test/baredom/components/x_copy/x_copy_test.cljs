(ns baredom.components.x-copy.x-copy-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-copy.x-copy :as x]
            [baredom.components.x-copy.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el s]
  (.querySelector (.-shadowRoot el) s))

;; ── Registration ──────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ─────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=wrap]")))
    (is (some? (shadow-part el "[part=trigger]")))
    (is (some? (shadow-part el "[part=tooltip]")))
    (is (some? (shadow-part el "[part=tooltip-text]")))
    (is (some? (shadow-part el "slot")))))

;; ── Trigger is a button ───────────────────────────────────────────────────────
(deftest trigger-is-button-test
  (let [^js el      (append! (make-el))
        ^js trigger (shadow-part el "[part=trigger]")]
    (is (= "button" (.toLowerCase (.-tagName trigger))))))

;; ── Disabled attribute ────────────────────────────────────────────────────────
(deftest disabled-disables-trigger-test
  (let [^js el      (append! (make-el))
        ^js trigger (shadow-part el "[part=trigger]")]
    (.setAttribute el model/attr-disabled "")
    (is (= true (.-disabled trigger)))))

(deftest not-disabled-by-default-test
  (let [^js el      (append! (make-el))
        ^js trigger (shadow-part el "[part=trigger]")]
    (is (= false (.-disabled trigger)))))

;; ── Property reflection ───────────────────────────────────────────────────────
(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-text el) "Hello")
    (is (= "Hello" (.getAttribute el model/attr-text)))
    (is (= "Hello" (.-text el)))))

(deftest mode-property-test
  (let [^js el (append! (make-el))]
    (set! (.-mode el) "html")
    (is (= "html" (.getAttribute el model/attr-mode)))))

(deftest show-tooltip-property-test
  (let [^js el (append! (make-el))]
    (is (= true (.-showTooltip el)))
    (set! (.-showTooltip el) false)
    (is (= false (.-showTooltip el)))))

(deftest tooltip-ms-property-test
  (let [^js el (append! (make-el))]
    (is (= 1200 (.-tooltipMs el)))
    (set! (.-tooltipMs el) 3000)
    (is (= "3000" (.getAttribute el model/attr-tooltip-ms)))
    (is (= 3000 (.-tooltipMs el)))))

(deftest text-value-property-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-textValue el)))
    (set! (.-textValue el) "override")
    (is (= "override" (.-textValue el)))
    ;; should not appear as an attribute
    (is (nil? (.getAttribute el "textValue")))))

;; ── Tooltip not shown by default ──────────────────────────────────────────────
(deftest tooltip-hidden-by-default-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el "data-tooltip-open")))))

;; ── Copy disabled — no events ─────────────────────────────────────────────────
(deftest copy-disabled-fires-no-events-test
  (let [^js el     (append! (make-el))
        events     (atom [])
        _          (.setAttribute el model/attr-disabled "")
        _          (.addEventListener el model/event-copy-request
                                      (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=trigger]"))
    (is (= 0 (count @events)))))

;; ── Copy request event (cancelable) ──────────────────────────────────────────
(deftest copy-request-event-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-text "test text")
        _        (.addEventListener el model/event-copy-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=trigger]"))
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (.-cancelable ev))
      (is (.-bubbles ev))
      (is (.-composed ev)))))

;; ── Preventing copy-request stops copy ───────────────────────────────────────
(deftest prevent-copy-request-stops-copy-test
  (let [^js el     (append! (make-el))
        successes  (atom [])
        _          (.setAttribute el model/attr-text "test text")
        _          (.addEventListener el model/event-copy-request
                                      (fn [^js e] (.preventDefault e)))
        _          (.addEventListener el model/event-copy-success
                                      (fn [^js e] (swap! successes conj e)))]
    (.click (shadow-part el "[part=trigger]"))
    ;; success should not fire because request was prevented
    ;; (async nature of clipboard means we check immediately for the sync part)
    ;; The request handler prevents it before any async operation starts
    (is (= 0 (count @successes)))))

;; ── copy() public method returns Promise ──────────────────────────────────────
(deftest copy-method-exists-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-text "some text")
    (is (fn? (.-copy el)))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-single-request-event-test
  (let [^js el  (make-el)
        events  (atom [])
        _       (.setAttribute el model/attr-text "hello")
        _       (.addEventListener el model/event-copy-request
                                   (fn [^js e] (swap! events conj e)))]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.click (shadow-part el "[part=trigger]"))
    ;; Only 1 event despite reconnect
    (is (= 1 (count @events)))))

;; ── Success message attribute ─────────────────────────────────────────────────
(deftest success-message-property-test
  (let [^js el (append! (make-el))]
    (set! (.-successMessage el) "Done!")
    (is (= "Done!" (.getAttribute el model/attr-success-message)))))

(deftest error-message-property-test
  (let [^js el (append! (make-el))]
    (set! (.-errorMessage el) "Oops!")
    (is (= "Oops!" (.getAttribute el model/attr-error-message)))))

;; ── hotkey property ───────────────────────────────────────────────────────────
(deftest hotkey-property-test
  (let [^js el (append! (make-el))]
    (set! (.-hotkey el) "ctrl+c")
    (is (= "ctrl+c" (.getAttribute el model/attr-hotkey)))
    (is (= "ctrl+c" (.-hotkey el)))))
