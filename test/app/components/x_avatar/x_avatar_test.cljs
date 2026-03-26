(ns app.components.x-avatar.x-avatar-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [app.components.x-avatar.x-avatar :as x]
            [app.components.x-avatar.model    :as model]))

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

;; ── Registration ──────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=root]")))
    (is (some? (shadow-part el "[part=image]")))
    (is (some? (shadow-part el "[part=initials]")))
    (is (some? (shadow-part el "[part=fallback]")))
    (is (some? (shadow-part el "[part=status]")))
    (is (some? (shadow-part el "[part=badge]")))
    (is (some? (shadow-part el "slot[name=badge]")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "md"      (.getAttribute el "data-size")))
    (is (= "circle"  (.getAttribute el "data-shape")))
    (is (= "neutral" (.getAttribute el "data-variant")))
    ;; No src → fallback visible, image hidden
    (is (= "none"   (.. (shadow-part el "[part=image]")    -style -display)))
    (is (= "none"   (.. (shadow-part el "[part=initials]") -style -display)))
    (is (= "inline" (.. (shadow-part el "[part=fallback]") -style -display)))
    ;; No status → indicator hidden
    (is (= "none"   (.. (shadow-part el "[part=status]") -style -display)))
    ;; Decorative by default (no name/alt)
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── name attribute → derived initials ────────────────────────────────────
(deftest name-derives-initials-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-name "Alice Bob")
    (is (= "inline" (.. (shadow-part el "[part=initials]") -style -display)))
    (is (= "AB"     (.-textContent (shadow-part el "[part=initials]"))))
    (is (= "none"   (.. (shadow-part el "[part=fallback]") -style -display)))))

;; ── initials attribute — explicit overrides derived ───────────────────────
(deftest explicit-initials-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-initials "ZZ")
    (is (= "inline" (.. (shadow-part el "[part=initials]") -style -display)))
    (is (= "ZZ"     (.-textContent (shadow-part el "[part=initials]"))))))

(deftest explicit-initials-wins-over-name-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-name     "Alice Bob")
    (.setAttribute el model/attr-initials "XY")
    (is (= "XY" (.-textContent (shadow-part el "[part=initials]"))))))

;; ── size attribute ────────────────────────────────────────────────────────
(deftest size-attribute-test
  (testing "valid sizes set data-size"
    (doseq [sz ["xs" "sm" "md" "lg" "xl"]]
      (let [^js el (append! (make-el))]
        (.setAttribute el model/attr-size sz)
        (is (= sz (.getAttribute el "data-size"))))))
  (testing "invalid size falls back to md"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-size "huge")
      (is (= "md" (.getAttribute el "data-size"))))))

;; ── shape attribute ───────────────────────────────────────────────────────
(deftest shape-attribute-test
  (doseq [sh ["circle" "square" "rounded"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-shape sh)
      (is (= sh (.getAttribute el "data-shape"))))))

;; ── variant attribute ─────────────────────────────────────────────────────
(deftest variant-attribute-test
  (doseq [v ["neutral" "brand" "subtle"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant v)
      (is (= v (.getAttribute el "data-variant"))))))

;; ── status attribute ──────────────────────────────────────────────────────
(deftest status-shows-indicator-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "online")
    (is (= "block" (.. (shadow-part el "[part=status]") -style -display)))))

(deftest status-absent-hides-indicator-test
  (let [^js el (append! (make-el))]
    (is (= "none" (.. (shadow-part el "[part=status]") -style -display)))))

(deftest status-removed-hides-indicator-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-status "online")
    (.removeAttribute el model/attr-status)
    (is (= "none" (.. (shadow-part el "[part=status]") -style -display)))))

;; ── disabled attribute ────────────────────────────────────────────────────
(deftest disabled-aria-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute el "aria-disabled")))))

(deftest not-disabled-no-aria-disabled-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-disabled")))))

;; ── ARIA label from alt / name ────────────────────────────────────────────
(deftest aria-label-from-alt-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-alt "Profile photo")
    (is (= "img"           (.getAttribute el "role")))
    (is (= "Profile photo" (.getAttribute el "aria-label")))
    (is (nil?              (.getAttribute el "aria-hidden")))))

(deftest aria-label-from-name-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-name "Alice")
    (is (= "Alice" (.getAttribute el "aria-label")))))

(deftest aria-hidden-when-no-label-test
  (let [^js el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest string-property-roundtrip-test
  (let [^js el (append! (make-el))]
    (set! (.-name el) "Bob Carol")
    (is (= "Bob Carol" (.getAttribute el model/attr-name)))
    (is (= "Bob Carol" (.-name el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (is (true? (.-disabled el)))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))
    (is (false? (.-disabled el)))))

(deftest size-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "md" (.-size el)))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────
(deftest reconnect-model-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-name "Alice Bob")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    ;; After reconnect, initials should still render correctly
    (is (= "AB" (.-textContent (shadow-part el "[part=initials]"))))))
