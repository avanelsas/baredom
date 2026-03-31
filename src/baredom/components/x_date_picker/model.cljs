(ns baredom.components.x-date-picker.model)

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
(def attr-aria-label        "aria-label")
(def attr-aria-describedby  "aria-describedby")

(def event-input          "x-date-picker-input")
(def event-change-request "x-date-picker-change-request")
(def event-change         "x-date-picker-change")

(def observed-attributes
  #js ["mode" "value" "start" "end" "min" "max" "format" "locale" "separator"
       "auto-swap" "range-allow-same-day" "close-on-select" "placeholder"
       "disabled" "readonly" "required" "name" "autocomplete"
       "aria-label" "aria-describedby" "open"])

(def property-api
  {:mode        {:type 'string  :reflects-attribute attr-mode}
   :value       {:type 'string  :reflects-attribute attr-value}
   :start       {:type 'string  :reflects-attribute attr-start}
   :end         {:type 'string  :reflects-attribute attr-end}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly    {:type 'boolean :reflects-attribute attr-readonly}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :open        {:type 'boolean :reflects-attribute "open"}})

;; ---------------------------------------------------------------------------
;; String / parsing helpers
;; ---------------------------------------------------------------------------

(defn normalize-str
  "Trim and return nil if empty."
  [s]
  (when (string? s)
    (let [t (.trim s)]
      (when (not= t "") t))))

(defn parse-bool-attr
  "Returns true if attr string is non-nil and not \"false\"."
  [s]
  (and (some? s) (not= s "false")))

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
;; UTC date primitives
;; ---------------------------------------------------------------------------

(defn pad2
  "Zero-pad integer n to 2 digits."
  [n]
  (if (< n 10) (str "0" n) (str n)))

(defn date->iso
  "JS Date → \"YYYY-MM-DD\" using UTC fields."
  [^js d]
  (when d
    (str (.getUTCFullYear d)
         "-" (pad2 (inc (.getUTCMonth d)))
         "-" (pad2 (.getUTCDate d)))))

(defn iso->date
  "\"YYYY-MM-DD\" → JS Date at UTC midnight, or nil on parse failure."
  [s]
  (when (and (string? s) (re-matches #"\d{4}-\d{2}-\d{2}" s))
    (let [^js d (js/Date. (str s "T00:00:00.000Z"))]
      (when-not (js/isNaN (.getTime d)) d))))

(defn compare-date
  "Returns -1, 0, or 1 comparing two UTC dates by day only."
  [^js a ^js b]
  (cond
    (and (nil? a) (nil? b)) 0
    (nil? a) -1
    (nil? b) 1
    :else
    (let [at (.getTime a)
          bt (.getTime b)]
      (cond (< at bt) -1
            (> at bt) 1
            :else 0))))

(defn min-date
  [^js a ^js b]
  (if (neg? (compare-date a b)) a b))

(defn max-date
  [^js a ^js b]
  (if (pos? (compare-date a b)) a b))

(defn add-days
  "Return a new Date that is d + n days (UTC)."
  [^js d n]
  (when d
    (let [t (.getTime d)]
      (js/Date. (+ t (* n 86400000))))))

(defn start-of-month
  "Return UTC midnight on the 1st of d's month."
  [^js d]
  (when d
    (js/Date. (js/Date.UTC (.getUTCFullYear d) (.getUTCMonth d) 1))))

(defn days-in-month
  "Number of days in d's UTC month."
  [^js d]
  (when d
    (.getUTCDate (js/Date. (js/Date.UTC (.getUTCFullYear d)
                                        (inc (.getUTCMonth d))
                                        0)))))

(defn weekday-0-sun
  "Day of week 0=Sunday..6=Saturday for d's UTC date."
  [^js d]
  (when d (.getUTCDay d)))

(defn add-months
  "Return a new Date that is d +/- n months (UTC). Clamps day to target month's
   last day when overflow would occur (e.g. Jan 31 + 1 → Feb 28)."
  [^js d n]
  (when d
    (let [y   (.getUTCFullYear d)
          m   (.getUTCMonth d)
          day (.getUTCDate d)
          tm  (+ m n)
          last-day (.getUTCDate (js/Date. (js/Date.UTC y (inc tm) 0)))]
      (js/Date. (js/Date.UTC y tm (min day last-day))))))

(defn start-of-week
  "Return the Sunday of d's week (UTC)."
  [^js d]
  (when d (add-days d (- (weekday-0-sun d)))))

(defn end-of-week
  "Return the Saturday of d's week (UTC)."
  [^js d]
  (when d (add-days d (- 6 (weekday-0-sun d)))))

(defn clamp-to-range
  "Clamp d to [min-d, max-d]. Either bound may be nil (unbounded)."
  [^js d ^js min-d ^js max-d]
  (let [d1 (if (and min-d d (neg? (compare-date d min-d))) min-d d)
        d2 (if (and max-d d1 (pos? (compare-date d1 max-d))) max-d d1)]
    d2))

(defn in-range?
  "True if d is between a and b inclusive (either order)."
  [^js d ^js a ^js b]
  (when (and d a b)
    (let [lo (if (neg? (compare-date a b)) a b)
          hi (if (neg? (compare-date a b)) b a)]
      (and (not (neg? (compare-date d lo)))
           (not (pos? (compare-date d hi)))))))

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
      (date->iso d))))

;; ---------------------------------------------------------------------------
;; Display string parsing
;; ---------------------------------------------------------------------------

(defn parse-display->single
  "Try to parse a display string to a single date.
   Accepts ISO \"YYYY-MM-DD\". Returns {:ok? boolean :date js/Date|nil}."
  [display]
  (let [s    (normalize-str display)
        date (when s (iso->date s))]
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
                d1 (iso->date s1)
                d2 (iso->date s2)]
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
        min-d     (iso->date min)
        max-d     (iso->date max)
        auto-swap? (boolean auto-swap?)
        allow-same? (boolean range-allow-same-day?)]
    (if (= m :single)
      (let [value-d (iso->date value)
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
      (let [start-d  (iso->date start)
            end-d    (iso->date end)
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
;; Month grid
;; ---------------------------------------------------------------------------

(defn month-grid
  "Return a 42-item vector for a calendar view of the month containing d.
   Each entry is {:date jsDate :in-month? boolean}."
  [^js d]
  (when d
    (let [som      (start-of-month d)
          dow      (weekday-0-sun som)
          grid-start (add-days som (- dow))]
      (mapv (fn [i]
              (let [^js cell-date (add-days grid-start i)
                    in-month?    (= (.getUTCMonth cell-date) (.getUTCMonth d))]
                {:date cell-date :in-month? in-month?}))
            (range 42)))))

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
