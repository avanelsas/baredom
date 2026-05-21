(ns baredom.components.x-code.model
  "Pure model layer for x-code — a code-display component with an in-house,
   regex-based syntax tokenizer.

   No DOM, no side effects. Every function here is a plain data transform and
   is exercised directly by model_test.cljs with sparse inputs.

   The tokenizer is deliberately lightweight. It covers js / json / css / html
   only and degrades any other language to plain text. It is a highlighter,
   not a parser: it does not track nested languages, and a handful of
   context-sensitive cases are knowingly imperfect (see docs/x-code.md)."
  (:require [clojure.string :as str]))

;; ── Tag & attribute constants ────────────────────────────────────────────────
(def tag-name "x-code")

(def attr-language     "language")
(def attr-filename     "filename")
(def attr-show-copy    "show-copy")
(def attr-line-numbers "line-numbers")
(def attr-wrap         "wrap")
(def attr-max-lines    "max-lines")
(def attr-expanded     "expanded")
(def attr-code         "code")

(def observed-attributes
  #js ["language" "filename" "show-copy" "line-numbers"
       "wrap" "max-lines" "expanded" "code"])

;; ── Event constants ──────────────────────────────────────────────────────────
(def event-copy   "x-code-copy")
(def event-toggle "x-code-toggle")

;; ── CSS custom-property prefix ───────────────────────────────────────────────
(def css-prefix "--x-code-")

;; ── Languages ────────────────────────────────────────────────────────────────
(def lang-js   "js")
(def lang-json "json")
(def lang-css  "css")
(def lang-html "html")
(def lang-text "text")

(def ^:private lang-aliases
  {"js"         lang-js
   "javascript" lang-js
   "jsx"        lang-js
   "mjs"        lang-js
   "json"       lang-json
   "css"        lang-css
   "html"       lang-html
   "htm"        lang-html
   "xml"        lang-html
   "svg"        lang-html
   ""           lang-text
   "text"       lang-text
   "txt"        lang-text
   "plain"      lang-text})

(def default-language  lang-text)
(def default-max-lines 0)

;; ── API metadata ─────────────────────────────────────────────────────────────
(def property-api
  {:language    {:type 'string  :reflects-attribute attr-language     :default ""}
   :filename    {:type 'string  :reflects-attribute attr-filename     :default ""}
   :showCopy    {:type 'boolean :reflects-attribute attr-show-copy}
   :lineNumbers {:type 'boolean :reflects-attribute attr-line-numbers}
   :wrap        {:type 'boolean :reflects-attribute attr-wrap}
   :maxLines    {:type 'number  :reflects-attribute attr-max-lines    :default 0}
   :expanded    {:type 'boolean :reflects-attribute attr-expanded}
   :code        {:type 'string  :reflects-attribute nil
                  :note "property-only override; falls back to light-DOM textContent"}})

(def event-schema
  {event-copy   {:cancelable false :detail {:code 'string}}
   event-toggle {:cancelable false :detail {:expanded 'boolean}}})

(def method-api
  {:expand   {:args [] :returns 'void}
   :collapse {:args [] :returns 'void}})

;; ── Attribute parsers ────────────────────────────────────────────────────────
(defn parse-language
  "Canonicalise a `language` attribute. Aliases map onto js/json/css/html;
   anything unknown (including nil) becomes `text`."
  [s]
  (let [k (when (string? s) (.toLowerCase (.trim s)))]
    (get lang-aliases k default-language)))

(defn parse-max-lines
  "Parse the `max-lines` attribute into a non-negative integer. 0 (the
   default for nil / non-numeric / non-positive input) means no collapsing."
  [s]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (not (js/isNaN n)) (pos? n)) n default-max-lines))
    default-max-lines))

;; ── Code preparation (dedent + trim edges) ───────────────────────────────────
(defn- blank-line? [l]
  (= "" (.trim l)))

(defn- leading-ws-count [l]
  (count (re-find #"^[\t ]*" l)))

(defn dedent
  "Remove the longest run of leading whitespace shared by every non-blank
   line. Lets authors indent slotted code to match surrounding markup
   without that indentation showing up in the rendered block."
  [s]
  (let [lines     (vec (.split s "\n"))
        non-blank (remove blank-line? lines)
        common    (if (seq non-blank)
                    (reduce min (map leading-ws-count non-blank))
                    0)]
    (if (pos? common)
      (str/join "\n"
                (map (fn [l] (if (>= (count l) common) (subs l common) l))
                     lines))
      s)))

(defn- strip-edge-blank-lines
  "Drop fully blank leading and trailing lines."
  [s]
  (let [lines (vec (.split s "\n"))
        n     (count lines)
        start (loop [i 0]
                (if (and (< i n) (blank-line? (nth lines i)))
                  (recur (inc i))
                  i))
        end   (loop [i (dec n)]
                (if (and (>= i start) (blank-line? (nth lines i)))
                  (recur (dec i))
                  i))]
    (if (<= start end)
      (str/join "\n" (subvec lines start (inc end)))
      "")))

(defn prepare-code
  "Normalise raw code for display: convert CRLF / CR line endings to LF, drop
   leading/trailing blank lines, then strip common indentation. Non-string
   input becomes the empty string."
  [s]
  (if (string? s)
    (-> s
        (str/replace #"\r\n?" "\n")
        strip-edge-blank-lines
        dedent)
    ""))

;; ── Tokenizer ────────────────────────────────────────────────────────────────
;;
;; Each language declares an ordered vector of [token-type pattern]. The
;; patterns are combined into a single regex — every pattern wrapped in one
;; capturing group, joined with `|`. The capture-group index of a match
;; identifies its token type. Order matters: comments and strings come first
;; so a keyword inside a string is never mis-classified (first match wins).

(def ^:private js-spec
  [[:comment  #"/\*[\s\S]*?\*/|//[^\n]*"]
   [:template #"`(?:[^`\\]|\\[\s\S])*`"]
   [:string   #"\"(?:[^\"\\\n]|\\.)*\"|'(?:[^'\\\n]|\\.)*'"]
   [:keyword  #"\b(?:const|let|var|function|return|if|else|for|while|do|switch|case|break|continue|new|delete|typeof|instanceof|class|extends|super|this|import|export|from|as|default|try|catch|finally|throw|async|await|yield|void|null|undefined|true|false|NaN|Infinity)\b"]
   [:number   #"0[xX][0-9a-fA-F]+|(?:\d+\.?\d*|\.\d+)(?:[eE][+-]?\d+)?"]
   [:punct    #"[-+*/%=<>!&|?:;,.~^{}()\[\]]"]])

(def ^:private json-spec
  [[:key     #"\"(?:[^\"\\]|\\.)*\"(?=\s*:)"]
   [:string  #"\"(?:[^\"\\]|\\.)*\""]
   [:number  #"-?(?:\d+\.?\d*|\.\d+)(?:[eE][+-]?\d+)?"]
   [:keyword #"\b(?:true|false|null)\b"]
   [:punct   #"[{}\[\]:,]"]])

(def ^:private css-spec
  [[:comment  #"/\*[\s\S]*?\*/"]
   [:string   #"\"(?:[^\"\\\n]|\\.)*\"|'(?:[^'\\\n]|\\.)*'"]
   [:atrule   #"@[\w-]+"]
   [:property #"[A-Za-z-][A-Za-z0-9-]*(?=\s*:)"]
   [:number   #"(?:\d+\.?\d*|\.\d+)(?:%|[a-zA-Z]+)?"]
   [:punct    #"[{}();:,]"]])

(def ^:private html-spec
  [[:comment    #"<!--[\s\S]*?-->"]
   [:tag        #"<![^>]*>"]
   [:tag        #"</?[A-Za-z][\w-]*"]
   [:attr-value #"\"[^\"]*\"|'[^']*'"]
   [:entity     #"&(?:#\d+|#[xX][0-9a-fA-F]+|[A-Za-z][A-Za-z0-9]*);"]
   [:punct      #"[<>/=]"]])

(defn- compile-spec
  "Turn a [[type pattern] ...] spec into {:source combined-regex-string
   :types [type ...]}."
  [spec]
  {:source (str/join "|" (map (fn [[_ ^js re]] (str "(" (.-source re) ")")) spec))
   :types  (mapv first spec)})

(def ^:private js-compiled   (compile-spec js-spec))
(def ^:private json-compiled (compile-spec json-spec))
(def ^:private css-compiled  (compile-spec css-spec))
(def ^:private html-compiled (compile-spec html-spec))

(defn- matched-group
  "Index (1-based) of the first capturing group that matched, capped so a
   pathological non-match cannot loop forever."
  [^js m n]
  (loop [i 1]
    (cond
      (> i n)              1
      (some? (aget m i))   i
      :else                (recur (inc i)))))

(defn- scan
  "Run the combined tokenizer regex over `code`, returning a flat vector of
   [type text] tokens. `types` maps capture-group index → token type;
   everything between matches is emitted as a :plain token."
  [code source types]
  (let [^js re (js/RegExp. source "g")
        n      (count types)
        len    (count code)]
    (loop [tokens (transient [])
           cursor 0]
      (let [^js m (.exec re code)]
        (if (nil? m)
          (persistent! (if (< cursor len)
                         (conj! tokens [:plain (subs code cursor)])
                         tokens))
          (let [text  (aget m 0)
                start (.-index m)
                ttype (nth types (dec (matched-group m n)))
                gap   (when (> start cursor)
                        [:plain (subs code cursor start)])
                end   (+ start (.-length text))]
            (when (zero? (.-length text))
              (set! (.-lastIndex re) (inc (.-lastIndex re))))
            (recur (cond-> tokens
                     gap  (conj! gap)
                     true (conj! [ttype text]))
                   end)))))))

(defn tokenize
  "Tokenize `code` for `language` into a flat [[type text] ...] vector.
   The `text` language and any unknown language degrade to a single :plain
   token spanning the whole input."
  [language code]
  (let [code (str code)]
    (cond
      (= "" code)            []
      (= language lang-js)   (scan code (:source js-compiled)   (:types js-compiled))
      (= language lang-json) (scan code (:source json-compiled) (:types json-compiled))
      (= language lang-css)  (scan code (:source css-compiled)  (:types css-compiled))
      (= language lang-html) (scan code (:source html-compiled) (:types html-compiled))
      :else                  [[:plain code]])))

(defn tokens->lines
  "Split a flat token vector into per-line token vectors. A token whose text
   spans newlines is split so each line carries only its own slice."
  [tokens]
  (loop [ts      (seq tokens)
         current []
         lines   []]
    (if (nil? ts)
      (conj lines current)
      (let [[ttype text] (first ts)
            parts         (vec (.split text "\n"))
            k             (count parts)]
        (if (= k 1)
          (recur (next ts) (conj current [ttype text]) lines)
          (recur (next ts)
                 [[ttype (peek parts)]]
                 (-> lines
                     (conj (conj current [ttype (first parts)]))
                     (into (map (fn [p] [[ttype p]]) (subvec parts 1 (dec k)))))))))))

;; ── Derived helpers ──────────────────────────────────────────────────────────
(defn collapsible?
  "True when `max-lines` is positive and the code has more lines than that."
  [max-lines line-count]
  (boolean (and (pos? max-lines) (> line-count max-lines))))

(defn language-label
  "Short uppercase badge for a language, or \"\" for plain text."
  [language]
  (if (= language lang-text) "" (.toUpperCase language)))

;; ── Normalize ────────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw inputs into the render-ready view-model.

   Input keys:
     :code-raw       string | nil   (already resolved from property/attr/text)
     :language-raw   string | nil
     :filename-raw   string | nil
     :show-copy?     boolean
     :line-numbers?  boolean
     :wrap?          boolean
     :max-lines-raw  string | nil
     :expanded?      boolean
     :lines          optional precomputed per-line tokens. When supplied
                     (a caller holding a tokenization for the same code +
                     language) the tokenizer is skipped; otherwise :lines is
                     computed from code + language.

   Output keys:
     :code            prepared code string
     :language        canonical language id
     :language-label  uppercase badge | \"\"
     :filename        trimmed filename | \"\"
     :show-copy?      boolean
     :line-numbers?   boolean
     :wrap?           boolean
     :max-lines       int >= 0
     :expanded?       boolean
     :line-count      int
     :lines           [[[type text] ...] ...]  per-line tokens
     :collapsible?    boolean
     :header?         boolean"
  [{:keys [code-raw language-raw filename-raw show-copy?
           line-numbers? wrap? max-lines-raw expanded? lines]}]
  (let [language  (parse-language language-raw)
        code      (prepare-code code-raw)
        filename  (let [f (when (string? filename-raw) (.trim filename-raw))]
                    (if (and f (not= "" f)) f ""))
        max-lines (parse-max-lines max-lines-raw)
        lines     (or lines (tokens->lines (tokenize language code)))
        n         (count lines)
        show?     (boolean show-copy?)]
    {:code           code
     :language       language
     :language-label (language-label language)
     :filename       filename
     :show-copy?     show?
     :line-numbers?  (boolean line-numbers?)
     :wrap?          (boolean wrap?)
     :max-lines      max-lines
     :expanded?      (boolean expanded?)
     :line-count     n
     :lines          lines
     :collapsible?   (collapsible? max-lines n)
     :header?        (boolean (or show? (not= "" filename)))}))
