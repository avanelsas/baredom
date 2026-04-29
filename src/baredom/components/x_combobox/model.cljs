(ns baredom.components.x-combobox.model
  (:require [clojure.string :as str]))

(def tag-name "x-combobox")

;; Attribute name constants
(def attr-value       "value")
(def attr-placeholder "placeholder")
(def attr-name        "name")
(def attr-disabled    "disabled")
(def attr-required    "required")
(def attr-open        "open")
(def attr-placement   "placement")

;; Event name constants
(def event-change "x-combobox-change")
(def event-input  "x-combobox-input")
(def event-toggle "x-combobox-toggle")

;; Placement constants
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")

;; UI text
(def empty-message "No matches")

(def observed-attributes
  #js [attr-value attr-placeholder attr-name attr-disabled
       attr-required attr-open attr-placement])

(def property-api
  {:value       {:type 'string  :reflects-attribute attr-value}
   :placeholder {:type 'string  :reflects-attribute attr-placeholder}
   :name        {:type 'string  :reflects-attribute attr-name}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :open        {:type 'boolean :reflects-attribute attr-open}
   :placement   {:type 'string  :reflects-attribute attr-placement}})

(def event-schema
  {event-change {:cancelable false
                 :detail     {:value 'string :label 'string}}
   event-input  {:cancelable false
                 :detail     {:query 'string}}
   event-toggle {:cancelable true
                 :detail     {:open 'boolean :source 'string}}})

;; ---------------------------------------------------------------------------
;; Normalization
;; ---------------------------------------------------------------------------
(defn normalize-placement
  "Normalizes raw placement string. Falls back to default-placement if invalid or nil."
  [s]
  (if (contains? allowed-placements s) s default-placement))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [value-raw placeholder-raw name-raw
           disabled-present? required-present? open-present? placement-raw]}]
  {:value       (or value-raw "")
   :placeholder (or placeholder-raw "")
   :name        (or name-raw "")
   :disabled?   (boolean disabled-present?)
   :required?   (boolean required-present?)
   :open?       (boolean open-present?)
   :placement   (normalize-placement placement-raw)})

;; ---------------------------------------------------------------------------
;; Option filtering
;; ---------------------------------------------------------------------------
(defn filter-options
  "Filters options by case-insensitive substring match on label.
   Returns all options when query is nil or empty."
  [options query]
  (if (or (nil? query) (= query ""))
    options
    (let [q (str/lower-case query)]
      (filterv (fn [{:keys [label]}]
                 (not= -1 (.indexOf (str/lower-case label) q)))
               options))))

(defn find-option-by-value
  "Returns the first option map where :value matches, or nil."
  [options value]
  (when (some? value)
    (some (fn [opt] (when (= (:value opt) value) opt)) options)))

;; ---------------------------------------------------------------------------
;; Navigation helpers
;; ---------------------------------------------------------------------------
(defn next-active-idx
  "Advance active-idx forward, wrapping."
  [n active-idx]
  (if (zero? n) -1 (mod (inc (or active-idx -1)) n)))

(defn prev-active-idx
  "Advance active-idx backward, wrapping."
  [n active-idx]
  (if (zero? n) -1 (mod (dec (or active-idx n)) n)))

;; ---------------------------------------------------------------------------
;; Highlight match
;; ---------------------------------------------------------------------------
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

(def method-api nil)
