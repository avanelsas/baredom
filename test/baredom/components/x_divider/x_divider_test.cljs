(ns baredom.components.x-divider.x-divider-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-divider.x-divider :as x]
            [baredom.components.x-divider.model     :as model]))

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

(deftest idempotent-registration-test
  ;; Calling init! again must not throw
  (x/init!)
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────
(deftest shadow-structure-no-label-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=line]")))
    (is (nil?  (shadow-part el "[part=line-left]")))
    (is (nil?  (shadow-part el "[part=label]")))))

(deftest shadow-structure-with-label-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-label "Section")
    (append! el)
    (is (some? (shadow-part el "[part=line-left]")))
    (is (some? (shadow-part el "[part=label]")))
    (is (some? (shadow-part el "[part=label-text]")))
    (is (some? (shadow-part el "[part=line-right]")))
    (is (nil?  (shadow-part el "[part=line]")))))

;; ── Default state ─────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.getAttribute el "data-orientation")))
    (is (= "solid"      (.getAttribute el "data-variant")))
    (is (= "center"     (.getAttribute el "data-align")))
    (is (= "separator"  (.getAttribute el "role")))))

;; ── Orientation attribute ─────────────────────────────────────────────────
(deftest orientation-horizontal-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-orientation "horizontal")
    (is (= "horizontal" (.getAttribute el "data-orientation")))
    (is (nil? (.getAttribute el "aria-orientation")))))

(deftest orientation-vertical-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-orientation "vertical")
    (is (= "vertical"   (.getAttribute el "data-orientation")))
    (is (= "vertical"   (.getAttribute el "aria-orientation")))))

(deftest orientation-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-orientation "diagonal")
    (is (= "horizontal" (.getAttribute el "data-orientation")))))

;; ── Variant attribute ─────────────────────────────────────────────────────
(deftest variant-attribute-test
  (doseq [v ["solid" "dashed" "dotted"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-variant v)
      (is (= v (.getAttribute el "data-variant"))))))

(deftest variant-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-variant "wavy")
    (is (= "solid" (.getAttribute el "data-variant")))))

;; ── Align attribute ───────────────────────────────────────────────────────
(deftest align-attribute-test
  (doseq [a ["center" "start" "end"]]
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-align a)
      (is (= a (.getAttribute el "data-align"))))))

(deftest align-invalid-falls-back-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-align "left")
    (is (= "center" (.getAttribute el "data-align")))))

;; ── Role attribute ────────────────────────────────────────────────────────
(deftest role-separator-test
  (let [^js el (append! (make-el))]
    (is (= "separator" (.getAttribute el "role")))))

(deftest role-presentation-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-role "presentation")
    (is (= "presentation" (.getAttribute el "role")))))

(deftest role-none-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-role "none")
    (is (= "presentation" (.getAttribute el "role")))))

;; ── Label rendering ───────────────────────────────────────────────────────
(deftest label-text-rendered-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "My Label")
    (let [^js lt (shadow-part el "[part=label-text]")]
      (is (some? lt))
      (is (= "My Label" (.-textContent lt))))))

(deftest label-removed-reverts-to-single-line-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Exists")
    (is (some? (shadow-part el "[part=line-left]")))
    (.removeAttribute el model/attr-label)
    (is (nil?  (shadow-part el "[part=line-left]")))
    (is (some? (shadow-part el "[part=line]")))))

(deftest empty-label-uses-no-label-dom-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "")
    (is (nil?  (shadow-part el "[part=line-left]")))
    (is (some? (shadow-part el "[part=line]")))))

;; ── aria-label ────────────────────────────────────────────────────────────
(deftest aria-label-set-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-aria-label "Divides content")
    (is (= "Divides content" (.getAttribute el "aria-label")))))

(deftest aria-label-absent-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "aria-label")))))

;; ── Property accessors ────────────────────────────────────────────────────
(deftest orientation-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "horizontal" (.-orientation el)))))

(deftest variant-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "solid" (.-variant el)))))

(deftest align-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "center" (.-align el)))))

(deftest label-property-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "My Section")
    (is (= "My Section" (.getAttribute el model/attr-label)))))

(deftest thickness-property-test
  (let [^js el (append! (make-el))]
    (set! (.-thickness el) "2px")
    (is (= "2px" (.getAttribute el model/attr-thickness)))))

(deftest color-property-test
  (let [^js el (append! (make-el))]
    (set! (.-color el) "#ccc")
    (is (= "#ccc" (.getAttribute el model/attr-color)))))

(deftest inset-property-test
  (let [^js el (append! (make-el))]
    (set! (.-inset el) "8px")
    (is (= "8px" (.getAttribute el model/attr-inset)))))

(deftest length-property-test
  (let [^js el (append! (make-el))]
    (set! (.-length el) "100%")
    (is (= "100%" (.getAttribute el model/attr-length)))))

;; ── Reconnect stability ───────────────────────────────────────────────────
(deftest reconnect-stable-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-label "Persist")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [^js lt (shadow-part el "[part=label-text]")]
      (is (some? lt))
      (is (= "Persist" (.-textContent lt))))))

(deftest reconnect-no-duplicate-shadow-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (let [root1 (.-shadowRoot el)]
      (.remove el)
      (.appendChild (.-body js/document) el)
      ;; Same shadow root (shadow roots are not recreated)
      (is (= root1 (.-shadowRoot el))))))
