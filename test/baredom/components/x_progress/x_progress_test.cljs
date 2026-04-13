(ns baredom.components.x-progress.x-progress-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-progress.x-progress :as x-progress]
            [baredom.components.x-progress.model :as model]))

(x-progress/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-base [^js el]
  (.querySelector (.-shadowRoot el) "[part=base]"))

(defn shadow-fill [^js el]
  (.querySelector (.-shadowRoot el) "[part=fill]"))

(defn shadow-header [^js el]
  (.querySelector (.-shadowRoot el) "[part=header]"))

(defn shadow-label [^js el]
  (.querySelector (.-shadowRoot el) "[part=label-text]"))

(defn shadow-value [^js el]
  (.querySelector (.-shadowRoot el) "[part=value-text]"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── ARIA attributes ───────────────────────────────────────────────────────
(deftest aria-defaults-test
  (let [el (append! (make-el))]
    (is (= "progressbar" (.getAttribute el "role")))
    (is (= "0"  (.getAttribute el "aria-valuenow")))
    (is (= "0"  (.getAttribute el "aria-valuemin")))
    (is (= "100" (.getAttribute el "aria-valuemax")))
    (is (= "0%" (.getAttribute el "aria-valuetext")))))

(deftest aria-value-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-value "60")
    (is (= "60" (.getAttribute el "aria-valuenow")))
    (is (= "60%" (.getAttribute el "aria-valuetext")))))

(deftest aria-indeterminate-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-indeterminate "")
    (is (= "true" (.getAttribute el "aria-busy")))
    (is (nil? (.getAttribute el "aria-valuenow")))
    (is (= "Loading\u2026" (.getAttribute el "aria-valuetext")))))

;; ── Variant and size data attributes ─────────────────────────────────────
(deftest variant-data-attr-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-variant "success")
    (is (= "success" (.getAttribute base "data-variant")))))

(deftest variant-invalid-falls-back-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-variant "invalid")
    (is (= "default" (.getAttribute base "data-variant")))))

(deftest size-data-attr-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute base "data-size")))))

;; ── Indeterminate data attribute ──────────────────────────────────────────
(deftest indeterminate-data-attr-test
  (let [el   (append! (make-el))
        base (shadow-base el)]
    (.setAttribute el model/attr-indeterminate "")
    (is (= "true" (.getAttribute base "data-indeterminate")))
    (.removeAttribute el model/attr-indeterminate)
    (is (= "false" (.getAttribute base "data-indeterminate")))))

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

(deftest value-property-test
  (let [el (append! (make-el))]
    (set! (.-value el) "75")
    (is (= "75" (.getAttribute el model/attr-value)))
    (is (= "75" (.-value el)))))

(deftest max-property-test
  (let [el (append! (make-el))]
    (set! (.-max el) "200")
    (is (= "200" (.getAttribute el model/attr-max)))
    (is (= "200" (.-max el)))))

(deftest variant-property-test
  (let [el (append! (make-el))]
    (set! (.-variant el) "warning")
    (is (= "warning" (.getAttribute el model/attr-variant)))
    (is (= "warning" (.-variant el)))))

(deftest size-property-test
  (let [el (append! (make-el))]
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))
    (is (= "lg" (.-size el)))))

(deftest label-property-test
  (let [el (append! (make-el))]
    (set! (.-label el) "Uploading")
    (is (= "Uploading" (.getAttribute el model/attr-label)))
    (is (= "Uploading" (.-label el)))
    (set! (.-label el) nil)
    (is (nil? (.getAttribute el model/attr-label)))))

;; ── Label attribute ───────────────────────────────────────────────────────
(deftest label-sets-aria-label-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Upload progress")
    (is (= "Upload progress" (.getAttribute el "aria-label")))))

(deftest label-renders-in-shadow-dom-test
  (let [el    (append! (make-el))
        label (shadow-label el)]
    (.setAttribute el model/attr-label "Downloading")
    (is (= "Downloading" (.-textContent label)))))

;; ── Header visibility ─────────────────────────────────────────────────────
(deftest header-hidden-by-default-test
  (let [el     (append! (make-el))
        header (shadow-header el)]
    (is (= "none" (.-display (.-style header))))))

(deftest header-visible-with-label-test
  (let [el     (append! (make-el))
        header (shadow-header el)]
    (.setAttribute el model/attr-label "Progress")
    (is (= "flex" (.-display (.-style header))))))

(deftest header-visible-with-show-value-test
  (let [el     (append! (make-el))
        header (shadow-header el)]
    (.setAttribute el model/attr-show-value "")
    (is (= "flex" (.-display (.-style header))))))

;; ── Show-value renders percentage ─────────────────────────────────────────
(deftest show-value-renders-percent-test
  (let [el        (append! (make-el))
        value-txt (shadow-value el)]
    (.setAttribute el model/attr-value "60")
    (.setAttribute el model/attr-show-value "")
    (is (= "60%" (.-textContent value-txt)))))

;; ── Fill width ────────────────────────────────────────────────────────────
(deftest fill-width-reflects-value-test
  (let [el   (append! (make-el))
        fill (shadow-fill el)]
    (.setAttribute el model/attr-value "50")
    (is (= "50%" (.-width (.-style fill))))))

;; ── x-progress-complete event ─────────────────────────────────────────────
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
