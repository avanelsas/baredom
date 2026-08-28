;; metadata.bb — Shared metadata discovery and parsing utilities for BareDOM
;; code generation scripts (generate_types.bb, generate_react.bb, etc.).
;;
;; Usage: (load-file "scripts/metadata.bb")

(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[cheshire.core :as json])

;; ── Configuration ───────────────────────────────────────────────────────────
(def components-dir "src/baredom/components")
(def exports-dir    "src/baredom/exports")
(def dist-dir       "dist")
(def package-json   "package.json")

;; ── Type mapping ────────────────────────────────────────────────────────────
(defn normalize-type-sym
  "Normalize a type value from EDN. Handles:
   - symbol 'boolean (quote prefix as part of name)
   - list (quote boolean)
   - bare symbol boolean"
  [v]
  (cond
    (and (symbol? v) (str/starts-with? (str v) "'"))
    (subs (str v) 1)

    (and (list? v) (= (first v) 'quote))
    (str (second v))

    (symbol? v)
    (str v)

    (string? v)
    v

    :else (str v)))

(defn cljs-type->ts [v]
  (case (normalize-type-sym v)
    "string"  "string"
    "boolean" "boolean"
    "number"  "number"
    "object"  "Record<string, any>"
    "void"    "void"
    ;; DOM interfaces reached through ElementInternals on form-associated
    ;; components, and element references carried in CustomEvent details —
    ;; lib.dom.d.ts already declares them, so emit them verbatim.
    "ValidityState"    "ValidityState"
    "HTMLFormElement"  "HTMLFormElement | null"
    "NodeList"         "NodeList | null"
    "HTMLElement"      "HTMLElement"
    ;; A detail field that is genuinely absent rather than empty — forcing a
    ;; null check at the call site is the point.
    "NullableString"   "string | null"
    "any"))

;; ── Naming conventions ──────────────────────────────────────────────────────
(defn tag->interface-name
  "Convert 'x-button' to 'XButton'."
  [tag-name]
  (->> (str/split tag-name #"-")
       (map str/capitalize)
       (str/join)))

(defn tag->module-name
  "Convert 'x-button' to 'x-button' (identity — used for file names)."
  [tag-name]
  tag-name)

(defn kebab->camel
  "Convert kebab-case to camelCase.
   e.g. 'max-items' -> 'maxItems', 'timeout-ms' -> 'timeoutMs'"
  [s]
  (let [parts (str/split s #"-")]
    (str (first parts)
         (str/join (map str/capitalize (rest parts))))))

;; ── EDN parsing helpers ─────────────────────────────────────────────────────
(defn preprocess-cljs
  "Strip ClojureScript-specific syntax for EDN parsing."
  [s]
  (-> s
      (str/replace #"#js\s*\[" "[")
      (str/replace #"#js\s*\{" "{")
      (str/replace #"\^:private\s+" "")
      (str/replace #"\^:export\s+" "")))

(defn extract-def
  "Extract the value of a (def name ...) form from file text.
   Returns the raw string of the value, or nil if not found."
  [text def-name]
  (let [pattern (re-pattern (str "\\(def\\s+" (java.util.regex.Pattern/quote def-name) "\\s+"))
        matcher (re-matcher pattern text)]
    (when (.find matcher)
      (let [start (.end matcher)
            chars (seq (subs text start))
            result (loop [cs chars depth 0 acc [] in-string false escape false]
                     (if (empty? cs)
                       (str/join acc)
                       (let [c (first cs)]
                         (cond
                           escape
                           (recur (rest cs) depth (conj acc c) in-string false)

                           (= c \\)
                           (recur (rest cs) depth (conj acc c) in-string true)

                           (and (= c \") (not escape))
                           (recur (rest cs) depth (conj acc c) (not in-string) false)

                           in-string
                           (recur (rest cs) depth (conj acc c) in-string false)

                           (or (= c \() (= c \[) (= c \{))
                           (recur (rest cs) (inc depth) (conj acc c) in-string false)

                           (or (= c \)) (= c \]) (= c \}))
                           (if (zero? depth)
                             (str/join acc)
                             (recur (rest cs) (dec depth) (conj acc c) in-string false))

                           :else
                           (recur (rest cs) depth (conj acc c) in-string false)))))]
        (str/trim result)))))

(defn parse-def-value
  "Extract and parse a def value from file text as EDN."
  [text def-name]
  (when-let [raw (extract-def text def-name)]
    (try
      (edn/read-string (preprocess-cljs raw))
      (catch Exception _e))))

(defn extract-tag-name
  "Extract tag-name string from model file text."
  [text]
  (second (re-find #"\(def\s+tag-name\s+\"([^\"]+)\"" text)))

(defn extract-observed-attributes
  "Extract observed-attributes as a vector of strings."
  [text]
  (when-let [raw (extract-def text "observed-attributes")]
    (let [processed (preprocess-cljs raw)]
      (let [attr-defs (re-seq #"\(def\s+(?:\^:private\s+)?(\S+)\s+\"([^\"]+)\"\)" text)
            attr-map  (into {} (map (fn [[_ k v]] [(symbol k) v]) attr-defs))
            syms (try (edn/read-string processed) (catch Exception _))]
        (if (sequential? syms)
          (mapv (fn [s]
                  (if (string? s) s
                      (get attr-map s (str s))))
                syms)
          (vec (map second (re-seq #"\"([^\"]+)\"" raw))))))))

(defn extract-slots
  "Extract slot name constants (def slot-* \"...\") from model text."
  [text]
  (mapv second (re-seq #"\(def\s+slot-\S+\s+\"([^\"]+)\"\)" text)))

(defn extract-string-defs
  "Build a map of symbol -> string for all (def name \"value\") forms."
  [text]
  (into {} (map (fn [[_ k v]] [(symbol k) v])
                (re-seq #"\(def\s+(?:\^:private\s+)?(\S+)\s+\"([^\"]+)\"\)" text))))

;; ── CSS API ─────────────────────────────────────────────────────────────────
;; A component's styling surface lives in its implementation, not its model: the
;; custom properties are written into the CSS it installs, and the parts are the
;; names it stamps on its own elements. Both are read from there so the manifest
;; reports what the component actually exposes rather than a second declaration
;; that could disagree with it.

(defn component-source
  "Every .cljs in `dir`, concatenated. The custom properties are written into the
   CSS the implementation installs, and a part is named in whichever file stamps
   it, which for some components is the model."
  [dir]
  (->> (.listFiles ^java.io.File dir)
       (filter (fn [^java.io.File f]
                 (and (.isFile f) (str/ends-with? (.getName f) ".cljs"))))
       (map slurp)
       (str/join "\n")))

(defn- declared-tokens
  "The custom properties a component names in a `tk-` def. x-theme owns the
   shared vocabulary, so its tokens are not namespaced to its tag and it says
   which they are instead."
  [text]
  (set (map second (re-seq #"\(def\s+(?:\^:private\s+)?tk-[a-z0-9-]+\s+\"(--x-[a-z0-9-]+)\"" text))))

;; A property is namespaced to the component that owns it, and a tag can prefix
;; another tag: --x-table-cell-padding starts with --x-table- but belongs to
;; x-table-cell. Ownership goes to the longest tag that prefixes the property, so
;; a parent styling its children publishes their tokens as theirs, not its own.
(defn- owner-of
  [tags prop]
  (->> tags
       (filter (fn [t] (str/starts-with? prop (str "--" t "-"))))
       (sort-by count)
       last))

(defn- own-property?
  "True when `prop` belongs to `tag`: namespaced to it more closely than to any
   other tag, or named in one of its `tk-` defs."
  [tag tags tokens prop]
  (or (= tag (owner-of tags prop))
      (contains? tokens prop)))

;; Two ways a component says what a property falls back to. It declares a value,
;; `--x-button-gap:0.5rem`, or it reads the property with a fallback,
;; `var(--x-command-palette-width,560px)`. A declaration is the component's own
;; setting and wins. The fallback pattern allows one level of nesting, so
;; `var(--x-a,var(--x-b,#fff))` is read whole.

;; A component that builds its CSS by concatenating strings leaves a symbol name
;; where a value should be. That value is only known at runtime, so it is no
;; default at all rather than a wrong one.
(defn- literal-value
  [v]
  (let [v (str/trim v)]
    (when-not (or (str/blank? v) (re-find #"[\"{}]" v)) v)))

(defn- values-by-pattern
  [re text]
  (into {} (keep (fn [[_ k v]] (when-let [lit (literal-value v)] [k lit])))
        (re-seq re text)))

(defn- declared-values
  [text]
  (values-by-pattern #"(--x-[a-z0-9-]+)\s*:\s*([^;\"}]+)" text))

(defn- fallback-values
  [text]
  (values-by-pattern #"var\(\s*(--x-[a-z0-9-]+)\s*,\s*((?:[^()]|\([^()]*\))*)\)" text))

(defn extract-css-properties
  "The custom properties `tag` exposes, as {:name n} or {:name n :default d}.
   `tags` is every tag in the library, which is what tells a parent's tokens from
   a child's. A property with no default anywhere is one the page is expected to
   supply."
  [tag tags text]
  (let [defaults (merge (fallback-values text) (declared-values text))
        tokens   (declared-tokens text)
        used     (set (re-seq #"--x-[a-z0-9-]+" text))]
    (->> (filter (partial own-property? tag tags tokens) used)
         sort
         (mapv (fn [prop]
                 (let [d (get defaults prop)]
                   (cond-> {:name prop}
                     (seq d) (assoc :default d))))))))

;; Three ways a component names a part: a def, a literal at the call site, and
;; the `attr-part` symbol followed by a literal. A component that computes a part
;; name at runtime, as x-calendar does for one of its buttons, cannot be read
;; statically and that part goes unpublished.
(def ^:private part-patterns
  [#"\(def\s+(?:\^:private\s+)?[a-z-]*part-[a-z0-9-]+\s+\"([a-z0-9-]+)\""
   #"\"part\"\s+\"([a-z0-9-]+)\""
   #"attr-part\s+\"([a-z0-9-]+)\""])

(defn extract-css-parts
  "The shadow parts named in `text`."
  [text]
  (->> (mapcat (fn [re] (map second (re-seq re text))) part-patterns)
       distinct
       sort
       (mapv (fn [n] {:name n}))))

;; ── Model discovery ─────────────────────────────────────────────────────────
;; Two passes: a tag cannot be attributed a custom property until every tag is
;; known, because ownership is decided by the longest tag that prefixes it.
(defn- component-dirs []
  (->> (.listFiles (io/file components-dir))
       (filter (fn [^java.io.File f] (.isDirectory f)))
       (filter (fn [^java.io.File d] (.exists (io/file d "model.cljs"))))))

(defn all-tag-names
  "Every tag the component directories declare."
  []
  (into #{} (keep (fn [d] (extract-tag-name (slurp (io/file d "model.cljs")))))
        (component-dirs)))

(defn discover-models
  "Find all model.cljs files and extract metadata."
  []
  (let [comps-dir (io/file components-dir)
        tags      (all-tag-names)]
    (->> (.listFiles comps-dir)
         (filter #(.isDirectory %))
         (map (fn [dir]
                (let [model-file (io/file dir "model.cljs")]
                  (when (.exists model-file)
                    (let [text   (slurp model-file)
                          tag    (extract-tag-name text)
                          source (component-source dir)]
                      (when tag
                        ;; array-map: ten keys is past the size a map literal keeps
                        ;; in insertion order, and a caller printing or diffing
                        ;; these would see them reshuffle.
                        (array-map
                         :tag-name       tag
                         :dir-name       (.getName dir)
                         :properties     (parse-def-value text "property-api")
                         :events         (parse-def-value text "event-schema")
                         :methods        (parse-def-value text "method-api")
                         :attributes     (extract-observed-attributes text)
                         :slots          (extract-slots text)
                         :css-properties (extract-css-properties tag tags source)
                         :css-parts      (extract-css-parts source)
                         :string-defs    (extract-string-defs text))))))))
         (remove nil?)
         (vec))))

(defn load-package-exports
  "Read package.json and return the set of exported component names (e.g. #{\"x-button\" ...}).

   `prefixes` selects which export names to keep; defaults to [\"x-\"] so the
   framework-adapter generators (which call this with no args) only ever see the
   x- components."
  ([] (load-package-exports ["x-"]))
  ([prefixes]
   (let [pkg (json/parse-string (slurp package-json) true)]
     (->> (:exports pkg)
          keys
          (map name)
          (filter (fn [n] (some #(str/starts-with? n %) prefixes)))
          set))))

;; ── Symbol resolution ────────────────────────────────────────────────────────
(defn resolve-sym
  "Resolve a symbol to its string value using the string-defs map.
   Falls back to (str sym) if not found."
  [sym string-defs]
  (if (symbol? sym)
    (or (get string-defs sym) (str sym))
    (str sym)))

;; ── Event detail ────────────────────────────────────────────────────────────
;; A detail field is the key of the `#js {}` literal the component dispatches, so
;; it reaches JavaScript spelled exactly as the model writes it. It is not
;; camel-cased on the way out, unlike a property name, which reflects an attribute
;; and genuinely changes spelling. Camel-casing it published `pressX` for an
;; `x-particle-button` burst that dispatches `press-x`, so every typed caller read
;; a field that is not there.
(defn event-detail-fields
  "An event detail as [field-name ts-type] pairs.

   A map keeps the order the model declares, which a map literal holds up to eight
   entries; no detail is near that. A set has no order and is sorted.
   Handles both map format {:key 'type} and set format #{:key1 :key2}."
  [detail]
  (cond
    (set? detail) (map (fn [k] [(name k) "string"]) (sort detail))
    (map? detail) (map (fn [[k v]] [(name k) (cljs-type->ts v)]) detail)
    :else         nil))

(defn- ts-property-name
  "`nm` as a TypeScript property name. A name that is not an identifier is quoted,
   which is how `press-x` is written in a type and how it must be read off a
   detail."
  [nm]
  (if (re-matches #"[A-Za-z_$][A-Za-z0-9_$]*" nm) nm (pr-str nm)))

(defn generate-event-detail-type
  "Generate TypeScript type for an event detail."
  [detail]
  (if-let [fields (seq (event-detail-fields detail))]
    (str "{ " (str/join "; " (map (fn [[nm ty]] (str (ts-property-name nm) ": " ty)) fields)) " }")
    "{}"))

;; ── Compound component detection ────────────────────────────────────────────
(def compound-children
  "Map of parent tag-name to child dir-names."
  {"x-welcome-tour"  ["x_welcome_tour_step"]
   "x-tabs"          ["x_tab"]
   "x-timeline"      ["x_timeline_item"]
   "x-table"         ["x_table_row" "x_table_cell"]})

(defn find-child-models
  "Find child component models for a compound parent."
  [parent-tag all-models]
  (when-let [child-dirs (get compound-children parent-tag)]
    (->> all-models
         (filter #(some #{(:dir-name %)} child-dirs))
         vec)))
