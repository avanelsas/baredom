(ns baredom.components.x-progress-circle.x-progress-circle-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-progress-circle.x-progress-circle :as x-progress-circle]
            [baredom.components.x-progress-circle.model :as model]))

(x-progress-circle/init!)

(defn cleanup-fixture
  [f]
  (f)
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each cleanup-fixture)

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-base [el]
  (.querySelector (.-shadowRoot el) "[part=base]"))

(defn shadow-fill [el]
  (.querySelector (.-shadowRoot el) "[part=fill]"))

(defn shadow-value [el]
  (.querySelector (.-shadowRoot el) "[part=value-text]"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ───────────────────────────────────────────────────
(deftest shadow-dom-structure-test
  (let [el   (append! (make-el))
        root (.-shadowRoot el)]
    (is (some? (.querySelector root "[part=base]")))
    (is (some? (.querySelector root "[part=svg]")))
    (is (some? (.querySelector root "[part=track]")))
    (is (some? (.querySelector root "[part=fill]")))
    (is (some? (.querySelector root "[part=center]")))
    (is (some? (.querySelector root "[part=value-text]")))))

;; ── SVG stroke attributes ─────────────────────────────────────────────────
(deftest stroke-dasharray-set-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    (is (= "100" (.getAttribute fill "stroke-dasharray")))))

(deftest stroke-dashoffset-at-zero-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    ;; value=0, percent=0 → offset = 100*(1-0/100) = 100
    (is (= "100" (.getAttribute fill "stroke-dashoffset")))))

(deftest stroke-dashoffset-at-fifty-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    (.setAttribute el model/attr-value "50")
    ;; percent=50 → offset = 100*(1-50/100) = 50
    (is (= "50" (.getAttribute fill "stroke-dashoffset")))))

(deftest stroke-dashoffset-at-full-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    (.setAttribute el model/attr-value "100")
    ;; percent=100 → offset = 100*(1-1) = 0
    (is (= "0" (.getAttribute fill "stroke-dashoffset")))))

(deftest stroke-dashoffset-indeterminate-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    (.setAttribute el model/attr-indeterminate "")
    ;; indeterminate → offset = 100*0.75 = 75
    (is (= "75" (.getAttribute fill "stroke-dashoffset")))))

;; ── ARIA attributes ───────────────────────────────────────────────────────
(deftest aria-defaults-test
  (let [el (append! (make-el))]
    (is (= "progressbar" (.getAttribute el "role")))
    (is (= "0"   (.getAttribute el "aria-valuenow")))
    (is (= "0"   (.getAttribute el "aria-valuemin")))
    (is (= "100" (.getAttribute el "aria-valuemax")))
    (is (= "0%"  (.getAttribute el "aria-valuetext")))))

(deftest aria-value-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "60")
    (is (= "60"  (.getAttribute el "aria-valuenow")))
    (is (= "60%" (.getAttribute el "aria-valuetext")))))

(deftest aria-indeterminate-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-indeterminate "")
    (is (= "true" (.getAttribute el "aria-busy")))
    (is (nil? (.getAttribute el "aria-valuenow")))
    (is (= "Loading\u2026" (.getAttribute el "aria-valuetext")))))

;; ── Variant and size data attributes ──────────────────────────────────────
(deftest variant-data-attr-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-variant "success")
    (is (= "success" (.getAttribute base "data-variant")))))

(deftest size-data-attr-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute base "data-size")))))

;; ── Property reflection ───────────────────────────────────────────────────
(deftest indeterminate-property-test
  (let [el (append! (make-el))]
    (set! (.-indeterminate el) true)
    (is (.hasAttribute el model/attr-indeterminate))
    (set! (.-indeterminate el) false)
    (is (not (.hasAttribute el model/attr-indeterminate)))))

(deftest show-value-property-test
  (let [el ^js (append! (make-el))]
    (set! (.-showValue el) true)
    (is (.hasAttribute el model/attr-show-value))
    (set! (.-showValue el) false)
    (is (not (.hasAttribute el model/attr-show-value)))))

;; ── Label attribute ───────────────────────────────────────────────────────
(deftest label-sets-aria-label-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Upload progress")
    (is (= "Upload progress" (.getAttribute el "aria-label")))))

;; ── Value text visibility ─────────────────────────────────────────────────
(deftest value-text-hidden-by-default-test
  (let [el    (append! (make-el))
        vnode (shadow-value el)]
    (is (= "none" (.-display (.-style vnode))))))

(deftest value-text-visible-with-show-value-test
  (let [el    (append! (make-el))
        vnode (shadow-value el)]
    (.setAttribute el model/attr-value "42")
    (.setAttribute el model/attr-show-value "")
    (is (= "" (.-display (.-style vnode))))
    (is (= "42%" (.-textContent vnode)))))

(deftest value-text-hidden-when-indeterminate-test
  (let [el    (append! (make-el))
        vnode (shadow-value el)]
    (.setAttribute el model/attr-show-value "")
    (.setAttribute el model/attr-indeterminate "")
    (is (= "none" (.-display (.-style vnode))))))

;; ── x-progress-circle-complete event ─────────────────────────────────────
(deftest complete-event-fires-once-test
  (let [el     (append! (make-el))
        count  (atom 0)
        detail (atom nil)]
    (.addEventListener el model/event-complete
                       (fn [e]
                         (swap! count inc)
                         (reset! detail (js->clj (.-detail e) :keywordize-keys true))))
    (.setAttribute el model/attr-value "100")
    (is (= 1 @count))
    (is (= {:value 100 :max 100} @detail))
    ;; Firing again at same value should not re-fire
    (.setAttribute el model/attr-variant "success")
    (is (= 1 @count))))

(deftest complete-event-resets-on-lower-value-test
  (let [el    (append! (make-el))
        count (atom 0)]
    (.addEventListener el model/event-complete (fn [_] (swap! count inc)))
    (.setAttribute el model/attr-value "100")
    (is (= 1 @count))
    ;; Drop below max — resets flag
    (.setAttribute el model/attr-value "50")
    ;; Reach max again — should fire again
    (.setAttribute el model/attr-value "100")
    (is (= 2 @count))))
