(ns baredom.components.x-date-picker.model
  (:require [baredom.utils.dates :as dates]))

(def tag-name "x-date-picker")

(def attr-mode              "mode")
(def attr-value             "value")
(def attr-start             "start")
(def attr-end               "end")
(def attr-min               "min")
(def attr-max               "max")
(def attr-format            "format")
(def attr-locale            "locale")
(def attr-separator         "separator")
(def attr-auto-swap         "auto-swap")
(def attr-range-allow-same  "range-allow-same-day")
(def attr-close-on-select   "close-on-select")
(def attr-placeholder       "placeholder")
(def attr-disabled          "disabled")
(def attr-readonly          "readonly")
(def attr-required          "required")
(def attr-name              "name")
(def attr-autocomplete      "autocomplete")
(def attr-error             "error")
(def attr-aria-label        "aria-label")
(def attr-aria-describedby  "aria-describedby")

(def event-input          "x-date-picker-input")
(def event-change-request "x-date-picker-change-request")
(def event-change         "x-date-picker-change")

(def observed-attributes
  #js ["mode" "value" "start" "end" "min" "max" "format" "locale" "separator"
       "auto-swap" "range-allow-same-day" "close-on-select" "placeholder"
       "disabled" "readonly" "required" "name" "autocomplete" "error"
       "aria-label" "aria-describedby" "open"])

(def property-api
  {:mode        {:type 'string  :reflects-attribute attr-mode}
   :value       {:type 'string  :reflects-attribute attr-value}
   :start       {:type 'string  :reflects-attribute attr-start}
   :end         {:type 'string  :reflects-attribute attr-end}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly    {:type 'boolean :reflects-attribute attr-readonly}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :open        {:type 'boolean :reflects-attribute "open"}
   ;; `name` reflects the attribute like every other form control (and native
   ;; form inputs), so `el.name` works and x-form can collect this field by name.
   :name        {:type 'string  :reflects-attribute attr-name}
   ;; `error` reflects the attribute so x-form's setFieldError can drive the
   ;; inline validation message, mirroring x-form-field.
   :error       {:type 'string  :reflects-attribute attr-error}})

;; ---------------------------------------------------------------------------
;; String / parsing helpers
;; ---------------------------------------------------------------------------

(defn normalize-str
  "Trim and return nil if empty."
  [s]
  (when (string? s)
    (let [t (.trim s)]
      (when (not= t "") t))))

(defn parse-format
  "Returns :iso or :localized.  Default is :iso."
  [s]
  (if (= s "localized") :localized :iso))

(defn parse-mode
  "Returns :single or :range.  Default is :single."
  [s]
  (if (= s "range") :range :single))

(defn parse-int
  "Parse integer string, returning default-v on failure."
  [s default-v]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (js/isNaN n) default-v n))
    default-v))

;; ---------------------------------------------------------------------------
;; Formatting
;; ---------------------------------------------------------------------------

(defn format-date
  "Format a JS Date for display.  Uses Intl.DateTimeFormat when available."
  [^js d {:keys [format locale]}]
  (when d
    (if (= format :localized)
      (let [opts #js {:year "numeric" :month "long" :day "numeric"
                      :timeZone "UTC"}
            loc  (or locale "default")]
        (.format (js/Intl.DateTimeFormat. loc opts) d))
      (dates/date->iso d))))

;; ---------------------------------------------------------------------------
;; Display string parsing
;; ---------------------------------------------------------------------------

(defn parse-display->single
  "Try to parse a display string to a single date.
   Accepts ISO \"YYYY-MM-DD\". Returns {:ok? boolean :date js/Date|nil}."
  [display]
  (let [s    (normalize-str display)
        date (when s (dates/iso->date s))]
    {:ok? (some? date) :date date}))

(defn parse-display->range
  "Try to parse a display string that may contain a separator.
   Returns {:ok? boolean :start js/Date|nil :end js/Date|nil}."
  [display {:keys [separator]}]
  (let [sep (if (and (string? separator) (pos? (.-length separator))) separator " - ")
        s   (normalize-str display)]
    (if (nil? s)
      {:ok? false :start nil :end nil}
      (let [idx (.indexOf s sep)]
        (if (< idx 0)
          ;; try as single start date
          (let [{:keys [ok? date]} (parse-display->single s)]
            {:ok? ok? :start date :end nil})
          (let [s1 (.trim (.substring s 0 idx))
                s2 (.trim (.substring s (+ idx (.-length sep))))
                d1 (dates/iso->date s1)
                d2 (dates/iso->date s2)]
            {:ok? (and (some? d1) (some? d2)) :start d1 :end d2}))))))

;; ---------------------------------------------------------------------------
;; Model canonicalization
;; ---------------------------------------------------------------------------

(defn canonicalize
  "Build stable model from raw attr strings."
  [{:keys [mode value start end min max format locale separator
           auto-swap? range-allow-same-day?]}]
  (let [m         (parse-mode mode)
        fmt       (parse-format format)
        loc       (normalize-str locale)
        sep       (or (normalize-str separator) " - ")
        min-d     (dates/iso->date min)
        max-d     (dates/iso->date max)
        auto-swap? (boolean auto-swap?)
        allow-same? (boolean range-allow-same-day?)]
    (if (= m :single)
      (let [value-d (dates/iso->date value)
            complete? (some? value-d)]
        {:mode       :single
         :format     fmt
         :locale     loc
         :separator  sep
         :min-d      min-d
         :max-d      max-d
         :auto-swap? auto-swap?
         :allow-same-day? allow-same?
         :value-d    value-d
         :value-str  (or (normalize-str value) "")
         :complete?  complete?})
      (let [start-d  (dates/iso->date start)
            end-d    (dates/iso->date end)
            complete? (and (some? start-d) (some? end-d))]
        {:mode       :range
         :format     fmt
         :locale     loc
         :separator  sep
         :min-d      min-d
         :max-d      max-d
         :auto-swap? auto-swap?
         :allow-same-day? allow-same?
         :start-d    start-d
         :end-d      end-d
         :start-str  (or (normalize-str start) "")
         :end-str    (or (normalize-str end) "")
         :complete?  complete?}))))

;; ---------------------------------------------------------------------------
;; Display value helper
;; ---------------------------------------------------------------------------

(defn display-value
  "Compute the input display string from canonicalized state."
  [canon {:keys [format locale]}]
  (let [config {:format format :locale locale}]
    (if (= (:mode canon) :single)
      (if (:value-d canon)
        (format-date (:value-d canon) config)
        "")
      (let [sep (:separator canon)]
        (cond
          (and (:start-d canon) (:end-d canon))
          (str (format-date (:start-d canon) config)
               sep
               (format-date (:end-d canon) config))
          (:start-d canon)
          (format-date (:start-d canon) config)
          :else "")))))

(def event-schema
  {event-input          {:cancelable false :detail {:value 'string :mode 'string}}
   event-change-request {:cancelable true  :detail {:value 'string :mode 'string :reason 'string}}
   event-change         {:cancelable false :detail {:value 'string :mode 'string :reason 'string}}})

(def method-api
  {:focus  {:args [] :returns 'void}
   :commit {:args [] :returns 'void}
   :clear  {:args [] :returns 'void}})
