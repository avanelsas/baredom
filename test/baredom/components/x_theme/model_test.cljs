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

(defn- scale
  "The values of the tokens `--x-<family>-<step>` in the CSS of `preset`, one for each step."
  [preset family steps]
  (let [css (model/preset->css preset)]
    (mapv #(second (re-find (re-pattern (str "--x-" family "-" % ":([^;]+);")) css)) steps)))

(def ^:private weight-steps ["normal" "medium" "semibold" "bold"])

(def ^:private radius-steps ["sm" "md" "lg" "xl" "full"])

(deftest the-weight-scale-ends-in-bold
  (is (= ["400" "500" "600" "700"] (scale "default" "font-weight" weight-steps)))
  (is (= ["400" "500" "700" "700"] (scale "neo-brutalist" "font-weight" weight-steps))))

(deftest the-radius-scale-has-five-steps
  (is (= ["0.375rem" "0.75rem" "1rem" "1.5rem" "9999px"]
         (scale "default" "radius" radius-steps))))

(deftest the-neo-brutalist-preset-has-no-radius
  (is (= ["0" "0" "0" "0" "0"] (scale "neo-brutalist" "radius" radius-steps))))

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

(defn- level
  "The layer level that the CSS of `preset` gives `token`, as a number."
  [preset token]
  (js/parseInt (second (re-find (re-pattern (str token ":(\\d+);")) (model/preset->css preset)))))

(deftest the-layers-of-every-preset-rise-from-popup-to-dialog-to-toast
  (doseq [preset model/allowed-presets]
    (is (< (level preset "--x-z-dropdown") (level preset "--x-z-modal") (level preset "--x-z-toast"))
        preset)))

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

(def ^:private status-shades
  ["--x-color-danger-hover" "--x-color-danger-active"
   "--x-color-success-hover" "--x-color-success-active"
   "--x-color-warning-hover" "--x-color-warning-active"])

(defn- missing-shades
  "The status shades that the CSS of `preset` does not declare, each with the preset."
  [preset]
  (let [css (model/preset->css preset)]
    (for [token status-shades
          :when (not (re-find (re-pattern (str token ":")) css))]
      [preset token])))

(deftest every-preset-has-the-six-status-shades
  (is (empty? (mapcat missing-shades model/allowed-presets))))

(deftest a-status-shade-is-mixed-with-black-in-light-mode
  (is (= "color-mix(in srgb,var(--x-color-danger) 85%,#000)"
         (get-in (model/preset-tokens "default") [:light "--x-color-danger-hover"])))
  (is (= "color-mix(in srgb,var(--x-color-warning) 70%,#000)"
         (get-in (model/preset-tokens "ocean") [:light "--x-color-warning-active"]))))

(deftest a-fill-shade-is-mixed-with-white-in-dark-mode
  (is (= "color-mix(in srgb,var(--x-color-danger) 85%,#fff)"
         (get-in (model/preset-tokens "default") [:dark "--x-color-danger-hover"])))
  (is (= "color-mix(in srgb,var(--x-color-primary) 70%,#fff)"
         (get-in (model/preset-tokens "aurora") [:dark "--x-color-primary-active"]))))

(deftest a-custom-preset-with-only-a-status-colour-has-its-shades
  (model/register-preset! "test-status" #js {:light #js {"--x-color-danger" "#ab1234"}})
  (is (empty? (missing-shades "test-status"))))

(deftest a-preset-can-name-its-own-status-shade
  (model/register-preset! "test-own-shade" #js {:light #js {"--x-color-danger-hover" "#010203"}})
  (is (re-find #"--x-color-danger-hover:#010203;" (model/preset->css "test-own-shade"))))

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

;; ── contrast ────────────────────────────────────────────────────────────────

(def ^:private contrast-rules
  "The least contrast that WCAG AA asks, with the pairs of colour tokens it applies to."
  {4.5 [["text" "bg"] ["text" "surface"]
        ["text-muted" "bg"] ["text-muted" "surface"] ["text-muted" "surface-active"]
        ["primary" "bg"] ["primary" "surface"]
        ["on-primary" "primary"] ["on-primary" "primary-hover"] ["on-primary" "primary-active"]
        ["on-danger" "danger"] ["on-danger" "danger-hover"] ["on-danger" "danger-active"]
        ["danger" "surface"] ["success" "surface"]]
   3   [["warning" "surface"]
        ["secondary" "bg"] ["secondary" "surface"]
        ["focus-ring" "bg"] ["focus-ring" "surface"]]})

(defn- token
  "The custom property of the colour token `color-name`."
  [color-name]
  (str "--x-color-" color-name))

(defn- hex->rgb
  "The red, green and blue of a hex colour, each from 0 to 255."
  [s]
  (let [h (subs s 1)
        h (if (= 3 (count h)) (apply str (mapcat #(repeat 2 %) h)) h)]
    (mapv #(js/parseInt (subs h % (+ % 2)) 16) [0 2 4])))

(defn- blend
  "The colour that is `weight` of `a` and `b` for the rest."
  [a b weight]
  (mapv #(+ (* %1 weight) (* %2 (- 1 weight))) a b))

(defn- rgb
  "The rgb of the CSS colour `value`, with its tokens taken from `tokens`.
  A translucent colour is laid over `--x-color-bg`."
  [tokens value]
  (condp re-find value
    #"^#" (hex->rgb value)
    #"^var\((--[a-z-]+)\)$"
    :>> (fn [[_ ref]] (rgb tokens (get tokens ref)))
    #"^color-mix\(in srgb,var\((--[a-z-]+)\) (\d+)%,(#[0-9a-f]+)\)$"
    :>> (fn [[_ ref percent partner]]
          (blend (rgb tokens (get tokens ref)) (hex->rgb partner) (/ (js/parseFloat percent) 100)))
    #"^rgba\(([\d.]+),([\d.]+),([\d.]+),([\d.]+)\)$"
    :>> (fn [[_ r g b alpha]]
          (blend (mapv js/parseFloat [r g b]) (rgb tokens (get tokens (token "bg"))) (js/parseFloat alpha)))))

(defn- linear-channel
  "One channel of an rgb colour, from 0 to 255, as linear light from 0 to 1."
  [c]
  (let [c (/ c 255)]
    (if (<= c 0.03928) (/ c 12.92) (js/Math.pow (/ (+ c 0.055) 1.055) 2.4))))

(defn- luminance
  "The relative luminance of an rgb colour, as WCAG defines it."
  [color]
  (let [[r g b] (map linear-channel color)]
    (+ (* 0.2126 r) (* 0.7152 g) (* 0.0722 b))))

(defn- contrast
  "The WCAG contrast ratio of two rgb colours."
  [a b]
  (let [[hi lo] (sort > [(luminance a) (luminance b)])]
    (/ (+ hi 0.05) (+ lo 0.05))))

(defn- rule-failure
  "How `fg` on `bg` in `tokens` breaks the rule of `least` contrast, or nil when it holds."
  [tokens fg bg least]
  (let [missing (remove #(get tokens (token %)) [fg bg])]
    (if (seq missing)
      [:missing missing]
      (let [ratio (contrast (rgb tokens (get tokens (token fg)))
                            (rgb tokens (get tokens (token bg))))]
        (when (< ratio least)
          [fg bg (.toFixed ratio 2)])))))

(defn- contrast-failures
  "The contrast rules that `preset` breaks, each with its mode."
  [preset]
  (let [{:keys [light dark]} (model/preset-tokens preset)]
    (for [[mode tokens] {:light light :dark (merge light dark)}
          [least pairs] contrast-rules
          [fg bg]       pairs
          :let  [failure (rule-failure tokens fg bg least)]
          :when failure]
      (into [preset mode] failure))))

(deftest every-preset-meets-the-contrast-rules
  (is (empty? (mapcat contrast-failures model/allowed-presets))))
