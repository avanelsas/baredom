#!/usr/bin/env bb
;; sync_doc_defaults.bb — Write the default of each custom property into its row
;; in the component docs.
;;
;; The manifest is the source. A doc keeps its own rows, descriptions and dark
;; columns. Only the default cell of a row is written.
;;
;; A table is read when its lines start with a pipe and its second column is
;; headed as a default. A table inside a code fence is read as a table too.
;;
;; Usage: bb scripts/sync_doc_defaults.bb

(require '[cheshire.core :as json]
         '[clojure.java.io :as io]
         '[clojure.string :as str])

(def ^:private manifest-file "custom-elements.json")
(def ^:private docs-dir "docs")

;; ── the manifest ────────────────────────────────────────────────────────────

(defn- properties
  "A map from each custom property of `manifest` to its default, or to nil when it has none."
  [manifest]
  (into {}
        (for [module      (:modules manifest)
              declaration (:declarations module)
              property    (:cssProperties declaration)]
          [(:name property) (:default property)])))

;; ── a table ─────────────────────────────────────────────────────────────────

(defn- table-line? [line]
  (str/starts-with? (str/triml line) "|"))

(defn- cells
  "The cells of a table line, with the text before the first pipe and after the last."
  [line]
  (str/split line #"\|" -1))

(defn- property-of
  "The custom property that a table line documents, or nil."
  [line]
  (second (re-find #"^\s*`(--x-[a-z0-9-]+)`\s*$" (get (cells line) 1 ""))))

(defn- default-column?
  "True when the heading of a table calls its second column a default."
  [heading]
  (boolean (re-find #"(?i)default" (get (cells heading) 2 ""))))

(defn- as-wide-as
  "`text` with spaces at its end, up to the width of `cell`."
  [text cell]
  (let [trimmed (str/trimr text)]
    (str trimmed (apply str (repeat (max 1 (- (count cell) (count trimmed))) " ")))))

(defn- with-default
  "`cell` with `default` in place of its first value in backticks, or in place of all its
   text when it has none."
  [cell default]
  (let [code (str "`" default "`")]
    (as-wide-as (if (re-find #"`[^`]*`" cell)
                  (str/replace-first cell #"`[^`]*`" (fn [_] code))
                  (str " " code))
                cell)))

(defn- synced-line
  "A table line with the default of its property from `defaults`, when that property has one."
  [defaults line]
  (if-let [default (defaults (property-of line))]
    (str/join "|" (update (cells line) 2 with-default default))
    line))

(defn- synced-table
  "The lines of a table with their defaults synced, when its second column holds defaults."
  [defaults [heading :as lines]]
  (if (default-column? heading)
    (map (partial synced-line defaults) lines)
    lines))

(defn- synced-run
  "A run of lines that are all of a table, or all outside one, with its defaults synced."
  [defaults lines]
  (if (table-line? (first lines))
    (synced-table defaults lines)
    lines))

(defn- synced
  "The text of a doc with the default cell of every property row from `defaults`."
  [defaults text]
  (->> (str/split text #"\n" -1)
       (partition-by table-line?)
       (mapcat (partial synced-run defaults))
       (str/join "\n")))

(defn- outdated
  "The docs of `texts`, a map from a doc to its text, whose text changes when synced. Each
   comes with its new text."
  [defaults texts]
  (into {}
        (for [[doc text] texts
              :let  [new-text (synced defaults text)]
              :when (not= text new-text)]
          [doc new-text])))

;; ── the report ──────────────────────────────────────────────────────────────

(defn- documented
  "The custom properties that have a row in `text`."
  [text]
  (keep property-of (str/split text #"\n")))

(defn- doc-files []
  (sort (filter #(re-find #"^x-.*\.md$" (.getName ^java.io.File %))
                (.listFiles (io/file docs-dir)))))

(defn- write! [[doc text]]
  (spit doc text))

(defn- main []
  (let [props   (properties (json/parse-string (slurp manifest-file) true))
        texts   (into {} (map (juxt identity slurp) (doc-files)))
        stale   (outdated (into {} (filter val props)) texts)
        rows    (set (mapcat documented (vals (merge texts stale))))
        no-row  (remove rows (keys props))
        unknown (remove (set (keys props)) rows)]
    (run! write! stale)
    (println (format "Wrote %d of %d docs." (count stale) (count texts)))
    (println (format "%d of %d custom properties have no row in a doc." (count no-row) (count props)))
    (when (seq unknown)
      (println "Rows for a property the manifest does not list:" (str/join ", " (sort unknown))))))

(when (= *file* (System/getProperty "babashka.file"))
  (main))
