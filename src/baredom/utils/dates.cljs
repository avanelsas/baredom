(ns baredom.utils.dates
  "Pure UTC date primitives shared by date components (x-date-picker,
   x-calendar). No DOM, no side effects — every function is a value
   transform. Dates are JS `Date` objects pinned to UTC midnight; ISO
   strings are `\"YYYY-MM-DD\"`.

   `first-day-of-week` (`fdow`) is `0`=Sunday .. `6`=Saturday. The
   `start-of-week` / `end-of-week` / `month-grid` functions default to
   `0` (Sunday) so existing single-arity callers keep their behaviour.")

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
  "Return a new Date that is d +/- n months (UTC). Clamps day to target
   month's last day when overflow would occur (e.g. Jan 31 + 1 → Feb 28)."
  [^js d n]
  (when d
    (let [y   (.getUTCFullYear d)
          m   (.getUTCMonth d)
          day (.getUTCDate d)
          tm  (+ m n)
          last-day (.getUTCDate (js/Date. (js/Date.UTC y (inc tm) 0)))]
      (js/Date. (js/Date.UTC y tm (min day last-day))))))

(defn start-of-week
  "Return the start of d's week (UTC). `fdow` is the first day of the week
   (0=Sunday..6=Saturday); defaults to 0 (Sunday)."
  ([^js d] (start-of-week d 0))
  ([^js d fdow]
   (when d
     (add-days d (- (mod (- (weekday-0-sun d) fdow) 7))))))

(defn end-of-week
  "Return the last day of d's week (UTC), 6 days after `start-of-week`.
   `fdow` defaults to 0 (Sunday → Saturday)."
  ([^js d] (end-of-week d 0))
  ([^js d fdow]
   (when d (add-days (start-of-week d fdow) 6))))

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

(defn month-grid
  "Return a 42-item vector for a calendar view of the month containing d.
   Each entry is {:date jsDate :in-month? boolean}. `fdow` is the first
   day of the week (0=Sunday..6=Saturday); defaults to 0 (Sunday)."
  ([^js d] (month-grid d 0))
  ([^js d fdow]
   (when d
     (let [som        (start-of-month d)
           grid-start (start-of-week som fdow)]
       (mapv (fn [i]
               (let [^js cell-date (add-days grid-start i)
                     in-month?     (= (.getUTCMonth cell-date)
                                      (.getUTCMonth d))]
                 {:date cell-date :in-month? in-month?}))
             (range 42))))))

(defn iso-week-number
  "ISO-8601 week number (1-53) for d's UTC date. Weeks start Monday; week 1
   is the week containing the first Thursday of the year."
  [^js d]
  (when d
    (let [wd      (.getUTCDay d)
          ;; ISO weekday: Monday=1 .. Sunday=7
          iso-wd  (if (zero? wd) 7 wd)
          ;; the Thursday of d's ISO week determines the week-numbering year
          ^js thu (add-days d (- 4 iso-wd))
          jan1    (js/Date. (js/Date.UTC (.getUTCFullYear thu) 0 1))
          days    (/ (- (.getTime thu) (.getTime jan1)) 86400000)]
      (inc (js/Math.floor (/ days 7))))))
