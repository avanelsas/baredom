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
      (catch Exception _e
        nil))))

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
            syms (try (edn/read-string processed) (catch Exception _ nil))]
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

;; ── Model discovery ─────────────────────────────────────────────────────────
(defn discover-models
  "Find all model.cljs files and extract metadata."
  []
  (let [comps-dir (io/file components-dir)]
    (->> (.listFiles comps-dir)
         (filter #(.isDirectory %))
         (map (fn [dir]
                (let [model-file (io/file dir "model.cljs")]
                  (when (.exists model-file)
                    (let [text (slurp model-file)
                          tag  (extract-tag-name text)]
                      (when tag
                        {:tag-name    tag
                         :dir-name    (.getName dir)
                         :properties  (parse-def-value text "property-api")
                         :events      (parse-def-value text "event-schema")
                         :methods     (parse-def-value text "method-api")
                         :attributes  (extract-observed-attributes text)
                         :slots       (extract-slots text)
                         :string-defs (extract-string-defs text)}))))))
         (remove nil?)
         (vec))))

(defn load-package-exports
  "Read package.json and return the set of exported component names (e.g. #{\"x-button\" ...})."
  []
  (let [pkg (json/parse-string (slurp package-json) true)]
    (->> (:exports pkg)
         keys
         (map name)
         (filter #(str/starts-with? % "x-"))
         set)))

;; ── Symbol resolution ────────────────────────────────────────────────────────
(defn resolve-sym
  "Resolve a symbol to its string value using the string-defs map.
   Falls back to (str sym) if not found."
  [sym string-defs]
  (if (symbol? sym)
    (or (get string-defs sym) (str sym))
    (str sym)))

;; ── Event detail type generation ─────────────────────────────────────────────
(defn generate-event-detail-type
  "Generate TypeScript type for an event detail.
   Handles both map format {:key 'type} and set format #{:key1 :key2}."
  [detail]
  (cond
    (or (nil? detail) (and (coll? detail) (empty? detail)))
    "{}"

    (set? detail)
    (let [fields (map #(str (name %) ": string") (sort detail))]
      (str "{ " (str/join "; " fields) " }"))

    (map? detail)
    (let [fields (map (fn [[k v]]
                        (str (name k) ": " (cljs-type->ts v)))
                      detail)]
      (str "{ " (str/join "; " fields) " }"))

    :else "{}"))

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
