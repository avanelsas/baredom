(ns baredom.components.x-splash.model)

(def tag-name "x-splash")

(def attr-active   "active")
(def attr-variant  "variant")
(def attr-progress "progress")
(def attr-spinner  "spinner")
(def attr-overlay  "overlay")

(def observed-attributes
  #js [attr-active attr-variant attr-progress attr-spinner attr-overlay])

(def event-hidden "x-splash-hidden")

(def property-api
  {:active   {:type 'boolean}
   :variant  {:type 'string}
   :progress {:type 'number}
   :spinner  {:type 'boolean}
   :overlay  {:type 'string}})

(def event-schema
  {event-hidden {:detail     {}
                 :cancelable false}})

(def ^:private allowed-variants #{"default" "branded" "minimal"})
(def ^:private default-variant "default")

(def ^:private allowed-overlays #{"solid" "blur" "transparent"})
(def ^:private default-overlay "solid")

(defn normalize-variant
  "Normalise variant attribute to a valid enum value."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-variants v) v default-variant)))

(defn normalize-overlay
  "Normalise overlay attribute to a valid enum value."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-overlays v) v default-overlay)))

(defn parse-progress
  "Parse progress attribute to a number 0-100, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseFloat (.trim s))]
      (when-not (js/isNaN n)
        (max 0 (min 100 n))))))

(defn parse-bool-default-true
  "Parse an attribute that is true when absent or empty, false only when \"false\"."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn derive-state
  "Derive a stable view-model from raw attribute inputs.

  Input keys:
    :active-present?  boolean
    :variant-raw      string | nil
    :progress-raw     string | nil
    :spinner-attr     string | nil  (nil = absent → true)
    :overlay-raw      string | nil

  Output keys:
    :active?   boolean
    :variant   string
    :progress  number | nil
    :spinner?  boolean
    :overlay   string"
  [{:keys [active-present? variant-raw progress-raw spinner-attr overlay-raw]}]
  {:active?  (boolean active-present?)
   :variant  (normalize-variant variant-raw)
   :progress (parse-progress progress-raw)
   :spinner? (parse-bool-default-true spinner-attr)
   :overlay  (normalize-overlay overlay-raw)})

(def method-api nil)
