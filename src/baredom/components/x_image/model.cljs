(ns baredom.components.x-image.model
  (:require [baredom.utils.model :as mu]))

(def tag-name "x-image")

(def attr-src        "src")
(def attr-alt        "alt")
(def attr-decorative "decorative")
(def attr-ratio      "ratio")
(def attr-fit        "fit")
(def attr-position   "position")
(def attr-loading    "loading")

(def observed-attributes
  #js [attr-src attr-alt attr-decorative attr-ratio attr-fit attr-position attr-loading])

(def event-load  "x-image-load")
(def event-error "x-image-error")

(def property-api
  {:src           {:type 'string}
   :alt           {:type 'string}
   :decorative    {:type 'boolean}
   :ratio         {:type 'string}
   :fit           {:type 'string}
   :position      {:type 'string}
   :loading       {:type 'string}
   :naturalWidth  {:type 'number :readonly true}
   :naturalHeight {:type 'number :readonly true}
   :state         {:type 'string :readonly true}})

(def event-schema
  {event-load  {:detail     {:src 'string :naturalWidth 'number :naturalHeight 'number}
                :cancelable false}
   event-error {:detail     {:src 'string}
                :cancelable false}})

(def state-loading "loading")
(def state-loaded  "loaded")
(def state-error   "error")

(def default-fit      "cover")
(def default-position "center")
(def default-loading  "lazy")

(def ^:private fit-values
  #{"cover" "contain" "fill" "none" "scale-down"})

(def ^:private loading-values
  #{"lazy" "eager"})

(defn parse-fit
  "Normalise fit attribute. Unknown / nil fall back to \"cover\"."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? fit-values v) v default-fit))
    default-fit))

(defn parse-loading
  "Normalise loading attribute. Unknown / nil fall back to \"lazy\"."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? loading-values v) v default-loading))
    default-loading))

(defn parse-position
  "Normalise object-position. Non-empty trimmed string or \"center\"."
  [s]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "") default-position v))
    default-position))

(def ^:private ratio-re
  #"^\s*(\d+(?:\.\d+)?)\s*[:/]\s*(\d+(?:\.\d+)?)\s*$")

(defn parse-ratio
  "Parse a ratio string like \"16:9\" or \"16/9\" into a CSS aspect-ratio value.
  Returns {:css <string|nil> :valid? <boolean>}.
    - nil / \"\" / \"auto\" → {:css nil :valid? true} (no explicit ratio)
    - valid ratio           → {:css \"16 / 9\" :valid? true}
    - invalid              → {:css nil :valid? false}"
  [s]
  (cond
    (nil? s) {:css nil :valid? true}
    (not (string? s)) {:css nil :valid? false}
    :else
    (let [v (.toLowerCase (.trim s))]
      (cond
        (= v "") {:css nil :valid? true}
        (= v "auto") {:css nil :valid? true}
        :else
        (if-let [m (re-matches ratio-re v)]
          (let [w (js/parseFloat (nth m 1))
                h (js/parseFloat (nth m 2))]
            (if (and (pos? w) (pos? h))
              {:css (str w " / " h) :valid? true}
              {:css nil :valid? false}))
          {:css nil :valid? false})))))

(defn safe-src
  "Return sanitized src, or nil when absent/unsafe."
  [s]
  (when (string? s)
    (let [u (mu/sanitize-url s)]
      (when (and (string? u) (not= "" u)) u))))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :src-raw             string | nil
    :alt-raw             string | nil
    :alt-present?        boolean
    :decorative-present? boolean
    :ratio-raw           string | nil
    :fit-raw             string | nil
    :position-raw        string | nil
    :loading-raw         string | nil

  Output keys:
    :src          string | nil   (sanitized; nil when absent/unsafe)
    :alt          string         (\"\" when decorative or absent)
    :decorative?  boolean
    :ratio-css    string | nil   (\"16 / 9\" or nil)
    :ratio-valid? boolean
    :fit          string         (one of fit-values)
    :position     string
    :loading      string         (\"lazy\" | \"eager\")
    :warn-alt?    boolean        (true iff non-decorative AND alt attribute absent)"
  [{:keys [src-raw alt-raw alt-present? decorative-present?
           ratio-raw fit-raw position-raw loading-raw]}]
  (let [decorative? (boolean decorative-present?)
        {:keys [css valid?]} (parse-ratio ratio-raw)
        alt-str     (cond
                      decorative?  ""
                      alt-present? (or alt-raw "")
                      :else        "")
        warn-alt?   (and (not decorative?) (not alt-present?))]
    {:src          (safe-src src-raw)
     :alt          alt-str
     :decorative?  decorative?
     :ratio-css    css
     :ratio-valid? valid?
     :fit          (parse-fit fit-raw)
     :position     (parse-position position-raw)
     :loading      (parse-loading loading-raw)
     :warn-alt?    warn-alt?}))

(defn load-detail
  "Build the event detail map for x-image-load."
  [src natural-w natural-h]
  {:src           (or src "")
   :naturalWidth  (or natural-w 0)
   :naturalHeight (or natural-h 0)})

(defn error-detail
  "Build the event detail map for x-image-error."
  [src]
  {:src (or src "")})

(def method-api nil)
