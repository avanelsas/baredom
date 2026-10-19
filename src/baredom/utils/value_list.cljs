(ns baredom.utils.value-list
  "A list of values in one attribute, as comma-separated text or as a JSON array of strings."
  (:require [clojure.string :as str]))

;; ── Value list separator ────────────────────────────────────────────────
(def value-separator ",")

;; ── Value formats ───────────────────────────────────────────────────────
(def value-format-csv  "csv")
(def value-format-json "json")
(def allowed-value-formats #{value-format-csv value-format-json})
(def default-value-format value-format-csv)

;; ── Value parsing ────────────────────────────────────────────────────────
(defn parse-value
  "Parses a comma-separated value string into a set of trimmed, non-empty strings.
   nil or empty string yields #{}."
  [s]
  (if (or (nil? s) (= s ""))
    #{}
    (into #{}
          (comp (map str/trim)
                (remove str/blank?))
          (str/split s value-separator))))

(defn serialize-value
  "Serializes a set of values into a sorted, comma-separated string.
   Empty set yields empty string."
  [value-set]
  (if (empty? value-set)
    ""
    (str/join value-separator (sort value-set))))

;; ── Value formats ────────────────────────────────────────────────────────
(defn normalize-value-format
  "Normalizes raw value-format string. Falls back to default-value-format if invalid."
  [s]
  (if (contains? allowed-value-formats s) s default-value-format))

(defn- string-array?
  "True when `x` is a JS array of strings."
  [x]
  (and (array? x) (every? string? (array-seq x))))

(defn parse-json-value
  "Parses a JSON array of strings into a set of its non-empty strings, kept exactly. Returns nil
   when `s` is not a JSON array of strings, and #{} when `s` is nil or empty."
  [s]
  (if (or (nil? s) (= s ""))
    #{}
    (let [parsed (try (js/JSON.parse s) (catch :default _ nil))]
      (when (string-array? parsed)
        (disj (set (array-seq parsed)) "")))))

(defn serialize-json-value
  "Serializes a set of values into a sorted JSON array. Empty set yields empty string."
  [value-set]
  (if (empty? value-set)
    ""
    (js/JSON.stringify (to-array (sort value-set)))))

(defn parse-value-as
  "Parses a value attribute in `value-format` into `{:value set :invalid? boolean}`. An invalid
   json value yields the empty set."
  [value-format s]
  (if (= value-format value-format-json)
    (let [parsed (parse-json-value s)]
      {:value (or parsed #{}) :invalid? (nil? parsed)})
    {:value (parse-value s) :invalid? false}))

(defn serialize-value-as
  "Serializes a set of values in `value-format`."
  [value-format value-set]
  (if (= value-format value-format-json)
    (serialize-json-value value-set)
    (serialize-value value-set)))

(defn- csv-alters?
  "True when a comma-separated value splits or trims the value `v`."
  [v]
  (or (str/includes? v value-separator)
      (not= v (str/trim v))))

(defn csv-unrepresentable
  "The values that a comma-separated value cannot hold: those with the separator, or with
   leading or trailing whitespace."
  [values]
  (filterv csv-alters? values))
