(ns baredom.components.x-code.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-code.model :as model]))

(defn- types-of [tokens]
  (set (map first tokens)))

;; ── parse-language ───────────────────────────────────────────────────────────
(deftest parse-language-test
  (testing "canonical ids pass through"
    (is (= "js"   (model/parse-language "js")))
    (is (= "json" (model/parse-language "json")))
    (is (= "css"  (model/parse-language "css")))
    (is (= "html" (model/parse-language "html"))))
  (testing "aliases map onto canonical ids"
    (is (= "js"   (model/parse-language "javascript")))
    (is (= "js"   (model/parse-language "jsx")))
    (is (= "html" (model/parse-language "htm")))
    (is (= "html" (model/parse-language "xml"))))
  (testing "case-insensitive and trimmed"
    (is (= "js"   (model/parse-language "  JS ")))
    (is (= "html" (model/parse-language "HTML"))))
  (testing "unknown / nil / empty fall back to text"
    (is (= "text" (model/parse-language "ruby")))
    (is (= "text" (model/parse-language nil)))
    (is (= "text" (model/parse-language "")))))

;; ── parse-max-lines ──────────────────────────────────────────────────────────
(deftest parse-max-lines-test
  (is (= 10 (model/parse-max-lines "10")))
  (is (= 0  (model/parse-max-lines nil)))
  (is (= 0  (model/parse-max-lines "")))
  (is (= 0  (model/parse-max-lines "abc")))
  (is (= 0  (model/parse-max-lines "-3")))
  (is (= 0  (model/parse-max-lines "0")))
  (is (= 5  (model/parse-max-lines "5px")) "leading integer is parsed"))

;; ── dedent / prepare-code ────────────────────────────────────────────────────
(deftest dedent-test
  (is (= "a\nb"   (model/dedent "  a\n  b")))
  (is (= "a\n  b" (model/dedent "  a\n    b")))
  (is (= "a\nb"   (model/dedent "a\nb")) "no common indent is a no-op")
  (testing "blank lines do not constrain the common indent"
    (is (= "a\n\nb" (model/dedent "  a\n\n  b")))))

(deftest prepare-code-test
  (is (= "const x = 1;" (model/prepare-code "\n  const x = 1;\n")))
  (is (= "a\nb"         (model/prepare-code "\n\n  a\n  b\n\n")))
  (is (= ""             (model/prepare-code nil)))
  (is (= ""             (model/prepare-code "\n\n  \n")))
  (testing "CRLF and lone CR line endings are normalised to LF"
    (is (= "a\nb" (model/prepare-code "a\r\nb\r\n")))
    (is (= "a\nb" (model/prepare-code "a\rb")))
    (is (= "a\nb" (model/prepare-code "\r\n  a\r\n  b\r\n")))))

;; ── tokenize ─────────────────────────────────────────────────────────────────
(deftest tokenize-js-test
  (let [toks (model/tokenize "js" "const x = 42;")]
    (is (= [:keyword "const"] (first toks)))
    (is (contains? (types-of toks) :number))
    (is (contains? (types-of toks) :punct)))
  (testing "a keyword inside a string stays a string token"
    (is (= [[:string "'const'"]] (model/tokenize "js" "'const'"))))
  (testing "a block comment spanning newlines is a single token"
    (let [toks (model/tokenize "js" "/* a\nb */")]
      (is (= 1 (count toks)))
      (is (= :comment (ffirst toks)))
      (is (re-find #"\n" (second (first toks))))))
  (testing "empty input yields no tokens"
    (is (= [] (model/tokenize "js" "")))))

(deftest tokenize-json-test
  (let [toks (model/tokenize "json" "{\"a\": 1, \"b\": true}")]
    (is (contains? (types-of toks) :key))
    (is (contains? (types-of toks) :number))
    (is (contains? (types-of toks) :keyword))
    (is (contains? (types-of toks) :punct))))

(deftest tokenize-css-test
  (let [toks (model/tokenize "css" "@media all { color: red; }")]
    (is (contains? (types-of toks) :atrule))
    (is (contains? (types-of toks) :property))
    (is (contains? (types-of toks) :punct))))

(deftest tokenize-html-test
  (let [toks (model/tokenize "html" "<div class=\"x\">hi &amp; bye</div>")]
    (is (contains? (types-of toks) :tag))
    (is (contains? (types-of toks) :attr-value))
    (is (contains? (types-of toks) :entity))))

(deftest tokenize-text-test
  (is (= [[:plain "const x"]] (model/tokenize "text" "const x")))
  (is (= [[:plain "anything"]] (model/tokenize "ruby" "anything"))
      "an unknown language degrades to plain text"))

;; ── tokens->lines ────────────────────────────────────────────────────────────
(deftest tokens->lines-test
  (testing "a token with newlines is split across lines"
    (is (= [[[:plain "a"]] [[:plain "b"]]]
           (model/tokens->lines [[:plain "a\nb"]]))))
  (testing "single-line tokens collect onto one line"
    (is (= [[[:keyword "const"] [:plain " x"]]]
           (model/tokens->lines [[:keyword "const"] [:plain " x"]]))))
  (testing "no tokens still produces one (empty) line"
    (is (= [[]] (model/tokens->lines []))))
  (testing "a trailing newline produces a final empty line"
    (is (= [[[:plain "a"]] [[:plain ""]]]
           (model/tokens->lines [[:plain "a\n"]])))))

;; ── collapsible? / language-label ────────────────────────────────────────────
(deftest collapsible?-test
  (is (false? (model/collapsible? 0 100)) "max-lines 0 disables collapsing")
  (is (true?  (model/collapsible? 3 10)))
  (is (false? (model/collapsible? 3 2)))
  (is (false? (model/collapsible? 3 3)) "exactly max-lines is not collapsible"))

(deftest language-label-test
  (is (= "JS"   (model/language-label "js")))
  (is (= "HTML" (model/language-label "html")))
  (is (= ""     (model/language-label "text"))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "text" (:language m)))
    (is (= ""     (:code m)))
    (is (= ""     (:filename m)))
    (is (= 0      (:max-lines m)))
    (is (false?   (:show-copy? m)))
    (is (false?   (:line-numbers? m)))
    (is (false?   (:wrap? m)))
    (is (false?   (:expanded? m)))
    (is (false?   (:collapsible? m)))
    (is (false?   (:header? m)))))

(deftest normalize-boolean-coercion-test
  (testing "nil boolean inputs coerce to strict false, not nil"
    (is (false? (:show-copy?    (model/normalize {:show-copy? nil}))))
    (is (false? (:line-numbers? (model/normalize {:line-numbers? nil}))))
    (is (false? (:expanded?     (model/normalize {:expanded? nil}))))))

(deftest normalize-full-test
  (let [m (model/normalize {:code-raw      "const x = 1;\nconst y = 2;"
                            :language-raw  "javascript"
                            :filename-raw  "  app.js "
                            :show-copy?    true
                            :line-numbers? true
                            :wrap?         true
                            :max-lines-raw "1"
                            :expanded?     false})]
    (is (= "js"      (:language m)))
    (is (= "app.js"  (:filename m)) "filename is trimmed")
    (is (= "JS"      (:language-label m)))
    (is (= 2         (:line-count m)))
    (is (true?       (:show-copy? m)))
    (is (true?       (:header? m)))
    (is (true?       (:collapsible? m)) "2 lines over a 1-line cap collapses")
    (is (= 2         (count (:lines m))))))

(deftest normalize-header-without-filename-or-copy-test
  (is (false? (:header? (model/normalize {:language-raw "js"})))
      "a language alone does not force a header"))

(deftest normalize-precomputed-lines-test
  (testing "a supplied :lines value skips the tokenizer"
    (let [supplied [[[:plain "PRECOMPUTED"]]]
          m        (model/normalize {:code-raw     "const ignored = 1;"
                                     :language-raw "js"
                                     :lines        supplied})]
      (is (= supplied (:lines m)))
      (is (= 1 (:line-count m)))))
  (testing "without :lines the tokenizer still runs"
    (let [m (model/normalize {:code-raw "a\nb" :language-raw "text"})]
      (is (= 2 (:line-count m))))))
