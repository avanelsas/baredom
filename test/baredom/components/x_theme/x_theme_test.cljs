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

;; ── Expanded tokens (33 → 50) ───────────────────────────────────────────────
(deftest expanded-tokens-rendered-test
  (let [^js el   (append! (make-el))
        css      (.-textContent (.querySelector (.-shadowRoot el) "style"))]
    (testing "new typography tokens"
      (is (re-find #"--x-font-size-xs:" css))
      (is (re-find #"--x-font-weight-semibold:" css))
      (is (re-find #"--x-line-height-normal:" css)))
    (testing "spacing tokens"
      (is (re-find #"--x-space-md:" css)))
    (testing "z-index tokens"
      (is (re-find #"--x-z-modal:" css)))
    (testing "opacity tokens"
      (is (re-find #"--x-opacity-disabled:" css)))
    (testing "border-width token"
      (is (re-find #"--x-border-width:" css)))))

(deftest neo-brutalist-expanded-overrides-test
  (let [^js el (append! (make-el))]
    (set! (.-preset el) "neo-brutalist")
    (let [css (.-textContent (.querySelector (.-shadowRoot el) "style"))]
      (is (re-find #"--x-border-width:2px" css))
      (is (re-find #"--x-font-weight-semibold:700" css)))))

;; ── Display contents ────────────────────────────────────────────────────────
(deftest display-contents-test
  (let [^js el (append! (make-el))
        css    (.-textContent (.querySelector (.-shadowRoot el) "style"))]
    (is (re-find #"display:contents" css))))

;; ── Status shades ───────────────────────────────────────────────────────────
(defn- themed
  "A theme in the document with `value` as its attribute `attr`."
  [attr value]
  (doto (make-el)
    (.setAttribute attr value)
    (append!)))

(defn- background
  "The computed background that `value` gives a child of the theme `el`."
  [^js el value]
  (let [^js child (.createElement js/document "div")]
    (set! (.. child -style -background) value)
    (.appendChild el child)
    (.-backgroundColor (js/getComputedStyle child))))

(deftest a-status-shade-follows-the-colour-of-a-custom-preset
  (model/register-preset!
   "test-blue-danger"
   #js {:light #js {"--x-color-danger" "#0000ff"}
        :dark  #js {"--x-color-danger" "#0000ff"}})
  (let [el (themed "preset" "test-blue-danger")]
    (is (= (background el "color-mix(in srgb,#0000ff 85%,#000)")
           (background el "var(--x-color-danger-hover)")))
    (is (= (background el "color-mix(in srgb,#0000ff 70%,#000)")
           (background el "var(--x-color-danger-active)")))))

(deftest a-status-shade-follows-a-colour-set-on-the-theme-element
  (let [el (themed "style" "--x-color-success:#0000ff")]
    (is (= (background el "color-mix(in srgb,#0000ff 85%,#000)")
           (background el "var(--x-color-success-hover)")))))
