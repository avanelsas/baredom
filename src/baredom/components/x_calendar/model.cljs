(ns baredom.components.x-calendar.model
  "Pure model layer for x-calendar. No DOM, no side effects — every
   function is a value transform. Dates flow as ISO \"YYYY-MM-DD\" strings;
   the canonical model holds only value-comparable data (strings, keywords,
   numbers, sets) so the render change-guard can compare it with `not=`.
   UTC date math lives in baredom.utils.dates."
  (:require [baredom.utils.dates :as dates]))

(def tag-name "x-calendar")

;; ── Attribute names ──────────────────────────────────────────────────────────
(def attr-mode              "mode")
(def attr-value             "value")
(def attr-start             "start")
(def attr-end               "end")
(def attr-min               "min")
(def attr-max               "max")
(def attr-disabled-dates    "disabled-dates")
(def attr-first-day-of-week "first-day-of-week")
(def attr-locale            "locale")
(def attr-month             "month")
(def attr-show-week-numbers "show-week-numbers")
(def attr-disabled          "disabled")
(def attr-range-allow-same  "range-allow-same-day")
(def attr-auto-swap         "auto-swap")

;; ── Event names ──────────────────────────────────────────────────────────────
(def event-change   "x-calendar-change")
(def event-navigate "x-calendar-navigate")

(def observed-attributes
  #js [attr-mode attr-value attr-start attr-end attr-min attr-max
       attr-disabled-dates attr-first-day-of-week attr-locale attr-month
       attr-show-week-numbers attr-disabled attr-range-allow-same
       attr-auto-swap])

(def property-api
  {:mode              {:type 'string  :reflects-attribute attr-mode}
   :value             {:type 'string  :reflects-attribute attr-value}
   :start             {:type 'string  :reflects-attribute attr-start}
   :end               {:type 'string  :reflects-attribute attr-end}
   :min               {:type 'string  :reflects-attribute attr-min}
   :max               {:type 'string  :reflects-attribute attr-max}
   :month             {:type 'string  :reflects-attribute attr-month}
   :locale            {:type 'string  :reflects-attribute attr-locale}
   :firstDayOfWeek    {:type 'string  :reflects-attribute attr-first-day-of-week}
   :disabledDates     {:type 'string  :reflects-attribute attr-disabled-dates}
   :disabled          {:type 'boolean :reflects-attribute attr-disabled}
   :showWeekNumbers   {:type 'boolean :reflects-attribute attr-show-week-numbers}
   :rangeAllowSameDay {:type 'boolean :reflects-attribute attr-range-allow-same}
   :autoSwap          {:type 'boolean :reflects-attribute attr-auto-swap}})

(def event-schema
  {event-change   {:cancelable false
                   :detail {:value 'string :start 'string :end 'string :mode 'string}}
   event-navigate {:cancelable false
                   :detail {:month 'string}}})

(def method-api
  {:focus     {:args [] :returns 'void}
   :goToMonth {:args [{:name "month" :type 'string}] :returns 'void}
   :clear     {:args [] :returns 'void}})

;; ── String / attribute parsing ───────────────────────────────────────────────

(defn- normalize-str
  "Trim and return nil if empty."
  [s]
  (when (string? s)
    (let [t (.trim s)]
      (when (not= t "") t))))

(defn- valid-locale?
  "True when `Intl.DateTimeFormat` accepts s. A malformed BCP-47 tag such as
   \"en_US\" (underscore instead of hyphen) makes the constructor throw
   RangeError."
  [s]
  (try
    (js/Intl.DateTimeFormat. s)
    true
    (catch :default _ false)))

(defn safe-locale
  "Normalize a raw `locale` attribute and return it only when it is a tag
   `Intl.DateTimeFormat` accepts; otherwise nil so callers fall back to the
   runtime default. Guards every Intl call site from a RangeError that would
   otherwise crash the render."
  [s]
  (let [t (normalize-str s)]
    (when (and t (valid-locale? t)) t)))

(defn parse-mode
  "Returns :single or :range. Default is :single."
  [s]
  (if (= s "range") :range :single))

(def ^:private weekday-words
  {"sunday" 0 "monday" 1 "tuesday" 2 "wednesday" 3
   "thursday" 4 "friday" 5 "saturday" 6})

(defn parse-first-dow
  "Parse the first-day-of-week attribute. Accepts a digit 0-6 or a weekday
   word (case-insensitive). Returns 0 (Sunday) for anything unrecognised."
  [s]
  (if (string? s)
    (let [t (.toLowerCase (.trim s))]
      (if-let [w (get weekday-words t)]
        w
        (let [n (js/parseInt t 10)]
          (if (and (not (js/isNaN n)) (>= n 0) (<= n 6)) n 0))))
    0))

(defn parse-disabled-dates
  "Parse a space/comma-separated list of ISO dates into a set of ISO
   strings. Entries that are not valid ISO dates are dropped."
  [s]
  (if (string? s)
    (->> (.split (.trim s) #"[\s,]+")
         (filter #(some? (dates/iso->date %)))
         (set))
    #{}))

(defn parse-year-month
  "Parse a \"YYYY-MM\" (or full \"YYYY-MM-DD\") string to a UTC Date at the
   1st of that month, or nil."
  [s]
  (when (string? s)
    (cond
      (re-matches #"\d{4}-\d{2}" s)       (dates/iso->date (str s "-01"))
      (re-matches #"\d{4}-\d{2}-\d{2}" s) (dates/start-of-month (dates/iso->date s))
      :else nil)))

(defn date->year-month
  "JS Date → \"YYYY-MM\" (the `month` attribute format)."
  [^js d]
  (when d (subs (dates/date->iso d) 0 7)))

;; ── Displayed-month resolution ───────────────────────────────────────────────

(defn displayed-month
  "Resolve which month the grid shows, as a UTC Date on the 1st.
   Priority: explicit `month` attr → month of the selected value/start →
   `today`. `today` must be supplied by the caller (keeps this pure)."
  [{:keys [month-raw value-d start-d ^js today]}]
  (dates/start-of-month
   (or (parse-year-month month-raw)
       value-d
       start-d
       today)))

;; ── Canonical model ──────────────────────────────────────────────────────────

(defn canonicalize
  "Build the stable, value-comparable model from raw attribute strings.
   `today` is a JS Date supplied by the caller. The returned map holds only
   strings/keywords/numbers/sets so the render change-guard can compare it."
  [{:keys [mode value start end min max disabled-dates first-day-of-week
           locale month-raw disabled? show-week-numbers?
           range-allow-same-day? auto-swap? ^js today]}]
  (let [m       (parse-mode mode)
        fdow    (parse-first-dow first-day-of-week)
        value-d (dates/iso->date value)
        start-d (dates/iso->date start)
        end-d   (dates/iso->date end)
        view    (displayed-month {:month-raw month-raw
                                  :value-d   value-d
                                  :start-d   start-d
                                  :today     today})]
    {:mode                  m
     :fdow                  fdow
     :locale                (safe-locale locale)
     :value                 (dates/date->iso value-d)
     :start                 (dates/date->iso start-d)
     :end                   (dates/date->iso end-d)
     :min                   (dates/date->iso (dates/iso->date min))
     :max                   (dates/date->iso (dates/iso->date max))
     :disabled-set          (parse-disabled-dates disabled-dates)
     :view-iso              (dates/date->iso view)
     :disabled?             (boolean disabled?)
     :show-week-numbers?    (boolean show-week-numbers?)
     :range-allow-same-day? (boolean range-allow-same-day?)
     :auto-swap?            (boolean auto-swap?)}))

;; ── Localized labels ─────────────────────────────────────────────────────────

(defn weekday-names
  "Seven short weekday labels, localized, starting at `fdow`
   (0=Sunday..6=Saturday)."
  [locale fdow]
  (let [fmt    (js/Intl.DateTimeFormat. (or locale "default")
                                        #js {:weekday "short" :timeZone "UTC"})
        ;; 2024-01-07 is a Sunday (UTC).
        sunday (js/Date. (js/Date.UTC 2024 0 7))]
    (mapv (fn [i]
            (.format fmt (dates/add-days sunday (mod (+ fdow i) 7))))
          (range 7))))

(defn month-label
  "Localized \"<Month> <Year>\" label for a UTC month Date."
  [^js d locale]
  (when d
    (.format (js/Intl.DateTimeFormat. (or locale "default")
                                      #js {:year "numeric" :month "long"
                                           :timeZone "UTC"})
             d)))

(defn month-options
  "Twelve {:index 0-11 :label \"<Month>\"} entries for the quick-jump panel,
   localized."
  [locale]
  (let [fmt (js/Intl.DateTimeFormat. (or locale "default")
                                     #js {:month "long" :timeZone "UTC"})]
    (mapv (fn [m]
            {:index m
             :label (.format fmt (js/Date. (js/Date.UTC 2024 m 1)))})
          (range 12))))

;; ── Per-cell display flags ───────────────────────────────────────────────────

(defn compute-cell-flags
  "Derive the display flags for a single calendar day cell.
   `model` is the canonicalized map, `cell-iso` the cell's ISO date,
   `in-month?` whether it belongs to the displayed month, and `today-iso`
   today's ISO date. All output booleans are explicitly coerced."
  [{:keys [mode value start end min max disabled-set]} cell-iso in-month? today-iso]
  (let [cell  (dates/iso->date cell-iso)
        min-d (dates/iso->date min)
        max-d (dates/iso->date max)
        out-of-range? (or (and min-d (neg? (dates/compare-date cell min-d)))
                          (and max-d (pos? (dates/compare-date cell max-d))))
        sd    (dates/iso->date start)
        ed    (dates/iso->date end)]
    {:iso         cell-iso
     :in-month?   (boolean in-month?)
     :today?      (boolean (and today-iso (= cell-iso today-iso)))
     :disabled?   (boolean (or out-of-range?
                               (contains? disabled-set cell-iso)))
     :selected?   (boolean (or (= cell-iso value)
                               (= cell-iso start)
                               (= cell-iso end)))
     :in-range?   (boolean (and (= mode :range) sd ed
                                (dates/in-range? cell sd ed)))
     :range-edge? (boolean (and (= mode :range)
                                (or (= cell-iso start) (= cell-iso end))))}))
