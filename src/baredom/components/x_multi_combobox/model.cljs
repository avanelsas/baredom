(ns baredom.components.x-multi-combobox.model
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

(def tag-name "x-multi-combobox")

;; ── Attribute name constants ─────────────────────────────────────────────
(def attr-value       "value")
(def attr-placeholder "placeholder")
(def attr-name        "name")
(def attr-disabled    "disabled")
(def attr-required    "required")
(def attr-open        "open")
(def attr-placement   "placement")
(def attr-max         "max")
(def attr-error       "error")
(def attr-value-format "value-format")

;; ── Event name constants ─────────────────────────────────────────────────
(def event-change-request "x-multi-combobox-change-request")
(def event-change         "x-multi-combobox-change")
(def event-input          "x-multi-combobox-input")
(def event-toggle         "x-multi-combobox-toggle")
(def event-error          "x-multi-combobox-error")

;; ── Placement constants ──────────────────────────────────────────────────
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")

;; ── UI text ──────────────────────────────────────────────────────────────
(def empty-message "No matches")

;; ── Performance cap ──────────────────────────────────────────────────────
(def max-rendered-options 200)

;; ── Value list separator ────────────────────────────────────────────────
(def value-separator ",")

;; ── Value formats ───────────────────────────────────────────────────────
(def value-format-csv  "csv")
(def value-format-json "json")
(def allowed-value-formats #{value-format-csv value-format-json})
(def default-value-format value-format-csv)

(def observed-attributes
  #js [attr-value attr-placeholder attr-name attr-disabled
       attr-required attr-open attr-placement attr-max attr-error
       attr-value-format])

;; ── Property API metadata ────────────────────────────────────────────────
(def property-api
  {:value       {:type 'string  :reflects-attribute attr-value}
   :placeholder {:type 'string  :reflects-attribute attr-placeholder}
   :name        {:type 'string  :reflects-attribute attr-name}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :open        {:type 'boolean :reflects-attribute attr-open}
   :placement   {:type 'string  :reflects-attribute attr-placement :enum allowed-placements}
   :max         {:type 'number  :reflects-attribute attr-max}
   ;; `error` reflects the attribute so x-form's setFieldError can drive the
   ;; inline validation message, mirroring x-form-field.
   :error       {:type 'string  :reflects-attribute attr-error}
   :valueFormat {:type 'string  :reflects-attribute attr-value-format :enum allowed-value-formats}
   :validity          {:type 'ValidityState   :readonly true}
   :validationMessage {:type 'string          :readonly true}
   :willValidate      {:type 'boolean         :readonly true}
   :form              {:type 'HTMLFormElement :readonly true}
   :labels            {:type 'NodeList        :readonly true}})

;; ── Event schema ─────────────────────────────────────────────────────────
(def event-schema
  {event-change-request {:cancelable true
                         :requests   {attr-value :value}
                         :detail     {:value 'array :action 'string :item 'string}}
   event-change         {:cancelable false
                         :detail     {:value 'array}}
   event-input          {:cancelable false
                         :detail     {:query 'string}}
   event-toggle         {:cancelable true
                         :requests   {attr-open :open}
                         :detail     {:open 'boolean :source 'string}}
   event-error          {:cancelable false
                         :detail     {:error 'string :values 'array}}})

;; ── Method API ───────────────────────────────────────────────────────────
(def method-api
  {:show {:args [] :returns nil}
   :hide {:args [] :returns nil}
   :checkValidity  {:args [] :returns 'boolean}
   :reportValidity {:args [] :returns 'boolean}})

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
  "Parses a JSON array of strings into a set of the exact strings. nil or empty string yields #{}.
   Returns nil when `s` is not a JSON array of strings."
  [s]
  (if (or (nil? s) (= s ""))
    #{}
    (let [parsed (try (js/JSON.parse s) (catch :default _ nil))]
      (when (string-array? parsed)
        (set (array-seq parsed))))))

(defn serialize-json-value
  "Serializes a set of values into a sorted JSON array. Empty set yields empty string."
  [value-set]
  (if (empty? value-set)
    ""
    (js/JSON.stringify (to-array (sort value-set)))))

(defn parse-value-as
  "Parses a value attribute in `value-format`. Returns `{:value set :invalid? boolean}`.
   An invalid json value yields the empty set."
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

(defn csv-unrepresentable
  "The values that a comma-separated value cannot hold: those with the separator, or with
   leading or trailing whitespace."
  [values]
  (filterv (fn [v]
             (or (str/includes? v value-separator)
                 (not= v (str/trim v))))
           values))

(defn unrepresentable-message
  "The error text for `values` that a comma-separated value cannot hold."
  [values]
  (str "Cannot store " (js/JSON.stringify (to-array values))
       " as a comma-separated value. Set value-format=\"json\"."))

(defn invalid-json-message
  "The error text for a value attribute `s` that is not a JSON array of strings."
  [s]
  (str "The value " (js/JSON.stringify s) " is not a JSON array of strings."))

;; ── Attribute parsing ────────────────────────────────────────────────────
(defn parse-max
  "Parses max attribute to a positive integer, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseInt s 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        n))))

(defn normalize-placement
  "Normalizes raw placement string. Falls back to default-placement if invalid."
  [s]
  (if (contains? allowed-placements s) s default-placement))

;; ── Normalize ────────────────────────────────────────────────────────────
(defn normalize
  "Derives a complete view-model from raw attribute presence/values."
  [{:keys [value-raw placeholder-raw name-raw
           disabled-present? required-present? open-present?
           placement-raw max-raw error-raw value-format-raw]}]
  (let [value-format             (normalize-value-format value-format-raw)
        {:keys [value invalid?]} (parse-value-as value-format value-raw)]
    {:value          value
     :value-format   value-format
     :value-invalid? (boolean invalid?)
     :placeholder    (or placeholder-raw "")
     :name           (or name-raw "")
     :disabled?      (boolean disabled-present?)
     :required?      (boolean required-present?)
     :open?          (boolean open-present?)
     :placement      (normalize-placement placement-raw)
     :max            (parse-max max-raw)
     :error          (or error-raw "")
     :has-error?     (and (string? error-raw) (not= error-raw ""))}))

;; ── Max enforcement ──────────────────────────────────────────────────────
(defn max-reached?
  "Returns true when a max is set and the value set has reached it."
  [value-set max-val]
  (and (some? max-val) (>= (count value-set) max-val)))

;; ── Option filtering ─────────────────────────────────────────────────────
(defn filter-options
  "Filters options by case-insensitive substring match on label,
   excludes already-selected values, and caps at max-rendered-options."
  [options query selected-set]
  (let [q          (when (and (some? query) (not= query ""))
                     (str/lower-case query))
        match-fn   (if q
                     (fn [{:keys [label]}]
                       (not= -1 (.indexOf (str/lower-case label) q)))
                     (constantly true))]
    (into []
          (comp
           (remove (fn [{:keys [value]}] (contains? selected-set value)))
           (filter match-fn)
           (take max-rendered-options))
          options)))

(defn find-option-by-value
  "Returns the first option map where :value matches, or nil."
  [options value]
  (when (some? value)
    (some (fn [opt] (when (= (:value opt) value) opt)) options)))

(defn chip-label
  "The label of the chip for `value`: the label of its option, or the value when it has none."
  [options value]
  (if-let [option (find-option-by-value options value)]
    (:label option)
    value))

;; ── Navigation helpers ───────────────────────────────────────────────────
(defn next-active-idx
  "Advance active-idx forward, wrapping."
  [n active-idx]
  (if (zero? n) -1 (mod (inc (or active-idx -1)) n)))

(defn prev-active-idx
  "Advance active-idx backward, wrapping."
  [n active-idx]
  (if (zero? n) -1 (mod (dec (or active-idx n)) n)))

(defn clamp-active-idx
  "Returns active-idx bounded by visible-count.
   -1 when there is nothing visible; otherwise the index, capped at the last
   visible position. raw-idx of nil is treated as 0."
  [raw-idx visible-count]
  (if (zero? visible-count)
    -1
    (min (or raw-idx 0) (dec visible-count))))

(defn last-value
  "Returns the largest value in the set in sort order, or nil for an empty set.
   The selected-set is rendered sorted, so this is the value Backspace removes."
  [value-set]
  (when (seq value-set)
    (last (sort value-set))))

;; ── Highlight match ──────────────────────────────────────────────────────
(defn highlight-match
  "Splits label around the first case-insensitive match of query.
   Returns {:before :match :after} or nil if no match."
  [label query]
  (when (and (some? query) (not= query ""))
    (let [lower-label (str/lower-case label)
          lower-query (str/lower-case query)
          idx         (.indexOf lower-label lower-query)]
      (when (not= idx -1)
        (let [end (+ idx (count query))]
          {:before (.substring label 0 idx)
           :match  (.substring label idx end)
           :after  (.substring label end)})))))

;; ── Shown panel ──────────────────────────────────────────────────────────
(def empty-key "empty")

(def ^:private option-id-prefix "x-mcb-opt-")

(defn- shown-option
  "What the option at `position` among the visible ones shows."
  [query active-idx at-max? position {option-key :key :keys [value label]}]
  (let [{:keys [before match after]} (or (highlight-match label query)
                                         {:before label :match "" :after ""})]
    {:key           option-key
     :id            (str option-id-prefix position)
     :value         value
     :active        (when (= position active-idx) "")
     :disabled      (when at-max? "")
     :aria-disabled (when at-max? "true")
     :before        before
     :match         match
     :after         after}))

(defn shown-panel
  "What the panel shows for the model `m`: `:items`, each with its key and the text of what it
   shows, and `:active-id`, the id of the active option or nil. With no option to show, the one
   item is the empty message. The key of an option is made of its value, so an option keeps
   its key while the query and the selection change."
  [options query wanted-active-idx {:keys [value max]}]
  (let [visible    (filter-options (mu/with-unique-keys :value options) query value)
        active-idx (clamp-active-idx wanted-active-idx (count visible))
        at-max?    (max-reached? value max)]
    (if (empty? visible)
      {:items [{:key empty-key :text empty-message}] :active-id nil}
      {:items     (into [] (map-indexed (partial shown-option query active-idx at-max?)) visible)
       :active-id (str option-id-prefix active-idx)})))

