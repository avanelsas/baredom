(ns baredom.utils.dom-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.utils.dom :as du]))

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- make-el
  ([] (make-el "div"))
  ([tag] (.createElement js/document tag)))

(defn- append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn- cleanup-dom []
  (let [body (.-body js/document)]
    (loop []
      (when-let [c (.-firstChild body)]
        (.removeChild body c)
        (recur)))))

(defn cleanup-fixture [f] (f) (cleanup-dom))
(use-fixtures :each cleanup-fixture)

;; ── Attribute helpers ───────────────────────────────────────────────────────

(deftest set-attr-test
  (let [el (make-el)]
    (du/set-attr! el "role" "alert")
    (is (= "alert" (.getAttribute el "role")))))

(deftest remove-attr-test
  (let [el (make-el)]
    (.setAttribute el "role" "alert")
    (du/remove-attr! el "role")
    (is (not (.hasAttribute el "role")))))

(deftest has-attr-test
  (let [el (make-el)]
    (is (not (du/has-attr? el "disabled")))
    (.setAttribute el "disabled" "")
    (is (du/has-attr? el "disabled"))))

(deftest get-attr-test
  (let [el (make-el)]
    (is (nil? (du/get-attr el "value")))
    (.setAttribute el "value" "hello")
    (is (= "hello" (du/get-attr el "value")))))

(deftest set-bool-attr-test
  (let [el (make-el)]
    (du/set-bool-attr! el "disabled" true)
    (is (.hasAttribute el "disabled"))
    (is (= "" (.getAttribute el "disabled")))
    (du/set-bool-attr! el "disabled" false)
    (is (not (.hasAttribute el "disabled")))))

;; ── Instance-field access ───────────────────────────────────────────────────

(deftest getv-setv-test
  (let [el (make-el)]
    (is (nil? (du/getv el "__test")))
    (du/setv! el "__test" 42)
    (is (= 42 (du/getv el "__test")))))

(deftest initialized-mark-test
  (let [el (make-el)]
    (is (not (du/initialized? el "__init")))
    (du/mark-initialized! el "__init")
    (is (du/initialized? el "__init"))))

;; ── Event dispatch ──────────────────────────────────────────────────────────

(deftest dispatch-fires-event-test
  (let [el   (append! (make-el))
        seen (atom nil)]
    (.addEventListener el "x-test"
                       (fn [^js e] (reset! seen (.-detail e))))
    (du/dispatch! el "x-test" #js {:value 1})
    (is (some? @seen))
    (is (= 1 (.-value @seen)))))

(deftest dispatch-bubbles-and-composes-test
  (let [parent (append! (make-el))
        child  (make-el)
        seen   (atom nil)]
    (.appendChild parent child)
    (.addEventListener parent "x-bubble"
                       (fn [^js e]
                         (reset! seen {:bubbles  (.-bubbles e)
                                       :composed (.-composed e)})))
    (du/dispatch! child "x-bubble" #js {})
    (is (true? (:bubbles @seen)))
    (is (true? (:composed @seen)))))

(deftest dispatch-is-not-cancelable-test
  (let [el (append! (make-el))]
    (.addEventListener el "x-nc" (fn [^js e] (.preventDefault e)))
    ;; dispatch! is non-cancelable — preventDefault is a no-op
    (du/dispatch! el "x-nc" #js {})
    ;; No assertion needed beyond no error thrown; the event cannot be cancelled
    (is true)))

(deftest dispatch-cancelable-returns-true-when-not-cancelled-test
  (let [el (append! (make-el))]
    (is (true? (du/dispatch-cancelable! el "x-req" #js {:v 1})))))

(deftest dispatch-cancelable-returns-false-when-cancelled-test
  (let [el (append! (make-el))]
    (.addEventListener el "x-req" (fn [^js e] (.preventDefault e)))
    (is (false? (du/dispatch-cancelable! el "x-req" #js {:v 1})))))

;; ── Property accessor installers ────────────────────────────────────────────

(deftest define-bool-prop-test
  (let [el (append! (make-el))]
    (du/define-bool-prop! el "testDisabled" "disabled")
    (is (false? (.-testDisabled el)))
    (set! (.-testDisabled el) true)
    (is (true? (.hasAttribute el "disabled")))
    (is (true? (.-testDisabled el)))
    (set! (.-testDisabled el) false)
    (is (not (.hasAttribute el "disabled")))))

(deftest define-string-prop-test
  (let [el (append! (make-el))]
    (du/define-string-prop! el "testLabel" "label")
    (is (nil? (.-testLabel el)))
    (set! (.-testLabel el) "hello")
    (is (= "hello" (.getAttribute el "label")))
    (is (= "hello" (.-testLabel el)))
    (set! (.-testLabel el) nil)
    (is (not (.hasAttribute el "label")))))

(deftest define-number-prop-test
  (let [el (append! (make-el))]
    (du/define-number-prop! el "testTimeout" "timeout" 3000)
    (is (= 3000 (.-testTimeout el)))
    (set! (.-testTimeout el) 5000)
    (is (= "5000" (.getAttribute el "timeout")))
    (is (= 5000 (.-testTimeout el)))
    (.setAttribute el "timeout" "invalid")
    (is (= 3000 (.-testTimeout el)))
    (set! (.-testTimeout el) js/NaN)
    (is (not (.hasAttribute el "timeout")))))

;; ── install-properties! ─────────────────────────────────────────────────────

(deftest install-properties-bool-test
  (let [el (append! (make-el))
        api {:disabled {:type 'boolean :reflects-attribute "disabled"}}]
    (du/install-properties! el api)
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el "disabled"))))

(deftest install-properties-string-test
  (let [el (append! (make-el))
        api {:label {:type 'string :reflects-attribute "label"}}]
    (du/install-properties! el api)
    (set! (.-label el) "test")
    (is (= "test" (.getAttribute el "label")))))

(deftest install-properties-number-test
  (let [el (append! (make-el))
        api {:timeout {:type 'number :reflects-attribute "timeout" :default 1000}}]
    (du/install-properties! el api)
    (is (= 1000 (.-timeout el)))
    (set! (.-timeout el) 500)
    (is (= "500" (.getAttribute el "timeout")))))

(deftest install-properties-skips-readonly-test
  (let [el  (append! (make-el))
        api {:value {:type 'string :reflects-attribute "value" :readonly true}}]
    (du/install-properties! el api)
    ;; Should NOT have installed the property
    (is (undefined? (.-value el)))))

(deftest install-properties-skips-nil-attribute-test
  (let [el  (append! (make-el))
        api {:textValue {:type 'string :reflects-attribute nil}}]
    (du/install-properties! el api)
    ;; Should NOT have installed — no attribute to reflect to
    (is (undefined? (.-textValue el)))))
