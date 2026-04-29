(ns baredom.components.x-typography.model)

(def tag-name "x-typography")

(def attr-variant    "variant")
(def attr-align      "align")
(def attr-truncate   "truncate")
(def attr-line-clamp "line-clamp")

(def observed-attributes
  #js [attr-variant attr-align attr-truncate attr-line-clamp])

(def property-api
  {:variant   {:type 'string}
   :align     {:type 'string}
   :truncate  {:type 'boolean}
   :lineClamp {:type 'number}})

(def event-schema {})

(def ^:private allowed-variants
  #{"h1" "h2" "h3" "h4" "h5" "h6"
    "subtitle1" "subtitle2"
    "body1" "body2"
    "caption" "overline"
    "blockquote" "code" "kbd" "small"})

(def ^:private default-variant "body1")

(def ^:private allowed-aligns #{"left" "center" "right" "justify"})

(def ^:private default-align "left")

(defn parse-variant
  "Normalise a raw variant attribute string.
  Unknown / nil values fall back to \"body1\"."
  [s]
  (let [v (when (string? s) (.toLowerCase s))]
    (if (contains? allowed-variants v) v default-variant)))

(defn parse-align
  "Normalise a raw align attribute string.
  Unknown / nil values fall back to \"left\"."
  [s]
  (let [v (when (string? s) (.toLowerCase s))]
    (if (contains? allowed-aligns v) v default-align)))

(defn parse-truncate
  "Parse a boolean presence attribute. Present (any value) = true, absent (nil) = false."
  [s]
  (some? s))

(defn parse-line-clamp
  "Parse line-clamp attribute to a positive integer, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (number? n) (not (js/isNaN n)) (pos? n))
        n))))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :variant-raw     string | nil
    :align-raw       string | nil
    :truncate-raw    string | nil  (nil = absent, any string = present)
    :line-clamp-raw  string | nil

  Output keys:
    :variant     string
    :align       string
    :truncate?   boolean
    :line-clamp  int | nil"
  [{:keys [variant-raw align-raw truncate-raw line-clamp-raw]}]
  {:variant    (parse-variant variant-raw)
   :align      (parse-align align-raw)
   :truncate?  (parse-truncate truncate-raw)
   :line-clamp (parse-line-clamp line-clamp-raw)})

(def method-api nil)
