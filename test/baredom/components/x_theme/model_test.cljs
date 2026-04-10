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
      (is (re-find #"--x-radius-md:" css)))))

(deftest register-preset-with-dark-mode-test
  (model/register-preset!
   "test-dark"
   #js {:light #js {"--x-color-primary" "#111111"}
        :dark  #js {"--x-color-primary" "#eeeeee"}})
  (let [css (model/preset->css "test-dark")]
    (is (re-find #"#111111" css))
    (is (re-find #"#eeeeee" css))))
