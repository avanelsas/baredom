(ns baredom.components.x-multi-combobox.model
  (:require [clojure.string :as str]))

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

;; ── Event name constants ─────────────────────────────────────────────────
(def event-change-request "x-multi-combobox-change-request")
(def event-change         "x-multi-combobox-change")
(def event-input          "x-multi-combobox-input")
(def event-toggle         "x-multi-combobox-toggle")

;; ── Placement constants ──────────────────────────────────────────────────
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")

;; ── UI text ──────────────────────────────────────────────────────────────
(def empty-message "No matches")

;; ── Performance cap ──────────────────────────────────────────────────────
(def max-rendered-options 200)

;; ── Value list separator ────────────────────────────────────────────────
(def value-separator ",")

(def observed-attributes
  #js [attr-value attr-placeholder attr-name attr-disabled
       attr-required attr-open attr-placement attr-max attr-error])

;; ── Property API metadata ────────────────────────────────────────────────
(def property-api
  {:value       {:type 'string  :reflects-attribute attr-value}
   :placeholder {:type 'string  :reflects-attribute attr-placeholder}
   :name        {:type 'string  :reflects-attribute attr-name}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :open        {:type 'boolean :reflects-attribute attr-open}
   :placement   {:type 'string  :reflects-attribute attr-placement}
   :max         {:type 'number  :reflects-attribute attr-max}
   ;; `error` reflects the attribute so x-form's setFieldError can drive the
   ;; inline validation message, mirroring x-form-field.
   :error       {:type 'string  :reflects-attribute attr-error}})

;; ── Event schema ─────────────────────────────────────────────────────────
(def event-schema
  {event-change-request {:cancelable true
                         :detail     {:value 'array :action 'string :item 'string}}
   event-change         {:cancelable false
                         :detail     {:value 'array}}
   event-input          {:cancelable false
                         :detail     {:query 'string}}
   event-toggle         {:cancelable true
                         :detail     {:open 'boolean :source 'string}}})

;; ── Method API ───────────────────────────────────────────────────────────
(def method-api
  {:show {:args [] :returns nil}
   :hide {:args [] :returns nil}})

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
           placement-raw max-raw error-raw]}]
  {:value       (parse-value value-raw)
   :placeholder (or placeholder-raw "")
   :name        (or name-raw "")
   :disabled?   (boolean disabled-present?)
   :required?   (boolean required-present?)
   :open?       (boolean open-present?)
   :placement   (normalize-placement placement-raw)
   :max         (parse-max max-raw)
   :error       (or error-raw "")
   ;; Coerce to a strict boolean: tests call normalize with sparse maps, so
   ;; `(and (string? nil) …)` must not leak nil into a has-error? predicate.
   :has-error?  (boolean (and (string? error-raw) (not= error-raw "")))})

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
