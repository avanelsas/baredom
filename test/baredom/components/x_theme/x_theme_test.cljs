(ns baredom.components.x-theme.x-theme-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-theme.x-theme :as x]
            [baredom.components.x-theme.model   :as model]))

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

;; ── Default preset ──────────────────────────────────────────────────────────
(deftest default-preset-applies-tokens-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)
        css      (.-textContent (.querySelector root "style"))]
    (is (re-find #"--x-color-primary:" css))
    (is (re-find #"display:contents" css))))

;; ── Preset attribute change ─────────────────────────────────────────────────
(deftest preset-attribute-change-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (.setAttribute el "preset" "ocean")
    (let [css (.-textContent (.querySelector root "style"))]
      (is (re-find #"#0891b2" css)))))

;; ── Property accessor ───────────────────────────────────────────────────────
(deftest preset-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "preset" "forest")
    (is (= "forest" (.-preset el)))))

(deftest preset-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-preset el) "sunset")
    (is (= "sunset" (.getAttribute el "preset")))
    (let [css (.-textContent (.querySelector (.-shadowRoot el) "style"))]
      (is (re-find #"#ea580c" css)))))

;; ── Nested themes ───────────────────────────────────────────────────────────
(deftest nested-themes-test
  (let [^js outer (make-el)
        ^js inner (make-el)]
    (set! (.-preset outer) "ocean")
    (set! (.-preset inner) "sunset")
    (.appendChild outer inner)
    (append! outer)
    (let [inner-css (.-textContent (.querySelector (.-shadowRoot inner) "style"))]
      (is (re-find #"#ea580c" inner-css)))))

;; ── Custom preset via registerPreset ────────────────────────────────────────
(deftest custom-registered-preset-test
  (model/register-preset!
   "test-integration"
   #js {:light #js {"--x-color-primary" "#facade"}
        :dark  #js {"--x-color-primary" "#decade"}})
  (let [^js el (append! (make-el))]
    (set! (.-preset el) "test-integration")
    (let [css (.-textContent (.querySelector (.-shadowRoot el) "style"))]
      (is (re-find #"#facade" css))
      (is (re-find #"#decade" css)))))

;; ── Display contents ────────────────────────────────────────────────────────
(deftest display-contents-test
  (let [^js el (append! (make-el))
        css    (.-textContent (.querySelector (.-shadowRoot el) "style"))]
    (is (re-find #"display:contents" css))))
