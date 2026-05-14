(ns baredom.components.x-icon.model)

(def tag-name "x-icon")

(def attr-size  "size")
(def attr-color "color")
(def attr-label "label")

(def observed-attributes #js [attr-size attr-color attr-label])

(def default-size  "md")
(def default-color "inherit")

(def ^:private size-token-px
  {"sm" 16
   "md" 20
   "lg" 24
   "xl" 32})

(defn- token-fallback-css [token fallback]
  (str "var(" token "," fallback ")"))

;; Single colour table. `muted` intentionally maps to `--x-color-text-muted`
;; (the theme has no dedicated `--x-color-muted`); the irregularity is local
;; to this row rather than spread across sibling maps.
(def ^:private colors
  {"inherit"   {:css "inherit"}
   "primary"   {:css (token-fallback-css "--x-color-primary"    "#2563eb")}
   "secondary" {:css (token-fallback-css "--x-color-secondary"  "#64748b")}
   "tertiary"  {:css (token-fallback-css "--x-color-tertiary"   "#94a3b8")}
   "success"   {:css (token-fallback-css "--x-color-success"    "#16a34a")}
   "warning"   {:css (token-fallback-css "--x-color-warning"    "#d97706")}
   "danger"    {:css (token-fallback-css "--x-color-danger"     "#dc2626")}
   "muted"     {:css (token-fallback-css "--x-color-text-muted" "#64748b")}})

(def ^:private color-values (set (keys colors)))

(def property-api
  {:size  {:type 'string :reflects-attribute attr-size :default ""}
   :color {:type 'string :reflects-attribute attr-color :default ""}
   :label {:type 'string :reflects-attribute attr-label :default ""}})

(def event-schema {})

(def ^:private default-px (get size-token-px default-size))

(def ^:private default-size-map
  {:css   (str default-px "px")
   :token default-size
   :px    default-px})

(defn- parse-size-token [s]
  (when (string? s)
    (let [lower (.toLowerCase (.trim s))]
      (when-let [px (get size-token-px lower)]
        {:css (str px "px") :token lower :px px}))))

(defn- valid-pixels? [n trimmed]
  ;; `js/parseInt` always returns a Number, so the only checks that carry
  ;; weight are: not NaN, positive, and the full string is digits (rejects
  ;; "48abc", "16.5", "-5", "").
  (and (not (js/isNaN n))
       (pos? n)
       (= (str n) trimmed)))

(defn- parse-size-pixels [s]
  (when (string? s)
    (let [trimmed (.trim s)
          n       (js/parseInt trimmed 10)]
      (when (valid-pixels? n trimmed)
        {:css (str n "px") :token nil :px n}))))

(defn parse-size
  "Parse the `size` attribute.
  Accepts a token (`sm`/`md`/`lg`/`xl`) or a positive integer in pixels.
  Returns a map `{:css \"<n>px\" :token <string|nil> :px <int>}`.
  Unknown / invalid values fall back to the default `md` (20px)."
  [s]
  (or (parse-size-token  s)
      (parse-size-pixels s)
      default-size-map))

(defn parse-color
  "Parse the `color` attribute. Unknown / nil fall back to \"inherit\"."
  [s]
  (if (string? s)
    (let [lower (.toLowerCase (.trim s))]
      (if (contains? color-values lower) lower default-color))
    default-color))

(defn color-css-value
  "Produce the CSS value for a normalized colour name."
  [c]
  (get-in colors [c :css]))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :size-raw       string | nil
    :color-raw      string | nil
    :label-raw      string | nil
    :label-present? boolean

  Output keys:
    :size-css  string   (e.g. \"20px\")
    :color-css string   (\"inherit\" or \"var(...)\")
    :label     string   (\"\" when absent / empty)
    :labelled? boolean  (true only when label attribute is present AND non-empty)"
  [{:keys [size-raw color-raw label-raw label-present?]}]
  (let [size  (parse-size  size-raw)
        color (parse-color color-raw)
        label (if (and label-present? (string? label-raw)) label-raw "")]
    {:size-css  (:css size)
     :color-css (color-css-value color)
     :label     label
     :labelled? (boolean (and label-present? (not= "" label)))}))

(def method-api {})
