#!/usr/bin/env bb
;; audit_tokens_test.bb — Tests for the rules of audit_tokens.bb.
;;
;; Usage: bb scripts/audit_tokens_test.bb

(load-file "scripts/audit_tokens.bb")
(require '[clojure.test :refer [deftest is run-tests]])

(deftest a-property-on-the-plain-host-rule-is-always-declared
  (is (= #{"--x-card-bg"}
         (always-declared ":host{display:block;--x-card-bg:#fff;}[part=a]{color:red;}"))))

(deftest a-property-declared-after-another-rule-is-always-declared
  (is (= #{"--x-card-bg"}
         (always-declared "[part=a]{color:red;}:host{--x-card-bg:#fff;}"))))

(deftest a-property-only-in-a-media-block-is-not-always-declared
  (is (= #{}
         (always-declared "@media (prefers-color-scheme:dark){:host{--x-card-bg:#000;}}"))))

(deftest a-property-only-under-a-variant-is-not-always-declared
  (is (= #{}
         (always-declared ":host([data-variant='a']){--x-card-bg:#000;}"))))

(deftest a-token-of-the-theme-is-not-an-own-property
  (is (= #{}
         (always-declared ":host{--x-color-bg:#fff;}"))))

(deftest a-length-in-the-colour-family-is-a-measure
  (is (= [true true false false]
         (mapv (comp boolean measure-as-colour?)
               [{:family "color" :value "240px"}
                {:family "color" :value "0.45"}
                {:family "color" :value "#fff"}
                {:family "space" :value "240px"}]))))

(deftest a-colour-on-one-side-of-a-border-is-in-the-colour-family
  (is (= ["color" "color" "color" "border-width" nil]
         (mapv family-of ["border-bottom-color" "background-image" "border-color"
                          "border-bottom-width" "border-bottom-style"]))))

(deftest a-custom-property-that-holds-a-border-stands-for-its-width-and-its-colour
  (is (= [["border-width" "1px"] ["border-color" "var(--x-color-border,#e2e8f0)"]]
         (longhand ["--x-card-border" "1px solid var(--x-color-border,#e2e8f0)"]))))

(deftest a-border-in-a-custom-property-is-read-with-a-space-inside-var
  (is (= [["border-width" "var(--x-border-width, 1px)"] ["border-color" "var(--x-color-border, #ddd)"]]
         (longhand ["--x-card-border" "var(--x-border-width, 1px) solid var(--x-color-border, #ddd)"]))))

(deftest an-outline-has-no-border-width
  (is (= [["border-color" "#60a5fa"]]
         (longhand ["outline" "2px solid #60a5fa"]))))

(deftest a-custom-property-named-like-a-ring-gives-its-width
  (is (= [["border-width" "1px"] ["border-color" "#000"]]
         (longhand ["--x-card-ordering-border" "1px solid #000"]))))

(deftest a-custom-property-with-another-value-stands-for-itself
  (is (= [["--x-card-padding" "1px 2px"]]
         (longhand ["--x-card-padding" "1px 2px"]))))

(deftest a-rule-that-holds-a-block-keeps-its-own-declarations
  (is (= #{["color" "red"] ["padding" "4px"] ["transition" "none"]}
         (set (declarations "a{color:red;@media (min-width:1px){transition:none;}padding:4px;}")))))

(deftest text-outside-every-block-is-no-declaration
  (is (= [["color" "red"]]
         (declarations "@charset 'utf-8';\na{color:red;}\n"))))

(deftest a-block-inside-a-media-block-is-read-once
  (is (= [["color" "red"]]
         (declarations "@media (prefers-color-scheme:dark){:host{color:red;}}"))))

(deftest a-border-on-a-logical-side-stands-for-its-width-and-its-colour
  (is (= [["border-width" "1px"] ["border-color" "#000"]]
         (longhand ["border-block-end" "1px solid #000"]))))

(deftest a-fallback-that-holds-a-border-gives-its-width-and-its-colour
  (is (= #{["border-width" "2px" :own-literal] ["color" "var(--x-color-border,#ddd)" :own-token]}
         (set (map (juxt :family :value :kind)
                   (used-rows "x-card" "a{border:var(--x-card-edge,2px solid var(--x-color-border,#ddd));}"))))))

(let [{:keys [fail error]} (run-tests)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
