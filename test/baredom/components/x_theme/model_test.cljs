(ns baredom.components.x-theme.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-theme.model :as model]))

;; ── normalize-preset ────────────────────────────────────────────────────────
(deftest normalize-preset-known-values-test
  (is (= "default"       (model/normalize-preset "default")))
  (is (= "ocean"         (model/normalize-preset "ocean")))
  (is (= "forest"        (model/normalize-preset "forest")))
  (is (= "sunset"        (model/normalize-preset "sunset")))
  (is (= "neo-brutalist"  (model/normalize-preset "neo-brutalist")))
  (is (= "aurora"        (model/normalize-preset "aurora")))
  (is (= "mono-ai"       (model/normalize-preset "mono-ai")))
  (is (= "warm-mineral"  (model/normalize-preset "warm-mineral"))))

(deftest normalize-preset-unknown-falls-back-test
  (is (= "default" (model/normalize-preset nil)))
  (is (= "default" (model/normalize-preset "")))
  (is (= "default" (model/normalize-preset "bad")))
  (is (= "default" (model/normalize-preset "OCEAN"))))

;; ── preset->css ─────────────────────────────────────────────────────────────
(deftest preset->css-returns-string-test
  (is (string? (model/preset->css "default"))))

(deftest preset->css-contains-host-test
  (let [css (model/preset->css "default")]
    (is (re-find #":host\{" css))
    (is (re-find #"display:contents" css))))

(deftest preset->css-contains-dark-mode-test
  (let [css (model/preset->css "default")]
    (is (re-find #"prefers-color-scheme:dark" css))))

(deftest preset->css-contains-primary-token-test
  (let [css (model/preset->css "ocean")]
    (is (re-find #"--x-color-primary:" css))))

(deftest preset->css-all-presets-produce-valid-css-test
  (doseq [name #{"default" "ocean" "forest" "sunset"
                  "neo-brutalist" "aurora" "mono-ai" "warm-mineral"}]
    (testing name
      (let [css (model/preset->css name)]
        (is (string? css))
        (is (re-find #":host\{" css))
        (is (re-find #"--x-color-primary:" css))))))

;; ── expanded tokens (33 → 50) ───────────────────────────────────────────────
(deftest preset->css-contains-new-typography-tokens-test
  (let [css (model/preset->css "default")]
    (is (re-find #"--x-font-size-xs:" css))
    (is (re-find #"--x-font-size-lg:" css))
    (is (re-find #"--x-font-weight-normal:" css))
    (is (re-find #"--x-font-weight-medium:" css))
    (is (re-find #"--x-font-weight-semibold:" css))
    (is (re-find #"--x-line-height-normal:" css))))

(deftest preset->css-contains-spacing-tokens-test
  (let [css (model/preset->css "default")]
    (is (re-find #"--x-space-xs:" css))
    (is (re-find #"--x-space-sm:" css))
    (is (re-find #"--x-space-md:" css))
    (is (re-find #"--x-space-lg:" css))
    (is (re-find #"--x-space-xl:" css))))

(deftest preset->css-contains-z-index-tokens-test
  (let [css (model/preset->css "default")]
    (is (re-find #"--x-z-dropdown:" css))
    (is (re-find #"--x-z-modal:" css))
    (is (re-find #"--x-z-toast:" css))))

(deftest preset->css-contains-opacity-tokens-test
  (let [css (model/preset->css "default")]
    (is (re-find #"--x-opacity-disabled:" css))
    (is (re-find #"--x-opacity-placeholder:" css))))

(deftest preset->css-contains-border-width-token-test
  (let [css (model/preset->css "default")]
    (is (re-find #"--x-border-width:" css))))

(deftest preset->css-neo-brutalist-overrides-test
  (let [css (model/preset->css "neo-brutalist")]
    (is (re-find #"--x-border-width:2px" css))
    (is (re-find #"--x-font-weight-semibold:700" css))
    (is (re-find #"--x-space-md:1rem" css))))

(deftest preset->css-mono-ai-overrides-test
  (let [css (model/preset->css "mono-ai")]
    (is (re-find #"--x-line-height-normal:1\.4" css))))

(deftest preset->css-aurora-overrides-test
  (let [css (model/preset->css "aurora")]
    (is (re-find #"--x-opacity-disabled:0\.45" css))))

(deftest preset->css-new-tokens-in-all-presets-test
  (doseq [name #{"default" "ocean" "forest" "sunset"
                  "neo-brutalist" "aurora" "mono-ai" "warm-mineral"}]
    (testing name
      (let [css (model/preset->css name)]
        (is (re-find #"--x-space-md:" css))
        (is (re-find #"--x-z-modal:" css))
        (is (re-find #"--x-opacity-disabled:" css))
        (is (re-find #"--x-border-width:" css))
        (is (re-find #"--x-font-weight-semibold:" css))))))

;; ── register-preset! ────────────────────────────────────────────────────────
(deftest register-preset-basic-test
  (model/register-preset!
   "test-custom"
   #js {:light #js {"--x-color-primary" "#aabbcc"}})
  (is (= "test-custom" (model/normalize-preset "test-custom")))
  (let [css (model/preset->css "test-custom")]
    (is (re-find #"#aabbcc" css))))

(deftest register-preset-partial-merges-with-default-test
  (model/register-preset!
   "test-partial"
   #js {:light #js {"--x-color-primary" "#112233"}})
  (let [css (model/preset->css "test-partial")]
    (testing "custom value present"
      (is (re-find #"#112233" css)))
    (testing "default fallback values present"
      (is (re-find #"--x-color-border:" css))
      (is (re-find #"--x-radius-md:" css)))
    (testing "new token defaults inherited"
      (is (re-find #"--x-space-md:" css))
      (is (re-find #"--x-z-modal:" css))
      (is (re-find #"--x-opacity-disabled:" css))
      (is (re-find #"--x-border-width:" css))
      (is (re-find #"--x-font-weight-semibold:" css)))))

(deftest register-preset-with-dark-mode-test
  (model/register-preset!
   "test-dark"
   #js {:light #js {"--x-color-primary" "#111111"}
        :dark  #js {"--x-color-primary" "#eeeeee"}})
  (let [css (model/preset->css "test-dark")]
    (is (re-find #"#111111" css))
    (is (re-find #"#eeeeee" css))))
