(ns baredom.components.x-spinner.x-spinner-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-spinner.x-spinner :as x-spinner]
            [baredom.components.x-spinner.model     :as model]))

(x-spinner/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow-ring [el]
  (.querySelector (.-shadowRoot el) "[part=ring]"))

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-ring-exists-test
  (let [el (append! (make-el))]
    (is (some? (shadow-ring el)))))

(deftest ring-aria-hidden-test
  (let [el (append! (make-el))]
    (is (= "true" (.getAttribute (shadow-ring el) "aria-hidden")))))

;; ── Static ARIA on host ───────────────────────────────────────────────────
(deftest host-role-test
  (let [el (append! (make-el))]
    (is (= "status" (.getAttribute el "role")))))

(deftest host-aria-live-test
  (let [el (append! (make-el))]
    (is (= "polite" (.getAttribute el "aria-live")))))

;; ── Default attributes ────────────────────────────────────────────────────
(deftest defaults-test
  (let [el (append! (make-el))]
    (is (= "md"      (.getAttribute el "data-size")))
    (is (= "default" (.getAttribute el "data-variant")))
    (is (= "Loading" (.getAttribute el "aria-label")))))

;; ── Size attribute ────────────────────────────────────────────────────────
(deftest size-valid-test
  (let [el (append! (make-el))]
    (doseq [s ["xs" "sm" "md" "lg" "xl"]]
      (.setAttribute el model/attr-size s)
      (is (= s (.getAttribute el "data-size"))))))

(deftest size-invalid-falls-back-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-size "huge")
    (is (= "md" (.getAttribute el "data-size")))))

;; ── Variant attribute ─────────────────────────────────────────────────────
(deftest variant-valid-test
  (let [el (append! (make-el))]
    (doseq [v ["default" "primary" "success" "warning" "danger"]]
      (.setAttribute el model/attr-variant v)
      (is (= v (.getAttribute el "data-variant"))))))

(deftest variant-invalid-falls-back-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-variant "neon")
    (is (= "default" (.getAttribute el "data-variant")))))

;; ── Label attribute ───────────────────────────────────────────────────────
(deftest label-sets-aria-label-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Saving changes")
    (is (= "Saving changes" (.getAttribute el "aria-label")))))

(deftest label-removed-restores-default-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-label "Custom")
    (.removeAttribute el model/attr-label)
    (is (= "Loading" (.getAttribute el "aria-label")))))

;; ── Property reflection ───────────────────────────────────────────────────
(deftest size-property-test
  (let [el ^js (append! (make-el))]
    (set! (.-size el) "xl")
    (is (= "xl" (.getAttribute el model/attr-size)))
    (is (= "xl" (.-size el)))))

(deftest variant-property-test
  (let [el ^js (append! (make-el))]
    (set! (.-variant el) "danger")
    (is (= "danger" (.getAttribute el model/attr-variant)))
    (is (= "danger" (.-variant el)))))

(deftest label-property-test
  (let [el ^js (append! (make-el))]
    (set! (.-label el) "Please wait")
    (is (= "Please wait" (.getAttribute el model/attr-label)))
    (is (= "Please wait" (.-label el)))))

;; ── Property-to-null fallback ─────────────────────────────────────────────
(deftest size-property-null-restores-default-test
  (let [el ^js (append! (make-el))]
    (set! (.-size el) "xl")
    (set! (.-size el) nil)
    (is (nil? (.getAttribute el model/attr-size)))
    (is (= model/default-size (.-size el)))))

(deftest variant-property-null-restores-default-test
  (let [el ^js (append! (make-el))]
    (set! (.-variant el) "danger")
    (set! (.-variant el) nil)
    (is (nil? (.getAttribute el model/attr-variant)))
    (is (= model/default-variant (.-variant el)))))

(deftest label-property-null-restores-default-test
  (let [el ^js (append! (make-el))]
    (set! (.-label el) "Custom")
    (set! (.-label el) nil)
    (is (nil? (.getAttribute el model/attr-label)))
    (is (= model/default-label (.-label el)))))

;; ── Reconnect behaviour ──────────────────────────────────────────────────
(deftest reconnect-renders-correctly-test
  (let [el ^js (append! (make-el))]
    (.setAttribute el model/attr-size "lg")
    (.setAttribute el model/attr-variant "primary")
    (.setAttribute el model/attr-label "Saving")
    ;; Remove and re-append
    (.remove el)
    (append! el)
    (is (= "lg"      (.getAttribute el "data-size")))
    (is (= "primary" (.getAttribute el "data-variant")))
    (is (= "Saving"  (.getAttribute el "aria-label")))))
