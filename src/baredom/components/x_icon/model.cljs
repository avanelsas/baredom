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

(def ^:private color-values
  #{"inherit" "primary" "secondary" "tertiary"
    "success" "warning" "danger" "muted"})

;; Fallback hex per colour, used as the `var(--x-color-*, fallback)` second arg.
(def ^:private color-fallback
  {"primary"   "#2563eb"
   "secondary" "#64748b"
   "tertiary"  "#94a3b8"
   "success"   "#16a34a"
   "warning"   "#d97706"
   "danger"    "#dc2626"
   "muted"     "#64748b"})

;; `muted` maps to `--x-color-text-muted` (there is no dedicated `--x-color-muted`
;; in the theme). All others map to `--x-color-<name>`.
(def ^:private color-token-name
  {"primary"   "--x-color-primary"
   "secondary" "--x-color-secondary"
   "tertiary"  "--x-color-tertiary"
   "success"   "--x-color-success"
   "warning"   "--x-color-warning"
   "danger"    "--x-color-danger"
   "muted"     "--x-color-text-muted"})

(def property-api
  {:size  {:type 'string}
   :color {:type 'string}
   :label {:type 'string}})

(def event-schema {})

(def ^:private default-px (get size-token-px default-size))

(defn- default-size-map []
  {:css   (str default-px "px")
   :token default-size
   :px    default-px})

(defn parse-size
  "Parse the `size` attribute.
  Accepts a token (`sm`/`md`/`lg`/`xl`) or a positive integer in pixels.
  Returns a map `{:css \"<n>px\" :token <string|nil> :px <int>}`.
  Unknown / invalid values fall back to the default `md` (20px)."
  [s]
  (if (string? s)
    (let [trimmed (.trim s)
          lower   (.toLowerCase trimmed)]
      (cond
        (= "" trimmed)
        (default-size-map)

        (contains? size-token-px lower)
        (let [px (get size-token-px lower)]
          {:css (str px "px") :token lower :px px})

        :else
        ;; Only accept integers — no units allowed in the attribute.
        (let [n (js/parseInt trimmed 10)]
          (if (and (number? n)
                   (not (js/isNaN n))
                   (pos? n)
                   ;; reject strings like "48abc" by requiring full-numeric form
                   (= (str n) trimmed))
            {:css (str n "px") :token nil :px n}
            (default-size-map)))))
    (default-size-map)))

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
  (if (= c "inherit")
    "inherit"
    (let [token    (get color-token-name c)
          fallback (get color-fallback c)]
      (str "var(" token "," fallback ")"))))

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
  (let [size   (parse-size size-raw)
        colour (parse-color color-raw)
        label  (if (and label-present? (string? label-raw)) label-raw "")]
    {:size-css  (:css size)
     :color-css (color-css-value colour)
     :label     label
     :labelled? (and (boolean label-present?)
                     (not= "" label))}))
