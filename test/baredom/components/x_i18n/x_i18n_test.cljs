(ns baredom.components.x-i18n.x-i18n-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-i18n.x-i18n :as x]
            [baredom.components.x-i18n.model :as model]
            [baredom.components.x-i18n-provider.x-i18n-provider :as xp]
            [baredom.components.x-i18n-provider.model :as provider-model]
            [baredom.utils.dom :as du]))

(xp/init!)
(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [node (.querySelectorAll js/document provider-model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js make-provider []
  (.createElement js/document provider-model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn set-translations!
  "Directly set translations on a provider for test purposes."
  [^js prov current fallback]
  (du/setv! prov xp/k-translations
            #js {:current current :fallback fallback}))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (.querySelector root "style")))
    (is (some? (.querySelector root "[part=text]")))))

;; ── Display inline ──────────────────────────────────────────────────────────
(deftest display-inline-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)
        css      (.-textContent (.querySelector root "style"))]
    (is (re-find #"display:inline" css))))

;; ── Shows key when no provider ──────────────────────────────────────────────
(deftest shows-key-without-provider-test
  (let [^js el (make-el)]
    (.setAttribute el "key" "greeting.hello")
    (append! el)
    (is (= "greeting.hello" (.-value el)))))

;; ── Shows translated text ───────────────────────────────────────────────────
(deftest shows-translated-text-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "greeting")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {"greeting" "Hello"} nil)
    ;; Dispatch change to trigger re-render
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Hello" (.-value el)))))

;; ── Interpolation ───────────────────────────────────────────────────────────
(deftest interpolation-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "greeting")
    (.setAttribute el "params" "{\"name\":\"World\"}")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {"greeting" "Hello {name}"} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Hello World" (.-value el)))))

;; ── Fallback translations ───────────────────────────────────────────────────
(deftest fallback-translation-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "only-in-fallback")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {} #js {"only-in-fallback" "Fallback text"})
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Fallback text" (.-value el)))))

;; ── Falls back to key string ────────────────────────────────────────────────
(deftest falls-back-to-key-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "missing.key")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "missing.key" (.-value el)))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest key-property-test
  (let [^js el (append! (make-el))]
    (set! (.-key el) "nav.home")
    (is (= "nav.home" (.getAttribute el "key")))
    (is (= "nav.home" (.-key el)))))

(deftest params-property-test
  (let [^js el (append! (make-el))]
    (set! (.-params el) "{\"a\":1}")
    (is (= "{\"a\":1}" (.getAttribute el "params")))
    (is (= "{\"a\":1}" (.-params el)))))

;; ── Key attribute change re-renders ─────────────────────────────────────────
(deftest key-change-rerenders-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "a")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {"a" "Alpha" "b" "Bravo"} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Alpha" (.-value el)))
    (.setAttribute el "key" "b")
    (is (= "Bravo" (.-value el)))))

;; ── Empty key renders empty string ──────────────────────────────────────────
(deftest empty-key-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {"a" "Alpha"} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "" (.-value el)))))

;; ── Multiple siblings under one provider ────────────────────────────────────
(deftest multiple-siblings-test
  (let [^js prov (make-provider)
        ^js el1  (make-el)
        ^js el2  (make-el)]
    (.setAttribute el1 "key" "a")
    (.setAttribute el2 "key" "b")
    (.appendChild prov el1)
    (.appendChild prov el2)
    (append! prov)
    (set-translations! prov #js {"a" "Alpha" "b" "Bravo"} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Alpha" (.-value el1)))
    (is (= "Bravo" (.-value el2)))))

;; ── Nested dot-notation keys ────────────────────────────────────────────────
(deftest nested-key-test
  (let [^js prov (make-provider)
        ^js el   (make-el)]
    (.setAttribute el "key" "nav.home")
    (.appendChild prov el)
    (append! prov)
    (set-translations! prov #js {"nav" #js {"home" "Home"}} nil)
    (du/dispatch! prov provider-model/event-change #js {:locale "en"})
    (is (= "Home" (.-value el)))))
