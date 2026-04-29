(ns baredom.components.x-ripple-effect.model)

(def tag-name "x-ripple-effect")

(def attr-intensity "intensity")
(def attr-duration  "duration")
(def attr-frequency "frequency")
(def attr-disabled  "disabled")

(def observed-attributes
  #js [attr-intensity attr-duration attr-frequency attr-disabled])

(def event-start "x-ripple-effect-start")
(def event-end   "x-ripple-effect-end")

(def property-api
  {:intensity {:type 'number}
   :duration  {:type 'number}
   :frequency {:type 'number}
   :disabled  {:type 'boolean}})

(def event-schema
  {event-start {:detail {:x 'number :y 'number} :cancelable false}
   event-end   {:detail {:x 'number :y 'number} :cancelable false}})

(def ^:private default-intensity 25)
(def ^:private default-duration  800)
(def ^:private default-frequency 0.04)

(defn parse-intensity
  "Parse intensity attribute to a number in [1, 100], default 25."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-intensity
        (js/Math.min 100 (js/Math.max 1 n))))
    default-intensity))

(defn parse-duration
  "Parse duration attribute to a positive int in [100, 5000], default 800."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (or (js/isNaN n) (not (pos? n)))
        default-duration
        (js/Math.min 5000 (js/Math.max 100 n))))
    default-duration))

(defn parse-frequency
  "Parse frequency attribute to a float in [0.005, 0.2], default 0.04."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-frequency
        (js/Math.min 0.2 (js/Math.max 0.005 n))))
    default-frequency))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :intensity-raw   string | nil
    :duration-raw    string | nil
    :frequency-raw   string | nil
    :disabled-present? boolean

  Output keys:
    :intensity  number
    :duration   int
    :frequency  number
    :disabled?  boolean"
  [{:keys [intensity-raw duration-raw frequency-raw disabled-present?]}]
  {:intensity (parse-intensity intensity-raw)
   :duration  (parse-duration duration-raw)
   :frequency (parse-frequency frequency-raw)
   :disabled? (boolean disabled-present?)})

(def method-api nil)
