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

(let [{:keys [fail error]} (run-tests)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
