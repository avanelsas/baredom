(ns baredom.components.x-i18n-provider.x-i18n-provider-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-i18n-provider.x-i18n-provider :as x]
            [baredom.components.x-i18n-provider.model :as model]))

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

(defn set-translations!
  "Directly set translations on a provider for test purposes."
  [^js prov current fallback]
  (x/set-translations! prov #js {:current current :fallback fallback}))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (.querySelector root "style")))
    (is (some? (.querySelector root "slot")))))

;; ── Default locale ──────────────────────────────────────────────────────────
(deftest default-locale-test
  (let [^js el (append! (make-el))]
    (is (nil? (.getAttribute el "locale"))
        "locale attribute is not set by default")))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest src-property-test
  (let [^js el (make-el)]
    (set! (.-src el) "/locales/{locale}.json")
    (is (= "/locales/{locale}.json" (.getAttribute el "src")))
    (is (= "/locales/{locale}.json" (.-src el)))))

(deftest locale-property-test
  (let [^js el (append! (make-el))]
    (set! (.-locale el) "nl")
    (is (= "nl" (.getAttribute el "locale")))
    (is (= "nl" (.-locale el)))))

(deftest fallback-locale-property-test
  (let [^js el (append! (make-el))]
    (set! (.-fallbackLocale el) "en")
    (is (= "en" (.getAttribute el "fallback-locale")))
    (is (= "en" (.-fallbackLocale el)))))

;; ── Display contents ────────────────────────────────────────────────────────
(deftest display-contents-test
  (let [^js el  (append! (make-el))
        ^js root (.-shadowRoot el)
        css      (.-textContent (.querySelector root "style"))]
    (is (re-find #"display:contents" css))))

;; ── Translations property (nil when no fetch) ──────────────────────────────
(deftest translations-nil-without-fetch-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-translations el)))))

;; ── Subtree scanning: data-i18n-* ──────────────────────────────────────────

(deftest subtree-scan-sets-single-attribute-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-title" "nav.home")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov #js {"nav" #js {"home" "Home"}} nil)
    (x/translate-subtree! prov)
    (is (= "Home" (.getAttribute div "title")))))

(deftest subtree-scan-sets-multiple-attributes-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "actions.save")
    (.setAttribute div "data-i18n-placeholder" "actions.confirm")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov
                       #js {"actions" #js {"save" "Save" "confirm" "Are you sure?"}}
                       nil)
    (x/translate-subtree! prov)
    (is (= "Save" (.getAttribute div "label")))
    (is (= "Are you sure?" (.getAttribute div "placeholder")))))

(deftest subtree-scan-with-interpolation-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "greeting.hello")
    (.setAttribute div "data-i18n-params" "{\"name\":\"World\"}")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov #js {"greeting" #js {"hello" "Hello {name}!"}} nil)
    (x/translate-subtree! prov)
    (is (= "Hello World!" (.getAttribute div "label")))))

(deftest subtree-scan-fallback-translations-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "only-in-fallback")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov #js {} #js {"only-in-fallback" "Fallback value"})
    (x/translate-subtree! prov)
    (is (= "Fallback value" (.getAttribute div "label")))))

(deftest subtree-scan-missing-key-uses-key-string-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "missing.key")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov #js {} nil)
    (x/translate-subtree! prov)
    (is (= "missing.key" (.getAttribute div "label")))))

(deftest subtree-scan-skips-nested-provider-test
  (let [^js outer (make-el)
        ^js inner (make-el)
        ^js div   (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "greeting")
    (.appendChild inner div)
    (.appendChild outer inner)
    (append! outer)
    (set-translations! outer #js {"greeting" "Outer"} nil)
    (set-translations! inner #js {"greeting" "Inner"} nil)
    ;; Only outer's translate-subtree! — should skip div inside inner
    (x/translate-subtree! outer)
    (is (nil? (.getAttribute div "label"))
        "Outer provider should not translate elements inside nested provider")))

(deftest subtree-scan-multiple-children-test
  (let [^js prov (make-el)
        ^js div1 (.createElement js/document "div")
        ^js div2 (.createElement js/document "div")]
    (.setAttribute div1 "data-i18n-title" "nav.home")
    (.setAttribute div2 "data-i18n-title" "nav.about")
    (.appendChild prov div1)
    (.appendChild prov div2)
    (append! prov)
    (set-translations! prov #js {"nav" #js {"home" "Home" "about" "About"}} nil)
    (x/translate-subtree! prov)
    (is (= "Home" (.getAttribute div1 "title")))
    (is (= "About" (.getAttribute div2 "title")))))

(deftest subtree-scan-ignores-data-i18n-params-as-target-test
  (let [^js prov (make-el)
        ^js div  (.createElement js/document "div")]
    (.setAttribute div "data-i18n-label" "greeting")
    (.setAttribute div "data-i18n-params" "{\"name\":\"World\"}")
    (.appendChild prov div)
    (append! prov)
    (set-translations! prov #js {"greeting" "Hello"} nil)
    (x/translate-subtree! prov)
    ;; params should not be set as a target attribute called "params"
    (is (= "Hello" (.getAttribute div "label")))
    (is (nil? (.getAttribute div "params"))
        "data-i18n-params should not be treated as a target attribute")))
